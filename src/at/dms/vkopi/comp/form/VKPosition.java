/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: VKPosition.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.comp.form;

import java.awt.Point;
import at.dms.vkopi.comp.base.VKPhylum;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPrettyPrinter;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * A position within a block
 */
public abstract class VKPosition extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   */
  protected VKPosition(TokenReference where) {
    super(where);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract void checkCode(VKContext context, VKBlock block) throws PositionedError;

  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point		the current bottomRight point
   */
  public abstract void checkBR(Point point, VKField field);

  /**
   * checks that there is no other field at the same position
   *
   * @param freePositions	true at those places wich are free
   * @param name                the name of this field
   */
  public abstract void checkPlace(VKContext context, VKField field, String[][] freePositions) throws PositionedError ;

  // ----------------------------------------------------------------------
  // Position method
  // ----------------------------------------------------------------------

  public abstract int getLine();
  public abstract int getColumn();
  public abstract int getColumnEnd();

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract JExpression genCode();

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public abstract void genVKCode(VKPrettyPrinter p);
}
