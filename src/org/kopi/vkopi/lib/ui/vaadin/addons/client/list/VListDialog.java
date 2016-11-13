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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VInputTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ui.calendar.schedule.FocusableComplexPanel;

/**
 * The list dialog widget composed of HTML sortable table
 * that allows single row selection.
 */
public class VListDialog extends FocusableComplexPanel implements ClickHandler, KeyPressHandler, CloseHandler<PopupPanel> {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new {@code VListDialog} instance.
   */
  public VListDialog() {
    setElement(Document.get().createDivElement());
    makeFocusable();
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    setStyleName(Styles.LIST_DIALOG);
    sinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
    addKeyPressHandler(this);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Shows the list dialog.
   * @param parent The parent where to attach the list. 
   */
  public void show(HasWidgets parent) {
    if (popup != null && table != null) {
      parent.add(popup);
      table.render();
      popup.setWidget(this); // set the popup widget
      add(table); // put table inside the focus panel
      if (newForm != null ) {
	add(newForm);
      }
    }
    if (VInputTextField.getLastFocusedTextField() != null) {
      lastActiveWindow = VInputTextField.getLastFocusedTextField().getParentWindow();
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
    popup = new VPopup(connection, false, true);
    popup.addCloseHandler(this);
    popup.setAnimationEnabled(true);
    setRollDownAnimation(popup);
    table = new Table(connection);
    table.setStyleName(Styles.LIST_DIALOG_TABLE);
    handlerRegistration = table.addClickHandler(this);
  }
  
  /**
   * Sets the list dialog table model.
   * @param model The table model.
   */
  public void setModel(TableModel model) {
    if (table != null) {
      table.setModel(model);
    }
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
	  handleRowSelection(-1, false, true);
	}
      });
      newForm.getInputElement().setValue(newText);
      newForm.setStyleName("new-button");
      newForm.setSize("100%", "100%"); // occupy all available space.
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
  
  /**
   * Shows the list dialog in the screen.
   */
  public void showCentred() {
    if (popup == null) {
      return;
    }
    
    popup.center();
  }
  
  @Override
  public void onBrowserEvent(Event event) {
    if (event.getTypeInt() == Event.ONKEYDOWN) {
      if (table != null) {
	doKeyAction(event.getKeyCode());
      }
    }
    
    super.onBrowserEvent(event);
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    if (table != null) {
      char      c = event.getCharCode();
      int       row =  0;
      
      if (current == null) {
        current = "";
        table.unhighlightCell();
      }
      
      current += ("" + c).toLowerCase().charAt(0);
      columnsLoop:
      for (int col = 0; col < table.getColumnCount(); col++) {
        for (row = 0; row < table.getModelRowCount(); row++) {
          if (table.getDisplayedValueAt(row, col).toLowerCase().startsWith(current)) {
            table.setCurrentRow(row);
            table.highlightCell(row, col);
            break columnsLoop;
          }
        }
      }
      
      if (row == table.getModelRowCount()) {
        current = "";
        table.unhighlightCell();
      }
    }
  }
  
  /**
   * Allows access to the key events.
   * @param keyCode The key code.
   */
  protected void doKeyAction(final int keyCode) {
    switch (keyCode) {
      case KeyCodes.KEY_HOME:
        table.setCurrentRow(0);
        current = "";
        table.unhighlightCell();
        break;
      case KeyCodes.KEY_END:
	table.setCurrentRow(table.getRowCount() - 1);
	current = "";
	table.unhighlightCell();
        break;
      case KeyCodes.KEY_UP:
        table.shiftUp(1);
        current = "";
        table.unhighlightCell();
        break;
      case KeyCodes.KEY_DOWN:
        table.shiftDown(1);
        current = "";
        table.unhighlightCell();
        break;
      case KeyCodes.KEY_PAGEUP:
        table.shiftUp(20);
        current = "";
        table.unhighlightCell();
        break;
      case KeyCodes.KEY_PAGEDOWN:
        table.shiftDown(20);
        current = "";
        table.unhighlightCell();
        break;
      case KeyCodes.KEY_SPACE:
        handleRowSelection(-1, false, true);
        break;
      case KeyCodes.KEY_ENTER:
	 handleRowSelection(table.getSelectedRow(), false, false);
        break;
      case KeyCodes.KEY_ESCAPE:
	handleRowSelection(-1, true, false); // when escape, we return -1 as selected row
        break;
      default:
    }
  }
  
  @Override
  public void onClick(ClickEvent event) {
    if (table != null) {
      int	clicked;

      clicked = table.getClickedRow(event);
      if (clicked != -1) {
	handleRowSelection(clicked, false, false);
      }
    }
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void add(Widget child) {
    add(child, getElement());
  }
  
  /**
   * Handles a row selection in the list dialog table.
   * @param selectedRow The selected row.
   * @param escaped Should we escape ?
   * @param newForm Should we do a new form ?
   */
  protected void handleRowSelection(int selectedRow, boolean escaped, boolean newForm) {
    if (popup != null) {
      ListDialogConnector	connector;
      
      connector = getConnector();
      if (connector != null && table != null) {
	// we return the model row to used directly in server side.
	connector.fireOnSelection(selectedRow == -1 ? -1 : table.getModelRow(selectedRow), escaped, newForm);
      }
      popup.hide();
      if (handlerRegistration != null) {
	handlerRegistration.removeHandler(); // remove click handler
      }
    }
  }
  
  /**
   * Returns the list dialog connector.
   * @return The list dialog connector.
   */
  protected ListDialogConnector getConnector() {
    if (connection != null) {
      return (ListDialogConnector) ConnectorMap.get(connection).getConnector(this);
    } else {
      return null;
    }
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent();
    if (lastActiveWindow != null) {
      lastActiveWindow.goBackToLastFocusedTextBox();
    }
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
	if (newForm != null) {
	  if (newForm.getElement().getClientWidth() > table.getTHeadElement().getClientWidth()) {
	    table.getElement().getStyle().setWidth(newForm.getElement().getClientWidth(), Unit.PX);
	    table.setTableWidth(newForm.getElement().getClientWidth());
	  }
	}
	if (reference != null) {
	  popup.showRelativeTo(reference);
	} else {
	  showCentred();
	}
	focus(); // get focus to activate keyboard events.
      }
    });
  }
  
  @Override
  public void focus() {
    Scheduler.get().scheduleEntry(new ScheduledCommand() {
      
      @Override
      public void execute() {
        VListDialog.super.focus();
        if (table != null) {
          table.selectRow(0);
        }
      }
    });
  }
  
  /**
   * Sets the animation type to roll down.
   */
  public native void setRollDownAnimation(VPopup popup) /*-{
    popup.@com.google.gwt.user.client.ui.PopupPanel::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ROLL_DOWN);
  }-*/;

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private ApplicationConnection			connection;
  private Table					table;
  private VPopup				popup;
  private HandlerRegistration 			handlerRegistration;
  private Widget				reference;
  private VInputButton				newForm;
  private VWindow                               lastActiveWindow;
  private String                                current;
}
