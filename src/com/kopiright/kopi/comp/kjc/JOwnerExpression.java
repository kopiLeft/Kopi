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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * A 'this' expression
 */
public class JOwnerExpression extends JThisExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	self		the class onto this suffix is applied
   */
  public JOwnerExpression(TokenReference where, CClass self) {
    super(where, self);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

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
    TypeFactory         factory = context.getTypeFactory();

    if (prefix != null) {
      prefix = prefix.analyse(context);
      check(context, prefix.getType(factory).isClassType(), KjcMessages.THIS_BADACCESS);
      self = prefix.getType(factory).getCClass();
    } else if (self == null) {
      self = context.getClassContext().getCClass();
    }
    CClass      clazz = context.getClassContext().getCClass();

    if (!clazz.descendsFrom(self)) {//clazz.isDefinedInside(self) && (clazz != self)) {//!context.getClassContext().getCClass().descendsFrom(self)) {
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
	    JGeneratedLocalVariable local = new JGeneratedLocalVariable(null, 0, clazz.getOwner().getAbstractType(), "toto", null) {
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


  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------


  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
}
