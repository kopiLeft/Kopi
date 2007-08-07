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

header { package com.kopiright.xkopi.comp.xkjc; }
{
  import java.util.ArrayList;
  import java.util.Vector;

  import com.kopiright.compiler.base.Compiler;
  import com.kopiright.compiler.base.CWarning;
  import com.kopiright.compiler.tools.antlr.extra.InputBuffer;
  import com.kopiright.compiler.base.PositionedError;
  import com.kopiright.compiler.base.TokenReference;
  import com.kopiright.kopi.comp.kjc.*;
  import com.kopiright.xkopi.comp.sqlc.*;
  import com.kopiright.util.base.Utils;
  import com.kopiright.xkopi.lib.type.Fixed;
  import com.kopiright.xkopi.lib.type.NotNullFixed;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class XSqlcParser extends SqlcParser;

options {
  k = 2;				// two token lookahead
  importVocab = XSqlc;			// Call its vocabulary "XSqlc"
  exportVocab = XSqlc;			// Call its vocabulary "XSqlc"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass="com.kopiright.compiler.tools.antlr.extra.Parser";
  access = "private";			// Set default rule access
}
{
  public XSqlcParser(Compiler compiler, InputBuffer buffer, KjcEnvironment environment) {
    super(compiler, new XSqlcScanner(compiler, buffer), MAX_LOOKAHEAD);
    this.environment = environment;
  }

  private XKjcParser buildXKjcParser() {
    return new XKjcParser(getCompiler(), getBuffer(), environment);
  }

  private final KjcEnvironment  environment;
}

// Accessed from XKjc
public xUpdateStatement []
  returns [Statement self = null]
:
  //LCURLY
  (
    self = sDeleteStatement[]
  |
    self = sInsertStatement[]
  |
    self = sUpdateStatement[]
  )
  RCURLY
;

// Accessed from XKjc
public xAnyStatement []
  returns [Statement self = null]
:
  //LCURLY
  (
    self = sSelectStatement[]
  |
    self = sDeleteStatement[]
  |
    self = sInsertStatement[]
  |
    self = sUpdateStatement[]
  )
  RCURLY
;


// Accessed from XKjc
public xSelectIntoStatement[ArrayList types]
 returns [SelectStatement self = null]
{
  String		type = null;
  ArrayList		selectList;
  FromClause		fromClause;
  WhereClause		whereClause = null;
  GroupByClause		groupByClause = null;
  HavingClause		havingClause = null;
  SortSpec		sortSpec = null;
  UpdSpec		updateSpec = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "SELECT"
  ( "ALL" { type = "ALL"; } | "DISTINCT" { type = "DISTINCT"; } )?
  selectList = xSelectIntoList[types]
  fromClause = sFromClause[]
  ( whereClause = sWhereClause[] )?
  ( groupByClause = sGroupByClause[] )?
  ( havingClause = sHavingClause[] )?
  (
    sortSpec = sSortSpec[]
  |
    updateSpec = sUpdSpec[]
  )?
  {
    self = new SelectStatement(sourceRef,
			       type,
			       selectList,
			       fromClause,
			       whereClause,
			       groupByClause,
			       havingClause,
			       sortSpec,
			       updateSpec);
  }
  "INTO"
;

xSelectIntoList [ArrayList types]
  returns [ArrayList self = new ArrayList()]
{
  SelectElem	p;
  CType		t;
}
:
  t = jType[] p = sSelectElem[]
    { types.add(t); self.add(new XTypedSqlExpr(p.getTokenReference(), p, t, true)); }
  (
    COMMA t = jType[] p = sSelectElem[]
      { types.add(t); self.add(new XTypedSqlExpr(p.getTokenReference(), p, t, false)); }
  )*
;

// Accessed from XKjc
public xSelectStatement[]
 returns [XSelectStatement self = null]
{
  String		type = null;
  ArrayList		selectList;
  FromClause		fromClause;
  WhereClause		whereClause = null;
  GroupByClause		groupByClause = null;
  HavingClause		havingClause = null;
  SortSpec		sortSpec = null;
  UpdSpec		updateSpec = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "SELECT"
  ( "ALL" { type = "ALL"; } | "DISTINCT" { type = "DISTINCT"; } )?
  selectList = xTypedSelectList[]
  fromClause = sFromClause[]
  ( whereClause = sWhereClause[] )?
  ( groupByClause = sGroupByClause[] )?
  ( havingClause = sHavingClause[] )?
  (
    sortSpec = sSortSpec[]
  |
    updateSpec = sUpdSpec[]
  )?
  {
    self = new XSelectStatement(sourceRef,
				type,
				selectList,
				fromClause,
				whereClause,
				groupByClause,
				havingClause,
				sortSpec,
				updateSpec);
  }
  RCURLY
;


xTypedSelectList []
  returns [ArrayList self = new ArrayList()]
{
  XTypedSelectElem	p;
  int			n = 1;
}
:
  p = xTypedSelectElem[n] { self.add(p); n++; }
  ( COMMA p = xTypedSelectElem[n] { self.add(p); n++; } )*
;

xTypedSelectElem [int pos]
  returns [XTypedSelectElem self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  CType			type = null;
  Expression		expr;
  FieldReference	field = null;
}
:
  (type = jType[] | "NULL" {type = com.kopiright.kopi.comp.kjc.CStdType.Null;} )
  expr = sExpression[]
  ( "AS" field = sFieldReference[] )?
    {
      if (field != null) {
        self = new XTypedSelectElem(sourceRef, type, expr, field);
      } else if (!(expr instanceof FieldReference)) {
        // no AS given => expr must be a field reference
        reportTrouble(new PositionedError(sourceRef, XSqlcMessages.EXPRESSION_CANNOT_BE_REFERENCED));
      } else {
        self = new XTypedSelectElem(sourceRef, type, null, (FieldReference)expr);
      }
    }
;

sSortElem[]
 returns [SortElem self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  Expression		p1 = null;
  boolean		ifDesc = false;
}
:
  (
    p1 =  sFieldReference[]       
  |
    p1 = sIntegerLiteral[]
  |
    p1 = sJdbcFunction[]
  |
    p1 = xJavaExpression[]
  )
  ( "ASC" | "DESC" { ifDesc = true; } )?
    { self = new SortElem(sourceRef, p1, ifDesc); }
;

sTableName[]
  returns [Expression self = null]
{
  String		ident;
  String		ident2 = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    ident = sIdentifier[] (DOT ident2 = sIdentifier[] {ident += "." + ident2;})?
      { self = new SimpleIdentExpression(sourceRef, ident); }
  |
    COLON BANG LPAREN
    {
      JExpression	expr = buildXKjcParser().xExpressionInSql();
      self = new SqlExprJava(sourceRef, expr, true);
    }
    //RPAREN
  )
;

sAssignment[]
 returns [Assignment self = null]
{
  Expression		p1 = null;
  String		ident;
  String		ident2;
  Expression		p2;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    (
      ident = sIdentifier[]
        (
          { p1 = new SimpleIdentExpression(sourceRef, ident); }
        |
	  DOT ident2 = sIdentifier[]
	   { p1 = new FieldReference(sourceRef, ident, ident2, false); }
        )
     )
  |
    COLON BANG LPAREN
      {
        JExpression	expr = buildXKjcParser().xExpressionInSql();
        p1 = new SqlExprJava(sourceRef, expr, true);
      }
    //RPAREN
  )
  ASSIGN p2 = sExpression[]
    { self = new Assignment(sourceRef, p1, p2); }
;

xJavaExpression []
  returns [Expression self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  boolean	asText = false;
  JExpression	expr = null;
  String	str;
}
:
  COLON ( BANG { asText = true; } )?
  (
    str = jName[]
      { expr = JNameExpression.build(sourceRef, str.replace('.', '/')); }
  |
    LPAREN
    {
      expr = buildXKjcParser().xExpressionInSql();
    }
    //RPAREN
  )
    { self = new SqlExprJava(sourceRef, expr, asText); }
;

xJavaIfExpression []
  returns [JExpression self = null]
:
  {
    self = buildXKjcParser().xExpressionInSql();
  }
;

sSearchCondition []
  returns [SearchCondition self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  Predicate             p;
  String		oper = "";
  boolean		isAnd = false;
  boolean		hasNot = false;
  SearchCondition	right;
}
:
  (
    "NOT" p = sPredicate[]
      {
        self = new SimpleSearchCondition(sourceRef, p);
        self = new NotCondition(sourceRef, self);
      }
  |
    p = sPredicate[]
      {
	self = new SimpleSearchCondition(sourceRef, p);
      }
  )
  (
    (
      "AND" { isAnd = true; oper = "AND"; }
    |
      "OR" { isAnd = false; oper = "OR"; }
    )
    (
      (
        "NOT" {oper += " NOT "; hasNot = true; } )?
        (
          p = sPredicate[]
	    {
	      right = new SimpleSearchCondition(sourceRef, p);
	      if (hasNot) {
		right = new NotCondition(sourceRef, right);
	      }
	      self = isAnd ? (BinarySearchCondition)new AndCondition(sourceRef, self, right)
		: (BinarySearchCondition)new OrCondition(sourceRef, self, right);
	    }
        |
          self = xIfCondition[oper, self]
      )
    )
  )*
;

xIfCondition [String oper, SearchCondition left]
  returns [XIfCondition self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  JExpression		cond;
  Predicate		thenClause;
  Predicate		elseClause = null;
}
:
  KOPI_IF LPAREN cond = xJavaIfExpression[] //RPAREN
  thenClause = sPredicate[]
  ( "else" elseClause = sPredicate[] )?
  RCURLY
    {
      self = new XIfCondition(sourceRef, oper, left, cond,
			      new XSqlExpr(sourceRef, thenClause),
			      elseClause == null ? null : new XSqlExpr(sourceRef, elseClause));
    }
;

sSimplePrimary []
 returns [Expression self = null]
:
  self = sLiteral[]
|
  self = sFieldReference[]
|
  self = sSubTableExpression[]
|
  self = sIfExpression[]
|
  self = sDecodeExpression[]
|
  self = sCaseExpression[]
|
  self = sCountExpression[]
|
  self = sSetFunction[]
|
  self = sJdbcFunction[]
|
  self = xJavaExpression[]
;

// A type specification is a type name with possible brackets afterwards
// (which would make it an array type).
// A type name. which is either a (possibly qualified) class name or
// a primitive type
jType []
  returns [CType self = null]
{
  String	name = null;
  int		bounds = 0;
}
:
  (
    name = jName[]
      { self = environment.getTypeFactory().createType(name.replace('.', '/'), false); }
  |
    self = jPrimitiveType []
  )
  ( LBRACKET RBRACKET { bounds += 1; } )*
    {
      if (bounds > 0) {
	self = new CArrayType(self, bounds);
      }
    }
;

jPrimitiveType []
  returns [CType self = null]
{
  TypeFactory factory = environment.getTypeFactory();
}
:
  "fixed" { self = factory.createReferenceType(XTypeFactory.PRM_PFIXED); }
|
  "month" { self = factory.createReferenceType(XTypeFactory.PRM_PMONTH); }
|
  "date" { self = factory.createReferenceType(XTypeFactory.PRM_PDATE); }
|
  "time" { self = factory.createReferenceType(XTypeFactory.PRM_PTIME); }
|
  "timestamp" { self = factory.createReferenceType(XTypeFactory.PRM_PTIMESTAMP); }
|
  "week" { self = factory.createReferenceType(XTypeFactory.PRM_PWEEK); }
|
  "boolean" { self = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN); }
|
  "byte" { self = factory.getPrimitiveType(TypeFactory.PRM_BYTE); }
|
  "char" { self = factory.getPrimitiveType(TypeFactory.PRM_CHAR); }
|
  "double" { self = factory.getPrimitiveType(TypeFactory.PRM_DOUBLE); }
|
  "float" { self = factory.getPrimitiveType(TypeFactory.PRM_FLOAT); }
|
  "int" { self = factory.getPrimitiveType(TypeFactory.PRM_INT); }
|
  "long" { self = factory.getPrimitiveType(TypeFactory.PRM_LONG); }
|
  "short" { self = factory.getPrimitiveType(TypeFactory.PRM_SHORT); }
|
  "void" { self = factory.getVoidType(); }
;

// A (possibly-qualified) java name.
jName []
  returns [String self = null]
:
  i:STANDARD_ID { self = i.getText(); }
  (
    options {warnWhenFollowAmbig = false;} :
    DOT j:STANDARD_ID { self += "." + j.getText(); }
  )*
;
