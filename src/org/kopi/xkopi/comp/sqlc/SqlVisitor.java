/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.xkopi.comp.sqlc;

import java.util.ArrayList;
import java.util.List;

import org.kopi.compiler.base.PositionedError;
import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Fixed;
import org.kopi.xkopi.lib.type.Time;
import org.kopi.xkopi.lib.type.Timestamp;

/**
 * This interface defines SQL phylum visitors.
 */
public interface SqlVisitor {

  /**
   * Visits SELECT
   */
  void visitSelectStatement(SelectStatement self,
			    String type,
			    ArrayList columns,
			    FromClause fromClause,
			    WhereClause whereClause,
			    GroupByClause groupByClause,
			    HavingClause havingClause,
			    SortSpec sortSpec,
			    UpdSpec updateSpec)
    throws PositionedError;

  /**
   * Visits SELECT
   */
  void visitSelectExpression(SelectExpression self,
			     String type,
			     ArrayList columns,
			     FromClause fromClause,
			     WhereClause whereClause,
			     GroupByClause groupByClause,
			     HavingClause havingClause)
    throws PositionedError;

  /**
   * Visits GROUP BY
   * @param	fields	                   the fields refernces
   */
  void visitGroupByClause(GroupByClause self, ArrayList fields)
    throws PositionedError;

  /**
   * Visits HAVING
   * @param	condition	           the SqlPhylum for the having
   */
  void visitHavingClause(HavingClause self, SearchCondition condition)
    throws PositionedError;

  /**
   * Visits SORTELEM
   * @param	elem
   * @param	isDesc
   */
  void visitSortElem(SortElem self, Expression elem, boolean isDesc)
    throws PositionedError;

  /**
   * Visits UPDSPEC
   */
  void visitUpdSpec(UpdSpec self)
    throws PositionedError;

  /**
   * Visits SORTSPEC
   */
  void visitSortSpec(SortSpec self, ArrayList elems)
    throws PositionedError;

  /**
   * Visits SORTELEM
   * !!!
   void visitSortElem(SortElem self,
   IntegerLiteral intLiteral,
   IntegerLiteral exprLiteral,
   boolean hasAsc,
   boolean isAsc)
   throws PositionedError;

   /**
    * Visits UPDATE
    * @param	schemaName	schema name
    * @param	tableName	table name
    * @param	listIdent       vector of listIdent
    * @param	searchCondition search condition
    * @param	hasCondition     there is a search condition ?
    */
  void visitUpdateStatement(UpdateStatement self,
			    TableName table,
			    ArrayList listIdent,
			    SearchCondition searchCondition,
			    boolean hasCondition)
    throws PositionedError;

  /**
   * Visits Insert statement
   * @param	ident		type ident of the table
   * @param	fields		the field name list
   * @param	soure		the source of data from clause
   */
  void visitInsertStatement(InsertStatement self,
			    String ident,
			    FieldNameList fields,
			    InsertSource source)
    throws PositionedError;

  /**
   * Visits ExpressionList
   * @param	exprs	a vector of SqlPhylum
   */
  void visitExpressionList(ExpressionList self, List exprs)
    throws PositionedError;

  /**
   * Visits FieldNameList
   * @param	exprs	a vector of SqlPhylum
   */
  void visitFieldNameList(FieldNameList self, List exprs)
    throws PositionedError;

  /**
   * Visits WhereClause
   * @param	condition	a SqlPhylum
   */
  void visitWhereClause(WhereClause self, SearchCondition condition)
    throws PositionedError;

  /**
   * Visits NotCondition
   * @param	condition	a SqlPhylum
   */
  void visitNotCondition(NotCondition self, SearchCondition condition)
    throws PositionedError;

  /**
   * Visits SimpleSearchCondition
   * @param	predicate	a SqlPhylum
   */
  void visitSimpleSearchCondition(SimpleSearchCondition self, Predicate predicate)
    throws PositionedError;

  /**
   * Visits OnJoinPred
   * @param	condition
   */
  void visitOnJoinPred(OnJoinPred self, SearchCondition condition)
    throws PositionedError;

  /**
   * Visits UsingJoinPred
   * @param	list
   */
  void visitUsingJoinPred(UsingJoinPred self, FieldNameList list)
    throws PositionedError;

  /**
   * Visits AndCondition
   * @param	left	the left search condition
   * @param	right	the right search condition
   */
  void visitAndCondition(AndCondition self, SearchCondition left, SearchCondition right)
    throws PositionedError;

  /**
   * Visits OrCondition
   * @param	left	the left search condition
   * @param	right	the right search condition
   */
  void visitOrCondition(OrCondition self, SearchCondition left, SearchCondition right)
    throws PositionedError;

  /**
   * Visits FieldReference
   * @param	table	           the table name if any
   * @param	ident	           the field ident
   */
  void visitFieldReference(FieldReference self, String table, String ident, boolean hasPlus)
    throws PositionedError;

  /**
   * Visits BinaryExpression
   * @param	operator                   the operator
   * @param	left	                   the left expression
   * @param	right	                   the right expression
   */
  void visitBinaryExpression(BinaryExpression self,
			     String operator,
			     Expression left,
			     Expression right)
    throws PositionedError;

  /**
   * Visits SimpleIdentExpression
   * @param	ident	                   the ident
   */
  void visitSimpleIdentExpression(SimpleIdentExpression self, String ident)
    throws PositionedError;

  /**
   * Visits UnaryExpression
   * @param	expr	                   the unary expression
   * @param	operator                   the operator
   */
  void visitUnaryExpression(UnaryExpression self, String operator, Expression operand)
    throws PositionedError;

  /**
   * Visits FROM
   * @param	tableRefs	a vector of table references
   */
  void visitFromClause(FromClause self, ArrayList tableRefs)
    throws PositionedError;

  /**
   * Visits BooleanLiteral
   * @param	value	the boolean literal
   */
  void visitBooleanLiteral(BooleanLiteral self, boolean value)
    throws PositionedError;

  /**
   * Visits NullLiteral
   * @param	value	the null literal
   */
  void visitNullLiteral(NullLiteral self)
    throws PositionedError;

  /**
   * Visits StringLiteral
   * @param	value	the string literal
   */
  void visitStringLiteral(StringLiteral self, String value)
    throws PositionedError;

  /**
   * Visits ExistsPredicate
   * @param	sub the sub table
   */
  void visitExistsPredicate(ExistsPredicate self, SubTableExpression sub)
    throws PositionedError;

  /**
   * Visits ValueList
   * @param	value	the list of value
   */
  void visitValueList(ValueList self, ExpressionList expr)
    throws PositionedError;

  /**
   * Visits ArrayPrecision
   * @param	size	a SqlPhylum
   */
  void visitArrayPrecision(ArrayPrecision self, IntegerLiteral size)
    throws PositionedError;

  /**
   * Visits BetweenPredicate
   * @param	start		begining of the range
   * @param	startQualifier	mode (INCLUSIVE | EXCLUSIVE)
   * @param	end		end of the range
   * @param	endQualifier	mode (INCLUSIVE | EXCLUSIVE)
   */
  void visitBetweenPredicate(BetweenPredicate self,
			     Expression start,
			     String startQualifier,
			     Expression end,
			     String endQualifier)
    throws PositionedError;

  /**
   * Visits ValueInPredicate
   * @param	value		value  of the predicate
   */
  void visitValueInPredicate(ValueInPredicate self, ExpressionList value)
    throws PositionedError;

  /**
   * Visits LikePredicate
   * @param	sens
   * @param	expr
   * @param	escape
   */
  void visitLikePredicate(LikePredicate self,
			  String sens,
			  Expression expr,
			  JdbcEscape escape)
    throws PositionedError;

  /**
   * Visits MatchesPredicate
   * @param	sens
   * @param	matchesExpr
   * @param	escapeExpr
   */
  void visitMatchesPredicate(MatchesPredicate self,
			     String sens,
			     Expression matchesExpr,
			     Expression escapeExpr)
    throws PositionedError;

  /**
   * Visits MatchesPredicate
   */
  void visitTupleInPredicate(TupleInPredicate self,
			     ExpressionList exprs,
			     boolean hasNot,
			     SubTableExpression table)
    throws PositionedError;

  /**
   * Visits TupleAssignment
   */
  void visitTupleAssignment(TupleAssignment self,
                            FieldNameList fields,
                            SubTableExpression table)
    throws PositionedError;

  /**
   * Visits ExpressionPredicate
   * @param	expr			the predicate expression
   */
  void visitExpressionPredicate(ExpressionPredicate self, Expression expr)
    throws PositionedError;

  /**
   * Visits IsPredicate
   */
  void visitIsPredicate(IsPredicate self, Predicate predicate, boolean hasNot)
    throws PositionedError;

  /**
   * Visits QuantifiedPredicate
   */
  void visitQuantifiedPredicate(QuantifiedPredicate self,
				String type,
				SubTableExpression table)
    throws PositionedError;

  /**
   * Visits ComparisonPredicate
   */
  void visitComparisonPredicate(ComparisonPredicate self,
				Predicate left,
				Predicate right,
				String operator)
    throws PositionedError;

  /**
   * Visits ExtendedPredicate
   */
  void visitExtendedPredicate(ExtendedPredicate self,
			      Predicate left,
			      String operator,
			      Predicate right)
    throws PositionedError;

  /**
   * Visits JdbcFunction
   */
  void visitJdbcFunction(JdbcFunction self, String name, ExpressionList params)
    throws PositionedError;

  /**
   * Visits JdbcFunction
   */
  void visitJdbcEscape(JdbcEscape self, String name)
    throws PositionedError;

  /**
   * Visits JdbcDateLiteral
   * @param	value	the JDBC date
   */
  void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError;

  /**
   * Visits JdbcTimeLiteral
   * @param	value	the JDBC time
   */
  void visitJdbcTimeLiteral(JdbcTimeLiteral self, Time value)
    throws PositionedError;

  /**
   * Visits JdbcTimeStampLiteral
   * @param	value	the JDBC time stamp
   */
  void visitJdbcTimeStampLiteral(JdbcTimeStampLiteral self, Timestamp value)
    throws PositionedError;

  /**
   * Visits IntegerLiteral
   * @param	value	the integer literal
   */
  void visitIntegerLiteral(IntegerLiteral self, Integer value)
    throws PositionedError;

  /**
   * Visits NumericLiteral
   * @param	value	the numeric literal
   */
  void visitNumericLiteral(NumericLiteral self, Fixed value)
    throws PositionedError;

  /**
   * Visits an ident
   * @param	ident	ident.*  if it is not null else only * is print
   */
  void visitSelectElemStar(SelectElemStar self, String ident)
    throws PositionedError;

  /**
   * Visits an column name with is new name is it exists
   * @param	columnName	column name
   * @param	newColumnName	new column name
   */
  void visitSelectElemExpression(SelectElemExpression self,
				 Expression columnExpr,
				 String newColumnName)
    throws PositionedError;

  /**
   * Visits a max min avg sum expression
   * @param	oper	                   the operator
   * @param	type	                   the expression type
   * @param	expr	                   the Sql Expression
   */
  void visitSetFunction(SetFunction self, String oper, int type, Expression expr)
    throws PositionedError;

  /**
   * Visits a count expression
   */
  void visitCountExpression(CountExpression self,
			    boolean hasDistinct,
                            boolean countAll,
			    Expression expr)
    throws PositionedError;

  /**
   * Visits the SimpleWhenClause
   * @param	whenExpr	           the Sql Expression for the when
   * @param	thenExpr	           the Sql Expression for the else
   */
  void visitSimpleWhenClause(SimpleWhenClause self,
			     Expression whenExpr,
			     Expression thenExpr)
    throws PositionedError;

  /**
   * Visits the SearchedWhenClause
   * @param	condition	           the condition for the when
   * @param	thenExpr	           the Sql Expression for the else
   */
  void visitSearchedWhenClause(SearchedWhenClause self,
			       SearchCondition condition,
			       Expression thenExpr)
    throws PositionedError;

  /**
   * Visits the SimpleCaseExpression
   * @param	expr	                   the Sql Expression for the case
   * @param	conditions	           List of the conditions
   * @param	elseExpr	           the Sql Expression for the else
   */
  void visitSimpleCaseExpression(SimpleCaseExpression self,
				 Expression expr,
				 ArrayList conditions,
				 Expression elseExpr)
    throws PositionedError;

  /**
   * Visits the SearchedCaseExpression
   * @param	conditions	           List of the conditions
   * @param	elseExpr	           the Sql Expression for the else
   */
  void visitSearchedCaseExpression(SearchedCaseExpression self,
				   ArrayList conditions,
				   Expression elseExpr)
    throws PositionedError;

  /**
   * Visits ASSIGNMENT
   * @param	ident        	ident
   * @param	expr         	expression
   * @param	searchCondition	search condition
   */
  void visitAssignment(Assignment self, Expression left, Expression right)
    throws PositionedError;

  /**
   * Visits the CorrespondingSpec
   */
  void visitCorrespondingSpec(CorrespondingSpec self, FieldNameList fields)
    throws PositionedError;

  /**
   * Visits the IntersectSpec
   */
  void visitIntersectSpec(IntersectSpec self, CorrespondingSpec spec)
    throws PositionedError;

  /**
   * Visits the unidiff spec
   */
  void visitUnidiffSpec(UnidiffSpec self, String oper, CorrespondingSpec spec)
    throws PositionedError;

  /**
   * Visits the union
   */
  void visitUnidiffTableTerm(UnidiffTableTerm self,
			     UnidiffSpec spec,
			     SubTable left,
			     SubTable right)
    throws PositionedError;

  /**
   * Visits the union
   */
  void visitUnidiffTableReference(UnidiffTableReference self,
				  UnidiffSpec spec,
				  TableReference left,
				  TableReference right)
    throws PositionedError;

  /**
   * Visits ParenthesedTableReference
   */
  void visitParenthesedTableReference(ParenthesedTableReference self,
				      TableReference expr)
    throws PositionedError;

  /**
   * Visits SelectTableReference
   */
  void visitSelectTableReference(SelectTableReference self,
				 SelectExpression expr)
    throws PositionedError;

  /**
   * Visits DELETE
   * @param	table
   * @param	searchCondition	search condition
   */
  void visitDeleteStatement(DeleteStatement self,
			    TableName table,
			    SearchCondition searchCondition)
    throws PositionedError;

  /**
   * Visits SubTableExpression
   * @param	expr		the table expression
   * @param	expr		the alias
   */
  void visitSubTableExpression(SubTableExpression self, SubTable table)
    throws PositionedError;

  /**
   * Visits DefaultValuesInsertSource
   */
  void visitDefaultValuesInsertSource(DefaultValuesInsertSource self)
    throws PositionedError;

  /**
   * Visits ValueListInsertSource
   */
  void visitValueListInsertSource(ValueListInsertSource self, ValueList value)
    throws PositionedError;

  /**
   * Visits TableExpressionInsertSource
   */
  void visitTableExpressionInsertSource(TableExpressionInsertSource self,
					TableReference table)
    throws PositionedError;

  /**
   * Visits TableInsertSource
   */
  void visitTableInsertSource(TableInsertSource self, ArrayList table)
    throws PositionedError;

  /**
   * Visits TableAlias
   */
  void visitTableAlias(TableAlias self, String name, FieldNameList list)
    throws PositionedError;

  /**
   * Visits TableName
   * @param	alias		the alias for this reference
   * @param	expr		the table expression
   */
  void visitTableName(TableName self, Expression name, TableAlias alias)
    throws PositionedError;

  /**
   * Visits TinyIntType
   */
  void visitTinyIntType(TinyIntType self)
    throws PositionedError;

  /**
   * Visits SmallIntType
   */
  void visitSmallIntType(SmallIntType self)
    throws PositionedError;

  /**
   * Visits IntegerType
   */
  void visitIntegerType(IntegerType self)
    throws PositionedError;

  /**
   * Visits NumericType
   * @param	left		the number of digits
   * @param	right		the position of the comma
   */
  void visitNumericType(NumericType self, IntegerLiteral left, IntegerLiteral right)
    throws PositionedError;

  /**
   * Visits FloatType
   */
  void visitFloatType(FloatType self)
    throws PositionedError;

  /**
   * Visits DoubleType
   */
  void visitDoubleType(DoubleType self)
    throws PositionedError;

  /**
   * Visits RealType
   */
  void visitRealType(RealType self)
    throws PositionedError;

  /**
   * Visits CharType
   */
  void visitCharType(CharType self, ArrayPrecision prec)
    throws PositionedError;

  /**
   * Visits TypeName
   */
  void visitTypeName(TypeName self, String ident)
    throws PositionedError;

  /**
   * Visits StringType
   */
  void visitStringType(StringType self)
    throws PositionedError;

  /**
   * Visits BoolType
   */
  void visitBoolType(BoolType self)
    throws PositionedError;

  /**
   * Visits BlobType
   */
  void visitBlobType(BlobType self)
    throws PositionedError;

  /**
   * Visits ClobType
   */
  void visitClobType(ClobType self)
    throws PositionedError;

  /**
   * Visits SimpleSubTableReference
   * @param	alias		the alias for this reference
   * @param	expr		the table expression
   */
  void visitSimpleSubTableReference(SimpleSubTableReference self,
				    TableAlias alias,
				    TableReference table)
    throws PositionedError;

  /**
   * Visits SubTableExpression
   * @param	expr		the table expression
   */
  void visitSubTableReference(SubTableReference self, SearchCondition expr)
    throws PositionedError;

  /**
   * Visits SimpleTableReference
   * @param	alias		the alias for this reference
   * @param	expr		the table expression
   */
  void visitSimpleTableReference(SimpleTableReference self,
				 Expression name,
				 TableAlias alias)
    throws PositionedError;

  /**
   * Visits IntersectTableReference
   * @param	left		the left reference
   * @param	right		the right reference
   */
  void visitIntersectTableReference(IntersectTableReference self,
				    IntersectSpec spec,
				    TableReference left,
				    TableReference right)
    throws PositionedError;

  /**
   * Visits IntersectTableTerm
   * @param	left		the left reference
   * @param	right		the right reference
   */
  void visitIntersectTableTerm(IntersectTableTerm self,
			       IntersectSpec spec,
			       SubTable left,
			       SubTable right)
    throws PositionedError;

  /**
   * Visits JdbcOuterJoin
   * @param	leftTable	the left table reference
   * @param	type		the join type
   * @param	rightTable	the right table reference
   * @param	joinPred	the join pred.
   */

  void visitJdbcOuterJoin(JdbcOuterJoin self,
                          TableReference leftTable,
                          String type,
                          TableReference rightTable,
                          JoinPred joinPred)
    throws PositionedError;
  
  /**
   * Visits OuterJoin
   * @param	table	        reference to the root table in the outer join tree
   * @param	subOuterJoins   subOuterJoins expressions.
   */
  void visitOuterJoin(OuterJoin self,
		      TableReference table,
                      SubOuterJoin[] subOuterJoins)
    throws PositionedError;
  
  /**
   * Visits CastPrimary
   */
  void visitCastPrimary(CastPrimary self,
			Expression left,
			Type right)
    throws PositionedError;

  // ----------------------------------------------------------------------
  // CONTEXT HANDLING
  // ----------------------------------------------------------------------

  SqlContext getContext();

  void enter(SqlContext context);

  void exit(SqlContext context);
}
