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

package at.dms.vkopi.comp.form;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.JCompoundStatement;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPrettyPrinter;

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
     public void genCode(at.dms.vkopi.comp.main.PrintWriter p) {
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
