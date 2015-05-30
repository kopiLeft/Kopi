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

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.FieldLocalizer;
import com.kopiright.vkopi.lib.l10n.TypeLocalizer;
import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.VlibProperties;

@SuppressWarnings("serial")
public abstract class VCodeField extends VField {

  /**
   * Constructor
   *
   * @param     type            the identifier of the type in the source file
   * @param     source          the qualified name of the source file defining the list
   * @param     idents          an array of identifiers identifying each code value
   */
  protected VCodeField(String type, String source, String[] idents) {
    super(1, 1);
    this.type = type;
    this.source = source;
    this.idents = idents;
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
    return VlibProperties.getString("code-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString("Code");
  }

  /**
   *
   */
  protected void helpOnType(VHelpGenerator help) {
    super.helpOnType(help, (this instanceof VBooleanField) ? null : labels);
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

    for (int i = 0; i < labels.length; i++) {
      if (labels[i].toLowerCase().startsWith(s)) {
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

      for (int i = 0; found != -2 && i < labels.length; i++) {
	if (labels[i].toLowerCase().startsWith(s)) {
	  if (labels[i].toLowerCase().equals(s)) {
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
	throw new VFieldException(this, MessageCode.getMessage("VIS-00001"));
      case -2:	/* two (or more) exact matches: cannot choose */
        final VListDialog	listDialog;
        final int            	selected;
        int count;
        int[] selectedToModel;
        Object[] codes;

        count = 0;
        for (int i = 0; i < labels.length; i++) {
          if (labels[i].toLowerCase().startsWith(s)) {
            count ++;
          }
        }
        codes = new Object[count];
        selectedToModel = new int[count];
        int j = 0;

        for (int i = 0; i < labels.length ; i++) {
          if (labels[i].toLowerCase().startsWith(s)) {
            codes[j] = getCodes()[i];
            selectedToModel[j] = i;
            j++;
          }
        }
        listDialog = new VListDialog(new VListColumn[] {getListColumn()},
                                    new Object[][]{ codes });
        selected = listDialog.selectFromDialog(getForm(), null, this);
        if (selected != -1) {
          setCode(block.getActiveRecord(), selectedToModel[selected]);
        } else {
          throw new VFieldException(this, MessageCode.getMessage("VIS-00002"));
        }
        break;
      default:
	setCode(block.getActiveRecord(), found);
      }
    }
  }

  public boolean fillField(PredefinedValueHandler handler)
    throws VException
  {
    if (handler != null) {
      String    selected;

      selected = handler.selectFromList(new VListColumn[] { getListColumn() },
                                        new Object[][]{ getCodes() },
                                        labels);

      if (selected != null) {
        /*
         * -1:  no match
         * >=0: one match
         * -2:  two (or more) matches: cannot choose
         */
        int     found = -1;

        for (int i = 0; found != -2 && i < labels.length; i++) {
	  if (labels[i].equals(selected)) {
            if (found == -1) {
              found = i;
            } else {
              found = -2;
            }
          }
        }

        assert found >= 0;
	setCode(block.getActiveRecord(), found);
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
      pos = labels.length;
    }
    pos += desc ? -1 : 1;

    if (pos < 0 || pos >= labels.length) {
      throw new VExecFailedException();	// no message to display
    } else {
      setCode(getBlock().getActiveRecord(), pos);
    }
  }

  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * return the soource file of this code field.
   */
  public String getSource() {
    return source;
  }


  /**
   * return the name of this field
   */
  public String[] getLabels() {
    return labels;
  }

  /**
   * Returns the array of codes.
   */
  public abstract Object[] getCodes();

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
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == -1;
  }

  /**
   * Returns the field value of given record as a bigdecimal value.
   */
  @SuppressWarnings("deprecation")
  public com.kopiright.xkopi.lib.type.Fixed getFixed(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a boolean value.
   */
  @SuppressWarnings("deprecation")
  public Boolean getBoolean(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a int value.
   */
  @SuppressWarnings("deprecation")
  public Integer getInt(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the field value of given record as a string value.
   */
  @SuppressWarnings("deprecation")
  public String getString(int r) {
    throw new InconsistencyException();
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    return value[r] == -1 ? "" : labels[value[r]];
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
    return labels[code];
  }

  /**
   * Returns a string representation of a bigdecimal value wrt the field type.
   */
  @SuppressWarnings("deprecation")
  protected String formatFixed(com.kopiright.xkopi.lib.type.Fixed value) {
    throw new InconsistencyException();
  }

  /**
   * Returns a string representation of a boolean value wrt the field type.
   */
  @SuppressWarnings("deprecation")
  protected String formatBoolean(boolean value) {
    throw new InconsistencyException();
  }

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  @SuppressWarnings("deprecation")
  protected String formatInt(int value) {
    throw new InconsistencyException();
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  protected void localize(FieldLocalizer parent) {
    TypeLocalizer       loc;

    loc = parent.getManager().getTypeLocalizer(source, type);
    labels = new String[idents.length];
    for (int i = 0; i < labels.length; i++) {
      labels[i] = loc.getCodeLabel(idents[i]);
    }
    setDimension(getMaxWidth(labels), 1);
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private static int getMaxWidth(String[] labels) {
    int		res = 0;

    for (int i = 0; i < labels.length; i++) {
      res = Math.max(labels[i].length(), res);
    }

    return res;
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private final String          type;
  private final String          source;

  // static (compiled) data
  private final String[]	idents;
  private String[]		labels;

  // dynamic data
  protected int[]		value;		// -1: null
}
