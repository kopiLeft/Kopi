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

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.Time;
import com.kopiright.xkopi.lib.type.Timestamp;

/**
 * This class implements a Java pretty printer
 */
public class SqlChecker implements SqlVisitor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new visitor
   */
  public SqlChecker(SqlContext context) {
    this.current = new StringBuffer();
    this.contexts = new Stack();

    enter(context);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the current string
   */
  public String toString() {
    return current.toString();
  }

  // ----------------------------------------------------------------------
  // VISITORS
  // ----------------------------------------------------------------------

  /*
   * Visits AndCondition
   */
  public void visitAndCondition(AndCondition self,
                                SearchCondition left,
                                SearchCondition right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" AND ");
    right.accept(this);
  }

  /*
   * Visits ArrayPrecision
   */
  public void visitArrayPrecision(ArrayPrecision self, IntegerLiteral size)
    throws PositionedError
  {
    current.append("(");
    if (size != null) {
      size.accept(this);
    } else {
      current.append("*");
    }
    current.append(")");
  }

  /*
   * Visits Assignment
   */
  public void visitAssignment(Assignment self, Expression left, Expression right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" = ");
    right.accept(this);

    // check if left is a text and if it exists in the table
    if (left instanceof SimpleIdentExpression) {
      TableReference table = (TableReference)(getContext().getTables()).get(0);
      if (!table.hasColumn(((SimpleIdentExpression)left).getIdent())) {
	reportTrouble(new CWarning(self.getTokenReference(), SqlcMessages.COLUMN_NOT_RESOLVABLE, ((SimpleIdentExpression)left).getIdent()));
      }
    }
  }

  /*
   * Visits BetweenPredicate
   */
  public void visitBetweenPredicate(BetweenPredicate self,
				    Expression start,
				    String startQualifier,
				    Expression end,
				    String endQualifier)
    throws PositionedError
  {
    current.append(" BETWEEN ");
    start.accept(this);
    if (startQualifier != null) {
      current.append(" ");
      current.append(startQualifier);
    }
    current.append(" AND ");
    end.accept(this);
    if (endQualifier != null) {
      current.append(" ");
      current.append(endQualifier);
    }
  }

  /*
   * Visits BinaryExpression
   */
  public void visitBinaryExpression(BinaryExpression self,
                                    String operator,
                                    Expression left,
                                    Expression right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" ");
    current.append(operator);
    current.append(" ");
    right.accept(this);
  }

  /*
   * Visits BlobType
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    current.append("BLOB");
  }

  /*
   * Visits ClobType
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    current.append("CLOB");
  }

  /*
   * Visits BoolType
   */
  public void visitBoolType(BoolType self)
    throws PositionedError
  {
    current.append("BOOL");
  }

  /*
   * Visits BooleanLiteral
   */
  public void visitBooleanLiteral(BooleanLiteral self, boolean value)
    throws PositionedError
  {
    current.append(value ? "{fn TRUE}" : "{fn FALSE}");
  }

  /*
   * Visits CaseExpression
   */
  public void visitCaseExpression(CaseExpression self,
                                  AbstractCaseExpression cases)
    throws PositionedError
  {
    current.append("CASE ");
    cases.accept(this);
    current.append(" END");
  }

  /*
   * Visits CastPrimary
   */
  public void visitCastPrimary(CastPrimary self, Expression left, Type right)
    throws PositionedError
  {
    current.append(" CAST (");
    left.accept(this);
    current.append(" AS ");
    right.accept(this);
    current.append(")");
  }

  /*
   * Visits CharType
   */
  public void visitCharType(CharType self, ArrayPrecision prec)
    throws PositionedError
  {
    current.append("CHAR");
    if (prec != null) {
      prec.accept(this);
    }
  }

  /*
   * Visits ComparisonPredicate
   */
  public void visitComparisonPredicate(ComparisonPredicate self,
                                       Predicate left,
                                       Predicate right,
                                       String operator)
    throws PositionedError
  {
    left.accept(this);
    current.append(" ");
    current.append(operator);
    current.append(" ");
    right.accept(this);
  }

  /*
   * Visits CorrespondingSpec
   */
  public void visitCorrespondingSpec(CorrespondingSpec self, FieldNameList fields)
    throws PositionedError
  {
    current.append("CORRESPONDING");
    if (fields != null) {
      current.append(" BY (");
      fields.accept(this);
      current.append(")");
    }
  }

  /*
   * Visits CountExpression
   */
  public void visitCountExpression(CountExpression self,
                                   boolean hasDistinct,
                                   Expression expr)
    throws PositionedError
  {
    if (hasDistinct) {
      current.append("COUNT(DISTINCT ");
      expr.accept(this);
      current.append(")");
    } else {
      current.append("COUNT(*)");
    }
  }

  /*
   * Visits DefaultValuesInsertSource
   */
  public void visitDefaultValuesInsertSource(DefaultValuesInsertSource self)
    throws PositionedError
  {
    current.append("DEFAULT VALUES");
  }

  /*
   * Visits DeleteStatement
   */
  public void visitDeleteStatement(DeleteStatement self,
                                   TableName table,
                                   SearchCondition searchCondition)
    throws PositionedError
  {
    current.append("DELETE FROM ");
    table.accept(this);
    if (searchCondition != null) {
      current.append(" WHERE ");
      searchCondition.accept(this);
    }
  }

  /*
   * Visits DoubleType
   */
  public void visitDoubleType(DoubleType self)
    throws PositionedError
  {
    current.append("DOUBLE");
  }

  /*
   * Visits ExistsPredicate
   */
  public void visitExistsPredicate(ExistsPredicate self, SubTableExpression sub)
    throws PositionedError
  {
    current.append("EXISTS (");
    sub.accept(this);
    current.append(")");
  }

  /*
   * Visits ExpressionList
   */
  public void visitExpressionList(ExpressionList self, List exprs)
    throws PositionedError
  {
    for (int i = 0; i < exprs.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((Expression)exprs.get(i)).accept(this);
    }
  }

  /*
   * Visits ExpressionPredicate
   */
  public void visitExpressionPredicate(ExpressionPredicate self,
                                       Expression expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /*
   * Visits ExtendedPredicate
   */
  public void visitExtendedPredicate(ExtendedPredicate self,
                                     Predicate left,
                                     String operator,
                                     Predicate right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" ");
    if (operator != null) {
      current.append(operator);
      current.append(" ");
    }
    right.accept(this);
  }

  /*
   * Visits FieldNameList
   */
  public void visitFieldNameList(FieldNameList self, List exprs)
    throws PositionedError
  {
    for (int i = 0; i < exprs.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      current.append(exprs.get(i));
    }
  }

  /*
   * Visits FieldReference
   */
  public void visitFieldReference(FieldReference self,
                                  String table,
                                  String ident,
				  boolean hasPlus)
    throws PositionedError
  {
    if (table != null) {
      current.append(table);
      current.append(".");
    }
    current.append(ident);
    if (hasPlus) {
      current.append("(+)");
    }
    FieldReference.checkColumnExists(self.getTokenReference(),
                                     getContext(),
                                     table,
                                     ident);
  }

  /*
   * Visits FloatType
   */
  public void visitFloatType(FloatType self)
    throws PositionedError
  {
    current.append("FLOAT");
  }

  /*
   * Visits FromClause
   */
  public void visitFromClause(FromClause self, ArrayList tableRefs)
    throws PositionedError
  {
    current.append("FROM ");
    for (int i = 0; i < tableRefs.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((SimpleTableReference)tableRefs.get(i)).accept(this);
    }
  }

  /*
   * Visits GroupByClause
   */
  public void visitGroupByClause(GroupByClause self, ArrayList fields)
    throws PositionedError
  {
    current.append("GROUP BY ");
    for (int i = 0; i < fields.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((FieldReference)fields.get(i)).accept(this);
    }
    current.append(" ");
  }

  /*
   * Visits HavingClause
   */
  public void visitHavingClause(HavingClause self, SearchCondition condition)
    throws PositionedError
  {
    current.append("HAVING ");
    condition.accept(this);
    current.append(" ");
  }

  /*
   * Visits IfExpression
   */
  public void visitIfExpression(IfExpression self,
                                SearchCondition condition,
                                Expression thenExpr,
                                Expression elseExpr)
    throws PositionedError
  {
    current.append("IF ");
    condition.accept(this);

    current.append(" ");
    current.append("THEN ");
    thenExpr.accept(this);

    current.append(" ");
    current.append("ELSE ");
    elseExpr.accept(this);

    current.append(" ");
    current.append("FI");
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
    current.append("DECODE(");
    checkExpr.accept(this);
    if (searchResult != null) {
      for (int i = 0; i < searchResult.length; i++) {
	current.append(", ");
        searchResult[i][0].accept(this);
	current.append(", ");
        searchResult[i][1].accept(this);
      }
    }
    current.append(", ");
    defaultExpr.accept(this);
    current.append(")");
  }

  /*
   * Visits InsertStatement
   */
  public void visitInsertStatement(InsertStatement self,
                                   String ident,
                                   FieldNameList fields,
                                   InsertSource source)
    throws PositionedError
  {
    current.append("INSERT INTO ");
    current.append(ident);
    if (fields != null) {
      current.append("(");
      fields.accept(this);
      current.append(")");
    }
    current.append(" ");
    source.accept(this);

    if (!XUtils.tableExists(ident)) {
      reportTrouble(new CWarning(self.getTokenReference(),
                                 SqlcMessages.TABLE_NOT_FOUND, ident));
    } else {
      // !!! TESTER POUR LES AUTRES CAS
      //if (source instanceof TableInsertSource) {
      if ((source instanceof ValueListInsertSource) && (fields != null)) {
	if (((ValueListInsertSource)source).elemNumber() != fields.elemNumber()) {
	  reportTrouble(new CWarning(self.getTokenReference(),
                                     SqlcMessages.NUMBER_COLUMNS_VALUES_NOT_EQUAL));
	}
      }
      if (fields != null) {
	// check if the columns are unique
	if (fields.checkIdentsAreUnique(getContext())) {
	  // !!! ne pas oublier de verifier que le nombre d`elements dans le
	  // FieldNameList est egal au nombre de colonnes de la table
	} else {
	  // System.err.println("The columns aren't unique");
	}
      } else {
	// verifier que (ValueListInsertSource)source).elemNumber() == nombre
	// de colonnes dans la table ident
      }
    }
  }

  /*
   * Visits IntegerLiteral
   */
  public void visitIntegerLiteral(IntegerLiteral self, Integer value)
    throws PositionedError
  {
    current.append(value);
  }

  /*
   * Visits IntegerType
   */
  public void visitIntegerType(IntegerType self)
    throws PositionedError
  {
    current.append("INTEGER");
  }

  /*
   * Visits IntersectSpec
   */
  public void visitIntersectSpec(IntersectSpec self, CorrespondingSpec spec)
    throws PositionedError
  {
    current.append("INTERSECT");
    if (spec != null) {
      current.append(" ");
      spec.accept(this);
    }
  }

  /*
   * Visits IntersectTableReference
   */
  public void visitIntersectTableReference(IntersectTableReference self,
					   IntersectSpec spec,
					   TableReference left,
					   TableReference right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" ");
    spec.accept(this);
    current.append(" ");
    right.accept(this);
  }

  /*
   * Visits IntersectTableTerm
   */
  public void visitIntersectTableTerm(IntersectTableTerm self,
				      IntersectSpec spec,
				      SubTable left,
				      SubTable right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" ");
    spec.accept(this);
    current.append(" ");
    right.accept(this);
  }

  /*
   * Visits IsPredicate
   */
  public void visitIsPredicate(IsPredicate self,
                               Predicate predicate,
                               boolean hasNot)
    throws PositionedError
  {
    predicate.accept(this);
    current.append(" ");
    if (hasNot) {
      current.append("IS NOT NULL");
    } else {
      current.append("IS NULL");
    }
  }

  /*
   * Visits JdbcDateLiteral
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
    current.append(value.toSql());
  }

  /*
   * Visits JdbcEscape
   */
  public void visitJdbcEscape(JdbcEscape self, String name)
    throws PositionedError
  {
    current.append("{escape ");
    current.append(name);
    current.append("}");
  }

  /*
   * Visits JdbcFunction
   */
  public void visitJdbcFunction(JdbcFunction self,
                                String name,
                                ExpressionList params)
    throws PositionedError
  {
    current.append("{fn ");
    current.append(name);
    current.append("(");
    if (params != null) {
      params.accept(this);
    }
    current.append(")}");
  }

  /*
   * Visits JdbcTimeLiteral
   */
  public void visitJdbcTimeLiteral(JdbcTimeLiteral self, Time value)
    throws PositionedError
  {
    current.append(value.toSql());
  }

  /*
   * Visits JdbcTimeStampLiteral
   */
  public void visitJdbcTimeStampLiteral(JdbcTimeStampLiteral self,
                                        Timestamp value)
    throws PositionedError
  {
    current.append(value.toSql());
  }

  /*
   * Visits LikePredicate
   */
  public void visitLikePredicate(LikePredicate self,
                                 String sens,
                                 Expression expr,
                                 JdbcEscape escape)
    throws PositionedError
  {
    current.append("LIKE");
    if (sens != null) {
      current.append(" ");
      current.append(sens);
    }
    current.append(" ");
    expr.accept(this);
    if (escape != null) {
      current.append(" ");
      escape.accept(this);
    }
  }

  /*
   * Visits MatchesPredicate
   */
  public void visitMatchesPredicate(MatchesPredicate self,
				    String sens,
				    Expression matchesExpr,
				    Expression escapeExpr)
    throws PositionedError
  {
    current.append(" MATCHES ");
    current.append(sens);
    current.append(" ");
    matchesExpr.accept(this);
    if (escapeExpr != null) {
      current.append(" ESCAPE ");
      escapeExpr.accept(this);
    }
  }

  /*
   * Visits NotCondition
   */
  public void visitNotCondition(NotCondition self, SearchCondition condition)
    throws PositionedError
  {
    current.append("NOT ");
    condition.accept(this);
  }

  /*
   * Visits NullLiteral
   */
  public void visitNullLiteral(NullLiteral self)
    throws PositionedError
  {
    current.append("NULL");
  }

  /*
   * Visits NumericLiteral
   */
  public void visitNumericLiteral(NumericLiteral self, Fixed value)
    throws PositionedError
  {
    current.append(value.toSql());
  }

  /*
   * Visits NumericType
   */
  public void visitNumericType(NumericType self,
                               IntegerLiteral left,
                               IntegerLiteral right)
    throws PositionedError
  {
    current.append("NUMERIC");
    if (left != null) {
      current.append("(");
      left.accept(this);
      if (right != null) {
	current.append(", ");
	right.accept(this);
      }
      current.append(")");
    }
  }

  /*
   * Visits OnJoinPred
   */
  public void visitOnJoinPred(OnJoinPred self, SearchCondition condition)
    throws PositionedError
  {
    current.append("ON ");
    condition.accept(this);
  }

  /*
   * Visits OrCondition
   */
  public void visitOrCondition(OrCondition self,
                               SearchCondition left,
                               SearchCondition right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" OR ");
    right.accept(this);
  }



  /*
   * Visits JdbcOuterJoin
   */
  public void visitJdbcOuterJoin(JdbcOuterJoin self,
                                 TableReference leftTable,
                                 String type,
                                 TableReference rightTable,
                                 JoinPred joinPred)
    throws PositionedError
  {
    current.append("{oj ");
    leftTable.accept(this);
    current.append(" ");
    current.append(type);
    current.append(" OUTER JOIN ");
    rightTable.accept(this);
    current.append(" ");
    if (joinPred != null) {
      joinPred.accept(this);
    }
    current.append("}");
  }
  /*
   * Visits OuterJoin
   */
  public void visitOuterJoin(OuterJoin self,
			     TableReference table,
                             SubOuterJoin[] subOuterJoins) 
    throws PositionedError
  {
    table.accept(this);
    for (int i = 0; i < subOuterJoins.length; i++) {
      current.append(" ");
      current.append(subOuterJoins[i].getType());
      current.append(" OUTER JOIN ");
      subOuterJoins[i].getTable().accept(this);
      current.append(" ");
      if (subOuterJoins[i].getJoinPred() != null) {
        subOuterJoins[i].getJoinPred().accept(this);
      }
    }
  }

  /*
   * Visits ParenthesedTableReference
   */
  public void visitParenthesedTableReference(ParenthesedTableReference self,
                                             TableReference expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /*
   * Visits QuantifiedPredicate
   */
  public void visitQuantifiedPredicate(QuantifiedPredicate self,
                                       String type,
                                       SubTableExpression table)
    throws PositionedError
  {
    current.append(type);
    table.accept(this);
  }

  /*
   * Visits RealType
   */
  public void visitRealType(RealType self)
    throws PositionedError
  {
    current.append("REAL");
  }

  /*
   * Visits SearchedCaseExpression
   */
  public void visitSearchedCaseExpression(SearchedCaseExpression self,
                                          ArrayList conditions,
                                          Expression elseExpr)
    throws PositionedError
  {
    for (int i = 0; i < conditions.size(); i++) {
      ((SearchedWhenClause)conditions.get(i)).accept(this);
      current.append(" ");
    }
    if (elseExpr != null) {
      current.append("ELSE ");
      elseExpr.accept(this);
      //      println();
    }
  }

  /*
   * Visits SearchedWhenClause
   */
  public void visitSearchedWhenClause(SearchedWhenClause self,
                                      SearchCondition condition,
                                      Expression thenExpr)
    throws PositionedError
  {
    current.append("WHEN ");
    condition.accept(this);
    current.append(" THEN ");
    thenExpr.accept(this);
  }

  /*
   * Visits SelectElemExpression
   */
  public void visitSelectElemExpression(SelectElemExpression self,
                                        Expression columnExpr,
                                        String newColumnName)
    throws PositionedError
  {
    columnExpr.accept(this);
    if (newColumnName != null) {
      if (columnExpr instanceof FieldReference
          && ((FieldReference) columnExpr).getQualifiedFieldName().equals(newColumnName)) {
        reportTrouble(new CWarning(self.getTokenReference(),
                                   SqlcMessages.NOT_USEFUL_ALIAS,
                                   newColumnName + " AS " + newColumnName));
      }
      if (newColumnName.indexOf('.') != -1) {
        reportTrouble(new CWarning(self.getTokenReference(),
                                   SqlcMessages.BAD_ALIAS_FORMAT,
                                   newColumnName));
      }
      current.append(" AS ");
      current.append(newColumnName);
    }
  }

  /*
   * Visits SelectElemStar
   */
  public void visitSelectElemStar(SelectElemStar self, String ident)
    throws PositionedError
  {
    if (ident != null) {
      current.append(ident);
      current.append(".*");
    } else  {
      current.append("*");
    }
  }

  /*
   * Visits SelectExpression
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
    current.append("SELECT ");
    if (type != null) {
      current.append(type);
      current.append(" ");
    }
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((SelectElem)columns.get(i)).accept(this);
    }

    current.append(" ");
    fromClause.accept(this);

    if (whereClause != null) {
      current.append(" ");
      whereClause.accept(this);
    }
    if (groupByClause != null) {
      current.append(" ");
      groupByClause.accept(this);
    }
    if (havingClause !=null) {
      current.append(" ");
      havingClause.accept(this);
    }
  }

  /*
   * Visits SelectStatement
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
    current.append("SELECT ");
    if (type != null) {
      current.append(type);
      current.append(" ");
    }
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((SelectElem)columns.get(i)).accept(this);
    }

    current.append(" ");
    fromClause.accept(this);

    if (whereClause != null) {
      current.append(" ");
      whereClause.accept(this);
    }
    if (groupByClause != null) {
      current.append(" ");
      groupByClause.accept(this);
    }
    if (havingClause !=null) {
      current.append(" ");
      havingClause.accept(this);
    }
    if (sortSpec != null) {
      current.append(" ");
      sortSpec.accept(this);
    }
    if (updateSpec != null) {
      current.append(" ");
      updateSpec.accept(this);
    }

    fromClause.checkAliasAreUnique(getContext());

    if (sortSpec != null) {
      // check if the idents are unique
      // !!!if (sortSpec.checkIdentsAreUnique(this)) {
      if (columns.size() < sortSpec.getNumberOfIdents()) {
	reportTrouble(new CWarning(self.getTokenReference(),
                                   SqlcMessages.NUMBER_COLUMNS_NOT_CORRECT));
      }
    }
  }

  /*
   * Visits SelectTableReference
   */
  public void visitSelectTableReference(SelectTableReference self,
                                        SelectExpression expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /*
   * Visits SetFunction
   */
  public void visitSetFunction(SetFunction self,
                               String oper,
                               int type,
                               Expression expr)
    throws PositionedError
  {
    current.append(oper);
    current.append("(");
    switch(type) {
    case SetFunction.TYP_NONE:
      break;
    case SetFunction.TYP_ALL:
      current.append("ALL ");
      break;
    case SetFunction.TYP_DISTINCT:
      current.append("DISTINCT ");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
    expr.accept(this);
    current.append(")");
  }

  /*
   * Visits SimpleCaseExpression
   */
  public void visitSimpleCaseExpression(SimpleCaseExpression self,
                                        Expression expr,
                                        ArrayList conditions,
                                        Expression elseExpr)
    throws PositionedError
  {
    expr.accept(this);
    current.append(" ");
    for (int i = 0; i < conditions.size(); i++) {
      ((SimpleWhenClause)conditions.get(i)).accept(this);
      current.append(" ");
    }
    if (elseExpr != null) {
      current.append("ELSE ");
      elseExpr.accept(this);
      current.append(" ");
    }
  }

  /*
   * Visits SimpleIdentExpression
   */
  public void visitSimpleIdentExpression(SimpleIdentExpression self,
                                         String ident)
    throws PositionedError
  {
    current.append(ident);
  }

  /*
   * Visits SimpleSearchCondition
   */
  public void visitSimpleSearchCondition(SimpleSearchCondition self,
                                         Predicate predicate)
    throws PositionedError
  {
    predicate.accept(this);
  }

  /*
   * Visits SimpleSubTableReference
   */
  public void visitSimpleSubTableReference(SimpleSubTableReference self,
                                           TableAlias alias,
                                           TableReference table)
    throws PositionedError
  {
    current.append("(");
    table.accept(this);
    current.append(") ");
    alias.accept(this);
  }

  /**
   * Visits SimpleTableReference
   * @param	name		the table name
   * @param	alias		the alias for this reference
   */
  public void visitSimpleTableReference(SimpleTableReference self,
                                        Expression name,
                                        TableAlias alias)
    throws PositionedError
  {
    name.accept(this);
    if (alias != null) {
      current.append(" ");
      alias.accept(this);
    }
  }

  /*
   * Visits SimpleWhenClause
   */
  public void visitSimpleWhenClause(SimpleWhenClause self,
                                    Expression whenExpr,
                                    Expression thenExpr)
    throws PositionedError
  {
    current.append("WHEN ");
    whenExpr.accept(this);
    current.append(" THEN ");
    thenExpr.accept(this);
  }

  /*
   * Visits SmallIntType
   */
  public void visitSmallIntType(SmallIntType self)
    throws PositionedError
  {
    current.append("SMALLINT");
  }

  /*
   * Visits SortElem
   */
  public void visitSortElem(SortElem self, Expression elem, boolean isDesc)
    throws PositionedError
  {
    elem.accept(this);
    if (isDesc) {
      current.append(" DESC");
    }
  }

  /*
   * Visits SortSpec
   */
  public void visitSortSpec(SortSpec self, ArrayList elems)
    throws PositionedError
  {
    current.append("ORDER BY ");
    for (int i = 0; i < elems.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((SortElem)elems.get(i)).accept(this);
    }

    //    SelectStatement newGetContext() = (SelectStatement)getContext();
    //    for (int i = 0; i < elems.size(); i++) {
    //      Expression expr = ((SortElem)elems.get(i)).getExpression();
    //      if (expr == null) {
    //	// check if the IntegerLiteral is not too big
    //	Integer literal = (Integer)(((SortElem)elems.get(i)).getIntegerLiteral()).getValue();
    //	if (literal != null) {
    //	  if (literal.intValue() > newGetContext().getNumberOfColumns()) {
    //	   System.err.println("The number of column " + literal.intValue() + " is too big");
    //	  }
    //	}
    //      }
    //    }
  }

  /*
   * Visits StringLiteral
   */
  public void visitStringLiteral(StringLiteral self, String value)
    throws PositionedError
  {
    current.append("'");
    current.append(value);	//!!!FIXME graf 010212
    current.append("'");
  }

  /*
   * Visits StringType
   */
  public void visitStringType(StringType self)
    throws PositionedError
  {
    current.append("STRING");
  }

  /*
   * Visits SubTableExpression
   */
  public void visitSubTableExpression(SubTableExpression self, SubTable table)
    throws PositionedError
  {
    current.append("(");
    table.accept(this);
    current.append(")");
  }

  /*
   * Visits SubTableReference
   */
  public void visitSubTableReference(SubTableReference self, SearchCondition expr)
    throws PositionedError
  {
    expr.accept(this);
  }

  /*
   * Visits TableAlias
   */
  public void visitTableAlias(TableAlias self, String name, FieldNameList list)
    throws PositionedError
  {
    if (name != null) {
      current.append(" ");
      current.append(name);
    }
    if (list != null) {
      current.append("(");
      list.accept(this);
      current.append(")");
    }
  }

  /*
   * Visits TableExpressionInsertSource
   */
  public void visitTableExpressionInsertSource(TableExpressionInsertSource self,
                                               TableReference table)
    throws PositionedError
  {
    table.accept(this);
  }

  /*
   * Visits TableInsertSource
   */
  public void visitTableInsertSource(TableInsertSource self, ArrayList table)
    throws PositionedError
  {
    current.append("TABLE (");
    for (int i = 0; i < table.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((ValueList)table.get(i)).accept(this);
    }
    current.append(")");
  }

  /*
   * Visits TableName
   */
  public void visitTableName(TableName self, Expression name, TableAlias alias)
    throws PositionedError
  {
    if (name instanceof SimpleIdentExpression) {
      String    table = ((SimpleIdentExpression)name).getIdent();

      if (!XUtils.tableExists(table)) {
        reportTrouble(new CWarning(self.getTokenReference(),
                                   SqlcMessages.TABLE_NOT_FOUND,
                                   table));
      }
    }

    name.accept(this);
    if (alias != null) {
      current.append(" ");
      alias.accept(this);
    }
  }

  /*
   * Visits TinyIntType
   */
  public void visitTinyIntType(TinyIntType self)
    throws PositionedError
  {
    current.append("TINYINT");
  }

  /*
   * Visits TupleInPredicate
   */
  public void visitTupleInPredicate(TupleInPredicate self,
                                    ExpressionList exprs,
                                    boolean hasNot,
                                    SubTableExpression table)
    throws PositionedError
  {
    // laurent 20020304 Removed the brackets because it is specific
    // only to Transbase. Furthermore Transbase seems to accept the
    // syntax without brackets
    //    current.append("[");
    // lackner 20041230 oracle needs "(" and ")"
    // transbase and maxdb work with "(" and ")" too
    current.append("(");
    exprs.accept(this);
    current.append(")");
    //    current.append("]");
    if (hasNot) {
      current.append(" NOT");
    }
    current.append(" IN ");
    table.accept(this);
  }

  /*
   * Visits Assignment
   */
  public void visitTupleAssignment(TupleAssignment self,
                                   FieldNameList fields,
                                   SubTableExpression subTable)
    throws PositionedError
  {
    current.append("(");
    fields.accept(this);
    current.append(")");
    current.append(" = ");
    subTable.accept(this);

    // !!! To check if the number of column of the subquery is the number of field specified.

    // check if the columns are unique
    if (!fields.checkIdentsAreUnique(getContext())) {
	reportTrouble(new CWarning(fields.getTokenReference(), SqlcMessages.COLUMN_NOT_UNIQUE));
    } else {
      TableReference    table = (TableReference)(getContext().getTables()).get(0);
      Iterator          it = fields.getFields().iterator();

      // check if the columns exist in the table.
      while (it.hasNext()) {
        String          field = (String) it.next();

        if (!table.hasColumn(field)) {
	reportTrouble(new CWarning(fields.getTokenReference(), SqlcMessages.COLUMN_NOT_RESOLVABLE, field));
        }
      }
    }
  }

  /*
   * Visits TypeName
   */
  public void visitTypeName(TypeName self, String ident)
    throws PositionedError
  {
    current.append(ident);
  }

  /*
   * Visits UnaryExpression
   */
  public void visitUnaryExpression(UnaryExpression self,
                                   String operator,
                                   Expression operand)
    throws PositionedError
  {
    current.append(operator);
    current.append(" ");
    operand.accept(this);
  }

  /*
   * Visits UnidiffSpec
   */
  public void visitUnidiffSpec(UnidiffSpec self, String oper, CorrespondingSpec spec)
    throws PositionedError
  {
    current.append(oper + " ");
    if (spec != null) {
      current.append(" ");
      spec.accept(this);
    }
  }

  /*
   * Visits UnidiffTableReference
   */
  public void visitUnidiffTableReference(UnidiffTableReference self,
                                         UnidiffSpec spec,
                                         TableReference left,
                                         TableReference right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" ");
    spec.accept(this);
    current.append(" ");
    right.accept(this);
  }

  /*
   * Visits UnidiffTableTerm
   */
  public void visitUnidiffTableTerm(UnidiffTableTerm self,
                                    UnidiffSpec spec,
                                    SubTable left,
                                    SubTable right)
    throws PositionedError
  {
    left.accept(this);
    current.append(" ");
    spec.accept(this);
    current.append(" ");
    right.accept(this);
  }

  /*
   * Visits UpdSpec
   */
  public void visitUpdSpec(UpdSpec self)
    throws PositionedError
  {
    current.append("FOR UPDATE");
  }

  /*
   * Visits UpdateStatement
   */
  public void visitUpdateStatement(UpdateStatement self,
				   TableName table,
				   ArrayList listIdent,
				   SearchCondition searchCondition,
				   boolean hasCondition)
    throws PositionedError
  {
    current.append("UPDATE ");
    table.accept(this);
    current.append(" ");
    current.append("SET ");

    for (int i = 0; i < listIdent.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((Assignment)listIdent.get(i)).accept(this);
    }

    if (hasCondition) {
      current.append(" WHERE ");
      if (searchCondition != null) {
	searchCondition.accept(this);
      } else {
	current.append("CURRENT");
      }
    }
  }

  /*
   * Visits UsingJoinPred
   */
  public void visitUsingJoinPred(UsingJoinPred self, FieldNameList list)
    throws PositionedError
  {
    current.append("USING (");
    list.accept(this);
    current.append(")");
  }

  /*
   * Visits ValueInPredicate
   */
  public void visitValueInPredicate(ValueInPredicate self, ExpressionList value)
    throws PositionedError
  {
    current.append("IN (");
    value.accept(this);
    current.append(")");
  }

  /*
   * Visits ValueList
   */
  public void visitValueList(ValueList self, ExpressionList expr)
    throws PositionedError
  {
    current.append("(");
    expr.accept(this);
    current.append(")");
  }

  /*
   * Visits ValueListInsertSource
   */
  public void visitValueListInsertSource(ValueListInsertSource self, ValueList value)
    throws PositionedError
  {
    current.append("VALUES ");
    value.accept(this);
  }

  /*
   * Visits WhereClause
   */
  public void visitWhereClause(WhereClause self, SearchCondition condition)
    throws PositionedError
  {
    current.append("WHERE ");
    condition.accept(this);
  }

  // ----------------------------------------------------------------------
  // CONTEXT HANDLING
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
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Add an error into the list and eat it
   * This method should be called after a try catch block after catching exception
   * or directly without exception thrown
   * @param	error		the error
   */
  private void reportTrouble(PositionedError trouble) {
    getContext().reportTrouble(trouble);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected StringBuffer		current;
  protected Stack			contexts;
}
