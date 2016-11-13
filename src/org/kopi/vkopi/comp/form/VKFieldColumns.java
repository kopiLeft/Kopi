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

import org.kopi.vkopi.comp.base.*;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * This class define a column list information
 */
public class VKFieldColumns extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * This class define a column list information
   *
   * @param where		the token reference of this node
   * @param columns		a vector of columns
   * @param indices		the indice of this column
   * @param priority		the priority in sorting
   */
  public VKFieldColumns(TokenReference where,
			VKFieldColumn[] columns,
			int indices,
			int priority)
  {
    super(where);
    this.columns = columns;
    this.indices = indices;
    this.priority = priority;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Sets the position in an array of fields
   */
  public VKFieldColumns cloneToPos(int pos) {
    VKFieldColumn[]	clone = new VKFieldColumn[columns.length];
    for (int i = 0; i < columns.length; i++) {
      clone[i] = columns[i].cloneToPos(pos);
    }
    return new VKFieldColumns(getTokenReference(), clone, indices, priority);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKField field) throws PositionedError {
    for (int i = 0; i < columns.length; i++) {
      columns[i].checkCode(context, this, field);
    }

    if (indices != 0) {
      // max. 32 indices per field (= sizeof int)
      for (int i = 0; i < 32; i++) {
	if ((indices & (1 << i)) != 0) {
	  check(field.getBlock().hasIndex(i), FormMessages.UNDEFINED_INDEX, Integer.toString(i));
	}
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate init code for this node.
   */
  public JExpression[] genCode(VKField field) {
    JExpression[]       exprs;
    TokenReference	ref;
    JExpression[]	init;

    exprs = new JExpression[3];
    ref = getTokenReference();
    init = new JExpression[columns.length];
    for (int i = 0; i < columns.length; i++) {
      init[i] = columns[i].genCode();
    }
    exprs[0] = VKUtils.createArray(ref, VKStdType.VColumn, init);
    exprs[1] = VKUtils.toExpression(ref, indices);
    exprs[2] = VKUtils.toExpression(ref, priority);
    return exprs;
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
    /*
    genComments(p);
    ((VKFormPrettyPrinter)p).printFieldColumns(columns, indices, priority);
    */
  }

  public VKFieldColumn[] getColumns() {
    return columns;
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKFieldColumn[]	columns;
  private int			indices;
  private int			priority;
}
