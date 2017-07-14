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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VConstants;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.CheckTypeException;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.FormConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.LabelConnector;

/**
 * A column view represents the display entity of a field for each record
 * in a block model. This will collect all field connector that represents
 * the same field but in different records.
 */
public class ColumnView {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public ColumnView(BlockConnector block) {
    displays = new FieldConnector[block.getState().displaySize];
    values = new String[block.getState().bufferSize];
    fgColors = new String[block.getState().bufferSize];
    bgColors = new String[block.getState().bufferSize];
    index = -1;
    this.block = block;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the label of this column view.
   * @param label The label of this column.
   */
  protected void setLabel(LabelConnector label) {
    this.label = label;
  }
  
  /**
   * Sets the detail label of this column view.
   * @param detailLabel The detail label.
   */
  protected void setDetailLabel(LabelConnector detailLabel) {
    this.detailLabel = detailLabel;
  }
  
  /**
   * Sets the detail display of this column view.
   * @param detailDisplay The detail display field.
   */
  protected void setDetailDisplay(FieldConnector detailDisplay) {
    this.detailDisplay = detailDisplay;
    this.detailDisplay.setColumnView(this);
    if (index == -1) {
      index = detailDisplay.getColumnViewIndex();
    }
  }
  
  /**
   * Appends the given field to the column view collector.
   * @param field The field to be added.
   */
  protected void addField(FieldConnector field) {
    displays[field.getPosition()] = field;
    field.setColumnView(this);
    index = field.getColumnViewIndex();
  }
  
  /**
   * Returns the field for the given display line.
   * @param displayLine The display line.
   * @return The field object.
   */
  protected FieldConnector getField(int displayLine) {
    if (displayLine != -1) {
      return displays[displayLine];
    } else {
      return null;
    }
  }
  
  /**
   * Returns {@code true} if this column view has a PREFLD trigger.
   * @return {@code true} if this column view has a PREFLD trigger.
   */
  protected boolean hasPreFieldTrigger() {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        return detailDisplay.hasPreFieldTrigger();
      }
    } else {
      if (block.getDisplayLine() != -1) {
        return displays[block.getDisplayLine()].hasPreFieldTrigger();
      }
    }
    
    return false;
  }
  
  /**
   * Returns the access of the column view for the block display line.
   * @return The access of the column view.
   */
  protected int getAccess() {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        return detailDisplay.getAccess(block.getActiveRecord());
      }
    } else {
      if (block.getDisplayLine() != -1) {
        return displays[block.getDisplayLine()].getAccess(block.getActiveRecord());
      }
    }

    return VConstants.ACS_HIDDEN;
  }
  
  /**
   * Checks if the column view is no chart for the block display line.
   * @return {@code true} if the field in the display line has no chart option.
   */
  protected boolean noChart() {
    if (block.getDisplayLine() != -1) {
      return displays[block.getDisplayLine()].noChart();
    } else {
      return false;
    }
  }
  
  /**
   * Checks if the column view is no detail for the block display line.
   * @return {@code true} if the field in the display line has no detail option.
   */
  protected boolean noDetail() {
    if (block.getDisplayLine() != -1) {
      return displays[block.getDisplayLine()].noDetail();
    } else {
      return false;
    }
  }
  
  /**
   * Checks if the navigation from this column view should be delegated to server.
   * @return {@code true} if the navigation should be delegated to server.
   */
  protected boolean delegateNavigationToServer() {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        return detailDisplay.delegateNavigationToServer();
      }
    } else {
      if (block.getDisplayLine() != -1) {
        return displays[block.getDisplayLine()].delegateNavigationToServer();
      }
    }
    
    return false;
  }
  
  /**
   * Tells the server side that the focused field is the active one
   * in the client side.
   */
  protected void transferFocus() {
    if (getServerRpc() != null) {
      getServerRpc().transferFocus();
    }
  }
  
  /**
   * Returns the server RPC handler for the column view.
   * @return The server RPC handler.
   */
  protected FieldServerRpc getServerRpc() {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        return detailDisplay.getServerRpc();
      }
    } else {
      if (block.getDisplayLine() != -1) {
        return displays[block.getDisplayLine()].getServerRpc();
      }
    }
    
    return null;
  }
  
  /**
   * Leaves the field in the given display line.
   * @param rec The concerned record.
   * @throws CheckTypeException  When type check fails.
   */
  protected void leave(int rec) throws CheckTypeException {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        detailDisplay.leave(rec);
      }
    } else {
      if (block.getDisplayLine() != -1) {
        displays[block.getDisplayLine()].leave(rec);
      }
    }
  }
  
  /**
   * Enters to this column view.
   * This will gain the focus to the display line field in this column view.
   */
  protected void enter() {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        detailDisplay.enter();
      }
    } else {
      if (block.getDisplayLine() != -1) {
        displays[block.getDisplayLine()].enter();
      }
    }
  }
  
  /**
   * Checks if the current display line is null for this column view.
   * @return {@code true} when the current display line is null for this column view.
   */
  protected boolean isNull() {
    if (block.getDisplayLine() != -1) {
      return displays[block.getDisplayLine()].isNull();
    } else {
      return true;
    }
  }
  
  /**
   * Cleans the dirty values of this column view.
   */
  protected void cleanDirtyValues() {
    if (displays == null) {
      return;
    }
    for (FieldConnector field : displays) {
      if (field != null && field.isDirty()) {
        field.cleanDirtyValues();
      }
    }
    if (detailDisplay != null && detailDisplay.isDirty()) {
      detailDisplay.cleanDirtyValues();
    }
  }
  
  /**
   * Returns {@code true} if the column view has at least one dirty field.
   * @return {@code true} if the column view has at least one dirty field.
   */
  protected boolean isDirty() {
    if (displays == null) {
      return false;
    }
    
    for (FieldConnector field : displays) {
      if (field != null && field.isDirty()) {
        return true;
      }
    }

    return detailDisplay != null && detailDisplay.isDirty();
  }
  
  /**
   * Updates the current block display line field value
   */
  protected void updateValue() {
    if (block.getDisplayLine() != -1) {
      displays[block.getDisplayLine()].updateValue();
    }
    // update detail display value
    if (detailDisplay != null) {
      detailDisplay.updateValue();
    }
  }
  
  /**
   * Updates the current block display line field color
   */
  protected void updateColor() {
    if (block.getDisplayLine() != -1) {
      displays[block.getDisplayLine()].updateColor();
    }
    // update detail display color
    if (detailDisplay != null) {
      detailDisplay.updateColor();
    }
  }
  
  /**
   * Updates the current block display line field value
   * @param record The record number.
   */
  protected void updateValue(int record) {
    int         displayLine = block.getDisplayLine(record);

    if (displayLine != -1) {
      displays[displayLine].updateValue();
    }
    // update detail display value
    if (detailDisplay != null) {
      detailDisplay.updateValue();
    }
  }
  
  /**
   * Updates the current block display line field color.
   * @param record The record number.
   */
  protected void updateColor(int record) {
    int         displayLine = block.getDisplayLine(record);
      
    if (displayLine != -1) {
      displays[displayLine].updateColor();
    }
    // update detail display color
    if (detailDisplay != null) {
      detailDisplay.updateColor();
    }
  }
  
  /**
   * Checks the value of the current display.
   * @param rec The concerned record.
   * @throws CheckTypeException  When type check fails.
   */
  protected void checkValue(int rec) throws CheckTypeException {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        detailDisplay.getWidget().checkValue(rec);
      } 
    } else {
      if (block.getDisplayLine() != -1) {
        if (displays[block.getDisplayLine()].isChanged()) {
          displays[block.getDisplayLine()].getWidget().checkValue(rec);
        }
      }
    }
  }
  
  /**
   * Checks if the current field of this column view has a dirty value.
   * @param rec The current record number.
   */
  protected void maybeHasDirtyValues(int rec) {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        detailDisplay.markAsDirty(rec);
      }
    } else {
      if (block.getDisplayLine() != -1) {
        if (displays[block.getDisplayLine()].isChanged()) {
          displays[block.getDisplayLine()].markAsDirty(rec);
        }
      }
    }
  }
  
  /**
   * Returns the column view index;
   * @return The column view index.
   */
  protected int getIndex() {
    return index;
  }
  
  /**
   * Sets the value of this column view in the given record number.
   * @param record The record number.
   * @param value The record value.
   */
  public void setValueAt(int record, String newValue) {
    String              oldValue;
    
    oldValue = values[record];
    if (oldValue == null) {
      oldValue = "";
    }
    if (newValue == null) {
      newValue = "";
    }
    if (!newValue.equals(oldValue)) {
      values[record] = newValue;
      setRecordChanged(record, true);
    }
  }
  
  /**
   * Sets the foreground color of this column view.
   * @param record The record number.
   * @param color The color value.
   */
  public void setForegroundColorAt(int record, String color) {
    fgColors[record] = color;
  }
  
  /**
   * Sets the background color of this column view.
   * @param record The record number.
   * @param color The color value.
   */
  public void setBackgroundColorAt(int record, String color) {
    bgColors[record] = color;
  }
  
  /**
   * Returns the value at the given display line.
   * @param displayLine The record number.
   * @return The value at the given display line.
   */
  public String getValueAt(int displayLine) {
    return values[getRecordFromDisplayLine(displayLine)];
  }
  
  /**
   * Returns the foreground color at the given display line.
   * @param displayLine The record number.
   * @return The foreground color at the given display line.
   */
  public String getForegroundColorAt(int displayLine) {
    return fgColors[getRecordFromDisplayLine(displayLine)];
  }
  
  /**
   * Returns the background color at the given display line.
   * @param displayLine The record number.
   * @return The background color at the given display line.
   */
  public String getBackgroundColorAt(int displayLine) {
    return bgColors[getRecordFromDisplayLine(displayLine)];
  }
  
  /**
   * Returns the record for the given display line.
   * @param displayLine The display line.
   * @return The record number.
   */
  public int getRecordFromDisplayLine(int displayLine) {
    if (block != null) {
      return block.getRecordFromDisplayLine(displayLine);
    } else {
      return -1;
    }
  }
  
  /**
   * Returns the column view value at the given record.
   * @param record The record number.
   * @return The column view value.
   */
  public String getRecordValueAt(int record) {
    return values[record];
  }
  
  /**
   * Scrolls to the given to record.
   * @param toprec The record to be scrolled to
   */
  public void scrollTo(int toprec) {
    if (displays == null) {
      return;
    }

    for (FieldConnector field : displays) {
      if (field != null) {
        field.updateValue();
        field.updateColor();
      }
    }
    if (detailDisplay != null) {
      int       record = block.getActiveRecord();
      // is there no active line, show the same content then the first row
      // in the chart
      int       dispLine = (record >= 0) ? block.getDisplayLine(record) : 0;
      // is the active line, is not in the visible part then show the same
      // content then in the first line of the chart
      if (dispLine < 0) {
        dispLine = 0;
      }

      detailDisplay.setPosition(dispLine);
      detailDisplay.updateValue();
      detailDisplay.updateColor();
    }
  }
  
  /**
   * Sets this column view as the active field.
   */
  public void setAsActiveField() {
    setAsActiveField(-1);
  }
  
  /**
   * Sets this column view as the active field.
   * @param rec The record of the active field
   */
  public void setAsActiveField(int rec) {
    if (block != null) {
      block.setActiveField(this);
      if (rec != -1) {
        block.setActiveRecord(rec);
      }
    }
  }
  
  /**
   * Tells the block that this column is not the active field anymore.
   */
  public void unsetAsActiveField() {
    if (block != null) {
      block.setActiveField(null);
    }
  }
  
  /**
   * Checks if this column view is the active block field.
   * @return {@code true} if this column view is the block active field.
   */
  public boolean isBlockActiveField() {
    return block.getActiveField() == this;
  }
  
  /**
   * Checks if the active field of the block containing this field view is {@code null}.
   * @return {@code true} if the active field of the block containing this field view is {@code null}.
   */
  public boolean isBlockActiveFieldNull() {
    return block.getActiveField() == null;
  }
  
  /**
   * Returns the block active record.
   * @return The block active record.
   */
  public int getBlockActiveRecord() {
    return block.getActiveRecord();
  }
  
  /**
   * Returns the block old active record.
   * @return The block old active record.
   */
  public int getBlockOldActiveRecord() {
    return block.getOldActiveRecord();
  }
  
  /**
   * Navigates to the next field in container block.
   */
  public void gotoNextField() {
    if (block != null) {
      block.gotoNextField();
    }
  }
  
  /**
   * Navigates to the previous field in container block.
   */
  public void gotoPrevField() {
    if (block != null) {
      block.gotoPrevField();
    }
  }
  
  /**
   * Navigates to next empty must fill field in container block.
   */
  public void gotoNextEmptyMustfill() {
    if (block != null) {
      block.gotoNextEmptyMustfill();
    }
  }
  
  /**
   * Navigates to the next record in container block.
   */
  public void gotoNextRecord() {
    if (block != null) {
      block.gotoNextRecord();
    }
  }
  
  /**
   * Navigates to the previous record in container block.
   */
  public void gotoPrevRecord() {
    if (block != null) {
      block.gotoPrevRecord();
    }
  }
  
  /**
   * Navigates to the first record in container block.
   */
  public void gotoFirstRecord() {
    if (block != null) {
      block.gotoFirstRecord();
    }
  }
  
  /**
   * Navigates to the last record in container block.
   */
  public void gotoLastRecord() {
    if (block != null) {
      block.gotoLastRecord();
    }
  }
  
  /**
   * Sets the given record to be changed.
   * @param rec The record number.
   * @param val The change value.
   */
  public void setRecordChanged(int rec, boolean val) {
    if (block != null) {
      block.setRecordChanged(rec, val);
    }
  }
  
  /**
   * Sets the given record to be fetched.
   * @param rec The record number.
   * @param val The fetch value.
   */
  public void setRecordFetched(int rec, boolean val) {
    if (block != null) {
      block.setRecordFetched(rec, val);
    }
  }
  
  /**
   * Sets the block active record from a given display line.
   * @param displayLine The display line.
   */
  public void setBlockActiveRecordFromDisplayLine(int displayLine) {
    if (block != null) {
      block.setActiveRecordFromDisplay(displayLine);
    }
  }
  
  /**
   * Returns {@code true} if we are in multiple block context.
   * @return {@code true} if we are in multiple block context.
   */
  public boolean isMultiBlock() {
    return block == null ? false : block.isMulti();
  }
  
  /**
   * Returns the label associated with this column view.
   * @return The label associated with this column view.
   */
  public LabelConnector getLabel() {
    return label;
  }
  
  /**
   * Returns the detail label associated with this column view.
   * @return The detail label associated with this column view.
   */
  public LabelConnector getDetailLabel() {
    return detailLabel;
  }
  
  /**
   * Returns {@code true} if this column view has auto fill feature.
   * @return {@code true} if this column view has auto fill feature.
   */
  public boolean hasAutofill() {
    boolean             hasAutofill;
    
    hasAutofill = false;
    if (label != null) {
      hasAutofill |= label.hasAction();
    }
    if (detailLabel != null) {
      hasAutofill |= detailLabel.hasAction();
    }
    
    return hasAutofill;
  }
  
  /**
   * Sets the actors ability of this column view.
   * @param enabled The actors ability.
   */
  public void setActorsEnabled(boolean enabled) {
    if (displays == null) {
      return;
    }
    
    for (FieldConnector display : displays) {
      if (display != null) {
        display.setActorsEnabled(enabled);
      }
    }
    if (detailDisplay != null) {
      detailDisplay.setActorsEnabled(enabled);
    }
  }
  
  /**
   * Disables all block column view actors.
   */
  public void disableBlockActors() {
    if (block != null) {
      block.setColumnViewsActorsEnabled(false);
    }
  }
  
  /**
   * Disables all blocks actors
   */
  public void disableAllBlocksActors() {
    ConnectorUtils.getParent(block, FormConnector.class).disableAllBlocksActors();
  }
  
  /**
   * Releases the content of this column view
   */
  public void release() {
    label = null;
    displays = null;
    detailLabel = null;
    values = null;
    fgColors = null;
    bgColors = null;
    if (block != null) {
      block.clearFields();
    }
    block = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private LabelConnector                        label;
  private FieldConnector[]                      displays;
  private LabelConnector                        detailLabel;
  private FieldConnector                        detailDisplay;
  private String[]                              values;
  private String[]                              fgColors;
  private String[]                              bgColors;
  private BlockConnector                        block;
  private int                                   index;;
}
