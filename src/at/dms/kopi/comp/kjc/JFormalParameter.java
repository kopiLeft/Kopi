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

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents a parameter declaration in the syntax tree
 */
public class JFormalParameter extends JLocalVariable {

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
  public JFormalParameter(TokenReference where,
			  int desc,
			  CType type,
			  String ident,
			  boolean isFinal) {
    super(where, isFinal ? ACC_FINAL : 0, desc, type, ident, null);

    verify(type != null);
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * sub classes must check modifiers and call checkInterface(super)
   * @return true iff sub tree is correct enought to check code
   */
  public CType checkInterface(CTypeContext context) {
    try {
      type = type.checkType(context);
      return type;
    } catch (UnpositionedError cue) {
      context.reportTrouble(cue.addPosition(getTokenReference()));
      return context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT);
    }
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
    try {
      type = type.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    try {
      context.getBlockContext().addVariable(this);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    context.setVariableInfo(getIndex(), CVariableInfo.INITIALIZED);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitFormalParameters(this, isFinal(), getType(), getIdent());
  }

  // ----------------------------------------------------------------------
  // PUBLIC CONSTANTS
  // ----------------------------------------------------------------------

  public static final JFormalParameter[]	EMPTY = new JFormalParameter[0];
}
