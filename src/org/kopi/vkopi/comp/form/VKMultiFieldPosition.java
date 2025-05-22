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

package org.kopi.vkopi.comp.form;

import java.awt.Point;

import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKStdType;
import org.kopi.vkopi.comp.base.VKUtils;
import org.kopi.vkopi.comp.base.VKVisitor;

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
  public VKMultiFieldPosition(TokenReference where, int line, int endLine) {
    super(where);

    this.line = line;
    this.endLine = endLine == 0 ? line : endLine;;
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
  VKMultiFieldPosition(TokenReference where, int line, int endLine, int column) {
    super(where);

    this.line = line;
    this.endLine = endLine == 0 ? line : endLine;
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
   * Return the line end pos
   */
  public int getLineEnd() {
    return endLine;
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
				      VKUtils.toExpression(ref, endLine),
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
  // Galite CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param visitor the visitor
   */
  @Override
  public void accept(VKVisitor visitor) {}

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int		line;
  private int           endLine;
  private int		column;
}
