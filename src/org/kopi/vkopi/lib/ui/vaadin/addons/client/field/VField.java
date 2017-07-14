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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.FieldListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VDragAndDropWrapper;

/**
 * The field widget containing a text input field and
 * two buttons for incrementing and decrementing values.
 */
public class VField extends VSpanPanel implements HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the field widget.
   */
  public VField() {
    listeners = new ArrayList<FieldListener>();
    setStyleName(Styles.FIELD);
    sinkEvents(Event.ONCLICK);
    sinkEvents(Event.ONCONTEXTMENU);
    addDomHandler(new ContextMenuHandler() {
      
      @Override
      public void onContextMenu(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
      }
    }, ContextMenuEvent.getType());
  }

  //---------------------------------------------------
  // IMPLMENTATIONS
  //---------------------------------------------------
  
  /**
   * Initializes the field widget.
   * @param connection The application connection.
   * @param hasIncrement Has increment button ?
   * @param hasDecrement Has decrement button ?
   */
  public void init(ApplicationConnection connection,
                   boolean hasIncrement,
                   boolean hasDecrement)
  {
    //!!! spinner buttons are removed. See ticket srd#1074431
    /*if (hasDecrement) {
      decr = new VButton();
      decr.setIcon("caret-left");
      add(decr);
      decr.addClickHandler(new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          fireDecremented();
        }
      });
    }
    
    if (hasIncrement) {
      incr = new VButton();
      incr.setIcon("caret-right");
      add(incr);
      incr.addClickHandler(new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          fireIncremented();
        }
      });
    }*/
    
    addDomHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        fireClicked();
      }
    }, ClickEvent.getType());
  }
  
  /**
   * Sets the text input widget.
   * @param textField The input widget.
   */
  public void setTextField(VTextField textField) {
    if (textField != null) {
      this.textField = textField;
      insert(this.textField, 0); // add it at first position.
    }
  }
  
  /**
   * Sets the object field widget.
   * @param objectField The object field widget.
   */
  public void setObjectField(VObjectField objectField) {
    if (objectField != null) {
      this.objectField = objectField;
      insert(objectField, 0); // add it at first position.
    }
  }
  
  /**
   * Sets the object field widget.
   * @param objectField The object field widget.
   */
  public void setDnDWrapper(VDragAndDropWrapper wrapper) {
    if (wrapper != null) {
      this.objectField = (VObjectField) wrapper.getWidget();
      insert(wrapper, 0); // add it at first position.
    }
  }
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }
  
  @Override
  public void clear() {
    listeners.clear();
    listeners = null;
    incr = null;
    decr = null;
    textField = null;
    objectField = null;
    client = null;
    super.clear();
  }
  
  /**
   * Sets the field visible height;
   * @param visibleHeight The field visible height.
   */
  public void setApplicationConnection(ApplicationConnection client) {
    this.client = client;
  }
  
  /**
   * Returns the visible height
   * @return The visible height.
   */
  public int getVisibleHeight() {
    if (client == null) {
      return 1;
    }
    
    // Get it from state since the connector hierarchy is fired before state change event.
    return ConnectorUtils.getConnector(client, this, FieldConnector.class).getState().visibleHeight;
  }
  
  @Override
  public void setVisible(final boolean visible) {
    this.visible = visible;
    if (!visible) {
      getElement().getStyle().setVisibility(Visibility.HIDDEN);
      if (DOM.getParent(getElement()) != null) {
        DOM.getParent(getElement()).getStyle().setVisibility(Visibility.HIDDEN);
      }
    } else {
      getElement().getStyle().setVisibility(Visibility.VISIBLE);
      if (DOM.getParent(getElement()) != null) {
        DOM.getParent(getElement()).getStyle().setVisibility(Visibility.VISIBLE);
      }
    }
    if (objectField != null) {
      objectField.setParentVisibility(visible);
    }
  }
  
  @Override
  public boolean isVisible() {
    return visible;
  }
  
  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (incr != null) {
      incr.setEnabled(enabled);
    }
    if (decr != null) {
      incr.setEnabled(enabled);
    }
    if (!enabled) {
      addStyleName("v-disabled");
    } else {
      removeStyleName("v-disabled");
    }
    if (objectField != null) {
      objectField.setEnabled(enabled);
    }
  }
  
  /**
   * Registers a field listener.
   * @param l The listener to be registered.
   */
  public void addFieldListener(FieldListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a field listener.
   * @param l The listener to be removed.
   */
  public void removeFieldListener(FieldListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires an increment action.
   */
  protected void fireIncremented() {
    for (FieldListener l : listeners) {
      if (l != null) {
	l.onIncrement();
      }
    }
  }
  
  /**
   * Fires an decrement action.
   */
  protected void fireDecremented() {
    for (FieldListener l : listeners) {
      if (l != null) {
	l.onDecrement();
      }
    }
  }
  
  /**
   * Fires when this field is clicked.
   */
  protected void fireClicked() {
    for (FieldListener l : listeners) {
      if (l != null) {
	l.onClick();
      }
    }
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        if (incr != null && textField != null) {
          incr.getElement().getStyle().setHeight(textField.getElement().getClientHeight(), Unit.PX);
        }
        if (decr != null && textField != null) {
          decr.getElement().getStyle().setHeight(textField.getElement().getClientHeight(), Unit.PX);
        }
      }
    });
  }
  
  /**
   * Checks the content of this field.
   * @param rec The active record.
   * @throws CheckTypeException When the field content is not valid
   */
  public void checkValue(int rec) throws CheckTypeException {
    if (textField != null) {
      textField.checkValue(rec);
    } else if (objectField != null) {
      objectField.checkValue(rec);
    }
  }
  
  /**
   * Sets the value of the this field.
   * @param o The field value.
   */
  public void setValue(Object o) {
    if (textField != null) {
      textField.setValue(o);
    } else if (objectField != null) {
      objectField.setValue(o);
    }
  }
  
  /**
   * Sets the field background and foreground colors.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  public void setColor(String foreground, String background) {
    if (textField != null) {
      textField.setColor(foreground, background);
    } else if (objectField != null) {
      objectField.setColor(foreground, background);
    }
  }
  
  /**
   * Returns the field value.
   * @return The field value.
   */
  public Object getValue() {
    if (textField != null) {
      return textField.getValue();
    } else if (objectField != null) {
      return objectField.getValue();
    } else {
      return null;
    }
  }
  
  /**
   * Checks if the content of this field is empty.
   * @return {@code true} if this field is empty.
   */
  public boolean isNull() {
    if (textField != null) {
      return textField.isNull();
    } else if (objectField != null) {
      return objectField.isNull();
    } else {
      return true;
    }
  }
  
  /**
   * Gains the focus on this field.
   */
  public void focus() {
    if (textField != null) {
      textField.setFocus(true);
    } else if (objectField != null) {
      objectField.focus();
    }
  }
  
  /**
   * Returns the increment button width.
   * @return The increment button width.
   */
  protected int getIncrementButtonWidth() {
    if (incr != null) {
      return incr.getElement().getOffsetWidth() + 5;
    } else {
      return 0;
    }
  }
  
  /**
   * Returns the decrement button width.
   * @return The decrement button width.
   */
  protected int getDecrementButtonWidth() {
    if (decr != null) {
      return decr.getElement().getOffsetWidth();
    } else {
      return 0;
    }
  }
  
  /**
   * Returns the text field component width.
   * @return The text field component width.
   */
  protected int getTextFieldWidth() {
    if (textField != null) {
      return textField.getElement().getOffsetWidth();
    } else if (objectField != null) {
      return objectField.getElement().getOffsetWidth();
    } else {
      return 0;
    }
  }
  
  /**
   * Returns the buttons width.
   * @return The buttons width.
   */
  protected int getButtonsWidth() {
    return getIncrementButtonWidth() + getDecrementButtonWidth();
  }
  
  /**
   * Returns the estimated width of this field.
   * @return The estimated width of this field.
   */
  public int getWidth() {
    if (getElement().getOffsetWidth() >= getTextFieldWidth() + getButtonsWidth()) {
      return getElement().getOffsetWidth();
    } else {
      return getElement().getOffsetWidth() + getButtonsWidth();
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<FieldListener>		listeners;
  private boolean			enabled;
  private boolean                       visible;
  private VButton			incr;
  private VButton			decr;
  private VTextField			textField;
  private VObjectField			objectField;
  private ApplicationConnection		client;
}
