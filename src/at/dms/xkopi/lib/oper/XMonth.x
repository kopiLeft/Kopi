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
 * $Id: XMonth.x,v 1.4 2001/05/24 19:28:57 graf Exp $
 */

package at.dms.xkopi.lib.oper;

import at.dms.xkopi.lib.type.Date;
import at.dms.xkopi.lib.type.Month;
import at.dms.xkopi.lib.type.NotNullDate;
import at.dms.xkopi.lib.type.NotNullMonth;

/**
 * This class implements all overloaded operators for months.
 */
public class XMonth {

  // ----------------------------------------------------------------------
  // SPECIAL OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * The current month
   */
  public static NotNullMonth now() {
    return Month.now();
  }

  // ----------------------------------------------------------------------
  // CASTS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion "(month)Month"
   */
  public static NotNullMonth operator:()(Month i) {
    if (i == null) {
      throw new NullValueInCastOperator("(month)null");
    }
    return NotNullMonth.castToNotNull(i);
  }

  /**
   * Allows conversion "(month)date"
   */
  public static NotNullMonth operator:()(NotNullDate i) {
    if (i == null) {
      throw new NullValueInCastOperator("(month)null");
    }
    return new NotNullMonth(i);
  }

  /**
   * Allows conversion "(Month)Date"
   */
  public static Month operator:()(Date i) {
    return i == null ? null : new NotNullMonth(i);
  }

  /**
   * Allows conversion "(date)month"
   *
   * Returns the first day of the month
   */
  public static NotNullDate operator:()(NotNullMonth i) {
    if (i == null) {
      throw new NullValueInCastOperator("(date)null");
    }
    return i.getDate();
  }

  /**
   * Allows conversion "(Date)Month"
   *
   * Returns the first day of the month
   */
  public static Date operator:()(Month m) {
    return m == null ? null : m.getDate();
  }

  // ----------------------------------------------------------------------
  // UNARY OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Operator "*++"
   *
   * This add a month to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  public static Month operator:*++(Month l) {
    if (l == null) {
      return null;
    } else {
      NotNullMonth	r;

      r = l.copy();
      l.addTo(1);
      return r;
    }
  }

  /**
   * Operator "++*"
   *
   * This add a month to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  public static Month operator:++*(Month l) {
    if (l == null) {
      return null;
    } else {
      NotNullMonth	r;

      l.addTo(1);
      r = l.copy();
      return r;
    }
  }

  /**
   * Operator "*--"
   *
   * This subtracts a month to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  public static Month operator:*--(Month l) {
    if (l == null) {
      return null;
    } else {
      NotNullMonth	r;

      r = l.copy();
      l.addTo(-1);
      return r;
    }
  }

  /**
   * Operator "--*"
   *
   * This subtract a month to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  public static Month operator:--*(Month l) {
    if (l == null) {
      return null;
    } else {
      NotNullMonth	r;

      l.addTo(-1);
      r = l.copy();
      return r;
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
  public static boolean operator:>(Month l, Month r) {
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
  public static boolean operator:>=(Month l, Month r) {
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
  public static boolean operator:<(Month l, Month r) {
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
  public static boolean operator:<=(Month l, Month r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l.compareTo(r) <= 0;
  }

  // ----------------------------------------------------------------------
  // ADD OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "+"
   *
   * This add r months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Month operator:+(Month l, int r) {
    if (l == null) {
      return null;
    } else {
      return l.add(r);
    }
  }

  /**
   * Operator "+"
   *
   * This add r months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullMonth operator:+(NotNullMonth l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + "+" + r);
    }
    return l.add(r);
  }

  /**
   * Operator "+"
   *
   * This add r months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Month operator:+(Month l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return l.add(r.intValue());
    }
  }

  /**
   * Operator "+"
   *
   * This add l months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Month operator:+(int l, Month r) {
    if (r == null) {
      return null;
    } else {
      return r.add(l);
    }
  }

  /**
   * Operator "+"
   *
   * This add l months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullMonth operator:+(int l, NotNullMonth r) {
    if (r == null) {
      throw new NullValueInOperation(l + "+" + r);
    }
    return r.add(l);
  }

  /**
   * Operator "+"
   *
   * This add l months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Month operator:+(Integer l, Month r) {
    if (l == null || r == null) {
      return null;
    } else {
      return r.add(l.intValue());
    }
  }

  // ----------------------------------------------------------------------
  // MINUS OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "-"
   *
   * This subtract r months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Month operator:-(Month l, int r) {
    if (l == null) {
      return null;
    } else {
      return l.add(-r);
    }
  }

  /**
   * Operator "-"
   *
   * This subtract r months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static NotNullMonth operator:-(NotNullMonth l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + "-" + r);
    }
    return l.add(-r);
  }

  /**
   * Operator "-"
   *
   * This subtract r months to the month
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Month operator:-(Month l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return l.add(-r.intValue());
    }
  }


  /**
   * Operator "-"
   *
   * This returns the difference in month between the two months
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Month l, Month r) {
    if (l == null || r == null) {
      return null;
    } else {
      return l.subtract(r);
    }
  }

  /**
   * Operator "-"
   *
   * This returns the difference in month between the two months
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static int operator:-(NotNullMonth l, NotNullMonth r) {
    if (l == null || r == null) {
      throw new NullValueInOperation(l + "-" + r);
    }
    return l.subtract(r);
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
  public static boolean operator:==(Month l, Month r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.compareTo(r) == 0;
    }
  }

  /**
   * Operator "!="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l != r
   */
  public static boolean operator:!=(Month l, Month r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return l.compareTo(r) != 0;
    }
  }
}
