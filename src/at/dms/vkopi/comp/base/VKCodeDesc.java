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

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.TokenReference;
import at.dms.util.base.InconsistencyException;
import at.dms.xkopi.lib.type.Fixed;

/**
 * This class represents the description of acode value
 */
public class VKCodeDesc extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param name		the name of this element
   * @param val			the value of this element
   */
  public VKCodeDesc(TokenReference where, String name, Object val) {
    super(where);
    this.name = name;
    this.val = val;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the name of this code
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the value of this node as a boolean
   */
  public boolean getBoolean() {
    if (val instanceof Boolean) {
      return ((Boolean)val).booleanValue();
    }
    throw new InconsistencyException();
    // !!!!throw new PositionedError(getTokenReference(), "vk-not-boolean", getName(), val);
  }

  /**
   * Returns the value of this node as an integer
   */
  public int getInteger() {
    if (val instanceof Integer) {
      return ((Integer)val).intValue();
    }
    throw new InconsistencyException();
    // !!!throw new PositionedError(getTokenReference(), "vk-not-integer", getName(), val);
  }

  /**
   * Returns the value of this node as an integer
   */
  public Fixed getFixed() {
    if (val instanceof Fixed) {
      return (Fixed)val;
    }
    // !!! check somewhere else but not in gen code !!!throw new PositionedError(getTokenReference(), "vk-not-fixed", getName(), val);
    throw new InconsistencyException();
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
    if (val instanceof Boolean) {
      p.printCodeDesc(name, ((Boolean)val).booleanValue() ? "TRUE" : "FALSE");
    } else {
      p.printCodeDesc(name, val.toString());
    }
  }

  // ----------------------------------------------------------------------
  // DATA
  // ----------------------------------------------------------------------

  private String      name;
  private Object      val;
}
