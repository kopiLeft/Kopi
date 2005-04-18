/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.form;

import java.sql.SQLException;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.visual.VExecFailedException;
import at.dms.xkopi.lib.base.Query;

public class VStringField extends VField {

  /**
   * Constructor
   */
  public VStringField(int width, int height, int convert) {
    this(width, height, 0, convert);
  }

  /**
   * Constructor
   */
  public VStringField(int width, int height, int visibleHeight, int convert) {
    super(width, height);
    this.visibleHeight = visibleHeight;
    this.convert = convert;
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
      return Message.getMessage("string-type-area", new Object[]{ new Integer(width), new Integer(height)});
    } else {
      return Message.getMessage("string-type-field", new Object[]{ new Integer(width) });
    }
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage(getHeight() == 1 ? "String" : "StringArea");
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

    end = textToModel(s).length();
    if (end > width * height) {
      return false;
    }

    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may be raised if text is bad
   */
  public void checkType(Object o) throws VException {
    String s = (String)o;

    if (s == null || s.equals("")) {
      setNull(block.getActiveRecord());
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
	s = s.toLowerCase();	// !!! add function
	break;

      default:
	throw new InconsistencyException();
      }

      if (! checkText(s)) {
	throw new VExecFailedException();
      }

      setString(block.getActiveRecord(), s);
    }
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

    if (isChangedUI() 
        || (value[r] == null && modelVal != null)
        || (value[r] != null && !value[r].equals(modelVal))) {
      // trails (backup) the record if necessary
      trail(r);
      // set value in the defined row
      value[r] = modelVal;
      // inform that value has changed
      setChanged(r);
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
    return at.dms.xkopi.lib.base.KopiUtils.toSql(value[r]);
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    value[t] = value[f];
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
   * @param	col	the width of the text
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
    StringBuffer	target = new StringBuffer();
    int		length = source.length();
    int		start = 0;
    int		lines = 0;

    while (start < length && lines < lin) {
      int	index = source.indexOf('\n', start);
      if (index == -1) {
	target.append(source.substring(start, length));
	start = length;
      } else {
	target.append(source.substring(start, index));
	for (int i = index - start; i < col; i++) {
	  target.append(' ');
	}
	start = index + 1;
	lines++;
      }
    }

    return target.toString();
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
    if (source != null) {
      StringBuffer      target = new StringBuffer();
      int               length = source.length();

      for (int start = 0; start < length; start += col) {
        String  line = source.substring(start, Math.min(start + col, length));
        int     last = -1;

        for (int i = line.length() - 1; last == -1 && i >= 0; --i) {
          if (! Character.isWhitespace(line.charAt(i))) {
            last = i;
          }
        }

        if (start != 0) {
          target.append('\n');
        }
        if (last != -1) {
          target.append(line.substring(0, last + 1));
        }
      }

      return target.toString();
    } else {
      return "";
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final int			convert;
  private final int			visibleHeight;

  protected String[]			value;
}
