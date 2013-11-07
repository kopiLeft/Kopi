/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
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
 * $Id$
 */

package com.kopiright.vkopi.lib.form;

import java.util.EventListener;

import com.kopiright.vkopi.lib.visual.VException;

public interface FieldListener extends EventListener {

  /**
   * Updates the field model.
   * @throws VException Update operation may fail.
   */
  void updateModel() throws VException;

  /**
   * Returns the displayed value in the field.
   * @param trim Should be the text be trim ?
   * @return The displayed value in the field.
   * @throws VException May fail when retrieving value.
   */
  Object getDisplayedValue(boolean trim) throws VException;

  /**
   * Returns the current {@link UField} display.
   * @return The current {@link UField} display.
   */
  UField getCurrentDisplay(); // please do not use!

  /**
   * Display a field error.
   * @param message The error to be displayed.
   */
  void fieldError(String message);

  /**
   * Requests the focus on this field.
   * @return True if the focus has been gained.
   * @throws VException Focus may have not been gained.
   */
  boolean requestFocus() throws VException;

  /**
   * Loads an item from a given mode {@code mode}.
   * @param mode The load mode.
   * @return True if the item was loaded.
   * @throws VException Load operation may fail.
   */
  boolean loadItem(int mode) throws VException;

  /**
   * Predefined value fill for this field.
   * @return True if value is loaded.
   * @throws VException Fill operation may fail.
   */
  boolean predefinedFill() throws VException;

  /**
   * Enters the field. This operation leads to a focus gain.
   */
  void enter();

  /**
   * Leaves the field. This operation leads to a focus loss.
   */
  void leave();
}
