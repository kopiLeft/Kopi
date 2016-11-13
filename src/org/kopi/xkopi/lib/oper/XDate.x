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

package org.kopi.xkopi.lib.oper;

import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.NotNullDate;

/**
 * This class implements all overloaded operators for date and some utilities
 *
 * The class used to represents date is org.kopi.xkopi.lib.type.Date
 * We define here every operators than we want to use on date
 *
 * Overloading of operator allows such expression:
 * <PRE>
 * Date	d1 = XDate.now();
 * Date d2 = d1 + 1; // tomorrow
 * int	i = d2 - d1; // one
 * </PER>
 */
public class XDate {

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  /**
   * The current date
   */
  public static NotNullDate now() {
    return Date.now();
  }

  /**
   * Allows conversion "(date)Date"
   */
  public static NotNullDate operator:()(Date i) {
    if (i == null) {
      throw new NullValueInCastOperator("(date)null");
    }

    return org.kopi.xkopi.lib.type.NotNullDate.castToNotNull(i);
  }

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS (NOT USED IN DATE)
  // ----------------------------------------------------------------------

  /**
   * Allows narrowing conversion "(float)1.3d"
   */
  //public static Date operator:()(int i) {
  //  return new Integer(i);
  //}

  /**
   * Do not allow narrowing conversion "double d = new Double(12.5);"
   */
  //public static Integer operator:=(int i) {
  //  return new Integer(i);
  //}

  // ----------------------------------------------------------------------
  // UNARY OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Operator "*++"
   *
   * Adds a day to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  public static Date operator:*++(Date l) {
    if (l == null) {
      return null;
    } else {
      int	val = l.getScalar();

      l.setScalar(val + 1);
      return new NotNullDate(val);
    }
  }

  /**
   * Operator "++*"
   *
   * Adds a day to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  public static Date operator:++*(Date l) {
    if (l == null) {
      return null;
    }

    l.setScalar(l.getScalar() + 1);
    return new NotNullDate(l.getScalar());
  }

  /**
   * Operator "*--"
   *
   * Substracts a day to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  public static Date operator:*--(Date l) {
    if (l == null) {
      return null;
    } else {
      int	val = l.getScalar();

      l.setScalar(val - 1);
      return new NotNullDate(val);
    }
  }

  /**
   * Operator "--*"
   *
   * Substracts a day to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  public static Date operator:--*(Date l) {
    if (l == null) {
      return null;
    } else {
      l.setScalar(l.getScalar() - 1);
      return new NotNullDate(l.getScalar());
    }
  }

  // ----------------------------------------------------------------------
  // BINARY OPERATORS
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
  public static boolean operator:>(Date l, Date r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">" + r);
    }

    return l.getScalar() > r.getScalar();
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
  public static boolean operator:>=(Date l, Date r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">=" + r);
    }

    return l.getScalar() >= r.getScalar();
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
  public static boolean operator:<(Date l, Date r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<" + r);
    }
    return l.getScalar() < r.getScalar();
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
  public static boolean operator:<=(Date l, Date r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l.getScalar() <= r.getScalar();
  }

  // ----------------------------------------------------------------------
  // ADD OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "+"
   *
   * Adds r days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Date operator:+(Date l, int r) {
    if (l == null) {
      return null;
    }
    return new NotNullDate(l.getScalar() + r);
  }

  /**
   * Operator "+"
   *
   * Adds r days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullDate operator:+(NotNullDate l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + " + " + r);
    }
    return new NotNullDate(l.getScalar() + r);
  }

  /**
   * Operator "+"
   *
   * Adds r days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Date operator:+(Date l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new NotNullDate(l.getScalar() + r.intValue());
    }
  }

  /**
   * Operator "+"
   *
   * Adds l days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Date operator:+(int l, Date r) {
    if (r == null) {
      return null;
    } else {
      return new NotNullDate(r.getScalar() + l);
    }
  }

  /**
   * Operator "+"
   *
   * Adds l days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullDate operator:+(int l, NotNullDate r) {
    if (r == null) {
      throw new NullValueInOperation(l + " + " + r);
    }

    return new NotNullDate(r.getScalar() + l);
  }

  /**
   * Operator "+"
   *
   * Adds l days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Date operator:+(Integer l, Date r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new NotNullDate(r.getScalar() + l.intValue());
    }
  }

  // ----------------------------------------------------------------------
  // MINUS OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "-"
   *
   * Substracts r days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Date operator:-(Date l, int r) {
    if (l == null) {
      return null;
    } else {
      return new NotNullDate(l.getScalar() - r);
    }
  }

  /**
   * Operator "-"
   *
   * Substracts r days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static NotNullDate operator:-(NotNullDate l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + " + " + r);
    }
    return new NotNullDate(l.getScalar() - r);
  }

  /**
   * Operator "-"
   *
   * Substracts r days to this date
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Date operator:-(Date l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new NotNullDate(l.getScalar() - r.intValue());
    }
  }

  /**
   * Operator "-"
   *
   * Substracts two dates and returns a day
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Date l, Date r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new Integer(l.getScalar() - r.getScalar());
    }
  }

  /**
   * Operator "-"
   *
   * Substracts two dates and returns a day
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static int operator:-(NotNullDate l, NotNullDate r) {
    if (l == null || r == null) {
      throw new NullValueInOperation(l + " - " + r);
    }

    return  l.getScalar() - r.getScalar();
  }

  // ----------------------------------------------------------------------
  // BINARY OPERATORS (COMPARISON)
  // ----------------------------------------------------------------------

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Date l, Date r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.getScalar() == r.getScalar();
    }
  }

  /**
   * Operator "!="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l != r
   */
  public static boolean operator:!=(Date l, Date r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return l.getScalar() != r.getScalar();
    }
  }
}
