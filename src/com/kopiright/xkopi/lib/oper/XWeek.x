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

import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Week;
import com.kopiright.xkopi.lib.type.NotNullDate;
import com.kopiright.xkopi.lib.type.NotNullWeek;

/**
 * This class implements all overloaded operators for weeks.
 */
public class XWeek {

  // ----------------------------------------------------------------------
  // SPECIAL OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * The current week
   */
  public static NotNullWeek now() {
    return Week.now();
  }

  // ----------------------------------------------------------------------
  // CASTS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion "(week)Week"
   */
  public static NotNullWeek operator:()(Week i) {
    if (i == null) {
      throw new NullValueInCastOperator("(week)null");
    }
    return NotNullWeek.castToNotNull(i);
  }

  /**
   * Allows conversion "(week)date"
   */
  public static NotNullWeek operator:()(NotNullDate i) {
    if (i == null) {
      throw new NullValueInCastOperator("(week)null");
    }
    return new NotNullWeek(i);
  }

  /**
   * Allows conversion "(Week)Date"
   */
  public static Week operator:()(Date i) {
    return i == null ? null : new NotNullWeek(i);
  }

  /**
   * Allows conversion "(date)week"
   *
   * Returns the first day of the week
   */
  public static NotNullDate operator:()(NotNullWeek i) {
    if (i == null) {
      throw new NullValueInCastOperator("(date)null");
    }
    return i.getDate();
  }

  /**
   * Allows conversion "(Date)Week"
   *
   * Returns the first day of the week
   */
  public static Date operator:()(Week m) {
    return m == null ? null : m.getDate();
  }

  // ----------------------------------------------------------------------
  // UNARY OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Operator "*++"
   *
   * This add a week to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  public static Week operator:*++(Week l) {
    if (l == null) {
      return null;
    } else {
      NotNullWeek	r;

      r = l.copy();
      l.addTo(1);
      return r;
    }
  }

  /**
   * Operator "++*"
   *
   * This add a week to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  public static Week operator:++*(Week l) {
    if (l == null) {
      return null;
    } else {
      NotNullWeek	r;

      l.addTo(1);
      r = l.copy();
      return r;
    }
  }

  /**
   * Operator "*--"
   *
   * This subtracts a week to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  public static Week operator:*--(Week l) {
    if (l == null) {
      return null;
    } else {
      NotNullWeek	r;

      r = l.copy();
      l.addTo(-1);
      return r;
    }
  }

  /**
   * Operator "--*"
   *
   * This subtract a week to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  public static Week operator:--*(Week l) {
    if (l == null) {
      return null;
    } else {
      NotNullWeek	r;

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
  public static boolean operator:>(Week l, Week r) {
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
  public static boolean operator:>=(Week l, Week r) {
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
  public static boolean operator:<(Week l, Week r) {
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
  public static boolean operator:<=(Week l, Week r) {
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
   * This add r weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Week operator:+(Week l, int r) {
    if (l == null) {
      return null;
    } else {
      return l.add(r);
    }
  }

  /**
   * Operator "+"
   *
   * This add r weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullWeek operator:+(NotNullWeek l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + "+" + r);
    }
    return l.add(r);
  }

  /**
   * Operator "+"
   *
   * This add r weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Week operator:+(Week l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return l.add(r.intValue());
    }
  }

  /**
   * Operator "+"
   *
   * This add l weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Week operator:+(int l, Week r) {
    if (r == null) {
      return null;
    } else {
      return r.add(l);
    }
  }

  /**
   * Operator "+"
   *
   * This add l weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullWeek operator:+(int l, NotNullWeek r) {
    if (r == null) {
      throw new NullValueInOperation(l + "+" + r);
    }
    return r.add(l);
  }

  /**
   * Operator "+"
   *
   * This add l weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Week operator:+(Integer l, Week r) {
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
   * This subtract r weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Week operator:-(Week l, int r) {
    if (l == null) {
      return null;
    } else {
      return l.add(-r);
    }
  }

  /**
   * Operator "-"
   *
   * This subtract r weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static NotNullWeek operator:-(NotNullWeek l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + "-" + r);
    }
    return l.add(-r);
  }

  /**
   * Operator "-"
   *
   * This subtract r weeks to the week
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Week operator:-(Week l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return l.add(-r.intValue());
    }
  }


  /**
   * Operator "-"
   *
   * This returns the difference in week between the two weeks
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Week l, Week r) {
    if (l == null || r == null) {
      return null;
    } else {
      return l.subtract(r);
    }
  }

  /**
   * Operator "-"
   *
   * This returns the difference in week between the two weeks
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static int operator:-(NotNullWeek l, NotNullWeek r) {
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
  public static boolean operator:==(Week l, Week r) {
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
  public static boolean operator:!=(Week l, Week r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return l.compareTo(r) != 0;
    }
  }
}
