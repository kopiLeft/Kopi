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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.notification;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.NotificationListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VInputTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ApplicationConnection;

/**
 * An abstract implementation of notification components such as
 * warnings, errors, confirms and informations.
 */
public abstract class VAbstractNotification extends FocusPanel implements CloseHandler<PopupPanel>, KeyPressHandler {
  
  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  
  /**
   * Creates a new notification widget with table containing
   * a title, a message, an image and buttons location.
   */
  protected VAbstractNotification() {
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    setStyleName(Styles.NOTIFICATION);
    listeners = new ArrayList<NotificationListener>();
    table = new FlexTable();
    table.setCellSpacing(5);
    table.setCellPadding(2);
    table.getElement().setPropertyString("align", "center");
    
    title = new VSpan();
    table.setWidget(0, 0, title);
    table.getFlexCellFormatter().setStyleName(0, 0, Styles.NOTIFICATION_TITLE);    
    table.getFlexCellFormatter().setColSpan(0, 0, 2);;
    
    image = new VImage();
    image.setStyleName(Styles.NOTIFICATION_IMAGE);
    table.setWidget(1, 0, image);
    table.getCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    
    message = new VSpan();
    message.setStyleName(Styles.NOTIFICATION_MESSAGE);
    table.setWidget(1, 1, message);

    buttons = new VSpanPanel();
    buttons.setStyleName(Styles.NOTIFICATION_BUTTONS);
    table.setWidget(2, 0, buttons);
    table.getFlexCellFormatter().setAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    table.getFlexCellFormatter().setColSpan(2, 0, 2);
    table.setWidth("100%");
    setWidget(table);
    addKeyPressHandler(this);
    sinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
  }
  
  /**
   * Initializes the notification panel.
   * @param connection  The application connection.
   */
  public void init(ApplicationConnection connection) {
    setImage(connection);
    popup = new VPopup(connection, false, true);
    popup.addCloseHandler(this);
    popup.setAnimationEnabled(true);
    if (showGlassPane()) {
      popup.setGlassEnabled(true);
      popup.setGlassStyleName(Styles.NOTIFICATION + "-glass");
    }
  }
  
  /**
   * Shows the notification popup.
   */
  public void show(final HasWidgets parent, final String locale) {
    if (popup != null) {
      new Timer() {

        @Override
        public void run() {
          setButtons(locale); 
          parent.add(popup);
          popup.setWidget(VAbstractNotification.this);
          popup.center();
          if (VInputTextField.getLastFocusedTextField() != null) {
            lastFocusedWindow = VInputTextField.getLastFocusedTextField().getParentWindow();
          }
          Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
              focus();
            }
          });  
        }
      }.schedule(80); // delay it after a popup close to ensure that it will not show behind the glass pane
    }
  }
  
  /**
   * Closes the notification panel.
   */
  public void close() {
    hide();
  }
  
  /**
   * Hides the notification dialog.
   */
  protected void hide() {
    if (popup != null) {
      popup.hide();
    }
  }
  
  /**
   * Registers a new notification listener.
   * @param l The listener to be added.
   */
  public void addNotificationListener(NotificationListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a new notification listener.
   * @param l The listener to be removed.
   */
  public void removeNotificationListener(NotificationListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires a close event.
   * @param action The user action.
   */
  protected void fireOnClose(boolean action) {
    for (NotificationListener l : listeners) {
      if (l != null) {
        l.onClose(action);
      }
    }
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent();
    if (goBackToLastFocusedWindow() && lastFocusedWindow != null) {
      lastFocusedWindow.goBackToLastFocusedTextBox();
    }
  }
  
  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------
  
  /**
   * Sets the notification title.
   * @param title The notification title.
   */
  public void setNotificationTitle(String title) {
    this.title.setText(title);
  }
  
  /**
   * Sets the notification message.
   * @param text The notification message.
   */
  public void setNotificationMessage(String text) {
    if (text != null) {
      message.setHtml(text.replaceAll("\n", "<br>").replaceAll("<br><br>", "<br>"));
    }
  }
  
  /**
   * Shows an optional glass pane.
   * @return {@code true} if a glass pane should be shown
   */
  protected boolean showGlassPane() {
    return false;
  }
  
  /**
   * Should we go back to the last focused field when the notification is closed ?
   * @return {@code true} if we should go back to the last focused field when the notification is closed.
   */
  protected boolean goBackToLastFocusedWindow() {
    return true;
  }
  
  /**
   * Focus a component.
   */
  protected void focus() {
    // focus a component.
  }
  
  /**
   * Sets yes is a default answer.
   * @param yesIsDefault Yes is the default answer.
   */
  public void setYesIsDefault(boolean yesIsDefault) {
    this.yesIsDefault = yesIsDefault;
  }
  
  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------
  
  /**
   * Sets the notification buttons.
   * @param locale The notification locale.
   */
  public abstract void setButtons(String locale);
  
  /**
   * Sets the notification image.
   * @param applicationConnection The application connection.
   */
  public abstract void setImage(ApplicationConnection applicationConnection);
    
  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  
  private final List<NotificationListener>      listeners;
  protected VPopup                              popup;
  private final FlexTable                       table;
  private final VSpan                           title;
  protected final VImage                        image;
  private final VSpan                           message;
  protected final VSpanPanel                    buttons;
  protected boolean				yesIsDefault;
  private VWindow                               lastFocusedWindow;
}