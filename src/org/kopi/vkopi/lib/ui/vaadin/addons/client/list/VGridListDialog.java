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
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VTabSheet;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VInputTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.VForm;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.VPage;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.VEditorTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.GridListDialogState.SelectionTarget;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
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
    handlerRegistration = Window.addResizeHandler(new ResizeHandler() {
      
      @Override
      public void onResize(ResizeEvent event) {
        windowResized = true;
        calculateTableSize(event.getWidth(), event.getHeight(), true);
      }
    });
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
            calculateTableSize();
            if (reference != null) {
              popup.showRelativeTo(reference);
            } else {
              popup.setGlassEnabled(true);
              popup.setGlassStyleName(Styles.LIST_DIALOG + "-glass");
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
  
  /**
   * Forces the table to have scroll bars.
   */
  protected void forceScrollBar() {
    double      height = table.getHeightByRows() * 41;
    
    if (!windowResized){
      if (hasVerticalScrollBar(height)) {
        table.setWidth(table.getOffsetWidth() + 8 + "px"); //add horizontal scroll bar width
        scrollBarAdded = true;
      }
    } else {
      if (scrollBarAdded && !hasVerticalScrollBar(height)) {
        table.setWidth(table.getOffsetWidth() - 16 + "px"); //remove horizontal scroll bar width
        scrollBarAdded = false;
      } else if (!scrollBarAdded && hasVerticalScrollBar(height)) {
        table.setWidth(table.getOffsetWidth() + 16 + "px"); //add horizontal scroll bar width
        scrollBarAdded = true;
      }
    }
  }
  
  /**
   *  Returns true if the list table should have a vertical scroll bar according to a given height.
   * @param height The reference height.
   * @return true if the list table should have a vertical scroll bar.
   */
  protected boolean hasVerticalScrollBar(double height) {
    return table.getScrollHeight() > height;
  }
  
  /**
   * Returns true if the list table should have an horizontal scroll bar according to a given width.
   * @param width The reference width.
   * @return true if the list table should have an horizontal scroll bar.
   */
  protected boolean hasHorizontalScrollBar(double width) {
    return table.getScrollWidth() > width;
  }
  
  /**
   * Calculates and show the list widget according to the browser window size.
   */
  protected final void calculateTableSize() {
    calculateTableSize(Window.getClientWidth(), Window.getClientHeight(), false);
  }
  
  /**
   * Calculates the size of the list table and then shows it in a container popup.
   * @param width The available browser window width.
   * @param height The available browser window height.
   * @param resizing Is it a resizing context ?
   */
  protected void calculateTableSize(int width, int height, boolean resizing) {
    int                 available;
    int                 rows;
   
    if (VMainWindow.get().getCurrentWindow() == null) {
      rows = Math.max(1, (int) (VMainWindow.get().getOffsetHeight() / 41) - 1);
      table.setHeightByRows(Math.min(table.getDataSource().size(), rows)); 
      popup.center();
    } else if (reference != null) {
      available = Math.max(0, height - reference.getAbsoluteTop() - reference.getOffsetHeight() - 80 );
      if (hasVerticalScrollBar(available - 41)) {
        available = Math.max(available , reference.getAbsoluteTop() - 80 );
      }
      rows = Math.max(1, (int) (available / 41) - 1); // row heigh is 41px
      table.setHeightByRows(Math.min(table.getDataSource().size(), rows)); 
      forceScrollBar();
      popup.showRelativeTo(reference);

    } else {
      Element   positionContextElement = getPositionContextWidget().getElement();
      
      available = Math.max(0, height - positionContextElement.getAbsoluteTop() - 70); 
      if (newForm != null) {
        available = Math.max(0, available - 20); // new button height
      }
      rows = Math.max(1, (int) (available / 41) - 1); // row heigh is 41px
      table.setHeightByRows(Math.min(table.getDataSource().size(), rows)); 
      forceScrollBar();
      if (newForm != null && newForm.getElement().getOffsetWidth() > table.getOffsetWidth()) {
        table.setWidth(newForm.getElement().getOffsetWidth() + "px");
      }
      if (resizing){
        if (!hasHorizontalScrollBar(width)){
          table.setWidth(table.getScrollWidth() + (scrollBarAdded ? 16 : 0 ) + "px");
        } else {
          table.setWidth(width + "px");
        }
      }
      popup.center();
      popup.setPopupPosition(calculatePopupLeftPosition(positionContextElement), calculatePopupTopPosition(positionContextElement));
    }
  }
  
  /**
   * Returns the widget to be used as a reference to the the list widget position.
   * @return The widget to be used as a reference to the the list widget position.
   */
  protected Widget getPositionContextWidget() {
    VWindow             window;
    Widget              content;

    window = (VWindow) VMainWindow.get().getCurrentWindow();
    content = ((ScrollPanel)window.getContent()).getWidget();
    if (content instanceof VForm) {
      content = ((VForm) content).getWidget();
      if (content instanceof VPage) {
        return ((VPage)content).getContent();
      } else {
        return ((VTabSheet)content).getWidget();
      }
    } else {
      return content;
    }
  }
  
  /**
   * Calculates the left position of the list popup.
   * @param positionContextElement The position context element.
   * @return The calculated position.
   */
  protected int calculatePopupLeftPosition(Element positionContextElement) {
    int                 left = 0;
    int                 padding = 20;
  
    if (Window.getClientWidth() > table.getOffsetWidth()) {
      left = positionContextElement.getAbsoluteLeft() + (Math.min(Window.getClientWidth(), positionContextElement.getClientWidth()) - table.getOffsetWidth()) / 2;
    }
    if (left < 0) {
      left = padding;
      table.setWidth(table.getScrollWidth() - padding + "px");
    }
    
    return left;
  }
  /**
   * Calculates the top position of the list popup.
   * @param positionContextElement The position context element.
   * @return The calculated position.
   */
  protected int calculatePopupTopPosition(Element positionContextElement) {
    int      top =  positionContextElement.getAbsoluteTop();

    if (positionContextElement.getOffsetHeight() > table.getOffsetHeight() && table.getScrollHeight() == 0) {
      top = positionContextElement.getAbsoluteTop() +  (positionContextElement.getOffsetHeight() - table.getOffsetHeight()) / 2;
    }
    
    return top;
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
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
    }
    handlerRegistration = null;
  }
  
  /**
   * Clears the grid escalator using JSNI cause grid attributes are not accessible.
   * @param escalator The escalator instance.
   */
  private static native void clearEscalator(Escalator escalator) /*-{
    escalator.@com.vaadin.client.widgets.Escalator::positions = null;
  }-*/;
  
  /**
   * Clear the grid widget using JSNI cause grid attributes are not accessible.
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
  
  private boolean                               scrollBarAdded;
  private boolean                               windowResized;
  private ApplicationConnection                 connection;
  private Grid<JsonObject>                      table;
  private VPopup                                popup;
  private Widget                                reference;
  private VInputButton                          newForm;
  private VWindow                               lastActiveWindow;
  private VIcon                                 close;
  private VerticalPanel                         content;
  private String                                pattern;
  private HandlerRegistration                   handlerRegistration;
}
