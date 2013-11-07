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

/**
 * {@code UTextField} is the top-level interface that must be implemented
 * by all kopi text fields.
 */
public interface UTextField extends UField {

  /**
   * Returns the text field content.
   * @return The text field content.
   */
  public String getText();

  /**
   * Sets if the field has a critical value.
   * @param b The critical value state.
   */
  public void setHasCriticalValue(boolean b);

  /**
   * Adds selection focus listener
   */
  public void addSelectionFocusListener();

  /**
   * Removes selection focus Listener
   */
  public void removeSelectionFocusListener();

  /**
   * Disables / Enables the selection after the update operation.
   */
  public void setSelectionAfterUpdateDisabled(boolean disable);
}
