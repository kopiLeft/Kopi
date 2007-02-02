/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.xkjc;

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.JavadocComment;

/**
 * This class represents cursor declaration in code part
 */
public class XGotoStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new goto statement.
   * @param	where		the line of this node in the source code
   * @param	label		the target of the goto
   * @param	comments	comments in the source code
   */
  public XGotoStatement(TokenReference where,
                        String label,
                        JavaStyleComment[] comments)
  {
    super(where, comments);
    this.label = label;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    // Look for labeled statement
    LabelVisitor visitor = new LabelVisitor(label);

    ((CSourceMethod)context.getMethodContext().getCMethod()).getBody().accept(visitor);
    
    target = visitor.target1;

    check(context, target != null, KjcMessages.LABEL_UNKNOWN, label);
    check(context,
          ((XKjcEnvironment)context.getEnvironment()).isGotoAllowed(),
          XKjcMessages.GOTO_NOT_ALLOWED);
  }
  
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------
  
  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genXKjcCode(XKjcPrettyPrinter p) {
    p.visitGotoStatement(this, label);
  }

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    super.accept(p);

    if (p instanceof XKjcPrettyPrinter) {
      genXKjcCode((XKjcPrettyPrinter)p);
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();
    
    setLineNumber(code);

    code.plantJumpInstruction(opc_goto, ((JLabeledStatement)target).getBeginLabel());
    
    target = null;
  }

  // ----------------------------------------------------------------------
  // LABEL VISITOR
  // ----------------------------------------------------------------------

  class LabelVisitor implements com.kopiright.kopi.comp.kjc.KjcVisitor{
    String              label1;
    JStatement          target1;

    public LabelVisitor(String label) {
      this.label1 = label;
    }

    /**
     * prints a compilation unit
     */
    public void visitCompilationUnit(JCompilationUnit self,
                                     JPackageName packageName,
                                     JPackageImport[] importedPackages,
                                     JClassImport[] importedClasses,
                                     JTypeDeclaration[] typeDeclarations)
    {
      throw new RuntimeException();
    }
    
    // ----------------------------------------------------------------------
    // TYPE DECLARATION
    // ----------------------------------------------------------------------
    
    /**
     * prints a class declaration
     */
    public void visitClassDeclaration(JClassDeclaration self,
                                      int modifiers,
                                      String ident,
                                      CTypeVariable[] typeVariables,
                                      String superClass,
                                      CReferenceType[] interfaces,
                                      JPhylum[] body,
                                      JMethodDeclaration[] methods,
                                      JTypeDeclaration[] decls)
    {
      throw new RuntimeException();
    }

    /**
     *
     */
    public void visitClassBody(JTypeDeclaration[] decls,
                               JMethodDeclaration[] methods,
                               JPhylum[] body)
    {
      throw new RuntimeException();
    }

    /**
     * prints a class declaration
     */
    public void visitInnerClassDeclaration(JClassDeclaration self,
                                           int modifiers,
                                           String ident,
                                           String superClass,
                                           CReferenceType[] interfaces,
                                           JTypeDeclaration[] decls,
                                           JPhylum[] body,
                                           JMethodDeclaration[] methods)
    {
      throw new RuntimeException();
    }

    /**
     * prints an interface declaration
     */
    public void visitInterfaceDeclaration(JInterfaceDeclaration self,
                                          int modifiers,
                                          String ident,
                                          CReferenceType[] interfaces,
                                          JPhylum[] body,
                                          JMethodDeclaration[] methods)
    {
      throw new RuntimeException();
    }

    /**
     * prints a field declaration
     */
    public void visitFieldDeclaration(JFieldDeclaration self,
                                      int modifiers,
                                      CType type,
                                      String ident,
                                      JExpression expr)
    {
      throw new RuntimeException();
    }
    
    /**
     * prints a method declaration
     */
    public void visitMethodDeclaration(JMethodDeclaration self,
                                       int modifiers,
                                       CTypeVariable[] typeVariables,
                                       CType returnType,
                                       String ident,
                                       JFormalParameter[] parameters,
                                       CReferenceType[] exceptions,
                                       JBlock body)
    {
      throw new RuntimeException();
    }

    /**
     * prints a constrained method declaration
     */
    public void visitKopiMethodDeclaration(JMethodDeclaration self,
                                           int modifiers,
                                           CType returnType,
                                           String ident,
                                           JFormalParameter[] parameters,
                                           CReferenceType[] exceptions,
                                           JBlock body,
                                           JBlock ensure,
                                           JBlock require)
    {
      throw new RuntimeException();
    }
    
    /**
     * prints a method declaration
     */
    public void visitConstructorDeclaration(JConstructorDeclaration self,
                                            int modifiers,
                                            String ident,
                                            JFormalParameter[] parameters,
                                            CReferenceType[] exceptions,
                                            JConstructorBlock body)
    {
      throw new RuntimeException();
    }

    // ----------------------------------------------------------------------
    // STATEMENT
    // ----------------------------------------------------------------------
    
    /**
     * prints a while statement
     */
    public void visitWhileStatement(JWhileStatement self,
                                    JExpression cond,
                                    JStatement body)
    {
      body.accept(this);
    }
    
    /**
     * prints a variable declaration statement
     */
    public void visitVariableDeclarationStatement(JVariableDeclarationStatement self,
                                                  JVariableDefinition[] vars)
    {
    }
    
    /**
     * prints a variable declaration statement
     */
    public void visitVariableDefinition(JVariableDefinition self,
                                        int modifiers,
                                        CType type,
                                        String ident,
                                        JExpression expr)
    {
       throw new RuntimeException();
    }
    
    /**
     * prints a try-catch statement
     */
    public void visitTryCatchStatement(JTryCatchStatement self,
                                       JBlock tryClause,
                                       JCatchClause[] catchClauses)
    {
      tryClause.accept(this);

      for (int i = 0; catchClauses != null && i < catchClauses.length; i++) {
        catchClauses[i].accept(this);
      }
    }
    
    /**
     * prints a try-finally statement
     */
    public void visitTryFinallyStatement(JTryFinallyStatement self,
                                         JBlock tryClause,
                                         JBlock finallyClause)
    {
      tryClause.accept(this);
      if (finallyClause != null) {
        finallyClause.accept(this);
      }
    }
    
    /**
     * prints a throw statement
     */
    public void visitThrowStatement(JThrowStatement self,
                                    JExpression expr)
    {
    }

    /**
     * prints a synchronized statement
     */
    public void visitSynchronizedStatement(JSynchronizedStatement self,
                                           JExpression cond,
                                           JStatement body)
    {
      body.accept(this);
    }

    /**
     * prints a switch statement
     */
    public void visitSwitchStatement(JSwitchStatement self,
                                     JExpression expr,
                                     JSwitchGroup[] body)
    {
      for (int i = 0; i < body.length; i++) {
        body[i].accept(this);
      }
    }

    /**
     * prints a return statement
     */
    public void visitReturnStatement(JReturnStatement self,
                                     JExpression expr)
    {
    }

    /**
     * prints a labeled statement
     */
    public void visitLabeledStatement(JLabeledStatement self,
                                      String label,
                                      JStatement stmt)
    {
      if (label.equals(this.label1)) {
        this.target1 = self;
      }
    }
    
    /**
     * prints a if statement
     */
    public void visitIfStatement(JIfStatement self,
                                 JExpression cond,
                                 JStatement thenClause,
                                 JStatement elseClause)
    {
      thenClause.accept(this);
      if (elseClause != null) {
        elseClause.accept(this);
      }
    }
    
    /**
     * prints a for statement
     */
    public void visitForStatement(JForStatement self,
                                  JStatement init,
                                  JExpression cond,
                                  JStatement incr,
                                  JStatement body)
    {
      body.accept(this);
    }
    
    /**
     * prints a compound statement
     */
    public void visitCompoundStatement(JCompoundStatement self,
                                       JStatement[] body)
    {
      visitCompoundStatement(body);
    }
    
    /**
     * prints a compound statement
     */
    public void visitCompoundStatement(JStatement[] body)
    {
      for (int i = 0; i < body.length; i++) {
        body[i].accept(this);
      }
    }
    
    /**
     * prints an expression statement
     */
    public void visitExpressionStatement(JExpressionStatement self,
                                         JExpression expr)
    {
    }

    /**
     * prints an expression list statement
     */
    public void visitExpressionListStatement(JExpressionListStatement self,
                                             JExpression[] expr)
    {
    }
    
    /**
     * prints a empty statement
     */
    public void visitEmptyStatement(JEmptyStatement self)
    {
    }

    /**
     * prints a do statement
     */
    public void visitDoStatement(JDoStatement self,
                                 JExpression cond,
                                 JStatement body)
    {
      body.accept(this);
    }

    /**
     * prints a continue statement
     */
    public void visitContinueStatement(JContinueStatement self,
                                       String label)
    {
    }
    
    /**
     * prints a break statement
     */
    public void visitBreakStatement(JBreakStatement self,
                                    String label)
    {
    }
    
    /**
     * prints an constructor body
     */
    public void visitConstructorBlockStatement(JBlock self,
                                               JExpression constructorCall,
                                               JStatement[] body,
                                               JavaStyleComment[] comments)
    {
      throw new RuntimeException();
    }

    /**
     * prints an expression statement
     */
    public void visitBlockStatement(JBlock self,
                                    JStatement[] body,
                                    JavaStyleComment[] comments)
    {
      visitCompoundStatement(body);
    }

    /**
     * prints a type declaration statement
     */
    public void visitTypeDeclarationStatement(JTypeDeclarationStatement self,
                                              JTypeDeclaration decl)
    {
    }
    
    /**
     * prints an assert statement
     */
    public void visitAssertStatement(KopiAssertStatement self,
                                     JExpression cond,
                                     JExpression expr)
    {
      throw new RuntimeException();
    }
    
    /**
     * prints a fail statement
     */
    public void visitFailStatement(KopiFailStatement self,
                                   JExpression expr)
    {
    }

    // ----------------------------------------------------------------------
    // EXPRESSION
    // ----------------------------------------------------------------------
    
    /**
     * prints an unary plus expression
     */
    public void visitUnaryPlusExpression(JUnaryExpression self,
                                         JExpression expr)
    {
      throw new RuntimeException();
    }
    
    /**
     * prints an unary minus expression
     */
    public void visitUnaryMinusExpression(JUnaryExpression self,
                                          JExpression expr)
    {
    }
    
    /**
     * prints a bitwise complement expression
     */
    public void visitBitwiseComplementExpression(JUnaryExpression self,
                                                 JExpression expr)
    {
    }
    
    /**
     * prints a logical complement expression
     */
    public void visitLogicalComplementExpression(JUnaryExpression self,
                                                 JExpression expr)
    {
    }
    
    /**
     * prints a type name expression
     */
    public void visitTypeNameExpression(JTypeNameExpression self,
                                        CType type)
    {
    }
    
    /**
     * prints a this expression
     */
    public void visitThisExpression(JThisExpression self,
                                    JExpression prefix)
    {
    }
    
    /**
     * prints a super expression
     */
    public void visitSuperExpression(JSuperExpression self)
    {
    }
    
    /**
     * prints a shift expression
     */
    public void visitShiftExpression(JShiftExpression self,
                                     int oper,
                                     JExpression left,
                                     JExpression right)
    {
    }
    
    /**
     * prints a shift expressiona
     */
    public void visitRelationalExpression(JRelationalExpression self,
                                          int oper,
                                          JExpression left,
                                          JExpression right)
    {
    }
    
    /**
     * prints a prefix expression
     */
    public void visitPrefixExpression(JPrefixExpression self,
                                      int oper,
                                      JExpression expr)
    {
    }
    
    /**
     * prints a postfix expression
     */
    public void visitPostfixExpression(JPostfixExpression self,
                                       int oper,
                                       JExpression expr)
    {
    }
    
    /**
     * prints a parenthesed expression
     */
    public void visitParenthesedExpression(JParenthesedExpression self,
                                           JExpression expr)
    {
    }
    
    /**
     * Prints an unqualified anonymous class instance creation expression.
     */
    public void visitQualifiedAnonymousCreation(JQualifiedAnonymousCreation self,
                                                JExpression prefix,
                                                String ident,
                                                JExpression[] params,
                                                JClassDeclaration decl)
    {
    }
    
    /**
     * Prints an unqualified instance creation expression.
     */
    public void visitQualifiedInstanceCreation(JQualifiedInstanceCreation self,
                                               JExpression prefix,
                                               String ident,
                                               JExpression[] params)
    {
    }
    
    /**
     * Prints an unqualified anonymous class instance creation expression.
     */
    public void visitUnqualifiedAnonymousCreation(JUnqualifiedAnonymousCreation self,
                                                  CReferenceType type,
                                                  JExpression[] params,
                                                  JClassDeclaration decl)
    {
    }
    
    /**
     * Prints an unqualified instance creation expression.
     */
    public void visitUnqualifiedInstanceCreation(JUnqualifiedInstanceCreation self,
                                                 CReferenceType type,
                                                 JExpression[] params)
    {
    }
    
    /**
     * prints an array allocator expression
     */
    public void visitNewArrayExpression(JNewArrayExpression self,
                                        CType type,
                                        JExpression[] dims,
                                        JArrayInitializer init)
    {
    }
    
    /**
     * prints a name expression
     */
    public void visitNameExpression(JNameExpression self,
                                    JExpression prefix,
                                    String ident)
    {
    }
    
    /**
     * prints an array allocator expression
     */
    public void visitBinaryExpression(JBinaryExpression self,
                                      String oper,
                                      JExpression left,
                                      JExpression right)
    {
    }
    
    /**
     * prints a method call expression
     */
    public void visitMethodCallExpression(JMethodCallExpression self,
                                          JExpression prefix,
                                          String ident,
                                          JExpression[] args)
    {
    }
    
    /**
     * prints a local variable expression
     */
    public void visitLocalVariableExpression(JLocalVariableExpression self,
                                             String ident)
    {
    }
    
    /**
     * prints an instanceof expression
     */
    public void visitInstanceofExpression(JInstanceofExpression self,
                                          JExpression expr,
                                          CType dest)
    {
    }
    
    /**
     * prints an equality expression
     */
    public void visitEqualityExpression(JEqualityExpression self,
                                        boolean equal,
                                        JExpression left,
                                        JExpression right)
    {
    }
    
    /**
     * prints a conditional expression
     */
    public void visitConditionalExpression(JConditionalExpression self,
                                           JExpression cond,
                                           JExpression left,
                                           JExpression right)
    {
    }
    
    /**
     * prints a compound expression
     */
    public void visitCompoundAssignmentExpression(JCompoundAssignmentExpression self,
                                                  int oper,
                                                  JExpression left,
                                                  JExpression right)
    {
    }
    
    /**
     * prints a field expression
     */
    public void visitFieldExpression(JFieldAccessExpression self,
                                     JExpression left,
                                     String ident)
    {
    }
    
    /**
     * prints a class expression
     */
    public void visitClassExpression(JClassExpression self,
                                     CType type,
                                     JExpression prefix,
                                     int bounds)
    {
    }
    
    /**
     * prints a cast expression
     */
    public void visitCastExpression(JCastExpression self,
                                    JExpression expr,
                                    CType type)
    {
    }
    
    /**
     * prints a cast expression
     */
    public void visitUnaryPromoteExpression(JUnaryPromote self,
                                            JExpression expr,
                                            CType type)
    {
    }
    
    /**
     * prints a compound assignment expression
     */
    public void visitBitwiseExpression(JBitwiseExpression self,
                                       int oper,
                                       JExpression left,
                                       JExpression right)
    {
    }
    
    /**
     * prints an assignment expression
     */
    public void visitAssignmentExpression(JAssignmentExpression self,
                                          JExpression left,
                                          JExpression right)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitArrayLengthExpression(JArrayLengthExpression self,
                                           JExpression prefix)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitArrayAccessExpression(JArrayAccessExpression self,
                                           JExpression prefix,
                                           JExpression accessor)
    {
    }
    
    /**
     * Hint for the object comming
     */
    public void hintOnCommingObject(int objectType) {
    }
    
    public void printCommentsIfAny(JStatement object) {
    }
    
    /**
     * prints an array length expression
     */
    public void visitComments(JavaStyleComment[] comments)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitComment(JavaStyleComment comment)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitJavadoc(JavadocComment comment)
    {
    }
    
    // ----------------------------------------------------------------------
    // UTILS
    // ----------------------------------------------------------------------
    
    /**
     * prints an array length expression
     */
    public void visitSwitchLabel(JSwitchLabel self,
                                 JExpression expr)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitSwitchGroup(JSwitchGroup self,
                                 JSwitchLabel[] labels,
                                 JStatement[] stmts)
    {
      for (int i = 0; i < stmts.length; i++) {
        stmts[i].accept(this);
      }
    }
    
    /**
     * prints an array length expression
     */
    public void visitCatchClause(JCatchClause self,
                                 JFormalParameter exception,
                                 JBlock body)
    {
      body.accept(this);
    }
    
    /**
     * prints a boolean literal
     */
    public void visitBooleanLiteral(boolean value)
    {
    }
    
    /**
     * prints a byte literal
     */
    public void visitByteLiteral(byte value)
    {
    }
    
    /**
     * prints a character literal
     */
    public void visitCharLiteral(char value)
    {
    }
    
    /**
     * prints a double literal
     */
    public void visitDoubleLiteral(double value)
    {
    }
    
    /**
     * prints a float literal
     */
    public void visitFloatLiteral(float value)
    {
    }
    
    /**
     * prints a int literal
     */
    public void visitIntLiteral(int value)
    {
    }
    
    /**
     * prints a long literal
     */
    public void visitLongLiteral(long value)
    {
    }
    
    /**
     * prints a short literal
     */
    public void visitShortLiteral(short value)
    {
    }
    
    /**
     * prints a string literal
     */
    public void visitStringLiteral(String value)
    {
    }
    
    /**
     * prints a null literal
     */
    public void visitNullLiteral() {
    }
    
    /**
     * prints an array length expression
     */
    public void visitPackageName(String name)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitPackageImport(String name)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitClassImport(String name)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitFormalParameters(JFormalParameter self,
                                      boolean isFinal,
                                      CType type,
                                      String ident)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitArgs(JExpression[] args)
    {
    }
    
    /**
     * prints an array length expression
     */
    public void visitConstructorCall(JConstructorCall self,
                                     boolean functorIsThis,
                                     JExpression[] params)
    {
    }
    
    /**
     * prints an array initializer expression
     */
    public void visitArrayInitializer(JArrayInitializer self,
                                      JExpression[] elems)
    {
    }    
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private String                label;
  private JStatement		target;
}
