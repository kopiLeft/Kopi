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

header { package org.kopi.vkopi.comp.base; }
{
  import java.util.ArrayList;
  import java.util.Vector;

  import org.kopi.compiler.base.Compiler;
  import org.kopi.compiler.base.PositionedError;
  import org.kopi.compiler.base.TokenReference;
  import org.kopi.compiler.tools.antlr.extra.InputBuffer;
  import org.kopi.kopi.comp.kjc.*;
  import org.kopi.util.base.MessageDescription;
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

class BaseParser extends Parser;

options {
  k = 1;                // one token lookahead !
  importVocab = Base;   // Call its vocabulary "VK"
  exportVocab = Base;   // Call its vocabulary "VK"
  codeGenMakeSwitchThreshold = 2; // Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;    // Don't generate parser error handlers
  superClass = "org.kopi.vkopi.comp.base.Parser";
  access = "private";   // Set default rule access
}
{
  public BaseParser(Compiler compiler, InputBuffer buffer, VKEnvironment environment) {
    super(compiler, new BaseScanner(compiler, buffer), MAX_LOOKAHEAD);
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

public vkLocaleDeclaration []
{
  String        locale = null;
}
:
  ( "LOCALE" locale = vkString[] )?
    { setLocale(locale); }
;

public vkCompilationUnit []
  returns [VKInsert self]
:
  vkLocaleDeclaration[]
    {  self = new VKInsert(buildTokenReference(), environment, getLocale()); }
  vkContextHeader[self.getCompilationUnitContext()]
  vkDefinitions[self.getDefinitionCollector(), self.getCompilationUnitContext().getPackageName().getName()]
  EOF
;

vkBool []
  returns [boolean self]
:
  "TRUE" { self = true; }
|
  "FALSE" { self = false; }
;

vkPredefinedFieldType []
  returns [VKType self]
{
  String	       fieldType;
}
:
  (
      self = vkFixnumFieldType[]
    |
      self = vkImageFieldType[]
    |
      self = vkIntegerFieldType[]
    |
      self = vkStringFieldType[]
    |
      self = vkTextFieldType[]
    |
      "DATE"        { self = new VKDateType(buildTokenReference()); }
    |
      "BOOL"        { self = new VKBooleanType(buildTokenReference()); }
    |
      "COLOR"       { self = new VKColorType(buildTokenReference()); }
    |
      "MONTH"       { self = new VKMonthType(buildTokenReference()); }
    |
      "TIME"        { self = new VKTimeType(buildTokenReference()); }
    |
      "TIMESTAMP"   { self = new VKTimestampType(buildTokenReference()); }
    |
      "WEEK"        { self = new VKWeekType(buildTokenReference()); }
  )
  (
    "IMPLEMENTS" fieldType = vkQualifiedIdent[]
    {
      self.setType(environment.getTypeFactory().createType(fieldType.replace('.', '/'), false));
    }
  )?
;

vkStringFieldType []
  returns [VKStringType self]
{
  int                   width;
  int                   height = 1;
  int                   visibleHeight = 0;
  int                   fixed = 0;
  int                   convert = org.kopi.vkopi.lib.form.VConstants.FDO_CONVERT_NONE;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "STRING" LPAREN width = vkInteger[] ( COMMA height = vkInteger[] ( COMMA visibleHeight = vkInteger[] )?)? RPAREN
   (
      "FIXED"
      (
        "ON"    { fixed = org.kopi.vkopi.lib.form.VConstants.FDO_FIX_NL; }
      |
        "OFF"   { fixed = org.kopi.vkopi.lib.form.VConstants.FDO_DYNAMIC_NL; }
      )  
   )?
   (
      "CONVERT"
      (
        "UPPER" { convert = org.kopi.vkopi.lib.form.VConstants.FDO_CONVERT_UPPER; }
      |
        "LOWER" { convert = org.kopi.vkopi.lib.form.VConstants.FDO_CONVERT_LOWER; }
      |
        "NAME"  { convert = org.kopi.vkopi.lib.form.VConstants.FDO_CONVERT_NAME; }
      )
   )?
    { self = new VKStringType(sourceRef, width, height, visibleHeight, fixed | convert); }
;

vkTextFieldType []
  returns [VKTextType self]
{
  int                   width;
  int                   height;
  int                   visibleHeight = 0;
  int                   fixed = org.kopi.vkopi.lib.form.VConstants.FDO_CONVERT_NONE;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "TEXT" LPAREN width = vkInteger[] COMMA height = vkInteger[] ( COMMA visibleHeight = vkInteger[] )? RPAREN
  (
    "FIXED"
    (
      "ON"    { fixed = org.kopi.vkopi.lib.form.VConstants.FDO_FIX_NL; }
    |
      "OFF"   { fixed = org.kopi.vkopi.lib.form.VConstants.FDO_DYNAMIC_NL; }
    )  
  )?
    { self = new VKTextType(sourceRef, width, height, visibleHeight, fixed); }
;

vkImageFieldType []
  returns [VKImageType self]
{
  int                   width;
  int                   height;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "IMAGE" LPAREN width = vkInteger[] COMMA height = vkInteger[] RPAREN
    { self = new VKImageType(sourceRef, width, height); }
;

vkFixnumFieldType []
  returns [VKFixnumType self]
{
  int                   width;
  int                   scale = 0;
  boolean               isFraction = false;
  Fixed                 min = null;
  Fixed                 max = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  (
    "FIXNUM" LPAREN width = vkInteger[] COMMA scale = vkInteger[] RPAREN
  |
    "FRACTION" LPAREN width = vkInteger[] RPAREN
      {
        isFraction = true;
        scale = 6;
      }
  )
  ( "MINVAL" min = vkFixedOrInteger[] )?
  ( "MAXVAL" max = vkFixedOrInteger[] )?
    { 
      self = new VKFixnumType(sourceRef, width, scale, isFraction, min, max);
    }
;

vkIntegerFieldType []
  returns [VKIntegerType self]
{
  int                   width;
  int                   min = Integer.MIN_VALUE;
  int                   max = Integer.MAX_VALUE ;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "LONG" LPAREN width = vkInteger[] RPAREN
  ( "MINVAL" min = vkSignedInteger[] )?
  ( "MAXVAL" max = vkSignedInteger[] )?
    { self = new VKIntegerType(sourceRef, width, 1, min, max); }
;

vkCodeFieldType [String pack, String type]
  returns [VKCodeType self]
{
  VKCodeDesc[]          codes;
  String[]              names;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "ENUM" LPAREN names = vkStringList[] RPAREN
    {
      codes = new VKCodeDesc[names.length];
      for (int i = 0; i < names.length; i++) {
        codes[i] = new VKCodeDesc(sourceRef, "Id$" + i, names[i], names[i]);
      }
      self = new VKStringCodeType(sourceRef, pack, type, codes);
    }
|
  "CODE"
  (
    "BOOL" "IS" codes = vkBooleanCodeList[]
      { self = new VKBooleanCodeType(sourceRef, pack, type, codes); }
  |
    "FIXED" "IS" codes = vkFixnumCodeList[]
      {
        self = new VKFixnumCodeType(sourceRef, pack, type, codes);
      }
  |
    "LONG" "IS" codes = vkIntegerCodeList[]
      { self = new VKIntegerCodeType(sourceRef, pack, type, codes); }
  |
    "STRING" "IS" codes = vkStringCodeList[]
      { self = new VKStringCodeType(sourceRef, pack, type, codes); }
  )
  "END" "CODE"
;

vkBooleanCodeList []
  returns [VKCodeDesc[] self = null]
{
  ArrayList             vect = new ArrayList();
  VKCodeDesc            item;
  TokenReference        sourceRef = buildTokenReference();
}
:
  (
    item = vkBooleanCodeItem[vect.size()]
      { vect.add(item); }
  )+
    { self = (VKCodeDesc[])vect.toArray(new VKCodeDesc[vect.size()]); }
;

vkBooleanCodeItem [int count]
  returns [VKCodeDesc self]
{
  String                label = null;
  String                ident = null;
  boolean               value;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( label = vkString[] ASSIGN )?
    { checkLocaleIsSpecifiedIff(label != null, "CODE LABEL"); }
  ( ident = vkSimpleIdent[] COLON)?
    {
      if (ident == null) {
        checkLocaleIsSpecified("CODE IDENT");
        // if null, create a synthetic identifier
        ident = "Id$" + count;
      }
    }
  value = vkBool[]
    { self = new VKCodeDesc(sourceRef, ident, label, value ? Boolean.TRUE : Boolean.FALSE); }
;

vkFixnumCodeList []
  returns [VKCodeDesc[] self = null]
{
  ArrayList             vect = new ArrayList();
  VKCodeDesc            item;
  TokenReference        sourceRef = buildTokenReference();
}
:
  (
    item = vkFixnumCodeItem[vect.size()]
      { vect.add(item); }
  )+
    { self = (VKCodeDesc[])vect.toArray(new VKCodeDesc[vect.size()]); }
;

vkFixnumCodeItem [int count]
  returns [VKCodeDesc self]
{
  String                label = null;
  String                ident = null;
  Fixed                 value;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( label = vkString[] ASSIGN )?
    { checkLocaleIsSpecifiedIff(label != null, "CODE LABEL"); }
  ( ident = vkSimpleIdent[] COLON)?
    {
      if (ident == null) {
        checkLocaleIsSpecified("CODE IDENT");
        // if null, create a synthetic identifier
        ident = "Id$" + count;
      }
    }
  value = vkFixedOrInteger[]
    { self = new VKCodeDesc(sourceRef, ident, label, value); }
;

vkIntegerCodeList []
  returns [VKCodeDesc[] self = null]
{
  Vector                vect = new Vector();
  VKCodeDesc            item;
  TokenReference        sourceRef = buildTokenReference();
}
:
  (
    item = vkIntegerCodeItem[vect.size()]
      { vect.add(item); }
  )+
    { self = (VKCodeDesc[])vect.toArray(new VKCodeDesc[vect.size()]); }
;

vkIntegerCodeItem [int count]
  returns [VKCodeDesc self]
{
  String                label = null;
  String                ident = null;
  int                   value;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( label = vkString[] ASSIGN )?
    { checkLocaleIsSpecifiedIff(label != null, "CODE LABEL"); }
  ( ident = vkSimpleIdent[] COLON)?
    {
      if (ident == null) {
        checkLocaleIsSpecified("CODE IDENT");
        // if null, create a synthetic identifier
        ident = "Id$" + count;
      }
    }
  value = vkSignedInteger[]
    { self = new VKCodeDesc(sourceRef, ident, label, new Integer(value)); }
;

vkStringCodeList []
  returns [VKCodeDesc[] self = null]
{
  Vector                vect = new Vector();
  VKCodeDesc            item;
  TokenReference        sourceRef = buildTokenReference();
}
:
  (
    item = vkStringCodeItem[vect.size()]
      { vect.add(item); }
  )+
    { self = (VKCodeDesc[])vect.toArray(new VKCodeDesc[vect.size()]); }
;

vkStringCodeItem [int count]
  returns [VKCodeDesc self]
{
  String                label = null;
  String                ident = null;
  String                value;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( label = vkString[] ASSIGN )?
    { checkLocaleIsSpecifiedIff(label != null, "CODE LABEL"); }
  ( ident = vkSimpleIdent[] COLON)?
    {
      if (ident == null) {
        checkLocaleIsSpecified("CODE IDENT");
        // if null, create a synthetic identifier
        ident = "Id$" + count;
      }
    }
  value = vkString[]
    { self = new VKCodeDesc(sourceRef, ident, label, value); }
;

vkFixed []
  returns [Fixed self]
:
  t:REAL_LITERAL { self = new NotNullFixed(t.getText()); }
;

vkFixedOrInteger []
  returns [Fixed self]
{
  int   i;
}
:
  MINUS 
  (
    self = vkFixed[]
     {self = self.negate();}
  |
    t:INTEGER_LITERAL
      { self = new NotNullFixed(Integer.valueOf(t.getText()).intValue() * -1); }
  )
|
  self = vkFixed[]
|
  i = vkInteger[] { self = new NotNullFixed(i); }
;

vkHelp []
  returns [String self = null]
{
  String        line;
}
:
  ( "HELP" line = vkString[] { self = self == null ? line : self + " " + line; } )*
;

vkInteger []
  returns [int self]
:
  t:INTEGER_LITERAL
    { self = Integer.valueOf(t.getText()).intValue(); }
;

vkSignedInteger []
  returns [int self]
{
  int   minus = 1;
}
:
  ( MINUS { minus = -1; } )? t:INTEGER_LITERAL
    { self = Integer.valueOf(t.getText()).intValue() * minus; }
;

vkCommand [int mode]
  returns [VKCommand self]
{
  String                name;
  VKCommandBody         cmd;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "COMMAND"
  (
    name = vkQualifiedIdent[]   { self = new VKCommandName(sourceRef, name, mode); }
  |
    cmd = vkCommandIn[null]     { self = new VKDefaultCommand(sourceRef, mode, cmd); }
    "END" "COMMAND"
  )
;

vkCommandIn [String cmd]
  returns [VKCommandBody self]
{
  String                name;
  String                method;
  VKAction              action;
  TokenReference        sourceRef = buildTokenReference();
  VKParseCommandContext context = new VKParseCommandContext();
}
:
  "ITEM" name = vkSimpleIdent[]
  (
    "ACTION" action = vkAction[cmd]
  |
    "EXTERN" method = vkQualifiedIdent[] // SHORTCUT
      { action = new VKExternAction(sourceRef, method); }
  |
    "CALL" method = vkSimpleIdent[]
      { action = new VKInternAction(sourceRef, method); }
  )
  ( vkCommandTriggers[context] )?
  { self = new VKCommandBody(sourceRef, name, action, context.getTriggers()); }
;

vkCommandTriggers [VKParseCommandContext context]
{
  long		e;
  VKAction	t;
  TokenReference	sourceRef = buildTokenReference();	// !!! add comments;
}
:
  (
   e = vkCommandEventList[] t = vkTriggerAction[]
    { context.addTrigger(new VKTrigger(sourceRef, e, t)); }
  )+
;

vkCommandEvent []
  returns [long self]
:
  "ACCESS"	{ self = org.kopi.vkopi.lib.form.VConstants.TRG_CMDACCESS; }
;

vkCommandEventList []
  returns [long self = 0]
{
  long		e;
}
:
  e = vkCommandEvent[] { self |= (1L << e); }
  ( COMMA e = vkCommandEvent[] { self |= (1L << e); } )*
;

vkListDescs []
  returns [VKListDesc[] self = null]
{
  ArrayList     vect = new ArrayList();
  VKListDesc    list;
}
:
  ( list = vkListDesc[] { vect.add(list); } )+
    { self = (VKListDesc[])vect.toArray(new VKListDesc[vect.size()]); }
;

vkListDesc []
  returns [VKListDesc self]
{
  String                title = null;
  String                column;
  VKType                type;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( title = vkString[] ASSIGN )?
    { checkLocaleIsSpecifiedIff(title != null, "LIST DESC TITLE"); }
  column = vkSimpleIdent[] COLON type = vkFieldType[null, null]
    {
      if (type instanceof VKFixnumType) {
        self = new VKListDesc(sourceRef, title, column, type);
      } else {
        self = new VKListDesc(sourceRef, title, column, type);
      }
    }
;

vkDefinitions [VKDefinitionCollector coll, String pack]
{
  VKDefinition  def;
}
:
  (
    def = vkMenuDef[pack]
      { coll.addMenuDef((VKMenuDefinition)def); }
  |
    def = vkActorDef[pack]
      { coll.addActorDef((VKActor)def); }
  |
    def = vkTypeDef[pack]
      { coll.addFieldTypeDef((VKTypeDefinition)def); }
  |
    def = vkCommandDef[]
      { coll.addCommandDef((VKCommandDefinition)def); }
  |
    vkInsertDefinitions[coll]
  )*
;

vkInsertDefinitions [VKDefinitionCollector coll]
{
  String        name;
}
:
  "INSERT" name = vkString[]
    { coll.addInsert(name, buildTokenReference().getPath()); }
;


vkMenuDef [String pack]
  returns [VKMenuDefinition self]
{
  String                ident;
  String                label = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "MENU" ident = vkSimpleIdent[]
  ( "LABEL" label = vkString[] )?
    {
      checkLocaleIsSpecifiedIff(label != null, "MENU LABEL");
      self = new VKMenuDefinition(sourceRef, pack, ident, label);
    }
  "END" "MENU"
;

vkActorDef [String pack]
  returns [VKActor self]
{
  String                ident;
  String                menu;
  String                label = null;
  String                help;
  String                key = null;
  String                icon = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "ACTOR" ident = vkSimpleIdent[]
  "MENU" menu = vkSimpleIdent[]
  ( "LABEL" label = vkString[] )?
  help = vkHelp[]
  ( "KEY" key = vkString[] )?
  ( "ICON" icon = vkString[] )?
  "END" "ACTOR"
    {
      checkLocaleIsSpecifiedIff(label != null, "ACTOR LABEL");
      checkLocaleIsSpecifiedIff(help != null, "ACTOR HELP");
      self = new VKActor(sourceRef, pack, ident, menu, label, help, key, icon);
    }
;

vkTypeDef [String pack]
  returns [VKTypeDefinition self]
{
  String                name;
  VKType                type;
  VKFieldList           list;
  JFormalParameter[]    params = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "TYPE" name = vkSimpleIdent[]
  (
    LPAREN
      { params = buildGKjcParser().gFormalParameter(JLocalVariable.DES_PARAMETER); }
    // RPAREN
  )?
  "IS"
  type = vkFieldType[pack, name] 
  ( list = vkFieldList[pack, name] { type.addList(list); } )?
  "END" "TYPE"
    { self = new VKTypeDefinition(sourceRef, name, type, params); }
;


vkFieldType[String pack, String name]
  returns [VKType self]
:
  self = vkPredefinedFieldType[]
|
  self = vkCodeFieldType[pack, name]
;

vkFieldList [String pack, String type]
  returns [VKFieldList self]
{
  TableReference        table;
  VKFieldListAction     action = null;
  int                   autocompleteType = VList.AUTOCOMPLETE_NONE;
  int                   autocompleteLength = 0;
  String                name = null;
  boolean               access = false;
  VKListDesc[]          columns;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "LIST"
  table = vkListTable[]
  ( "COMPLETE"
    ( 
      "LEFT" { autocompleteType = VList.AUTOCOMPLETE_STARTSWITH; }
      | 
      "CENTER" { autocompleteType = VList.AUTOCOMPLETE_CONTAINS; } 
    )
    autocompleteLength = vkInteger[]
  )?
  (
    ( "NEW" | "ACCESS" { access = true; } ) (name = vkQualifiedIdent[] | action = vkFieldListAction[])
  )?
  "IS" columns = vkListDescs[]
  "END" "LIST"
    {
      self = new VKFieldList(sourceRef,
                             pack,
                             type,
                             table,
                             action,
                             columns,
                             autocompleteType,
                             autocompleteLength,
                             name == null ? null : environment.getTypeFactory().createType(name.replace('.', '/'), false),
                             access);
    }
;

vkFieldListAction[]
  returns [VKFieldListAction self]
{
  JStatement[]          stmts;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LCURLY
    {
      stmts = buildGKjcParser().gAction();
      self = new VKFieldListAction(sourceRef, new JCompoundStatement(sourceRef, stmts));
    }
  // RCURLY
;

vkListTable[]
  returns [TableReference self]
{
  String                name;
  TokenReference        sourceRef = buildTokenReference();
}
:
  name = vkString[]
    { self = new TableName(sourceRef, new SimpleIdentExpression(sourceRef, name), null); }
|
  LCURLY
    { self = buildGSqlcParser().gSimpleTableReference(); }
  //RCURLY
;

vkCommandDef []
  returns [VKCommandDefinition self]
{
  VKCommandBody         cmd;
  String                name;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "COMMAND" name = vkSimpleIdent[]
  cmd = vkCommandIn[name]
  "END" "COMMAND"
    { self = new VKCommandDefinition(sourceRef, name, cmd); }
;

vkSimpleIdent []
  returns [String self]
:
  t:IDENT
    { self = t.getText(); }
;

vkQualifiedIdent []
  returns [String self]
:
  t:IDENT { self = t.getText(); }
  ( DOT tt:IDENT { self += "." + tt.getText(); } )*
;

vkSQLIdent[] returns [String self]:
    t:IDENT 
      { self = t.getText(); }
  | 
    v:STRING_LITERAL
      { self = v.getText(); }
;

vkString []
  returns [String self]
:
  t:STRING_LITERAL
    { self = t.getText(); }
;

vkStringList []
  returns [String[] self = null]
{
  String        str;
  ArrayList     vect = new ArrayList();
}
:
  str = vkString[] { vect.add(str); }
  ( COMMA str = vkString[] { vect.add(str); } )*
    { self = (String[])vect.toArray(new String[vect.size()]); }
;

vkContextFooter[CParseClassContext context]
:
  LCURLY
  {
     buildGKjcParser().gFooter(context);
  }
  // RCURLY
;

vkContextHeader[CParseCompilationUnitContext context]
:
  LCURLY
  {
    buildGKjcParser().gHeader(context);
  }
  // RCURLY
;

vkTriggerAction []
  returns [VKAction self]
{
  String                name;
  TokenReference        sourceRef = buildTokenReference();
}
:
    self = vkAction[null]
  |
    name = vkSimpleIdent[]
      { self = new VKInternAction(sourceRef, name); }
;

vkAction [String name]
  returns [VKAction self]
{
  JStatement[]          stmts;
  String                method;
  TokenReference        sourceRef = buildTokenReference();
}
:
    LPAREN
      {
        ArrayList         params = new ArrayList(1);
      
        stmts = buildGKjcParser().gActionWithParameter(name, params);
        if (params.size() == 1) {
          self = new VKMethodAction(sourceRef,
                                    new JCompoundStatement(sourceRef, stmts),
                                    (JFormalParameter[])params.toArray(new JFormalParameter[params.size()]),
                                    name);
        } else {
         // OLD SYNTAX !!!
         self = new VKBlockAction(sourceRef,
                                  new JCompoundStatement(sourceRef,stmts),
                                  name);
        }
      }
  |
    LCURLY
      {
        stmts = buildGKjcParser().gAction();
        self = new VKBlockAction(sourceRef,
                                 new JCompoundStatement(sourceRef,stmts),
                                 name);
      }
    // RCURLY
  |
    "EXTERN" method = vkQualifiedIdent[]
      { self = new VKExternAction(sourceRef, method); }
;
