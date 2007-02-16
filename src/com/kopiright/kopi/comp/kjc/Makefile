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
## $Id$
##

TOPDIR =	../../..

PACKAGE =	com/kopiright/kopi/comp/kjc

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Kjc
GRM1DEP =
SCANNER1 =	Kjc

# Compiler classes
JAVAFILES +=	BytecodeOptimizer					\
		CArrayType CBadClass					\
		CBinaryClass CBinaryField CBinaryMethod CBlockContext	\
		CBlockError CBodyContext CBooleanType			\
		CByteType CCatchContext CCharType CClass		\
		CClassContext CClassNameType CReferenceType		\
		CCompilationUnit CCompilationUnitContext		\
		CConstructorContext CContext CClassOrInterfaceType	\
		CDoubleType CExpressionContext				\
		CExpressionError CField					\
		CFloatType CInitializerContext				\
		CIntType CInterfaceContext CLabeledContext		\
		CLineError CLongType CLoopContext CMember		\
		CMethod CMethodContext					\
		CMethodNotFoundError CModifier CNullType		\
		CNumericType CParseClassContext	CParseEnumContext	\
		CParseCompilationUnitContext CPrimitiveType CShortType	\
		CSimpleBodyContext CSourceClass CSourceField		\
		CSourceMethod CStdType					\
		CSwitchBodyContext CSwitchGroupContext			\
		CThrowableInfo CTryContext				\
		CTryFinallyContext CType CVariableInfo CVoidType	\
		CodeLabel CodeSequence Constants			\
		DefaultFilter JCheckedExpression KjcPrettyPrinter	\
		KjcScanner KjcVisitor Main				\
		KjcEnvironment TypeFactory KjcTypeFactory		\
		ClassReader KjcClassReader CTypeContext			\
		CBinaryTypeContext SignatureParser CTypeVariable	\
		KjcSignatureParser CBinaryType CTypeVariableAlias	\
		CErasedReferenceType GenerationContext

# Syntax tree classes
JAVAFILES +=	JAddExpression JArrayAccessExpression			\
		JArrayInitializer JArrayLengthExpression		\
		JAssignmentExpression JBinaryArithmeticExpression	\
		JBinaryExpression JBitwiseComplementExpression		\
		JBitwiseExpression JBlock				\
		JBooleanLiteral JBreakStatement JByteLiteral		\
		JCastExpression JCatchClause JCharLiteral		\
		JClassBlock JClassDeclaration JEnumDeclaration		\
		JClassExpression JClassFieldDeclarator			\
		JClassImport JCompilationUnit				\
		JCompoundAssignmentExpression JCompoundStatement	\
		JConditionalAndExpression JConditionalExpression	\
		JConditionalOrExpression JConstructorBlock		\
		JConstructorCall					\
		JConstructorDeclaration JContinueStatement		\
		JDivideExpression JDoStatement				\
		JDoubleLiteral JEmptyStatement                    	\
		JEqualityExpression	JEnhancedForStatement		\
		JExpression JExpressionListStatement			\
		JExpressionStatement					\
		JFieldAccessExpression JFieldDeclaration		\
		JFloatLiteral JForStatement JEnhancedForStatement	\
		JFormalParameter					\
		JGeneratedLocalVariable JIfStatement			\
		JInitializerDeclaration JInstanceofExpression		\
		JIntLiteral						\
		JInterfaceDeclaration JLabeledStatement JLiteral	\
		JLocalVariable JLocalVariableExpression			\
		JLogicalComplementExpression				\
		JLongLiteral JLoopStatement				\
		JMemberDeclaration JMethodCallExpression		\
		JEnumMethodValueOf JEnumMethodValues 			\
		JMethodDeclaration JMinusExpression JModuloExpression	\
		JMultExpression JNameExpression				\
		JNewArrayExpression					\
		JNullLiteral JOuterLocalVariableExpression		\
		JPackageImport JPackageName JParenthesedExpression	\
		JPhylum JPostfixExpression JPrefixExpression		\
		JQualifiedAnonymousCreation				\
		JQualifiedInstanceCreation				\
		JRelationalExpression					\
		JReturnStatement JShiftExpression			\
		JShortLiteral JStatement JOwnerExpression		\
		JStringLiteral JSuperExpression JSwitchGroup		\
		JEnumSwitchInnerDeclaration				\
		JSwitchLabel JSwitchStatement JSynchronizedStatement	\
		JThisExpression JThrowStatement JTryCatchStatement	\
		JTryFinallyStatement JTypeDeclaration			\
		JTypeDeclarationStatement JTypeNameExpression		\
		JUnaryExpression JUnaryMinusExpression			\
		JUnaryPlusExpression JUnaryPromote			\
		JUnqualifiedAnonymousCreation				\
		JUnqualifiedInstanceCreation				\
		JVariableDeclarationStatement JVariableDefinition	\
		JWhileStatement JAccessorMethod				\
		LanguageExtensions					\
		KopiInvariantDeclaration KopiInvariantStatement		\
		KopiConstraintStatement KopiPostconditionDeclaration	\
		KopiPreconditionDeclaration KopiPreconditionStatement	\
		KopiMethodCallExpression KopiMethodDeclaration		\
		KopiMethodPreconditionDeclaration			\
		KopiPostconditionStatement KopiReturnStatement		\
		KopiMethodPostconditionDeclaration			\
		KopiImplicitReturnBlock KopiAssertionClassDeclaration	\
		KopiConstructorDeclaration KopiConstructorBlock		\
		KopiReturnValueExpression KopiStoreClassDeclaration	\
		KopiOldValueExpression KopiStoreFieldAccessExpression	\
		KopiOldValueStoreCreation KopiAssertStatement		\
		KopiFailStatement


MESSAGES =	KjcMessages

OPTIONS =	KjcOptions

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
