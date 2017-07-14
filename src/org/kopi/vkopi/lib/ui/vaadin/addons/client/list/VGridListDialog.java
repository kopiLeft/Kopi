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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.list;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VInputTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.VEditorTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogState.SelectionTarget;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.FocusableFlowPanel;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.client.widgets.Grid;

import elemental.json.JsonObject;

/**
 * Based grid list dialog list widget
 */
public class VGridListDialog extends FocusableFlowPanel implements KeyDownHandler, KeyPressHandler, CloseHandler<PopupPanel> {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public VGridListDialog() {
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    setStyleName(Styles.LIST_DIALOG);
    sinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
    addKeyDownHandler(this);
    addKeyPressHandler(this);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void onKeyDown(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
      if (pattern != null && pattern.length() > 1) {
        pattern = pattern.substring(0, pattern.length() - 1);
      } else {
        pattern = "";
      }
    }
    doKeyAction(event.getNativeKeyCode());
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    char      c = event.getCharCode();
    
    if (pattern == null) {
      pattern = "";
    }
    if ((int)c != 0) {
      pattern += ("" + c).toLowerCase().charAt(0);
    }
    getConnector().fireSearch(pattern);
  }
  
  /**
   * Allows access to the key events.
   * @param keyCode The key code.
   */
  protected void doKeyAction(final int keyCode) {
    switch (keyCode) {
      case KeyCodes.KEY_HOME:
        pattern = "";
        getConnector().fireSelection(SelectionTarget.FIRST_ROW);
        break;
      case KeyCodes.KEY_END:
        pattern = "";
        getConnector().fireSelection(SelectionTarget.LAST_ROW);
        break;
      case KeyCodes.KEY_UP:
        pattern = "";
        getConnector().fireSelection(SelectionTarget.PREVIOUS_ROW);
        break;
      case KeyCodes.KEY_DOWN:
        pattern = "";
        getConnector().fireSelection(SelectionTarget.NEXT_ROW);
        break;
      case KeyCodes.KEY_PAGEUP:
        pattern = "";
        getConnector().fireSelection(SelectionTarget.PREVIOUS_PAGE);
        break;
      case KeyCodes.KEY_PAGEDOWN:
        pattern = "";
        getConnector().fireSelection(SelectionTarget.NEXT_PAGE);
        break;
      case KeyCodes.KEY_SPACE:
        if (newForm != null) {
          getConnector().fireClosed(false, true);
        }
        break;
      case KeyCodes.KEY_ENTER:
        getConnector().fireSelection(SelectionTarget.CURRENT_ROW);
        break;
      case KeyCodes.KEY_ESCAPE:
        getConnector().fireClosed(true, false);
        break;
      default:
    }
  }
  
  /**
   * Returns the connector for this list widget.
   * @return The connector for this list widget.
   */
  protected GridListDialogConnector getConnector() {
    return ConnectorUtils.getConnector(connection, this, GridListDialogConnector.class);
  }
  
  /**
   * Shows the list dialog.
   * @param parent The parent where to attach the list. 
   */
  public void show(final HasWidgets parent) {
    if (popup != null) {
      parent.add(popup);
      popup.setWidget(this);
      if (VInputTextField.getLastFocusedTextField() != null) {
        lastActiveWindow = VInputTextField.getLastFocusedTextField().getParentWindow();
      }
      // it can be an editor widget
      if (lastActiveWindow == null && VEditorTextField.getLastFocusedEditor() != null) {
        lastActiveWindow = VEditorTextField.getLastFocusedEditor().getWindow();
      }
    }
  }
  
  
  /**
   * Initializes the list dialog widget.
   * @param connection The application connection.
   */
  public void init(ApplicationConnection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Application connection should be provided");
    }
    this.connection = connection;
    content = new VerticalPanel();
    close = new VIcon();
    close.setName("close");
    popup = new VPopup(connection, false, true) {
      
      @Override
      protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
          
          @Override
          public void execute() {
            calculateTableHeight();
            if (reference != null) {
              popup.showRelativeTo(reference);
            } else {
              popup.setGlassEnabled(true);
              popup.setGlassStyleName(Styles.LIST_DIALOG + "-glass");
              popup.center(VMainWindow.get().getCurrentWindow());
            }
            focus();
          }
        });
      }
    };
    popup.addCloseHandler(this);
    close.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        getConnector().fireClosed(true, false);
      }
    });
  }
  
  protected void calculateTableHeight() {
    int         available;
    int         top;
    int         height;
    
    height = Window.getClientHeight();
    if (reference != null) {
      top = reference.getAbsoluteTop() + reference.getOffsetHeight();
    } else if (VMainWindow.get().getCurrentWindow() != null) {
      top = (VMainWindow.get().getCurrentWindow().getOffsetHeight() - popup.getOffsetHeight()) >> 1;
      top = Math.max(Window.getScrollTop() + top, (VMainWindow.get().getCurrentWindow().getAbsoluteTop()));
    } else {
      top = Window.getScrollTop(); // the hole window
    }
    available = Math.max(0, height - top - 72); // for headers and filters
    if (newForm != null) {
      available = Math.max(0, available - 20); // new button height
    }
    if (available <= 40) {
      available = recalculateAvailableHeight(available);
    }
    table.setHeightByRows(Math.min(table.getDataSource().size(), available / 40)); // row heigh is 40px
    if (table.getOffsetHeight() > available) {
      table.setWidth(table.getOffsetWidth() + 16 + "px"); //add horizontal scroll bar width
    }
    if (newForm != null && newForm.getElement().getOffsetWidth() > table.getOffsetWidth()) {
      table.setWidth(newForm.getElement().getOffsetWidth() + "px");
    }
  }
  
  /**
   * Recalculates the available height based on the top of the window
   * This is only needed when putting the popup according to a reference component.
   */
  protected int recalculateAvailableHeight(int available) {
    if (reference != null) {
      available = reference.getAbsoluteTop();
      if (newForm != null) {
        available = Math.max(0, available - 20); // new button height
      }
    }
    
    return available;
  }
  
  /**
   * Sets the table component associated with this list dialog.
   * @param table The table widget.
   */
  public void setTable(final Grid<JsonObject> table) {
    this.table = table;
    this.table.setStyleName(Styles.LIST_DIALOG_TABLE);
    content.add(close);
    content.add(this.table); // put table inside the focus panel
    if (newForm != null ) {
      content.add(newForm);
    }
    add(content);
  }
  
  /**
   * Sets the new text widget.
   * @param newText The new text widget.
   */
  public void setNewText(String newText) {
    if (newText != null) {
      newForm = new VInputButton(newText, new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          getConnector().fireClosed(false, true);
        }
      });
      newForm.getInputElement().setValue(newText);
      newForm.setStyleName("new-button");
      newForm.setWidth("100%"); // occupy all available space.
    }
  }
  
  /**
   * Shows the list dialog relatively to a reference widget.
   * @param connection The application connection.
   * @param model The table model.
   * @param reference The reference widget.
   */
  public void showRelativeTo(Widget reference) {
    this.reference = reference;
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent();
  }
  
  @Override
  public void clear() {
    super.clear();
    close = null;
    connection = null;
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        clearGrid(table);
        clearEscalator(table.getEscalator());
        table = null;
      }
    });
    popup = null;
    reference = null;
    newForm = null;
    lastActiveWindow = null;
  }
  
  /**
   * Clears the grid escalator.
   * @param escalator The escalator instance.
   */
  private static native void clearEscalator(Escalator escalator) /*-{
    escalator.@com.vaadin.client.widgets.Escalator::positions = null;
  }-*/;
  
  /**
   * Clear the grid widget.
   * @param table The grid instance.
   */
  private static native void clearGrid(Grid<JsonObject> table) /*-{
    table.@com.vaadin.client.widgets.Grid::cellFocusHandler = null;
    table.@com.vaadin.client.widgets.Grid::rowReference = null;
    table.@com.vaadin.client.widgets.Grid::cellReference = null;
    table.@com.vaadin.client.widgets.Grid::rendererCellReference = null;
    table.@com.vaadin.client.widgets.Grid::eventCell = null;
    table.@com.vaadin.client.widgets.Grid::keyDown = null;
    table.@com.vaadin.client.widgets.Grid::keyUp = null;
    table.@com.vaadin.client.widgets.Grid::keyPress = null;
    table.@com.vaadin.client.widgets.Grid::clickEvent = null;
    table.@com.vaadin.client.widgets.Grid::doubleClickEvent = null;
    table.@com.vaadin.client.widgets.Grid::header = null;
    table.@com.vaadin.client.widgets.Grid::footer = null;
    table.@com.vaadin.client.widgets.Grid::sidebar = null;
    table.@com.vaadin.client.widgets.Grid::cellOnPrevMouseDown = null;
  }-*/;
  
  /**
   * Delays the focus to got it from last focused window text box
   * when another popup is closed before showing this list 
   */
  @Override
  public void focus() {
    new Timer() {
      
      @Override
      public void run() {
        VGridListDialog.super.focus();  
      }
    }.schedule(50);
  }
  
  /**
   * Closes this dialog
   */
  protected void close() {
    if (popup != null) {
      popup.hide();
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private ApplicationConnection                 connection;
  private Grid<JsonObject>                      table;
  private VPopup                                popup;
  private Widget                                reference;
  private VInputButton                          newForm;
  private VWindow                               lastActiveWindow;
  private VIcon                                 close;
  private VerticalPanel                         content;
  private String                                pattern;
}
