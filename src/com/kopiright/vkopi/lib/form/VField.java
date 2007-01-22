/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
import java.io.InputStream;
import java.sql.SQLException;

import javax.swing.event.EventListenerList;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.BlockLocalizer;
import com.kopiright.vkopi.lib.l10n.FieldLocalizer;
import com.kopiright.vkopi.lib.list.VColumn;
import com.kopiright.vkopi.lib.list.VList;
import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.Month;
import com.kopiright.xkopi.lib.type.Time;
import com.kopiright.xkopi.lib.type.Week;

/**
 * A field is a column in the the database (a list of rows)
 * it provides an access to data both programmatically or via a UI
 * (DForm)
 */
public abstract class VField implements VConstants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  protected VField(int width, int height) {
    setDimension(width, height);
  }

  /**
   * Sets the dimensions
   */
  public void setDimension(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   *
   */
  public void setBlock(VBlock block) {
    this.block = block;
    this.dynAccess = new int[block.getBufferSize()];
    setAccess(-1);
  }

  /**
   * set information on the field.
   */
  public void setInfo(String name,
		      int index,
		      int posInArray,
		      int options,
		      int[] access,
		      VList list,
		      VColumn[] columns,
		      int indices,
		      int priority,
		      VCommand[] commands,
		      VPosition pos,
		      int align,
		      VField alias)
  {
    this.name = name;
    this.index = index;
    this.posInArray = posInArray;
    this.options = options;
    this.access = access;
    this.list = list;
    this.columns = columns;
    if (columns == null) {
      this.columns = new VColumn[0];
    }
    this.indices = indices;
    this.priority = priority;
    this.align = align;
    if (this instanceof VFixedField) {
      // move it to compiler !!!
      this.align = ALG_RIGHT;
    }
    this.pos = pos;
    this.cmd = commands;
    this.alias = alias;
    if (alias != null) {
      alias.addFieldChangeListener(new FieldChangeListener() {
          public void labelChanged() {}
          public void searchOperatorChanged() {}
          public void valueChanged(int r) {
            fireValueChanged(r);
          }
          public void accessChanged(int r) {}
        });
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * The name of the field is the ident in the kopi language
   * @return	the name of this field
   */
  public String getName() {
    return name;
  }

  /**
   * The name displayed on the left of this field
   */
  public String getLabel() {
    return label;
  }

  /**
   * The name displayed on the left of this field
   */
  public void setLabel(String label) {
    this.label = label;
    fireLabelChanged();
  }

  /**
   * The position of the label (left / top)
   */
  public void setLabelPos(int pos) {
    fireLabelChanged();
  }

  /**
   * Returns the list
   */
  public VList getList() {
    return list;
  }

  /**
   * The tooltip of the field is a small sentence that describe usage of the field
   * It is the first line of the field help
   * @return	the help of this field
   */
  public String getToolTip() {
    return help;
  }

  /**
   *
   */
  public boolean isNoEdit() {
    return (options & FDO_NOEDIT) != 0;
  }

  /**
   *
   */
  public boolean isTransient() {
    return (options & FDO_TRANSIENT) != 0;
  }

  /**
   *
   */
  public boolean noDetail() {
    return (options & FDO_NODETAIL) != 0 || block.noDetail();
  }
  /**
   *
   */
  public boolean noChart() {
    return (options & FDO_NOCHART) != 0 || block.noChart();
  }
  /**
   *
   */
  public boolean isSortable() {
    return (options & FDO_SORT) != 0;
  }

  /**
   *
   */
  public boolean eraseOnLookup() {
    return (options & FDO_DO_NOT_ERASE_ON_LOOKUP) == 0;
  }

  public boolean hasAutofill() {
     return list != null;
   }

  /**
   * return true if this field implements "enumerateValue"
   */
  public boolean hasNextPreviousEntry() {
    return list != null;
  }

  /**
   * The width of a field is the max number of character needed to display
   * any value
   * @return	the width of this field
   */
  public int getWidth() {
    return width;
  }

  /**
   * The height of a field is the max number of line needed to display
   * any value
   * @return	the width of this field
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns the option of this field
   */
  public int getOptions() {
    return options;
  }

  /**
   * For Oracle
   */
  public void setOptions(int options) {
    this.options = options;
  }

  /**
   * Returns the alignment
   */
  public int getAlign() {
    return align;
  }

  public VPosition getPosition() {
    return pos;
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  public void localize(BlockLocalizer parent) {
    FieldLocalizer      loc;

    loc = parent.getFieldLocalizer(name);
    setLabel(loc.getLabel()); 
    help = loc.getHelp();
    if (list != null) {
      list.localize(loc.getManager());
    }

    // field type specific localizations
    localize(loc);
  }
  
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  protected void localize(FieldLocalizer loc) {
    // by default nothing to do
  }

  // ----------------------------------------------------------------------
  // PUBLIC COMMANDS
  // ----------------------------------------------------------------------

  // called if the in a chart the line changes, but it is still visible
  public void updateText() throws VException {
    if (changedUI) {
      modelNeedUpdate();
    }
  }

  /**
   * Validate the field, ie: get the last displayed value, check it and check mustfill
   */
  public void validate() throws VException {
    if (changed) {
      if (changedUI) {
        modelNeedUpdate();
      }
      callTrigger(TRG_PREVAL);
      checkList();
      try {
	if (!isNull(block.getActiveRecord())) {
	  callTrigger(TRG_VALFLD);
	}
	callTrigger(TRG_POSTCHG);
      } catch (VFieldException e) {
	e.resetValue();
	throw e;
      }
      changed = false; // !!! check
      changedUI = false;
    }
    //if (getAccess() == ACS_MUSTFILL && isNull(block.getActiveRecord())) {
    //  throw new VFieldException(this, Message.getMessage("field_mustfill"));
    //}
  }

  /**
   * Verify that text is valid (during typing)
   *
   * @param	s		the text to check
   * @return	true if the text is valid
   */
  protected abstract boolean checkText(String s);

  /**
   * verify that value is valid (on exit)
   *
   * @param	s		the object to check
   * @exception	VException	an exception is raised if text is bad
   */
  protected abstract void checkType(Object s) throws VException;

  /**
   * text has changed (key typed on a display)
   */
  protected void onTextChange(String text) {
    changed = true;
    changedUI = true;
    autoLeave(text);
  }

  private void autoLeave(String check) {
    assert this == block.getActiveField() : threadInfo() + "current field: "+block.getActiveField();
    if (!hasTrigger(TRG_AUTOLEAVE)) {
      return;
    }

    boolean autoleave = false;

    try {
      autoleave = ((Boolean)callTrigger(TRG_AUTOLEAVE)).booleanValue();
    } catch (VException e) {
      throw new InconsistencyException("autoleave can not throw a VException", e);
    }

    if (autoleave) {
      KopiAction action = new KopiAction("autoleave") {
          public void execute() throws VException {
            getBlock().getForm().getActiveBlock().gotoNextField();
          }
        };    
      ((DField) getDisplay()).getBlockView().getFormView().performAsyncAction(action);
    }
  }

  /**
   * Return the display
   */
  public Component getDisplay() {
    Component           value = null;

    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0 && value == null; i-=2) {
        if (listeners[i]==FieldListener.class) {
          value = ((FieldListener)listeners[i+1]).getCurrentDisplay();
        }
      }
    }

    return value;
  }

  public int getType() {
    return MDL_FLD_TEXT;
  }

  public void build() {
   setAccess(access[MOD_QUERY]);
  }

  public VCommand[] getCommand() {
    return cmd;
  }

  /**
   * Display error
   */
  public void displayFieldError(String message) {
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldListener.class) {
          ((FieldListener)listeners[i+1]).fieldError(message);
        }
      }
    }
  }


  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception	VException	an exception may occur in gotoNextField
   */
  public final void autofill() throws VException {
    // programatic autofill => no UI
    //    autofill(false, false); replaced by
    fillField(null); // no Handler
  }

  public final void autofill(boolean showDialog, boolean gotoNextField) throws VException {
    autofill();
  }
  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception	VException	an exception may occur in gotoNextField
   */
  public final void predefinedFill() throws VException {
    if (hasListener) {
      boolean           filled = false;
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0 && !filled; i-=2) {
        if (listeners[i]==FieldListener.class) {
          filled = ((FieldListener)listeners[i+1]).predefinedFill();
        }
      }
    }
  }

  protected boolean fillField(PredefinedValueHandler handler) throws VException {
    if (handler != null) {
      return handler.selectDefaultValue();
    } else {
      return false;
    }
  }

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return a list column for list
   */
  protected abstract VListColumn getListColumn();

  // ----------------------------------------------------------------------
  // NAVIGATING
  // ----------------------------------------------------------------------

  /**
   * enter a field
   */
  public final void enter() {
    assert block == getForm().getActiveBlock() : threadInfo() + "field : " + getName() + " block : " + block.getName() + " active block : " + getForm().getActiveBlock().getName();
    assert block.getActiveRecord() != -1 : threadInfo() + "current record = " + block.getActiveRecord();
    assert block.getActiveField() == null : threadInfo() + "current field: " + block.getActiveField();
    block.setActiveField(this);
    changed = false;

    fireEntered();

    try {
      callTrigger(TRG_PREFLD);
    } catch (VException e) {
      throw new InconsistencyException(e);
    }
  }

  /**
   * when leaving field, if text is okay, set value in record
   * @exception	VException	an exception is raised if text is bad
   */
  public final void leave(boolean check) throws VException {
    assert this == block.getActiveField() : threadInfo() + "current field: "+block.getActiveField();
    try {
      if (check && changed) {
	if (changedUI && hasListener) {
	  checkType(getDisplayedValue(true));
	}
	callTrigger(TRG_PREVAL);
	checkList();
	try {
	  if (!isNull(block.getActiveRecord())) {
	    callTrigger(TRG_VALFLD);
	  }
	  callTrigger(TRG_POSTCHG);
	} catch (VFieldException e) {
	  e.resetValue();
	  throw e;
	}
      } else if (getForm().getEnvironment().setTextOnFieldLeave()) {
        if (changed && changedUI && hasListener) {
          checkType(getDisplayedValue(true));
        }
      }
    } catch (VException e) {
      throw e;
    }
    changed = false;
    changedUI = false;

    callTrigger(TRG_POSTFLD);

    block.setActiveField(null);
    fireLeaved();
  }

  /**
   *
   */
  public boolean hasFocus() {
    return block.getActiveField() == this;
  }

  /**
   * Changes access dynamically, overriding mode access
   */
  public void setAccess(int at, int value) {
    if (getDefaultAccess() < value) {
      // access can never be higher than the default
      // access
      value = getDefaultAccess();
    }

    if (value != dynAccess[at]) {
      dynAccess[at] = value;
      fireAccessChanged(at);
    }
  }

  public void setAccess(int value) {
    for (int i = 0; i < block.getBufferSize(); i++) {
      setAccess(i, value);
    }
  }

  /**
   * Changes access dynamically, overriding mode access
   */
  public void setAccess(int[] access) {
    assert access.length == this.access.length : threadInfo() + "new acces length: " + access.length + " old: " + this.access.length;
    this.access = access;
  }

  /**
   * return access of this field in current mode
   */
  public int getDefaultAccess() {
    return access[block.getMode()];
  }

  public int getAccess(int i) {
    if (i == -1) {
      return getDefaultAccess();
    } else {
      if (isInternal()) {
        return ACS_HIDDEN;
      } else {
        return dynAccess[i];
      }
    }
  }

  /**
   * @deprecated use the method <code>updateAccess()<code> instead.
   */
  public final void getAccess() {
    updateAccess(getBlock().getActiveRecord());
  }

  public final void updateAccess() {
    updateAccess(getBlock().getCurrentRecord());
  }

  public final void updateAccess(int current) {
    if (isInternal()) {
      // internal fields are always hidden
      // there no need to update the field
      // (also neccessary for performance)
      return;
    }

    // used for debugging
    //    if (current < 5) System.out.println("---- VBlock updateAccess() : current = " + current + "  " + getName());

    int		defaultAccess = getDefaultAccess();
    int		accessTemp = defaultAccess;//dynAccess;

    if (current != -1) {
      if ((!getBlock().isRecordInsertAllowed(current))) {
        accessTemp = ACS_SKIPPED;
      } else if (hasTrigger(TRG_FLDACCESS)) {
        // evaluate ACCESS-Trigger
        int     oldrow = getBlock().getActiveRecord();
	VField  old = getBlock().getActiveField();

        // used by callTrigger
        getBlock().setActiveRecord(current);
        try {
          getBlock().setActiveField(this);
          accessTemp = ((Integer)callTrigger(TRG_FLDACCESS)).intValue();
          getBlock().setActiveField(old);
        } catch (Exception e) {
          e.printStackTrace();
          getBlock().setActiveField(old);
          // !!! fix reporting of bug
          //        throw new InconsistencyException(e.getMessage());
        }
        getBlock().setActiveRecord(oldrow);
      }

    }

    if (defaultAccess < accessTemp) {
      accessTemp = defaultAccess;
    }
    if (current == -1) {
      setAccess(accessTemp);
    } else {
      setAccess(current, accessTemp);
    }
  }

  public void setBorder(int border) {
    this.border = border;
  }

  public int getBorder() {
    return border;
  }

  public void updateModeAccess() {
    // TOO SIMPLE (ACCESS TRIGGER IGNORED)
    //    setAccess(access[block.getMode()]);
    //    assert !block.isMulti() || block.getActiveRecord() == -1 : threadInfo() + "current record = " + block.getActiveRecord();

    for (int i=0; i < block.getBufferSize(); i++) {
      updateAccess(i);
    }
  }

  // ----------------------------------------------------------------------
  // RESET TO DEFAULT
  // ----------------------------------------------------------------------

  /**
   * Sets default values
   */
  public void setDefault() {
    if (isNull(block.getActiveRecord())) {
      try {
	callTrigger(TRG_DEFAULT);
      } catch (VException e) {
	throw new InconsistencyException(); // !!! NO, Just a VExc...
      }
    }
  }

  // ----------------------------------------------------------------------
  // QUERY BUILD
  // ----------------------------------------------------------------------

  /**
   * Returns the number of database columns associated to the field.
   * !!! change name
   */
  public int getColumnCount() {
    return columns.length;
  }

  /**
   * Returns the database column at given position.
   */
  public VColumn getColumn(int n) {
    return columns[n];
  }

  /**
   * Returns the column name in the table with specified correlation.
   * returns null if the field has no access to this table.
   */
  public String lookupColumn(int corr) {
    for (int i = 0; i < getColumnCount(); i++) {
      if (corr == columns[i].getTable()) {
	return columns[i].getName();
      }
    }
    return null;
  }

  /**
   * Returns true if the first column is a key in table
   */
  public boolean isKey() {
    return columns.length == 0 ? false : columns[0].isKey();
  }

  /**
   * Is the field part of given index ?
   */
  public boolean hasIndex(int idx) {
    return (indices & (1 << idx)) != 0;
  }

  /**
   * Returns the position in select results.
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @return the type of search condition for this field.
   *
   * @see VConstants
   */
  public int getSearchType() {
    if (isNull(block.getActiveRecord())) {
      if (getSearchOperator() == SOP_EQ) {
	return STY_NO_COND;
      } else if (getSearchOperator() == SOP_NE) {
	return STY_MANY;
      } else {
	return STY_EXACT;
      }
    } else {
      String	 buffer = getSql(block.getActiveRecord());

      if (buffer.indexOf('*') == -1) {
	return getSearchOperator() == SOP_EQ ? STY_EXACT : STY_MANY;
      } else {
	return STY_MANY;
      }
    }
  }

  /**
   * Returns the search conditions for this field.
   */
  public String getSearchCondition() {
    if (isNull(block.getActiveRecord())) {
      if (getSearchOperator() == SOP_EQ) {
	return null;
      } else if (getSearchOperator() == SOP_NE) {
	return "IS NOT NULL";
      } else {
	return "IS NULL";
      }
    } else {
      String	operator = OPERATOR_NAMES[getSearchOperator()];
      String	operand = getSql(block.getActiveRecord());


      if (operand.indexOf('*') == -1) {
	// nothing to change: standard case
      } else {
	switch (getSearchOperator()) {
	case SOP_EQ:
	  operator = "LIKE ";
	  operand = operand.replace('*', '%');
	  break;

	case SOP_NE:
	  operator = "NOT LIKE ";
	  operand = operand.replace('*', '%');
	  break;

	case SOP_GE:
	case SOP_GT:
	  // remove everything after at '*'
	  operand = operand.substring(0, operand.indexOf('*')) + "'";
	  break;

	case SOP_LE:
	case SOP_LT:
	  // replace substring starting at '*' by highest (ascii) char
	  operand = operand.substring(0, operand.indexOf('*')) + "\377'";
	  break;

	default:
	  throw new InconsistencyException();
	}
      }

      switch (options & FDO_SEARCH_MASK) {
      case FDO_SEARCH_NONE:
        break;
      case FDO_SEARCH_UPPER:
        operand = "{fn UPPER(" + operand + ")}";
        break;
      case FDO_SEARCH_LOWER:
        operand = "{fn LOWER(" + operand + ")}";
        break;
      default:
        throw new InconsistencyException("FATAL ERROR: bad search code: " + options);
      }

      return operator + " " + operand;
    }
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns the field label.
   * @kopi	inaccessible
   */
  String getHeader() {
    return label == null ? "" : label.substring(0, label.length() - 1);
  }

  /**
   * Returns the position of this field in the array of fields or -1
   * if this field is not in a multifield
   */
  public int getPosInArray() {
    return posInArray;
  }

  // ----------------------------------------------------------------------
  // MANAGING FIELD VALUES
  // ----------------------------------------------------------------------
  /**
   * return the name of this field
   */
  public int getTypeOptions() {
    return 0;
  }

  /**
   * Sets the search operator for the field
   * @see VConstants
   */
  public void setSearchOperator(int value) {
    if (value >= OPERATOR_NAMES.length) {
      throw new InconsistencyException("Value " + value + " is not a valid operator");
    }

    //    fireInfoChanged();
//     if (ui != null) {
//       ui.fireInfoHasChanged();
//     }
    if (searchOperator != value) {
      searchOperator = value;
      fireSearchOperatorChanged();
    }
  }

  /**
   * @return the search operator for the field
   * @see VConstants
   */
  public int getSearchOperator() {
    return searchOperator;
  }

  /**
   * Sets the field value of the current record to a null value.
   */
  public void setNull() {
    setNull(block.getCurrentRecord());
  }

  /**
   * Sets the field value of the current record to a bigdecimal value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setFixed(Fixed v) {
    setFixed(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a boolean value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setBoolean(Boolean v) {
    setBoolean(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a date value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setDate(Date v) {
    setDate(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a month value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setMonth(Month v) {
    setMonth(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a int value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setInt(Integer v) {
    setInt(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setObject(Object v) {
    setObject(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a string value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setString(String v) {
    setString(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of given record to a date value.
   */
  public void setImage(byte[] v) {
    setImage(getBlock().getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a time value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setTime(Time v) {
    setTime(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a week value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setWeek(Week v) {
    setWeek(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of the current record to a color value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setColor(java.awt.Color v) {
    setColor(block.getCurrentRecord(), v);
  }

  /**
   * Sets the field value of given record to a null value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public abstract void setNull(int r);

  /**
   * Sets the field value of given record to a bigdecimal value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setFixed(int r, Fixed v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a boolean value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setBoolean(int r, Boolean v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a date value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setDate(int r, Date v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a month value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setMonth(int r, Month v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a week value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setWeek(int r, Week v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a int value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setInt(int r, Integer v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public abstract void setObject(int r, Object v);

  /**
   * Sets the field value of given record to a string value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setString(int r, String v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a date value.
   */
  public void setImage(int r, byte[] v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a time value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setTime(int r, Time v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of given record to a color value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setColor(int r, java.awt.Color v) {
    throw new InconsistencyException();
  }

  /**
   * Sets the field value of the current record from a query tuple.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public void setQuery(Query query, int column) throws SQLException {
    setQuery(block.getCurrentRecord(), query, column);
  }

  /**
   * Sets the field value of given record from a query tuple.
   * @param	record		the index of the record
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public void setQuery(int record, Query query, int column)
    throws SQLException
  {
    setObject(record, retrieveQuery(query, column));
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public abstract Object retrieveQuery(Query query, int column)
    throws SQLException;

  // ----------------------------------------------------------------------
  // FIELD VALUE ACCESS
  // ----------------------------------------------------------------------

  /**
   * Is the field value of the current record null ?
   */
  public boolean isNull() {
    return isNull(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObject() {
    return getObject(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a bigdecimal value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Fixed getFixed() {
    return getFixed(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a boolean value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Boolean getBoolean() {
    return getBoolean(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a date value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Date getDate() {
    return getDate(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a int value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Integer getInt() {
    return getInt(block.getCurrentRecord());
  }

  /**
   * Returns the field value of given record as a date value.
   */
  public byte[] getImage() {
    return getImage(getBlock().getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a month value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Month getMonth() {
    return getMonth(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a string value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public String getString() {
    return getString(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a time value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Time getTime() {
    return getTime(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a week value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Week getWeek() {
    return getWeek(block.getCurrentRecord());
  }

  /**
   * Returns the field value of the current record as a time value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public java.awt.Color getColor() {
    return getColor(block.getCurrentRecord());
  }

  /**
   * Returns the display representation of field value of the current record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public String getText() {
    return getText(block.getCurrentRecord());
  }

  /**
   * Returns the SQL representation of field value of the current record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public String getSql() {
    return getSql(block.getCurrentRecord());
  }

  /**
   * Is the field value of given record null ?
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public final boolean isNull(int r) {
    if (alias != null) {
      return alias.isNull(0);
    }
    if (hasTrigger(TRG_VALUE)) {
      return callSafeTrigger(TRG_VALUE) == null;
    }
    return isNullImpl(r);
  }

  /**
   * Is the field value of given record null ?
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public abstract boolean isNullImpl(int r);

  /**
   * Returns the field value of the current record as an object
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public final Object getObject(int r) {
    if (alias != null) {
      return alias.getObject(0);
    }
    if (hasTrigger(TRG_VALUE)) {
      return callSafeTrigger(TRG_VALUE);
    }

    return getObjectImpl(r);
  }

  /**
   * Returns the field value of the current record as an object
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public abstract Object getObjectImpl(int r);

  /**
   * Returns the field value of given record as a bigdecimal value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Fixed getFixed(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a boolean value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Boolean getBoolean(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a date value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Date getDate(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a month value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Month getMonth(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a week value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Week getWeek(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a int value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Integer getInt(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a date value.
   */
  public byte[] getImage(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a string value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public String getString(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a time value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public Time getTime(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a color value.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public java.awt.Color getColor(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the display representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public final String getText(int r) {
    if (alias != null) {
      return alias.getText(0);
    }
    if (hasTrigger(TRG_VALUE)) {
      Object    value = callSafeTrigger(TRG_VALUE);
      Object    currentValue = getObjectImpl(r);

      if (!value.equals(currentValue)) {
        // set Value only if necessary otherwise an endless loop
        // alternative solution: do this check in setChanged
        setObject(r, value);
      }
    }

    return getTextImpl(r);
  }

  /**
   * Returns the display representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public abstract String getTextImpl(int r);

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public final String getSql(int r) {
    if (alias != null) {
      return alias.getSql(0);
    }
    if (hasTrigger(TRG_VALUE)) {
      setObject(r, callSafeTrigger(TRG_VALUE));
    }
    return getSqlImpl(r);
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public abstract String getSqlImpl(int r);

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasLargeObject(int r) {
    return false;
  }

  /**
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasBinaryLargeObject(int r) {
    throw new InconsistencyException("NO LOB WITH THIS FIELD " + this);
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public InputStream getLargeObject(int r) {
    throw new InconsistencyException("NO BLOB WITH THIS FIELD " + this);
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  /**
   * Copies the fields value of a record to another
   */
  public abstract void copyRecord(int f, int t);

  /**
   * Returns the containing block.
   */
  public VBlock getBlock() {
    return block;
  }

  /**
   * Returns the containing block.
   */
  public VForm getForm() {
    return block.getForm();
  }

  /**
   * Returns true if field is never displayed.
   */
  public final boolean isInternal() {
    return
      access[MOD_QUERY] == ACS_HIDDEN &&
      access[MOD_INSERT] == ACS_HIDDEN &&
      access[MOD_UPDATE] == ACS_HIDDEN;
  }

  // ----------------------------------------------------------------------
  // PROTECTED UTILS
  // ----------------------------------------------------------------------

  /**
   * trails (backups) the record if called in a transaction and restore it
   * if the transaction is aborted.
   */
  protected final void trail(int r) {
    if (!isTransient() && !hasTrigger(TRG_VALUE) && alias == null) {
      block.trailRecord(r);
    }
  }

  /**
   * Marks the field changed, trails the record if necessary
   */
  protected final void setChanged(int r) {
    if (!isTransient() && !hasTrigger(TRG_VALUE) && alias == null) {
      block.setRecordChanged(r, true);
    } else {
      getBlock().updateAccess(r);
    }

    changed = true;
    changedUI = false;

    fireValueChanged(r);
  }

  public boolean isChanged() {
    return changed;
  }

  public boolean isChangedUI() {
    return changedUI;
  }

  /**
   * Marks the field changed, trails the record if necessary
   */
  public final void setChanged(boolean changed) {
    if (changed && block.getActiveRecord() != -1) {
      block.setRecordChanged(block.getActiveRecord(), true);
    }

    this.changed = changed;
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILS
  // ----------------------------------------------------------------------

  /**
   * @return a String with the current thread information for debugging
   */
  private static String threadInfo() {
    return "Thread: " + Thread.currentThread() + "\n";
  }


  /**
   * Checks that field value exists in list
   */
  private void checkList() throws VException {
    if (!getForm().getEnvironment().forceCheckList()) {
      // Oracle doesn't force the value to be in the list
      return;
    }

    final String SELECT_IS_IN_LIST =
      " SELECT   1					" +
      " FROM	 $2					" +
      " WHERE	 $1 = $3";

    final String SELECT_MATCHING_STRINGS =
      " SELECT	 $1					" +
      " FROM	 $2					" +
      " WHERE	 {fn SUBSTRING($1, 1, {fn LENGTH(#3)})} = #3	" +
      " ORDER BY 1";

    if (isNull(block.getActiveRecord())) {
      return;
    }

    if (list == null) {
      return;
    }

    boolean alreadyProtected = getForm().inTransaction();
    if (!(this instanceof VStringField)) {
      boolean		exists;

      try {
	for (;;) {
	  try {
	    if (!alreadyProtected) {
	      getForm().startProtected(null);
	    }

	    Query	query = new Query(getForm().getDBContext().getDefaultConnection());

	    query.addString(list.getColumn(0).getColumn());
	    query.addString(evalListTable());
	    query.addString(getSql(block.getActiveRecord()));
	    query.open(SELECT_IS_IN_LIST);
	    exists = query.next();
	    query.close();

	    if (!alreadyProtected) {
	      getForm().commitProtected();
	    }
	    break;
	  } catch (SQLException e) {
	    if (!alreadyProtected) {
	      getForm().abortProtected(e);
	    } else {
	      throw e;
	    }
	  } catch (Error error) {
	    if (!alreadyProtected) {
	      getForm().abortProtected(error);
	    } else {
	      throw error;
	    }
	  } catch (RuntimeException rte) {
	    if (!alreadyProtected) {
	      getForm().abortProtected(rte);
	    } else {
	      throw rte;
	    }
          }
	}
      } catch (Throwable e) {
	throw new VExecFailedException(e);
      }

      if (! exists) {
	throw new VFieldException(this, Message.getMessage("no_match"));
      }

      return;
    } else {
      Query		query;
      String		fldbuf;
      int		count = 0;
      String		result = null;

      fldbuf = getSql(block.getActiveRecord());
      if (fldbuf.indexOf('*') > 0) {
	return;
      }

      try {
	for (;;) {
	  try {
	    if (!alreadyProtected) {
	      getForm().startProtected(null);
	    }

	    query = new Query(getForm().getDBContext().getDefaultConnection());
	    query.addString(list.getColumn(0).getColumn());
	    query.addString(evalListTable());
	    query.addString(getString(block.getActiveRecord()));
	    query.open(SELECT_MATCHING_STRINGS);
	    if (!query.next()) {
	      count = 0;
	    } else {
	      count = 1;
	      result = query.getString(1);

	      if (query.next()) {
		count = 2;
	      }
	    }
	    query.close();

	    if (!alreadyProtected) {
	      getForm().commitProtected();
	    }
	    break;
	  } catch (SQLException e) {
	    if (!alreadyProtected) {
	      getForm().abortProtected(e);
	    } else {
	      throw e;
	    }
	  } catch (Error error) {
	    if (!alreadyProtected) {
	      getForm().abortProtected(error);
	    } else {
	      throw error;
	    }
	  } catch (RuntimeException rte) {
	    if (!alreadyProtected) {
	      getForm().abortProtected(rte);
	    } else {
	      throw rte;
	    }
	  }
	}
      } catch (Throwable e) {
	throw new VExecFailedException(e);
      }

      switch (count) {
      case 0:
	throw new VFieldException(this, Message.getMessage("no_match"));

      case 1:
	if (! result.equals(getString(block.getActiveRecord()))) {
	  setString(block.getActiveRecord(), result);
	}
	return;

      case 2:
	if (result.equals(getString(block.getActiveRecord()))) {
	  return;
	} else {
	  String	qrybuf;
	  String	colbuf = "";

	  for (int i = 0; i < list.columnCount(); i++) {
	    if (i != 0) {
	      colbuf += ", ";
	    }
	    colbuf += list.getColumn(i).getColumn();
	  }

	  qrybuf =
	    " SELECT   " + colbuf +
	    " FROM     " + evalListTable() +
            " WHERE    {fn SUBSTRING(" + list.getColumn(0).getColumn() + ", 1, {fn LENGTH(" + fldbuf + ")})} = " + fldbuf +
	    " ORDER BY 1";

	  result = (String)displayQueryList(qrybuf, list.getColumns());

	  if (result == null) {
	    throw new VExecFailedException();	// no message to display
	  } else {
	    setString(block.getActiveRecord(), result);
	    return;
	  }
	}

      default:
	throw new InconsistencyException(threadInfo() + "count = " + count);
      }
    }
  }

  /**
   * Checks that field value exists in list
   * !!! TRY TO MERGE WITH checkList ???
   */
  public int getListID() throws VException {
    final String SELECT_IS_IN_LIST =
      " SELECT  ID			" +
      " FROM	$2			" +
      " WHERE	$1 = $3";

    assert !isNull(block.getActiveRecord()) : threadInfo() + " is null";
    assert list != null : threadInfo() + "list ist not null";
    int		id = -1;
    try {
      for (;;) {
	try {
	  getForm().startProtected(null);

	  Query	query = new Query(getForm().getDBContext().getDefaultConnection());

	  query.addString(list.getColumn(0).getColumn());
	  query.addString(evalListTable());
	  query.addString(getSql(block.getActiveRecord()));
	  query.open(SELECT_IS_IN_LIST);
	  if (query.next()) {
	    id = query.getInt(1);
	  }

	  query.close();

	  getForm().commitProtected();
	    break;
	} catch (SQLException e) {
	  getForm().abortProtected(e);
	} catch (Error error) {
          getForm().abortProtected(error);
        } catch (RuntimeException rte) {
          getForm().abortProtected(rte);
        }
      }
    } catch (Throwable e) {
      throw new VExecFailedException(e);
    }

    if (id == -1) {
      throw new VFieldException(this, Message.getMessage("no_match"));
    }

    return id;
  }

  private Object displayQueryList(String queryText, VListColumn[] columns)
    throws VException {

    final String	newForm = list.getNewForm();

    final int		MAX_LINE_COUNT = 1024;
    final boolean	SKIP_FIRST_COLUMN = false;
    final boolean	SHOW_SINGLE_ENTRY = newForm != null;

    Object[][]		lines = new Object[columns.length - (SKIP_FIRST_COLUMN ? 1 : 0)][MAX_LINE_COUNT];
    int			lineCount = 0;

    try {
      for (;;) {
	try {
	  getForm().startProtected(Message.getMessage("searching_database"));

	  Query		query = new Query(getForm().getDBContext().getDefaultConnection());


	  query.open(queryText);
	  lineCount = 0;
	  while (query.next() && lineCount < MAX_LINE_COUNT - 1) {
	    if (query.isNull(1)) {
	      continue;
	    }

	    for (int i = 0; i < lines.length; i += 1) {
	      lines[i][lineCount] = query.getObject(i + (SKIP_FIRST_COLUMN ? 2 : 1));
	    }
	    lineCount += 1;
	  }
	  query.close();

	  getForm().commitProtected();
	  break;
	} catch (SQLException e) {
	  getForm().abortProtected(e);
	} catch (Error error) {
          getForm().abortProtected(error);
        } catch (RuntimeException rte) {
          getForm().abortProtected(rte);
	}
      }
    } catch (Throwable e) {
      throw new VRuntimeException(e);
    }

    if (lineCount == 0 && (newForm == null || !isNull(block.getActiveRecord()))) {
      throw new VFieldException(this, Message.getMessage("no_match"));
    } else {
      int     selected;

      if (lineCount == 0 && (newForm != null && isNull(block.getActiveRecord()))) {
	selected = ((VDictionaryForm)Module.getKopiExecutable(newForm)).newRecord(getForm());
      } else {
	if (lineCount == MAX_LINE_COUNT - 1) {
	  getForm().notice(Message.getMessage("too_many_rows"));
	}

	if (lineCount == 1 && ! SHOW_SINGLE_ENTRY) {
	  selected = 0;
	} else {
          final ListDialog	list;

	  list = new ListDialog(columns, lines, lineCount, newForm);
          selected = list.selectFromDialog(getForm(), getDisplay());
	}
      }

      if (selected == -1) {
	throw new VExecFailedException();	// no message needed
      } else if (selected >= lineCount) {
	// new, retrieve it
	Object result = null;

	try {
	  for (;;) {
	    try {
	      getForm().startProtected(null);
	      final String SELECT_IS_IN_LIST =
		" SELECT   $1			" +
		" FROM	   $2			" +
		" WHERE	   ID = " + selected;

	      Query	query = new Query(getForm().getDBContext().getDefaultConnection());

	      query.addString(list.getColumn(0).getColumn());
	      query.addString(evalListTable());
	      query.open(SELECT_IS_IN_LIST);
	      query.next();
	      result = query.getObject(1);
	      query.close();
	      getForm().commitProtected();
	      break;
	    } catch (SQLException e) {
	      getForm().abortProtected(e);
            } catch (Error error) {
              getForm().abortProtected(error);
            } catch (RuntimeException rte) {
              getForm().abortProtected(rte);
	    }
	  }
	} catch (Throwable e) {
	  throw new VRuntimeException(e);
	}
	return result;
      } else {
	return lines[0][selected];
      }
    }
  }

  /**
   * Checks that field value exists in list
   */
  protected void selectFromList(boolean gotoNextField) throws VException {
    StringBuffer	qrybuf = new StringBuffer();

    qrybuf.append("SELECT ");
    for (int i = 0; i < list.columnCount(); i++) {
      if (i != 0) {
	qrybuf.append(", ");
      }
      qrybuf.append(list.getColumn(i).getColumn());
    }

    qrybuf.append(" FROM ");
    qrybuf.append(evalListTable());

    if (getSearchType() == STY_MANY) {
      qrybuf.append(" WHERE ");
      switch (options & FDO_SEARCH_MASK) {
      case FDO_SEARCH_NONE:
	qrybuf.append(list.getColumn(0).getColumn());
	break;
      case FDO_SEARCH_UPPER:
	qrybuf.append("{fn UPPER(");
	qrybuf.append(list.getColumn(0).getColumn());
	qrybuf.append(")}");
	break;
      case FDO_SEARCH_LOWER:
	qrybuf.append("{fn LOWER(");
	qrybuf.append(list.getColumn(0).getColumn());
	qrybuf.append(")}");
	break;
      default:
	throw new InconsistencyException("FATAL ERROR: bad search code: " + options);
      }
      qrybuf.append(" ");
      qrybuf.append(getSearchCondition());
    }

    qrybuf.append(" ORDER BY 1");

    Object	result = displayQueryList(qrybuf.toString(), list.getColumns());

    if (result == null) {
      throw new VExecFailedException();	// no message to display
    } else {
      setObject(block.getActiveRecord(), result);
      if (gotoNextField) {
	block.gotoNextField();
      }
    }
  }

  /**
   * Checks that field value exists in list
   */
  protected void enumerateValue(boolean desc) throws VException {
    String	qrybuf;
    Object      value = null;

    qrybuf =
      " SELECT " + list.getColumn(0).getColumn() +
      " FROM " + evalListTable() +
      (isNull(block.getActiveRecord()) ? "" :
       " WHERE " + list.getColumn(0).getColumn() +
       (desc ? " > " : " < ") + (getSql(block.getActiveRecord()))) +
      " ORDER BY 1" + (desc ? "" : " DESC");

    for (;;) {
      try {
	getForm().startProtected(null);

	Query		query = new Query(getForm().getDBContext().getDefaultConnection());
	query.open(qrybuf);
	while (value == null && query.next()) {
	  value = query.getObject(1);
	}
	query.close();

	getForm().commitProtected();
	break;
      } catch (SQLException e) {
        try {
          getForm().abortProtected(e);
        } catch(SQLException abortEx) {
          throw new VExecFailedException(abortEx);
        }
      } catch (Error error) {
        try {
          getForm().abortProtected(error);
        } catch(Error abortEx) {
          throw new VExecFailedException(abortEx);
        }
      } catch (RuntimeException rte) {
        try {
          getForm().abortProtected(rte);
        } catch(RuntimeException abortEx) {
          throw new VExecFailedException(abortEx);
        }
      }
    }
    if (value == null) {
      throw new VExecFailedException();	// no message to display
    } else {
      setObject(block.getActiveRecord(), value);
    }
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------

  /**
   * Returns the list table.
   */
  private String evalListTable() {
    try {
      return (String)getBlock().executeObjectTrigger(list.getTable());
    } catch (VException e) {
      throw new InconsistencyException();
    }
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  public Object callTrigger(int event) throws VException {
    Object      res;

    //getBlock().setCurrentRecord(block.getActiveRecord());
    res = block.callTrigger(event, index + 1);
    return res;
  }

  /**
   * return if there is trigger associated with event
   */
  private boolean hasTrigger(int event) {
    return getBlock().hasTrigger(event, index + 1);
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  private Object callSafeTrigger(int event) {
    try {
      return callTrigger(event);
    } catch (VException ve) {
      throw new VRuntimeException(ve);
    }
  }

  // ----------------------------------------------------------------------
  // F2
  // ----------------------------------------------------------------------

  /**
   * // TRY TO MERGE WITH queryList !!!!
   * !!!graf 030729: was ist das ???
   */
  public void setValueID(int id) {
    Object result = null;
    try {
      for (;;) {
	try {
	  getForm().startProtected(null);
	  Query	query = new Query(getForm().getDBContext().getDefaultConnection());
	  query.addString(list.getColumn(0).getColumn());
	  query.addString(evalListTable());
	  query.addInt(id);
	  query.open("SELECT $1 FROM $2 WHERE ID = #3");
	  if (query.next()) {
	    result = query.getObject(1);
	  } else {
	    result = null;
	  }
	  query.close();
	  getForm().commitProtected();
	  break;
	} catch (SQLException e) {
	  getForm().abortProtected(e);
	} catch (Error error) {
          getForm().abortProtected(error);
        } catch (RuntimeException rte) {
          getForm().abortProtected(rte);
        }
      }
    } catch (Throwable e) {
      throw new VRuntimeException(e);
    }
    setObject(block.getActiveRecord(), result);
    changed = true; // if you edit the value it's like if you change it
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  public void helpOnField(VHelpGenerator help) {
    String	label = getLabel();
    if (label != null) {
      label = label.lastIndexOf(":") == -1 ? label.replace(' ', '_') : label.substring(0, label.lastIndexOf(":")).replace(' ', '_');

//       VFieldUI ui = getUI() == null ?
// 	new VFieldUI(this, cmd, pos, align, list) :
// 	getUI();

      help.helpOnField(getBlock().getTitle(),
		       getBlock().getFieldPos(this),
		       getLabel(),
		       label == null ? getName() : label,
		       getToolTip());
      if (access[MOD_UPDATE] != ACS_SKIPPED
          || access[MOD_INSERT] != ACS_SKIPPED
          || access[MOD_QUERY] != ACS_SKIPPED)
        {
          helpOnType(help);
          help.helpOnFieldCommand(cmd);
        }
    }
  }

  /**
   * return the name of this field
   */
  public abstract String getTypeInformation();

  /**
   * return the name of this field
   */
  public abstract String getTypeName();

  /**
   *
   */
  protected void helpOnType(VHelpGenerator help) {
    helpOnType(help, null);
  }

  /**
   *
   */
  protected void helpOnType(VHelpGenerator help, String[] names) {
    String modeName = null;
    String modeDesc = null;
    if (access[MOD_UPDATE] == ACS_MUSTFILL
        || access[MOD_INSERT] == ACS_MUSTFILL
        || access[MOD_QUERY] == ACS_MUSTFILL) {
      modeName = Message.getMessage("mustfill");
      modeDesc = Message.getMessage("mustfill-long");
    } else if (access[MOD_UPDATE] == ACS_MUSTFILL
               || access[MOD_INSERT] == ACS_VISIT
               || access[MOD_QUERY] == ACS_VISIT) {
      modeName = Message.getMessage("visit");
      modeDesc = Message.getMessage("visit-long");
    } else if (access[MOD_UPDATE] == ACS_MUSTFILL
               || access[MOD_INSERT] == ACS_SKIPPED
               || access[MOD_QUERY] == ACS_SKIPPED) {
      modeName = Message.getMessage("skipped");
      modeDesc = Message.getMessage("skipped-long");
    } else {
      modeName = Message.getMessage("skipped");
      modeDesc = Message.getMessage("skipped-long");
    }

    help.helpOnType(modeName,
		    modeDesc,
		    getTypeName(),
		    getTypeInformation(),
		    names);
  }

  /**
   * prepare a snapshot
   *
   * @param	fieldPos	position of this field within block visible fields
   */
  public void prepareSnapshot(int fieldPos, boolean active) {
    // !!! TO DO

//     if (getUI() != null) {
//       getUI().prepareSnapshot(fieldPos, active);
//     }
  }

  public String toString() {
    StringBuffer        information = new StringBuffer();

    try {
      information.append("\nFIELD ");
      information.append(name);
      information.append(" label: ");
      information.append(label);
      information.append("\n");

      try {
        Object  value = getObject(block.getActiveRecord());

        if (value == null) {
          information.append("    value: null");
        } else {
          information.append("    value: \"");
          information.append(value);
          information.append("\"");
        }
      } catch (Exception e) {
        information.append("value information exception ");
      }
      information.append("\n");

      try {
        information.append("    type name: ");
        information.append(getTypeName());
      } catch (Exception e) {
        information.append("type information exception");
      }
      information.append("\n");

      information.append("    changed: ");
      information.append(changed);
      information.append("\n");
    } catch (Exception e) {
      information.append("exception while retrieving field information\n");
    }

    return information.toString();
  }

  // ----------------------------------------------------------------------
  // LISTENER
  // ----------------------------------------------------------------------

  public void addFieldListener(FieldListener fl) {
    if (!hasListener) {
      hasListener = true;
      if (fieldListener == null) {
        fieldListener = new EventListenerList();
      }
    }

    fieldListener.add(FieldListener.class, fl);
  }
  public void removeFieldListener(FieldListener fl) {
    fieldListener.remove(FieldListener.class, fl);

    if (fieldListener.getListenerCount() == 0) {
      hasListener = false;
    }
  }
  public void addFieldChangeListener(FieldChangeListener fl) {
    if (!hasListener) {
      hasListener = true;
      if (fieldListener == null) {
        fieldListener = new EventListenerList();
      }
    }

    fieldListener.add(FieldChangeListener.class, fl);
  }
  public void removeFieldChangeListener(FieldChangeListener fl) {
    fieldListener.remove(FieldChangeListener.class, fl);

    if (fieldListener.getListenerCount() == 0) {
      hasListener = false;
    }
  }

  public void fireValueChanged(int r) {
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldChangeListener.class) {
          ((FieldChangeListener)listeners[i+1]).valueChanged(r);
        }
      }
    }
  }
  public void fireSearchOperatorChanged() {
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldChangeListener.class) {
          ((FieldChangeListener)listeners[i+1]).searchOperatorChanged();
        }
      }
    }
  }
  public void fireLabelChanged() {
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldChangeListener.class) {
          ((FieldChangeListener)listeners[i+1]).labelChanged();
        }
      }
    }
  }
  public void fireAccessChanged(int r) {
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldChangeListener.class) {
          ((FieldChangeListener)listeners[i+1]).accessChanged(r);
        }
      }
    }
  }
  // NOT NECESSARY
//   public void fireRecordChanged() {
//     if (hasListener) {
//       Iterator    iterator = fieldListener.listIterator();

//       while (iterator.hasNext()) {
//         ((FieldListener) iterator.next()).recordChanged();
//       }
//     }
//   }

  // removed not useful code:
//   public void fireInfoChanged() {
//     if (hasListener) {
//       Iterator    iterator = fieldListener.listIterator();

//       while (iterator.hasNext()) {
//         ((FieldListener) iterator.next()).infoChanged();
//       }
//     }
//   }
  public void fireEntered(){
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldListener.class) {
          ((FieldListener)listeners[i+1]).enter();
        }
      }
    }
  }
  public void fireLeaved(){
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldListener.class) {
          ((FieldListener)listeners[i+1]).leave();
        }
      }
    }
  }

  public void requestFocus() throws VException {
    if (hasListener) {
      boolean           consumed = false;
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0 && !consumed; i-=2) {
        if (listeners[i]==FieldListener.class) {
          consumed = ((FieldListener)listeners[i+1]).requestFocus();
        }
      }
    }
  }

  public Object getDisplayedValue(boolean trim) throws VException {
    Object              value = null;

    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0 && value == null; i-=2) {
        if (listeners[i]==FieldListener.class) {
          value = ((FieldListener)listeners[i+1]).getDisplayedValue(trim);
        }
      }
    }

    return value;
  }

  public void loadItem(int item) throws VException {
    if (hasListener) {
      boolean           loaded = false;
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0 && !loaded; i-=2) {
        if (listeners[i]==FieldListener.class) {
          loaded = ((FieldListener)listeners[i+1]).loadItem(item);
        }
      }
    }
  }

  public void modelNeedUpdate() throws VException {
    if (hasListener) {
      Object[]          listeners = fieldListener.getListenerList();

      for (int i = listeners.length-2; i>=0; i-=2) {
        if (listeners[i]==FieldListener.class) {
          ((FieldListener)listeners[i+1]).updateModel();
        }
      }
    }
  }

  // ----------------------------------------------------------------------
  // !!! Remove after merging the new MVC
  // ----------------------------------------------------------------------

  /**
   * @deprecated
   */
  public class Compatible {
    public Object getDisplayedValue(boolean trim) throws VException {
      return VField.this.getDisplayedValue(trim);
    }
  }

  /**
   * @deprecated
   */
  public Compatible getUI() {
    return new Compatible();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // static (compiled) data
  protected	int		width;		// max # of chars per line
  protected	int		height;		// max # of lines

  private	int[]		access;		// access in each mode
  private	int		priority;	// order in select results
  private	int		indices;	// bitset of unique indices
  private	String		name;		// field name (for dumps)
  private	String		label;		// field label
  private	int		options;	// options
  private	String		help;		// help text
  private	int		index;		// The position in parent field array
  private	int		align;		// field alignment
  private       int		posInArray;	// position in array of fields

  protected	VList		list;		// list
  protected	VBlock		block;		// containing block
  private	VColumn[]	columns;	// columns in block's tables
  //  private	VFieldUI	ui;		// The UI manager
  private	VField		alias;		// The alias field

  // changed?
  private	boolean		changed;	// changed by user / changes are done in the model
  private	boolean		changedUI;	// changed by user / changes are in the ui -> update model
                                                // UPDATE model before doing anything
  private       int             border;

  // dynamic data
  private	int		searchOperator;	// search operator
  private	int[]		dynAccess;	// dynamic access

  // ####
  private       EventListenerList       fieldListener;
  // if there is only the model and no gui
  // all the job use less memory and are faster
  private       boolean         hasListener;

  private	VPosition	pos;
  private	VCommand[]	cmd;
//   private	VCommand	incrementCommand;
//   private	VCommand	decrementCommand;
//   private	VCommand	autofillCommand;


  public static final int              MDL_FLD_COLOR = 1;
  public static final int              MDL_FLD_IMAGE = 2;
  public static final int              MDL_FLD_EDITOR = 3;
  public static final int              MDL_FLD_TEXT = 4;
}
