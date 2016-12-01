/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.notification;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedMessages;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.NotificationListener;

import com.google.gwt.user.client.ui.HasWidgets;
import com.vaadin.client.ApplicationConnection;

/**
 * Utilities to show notifications in the client side.
 */
public abstract class NotificationUtils {

  /**
   * Shows an error completely in the client side.
   * @param connection The application connection.
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   */
  public static void showError(ApplicationConnection connection,
                               NotificationListener callback,
                               HasWidgets parent,
                               String locale,
                               String messageKey)
  {
    showError(connection, callback, parent, locale, messageKey, (Object[])null);
  }

  /**
   * Shows an error completely in the client side.
   * @param connection The application connection.
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   * @param params The message parameters.
   */
  public static void showError(ApplicationConnection connection,
                               NotificationListener callback,
                               HasWidgets parent,
                               String locale,
                               String messageKey,
                               Object... params)
  {
    VErrorNotification          error;
    
    error = new VErrorNotification();
    error.init(connection);
    error.setNotificationMessage(LocalizedMessages.getMessage(locale, messageKey, params));
    error.setNotificationTitle(LocalizedProperties.getString(locale, "Error"));
    if (callback != null) {
      error.addNotificationListener(callback);
    }
    error.show(parent, locale);
  }
  
  /**
   * Shows a warning completely in the client side.
   * @param connection The application connection.
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   */
  public static void showWarning(ApplicationConnection connection,
                                 NotificationListener callback,
                                 HasWidgets parent,
                                 String locale,
                                 String messageKey)
  {
    showWarning(connection, callback, parent, locale, messageKey, (Object[])null);
  }
  
  /**
   * Shows a warning completely in the client side.
   * @param connection The application connection.
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   * @param params The message parameters.
   */
  public static void showWarning(ApplicationConnection connection,
                                 NotificationListener callback,
                                 HasWidgets parent,
                                 String locale,
                                 String messageKey,
                                 Object... params)
  {
    VWarningNotification        warning;
    
    warning = new VWarningNotification();
    warning.init(connection);
    warning.setNotificationMessage(LocalizedMessages.getMessage(locale, messageKey, params));
    warning.setNotificationTitle(LocalizedProperties.getString(locale, "Warning"));
    if (callback != null) {
      warning.addNotificationListener(callback);
    }
    warning.show(parent, locale);
  }
  
  /**
   * Shows an information completely in the client side.
   * @param connection The application connection.
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   */
  public static void showInformation(ApplicationConnection connection,
                                     NotificationListener callback,
                                     HasWidgets parent,
                                     String locale,
                                     String messageKey)
  {
    showInformation(connection, callback, parent, locale, messageKey, (Object[])null);
  }
  
  /**
   * Shows an information completely in the client side.
   * @param connection The application connection.
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   * @param params The message parameters.
   */
  public static void showInformation(ApplicationConnection connection,
                                     NotificationListener callback,
                                     HasWidgets parent,
                                     String locale,
                                     String messageKey,
                                     Object... params)
  {
    VInformationNotification          information;
    
    information = new VInformationNotification();
    information.init(connection);
    information.setNotificationMessage(LocalizedMessages.getMessage(locale, messageKey, params));
    information.setNotificationTitle(LocalizedProperties.getString(locale, "Notice"));
    if (callback != null) {
      information.addNotificationListener(callback);
    }
    information.show(parent, locale);
  }
}
