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
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * A position within a block
 */
public class VKDescriptionPosition extends VKPosition {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param field		the master field
   */
  public VKDescriptionPosition(TokenReference where, String field) {
    super(where);
    this.field = field;
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
    vkField = block.getField(field);
    check(vkField != null, FormMessages.FIELD_NOT_FOUND, field);
    check(!vkField.isInternal(), FormMessages.FOLLOW_INTERNAL, field);
  }

  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point		the current bottomRight point
   */
  public void checkBR(Point point, VKField field) {
    // do nothing
  }

  /**
   * checks that there is no other field at the same position
   *
   * @param freePositions	true at those places wich are free
   * @param name                the name of this field
   */
  public void checkPlace(VKContext context, VKField field, String[][] freePositions){
    // do nothing
  }
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Return the line pos
   */
  public int getLine() {
    return vkField.getDetailedPosition().getLine();
  }

  /**
   * Return the column pos
   */
  public int getColumn() {
    return vkField.getDetailedPosition().getColumn();
  }

  /**
   * Return the column end pos
   */
  public int getColumnEnd() {
    return vkField.getDetailedPosition().getColumnEnd();
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode() {
    VKPosition          pos = vkField.getDetailedPosition();
    int                 line = pos.getLine();
    int                 column = pos.getColumn();
    int                 columnEnd = pos.getColumnEnd();
    TokenReference	ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
				    VKStdType.VPosition,
				    new JExpression[] {
				      VKUtils.toExpression(ref, line),
				      VKUtils.toExpression(ref, column),
				      VKUtils.toExpression(ref, columnEnd)
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
    p.printDescriptionPosition(field);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String	field;
  private VKField	vkField;
}
