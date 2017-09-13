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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.HashMap;
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.addons.Field;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.ColumnView;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.FieldListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldState.NavigationDelegationMode;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.WindowConnector;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.VDragAndDropWrapper;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The field connector.
 */
@SuppressWarnings("serial")
@Connect(value = Field.class, loadStyle = LoadStyle.DEFERRED)
public class FieldConnector extends AbstractSingleComponentContainerConnector implements FieldListener {

  @Override
  protected void init() {
    super.init();
    getWidget().setApplicationConnection(getConnection());
    getWidget().addFieldListener(this);
  }
  
  @Override
  public VField getWidget() {
    return (VField) super.getWidget();
  }
  
  @Override
  public FieldState getState() {
    return (FieldState) super.getState();
  }
  
  @OnStateChange({"hasIncrement", "hasDecrement"})
  /*package*/ void iniWidget() {
    getWidget().init(getConnection(), getState().hasIncrement, getState().hasDecrement);
  }
  
  @OnStateChange("visible")
  /*package*/ void setVisible() {
    getWidget().setVisible(getState().visible);
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    // do not delegate caption handling
    return false;
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // not handled.
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    final Widget              content = getContentWidget();
    
    if (content instanceof VTextField) {
      getWidget().setTextField((VTextField)content);
    } else if (content instanceof VObjectField) {
      getWidget().setObjectField((VObjectField) content);
    } else if (content instanceof VDragAndDropWrapper) {
      getWidget().setDnDWrapper((VDragAndDropWrapper) content);
    } else if (content instanceof VRichTextField) {
      getWidget().setRichTextField((VRichTextField) content);
    }
  }
  
  @Override
  public ComponentConnector getContent() {
    return super.getContent();
  }

  @Override
  public void onIncrement() {
    getRpcProxy(FieldServerRpc.class).onIncrement();
  }

  @Override
  public void onDecrement() {
    getRpcProxy(FieldServerRpc.class).onDecrement();
  }

  @Override
  public void onClick() {
    // no click event is for rich text field
    if (getContent() instanceof RichTextFieldConnector) {
      return;
    }
    
    getColumnView().setBlockActiveRecordFromDisplayLine(getPosition());
    getWindow().cleanDirtyValues(getBlock(), false); //!! do not make a focus transfer.
    getRpcProxy(FieldServerRpc.class).onClick();
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
    dirtyValues = null;
    columnView.release();
    columnView = null;
  }
  
  /**
   * Leaves this field by performing validations that does not depend on server side.
   * @param rec The active record.
   * @throws CheckTypeException 
   */
  public void leave(int rec) throws CheckTypeException {
    if (!columnView.isBlockActiveField()) {
      throw new AssertionError("wrong active field");
    }
    
    if (changed) {
      getWidget().checkValue(rec);
    }
    
    if (!doNotLeaveActiveField) {
      columnView.unsetAsActiveField();
      setActorsEnabled(false);
      getColumnView().disableBlockActors();
    }
  }
  
  /**
   * Enters to this field. This will obtain the focus to this field.
   */
  public void enter() {
    if (doNotLeaveActiveField) {
      return;
    }
    
    if (columnView.getBlockActiveRecord() == -1) {
      throw new AssertionError("wrong active record");
    }
    if (!columnView.isBlockActiveFieldNull()) {
      throw new AssertionError("wrong active field");
    }
    changed = false;
    getWidget().focus();
    columnView.setAsActiveField();
    setActorsEnabled(true);
  }
  
  /**
   * Returns the field access for the given record.
   * @param record The concerned record.
   * @return The field access.
   */
  public int getAccess(int record) {
    if (record == -1) {
      return getState().defaultAccess;
    } else {
      return getState().dynAccess;
    }
  }
  
  /**
   * Returns {@code true} if this field has a PREFLD trigger.
   * @return {@code true} if this field has a PREFLD trigger.
   */
  public boolean hasPreFieldTrigger() {
    return getState().hasPreFieldTrigger;
  }
  
  /**
   * Checks if the navigation from this field should be delegated to server.
   * @return {@code true} if the navigation should be delegated to server.
   */
  public boolean delegateNavigationToServer() {
    if (getState().navigationDelegationMode == NavigationDelegationMode.ALWAYS) {
      return true;
    } else if (getState().navigationDelegationMode == NavigationDelegationMode.ONCHANGE) {
      return isChanged();
    } else if (getState().navigationDelegationMode == NavigationDelegationMode.ONVALUE) {
      return !isNull() || isChanged();
    } else {
      return false;
    }
  }
  
  /**
   * Returns {@code true} if the content of this field has changed.
   * @return {@code true} if the content of this field has changed.
   */
  public boolean isChanged() {
    return changed;
  }
  
  /**
   * Sets the content of this field to be changed.
   * @param changed The change flag.
   */
  public void setChanged(boolean changed) {
    this.changed = changed;
  }
  
  /**
   * Returns {@code true} if the field is never displayed in the detail view.
   * @return {@code true} if the field is never displayed in the detail view.
   */
  public boolean noDetail() {
    return getState().noDetail;
  }
  
  /**
   * Returns {@code true} if the field is never displayed in the chart view.
   * @return {@code true} if the field is never displayed in the chart view.
   */
  public boolean noChart() {
    return getState().noChart;
  }
  
  /**
   * Checks if this field is {@code null}.
   * @return {@code true} if this field is empty.
   */
  public boolean isNull() {
    return getWidget().isNull();
  }
  
  /**
   * Returns the server RPC attached with this connector.
   * @return The server RPC attached with this connector.
   */
  public FieldServerRpc getServerRpc() {
    return getRpcProxy(FieldServerRpc.class);
  }
  
  /**
   * Returns {@code true} if this connector is dirty.
   * @return {@code true} if this connector is dirty.
   */
  public boolean isDirty() {
    return dirty;
  }
  
  /**
   * Sets this field to not be a dirty one.
   */
  public void unsetDirty() {
    dirty = false;
    changed = false;
  }
  
  /**
   * Marks the value of this field to be dirty for its current value.
   * @param rec The concerned record number.
   */
  public void markAsDirty(int rec) {
    markAsDirty(rec, getWidget().getValue() == null ? "" : getWidget().getValue().toString());
  }
  
  /**
   * Marks the given text field connector to be dirty.
   * This means that its value should be synchronized
   * with the server as soon as possible.
   * @param rec The value record.
   * @param value The text value to be sent for the given record
   */
  protected void markAsDirty(int rec, String value) {
    if (dirtyValues == null) {
      dirtyValues = new HashMap<Integer, String>();
    }
    
    if (rec != -1) {
      dirtyValues.put(rec, value);
      // set internal cached value
      getColumnView().setValueAt(rec, value);
      dirty = true;
    }
  }
  
  /**
   * Sets the cached value of this field for the given record.
   * @param rec The record number.
   * @param value The text value.
   */
  protected void setCachedValueAt(int rec, String value) {
    if (!getColumnView().getRecordValueAt(rec).equals(value) && rec != -1) {
      getColumnView().setValueAt(rec, value);
      changed = true;
    }
  }
  
  /**
   * Returns the field cached value at the given record.
   * @param rec The record number.
   * @return The cached value.
   */
  protected String getCachedValueAt(int rec) {
    return getColumnView().getRecordValueAt(rec);
  }
  
  /**
   * Sets the actors associated with this field to be enabled or disabled.
   * @param enabled The enabled status
   */
  public void setActorsEnabled(boolean enabled) {
    WindowConnector             window;
    
    window = ConnectorUtils.getParent(this, WindowConnector.class);
    for (Connector actor : getState().actors) {
      window.setActorEnabled(actor, enabled);
    }
  }
  
  /**
   * Cleans the dirty values. This will send all buffered values
   * for the server side.
   */
  public void cleanDirtyValues() {
    if (!isEnabled()) {
      return;
    }
    if (dirtyValues != null && !dirtyValues.isEmpty()) {
      ((TextFieldConnector)getContent()).sendTextToServer();
      ((TextFieldConnector)getContent()).sendDirtyValuesToServer(new HashMap<Integer, String>(dirtyValues));
      dirtyValues.clear();
    }
    dirty = false;
  }
  
  /**
   * Updates the value of this field according to its position.
   */
  public void updateValue() {
    getWidget().setValue(getColumnView().getValueAt(getPosition()));
  }
  
  /**
   * Updates the color of this field according to its position.
   */
  public void updateColor() {
    getWidget().setColor(getColumnView().getForegroundColorAt(getPosition()),
                         getColumnView().getBackgroundColorAt(getPosition()));
  }
  
  /**
   * Sets the field position.
   * @param position The new field position.
   */
  public void setPosition(int position) {
    getState().position = position;
  }
  
  /**
   * Returns the field position.
   * @return The field position.
   */
  public int getPosition() {
    return getState().position;
  }
  
  /**
   * Returns the column view index of this field.
   * @return The column view index of this field.
   */
  public int getColumnViewIndex() {
    return getState().index;
  }
  
  /**
   * Sets the column view of this field.
   * @param columnView The column view of this field.
   */
  public void setColumnView(ColumnView columnView) {
    this.columnView = columnView;
  }
  
  /**
   * Returns the column view where this field belong to.
   * @return the column view where this field belong to.
   */
  public ColumnView getColumnView() {
    return columnView;
  }
  
  /**
   * Returns the parent window of this field.
   * @return The parent window of this field.
   */
  protected WindowConnector getWindow() {
    return ConnectorUtils.getParent(this, WindowConnector.class);
  }
  
  /**
   * Returns the parent block of this field.
   * @return The parent block of this field.
   */
  protected BlockConnector getBlock() {
    return ConnectorUtils.getParent(this, BlockConnector.class);
  }
  
  /**
   * Enables and disables the leave action of the active field.
   * This is used to simulate the modal popups that blocks the execution
   * Thread since Javascript can not handle multi thread and it is a single threaded.
   * @param doNotLeaveActiveField Should we leave the active field.
   */
  public static void setDoNotLeaveActiveField(boolean doNotLeaveActiveField) {
    FieldConnector.doNotLeaveActiveField = doNotLeaveActiveField;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean                               changed;
  private boolean                               dirty;
  private Map<Integer, String>                  dirtyValues;
  private ColumnView                            columnView;
  private static boolean                        doNotLeaveActiveField;
}
