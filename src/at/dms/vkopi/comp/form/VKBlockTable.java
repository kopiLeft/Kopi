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
 * $Id: VKBlockTable.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.comp.form;

import at.dms.vkopi.comp.base.VKPhylum;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPrettyPrinter;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JStringLiteral;
import at.dms.compiler.base.TokenReference;

/**
 * A block table information
 */
public class VKBlockTable extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class describe the alignment of multi blocks
   *
   * @param where		the token reference of this node
   * @param name		the name of the table
   * @param corr		the shortcut
   */
  public VKBlockTable(TokenReference where, String name, String corr) {
    super(where);
    this.name = name;
    this.corr = corr;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * Print expression to output stream
   */
  public String getName() {
    return name;
  }

  /**
   * Return table num
   */
  public String getCorr() {
    return corr;
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
    // don't do nothing now
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode() {
    return new JStringLiteral(getTokenReference(), name);
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
    p.printBlockTable(name, corr);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBER
  // ----------------------------------------------------------------------

  private String	name;
  private String	corr;
}
