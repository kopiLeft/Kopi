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
import com.kopiright.compiler.base.UnpositionedError;

/**
 * This class represents a local variable definition in the syntax tree
 */
public class JVariableDefinition extends JLocalVariable {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	modifiers	the modifiers of this variable
   * @param	ident		the name of this variable
   * @param	initializer	the initializer
   */
  public JVariableDefinition(TokenReference where,
			     int modifiers,
			     CType type,
			     String ident,
			     JExpression initializer)
  {
    super(where, modifiers, DES_LOCAL_VAR, type, ident, initializer);
    verify(type != null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * hasInitializer
   * @return	true if there is an initializer
   */
  public boolean hasInitializer() {
    return getValue() != null;
  }

  /**
   * @return	the initial value
   */
  public JExpression getValue() {
    return expr;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @param	context		the current context
   * @return	true iff sub tree is correct enought to check code
   */
  public void checkInterface(CClassContext context) {
    try {
      type = type.checkType(context);
    } catch (UnpositionedError cue) {
      /*checkbody will do it*/
    }
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param	context			the actual context of analyse
   * @exception	PositionedError Error catched as soon as possible
   */
  public void analyse(CBodyContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    try {
      type = type.checkType(context);
    } catch (UnpositionedError cue) {
      throw cue.addPosition(getTokenReference());
    }
    if (! type.isPrimitive()) {
      // JLS 6.6.1
      // An array type is accessible if and only if its element type is accessible. 
      if (type.isArrayType()) {
        check(context,
              ((CArrayType)type).getBaseType().isPrimitive() 
              || ((CArrayType)type).getBaseType().getCClass().isAccessible(context.getClassContext().getCClass()),
              KjcMessages.CLASS_NOACCESS, ((CArrayType)type).getBaseType());        
      }

      check(context,
	    type.getCClass().isAccessible(context.getClassContext().getCClass()),
	    KjcMessages.CLASS_NOACCESS, type);
    }

    if (expr != null) {
      // special case for array initializers
      if (expr instanceof JArrayInitializer) {
	check(context, type.isArrayType(), KjcMessages.ARRAY_INIT_NOARRAY, type);
	((JArrayInitializer)expr).setType((CArrayType)type);
      }

      CExpressionContext	expressionContext = new CExpressionContext(context, context.getEnvironment());

      expr = expr.analyse(expressionContext);
      if (expr instanceof JTypeNameExpression) {
	check(context,
	      false,
	      KjcMessages.VAR_UNKNOWN,
	      ((JTypeNameExpression)expr).getQualifiedName());
      }

      check(context,
	    expr.isAssignableTo(context, type),
	    KjcMessages.VAR_INIT_BADTYPE, getIdent(), expr.getType(factory));
      expr = expr.convertType(expressionContext, type);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitVariableDefinition(this, modifiers, type, getIdent(), expr);
  }
}
