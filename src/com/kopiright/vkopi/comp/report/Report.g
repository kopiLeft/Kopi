/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
header { package com.kopiright.vkopi.comp.report; }
{
  import java.util.Vector;
  import java.util.ArrayList;

  import com.kopiright.compiler.base.Compiler;
  import com.kopiright.compiler.tools.antlr.extra.InputBuffer;
  import com.kopiright.compiler.base.TokenReference;
  import com.kopiright.kopi.comp.kjc.*;
  import com.kopiright.xkopi.comp.sqlc.SimpleIdentExpression;
  import com.kopiright.xkopi.comp.sqlc.TableName;
  import com.kopiright.xkopi.comp.sqlc.TableReference;
  import com.kopiright.vkopi.comp.base.*;
  import com.kopiright.vkopi.comp.trig.GKjcParser;
  import com.kopiright.vkopi.comp.trig.GSqlcParser;
  import com.kopiright.xkopi.lib.type.Fixed;
  import com.kopiright.xkopi.lib.type.NotNullFixed;
  import com.kopiright.vkopi.comp.base.VKEnvironment;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class ReportParser extends BaseParser;

options {
  k = 1;				// one token lookahead !
  importVocab = Report;			// Call its vocabulary "Report"
  exportVocab = Report;			// Call its vocabulary "Report"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass = "com.kopiright.vkopi.comp.base.Parser";
  access = "private";			// Set default rule access
}
{
  public ReportParser(Compiler compiler, InputBuffer buffer, VKEnvironment environment) {
    super(compiler, new ReportScanner(compiler, buffer), MAX_LOOKAHEAD);
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

public vrCompilationUnit []
  returns [VRReport self]
:
  self = vrReport[]
  EOF
;

vrReport []
  returns [VRReport self]
{
  String                name;
  String                help = "";
  VRField               fld;
  TokenReference        sourceRef = buildTokenReference();	// !!! add comment
  VRParseReportContext	context = VRParseReportContext.getInstance(environment);
}
:
  "REPORT" name = vkString[]
  vkContextHeader[context.getCompilationUnitContext()]
  help = vkHelp[] 
  vkDefinitions[context.getDefinitionCollector()]
  "BEGIN"
  ( vrReportCommands[context] )?
  ( vrReportTriggers[context] )?
  ( vrField[context])*
  ( vkContextFooter[context.getClassContext()] )?
  "END" "REPORT"
    {
      self = new VRReport(sourceRef,
                          environment,
			  context.getCompilationUnitContext(),
			  context.getClassContext(),
			  context.getDefinitionCollector(),
			  name,
			  context.getCommands(),
			  context.getTriggers(),
			  context.getFields(),
                          help);
      context.release();
    }
;

vrReportTriggers [VRParseReportContext context]
{
  int			e;
  VKAction		t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  (
    e = vrReportEventList[] t = vkAction[null]
      { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vrReportEventList []
  returns [int self = 0]
{
  int		e;
}
:
  e = vrReportEvent[] { self |= (1 << e); }
  ( COMMA e = vrReportEvent[] { self |= (1 << e); } )*
;

vrReportCommands [VRParseReportContext context]
{
  VKCommand	cmd;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
  int modes;
}
:

  ("REPORT" "COMMAND" { VRReport.addDefaultReportCommands(sourceRef, context); } )?
  (
    { modes = com.kopiright.vkopi.lib.form.VConstants.MOD_ANY; }
    cmd = vkCommand[modes]
      { context.addCommand(cmd); }
  )*
;

vrReportEvent []
  returns [int self]
:
  "PREREPORT"	{ self = com.kopiright.vkopi.lib.report.Constants.TRG_PREREPORT; }
|
  "POSTREPORT"	{ self = com.kopiright.vkopi.lib.report.Constants.TRG_POSTREPORT; }
;

vrField[VRParseReportContext report]
{
  String		name;
  ArrayList		label	= null;
  String		help;
  String		group = null;
  VRFieldType		type;
  int			align = com.kopiright.vkopi.lib.report.Constants.ALG_DEFAULT;
  int			opts;
  int			multiField = 1;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
  VRParseFieldContext	context = VRParseFieldContext.getInstance();
}
:
  "FIELD"
  ( LPAREN multiField = vkInteger[] RPAREN )?
  name = vkSimpleIdent[]
  (label = vrFieldLabel[name] )?
  help = vkHelp[]
  type = vrFieldType[]
  ( align = vrFieldAlign[] )?
  opts = vrFieldOptions[]
  ( "GROUP" group = vkSimpleIdent[] )*
  ( vrCommands[context] )?
  ( vrFieldTriggers[context] )?
  "END" "FIELD"
    {
      if (multiField > 1) {
	VRMultiField.generateFields(report,
				    sourceRef,
				    name,
				    multiField,
				    label,
				    help,
				    type,
				    align,
				    opts,
				    group,
				    context.getCommands(),
				    context.getTriggers());
      } else {
 	report.addField(new VRField(sourceRef,
				    name,
				    label == null ? null : (String)label.get(0),
				    help,
				    type,
				    align,
				    opts,
				    group,
				    context.getCommands(),
				    context.getTriggers()));
      }
      context.release();
   }
;

vrFieldAlign []
  returns [int self]
:
  "ALIGN"
  (
    "LEFT"	{ self = com.kopiright.vkopi.lib.report.Constants.ALG_LEFT; }
  |
    "RIGHT"	{ self = com.kopiright.vkopi.lib.report.Constants.ALG_RIGHT; }
  |
    "CENTER"	{ self = com.kopiright.vkopi.lib.report.Constants.ALG_CENTER; }
  )
;

vrFieldOptions []
  returns [int self = 0]
:
  ( "HIDDEN" { self = com.kopiright.vkopi.lib.report.Constants.CLO_HIDDEN; } )*
;

vrFieldTriggers [VRParseFieldContext context]
{
  int		e;
  VKAction	t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  (
    e = vrFieldEventList[] t = vkAction[null]
    { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vrFieldEventList []
  returns [int self = 0]
{
  int		e;
}
:
  e = vrFieldEvent[] { self |= (1 << e); }
  ( COMMA e = vrFieldEvent[] { self |= (1 << e); } )*
;

vrFieldEvent []
  returns [int self]
:
  "FORMAT"	{ self = com.kopiright.vkopi.lib.report.Constants.TRG_FORMAT; }
|
  "COMPUTE"	{ self = com.kopiright.vkopi.lib.report.Constants.TRG_COMPUTE; }
;

vrCommands[VRParseFieldContext context]
{
  VKCommand	cmd;
}
:
  (
    cmd = vkCommand[com.kopiright.vkopi.lib.form.VConstants.MOD_ANY]
      { context.addCommand(cmd); }
  )+
;

vrFieldLabel [String name]
  returns [ArrayList self = null]
{
  String str = null;
}
:
  "NO" "LABEL"
|
  "LABEL"
  { self = new ArrayList(1); }
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

vrFieldType []
  returns [VRFieldType self = null]
{
  VKType		def;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  self = vrFieldTypeName[]
|
  def = vkPredefinedFieldType[]
    { self = new VRDefinitionType(sourceRef, def); }
;

vrFieldTypeName []
  returns [VRFieldTypeName self]
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
    { self = new VRFieldTypeName(sourceRef, name, params); }
;
