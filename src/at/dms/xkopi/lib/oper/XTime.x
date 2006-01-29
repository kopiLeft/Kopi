/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: XTime.x,v 1.2 2001/05/24 19:28:57 graf Exp $
 */

package at.dms.xkopi.lib.oper;

import at.dms.xkopi.lib.type.Time;
import at.dms.xkopi.lib.type.NotNullTime;

/**
 * This class implements all overloaded operators for time and some utilities
 *
 * The class used to represents time is Time from the JDK libs
 * We define here every operators than we want to use on time
 *
 * Overloading of operator allows such expression:
 * <PRE>
 * Time	d1 = XTime.now();
 * Time d2 = d1 + 1; // one second later
 * int	i = d2 - d1; // one
 * </PER>
 */
public class XTime {

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  /**
   * The current time
   */
  public static NotNullTime now() {
    return NotNullTime.now();
  }

  /**
   * Allows conversion "(time)Time"
   */
  public static time operator:()(Time i) {
    if (i == null) {
      throw new NullValueInCastOperator("(time)null");
    }
    return NotNullTime.castToNotNull(i);
  }

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS (NOT USED IN TIME)
  // ----------------------------------------------------------------------

  /**
   * Allows narrowing conversion "(float)1.3d"
   */
  //public static Time operator:()(int i) {
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
   * This add a second to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  public static Time operator:*++(Time l) {
    if (l == null) {
      return null;
    } else {
      int	val = l.getScalar();

      l.setScalar(val + 1);
      return new NotNullTime(val);
    }
  }

  /**
   * Operator "++*"
   *
   * This add a second to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  public static Time operator:++*(Time l) {
    if (l == null) {
      return null;
    } else {
      l.setScalar(l.getScalar() + 1);
      return new NotNullTime(l.getScalar());
    }
  }

  /**
   * Operator "*--"
   *
   * This substract a second to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  public static Time operator:*--(Time l) {
    if (l == null) {
      return null;
    } else {
      int	val = l.getScalar();

      l.setScalar(val - 1);
      return new NotNullTime(val);
    }
  }

  /**
   * Operator "--*"
   *
   * This substract a second to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  public static Time operator:--*(Time l) {
    if (l == null) {
      return null;
    } else {
      l.setScalar(l.getScalar() - 1);
      return new NotNullTime(l.getScalar());
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
  public static boolean operator:>(Time l, Time r) {
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
  public static boolean operator:>=(Time l, Time r) {
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
  public static boolean operator:<(Time l, Time r) {
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
  public static boolean operator:<=(Time l, Time r) {
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
   * This add r seconds to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Time operator:+(Time l, int r) {
    if (l == null) {
      return null;
    } else {
      return new NotNullTime(l.getScalar() + r);
    }
  }

  /**
   * Operator "+"
   *
   * This add r seconds to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Time operator:+(Time l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new NotNullTime(l.getScalar() + r.intValue());
    }
  }

  /**
   * Operator "+"
   *
   * This add l seconds to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Time operator:+(int l, Time r) {
    if (r == null) {
      return null;
    } else {
      return new NotNullTime(r.getScalar() + l);
    }
  }

  /**
   * Operator "+"
   *
   * This add l seconds to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Time operator:+(Integer l, Time r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new NotNullTime(r.getScalar() + l.intValue());
    }
  }

  // ----------------------------------------------------------------------
  // MINUS OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "-"
   *
   * This substract r seconds to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Time operator:-(Time l, int r) {
    if (l == null) {
      return null;
    } else {
      return new NotNullTime(l.getScalar() - r);
    }
  }

  /**
   * Operator "-"
   *
   * This substract r seconds to the time
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Time operator:-(Time l, Integer r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new NotNullTime(l.getScalar() - r.intValue());
    }
  }


  /**
   * Operator "-"
   *
   * This returns the difference in seconds between the two times
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Time l, Time r) {
    if (l == null || r == null) {
      return null;
    } else {
      return new Integer(l.getScalar() - r.getScalar());
    }
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
  public static boolean operator:==(Time l, Time r) {
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
  public static boolean operator:!=(Time l, Time r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return l.getScalar() != r.getScalar();
    }
  }
}
