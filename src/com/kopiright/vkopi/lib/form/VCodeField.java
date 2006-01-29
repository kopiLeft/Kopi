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

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;

public abstract class VCodeField extends VField {

  /**
   * Constructor
   */
  protected VCodeField(String[] names) {
    super(getMaxWidth(names), 1);
    this.names = names;
  }

  /**
   *
   */
  public boolean hasAutofill() {
    return true;
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new int[2 * block.getBufferSize()];
    for (int i = 0; i < block.getBufferSize(); i++) {
      value[i] = -1;
    }
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return Message.getMessage("code-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage("Code");
  }

  /**
   *
   */
  protected void helpOnType(VHelpGenerator help) {
    super.helpOnType(help, (this instanceof VBooleanField) ? null : names);
  }

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    s = s.toLowerCase();

    for (int i = 0; i < names.length; i++) {
      if (names[i].toLowerCase().startsWith(s)) {
	return true;
      }
    }
    return false;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(Object o) throws VException {
    String s = (String)o;

    if (s.equals("")) {
      setNull(block.getActiveRecord());
    } else {
      /*
       * -1:  no match
       * >=0: one match
       * -2:  two (or more) matches: cannot choose
       */
      int	found = -1;

      s = s.toLowerCase();

      for (int i = 0; found != -2 && i < names.length; i++) {
	if (names[i].toLowerCase().startsWith(s)) {
	  if (names[i].toLowerCase().equals(s)) {
	    found = i;
	    break;
	  }
	  if (found == -1) {
	    found = i;
	  } else {
	    found = -2;
	  }
	}
      }

      switch (found) {
      case -1:	/* no match */
	throw new VFieldException(this, Message.getMessage("no_match"));

      case -2:	/* two (or more) exact matches: cannot choose */
	throw new VFieldException(this, Message.getMessage("multiple_choice"));

      default:
	setString(block.getActiveRecord(), names[found]);
      }
    }
  }

//   /**
//    * autofill (with a list of possible value if more than one)
//    * @exception com.kopiright.vkopi.lib.visual.VException an exception may occur
//    * in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) 
//     throws VException
//   {
//     final int		selected;
//     final ListDialog	list;
    
//     list = new ListDialog(new VListColumn[] { getListColumn() },
//                           new Object[][]{ getCodes() });
//     selected = list.selectFromDialog(getForm().getDisplay(), getDisplay());
    
//     if (selected != -1) {
//       setString(names[selected]);
//       if (gotoNextField) {
// 	block.gotoNextField();
//       }
//     }
//   }
  public boolean fillField(PredefinedValueHandler handler) 
    throws VException
  {    
    if (handler != null) {
      String    value;

      value = handler.selectFromList(new VListColumn[] { getListColumn() },
                                     new Object[][]{ getCodes() },
                                     names);

      if (value != null) {
        setString(block.getActiveRecord(), value);
        return true;
      }
    }
    return false;
  }

  /**
   * return true if this field implements "enumerateValue"
   */
  public boolean hasNextPreviousEntry() {
    return true;
  }

  /**
   * Checks that field value exists in list
   */
  protected void enumerateValue(boolean desc) throws VException {
    desc = !getListColumn().isSortAscending() ? desc : !desc;
    int	pos = value[getBlock().getActiveRecord()];

    if (pos == -1 && desc) {
      pos = names.length;
    }
    pos += desc ? -1 : 1;

    if (pos < 0 || pos >= names.length) {
      throw new VExecFailedException();	// no message to display
    } else {
      setCode(getBlock().getActiveRecord(), pos);
    }
  }

  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Returns the array of codes.
   */
  protected abstract Object[] getCodes();

  /*
   *
   */
  protected void setCode(int r, int v) {
    if (isChangedUI() || value[r] != v) {
      // trails (backup) the record if necessary
      trail(r);
      // set value in the defined row
      value[r] = v;
      // inform that value has changed
      setChanged(r);
    }
    // else nothing to do
  }

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setCode(r, -1);
  }

  /**
   * Sets the field value of given record to a boolean value.
   */
  public void setString(int r, String v) {
    if (v == null || v.equals("")) {
      setCode(r, -1);
    } else {
      int	code = -1;	// cannot be null

      for (int i = 0; code == -1 && i < names.length; i++) {
	if (v.equals(names[i])) {
	  code = i;
	}
      }
      if (code == -1) {
	throw new InconsistencyException("bad code string " + v);
      }

      setCode(r, code);
    }
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == -1;
  }

  /**
   * Returns the field value of given record as a bigdecimal value.
   */
  public com.kopiright.xkopi.lib.type.Fixed getFixed(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a boolean value.
   */
  public Boolean getBoolean(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a int value.
   */
  public Integer getInt(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a boolean value.
   */
  public String getString(int r) {
    return value[r] == -1 ? null : names[value[r]];
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    return value[r] == -1 ? "" : names[value[r]];
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public abstract String getSqlImpl(int r);

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    value[t] = value[f];
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a code value wrt the field type.
   */
  protected String formatCode(int code) {
    return names[code];
  }

  /**
   * Returns a string representation of a bigdecimal value wrt the field type.
   */
  protected String formatFixed(com.kopiright.xkopi.lib.type.Fixed value) {
    throw new InconsistencyException();
  }

  /**
   * Returns a string representation of a boolean value wrt the field type.
   */
  protected String formatBoolean(boolean value) {
    throw new InconsistencyException();
  }

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  protected String formatInt(int value) {
    throw new InconsistencyException();
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private static int getMaxWidth(String[] names) {
    int		res = 0;

    for (int i = 0; i < names.length; i++) {
      res = Math.max(names[i].length(), res);
    }

    return res;
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  // static (compiled) data
  protected String[]		names;

  // dynamic data
  protected int[]		value;		// -1: null
}
