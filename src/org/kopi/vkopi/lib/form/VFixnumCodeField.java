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
import org.kopi.vkopi.lib.list.VFixnumCodeColumn;
import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.xkopi.lib.base.Query;
import org.kopi.xkopi.lib.type.Fixed;
import org.kopi.xkopi.lib.type.NotNullFixed;

@SuppressWarnings("serial")
public class VFixnumCodeField extends VCodeField {

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
  public VFixnumCodeField(String ident,
                          String source,
                          String[] names,
                          Fixed[] codes)
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
    return new VFixnumCodeColumn(getHeader(), null, getLabels(), codes, getPriority() >= 0);
  }

  /**
   * Returns the array of codes.
   */
  public Object[] getCodes() {
    return codes;
  }

  // ----------------------------------------------------------------------
  // FIELD VALUE ACCESS
  // ----------------------------------------------------------------------

  /**
   * Returns the sum of the field values of all records.
   *
   * @param     exclude         exclude the current record
   * @return    the sum of the field values, null if none is filled.
   */
  public Fixed computeSum(boolean exclude) {
    Fixed       sum = null;

    for (int i = 0; i < getBlock().getBufferSize(); i++) {
      if (getBlock().isRecordFilled(i)
          && !isNull(i)
          && (!exclude || i != getBlock().getActiveRecord())) {
        if (sum == null) {
          sum = new NotNullFixed(0);
        }
        sum = sum.add((NotNullFixed)getFixed(i));
      }
    }
    return sum;
  }

  /**
   * Returns the sum of the field values of all records.
   *
   * @return    the sum of the field values, null if none is filled.
   */
  public Fixed computeSum() {
    return computeSum(false);
  }

  /**
   * Returns the sum of every filled records in block.
   * @deprecated
   */
  public NotNullFixed getSum() {
    Fixed       sum;

    sum = computeSum();
    return sum == null ? new NotNullFixed(0) : (NotNullFixed)sum;
  }

  /*
   * ----------------------------------------------------------------------
   * Interface bd/Triggers
   * ----------------------------------------------------------------------
   */

  /**
   * Sets the field value of given record to a fixed value.
   */
  public void setFixed(int r, Fixed v) {
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
    setFixed(r, (Fixed)v);
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
      return query.getFixed(column);
    }
  }

  /**
   * Returns the field value of given record as a int value.
   */
  public Fixed getFixed(int r) {
    return (Fixed) getObject(r);
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
    return value[r] == -1 ? "NULL" : codes[value[r]].toSql();
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a bigdecimal value wrt the field type.
   */
  protected String formatFixed(org.kopi.xkopi.lib.type.Fixed value) {
    int	code = -1;	// cannot be null

    for (int i = 0; code == -1 && i < codes.length; i++) {
      if (value.equals(codes[i])) {
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
  
  private Fixed[]               codes; // code array
}
