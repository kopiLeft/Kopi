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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.MessageDescription;

/**
 * This class represents a parameter declaration in the syntax tree
 */
public class JSwitchLabel extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	expr		the expression (null if default label)
   */
  public JSwitchLabel(TokenReference where, JExpression expr) {
    super(where);

    this.expr = expr;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return	true if this label is a "default:" label
   */
  public boolean isDefault() {
    return expr == null;
  }

  /**
   * @return    the value of this label
   */
  public Integer getLabel(TypeFactory factory) {
    return new Integer(getLabelValue(factory));
  }
  
   /**
   * @return    the expression of this label
   */
  public JExpression getExpression() {
    return expr;
  }

  public void setExpression(JExpression expr) {
      this.expr = expr;
  }
  
  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CSwitchGroupContext context, boolean isEnum) throws PositionedError
  {
    TypeFactory factory = context.getTypeFactory();
    
    if (expr != null) {
      if (!isEnum) {
        expr = expr.analyse(new CExpressionContext(context, context.getEnvironment()));
        check(context, expr.isConstant(), KjcMessages.SWITCH_LABEL_EXPR_NOTCONST);
        check(context,
              expr.isAssignableTo(context, context.getType()),
              KjcMessages.SWITCH_LABEL_OVERFLOW, expr.getType(factory));
      } else {
        expr = new JIntLiteral(getTokenReference(), 
                               context.getInnerEnumMap().addMapField((JNameExpression)expr, context));
        expr.analyse(new CExpressionContext(context, context.getEnvironment()));
      }
      try {
        context.addLabel(new Integer(getLabelValue(factory)));
      } catch (UnpositionedError e) {
        throw e.addPosition(getTokenReference());
      }
    } else {
      try {
        context.addDefault();
      } catch (UnpositionedError e) {
        throw e.addPosition(getTokenReference());
      }
    }
  }

  /**
   * Adds a compiler error.
   * @param	context		the context in which the error occurred
   * @param	key		the message ident to be displayed
   * @param	params		the array of parameters
   *
   */
  protected void fail(CContext context, MessageDescription key, Object[] params)
    throws PositionedError
  {
    throw new CLineError(getTokenReference(), key, params);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitSwitchLabel(this, expr);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  private int getLabelValue(TypeFactory factory) {
    CType	type = expr.getType(factory);

    if (type == factory.getPrimitiveType(TypeFactory.PRM_BYTE)) {
      return expr.byteValue();
    } else if (type == factory.getPrimitiveType(TypeFactory.PRM_CHAR)) {
      return expr.charValue();
    } else if (type == factory.getPrimitiveType(TypeFactory.PRM_SHORT)) {
      return expr.shortValue();
    } else if (type == factory.getPrimitiveType(TypeFactory.PRM_INT)) {
      return expr.intValue();
        } else { 
            throw new InconsistencyException("unexpected type " + type);
        }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		expr;
}
