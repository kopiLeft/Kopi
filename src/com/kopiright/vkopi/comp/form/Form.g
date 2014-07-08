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
header { package com.kopiright.vkopi.comp.form; }
{
  import java.util.Vector;
  import java.util.ArrayList;

  import com.kopiright.compiler.base.Compiler;
  import com.kopiright.compiler.base.PositionedError;
  import com.kopiright.compiler.base.TokenReference;
  import com.kopiright.compiler.tools.antlr.extra.InputBuffer;
  import com.kopiright.kopi.comp.kjc.*;
  import com.kopiright.vkopi.comp.base.*;
  import com.kopiright.vkopi.comp.base.VKEnvironment;
  import com.kopiright.vkopi.comp.trig.GKjcParser;
  import com.kopiright.vkopi.comp.trig.GSqlcParser;
  import com.kopiright.xkopi.comp.sqlc.SimpleIdentExpression;
  import com.kopiright.xkopi.comp.sqlc.TableName;
  import com.kopiright.xkopi.comp.sqlc.TableReference;
  import com.kopiright.xkopi.lib.type.Fixed;
  import com.kopiright.xkopi.lib.type.NotNullFixed;
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
  superClass = "com.kopiright.vkopi.comp.base.Parser";
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

  private final VKEnvironment   environment;
}

public vfCompilationUnit []
  returns [VKWindow self = null]
:
  vkLocaleDeclaration[]
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
  TokenReference        sourceRef = buildTokenReference();	// !!! add comments;
  VKBlock               block;
  VKParseFormContext    context = VKParseFormContext.getInstance(environment);
}
:
  "BLOCK" "INSERT"
  vkContextHeader[context.getCompilationUnitContext()]
  vkDefinitions[context.getDefinitionCollector(), context.getCompilationUnitContext().getPackageName().getName()]
  block = vkBlock[context.getCompilationUnitContext().getPackageName().getName()]
  {
    context.addFormElement(block);
    self = new VKBlockInsert(sourceRef,
                             environment,
                             context.getCompilationUnitContext(),
                             context.getClassContext(),
                             context.getDefinitionCollector(),
                             context.getElements(),
                             getLocale());
  }
  "END" "INSERT"
;

vkBlock [String pkg]
  returns [VKBlock self]
{
  final int		ACCESS = com.kopiright.vkopi.lib.form.VConstants.ACS_MUSTFILL;
  String		name;
  String		ident;
  String		shortcut = null;
  String		title = null;
  int			border = 0;
  String		superName;
  CReferenceType	superBlock = null;
  VKBlockAlign		align = null;
  String		help;
  String		inter;
  int			opts;
  VKBlockIndex[]        indices;
  int			buffer;
  int			vis;
  int[]			access		= new int[] {ACCESS, ACCESS, ACCESS};
  VKParseBlockContext	context = VKParseBlockContext.getInstance();
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
}
:
  "BLOCK" LPAREN buffer = vkInteger[] COMMA vis = vkInteger[] RPAREN
  ident = vkSimpleIdent[] ( COLON shortcut = vkSimpleIdent[] )?
  ( title = vkString[] )?
    { checkLocaleIsSpecifiedIff(title != null, "BLOCK TITLE"); }
  (
    "IS" superName = vkQualifiedIdent[]
      { superBlock = environment.getTypeFactory().createType(superName.replace('.', '/'), false); }
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
  indices = vkBlockIndices[]
  ( vkModeAndCommands[access, context] )?
  ( vkBlockTriggers[context] )?
  ( vkField[context] )+
  ( vkContextFooter[context.getClassContext()] )?
  "END" "BLOCK"
    {
      self = new VKBlock(sourceRef,
                         pkg,
			 context.getClassContext(),
			 context.getInterfaces(),
			 buffer,
			 vis,
			 ident,
                         shortcut,
			 superBlock,
			 title,
			 border,
			 align,
			 help,
			 opts,
			 context.getTables(),
			 indices,
			 access,
			 context.getCommands(),
			 context.getTriggers(),
			 context.getFields(),
			 context.getDropListMap());
      self.addDefaultCommands(buildTokenReference());
      context.release();
    }
;

vkBorder []
  returns [int self]
:
  "BORDER"
  (
    "LINE"	{ self = com.kopiright.vkopi.lib.form.VConstants.BRD_LINE; }
  |
    "RAISED"	{ self = com.kopiright.vkopi.lib.form.VConstants.BRD_RAISED; }
  |
    "LOWERED"	{ self = com.kopiright.vkopi.lib.form.VConstants.BRD_LOWERED; }
  |
    "ETCHED"	{ self = com.kopiright.vkopi.lib.form.VConstants.BRD_ETCHED; }
  )
;

vkImportedBlock [String pkg]
  returns [VKImportedBlock self]
{
  String		ident;
  String                shortcut = null;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "INSERT" ident = vkQualifiedIdent[] ( COLON shortcut = vkSimpleIdent[] )?
    { self = new VKImportedBlock(sourceRef, pkg, ident, shortcut); }
;

vkAccess []
  returns [int self]
:
  "VISIT"	{ self = com.kopiright.vkopi.lib.form.VConstants.ACS_VISIT; }
|
  "MUSTFILL"	{ self = com.kopiright.vkopi.lib.form.VConstants.ACS_MUSTFILL; }
|
  "SKIPPED"	{ self = com.kopiright.vkopi.lib.form.VConstants.ACS_SKIPPED; }
|
  "HIDDEN"	{ self = com.kopiright.vkopi.lib.form.VConstants.ACS_HIDDEN; }
;

vkModeAndCommands[int[] access, VKParseContext context]
{
  int		m = com.kopiright.vkopi.lib.form.VConstants.MOD_ANY;
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
      cmd = vkCommand[com.kopiright.vkopi.lib.form.VConstants.MOD_ANY]
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
	if ((mode & (1 << com.kopiright.vkopi.lib.form.VConstants.MOD_QUERY)) != 0) {
	  self[0] = a;
	}
	if ((mode & (1 << com.kopiright.vkopi.lib.form.VConstants.MOD_INSERT)) != 0) {
	  self[1] = a;
	}
	if ((mode & (1 << com.kopiright.vkopi.lib.form.VConstants.MOD_UPDATE)) != 0) {
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
			      com.kopiright.util.base.Utils.toIntArray(sources),
			      com.kopiright.util.base.Utils.toIntArray(targets));
    }
;

vkBlockEvent []
  returns [int self]
:
  "PREQRY"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREQRY; }
|
  "POSTQRY"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTQRY; }
|
  "PREDEL"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREDEL; }
|
  "POSTDEL"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTDEL; }
|
  "PREINS"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREINS; }
|
  "POSTINS"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTINS; }
|
  "PREUPD"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREUPD; }
|
  "POSTUPD"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTUPD; }
|
  "PRESAVE"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PRESAVE; }
|
  "PREREC"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREREC; }
|
  "POSTREC"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTREC; }
|
  "PREBLK"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREBLK; }
|
  "POSTBLK"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTBLK; }
|
  "VALBLK"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_VALBLK; }
|
  "VALREC"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_VALREC; }
|
  "DEFAULT"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_DEFAULT; }
|
  "INIT"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_INIT; }
|
  "RESET"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_RESET; }
|
  "CHANGED"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_CHANGED; }
|
  "ACCESS"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_ACCESS; }
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

vkBlockIndices []
  returns [VKBlockIndex[] self]
{
  ArrayList     container = new ArrayList();
  VKBlockIndex	index;
}
:
  (
    index = vkBlockIndex[container.size()]
      { container.add(index); }
  )*
    { self = (VKBlockIndex[])container.toArray(new VKBlockIndex[container.size()]); }
;

vkBlockIndex [int count]
  returns [VKBlockIndex self]
{
  String                ident = null;
  String                message = null;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "INDEX"
  ( ident = vkSimpleIdent[] )?
    {
      if (ident == null) {
        checkLocaleIsSpecified("BLOCK INDEX IDENT");
        // if null, create a synthetic identifier
        ident = "Id$" + count;
      }
    }
  ( message = vkString[] )?
    { checkLocaleIsSpecifiedIff(message != null, "BLOCK INDEX MESSAGE"); }
    { self = new VKBlockIndex(sourceRef, ident, message); }
;

vkBlockOptions []
  returns [int self]
{ self = 0; }
:
  (
    "NO"
    (
      "CHART"   { self |= com.kopiright.vkopi.lib.form.VConstants.BKO_NOCHART; }
    |
      "DELETE" { self |= com.kopiright.vkopi.lib.form.VConstants.BKO_NODELETE; }
    |
      "DETAIL"   { self |= com.kopiright.vkopi.lib.form.VConstants.BKO_NODETAIL; }
    |
      "INSERT" { self |= com.kopiright.vkopi.lib.form.VConstants.BKO_NOINSERT; }
    |
      "MOVE"   { self |= com.kopiright.vkopi.lib.form.VConstants.BKO_NOMOVE; }
    )
  |
    "UPDATE" "INDEX" { self |= com.kopiright.vkopi.lib.form.VConstants.BKO_INDEXED; }
  |
    "ACCESS" "ON" "SKIPPED" { self |= com.kopiright.vkopi.lib.form.VConstants.BKO_ALWAYS_ACCESSIBLE; }
  )*
;

vkBlockTables [VKParseBlockContext context]
{
  String                n;
  String                c;
  String                t;
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
  VKFormElement         block;
  VKPage                page;
}
:
  (
    (
      page = vkNewpage[context.getPages().length]
        {
          if (context.getElements().length > 0 && context.getPages().length == 0) {
            reportTrouble(new PositionedError(buildTokenReference(),
                                              FormMessages.MISSING_PAGE));
          }
          context.addPage(page);
        }
    )?
    (
      block = vkBlock[context.getCompilationUnitContext().getPackageName().getName()]
    |
      block = vkImportedBlock[context.getCompilationUnitContext().getPackageName().getName()]
    )
      { context.addFormElement(block); }
  )+
;

vkPosition []
  returns [VKPosition self]
{
  String                field = null;
  int                   line;
  int                   column;
  int                   end = 0;
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
  String                name = null;
  VKPosition            pos = null;
  ArrayList             label = null;
  String		help;
  VKFieldType		type;
  VKFieldColumns	columns = null;
  String[]		dropList = null;
  int			align = com.kopiright.vkopi.lib.form.VConstants.ALG_LEFT;
  int			opts = 0;
  int[]			access;
  String		alias = null;
  int			multiField = 1;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comment
  VKParseFieldContext	context = VKParseFieldContext.getInstance();
}
:
  access = vkFieldSpec[]
  ( LPAREN multiField = vkInteger[] RPAREN )?
  ( name = vkSimpleIdent[] )?
  ( pos = vkPosition[] )?
  ( label = vkFieldLabel[name] )?
  help = vkHelp[]
  type = vfFieldType[]
  (
    "IS" alias = vkQualifiedIdent[]
    ( columns = vkFieldColumns[] )?
  |
    ( align = vkFieldAlign[] )?
    ( dropList = vkFieldDropList[] )?
    opts = vkFieldOptions[]
    ( columns = vkFieldColumns[] )?
    ( vkModeAndCommands[access, context] )?
    ( vkFieldTriggers[context] )?
  )
  "END" "FIELD"
    {
      if (multiField > 1) {
        if (dropList != null) {
          reportTrouble(new PositionedError(sourceRef,
                  FormMessages.MULTIFIELD_DROP_LIST,
                  name));
        }
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
        VKField field = new VKField(sourceRef,
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
                                    alias);
        if (dropList == null) {
          block.addField(field);
        } else {
          if (!(type.getDef() instanceof VKStringType)
              && !(type.getDef() instanceof VKImageType))
          {
            reportTrouble(new PositionedError(sourceRef,
                  FormMessages.UNSUPPORTED_DROP_FIELD_TYPE,
                  name));
          } else {
            String	flavor = block.addDropList(dropList, field);

            if (flavor == null) {
              block.addField(field);
            } else {
              reportTrouble(new PositionedError(sourceRef,
                    FormMessages.DUPLICATE_DROP_FLAVOR,
                    flavor,
                    block.getDropListMap().get(flavor)));
            }
          }
        }
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
  boolean   nullable = false;
  String	name;
  String	ident;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  ( "KEY" { isKey = true; } )?
  ("NULLABLE" { nullable = true; } )?
  name = vkSQLIdent[]
  DOT ident = vkSQLIdent[]
  ( DOT
    { name += "." + ident; }
    ident = vkSQLIdent[]
  )?
    { self = new VKFieldColumn(sourceRef, name, ident, isKey, nullable); }
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
{
  if (self.size() == 2) {
    if (((VKFieldColumn)(self.get(0))).isNullable()) {
                    reportTrouble(new PositionedError(buildTokenReference(),
                            FormMessages.FIRST_COLUMN_NULLABLE));
    }
  }
}
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
  "PREFLD"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREFLD; }
|
  "POSTFLD"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTFLD; }
|
  "POSTCHG"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTCHG; }
|
  "PREVAL"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREVAL; }
|
  "VALFLD"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_VALFLD; }
|
  "VALIDATE"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_VALFLD; }
|
  "DEFAULT"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_DEFAULT; }
|
  "FORMAT"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_FORMAT; }
|
  "ACCESS"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_FLDACCESS; }
|
  "VALUE"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_VALUE; }
|
  "AUTOLEAVE"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_AUTOLEAVE; }
|
  "PREINS"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREINS; }
|
  "PREUPD"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREUPD; }
|
  "PREDEL"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREDEL; }
|
  "POSTINS"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTINS; }
|
  "POSTUPD"	{ self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTUPD; }
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
          reportTrouble(new PositionedError(buildTokenReference(),
                                            FormMessages.INDEX_OUT_OF_RANGE));
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

vkFieldDropList []
  returns [String[] self]
:
  "DROPPABLE" LPAREN self = vkStringList[] RPAREN
;

vkFieldAlign []
  returns [int self]
:
  "ALIGN"
  (
    "LEFT"	{ self = com.kopiright.vkopi.lib.form.VConstants.ALG_LEFT; }
  |
    "RIGHT"	{ self = com.kopiright.vkopi.lib.form.VConstants.ALG_RIGHT; }
  |
    "CENTER"	{ self = com.kopiright.vkopi.lib.form.VConstants.ALG_CENTER; }
  )
;

vkFieldOptions []
  returns [int self]
{
  self = 0;
}
:
  (
    "NOECHO" { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_NOECHO; }
  |
    "NOEDIT" { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_NOEDIT; }
  |
    "SORTABLE" { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_SORT; }
  |
    "TRANSIENT" { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_TRANSIENT; }
  |
    "NO"
    (
      "DELETE" "ON" "UPDATE" { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_DO_NOT_ERASE_ON_LOOKUP; }
    |
      "DETAIL" { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_NODETAIL; }
    |
      "CHART" { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_NOCHART; }
    )

  |
    "QUERY"
      { self &= ~com.kopiright.vkopi.lib.form.VConstants.FDO_SEARCH_MASK; }
    (
      "UPPER"
        { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_SEARCH_UPPER; }
    |
      "LOWER"
        { self |= com.kopiright.vkopi.lib.form.VConstants.FDO_SEARCH_LOWER; }
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

vfFieldType []
  returns [VKFieldType self = null]
{
  VKType                def;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = vfFieldTypeName[]
|
  def = vkPredefinedFieldType[]
    { self = new VKDefinitionType(sourceRef, def); }
;

vfFieldTypeName []
  returns [VKFieldTypeName self]
{
  String		name;
  JExpression[]         params = null;
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
  String                title = null;
  String                ident;
  CReferenceType        superForm = null;
  TokenReference        sourceRef = buildTokenReference();	// !!! add comments;
  VKParseFormContext    context = VKParseFormContext.getInstance(environment);
}
:
  "FORM"
  ( title = vkString[] )?
    { checkLocaleIsSpecifiedIff(title != null, "FORM TITLE"); }
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
  vkDefinitions[context.getDefinitionCollector(), context.getCompilationUnitContext().getPackageName().getName()]
  "BEGIN"
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
                        title,
                        getLocale(),
                        superForm,
                        context.getInterfaces(),
                        context.getCommands(),
                        context.getTriggers(),
                        context.getElements(),
                        context.getPages());
      context.release();
    }
;

vkFormEvent []
  returns [int self]
:
  "PREFORM"
    { self = com.kopiright.vkopi.lib.form.VConstants.TRG_PREFORM; }
|
  "QUITFORM"
    { self = com.kopiright.vkopi.lib.form.VConstants.TRG_QUITFORM; }
|
  "POSTFORM"
    { self = com.kopiright.vkopi.lib.form.VConstants.TRG_POSTFORM; }
|
  "INIT"
    { self = com.kopiright.vkopi.lib.form.VConstants.TRG_INIT; }
|
  "RESET"
    { self = com.kopiright.vkopi.lib.form.VConstants.TRG_RESET; }
|
  "CHANGED"
    { self = com.kopiright.vkopi.lib.form.VConstants.TRG_CHANGED; }
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
      { modes = com.kopiright.vkopi.lib.form.VConstants.MOD_ANY; }
    ( modes = vkModeList[] )?
    cmd = vkCommand[modes]
      { context.addCommand(cmd); }
  )+
;

vkMode []
  returns [int self]
:
  "QUERY"
    { self = 1 << com.kopiright.vkopi.lib.form.VConstants.MOD_QUERY; }
|
  "INSERT"
    { self = 1 << com.kopiright.vkopi.lib.form.VConstants.MOD_INSERT; }
|
  "UPDATE"
    { self = 1 << com.kopiright.vkopi.lib.form.VConstants.MOD_UPDATE; }
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

vkNewpage [int count]
  returns [VKPage self]
{
  String                ident = null;
  String                title = null;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  "NEW" "PAGE"
  ( ident = vkSimpleIdent[])?
    {
      if (ident == null) {
        checkLocaleIsSpecified("PAGE IDENT");
        // if null, create a synthetic identifier
        ident = "Id$" + count;
      }
    }
  ( title = vkString[] )?
    { checkLocaleIsSpecifiedIff(title != null, "PAGE TITLE"); }
    { self = new VKPage(sourceRef, ident, title); }
;
