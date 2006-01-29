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

package com.kopiright.vkopi.comp.form;

import java.awt.Point;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;
import com.kopiright.vkopi.comp.base.VKUtils;
import com.kopiright.vkopi.comp.base.VKStdType;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.TokenReference;

/**
 * A position within a block given by x and y location
 */
public class VKMultiFieldPosition extends VKPosition {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param line		the line
   * @param column		the column
   * @param end			the last column onto this field may be placed
   */
  public VKMultiFieldPosition(TokenReference where, int line) {
    super(where);

    this.line = line;
    this.column = 1;
  }

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param line		the line
   * @param column		the column
   * @param end			the last column onto this field may be placed
   */
  VKMultiFieldPosition(TokenReference where, int line, int column) {
    super(where);

    this.line = line;
    this.column = column;
  }

  public void translate(int amount) {
    column += amount;
  }

  public Object clone() {
    return new VKMultiFieldPosition(getTokenReference(), line, column);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKBlock block) {
    // !!! BEEP check field is multifield
  }

  /**
   * Return the line pos
   */
  public int getLine() {
    return line;
  }

  /**
   * Return the column pos
   */
  public int getColumn() {
    return -1;
  }

  /**
   * Return the column end pos
   */
  public int getColumnEnd() {
    return column;
  }
  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point		the current bottomRight point
   */
  public void checkBR(Point point, VKField field) {
    point.x = Math.max(point.x, column);
    point.y = Math.max(point.y, line + field.getFieldType().getDef().getHeight());
  }

  /**
   * checks that there is no other field at the same position
   *
   * @param freePositions	true at those places wich are free
   * @param name                the name of this field
   */
  public void checkPlace(VKContext context, VKField field, String[][] freePositions) {
    if (freePositions[column][line] != null) {
      context.reportTrouble(new CWarning(getTokenReference(),
                                         FormMessages.POSITION_USED,
                                         new Object[]{"" + line, "" + column, freePositions[column][line]}));
    }
    freePositions[column][line] = field.getIdent();
  }
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode() {
    TokenReference	ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
				    VKStdType.VPosition,
				    new JExpression[] {
				      VKUtils.toExpression(ref, line),
				      VKUtils.toExpression(ref, -1),
				      VKUtils.toExpression(ref, column)
				    });
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    //p.printCoordinatePosition(line, column, columnEnd);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int		line;
  private int		column;
}
