/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

header { package com.kopiright.xkopi.comp.dbi; }
{
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Vector;

  import com.kopiright.compiler.base.CWarning;
  import com.kopiright.compiler.base.Compiler;
  import com.kopiright.compiler.tools.antlr.extra.InputBuffer;
  import com.kopiright.compiler.base.PositionedError;
  import com.kopiright.compiler.base.TokenReference;
  import com.kopiright.xkopi.comp.sqlc.*;
  import com.kopiright.util.base.Utils;
  import com.kopiright.xkopi.lib.type.Fixed;
  import com.kopiright.xkopi.lib.type.NotNullFixed;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class DbiParser extends SqlcParser;

options {
  k = 2;				// two token lookahead
  importVocab = Dbi;			// Call its vocabulary Dbi
  exportVocab = Dbi;			// Call its vocabulary Dbi
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass = "com.kopiright.compiler.tools.antlr.extra.Parser";
  access = "private";			// Set default rule access
}
{
  public DbiParser(Compiler compiler, InputBuffer buffer) {
    super(compiler, new DbiScanner(compiler, buffer), MAX_LOOKAHEAD);
  }
}

public makeDB []
  returns [SCompilationUnit self = null]
{
  self = new SCompilationUnit(buildTokenReference());

  com.kopiright.xkopi.comp.sqlc.Statement statement = null;
}
:
  ( scriptHead[self] )?

  ( 
    statement = statement[] { self.addStmt(statement); }
  |
    SEMICOLON
  ) +
  EOF
;

public statement []
returns [com.kopiright.xkopi.comp.sqlc.Statement self = null]
:
  (
    self = dataDefinitionLanguageStatement[]
  |
    self = dataManipulationLanguageStatement[]
  )
;

scriptHead [SCompilationUnit self]
{
  TokenReference	sourceRef = buildTokenReference();
  String		versionLiteral = null;
  String		pack = null;
  int			version = -1;
  String		comment = null;
}
:
  "UPDATE" pack = sString[]
  "VERSION" ASSIGN version = sInteger[]
  "DESC" ASSIGN comment = sString[]
    {
      self.addStmt(new ScriptHeadStatement(sourceRef, pack, version, comment));
    }
;

/**
 * This is the main entry point for the grammar
 */
dataDefinitionLanguageStatement []
  returns [DbiStatement self = null]
:
  (
    self = sequenceDefinition[]
  |
    self = dropSequenceStatement[]
  |
    self = tableDefinition[]
  |
    self = viewDefinition[]
  |
    self = dropViewStatement[]
  |
    self = alterTableStatement[]
  |
    self = dropTableStatement[]
  |
    self = indexDefinition[]
  |
    self = dropIndexStatement[]
  |
    self = grantStatement[]
  |
    self = spoolStatement[]
  )
    {
      self.setComment(getComment());
    }
;

spoolStatement []
  returns [DbiStatement self = null]
:
  "SPOOL"
  (
    self = spoolTableStatement[]
  |
    self = spoolFileStatement[]
  )
;

spoolTableStatement []
  returns [SpoolTableStatement self = null]
{
  NullDelimiterSpec	spec;
  boolean		hasSorted = false;
  boolean		hasLocal = false;
  String		fileName;
  Expression		tableName;
  TokenReference	sourceRef = buildTokenReference();
}
:
  tableName = sTableName[]
  "FROM" ("SORTED" { hasSorted = true; } )?
  fileName = fileName[] ("LOCAL" { hasLocal = true; } )?
  spec = nullDelimiterSpec[]
    { self = new SpoolTableStatement(sourceRef, tableName, hasSorted, fileName, hasLocal, spec); }
;

spoolFileStatement []
  returns [SpoolFileStatement self = null]
{
  NullDelimiterSpec	spec;
  String		fileName;
  SelectStatement	select;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "INTO" fileName = fileName[]
  spec = nullDelimiterSpec[]
  select = sSelectStatement[]
    { self = new SpoolFileStatement(sourceRef, fileName, spec, select); }
;

nullDelimiterSpec []
  returns [NullDelimiterSpec self = null]
{
  NullSpec		nullSpec = null;
  DelimSpec		delimSpec = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    nullSpec = nullSpec[]
    //!!! check that not redefined
  |
    delimSpec = delimSpec[]
    //!!! check that not redefined
  )*
    { self = new NullDelimiterSpec(sourceRef, nullSpec, delimSpec); }
;

nullSpec []
  returns [NullSpec self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  String		spec = null;
}
:
  "NULL" ( "IS" )? spec = sString[]
    { self = new NullSpec(sourceRef, spec); }
;

delimSpec []
  returns [DelimSpec self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  String		spec = null;
}
:
  "DELIM" ( "IS" )?
  (
    "TAB" { spec = "TAB"; }
  |
    spec = sString[]
  )
    { self = new DelimSpec(sourceRef, spec); }
;

fileName[]
  returns [String self = null]
:
  t1 : DELIMITER_ID { self = t1.getText(); }
|
  self = sString[]
;

grantStatement []
  returns [DbiStatement self = null]
:
  "GRANT"
  (
    self = grantUserClassStatement[]
  |
    self = grantPrivilegeStatement[]
  )
;

grantUserClassStatement []
  returns [GrantUserClassStatement self = null;]
{
  int			userClass = GrantUserClassStatement.TYP_ACCESS;
  String		userName;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    "ACCESS" { userClass = GrantUserClassStatement.TYP_ACCESS; }
  |
    "RESOURCE" { userClass = GrantUserClassStatement.TYP_RESOURCE; }
  |
    "DBA" { userClass = GrantUserClassStatement.TYP_DBA; }
  )
  "TO" userName = sIdentifier[]
    { self = new GrantUserClassStatement(sourceRef, userClass, userName); }
;

grantPrivilegeStatement []
  returns [GrantPrivilegeStatement self = null]
{
  Expression		tableName;
  ArrayList		privilege;
  ArrayList		userList;
  boolean		hasGrantOption = false;
  TokenReference	sourceRef = buildTokenReference();
}
:
  privilege = privileges[]
  "ON" tableName = sTableName[]
  "TO"  userList = userList[]
  ("WITH" "GRANT" "OPTION" { hasGrantOption = true; } )?
  { self = new GrantPrivilegeStatement(sourceRef, tableName, privilege, userList, hasGrantOption); }
;

privileges []
  returns [ArrayList self = null]
{
  self = new ArrayList();
  TablePrivilege	privilege;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "ALL"
    {
      privilege = new TablePrivilege(sourceRef, TablePrivilege.TYP_ALL, null);
      self.add(privilege);
    }
|
  privilege = privilege[] { self.add(privilege); }
  (COMMA privilege = privilege[] { self.add(privilege); } )*
;

privilege []
  returns [TablePrivilege self = null]
{
  int			privilege = TablePrivilege.TYP_SELECT;;
  FieldNameList		field = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
(
  "SELECT" { privilege = TablePrivilege.TYP_SELECT; }
|
  "INSERT" { privilege = TablePrivilege.TYP_INSERT; }
|
  "DELETE" { privilege = TablePrivilege.TYP_DELETE; }
|
  "UPDATE" (LPAREN field = sFieldNameList[] RPAREN)?
    { privilege = TablePrivilege.TYP_UPDATE; }
)
  { self = new TablePrivilege(sourceRef, privilege, field); }
;

userList []
  returns [ArrayList self]
{
  self = new ArrayList();
  String	user;
}
:
  user = userIdent[] { self.add(user); }
  (COMMA user = userIdent[] { self.add(user); } )*
;

userIdent []
  returns [String self = null]
:
  "PUBLIC" { self = "PUBLIC"; }
|
  self = sIdentifier[]
;

alterTableStatement []
  returns [DbiStatement self = null]
{
  Expression		tableName;
  Type			type;
  String		columnName;
  String		constraintName = null;
  TableConstraint	tableConstraint;
  TokenReference	sourceRef = buildTokenReference();
  Expression		defaultValue;
  Column        column = null;
}
:
  "ALTER" "TABLE" tableName = sTableName[]
  (
    // Alter definition
    "ALTER" columnName = sIdentifier[]
    (
      "SET" "DEFAULT" defaultValue = sExpression[]
        { self = new AlterSetDefaultColumnStatement(sourceRef, tableName, columnName, defaultValue); }
    |
      "DROP" "DEFAULT"
        { self = new  AlterDropDefaultColumnStatement(sourceRef, tableName, columnName); }
    |
      "NOT" "NULL"
        { self = new  AlterNotNullColumnStatement(sourceRef, tableName, columnName); }
    )
  |
    // Add definition
    "ADD" 
    (
      "CONSTRAINT" tableConstraint = tableConstraint[]
        { self = new AddTableConstraintStatement(sourceRef, tableName, tableConstraint); }
    |
      "COLUMN" LPAREN column = columnDefinition [] RPAREN
        { self = new AddTableColumnsStatement(sourceRef, tableName, column); }
    )
  |
    // drop definition
    "DROP" "CONSTRAINT" constraintName = sIdentifier[]
      { self = new DropTableConstraintStatement(sourceRef, tableName, constraintName); }
  )
;

indexDefinition []
  returns [IndexDefinition self = null]
{
  ArrayList		indexElemList = new ArrayList();
  IndexElem		indexElem;
  boolean		hasUnique = false;
  Expression		tableName;
  String		indexName;
  int			type = IndexDefinition.TYP_NONE;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "CREATE" ("UNIQUE" { hasUnique = true; } )? "INDEX" indexName = sIdentifier[]
  "ON" tableName = sTableName[]
  LPAREN
  indexElem = indexElem[]
    { indexElemList.add(indexElem); }
  (
    COMMA indexElem = indexElem[]
       { indexElemList.add(indexElem); }
  )*
  RPAREN
  (
    "WITH"
    (
      "PRIMARY" { type = IndexDefinition.TYP_PRIMARY; }
    |
      "DISALLOW" "NULL"  { type = IndexDefinition.TYP_DISALLOW; }
    |
      "IGNORE" "NULL" { type = IndexDefinition.TYP_IGNORE; }
    )
  )?
    { self = new IndexDefinition(sourceRef, hasUnique, indexName, tableName, indexElemList, type); }
;

indexElem[]
  returns [IndexElem self = null]
{
  String		nameElem;
  boolean		isDesc = false;
  TokenReference	sourceRef = buildTokenReference();
}
:
  nameElem = sIdentifier[]
  (
    "ASC" { isDesc = false; }
  |
    "DESC" { isDesc = true; }
  )?
    { self = new IndexElem(sourceRef, nameElem, isDesc); }
;

sequenceDefinition[]
  returns [SequenceDefinition self = null]
{
  Expression		tableName;
  TokenReference	sourceRef = buildTokenReference();
  Integer               startValue = null;
  int                   value;
  Vector		comments = null;
}
:
  "CREATE" "SEQUENCE" tableName = sTableName[] 
  ( 
    "START" "WITH" value = sInteger[]
  { startValue = new Integer(value); }
  )?
  {
    comments = getComment();
    self = new SequenceDefinition(sourceRef, tableName, startValue);
    self.setComment(comments);
  }
;

tableDefinition []
  returns [TableDefinition self = null]
{
  Expression		tableName;
  ArrayList		columns;
  String		keyName;
  List		    keyList = new ArrayList();
  Vector		comments = null;
  Key			key = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "CREATE" "TABLE" tableName = sTableName[]
  {
    comments = getComment();
  }
  LPAREN columns = columnDefinitions [] RPAREN
  (
    "KEY" "IS" keyName = sIdentifier[] { keyList.add(keyName); }
    ( COMMA keyName = sIdentifier[] { keyList.add(keyName); } )*
      { key = new Key(sourceRef, keyList); }
  )?
    {
      self = new TableDefinition(sourceRef, tableName, columns, key);
      self.setComment(comments);
    }
;

viewDefinition []
  returns [ViewDefinition self = null]
{
  Expression		tableName;
  ViewColumn		viewColumn = null;
  ArrayList		columns = new ArrayList();
  Vector		comments = null;
  TableReference	reference;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "CREATE" "VIEW" tableName = sTableName[]
  {
    comments = getComment();
  }
  LPAREN
  viewColumn = viewColumn[] { columns.add(viewColumn); }
  ( COMMA viewColumn = viewColumn[] { columns.add(viewColumn); } )*
  RPAREN
  "AS"
  LPAREN
  reference = sTableExpression[]
  RPAREN
    {
      self = new ViewDefinition(sourceRef, tableName, columns, reference);
      self.setComment(comments);
    }
;

viewColumn []
  returns [ViewColumn self = null]
{
  String		name;
  Type			type;
  boolean		nullable = true;
  TokenReference	sourceRef = buildTokenReference();
}
:
  name = sIdentifier[] type = sDataType[] ("NOT" "NULL" { nullable = false; } )?
    {
      self = new ViewColumn(sourceRef, name, type, nullable);
      self.setComment(getComment());
    }
;

columnDefinitions []
  returns [ArrayList self = new ArrayList()]
{
  Column        column;
}
:
  column = columnDefinition[] { self.add(column); }
  (COMMA column = columnDefinition[] { self.add(column); } )*
;

columnDefinition []
  returns [Column self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  String		name = null;
  Type			type = null;
  boolean		nullable = true;
  Expression    defaultValue = null;
  String		constraintName = null;
}
:
  name = sIdentifier[] type = sDataType[]
  ("NOT" "NULL" { nullable = false; } )?
  ( "DEFAULT" defaultValue = sExpression[])?
  ("CONSTRAINT" constraintName = sIdentifier[])?
    {
      self = new Column(sourceRef, name, type, nullable, defaultValue, constraintName);
      self.setComment(getComment());
    }
;

dropViewStatement []
  returns [DropViewStatement self = null]
{
  Expression		tableName;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "DROP" "VIEW" tableName = sTableName[]
    { self = new DropViewStatement(sourceRef,tableName); }
;

dropTableStatement []
  returns [DropTableStatement self = null]
{
  Expression		tableName;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "DROP" "TABLE" tableName = sTableName[]
    { self = new DropTableStatement(sourceRef,tableName); }
;

dropSequenceStatement []
  returns [DropSequenceStatement self = null]
{
  Expression		sequenceName;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "DROP" "SEQUENCE" sequenceName = sTableName[]
    { self = new DropSequenceStatement(sourceRef, sequenceName); }
;

dropIndexStatement []
  returns [DropIndexStatement self = null]
{
  String		indexName;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "DROP"  "INDEX" indexName = sIdentifier[]
    { self = new DropIndexStatement(sourceRef, indexName); }
;

columnConstraintDefinition []
:
  constraintNameDefinition[]
  columnConstraint[]
	{
		// !!! 000911 coco warning at the compilation because return nothing
	}
;

columnConstraint []
:
  uniqueSpecification[]
|
  "REFERENCES" referencedTableAndColumns[]
	{
		// !!! 000911 coco warning at the compilation because return nothing
	}
;

constraintNameDefinition []
:
  "CONSTRAINT" sIdentifier[]
	{
		// !!! 000911 coco warning at the compilation because return nothing
	}
;

tableConstraintDefinition []
:
  constraintNameDefinition[]
  tableConstraint[]
	{
		// !!! 000911 coco warning at the compilation because return nothing
	}
;

tableConstraint []
  returns [TableConstraint self = null]
:
  self = uniqueConstraintDefinition[]
|
  self = referentialConstraintDefinition[]
;

uniqueConstraintDefinition []
  returns [UniqueConstraintDefinition self = null]
{
  int	type;
  FieldNameList			field = null;
  TokenReference		sourceRef = buildTokenReference();
}
:
  type = uniqueSpecification[] LPAREN field = sFieldNameList[] RPAREN
    { self = new UniqueConstraintDefinition(sourceRef, type, field); }
;

uniqueSpecification []
  returns [int self = UniqueConstraintDefinition.TYP_UNIQUE;]
:
  "UNIQUE" { self =  UniqueConstraintDefinition.TYP_UNIQUE;}
|
  "PRIMARY" "KEY" { self =  UniqueConstraintDefinition.TYP_PRIMARY;}
;

referentialConstraintDefinition []
  returns [ReferentialConstraintDefinition self = null]
{
  FieldNameList			field;
  String			name;
  ReferencedTableAndColumns	reference;
  TokenReference		sourceRef = buildTokenReference();
  int				type = ReferentialConstraintDefinition.TYP_RESTRICT;
}
:
  name = sIdentifier[]
  "FOREIGN" "KEY" LPAREN field = sFieldNameList[] RPAREN
  "REFERENCES" reference = referencedTableAndColumns[]
  ("ON" "DELETE")?
  (
    "RESTRICT"
  |
    "CASCADE"
    { type = ReferentialConstraintDefinition.TYP_CASCADE; }
  |
    "SET" "NULL"
    { type = ReferentialConstraintDefinition.TYP_NULL; }
  |
    "SET" "DEFAULT"
    { type = ReferentialConstraintDefinition.TYP_DEFAULT; }
  )
    { self = new ReferentialConstraintDefinition(sourceRef, name, field, reference, type); }
;

referencedTableAndColumns []
  returns [ReferencedTableAndColumns self = null]
{
  FieldNameList		field;
  Expression		tableName;
  TokenReference	sourceRef = buildTokenReference();
}
: tableName = sTableName[]
  LPAREN field  = sFieldNameList[] RPAREN
    { self = new ReferencedTableAndColumns(sourceRef, tableName, field); }
;

sDataType []
 returns [Type self = null]
{
  TokenReference	sourceRef = buildTokenReference();
}
:
  self = dImageType []
|
  self = dStringType []
|
  self = dTextType[]
|
  self = dFixedType[]
|
  self = dIntegerType[]
|
  self = dEnumType[]
|
  self = dCodeType[]
|
  "DATE" { self = new DateType(sourceRef); }
|
  "BLOB" { self = new BlobType(sourceRef); }
|
  "CLOB" { self = new ClobType(sourceRef); }
|
  "BOOL" { self = new BooleanType(sourceRef); }
|
  "COLOR" { self = new ColorType(sourceRef); }
|
  "MONTH" { self = new MonthType(sourceRef); }
|
  "TIME" { self = new TimeType(sourceRef); }
|
  "TIMESTAMP" { self = new TimestampType(sourceRef); }
|
  "WEEK" { self = new WeekType(sourceRef); }
;

dIntegerType []
  returns [Type self = null]
{
  int			size = -1;
}
:
  self = dIntType[]
|
  self = dShortType[]
|
  self = dByteType[]
;

dIntType []
  returns [IntType self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  boolean		hasMin = false;
  int			minVal = -1;
  boolean		hasMax = false;
  int			maxVal = -1;
}
:
  "INT"
  (
    "MIN" minVal = dSignedInteger[]
      {
	//!!! graf 000812 check that minVal >= Int.MIN
	//!!! graf 000905 difficile de tester (type de minVal: int)
	//!!!             mais pour short et byte, ca doit marcher
	if (minVal >= Integer.MIN_VALUE) {
	  reportTrouble(new CWarning(sourceRef, DbiMessages.INT_TOO_SMALL, new Integer(minVal), new Integer(Integer.MIN_VALUE)));
	} else {
	  hasMin = true;
	}
      }
  )?
  (
    "MAX" maxVal = dSignedInteger[]
      {
	//!!! graf 000812 check that maxVal <= Int.MAX
	if (maxVal <= Integer.MAX_VALUE) {
	  reportTrouble(new CWarning(sourceRef, DbiMessages.INT_TOO_BIG, new Integer(maxVal), new Integer(Integer.MIN_VALUE)));
	} else {
	  hasMax = true;
	}
      }
  )?
    {
      self = new IntType(sourceRef,
			 hasMin ? new Integer(minVal) : null,
			 hasMax ? new Integer(maxVal) : null);
    }
;

dShortType []
  returns [ShortType self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  boolean		hasMin = false;
  int			minVal = -1;
  boolean		hasMax = false;
  int			maxVal = -1;
}
:
  "SHORT"
  (
    "MIN" minVal = dSignedInteger[]
      {
	//!!! graf 000812 check that minVal >= Short.MIN
	if (minVal >= Short.MIN_VALUE) {
	  reportTrouble(new CWarning(sourceRef, DbiMessages.SHORT_TOO_SMALL, new Integer(minVal), new Integer(Short.MIN_VALUE)));
	} else {
	  hasMin = true;
	}
      }
  )?
  (
    "MAX" maxVal = dSignedInteger[]
      {
	//!!! graf 000812 check that maxVal <= Short.MAX
	if (maxVal <= Short.MAX_VALUE) {
	  reportTrouble(new CWarning(sourceRef, DbiMessages.SHORT_TOO_BIG, new Integer(maxVal), new Integer(Short.MAX_VALUE)));
	} else {
	  hasMax = true;
	}
      }
  )?
    {
      self = new ShortType(sourceRef,
			   hasMin ? new Integer(minVal) : null,
			   hasMax ? new Integer(maxVal) : null);
    }
;

dByteType []
  returns [ByteType self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  boolean		hasMin = false;
  int			minVal = -1;
  boolean		hasMax = false;
  int			maxVal = -1;
}
:
  "BYTE"
  (
    "MIN" minVal = dSignedInteger[]
      {
	//!!! graf 000812 check that minVal >= Byte.MIN
	if (minVal >= Byte.MIN_VALUE) {
	  reportTrouble(new CWarning(sourceRef, DbiMessages.BYTE_TOO_SMALL, new Integer(minVal), new Integer(Byte.MIN_VALUE)));
	} else {
	  hasMin = true;
	}
      }
  )?
  (
    "MAX" maxVal = dSignedInteger[]
      {
	//!!! graf 000812 check that maxVal <= Byte.MAX
	if (maxVal <= Byte.MAX_VALUE) {
	  reportTrouble(new CWarning(sourceRef, DbiMessages.BYTE_TOO_BIG, new Integer(maxVal), new Integer(Byte.MAX_VALUE)));
	} else {
	  hasMax = true;
	}
      }
  )?
    {
      self = new ByteType(sourceRef,
			  hasMin ? new Integer(minVal) : null,
			  hasMax ? new Integer(maxVal) : null);
    }
;

dSignedInteger []
  returns [int self = -1]
{
  int	sign = 1;
  int	value;
}
:
  ( MINUS { sign = -1; } )? value = sInteger[]
    { self = sign * value; }
;

dImageType []
  returns [ImageType self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  int			l;
  int			r;
}
:
  "IMAGE" LPAREN l = sInteger[] COMMA r = sInteger[] RPAREN
  { self = new ImageType(sourceRef, l, r); }
;

dStringType []
  returns [StringType self = null]
{
  boolean		fixed = false;
  int			width = -1;
  int			height = 1;
  int			convert = StringType.TYP_NONE;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    "STRING"
      { fixed = false; width = -1; }
  |
    "CHAR"
      { fixed = true; width = 1; }
  )
  (
    LPAREN
    width = sInteger[]
    ( COMMA height = sInteger[] )?
    RPAREN
  )?
  (
    "CONVERT"
    (
      "UPPER" { convert = StringType.TYP_UPPER; }
    |
      "LOWER" { convert = StringType.TYP_LOWER; }
    |
      "NAME"  { convert = StringType.TYP_NAME; }
    )
  )?
    {
      if (width == -1) {
	reportTrouble(new CWarning(sourceRef, DbiMessages.STRING_SIZE));
      }
      self = new StringType(sourceRef, fixed, width, height, convert);
    }
;

dTextType []
  returns [TextType self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  int			width;
  int			height;
}
:
  "TEXT" LPAREN width = sInteger[] COMMA height = sInteger[] RPAREN
    { self = new TextType(sourceRef, width, height); }
;

dFixedType []
  returns [FixedType self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  Fixed			min = null;
  Fixed			max = null;
  int			prec;
  int			scale;
}
:
  "FIXED" LPAREN prec = sInteger[] COMMA scale = sInteger[] RPAREN
  ( "MIN" min = dFixedOrInteger[] )?
  ( "MAX" max = dFixedOrInteger[] )?
    { self = new FixedType(sourceRef, prec, scale, min, max); }
;

dEnumType []
  returns [EnumType self = null]
{
  ArrayList		stringList = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "ENUM" LPAREN stringList = dStringList[] RPAREN
    { self = new EnumType(sourceRef, stringList); }
;

dStringList []
  returns [ArrayList self = new ArrayList()]
{
  String	elem = null;
}
:
  elem = sString[]
    { self.add(elem); }
  (
    COMMA elem = sString[]
      { self.add(elem); }
  )*
;

dFixedOrInteger []
  returns [Fixed self = null]
{
  //!!!! graf 000812 and as fixed ???
  int		i;
}
:
  i = dSignedInteger[]
    { self = new NotNullFixed(i); }
;

dCodeType []
  returns [Type self = null]
{
  ArrayList		list = null;
  TokenReference	sourceRef = buildTokenReference();
  int	prec;
  int	scale;
  //!!! graf 000812 split dCodeList wrt type
}
:
  "CODE"
  (
    "BOOL" "IS" list = dCodeBoolList[] { self = new CodeBoolType(sourceRef, list); }
  |
    "LONG" "IS" list = dCodeLongList[] { self = new CodeLongType(sourceRef, list); }
  |
    "FIXED" LPAREN prec = sInteger[] COMMA scale = sInteger[] RPAREN "IS" list = dCodeFixedList[] { self = new CodeFixedType(sourceRef, prec, scale, list); }
  )
  "END" "CODE"
;

dCodeBoolList []
  returns [ArrayList self = new ArrayList()]
{
  String		name;
  Boolean		value;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    name = sString[] ASSIGN value = dCodeBoolValue[]
      { self.add(new CodeBoolDesc(sourceRef, name, value)); }
  )+
;

dCodeLongList []
  returns [ArrayList self = new ArrayList()]
{
  String		name;
  Integer		value;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    name = sString[] ASSIGN value = dCodeLongValue[]
      { self.add(new CodeLongDesc(sourceRef, name, value)); }
  )+
;

dCodeFixedList []
  returns [ArrayList self = new ArrayList()]
{
  String		name;
  Fixed			value;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    name = sString[] ASSIGN value = dCodeFixedValue[]
      { self.add(new CodeFixedDesc(sourceRef, name, value)); }
  )+
;

dCodeBoolValue []
  returns [Boolean self = null]
:
  self = dBool[]
;

dCodeLongValue []
  returns [Integer self = null]
{
  int		i;
}
:
  i = sInteger[] { self = new Integer(i); }
;

dCodeFixedValue []
  returns [Fixed self = null]
:
  self = dFixed[]
;

dBool []
  returns [Boolean self = null]
:
  "TRUE" { self = Boolean.TRUE; }
|
  "FALSE" { self = Boolean.FALSE; }
;

dFixed []
  returns [Fixed self = null]
:
  t:NUMERIC_LIT { self = new NotNullFixed(t.getText()); }
;
