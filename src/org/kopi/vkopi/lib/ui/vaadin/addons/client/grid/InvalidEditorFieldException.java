/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.NotificationListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.notification.NotificationUtils;

/**
 * Thrown when the field content is checked against its validation
 * strategy. This exception is transformed later to an error notification.
 */
@SuppressWarnings("serial")
public class InvalidEditorFieldException extends Exception implements NotificationListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new check type exception from a message key.
   * @param field The concerned text input zone. 
   * @param messageKey The message key.
   */
  public InvalidEditorFieldException(EditorField<?> field, String messageKey) {
    this(field, messageKey, (Object[])null);
  }

  /**
   * Creates a new check type exception from a message key.
   * @param field The concerned text input zone. 
   * @param messageKey The message key.
   * @param params The message parameters
   */
  public InvalidEditorFieldException(EditorField<?> field, String messageKey, Object... params) {
    this.field = field;
    this.messageKey = messageKey;
    this.params = params;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Displays the check type error.
   */
  public void displayError() {
    field.setBlink(true);
    NotificationUtils.showError(field.getConnection(),
                                this,
                                VMainWindow.get(),
                                VMainWindow.getLocale(),
                                messageKey,
                                params);
  }

  @Override
  public void onClose(boolean action) {
    field.setBlink(false);
    field.focus();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private final EditorField<?>                  field;
  private final String                          messageKey;
  private final Object[]                        params;
}
