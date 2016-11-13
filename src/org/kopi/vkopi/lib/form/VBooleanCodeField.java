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
import org.kopi.vkopi.lib.list.VBooleanCodeColumn;
import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.xkopi.lib.base.Query;

@SuppressWarnings("serial")
public class VBooleanCodeField extends VCodeField {

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
  public VBooleanCodeField(String ident,
                           String source,
                           String[] names,
                           Boolean[] codes)
  {
    super(ident, source, names);
    this.codes = codes;
  }

  /**
   * Constructor
   *
   * @param     ident           the identifier of the type in the source file
   * @param     source          the qualified name of the source file defining the list
   */
  public VBooleanCodeField(String ident,
                           String source,
                           String[] names,
                           boolean[] codes)
  {
    super(ident, source, names);
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
    return new VBooleanCodeColumn(getHeader(), null, getLabels(), codes, getPriority() >= 0);
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
	throw new InconsistencyException("bad code value " + v + "field " + getName());
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
      throw new InconsistencyException("bad code value " + value + "field " + getName());
    }

    return formatCode(code);
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private final Boolean[]	codes;
}
