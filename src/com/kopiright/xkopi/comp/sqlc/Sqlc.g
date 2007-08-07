/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that Ssinsertit will be useful,
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

header { package com.kopiright.xkopi.comp.sqlc; }
{
  import java.util.ArrayList;
  import java.util.Vector;

  import com.kopiright.compiler.base.CWarning;
  import com.kopiright.compiler.base.Compiler;
  import com.kopiright.compiler.base.PositionedError;
  import com.kopiright.compiler.base.TokenReference;
  import com.kopiright.compiler.tools.antlr.extra.InputBuffer;
  import com.kopiright.util.base.Utils;
  import com.kopiright.xkopi.lib.type.NotNullFixed;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class SqlcParser extends Parser;

options {
  k = 2;                                // two token lookahead
  importVocab = Sqlc;                   // Call its vocabulary "Sqlc"
  exportVocab = Sqlc;                   // Call its vocabulary "Sqlc"
  codeGenMakeSwitchThreshold = 2;       // Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;          // Don't generate parser error handlers
  access = "private";                   // Set default rule access
  superClass = "com.kopiright.compiler.tools.antlr.extra.Parser";
}
{
  public SqlcParser(Compiler compiler, InputBuffer buffer) {
    super(compiler, new SqlcScanner(compiler, buffer), MAX_LOOKAHEAD);
  }
}

/**
 * This is the main entry point for the grammar
 */
public sCompilationUnit []
  returns [SCompilationUnit self]
{
  self = new SCompilationUnit(buildTokenReference());

  Statement     stmt;
}
:
  (
    stmt = dataManipulationLanguageStatement[] { self.addStmt(stmt); }
  |
    SEMICOLON
  )+
  EOF
;

/**
 * This is the main entry point for the grammar
 */
dataManipulationLanguageStatement []
  returns [com.kopiright.xkopi.comp.sqlc.Statement self = null]
:
  (
    self = sSelectStatement[]
  |
    self = sInsertStatement[]
  |
    self = sUpdateStatement[]
  |
    self = sDeleteStatement[]
  )
    {
      self.setComment(getComment());
    }
;

sSelectStatement []
  returns [SelectStatement self = null]
{
  String                type = null;
  ArrayList             selectList;
  FromClause            fromClause;
  WhereClause           whereClause = null;
  GroupByClause         groupByClause = null;
  HavingClause          havingClause = null;
  SortSpec              sortSpec = null;
  UpdSpec               updateSpec = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "SELECT"
  ( "ALL" { type = "ALL"; } | "DISTINCT" { type = "DISTINCT"; } )?
  selectList = sSelectList[]
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
;

sArrayPrecision []
  returns [ArrayPrecision self = null]
{
  IntegerLiteral        p = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LPAREN ( p = sIntegerLiteral[] | STAR ) RPAREN
    { self = new ArrayPrecision(sourceRef, p); }
;

sAssignList []
  returns [ArrayList self = new ArrayList()]
{
  Assignment    p;
}
:
  (
    p = sAssignment[] 
  |
    p = sTupleAssignment[] 
  )
    { self.add(p); }
  (
    COMMA 
    (
      p = sAssignment[]
    |
      p = sTupleAssignment[]
    )
      { self.add(p); }
  )*
;

sAssignment []
  returns [Assignment self = null]
{
  FieldReference        lhs;
  Expression            rhs;
  TokenReference        sourceRef = buildTokenReference();
}
:
  lhs = sFieldReference[] ASSIGN rhs = sExpression[]
    { self = new Assignment(sourceRef, lhs, rhs); }
;

sBetweenPredicate []
  returns [BetweenPredicate self = null]
{
  Expression            p1;
  String                p2 = null;
  Expression            p3;
  String                p4 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "BETWEEN"
  p1 = sExpression[] ( p2 = sBetweenQualifier[] )?
  "AND"
  p3 = sExpression[] ( p4 = sBetweenQualifier[] )?
    { self = new BetweenPredicate(sourceRef, p1, p2, p3, p4); }
;

sBetweenQualifier []
  returns [String self = null]
:
  "INCLUSIVE"
    { self = "INCLUSIVE"; }
|
  "EXCLUSIVE"
    { self = "EXCLUSIVE"; }
;

sCaseExpression []
  returns [CaseExpression self = null]
{
  AbstractCaseExpression        p1;
  TokenReference                sourceRef = buildTokenReference();
}
:
  "CASE"
  (
    p1 = sSearchedCaseExpression[]
  |
    p1 = sSimpleCaseExpression[]
  )
  "END"
    { self = new CaseExpression(sourceRef,  p1); }
;

sCorrespondingSpec []
  returns [CorrespondingSpec self = null]
{
  FieldNameList p1 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "CORRESPONDING"
  ( "BY" LPAREN p1 = sFieldNameList[] RPAREN )?
    { self = new CorrespondingSpec(sourceRef, p1); }
;

sDataType []
  returns [Type self = null]
{
  ArrayPrecision        prec = null;
  IntegerLiteral        exp1 = null;
  IntegerLiteral        exp2 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "TINYINT" { self = new TinyIntType(sourceRef); }
|
  "SMALLINT" { self = new SmallIntType(sourceRef); }
|
  "INTEGER" { self = new IntegerType(sourceRef); }
|
  "NUMERIC"
  (
    LPAREN exp1 = sIntegerLiteral[] ( COMMA exp2 = sIntegerLiteral[] )? RPAREN
      { self = new NumericType(sourceRef, exp1, exp2); }
  )?
|
   "FLOAT" { self = new FloatType(sourceRef); }
|
   "DOUBLE" { self = new DoubleType(sourceRef); }
|
   "REAL" { self = new RealType(sourceRef); }
|
   "CHAR" ( (sArrayPrecision[])=> prec = sArrayPrecision[] )?
     { self = new CharType(sourceRef, prec); }
|
   "STRING" { self = new StringType(sourceRef); }
|
   "BOOL" { self = new BoolType(sourceRef); }
|
   "BLOB" { self = new BlobType(sourceRef); }
|
   "CLOB" { self = new ClobType(sourceRef); }
;

sDataTypeSpec []
  returns [Type self = null]
{
  String                p1;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sDataType[]
|
  p1 = sIdentifier[]
    { self = new TypeName(sourceRef, p1); }
;

sDeleteStatement []
  returns [DeleteStatement self = null]
{
  TableName             table;
  SearchCondition       cond = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "DELETE" "FROM" table = sSimpleTableName[]
  ( "WHERE" cond = sSearchCondition[] )?
    { self = new DeleteStatement(sourceRef, table, cond); }
;

sExistsPredicate []
  returns [ExistsPredicate self = null]
{
  SubTableExpression    p1;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "EXISTS" p1 = sSubTableExpression[]
    { self = new ExistsPredicate(sourceRef, p1); }
;

sExpression []
  returns [Expression self = null]
{
  Expression            right = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sFactor[]
  (
    PLUS right = sFactor[]
      { self = new PlusExpression(sourceRef, self, right); }
  |
    MINUS right = sFactor[]
      { self = new MinusExpression(sourceRef, self, right); }
  |
    STAR right = sFactor[]
      { self = new StarExpression(sourceRef, self, right); }
  |
    SLASH right = sFactor[]
      { self = new SlashExpression(sourceRef, self, right); }
  |
    DBLPIPE right = sFactor[]
      { self = new DbpipeExpression(sourceRef, self, right); }
  )*
;

sFactor []
  returns [Expression self = null]
{
  TokenReference        sourceRef = buildTokenReference();
}
:
  PLUS self = sPrimary[]
    { self = new UnaryPlusExpression(sourceRef, self); }
|
  MINUS self = sPrimary[]
    { self = new UnaryMinusExpression(sourceRef, self); }
|
  self = sPrimary[]
;

sExpressionList []
  returns [ExpressionList self = null]
{
  ArrayList             exprs = new ArrayList();
  Expression            p;
  TokenReference        sourceRef = buildTokenReference();
}
:
  p = sExpression[] { exprs.add(p); }
  ( COMMA p = sExpression[] { exprs.add(p); } )*
    { self = new ExpressionList(sourceRef, exprs); }
;

sFieldNameList []
  returns [FieldNameList self = null]
{
  ArrayList             fields = new ArrayList();
  String                p;
  TokenReference        sourceRef = buildTokenReference();
}
:
  p = sIdentifier[] { fields.add(p); }
  ( COMMA p = sIdentifier[] { fields.add(p); } )*
    { self = new FieldNameList(sourceRef, fields); }
;

sFieldReference []
  returns [FieldReference self = null]
{
  String                p1;
  String                p2 = null;
  String                p3 = null;
  TokenReference        sourceRef = buildTokenReference();
  boolean               hasPlus = false;
}
:
  p1 = sIdentifier[]
  ( 
   DOT p2 = sIdentifier[]
   ( DOT p3 = sIdentifier[] { p1 = p1 + "." + p2; p2 = p3; } )?
  )?
  ( LPAREN PLUS RPAREN { hasPlus = true; } )?
    { self = new FieldReference(sourceRef, p2 == null ? null : p1, p2 == null ? p1 : p2, hasPlus); }
;

sFromClause []
  returns [FromClause self = null]
{
  ArrayList             v = new ArrayList();
  SimpleTableReference  p;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "FROM" p = sSimpleTableReference[] { v.add(p); }
  (
    COMMA p = sSimpleTableReference[] { v.add(p); }
  )*
    { self = new FromClause(sourceRef, v); }
;

sGroupByClause []
  returns [GroupByClause self = null]
{
  ArrayList             v = new ArrayList();
  FieldReference        p;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "GROUP" "BY" p = sFieldReference[] { v.add(p); }
  (
    COMMA p = sFieldReference[]
      { v.add(p); }
  )*
    { self = new GroupByClause(sourceRef, v); }
;

sHavingClause []
  returns [HavingClause self = null]
{
  SearchCondition       p;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "HAVING" p = sSearchCondition[]
    { self = new HavingClause(sourceRef, p); }
;

sIdentifier []
  returns [String self = null]
:
  t1:STANDARD_ID { self = t1.getText(); }
|
  t2:DELIMITER_ID { self = t2.getText(); }
;

/*
 * DECODE(check, ( search, result )*, default)
 *
 * This is not parsable in a straightforward way by a LL(1) parser
 */
sDecodeExpression []
  returns [DecodeExpression self = null]
{
  Vector                container = new Vector();
  Expression            p1;
  Expression            p2;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "DECODE" LPAREN
  p1 = sExpression[]            // check expression
  COMMA p2 = sExpression[]      // default if there are no search expressions, first search otherwise
    {
      container.addElement(p1);
      container.addElement(p2);
    }
  (
    COMMA p1 = sExpression[]
    COMMA p2 = sExpression[]
      {
        container.addElement(p1);
        container.addElement(p2);
      }
  )*
  RPAREN
    {
      Expression        checkExpr;
      Vector            searchResult;
      Expression        defaultExpr;

      checkExpr = (Expression)container.elementAt(0);
      searchResult = new Vector();
      for (int i = 1; i < container.size() - 1; i += 2) {
        searchResult.addElement(new Expression[]{ (Expression)container.elementAt(i), (Expression)container.elementAt(i + 1) });
      }
      defaultExpr = (Expression)container.elementAt(container.size() - 1);

      self = new DecodeExpression(sourceRef,
                                  checkExpr,
                                  (Expression[][])Utils.toArray(searchResult, Expression[].class),
                                  defaultExpr);
    }
;

sIfExpression []
  returns [IfExpression self = null]
{
  SearchCondition       p1;
  Expression            p2;
  Expression            p3;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "IF" p1 = sSearchCondition[]
  "THEN" p2 = sExpression[]
  "ELSE" p3 = sExpression[]
  "FI"
    { 
      reportTrouble(new CWarning(sourceRef, SqlcMessages.IF_EXPR));
      self = new IfExpression(sourceRef, p1, p2, p3); 
    }
;

sInsertSource []
  returns [InsertSource self = null]
{
  ValueList             value;
  ArrayList             list = new ArrayList();
  TableReference        table;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "VALUES" value = sValueList[]
    { self = new ValueListInsertSource(sourceRef,  value); }
|
  ( "TABLE" LPAREN )=>
  "TABLE" LPAREN
  value = sValueList[] { list.add(value); }
  ( COMMA value = sValueList[] { list.add(value); } )*
  RPAREN
    { self = new TableInsertSource(sourceRef,  list); }
|
  table = sTableExpression[] { self = new TableExpressionInsertSource(sourceRef, table);}
|
  "DEFAULT" "VALUES" { self = new DefaultValuesInsertSource(sourceRef); }
;

sInsertStatement []
  returns [InsertStatement self = null]
{
  String                p1;
  FieldNameList         p2 = null;
  InsertSource          p3;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "INSERT" "INTO" p1 = sIdentifier[]
  (
    (LPAREN sFieldNameList[])=> LPAREN p2 = sFieldNameList[] RPAREN
  )?
  p3 = sInsertSource[]
    { self = new InsertStatement(sourceRef, p1, p2, p3); }
;

sIntegerLiteral []
  returns [IntegerLiteral self = null]
{
  TokenReference        sourceRef = buildTokenReference();
  int                   value;
}
:
  value = sInteger[]
    { self = new IntegerLiteral(sourceRef, new Integer(value)); }
;

sIntersectSpec []
  returns [IntersectSpec self = null]
{
  CorrespondingSpec     p1 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "INTERSECT" ( p1 = sCorrespondingSpec[] )?
    { self = new IntersectSpec(sourceRef, p1); }
;

sJoinPred []
  returns [JoinPred self = null]
{
  SearchCondition       condition;
  FieldNameList         list;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "ON" condition = sSearchCondition[]
    { self = new OnJoinPred(sourceRef, condition); }
|
  "USING" LPAREN list = sFieldNameList[] RPAREN
    { self = new UsingJoinPred(sourceRef, list); }
;

sJoinType []
  returns [String self = null]
:
  "LEFT" { self =  "LEFT"; }
|
  "RIGHT" { self = "RIGHT"; }
|
  "FULL" { self = "FULL"; }
;

sLikePredicate []
  returns [LikePredicate self = null]
{
  String                p1 = null;
  Expression            p2;
  JdbcEscape            p3 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "LIKE"
  ( p1 = sSensSpec[] ) ?
  p2 = sExpression[]
  ( p3 = sEscapeExpression[] )?
    { self = new LikePredicate(sourceRef, p1, p2, p3); }
;

sLiteral []
  returns [Literal self = null]
{
}
:
  self = sIntegerLiteral[]
|
  self = sNumericLiteral[]
|
  self = sStringLiteral[]
|
  self = sBooleanLiteral[]
|
  self = sNullLiteral[]
|
  self = sDateLiteral[]
|
  self = sTimeLiteral[]
|
  self = sTimestampLiteral[]
;

sNumericLiteral []
  returns [NumericLiteral self = null]
{
  TokenReference        sourceRef = buildTokenReference();
}
:
  t2:NUMERIC_LIT
    { self = new NumericLiteral(sourceRef, new NotNullFixed(t2.getText())); }
;

sStringLiteral []
  returns [StringLiteral self = null]
{
  String                text = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  text = sString[]
    { self = new StringLiteral(sourceRef, text); }
;

sBooleanLiteral []
  returns [BooleanLiteral self = null]
{
  TokenReference        sourceRef = buildTokenReference();
}
:
  "FALSE"
    { self = new BooleanLiteral(sourceRef, false); }
|
  "TRUE"
    { self = new BooleanLiteral(sourceRef, true); }
;

sNullLiteral []
  returns [NullLiteral self = null]
{
  TokenReference        sourceRef = buildTokenReference();
}
:
  "NULL"
    { self = new NullLiteral(sourceRef); }
;

sDateLiteral []
  returns [JdbcDateLiteral self = null]
{
  String                text = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  JDBC_DATE text = sString[] RCURLY
    {
      try {
        self = new JdbcDateLiteral(sourceRef, text);
      } catch (PositionedError e) {
        reportTrouble(e);
        // allow parsing to continue
        self = JdbcDateLiteral.DEFAULT;
      }
    }
;

sTimeLiteral []
  returns [JdbcTimeLiteral self = null]
{
  String                text = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  JDBC_TIME text = sString[] RCURLY
    {
      try {
        self = new JdbcTimeLiteral(sourceRef, text);
      } catch (PositionedError e) {
        reportTrouble(e);
        // allow parsing to continue
        self = JdbcTimeLiteral.DEFAULT;
      }
    }
;

sTimestampLiteral []
  returns [JdbcTimeStampLiteral self = null]
{
  String                text = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  JDBC_TIMESTAMP text = sString[] RCURLY
    {
      try {
        self = new JdbcTimeStampLiteral(sourceRef, text);
      } catch (PositionedError e) {
        reportTrouble(e);
        // allow parsing to continue
        self = JdbcTimeStampLiteral.DEFAULT;
      }
    }
;

sMatchesPredicate []
  returns [MatchesPredicate self = null]
{
  String                p1 = null;
  Expression            p2;
  Expression            p3 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "MATCHES" ( p1 = sSensSpec[] )? p2 = sExpression[]
  ( "ESCAPE" p3 = sExpression[] )?
    { self = new MatchesPredicate(sourceRef, p1, p2, p3); }
;

sPredicate []
  returns [Predicate self = null]
{
  Expression            expr;
  String                s = null;
  Predicate             right = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sTupleInPredicate[]
|
  self = sExistsPredicate[]
|
  expr = sExpression[]
    { self = new ExpressionPredicate(sourceRef, expr); }
  (
    ( "NOT" { s = "NOT"; } )?
    (
      "SUBSET" ( "OF" )? { s += " SUBSET OF"; }
      expr = sExpression[]
       { right = new ExpressionPredicate(sourceRef, expr); }
    |
      right = sWithNotPredicate[]
    )
    { self = new ExtendedPredicate(sourceRef, self, s, right); }
  |
    self = sWithCompPredicate[self]
    { // NULL on the left side of a comparison expression
      if (expr instanceof NullLiteral) {
        if (self instanceof IsPredicate) {
          reportTrouble(new PositionedError(sourceRef,
                                            SqlcMessages.INVALID_NULL_COMPARISON,
                                            "IS (NOT) NULL"));
        } else {
          ComparisonPredicate   cp = (ComparisonPredicate) self;

          if (cp.getOperator().equals("=")) {
            reportTrouble(new CWarning(sourceRef, SqlcMessages.ASSIGN_NULL)); 
          } else if (cp.getOperator().equals("<>")) {
            reportTrouble(new CWarning(sourceRef, SqlcMessages.NE_NULL)); 
          } else {
            reportTrouble(new PositionedError(sourceRef,
                                              SqlcMessages.INVALID_NULL_COMPARISON,
                                              cp.getOperator()));
          }
        }
      }
    }
  )?
;

sPrimary []
  returns [Expression self = null]
{
  Type                  right = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sSimplePrimary[]
|
  "CAST" LPAREN self = sSimplePrimary[] "AS" right = sDataTypeSpec[] RPAREN
    { self = new CastPrimary(sourceRef, self, right); }
;

sQuantifiedPredicate []
  returns [QuantifiedPredicate self = null]
{
  SubTableExpression    p1;
  String                s = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  (
    "ALL" { s = "ALL"; }
  |
    "ANY" { s = "ANY"; }
  |
    "SOME" { s = "SOME"; }
  )
  p1 = sSubTableExpression[]
    { self = new QuantifiedPredicate(sourceRef, s, p1); }
;

sSearchCondition []
  returns [SearchCondition self = null]
{
  SearchCondition       right = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sBooleanTerm[]
  (
    "OR" right = sBooleanTerm[]
      { self = new OrCondition(sourceRef, self, right); }
  )*
;

sBooleanTerm []
  returns [SearchCondition self = null]
{
  SearchCondition       right = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sBooleanFactor[]
  (
    "AND" right = sBooleanFactor[]
      { self = new AndCondition(sourceRef, self, right); }
  )*
;

sBooleanFactor []
  returns [SearchCondition self = null]
{
  Predicate             pred;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "NOT" pred = sPredicate[]
    { self = new NotCondition(sourceRef, new SimpleSearchCondition(sourceRef, pred)); }
|
  pred = sPredicate[]
    { self = new SimpleSearchCondition(sourceRef, pred); }
;

sSearchedCaseExpression []
  returns [SearchedCaseExpression self = null]
{
  Expression            p = null;
  ArrayList             conditions = new ArrayList();
  SearchedWhenClause    condition = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( condition = sSearchedWhenClause[] { conditions.add(condition); } )+
  ( "ELSE" p = sExpression[] )?
    { self = new SearchedCaseExpression(sourceRef, conditions, p); }
;

sSearchedWhenClause []
  returns [SearchedWhenClause self = null]
{
  SearchCondition       p1;
  Expression            p2;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "WHEN" p1 = sSearchCondition[]
  "THEN" p2 = sExpression[]
    { self = new SearchedWhenClause(sourceRef, p1, p2); }
;

sSelectElem []
  returns [SelectElem self = null]
{
  String                s = null;
  Expression            p1;
  String                s2;
  TokenReference        sourceRef = buildTokenReference();
}
:
  STAR
    { self = new SelectElemStar(sourceRef); }
|
  ( sIdentifier[] DOT STAR )=>
  s = sIdentifier[] DOT STAR
    { self = new SelectElemStar(sourceRef, s); }
|
  p1 = sExpression[]
  (
     "AS" s2 = sIdentifier[] { self = new SelectElemExpression(sourceRef, p1, s2); }
  |
     { self = new SelectElemExpression(sourceRef, p1); }
  )
;

sSelectExpression []
  returns [SelectExpression self = null]
{
  String                type =  null;
  FromClause            fromClause;
  WhereClause           whereClause = null;
  GroupByClause         groupByClause = null;
  HavingClause          havingClause = null;
  ArrayList             selectList;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "SELECT"
  ( "ALL" { type =  "ALL"; } | "DISTINCT" { type = "DISTINCT"; } )?
  selectList = sSelectList[]
  fromClause = sFromClause[]
  ( whereClause = sWhereClause[] )?
  ( groupByClause = sGroupByClause[] )?
  ( havingClause = sHavingClause[] )?
  {
    self = new SelectExpression(sourceRef, type, selectList, fromClause, whereClause, groupByClause, havingClause);
  }
;

sSelectList []
  returns [ArrayList self = new ArrayList()]
{
  SelectElem    p;
}
:
  p = sSelectElem[] { self.add(p); }
  (
    COMMA p = sSelectElem[]
      { self.add(p); }
  )*
;

sSensSpec []
  returns [String self = null]
:
  "SENSITIVE" { self = "SENSITIVE"; }
  |
  "INSENSITIVE" { self = "INSENSITIVE"; }
;

sSimpleCaseExpression []
  returns [SimpleCaseExpression self = null]
{
  Expression            p;
  ArrayList             conditions = new ArrayList();
  SimpleWhenClause      condition = null;
  Expression            elseExpr = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  p = sExpression[]
  ( condition = sSimpleWhenClause[] { conditions.add(condition); } )+
  (
    "ELSE" elseExpr = sExpression[]
  )?
    {
      self = new SimpleCaseExpression(sourceRef, p, conditions, elseExpr);
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
;

sSimpleTableReference []
  returns [SimpleTableReference self = null]
{
  TableReference        columns;
  Expression            name;
  TableAlias            alias;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LPAREN columns = sTableExpression[] RPAREN
  alias = sTableAlias[]
    { self = new SimpleSubTableReference(sourceRef, columns, alias); }
|
  ( "TABLE" )?
  name = sTableName[]
  alias = sTableAlias[]
    { self = new TableName(sourceRef, name, alias); }
|
  self = sOuterJoin[]
|
  self = sJdbcOuterJoin[]
;

sSimpleWhenClause []
  returns [SimpleWhenClause self = null]
{
  Expression            p1;
  Expression            p2;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "WHEN" p1 = sExpression[] "THEN" p2 = sExpression[]
    { self = new SimpleWhenClause(sourceRef, p1, p2); }
;

sSortElem []
  returns [SortElem self = null]
{
  Expression            p1 = null;
  boolean               isDesc = false;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( 
    p1 = sFieldReference[]
  |
    p1 = sIntegerLiteral[]
  |
    p1 = sJdbcFunction[]         
  )
  ( "ASC" | "DESC" { isDesc = true; } )?
    { self = new SortElem(sourceRef, p1, isDesc); }
;

sSortSpec []
  returns [SortSpec self = null]
{
  SortElem              p;
  ArrayList             v = new ArrayList();
  TokenReference        sourceRef = buildTokenReference();
}
:
  "ORDER" "BY" p = sSortElem[] { v.add(p); }
  ( COMMA p = sSortElem[] { v.add(p); } )*
    { self = new SortSpec(sourceRef, v); }
;

sSubTableExpression []
  returns [SubTableExpression self = null]
{
  SubTable              left;
  UnidiffSpec           spec;
  SubTable              right;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LPAREN
  left = sSubTableTerm[]
  (
    spec = sUnidiffSpec[] right = sSubTableTerm[]
      { left = new UnidiffTableTerm(sourceRef, spec, left, right); }
  )*
  RPAREN
  { self = new SubTableExpression(sourceRef, left); }
;

sSubTableReference []
  returns [SubTable self = null]
{
  SearchCondition       p1;
  TokenReference        sourceRef = buildTokenReference();
}
:
  p1 = sSearchCondition[]
    { self = new SubTableReference(sourceRef, p1); }
|
  self = sSelectExpression[]
;

sSubTableTerm []
  returns [SubTable self = null]
{
  IntersectSpec         spec;
  SubTable              right;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sSubTableReference[]
  (
    spec = sIntersectSpec[] right = sSubTableReference[]
      { self = new IntersectTableTerm(sourceRef, spec, self, right); }
  )*
;

sTableAlias []
  returns [TableAlias self = null]
{
  String                ident = null;
  FieldNameList         list = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ( ident = sIdentifier[] )?
  (
   LPAREN list = sFieldNameList[] RPAREN
    { reportTrouble(new CWarning(sourceRef, SqlcMessages.NOT_ANSI_SYNTAX)); }
  )?
    { self = new TableAlias(sourceRef, ident, list); }
;

sTableName []
  returns [Expression self = null]
{
  String                ident = null;
  String                ident2 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  ident = sIdentifier[] (DOT ident2 = sIdentifier[] {ident += "." + ident2;})?
    { self = new SimpleIdentExpression(sourceRef, ident); }
;

sTupleAssignment []
  returns [TupleAssignment self = null]
{
  FieldNameList         fieldList;
  SubTableExpression    subTable;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LPAREN fieldList = sFieldNameList[] RPAREN
  ASSIGN subTable = sSubTableExpression[]
    { self = new TupleAssignment(sourceRef, fieldList, subTable); }
;

sTupleInPredicate []
  returns [TupleInPredicate self = null]
{
  boolean               hasNot = false;
  ExpressionList        p1;
  SubTableExpression    p2;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LBRACKET p1 = sExpressionList[] RBRACKET
  ( "NOT" { hasNot = true; } )?
  "IN" p2 = sSubTableExpression[]
    { self = new TupleInPredicate(sourceRef, p1, hasNot, p2); }
;

sUnidiffOp []
  returns [String self = null]
:
  "UNION"
    { self = "UNION"; }
  ( "ALL"  { self += " ALL"; } )?
|
  "DIFF"  { self = "DIFF"; }
|
  "EXCEPT" { self = "EXCEPT"; }
|
  "MINUS" { self = "MINUS"; }
;

sUnidiffSpec []
  returns [UnidiffSpec self = null]
{
  String                oper;
  CorrespondingSpec     corr = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  oper = sUnidiffOp[]
  ( corr = sCorrespondingSpec[] )?
    { self = new UnidiffSpec(sourceRef, oper, corr); }
;

sUpdSpec []
  returns [UpdSpec self = null]
{
  TokenReference        sourceRef = buildTokenReference();
}
:
  "FOR" "UPDATE"
    { self = new UpdSpec(sourceRef); }
;

sUpdateStatement []
  returns [UpdateStatement self = null]
{
  TableName             table;
  ArrayList             list = null;
  SearchCondition       cond = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "UPDATE" table = sSimpleTableName[]
  "SET" list = sAssignList[]
  (
    (
      "WHERE"
      (
        cond = sSearchCondition[]
      |
        "CURRENT"
      )
        { self = new UpdateStatement(sourceRef, table, list, cond); }
    )
  |
    { self = new UpdateStatement(sourceRef, table, list); }
  )
;

sSimpleTableName []
  returns [TableName self = null]
{
  Expression            name;
  String                alias = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  name = sTableName[] ( alias = sIdentifier[] )?
    { self = new TableName(sourceRef, name, new TableAlias(sourceRef, alias, null)); }
;

sValueInPredicate []
  returns [ValueInPredicate self = null]
{
  ExpressionList        p1;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "IN" LPAREN p1 = sExpressionList[] RPAREN
    { self = new ValueInPredicate(sourceRef, p1); }
;

sValueList []
  returns [ValueList self = null]
{
  ExpressionList        p1;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LPAREN p1 = sExpressionList[] RPAREN
    { self = new ValueList(sourceRef, p1); }
;

sWithCompPredicate[Predicate predicate]
  returns [Predicate self = null]
{
  String                s = null;
  Expression            p = null;
  boolean               hasNot = false;
  Predicate             right = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "IS"
  ( "NOT" { hasNot = true; } )?
  "NULL"
    { self = new IsPredicate(sourceRef, predicate, hasNot); }
|
  (
    LT { s = "<"; }
  |
    LE { s = "<="; }
  |
    GT { s = ">"; }
  |
    GE { s = ">="; }
  |
    ASSIGN { s = "="; }
  |
    NE { s = "<>"; }
  )
  (
    right = sQuantifiedPredicate[]
  |
    p = sExpression[]
    { 
      if (p instanceof NullLiteral) {
        if (s.equals("=")) {
          reportTrouble(new CWarning(sourceRef, SqlcMessages.ASSIGN_NULL)); 
        } else if (s.equals("<>")){
          reportTrouble(new CWarning(sourceRef, SqlcMessages.NE_NULL)); 
        } else {
          reportTrouble(new PositionedError(sourceRef,
                                            SqlcMessages.INVALID_NULL_COMPARISON,
                                            s));
        }
      }
      right = new ExpressionPredicate(sourceRef, p);
    }
  )
    { self = new ComparisonPredicate(sourceRef, predicate, right, s); }
;

sWithNotPredicate []
  returns [Predicate self = null]
:
  self = sBetweenPredicate[]
|
  self = sLikePredicate[]
|
  self = sValueInPredicate[]
|
  self = sMatchesPredicate[]
;

sJdbcOuterJoin []
  returns [JdbcOuterJoin self = null]
{
  SimpleTableReference  p1;
  String                p2;
  SimpleTableReference  p3;
  JoinPred              p4 = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  JDBC_OUTERJOIN
  p1 = sSimpleTableReference[]
  p2 = sJoinType[] "OUTER" "JOIN"
  p3 = sSimpleTableReference[]
  ( p4 = sJoinPred[] )?
  RCURLY
   { self = new JdbcOuterJoin(sourceRef, p1, p2, p3, p4); }
;

sOuterJoin []
  returns [OuterJoin self = null]
{
  SimpleTableReference  p1;
  String                p2;
  SimpleTableReference  p3;
  JoinPred              p4 = null;
  TokenReference        sourceRef = buildTokenReference();
  ArrayList             list = new ArrayList();
}
:
  LCURLY
  p1 = sSimpleTableReference[]
  { self = new OuterJoin(sourceRef, p1);}
  (p2 = sJoinType[] "OUTER" "JOIN"
  p3 = sSimpleTableReference[]
  ( p4 = sJoinPred[] )?
   { list.add(new SubOuterJoin(p2, p3, p4)); }
  )*
  RCURLY
  {((OuterJoin)self).setSubOuterJoins((SubOuterJoin[])list.toArray(new SubOuterJoin[list.size()]));}        
;

sCountExpression []
  returns [CountExpression self = null]
{
  Expression            expr = null;
  boolean               hasDistinct = false;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "COUNT" LPAREN
  (
    STAR
  |
    "DISTINCT" expr = sExpression[] { hasDistinct = true; }
  )
  RPAREN
    { self = new CountExpression(sourceRef, hasDistinct, expr); }
;

sSetFunction []
  returns [SetFunction self = null]
{
  String                oper = null;
  Expression            expr = null;
  int                   type = SetFunction.TYP_NONE;
  TokenReference        sourceRef = buildTokenReference();
}
:
  oper = sSetFunctionName []
  LPAREN
  (
    (
      "ALL" { type = SetFunction.TYP_ALL; }
    |
      "DISTINCT" { type = SetFunction.TYP_DISTINCT; }
    )?
     expr = sExpression[] { self = new SetFunction(sourceRef, oper, type, expr); }
  )
  RPAREN
;

sSetFunctionName []
  returns [String self = null]
:
  "MAX" { self = "MAX"; }
|
  "MIN" { self = "MIN"; }
|
  "AVG" { self = "AVG"; }
|
  "SUM" { self = "SUM"; }
;

sJdbcFunction []
  returns [JdbcFunction self = null]
{
  String                name = null;
  ExpressionList        params = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  JDBC_FUNCTION
  (
    t:STANDARD_ID { name = t.getText(); }
            ( DOT t2:STANDARD_ID { name = name + "." + t2.getText(); } )?
  |
    name = sSetFunctionName[]
      { reportTrouble(new CWarning(sourceRef, SqlcMessages.SET_FUNCTION_IS_SQL, name)); }
  )
  LPAREN ( params = sExpressionList[] )? RPAREN
  RCURLY
    { self = new JdbcFunction(sourceRef, name, params); }
;

sEscapeExpression []
  returns [JdbcEscape self = null]
{
  String                text = null;
  TokenReference        sourceRef = buildTokenReference();
}
:
  JDBC_ESCAPE text = sString[] RCURLY
    { self = new JdbcEscape(sourceRef, text); }
;

sTableExpression []
  returns [TableReference self = null]
{
  UnidiffSpec           spec;
  TableReference        right;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sTableTerm[]
  (
    spec = sUnidiffSpec[] right = sTableTerm[]
      { self = new UnidiffTableReference(sourceRef, spec, self, right); }
  )*
;

sTableTerm []
  returns [TableReference self]
{
  IntersectSpec         spec;
  TableReference        right;
  TokenReference        sourceRef = buildTokenReference();
}
:
  self = sTableReference[]
  (
    spec = sIntersectSpec[] right = sTableReference[]
      { self = new IntersectTableReference(sourceRef, spec, self, right); }
  )*
;

sTableReference []
  returns [TableReference self = null]
{
  SelectExpression      p1;
  TableReference        sub;
  TokenReference        sourceRef = buildTokenReference();
}
:
  LPAREN sub = sTableExpression[] RPAREN
    { self = new ParenthesedTableReference(sourceRef, sub); }
|
  p1 = sSelectExpression[]
    { self = new SelectTableReference(sourceRef, p1); }
;

sWhereClause []
  returns [WhereClause self = null]
{
  SearchCondition       p;
  TokenReference        sourceRef = buildTokenReference();
}
:
  "WHERE" p = sSearchCondition[]
    { self = new WhereClause(sourceRef, p); }
;

sInteger []
  returns [int self = -1]
:
  text : INTEGER_LIT
    { self = Integer.valueOf(text.getText()).intValue(); }
;

sString []
  returns [String self = null]
:
  text : STRING_LIT
    { self = text.getText(); }
;
