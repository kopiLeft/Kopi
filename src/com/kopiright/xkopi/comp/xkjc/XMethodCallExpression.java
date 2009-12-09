/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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
import com.kopiright.compiler.base.UnpositionedError;

/**
 * This class represents methods call
 * <pre> methodname(explist) </pre>
 */
public class XMethodCallExpression extends JMethodCallExpression {

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	prefix		an expression that is a field of a class representing a method
   * @param	ident		the method ident
   * @param	args		the argument of the call
   */
  public XMethodCallExpression(TokenReference where,
			       JExpression prefix,
			       String ident,
			       JExpression[] args)
  {
    super(where, prefix, ident, args);
    if (prefix instanceof XNameExpression) {
      ((XNameExpression)prefix).setHasSuffix(false);
    }
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
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    // TRY JAVA-MATCH
    try {
      return super.analyse(context);
    } catch (CMethodNotFoundError ce) {
      // CHECK OVERLOADED
      if (prefix instanceof JNameExpression ||
	  ident == null ||
	  ce.getCaller() != this) {
	// problem in params
	throw ce;
      }

      // CONSTRUCT TYPE INTERFACE
      CType[]	argsType = new CType[args.length];
      for (int i = 0; i < argsType.length; i++) {
	argsType[i] = new XMutableType(args[i].getType(factory));
      }

      CClass		local = context.getClassContext().getCClass();

      if (prefix != null) {
	if (prefix.getType(factory) == null) {
	  throw ce;
	}
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

      if (method == null) {
      	throw ce;
      }

      // check access
      CClass		access = method.getOwner();

      if ((prefix == null) && !method.isStatic()) {
	if (access == local) {
	  prefix = new JThisExpression(getTokenReference()).analyse(context);
	} else {
	  prefix = new JOwnerExpression(getTokenReference(), access).analyse(context);
	}
      }

      TokenReference ref = getTokenReference();
      argsType = method.getParameters();
      for (int i = 0; i < argsType.length; i++) {
	args[i] = new XCastExpression(ref, args[i], argsType[i], true).analyse(context);
      }

      return this.analyse(context);
    }
  }
}
