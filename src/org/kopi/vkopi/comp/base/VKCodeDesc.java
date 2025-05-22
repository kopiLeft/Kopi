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

package org.kopi.vkopi.comp.base;

import org.kopi.compiler.base.TokenReference;
import org.kopi.util.base.InconsistencyException;
import org.kopi.xkopi.lib.type.Fixed;

/**
 * This class represents the description of a code label value pair.
 */
public class VKCodeDesc extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a new code label value pair.
   *
   * @param where		the token reference of this node
   * @param ident               the identifier of the code value
   * @param label		the string representation in the default locale
   * @param value		the value of this element
   */
  public VKCodeDesc(TokenReference where,
                    String ident,
                    String label,
                    Object value)
  {
    super(where);
    this.ident = ident;
    this.label = label;
    this.value = value;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the ident of this code.
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Returns the label of this code.
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the value of this node as a boolean.
   */
  public boolean getBoolean() {
    if (value instanceof Boolean) {
      return ((Boolean)value).booleanValue();
    }
    throw new InconsistencyException();
    // !!!!throw new PositionedError(getTokenReference(), "vk-not-boolean", getLabel(), value);
  }

  /**
   * Returns the value of this node as an integer.
   */
  public int getInteger() {
    if (value instanceof Integer) {
      return ((Integer)value).intValue();
    }
    throw new InconsistencyException();
    // !!!throw new PositionedError(getTokenReference(), "vk-not-integer", getLabel(), value);
  }

  /**
   * Returns the value of this node as a fixed.
   */
  public Fixed getFixed() {
    if (value instanceof Fixed) {
      return (Fixed)value;
    }
    // !!! check somewhere else but not in gen code !!!throw new PositionedError(getTokenReference(), "vk-not-fixed", getLabel(), value);
    throw new InconsistencyException();
  }

  /**
   * Returns the value of this node as a string.
   */
  public String getString() {
    if (value instanceof String) {
      return (String)value;
    }
    // !!! check somewhere else but not in gen code !!!throw new PositionedError(getTokenReference(), "vk-not-string", getLabel(), value);
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
    if (value instanceof Boolean) {
      p.printCodeDesc(ident, label, ((Boolean)value).booleanValue() ? "TRUE" : "FALSE");
    } else {
      p.printCodeDesc(ident, label, value.toString());
    }
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
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates ListDesc localization.
   */
  public void genLocalization(VKLocalizationWriter writer) {
    writer.genCodeDesc(ident, label);
  }

  // ----------------------------------------------------------------------
  // DATA
  // ----------------------------------------------------------------------

  private final String          ident;
  private final String          label;
  private final Object          value;
}
