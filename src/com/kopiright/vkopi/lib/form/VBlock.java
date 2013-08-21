/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.form;

import java.awt.Component;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.BlockLocalizer;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.visual.ActionHandler;
import com.kopiright.vkopi.lib.visual.Application;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.VActor;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VDatabaseUtils;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.xkopi.lib.base.Connection;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBContextHandler;
import com.kopiright.xkopi.lib.base.DBDeadLockException;
import com.kopiright.xkopi.lib.base.DBForeignKeyException;
import com.kopiright.xkopi.lib.base.DBInterruptionException;
import com.kopiright.xkopi.lib.base.KopiUtils;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.base.SapdbDriverInterface;


public abstract class VBlock implements VConstants, DBContextHandler, ActionHandler {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VBlock(VForm form) {
    this.form = form;
    blockListener = new EventListenerList();
    orderModel = new OrderModel();
  }

  /**
   * Build everything after construction
   */
  protected void buildCstr() {
    activeCommands = new Vector();
    if (bufferSize == 1) {
      this.fetchSize = displaySize;
      this.displaySize = 1;
    } else {
      this.fetchSize = bufferSize;
    }
    this.mode = MOD_QUERY;

    recordInfo = new int[2 * bufferSize];
    fetchBuffer = new int[fetchSize];
    fetchCount = 0;

    activeField = null;
    if (isMulti()) {
      activeRecord = -1;
    } else {
      activeRecord = 0;
    }
    setCurrentRecord(-1);

    detailMode = (displaySize == 1);
    if (isMulti()) {
      sortedRecords  = new int[getBufferSize()];
    } else {
      sortedRecords  = new int[1];
    }

    for (int i = 0; i < sortedRecords.length; i++) {
      sortedRecords[i] = i; // "default order"
    }
  }

  /**
   * @return The corresponding display associated to this model.
   */
  protected Component getDisplay() {
    Component           view = null;
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length - 2; i >= 0 && view == null; i -= 2) {
      if (listeners[i] == BlockListener.class) {
        view = ((BlockListener)listeners[i+1]).getCurrentDisplay();
      }
    }
    return view;
  }


  public VCommand[] getActiveCommands() {
    VCommand[]  temp = new VCommand[activeCommands.size()];

    activeCommands.toArray(temp);

    return temp;
  }

  /**
   * @return The page number of this block
   */
  public int getPageNumber() {
    return page;
  }

  /**
   * @param page the page number of this block
   */
  public void setPageNumber(int page) {
    this.page = page;
  }

  /**
   * @param page the page number of this block
   */
  public void setInfo(int page) {
    setPageNumber(page);
    setInfo();
    buildCstr();
  }

  protected void setInfo() {
    // Do nothing, should be redefined if some info
    // has to be set
  }

  public void build() {
    //  default does nothing
  }

  /**
   * @param fields The fields of this block
   */
  public void setFields(VField[] fields) {
    this.fields = fields;
  }

  /**
   * @return  The fields of this block
   */
  public VField[] getFields() {
    return fields;
  }

  /**
   * Sets the column alignment against an other block
   */
  public void setAlignments(BlockAlignment align) {
    this.align = align;
  }

  public BlockAlignment getAlignment() {
    return align;
  }

  /**
   * @return true if this block follows an other block
   */
  public boolean isFollow() {
    return align != null;
  }

  public void setDetailMode(boolean mode) {
    if (mode != detailMode) {
      // remember field to enter it in the next view
      VField    field = activeField;

      fireViewModeLeaved(this, field);
      detailMode = mode;
      fireViewModeEntered(this, field);
    }
  }

  public boolean isDetailMode() {
    return detailMode;
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes this block
   *
   * @param     manager         the manger to use for localization
   */
  public void localize(LocalizationManager manager) {
    BlockLocalizer      loc;

    loc = manager.getBlockLocalizer(source, name);
    title = loc.getTitle();
    help = loc.getHelp();
    if (indices != null) {
      for (int i = 0; i < indices.length; i++) {
        //!!! for now, overwrite ident with localized message
        //!!! inhibits relocalization of a running form
        indices[i] = loc.getIndexMessage(indices[i]);
      }
    }
    for (int i = 0; i < fields.length; i++) {
      if (!fields[i].isInternal()) {
        fields[i].localize(loc);
      }
    }
  }

  // ----------------------------------------------------------------------
  // Navigation
  // ----------------------------------------------------------------------

  /**
   * Set current mode
   * @exception VException      an exception may occur in field.leave
   */
  public void setMode(int mode) throws VException {
    if (this != getForm().getActiveBlock()) {
      this.mode = mode;

      for (int i = 0; i < fields.length; i++) {
        fields[i].updateModeAccess();
      }
    } else {
      // is this restriction acceptable ?
      assert !isMulti() : "Block "+getName()+" is a multiblock.";

      VField    act = activeField;

      if (act != null) {
        act.leave(true);
      }

      this.mode = mode;

      for (int i = 0; i < fields.length; i++) {
        fields[i].updateModeAccess();
      }

      if (act != null && act.getAccess(getActiveRecord()) >= ACS_VISIT) {
        act.enter();
      }
    }
  }

  /**
   * Performs a void trigger
   *
   * @param     VKT_Type        the number of the trigger
   */
  public void executeVoidTrigger(final int VKT_Type) throws VException {
    // default: does nothing
  }

  public void executeProtectedVoidTrigger(final int VKT_Type)
    throws VException, SQLException
  {
    // default: does nothing
  }

  public Object executeObjectTrigger(final int VKT_Type) throws VException {
    // default: does nothing
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }

  public boolean executeBooleanTrigger(final int VKT_Type) throws VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }

  public int executeIntegerTrigger(final int VKT_Type) throws VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }

  /**
   * implemented for compatiblity with old gui
   * @deprecated
   */
  public void refresh(boolean x) {
    fireBlockChanged();
  }

  /**
   * Sets the access of the block
   * (if isAccessible does not evaluate the
   * access of the block, this method can be made
   * public)
   */
  protected void setAccess(boolean access) {
    if (blockAccess != access) {
      blockAccess = access;
      // inform BlockListener
      fireAccessChanged();
    }
  }

  /**
   * Calculates the access for this block
   */
  public void updateBlockAccess() {
    // !! fix that isAccessible do not
    // calculate the access
    // !! merge with updateAcess
    isAccessible();
  }

  /**
   * Returns true if the block is accessible
   */
  public boolean isAccessible() {
    if (hasTrigger(TRG_ACCESS)) {
      Object    res;

      try {
        res = callTrigger(TRG_ACCESS);
      } catch (VException e) {
        throw new InconsistencyException(e);
      }

      if (! ((Boolean)res).booleanValue()) {
        setAccess(false);
        return false;
      }
    }

    boolean     newAccess;

    newAccess = getAccess() >= ACS_VISIT || isAlwaysAccessible();
    setAccess(newAccess);
    return newAccess;
  }



  /**
   * sort the records to order it by the value of the
   * given column.
   *
   * @param     column column to order or if -1 back to original order
   */
  public void sort(int column, int order) {
    if (column == -1) {
      for (int i = 0; i < sortedRecords.length; i++) {
        sortedRecords[i] = i;
      }
    } else {
      sortArray(sortedRecords, column, order);
    }

    // inform blocklistener that the order of the rows is changed
    fireOrderChanged();
  }


  private void sortArray(int[] array, int column, int order) {
    mergeSort(array, column, order, 0, array.length - 1, new int[array.length]);
  }

  private void mergeSort(int[] array,
                         int column,
                         int order,
                         int lo,
                         int hi,
                         int[] scratch) {
    // a one-element array is always sorted
    VField      field = fields[column];

    if (lo < hi) {
      int       mid = (lo + hi)/2;

      // split into 2 sublists and sort them
      mergeSort(array, column, order, lo, mid, scratch);
      mergeSort(array, column, order, mid+1, hi, scratch);

      // Merge sorted sublists
      int       t_lo = lo;
      int       t_hi = mid+1;

      for (int k = lo; k <= hi; k++) {
        if (t_lo > mid || (t_hi <= hi
                           && field.getObject(array[t_hi]) != null
                           && (field.getObject(array[t_lo]) == null
                               || order * (compareIt(field.getObject(array[t_hi]),field.getObject(array[t_lo]))) < 0))) {
          scratch[k] = array[t_hi++];
        } else {
          scratch[k] = array[t_lo++];
        }
      }

      // Copy back to array
      for (int k = lo; k <= hi; k++) {
        array[k] = scratch[k];
      }
    }
  }

  private int compareIt(Object obj1, Object obj2) {
    if (obj1 instanceof Comparable) {
      return ((Comparable) (obj1)).compareTo((Comparable) obj2);
    } else if (obj1 instanceof Boolean) {
      assert obj2 instanceof Boolean : "Can't compare object (Boolean) with " +obj2.getClass();
      if (obj1.equals(obj2)) {
        return 0;
      } else if (((Boolean) obj1).booleanValue()) {
        return 1;
      } else {
        return -1;
      }
    } else {
      throw new InconsistencyException("Objects not comparable: " + obj1.getClass() + " " + obj2.getClass());
    }
  }

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
  public int getDataPosition(int rec) {
    if (!isMulti() || rec == -1) {
      return rec;
    }
    return sortedRecords[rec];
  }


  /**
   * nb record read (and not deleted)
   */
  public int getNumberOfValidRecord() {
    return getNumberOfValidRecord(getBufferSize());
  }

  /**
   * nb record read (and not deleted)
   */
  public int getNumberOfValidRecordBefore(int recno) {
    return getNumberOfValidRecord(getSortedPosition(recno));
  }
  private int getNumberOfValidRecord(int recno) {
    // don't forget to fireValidRecordNumberChanged if
    // the valid number is changed!!
    int         count = 0;
    int         lastFilled = 0;

    for (int i = 0; i < recno; i++) {
      if (!isRecordDeleted(sortedRecords[i])) {
        // && (nonEmptyReached || isRecordFilled(i))) {
        count += 1;
        if (isRecordFilled(sortedRecords[i])) {
          lastFilled = count;
        }
      }
    }
    // currently only used by the scrollbar.
    // make the size of the scrollbar only so big, that the top
    // most row is filled, when the srcollbar is on the bottom
    count = Math.min(count, lastFilled + getDisplaySize() - 1);

    return count; // $$$ May be optimised
  }

  public int getNumberOfFilledRecords() {
    int count = 0;

    for (int i = 0; i< getBufferSize(); i++) {
      if (isRecordFilled(i) && !isRecordDeleted(i)) {
        count += 1;
      }
    }

    return count;
  }

  /**
   * enter record
   */
  protected void enterRecord(int recno) {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert isMulti() : "Is not multiblock";
    assert getActiveRecord() == -1 : "Is multi and activeRecord = "+getActiveRecord();
    assert activeField == null : "currentfield != "+activeField;

    /* activate line */
    setActiveRecord(recno);
    setCurrentRecord(recno);
    /* calculate the access of all fields in the row */
    updateAccess(recno);

    // !!! lackner 2003.07.11 why before the trigger
    // !!! why must in edt/Modules.vf after Seances.enterRecord
    //     a refesh be called ??
    fireBlockChanged(); // cause a refresh of display


    try {
      callTrigger(TRG_PREREC);
    } catch (VException e) {
      throw new InconsistencyException(e);
    }
  }

  /**
   * leave record
   * @exception VException      an exception may occur in field.leave()
   */
  public void leaveRecord(boolean check) throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert isMulti() : this.getName() + " is not a multiblock";
    assert getActiveRecord() != -1 : "Is multi and activeRecord = "+getActiveRecord();

    if (activeField != null) {
      activeField.leave(check);
    }

    if (check) {
      callTrigger(TRG_POSTREC);
    }

    setActiveRecord(-1);
  }

  /**
   * GOTO FIRST RECORD
   * @exception VException      an exception may occur in record.leave()
   */
  public void gotoFirstRecord() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();

    if (!isMulti()) {
      changeActiveRecord(-fetchPosition);
    } else if (noMove()) {
      throw new VExecFailedException(MessageCode.getMessage("VIS-00025"));
    } else {
      VField            act;
      int               i;

      assert getActiveRecord() != -1 : "Is multi and activeRecord = " + getActiveRecord();
      assert activeField != null : "current field " + activeField;
      act = activeField;

      /* search target record */
      for (i = 0; i < getBufferSize(); i += 1) {
        if (!isRecordDeleted(i)) {
          break;
        }
      }
      if (i == getBufferSize() || !isRecordAccessible(i)) {
        throw new VExecFailedException();
      }

      leaveRecord(true);

      enterRecord(i);

      if (activeField != null) {
        act = activeField;
        activeField.leave(false);
      }

      act.enter();

      if (activeField.getAccess(getActiveRecord()) < ACS_VISIT) {
        gotoNextField();
      }
    }
  }

  /**
   * GOTO LAST RECORD
   * @exception VException      an exception may occur in record.leave()
   */
  public void gotoLastRecord() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();

    if (!isMulti()) {
      if (fetchPosition >= fetchCount - 1) {
        throw new VExecFailedException(MessageCode.getMessage("VIS-00015"));
      }
      changeActiveRecord(fetchCount - fetchPosition - 1);
    } else if (noMove()) {
      throw new VExecFailedException(MessageCode.getMessage("VIS-00025"));
    } else {
      VField            act;
      int               i;

      assert getActiveRecord() != -1 : "current record: " + getActiveRecord();
      assert activeField != null : "current field: "+activeField;
      act = activeField;

      /* search target record */
      for (i = getBufferSize() + 1; i >= 0; i -= 1) {
        if (isRecordFilled(i)) {
          break;
        }
      }
      if (i == 0 || !isRecordAccessible(i)) {
        throw new VExecFailedException(MessageCode.getMessage("VIS-00015"));
      }

      leaveRecord(true);

      enterRecord(i);

      if (activeField != null) {
        act = activeField;
        activeField.leave(false);
      }

      act.enter();

      if (activeField.getAccess(getActiveRecord()) < ACS_VISIT) {
        gotoNextField();
      }
    }
  }

  /**
   *
   */
  public boolean isRecordInsertAllowed(int rec) {
    return (!(noInsert() && !isRecordFetched(rec) && !isRecordChanged(rec)));
  }

  /**
   *
   */
  public boolean isRecordAccessible(int rec) {
    if (rec < 0 || rec >= getBufferSize()) {
      return false;
    } else if (!isAccessible()) {
      return false;
    } else {
      return isRecordInsertAllowed(rec);
    }
  }

  protected void changeActiveRecord(int record) throws VException  {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();

    if (!isMulti()) {
      VField            act;

      act = activeField;

      if (getMode() != MOD_UPDATE) {
        throw new VExecFailedException(MessageCode.getMessage("VIS-00025"));
      }

      if (isChanged() && (! form.ask(Message.getMessage("confirm_discard_changes")))) {
        return;
      }

      try {
        if (activeField != null) {
          activeField.leave(false);
        }
      } catch (VException e) {
        throw new InconsistencyException();
      }
      fetchNextRecord(record);
      try {
        if (activeField != null) {
          act = activeField;
          activeField.leave(false);
        }
        if (act == null || act.getAccess(getActiveRecord()) < ACS_VISIT) {
          gotoNextField();
        } else {
          act.enter();
        }
      } catch (VException e) {
        throw e;
      }
    } else if (noMove()) {
      throw new VExecFailedException(MessageCode.getMessage("VIS-00025"));
    } else {
      VField    act;
      int       oldRecord;

      act = activeField;
      oldRecord = getActiveRecord();

      if (oldRecord != -1) {
        leaveRecord(true);
      }
      enterRecord(record);

      try {
        if (activeField != null) {
          act = activeField;
          activeField.leave(false);
        }
        if (act == null || act.getAccess(getActiveRecord()) < ACS_VISIT) {
          gotoNextField();
        } else {
          act.enter();
        }
      } catch (VException e) {
        leaveRecord(false);
        enterRecord(oldRecord);
        throw e;
      }
    }
  }

  /**
   * GOTO NEXT RECORD OF CURRENT BLOCK
   * @exception VException      an exception may be raised bu record.leave
   */
  public void gotoNextRecord() throws VException {
    if (isMulti()) {
      int               currentRec = getActiveRecord();
      int               i;

      assert currentRec != -1 : " current record " + getActiveRecord();

      // get position in sorted order
      currentRec = getSortedPosition(currentRec);

      /* search target record*/
      for (i =  currentRec + 1; i < getBufferSize(); i += 1) {
        if (!isSortedRecordDeleted(i)) {
          break;
        }
      }
      if (i == getBufferSize() || !isRecordAccessible(getDataPosition(i))) {
        throw new VExecFailedException(MessageCode.getMessage("VIS-00015"));
      }
      // get position in data of next record in sorted order
      changeActiveRecord(getDataPosition(i));
    } else {
      if (fetchPosition >= fetchCount - 1) {
        throw new VExecFailedException(MessageCode.getMessage("VIS-00015"));
      }
      changeActiveRecord(1);
    }
  }

  /**
   * GOTO PREVIOUS RECORD
   * @exception VException      an exception may occur in record.leave()
   */
  public void gotoPrevRecord() throws VException {
    if (isMulti()) {
      int               currentRec = getActiveRecord();
      int               i;

      assert currentRec != -1 : " current record " + getActiveRecord();

      // get position in sorted order
      currentRec = getSortedPosition(currentRec);

      /* search target record*/
      for (i = currentRec - 1; i >= 0; i -= 1) {
        if (!isSortedRecordDeleted(i)) {
          break;
        }
      }

      if (i == -1 || !isRecordAccessible(getDataPosition(i))) {
        throw new VExecFailedException(MessageCode.getMessage("VIS-00015"));
      }
      // get position in data of previous record in sorted order
      changeActiveRecord(getDataPosition(i));
    } else {
      changeActiveRecord(-1);
    }
  }


  /**
   * GOTO SPECIFIED RECORD
   * @param recno               the record number
   * @exception VException      an exception may occur in record.leave()
   */
  public void gotoRecord(int recno) throws VException {
    assert this == form.getActiveBlock() :
      this.getName() + " != "
      + (form.getActiveBlock() == null ? "null" : form.getActiveBlock().getName());

    if (!isMulti()) {
      changeActiveRecord(recno - fetchPosition);
      return;
    }

    if (isRecordDeleted(recno)) {
      throw new VExecFailedException();
    }
    if (recno >= getBufferSize()) {
      throw new VExecFailedException();
    }

    if (noInsert() && !isRecordFetched(recno) && !isRecordChanged(recno)) {
      throw new VExecFailedException();
    }
    changeActiveRecord(recno);
  }



  /**
   * Goto field in current block and in current record.
   * @exception VException      an exception may occur in record.leave()
   */
  public void gotoField(VField fld) throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert fld.getAccess(getActiveRecord()) >= ACS_VISIT :
      " access= " + fld.getAccess(getActiveRecord())
      + " field=" + fld.getName()
      + " activeREcord=" + getActiveRecord();

    if (activeField != null) {
      activeField.leave(true);
    }

    fld.enter();
  }

  /**
   * Goto next field in current record
   * @exception VException      an exception may occur in record.leave()
   */
  public void gotoNextField() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    if (activeField == null) {
      return;
    }

    int         index = getFieldIndex(activeField);
    VField      target = null;
    VField      old = activeField;

    activeField.leave(true);

    for (int i = 0; target == null && i < fields.length; i += 1) {
      index += 1;
      if (index == fields.length) {
        index = 0;
      }

      if (fields[index].getAccess(getActiveRecord()) >= ACS_VISIT
          && ((detailMode && !fields[index].noDetail())
              || (!detailMode && !fields[index].noChart()))) {
        target = fields[index];
      }
    }

    if (target == null) {
      old.enter();
      throw new VExecFailedException();
    }
    target.enter();
  }

  /**
   * Goto previous field in current record
   * @exception VException      an exception may occur in field.leave()
   */
  public void gotoPrevField() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert activeField != null : "current field "+activeField;

    int         index = getFieldIndex(activeField);
    VField      target = null;
    VField      old = activeField;

    activeField.leave(true);

    for (int i = 0; target == null && i < fields.length; i += 1) {
      if (index == 0) {
        index = fields.length;
      }
      index -= 1;

      if (fields[index].getAccess(getActiveRecord()) >= ACS_VISIT
          && ((detailMode && !fields[index].noDetail())
              || (!detailMode && !fields[index].noChart()))) {
        target = fields[index];
      }
    }

    if (target == null) {
      old.enter();
      throw new VExecFailedException();
    }
    target.enter();
  }

  /**
   * Goto first accessible field in current record
   * @exception VException      an exception may occur in field.leave()
   */
  public void gotoFirstField() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert getActiveRecord() != -1 : " current record " + getActiveRecord();            // also valid for single blocks

    if (activeField != null) {
      activeField.leave(true);
    }

    VField      target = null;

    for (int i = 0; target == null && i < fields.length; i += 1) {
      if (fields[i].getAccess(getActiveRecord()) >= ACS_VISIT) {
        target = fields[i];
      }
    }
    if (target != null) {
      target.enter();
    } else {
      fireBlockChanged();
    }
  }

  /**
   * Goto first accessible field in current record that is not fill
   * @exception VException      an exception may occur in field.leave()
   */
  public void gotoFirstUnfilledField() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert getActiveRecord() != -1 : " current record " + getActiveRecord();            // also valid for single blocks

    if (activeField != null) {
      activeField.leave(true);
    }

    VField      target = null;

    for (int i = 0; target == null && i < fields.length; i += 1) {
      if ((fields[i].getAccess(getActiveRecord()) >= ACS_VISIT) && fields[i].isNull(getActiveRecord())) {
        target = fields[i];
      }
    }

    if (target == null) {
      gotoFirstField();
    } else {
      target.enter();
    }
  }

  /**
   * Goto next accessible field in current record that is not fill
   * @exception VException      an exception may occur in field.leave()
   */
  public void gotoNextEmptyMustfill() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert getActiveRecord() != -1 : " current record " + getActiveRecord();            // also valid for single blocks
    VField              current = activeField;

    if (activeField != null) {
      activeField.leave(true);
    } else {
      gotoFirstUnfilledField();
      return;
    }

    VField      target = null;
    int         i;

    // found field
    for (i = 0;  i < fields.length && fields[i] != current; i += 1) {
      // loop
    }
    assert i < fields.length : "i: " + i + "  fields.length" + fields.length;
    i += 1;

    // walk next to next
    for (; target == null && i < fields.length; i += 1) {
      if (fields[i].getAccess(getActiveRecord()) == ACS_MUSTFILL && fields[i].isNull(getActiveRecord())) {
        target = fields[i];
      }
    }

    // redo from start
    for (i = 0; target == null && i < fields.length; i += 1) {
      if (fields[i].getAccess(getActiveRecord()) == ACS_MUSTFILL && fields[i].isNull(getActiveRecord())) {
        target = fields[i];
      }
    }

    if (target == null) {
      gotoFirstUnfilledField();
    } else {
      target.enter();
    }
  }

  /**
   * Goto last accessible field in current record.
   * @exception VException      an exception may occur in field.leave()
   */
  public void gotoLastField() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();
    assert getActiveRecord() != -1 : " current record " + getActiveRecord();            // also valid for single blocks

    if (activeField != null) {
      activeField.leave(true);
    }

    VField      target = null;

    for (int i = fields.length - 1; i >= 0; i -= 1) {
      if (fields[i].getAccess(getActiveRecord()) >= ACS_VISIT) {
        target = fields[i];
      }
    }
    if (target != null) {
      target.enter();
    }
  }

  /**
   * Returns true iff the block has changed wrt the database.
   */
  public boolean isChanged() {
    if (hasTrigger(TRG_CHANGED)) {
      Object    res;

      try {
        res = callTrigger(TRG_CHANGED);
      } catch (VException e) {
        throw new InconsistencyException(e);
      }

      return ((Boolean)res).booleanValue();
    } else {
      for (int i = 0; i < getBufferSize(); i++) {
        if (isRecordChanged(i)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * enter a new block
   */
  public void enter() {
    assert form.getActiveBlock() == null : "current block = " + form.getActiveBlock();
    if (isMulti()) {
      setActiveRecord(-1);
    }

    //    needResetCommands = true;
    form.setActiveBlock(this);

    try {
      callTrigger(TRG_PREBLK);
    } catch (VException e) {
      // a preblock trigger must not fail => chg compiler
      throw new InconsistencyException(e);
    }

    if (isMulti()) {
      // find a valid record
      if (getActiveRecord() == -1) {
        int     recno = -1;

        for (int i = 0; i < getBufferSize() && recno == -1; i++) {
          if (!isRecordDeleted(i)) {
            recno = i;
          }
        }
        assert recno < getBufferSize() : "reno: " + recno + "< buffer size:" +getBufferSize();

        enterRecord(recno);
      }
    } else {
      // if the block is not a multiblock
      // the record is not entered
      // so the update of the access must be done here
      updateAccess(0); // There is only and always record 0
    }

    if (getActiveRecord() != -1) {
      // SOME PREREC TRIGGERS MIGHT SET CURRENTFIELD, BUT STILL NEED TO
      // BE FORCED-REFRESHED, LEMI 06/08/00, LEMI 03/09/00
      fireBlockChanged();

      if (activeField == null) {
        try {
          gotoFirstField();
        } catch (VException e) {
          // should only be raised when leaving a field
          throw new InconsistencyException();
        }
      }
    }
  }

  /**
   * exit block
   * @exception VException      an exception may be raised by record.leave
   */
  public boolean leave(boolean check) throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != "+ form.getActiveBlock().getName();

    if (check) {
      validate();
    } else {
      if (isMulti()) {
        if (getActiveRecord() != -1) {
          leaveRecord(false);
        }
      } else {
        if (activeField != null) {
          activeField.leave(false);
        }
      }
    }

    //    needResetCommands = true;

    if (check) {
      callTrigger(TRG_POSTBLK);
    }

    form.setActiveBlock(null);

    // lackner 2003.07.31 setMode only if check is true
    if (check) {
      setMode(mode);
    }
    return true;
  }

  /**
   * Validate current block.
   * @exception VException      an exception may be raised by triggers
   */
  public void validate() throws VException {
    assert this == form.getActiveBlock() : this.getName() + " != " + ((form.getActiveBlock() == null) ? "null" : form.getActiveBlock().getName());

    int         lastRecord = getActiveRecord();

    try {
      if (!isMulti()) {
        if (activeField != null) {
          activeField.leave(true);
        }

        checkMustfillFields();
      } else {
        int             j;

        if (getActiveRecord() != -1) {
          leaveRecord(true);
        }

        for (int i = 0; i < getBufferSize(); i++) {
          /* check if record is empty */
          setActiveRecord(i);
          lastRecord = i;
          if (isRecordChanged(i)) {
            for (j = 0; j < fields.length; j++) {
              VField    fld = fields[j];

              if (fld.getAccess(getActiveRecord()) >= ACS_VISIT && !fld.isNull(i)) {
                break;
              }
            }

            if (j == fields.length && ! noDelete()) {
              if (!isRecordFetched(i)) {
                setRecordChanged(i, false);
              } else {
                setRecordDeleted(i, true);
              }
            }
          }

          if (isRecordFilled(i)) {
            checkMustfillFields();
            callTrigger(TRG_VALREC);
          }
          setActiveRecord(-1);
          lastRecord = -1;
        }
      }

      callTrigger(TRG_VALBLK);
    } catch (VFieldException exc) {
      throw exc;
    } catch (VException exc) {
      if (lastRecord != -1) {
        if (isMulti()) {
          // chart
          gotoRecord(lastRecord);
        } else {
          // single block
          gotoFirstField();
        }
      } else {
        // leave it on the hard way to be able to enter
        // it again
        getForm().setActiveBlock(null);
        // reenter the block
        enter();
      }
      throw exc;
    } finally {
      fireBlockChanged();
    }
  }

  public int getRecord() {
    int         current = 1;
    int         count = 0;

    if (isMulti()) {
      current = getActiveRecord() + 1;
    } else {
      for (int i = 0; i < fetchCount; i++) {
        if (fetchBuffer[i] != -1) {
          count++;
          if (i == fetchPosition) {
            current = count;
            break;
          }
        }
      }
    }

    return current;
  }

  public int getRecordCount() {
    int         count = 0;

    if (isMulti()) {
      for (int i = 0; i < getBufferSize(); i++) {
        if (isRecordFetched(i) || isRecordChanged(i)) {
          count++;
        }
      }
    } else {
      for (int i = 0; i < fetchCount; i++) {
        if (fetchBuffer[i] != -1) {
          count++;
        }
      }
    }

    return count;
  }

  protected void fireAccessChanged() {
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockListener.class) {
        ((BlockListener)listeners[i+1]).blockAccessChanged(this, blockAccess);
      }
    }
  }

  protected void fireViewModeEntered(VBlock block, VField field) {
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockListener.class) {
        ((BlockListener)listeners[i+1]).blockViewModeEntered(block, field);
      }
    }
  }

  protected void fireViewModeLeaved(VBlock block, VField field) {
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockListener.class) {
        ((BlockListener)listeners[i+1]).blockViewModeLeaved(block, field);
      }
    }
  }


  protected void fireRecordCountChanged() {
    int                 record = getRecord();
    int                 localRecordCount = getRecordCount();
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockRecordListener.class) {
        ((BlockRecordListener)listeners[i+1]).blockRecordChanged(getSortedPosition(record-1)+1, localRecordCount);
      }
    }
  }

  /**
   * check that user has proper UI with focus on a field on the good page
   */
  public void checkBlock() {
    if (getForm().getActiveBlock() == this) {
      if (activeField == null) {
        try {
          if (getActiveRecord() == -1 || isRecordDeleted(getActiveRecord())) {
            int         i = 0;

            for (; i < getBufferSize(); i += 1) {
              if (!isRecordDeleted(i)) {
                break;
              }
            }
            setActiveRecord(i);
          }
          gotoFirstField();
          // lackner 2003.07.31
          // - inserted to get information about the usage of this code
          // - can be removed if the method checkBlock is removed
          if (Application.getDefaults() != null
              && Application.getDefaults().isDebugModeEnabled()) {
            if (((DForm) getForm().getDisplay()).runtimeDebugInfo != null) {
              ((DForm) getForm().getDisplay()).runtimeDebugInfo.printStackTrace();
            }
            System.out.println("INFO: VBlock checkBlock " + Thread.currentThread());
          }
        } catch (VException f) {
          throw new InconsistencyException();
        }
      }
      fireRecordCountChanged();
    } else {
      setCommandsEnabled(false);
    }
  }

  /**
   * Checks that all mustfill fields are filled.
   */
  protected void checkMustfillFields() throws VException {
    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      if (fld.getAccess(getActiveRecord()) == ACS_MUSTFILL && fld.isNull(getActiveRecord())) {
        // !!! lackner 04.10.2003 I don't know if it is really necessary here
        fireBlockChanged();
        throw new VFieldException(fld, MessageCode.getMessage("VIS-00023"));
      }
    }
  }

  /**
   * Clears the entire block.
   */
  public void clear() {
    if (this == form.getActiveBlock()) {
      if (!isMulti()) {
        if (activeField != null) {
          try {
            activeField.setNull();
            activeField.leave(false);
          } catch (VException e) {
            throw new InconsistencyException();
          }
        }
      } else {
        if (getActiveRecord() != -1) {
          try {
            leaveRecord(false);
          } catch (VException e) {
            throw new InconsistencyException();
          }
        }
      }
    }

    for (int i = 0; i < fields.length; i++) {
      fields[i].setSearchOperator(SOP_EQ);
    }

    if (!noChart() && isDetailMode()) {
      setDetailMode(false);
    }

    setAccess(ACS_MUSTFILL);

    for (int i = 0; i < getBufferSize(); i++) {
      clearRecordImpl(i);
    }

    fetchPosition = -1;

    // clear sorting
    for (int i = 0; i < sortedRecords.length; i++) {
      sortedRecords[i] = i; // "default order"
    }

    fireBlockCleared();
  }

  /**
   * Sets defaults for block.
   */
  public void setDefault() {
    try {
      callTrigger(TRG_DEFAULT);
    } catch (VException e) {
      if (e.getMessage() != null) {
        getForm().notice(e.getMessage());
      }
    }

    for (int i = 0; i < getBufferSize(); i++) {
      setActiveRecord(i);               // also valid for single blocks

      for (int j = 0; j < fields.length; j++) {
        fields[j].setDefault();
      }

      setRecordChanged(i, false);
    }

    if (isMulti()) {
      setActiveRecord(-1);
    }

    fireBlockCleared();
  }

  /**
   * Sets visibility of block.
   */
  public void setAccess(int value) {
    assert this != form.getActiveBlock() || activeField == null :
      "current block: " + form.getActiveBlock() + "; current field: " + activeField;
    for (int i = 0; i < fields.length; i++) {
      fields[i].setAccess(value);
    }
  }

  /**
   * Returns true if field is never displayed.
   */
  public boolean isInternal() {
    return
      access[MOD_QUERY] == ACS_HIDDEN &&
      access[MOD_INSERT] == ACS_HIDDEN &&
      access[MOD_UPDATE] == ACS_HIDDEN;
  }

  /**
   * Clears given record.
   */
  public void clearRecord(int recno) {
    clearRecordImpl(recno);
    fireBlockChanged();
  }

  /**
   * Clears given record.
   */
  protected void clearRecordImpl(int recno) {
    assert this != form.getActiveBlock()
      || (isMulti() && recno != getActiveRecord())
      || (!isMulti() && activeField == null) :
      "activeBlock " + form.getActiveBlock()
      + " recno " +  recno + " current record " + getActiveRecord()
      + " isMulti? " + isMulti() + " current field " + activeField;

    // backups the records if it is called in a
    // transaction
    setRecordDeleted(recno, false);

    // don't update access
    ignoreAccessChange = true;
    for (int i = 0; i < fields.length; i++) {
      fields[i].clear(recno);
    }

    setRecordFetched(recno, false);
    setRecordChanged(recno, false);
    // update access again
    ignoreAccessChange = false;
    // done in setMode(...)
    // updateAccess(recno);
  }

  /**
   * Inserts an empty record at current position.
   * @exception VException      an exception may be raised by triggers
   */
  public void insertEmptyRecord(int recno) throws VException {
    int         i;

    assert isMulti() : getName() + " is not a multiblock";
    assert getActiveRecord() == -1 : " current record " + getActiveRecord();

    // search first free record starting at current position
    for (i = recno; i < getBufferSize(); i++) {
      if (!isRecordFetched(i) && !isRecordChanged(i)) {
        break;
      }
    }

    // already new && unchanged
    if (i == recno) {
      return;
    }

    // nothing is free
    if (i == getBufferSize()) {
      throw new VExecFailedException();
    }

    // shift from i down to current record */
    for (; i > recno; i -= 1) {
      copyRecord(i - 1, i, true);
    }

    clearRecord(recno);
    // refresh(true);
  }


  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Loads block from database.
   * @exception VException      an exception may be raised by triggers
   */
  public void load() throws VException, SQLException {
    Query       query = new Query(form.getDBContext().getDefaultConnection());
    String      whatbuf, frombuf, condbuf, orderbuf;
    int         idfld;
    int         idqry;

    // get select condition from first record in block
    if (isMulti()) {
      setActiveRecord(0);
    }

    callProtectedTrigger(TRG_PREQRY);

    // create database query
    whatbuf = getSearchColumns();
    frombuf = getSearchTables();
    condbuf = getSearchConditions();
    orderbuf = getSearchOrder();

    if (isMulti()) {
      setActiveRecord(-1);
      activeField = null;
    }

    // clear block: it will only hold the retrieved tuples
    clear();

    // get index of id field in BLOCK
    idfld = getFieldIndex(getIdField());

    // get index of id field in QUERY
    idqry = 0;
    for (int i = 0; i < idfld; i++) {
      if (fields[i].getColumnCount() > 0) {
        idqry += 1;
      }
    }

    // open database query, fetch tuples
    query.addString(whatbuf);
    query.addString(frombuf);
    query.addString(condbuf);
    query.addString(orderbuf);
    query.open("SELECT $1 $2 $3 $4");

    fetchCount = 0;

    while (fetchCount < fetchSize && query.next()) {
      /*
       * !!! signal error on overflow
       * if (fetchCount == fetchSize) {
       * rtc = EXC_QUERY_OVERFLOW;
       * goto _end_;
       * }
       */
      if (query.getInt(1 + idqry) == 0) {
        continue;
      }

      fetchBuffer[fetchCount] = query.getInt(1 + idqry);

      if (fetchCount >= getBufferSize()) {
        fetchCount += 1;
      } else {
        for (int i = 0, j = 0; i < fields.length; i++) {
          if (fields[i].getColumnCount() > 0) {
            fields[i].setQuery(fetchCount, query, 1+j);
            j += 1;
          }
        }

        setRecordFetched(fetchCount, true);
        setRecordChanged(fetchCount, false);
        setRecordDeleted(fetchCount, false);

        try {
          if (isMulti()) {
            setActiveRecord(fetchCount);
          }
          callProtectedTrigger(TRG_POSTQRY);
          if (isMulti()) {
            setActiveRecord(-1);
          }

          fetchCount += 1;
        } catch (VException e) {
          if (isMulti()) {
            setActiveRecord(-1);
          }

          if (e instanceof VSkipRecordException) {
            clearRecordImpl(fetchCount);
          } else {
            clear();
            throw e;
          }
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }

    query.close();

    fetchPosition = 0;
    // !!! REMOVE setActiveRecord(0);

    if (!isMulti() && fetchCount == 0) {
      throw new VQueryNoRowException(MessageCode.getMessage("VIS-00022"));
    } else if (!isMulti()) {
      setMode(MOD_UPDATE);
    }

    fireBlockChanged();
  }

  /**
   * Fetches record with given ID from database.
   * @exception VException      an exception may be raised by triggers
   */
  public void fetchRecord(int id) throws VException, SQLException {
    String       headbuf;
    String       frombuf;
    String       tailbuf;
    Query        query;

    headbuf = getSearchColumns();
    frombuf = getSearchTables();
    tailbuf = "";
    if (useOracleOuterJoinSyntax()) {
      tailbuf = VBlockOracleOuterJoin.getFetchRecordCondition(fields);
    } else {
      tailbuf = VBlockDefaultOuterJoin.getFetchRecordCondition(fields);
    }

    query = new Query(form.getDBContext().getDefaultConnection());
    query.addString(headbuf);
    query.addString(frombuf);
    query.addString(getIdColumn());
    query.addInt(id);
    query.addString(tailbuf);
    query.open("SELECT $1 $2 WHERE T0.$3 = #4$5");
    if (! query.next()) {
      /* Record does not exist anymore: it was deleted by another user */
      query.close();
      throw new VSkipRecordException();
    } else {
      /* set values */
      for (int i = 0, j = 0; i < fields.length; i++) {
        VField  fld = fields[i];

        if (fld.getColumnCount() > 0) {
          fld.setQuery(query, 1+j);
          j += 1;
        }
      }

      assert ! query.next() : "too many rows";
      query.close();
    }

    setRecordFetched(getActiveRecord(), true);
    setRecordChanged(getActiveRecord(), false);
    setRecordDeleted(getActiveRecord(), false);

    callProtectedTrigger(TRG_POSTQRY);
    setMode(MOD_UPDATE);
  }

  /**
   * Fetches next record (in given direction) in fetch buffer.
   * @exception VException      an exception may be raised by triggers
   */
  public void fetchNextRecord(int incr) throws VException {
    int         pos;

    assert !isMulti() : getName() + " is a multiblock";

    for (pos = fetchPosition + incr;
         pos >= 0 && pos < fetchCount;
         pos += incr) {

      if (fetchBuffer[pos] == -1) {
        continue;
      }

      try {
        for (;;) {
          try {
            form.startProtected(Message.getMessage("loading_record"));

            fetchPosition = pos;
            fetchRecord(fetchBuffer[pos]);

            form.commitProtected();
            return;
          } catch (VException e) {
            try {
              form.abortProtected(e);
            } catch(VException abortEx) {
              throw abortEx;
            }
          } catch (SQLException e) {
            try {
              form.abortProtected(e);
            } catch(DBDeadLockException abortEx) {
              throw new VExecFailedException(MessageCode.getMessage("VIS-00058"));
            } catch(DBInterruptionException abortEx) {
              throw new VExecFailedException(MessageCode.getMessage("VIS-00058"));
            } catch(SQLException abortEx) {
              throw new VExecFailedException(abortEx);
            }
          } catch (Error e) {
            try {
              form.abortProtected(e);
            } catch(Error abortEx) {
              throw new VExecFailedException(abortEx);
            }
          } catch (RuntimeException e) {
            try {
              form.abortProtected(e);
            } catch(RuntimeException abortEx) {
              throw new VExecFailedException(abortEx);
            }
          }
        }
      } catch (VException e) {
        if (!(e instanceof VSkipRecordException)) {
          throw e;
        }

        fetchBuffer[pos] = -1;
      }
    }
    throw new VExecFailedException();
  }

  /**
   * Saves changes in block to database.
   * @exception VException      an exception may be raised by triggers
   * @exception SQLException            an exception may be raised DB access
   */
  public void save() throws VException, SQLException {
    assert !isMulti() || getActiveRecord() == -1 : "Is multi and activeRecord = " + getActiveRecord();

    try {
      callProtectedTrigger(TRG_PRESAVE);
    } catch (VException e) {
      throw new InconsistencyException();
    }

    if (!isMulti()) {
      switch (getMode()) {
      case MOD_INSERT:
        insertRecord(0, -1);
        break;

      case MOD_UPDATE:
        updateRecord(0);
        break;

      default:
        throw new InconsistencyException();
      }
    } else {
      if (isIndexed()) {
        /* first delete all deleted and changed old records */

        for (int i = 0; i < getBufferSize(); i++) {
          if (isRecordFetched(i)) {
            if (isRecordChanged(i)) {
              Query     query = new Query(form.getDBContext().getDefaultConnection());

              query.addString(tables[0]);
              query.addString(getIdColumn());
              query.addInt(getIdField().getInt(i).intValue());
              // !!! check return value (= update count)
              query.run("DELETE FROM $1 WHERE $2 = #3");
            } else if (isRecordDeleted(i)) {
              deleteRecord(i);
            }
          }
        }
      }

      for (int i = 0; i < getBufferSize(); i++) {
        if (isRecordDeleted(i)) {
          if (!isRecordFetched(i)) {
            clearRecordImpl(i);
          } else {
            // IF INDEXUPDATE SET THEN RECORD ALREADY DELETED
            if (!isIndexed()) {
              deleteRecord(i);
            }
          }
        } else if (isRecordChanged(i)) {
          try {
            if (!isRecordFetched(i)) {
              insertRecord(i, -1);
            } else {
              if (isIndexed()) {
                // !!! update with ID
                insertRecord(i, getIdField().getInt(i).intValue());
              } else {
                updateRecord(i);
              }
            }
          } catch (VSkipRecordException doNothing) {
            setActiveRecord(-1);
          }
        }
      }
    }
  }

  /**
   * Deletes in database
   * @exception VException      an exception may be raised by triggers
   * @exception SQLException    an exception may be raised DB access
   */
  public void delete() throws VException, SQLException {
    if (this == form.getActiveBlock()) {
      if (!isMulti()) {
        if (activeField != null) {
          try {
            activeField.leave(false);
          } catch (VException e) {
            throw new InconsistencyException();
          }
        }
      } else {
        if (getActiveRecord() != -1) {
          try {
            leaveRecord(false);
          } catch (VException e) {
            throw new InconsistencyException();
          }
        }
      }
    }

    if (!isMulti()) {
      deleteRecord(0);
    } else {
      for (int i = 0; i < getBufferSize(); i++) {
        if (!isRecordFetched(i)) {
          clearRecord(i);
        } else {
          deleteRecord(i);
        }
      }
    }

    callProtectedTrigger(TRG_POSTDEL);
  }

  /**
   * Searches the field holding the ID of the block's base table.
   * May be overridden by actual form.
   */
  public VField getIdField() {
    VField      f = getBaseTableField("ID");

    if (f == null) {
      throw new InconsistencyException();
    }
    return f;
  }

  /**
   * Returns the name of the DB column of the ID field.
   */
  public String getIdColumn() {
    String    column = getIdField().lookupColumn(0);

    if (column == null) {
      throw new InconsistencyException();
    }
    return column;
  }

  /**
   * Searches field holding UC of block base table
   */
  public VField getUcField() {
    VField      f = getBaseTableField("UC");

    // laurent : return f even if it's null until we add this field in
    // all the forms. After we can throw an Exception if the field UC
    // of the block base table is not present.
    return f;
  }

  /**
   * Searches field holding TS of block base table
   */
  public VField getTsField() {
    VField      f = getBaseTableField("TS");

    return f;
  }

  /**
   * Searches a field of block base table
   *
   * @param     field   the name of the field to search for
   * @return    the field if found, otherwise null
   */
  protected VField getBaseTableField(String field) {
    for (int i = 0; i < fields.length; i++) {
      String    column = fields[i].lookupColumn(0);

      if (column != null && column.equals(field)) {
        return fields[i];
      }
    }
    return null;
  }

  /**
   * Returns the database columns of block.
   */
  public String getReportSearchColumns() {
    String       result;

    result = null;
    // take all visible fields with database access
    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      if (!fld.isInternal() && fld.getColumnCount() > 0) {
        if (result == null) {
          result = "";
        } else {
          result += ", ";
        }
        result += fld.getColumn(0).getQualifiedName();
      }
    }

    // add ID field AT END if it exists and not already taken
    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      //!!! graf 20080329: should we replace fld.getName().equals("ID") by fld == getIdField() ?
      if (fld.isInternal() && fld.getName().equals(getIdField().getName()) && fld.getColumnCount() > 0) {
        if (result == null) {
          result = "";
        } else {
          result += ", ";
        }
        result += fld.getColumn(0).getQualifiedName();
        break;
      }
    }
    return result;
  }

  /**
   * Returns the database columns of block.
   */
  public String getSearchColumns() {
    String              result = null;

    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      if (fld.getColumnCount() > 0) {
        if (result == null) {
          result = "";
        } else {
          result += ", ";
        }
        result += fld.getColumn(0).getQualifiedName();
      }
    }

    return result;
  }

  /**
   * Checks which outer join syntax (JDBC or Oracle) should be used.
   *
   * @return    true iff Oracle outer join syntax should be used.
   */
  private boolean useOracleOuterJoinSyntax() {
    return form.getDBContext().getDefaultConnection().useOracleOuterJoinSyntax();
  }

  /**
   * Tests whether the specified table has nullable columns.
   */
  public boolean hasNullableColumns(int table) {
    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      if (fld.fetchColumn(table) != -1
          && fld.isInternal()
          && fld.getColumn(fld.fetchColumn(table)).isNullable()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tests whether this table has only internal fields.
   */
  public boolean hasOnlyInternalFields(int table) {
    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      if (fld.fetchColumn(table) != -1 && !fld.isInternal()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the tables for database query, with outer joins conditions.
   */
  public String getSearchTables() {
    if (useOracleOuterJoinSyntax()) {
      return VBlockOracleOuterJoin.getSearchTables(this);
    } else {
      return VBlockDefaultOuterJoin.getSearchTables(this);
    }
  }

  /**
   * Returns the search conditions for database query.
   */
  public String getSearchConditions() {
    StringBuffer        buffer = null;

    for (int i = 0; i < fields.length; i++) {
      VField            fld = fields[i];

      if (fld.getColumnCount() > 0) {
        String          cond = fld.getSearchCondition();

        if (cond != null) {
          if (buffer == null) {
            buffer = new StringBuffer(" WHERE ");
          } else {
            buffer.append(" AND ");
          }
          switch (fld.getOptions() & FDO_SEARCH_MASK) {
          case FDO_SEARCH_NONE:
            buffer.append(fld.getColumn(0).getQualifiedName());
            break;
          case FDO_SEARCH_UPPER:
            buffer.append("{fn UPPER(");
            buffer.append(fld.getColumn(0).getQualifiedName());
            buffer.append(")}");
            break;
          case FDO_SEARCH_LOWER:
            buffer.append("{fn LOWER(");
            buffer.append(fld.getColumn(0).getQualifiedName());
            buffer.append(")}");
            break;
          default:
            throw new InconsistencyException("FATAL ERROR: bad search code: " + options);
          }
          buffer.append(" ");
          buffer.append(cond);
        }
      }
      if (useOracleOuterJoinSyntax()) {
        buffer = VBlockOracleOuterJoin.getSearchCondition(fld, buffer);
      } else {
        buffer = VBlockDefaultOuterJoin.getSearchCondition(fld, buffer);
      }
    }
    return buffer == null ? null : buffer.toString();
  }

  /**
   * Returns the search order for database query.
   */
  public String getSearchOrder() {
    int[]               positions;
    int[]               priorities;
    int[]               sizes;
    int                 elems;

    positions   = new int[fields.length];
    priorities  = new int[fields.length];
    sizes       = new int[fields.length];
    elems       = 0;

    // get the fields connected to the database with their priorities
    for (int i = 0, j = 0; i < fields.length; i++) {
      VField            fld = fields[i];

      if (fld.getColumnCount() != 0) {
        // this is a field connected to the database
        j += 1;

        if (fld.getPriority() != 0) {
          positions[elems]      = j;
          priorities[elems]     = fld.getPriority();
          sizes[elems]          = fld.getWidth() * fld.getHeight();
          elems += 1;
        }
      }
    }

    // (bubble) sort the fields with respect to their priorities
    for (int i = elems - 1; i > 0; i--) {
      boolean           swapped = false;

      for (int j = 0; j < i; j++) {
        if (Math.abs(priorities[j]) < Math.abs(priorities[j+1])) {
          int           tmp;

          tmp = positions[j];
          positions[j] = positions[j+1];
          positions[j+1] = tmp;

          tmp = priorities[j];
          priorities[j] = priorities[j+1];
          priorities[j+1] = tmp;

          tmp = sizes[j];
          sizes[j] = sizes[j+1];
          sizes[j+1] = tmp;

          swapped = true;
        }
      }

      if (!swapped) {
        break;
      }
    }

    // build the text
    String              result = null;
    int                 size = 0;
    int                 maxCharacters = form.getDBContext().getDefaultConnection().getMaximumCharactersCountInOrderBy();
    int                 maxColumns = form.getDBContext().getDefaultConnection().getMaximumColumnsInOrderBy();

    for (int i = 0; i < elems; i++) {

      // control the size (nbr of columns and size of characters in an "order by" clause)
      if (size + sizes[i] > maxCharacters || i > maxColumns) {
        break;
      }
      size += sizes[i];

      if (result == null) {
        result = "ORDER BY ";
      } else {
        result += ", ";
      }

      result += positions[i];
      if (priorities[i] < 0) {
        result += " DESC";
      }
    }

    return result;
  }

  /**
   * Fetches lookup fields with key
   * if there are more than one column specified, it takes the fist column
   * @exception VException      an exception may be raised by triggers
   */
  public void fetchLookupFirst(VField fld) throws VException {
    int         table = -1;

    assert fld != null : "fld = " + fld;
    assert this == form.getActiveBlock() :
      this.getName() + " != "
      + ((form.getActiveBlock() == null) ? "null" : form.getActiveBlock().getName());

    table  = fld.getColumn(0).getTable();
    fetchLookup(table, fld);
  }

  /**
   * Fetches lookup fields with key
   * @exception VException      an exception may be raised by triggers
   */
  public void fetchLookup(VField fld) throws VException {
    int         table;

    assert fld != null : "fld = " + fld;
    assert this == form.getActiveBlock() :
      this.getName() + " != "
      + ((form.getActiveBlock() == null) ? "null" : form.getActiveBlock().getName());

    assert fld.getColumnCount() == 1 : "column count: " + fld.getColumnCount();
    table = fld.getColumn(0).getTable();

    fetchLookup(table, fld);
  }

  protected void fetchLookup(int table, VField fld) throws VException {
    // clears all fields of lookup except the key
    for (int i = 0; i < fields.length; i++) {
      VField    f = fields[i];

      if (f != fld && f.lookupColumn(table) != null) {
        f.setNull(getActiveRecord());
      }
    }

    String      fldbuff = fld.getSearchCondition();

    // if field has a fixed value fetch table
    if (fldbuff != null && fldbuff.startsWith("= ")) {
      String    headbuff = null;

      for (int i = 0; i < fields.length; i++) {
        String          column = fields[i].lookupColumn(table);

        if (column != null) {
          if (headbuff == null) {
            headbuff = "";
          } else {
            headbuff += ", ";
          }

          headbuff += column;
        }
      }

      try {
        form.getDBContext().startWork();        // !!! BEGIN_SYNC(null);

        Query           query = new Query(form.getDBContext().getDefaultConnection());
        query.addString(headbuff);
        query.addString(tables[table]);
        query.addString(fld.lookupColumn(table));
        query.addString(fldbuff);
        query.open("SELECT $1 FROM $2 WHERE $3 $4");

        if (! query.next()) {
          query.close();
          form.getDBContext().abortWork();      // !!! END_SYNC();
          throw new VExecFailedException(MessageCode.getMessage("VIS-00016",
                                                                new Object[]{ tables[table] }));
        } else {
          int   j = 0;

          for (int i = 0; i < fields.length; i++) {
            VField      f = fields[i];

            if (f.lookupColumn(table) != null) {
              f.setQuery(query, 1+j);
              j += 1;
            }
          }

          if (query.next()) {
            query.close();
            form.getDBContext().abortWork();    // !!! END_SYNC();
            throw new VExecFailedException(MessageCode.getMessage("VIS-00020",
                                                                  new Object[]{ tables[table] }));
          }
          query.close();
        }

        form.getDBContext().commitWork();       // !!! END_SYNC();
      } catch (SQLException e) {
        throw new VExecFailedException("XXXX !!!!" + e.getMessage());
      }
    }
  }

  public void refreshLookup(int record) throws VException, SQLException {
    clearLookups(record);
    selectLookups(record);
  }

  // ----------------------------------------------------------------------
  // BUILD A MENU FROM DB RECORDS MATCHING BLOCK SEARCH CONDITIONS
  // ----------------------------------------------------------------------

  /**
   * Selects ID from block query menu
   *
   * @param     showSingleEntry         display menu even if there is only one element
   * @return    ID of selected record
   */
  public int singleMenuQuery(boolean showSingleEntry) {
    assert !isMulti() : getName() + " is a multiblock";

    ListDialog  dialog = null;

    try {
      for (;;) {
        try {
          form.startProtected(Message.getMessage("searching_database"));

          callProtectedTrigger(TRG_PREQRY);
          dialog = buildQueryDialog();

          form.commitProtected();
          break;
        } catch (VException e) {
          try {
            form.abortProtected(e);
          } catch(VException abortEx) {
            throw abortEx;
          }
        } catch (SQLException e) {
          try {
            form.abortProtected(e);
          } catch(DBDeadLockException abortEx) {
            throw new VExecFailedException(MessageCode.getMessage("VIS-00058"));
          } catch(DBInterruptionException abortEx) {
            throw new VExecFailedException(MessageCode.getMessage("VIS-00058"));
          } catch(SQLException abortEx) {
            throw new VExecFailedException(abortEx);
          }
        } catch (Error e) {
          try {
            form.abortProtected(e);
          } catch(Error abortEx) {
            throw new VExecFailedException(abortEx);
          }
        } catch (RuntimeException e) {
          try {
            form.abortProtected(e);
          } catch(RuntimeException abortEx) {
            throw new VExecFailedException(abortEx);
          }
        }
      }
    } catch (VException e) {
      if (e.getMessage() != null) {
        getForm().error(e.getMessage());
      }
      return -1;
    }

    if (dialog == null) {
      getForm().error(MessageCode.getMessage("VIS-00022"));
      return -1;
    } else {
      // !! jdk 1.4.1 lackner 07.08.2003
      // if the second parameter is null, it is slower
      return dialog.selectFromDialog(form.getDisplay(), null, showSingleEntry);
    }
  }

  /**
   * Warning, you should use this method under a protected statement
   */

  public ListDialog buildQueryDialog() throws SQLException {
    VField[]            query_tab = new VField[fields.length];
    int                 query_cnt = 0;

    /* get the fields to be displayed in the dialog */
    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      /* skip fields not related to the database */
      if (fld.getColumnCount() == 0) {
        continue;
      }

      /* skip fields we don't want to show */
      if (fld.getPriority() == 0) {
        continue;
      }

      /* skip fields with fixed value */
      if (!fld.isNull(getActiveRecord()) &&
          fld.getSearchOperator() == SOP_EQ &&
          fld.getSql(getActiveRecord()).indexOf('*') == -1) {
        continue;
      }

      query_tab[query_cnt++] = fld;
    }

    /* (bubble) sort fields wrt priorities */
    for (int i = query_cnt - 1; i > 0; i--) {
      boolean           swapped = false;

      for (int j = 0; j < i; j++) {
        if (Math.abs(query_tab[j].getPriority()) < Math.abs(query_tab[j+1].getPriority())) {
          VField        tmp;

          tmp = query_tab[j];
          query_tab[j] = query_tab[j+1];
          query_tab[j+1] = tmp;

          swapped = true;
        }
      }

      if (!swapped) {
        break;
      }
    }

    /* build query: first rows to select ... */
    String      whatbuf = "";

    for (int i = 0; i < query_cnt; i++) {
      whatbuf += query_tab[i].getColumn(0).getQualifiedName() + ", ";
    }

    /* ... and now their order */
    String      orderbuf = "";
    boolean     first = true;
    int         orderSize = 0;
    int         maxCharacters = form.getDBContext().getDefaultConnection().getMaximumCharactersCountInOrderBy();
    int         maxColumns = form.getDBContext().getDefaultConnection().getMaximumColumnsInOrderBy();

    for (int i = 0; i < query_cnt; i++) {
      int       size;

      // control the size (nbr of columns and size of characters in an "order by" clause)
      size = query_tab[i].getWidth() * query_tab[i].getHeight();
      if (orderSize + size > maxCharacters || i > maxColumns) {
        break;
      }
      orderSize += size;

      if (first) {
        orderbuf = "ORDER BY ";
        first = false;
      } else {
        orderbuf += ", ";
      }
      orderbuf += (i + 1);
      if (query_tab[i].getPriority() < 0) {
        orderbuf += " DESC";
      }
    }

    /* query from where ? */
    String      frombuf = getSearchTables();
    String      condbuf = getSearchConditions();

    Object[][]  values = new Object[query_cnt][fetchSize];
    int[]       ids = new int[fetchSize];
    int         rows = 0;

    Query       query = new Query(form.getDBContext().getDefaultConnection());
    query.addString(whatbuf);
    query.addString(getIdColumn());
    query.addString(frombuf);
    query.addString(condbuf);
    query.addString(orderbuf);
    query.open("SELECT $1 T0.$2 $3 $4 $5");
    while (query.next()) {
      if (rows == fetchSize) {
        break;
      }

      /* don't show record with ID = 0 */
      if (query.getInt(1 + query_cnt) == 0) {
        continue;
      }

      ids[rows] = query.getInt(1 + query_cnt);

      for (int i = 0; i < query_cnt; i++) {
        values[i][rows] = query_tab[i].retrieveQuery(query, 1+i);
      }

      rows += 1;
    }
    query.close();

    if (rows == 0) {
      return null;
    } else {
      ListDialog        dialog;
      VListColumn[]     cols;

      cols = new VListColumn[query_cnt];
      for (int i = 0; i < cols.length; i++) {
        cols[i] = query_tab[i].getListColumn();
      }
      dialog = new ListDialog(cols, values, ids, rows);
      if (rows == fetchSize) {
        dialog.setTooManyRows();
      }
      return dialog;
    }
  }

  // ----------------------------------------------------------------------
  // SETS/GETS INFORMATION ABOUT THE BLOCK
  // ----------------------------------------------------------------------

  /**
   * Returns the size of the record buffer.
   */
  public int getBufferSize() {
    return bufferSize;
  }

  /**
   * Returns the number of displayable records.
   */
  public int getDisplaySize() {
    return displaySize;
  }

  /**
   * Returns the current record.
   */
  public int getActiveRecord() {
    return activeRecord >= 0 && activeRecord < bufferSize ? activeRecord : -1;
  }

  /**
   * Returns true iff at least one record is filled
   */
  public boolean isFilled() {
    for (int i = 0; i < getBufferSize(); i += 1) {
      if (isRecordFilled(i)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true iff the record is filled
   */
  public boolean isRecordFilled(int rec) {
    return !isRecordDeleted(rec) && (isRecordFetched(rec) || isRecordChanged(rec));
  }

  /**
   * Returns true iff the specified record has been fetched from the database
   */
  public boolean isRecordFetched(int rec) {
    return (recordInfo[rec] & RCI_FETCHED) != 0;
  }

  /**
   * Returns true iff the specified record has been changed
   */
  public boolean isRecordChanged(int rec) {
    return (recordInfo[rec] & RCI_CHANGED) != 0;
  }

  /**
   * Returns true iff the specified record has been deleted
   */
  public boolean isRecordDeleted(int rec) {
    return (recordInfo[rec] & RCI_DELETED) != 0;
  }
  /**
   * Returns true iff the specified record has been deleted
   */
  public boolean isSortedRecordDeleted(int sortedRec) {
    return (recordInfo[sortedRecords[sortedRec]] & RCI_DELETED) != 0;
  }

  /**
   * Returns true iff the specified record is trailed
   */
  public boolean isRecordTrailed(int rec) {
    return (recordInfo[rec] & RCI_TRAILED) != 0;
  }

  /**
   * Sets the current record.
   */
  public void setCurrentRecord(int rec) {
    if (isMulti()) {
      currentRecord = rec;
    }
  }

  /**
   * Returns the current record.
   */
  protected int getCurrentRecord() {
    if (!isMulti()) {
      return 0;
    } else {
      assert currentRecord >= 0 && currentRecord < bufferSize : "Bad currentRecord " + currentRecord;
      return currentRecord;
    }
  }

  /**
   * Returns true iff the current record is filled
   */
  public boolean isCurrentRecordFilled() {
    return !isCurrentRecordDeleted() && (isCurrentRecordFetched() || isCurrentRecordChanged());
  }

  /**
   * Returns true iff the current record has been fetched from the database
   */
  public boolean isCurrentRecordFetched() {
    return (recordInfo[getCurrentRecord()] & RCI_FETCHED) != 0;
  }

  /**
   * Returns true iff the current record has been changed
   */
  public boolean isCurrentRecordChanged() {
    return (recordInfo[getCurrentRecord()] & RCI_CHANGED) != 0;
  }

  /**
   * Returns true iff the current record has been deleted
   */
  public boolean isCurrentRecordDeleted() {
    return (recordInfo[getCurrentRecord()] & RCI_DELETED) != 0;
  }

  /**
   * Returns true iff the current record is trailed
   */
  public boolean isCurrentRecordTrailed() {
    return (recordInfo[getCurrentRecord()] & RCI_TRAILED) != 0;
  }

  /**
   * Returns the current block mode.
   */
  public int getMode() {
    return mode;
  }

  /**
   * Returns the current block access.
   */
  public int getAccess() {
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getAccess(getActiveRecord()) >= ACS_VISIT) {
        return ACS_VISIT;
        //    return access[mode];
      }
    }
    return ACS_SKIPPED;
  }

  /**
   * Updates current access of block fields ind the current Record.
   */
  public void updateAccess() {
    updateAccess(getActiveRecord());
  }

  /**
   * Updates current access of fields in the defined record.
   */
  public void updateAccess(int record) {
    for (int i = 0; i < fields.length; i++) {
      if (!fields[i].isInternal()) {
        // internal fields are always hidden
        // no need for an update
        fields[i].updateAccess(record);
      }
    }
  }

  /**
   * Sets the current record.
   */
  public void setActiveRecord(int rec) {
    assert isMulti() || rec == 0 :  "multi? " + isMulti() + "rec: "+rec;
    activeRecord = rec;
  }

  /**
   *
   */
  public void setRecordFetched(int rec, boolean val) {
    final int   oldValue = recordInfo[rec];
    final int   newValue;

    // calculate new value
    if (val) {
      newValue = oldValue | RCI_FETCHED;
    } else {
      newValue = oldValue & ~RCI_FETCHED;
    }

    if (newValue != oldValue) {
      // backup record before we changed it
      trailRecord(rec);
      // set record info
      recordInfo[rec] = newValue;

      if (!ignoreAccessChange) {
        updateAccess(rec);
      }
      // inform listener that the number of rows changed
      fireValidRecordNumberChanged();
    } else {
      // a value changed - access can change
      if (!ignoreAccessChange) {
        updateAccess(rec);
      }
    }
  }

  /**
   * Use the default record
   */
  public void setRecordFetched(boolean val) {
    setRecordFetched(getActiveRecord(), val);
  }

  /**
   *
   */
  public void setRecordChanged(int rec, boolean val) {
    final int         oldValue = recordInfo[rec];
    final int         newValue;

    // calculate new value
    if (val) {
      newValue = oldValue | RCI_CHANGED;
    } else {
      newValue = oldValue & ~RCI_CHANGED;
    }

    if (newValue != oldValue) {
      // backup record before we change it
      trailRecord(rec);

      if (!val && getActiveField() != null && getActiveField().isChanged()) {
        getActiveField().setChanged(false);
      }
      recordInfo[rec] = newValue;

      if (!ignoreAccessChange) {
        updateAccess(rec);
      }

      // inform listener that the number of rows changed
      fireValidRecordNumberChanged();
    } else {
      // a value changed - access can change
      if (!ignoreAccessChange) {
        updateAccess(rec);
      }
    }
  }

  /**
   * Use the default record
   */
  public void setRecordChanged(boolean val) {
    setRecordChanged(getActiveRecord(), val);
  }

  /**
   *
   */
  public void setRecordDeleted(int rec, boolean val) {
    final int         oldValue = recordInfo[rec];
    final int         newValue;

    // calculate new value
    if (val) {
      newValue = oldValue | RCI_DELETED;
    } else{
      newValue = oldValue & ~RCI_DELETED;
    }

    if (newValue != oldValue) {
      // backup record before we change it
      trailRecord(rec);

      recordInfo[rec] = newValue;

      if (!ignoreAccessChange) {
        updateAccess(rec);
      }
      // inform listener that the number of rows changed
      fireValidRecordNumberChanged();
    } else {
      // a value changed - access can change
      if (!ignoreAccessChange) {
        updateAccess(rec);
      }
    }
  }

  /**
   * Use the default record
   */
  public void setRecordDeleted(boolean val) {
    setRecordDeleted(getActiveRecord(), val);
  }

  /**
   *
   */
  public void setRecordTrailed(int rec, boolean val) {
    if (val) {
      recordInfo[rec] |= RCI_TRAILED;
    } else {
      recordInfo[rec] &= ~RCI_TRAILED;
    }
  }

  /**
   * Use the default record
   */
  public void setRecordTrailed(boolean val) {
    setRecordTrailed(getActiveRecord(), val);
  }

  /**
   * COPY RECORD IN BLOCK
   */
  public void copyRecord(int from, int to, boolean trail) {
    if (trail) {
      trailRecord(to);
    }
    recordInfo[to] = recordInfo[from];
    for (int i = 0; i < fields.length; i++) {
      fields[i].copyRecord(from, to);
    }
  }

  /**
   * Initialises the block.
   * @exception VException      an exception may be raised by triggers
   */
  public void initialise() throws VException {
    callTrigger(TRG_INIT);
  }

  void initIntern() {
    for (int i = 0; i < fields.length; i++) {
      fields[i].setBlock(this);
    }
    build();
    for (int i = 0; i < fields.length; i++) {
      fields[i].build();
    }
  }

  // ----------------------------------------------------------------------
  // Utils
  // ----------------------------------------------------------------------

  /**
   * Returns the containing form
   */
  public VForm getForm() {
    return form;
  }

  /**
   *
   */
  public VActor getActor(int i) {
    return getForm().getActor(i);
  }

  /**
   * Returns the block title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the block name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the block title
   */
  public String getShortcut() {
    return shortcut;
  }

  /**
   * Returns true iff this block can display more than one record.
   * @deprecated        This method is replaced by noChart()
   */
  public boolean isChart() {
    return  !noChart();
  }

  /**
   * Returns true iff this block can display more than one record.
   */
  public boolean isMulti() {
    return bufferSize > 1;
  }

  /**
   * nb field on this block
   */
  public int getFieldCount() {
    return fields.length;
  }

  /**
   * Returns a field from its name
   *
   * @param     name    the name of the field
   * @return the field or null if no field with that name has been found
   */
  public VField getField(String name) {
    for (int i = 0; i < fields.length; i++) {
      if (name.equals(fields[i].getName())) {
        return fields[i];
      }
    }

    return null;
  }

  /**
   * Returns the current field
   */
  public VField getActiveField() {
    return activeField;
  }

  /**
   * Sets the current field
   */
  public void setActiveField(VField field) {
    activeField = field;
  }

  /**
   * Returns the index of field in block
   */
  protected int getFieldIndex(VField fld) {
    for (int i = 0; i < fields.length; i++) {
      if (fld == fields[i]) {
        return i;
      }
    }

    throw new InconsistencyException();
  }

  /*
   * Will empty records not be deleted automatically ?
   */
  protected boolean noDelete() {
    return (options & BKO_NODELETE) != 0;
  }

  /*
   * Are empty records inaccessible ?
   */
  protected boolean noInsert() {
    return (options & BKO_NOINSERT) != 0;
  }

  /*
   * Is navigation between records disabled ?
   */
  public boolean noMove() {
    return (options & BKO_NOMOVE) != 0;
  }

  /*
   * Should saving delete and reinsert modified records ?
   */
  protected boolean isIndexed() {
    return (options & BKO_INDEXED) != 0;
  }

  /*
   * Are empty records inaccessible ?
   */
  public boolean noDetail() {
    return (options & BKO_NODETAIL) != 0;
  }

  /*
   * Are empty records inaccessible ?
   */
  public boolean noChart() {
    return (options & BKO_NOCHART) != 0;
  }

  /*
   * Is this block accessible even when no fields are accessibles ?
   */
  protected boolean isAlwaysAccessible() {
    return (options & BKO_ALWAYS_ACCESSIBLE) != 0;
  }

  /*
   * Is this block accessible even when no fields are accessibles ?
   */
  protected boolean isAlwaysSkipped() {
    return access[MOD_QUERY] <= ACS_SKIPPED &&
      access[MOD_UPDATE] <= ACS_SKIPPED &&
      access[MOD_INSERT] <= ACS_SKIPPED;
  }


  // ----------------------------------------------------------------------
  // UI
  // ----------------------------------------------------------------------

  public int getBorder() {
    return border;
  }

  public int getMaxRowPos() {
    return maxRowPos;
  }

  public int getMaxColumnPos() {
    return maxColumnPos;
  }

  public int getDisplayedFields() {
    return displayedFields;
  }

  // ----------------------------------------------------------------------
  // TRAILING
  // ----------------------------------------------------------------------

  /**
   * Sets block untrailed (commits changes).
   */
  public void commitTrail() {
    for (int i = 0; i < getBufferSize(); i++) {
      setRecordTrailed(i, false);
    }
  }

  /**
   * Restore trailed information.
   */
  public void abortTrail() {
    boolean     foundTrailed = false;

    for (int i = 0; i < getBufferSize(); i++) {
      if (isRecordTrailed(i)) {
        copyRecord(i + getBufferSize(), i, false);
        setRecordTrailed(i, false);
        foundTrailed = true;
      }
    }

    if (foundTrailed) {
      fireValidRecordNumberChanged();
    }
  }

  /**
   * Returns a list of filled recors
   */
  public int[] getFilledRecords() {
    int         count = 0;
    for (int i = 0; i < getBufferSize(); i += 1) {
      if (isRecordFilled(i)) {
        count++;
      }
    }
    int[]       elems = new int[count];
    count = 0;
    for (int i = 0; i < getBufferSize(); i += 1) {
      if (isRecordFilled(i)) {
        elems[count++] = i;
      }
    }
    return elems;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Enables/disables block-level commands
   */
  public void setCommandsEnabled(boolean enable) {
    if (enable) {
      if (commands != null) {
        if (activeCommands.size() > 0) {
          // remove all commands currently in the list
          setCommandsEnabled(false);
        }
        // add active commands to the list
        for (int i = 0; i < commands.length; i++) {
          if (commands[i].isActive(mode)) {
            activeCommands.addElement(commands[i]);
            commands[i].setEnabled(true);
          }
        }
      }
    } else {
      for (int i = 0; i < activeCommands.size(); i++) {
        VCommand cmd = (VCommand)activeCommands.elementAt(i);
        cmd.setEnabled(false);
      }
      activeCommands.setSize(0);
    }
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param     action          the action to perform.
   * @param     block           This action should block the UI thread ?
   * @deprecated                Use method performAsyncAction without bool parameter
   */
  public void performAction(final KopiAction action, boolean block) {
    getForm().performAsyncAction(action);
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param     action          the action to perform.
   */
  public void performAsyncAction(final KopiAction action) {
    getForm().performAsyncAction(action);
  }

  /**
   * Trails information about the record.
   * This copy (backup) is used if the transaction is aborted
   * to rollback the form the the correct point.
   */
  protected void trailRecord(int rec) {
    // check if trailing needed
    if (! form.inTransaction() || isRecordTrailed(rec)) {
      return;
    }

    // copy record to trail area
    copyRecord(rec, getBufferSize() + rec, false);
    setRecordTrailed(rec, true);
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected Object callTrigger(int event) throws VException {
    return callTrigger(event, 0);
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected Object callProtectedTrigger(int event) throws VException, SQLException {
    return callProtectedTrigger(event, 0);
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected Object callProtectedTrigger(int event, int index) throws VException, SQLException {
    setCurrentRecord(getActiveRecord());
    executeProtectedVoidTrigger(VKT_Triggers[index][event]);
    return null;
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected Object callTrigger(int event, int index) throws VException {
    final int           oldCurrentRecord;
    final Object        returnValue;

    // do not use getCurrentRecord because getCurrentRecord throws an
    // exception if currentRecord is null.
    oldCurrentRecord = currentRecord;
    try {
      setCurrentRecord(getActiveRecord());
      switch (TRG_TYPES[event]) {
      case TRG_VOID:
        executeVoidTrigger(VKT_Triggers[index][event]);
        returnValue = null;
        break;
      case TRG_BOOLEAN:
        returnValue = new Boolean(executeBooleanTrigger(VKT_Triggers[index][event]));
        break;
      case TRG_INT:
        returnValue = new Integer(executeIntegerTrigger(VKT_Triggers[index][event]));
        break;
      case TRG_OBJECT:
        returnValue = executeObjectTrigger(VKT_Triggers[index][event]);
        break;
      default:
        throw new InconsistencyException("BAD TYPE" + TRG_TYPES[event]);
      }
    } finally {
      // Triggers like ACCESS or VALUE trigger can be called anywhere
      // but should not change the currentRecord for further calculations.
      setCurrentRecord(oldCurrentRecord);
    }
    return returnValue;
  }

  /**
   * Returns true iff there is trigger associated with given event.
   */
  protected boolean hasTrigger(int event) {
    return hasTrigger(event, 0);
  }

  /**
   * Returns true iff there is trigger associated with given event.
   */
  protected boolean hasTrigger(int event, int index) {
    return VKT_Triggers[index][event] != 0;
  }

  /*
   * Clears all hidden lookup fields.
   */
  protected void clearLookups(int recno) {
    if (tables != null) {
      for (int i = 1; i < tables.length; i++) {
        for (int j = 0; j < fields.length; j++) {
          VField        fld = fields[j];

          if (fld.isInternal() && fld.lookupColumn(i) != null && fld.eraseOnLookup()) {
            fld.setNull(recno);
          }
        }
      }
    }
  }

  /*
   * For each lookup-table of block check that record exists and is unique
   *
   * Selects a record from a lookup table
   * Checks that record exists and is unique
   */
  protected void selectLookups(int recno) throws SQLException, VException {
    if (tables != null) {
      for (int i = 1; i < tables.length; i++) {
        selectLookup(i, recno);
      }
    }
  }

  private boolean isNullReference(int table, int recno) {
    boolean     nullReference;

    // check if this lookup table has not only internal fields
    if (hasOnlyInternalFields(table)) {
      nullReference = false;
    } else {
      // check if all lookup fields for this table are null.
      nullReference = true;

      for (int i = 0; nullReference && i < fields.length; i++) {
        VField  fld = fields[i];

        if (fld.fetchColumn(table) != -1
            && !fld.isInternal()
            && !fld.isNull(recno)) {
          nullReference = false;
        }
      }
    }

    // this test is useful since we use outer join only for nullable columns.
    for (int i = 0; nullReference && i < fields.length; i++) {
      VField    fld = fields[i];

      if (fld.isInternal()
          && fld.fetchColumn(0) != -1
          && fld.fetchColumn(table) != -1
          && !fld.getColumn(fld.fetchColumn(0)).isNullable()) {
        nullReference = false;
      }
    }
    return nullReference;
  }

  /*
   *
   */
  protected void selectLookup(int table, int recno) throws SQLException, VException {
    String      headbuff;
    String      tailbuff;
    Query       query;

    headbuff = "";
    tailbuff = "";

    // set internal fields to null (null reference)
    if (isNullReference(table, recno)) {
      for (int i = 0; i < fields.length; i++) {
        VField  fld = fields[i];

        if (fld.isInternal() && fld.lookupColumn(table) != null) {
          fld.setNull(recno);
        }
      }
    } else {
      for (int i = 0; i < fields.length; i++) {
        VField  fld = fields[i];
        String  col = fld.lookupColumn(table);

        if (col != null) {
          if (! headbuff.equals("")) {
            headbuff += ", ";
          }
          headbuff += col;
          if (!fld.isInternal() || !fld.isNull(recno)) {
            String  sql = fld.getSql(recno);
            if (!sql.equals("?") ) { // dont lookup for blobs...
              if (! tailbuff.equals("")) {
                tailbuff += " AND ";
              }
              tailbuff += col;
              if (fld.getSql(recno).equals(com.kopiright.xkopi.lib.base.KopiUtils.NULL_LITERAL)) {
                tailbuff += " IS ";
              } else {
                tailbuff += " = ";
              }
              tailbuff += fld.getSql(recno);
            }
          }
        }
      }

      if (tailbuff.equals("")) {
        throw new InconsistencyException("no conditions for table " + tables[table]);
      }

      query = new Query(form.getDBContext().getDefaultConnection());
      query.addString(headbuff);
      query.addString(tables[table]);
      query.addString(tailbuff);

      query.open("SELECT $1 FROM $2 WHERE $3");
      if (! query.next()) {
        query.close();
        form.getDBContext().abortWork();
        setActiveRecord(recno);
        throw new VExecFailedException(MessageCode.getMessage("VIS-00016",
                                                              new Object[]{ tables[table] }));
      } else {
        for (int i = 0, j = 0; i < fields.length; i++) {
          VField                fld = fields[i];

          if (fld.lookupColumn(table) != null) {
            fld.setQuery(recno, query, 1+j);
            j += 1;
          }
        }

        if (query.next()) {
          query.close();
          form.getDBContext().abortWork();
          setActiveRecord(recno);
          throw new VExecFailedException(MessageCode.getMessage("VIS-00020",
                                                                new Object[]{ tables[table] }));
        }
        query.close();
      }
    }
  }

  /*
   * Checks unique index constraints
   * @exception VException      an exception may be raised by triggers
   */
  public void checkUniqueIndices(int recno) throws SQLException, VException {
    if (indices != null) {
      int       id;

      id = isRecordFetched(recno) ? getIdField().getInt(recno).intValue(): -1;

      for (int i = 0; i < indices.length; i++) {
        checkUniqueIndex(i, recno, id);
      }
    }
  }

  /*
   * Checks unique index constraints
   */
  protected void checkUniqueIndex(int idx, int recno, int id)
    throws SQLException, VException
  {
    String      buffer = "";

    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];
      String    col;

      if (fld.isNull(recno) || !fld.hasIndex(idx)) {
        col = null;
      } else {
        col = fld.lookupColumn(0);
      }

      if (col != null) {
        if (! buffer.equals("")) {
          buffer += " AND ";
        }

        buffer += col + " = " + fld.getSql(recno);
      }
    }

    if (! buffer.equals("")) {
      Query             query = new Query(form.getDBContext().getDefaultConnection());

      query.addString(getIdColumn());
      query.addString(tables[0]);
      query.addString(buffer);
      query.open("SELECT $1 FROM $2 WHERE $3");
      if (query.next()) {
        if (query.getInt(1) != id) {
          query.close();
          form.getDBContext().abortWork();
          form.setActiveBlock(this);
          setActiveRecord(recno);
          gotoFirstField();
          throw new VExecFailedException(MessageCode.getMessage("VIS-00014",
                                                                new Object[]{ indices[idx] }));
        }

        assert ! query.next() : "too many rows";
      }
      query.close();
    }
  }

  /**
   * Inserts the specified record of given block into database.
   *
   * @param     recno           the index of the record to insert
   * @param     id              the ID to give to the record or -1 if next available to get
   *
   * @exception VException      an exception may be raised by triggers
   */
  protected void insertRecord(int recno, int id)
    throws VException, SQLException
  {
    try {
      Query             query;
      String            colbuf, valbuf;

      assert !isMulti() || getActiveRecord() == -1 : "isMulti? " + isMulti() + " current record " + getActiveRecord();

      clearLookups(recno);

      if (isMulti()) {
        setActiveRecord(recno);
      }
      callProtectedTrigger(TRG_PREINS);
      for (int i = 0; i < fields.length; i++) {
        fields[i].callProtectedTrigger(TRG_PREINS);
      }
      if (isMulti()) {
        setActiveRecord(-1);
      }

      /* for each Lookup-Table of block check if record exists and is unique */
      selectLookups(recno);

      /* check if unique index constraints are respected by new record */
      checkUniqueIndices(recno);

      /* fill with next id if not given as argument and not overridden */
      fillIdField(recno, id);

      if (! blockHasNoUcOrTsField()) {
        VField          ucFld = getUcField();
        VField          tsFld = getTsField();

        assert ucFld != null || tsFld != null
          : "UC or TS field must exist (Block = " + getName() + ").";

        if (ucFld != null) {
          // The value of the UC field for an insert is 0. Should it be
          // handled by a default value in dbSchema or now ? => we can
          // do both
          ucFld.setInt(recno, new Integer(0));
        }
        if (tsFld != null) {
          // if there is a timestamp field set it with the
          // current time
          tsFld.setInt(recno, new Integer((int)(System.currentTimeMillis()/1000)));
        }
      }

      colbuf = "";
      valbuf = "";

      query = new Query(form.getDBContext().getDefaultConnection());

      for (int i = 0; i < fields.length; i++) {
        VField          fld = fields[i];
        String          col = fld.lookupColumn(0);

        if (col != null) {
          if (! colbuf.equals("")) {
            colbuf += ", ";
            valbuf += ", ";
          }

          colbuf += col;
          valbuf += fld.getSql(recno);
          if (fld.hasLargeObject(recno)) {
            if (fld.hasBinaryLargeObject(recno)) {
              query.addBlob(fld.getLargeObject(recno));
            } else {
              query.addClob(fld.getLargeObject(recno));
            }
          }
        }
      }

      query.addString(tables[0]);
      query.addString(colbuf);
      query.addString(valbuf);
      query.run("INSERT INTO $1 ($2) VALUES ($3)");

      setRecordFetched(recno, true);
      setRecordChanged(recno, false);

      if (isMulti()) {
        setActiveRecord(recno);
      }
      callProtectedTrigger(TRG_POSTINS);
      for (int i = 0; i < fields.length; i++) {
        fields[i].callProtectedTrigger(TRG_POSTINS);
      }
      if (isMulti()) {
        setActiveRecord(-1);
      }
    } catch (VException e) {
      if (isMulti() && getForm().getActiveBlock() != this) {
        setActiveRecord(-1);
      }
      throw e;
    }
  }

  /**
   *
   */
  protected void fillIdField(int recno, int id) throws VException, SQLException {
    if (id == -1) {
      id = KopiUtils.getNextTableId(form.getDBContext().getDefaultConnection(), tables[0]);
    }

    getIdField().setInt(recno, new Integer(id));
  }

  /**
   * Updates current record of given block in database.
   */
  protected void updateRecord(int recno) throws VException, SQLException {
    try {
      VField            idFld;
      VField            ucFld;
      VField            tsFld;
      StringBuffer      buffer;

      assert !isMulti() || getActiveRecord() == -1 : "isMulti? " + isMulti() + " current record " + getActiveRecord();

      clearLookups(recno);

      if (isMulti()) {
        setActiveRecord(recno);
      }
      callProtectedTrigger(TRG_PREUPD);
      for (int i = 0; i < fields.length; i++) {
        fields[i].callProtectedTrigger(TRG_PREUPD);
      }
      if (isMulti()) {
        setActiveRecord(-1);
      }

      /* for each lookup-table of block check if record exists and is unique */
      selectLookups(recno);

      /* check if unique index constraints are respected after update */
      checkUniqueIndices(recno);

      /* verify that the record has not been changed in the database */
      checkRecordUnchanged(recno);

      idFld = getIdField();
      ucFld = getUcField();
      tsFld = getTsField();

      Query   query = new Query(form.getDBContext().getDefaultConnection());

      if (tsFld != null) {
	tsFld.setInt(recno, new Integer((int)(System.currentTimeMillis()/1000)));
      }
      if (ucFld != null) {
        ucFld.setInt(recno, new Integer(ucFld.getInt().intValue() + 1));
      }

      buffer = new StringBuffer();

      for (int i = 0; i < fields.length; i++) {
        VField          fld = fields[i];
        String          col;

        /* do not update ID field */
        if (fld == idFld) {
          continue;
        }

        col = fld.lookupColumn(0);

        if (col != null) {
          if (buffer.length() != 0) {
            buffer.append(", ");
          }

          buffer.append(col + " = " + fld.getSql(recno));
          if (fld.hasLargeObject(recno)) {
            if (fld.hasBinaryLargeObject(recno)) {
              query.addBlob(fld.getLargeObject(recno));
            } else {
              query.addClob(fld.getLargeObject(recno));
            }
          }
        }
      }

      query.addString(tables[0]);
      query.addString(buffer.toString());
      query.addString(getIdColumn());
      query.addInt(idFld.getInt(recno).intValue());
      query.run("UPDATE $1 SET $2 WHERE $3 = #4");

      setRecordChanged(recno, false);

      if (isMulti()) {
        setActiveRecord(recno);
      }
      callProtectedTrigger(TRG_POSTUPD);
      for (int i = 0; i < fields.length; i++) {
        fields[i].callProtectedTrigger(TRG_POSTUPD);
      }
      if (isMulti()) {
        setActiveRecord(-1);
      }
    } catch (VException e) {
      if (isMulti() && getForm().getActiveBlock() != this) {
        setActiveRecord(-1);
      }
      throw e;
    }
  }

  /**
   * Deletes current record of given block from database.
   */
  protected void deleteRecord(int recno) throws VException, SQLException {
    try {
      int               id;

      assert !isMulti() || getActiveRecord() == -1 : "isMulti? " + isMulti() + " current record " + getActiveRecord();

      if (isMulti()) {
        setActiveRecord(recno);
      }
      callProtectedTrigger(TRG_PREDEL);
      for (int i = 0; i < fields.length; i++) {
        fields[i].callProtectedTrigger(TRG_PREDEL);
      }
      if (isMulti()) {
        setActiveRecord(-1);
      }

      id = getIdField().getInt(recno).intValue();

      if (id == 0) {
        form.getDBContext().abortWork();
        setActiveRecord(recno);
        throw new VExecFailedException(MessageCode.getMessage("VIS-00019"));
      }

      VDatabaseUtils.checkForeignKeys(form, id, tables[0]);

      /* verify that the record has not been changed in the database */
      checkRecordUnchanged(recno);

      Query       query = new Query(form.getDBContext().getDefaultConnection());

      try {
        query.addString(tables[0]);
        query.addString(getIdColumn());
        query.addInt(id);
        query.run("DELETE FROM $1 WHERE $2 = #3");
      } catch (DBForeignKeyException e) {
        //query.close(); --- in comment because it produces an error
        form.getDBContext().abortWork();
        setActiveRecord(recno);                 // also valid for single blocks
        throw convertForeignKeyException(e.getConstraint());
      }

      clearRecord(recno);
    } catch (VException e) {
      if (isMulti() && getForm().getActiveBlock() != this) {
        setActiveRecord(-1);
      }
      throw e;
    }
  }

  /**
   * Check whether the given record has been modified (deleted or updated) in the
   * database.
   */
  protected void checkRecordUnchanged(int recno)
    throws SQLException, VExecFailedException
  {
    //!!! samir 25032008 : Assertion enabled only for tables with ID
    if (! blockHasNoUcOrTsField()) {
      VField    idFld = getIdField();
      VField    ucFld = getUcField();
      VField    tsFld = getTsField();
      Query     query = new Query(form.getDBContext().getDefaultConnection());

      assert ucFld != null || tsFld != null
        : "UC or TS field must exist (Block = " + getName() + ").";

      query.addString(ucFld == null ? "-1" : "UC");
      query.addString(tsFld == null ? "-1" : "TS");
      query.addString(tables[0]);
      query.addString(getIdColumn());
      query.addInt(idFld.getInt(recno).intValue());
      query.open("SELECT $1, $2 FROM $3 WHERE $4 = #5");
      if (! query.next()) {
        // kein Eintrag gefunden
        query.close();
        form.getDBContext().abortWork();
        setActiveRecord(recno);
        throw new VExecFailedException(MessageCode.getMessage("VIS-00018"));
      } else {
        boolean         changed;

        changed = false;
        if (ucFld != null) {
          changed |= ucFld.getInt(recno).intValue() != query.getInt(1);
        }
        if (tsFld != null) {
          changed |= tsFld.getInt(recno).intValue() != query.getInt(2);
        }
        query.close();

        if (changed) {
          // record has been updated
          form.getDBContext().abortWork();
          setActiveRecord(recno);           // also valid for single blocks
          throw new VExecFailedException(MessageCode.getMessage("VIS-00017"));
        }
      }
    }
  }

  /**
   * Returns true iff this block has no UC and no TS field.
   * May be overridden in subclasses eg actual blocks. Note: In this case,
   * conflicting deletes or updates of a record being edited, are impossible to
   * detect.
   */
  protected boolean blockHasNoUcOrTsField() {
    return false;
  }

  /*
   * Checks if a foreign key is referenced in the view SYSTEMREFERENZEN
   */
  protected VExecFailedException convertForeignKeyException(String name) {
    try {
      Query             query;
      String            tabelle;
      String            spalte;
      String            referenz;

      form.getDBContext().startWork();

      query = new Query(form.getDBContext().getDefaultConnection());
      query.addString(name);
      query.open("SELECT         tabelle, spalte, referenz " +
                 "FROM          SYSTEMREFERENZEN " +
                 "WHERE         name = #1 ");
      assert query.next() : "no row";
      tabelle = query.getString(1);
      spalte = query.getString(2);
      referenz = query.getString(3);
      query.close();
      form.getDBContext().abortWork();
      return new VExecFailedException(MessageCode.getMessage("VIS-00021",
                                                             new Object[] {
                                                               tabelle + "." + spalte,
                                                               referenz
                                                             }));
    } catch (SQLException e) {
      throw new InconsistencyException();
    }
  }

  // ----------------------------------------------------------------------
  // ACTOR HANDLING (TBC)
  // ----------------------------------------------------------------------

  /**
   *
   */
  protected void setActors(VActor[] actors) {
    this.actors = actors;
  }

  /**
   *
   */
  public VActor[] getActors() {
    VActor[]    temp = actors;

    actors = null;
    return temp;
  }

  public void close() {
    setCommandsEnabled(false);
    if (getActiveField() != null) {
      // !!! TO DO
      //      getActiveField().getUI().close();
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF DBContextHandler
  // ----------------------------------------------------------------------

  public void setDBContext(DBContext context) {
    throw new InconsistencyException("CALL IT ON FORM");
  }

  public DBContext getDBContext() {
    return getForm().getDBContext();
  }

  public void startProtected(String message) {
    getForm().startProtected(message);
  }

  public void commitProtected() throws SQLException {
    getForm().commitProtected();
  }

  public void abortProtected(boolean interrupt) {
    getForm().abortProtected(interrupt);
  }

  public boolean retryableAbort(Exception reason) {
    return getForm().retryableAbort(reason);
  }

  public boolean retryProtected() {
    return getForm().retryProtected();
  }

  public boolean inTransaction() {
    return getForm().inTransaction();
  }

  // ----------------------------------------------------------------------
  // LISTENER
  // ----------------------------------------------------------------------

  public void addBlockListener(BlockListener bl) {
    blockListener.add(BlockListener.class, bl);
  }
  public void removeBlockListener(BlockListener bl) {
    blockListener.remove(BlockListener.class, bl);
  }
  public void addBlockRecordListener(BlockRecordListener bl) {
    blockListener.add(BlockRecordListener.class, bl);
  }
  public void removeBlockRecordListener(BlockRecordListener bl) {
    blockListener.remove(BlockRecordListener.class, bl);
  }

  protected void fireBlockChanged() {
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockListener.class) {
        ((BlockListener)listeners[i+1]).blockChanged();
      }
    }
  }
  protected void fireValidRecordNumberChanged() {
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockListener.class) {
        ((BlockListener)listeners[i+1]).validRecordNumberChanged();
      }
    }
  }
  protected void fireBlockCleared() {
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockListener.class) {
        ((BlockListener)listeners[i+1]).blockCleared();
      }
    }
  }
  protected void fireOrderChanged() {
    Object[]            listeners = blockListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== BlockListener.class) {
        ((BlockListener)listeners[i+1]).orderChanged();
      }
    }
  }
  // ----------------------------------------------------------------------
  // SORTING AND LABELS
  // ----------------------------------------------------------------------

  public OrderModel getOrderModel() {
    return orderModel;
  }

  interface OrderListener extends EventListener {
    void orderChanged();
  }

  public class OrderModel {
    public OrderModel() {
      sortedColumnIndex = -1; // no column is sorted
      orderListener = new EventListenerList();
    }

    private void setState(int i) {
      switch (i) {
      case STE_INC:
        sortOrder = 1;
        state = STE_INC;
        break;
      case STE_DESC:
        sortOrder = -1;
        state = STE_DESC;
        break;
      case STE_UNORDERED:
        sortedColumnIndex = -1;
        sortOrder = 1;
        state = STE_UNORDERED;
        break;
      }
    }

    public void sortColumn(int index) {
      if (sortedColumnIndex == index) {
        switch (state) {
        case STE_INC:
          setState(STE_DESC);
          break;
        case STE_DESC:
          setState(STE_UNORDERED);
          break;
        case STE_UNORDERED:
        default:
          setState(STE_INC);
        }
      } else {
        sortedColumnIndex = index;
        setState(STE_INC);
      }

      sort(sortedColumnIndex, sortOrder);
      fireOrderChanged();
    }

    public int getColumnOrder(int index) {
      if (index == sortedColumnIndex) {
        return state;
      } else {
        return STE_UNORDERED;
      }
    }


    private void fireOrderChanged() {
      Object[]            listeners = orderListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]== OrderListener.class) {
          ((OrderListener)listeners[i+1]).orderChanged();
        }
      }
    }

    public void addSortingListener(OrderListener sl) {
      orderListener.add(OrderListener.class, sl);
    }

    private int         sortedColumnIndex;
    private int         state;
    private int         sortOrder;

    private EventListenerList   orderListener;

    static final int    STE_UNORDERED = 1;
    static final int    STE_INC = 2;
    static final int    STE_DESC = 4;
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  public void helpOnBlock(VHelpGenerator help) {
    if (!isAlwaysSkipped()) {
      help.helpOnBlock(getForm().getClass().getName().replace('.', '_'),
                       title,
                       this.help,
                       commands,
                       fields,
                       getForm().blocks.length == 1);
    }
  }

  public int getFieldPos(VField field) {
    int count = 1;
    for (int i = 0; i < fields.length; i++) {
      if (field == fields[i]) {
        return count;
      }
      if (fields[i].getDefaultAccess() != ACS_HIDDEN) {
        count++;
      }
    }
    return 0;
  }

  public String[] getBlockTables() {
    return tables;
  }

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------

  /**
   * prepare a snapshot of all fields
   */
  public void prepareSnapshot(boolean active) {
    // set background ???
    if (commands != null) {
      for (int i = 0; i < commands.length; i++) {
        commands[i].setEnabled(true);
      }
    }

    int count = 1;
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getDefaultAccess() != ACS_HIDDEN) {
        fields[i].prepareSnapshot(count++, active);
      }
    }
  }

  public String toString() {
    StringBuffer        information = new StringBuffer();

    try {
      information.append("\n-----------------------------------------------\nBLOCK ");
      information.append(name);
      information.append(" Shortcut: ");
      information.append(shortcut);
      information.append(" Title: ");
      information.append(title);
      information.append("\n");

      information.append("bufferSize: ");
      information.append(bufferSize);
      information.append("; fetchSize: ");
      information.append(fetchSize);
      information.append("; displaySize: ");
      information.append(displaySize);
      information.append("; page: ");
      information.append(page);
      information.append("\n");

      information.append("mode: ");
      information.append(mode);
      information.append("\n");

      information.append("activeRecord :");
      information.append(activeRecord);
      information.append("; activeField ");
      if (activeField == null) {
        information.append(": null");
      } else {
        information.append("\n");
        information.append(activeField.toString());
      }
      information.append("\n");

      information.append("fetchCount: ");
      information.append(fetchCount);
      information.append("; recordCount: ");
      information.append(recordCount);
      information.append("; fetchPosition: ");
      information.append(fetchPosition);
      //       information.append("; toprec: ");
      //       information.append(toprec);
      information.append("\n");

      information.append("CURRENT RECORD:\n");
      if (fields != null) {
        for (int i=0; i < fields.length; i++) {
          if (fields[i] != null) {
            information.append(fields[i].toString());
          } else {
            information.append("Field ");
            information.append(i);
            information.append(" is null\n");
          }
        }
      } else {
        information.append("No information about fields available.\n");
      }
    } catch (Exception e) {
      information.append("Exception while retrieving bock information. \n");
    }
    return information.toString();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected int[]               sortedRecords;

  protected boolean             blockAccess;
  // prevent that the access of a field is updated
  // (performance in big charts)
  protected boolean             ignoreAccessChange;

  // record info flags
  protected static final int RCI_FETCHED                = 0x00000001;
  protected static final int RCI_CHANGED                = 0x00000002;
  protected static final int RCI_DELETED                = 0x00000004;
  protected static final int RCI_TRAILED                = 0x00000008;

  // static (compiled) data
  protected  VForm              form;           // enclosing form

  protected int                 bufferSize;     // max number of buffered records
  protected int                 fetchSize;      // max number of buffered IDs
  protected int                 displaySize;    // max number of displayed records
  protected int                 page;           // page number

  protected String              source;         // qualified name of source file
  protected String              name;           // block name
  protected String              shortcut;       // block short name
  protected String              title;          // block title
  protected BlockAlignment      align;
  protected String              help;           // the help on this block
  protected String[]            tables;         // names of database tables
  protected int                 options;        // block options
  protected int[]               access;         // access flags for each mode
  protected String[]            indices;        // error messages for violated indices

  protected VCommand[]          commands;       // commands
  protected VActor[]            actors;         // actors to send to form (move to block import)
  protected VField[]            fields;         // fields
  protected int[][]             VKT_Triggers;

  // dynamic data
  protected int                 activeRecord;   // current record
  protected VField              activeField;
  protected boolean             detailMode;
  protected int                 recordCount;    // number of active records
  protected Vector              activeCommands; // commands currently active

  protected int                 currentRecord;

  protected int                 mode;           // current mode
  protected int[]               recordInfo;     // status vector for records
  protected int[]               fetchBuffer;    // holds Id's of fetched records
  protected int                 fetchCount;     // # of fetched records
  protected int                 fetchPosition;  // position of current record

  protected EventListenerList   blockListener;
  protected OrderModel          orderModel;

  protected int                 border;
  protected int                 maxRowPos;
  protected int                 maxColumnPos;
  protected int                 displayedFields;
}
