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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * A 'this' expression
 */
public class JThisExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JThisExpression(TokenReference where) {
    super(where);
  }

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	self		the class onto this suffix is applied
   */
  public JThisExpression(TokenReference where, CClass self) {
    this(where);
    this.self = self;
  }

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source codegetTokenReference()
   * @param	prefix		the left expression like t.this
   */
  public JThisExpression(TokenReference where, JExpression prefix) {
    this(where);
    this.prefix = prefix;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return self.getAbstractType(); // !! FIXIFGEN
  }

  /**
   * @return is this expression a lvalue ?
   */
  public boolean isLValue(CExpressionContext context) {
    return false;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(final CExpressionContext context) throws PositionedError {
    TypeFactory       factory = context.getTypeFactory();
      
    if (prefix != null) {
      CExpressionContext        exprContext = new CExpressionContext(context, context.getEnvironment());

      exprContext.setIsTypeName(true);
      prefix = prefix.analyse(exprContext);
      check(context, prefix.getType(factory).isClassType(), KjcMessages.THIS_BADACCESS);
      self = prefix.getType(factory).getCClass();
    } else if (self == null) {
      self = context.getClassContext().getCClass();
    }
    CClass      clazz = context.getClassContext().getCClass();

    // JLS 15.8.4
    // Any lexically enclosing instance can be referred to by explicitly 
    // qualifying the keyword this.
    //
    //  Let C be the class denoted by ClassName. Let n be an integer such 
    // that C is the nth lexically enclosing class of the class in which 
    // the qualified this expression appears. The value of an expression 
    // of the form ClassName.this is the nth lexically enclosing instance 
    // of this (§8.1.2). The type of the expression is C. It is a 
    // compile-time error if the current class is not an inner class of 
    // class C or C itself.

    check(context,
          clazz.isDefinedInside(self),
          KjcMessages.THIS_INVALID_OUTER);

    if (clazz.isDefinedInside(self) && (clazz != self)) { 
            //!clazz.descendsFrom(self)) {//) {//!context.getClassContext().getCClass().descendsFrom(self)) {
      // access to outer class
      JExpression	expr = null;
      CClassContext	classContext = context.getClassContext();
      // this local variable is used for anonymous classes with an inner class as superclass
      boolean           callInnerSuper = context.getMethodContext() instanceof CConstructorContext 
        && !((CConstructorContext)context.getMethodContext()).isSuperConstructorCalled();
      boolean           first = true;

    while (!clazz.descendsFrom(self) || first || callInnerSuper) {
	check(context,
	      !classContext.getTypeDeclaration().getCClass().isStatic(),
	      KjcMessages.THIS_INVALID_OUTER);
        //	classContext.getTypeDeclaration().addOuterThis();
	classContext = classContext.getParentContext().getClassContext();
        first = false;

	if (expr == null) {
	  if (context.getMethodContext() instanceof CConstructorContext) {
            callInnerSuper = false;

            // this is an synthecial generated parameter of the constructor
	    JGeneratedLocalVariable local = new JGeneratedLocalVariable(null, 0, clazz.getOwner().getAbstractType(), clazz.getOwner().getQualifiedName().replace('/','.')+".this", null) {
		/**
		 * @return the local index in context variable table
		 */
		public int getPosition() {
                  //		  return context.getMethodContext().getCMethod().getParameters().length + 1 /*this*/;
		  return 1 /*this*/;
		}
	      };

	    expr = new JLocalVariableExpression(getTokenReference(), local) {
		public JExpression analyse(CExpressionContext ctxt) {
		  // already checked
		  return this;
		}
	      };
	  } else {
	    expr = new JFieldAccessExpression(getTokenReference(), new JThisExpression(getTokenReference()), JAV_OUTER_THIS);
	  }
	} else {
	  expr = new JFieldAccessExpression(getTokenReference(), expr, JAV_OUTER_THIS);
	}

	expr = expr.analyse(context);
	clazz = ((CReferenceType)expr.getType(factory)).getCClass();
      }

      if (prefix != null) {
	check(context, expr.getType(factory).equals(prefix.getType(factory)) ||
	      /*May be it is an innerclass with the same name, therefore the prefix name has been
	       wrongly assigned to the outer. So we compare the names instead:*/
	      ((CReferenceType)expr.getType(factory)).getCClass().getIdent().equals(((CReferenceType)prefix.getType(factory)).getCClass().getIdent()),
	      KjcMessages.THIS_INVALID_OUTER, prefix.getType(factory));
      }
      return expr;
    }
    check(context, !context.getMethodContext().getCMethod().isStatic(), KjcMessages.BAD_THIS_STATIC);

    return this;
  }

  public boolean equals(Object o) {
    return o instanceof JThisExpression &&
      self.equals(((JThisExpression)o).self);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitThisExpression(this, prefix);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    if (! discardValue) {
      setLineNumber(code);
      code.plantLoadThis();
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected CClass		self;
  protected JExpression		prefix;
}
