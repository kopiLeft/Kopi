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
 * This class represents a local variable definition in the syntax tree
 */
public class XVariableDefinition extends JVariableDefinition {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	ident		the name of this variable
   * @param	initializer	the initializer
   */
  public XVariableDefinition(TokenReference where,
			     int modifiers,
			     CType type,
			     String ident,
			     JExpression initializer)
  {
    super(where, modifiers, type, ident, initializer);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
 TypeFactory     factory = context.getTypeFactory();

    try {
      type = type.checkType(context);
    } catch (UnpositionedError cue) {
      throw cue.addPosition(getTokenReference());
    }

    if (expr != null) {
      // special case for array initializers
      if (expr instanceof JArrayInitializer) {
	check(context, type.isArrayType(), KjcMessages.ARRAY_INIT_NOARRAY, type);
	((JArrayInitializer)expr).setType((CArrayType)type);
      }

      expr = expr.analyse(new CExpressionContext(context, context.getEnvironment()));
      if (expr.isAssignableTo(context, type)) {
	expr = expr.convertType(new CExpressionContext(context, context.getEnvironment()), type);
      } else if (! (expr instanceof JArrayInitializer)) {
	JExpression	left = null;
       
	if (getType().isReference()) {
	  left = new JCastExpression(getTokenReference(),
				     new JNullLiteral(getTokenReference()),
				     getType());
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_BYTE)) {
	  left = new JByteLiteral(getTokenReference(), (byte)0);
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_SHORT)) {
	  left = new JShortLiteral(getTokenReference(), (short)0);
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_CHAR)) {
	  left = new JCharLiteral(getTokenReference(), (char)0);
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_INT)) {
	  left = new JIntLiteral(getTokenReference(), (int)0);
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_LONG)) {
	  left = new JLongLiteral(getTokenReference(), (long)0);
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_FLOAT)) {
	  left = new JFloatLiteral(getTokenReference(), (float)0);
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_DOUBLE)) {
	  left = new JDoubleLiteral(getTokenReference(),(double)0);
	} else if (getType() == factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN)) {
	  left = new JBooleanLiteral(getTokenReference(), true);
	} else {
	  check(context, false, KjcMessages.ASSIGNMENT_BADTYPE, getType(), getType());
	}

	XOverloadedMethodCallExpression	overload;

	overload = new XOverloadedMethodCallExpression(getTokenReference(),
						       null,
						       "operator$" + Constants.OPE_SIMPLE,
						       new JExpression[]{ left, expr });

	try {
	  JExpression	convert = XUtils.fetchOverloadedOperator(new CExpressionContext(context, context.getEnvironment()), overload);

	  if (convert == null) {
	    check(context, convert != null, KjcMessages.VAR_INIT_BADTYPE, getIdent(), expr.getType(factory));
	  } else {
	    expr = convert.analyse(new CExpressionContext(context, context.getEnvironment()));
            // fix lackner 011220
            // the variable left should guarantee that this condition is true
            // why not: parameter are contravariant, return types covariant!
            check(context,expr.isAssignableTo(context, type) , KjcMessages.VAR_INIT_BADTYPE, getIdent(), expr.getType(factory));
            // fix end
	  }
	} catch (PositionedError ce) {
	  check(context, false, KjcMessages.VAR_INIT_BADTYPE, getIdent(), expr.getType(factory));
	}
      } else {
	check(context, false, KjcMessages.VAR_INIT_BADTYPE, getIdent(), expr.getType(factory));
      }
    }
  }
}
