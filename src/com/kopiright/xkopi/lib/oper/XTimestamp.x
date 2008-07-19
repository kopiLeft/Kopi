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

package com.kopiright.xkopi.lib.oper;

import com.kopiright.xkopi.lib.type.Timestamp;
import com.kopiright.xkopi.lib.type.NotNullTimestamp;

/**
 * This class implements all overloaded operators for timestamp and some utilities
 *
 * The class used to represents timestamp is Timestamp from the JDK libs
 * We define here every operators than we want to use on timestamp
 *
 * Overloading of operator allows such expression:
 * <PRE>
 * Timestamp	d1 = XTimestamp.now();
 * </PER>
 */
public class XTimestamp {

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  /**
   * The current timestamp
   */
  public static NotNullTimestamp now() {
    return NotNullTimestamp.now();
  }

  /**
   * Allows conversion "(timestamp)Timestamp"
   */
  public static timestamp operator:()(Timestamp i) {
    if (i == null) {
      throw new NullValueInCastOperator("(timestamp)null");
    }
    return NotNullTimestamp.castToNotNull(i);
  }

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS (NOT USED IN TIMESTAMP)
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // UNARY OPERATORS
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // BINARY OPERATORS (COMPARISON)
  // ----------------------------------------------------------------------

  /**
   * Operator ">"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l > r
   */
  public static boolean operator:>(Timestamp l, Timestamp r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">" + r);
    }
    return l.compareTo(r) > 0;
  }

  /**
   * Operator ">="
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l >= r
   */
  public static boolean operator:>=(Timestamp l, Timestamp r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">=" + r);
    }
    return l.compareTo(r) >= 0;
  }

  /**
   * Operator "<"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l < r
   */
  public static boolean operator:<(Timestamp l, Timestamp r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<" + r);
    }
    return l.compareTo(r) < 0;
  }

  /**
   * Operator "<="
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l <= r
   */
  public static boolean operator:<=(Timestamp l, Timestamp r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l.compareTo(r) <= 0;
  }

}
