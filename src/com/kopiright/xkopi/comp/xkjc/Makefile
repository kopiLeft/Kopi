##
## Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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
## $Id$
##

TOPDIR =	../../..

PACKAGE =	com/kopiright/xkopi/comp/xkjc

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	XKjc
GRM1DEP =	$(TOPDIR)/kopi/comp/kjc/Kjc
SCANNER1 =	XKjc

GRM2VOC =	XSqlc
GRM2DEP =	$(TOPDIR)/xkopi/comp/sqlc/Sqlc
SCANNER2 =	XSqlc

# Compiler classes
JAVAFILES +=	JavaSqlContext XKjcScanner XSqlcScanner			\
		Main XConstants XUtils XKjcPrettyPrinter		\
		XProtectedContext XUnprotectedContext			\
		XPrimitiveClassType XDateType XTimeType XMonthType	\
		XFixedType XWeekType XTimestampType			\
		XSqlChecker XSqlVisitor XSqlcPrettyPrinter		\
		XKjcSignatureParser XKjcTypeFactory XTypeFactory

# Syntax tree classes
JAVAFILES +=	SqlExprJava						\
		XAddExpression						\
		XAssignmentExpression XBitwiseComplementExpression	\
		XBitwiseExpression XCastExpression			\
		XCompoundAssignmentExpression XCursor			\
		XContextCursorDeclaration				\
		XCursorDeclaration XCursorField				\
		XCursorFieldDeclaration XCursorFieldExpression		\
		XDivideExpression XEqualityExpression			\
		XExecSqlExpression XExecSqlStatement			\
		XExpression XFixedLiteral				\
		XFormalParameter XIfCondition				\
		XLocalCursorDeclaration					\
		XLogicalComplementExpression XMethodCallExpression	\
		XMinusExpression XModuloExpression			\
		XMultExpression XMutableType				\
		XNameExpression XOperatorDeclaration			\
		XOverloadedMethodCallExpression				\
		XPostfixExpression XPrefixExpression			\
		XProtectedStatement XRelationalExpression		\
		XReturnStatement XSelectStatement			\
		XShiftExpression XSqlExpr XStatement XStdType		\
		XTypedSelectElem XTypedSqlExpr				\
		XUnaryMinusExpression XUnaryPlusExpression		\
		XUnprotectedStatement XVariableDefinition               \
                XDatabaseColumn   XDatabaseMember XDatabaseTable	\
		XKjcEnvironment XProtectedExceptionHandler XTypeFactory \
		XKjcTypeFactory XNewArrayExpression XGotoStatement	\
		XParseSqlExpression XExecInsertExpression

# Generated class
MESSAGES =	XKjcMessages XSqlcMessages

OPTIONS =	XKjcOptions

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## FILE DEPENDECIES

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
