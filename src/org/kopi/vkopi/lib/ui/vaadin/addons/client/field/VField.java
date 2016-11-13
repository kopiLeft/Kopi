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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Icons;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
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
import com.vaadin.client.ConnectorMap;
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
    if (hasDecrement) {
      decr = new VButton();
      decr.setImage(ResourcesUtil.getThemeURL(connection, Icons.DECREMENT));
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
      incr.setImage(ResourcesUtil.getThemeURL(connection, Icons.INCREMENT));
      add(incr);
      incr.addClickHandler(new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          fireIncremented();
        }
      });
    }
    
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
  
  /**
   * Sets the field visible height;
   * @param visibleHeight The field visible height.
   */
  public void setApplicationConnectiont(ApplicationConnection client) {
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
    return ((FieldConnector)ConnectorMap.get(client).getConnector(this)).getState().visibleHeight;
  }
  
  @Override
  public void setVisible(final boolean visible) {
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
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
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

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<FieldListener>		listeners;
  private boolean			enabled;
  private VButton			incr;
  private VButton			decr;
  private VTextField			textField;
  private VObjectField			objectField;
  private ApplicationConnection		client;
}
