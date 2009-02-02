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

import java.sql.SQLException;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.list.VStringCodeColumn;
import com.kopiright.xkopi.lib.base.Query;

public class VStringCodeField extends VCodeField {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   *
   * @param     ident           the identifier of the type in the source file
   * @param     source          the qualified name of the source file defining the list
   */
  public VStringCodeField(String ident,
                          String source,
                          String[] names,
                          String[] codes)
  {
    super(ident, source, names);
    this.codes = codes;
  }

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VStringCodeColumn(getHeader(), null, getLabels(), codes, getPriority() >= 0);
  }

  /**
   * Returns the array of codes.
   */
  public Object[] getCodes() {
    return codes;
  }

  /*
   * ----------------------------------------------------------------------
   * Interface bd/Triggers
   * ----------------------------------------------------------------------
   */

  /**
   * Sets the field value of given record to a fixed value.
   */
  public void setString(int r, String v) {
    if (v == null) {
      setCode(r, -1);
    } else {
      int	code = -1;	// cannot be null

      for (int i = 0; code == -1 && i < codes.length; i++) {
	if (v.equals(codes[i])) {
	  code = i;
	}
      }
      if (code == -1) {
	throw new InconsistencyException("bad code value " + v
                                         + " for " + getType()
                                         + " in " + getSource());
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
   * Returns the field value of given record as a int value.
   */
  public String getString(int r) {
    return (String) getObject(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return value[r] == -1 ? null : codes[value[r]];
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return value[r] == -1 ?
      "NULL" :
      com.kopiright.xkopi.lib.base.KopiUtils.toSql(codes[value[r]]);
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  protected String formatString(String value) {
    int	code = -1;	// cannot be null

    for (int i = 0; code == -1 && i < codes.length; i++) {
      if (value == codes[i]) {
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

  private final String[]        codes;
}
