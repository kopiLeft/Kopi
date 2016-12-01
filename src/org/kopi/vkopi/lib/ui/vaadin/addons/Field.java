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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldState;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldState.NavigationDelegationMode;

import com.vaadin.ui.AbstractSingleComponentContainer;

/**
 * The field server component. Contains one text input field or other component
 * like image field.
 */
@SuppressWarnings("serial")
public class Field extends AbstractSingleComponentContainer {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the field server component.
   * @param hasIncrement has increment button ?
   * @param hasDecrement has decrement button ?
   */
  public Field(boolean hasIncrement, boolean hasDecrement) {
    listeners = new ArrayList<FieldListener>();
    registerRpc(rpc);
    getState().hasIncrement = hasIncrement;
    getState().hasDecrement = hasIncrement;
    setImmediate(true);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the default access of the field.
   * @param defaultAccess The field default access.
   */
  public void setDefaultAccess(int defaultAccess) {
    getState().defaultAccess = defaultAccess;
  }
  
  /**
   * Sets the dynamic access of the field.
   * @param dynAccess the dynamic access of the field.
   */
  public void setDynAccess(int dynAccess) {
    getState().dynAccess = dynAccess;
  }
  
  /**
   * Sets the field to be visible in chart view.
   * @param noChart The visibility in chart view.
   */
  public void setNoChart(boolean noChart) {
    getState().noChart = noChart;
  }
  
  /**
   * Sets the field to be visible in detail view.
   * @param noDetail The visibility in detail view.
   */
  public void setNoDetail(boolean noDetail) {
    getState().noDetail = noDetail;
  }
  
  /**
   * Sets the position of the field in the chart layout.
   * @param position The position of the field.
   */
  public void setPosition(int position) {
    getState().position = position;
  }
  
  /**
   * Sets the column view index of this field.
   * @param index The column view index.
   */
  public void setIndex(int index) {
    getState().index = index;
  }
  
  /**
   * Adds the given actors to this field.
   * @param actors The actors to be associated with field.
   */
  public void addActors(Collection<Actor> actors) {
    getState().actors.addAll(actors);
  }
  
  /**
   * Sets the navigation delegation to server mode for this field.
   * @param navigationDelegationMode The navigation delegation mode.
   */
  public void setNavigationDelegationMode(NavigationDelegationMode navigationDelegationMode) {
    getState().navigationDelegationMode = navigationDelegationMode;
  }
  
  /**
   * Sets this field to has a PREFLD trigger.
   * @param hasPreFieldTrigger The PREFDL trigger flag.
   */
  public void setHasPreFieldTrigger(boolean hasPreFieldTrigger) {
    getState().hasPreFieldTrigger = hasPreFieldTrigger;
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
   * Fired when this field is clicked.
   */
  protected void fireClicked() {
    for (FieldListener l : listeners) {
      if (l != null) {
	l.onClick();
      }
    }
  }
  
  /**
   * Fired when the focus is transferred to this field.
   */
  protected void fireFocusTransferred() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.transferFocus();
      }
    }
  }
  
  /**
   * Fired when a navigation to the next field event is detected.
   */
  protected void fireGotoNextField() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.gotoNextField();
      }
    }
  }
  
  /**
   * Fired when a navigation to the previous field event is detected.
   */
  protected void fireGotoPrevField() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.gotoPrevField();
      }
    }
  }
  
  /**
   * Fired when a navigation to the next empty must fill field event is detected.
   */
  protected void fireGotoNextEmptyMustfill() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.gotoNextEmptyMustfill();
      }
    }
  }
  
  /**
   * Fired when a navigation to the next record event is detected.
   */
  protected void fireGotoNextRecord() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.gotoNextRecord();
      }
    }
  }
  
  /**
   * Fired when a navigation to the previous record event is detected.
   */
  protected void fireGotoPrevRecord() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.gotoPrevRecord();
      }
    }
  }
  
  /**
   * Fired when a navigation to the first event is detected.
   */
  protected void fireGotoFirstRecord() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.gotoFirstRecord();
      }
    }
  }
  
  /**
   * Fired when a navigation to the last record event is detected.
   */
  protected void fireGotoLastRecord() {
    for (FieldListener l : listeners) {
      if (l != null) {
        l.gotoLastRecord();
      }
    }
  }
  
  @Override
  protected FieldState getState() {
    return (FieldState) super.getState();
  }
  
  @Override
  public void setVisible(boolean visible) {
    // use CSS styles to hide components.
    // in vaadin 7, invisible components are not sent to
    // client side. But we want that invisible components occupy
    // their place in the layout.
    if (getState().visible == visible) {
      return;
    }    
    getState().visible = visible;
  }
  
  @Override
  public boolean isVisible() {
    return true; // ensure that all fields are send to client side.
  }
  
  /**
   * Sets the field visible height.
   * @param visibleHeight The visible height.
   */
  public void setVisibleHeight(int visibleHeight) {
    getState().visibleHeight = visibleHeight;
  }
  
  @Override
  public boolean isConnectorEnabled() {
    return true;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<FieldListener>		listeners;
  private FieldServerRpc		rpc = new FieldServerRpc() {
    
    @Override
    public void onIncrement() {
      fireIncremented();
    }
    
    @Override
    public void onDecrement() {
      fireDecremented();
    }
    
    @Override
    public void onClick() {
      fireClicked();
    }

    @Override
    public void transferFocus() {
      fireFocusTransferred();
    }

    @Override
    public void gotoNextField() {
      fireGotoNextField();
    }

    @Override
    public void gotoPrevField() {
      fireGotoPrevField();
    }

    @Override
    public void gotoNextEmptyMustfill() {
      fireGotoNextEmptyMustfill();
    }

    @Override
    public void gotoNextRecord() {
      fireGotoNextRecord();
    }

    @Override
    public void gotoPrevRecord() {
      fireGotoPrevRecord();
    }

    @Override
    public void gotoFirstRecord() {
      fireGotoFirstRecord();
    }

    @Override
    public void gotoLastRecord() {
      fireGotoLastRecord();
    }
  };
}
