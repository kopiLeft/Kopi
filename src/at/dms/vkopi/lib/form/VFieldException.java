/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: VFieldException.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import at.dms.vkopi.lib.visual.VExecFailedException;

public class VFieldException extends VExecFailedException {

  /**
   * Constructs an exception with no message.
   *
   * @param	field		the field that has not a correct value
   */
  public VFieldException(VField field) {
    this(field, null, null);
  }

  /**
   * Constructs an exception with a message.
   *
   * @param	field		the field that has not a correct value
   * @param	message		the associated message
   */
  public VFieldException(VField field, String message) {
    this(field, message, null);
  }

  /**
   * Constructs an exception with a message.
   *
   * @param	field		the field that has not a correct value
   * @param	message		the associated message
   * @param	newValue	the new value for the field
   */
  public VFieldException(VField field, String message, Object newValue) {
    super(message);
    this.field = field;
    this.newValue = newValue;
  }

  /**
   * Returns the field where the error occurs
   */
  public VField getField() {
    return field;
  }

  /**
   * Returns the field where the error occure
   */
  public void resetValue() {
    if (newValue != null) {
      field.setObject(field.getBlock().getActiveRecord(), newValue);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Object		newValue;
  private final VField		field;
}
