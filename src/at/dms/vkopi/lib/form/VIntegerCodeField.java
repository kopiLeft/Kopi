/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: VIntegerCodeField.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import java.sql.SQLException;

import at.dms.util.base.InconsistencyException;
import at.dms.xkopi.lib.base.Query;

public class VIntegerCodeField extends VCodeField {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR / BUILD
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VIntegerCodeField(String[] names, Integer[] codes) {
    super(names);
    this.codes = codes;
  }

  /**
   * Constructor
   */
  public VIntegerCodeField(String[] names, int[] codes) {
    super(names);
    this.codes = new Integer[codes.length];
    for (int i = 0; i < codes.length; i++) {
      this.codes[i] = new Integer(codes[i]);
    }
  }

  // ----------------------------------------------------------------------
  // INTERFACE DISPLAY
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VIntegerCodeColumn(getHeader(), null, names, codes, getPriority() >= 0);
  }

  /**
   * Returns the array of codes.
   */
  protected Object[] getCodes() {
    return codes;
  }

  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a int value.
   */
  public void setInt(int r, Integer v) {
    if (v == null) {
      setCode(r, -1);
    } else {
      int	code = -1;	// cannot be null

      for (int i = 0; code == -1 && i < codes.length; i++) {
	if (v.intValue() == codes[i].intValue()) {
	  code = i;
	}
      }
      if (code == -1) {
	throw new InconsistencyException("bad code value " + v);
      }

      setCode(r, code);
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setObject(int r, Object v) {
    setInt(r, (Integer)v);
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public Object retrieveQuery(Query query, int column)
    throws SQLException
  {
    if (query.isNull(column)) {
      return null;
    } else {
      return new Integer(query.getInt(column));
    }
  }

  /**
   * Returns the field value of given record as a int value.
   */
  public Integer getInt(int r) {
    return value[r] == -1 ? null : codes[value[r]];
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return getInt(r);
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return at.dms.xkopi.lib.base.KopiUtils.toSql(value[r] == -1 ? null : codes[value[r]]);
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  protected String formatInt(int value) {
    int	code = -1;	// cannot be null

    for (int i = 0; code == -1 && i < codes.length; i++) {
      if (value == codes[i].intValue()) {
	code = i;
      }
    }
    if (code == -1) {
      throw new InconsistencyException("bad code value " + value);
    }

    return formatCode(code);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // dynamic data
  private final Integer[]	codes;		// code array
}
