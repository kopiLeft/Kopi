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

package com.kopiright.vkopi.comp.form;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.comp.base.VKPhylum;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;

import java.awt.Point;
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
  // ACCESSORS
  // ----------------------------------------------------------------------
  
  public void setChartPosition(int chartPos) {
    // this method should not be called
    throw new InconsistencyException("positionField(VKPosition) should not be called from here !!!");
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
