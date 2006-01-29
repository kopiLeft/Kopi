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

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.JCompoundStatement;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.NotImplementedException;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;

/**
 * This class represents a label in a block
 */
public class VKLabel extends VKObject {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Create a new label element within a block
   *
   * @param where	the token reference of this node
   * @param pos		the position of the object in the block
   * @param text	the text of this label
   * @param border	the color of this label
   */
  public VKLabel(TokenReference where, String name, VKPosition pos, int border) {
    super(where, pos);
    this.text = text;
    this.border = border;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkInterface(VKBlock block) {
    // always ok
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the object.
   */
  public void analyse(VKContext context, VKBlock block) {
    // nothing here (image != null ?)
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JCompoundStatement genNewObject() {
    throw new InconsistencyException();
    /**
     * Print expression to output stream
     *
     public void genCode(com.kopiright.vkopi.comp.main.PrintWriter p) {
     p.println("b.addLabel(" +
     "new DLabel(" + Util.stringify(name) + "), " +
     pos.line + ", " +
     pos.column + ", " +
     pos.columnEnd + ");");
     super.genCode(p);
     } */
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p, TypeFactory factory) {
    genComments(p);
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String         text;
  private int            border;
}
