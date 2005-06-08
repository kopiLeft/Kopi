/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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
header { package at.dms.vkopi.comp.form; }
{
  import java.util.Vector;
  import java.util.ArrayList;
  import at.dms.compiler.base.Compiler;
  import at.dms.compiler.base.PositionedError;
  import at.dms.compiler.tools.antlr.extra.InputBuffer;
  import at.dms.compiler.base.TokenReference;
  import at.dms.kopi.comp.kjc.*;
  import at.dms.xkopi.comp.sqlc.SimpleIdentExpression;
  import at.dms.xkopi.comp.sqlc.TableName;
  import at.dms.xkopi.comp.sqlc.TableReference;
  import at.dms.util.base.InconsistencyException;
  import at.dms.vkopi.comp.base.*;
  import at.dms.vkopi.comp.trig.GKjcParser;
  import at.dms.vkopi.comp.trig.GSqlcParser;
  import at.dms.xkopi.lib.type.Fixed;
  import at.dms.xkopi.lib.type.NotNullFixed;
  import at.dms.vkopi.comp.base.VKEnvironment;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class FormParser extends BaseParser;

options {
  k = 1;				// one token lookahead !
  importVocab = Form;			// Call its vocabulary "Form"
  exportVocab = Form;			// Call its vocabulary "Form"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass = "at.dms.compiler.tools.antlr.extra.Parser";
  access = "private";			// Set default rule access
}
{
  public FormParser(Compiler compiler, InputBuffer buffer, VKEnvironment environment) {
    super(compiler, new FormScanner(compiler, buffer), MAX_LOOKAHEAD);
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

public vfCompilationUnit []
  returns [VKWindow self = null]
:
  (
     self = vkForm[]
  |
     self = vkBlockUnit[]
  )
  EOF
;

vkBlockUnit []
  returns [VKBlockInsert self = null]
{
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
  VKBlock		block;
  VKParseFormContext	context = VKParseFormContext.getInstance(environment);
}
:
  "BLOCK" "INSERT"
  vkContextHeader[context.getCompilationUnitContext()]
  vkDefinitions[context.getDefinitionCollector()]
  block = vkBlock[]
  {
    context.addFormElement(block);
    self = new VKBlockInsert(sourceRef,
                             environment,
			     context.getCompilationUnitContext(),
			     context.getClassContext(),
	                     context.getDefinitionCollector(),
			     context.getElements());
  }
  "END" "INSERT"
;

vkBlock []
  returns [VKBlock self]
{
  final int		ACCESS		= at.dms.vkopi.lib.form.VConstants.ACS_MUSTFILL;
  String		name;
  String		ident;
  String		title;
  int			border		= 0;
  CReferenceType	superBlock	= null;
  VKBlockAlign		align		= null;
  String		help;
  String		inter;
  int			opts;
  int			buffer;
  int			vis;
  int[]			access		= new int[] {ACCESS, ACCESS, ACCESS};
  VKParseBlockContext	context		= VKParseBlockContext.getInstance();
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  "BLOCK" LPAREN buffer = vkInteger[] COMMA vis = vkInteger[] RPAREN
  name = vkBlockName[]
  title = vkString[]
  (
    "IS" ident = vkQualifiedIdent[]
      { superBlock = environment.getTypeFactory().createType(ident.replace('.', '/'), false); }
  )?
  (
    "IMPLEMENTS"
    inter = vkQualifiedIdent[]
      { context.addInterface(environment.getTypeFactory().createType(inter.replace('.', '/'), false)); }
    (
      COMMA inter = vkQualifiedIdent[]
        { context.addInterface(environment.getTypeFactory().createType(inter.replace('.', '/'), false)); }
    )*
  )?
  ( border = vkBorder[] )?
  ( align = vkBlockAlignment[] )?
  help = vkHelp[]
  opts = vkBlockOptions[]
  ( vkBlockTables[context] )?
  ( vkBlockIndices[context] )?
  ( vkModeAndCommands[access, context] )?
  ( vkBlockTriggers[context] )?
  vkBlockObjects[context]
  ( vkContextFooter[context.getClassContext()] )?
  "END" "BLOCK"
    {
      self = new VKBlock(sourceRef,
			 context.getClassContext(),
			 context.getInterfaces(),
			 buffer,
			 vis,
			 name,
			 superBlock,
			 title,
			 border,
			 align,
			 help,
			 opts,
			 context.getTables(),
			 context.getIndices(),
			 access,
			 context.getCommands(),
			 context.getTriggers(),
			 context.getFields());
      context.release();
    }
;

vkBorder []
  returns [int self]
:
  "BORDER"
  (
    "LINE"	{ self = at.dms.vkopi.lib.form.VConstants.BRD_LINE; }
  |
    "RAISED"	{ self = at.dms.vkopi.lib.form.VConstants.BRD_RAISED; }
  |
    "LOWERED"	{ self = at.dms.vkopi.lib.form.VConstants.BRD_LOWERED; }
  |
    "ETCHED"	{ self = at.dms.vkopi.lib.form.VConstants.BRD_ETCHED; }
  )
;

vkImportedBlock []
  returns [VKImportedBlock self]
{
  String			name;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "INSERT"  name = vkQualifiedIdent[]
    { self = new VKImportedBlock(sourceRef, name); }
;

vkAccess []
  returns [int self]
:
  "VISIT"	{ self = at.dms.vkopi.lib.form.VConstants.ACS_VISIT; }
|
  "MUSTFILL"	{ self = at.dms.vkopi.lib.form.VConstants.ACS_MUSTFILL; }
|
  "SKIPPED"	{ self = at.dms.vkopi.lib.form.VConstants.ACS_SKIPPED; }
|
  "HIDDEN"	{ self = at.dms.vkopi.lib.form.VConstants.ACS_HIDDEN; }
;

vkBlockObjects [VKParseBlockContext context]
:
  (
      //(
      vkField[context]
      //|
      //vkLabel[context]
      //|
      //vkImage[context]
      //)
  )+
;

vkModeAndCommands[int[] access, VKParseContext context]
{
  int		m = at.dms.vkopi.lib.form.VConstants.MOD_ANY;
  VKCommand	cmd;
}
:
  (
    ( m = vkModeList[]
      (
        vkModeAccess[m, access]
      |
        cmd = vkCommand[m]
        { context.addCommand(cmd); }
      )
    |
      cmd = vkCommand[at.dms.vkopi.lib.form.VConstants.MOD_ANY]
        { context.addCommand(cmd); }
    )
  )+
;

vkModeAccess [int mode, int[] self]
{
  int		a;
}
:
    a = vkAccess[]
      {
	if ((mode & (1 << at.dms.vkopi.lib.form.VConstants.MOD_QUERY)) != 0) {
	  self[0] = a;
	}
	if ((mode & (1 << at.dms.vkopi.lib.form.VConstants.MOD_INSERT)) != 0) {
	  self[1] = a;
	}
	if ((mode & (1 << at.dms.vkopi.lib.form.VConstants.MOD_UPDATE)) != 0) {
	  self[2] = a;
	}
      }
;

vkBlockAlignment []
  returns [VKBlockAlign self]
{
  String	block;
  Vector	sources = new Vector();
  Vector	targets = new Vector();
  int		source, target;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "ALIGN" block = vkSimpleIdent[]
  LT
  source = vkInteger[] MINUS target = vkInteger[]
    {
      sources.add(new Integer(target));
      targets.add(new Integer(source));
    }
  (
    COMMA source = vkInteger[] MINUS target = vkInteger[]
      {
	sources.add(new Integer(target));
	targets.add(new Integer(source));
      }
  )*
  GT
    {
      self = new VKBlockAlign(sourceRef,
			      block,
			      at.dms.util.base.Utils.toIntArray(sources),
			      at.dms.util.base.Utils.toIntArray(targets));
    }
;

vkBlockEvent []
  returns [int self]
:
  "PREQRY"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREQRY; }
|
  "POSTQRY"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTQRY; }
|
  "PREDEL"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREDEL; }
|
  "POSTDEL"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTDEL; }
|
  "PREINS"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREINS; }
|
  "POSTINS"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTINS; }
|
  "PREUPD"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREUPD; }
|
  "POSTUPD"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTUPD; }
|
  "PRESAVE"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PRESAVE; }
|
  "PREREC"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREREC; }
|
  "POSTREC"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTREC; }
|
  "PREBLK"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREBLK; }
|
  "POSTBLK"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTBLK; }
|
  "VALBLK"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_VALBLK; }
|
  "VALREC"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_VALREC; }
|
  "DEFAULT"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_DEFAULT; }
|
  "INIT"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_INIT; }
|
  "RESET"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_RESET; }
|
  "CHANGED"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_CHANGED; }
|
  "ACCESS"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_ACCESS; }
;

vkBlockEventList []
  returns [int self = 0]
{
  int		e;
}
:
  e = vkBlockEvent[] { self |= (1 << e); }
  ( COMMA e = vkBlockEvent[] { self |= (1 << e); } )*
;

vkBlockIndices[VKParseBlockContext context]
{
  String	s;
}
:
  ( "INDEX" s = vkString[] { context.addIndice(s); } )+
;

vkBlockName []
  returns [String self]
{
  String s;
}
:
  self = vkSimpleIdent[] ( DOT s = vkSimpleIdent[] { self += "." + s; } )?
;

vkBlockOptions []
  returns [int self]
{ self = 0; }
:
  (
    "NO"
    (
      "CHART"   { self |= at.dms.vkopi.lib.form.VConstants.BKO_NOCHART; }
    |
      "DELETE" { self |= at.dms.vkopi.lib.form.VConstants.BKO_NODELETE; }
    |
      "DETAIL"   { self |= at.dms.vkopi.lib.form.VConstants.BKO_NODETAIL; }
    |
      "INSERT" { self |= at.dms.vkopi.lib.form.VConstants.BKO_NOINSERT; }
    |
      "MOVE"   { self |= at.dms.vkopi.lib.form.VConstants.BKO_NOMOVE; }
    )
  |
    "UPDATE" "INDEX" { self |= at.dms.vkopi.lib.form.VConstants.BKO_INDEXED; }
  |
    "ACCESS" "ON" "SKIPPED" { self |= at.dms.vkopi.lib.form.VConstants.BKO_ALWAYS_ACCESSIBLE; }
  )*
;

vkBlockTables [VKParseBlockContext context]
{
  String	n;
  String	c;
  String	t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  (
    "TABLE"
    LT 
      n = vkSimpleIdent[]
      ( DOT t = vkSimpleIdent[] { n += "." + t; } )?
    COMMA 
      c = vkSimpleIdent[]
      ( DOT t = vkSimpleIdent[] { c += "." + t; }  )?
    GT
      { context.addTable(new VKBlockTable(sourceRef, n, c)); }
  )+
;

vkBlockTriggers [VKParseBlockContext context]
{
  int			e;
  VKAction		t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  (
    e = vkBlockEventList[] t = vkTriggerAction[]
      { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vkBlocks [VKParseFormContext context]
{
  VKFormElement	b;
  String	p;
}
:
  (
    ( 
      p = vkNewpage[] 
                { 
                  if (context.getElements().length > 0 && context.getPages().length == 0) {
                      reportTrouble(new PositionedError(buildTokenReference(),
                                                        FormMessages.MISSING_PAGE));
                  } 
                  context.addPage(p);
                } 
    )?
    ( b = vkBlock[] | b = vkImportedBlock[] ) { context.addFormElement(b); }
  )+
;

vkPosition []
  returns [VKPosition self]
{
  String	block       = null;
  String	field       = null;
  int		line, column;
  int		end         = 0;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  ( "AT"
    LT line = vkInteger[]
      (
        COMMA column = vkInteger[] ( MINUS end = vkInteger[] )?
          { self = new VKCoordinatePosition(sourceRef, line, column, end); }
      |
        { self = new VKMultiFieldPosition(sourceRef, line); }
      )
    GT
  )
  |
  "FOLLOW" field = vkSimpleIdent[]
    { self = new VKDescriptionPosition(sourceRef, field); }
;

/*
vkImage [Vector objects]
{
  String	ident;
  String	name;
  VKPosition	pos	= null;
  int		border	= 0;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "IMAGE" name = vkString[]
  ( pos = vkPosition[] )?
  ( border = vkBorder[] )?
    { objects.addElement(new VKImage(sourceRef, name, pos, border)); }
;

vkLabel [VKParseBlockContext block]
{
  String     ident;
  String     name;
  VKPosition pos = null;
  int        border = 0;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "LABEL" name = vkString[]
  ( pos = vkPosition[] )?
  ( "BORDER" border = vkBorder[] )?
    { block.addElement(new VKLabel(sourceRef, name, pos, border)); }
;
*/
vkField [VKParseBlockContext block]
{
  String		name		= null;
  VKPosition		pos		= null;
  ArrayList		label		= null;
  String		help;
  VKFieldType		type;
  VKFieldColumns	columns		= null;
  int			align		= at.dms.vkopi.lib.form.VConstants.ALG_LEFT;
  int			opts		= 0;
  int[]			access;
  String		alias		= null;
  int			multiField	= 1;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
  VKParseFieldContext	context		= VKParseFieldContext.getInstance();
}
:
  access = vkFieldSpec[]
  ( LPAREN multiField = vkInteger[] RPAREN )?
  ( name = vkSimpleIdent[] )?
  ( pos = vkPosition[] )?
  ( label = vkFieldLabel[name] )?
  help = vkHelp[]
  type = vkFieldType[]
  (
    "IS" alias = vkQualifiedIdent[]
    ( columns = vkFieldColumns[] )?
  |
    ( align = vkFieldAlign[] )?
    opts = vkFieldOptions[]
    ( columns = vkFieldColumns[] )?
    ( vkModeAndCommands[access, context] )?
    ( vkFieldTriggers[context] )?
  )
  "END" "FIELD"
    {
      if (multiField > 1) {
	VKMultiField.generateFields(block,
				    sourceRef,
				    name,
				    multiField,
				    pos,
				    label,
				    help,
				    type,
				    align,
				    opts,
				    columns,
				    access,
				    context.getCommands(),
				    context.getTriggers(),
				    alias);
      } else {
        block.addField(new VKField(sourceRef,
				     name,
				     pos,
				     label == null ? null : (String)label.get(0),
				     help,
				     type,
				     align,
				     opts,
				     columns,
				     access,
				     context.getCommands(),
				     context.getTriggers(),
				     alias));
      }
      context.release();
   }
;

vkFieldSpec []
  returns [int[] self]
{ int		a; }
:
  a = vkAccess[] { self = new int[] { a, a, a }; }
;

vkFieldColumn []
  returns [VKFieldColumn self]
{
  boolean	isKey = false;
  String	name;
  String	ident;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  ( "KEY" { isKey = true; } )?
  name = vkSQLIdent[] 
  DOT ident = vkSQLIdent[]
  ( DOT 
    { name += "." + ident; }
    ident = vkSQLIdent[] 
  )? 
    { self = new VKFieldColumn(sourceRef, name, ident, isKey); }
;

vkFieldColumnList []
  returns [ArrayList self = new ArrayList()]
{
  VKFieldColumn		c;
}
:
  LPAREN
  c = vkFieldColumn[] { self.add(c); }
  ( COMMA c = vkFieldColumn[] { self.add(c); } )*
  RPAREN
;

vkFieldColumns []
  returns [VKFieldColumns self]
{
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
  ArrayList		c;
  int			i = 0;
  int			p = 0;
}
:
  "COLUMNS"
  c = vkFieldColumnList[]
  ( i = vkFieldIndices[] )?
  ( p = vkFieldPriority[] )?
    {
      self = new VKFieldColumns(sourceRef,
				(c == null) ? new VKFieldColumn[0] : (VKFieldColumn[])c.toArray(new VKFieldColumn[c.size()]),
				i,
				p);
    }
;

vkFieldEvent []
  returns [int self]
:
  "POSTCHG"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTCHG; }
|
  "PREFLD"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREFLD; }
|
  "POSTFLD"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_POSTFLD; }
|
  "PREVAL"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_PREVAL; }
|
  "VALFLD"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_VALFLD; }
|
  "VALIDATE"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_VALFLD; }
|
  "DEFAULT"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_DEFAULT; }
|
  "FORMAT"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_FORMAT; }
|
  "ACCESS"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_FLDACCESS; }
|
  "VALUE"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_VALUE; }
|
  "AUTOLEAVE"	{ self = at.dms.vkopi.lib.form.VConstants.TRG_AUTOLEAVE; }
;

vkFieldEventList []
  returns [int self = 0]
{
  int		e;
}
:
  e = vkFieldEvent[] { self |= (1 << e); }
  ( COMMA e = vkFieldEvent[] { self |= (1 << e); } )*
;

vkFieldIndices []
  returns [int self = 0]
{
  int		x;
}
:
  "INDEX"
  (
    x = vkInteger[]
      {
        if (x < 0 || x >= 32) {
	  throw new InconsistencyException("INDEX VALUE OUT OF RANGE"); // !!!
	}
        self |= (1 << x);
      }
  )+
;

vkFieldLabel [String name]
  returns [ArrayList self = null]
{
  String	str;
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


vkFieldAlign []
  returns [int self]
:
  "ALIGN"
  (
    "LEFT"	{ self = at.dms.vkopi.lib.form.VConstants.ALG_LEFT; }
  |
    "RIGHT"	{ self = at.dms.vkopi.lib.form.VConstants.ALG_RIGHT; }
  |
    "CENTER"	{ self = at.dms.vkopi.lib.form.VConstants.ALG_CENTER; }
  )
;

vkFieldOptions []
  returns [int self]
{
  self = 0;
}
:
  (
    "NOECHO" { self |= at.dms.vkopi.lib.form.VConstants.FDO_NOECHO; }
  |
    "NOEDIT" { self |= at.dms.vkopi.lib.form.VConstants.FDO_NOEDIT; }
  |
    "SORTABLE" { self |= at.dms.vkopi.lib.form.VConstants.FDO_SORT; }
  |
    "TRANSIENT" { self |= at.dms.vkopi.lib.form.VConstants.FDO_TRANSIENT; }
  |
    "NO" 
    (
      "DELETE" "ON" "UPDATE" { self |= at.dms.vkopi.lib.form.VConstants.FDO_DO_NOT_ERASE_ON_LOOKUP; }
    |
      "DETAIL" { self |= at.dms.vkopi.lib.form.VConstants.FDO_NODETAIL; }
    |
      "CHART" { self |= at.dms.vkopi.lib.form.VConstants.FDO_NOCHART; }
    )

  |
    "QUERY"
      { self &= ~at.dms.vkopi.lib.form.VConstants.FDO_SEARCH_MASK; }
    (
      "UPPER"
        { self |= at.dms.vkopi.lib.form.VConstants.FDO_SEARCH_UPPER; }
    |
      "LOWER"
        { self |= at.dms.vkopi.lib.form.VConstants.FDO_SEARCH_LOWER; }
    )
  )*
;

vkFieldPriority []
  returns [int self]
{ int sign = 1; }
:
  "PRIORITY" ( MINUS { sign = -1; } )? self = vkInteger[] { self *= sign; }
;

vkFieldTriggers [VKParseFieldContext context]
{
  int		e;
  VKAction	t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  (
   e = vkFieldEventList[] t = vkTriggerAction[]
    { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vkFieldType []
  returns [VKFieldType self = null]
{
  VKType		def;
  VKFieldList		list;
  String		name;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  self = vkFieldTypeName[]
|
  def = vkFieldTypeDefinition[] ( list = vkFieldList[] { def.addList(list); } )?
    { self = new VKDefinitionType(sourceRef, def); }
;

vkFieldTypeName []
  returns [VKFieldTypeName self]
{
  String		name;
  JExpression[]	params = null;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "TYPE" name = vkQualifiedIdent[]
  (
    LPAREN
      { params = buildGKjcParser().gArguments(); }
      // RPAREN
  )?
    { self = new VKFieldTypeName(sourceRef, name, params); }
;

vkForm []
  returns [VKForm self = null]
{
  String		name;
  String		ident;
  CReferenceType	superForm = null;
  ArrayList		commands = null;
  ArrayList		triggers = null;
  ArrayList		interfaces = null;
  int			opt = 0;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
  VKParseFormContext	context = VKParseFormContext.getInstance(environment);
}
:
  "FORM"  name = vkString[]
  (
    "IS" ident = vkQualifiedIdent[]
      { superForm = environment.getTypeFactory().createType(ident.replace('.', '/'), false); }
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
  vkDefinitions[context.getDefinitionCollector()]
  "BEGIN"
  ( opt = vkFormOptions[] )?
  ( vkFormCommands[context] )?
  ( vkFormTriggers[context] )?
  vkBlocks[context]
  ( vkContextFooter[context.getClassContext()] )?
  "END" "FORM"
    {
      self = new VKForm(sourceRef,
                        environment,
			context.getCompilationUnitContext(),
			context.getClassContext(),
	                context.getDefinitionCollector(),
			name,
			superForm,
			context.getInterfaces(),
			opt,
			context.getCommands(),
			context.getTriggers(),
			context.getElements(),
			context.getPages());
      context.release();
   }
;

vkFormOptions []
  returns [int self]
{ 
  self = 0; 
}
:
    "NO" 
    (
      "MOVE"
        { self |= at.dms.vkopi.lib.form.VConstants.FMO_NOMOVE; }
    |
      "BLOCK" "MOVE"
        { self |= at.dms.vkopi.lib.form.VConstants.FMO_NOBLOCKMOVE; }
    )
;

vkFormEvent []
  returns [int self]
:
  "PREFORM"
    { self = at.dms.vkopi.lib.form.VConstants.TRG_PREFORM; }
|
  "QUITFORM"
    { self = at.dms.vkopi.lib.form.VConstants.TRG_QUITFORM; }
|
  "POSTFORM"
    { self = at.dms.vkopi.lib.form.VConstants.TRG_POSTFORM; }
|
  "INIT"
    { self = at.dms.vkopi.lib.form.VConstants.TRG_INIT; }
|
  "RESET"
    { self = at.dms.vkopi.lib.form.VConstants.TRG_RESET; }
|
  "CHANGED"
    { self = at.dms.vkopi.lib.form.VConstants.TRG_CHANGED; }
;

vkFormEventList []
  returns [int self = 0]
{
  int		e;
}
:
  e = vkFormEvent[] { self |= (1 << e); }
  ( COMMA e = vkFormEvent[] { self |= (1 << e); } )*
;

vkFormTriggers [VKParseFormContext context]
{
  int			e;
  VKAction		t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  (
    e = vkFormEventList[] t = vkTriggerAction[]
      { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vkFormCommands [VKParseFormContext context]
{
  VKCommand	cmd;
  int modes;
}
:
  (
      { modes = at.dms.vkopi.lib.form.VConstants.MOD_ANY; }
    ( modes = vkModeList[] )?
    cmd = vkCommand[modes]
      { context.addCommand(cmd); }
  )+
;

vkMode []
  returns [int self]
:
  "QUERY"
    { self = 1 << at.dms.vkopi.lib.form.VConstants.MOD_QUERY; }
|
  "INSERT"
    { self = 1 << at.dms.vkopi.lib.form.VConstants.MOD_INSERT; }
|
  "UPDATE"
    { self = 1 << at.dms.vkopi.lib.form.VConstants.MOD_UPDATE; }
;

vkModeList []
  returns [int self = 0]
{
  int		mode;
}
:
  "ON" mode = vkMode[] { self |= mode; }
  ( COMMA mode = vkMode[] { self |= mode; } )*
;

vkNewpage []
  returns [String self]
:
  "NEW" "PAGE" self = vkString[] ( "CENTER" { self += "<CENTER>"; })? // !!! temp
;
