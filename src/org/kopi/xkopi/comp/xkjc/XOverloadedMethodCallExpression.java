/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.xkopi.comp.xkjc;

import org.kopi.kopi.comp.kjc.*;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;

/**
 * This class represents methods call
 * <pre> methodname(explist) </pre>
 * the only difference with parent is that method can be checked a lot of time efficiently
 */
public class XOverloadedMethodCallExpression extends JMethodCallExpression {

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	prefix		an expression that is a field of a class representing a method
   * @param	ident		the method identifier
   * @param	args		the argument of the call
   */
  public XOverloadedMethodCallExpression(TokenReference where,
					 JExpression prefix,
					 String ident,
					 JExpression[] args)
  {
    super(where, prefix, ident, args);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param context the actual context of analyse
   * @return  a pure java expression including promote node
   * @exception	PositionedError	an error with reference to the source file
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void preCheckExpression(CExpressionContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    argsType = new CType[args.length];
    for (int i = 0; i < args.length; i++) {
      args[i] = args[i].analyse(context);
      argsType[i] = args[i].getType(factory);
    }

    verify(ident != null);

    if (prefix != null) {
      prefix = prefix.analyse(context);
      check(context, prefix.getType(factory).isReference(),
	    KjcMessages.METHOD_BADPREFIX, ident, prefix.getType(factory));
    }
  }

  /**
   * Set a checked prefix for this expression (after preCheck)
   */
  public void setPrefix(JExpression prefix) {
    this.prefix = prefix;
  }

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    CClass		local = context.getClassContext().getCClass();
    TypeFactory         factory = context.getTypeFactory();

    if (prefix != null) {
      try {
	method = prefix.getType(factory).getCClass().lookupMethod(context, 
                                                                  local, 
                                                                  prefix.getType(factory), 
                                                                  ident, 
                                                                  argsType,
                                                                  prefix.getType(factory).getArguments());
      } catch (UnpositionedError e) {
	throw e.addPosition(getTokenReference());
      }
    } else {
      try {
	method = context.lookupMethod(context, local, null, ident, argsType);
      } catch (UnpositionedError e) {
	throw e.addPosition(getTokenReference());
      }
    }

    check(context, method != null, KjcMessages.METHOD_NOTFOUND, ident);


    CReferenceType[]	exceptions = method.getThrowables();
    for (int i = 0; i < exceptions.length; i++) {
      if (exceptions[i].isCheckedException(context)) {
	if (prefix == null				// special case of clone
	    || !prefix.getType(factory).isArrayType()
	    || !ident.equals("clone")
	    || !exceptions[i].getCClass().getQualifiedName().equals("java/lang/CloneNotSupportedException")) {
	  context.getBodyContext().addThrowable(new CThrowableInfo(exceptions[i], this));
	}
      }
    }

    if ((prefix == null) && !method.isStatic()) {
      prefix = new JThisExpression(getTokenReference()).analyse(context);
    }

    argsType = method.getParameters();
    for (int i = 0; i < args.length; i++) {
      args[i] = args[i].convertType(context, argsType[i]);
    }

    return this;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	CType[]	argsType;
}
