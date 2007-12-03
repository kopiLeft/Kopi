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

package com.kopiright.xkopi.comp.sqlc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.List;
import java.util.Stack;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TabbedPrintWriter;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.Time;
import com.kopiright.xkopi.lib.type.Timestamp;

/**
 * This class implements a Java pretty printer
 */
public class SqlcPrettyPrinter implements SqlVisitor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public SqlcPrettyPrinter(String fileName) throws IOException {
    this(fileName, DEFAULT_TAB_SIZE);
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public SqlcPrettyPrinter(String fileName, int tabSize) throws IOException {
    this(new TabbedPrintWriter(new BufferedWriter(new FileWriter(fileName + ".gen"))));
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public SqlcPrettyPrinter(TabbedPrintWriter p) {
    this(p, DEFAULT_TAB_SIZE);
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public SqlcPrettyPrinter(TabbedPrintWriter p, int tabSize) {
    this.p = p;
    this.pos = 0;
    this.contexts = new Stack();
    TAB_SIZE = tabSize;
    TAB_AND_HALF = tabSize + (tabSize / 2);
  }

  /**
   * Close
   */
  public void close() {
    p.close();
  }

  /**
   * Sets the position of tabs
   */
  public void setPos(int pos) {
    this.pos = pos;
  }

  /**
   * Sets the position of tabs
   */
  public void setColumn(int pos) {
    this.column = pos;
  }


  // ----------------------------------------------------------------------
  // STATEMENT
  // ----------------------------------------------------------------------

  /**
   * prints a while statement
   */
  public void printCUnit(List elems)
    throws PositionedError
  {
    ListIterator iterator = elems.listIterator();

    while (iterator.hasNext()) {
      ((Statement)iterator.next()).accept(this);
      println();
    }
  }

  // ----------------------------------------------------------------------
  // AST NODES
  // ----------------------------------------------------------------------

  /**
   * Visits a AndCondition node.
   */
  public void visitAndCondition(AndCondition self, SearchCondition left, SearchCondition right)
    throws PositionedError
  {
    left.accept(this);
    println();
    pos -= TAB_AND_HALF;
    print("AND ");
    pos += TAB_AND_HALF;
    right.accept(this);
  }

  /**
   * Visits a ArrayPrecision node.
   */
  public void visitArrayPrecision(ArrayPrecision self, IntegerLiteral size)
    throws PositionedError
  {
    print("(");
    if (size != null) {
      size.accept(this);
    } else {
      print("*");
    }
    print(")");
  }

  /**
   * Visits a Assignment node.
   */
  public void visitAssignment(Assignment self, Expression left, Expression right)
    throws PositionedError
  {
    left.accept(this);
    print(" = ");
    right.accept(this);
  }

  /**
   * Visits a BetweenPredicate node.
   */
  public void visitBetweenPredicate(BetweenPredicate self,
				    Expression start,
				    String startQualifier,
				    Expression end,
				    String endQualifier)
    throws PositionedError
  {
    print(" BETWEEN ");
    start.accept(this);
    if (startQualifier != null) {
      print(" ");
      print(startQualifier);
    }
    print(" AND ");
    end.accept(this);
    if (endQualifier != null) {
      print(" ");
      print(endQualifier);
    }
  }

  /**
   * Visits a BinaryExpression node.
   */
  public void visitBinaryExpression(BinaryExpression self, String opt, Expression left, Expression right)
    throws PositionedError
  {
    left.accept(this);
    print(" ");
    print(opt);
    print(" ");
    right.accept(this);
  }

  /**
   * Visits a BlobType node.
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    print("BLOB");
  }

  /**
   * Visits a ClobType node.
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    print("CLOB");
  }

  /**
   * Visits a BoolType node.
   */
  public void visitBoolType(BoolType self)
    throws PositionedError
  {
    print("BOOL");
  }

  /**
   * Visits a BooleanLiteral node.
   */
  public void visitBooleanLiteral(BooleanLiteral self, boolean value)
    throws PositionedError
  {
    print(value ? "{fn TRUE}" : "{fn FALSE}");
  }

  /**
   * Visits a CaseExpression node.
   */
  public void visitCaseExpression(CaseExpression self, AbstractCaseExpression cases)
    throws PositionedError
  {
    print("CASE");
    println();
    cases.accept(this);
    print("END");
  }

  /**
   * Visits a CastPrimary node.
   */
  public void visitCastPrimary(CastPrimary self, Expression left, Type right)
    throws PositionedError
  {
    print(" CAST (");
    left.accept(this);
    print(" AS ");
    right.accept(this);
    print(")");
  }

  /**
   * Visits a CharType node.
   */
  public void visitCharType(CharType self, ArrayPrecision prec)
    throws PositionedError
  {
    print("CHAR");
    if (prec != null) {
      prec.accept(this);
    }
  }

  /**
   * Visits a ComparisonPredicate node.
   */
  public void visitComparisonPredicate(ComparisonPredicate self, Predicate left, Predicate right, String operator)
    throws PositionedError
  {
    left.accept(this);
    print(" ");
    print(operator);
    print(" ");
    right.accept(this);
  }

  /**
   * Visits a CorrespondingSpec node.
   */
  public void visitCorrespondingSpec(CorrespondingSpec self, FieldNameList fields)
    throws PositionedError
  {
    print("CORRESPONDING");
    if (fields != null) {
      print(" BY (");
      fields.accept(this);
      print(")");
    }
  }

  /**
   * Visits a CountExpression node.
   */
  public void visitCountExpression(CountExpression self, boolean hasDistinct, boolean countAll, Expression expr)
    throws PositionedError
  {
    if (hasDistinct) {
      print("COUNT(DISTINCT ");
      expr.accept(this);
      print(")");
    } else if (countAll){
      print("COUNT(*)");
    } else {
      print("COUNT(");
      expr.accept(this);
      print(")");
    }
  }

  /**
   * Visits a DefaultValuesInsertSource node.
   */
  public void visitDefaultValuesInsertSource(DefaultValuesInsertSource self)
    throws PositionedError
  {
    print("DEFAULT VALUES");
  }

  /**
   * Visits a DeleteStatement node.
   */
  public void visitDeleteStatement(DeleteStatement self, TableName table, SearchCondition searchCondition)
    throws PositionedError
  {
    print("DELETE FROM ");
    table.accept(this);
    if (searchCondition != null) {
      println();
      print("WHERE ");
      pos += TAB_AND_HALF;
      searchCondition.accept(this);
      pos -= TAB_AND_HALF;
    }
  }

  /**
   * Visits a DoubleType node.
   */
  public void visitDoubleType(DoubleType self)
    throws PositionedError
  {
    print("DOUBLE");
  }

  /**
   * Visits a ExistsPredicate node.
   */
  public void visitExistsPredicate(ExistsPredicate self, SubTableExpression sub)
    throws PositionedError
  {
    println("EXISTS (");
    sub.accept(this);
    print(")");
  }

  /**
   * Visits a ExpressionList node.
   */
  public void visitExpressionList(ExpressionList self, List exprs)
    throws PositionedError
  {
    for (int i = 0; i < exprs.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      ((Expression)exprs.get(i)).accept(this);
    }
  }

  /**
   * Visits a ExpressionPredicate node.
   */
  public void visitExpressionPredicate(ExpressionPredicate self, Expression expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /**
   * Visits a ExtendedPredicate node.
   */
  public void visitExtendedPredicate(ExtendedPredicate self, Predicate left,  String operator, Predicate right)
    throws PositionedError
  {
    left.accept(this);
    print(" ");
    if (operator != null) {
      print(operator);
      print(" ");
    }
    right.accept(this);
  }

  /**
   * Visits a FieldNameList node.
   */
  public void visitFieldNameList(FieldNameList self, List exprs)
    throws PositionedError
  {
    for (int i = 0; i < exprs.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      print(exprs.get(i));
    }
  }

  /**
   * Visits a FieldReference node.
   */
  public void visitFieldReference(FieldReference self, String table, String ident, boolean hasPlus)
    throws PositionedError
  {
    if (table != null) {
      print(table);
      print(".");
    }
    print(ident);
    if (hasPlus) {
      print("(+)");
    }
  }

  /**
   * Visits a FloatType node.
   */
  public void visitFloatType(FloatType self)
    throws PositionedError
  {
    print("FLOAT");
  }

  /**
   * Visits a FromClause node.
   */
  public void visitFromClause(FromClause self, ArrayList tableRefs)
    throws PositionedError
  {
    print("FROM");

    pos += TAB_AND_HALF;

    for (int i = 0; i < tableRefs.size(); i++) {
      if (i != 0) {
	println(",");
      }
      ((SimpleTableReference)tableRefs.get(i)).accept(this);
    }
    println();

    pos -= TAB_AND_HALF;
  }

  /**
   * Visits a GroupByClause node.
   */
  public void visitGroupByClause(GroupByClause self, ArrayList fields)
    throws PositionedError
  {
    print("GROUP BY ");
    pos += TAB_AND_HALF;
    for (int i = 0; i < fields.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      ((FieldReference)fields.get(i)).accept(this);
    }
    pos -= TAB_AND_HALF;
    println();
  }

  /**
   * Visits a HavingClause node.
   */
  public void visitHavingClause(HavingClause self, SearchCondition condition)
    throws PositionedError
  {
    print("HAVING");
    pos += TAB_AND_HALF;
    condition.accept(this);
    pos -= TAB_AND_HALF;
    println();
  }

  /**
   * Visits a IfExpression node.
   */
  public void visitIfExpression(IfExpression self, SearchCondition condition, Expression thenExpr, Expression elseExpr)
    throws PositionedError
  {
    print("IF");
    pos += TAB_SIZE;
    condition.accept(this);
    pos -= TAB_SIZE;

    println();
    print("THEN");
    pos += TAB_SIZE;
    thenExpr.accept(this);
    pos -= TAB_SIZE;

    println();
    print("ELSE");
    pos += TAB_SIZE;
    elseExpr.accept(this);
    pos -= TAB_SIZE;

    println();
    print("FI");
  }

  /**
   * Visits decode a expression
   */
  public void visitDecodeExpression(DecodeExpression self,
                                    Expression checkExpr,
                                    Expression[][] searchResult,
                                    Expression defaultExpr)
    throws PositionedError
  {
    print("DECODE(");
    checkExpr.accept(this);
    if (searchResult != null) {
      for (int i = 0; i < searchResult.length; i++) {
	print(", ");
        searchResult[i][0].accept(this);
	print(", ");
        searchResult[i][1].accept(this);
      }
    }
    print(", ");
    defaultExpr.accept(this);
    print(")");
  }

  /**
   * Visits a InsertStatement node.
   */
  public void visitInsertStatement(InsertStatement self, String ident, FieldNameList fields, InsertSource source)
    throws PositionedError
  {
    print("INSERT INTO ");
    print(ident);
    pos += TAB_AND_HALF;
    if (fields != null) {
      List      exprs = fields.getFields();

      println(" (");
      pos += 4;
      for (int i = 0; i < exprs.size(); i++) {
        if (i != 0) {
          println(",");
        }
        print(exprs.get(i));
      }
      println();
      pos -= 4;
      print(")");
    }
    println();
    pos -= TAB_AND_HALF;
    source.accept(this);
  }

  /**
   * Visits a IntegerLiteral node.
   */
  public void visitIntegerLiteral(IntegerLiteral self, Integer value) throws PositionedError  {
    print(value);
  }

  /**
   * Visits a IntegerType node.
   */
  public void visitIntegerType(IntegerType self)
    throws PositionedError
  {
    print("INTEGER");
  }

  /**
   * Visits a IntersectSpec node.
   */
  public void visitIntersectSpec(IntersectSpec self, CorrespondingSpec spec)
    throws PositionedError
  {
    print("INTERSECT");
    if (spec != null) {
      print(" ");
      spec.accept(this);
    }
  }

  /**
   * Visits a IntersectTableReference node.
   */
  public void visitIntersectTableReference(IntersectTableReference self,
					   IntersectSpec spec,
					   TableReference left,
					   TableReference right)
    throws PositionedError
  {
    left.accept(this);
    print(" ");
    spec.accept(this);
    print(" ");
    right.accept(this);
  }

  /**
   * Visits a IntersectTableTerm node.
   */
  public void visitIntersectTableTerm(IntersectTableTerm self,
				      IntersectSpec spec,
				      SubTable left,
				      SubTable right)
    throws PositionedError
  {
    left.accept(this);
    print(" ");
    spec.accept(this);
    print(" ");
    right.accept(this);
  }

  /**
   * Visits a IsPredicate node.
   */
  public void visitIsPredicate(IsPredicate self, Predicate predicate, boolean hasNot)
    throws PositionedError
  {
    predicate.accept(this);
    if (hasNot) {
      print(" IS NOT NULL");
    } else {
      print(" IS NULL");
    }
  }

  /**
   * Visits a JdbcDateLiteral node.
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
    print("{d ");
    print(value);
    print("}");
  }

  /**
   * Visits a JdbcEscape node.
   */
  public void visitJdbcEscape(JdbcEscape self, String name)
    throws PositionedError
  {
    print("{escape ");
    print(name);
    print("}");
  }

  /**
   * Visits a JdbcFunction node.
   */
  public void visitJdbcFunction(JdbcFunction self, String name, ExpressionList params)
    throws PositionedError
  {
    print("{fn ");
    print(name);
    print("(");
    if (params != null) {
      params.accept(this);
    }
    print(")}");
  }

  /**
   * Visits a JdbcTimeLiteral node.
   */
  public void visitJdbcTimeLiteral(JdbcTimeLiteral self, Time value)
    throws PositionedError
  {
    print("{t ");
    print(value);
    print("}");
  }

  /**
   * Visits a JdbcTimeStampLiteral node.
   */
  public void visitJdbcTimeStampLiteral(JdbcTimeStampLiteral self, Timestamp value)
    throws PositionedError
  {
    print("{ts ");
    print(value);
    print("}");
  }

  /**
   * Visits a LikePredicate node.
   */
  public void visitLikePredicate(LikePredicate self, String sens, Expression expr, JdbcEscape escape)
    throws PositionedError
  {
    print(" LIKE ");
    if (sens != null) {
      print(" ");
      print(sens);
    }
    print(" ");
    expr.accept(this);
    if (escape != null) {
      print(" ");
      escape.accept(this);
    }
  }

  /**
   * Visits a MatchesPredicate node.
   */
  public void visitMatchesPredicate(MatchesPredicate self,
				    String sens,
				    Expression matchesExpr,
				    Expression escapeExpr)
    throws PositionedError
  {
    print(" MATCHES ");
    if (sens != null) {
      print(" ");
      print(sens);
    }
    print(" ");
    matchesExpr.accept(this);
    if (escapeExpr != null) {
      print(" ESCAPE ");
      escapeExpr.accept(this);
    }
  }

  /**
   * Visits a NotCondition node.
   */
  public void visitNotCondition(NotCondition self, SearchCondition condition)
    throws PositionedError
  {
    print("NOT ");
    condition.accept(this);
  }

  /**
   * Visits a NullLiteral node.
   */
  public void visitNullLiteral(NullLiteral self)
    throws PositionedError
  {
    print("NULL");
  }

  /**
   * Visits a NumericLiteral node.
   */
  public void visitNumericLiteral(NumericLiteral self, Fixed value)
    throws PositionedError
  {
    print(value.toSql());
  }

  /**
   * Visits a NumericType node.
   */
  public void visitNumericType(NumericType self, IntegerLiteral left, IntegerLiteral right)
    throws PositionedError
  {
    print("NUMERIC");
    if (left != null) {
      print("(");
      left.accept(this);
      if (right != null) {
	print(", ");
	right.accept(this);
      }
      print(")");
    }
  }

  /**
   * Visits a OnJoinPred node.
   */
  public void visitOnJoinPred(OnJoinPred self, SearchCondition condition)
    throws PositionedError
  {
    print("ON ");
    condition.accept(this);
  }

  /**
   * Visits a OrCondition node.
   */
  public void visitOrCondition(OrCondition self, SearchCondition left, SearchCondition right)
    throws PositionedError
  {
    left.accept(this);
    println();
    pos -= TAB_AND_HALF;
    print("OR ");
    pos += TAB_AND_HALF;
    right.accept(this);
  }
  
  /**
   * Visits a JdbcOuterJoin node.
   */
  public void visitJdbcOuterJoin(JdbcOuterJoin self,
                                 TableReference leftTable,
                                 String type,
                                 TableReference rightTable,
                                 JoinPred joinPred)
    throws PositionedError
  {
    print("{oj ");
    leftTable.accept(this);
    print(" ");
    print(type);
    print(" OUTER JOIN ");
    rightTable.accept(this);
    joinPred.accept(this);
    print("}");
  }

  /**
   * Visits a OuterJoin node.
   */
  public void visitOuterJoin(OuterJoin self,
			     TableReference leftTable,
                             SubOuterJoin[] subOuterJoins
                             )
    throws PositionedError
  {
    leftTable.accept(this);
    for (int i = 0; i < subOuterJoins.length; i++) {
      print(" ");
      print(subOuterJoins[i].getType());
      print(" OUTER JOIN ");
      subOuterJoins[i].getTable().accept(this);
      subOuterJoins[i].getJoinPred().accept(this);
    }
  }

  /**
   * Visits a ParenthesedTableReference node.
   */
  public void visitParenthesedTableReference(ParenthesedTableReference self, TableReference table)
    throws PositionedError
  {
    print("(");
    table.accept(this);
    print(")");
  }

  /**
   * Visits a QuantifiedPredicate node.
   */
  public void visitQuantifiedPredicate(QuantifiedPredicate self, String type, SubTableExpression table)
    throws PositionedError
  {
    print(type);
    table.accept(this);
  }

  /**
   * Visits a RealType node.
   */
  public void visitRealType(RealType self)
    throws PositionedError
  {
    print("REAL");
  }

  /**
   * Visits a SearchedCaseExpression node.
   */
  public void visitSearchedCaseExpression(SearchedCaseExpression self, ArrayList conditions, Expression elseExpr)
    throws PositionedError
  {
    for (int i = 0; i < conditions.size(); i++) {
      ((SearchedWhenClause)conditions.get(i)).accept(this);
      println();
    }
    if (elseExpr != null) {
      print("ELSE ");
      pos += TAB_SIZE;
      elseExpr.accept(this);
      pos -= TAB_SIZE;
      println();
    }
  }

  /**
   * Visits a SearchedWhenClause node.
   */
  public void visitSearchedWhenClause(SearchedWhenClause self, SearchCondition condition, Expression thenExpr)
    throws PositionedError
  {
    print("WHEN ");
    pos += TAB_SIZE;
    condition.accept(this);
    pos -= TAB_SIZE;
    println();
    print("THEN ");
    pos += TAB_SIZE;
    thenExpr.accept(this);
    pos -= TAB_SIZE;
  }

  /**
   * Visits a SelectElemExpression node.
   */
  public void visitSelectElemExpression(SelectElemExpression self, Expression columnExpr, String newColumnName)
    throws PositionedError
  {
    columnExpr.accept(this);
    if (newColumnName != null && !columnExpr.toString().equals(newColumnName)) {
      print(" AS ");
      print(newColumnName);
    }
  }

  /**
   * Visits a SelectElemStar node.
   */
  public void visitSelectElemStar(SelectElemStar self, String ident)
    throws PositionedError
  {
    if (ident != null) {
      print(ident);
      print(".*");
    } else  {
      print("*");
    }
  }

  /**
   * Visits a SelectExpression node.
   */
  public void visitSelectExpression(SelectExpression self,
				    String type,
				    ArrayList columns,
				    FromClause fromClause,
				    WhereClause whereClause,
				    GroupByClause groupByClause,
				    HavingClause havingClause)
    throws PositionedError
  {
    int oldPos = pos;

    posToQueryTab();

    print("SELECT ");

    pos += TAB_AND_HALF;
    if (type != null) {
      print(type);
      print(" ");
    }
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
	println(",");
      }
      ((SelectElem)columns.get(i)).accept(this);
    }
    println();
    pos -= TAB_AND_HALF;

    fromClause.accept(this);

    if (whereClause != null) {
      whereClause.accept(this);
    }
    if (groupByClause != null) {
      groupByClause.accept(this);
    }
    if (havingClause !=null) {
      havingClause.accept(this);
    }

    pos = oldPos;
  }

  /**
   * Visits a SelectStatement node.
   */
  public void visitSelectStatement(SelectStatement self,
				   String type,
				   ArrayList columns,
				   FromClause fromClause,
				   WhereClause whereClause,
				   GroupByClause groupByClause,
				   HavingClause havingClause,
				   SortSpec sortSpec,
				   UpdSpec updateSpec)
    throws PositionedError
  {
    int oldPos = pos;

    posToQueryTab();

    print("SELECT ");

    pos += TAB_AND_HALF;
    if (type != null) {
      print(type);
      print(" ");
    }
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
	println(",");
      }
      ((SelectElem)columns.get(i)).accept(this);
    }
    println();
    pos -= TAB_AND_HALF;

    fromClause.accept(this);

    if (whereClause != null) {
      whereClause.accept(this);
    }
    if (groupByClause != null) {
      groupByClause.accept(this);
    }
    if (havingClause !=null) {
      havingClause.accept(this);
    }
    if (sortSpec != null) {
      sortSpec.accept(this);
    }
    if (updateSpec != null) {
      updateSpec.accept(this);
    }

    pos = oldPos;
  }

  /**
   * Visits a SelectTableReference node.
   */
  public void visitSelectTableReference(SelectTableReference self, SelectExpression expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /**
   * Visits a SetFunction node.
   */
  public void visitSetFunction(SetFunction self, String oper, int type, Expression expr)
    throws PositionedError
  {
    print(oper);
    print("(");
    switch(type) {
    case SetFunction.TYP_NONE:
      break;
    case SetFunction.TYP_ALL:
      print("ALL ");
      break;
    case SetFunction.TYP_DISTINCT:
      print("DISTINCT ");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
    expr.accept(this);
    print(")");
  }

  /**
   * Visits a SimpleCaseExpression node.
   */
  public void visitSimpleCaseExpression(SimpleCaseExpression self, Expression expr, ArrayList conditions, Expression elseExpr)
    throws PositionedError
  {
    pos += TAB_SIZE;
    expr.accept(this);
    pos -= TAB_SIZE;
    println();
    for (int i = 0; i < conditions.size(); i++) {
      ((SimpleWhenClause)conditions.get(i)).accept(this);
      println();
    }
    if (elseExpr != null) {
      print("ELSE ");
      pos += TAB_SIZE;
      elseExpr.accept(this);
      pos -= TAB_SIZE;
      println();
    }
  }

  /**
   * Visits a SimpleIdentExpression node.
   */
  public void visitSimpleIdentExpression(SimpleIdentExpression self, String ident)
    throws PositionedError
  {
    print(ident);
  }

  /**
   * Visits a SimpleSearchCondition node.
   */
  public void visitSimpleSearchCondition(SimpleSearchCondition self, Predicate predicate)
    throws PositionedError
  {
    predicate.accept(this);
  }

  /**
   * Visits a SimpleSubTableReference node.
   */
  public void visitSimpleSubTableReference(SimpleSubTableReference self, TableAlias alias, TableReference table)
    throws PositionedError
  {
    print("(");
    table.accept(this);
    print(") ");
    alias.accept(this);
  }

  /**
   * Visits a SimpleTableReference node.
   */
  public void visitSimpleTableReference(SimpleTableReference self, Expression name, TableAlias alias)
    throws PositionedError
  {
    name.accept(this);
    if (alias != null && alias.getIdent() != null) {
      print(" ");
      alias.accept(this);
    }
  }

  /**
   * Visits a SimpleWhenClause node.
   */
  public void visitSimpleWhenClause(SimpleWhenClause self, Expression whenExpr, Expression thenExpr)
    throws PositionedError
  {
    print("WHEN ");
    pos += TAB_SIZE;
    whenExpr.accept(this);
    pos -= TAB_SIZE;
    println();
    print("THEN ");
    pos += TAB_SIZE;
    thenExpr.accept(this);
    pos -= TAB_SIZE;
  }

  /**
   * Visits a SmallIntType node.
   */
  public void visitSmallIntType(SmallIntType self)
    throws PositionedError
  {
    print("SMALLINT");
  }

  /**
   * Visits a SortElem node.
   */
  public void visitSortElem(SortElem self, Expression elem, boolean isDesc)
    throws PositionedError
  {
    elem.accept(this);
    if (isDesc) {
      print(" DESC");
    }
  }

  /**
   * Visits a SortSpec node.
   */
  public void visitSortSpec(SortSpec self, ArrayList elems)
    throws PositionedError
  {
    print("ORDER BY ");
    pos += TAB_AND_HALF;
    for (int i = 0; i < elems.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      ((SortElem)elems.get(i)).accept(this);
    }
    pos -= TAB_AND_HALF;
  }

  /**
   * Visits a StringLiteral node.
   */
  public void visitStringLiteral(StringLiteral self, String value)
    throws PositionedError
  {
    print("'");
    print(value);
    print("'");
  }

  /**
   * Visits a StringType node.
   */
  public void visitStringType(StringType self)
    throws PositionedError
  {
    print("STRING");
  }

  /**
   * Visits a SubTableExpression node.
   */
  public void visitSubTableExpression(SubTableExpression self, SubTable table)
    throws PositionedError
  {
    print("(");
    table.accept(this);
    print(")");
  }

  /**
   * Visits a SubTableReference node.
   */
  public void visitSubTableReference(SubTableReference self, SearchCondition expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /**
   * Visits a TableAlias node.
   */
  public void visitTableAlias(TableAlias self, String name, FieldNameList list)
    throws PositionedError
  {
    if (name != null) {
      print(" ");
      print(name);
    }
    if (list != null) {
      print("(");
      list.accept(this);
      print(")");
    }
  }

  /**
   * Visits a TableExpressionInsertSource node.
   */
  public void visitTableExpressionInsertSource(TableExpressionInsertSource self, TableReference table)
    throws PositionedError
  {
    table.accept(this);
  }

  /**
   * Visits a TableInsertSource node.
   */
  public void visitTableInsertSource(TableInsertSource self, ArrayList table)
    throws PositionedError
  {
    print("TABLE (");
    for (int i = 0; i < table.size(); i++) {
      if (i != 0) {
	println(",");
      }
      ((ValueList)table.get(i)).accept(this);
    }
    print(")");
  }

  /**
   * Visits a TableName node.
   */
  public void visitTableName(TableName self, Expression name, TableAlias alias)
    throws PositionedError
  {
    name.accept(this);
    if (alias != null && alias.getIdent() != null) {
      print(" ");
      alias.accept(this);
    }
  }

  /**
   * Visits a TinyIntType node.
   */
  public void visitTinyIntType(TinyIntType self)
    throws PositionedError
  {
    print("TINYINT");
  }

  /**
   * Visits a TupleInPredicate node.
   */
  public void visitTupleInPredicate(TupleInPredicate self, ExpressionList exprs, boolean hasNot, SubTableExpression table)
    throws PositionedError
  {
    print("[");
    exprs.accept(this);
    print("]");
    if (hasNot) {
      print(" NOT ");
    }
    println(" IN ");
    table.accept(this);
  }


  /**
   * Visits a TupleAssignment node.
   */
  public void visitTupleAssignment(TupleAssignment self,
                                   FieldNameList fields,
                                   SubTableExpression subTable)
    throws PositionedError
  {
    print("(");
    fields.accept(this);
    print(")");
    println(" = ");
    subTable.accept(this);
  }

  /**
   * Visits a TypeName node.
   */
  public void visitTypeName(TypeName self, String ident)
    throws PositionedError
  {
    print(ident);
  }

  /**
   * Visits a UnaryExpression node.
   */
  public void visitUnaryExpression(UnaryExpression self, String opt, Expression expr)
    throws PositionedError
  {
    print(opt);
    expr.accept(this);
  }

  /**
   * Visits a UnidiffSpec node.
   */
  public void visitUnidiffSpec(UnidiffSpec self, String oper, CorrespondingSpec spec)
    throws PositionedError
  {
    print(oper + " ");
    if (spec != null) {
      print(" ");
      spec.accept(this);
    }
  }

  /**
   * Visits a UnidiffTableReference node.
   */
  public void visitUnidiffTableReference(UnidiffTableReference self, UnidiffSpec spec, TableReference left, TableReference right)
    throws PositionedError
  {
    left.accept(this);
    print(" ");
    spec.accept(this);
    print(" ");
    right.accept(this);
  }

  /**
   * Visits a UnidiffTableTerm node.
   */
  public void visitUnidiffTableTerm(UnidiffTableTerm self, UnidiffSpec spec, SubTable left, SubTable right)
    throws PositionedError
  {
    left.accept(this);
    print(" ");
    spec.accept(this);
    print(" ");
    right.accept(this);
  }

  /**
   * Visits a UpdSpec node.
   */
  public void visitUpdSpec(UpdSpec self)
    throws PositionedError
  {
    print("FOR UPDATE");
  }

  /**
   * Visits a UpdateStatement node.
   */
  public void visitUpdateStatement(UpdateStatement self,
				   TableName table,
				   ArrayList listIdent,
				   SearchCondition searchCondition,
				   boolean hasCondition)
    throws PositionedError
  {
    print("UPDATE ");

    pos += TAB_AND_HALF;
    table.accept(this);
    println();
    pos -= TAB_AND_HALF;
    print("SET ");
    pos += TAB_AND_HALF;

    for (int i = 0; i < listIdent.size(); i++) {
      if (i != 0) {
	println(",");
      }
      ((Assignment)listIdent.get(i)).accept(this);
    }

    if (hasCondition) {
      println();
      pos -= TAB_AND_HALF;
      print("WHERE ");
      if (searchCondition != null) {
	pos += TAB_AND_HALF;
	searchCondition.accept(this);
	pos -= TAB_AND_HALF;
      } else {
	print("CURRENT");
      }
    }
  }

  /**
   * Visits a UsingJoinPred node.
   */
  public void visitUsingJoinPred(UsingJoinPred self, FieldNameList list)
    throws PositionedError
  {
    print("USING (");
    list.accept(this);
    print(")");
  }

  /**
   * Visits a ValueInPredicate node.
   */
  public void visitValueInPredicate(ValueInPredicate self, ExpressionList value)
    throws PositionedError
  {
    print("  IN (");
    value.accept(this);
    print(")");
  }

  /**
   * Visits a ValueList node.
   */
  public void visitValueList(ValueList self, ExpressionList expr)
    throws PositionedError
  {
    print("(");
    expr.accept(this);
    print(")");
  }

  /**
   * Visits a ValueListInsertSource node.
   */
  public void visitValueListInsertSource(ValueListInsertSource self, ValueList value)
    throws PositionedError
  {
    List        exprs;

    print("VALUES ");
    pos += TAB_AND_HALF;
    println("(");
    pos += 4;
    exprs = value.getList().getExpression();
    for (int i = 0; i < exprs.size(); i++) {
      if (i != 0) {
	println(",");
      }
      ((Expression)exprs.get(i)).accept(this);
    }
    println();
    pos -= 4;
    print(")");
    pos -= TAB_AND_HALF;
  }

  /**
   * Visits a WhereClause node.
   */
  public void visitWhereClause(WhereClause self, SearchCondition condition)
    throws PositionedError
  {
    print("WHERE");
    pos += TAB_AND_HALF;
    condition.accept(this);
    println();
    pos -= TAB_AND_HALF;
  }

  // ----------------------------------------------------------------------
  // PROTECTED METHODS
  // ----------------------------------------------------------------------

  /**
   * change the position to n*8+4 from the beginning of the line.
   *
   */
  protected void posToQueryTab() {
    
    while( ((p.getPos() + pos) % 8) == 4 ){
     pos++; 
    } 
  }

  protected void print(String str) {
    checkPos();
    p.print(str);
    column += str.length();
  }

  protected void print(Integer str) {
    print("" + str);
  }

  protected void print(Object str) {
    print("" + str);
  }

  protected void print(Double str) {
    print("" + str);
  }

  protected void println(String str) {
    print(str);
    println();
  }

  protected void println() {
    p.println();
    column = 0;
    line++;
  }

  // ----------------------------------------------------------------------
  // TABS
  // ----------------------------------------------------------------------

  protected void checkPos() {
    if (column < pos) {
      p.print(space(pos - column, pos));
      column = Math.max(column, pos);
    }
  }

  protected String space(int count, int pos) {
    if (count <= 0) {
      count = 1;
    }
    return spaceIn(count, pos);
  }

  protected String spaceIn(int count, int pos) {
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < count; i++) {
      //buf.append(("" + pos).substring(("" + pos).length()-1));
      buf.append(' ');
    }
    return buf.toString();
  }

  // ----------------------------------------------------------------------
  // CONTEXT MANAGEMENT
  // ----------------------------------------------------------------------

  public SqlContext getContext() {
    return contexts.size() > 0 ? (SqlContext)contexts.peek() : null;
  }

  public void enter(SqlContext context) {
    contexts.push(context);
  }

  public void exit(SqlContext context) {
    if (contexts.pop() != context) {
      throw new InconsistencyException();
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int              DEFAULT_TAB_SIZE = 8;

  protected final int			TAB_SIZE;
  protected final int                   TAB_AND_HALF;
  protected final TabbedPrintWriter     p;
  protected final Stack			contexts;

  protected int				pos;
  protected int				line;
  protected int				column;
}
