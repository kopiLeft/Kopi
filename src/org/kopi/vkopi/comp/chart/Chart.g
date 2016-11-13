/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

// Import the necessary classes
header { package org.kopi.vkopi.comp.chart; }
{
  import java.util.Vector;
  import java.util.ArrayList;

  import org.kopi.compiler.base.Compiler;
  import org.kopi.compiler.base.TokenReference;
  import org.kopi.compiler.tools.antlr.extra.InputBuffer;
  import org.kopi.kopi.comp.kjc.*;
  import org.kopi.vkopi.comp.base.*;
  import org.kopi.vkopi.comp.base.VKEnvironment;
  import org.kopi.vkopi.comp.trig.GKjcParser;
  import org.kopi.vkopi.comp.trig.GSqlcParser;
  import org.kopi.vkopi.lib.list.VList;
  import org.kopi.xkopi.comp.sqlc.SimpleIdentExpression;
  import org.kopi.xkopi.comp.sqlc.TableName;
  import org.kopi.xkopi.comp.sqlc.TableReference;
  import org.kopi.xkopi.lib.type.Fixed;
  import org.kopi.xkopi.lib.type.NotNullFixed;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class ChartParser extends BaseParser;

options {
  k = 1;				// one token lookahead !
  importVocab = Chart;			// Call its vocabulary "Chart"
  exportVocab = Chart;			// Call its vocabulary "Chart"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass = "org.kopi.vkopi.comp.base.Parser";
  access = "private";			// Set default rule access
}
{
  public ChartParser(Compiler compiler, InputBuffer buffer, VKEnvironment environment) {
    super(compiler, new ChartScanner(compiler, buffer), MAX_LOOKAHEAD);
    this.environment = environment;
  }

  private GKjcParser buildGKjcParser() {
    return new GKjcParser(getCompiler(), getBuffer(), environment);
  }

  private GSqlcParser buildGSqlcParser() {
    return new GSqlcParser(getCompiler(), getBuffer(), environment);
  }

  private final VKEnvironment  environment;
}

public vcCompilationUnit []
  returns [VCChart self]
:
  vkLocaleDeclaration[]
  self = vcChart[]
  EOF
;

vcChart []
  returns [VCChart self]
{
  String                name;
  CReferenceType        superChart = null;
  String                ident;
  String                help = "";
  TokenReference        sourceRef = buildTokenReference();	// !!! add comment
  VCParseChartContext	context = VCParseChartContext.getInstance(environment);
}
:
  "CHART" name = vkString[]
  (
    "IS" ident = vkQualifiedIdent[]
      { superChart = environment.getTypeFactory().createType(ident.replace('.', '/'), false); }
  )?
  (
    "IMPLEMENTS"
    ident = vkQualifiedIdent[]
      { context.addInterface(environment.getTypeFactory().createType(ident.replace('.', '/'), false)); }
    (
      COMMA ident = vkQualifiedIdent[]
        { context.addInterface(environment.getTypeFactory().createType(ident.replace('.', '/'), false)); }
    )*
  )?
  vkContextHeader[context.getCompilationUnitContext()]
  help = vkHelp[] 
  vkDefinitions[context.getDefinitionCollector(), context.getCompilationUnitContext().getPackageName().getName()]
  "BEGIN"
  ( vcChartCommands[context] )?
  ( vcChartTriggers[context] )?
  ( vcField[context])*
  ( vkContextFooter[context.getClassContext()] )?
  "END" "CHART"
    {
      self = new VCChart(sourceRef,
                         environment,
			 context.getCompilationUnitContext(),
			 context.getClassContext(),
			 context.getDefinitionCollector(),
			 name,
                         getLocale(),
                         superChart,
                         context.getInterfaces(),
			 context.getCommands(),
			 context.getTriggers(),
			 context.getDimensions(),
			 context.getMeasures(),
                         help);
      context.release();
    }
;

vcChartTriggers [VCParseChartContext context]
{
  long			e;
  VKAction		t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  (
    e = vcChartEventList[] t = vkAction[null]
      { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vcChartEventList []
  returns [int self = 0]
{
  long		e;
}
:
  e = vcChartEvent[] { self |= (1L << e); }
  ( COMMA e = vcChartEvent[] { self |= (1L << e); } )*
;

vcChartCommands [VCParseChartContext context]
{
  VKCommand	cmd;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
  int modes;
}
:

  ("CHART" "COMMAND" { VCChart.addDefaultChartCommands(sourceRef, context); } )?
  (
    { modes = org.kopi.vkopi.lib.form.VConstants.MOD_ANY; }
    cmd = vkCommand[modes]
      { context.addCommand(cmd); }
  )*
;

vcChartEvent []
  returns [long self]
:
  "PRECHART"	{ self = org.kopi.vkopi.lib.chart.CConstants.TRG_PRECHART; }
|
  "INIT"        { self = org.kopi.vkopi.lib.chart.CConstants.TRG_INIT; }
|
  "POSTCHART"	{ self = org.kopi.vkopi.lib.chart.CConstants.TRG_POSTCHART; }
|
  "CHARTTYPE"   {self = org.kopi.vkopi.lib.chart.CConstants.TRG_CHARTTYPE;}
;

vcField[VCParseChartContext chart]
:
  vcDimension[chart]
|
  vcMeasure[chart]
;

vcDimension[VCParseChartContext chart]
{
  String		name;
  ArrayList<String>	label = null;
  String		help;
  VCFieldType		type;
  int			multiField = 1;
  TokenReference	sourceRef = buildTokenReference();
  VCParseFieldContext	context = VCParseFieldContext.getInstance();
}
:
  "DIMENSION"
  ( LPAREN multiField = vkInteger[] RPAREN )?
  name = vkSimpleIdent[]
  (label = vcFieldLabel[name] )?
  help = vkHelp[]
  type = vcFieldType[]
  ( vcCommands[context] )?
  ( vcFieldTriggers[context] )?
  "END" "DIMENSION"
    {
      if (multiField > 1) {
	VCMultiDimension.generateFields(chart,
				        sourceRef,
				        name,
				    	multiField,
				    	label,
				    	help,
				    	type,
				    	context.getCommands(),
				    	context.getTriggers());
      } else {
 	chart.addField(new VCDimension(sourceRef,
				       name,
				       label == null ? null : label.get(0),
				       help,
				       type,
				       context.getCommands(),
				       context.getTriggers()));
      }
      context.release();
   }
;

vcMeasure[VCParseChartContext chart]
{
  String		name;
  ArrayList<String>	label = null;
  String		help;
  VCFieldType		type;
  int			multiField = 1;
  TokenReference	sourceRef = buildTokenReference();
  VCParseFieldContext	context = VCParseFieldContext.getInstance();
}
:
  "MEASURE"
  ( LPAREN multiField = vkInteger[] RPAREN )?
  name = vkSimpleIdent[]
  (label = vcFieldLabel[name] )?
  help = vkHelp[]
  type = vcFieldType[]
  ( vcCommands[context] )?
  ( vcFieldTriggers[context] )?
  "END" "MEASURE"
    {
      if (multiField > 1) {
	VCMultiMeasure.generateFields(chart,
				      sourceRef,
				      name,
				      multiField,
				      label,
				      help,
				      type,
				      context.getCommands(),
				      context.getTriggers());
      } else {
 	chart.addField(new VCMeasure(sourceRef,
				     name,
				     label == null ? null : label.get(0),
				     help,
				     type,
				     context.getCommands(),
				     context.getTriggers()));
      }
      context.release();
   }
;

vcFieldTriggers [VCParseFieldContext context]
{
  int		e;
  VKAction	t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  (
    e = vcFieldEventList[] t = vkAction[null]
    { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vcFieldEventList []
  returns [int self = 0]
{
  int		e;
}
:
  e = vcFieldEvent[] { self |= (1 << e); }
  ( COMMA e = vcFieldEvent[] { self |= (1 << e); } )*
;

vcFieldEvent []
  returns [int self]
:
  "FORMAT"	{ self = org.kopi.vkopi.lib.chart.CConstants.TRG_FORMAT; }
|
  "COLOR"       { self = org.kopi.vkopi.lib.chart.CConstants.TRG_COLOR; }
;

vcCommands[VCParseFieldContext context]
{
  VKCommand	cmd;
}
:
  (
    cmd = vkCommand[org.kopi.vkopi.lib.form.VConstants.MOD_ANY]
      { context.addCommand(cmd); }
  )+
;

vcFieldLabel [String name]
  returns [ArrayList<String> self = null]
{
  String str = null;
}
:
  "NO" "LABEL"
|
  "LABEL"
  { self = new ArrayList<String>(1); }
  (
    str = vkString[]
    { self.add(str); }
    (
      COMMA str = vkString[]
      { self.add(str); }
    )*
  |
    { self.add(name);}
  )
;

vcFieldType []
  returns [VCFieldType self = null]
{
  VKType                def;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = vcFieldTypeName[]
|
  def = vkPredefinedFieldType[]
    { self = new VCDefinitionType(sourceRef, def); }
;

vcFieldTypeName []
  returns [VCFieldTypeName self]
{
  String		name;
  JExpression[]         params = null;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  "TYPE" name = vkQualifiedIdent[]
  (
    LPAREN
      { params = buildGKjcParser().gArguments(); }
      // RPAREN
  )?
    { self = new VCFieldTypeName(sourceRef, name, params); }
;
