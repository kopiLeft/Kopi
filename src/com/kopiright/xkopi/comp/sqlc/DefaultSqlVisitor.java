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

package com.kopiright.xkopi.comp.sqlc;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.Time;
import com.kopiright.xkopi.lib.type.Timestamp;

/**
 * This class implements a default SQL visitor.
 */
public class DefaultSqlVisitor implements SqlVisitor {

  // ----------------------------------------------------------------------
  // VISITORS
  // ----------------------------------------------------------------------

  /**
   * Visits a AndCondition node.
   */
  public void visitAndCondition(AndCondition self, SearchCondition left, SearchCondition right)
    throws PositionedError
  {
    left.accept(this);
    right.accept(this);
  }

  /**
   * Visits a ArrayPrecision node.
   */
  public void visitArrayPrecision(ArrayPrecision self, IntegerLiteral size)
    throws PositionedError
  {
    if (size != null) {
      size.accept(this);
    }
  }

  /**
   * Visits a Assignment node.
   */
  public void visitAssignment(Assignment self, Expression left, Expression right)
    throws PositionedError
  {
    left.accept(this);
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
    start.accept(this);
    end.accept(this);
  }

  /**
   * Visits a BinaryExpression node.
   */
  public void visitBinaryExpression(BinaryExpression self, String operator, Expression left, Expression right)
    throws PositionedError
  {
    left.accept(this);
    right.accept(this);
  }

  /**
   * Visits a BlobType node.
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
  }

  /**
   * Visits a ClobType node.
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
  }

  /**
   * Visits a BoolType node.
   */
  public void visitBoolType(BoolType self)
    throws PositionedError
  {
  }

  /**
   * Visits a BooleanLiteral node.
   */
  public void visitBooleanLiteral(BooleanLiteral self, boolean value)
    throws PositionedError
  {
  }

  /**
   * Visits a CaseExpression node.
   */
  public void visitCaseExpression(CaseExpression self, AbstractCaseExpression cases)
    throws PositionedError
  {
    cases.accept(this);
  }

  /**
   * Visits a CastPrimary node.
   */
  public void visitCastPrimary(CastPrimary self, Expression left, Type right)
    throws PositionedError
  {
    left.accept(this);
    right.accept(this);
  }

  /**
   * Visits a CharType node.
   */
  public void visitCharType(CharType self, ArrayPrecision prec)
    throws PositionedError
  {
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
    right.accept(this);
  }

  /**
   * Visits a CorrespondingSpec node.
   */
  public void visitCorrespondingSpec(CorrespondingSpec self, FieldNameList fields)
    throws PositionedError
  {
    if (fields != null) {
      fields.accept(this);
    }
  }

  /**
   * Visits a CountExpression node.
   */
  public void visitCountExpression(CountExpression self, boolean hasDistinct, boolean countAll, Expression expr)
    throws PositionedError
  {
    if (!countAll) {
      expr.accept(this);
    }
  }

  /**
   * Visits a DefaultValuesInsertSource node.
   */
  public void visitDefaultValuesInsertSource(DefaultValuesInsertSource self)
    throws PositionedError
  {
  }

  /**
   * Visits a DeleteStatement node.
   */
  public void visitDeleteStatement(DeleteStatement self, TableName table, SearchCondition searchCondition)
    throws PositionedError
  {
    table.accept(this);
    if (searchCondition != null) {
      searchCondition.accept(this);
    }
  }

  /**
   * Visits a DoubleType node.
   */
  public void visitDoubleType(DoubleType self)
    throws PositionedError
  {
  }

  /**
   * Visits a ExistsPredicate node.
   */
  public void visitExistsPredicate(ExistsPredicate self, SubTableExpression sub)
    throws PositionedError
  {
    sub.accept(this);
  }

  /**
   * Visits a ExpressionList node.
   */
  public void visitExpressionList(ExpressionList self, List exprs)
    throws PositionedError
  {
    for (int i = 0; i < exprs.size(); i++) {
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
    right.accept(this);
  }

  /**
   * Visits a FieldNameList node.
   */
  public void visitFieldNameList(FieldNameList self, List exprs)
    throws PositionedError
  {
  }

  /**
   * Visits a FieldReference node.
   */
  public void visitFieldReference(FieldReference self, String table, String ident, boolean hasPlus)
    throws PositionedError
  {
  }

  /**
   * Visits a FloatType node.
   */
  public void visitFloatType(FloatType self)
    throws PositionedError
  {
  }

  /**
   * Visits a FromClause node.
   */
  public void visitFromClause(FromClause self, ArrayList tableRefs)
    throws PositionedError
  {
    for (int i = 0; i < tableRefs.size(); i++) {
      ((SimpleTableReference)tableRefs.get(i)).accept(this);
    }
  }

  /**
   * Visits a GroupByClause node.
   */
  public void visitGroupByClause(GroupByClause self, ArrayList fields)
    throws PositionedError
  {
    for (int i = 0; i < fields.size(); i++) {
      ((FieldReference)fields.get(i)).accept(this);
    }
  }

  /**
   * Visits a HavingClause node.
   */
  public void visitHavingClause(HavingClause self, SearchCondition condition)
    throws PositionedError
  {
    condition.accept(this);
  }

  /**
   * Visits a IfExpression node.
   */
  public void visitIfExpression(IfExpression self, SearchCondition condition, Expression thenExpr, Expression elseExpr)
    throws PositionedError
  {
    condition.accept(this);
    thenExpr.accept(this);
    elseExpr.accept(this);
  }

  /**
   * Visits a decode expression
   */
  public void visitDecodeExpression(DecodeExpression self,
                                    Expression checkExpr,
                                    Expression[][] searchResult,
                                    Expression defaultExpr)
    throws PositionedError
  {
    checkExpr.accept(this);
    if (searchResult != null) {
      for (int i = 0; i < searchResult.length; i++) {
        searchResult[i][0].accept(this);
        searchResult[i][1].accept(this);
      }
    }
    defaultExpr.accept(this);
  }

  /**
   * Visits a InsertStatement node.
   */
  public void visitInsertStatement(InsertStatement self, String ident, FieldNameList fields, InsertSource source)
    throws PositionedError
  {
    if (fields != null) {
      fields.accept(this);
    }
    source.accept(this);
  }

  /**
   * Visits a IntegerLiteral node.
   */
  public void visitIntegerLiteral(IntegerLiteral self, Integer value)
    throws PositionedError
  {
  }

  /**
   * Visits a IntegerType node.
   */
  public void visitIntegerType(IntegerType self)
    throws PositionedError
  {
  }

  /**
   * Visits a IntersectSpec node.
   */
  public void visitIntersectSpec(IntersectSpec self, CorrespondingSpec spec)
    throws PositionedError
  {
    if (spec != null) {
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
    spec.accept(this);
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
    spec.accept(this);
    right.accept(this);
  }

  /**
   * Visits a IsPredicate node.
   */
  public void visitIsPredicate(IsPredicate self, Predicate predicate, boolean hasNot)
    throws PositionedError
  {
    predicate.accept(this);
  }

  /**
   * Visits a JdbcDateLiteral node.
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
  }

  /**
   * Visits a JdbcEscape node.
   */
  public void visitJdbcEscape(JdbcEscape self, String name)
    throws PositionedError
  {
  }

  /**
   * Visits a JdbcFunction node.
   */
  public void visitJdbcFunction(JdbcFunction self, String name, ExpressionList params)
    throws PositionedError
  {
    if (params != null) {
      params.accept(this);
    }
  }

  /**
   * Visits a JdbcTimeLiteral node.
   */
  public void visitJdbcTimeLiteral(JdbcTimeLiteral self, Time value)
    throws PositionedError
  {
  }

  /**
   * Visits a JdbcTimeStampLiteral node.
   */
  public void visitJdbcTimeStampLiteral(JdbcTimeStampLiteral self, Timestamp value)
    throws PositionedError
  {
  }

  /**
   * Visits a LikePredicate node.
   */
  public void visitLikePredicate(LikePredicate self, String sens, Expression expr, JdbcEscape escape)
    throws PositionedError
  {
    expr.accept(this);
    if (escape != null) {
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
    matchesExpr.accept(this);
    if (escapeExpr != null) {
      escapeExpr.accept(this);
    }
  }

  /**
   * Visits a NotCondition node.
   */
  public void visitNotCondition(NotCondition self, SearchCondition condition)
    throws PositionedError
  {
    condition.accept(this);
  }

  /**
   * Visits a NullLiteral node.
   */
  public void visitNullLiteral(NullLiteral self)
    throws PositionedError
  {
  }

  /**
   * Visits a NumericLiteral node.
   */
  public void visitNumericLiteral(NumericLiteral self, Fixed value)
    throws PositionedError
  {
  }

  /**
   * Visits a NumericType node.
   */
  public void visitNumericType(NumericType self, IntegerLiteral left, IntegerLiteral right)
    throws PositionedError
  {
    if (left != null) {
      left.accept(this);
      if (right != null) {
	right.accept(this);
      }
    }
  }

  /**
   * Visits a OnJoinPred node.
   */
  public void visitOnJoinPred(OnJoinPred self, SearchCondition condition)
    throws PositionedError
  {
    condition.accept(this);
  }

  /**
   * Visits a OrCondition node.
   */
  public void visitOrCondition(OrCondition self, SearchCondition left, SearchCondition right)
    throws PositionedError
  {
    left.accept(this);
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
    leftTable.accept(this);
    rightTable.accept(this);
    joinPred.accept(this);
  }

  /**
   * Visits a OuterJoin node.
   */
  public void visitOuterJoin(OuterJoin self,
			     TableReference leftTable,
                             SubOuterJoin[] subOuterJoins)
    throws PositionedError
  {
    leftTable.accept(this);
    for (int i = 0; i < subOuterJoins.length; i++) {
      subOuterJoins[i].getTable().accept(this);
      subOuterJoins[i].getJoinPred().accept(this);
    }
  }

  /**
   * Visits a ParenthesedTableReference node.
   */
  public void visitParenthesedTableReference(ParenthesedTableReference self, TableReference expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /**
   * Visits a QuantifiedPredicate node.
   */
  public void visitQuantifiedPredicate(QuantifiedPredicate self, String type, SubTableExpression table)
    throws PositionedError
  {
    table.accept(this);
  }

  /**
   * Visits a RealType node.
   */
  public void visitRealType(RealType self)
    throws PositionedError
  {
  }

  /**
   * Visits a SearchedCaseExpression node.
   */
  public void visitSearchedCaseExpression(SearchedCaseExpression self, ArrayList conditions, Expression elseExpr)
    throws PositionedError
  {
    for (int i = 0; i < conditions.size(); i++) {
      ((SearchedWhenClause)conditions.get(i)).accept(this);
    }
    if (elseExpr != null) {
      elseExpr.accept(this);
    }
  }

  /**
   * Visits a SearchedWhenClause node.
   */
  public void visitSearchedWhenClause(SearchedWhenClause self, SearchCondition condition, Expression thenExpr)
    throws PositionedError
  {
    condition.accept(this);
    thenExpr.accept(this);
  }

  /**
   * Visits a SelectElemExpression node.
   */
  public void visitSelectElemExpression(SelectElemExpression self, Expression columnExpr, String newColumnName)
    throws PositionedError
  {
    columnExpr.accept(this);
  }

  /**
   * Visits a SelectElemStar node.
   */
  public void visitSelectElemStar(SelectElemStar self, String ident)
    throws PositionedError
  {
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
    for (int i = 0; i < columns.size(); i++) {
      ((SelectElem)columns.get(i)).accept(this);
    }

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
    for (int i = 0; i < columns.size(); i++) {
      ((SelectElem)columns.get(i)).accept(this);
    }

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
    expr.accept(this);
  }

  /**
   * Visits a SimpleCaseExpression node.
   */
  public void visitSimpleCaseExpression(SimpleCaseExpression self, Expression expr, ArrayList conditions, Expression elseExpr)
    throws PositionedError
  {
    expr.accept(this);
    for (int i = 0; i < conditions.size(); i++) {
      ((SimpleWhenClause)conditions.get(i)).accept(this);
    }
    if (elseExpr != null) {
      elseExpr.accept(this);
    }
  }

  /**
   * Visits a SimpleIdentExpression node.
   */
  public void visitSimpleIdentExpression(SimpleIdentExpression self, String ident)
    throws PositionedError
  {
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
    table.accept(this);
    alias.accept(this);
  }

  /**
   * Visits a SimpleTableReference node.
   * @param	name		the table name
   * @param	alias		the alias for this reference
   */
  public void visitSimpleTableReference(SimpleTableReference self, Expression name, TableAlias alias)
    throws PositionedError
  {
    name.accept(this);
    if (alias != null) {
      alias.accept(this);
    }
  }

  /**
   * Visits a SimpleWhenClause node.
   */
  public void visitSimpleWhenClause(SimpleWhenClause self, Expression whenExpr, Expression thenExpr)
    throws PositionedError
  {
    whenExpr.accept(this);
    thenExpr.accept(this);
  }

  /**
   * Visits a SmallIntType node.
   */
  public void visitSmallIntType(SmallIntType self)
    throws PositionedError
  {
  }

  /**
   * Visits a SortElem node.
   */
  public void visitSortElem(SortElem self, Expression elem, boolean isDesc)
    throws PositionedError
  {
    elem.accept(this);
  }

  /**
   * Visits a SortSpec node.
   */
  public void visitSortSpec(SortSpec self, ArrayList elems)
    throws PositionedError
  {
    for (int i = 0; i < elems.size(); i++) {
      ((SortElem)elems.get(i)).accept(this);
    }
  }

  /**
   * Visits a StringLiteral node.
   */
  public void visitStringLiteral(StringLiteral self, String value)
    throws PositionedError
  {
  }

  /**
   * Visits a StringType node.
   */
  public void visitStringType(StringType self)
    throws PositionedError
  {
  }

  /**
   * Visits a SubTableExpression node.
   */
  public void visitSubTableExpression(SubTableExpression self, SubTable table)
    throws PositionedError
  {
    table.accept(this);
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
    if (list != null) {
      list.accept(this);
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
    for (int i = 0; i < table.size(); i++) {
      ((ValueList)table.get(i)).accept(this);
    }
  }

  /**
   * Visits a TableName node.
   * @param	name		the table name
   * @param	alias		the alias for this reference
   */
  public void visitTableName(TableName self, Expression name, TableAlias alias)
    throws PositionedError
  {
    name.accept(this);
    if (alias != null) {
      alias.accept(this);
    }
  }

  /**
   * Visits a TinyIntType node.
   */
  public void visitTinyIntType(TinyIntType self)
    throws PositionedError
  {
  }

  /**
   * Visits a TupleInPredicate node.
   */
  public void visitTupleInPredicate(TupleInPredicate self, ExpressionList exprs, boolean hasNot, SubTableExpression table)
    throws PositionedError
  {
    exprs.accept(this);
    table.accept(this);
  }

  /**
   * Visits TupleAssignment
   */
  public void visitTupleAssignment(TupleAssignment self,
                                   FieldNameList fields,
                                   SubTableExpression table)
    throws PositionedError
  {
    fields.accept(this);
    table.accept(this);
  }


  /**
   * Visits a TypeName node.
   */
  public void visitTypeName(TypeName self, String ident)
    throws PositionedError
  {
  }

  /**
   * Visits a UnaryExpression node.
   */
  public void visitUnaryExpression(UnaryExpression self, String operator, Expression operand)
    throws PositionedError
  {
    operand.accept(this);
  }

  /**
   * Visits a UnidiffSpec node.
   */
  public void visitUnidiffSpec(UnidiffSpec self, String oper, CorrespondingSpec spec)
    throws PositionedError
  {
    if (spec != null) {
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
    spec.accept(this);
    right.accept(this);
  }

  /**
   * Visits a UnidiffTableTerm node.
   */
  public void visitUnidiffTableTerm(UnidiffTableTerm self, UnidiffSpec spec, SubTable left, SubTable right)
    throws PositionedError
  {
    left.accept(this);
    spec.accept(this);
    right.accept(this);
  }

  /**
   * Visits a UpdSpec node.
   */
  public void visitUpdSpec(UpdSpec self)
    throws PositionedError
  {
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

    table.accept(this);
    for (int i = 0; i < listIdent.size(); i++) {
      ((Assignment)listIdent.get(i)).accept(this);
    }

    if (hasCondition) {
      if (searchCondition != null) {
	searchCondition.accept(this);
      }
    }
  }

  /**
   * Visits a UsingJoinPred node.
   */
  public void visitUsingJoinPred(UsingJoinPred self, FieldNameList list)
    throws PositionedError
  {
    list.accept(this);
  }

  /**
   * Visits a ValueInPredicate node.
   */
  public void visitValueInPredicate(ValueInPredicate self, ExpressionList value)
    throws PositionedError
  {
    value.accept(this);
  }

  /**
   * Visits a ValueList node.
   */
  public void visitValueList(ValueList self, ExpressionList expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /**
   * Visits a ValueListInsertSource node.
   */
  public void visitValueListInsertSource(ValueListInsertSource self, ValueList value)
    throws PositionedError
  {
    value.accept(this);
  }

  /**
   * Visits a WhereClause node.
   */
  public void visitWhereClause(WhereClause self, SearchCondition condition)
    throws PositionedError
  {
    condition.accept(this);
  }

  // ----------------------------------------------------------------------
  // CONTEXT HANDLING
  // ----------------------------------------------------------------------

  public SqlContext getContext() {
    return !contexts.empty() ? (SqlContext)contexts.peek() : null;
  }

  public void enter(SqlContext context) {
    contexts.push(context);
  }

  public void exit(SqlContext context) {
    if (contexts.pop() != context) {
      throw new InconsistencyException("Contexts does not match.");
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected Stack			contexts;

  // ----------------------------------------------------------------------
  // INITIALIZER
  // ----------------------------------------------------------------------

  {
    contexts = new Stack();
  }

}
