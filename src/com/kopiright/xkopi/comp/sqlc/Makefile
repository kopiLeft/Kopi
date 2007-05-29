##
## Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License version 2
## as published by the Free Software Foundation.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id: Makefile,v 1.7 2004/02/24 10:15:28 mike Exp $
##

TOPDIR =	../../..

PACKAGE =	com/kopiright/xkopi/comp/sqlc

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Sqlc
GRM1DEP =
SCANNER1 =	Sqlc

# Compiler classes
JAVAFILES +=	DefaultSqlVisitor Main SqlcScanner			\
		SqlContext SqlcPrettyPrinter				\
		SqlVisitor DBChecker SCompilationUnitContext SqlChecker

# Syntax tree classes
JAVAFILES +=	AbstractCaseExpression AndCondition ArrayPrecision	\
		Assignment BetweenPredicate				\
		BinaryExpression BinarySearchCondition 			\
		BlobType BoolType BooleanLiteral CaseExpression 	\
		CastPrimary CharType ClobType				\
		ComparisonPredicate CorrespondingSpec CountExpression	\
		DecodeExpression DbpipeExpression			\
		DefaultValuesInsertSource				\
		DeleteStatement DoubleType ExistsPredicate		\
		Expression ExpressionList ExpressionPredicate		\
		ExtendedPredicate FieldNameList FieldReference		\
		FloatType FromClause					\
		GroupByClause HavingClause IfExpression			\
		InsertSource InsertStatement IntegerLiteral		\
		IntegerType IntersectSpec IntersectTableReference	\
		IntersectTableTerm IsPredicate JdbcDateLiteral		\
		JdbcEscape JdbcFunction JdbcTimeLiteral			\
		JdbcTimeStampLiteral JoinPred LikePredicate		\
		Literal MatchesPredicate MinusExpression		\
		NotCondition NullLiteral NumericLiteral			\
		NumericType OnJoinPred OrCondition OuterJoin		\
		ParenthesedTableReference PlusExpression Predicate	\
		QuantifiedPredicate RealType SCompilationUnit		\
		SearchCondition SearchedCaseExpression			\
		SearchedWhenClause SelectElem SelectElemExpression	\
		SelectElemStar SelectExpression SelectStatement		\
		SelectTableReference SetFunction			\
		SimpleCaseExpression SimpleIdentExpression		\
		SimpleSearchCondition SimpleSubTableReference		\
		SimpleTableReference SimpleWhenClause			\
		SlashExpression SmallIntType SortElem			\
		SortSpec SqlPhylum StarExpression Statement		\
		StringLiteral StringType SubTable			\
		SubTableExpression SubTableReference			\
		TableAlias TableExpressionInsertSource			\
		TableInsertSource TableName TableReference		\
		TinyIntType TupleAssignment TupleInPredicate Type	\
		TypeName UnaryExpression UnaryMinusExpression		\
		UnaryPlusExpression UnidiffSpec				\
		UnidiffTableReference UnidiffTableTerm			\
		UpdSpec UpdateStatement UsingJoinPred			\
		ValueInPredicate ValueList				\
		ValueListInsertSource WhereClause			\
		FieldNameListTableReference XUtils 			\
		SubOuterJoin JdbcOuterJoin	

# Creates dynamically a dbSchema
JAVAFILES +=	DbSchemaVisitor

MESSAGES =	SqlcMessages

OPTIONS =	SqlcOptions

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
