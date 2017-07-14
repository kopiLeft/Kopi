/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import com.google.gwt.user.client.ui.HasEnabled;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Focusable;

/**
 * An editor field must provide its application connection
 * and a way to validate its content.
 */
public interface EditorField<T> extends Focusable, HasEnabled {
  
  /**
   * Returns the application connection attached to this editor field.
   * @return The application connection attached to this editor field.
   */
  ApplicationConnection getConnection();
  
  /**
   * Returns the field value.
   * @return The field value.
   */
  T getValue();
  
  /**
   * Sets the blink state of this editor field.
   * @param blink The blink state.
   */
  void setBlink(boolean blink);
  
  /**
   * Sets the color properties of the editor fields.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  void setColor(String foreground, String background);
  
  /**
   * Validates the content of this field state.
   * @throws InvalidEditorFieldException When field content is not valid.
   */
  void validate() throws InvalidEditorFieldException;
}
