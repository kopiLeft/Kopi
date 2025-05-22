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
public class VKCoordinatePosition extends VKPosition {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param line		the line
   * @param column		the column
   * @param endColumn           the last column onto this field may be placed
   * @param endLine             the last line onto this field may be placed
   */
  public VKCoordinatePosition(TokenReference where, int line, int endLine, int column, int endColumn, int chartPos) {
    super(where);
    this.line = line;
    this.lineEnd = endLine == 0 ? line : endLine;
    this.column = column;
    this.columnEnd = endColumn == 0 ? column : endColumn;
    this.chartPos = chartPos;
  }
  public VKCoordinatePosition(TokenReference where, int line, int endLine, int column, int endColumn) {
    this(where, line, endLine, column, endColumn, -1);
    
  }
  public VKCoordinatePosition(TokenReference where, int chartPos) {
    this(where, -1, -1, -1, -1, chartPos);
  }
  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param line		the line
   * @param column		the column
   */
  public VKCoordinatePosition(TokenReference where, int line, int column) {
    this(where, line, line, column, column, -1);
  }

  public void translate(int amount) {
    column += amount;
    columnEnd += amount;
    chartPos +=amount;
  }
  public void setChartPosition(int chartPos) {
    this.chartPos = chartPos;
  }

  public Object clone() {
    return new VKCoordinatePosition(getTokenReference(), line, column);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKBlock block) throws PositionedError {
    check(column <= columnEnd,
	  FormMessages.POSITION_NEGATIVE_LENGTH, "" + column, "" + columnEnd);
    check(line <= lineEnd,
        FormMessages.POSITION_NEGATIVE_LENGTH, "" + line, "" + lineEnd);
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
    return column;
  }

  /**
   * Return the column end pos
   */
  public int getColumnEnd() {
    return -1;
  }

  /**
   * Return the line end pos 
   */
  public int getLineEnd() {
    return -1;
  }
  
  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point		the current bottomRight point
   */
  public void checkBR(Point point, VKField field) {
    point.x = Math.max(point.x, columnEnd);
    point.y = Math.max(point.y, lineEnd); 
  }

  /**
   * checks that there is no other field at the same position
   *
   * @param freePositions	true at those places wich are free
   * @param name                the name of this field
   */
  public void checkPlace(VKContext context, VKField field, String[][] freePositions) {
    if (column != -1) {
      for (int i = column; i < columnEnd + 1; i++) {
        for (int j = line; j < lineEnd + 1 ; j++) {
          if (freePositions[i][j] != null) {
            context.reportTrouble(new CWarning(getTokenReference(),
                                               FormMessages.POSITION_USED,
                                               new Object[]{"" + j, "" + i, freePositions[i][j]}));
          }
          freePositions[i][j] = field.getIdent();
        }
      }
    }
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
	                              VKUtils.toExpression(ref, lineEnd),
				      VKUtils.toExpression(ref, column),
				      VKUtils.toExpression(ref, columnEnd),
				      VKUtils.toExpression(ref, chartPos)});
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
    p.printCoordinatePosition(line, lineEnd, column, columnEnd);
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

  private final int     line;
  private int           lineEnd;
  private int           column;
  private int           columnEnd;
  private int           chartPos;
}
