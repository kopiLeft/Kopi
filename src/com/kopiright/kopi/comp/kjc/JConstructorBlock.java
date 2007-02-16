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
 * This class represents the body of a constructor.
 */
public class JConstructorBlock extends JBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	constructorCall	an explicit constructor invocation
   * @param	body		the statements contained in the block
   */
  public JConstructorBlock(TokenReference where,
			   JConstructorCall constructorCall,
			   JStatement[] body)
  {
    super(where, body, null);
    this.constructorCall = constructorCall;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the constructor called by this constructor.
   */
  public CMethod getCalledConstructor() {
    return constructorCall == null ? null : constructorCall.getMethod();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the constructor body (semantically).
   *
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    sourceClass = (CSourceClass)context.getClassContext().getCClass();

    // JLS 8.8.5 :
    // If a constructor body does not begin with an explicit constructor
    // invocation and the constructor being declared is not part of the
    // primordial class Object, then the constructor body is implicitly
    // assumed by the compiler to begin with a superclass constructor
    // invocation "super();", an invocation of the constructor of its
    // direct superclass that takes no arguments.
   

    if (constructorCall == null && !(sourceClass.getQualifiedName() == JAV_OBJECT)) {
      constructorCall = new JConstructorCall(getTokenReference(),
					     false,
					     JExpression.EMPTY);
    }

    if (sourceClass.isNested()) {
      paramsLength = context.getMethodContext().getCMethod().getParameters().length;
    }

    // Insert a call to the instance initializer, iff :
    // - there exists an instance initializer
    // - there is no explicit invocation of a constructor of this class
    if (! context.getClassContext().hasInitializer()
	|| constructorCall == null
	|| constructorCall.isThisInvoke()) {
      initializerCall = null;
    } else {
      // "Block$();"
      initializerCall =
	new JExpressionStatement(getTokenReference(),
				 new JMethodCallExpression(getTokenReference(),
							   null,
							   JAV_INIT,
							   JExpression.EMPTY),
				 null);
    }

    if (constructorCall != null) {
      constructorCall.analyse(new CExpressionContext(context, context.getEnvironment()));
      if (constructorCall.isThisInvoke()) {
	((CConstructorContext)context.getMethodContext()).markAllFieldToInitialized();
      }
    }


    if (initializerCall != null) {
      initializerCall.analyse(context);
      ((CConstructorContext)context.getMethodContext()).adoptInitializerInfo();
    }

    super.analyse(context);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitConstructorBlockStatement(this, constructorCall, body, getComments());
  }

   /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    if (constructorCall != null) {
      constructorCall.genCode(context, true);
    }

    if (sourceClass.isNested()) {
      sourceClass.genInit(context, paramsLength);
    }

    if (initializerCall != null) {
      initializerCall.genCode(context);
    }

    for (int i = 0; i < body.length; i++) {
      body[i].genCode(context);
    }

    //!!! graf 010529 : needed ?
    //    code.plantNoArgInstruction(opc_return);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JConstructorCall		constructorCall;
  private JStatement			initializerCall;
  private CSourceClass			sourceClass;
  private int				paramsLength;
}
