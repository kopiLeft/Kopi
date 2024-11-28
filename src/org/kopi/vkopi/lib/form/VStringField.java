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

package org.kopi.vkopi.lib.form;

import java.sql.SQLException;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.list.VStringColumn;
import org.kopi.vkopi.lib.util.LineBreaker;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.base.Query;

@SuppressWarnings("serial")
public class VStringField extends VField {

  /**
   * Constructor
   */
  public VStringField(int width, int height, int convert, boolean styled) {
    this(width, height, 0, convert, styled);
  }

  /**
   * Constructor
   */
  public VStringField(int width, int height, int visibleHeight, int convert, boolean styled) {
    super(width, height);
    this.visibleHeight = visibleHeight;
    this.convert = convert;
    this.styled = styled;
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new String[2 * block.getBufferSize()];
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    if (height > 1) {
      return VlibProperties.getString("string-type-area", new Object[]{ new Integer(width), new Integer(height)});
    } else {
      return VlibProperties.getString("string-type-field", new Object[]{ new Integer(width) });
    }
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString(getHeight() == 1 ? "String" : "StringArea");
  }

  /**
   * return the name of this field
   */
  public int getTypeOptions() {
    return convert;
  }

  /**
   * Return the visible height
   */
  public int getVisibleHeight() {
    return visibleHeight == 0 ? height : visibleHeight;
  }

  /**
   * Returns true if the text field supports styled content.
   */
  public boolean isStyled() {
    return styled;
  }

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VStringColumn(getHeader(), null, getAlign(), width, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    int		end   = 0;

    end = textToModel(s, getWidth(), Integer.MAX_VALUE).length();
    return end <= width * height;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception may be raised if text is bad
   */
  @SuppressWarnings("deprecation")
  public void checkType(int rec, Object o) throws VException {
    String s = (String)o;

    if (s == null || s.equals("")) {
      setNull(rec);
    } else {
      switch (convert & FDO_CONVERT_MASK) {
      case FDO_CONVERT_NONE:
	break;

      case FDO_CONVERT_UPPER:
	s = s.toUpperCase();
	break;

      case FDO_CONVERT_LOWER:
	s = s.toLowerCase();
	break;

      case FDO_CONVERT_NAME:
	s = convertName(s);
	break;

      default:
	throw new InconsistencyException();
      }
      if (! checkText(s)) {
	throw new VExecFailedException();
      }
      setString(rec, s);
    }
  }

  /**
   * Convert the first letter in each word in the source text into upper case.
   *
   * @param	source		the source text.
   */
  private String convertName(String source) {
    char[]      chars = source.toLowerCase().toCharArray();
    boolean     found = false;

    for (int i = 0; i < chars.length; i++) {
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i]);
        found = true;
      } else if (Character.isWhitespace(chars[i])) {
        found = false;
      }
    }
    return String.valueOf(chars);
  }

  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setString(r, null);
  }

  /**
   * Sets the field value of given record to a string value.
   */
  public void setString(int r, String v) {
    String      modelVal =  v == null || v.equals("") ? null : v;

    //wael  : 04092007 , bad data in database this exception may cause problems when launching a form.
//     if(!checkText(v == null ? "" : v)) {
//       throw new IllegalArgumentException(MessageCode.getMessage("VIS-00065", Integer.toString(width), Integer.toString(height)));
//     }
    if (isChangedUI()
        || (value[r] == null && modelVal != null)
        || (value[r] != null && !value[r].equals(modelVal))) {
      // trails (backup) the record if necessary
      trail(r);
      // set value in the defined row
      value[r] = modelVal;
      // inform that value has changed
      setChanged(r);
      System.out.println("setChanged(r) is Done");
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setObject(int r, Object v) {
    setString(r, (String)v);
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public Object retrieveQuery(Query query, int column)
    throws SQLException
  {
    return query.getString(column);
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a string value.
   */
  public String getString(int r) {
    return (String) getObject(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return value[r];
  }

  @Override
  public String toText(Object o) {
    // TODO Auto-generated method stub
    return o == null ? "" : (String)o;
  }

  @Override
  public Object toObject(String s) throws VException {
    return s.equals("") ? null : s;
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    return value[r] == null ? "" : value[r];
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return org.kopi.xkopi.lib.base.KopiUtils.toSql(value[r]);
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    String              oldValue;

    oldValue = value[t];
    value[t] = value[f];
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < getBlock().getBufferSize()
        && ((oldValue != null && value[t] == null)
            || (oldValue == null && value[t] != null)
            || (oldValue != null && !oldValue.equals(value[t]))))
    {
      fireValueChanged(t);
    }
  }

  /**
   * Returns the data type handled by this field.
   */
  public Class getDataType() {
    return String.class;
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of a string value wrt the field type.
   */
  protected String formatString(String value) {
    return value;
  }

  /**
   * Replaces new-lines by blanks
   *
   * @param	source	the source text with carriage return
   */
  public String textToModel(String source) {
    return textToModel(source, getWidth(), getHeight());
  }

  /**
   * Replaces new-lines by blanks
   *
   * @param	source	the source text with carriage return
   * @param	col	the width of the text
   */
  public static String textToModel(String source, int col) {
    return textToModel(source, col, Integer.MAX_VALUE);
  }

  /**
   * Replaces new-lines by blanks
   *
   * @param	source	the source text with carriage return
   * @param	col	the width of the text
   */
  public static String textToModel(String source, int col, int lin) {
    // !!! graf 20070307: check whether different handling is needed
    // depending on the value of FDO_DYNAMIC_NL (ie FIXED ON/OFF)
    return LineBreaker.textToModel(source, col, lin);
  }

  /**
   * Replaces blanks by new-lines
   *
   * @param	record		the index of the record
   */
  public String modelToText(int record) {
    return modelToText(getString(record), getWidth());
  }

  /**
   * Replaces blanks by new-lines
   *
   * @param	source		the source text with white space
   * @param	col		the width of the text area
   */
  public static String modelToText(String source, int col) {
    // !!! graf 20070307: check whether different handling is needed
    // depending on the value of FDO_DYNAMIC_NL (ie FIXED ON/OFF)
    return LineBreaker.modelToText(source, col);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final int			convert;
  private final int			visibleHeight;
  private final boolean                 styled;

  protected String[]			value;
}
