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
 * $Id: Fixed.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.lib.type;

import com.ibm.math.BigDecimal;
import com.ibm.math.MathContext;

/**
 * This class represents kopi fixed type
 */
public class Fixed extends Number implements Comparable {

  /*package*/ Fixed(BigDecimal b) {
    value = b;
  }

  /*package*/ Fixed(java.math.BigDecimal b) {
    this(new BigDecimal(b));
  }

  /*package*/ Fixed(java.math.BigInteger b) {
    this(new BigDecimal(b));
  }

  /*package*/ Fixed(java.math.BigInteger b, int l) {
    this(new BigDecimal(b));
  }

  /*package*/ Fixed(long value, int scale) {
    this(BigDecimal.valueOf(value, scale));
  }

  /*package*/ Fixed(double d) {
    this(new BigDecimal(d));
  }

  /*package*/ Fixed(String s) {
    this(new BigDecimal(s));
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * add
   */
  public NotNullFixed add(NotNullFixed f) {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      return f;
    } else if (((Fixed)f).value.compareTo(BigDecimal.ZERO) == 0) {
      return new NotNullFixed(value);
    }

    return new NotNullFixed(value.add(((Fixed)f).value, MATH_CONTEXT));
  }

  /**
   * divide
   */
  public NotNullFixed divide(NotNullFixed f) {
    return new NotNullFixed(value.divide(((Fixed)f).value, DIV_CONTEXT).plus(MATH_CONTEXT));
  }

  /**
   * multiply
   */
  public NotNullFixed multiply(NotNullFixed f) {
    return new NotNullFixed(value.multiply(((Fixed)f).value, MATH_CONTEXT));
  }

  /**
   * subtract
   */
  public NotNullFixed subtract(NotNullFixed f) {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      return new NotNullFixed(((Fixed)f).value.negate());
    } else if (((Fixed)f).value.compareTo(BigDecimal.ZERO) == 0) {
      return new NotNullFixed(value);
    }

    return new NotNullFixed(value.subtract(((Fixed)f).value, MATH_CONTEXT));
  }

  /**
   * Unary minus
   */
  public NotNullFixed negate() {
    return new NotNullFixed(value.negate());
  }

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * setScale
   */
  public NotNullFixed setScale(int v) {
    return new NotNullFixed(value.setScale(v, java.math.BigDecimal.ROUND_HALF_UP));
  }

  /**
   * setScale
   */
  public NotNullFixed setScale(int v, int d) {
    return new NotNullFixed(value.setScale(v, d));
  }

  /**
   * getScale
   */
  public int getScale() {
    return value.scale();
  }

  /**
   * Returns the fixed as a double
   */
  public double doubleValue() {
    return value.doubleValue();
  }

  /**
   * Comparisons
   */
  public int compareTo(Fixed other) {
    return value.compareTo(other.value);
  }

  public int compareTo(Object other) {
    return compareTo((Fixed) other);
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Compares two objects
   */
  public boolean equals(Object other) {
    return (other instanceof Fixed) &&
      value.equals(((Fixed)other).value);
  }

  /**
   * Parse the String arguments and return the corresponding value
   */
  public static NotNullFixed valueOf(String val) {
    return new NotNullFixed(val);
  }

  /**
   * Format the object depending on the current language
   */
  public final String toString() {
    return toString(java.util.Locale.GERMAN); // !!!
  }

  /**
   * Format the object depending on the current language
   * @param	locale	the current language
   */
  public String toString(java.util.Locale locale) {
    String		str = value.toString();
    StringBuffer	buf = new StringBuffer();
    int			pos = 0;
    int			dot;

    // has minus sign ?
    if (str.charAt(0) == '-') {
      buf.append('-');
      pos = 1;
    }

    // get number of digits in front of the dot
    if ((dot = str.indexOf('.')) == -1) {
      if ((dot = str.indexOf(' ')) == -1) {		// FRACTION DOT IS SPACE
        dot = str.length();
      }
    }

    if (dot - pos <= 3) {
      buf.append(str.substring(pos, dot));
      pos = dot;
    } else {
      switch ((dot - pos) % 3) {
      case 1:
	buf.append(str.substring(pos, pos + 1));
	pos += 1;
	break;
      case 2:
	buf.append(str.substring(pos, pos + 2));
	pos += 2;
	break;
      case 0:
	buf.append(str.substring(pos, pos + 3));
	pos += 3;
	break;
      }

      do {
	buf.append(".").append(str.substring(pos, pos + 3));
	pos += 3;
      }
      while (dot - pos > 0);
    }

    if (str.length() > pos) {
      buf.append(",").append(str.substring(pos + 1));
    }

    return buf.toString();
  }

  /**
   * Represents the value in sql
   */
  public String toSql() {
    return value.toString();
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF NUMBER
  // ----------------------------------------------------------------------

  public int intValue() {
    return value.intValue();
  }

  public long longValue() {
    return value.longValue();
  }

  public float floatValue() {
    return value.floatValue();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static MathContext	MATH_CONTEXT	= new MathContext(0, MathContext.PLAIN);
  private static MathContext	DIV_CONTEXT	= new MathContext(30, MathContext.SCIENTIFIC, false, MathContext.ROUND_HALF_UP);

  private BigDecimal		value;

  // ----------------------------------------------------------------------
  // CONSTANTS
  // ----------------------------------------------------------------------

  public static final NotNullFixed	DEFAULT		= new NotNullFixed(0);
}
