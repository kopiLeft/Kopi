/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
import com.kopiright.vkopi.lib.list.VFixnumColumn;
import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.NotNullFixed;


public class VFixnumField extends VField {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public VFixnumField(int digits,
                      int maxScale,
                      String minval,
                      String maxval,
                      boolean fraction)
  {
    this(digits,
         maxScale,
         fraction,
         minval == null ? null : new NotNullFixed(minval),
         maxval == null ? null : new NotNullFixed(maxval));
  }

  /**
   * Constructor
   */
  public VFixnumField(int digits,
                      int maxScale,
                      boolean fraction,
                      Fixed minval,
                      Fixed maxval)
  {
    super(computeWidth(digits, maxScale, minval, maxval), 1);

    this.maxScale = maxScale;
    this.minval = minval == null ? calculateUpperBound(digits, maxScale).negate() : minval.setScale(maxScale);
    this.maxval = maxval == null ? calculateUpperBound(digits, maxScale) : maxval.setScale(maxScale);
    this.digits = digits;
    this.fraction = fraction;
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    int         size = 2 * block.getBufferSize();

    super.build();
    value = new Fixed[size];
    currentScale = new int[size];
    for (int i = 0; i < size; i++) {
      currentScale[i] = maxScale;
    }
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    Fixed       min = this.minval;
    Fixed       max = this.maxval;
    long        nines = 1;

    if (min == null) {
      min = new NotNullFixed(Integer.MIN_VALUE);
    }
    if (max == null) {
      max = new NotNullFixed(Integer.MAX_VALUE);
    }

    for (int i = width; i > 1; i--) {
      // comma
      if (i % 3 != 0) {
        nines *= 10;
      }
    }

    com.kopiright.xkopi.lib.type.Fixed big = new NotNullFixed(nines - 1);
    big = big.setScale(height);
    com.kopiright.xkopi.lib.type.Fixed mbig = new NotNullFixed(-(nines / 10 - 1));
    mbig = mbig.setScale(height);

    max = max.compareTo(big) > 0 ? max : big;
    min = min.compareTo(mbig) < 0 ? min : mbig;
    return VlibProperties.getString("fixed-type-field", new Object[] { min, max });
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString("Fixed");
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
    return new VFixnumColumn(getHeader(),
                             null,
                             getAlign(),
                             getWidth(),
                             maxScale,
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
      if (! ((s.charAt(i) >= '0' && s.charAt(i) <= '9')
             || s.charAt(i) == '.' || s.charAt(i) == '-' || s.charAt(i) == ' '
             || s.charAt(i) == ',' || s.charAt(i) == '/')) {
        return false;
      }
    }
    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception         com.kopiright.vkopi.lib.visual.VException       an exception may be raised if text is bad
   */
  public void checkType(Object o) throws VException {
    String      s = (String)o;
    int         scale = currentScale[block.getActiveRecord()];

    if (s.equals("")) {
      setNull(block.getActiveRecord());
    } else {
      Fixed   v;

      try {
        v = scanFixed(s);
      } catch (NumberFormatException e) {
        throw new VFieldException(this, MessageCode.getMessage("VIS-00006"));
      }

      if (v!= null) {
        if (v.getScale() > scale) {
          throw new VFieldException(this, MessageCode.getMessage("VIS-00011", new Object[]{ new Integer(scale) }));
        }
        if (minval != null && v.compareTo(minval) == -1) {
          throw new VFieldException(this, MessageCode.getMessage("VIS-00012", new Object[]{ minval }));
        }
        if (maxval != null && v.compareTo(maxval) == 1) {
          throw new VFieldException(this, MessageCode.getMessage("VIS-00009", new Object[]{ maxval }));
        }
        if (toText(v.setScale(maxScale)).length() > getWidth()) {
          throw new VFieldException(this, MessageCode.getMessage("VIS-00010"));
        }
      }

      setFixed(block.getActiveRecord(), v);
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
  public Fixed computeSum(boolean exclude) {
    Fixed       sum = null;

    for (int i = 0; i < getBlock().getBufferSize(); i++) {
      if (!isNullImpl(i)
          && getBlock().isRecordFilled(i)
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
   * @param     exclude         exclude the current record
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  public NotNullFixed computeSum(boolean exclude, NotNullFixed coalesceValue) {
    Fixed       sum;

    sum = computeSum(exclude);
    return sum == null ? coalesceValue : (NotNullFixed)sum;
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
   * Returns the sum of every filled records in block
   *
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  public NotNullFixed computeSum(NotNullFixed coalesceValue) {
    return computeSum(false, coalesceValue);
  }

  /**
   * Returns the current scale for the specified record.
   *
   * @param     record          the record value.
   * @return    the scale value.
   */
  public int getScale(int record) {
    return currentScale[record];
  }

  /**
   * Returns the current scale for the current record.
   *
   * @return    the scale value.
   */
  public int getScale() {
    return getScale(block.getActiveRecord());
  }

  /**
   * Sets the scale value for the specified record.
   *
   * @param     scale           the scale value.
   * @param     record          the record value.
   */  
  public void setScale(int record, int scale) throws VExecFailedException {
    if (scale > maxScale) {
      throw new InconsistencyException(MessageCode.getMessage("VIS-00060", String.valueOf(scale), String.valueOf(maxScale)));
    }

    currentScale[record] = scale;
  }

  /**
   * Sets the scale value for the current record.
   *
   * @param     scale           the scale value.
   */
  public void setScale(int scale) throws VExecFailedException {
    setScale(block.getCurrentRecord(), scale);
  }

  /**
   * Sets the maxScale value for the current record.
   *
   * @param     scale           the scale value.
   */
  public void setMaxScale(int scale) throws VExecFailedException {
    if (scale > maxScale) {
      throw new InconsistencyException(MessageCode.getMessage("VIS-00060", String.valueOf(scale), String.valueOf(maxScale)));
    }
    this.maxScale = scale;
    this.minval = minval.setScale(maxScale);
    this.maxval = maxval.setScale(maxScale);
    setDimension(computeWidth(digits, maxScale, minval, maxval), getHeight());
    //records scale must be <= maxscale
    for (int i = 0; i < currentScale.length; i++) {
      if(currentScale[i] > maxScale) {
        currentScale[i] = maxScale;
      }
    }
  }

  /**
   * Clears the field.
   *
   * @param     r       the recorde number.
   */
  public void clear(int r) {
    super.clear(r);
    
    for (int i = 0; i < currentScale.length; i++) {
      currentScale[i] = maxScale;
    }
  }

  /*
   * ----------------------------------------------------------------------
   * Interface bd/Triggers
   * ----------------------------------------------------------------------
   */

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setFixed(r, null);
  }

  /**
   * Sets the field value of given record to a Fixed value.
   */
  public void setFixed(int r, Fixed v) {
    // trails (backup) the record if necessary
    if (isChangedUI() 
        || value[r] == null
        || (value[r] != null && !value[r].equals(v))) {
      trail(r);

      if (v != null) {
        if (v.getScale() != currentScale[r]) {
          v = v.setScale(currentScale[r]);
        }

        if (minval != null && v.compareTo(minval) == -1) {
          // !!! fixed.underflow "Warnung: Wert außerhalb des erlaubten Bereichs."
          v = minval;
        } else if (maxval != null && v.compareTo(maxval) == 1) {
          // !!! fixed overflow "Warnung: Wert außerhalb des erlaubten Bereichs."
          v = maxval;
        }
      }

      // set value in the defined row
      value[r] = v;
      // inform that value has changed
      setChanged(r);
    }
  }

  /**
   * Sets the field value of given record.
   *
   * Warning:   This method will become inaccessible to kopi users in next release
   * @kopi      inaccessible
   */
  public void setObject(int r, Object v) {
    // !!! HACK for Oracle
    if (v != null && (v instanceof Integer)) {
      setFixed(r, new NotNullFixed(((Integer)v).intValue()));
    } else {
      setFixed(r, (Fixed)v);
    }
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   *
   * @param   query      the query holding the tuple
   * @param   column      the index of the column in the tuple
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
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a Fixed value.
   */
  public Fixed getFixed(int r) {
    return (Fixed) getObject(r);
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
    String      res;
    
    if (value[r] == null) {
      return "";
    }

    res = toText(value[r].setScale(currentScale[r]));
           
    // append spaces until the max scale is reached to make commas aligned.
    // append an extra space to replace the missing comma if the current scale is zero.
    if (block.isMulti()) {
      for (int i = (currentScale[r] == 0)? -1 : currentScale[r]; i < maxScale; i++) {
        res += " ";
      }
    }
    return res;
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return value[r] == null ? "NULL" : value[r].toSql();
  }

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
   * Returns a string representation of a bigdecimal value wrt the field type.
   */
  protected String formatFixed(com.kopiright.xkopi.lib.type.Fixed value) {
    return toText(value.setScale(currentScale[block.getActiveRecord()]));
  }

  /*
   * ----------------------------------------------------------------------
   * PRIVATE METHODS
   * ----------------------------------------------------------------------
   */

  /**
   * Parses the string argument as a fixed number in human-readable format.
   */
  private static Fixed scanFixed(String str) {
    boolean     negative = false;
    int         state = 0;
    int         scale = 0;
    long        value = 0;
    long        num = 0;
    long        den = 0;

    if (str.equals("")) {
      return null;
    }

    for (int i = 0; i < str.length(); i++) {
      // skip dots
      if (str.charAt(i) == '.') {
        continue;
      }

      switch (state) {
      case 0:         // start state
        if (str.charAt(i) == ' ') {
          state = 0;
        } else if (str.charAt(i) == '+') {
          state = 1;
        } else if (str.charAt(i) == '-') {
          negative = true;
          state = 1;
        } else if (str.charAt(i) == ',') {
          state = 3;
        } else if (Character.isDigit(str.charAt(i))) {
          value = Character.digit(str.charAt(i), 10);
          state = 2;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 1:         // after initial sign
        if (str.charAt(i) == ' ') {
          state = 1;
        } else if (str.charAt(i) == ',') {
          state = 3;
        } else if (Character.isDigit(str.charAt(i))) {
          value = Character.digit(str.charAt(i), 10);
          state = 2;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 2:         // after digit before comma
        if (str.charAt(i) == ',') {
          state = 3;
        } else if (str.charAt(i) == ' ') {
          state = 4;
        } else if (str.charAt(i) == '/') {
          num = value;
          value = 0;
          state = 6;
        } else if (Character.isDigit(str.charAt(i))) {
          value = 10 * value + Character.digit(str.charAt(i), 10);
          state = 2;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 3:         // after comma
        if (Character.isDigit(str.charAt(i))) {
          value = 10 * value + Character.digit(str.charAt(i), 10);
          scale += 1;
          state = 3;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 4:         // before numerator of fractional part
        if (str.charAt(i) == ' ') {
          state = 4;
        } else if (Character.isDigit(str.charAt(i))) {
          num = Character.digit(str.charAt(i), 10);
          state = 5;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 5:         // in numerator of fractional part
        if (str.charAt(i) == '/') {
          state = 6;
        } else if (Character.isDigit(str.charAt(i))) {
          num = 10 * num + Character.digit(str.charAt(i), 10);
          state = 5;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 6:         // before denominator of fractional part
        if (str.charAt(i) == '0') {
          state = 6;
        } else if (Character.isDigit(str.charAt(i))) {
          den = Character.digit(str.charAt(i), 10);
          state = 7;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 7:         // in denominator of fractional part
        if (Character.isDigit(str.charAt(i))) {
          den = 10 * den + Character.digit(str.charAt(i), 10);
          state = 7;
        } else {
          throw new NumberFormatException();
        }
        break;

      default:
        throw new InconsistencyException();
      }
    }

    switch (state) {
    case 0:         // start state
      return null;

    case 2:         // after digit before comma
      break;

    case 3:         // after comma
      // remove trailing zeroes after comma
      while (scale > 0 && value % 10 == 0) {
        value /= 10;
        scale -= 1;
      }
      break;

    case 7:         // in denominator of fractional part
      if (num > den || num % 2 == 0 || den > 64) {
        throw new NumberFormatException();
      }

      switch ((int)den) {
      case 2:
        value = 10 * value + 5 * num;
        scale = 1;
        break;

      case 4:
        value = 100 * value + 25 * num;
        scale = 2;
        break;

      case 8:
        value = 1000 * value + 125 * num;
        scale = 3;
        break;

      case 16:
        value = 10000 * value + 625 * num;
        scale = 4;
        break;

      case 32:
        value = 100000 * value + 3125 * num;
        scale = 5;
        break;

      case 64:
        value = 1000000 * value + 15625 * num;
        scale = 6;
        break;

      default:
        throw new NumberFormatException();
      }
      break;

    default:
      throw new NumberFormatException();
    }

    if (value == 0) {
      return Fixed.DEFAULT;
    } else {
      if (negative) {
        value = -value;
      }
      return new NotNullFixed(value, scale);
    }
  }

  /**
   * Calculaes the upper bound of a fixnum field : FIXNUM(digits, scale)
   *
   * @param     digits          the number of total digits.
   * @param     scale           the number of digits representing the fractional part.
   */
  public static NotNullFixed calculateUpperBound(int digits, int scale) {
    char[]      asciiBound;
    
    if (scale == 0) {
      asciiBound = new char[digits]; 
      
      for (int i = 0; i < digits; i++) {
        asciiBound[i] = '9';
      }
    } else {
      asciiBound = new char[digits+1];

      for (int i = 0; i < digits+1; i++) {
        asciiBound[i] = '9';
      }
      asciiBound[digits-scale] = '.';
    }

    return new NotNullFixed(new String(asciiBound));
  }

  /**
   * Computes the the width of a fixnum field : FIXNUM(digits, scale)
   *
   * @param     digits          the number of total digits.
   * @param     scale           the number of digits representing the fractional part.
   * @param     minVal          the minimal value the fixnum field can get.
   * @param     maxVal          the maximal value the fixnum field can get.
   */
  public static int computeWidth(int digits, int scale, Fixed minVal, Fixed maxVal) {
    Fixed       upperBound;
    Fixed       lowerBound;
    int         width;

    upperBound = calculateUpperBound(digits, scale);
    lowerBound = upperBound.negate();

    if (minVal != null && minVal.compareTo(lowerBound) > 0) {
      lowerBound = minVal.setScale(scale);
    }
    if (maxVal != null && maxVal.compareTo(upperBound) < 0) {
      upperBound = maxVal.setScale(scale);
    }

//!!!TEST
    {
      if (digits == 5 && scale == 2) {
        System.err.println("**** " + lowerBound + " " + upperBound);
      }
    }
//!!!TEST
    return Math.max(upperBound.toString().length(), lowerBound.toString().length());
  }

  /**
   * Computes the number of digits of a fixed field : FIXED(width, scale)
   *
   * @param     width           the width of the fixed field.
   * @param     scale           the number of digits representing the fractional part.
   */
  static public int computeDigits(int width, int scale) {
    if (scale == 0) {
      return width - width/4;
    } else if (width == scale || width == scale + 1) {
      return scale;
    } else {
      // decimal = width - scale - 1
      // digits = decimal - dicimal/4 + scale
      return width - 1 - (width - scale - 1)/4;
    }
  }

  /**
   * Returns the string represention in human-readable format.
   */
  public String toText(Fixed v) {
    if (!this.fraction) {
      return v.toString();
    } else {
      return toFraction(v.toString());
    }
  }

  private String toFraction(String str) {
    int         dot;

    if ((dot = str.indexOf(',')) == -1) {
      return str;
    }
    String precomma = str.substring(0, dot);
    int fract = Integer.valueOf(str.substring(dot + 1, str.length())).intValue();

    if (fract * 64 % 1000000 != 0) {
      return str;
    } else if (fract == 0) {
      return precomma;
    } else {
      int num, den;

      den = 64;
      num = (fract * den) / 1000000;
      while (num % 2 == 0) {
        num /= 2;
        den /= 2;
      }

      if (precomma.equals("0")) {
        return "" + num + "/" + den;
      } else if (precomma.equals("-0")) {
        return "-" + num + "/" + den;
      } else {
        return precomma + " " + num + "/" + den;
      }
    }
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  // static (compiled) data
  private final boolean         fraction; // display as fraction
  private final int             digits;
  // dynamic data
  private  Fixed           maxval;   // maximum value allowed
  private  Fixed           minval;   // minimum value allowed
  private  int             maxScale; 
  private int[]            currentScale; // number of digits after dot
  private Fixed[]          value;
}
