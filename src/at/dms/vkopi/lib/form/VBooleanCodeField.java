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
 * $Id: VBooleanCodeField.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import java.sql.SQLException;

import at.dms.util.base.InconsistencyException;
import at.dms.xkopi.lib.base.Query;

public class VBooleanCodeField extends VCodeField {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public VBooleanCodeField(String[] names, Boolean[] codes) {
    super(names);
    this.codes = codes;
  }

  /**
   * Constructor
   */
  public VBooleanCodeField(String[] names, boolean[] codes) {
    super(names);
    this.codes = new Boolean[codes.length];
    for (int i = 0; i < codes.length; i++) {
      this.codes[i] = new Boolean(codes[i]);
    }
  }

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a list column for list.
   */
  protected VListColumn getListColumn() {
    return new VBooleanCodeColumn(getHeader(), null, names, codes, getPriority() >= 0);
  }

  /**
   * Returns the array of codes.
   */
  protected Object[] getCodes() {
    return codes;
  }


  /*
   * ----------------------------------------------------------------------
   * Interface bd/Triggers
   * ----------------------------------------------------------------------
   */

  /**
   * Sets the field value of given record to a boolean value.
   */
  public void setBoolean(int r, Boolean v) {
    if (v == null) {
      setCode(r, -1);
    } else {
      int	code = -1;	// cannot be null

      for (int i = 0; code == -1 && i < codes.length; i++) {
	if (v.booleanValue() == codes[i].booleanValue()) {
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
    setBoolean(r, (Boolean)v);
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
      return query.getBoolean(column) ? Boolean.TRUE : Boolean.FALSE;
    }
  }

  /**
   * Returns the field value of given record as a boolean value.
   */
  public Boolean getBoolean(int r) {
    return value[r] == -1 ? null : codes[value[r]];
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return getBoolean(r);
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    if (value[r] == -1) {
      return "NULL";
    } else {
      return codes[value[r]].booleanValue() ? "{fn TRUE}" : "{fn FALSE}";
    }
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a boolean value wrt the field type.
   */
  protected String formatBoolean(boolean value) {
    int	code = -1;	// cannot be null

    for (int i = 0; code == -1 && i < codes.length; i++) {
      if (value == codes[i].booleanValue()) {
	code = i;
      }
    }
    if (code == -1) {
      throw new InconsistencyException("bad code value " + value);
    }

    return formatCode(code);
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  // dynamic data
  private final Boolean[]	codes;		// code array
}
