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

import org.kopi.vkopi.lib.list.VIntegerColumn;
import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.base.Query;

/**
 * !!! NEED COMMENTS
 */
@SuppressWarnings("serial")
public class VIntegerField extends VField {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VIntegerField(int width, int minval, int maxval) {
    super(width, 1);

    this.minval = minval;
    this.maxval = maxval;
    this.criticalMinValue = new Integer(minval);
    this.criticalMaxValue = new Integer(maxval);
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new Integer[2 * block.getBufferSize()];
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    int	min = minval;
    int max = maxval;
    int	nines = 1;

    for (int i = width; i > 0; i--) {
      nines *= 10;
    }

    max = Math.min(max, nines - 1);
    min = Math.max(min, - (nines / 10 - 1));

    return VlibProperties.getString("integer-type-field",
                              new Object[]{ new Integer(min),
                                            new Integer(max) });
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString("Long");
  }
  
  /**
   * Returns the min permitted value.
   * @return The min permitted value.
   */
  public int getMinValue() {
    return minval;
  }
  
  /**
   * Returns the max permitted value.
   * @return The max permitted value.
   */
  public int getMaxValue() {
    return maxval;
  }

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VIntegerColumn(getHeader(),
                              null,
                              getAlign(),
                              getWidth(),
                              getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    if (s.length() > width) {
      return false;
    }

    for (int i = 0; i < s.length(); i++) {
      char	c = s.charAt(i);
      if (! (Character.isDigit(c) || c == '.' || c == '-')) {
	return false;
      }
    }

    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception may be raised if text is bad
   */
  public void checkType(int rec, Object o) throws VException {
    String s = (String)o;

    if (s.equals("")) {
      setNull(rec);
    } else {
      int	v;

      try {
	v = Integer.parseInt(s);
      } catch (NumberFormatException e) {
	throw new VFieldException(this, MessageCode.getMessage("VIS-00006"));
      }

      if (v < minval) {
	throw new VFieldException(this, MessageCode.getMessage("VIS-00012", new Object[]{ new Integer(minval) }));
      }
      if (v > maxval) {
	throw new VFieldException(this, MessageCode.getMessage("VIS-00009", new Object[]{ new Integer(maxval) }));
      }

      setInt(rec, new Integer(v));
    }
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setInt(r, null);
  }

  /**
   * Sets the field value of given record to a int value.
   */
  public void setInt(int r, Integer v) {
    if (isChangedUI()
        || (value[r] == null && v != null)
        || (value[r] != null && !value[r].equals(v))) {
      // trails (backup) the record if necessary
      trail(r);

      if (v == null) {
        value[r] = null;
      } else {
        if (v.intValue() < minval) {
          // !!! int.underflow "Warnung: Wert außerhalb des erlaubten Bereichs."
          v = new Integer(minval);
        } else if (v.intValue() > maxval) {
          // !!! int overflow "Warnung: Wert außerhalb des erlaubten Bereichs."
          v = new Integer(maxval);
        }
        value[r] = v;
      }
      // inform that value has changed
      setChanged(r);
    }
    checkCriticalValue();
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
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a int value.
   */
  public Integer getInt(int r) {
    return (Integer) getObject(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return  value[r];
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    return value[r] == null ? "" : value[r].toString();
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
    Integer             oldValue;
    
    oldValue = value[t];
    value[t] = value[f];
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < getBlock().getBufferSize()
        && ((oldValue != null && value[t] == null)
            || (oldValue == null && value[t] != null)
            || (oldValue != null && !oldValue.equals(value[t]))))
    {
      setChanged(t);
    }
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
  public Integer computeSum(boolean exclude) {
    Integer       sum = null;

    for (int i = 0; i < getBlock().getBufferSize(); i++) {
      if (!isNullImpl(i)
          && getBlock().isRecordFilled(i)
          && (!exclude || i != getBlock().getActiveRecord())) {
        if (sum == null) {
          sum = new Integer(0);
        }
        sum = new Integer(sum.intValue() + getInt(i).intValue());
      }
    }
    return sum;
  }

  /**
   * Returns the sum of the field values of all records.
   *
   * @param     exclude         exclude the current record
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  public int computeSum(boolean exclude, int coalesceValue) {
    Integer     sum;

    sum = computeSum(exclude);
    return sum == null ? coalesceValue : sum.intValue();
  }

  /**
   * Returns the sum of the field values of all records.
   *
   * @return    the sum of the field values, null if none is filled.
   */
  public Integer computeSum() {
    return computeSum(false);
  }

  /**
   * Returns the sum of every filled records in block
   */
  public int getCoalesceSum(int coalesceValue) {
    Integer     sum;

    sum = computeSum();
    return sum == null ? coalesceValue : sum.intValue();
  }

  /**
   * Returns the sum of every filled records in block
   *
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  public int computeSum(int coalesceValue) {
    return computeSum(false, coalesceValue);
  }

  /**
   * Returns the sum of every filled records in block
   * @deprecated        use int getCoalesceSum(int) instead
   */
  public int getSum() {
    Integer     sum;

    sum = computeSum();
    return sum == null ? 0 : sum.intValue();
  }

  //----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  //----------------------------------------------------------------------

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  protected String formatInt(int value) {
    return Integer.toString(value);
  }

  public void setCriticalMinValue(int criticalMinValue) {
    this.criticalMinValue = new Integer(criticalMinValue);
  }

  public void setCriticalMaxValue(int criticalMaxValue) {
    this.criticalMaxValue = new Integer(criticalMaxValue);
  }

  public void setCriticalMinValue(Integer criticalMinValue) {
    this.criticalMinValue = criticalMinValue;
  }

  public void setCriticalMaxValue(Integer criticalMaxValue) {
    this.criticalMaxValue = criticalMaxValue;
  }

  public void checkCriticalValue() {
    if (value[0] != null && criticalMinValue != null) {
      if (value[0].compareTo(criticalMinValue) < 0) {
        setHasCriticalValue(true);
        return;
      }
    }

    if (value[0] != null && criticalMaxValue != null) {
      if (value[0].compareTo(criticalMaxValue) > 0) {
        setHasCriticalValue(true);
        return;
      }
    }
    setHasCriticalValue(false);
  }

  private void setHasCriticalValue(boolean critical) {
    if (getDisplay() != null) {
      ((UTextField)getDisplay()).setHasCriticalValue(critical);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // static (compiled) data
  private int			minval;		// minimum value allowed
  private int			maxval;		// maximum value allowed

  // dynamic data
  private Integer[]		value;		// value
  private Integer		criticalMinValue;
  private Integer		criticalMaxValue;
}

