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

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.Block;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VConstants;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockState.CachedColor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockState.CachedValue;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockState.RecordInfo;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.CheckTypeException;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.FormConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.notification.NotificationUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.VDragAndDropWrapper;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The block component connector.
 */
@SuppressWarnings("serial")
@Connect(value = Block.class, loadStyle = LoadStyle.DEFERRED)
public class BlockConnector extends AbstractSingleComponentContainerConnector implements ValueChangeHandler<Integer> {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    fields = new ArrayList<ColumnView>();
    registerRpc(BlockClientRpc.class, rpc);
  }
  
  @Override
  public VBlock getWidget() {
    return (VBlock) super.getWidget();
  }
  
  @Override
  public BlockState getState() {
    return (BlockState) super.getState();
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // not handled.
  }
  
  @OnStateChange("caption")
  /*package*/ void setCaption() {
    getWidget().setCaption(getState().caption);
  }
  
  @OnStateChange({"scrollPageSize", "maxScrollValue", "enableScroll", "scrollValue"})
  /*package*/ void updateScroll() {
    if (getLayout() != null) {
      getLayout().updateScroll(getState().scrollPageSize,
                               getState().maxScrollValue,
                               getState().enableScroll,
                               getState().scrollValue);
    }
  }
  
  @OnStateChange("displaySize")
  /*package*/ void setDisplaySize() {
    setDetailMode(getState().displaySize == 1);
  }
  
  @OnStateChange("activeRecord")
  /*package*/ void updateActiveRecord() {
    if (initialized) {
      setActiveRecord(getState().activeRecord);
      fireActiveRecordChanged();
      refresh(false);
    }
  }
  
  @OnStateChange("recordInfo")
  /*package*/ void updateRecordInfo() {
    if (initialized && !getState().recordInfo.isEmpty()) {
      for (RecordInfo info : getState().recordInfo) {
        recordInfo[info.rec] = info.value;
      }
      getRpcProxy(BlockServerRpc.class).clearRecordInfo(getState().recordInfo);
      // forces the display after a record info update
      refresh(true);
    }
  }
  
  @OnStateChange({"bufferSize", "displaySize"})
  /*package*/ void setBufferSize() {
    recordInfo = new int[2 * getState().bufferSize];
    if (isMulti()) {
      sortedRecords  = new int[getState().bufferSize];
      setActiveRecord(-1);
      sortedRecToDisplay = new int[getState().bufferSize];
      displayToSortedRec = new int[getState().displaySize];
    } else {
      sortedRecords  = new int[1];
      activeRecord = 0;
      sortedRecToDisplay = new int[1];
      displayToSortedRec = new int[1];
    }
    // create the default order
    for (int i = 0; i < sortedRecords.length; i++) {
      sortedRecords[i] = i;
    }
    addRecordPositionPanel();
    // build the cache buffers
    rebuildCachedInfos();
    initialized = true;
  }
  
  @OnStateChange("cachedValues")
  /*package*/ void updateCachedValues() {
    if (!initialized) {
      return; 
    }
    
    if (!getState().cachedValues.isEmpty()) {
      setCachedValues(getState().cachedValues);
      getRpcProxy(BlockServerRpc.class).clearCachedValues(getState().cachedValues);
    }
  }
  
  @OnStateChange("cachedColors")
  /*package*/ void updateCachedColors() {
    if (!initialized) {
      return; 
    }
    
    if (!getState().cachedColors.isEmpty()) {
      setCachedColors(getState().cachedColors);
      getRpcProxy(BlockServerRpc.class).clearCachedColors(getState().cachedColors);
    }
  }
  
  @OnStateChange("sortedRecords")
  /*package*/ void updateOrder() {
    if (initialized) {
      sortedRecords = getState().sortedRecords;
      refresh(true);
    }
  }
  
  /**
   * Sets the cached values of the block fields.
   */
  private void setCachedValues(List<CachedValue> cachedValues) {
    for (CachedValue cachedValue : cachedValues) {
      setCachedValue(cachedValue.col, cachedValue.rec, cachedValue.value);
    }
  }
  
  /**
   * Sets the cached colors of the block fields.
   */
  private void setCachedColors(List<CachedColor> cachedColors) {
    for (CachedColor cachedColor : cachedColors) {
      setCachedColors(cachedColor.col, cachedColor.rec, cachedColor.foreground, cachedColor.background);
    }
  }
  
  /**
   * Sets the cached values for the given column.
   * @param column The column view number.
   * @param rec The record number.
   * @param value The column value.
   */
  private void setCachedValue(int column, int rec, String value) {
    ColumnView              field;

    field = fields.get(column);
    if (field != null) {
      field.setValueAt(rec, value);
      field.updateValue(rec);
    }
  }
  
  /**
   * Sets the cached colors of a given column view.
   * @param column The column index.
   * @param rec The record number.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  private void setCachedColors(int column, int rec, String foreground, String background) {
    ColumnView              field;

    field = fields.get(column);
    if (field != null) {
      field.setForegroundColorAt(rec, foreground);
      field.setBackgroundColorAt(rec, background);
      field.updateColor(rec);
    }
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
    layout = null;
    dndWrapper = null;
    for (ColumnView field : fields) {
      field.release();
    }
    fields.clear();
    fields = null;
    activeField = null;
    sortedRecords = null;
    recordInfo = null;
    sortedRecToDisplay = null;
    displayToSortedRec = null;
  }
  
  /**
   * Handles the content widget.
   */
  protected void handleContentWidget() {
    Widget	content = getContentWidget();
    
    if (content instanceof VDragAndDropWrapper) {
      dndWrapper = (VDragAndDropWrapper)content;
    } else if (content instanceof BlockLayout) {
      layout = (BlockLayout) content;
    }
  }
  
  /**
   * Sets the block content.
   */
  protected void setContent() {
    if (dndWrapper != null) {
      getWidget().setContent(dndWrapper);
    } else {
      getWidget().setContent(layout.cast());
    }
  }
  
  /**
   * Returns the block layout.
   * @return The block layout;
   */
  protected BlockLayout getLayout() {
    return layout;
  }
  
  /**
   * Appends the given field to the block field list.
   * @param field The field to be added to block fields.
   */
  protected void addField(ColumnView field) {
    if (fields == null) {
      fields = new ArrayList<ColumnView>();
    }
    
    if (field.getIndex() != -1) {
      if (field.getIndex() > fields.size()) {
        fields.add(field);
      } else {
        fields.add(field.getIndex(), field);
      }
    }
  }
  
  /**
   * Returns the column view of the given index.
   * @param index The column view index.
   * @return The column view.
   */
  protected ColumnView getField(int index) {
    if (index >= fields.size()) {
      return null;
    } else {
      return fields.get(index);
    }
  }
  
  /**
   * Sets the ability of the hole block actors for column views.
   * @param enabled The actors ability.
   */
  public void setColumnViewsActorsEnabled(boolean enabled) {
    for (ColumnView field : fields) {
      if (field != null) {
        field.setActorsEnabled(enabled);
      }
    }
  }
  
  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    handleContentWidget();
    setContent();
    if (getLayout() != null) {
      getWidget().setLayout(getLayout());
      if (getLayout() instanceof VChartBlockLayout) {
	((VChartBlockLayout)getLayout()).addValueChangeHandler(this);
      }
    }
  }

  @Override
  public void onValueChange(ValueChangeEvent<Integer> event) {
    // when active record is set from the display. It means that
    // one block field has fired a click event. Extra scroll event
    // are blocked cause some FF version fire scroll events with
    // wrong scroll position and force UI to refresh and change its
    // content.
    if (activeRecordSetFromDisplay) {
      activeRecordSetFromDisplay = false;
    } else {
      setScrollPos(event.getValue());
    }
  }
  
  /**
   * Called when a column view is unregistered from a grid row detail.
   * We need to clear block fields cause layout was unregistered
   */
  protected void clearFields() {
    if (isLayoutBelongsToGridDetail() && fields != null) {
      fields.clear();
    }
  }
  
  //---------------------------------------------------
  // NAVIGATION
  //---------------------------------------------------
  
  /**
   * Navigates to the next field in this block.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  public void gotoNextField() {
    if (activeField == null) {
      return;
    }
    // first check if the navigation should be delegated to server side.
    if (activeField.delegateNavigationToServer() && activeField.getServerRpc() != null) {
      activeField.getServerRpc().gotoNextField();
    } else {
      int               index = fields.indexOf(activeField);
      ColumnView        target = null;
      ColumnView        old = activeField;
      
      for (int i = 0; target == null && i < fields.size(); i += 1) {
        index += 1;
        if (index == fields.size()) {
          index = 0;
        }
        if (fields.get(index) != null
            && !fields.get(index).hasAction()
            && fields.get(index).getAccess() >= VConstants.ACS_VISIT
            && ((detailMode && !fields.get(index).noDetail())
                || (!detailMode && !fields.get(index).noChart())))
        {
          target = fields.get(index);
        }
      }
      
      if (target == null) {
        old.enter();
      } else if (target.hasPreFieldTrigger() && activeField.getServerRpc() != null) {
        // the target field has a PREFLD trigger
        // delegate navigation to server side.
        activeField.getServerRpc().transferFocus();
        activeField.getServerRpc().gotoNextField();
      } else {
        // leave the active field
        try {
          activeField.leave(getActiveRecord());
        } catch (CheckTypeException e) {
          e.displayError();
          return;
        }
        // focus to the new one
        target.enter();
      }
    }
  }
  
  /**
   * Navigates to the previous field in this block.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  public void gotoPrevField() {
    if (activeField == null) {
      return;
    }
    if (activeField.delegateNavigationToServer() && activeField.getServerRpc() != null) {
      activeField.getServerRpc().gotoPrevField();
    } else {
      int               index = fields.indexOf(activeField);
      ColumnView        target = null;
      ColumnView        old = activeField;

      for (int i = 0; target == null && i < fields.size(); i += 1) {
        if (index == 0) {
          index = fields.size();
        }
        index -= 1;
        if (fields.get(index) != null
            && !fields.get(index).hasAction()
            && fields.get(index).getAccess() >= VConstants.ACS_VISIT
            && ((detailMode && !fields.get(index).noDetail())
                || (!detailMode && !fields.get(index).noChart())))
        {
          target = fields.get(index);
        }
      }
      
      if (target == null) {
        old.enter();
      } else if (target.hasPreFieldTrigger() && activeField.getServerRpc() != null) {
        // the target field has a PREFLD trigger
        // delegate navigation to server side.
        activeField.getServerRpc().transferFocus();
        activeField.getServerRpc().gotoPrevField();
      } else {
        // leave the active field
        try {
          activeField.leave(getActiveRecord());
        } catch (CheckTypeException e) {
          e.displayError();
          return;
        }
        // focus to the new one
        target.enter();
      }
    }
  }
  
  /**
   * Navigates to the next empty must fill field in this block.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  public void gotoNextEmptyMustfill() {
    ColumnView          current = activeField;

    if (activeField != null && activeField.delegateNavigationToServer()) {
      if (activeField.getServerRpc() != null) {
        activeField.getServerRpc().gotoNextEmptyMustfill();
      }
    } else {
      if (activeField != null) {
        try {
          activeField.leave(getActiveRecord());
        } catch (CheckTypeException e) {
          e.displayError();
          return;
        }
      } else {
        gotoFirstUnfilledField();
        return;
      }

      ColumnView        target = null;
      int               i;

      // found field
      for (i = 0;  i < fields.size() && fields.get(i) != current; i += 1) {
        // loop
      }
      if (i >= fields.size()) {
        return;
      }
      i += 1;
      // walk next to next
      for (; target == null && i < fields.size(); i += 1) {
        if (fields.get(i) != null
            && !fields.get(i).hasAction()
            && fields.get(i).getAccess() == VConstants.ACS_MUSTFILL
            && fields.get(i).isNull())
        {
          target = fields.get(i);
        }
      }

      // redo from start
      for (i = 0; target == null && i < fields.size(); i += 1) {
        if (fields.get(i) != null && fields.get(i).getAccess() == VConstants.ACS_MUSTFILL && fields.get(i).isNull()) {
          target = fields.get(i);
        }
      }

      if (target == null) {
        gotoFirstUnfilledField();
      } else {
        target.enter();
      }
    }
  }
  
  /**
   * Navigates to the next record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  public void gotoNextRecord() {
    if (activeField == null) {
      return;
    }

    // first check if the navigation should be delegated to server side.
    if (isLayoutBelongsToGridDetail() || activeField.delegateNavigationToServer() && activeField.getServerRpc() != null) {
      activeField.getServerRpc().gotoNextRecord();
    } else {
      if (isMulti() && !detailMode) {
        int               currentRec = getActiveRecord();
        int               i;

        if (currentRec == -1) {
          return;
        }

        // get position in sorted order
        currentRec = getSortedPosition(currentRec);
        /* search target record*/
        for (i =  currentRec + 1; i < getState().bufferSize; i += 1) {
          if (!isSortedRecordDeleted(i)) {
            break;
          }
        }

        if (i == getState().bufferSize || !isRecordAccessible(getDataPosition(i))) {
          NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00015");
        } else {
          // get position in data of next record in sorted order
          changeActiveRecord(getDataPosition(i));
        }
      } else if (activeField.getServerRpc() != null) {
        activeField.getServerRpc().gotoNextRecord();
      }
    }
  }
  
  /**
   * Navigates to the previous record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  public void gotoPrevRecord() {
    if (activeField == null) {
      return;
    }
    // first check if the navigation should be delegated to server side.
    if (isLayoutBelongsToGridDetail() || activeField.delegateNavigationToServer() && activeField.getServerRpc() != null) {
      activeField.getServerRpc().gotoPrevRecord();
    } else {
      if (isMulti() && !detailMode) {
        int               currentRec = getActiveRecord();
        int               i;

        if (currentRec == -1) {
          return;
        }
        
        // get position in sorted order
        currentRec = getSortedPosition(currentRec);
        /* search target record*/
        for (i = currentRec - 1; i >= 0; i -= 1) {
          if (!isSortedRecordDeleted(i)) {
            break;
          }
        }

        if (i == -1 || !isRecordAccessible(getDataPosition(i))) {
          NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00015");
        } else {
          // get position in data of previous record in sorted order
          changeActiveRecord(getDataPosition(i));
        }
      } else if (activeField.getServerRpc() != null) {
        activeField.getServerRpc().gotoPrevRecord();
      } 
    }
  }
  
  /**
   * Navigates to the first record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  public void gotoFirstRecord() {
    if (isLayoutBelongsToGridDetail() || activeField == null) {
      return;
    }
    // first check if the navigation should be delegated to server side.
    if (activeField.delegateNavigationToServer() && activeField.getServerRpc() != null) {
      activeField.getServerRpc().gotoFirstRecord();
    } else {
      if (!isMulti() && activeField.getServerRpc() != null) {
        activeField.getServerRpc().gotoFirstRecord();
      } else if (noMove()) {
        NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00025");
      } else {
        ColumnView        act;
        int               i;

        if (getActiveRecord() == -1) {
          return; //no active record we give up
        }
        
        act = activeField;
        /* search target record */
        for (i = 0; i < getState().bufferSize; i += 1) {
          if (!isSortedRecordDeleted(i)) {
            break;
          }
        }
        if (i == getState().bufferSize || !isRecordAccessible(i)) {
          NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00015");
          return;
        }

        try {
          leaveRecord();
        } catch (CheckTypeException e) {
          e.displayError();
          return;
        }
        enterRecord(i);
        act.enter();

        if (activeField.getAccess() < VConstants.ACS_VISIT || activeField.hasAction()) {
          gotoNextField();
        }
      }
    }
  }
  
  /**
   * Navigates to the lasts record.
   * This act is delegated to the server side when the target field contains triggers to be executed.
   */
  public void gotoLastRecord() {
    if (isLayoutBelongsToGridDetail() || activeField == null) {
      return;
    }
    // first check if the navigation should be delegated to server side.
    if (activeField.delegateNavigationToServer() && activeField.getServerRpc() != null) {
      activeField.getServerRpc().gotoLastRecord();
    } else {
      if (!isMulti() && activeField.getServerRpc() != null) {
        activeField.getServerRpc().gotoLastRecord();
      } else if (noMove()) {
        NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00025");
      } else {
        ColumnView        act;
        int               i;

        if (getActiveRecord() == -1) {
          return; //no active record we give up
        }

        act = activeField;
        /* search target record */
        for (i = getState().bufferSize  + 1; i >= 0; i -= 1) {
          if (!isSortedRecordDeleted(i)) {
            break;
          }
        }
        if (i == 0 || !isRecordAccessible(i)) {
          NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00015");
          return;
        }

        try {
          leaveRecord();
        } catch (CheckTypeException e) {
          e.displayError();
          return;
        }
        enterRecord(i);
        act.enter();
        if (activeField.getAccess() < VConstants.ACS_VISIT || activeField.hasAction()) {
          gotoNextField();
        }
      }
    }
  }
  
  /**
   * Changes the active record of this block.
   * @param rec The new active record.
   */
  protected void changeActiveRecord(int rec) {
    if (noMove()) {
      NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00025");
    } else {
      ColumnView        act;
      int               oldRecord;

      act = activeField;
      oldRecord = getActiveRecord();

      try {
        if (oldRecord != -1) {
          leaveRecord();
        }
        enterRecord(rec);
        if (activeField != null) {
          act = activeField;
          activeField.leave(getActiveRecord());
        }
        if (act == null || act.hasAction() || act.getAccess() < VConstants.ACS_VISIT) {
          gotoNextField();
        } else {
          act.enter();
        }
        fireActiveRecordChanged();
      } catch (CheckTypeException e) {
        enterRecord(oldRecord);
        e.displayError();
      }
    }
  }
  
  /**
   * Leaves the active record.
   * @throws CheckTypeException 
   */
  protected void leaveRecord() throws CheckTypeException {
    if (isMulti() && getActiveRecord() != -1) {
      if (activeField != null) {
        activeField.leave(getActiveRecord());
      }
      this.oldActiveRecord = getActiveRecord();
    }
    setActiveField(null);
    setActiveRecord(-1);
  }
  
  /**
   * Enters to the given record.
   * @param recno The new record.
   */
  protected void enterRecord(int recno) {
    setActiveRecord(recno);
    refresh(true);
  }
  
  /**
   * Refreshes the display of this block.
   */
  protected void refresh(boolean force) {
    boolean             redisplay = false;
    int                 recno; // row in view

    if (isLayoutBelongsToGridDetail() || !isMulti()) {
      return;
    }

    if (getActiveRecord() != -1) {
      recno = getSortedPosition(getActiveRecord());
    } else {
      rebuildCachedInfos();
      for (ColumnView field : fields) {
        if (field != null) {
          field.scrollTo(sortedToprec);
        }
      }

      return;
    }
    if (recno < sortedToprec) {
      // record to be displayed is above screen => redisplay
      sortedToprec = recno;

      // scroll some more, if there are some (non deleted) records
      int     i = recno -1;
      int     scrollMore =  getState().displaySize / 4;
              
      while (scrollMore > 0 && i > 0) {
        // is there a non deleted record to see?
        if (! isSortedRecordDeleted(i)) {
          sortedToprec -= 1;
          scrollMore--;
        }
        i--;
      }

      redisplay = true;
    } else {
      int       displine = 0;

      for (int i = sortedToprec; i < recno; i += 1) {
        if (!isSortedRecordDeleted(i)) {
          displine += 1;
        }
      }                         

      if (displine < getState().displaySize) {
        // record should be visible => redisplay iff requested
        redisplay = force;// do nothing
      } else {
        // scroll upwards until record is visible => redisplay
        do {
          if (!isSortedRecordDeleted(sortedToprec)) {
            displine -= 1;
          }
          sortedToprec += 1;
        } while (displine >= getState().displaySize);

        // scroll some more, if there are some (non deleted) records
        int     i = recno +1;
        int     scrollMore =  getState().displaySize / 4;
                
        while (scrollMore > 0 && i < getState().bufferSize) {
            // is there a non deleted record to see?
          if (! isSortedRecordDeleted(i)) {
            sortedToprec += 1;
            scrollMore--;
          }
          i++;
        }

        redisplay = true;
      }
    }

    rebuildCachedInfos();

    if (redisplay) {
      for (ColumnView field : fields) {
        if (field != null) {
          field.scrollTo(sortedToprec);
        }
      }
    }
    if (!doNotUpdateScrollPosition) {
      updateScrollbar(); 
    } else {
      doNotUpdateScrollPosition = false;
    }
  }

  /**
   * Performs a scroll action.
   */
  protected void setScrollPos(int val) {
    if (val >= getState().bufferSize) {
      return;
    }
    doNotUpdateScrollPosition = true;
    if (sortedToprec != val) {
      int               recno = 0;  //temp sortedToprec

      while (val > 0) {
        if (!isSortedRecordDeleted(recno)) {
          val--;
        }
        recno++;
      }
      if (noMove() || isSortedRecordDeleted(getDataPosition(recno))) {
        NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00025");
      } else {
        int             lastVisibleRec = recno;
        int             nbDisplay = getState().displaySize - 1;
        int             activeRecord = getActiveRecord();
        boolean         inside = false; // is active record still in the shown rows

        while (nbDisplay > 0) {
          if (!isSortedRecordDeleted(lastVisibleRec)) {
            nbDisplay--;
          }
          if (activeRecord == getDataPosition(lastVisibleRec)) {
            // active record is still in the shown rows
            inside = true;
          }
          lastVisibleRec += 1;
        }

        sortedToprec = recno;
        if (inside) {
          if (getActiveField() != null) {
            getActiveField().updateValue();
          }
          refresh(true);
        } else {
          int           nextRec;

          if (getSortedPosition(getActiveRecord()) < recno) {
            nextRec = getDataPosition(recno);
          } else {
            nextRec = getDataPosition(lastVisibleRec);
          }

          if (noMove() || !isRecordAccessible(nextRec)) {
            NotificationUtils.showError(getConnection(), null, VMainWindow.get(), VMainWindow.getLocale(), "00025");
          } else {
            changeActiveRecord(nextRec);
          }
        }
      }
    }
  }
  
  /**
   * Navigates to the first unfilled field in this block.
   */
  protected void gotoFirstUnfilledField() {
    if (activeField != null) {
      try {
        activeField.leave(getActiveRecord());
      } catch (CheckTypeException e) {
        e.displayError();
        return;
      }
    }

    ColumnView          target = null;

    for (int i = 0; target == null && i < fields.size(); i += 1) {
      if ((fields.get(i) != null
          && !fields.get(i).hasAction()
          && fields.get(i).getAccess() >= VConstants.ACS_VISIT)
          && fields.get(i).isNull())
      {
        target = fields.get(i);
      }
    }

    if (target == null) {
      gotoFirstField();
    } else {
      target.enter();
    }
  }
  
  /**
   * Navigates to the first field in this block.
   */
  protected void gotoFirstField() {
    if (activeField != null) {
      try {
        activeField.leave(getActiveRecord());
      } catch (CheckTypeException e) {
        e.displayError();
        return;
      }
    }
    
    ColumnView          target = null;
    
    for (int i = 0; target == null && i < fields.size(); i += 1) {
      if (fields.get(i) != null
          && !fields.get(i).hasAction()
          && fields.get(i).getAccess() >= VConstants.ACS_VISIT)
      {
        target = fields.get(i);
      }
    }
    if (target != null) {
      target.enter();
    }
  }
  
  /**
   * Returns the active record of this block.
   * @return The active record of this block.
   */
  public int getActiveRecord() {
    return activeRecord >= 0 && activeRecord < getState().bufferSize ? activeRecord : -1;
  }
  
  /**
   * Returns the old active record of this block.
   * @return The old active record of this block.
   */
  public int getOldActiveRecord() {
    return oldActiveRecord;
  }
  
  /**
   * Sets the active record.
   * @param activeRecord The new active record.
   */
  public void setActiveRecord(int activeRecord) {
    if (isMulti() || activeRecord == 0) {
      this.activeRecord = activeRecord;
    }
  }
   
  /**
   * Sets the block active record from a given display line.
   * @param displayLine The display line.
   */
  public void setActiveRecordFromDisplay(int displayLine) {
    activeRecordSetFromDisplay = true;
    setActiveRecord(getRecordFromDisplayLine(displayLine));
    fireActiveRecordChanged();
    refresh(false);
  }
  
  /**
   * Sets the active field of this block.
   * @param field The new active field.
   */
  public void setActiveField(ColumnView field) {
    activeField = field;
  }
  
  /**
   * Returns the active field of this block.
   * @return The active field of this block.
   */
  public ColumnView getActiveField() {
    return activeField;
  }
  
  /**
   * Sets the block detail mode.
   * @param detailMode The detail mode.
   */
  public void setDetailMode(boolean detailMode) {
    this.detailMode = detailMode;
  }
  
  /**
   * Returns {@code true} if the block is in detail mode.
   * @return {@code true} if the block is in detail mode.
   */
  public boolean inDetailMode() {
    return isMulti() && detailMode;
  }
  
  /**
   * Cleans dirty fields. This means that all
   * values that were not sent to the server
   * will be sent from now.
   */
  public void cleanDirtyValues(BlockConnector active, boolean transferFocus) {
    if (active == this) {
      if (activeField != null && getDisplayLine() >= 0) {
        if (activeRecord != -1) {
          if (!activeField.delegateNavigationToServer()) {
            try {
              activeField.checkValue(activeRecord);
            } catch (CheckTypeException e) {
              e.displayError();
              return;
            }
          } else {
            activeField.maybeHasDirtyValues(activeRecord);
          }
        }
        // tells the server side about the client active field.
        if (transferFocus) {
          activeField.transferFocus();
        }
      }
    }
    
    // send block dirty values
    for (ColumnView field : fields) {
      if (field != null && field.isDirty()) {
        field.cleanDirtyValues();
      }
    }
    
    if (activeField != null && activeRecord != -1) {
      // tells the server side about active record in client side
      sendActiveRecordToServerSide();
    }
  }
  
  /**
   * Sends the active record to the server side
   */
  public void sendActiveRecordToServerSide() {
    sendActiveRecordToServerSide(getActiveRecord());
  }
  
  /**
   * Sends the active record to the server side
   * @param record The active record.
   */
  public void sendActiveRecordToServerSide(int record) {
    if (isMulti() && getState().activeRecord != record) {
      getRpcProxy(BlockServerRpc.class).updateActiveRecord(record, sortedToprec);
    }
  }

  /**
   * Returns {@code true} if this block can display more than one record.
   * @return {@code true} if this block can display more than one record.
   */
  public boolean isMulti() {
    return getState().bufferSize > 1;
  }
  
  /**
   * Returns {@code true} if this block display only one record.
   * @return {@code true} if this block display only one record.
   */
  public boolean noChart() {
    return getState().noChart;
  }
  
  /**
   * Returns {@code true} if the block do not allow record move.
   * @return {@code true} if the block do not allow record move.
   */
  public boolean noMove() {
    return getState().noMove;
  }

  /**
   * Returns the sorted position of the given record.
   * @param rec The concerned record.
   * @return The sorted position of the record.
   */
  public int getSortedPosition(int rec) {
    if (!isMulti()) {
      return rec;
    }
    
    for (int i = 0; i < sortedRecords.length; i++) {
      if (sortedRecords[i] == rec) {
        return i;
      }
    }
    
    return -1;
  }
  
  /**
   * Returns the data position of the given record.
   * @param rec The concerned record.
   * @return The data position.
   */
  public int getDataPosition(int rec) {
    if (!isMulti() || rec == -1) {
      return rec;
    } else {
      return sortedRecords[rec];
    }
  }
  
  /**
   * Returns {@code true} if the specified record has been deleted.
   * @param sortedRec The record number.
   * @return {@code true} if the specified record has been deleted.
   */
  public boolean isSortedRecordDeleted(int sortedRec) {
    return (recordInfo[sortedRecords[sortedRec]] & VConstants.RCI_DELETED) != 0;
  }
  
  /**
   * Returns {@code true} if the specified record is filled.
   * @param sortedRec The record number.
   * @return {@code true} if the specified record is filled.
   */
  public boolean isSortedRecordFilled(int sortedRec) {
    return !isSortedRecordDeleted(sortedRec) && (isSortedRecordFetched(sortedRec) || isSortedRecordChanged(sortedRec));
  }

  /**
   * Returns {@code true} if the specified record has been fetched from the database
   * @param sortedRec The record number.
   * @return {@code true} if the specified record has been fetched from the database
   */
  public boolean isSortedRecordFetched(int sortedRec) {
    return (recordInfo[sortedRecords[sortedRec]] & VConstants.RCI_FETCHED) != 0;
  }

  /**
   * Returns {@code true} if the specified record has been changed
   * @param sortedRec The record number.
   * @return {@code true} if the specified record has been changed
   */
  public boolean isSortedRecordChanged(int sortedRec) {
    return (recordInfo[sortedRecords[sortedRec]] & VConstants.RCI_CHANGED) != 0;
  }
  
  /**
   * Returns {@code true} if the given record is accessible.
   * @param rec The concerned record.
   * @return {@code true} if the given record is accessible.
   */
  public boolean isRecordAccessible(int rec) {
    if (rec < 0 || rec >= getState().bufferSize) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Sets the record to be fetched.
   * @param rec The record number.
   * @param val The fetch value.
   */
  public void setRecordFetched(int rec, boolean val) {
    final int   oldValue = recordInfo[rec];
    final int   newValue;

    // calculate new value
    if (val) {
      newValue = oldValue | VConstants.RCI_FETCHED;
    } else {
      newValue = oldValue & ~VConstants.RCI_FETCHED;
    }

    if (newValue != oldValue) {
      // set record info
      recordInfo[rec] = newValue;
      // inform listener that the number of rows changed
      updateScrollbar();
    }
  }

  /**
   * Sets the record to be changed.
   * @param rec The record number.
   * @param val The change value.
   */
  public void setRecordChanged(int rec, boolean val) {
    final int   oldValue = recordInfo[rec];
    final int   newValue;

    // calculate new value
    if (val) {
      newValue = oldValue | VConstants.RCI_CHANGED;
    } else {
      newValue = oldValue & ~VConstants.RCI_CHANGED;
    }

    if (newValue != oldValue) {
      // set record info
      recordInfo[rec] = newValue;
      // inform listener that the number of rows changed
      updateScrollbar();
    }
  }
  
  /**
   * Returns the display line for the given record.
   * @param recno The record number.
   * @return The display line.
   */
  public int getDisplayLine(int recno) {
    // if the block layout belongs to a grid row detail
    // display line is always 0 cause it acts like a simple
    // block even is buffer size is > 1
    if (isLayoutBelongsToGridDetail() || noChart()) {
      return 0;
    }
    
    if (recno < 0) {
      return -1;
    }
    
    int         pos = getSortedPosition(recno);

    if (pos < 0) {
      return -1;
    }

    return sortedRecToDisplay[pos];
  }

  /**
   * Returns the current display line.
   * @return The current display line.
   */
  public int getDisplayLine() {
    return getDisplayLine(getActiveRecord());
  }
  
  /**
   * Returns the record number from the display line.
   * @param displayLine The display line.
   * @return The record number.
   */
  public int getRecordFromDisplayLine(int displayLine) {
    return getDataPosition(displayToSortedRec[displayLine]);
  }
  
  /**
   * Rebuilds cached information
   */
  protected void rebuildCachedInfos() {
    int         cnt = 0;
    int         i = 0;

    // sortedRecToDisplay
    for (; i < sortedToprec; i++) {
      sortedRecToDisplay[i] = -1;
    }

    for (; cnt < getState().displaySize && i < getState().bufferSize; i++) {
      // sortedRecToDisplay: view pos not real record number
      sortedRecToDisplay[i] = isSortedRecordDeleted(i) ? -1 : cnt++;
    }

    for (; i < getState().bufferSize; i++) {
      sortedRecToDisplay[i] = -1;
    }

    // displayToSortedRec
    cnt = sortedToprec;
    for (i = 0; i <  getState().displaySize; i++) {
      while (cnt < getState().bufferSize && isSortedRecordDeleted(cnt)) {
        cnt++;
      }
      // the last one can be deleted too
      if (cnt < getState().bufferSize) {
        displayToSortedRec[i] = cnt++;
      }
    }
  }

  /**
   * Updates the scroll bar position.
   */
  protected void updateScrollbar() {
    if (!isMulti() || getState().displaySize >= getState().bufferSize) {
      return;
    }

    int         validRecords = getNumberOfValidRecord();
    int         dispSize = getState().displaySize;

    if (getLayout() != null) {
      getLayout().updateScroll(dispSize,
                               validRecords,
                               validRecords > dispSize,
                               getNumberOfValidRecordBefore(getRecordFromDisplayLine(0)));
    } 
  }

  /**
   * Returns the number of valid records.
   * @return The number of valid records.
   */
  protected int getNumberOfValidRecord() {
    return getNumberOfValidRecord(getState().bufferSize);
  }
  
  /**
   * Returns the number of valid record before block refresh.
   * @param recno The record number.
   * @return the number of valid record before block refresh.
   */
  protected int getNumberOfValidRecordBefore(int recno) {
    return getNumberOfValidRecord(getSortedPosition(recno));
  }
  
  /**
   * Returns the number of valid records in this block.
   * @param recno The record number.
   * @return The number of valid records in this block.
   */
  protected int getNumberOfValidRecord(int recno) {
    // don't forget to fireValidRecordNumberChanged if
    // the valid number is changed!!
    int         count = 0;
    int         lastFilled = 0;

    for (int i = 0; i < recno; i++) {
      if (!isSortedRecordDeleted(i)) {
        // && (nonEmptyReached || isRecordFilled(i))) {
        count += 1;
        if (isSortedRecordFilled(i)) {
          lastFilled = count;
        }
      }
    }
    // currently only used by the scrollbar.
    // make the size of the scrollbar only so big, that the top
    // most row is filled, when the srcollbar is on the bottom
    count = Math.min(count, lastFilled + getState().displaySize - 1);

    return count; // $$$ May be optimised
  }
  
  /**
   * Notifies the form that the active record of this block has changed.
   */
  protected void fireActiveRecordChanged() {
    ((FormConnector)getParent()).setCurrentPosition(getSortedPosition(getCurrentRecord() - 1) + 1, getRecordCount());
  }

  /**
   * Returns the record count.
   * @return The record count.
   */
  protected int getRecordCount() {
    int         count = 0;

    if (isMulti()) {
      for (int i = 0; i < getState().bufferSize; i++) {
        if (isSortedRecordFilled(i)) {
          count++;
        }
      }
    }
    
    return count;
  }

  /**
   * Returns the current record.
   * @return The current record.
   */
  protected int getCurrentRecord() {
    int         current = 1;

    if (isMulti()) {
      current = getActiveRecord() + 1;
    }

    return current;
  }
  
  /**
   * Sets the layout of this block belongs to the grid detail component.
   * @param layoutBelongsToGridDetail Is the layout belongs to grid details ?
   */
  public void setLayoutBelongsToGridDetail(boolean layoutBelongsToGridDetail) {
    this.layoutBelongsToGridDetail = layoutBelongsToGridDetail;
  }
  
  /**
   * Returns true if the layout of this block belongs to the grid row detail.
   * @return true if the layout of this block belongs to the grid row detail.
   */
  public boolean isLayoutBelongsToGridDetail() {
    return layoutBelongsToGridDetail;
  }
  
  public void addRecordPositionPanel() {
    if (isMulti() && noChart()) {
      
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          
              getWidget().addDomHandler(new KeyPressHandler() {
                
                @Override
                public void onKeyPress(KeyPressEvent event) {
                  if (event.isAltKeyDown() && event.getCharCode() == 'i' && isMulti() && noChart()) {
                    ((FormConnector)getParent()).getWidget().showBlockInfo(getConnection(), getWidget());
                  }
                }
              }, KeyPressEvent.getType());
            }
      });
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * saved layout instance;
   */
  private BlockLayout			layout;
  private VDragAndDropWrapper		dndWrapper;
  private List<ColumnView>              fields;
  private ColumnView                    activeField;
  private boolean                       detailMode;
  private int                           activeRecord;
  private int                           oldActiveRecord;
  private int[]                         sortedRecords;
  // status vector for records
  private int[]                         recordInfo;
  //cached infos
  private  int                          sortedToprec;           // first record displayed
  private  int[]                        sortedRecToDisplay;
  private  int[]                        displayToSortedRec;
  private boolean                       initialized;
  private boolean                       doNotUpdateScrollPosition;
  /*
   * Some browsers fires extra scroll event with wrong scroll position
   * when a chart block field is clicked. This flag is used to prevent
   * these events from propagation to the block UI and thus block view refresh
   * with wron top scroll record.
   */
  private boolean                       activeRecordSetFromDisplay;
  private boolean                       layoutBelongsToGridDetail;
  
  /**
   * The client RPC implementation.
   */
  private BlockClientRpc		rpc = new BlockClientRpc() {
    
    @Override
    public void switchView(final boolean detail) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().switchView(detail);
          setDetailMode(detail);
        }
      });
    }

    @Override
    public void updateScroll(final int pageSize, final int maxValue, final boolean enable, final int value) {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          if (getLayout() != null) {
            getLayout().updateScroll(pageSize, maxValue, enable, value);
          }
        }
      });
    }
  };
}
