/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

// Import the necessary classes
header { package at.dms.vkopi.comp.trig; }
{
  import java.util.Vector;
  import java.util.ArrayList;
  import at.dms.kopi.comp.kjc.*;
  import at.dms.xkopi.comp.xkjc.*;
  import at.dms.compiler.base.Compiler;
  import at.dms.compiler.base.CWarning;
  import at.dms.compiler.tools.antlr.extra.InputBuffer;
  import at.dms.compiler.base.JavaStyleComment;
  import at.dms.compiler.base.JavadocComment;
  import at.dms.compiler.base.PositionedError;
  import at.dms.compiler.base.TokenReference;

  import at.dms.util.base.InconsistencyException;
  import at.dms.xkopi.lib.base.DBDeadLockException;
  import at.dms.xkopi.lib.base.DBInterruptionException;
  import at.dms.vkopi.lib.visual.VExecFailedException;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class GKjcParser extends XKjcParser;
options {
  k = 2;				// two token lookahead
  importVocab = GKjc;
  exportVocab = GKjc;			// Call its vocabulary "GKjc"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass = "at.dms.compiler.tools.antlr.extra.Parser";
  access = "private";			// Set default rule access
}
{
  public GKjcParser(Compiler compiler, InputBuffer buffer, KjcEnvironment environment) {
    super(compiler, new GKjcScanner(compiler, buffer), MAX_LOOKAHEAD);
    this.isKopiFile = !buffer.getFile().endsWith(".java");
    this.environment = environment;
  }

  private GSqlcParser buildXSqlcParser() {
    return new GSqlcParser(getCompiler(), getBuffer(), environment);
  }

  private final KjcEnvironment  environment;
  private final boolean		isKopiFile;
}


// JLS 7.5.3 Automatic Imports
jAutomaticImports [CParseCompilationUnitContext context]
:
{
  context.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
  context.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/base", null));
  context.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/type", null));
  context.addClassImport(new JClassImport(TokenReference.NO_REF, "java/sql/SQLException", null));
}
;

// Accessed from Base
public gAction []
  returns [JStatement[] self = null]
{
  ArrayList	vect = null;
  JStatement	stmt;
}
:
  // LCURLY already consumed
  (
    stmt = jBlockStatement[]
      {
	if (vect == null) {
	  vect = new ArrayList();
	}
	vect.add(stmt);
      }
  )*
  RCURLY
    { self = (vect == null) ? JStatement.EMPTY :(JStatement[])vect.toArray(new JStatement[vect.size()]); }
;

// Accessed from Base
// Note: side effect on parameter params
public gActionWithParameter [String name, ArrayList params]
  returns [JStatement[] self = null]
{
  ArrayList		vect = null;
  JFormalParameter	param;
  JStatement		stmt;
  TokenReference	sourceRef = buildTokenReference();
}
:
  // LPAREN already consumed
  (
    param = jParameterDeclaration[JLocalVariable.DES_PARAMETER]
      { params.add(param); }
  |
    // !!!{ reportTrouble(new CWarning(sourceRef, "vk-old-syntax-6", null, null)); }
  )
  RPAREN
  LCURLY
  (
    stmt = jBlockStatement[]
      {
	if (vect == null) {
	  vect = new ArrayList();
	}
	vect.add(stmt);
      }
  )*
  RCURLY
    { self = (vect == null) ? JStatement.EMPTY : (JStatement[])vect.toArray(new JStatement[vect.size()]);} 
;

// Accessed from Base
public gFormalParameter [int desc]
  returns [JFormalParameter[] self = null]
{
  JFormalParameter	elem;
  ArrayList		vect = null;
}
:
  // LPAREN already consumed
  elem = jParameterDeclaration[desc] 
  { vect = new ArrayList(); vect.add(elem); }
  ( COMMA elem = jParameterDeclaration[desc] { vect.add(elem); } )*
  RPAREN
    { self = (vect == null) ? JFormalParameter.EMPTY :(JFormalParameter[])vect.toArray(new JFormalParameter[vect.size()]);}
;

// Accessed from Form
public gArguments []
  returns [JExpression[] self]
:
  // LPAREN already consumed
  self = jExpressionList[]
  RPAREN
;

// Accessed from Print
public gJavaExpressionStatement []
  returns [JExpression self]
:
  self = jExpression[]
  SEMI
;

// Accessed from Print
public gJavaExpressionInParenethesis []
  returns [JExpression self]
:
  // LPAREN already consumed
  self = jExpression[]
  RPAREN
;

// Accessed from Base
public gHeader [CParseCompilationUnitContext context]
{
  JPackageName			pack;
}
:
  // LCURLY already consumed
  pack = jPackageDeclaration[]
    { context.setPackage(pack); }
  ( jImportDeclaration[context] )*
  RCURLY
;

// Accessed from Base
// JLS 8.1.5 Class Body and Member Declarations
public gFooter [CParseClassContext context]
{
  TokenReference	sourceRef = buildTokenReference();
}
:
  // LCURLY already consumed
  (
    jMember[context]
  |
    SEMI
      { reportTrouble(new CWarning(sourceRef, at.dms.kopi.comp.kjc.KjcMessages.STRAY_SEMICOLON, null)); }
  )*
  RCURLY
;

// the basic element of an expression
jPrimaryExpression []
  returns [JExpression self = null]
{
  int			bounds = 0;
  CType			type;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ident : IDENT
    { self = new JNameExpression(sourceRef, ident.getText()); }
|
  self = jUnqualifiedNewExpression[]
|
  self = jLiteral[]
|
  "super"
    { self = new JSuperExpression(sourceRef); }
|
  "true"
    { self = new JBooleanLiteral(sourceRef, true); }
|
  "false"
    { self = new JBooleanLiteral(sourceRef, false); }
|
  "this"
    { self = new JThisExpression(sourceRef); }
|
  "null"
    { self = new JNullLiteral(sourceRef); }
|
  LPAREN self = jAssignmentExpression[] RPAREN
    { self = new JParenthesedExpression(sourceRef, self); }
|
  type = jBuiltInType[]
  ( LBRACK RBRACK { bounds++; } )*
  DOT "class"
    { self = new JClassExpression(buildTokenReference(), type, bounds); }
|
  AT self = gPrimary[]
;

// the basic element of an expression
gPrimary[]
  returns [JExpression self = null]
{
  String	name;
  int		count = 0;
  JExpression	expr = null;
  JExpression	expr1 = null;
  JExpression	expr2 = null;
  boolean	hasQuestion = false;
  boolean	hasBang = false;
  TokenReference sourceRef = buildTokenReference();
}
:
  (
    QUESTION { hasQuestion = true; }
  |
    LNOT { hasBang = true; }
  )?
  name = jIdentifier[]
  (
    options { warnWhenFollowAmbig = false; } :
    LBRACK expr = jExpression[] RBRACK
      {
	if (count > 1) {
	  throw new NoViableAltException(LT(1), getFilename());
	} else if (count == 0) {
	  expr1 = expr;
	  count++;
	} else {
	  expr2 = expr;
	  count++;
	}
      }
   )*
    { self = new GVKAccess(sourceRef, name.replace('/', '.'), expr1, expr2, hasBang, hasQuestion); }
;

jAssignmentExpression []
  returns [JExpression self = null]
{
  int			oper = -1;
  JExpression		right;
}
:
  self = jConditionalExpression[]
  (
    ASSIGN right = jAssignmentExpression[]
      { self = new GAssignmentExpression(self.getTokenReference(), self, right); }
  |
    oper = jCompoundAssignmentOperator[] right = jAssignmentExpression[]
      { self = new GCompoundAssignmentExpression(self.getTokenReference(), oper, self, right); }
  )?
|
  self = xExecSqlExpression[] 
|
  self = xParseSqlExpression[]
|
  self = gKopiInsertExpression[]
;

gKopiInsertExpression []
  returns [GKopiInsertExpression self = null]
{
  TokenReference	sourceRef = buildTokenReference();
  JExpression	conn = null;
  JExpression	table = null;
  ArrayList	columns = new ArrayList();
  ArrayList	exprs = new ArrayList();
  String	name;
  JExpression	expr;
}
:
		KOPI_INSERT
		( conn = xOptionalContext[] )?
		LPAREN table = jExpression[] RPAREN
		LCURLY
		s:STRING_LITERAL ASSIGN expr = jExpression[]
		  { columns.add(s.getText()); exprs.add(expr); }
		(
			COMMA s2:STRING_LITERAL ASSIGN expr = jExpression[]
			  { columns.add(s2.getText()); exprs.add(expr); }
		)*
		RCURLY
		  {
		    self = new GKopiInsertExpression(sourceRef,
						     conn,
						     table,
						     (String[])columns.toArray(new String[columns.size()]),
						     (JExpression[])exprs.toArray(new JExpression[exprs.size()]));
		  }
	;

xExecSqlStatement []
  returns [XExecSqlStatement self = null]
{
  String		name;
  TokenReference	sourceRef = buildTokenReference();
  JExpression		conn = null;
  XSqlExpr		expr = null;
  JExpressionListStatement listStmt = null;
  JExpression[]		list;
  ArrayList		types = new ArrayList();
  JavaStyleComment[]	comments = getStatementComment();
}
	:
		EXECUTE_SQL
		( conn = xOptionalContext[] )?
		LCURLY
		  { expr = new XSqlExpr(sourceRef, buildXSqlcParser().xSelectIntoStatement(types)); }
		list = jExpressionList[]
		  { listStmt = new JExpressionListStatement(buildTokenReference(), list, getStatementComment()); }
		RCURLY
		  {
		    self = new GExecSqlStatement(sourceRef,
						 conn,
						 expr,
						 (CType[])types.toArray(new CType[types.size()]), 
						 listStmt,
						 comments);
		  }
;

xProtectedStatement []
  returns [JStatement self = null]
{
  JExpression		cont = null;
  JExpression		mess = null;
  JavaStyleComment[]	comments = getStatementComment();
  JStatement[]		stmts;
  TokenReference	sourceRef = buildTokenReference();
  CReferenceType        exceptionVException;
  CReferenceType        exceptionInconsistency;
  CReferenceType        exceptionSQLType;
  CReferenceType        exceptionDBDeadLock;
  CReferenceType        exceptionDBInterrupted;
}
:
  PROTECTED_SQL
  ( cont = xOptionalContext[] )?
  LPAREN ( mess = jExpression[] )? RPAREN
  stmts = jCompoundStatement[]
     {      
       exceptionVException = environment.getTypeFactory().createType(VExecFailedException.class.getName().replace('.','/'), false); 
       exceptionInconsistency = environment.getTypeFactory().createType(InconsistencyException.class.getName().replace('.','/'), false); 
       exceptionDBDeadLock = environment.getTypeFactory().createType(DBDeadLockException.class.getName().replace('.','/'), false); 
       exceptionDBInterrupted = environment.getTypeFactory().createType(DBInterruptionException.class.getName().replace('.','/'), false); 
       exceptionSQLType = environment.getTypeFactory().createType("java/sql/SQLException", false); 

       self = new GProtectionStatementWrapper(sourceRef,
                                              new XProtectedStatement(sourceRef, mess, cont, stmts, comments),
                                              exceptionVException,
                                              exceptionInconsistency,
                                              exceptionSQLType,
                                              exceptionDBDeadLock,
                                              exceptionDBInterrupted);
     }
;

xUnprotectedStatement []
  returns [JStatement self = null]
{
  JExpression		cont = null;
  JExpression		mess = null;
  JStatement[]		stmts;
  TokenReference	sourceRef = buildTokenReference();
  JavaStyleComment[]	comments = getStatementComment();
  CReferenceType        exceptionVException;
  CReferenceType        exceptionInconsistency;
  CReferenceType        exceptionSQLType;
  CReferenceType        exceptionDBDeadLock;
  CReferenceType        exceptionDBInterrupted;
}
:
  UNPROTECTED_SQL
  ( cont = xOptionalContext[] )?
  LPAREN ( mess = jExpression[] )? RPAREN
  stmts = jCompoundStatement[]
     {      
       exceptionVException = environment.getTypeFactory().createType(VExecFailedException.class.getName().replace('.','/'), false); 
       exceptionInconsistency = environment.getTypeFactory().createType(InconsistencyException.class.getName().replace('.','/'), false); 
       exceptionDBDeadLock = environment.getTypeFactory().createType(DBDeadLockException.class.getName().replace('.','/'), false); 
       exceptionDBInterrupted = environment.getTypeFactory().createType(DBInterruptionException.class.getName().replace('.','/'), false); 
       exceptionSQLType = environment.getTypeFactory().createType("java/sql/SQLException", false); 

       self = new GProtectionStatementWrapper(sourceRef,
                                              new XUnprotectedStatement(sourceRef, mess, cont, stmts, comments),
                                              exceptionVException,
                                              exceptionInconsistency,
                                              exceptionSQLType,
                                              exceptionDBDeadLock,
                                              exceptionDBInterrupted);
     }
;
