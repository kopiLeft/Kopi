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
header { package at.dms.xkopi.comp.xkjc; }
{
  import java.util.ArrayList;

  import at.dms.compiler.base.CWarning;
  import at.dms.compiler.base.Compiler;
  import at.dms.compiler.base.JavaStyleComment;
  import at.dms.compiler.base.JavadocComment;
  import at.dms.compiler.base.PositionedError;
  import at.dms.compiler.base.TokenReference;
  import at.dms.compiler.tools.antlr.extra.InputBuffer;
  import at.dms.kopi.comp.kjc.*;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class XKjcParser extends KjcParser;

options {
  k = 2;				// two token lookahead
  importVocab = XKjc;			// Call its vocabulary "XKjc"
  exportVocab = XKjc;			// Call its vocabulary "XKjc"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass="at.dms.compiler.tools.antlr.extra.Parser";
  access = "private";			// Set default rule access
}
{
  public XKjcParser(Compiler compiler, InputBuffer buffer, KjcEnvironment environment) {
    super(compiler, new XKjcScanner(compiler, buffer), MAX_LOOKAHEAD);
    this.isKopiFile = buffer.getFile().endsWith(".k");
    this.environment = environment;
  }

  private XSqlcParser buildXSqlcParser() {
    return new XSqlcParser(getCompiler(), getBuffer(), environment);
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
}
;

jMethodDefinition [CParseClassContext context, JavadocComment javadoc, int modifiers, CType type, CTypeVariable[] typeVariables]
  returns [JMethodDeclaration self = null]
{
  int			operator = -1;
  JFormalParameter[]	parameters;
  int			bounds = 0;
  CReferenceType[]		throwsList = CReferenceType.EMPTY;
  JStatement[]		body = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  name : IDENT
  ( COLON operator = xOverloadableOperator[] )?
  LPAREN parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
  ( LBRACK RBRACK { bounds += 1; } )*
    {
      if (bounds > 0) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
	type = new CArrayType(type, bounds);
      }
    }
  (throwsList = jThrowsClause[])?
  (
    body = jCompoundStatement[]
  |
    SEMI
  )
    {
      if (operator == -1) {
	self = new JMethodDeclaration(sourceRef,
				      modifiers, 
                                      typeVariables,
				      type,
				      name.getText(),
				      parameters,
				      throwsList,
				      body == null ? null : new JBlock(sourceRef, body, null),
				      javadoc,
				      getStatementComment());
      } else {
	self = new XOperatorDeclaration(sourceRef,
					modifiers,
					type,
					name.getText(),
					operator,
					parameters,
					throwsList,
					body == null ? null : new JBlock(sourceRef, body, null),
					false);
      }
    }
;

// Definition of a Java class
jClassDefinition[int modifiers]
  returns [JClassDeclaration self = null]
{
  CReferenceType		superClass = null;
  CReferenceType[]		interfaces = CReferenceType.EMPTY;
  CParseClassContext	context = new CParseClassContext();
  TokenReference	sourceRef = buildTokenReference();
  JavadocComment	javadoc = getJavadocComment();
  JavaStyleComment[]	comments = getStatementComment();
  XSelectStatement	sel = null;
  JFormalParameter[]	parameters = null;
}
:
  (
    "class" ident:IDENT
    superClass = jSuperClassClause[]
    interfaces = jImplementsClause[]
    jClassBlock[context]
      {
	self = new JClassDeclaration(sourceRef,
				     modifiers,
				     ident.getText(), 
                                     CTypeVariable.EMPTY,
				     superClass,
				     interfaces,
				     context.getFields(),
				     context.getMethods(),
				     context.getInnerClasses(),
				     context.getBody(),
				     javadoc,
				     comments);
	context.release();
      }
  )
|
  (
    CURSOR_SQL ident2:IDENT
    (
      xExplicitCursorBody[context]
        { 
          XCursorDeclaration.addCreateCopy(sourceRef,
					   context,
					   ident2.getText(),
					   environment.getTypeFactory()); 
	}
    |
      LPAREN parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
      sel = xImplicitCursorBody[context]
        { XCursorDeclaration.addImplicitSelect(context,
                                               ident2.getText(),
					       sel,
                                               parameters,
                                               environment.getTypeFactory()); }
    )
      {
	self = new XContextCursorDeclaration(sourceRef,
                                         modifiers,
                                         ident2.getText(),
                                         context.getFields(),
                                         context.getMethods(),
                                         context.getInnerClasses(),
                                         context.getBody(),
                                         sel,
                                         parameters,
                                         javadoc,
                                         comments);
	context.release();
      }
  )
;

jIfStatement []
  returns [JIfStatement self = null]
{
  JExpression		cond;
  JStatement		thenClause;
  JStatement		elseClause = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "if" LPAREN cond = jExpression[] RPAREN
  thenClause = jStatement[]
//   (
//     ("else") => "else" elseClause = jStatement[]
//     |
//   ) 
  {
    // LOOKAHEAD OF 2 FORBIDDEN
    // if there is a solution for this problem the rule of kjc.g can be used
    if (LA(1)==LITERAL_else) {
      match(LITERAL_else);
      elseClause = jStatement();
    }
  }
    {
      if (! (thenClause instanceof JBlock)) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_IF_THEN_IN_BLOCK, null));
      }
      if (elseClause != null && !(elseClause instanceof JBlock || elseClause instanceof JIfStatement)) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_IF_ELSE_IN_BLOCK, null));
      }
      self = new JIfStatement(sourceRef, cond, thenClause, elseClause, getStatementComment());
    }

;jAdditiveExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jMultiplicativeExpression[]
  (
    PLUS right = jMultiplicativeExpression[]
      { self = new XAddExpression(self.getTokenReference(), self, right); }
  |
    MINUS right = jMultiplicativeExpression[]
      { self = new XMinusExpression(self.getTokenReference(), self, right); }
  )*
;

// equality/inequality (==/!=) (level 6)
jEqualityExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jRelationalExpression[]
  (
    NOT_EQUAL right = jRelationalExpression[]
      { self = new XEqualityExpression(self.getTokenReference(), false, self, right); }
  |
    EQUAL right = jRelationalExpression[]
      { self = new XEqualityExpression(self.getTokenReference(), true, self, right); }
  )*
;

jUnaryExpression []
  returns [JExpression self = null]
{
  TokenReference	sourceRef = buildTokenReference();
}
:
  INC self = jUnaryExpression[]
    { self = new XPrefixExpression(sourceRef, Constants.OPE_PREINC, self); }
|
  DEC self = jUnaryExpression[]
    { self = new XPrefixExpression(sourceRef, Constants.OPE_PREDEC, self); }
|
  MINUS self = jUnaryExpression[]
    { self = new XUnaryMinusExpression(sourceRef, self); }
|
  PLUS self = jUnaryExpression[]
    { self = new XUnaryPlusExpression(sourceRef, self); }
|
  self = jUnaryExpressionNotPlusMinus[]
;

jUnaryExpressionNotPlusMinus []
  returns [JExpression self = null]
{
  JExpression		expr;
  CType			dest;
  TokenReference	sourceRef = buildTokenReference();
}
:
  BNOT self = jUnaryExpression[]
    { self = new XBitwiseComplementExpression(sourceRef, self); }
|
  LNOT self = jUnaryExpression[]
    { self = new XLogicalComplementExpression(sourceRef, self); }
|
  (
    // subrule allows option to shut off warnings
    options {
      // "(int" ambig with postfixExpr due to lack of sequence
      // info in linear approximate LL(k).  It's ok.  Shut up.
      generateAmbigWarnings=false;
    } :
    // If typecast is built in type, must be numeric operand
    // Also, no reason to backtrack if type keyword like int, float...
    LPAREN dest = jBuiltInTypeSpec[] RPAREN
    expr = jUnaryExpression[]
      { self = new XCastExpression(sourceRef, expr, dest); }
    // Have to backtrack to see if operator follows.  If no operator
    // follows, it's a typecast.  No semantic checking needed to parse.
    // if it _looks_ like a cast, it _is_ a cast; else it's a "(expr)"
  |
    (LPAREN jClassTypeSpec[] RPAREN jUnaryExpressionNotPlusMinus[])=>
    LPAREN dest = jClassTypeSpec[] RPAREN
    expr = jUnaryExpressionNotPlusMinus[]
      { self = new XCastExpression(sourceRef, expr, dest); }
  |
    self = jPostfixExpression[]
  )
;

// multiplication/division/modulo (level 2)
jMultiplicativeExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jUnaryExpression[]
  (
    STAR right = jUnaryExpression[]
      { self = new XMultExpression(self.getTokenReference(), self, right); }
  |
    SLASH right = jUnaryExpression[]
      { self = new XDivideExpression(self.getTokenReference(), self, right); }
  |
    PERCENT right = jUnaryExpression[]
      { self = new XModuloExpression(self.getTokenReference(), self, right); }
  )*
;

// qualified names, array expressions, method invocation, post inc/dec
jPostfixExpression[]
  returns [JExpression self = null]
{
  int			bounds = 0;
  JExpression		expr;
  JExpression[]		args = null;
  CType			type;
  TokenReference	sourceRef = buildTokenReference();
}
:
  self = jPrimaryExpression[] // start with a primary
  (
    // qualified id (id.id.id.id...) -- build the name
    DOT
    (
      ident : IDENT
        { self = new XNameExpression(sourceRef, self, ident.getText()); }
    |
      "this"
        { self = new JThisExpression(sourceRef, self); }
    |
      "class"
        { self = new JClassExpression(sourceRef, self, 0); }
    |
      self = jQualifiedNewExpression[self]
    )
  |
    // allow ClassName[].class
    ( LBRACK RBRACK { bounds++; } )+
    DOT "class"
      { self = new JClassExpression(sourceRef, self, bounds); }
  |
    LBRACK expr = jExpression[] RBRACK
      { self = new JArrayAccessExpression(sourceRef, self, expr); }
  |
    LPAREN args = jArgList[] RPAREN
      {
	if (! (self instanceof JNameExpression)) {
	  reportTrouble(new PositionedError(sourceRef, KjcMessages.INVALID_METHOD_NAME, null));
	} else {
	  self = new XMethodCallExpression(sourceRef,
					   ((JNameExpression)self).getPrefix(),
					   ((JNameExpression)self).getName(),
					   args);
	}
      }
  )*
  (
    INC
      { self = new XPostfixExpression(sourceRef, Constants.OPE_POSTINC, self); }
  |
    DEC
      { self = new XPostfixExpression(sourceRef, Constants.OPE_POSTDEC, self); }
  |
    // nothing
  )
;

// bitwise or non-short-circuiting or (|)  (level 9)
jInclusiveOrExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jExclusiveOrExpression[]
  (
    BOR right = jExclusiveOrExpression[]
      { self = new XBitwiseExpression(self.getTokenReference(), Constants.OPE_BOR, self, right); }
  )*
;


// exclusive or (^)  (level 8)
jExclusiveOrExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jAndExpression[]
  (
    BXOR right = jAndExpression[]
      { self = new XBitwiseExpression(self.getTokenReference(), Constants.OPE_BXOR, self, right); }
  )*
;


// bitwise or non-short-circuiting and (&)  (level 7)
jAndExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jEqualityExpression[]
  (
    BAND right = jEqualityExpression[]
      { self = new XBitwiseExpression(self.getTokenReference(), Constants.OPE_BAND, self, right); }
  )*
;

// boolean relational expressions (level 5)
jRelationalExpression []
  returns [JExpression self = null]
{
  int			operator = -1;
  JExpression		right;
  CType			type;
}
:
  self = jShiftExpression[]
  (
    (
      (
        LT { operator = Constants.OPE_LT; }
      |
	GT { operator = Constants.OPE_GT; }
      |
	LE { operator = Constants.OPE_LE; }
      |
	GE { operator = Constants.OPE_GE; }
      )
      right = jShiftExpression[]
        { self = new XRelationalExpression(self.getTokenReference(), operator, self, right); }
    )*
  |
    "instanceof" type = jTypeSpec[]
      { self = new JInstanceofExpression(self.getTokenReference(), self, type); }
  )
;

// bit shift expressions (level 4)
jShiftExpression []
  returns [JExpression self = null]
{
  int			operator = -1;
  JExpression		right;
}
:
  self = jAdditiveExpression[]
  (
    (
      SL { operator = Constants.OPE_SL; }
    |
      SR { operator = Constants.OPE_SR; }
    |
      BSR { operator = Constants.OPE_BSR; }
    )
    right = jAdditiveExpression[]
      { self = new XShiftExpression(self.getTokenReference(), operator, self, right); }
  )*
;

jBuiltInType []
  returns [CType self = null]
{
  boolean	nullable = false;
  TypeFactory   tf = environment.getTypeFactory();
}
:
  "void" { self = tf.getVoidType(); }
|
  ( "nullable" { nullable = true; } )?
  (
    "boolean" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_BOOLEAN) : tf.getPrimitiveType(TypeFactory.PRM_BOOLEAN); }
  |
    "byte" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_BYTE) : tf.getPrimitiveType(TypeFactory.PRM_BYTE); }
  |
    "char" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_CHARACTER) : tf.getPrimitiveType(TypeFactory.PRM_CHAR); }
  |
    "short" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_SHORT) : tf.getPrimitiveType(TypeFactory.PRM_SHORT); }
  |
    "int" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_INTEGER) : tf.getPrimitiveType(TypeFactory.PRM_INT); }
  |
    "long" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_LONG) : tf.getPrimitiveType(TypeFactory.PRM_LONG); }
  |
    "float" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_FLOAT) : tf.getPrimitiveType(TypeFactory.PRM_FLOAT); }
  |
    "double" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_DOUBLE) : tf.getPrimitiveType(TypeFactory.PRM_DOUBLE); }
  |
    "fixed" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_FIXED) : tf.createReferenceType(XTypeFactory.PRM_PFIXED); }
  |
    "date" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_DATE) : tf.createReferenceType(XTypeFactory.PRM_PDATE); }
  |
    "time" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_TIME) : tf.createReferenceType(XTypeFactory.PRM_PTIME); }
  |
    "timestamp" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_TIMESTAMP) : tf.createReferenceType(XTypeFactory.PRM_PTIMESTAMP); }
  |
    "month" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_MONTH) : tf.createReferenceType(XTypeFactory.PRM_PMONTH); }
  |
    "week" { self = nullable ? (CType)tf.createReferenceType(XTypeFactory.RFT_WEEK) : tf.createReferenceType(XTypeFactory.PRM_PWEEK); }
  )
;

/**
 * Declaration of a variable.  This can be a class/instance variable,
 * or a local variable in a method
 * It can also include possible initialization.
 */
jVariableDeclarator [int modifiers, CType type]
  returns [JVariableDefinition self = null]
{
  int			bounds = 0;
  JExpression		expr = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ident : IDENT
  ( LBRACK RBRACK { bounds += 1; } )*
  ( ASSIGN expr = jVariableInitializer[] )?
    {
      if (bounds > 0) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
	type = new CArrayType(type, bounds);
      }
      self = new XVariableDefinition(sourceRef, modifiers, type, ident.getText(), expr);
    }
;

jBlockStatement []
   returns [JStatement self = null]
{
  JTypeDeclaration	type;
  int			modifiers;
}
:
  // cursor definition
  self = xAnonymousCursorDefinition[0]
|
  ( jModifiers[] ( "class" | CURSOR_SQL ) ) =>
  modifiers = jModifiers[] type = jClassDefinition[modifiers]
    { self = new JTypeDeclarationStatement(type.getTokenReference(), type); }
|
  // declarations are ambiguous with "ID DOT" relative to expression
  // statements.  Must backtrack to be sure.  Could use a semantic
  // predicate to test symbol table to see what the type was coming
  // up, but that's pretty hard without a symbol table ;-)
  ( jModifiers[] jTypeSpec[] IDENT ) =>
  modifiers = jModifiers[] self = jLocalVariableDeclaration[modifiers] SEMI
|
  self = jStatement[]
;

jStatement []
   returns [JStatement self = null]
{
  JExpression		expr;
  JStatement[]		stmts;
  TokenReference	sourceRef = buildTokenReference();
}
:
  self = kAssertStatement[]
|
    // the following two statements are replaced with kAssertStatement
//   self = xAssertStatement[]
// |
//   self = xFailStatement[]
// |
  self = xExecSqlStatement[]
|
  self = xProtectedStatement[]
|
  self = xUnprotectedStatement[]
|
  // A list of statements in curly braces -- start a new scope!
  stmts = jCompoundStatement[]
    { self = new JBlock(sourceRef, stmts, getStatementComment()); }
|
  // An expression statement.  This could be a method call,
  // assignment statement, or any other expression evaluated for
  // side-effects.
  expr = jExpression[] SEMI
    { self = new JExpressionStatement(sourceRef, expr, getStatementComment()); }
|
  self = jLabeledStatement[]
|
  self = jIfStatement[]
|
  self = jForStatement[]
|
  self = jWhileStatement[]
|
  self = jDoStatement[]
|
  self = jBreakStatement[]
|
  self = jContinueStatement[]
|
  self = jReturnStatement[]
|
  self = jSwitchStatement[]
|
  // exception try-catch block
  self = jTryBlock[]
|
  self = jThrowStatement[]
|
  self = jSynchronizedStatement[]
|
  self = xGotoStatement[]
|
  // empty statement
  SEMI
    { self = new JEmptyStatement(sourceRef, getStatementComment()); }
;

xGotoStatement []
  returns [XGotoStatement self = null]
{
  TokenReference	sourceRef = buildTokenReference();
}
:
  "goto" label:IDENT SEMI
    { self = new XGotoStatement(sourceRef, label.getText(), getStatementComment()); }
;

jRealLiteral []
  returns [JLiteral self = null]
:
  r : REAL_LITERAL
    {
      if (isKopiFile) {
	self = new XFixedLiteral(buildTokenReference(), r.getText());
      } else {
	try {
	  self = JLiteral.parseReal(buildTokenReference(), r.getText());
	} catch (PositionedError e) {
	  reportTrouble(e);
          // allow parsing to continue
          self = new JFloatLiteral(TokenReference.NO_REF, 0f);
	}
      }
    }
;

jUnqualifiedNewExpression []
  returns [JExpression self = null]
{
  CType				type;
  JExpression[]			args;
  JArrayInitializer		init = null;
  JClassDeclaration		decl = null;
  CParseClassContext		context = null;
  TokenReference		sourceRef = buildTokenReference();
}
:
  "new"
  (
    type = jBuiltInType[]
    args = jNewArrayDeclarator[] ( init = jArrayInitializer[] )?
      { self = new XNewArrayExpression(sourceRef, type, args, init); }
  |
    type = jTypeName[]
    (
      args = jNewArrayDeclarator[] ( init = jArrayInitializer[] )?
        { self = new XNewArrayExpression(sourceRef, type, args, init); }
    |
      LPAREN args = jArgList[] RPAREN
      (
        { context = new CParseClassContext(); }
        jClassBlock[context]
          {
            JMethodDeclaration[]      methods;

            if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
              JMethodDeclaration[]    assertions = context.getAssertions();
              JMethodDeclaration[]    decMethods = context.getMethods();

              methods = new JMethodDeclaration[assertions.length+decMethods.length];
              // assertions first!
              System.arraycopy(assertions, 0, methods, 0, assertions.length);
              System.arraycopy(decMethods, 0, methods, assertions.length, decMethods.length);
            } else {
              methods = context.getMethods();
            }

	    decl = new JClassDeclaration(sourceRef,
					 Constants.ACC_FINAL, // JLS 15.9.5
					 "", //((CReferenceType)type).getQualifiedName(),
                                         CTypeVariable.EMPTY,
					 null,
					 CReferenceType.EMPTY,
					 context.getFields(),
					 methods,
					 context.getInnerClasses(),
					 context.getBody(),
					 getJavadocComment(),
					 getStatementComment());
	    context.release();
	  }
          { self = new JUnqualifiedAnonymousCreation(sourceRef, (CReferenceType)type, args, decl); }
      |
	// epsilon
        { self = new JUnqualifiedInstanceCreation(sourceRef, (CReferenceType)type, args); }
      )
    )
  )
;

// //////////////////////////////////////////////////////////////////////
// OVERLOADABLE OPERATORS
// //////////////////////////////////////////////////////////////////////

xOverloadableOperator []
  returns [int self = -1]
:
  EQUAL { self = Constants.OPE_EQ; }
|
  LNOT { self = Constants.OPE_LNOT; }
|
  BNOT { self = Constants.OPE_BNOT; }
|
  NOT_EQUAL { self = Constants.OPE_NE; }
|
  SLASH { self = Constants.OPE_SLASH; }
|
  PLUS { self = Constants.OPE_PLUS; }
|
  MINUS { self = Constants.OPE_MINUS; }
|
  STAR { self = Constants.OPE_STAR; }
|
  PERCENT { self = Constants.OPE_PERCENT; }
|
  SR { self = Constants.OPE_SR; }
|
  BSR { self = Constants.OPE_BSR; }
|
  GE { self = Constants.OPE_GE; }
|
  GT { self = Constants.OPE_GT; }
|
  SL { self = Constants.OPE_SL; }
|
  LE { self = Constants.OPE_LE; }
|
  LT { self = Constants.OPE_LT; }
|
  BXOR { self = Constants.OPE_BXOR; }
|
  BOR { self = Constants.OPE_BOR; }
|
  BAND { self = Constants.OPE_BAND; }
|
  LPAREN RPAREN { self = XConstants.OPE_CAST; }
|
  ASSIGN { self = XConstants.OPE_SIMPLE; }
|
  INC STAR { self = Constants.OPE_PREINC; }
|
  DEC STAR { self = Constants.OPE_PREDEC; }
|
  STAR
  (
    INC { self = Constants.OPE_POSTINC; }
  |
    DEC { self = Constants.OPE_POSTDEC; }
  )
;

// A formal parameter.
jParameterDeclaration [int desc]
  returns [JFormalParameter self = null]
{
  boolean	isFinal = false;
  int		bounds = 0;
  CType		type;
  String	name = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ( "final" { isFinal = true; } )?
  type = jTypeSpec[]
  (
    ident:IDENT {name = ident.getText();}
    ( LBRACK RBRACK { bounds += 1; } )*
  )?
    {
      if (bounds > 0) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
	type = new CArrayType(type, bounds);
      }
      self = new XFormalParameter(sourceRef, desc, type, name, isFinal);
    }
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
      { self = new XAssignmentExpression(self.getTokenReference(), self, right); }
  |
    oper = jCompoundAssignmentOperator[] right = jAssignmentExpression[]
      { self = new XCompoundAssignmentExpression(self.getTokenReference(), oper, self, right); }
  )?
|
  self = xExecSqlExpression[]
|
  self = xExecInsertExpression[]
|
  self = xParseSqlExpression[]
;

// //////////////////////////////////////////////////////////////////////
// NEW STATEMENTS
// //////////////////////////////////////////////////////////////////////

xExplicitCursorBody [CParseClassContext context]
{
  CType			type;
  JVariableDefinition	field;
  TokenReference	sourceRef = buildTokenReference();
  JavadocComment	javadoc = getJavadocComment();
  JavaStyleComment[]	comments = getStatementComment();
}
:
  LCURLY
  (
    type = jTypeSpec[] field = jVariableDeclarator[XConstants.ACC_PUBLIC, type] SEMI
      { context.addFieldDeclaration(new XCursorFieldDeclaration(sourceRef, field, javadoc, comments)); }
  )+
  RCURLY
;

xImplicitCursorBody [CParseClassContext context]
  returns [XSelectStatement sel = null]
:
  LCURLY
    { sel = buildXSqlcParser().xSelectStatement(); }
  //RCURLY
;

xOptionalContext []
  returns [JExpression self]
:
  LBRACK
  self = jExpression[]
  RBRACK
;

// accessed from XSqlc
public xExpressionInSql []
  returns [JExpression self = null]
:
  //LPAREN
  self = jExpression[]
  RPAREN
;

xExecSqlExpression []
  returns [XExecSqlExpression self = null]
{
  JExpression		conn = null;
  JExpression		cursor = null;
  XSqlExpr		expr = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ( UPDATE_SQL | DELETE_SQL )
  ( conn = xOptionalContext[] )?
  ( LPAREN cursor = jExpression[] RPAREN )?
  LCURLY
    { expr = new XSqlExpr(sourceRef, buildXSqlcParser().xUpdateStatement()); }
  //RCURLY
    { self = new XExecSqlExpression(sourceRef, conn, cursor, expr); }
;

xExecInsertExpression []
  returns [XExecInsertExpression self = null]
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
  INSERT_SQL ( conn = xOptionalContext[] )?
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
    self = new XExecInsertExpression(sourceRef,
                                     conn,
                                     table,
                                     (String[])columns.toArray(new String[columns.size()]),
                                     (JExpression[])exprs.toArray(new JExpression[exprs.size()]));
  }
;



xParseSqlExpression []
  returns [XParseSqlExpression self = null]
{
  XSqlExpr		expr = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  PARSE_SQL
  LCURLY
    { expr = new XSqlExpr(sourceRef, buildXSqlcParser().xAnyStatement()); }
  //RCURLY
    { self = new XParseSqlExpression(sourceRef, expr); }
;

xExecSqlStatement []
  returns [XExecSqlStatement self = null]
{
  JExpression		conn = null;
  XSqlExpr		expr = null;
  JExpressionListStatement listStmt = null;
  ArrayList		types = new ArrayList();
  TokenReference	sourceRef = buildTokenReference();
  JavaStyleComment[]	comments = getStatementComment();
  JExpression[]		list;
}
:
  EXECUTE_SQL
  ( conn = xOptionalContext[] )?
  LCURLY
    { expr = new XSqlExpr(sourceRef, buildXSqlcParser().xSelectIntoStatement(types)); }
  // INTO
  list = jExpressionList[]
    { listStmt = new JExpressionListStatement(buildTokenReference(), list, getStatementComment()); }
  RCURLY
    { self = new XExecSqlStatement(sourceRef,
				   conn,
				   expr,
				   (CType[])types.toArray(new CType[types.size()]), //at.dms.util.base.Utils.toArray(types, CType.class),
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
}
:
  PROTECTED_SQL
  ( cont = xOptionalContext[] )?
  LPAREN ( mess = jExpression[] )? RPAREN
  stmts = jCompoundStatement[]
    { self = new XProtectedStatement(sourceRef, mess, cont, stmts, comments); }
;

xUnprotectedStatement []
  returns [JStatement self = null]
{
  JExpression		cont = null;
  JExpression		mess = null;
  JStatement[]		stmts;
  TokenReference	sourceRef = buildTokenReference();
  JavaStyleComment[]	comments = getStatementComment();
}
:
  UNPROTECTED_SQL
  ( cont = xOptionalContext[] )?
  LPAREN ( mess = jExpression[] )? RPAREN
  stmts = jCompoundStatement[]
    { self = new XUnprotectedStatement(sourceRef, mess, cont, stmts, comments); }
;

xAnonymousCursorDefinition [int modifiers]
  returns [XLocalCursorDeclaration self = null]
{
  CParseClassContext	context = new CParseClassContext();
  XCursorDeclaration	curs = null;
  JExpression		ctxt = null;
  XSelectStatement	sel = null;
  JFormalParameter[]	parameters = null;
  TokenReference	sourceRef = buildTokenReference();
  JavadocComment	javadoc = getJavadocComment();
  JavaStyleComment[]	comments = getStatementComment();
}
:
  CURSOR_SQL
  ( ctxt = xOptionalContext[] )?
  (
    xExplicitCursorBody[context]
  |
    LPAREN parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
    sel = xImplicitCursorBody[context]
       { XCursorDeclaration.addImplicitSelect(context,
					      null,
                                              sel,
                                              parameters,
                                              environment.getTypeFactory()); }
  )
    {
      curs = new XCursorDeclaration(sourceRef,
				    /*XConstants.ACC_FINAL*/0,
				    null,
				    context.getFields(),
				    context.getMethods(),
				    context.getInnerClasses(),
				    context.getBody(),
				    sel,
				    parameters,
				    javadoc,
				    comments);
      context.release();

      self = new XLocalCursorDeclaration(sourceRef, curs, comments);
      self.setConnection(ctxt);
    }
  ident:IDENT
    { self.addDeclaration(ident.getText()); }
  (
    COMMA ident2:IDENT
      { self.addDeclaration(ident2.getText()); }
  )*
  SEMI
;
