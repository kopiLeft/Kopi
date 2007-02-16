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

import com.kopiright.xkopi.lib.type.Fixed;
import com.kopiright.xkopi.lib.type.NotNullFixed;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class implements all overloaded operators for fixed point numbers
 * and some utilities
 *
 */
public class XFixed {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Fixed)"123"
   *
   * Returns the value of the string as an double
   */
  public static Fixed operator:()(String m) {
    if (m == null) {
      return null;
    } else {
      return new NotNullFixed(m);
    }
  }

  /**
   * Allows conversion "(Fixed)1"
   *
   */
  public static NotNullFixed operator:()(double i) {
    return new NotNullFixed(i);
  }

  /**
   * Allows conversion "(Fixed)1"
   *
   */
  public static Fixed operator:()(double i) {
    return new NotNullFixed(i);
  }

  /**
   * Allows conversion "(Fixed)1"
   *
   */
  public static Fixed operator:()(Number i) {
    if (i == null) {
      return null;
    } else {
      return new NotNullFixed(i.doubleValue());
    }
  }

  /**
   * Allows conversion "(Fixed)1"
   *
   */
  public static NotNullFixed operator:()(Number i) {
    if (i == null) {
      throw new NullValueInCastOperator("(NotNullFixed)null");
    }
    return new NotNullFixed(i.doubleValue());
  }

  /**
   * Allows conversion "(NotNullFixed)Fixed"
   */
  public static NotNullFixed operator:()(Fixed i) {
    if (i == null) {
      throw new NullValueInCastOperator("(NotNullFixed)null");
    }
    return NotNullFixed.castToNotNull(i);
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from double to Fixed
   */
  public static NotNullFixed operator:=(double i) {
    return new NotNullFixed(i);
  }

  /**
   * Assignments from double to Fixed
   */
  public static Fixed operator:=(double i) {
    return new NotNullFixed(i);
  }

  /**
   * Assignments from double to Fixed
   */
  public static Fixed operator:=(Number i) {
    return new NotNullFixed(i.doubleValue());
  }

  /**
   * Assignment form Fixed to double
   */
  //  public static double operator:=(Number i) {
  //  return i.doubleValue();
  //}

  // ----------------------------------------------------------------------
  // UNARY OPERATORS
  // ----------------------------------------------------------------------

  /**
   * new Array
   */
  public static Object operator$array(fixed fix, int[] dims) {
    try {
     switch(dims.length) {
      case 0 :
        return
          java.lang.reflect.Array.newInstance(Class.forName(NotNullFixed.class.getName()), 0);
      case 1:
        Object o =
          java.lang.reflect.Array.newInstance(Class.forName(NotNullFixed.class.getName()),
                                              dims[0]);

        for (int i = 0; i < dims[0]; i++) {
          ((Object[]) o)[i] = new NotNullFixed(0);
        }
        return o;
      default:
        Object  inst = java.lang.reflect.Array.newInstance(Class.forName(NotNullFixed.class.getName()),
                                                           dims);
        operator$arrayHelper(inst, 0, dims);
        return inst;
     }
    } catch (ClassNotFoundException cnfe) {
      throw new InconsistencyException(cnfe.toString());
    }
  }

  private static void operator$arrayHelper(Object inst, int idx, int[] dims) {
    int         len = dims[idx];

    if (idx == dims.length-1) {
      for (int i = 0; i < len; i++) {
        ((Object[]) inst)[i] = new NotNullFixed(0);
      }
    } else {
      for (int i = 0; i < len; i++) {
        operator$arrayHelper(((Object[]) inst)[i], idx+1, dims);
      }
    }
  }

 /**
   * Unary Operator "+"
   *
   * Returns the operand.
   *
   * @param	l		the operand
   * @return	the result of applying the operator to the operand
   */
  public static NotNullFixed operator:+(NotNullFixed l) {
    if (l == null) {
      throw new NullValueInOperation(l.toString());
    }
    return l;
  }

  /**
   * Unary Operator "+"
   *
   * Returns the operand.
   *
   * @param	l		the operand
   * @return	the result of applying the operator to the operand
   */
  public static Fixed operator:+(Fixed l) {
    if (l == null) {
      return null;
    } else {
      return l;
    }
  }

  /**
   * Unary Operator "-"
   *
   * Returns the operand with inverted sign.
   *
   * @param	l		the operand
   * @return	the result of applying the operator to the operand
   */
  public static NotNullFixed operator:-(NotNullFixed l) {
    if (l == null) {
      throw new NullValueInOperation(l.toString());
    }
    return l.negate();
  }


  /**
   * Unary Operator "-"
   *
   * Returns the operand with inverted sign.
   *
   * @param	l		the operand
   * @return	the result of applying the operator to the operand
   */
  public static Fixed operator:-(Fixed l) {
    if (l == null) {
      return null;
    } else {
      return l.negate();
    }
  }

  // ----------------------------------------------------------------------
  // UNARY OPERATORS  DOUBLES ARE INMUTABLE => the compiler will
  //                  transform i++ into (i += 1)
  // ----------------------------------------------------------------------

  /*
   * Operator "*++"
   *
   * This add a double to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  //public static Fixed operator:*++(Fixed l) {
  //}

  /*
   * Operator "++*"
   *
   * This add a double to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  //public static Fixed operator:++*(Fixed l) {
  //}

  /*
   * Operator "*--"
   *
   * This substract a double to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  //public static Fixed operator:*--(Fixed l) {
  //}

  /*
   * Operator "--*"
   *
   * This substract a double to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  //public static Fixed operator:--*(Fixed l) {
  //}

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
  public static boolean operator:==(Fixed l, Number r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.equals(toFixed(r));
    }
  }

  /**
   * Operator "!="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Fixed l, Number r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return !l.equals(toFixed(r));
    }
  }

  /**
   * Operator ">"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l > r
   */
  public static boolean operator:>(Fixed l, Number r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">" + r);
    }
    return l.doubleValue() > r.doubleValue();
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
  public static boolean operator:>=(Fixed l, Number r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">=" + r);
    }
    return l.doubleValue() >= r.doubleValue();
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
  public static boolean operator:<(Fixed l, Number r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<" + r);
    }
    return l.doubleValue() < r.doubleValue();
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
  public static boolean operator:<=(Fixed l, Number r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l.doubleValue() <= r.doubleValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Number l, Fixed r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return toFixed(l).equals(r);
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Number l, Fixed r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
    return !toFixed(l).equals(r);
    }
  }


  /**
   * Operator ">"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l > r
   */
  public static boolean operator:>(Number l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">" + r);
    }
    return l.doubleValue() > r.doubleValue();
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
  public static boolean operator:>=(Number l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">=" + r);
    }
    return l.doubleValue() >= r.doubleValue();
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
  public static boolean operator:<(Number l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<" + r);
    }
    return l.doubleValue() < r.doubleValue();
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
  public static boolean operator:<=(Number l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l.doubleValue() <= r.doubleValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Fixed l, Fixed r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.compareTo(r) == 0;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Fixed l, Fixed r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return l.compareTo(r) != 0;
    }
  }

  /**
   * Operator ">"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l > r
   */
  public static boolean operator:>(Fixed l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">" + r);
    }
    return l.compareTo(r) == 1;
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
  public static boolean operator:>=(Fixed l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + ">=" + r);
    }
    return l.compareTo(r) != -1;
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
  public static boolean operator:<(Fixed l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<" + r);
    }
    return l.compareTo(r) == -1;
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
  public static boolean operator:<=(Fixed l, Fixed r) {
    if (l == null || r == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l.compareTo(r) != 1;
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(double l, Fixed r) {
    if (r == null) {
      return false;
    } else {
      return l == r.doubleValue();
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(double l, Fixed r) {
    if (r == null) {
      return true;
    } else {
      return l != r.doubleValue();
    }
  }

  /**
   * Operator ">"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l > r
   */
  public static boolean operator:>(double l, Fixed r) {
    if (r == null) {
      throw new NullValueInComparison("" + l + ">" + r);
    }
    return l > r.doubleValue();
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
  public static boolean operator:>=(double l, Fixed r) {
    if (r == null) {
      throw new NullValueInComparison("" + l + ">=" + r);
    }
    return l >= r.doubleValue();
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
  public static boolean operator:<(double l, Fixed r) {
    if (r == null) {
      throw new NullValueInComparison("" + l + "<" + r);
    }
    return l < r.doubleValue();
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
  public static boolean operator:<=(double l, Fixed r) {
    if (r == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l <= r.doubleValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Fixed l, double r) {
    if (l == null) {
      return false;
    } else {
      return l.doubleValue() == r;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Fixed l, double r) {
    if (l == null) {
      return true;
    } else {
      return l.doubleValue() != r;
    }
  }

  /**
   * Operator ">"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l > r
   */
  public static boolean operator:>(Fixed l, double r) {
    if (l == null) {
      throw new NullValueInComparison("" + l + ">" + r);
    }
    return l.doubleValue() > r;
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
  public static boolean operator:>=(Fixed l, double r) {
    if (l == null) {
      throw new NullValueInComparison("" + l + ">=" + r);
    }
    return l.doubleValue() >= r;
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
  public static boolean operator:<(Fixed l, double r) {
    if (l == null) {
      throw new NullValueInComparison("" + l + "<" + r);
    }
    return l.doubleValue() < r;
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
  public static boolean operator:<=(Fixed l, double r) {
    if (l == null) {
      throw new NullValueInComparison("" + l + "<=" + r);
    }
    return l.doubleValue() <= r;
  }

  // ----------------------------------------------------------------------
  // ADD OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "+"
   *
   * This add r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Fixed operator:+(Fixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.add(new NotNullFixed(r));
  }

  /**
   * Operator "+"
   *
   * This add r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullFixed operator:+(NotNullFixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.add(new NotNullFixed(r));
  }

  /**
   * Operator "+"
   *
   * This add r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Fixed operator:+(Fixed l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return l.add((NotNullFixed)r);
  }

  /**
   * Operator "+"
   *
   * This add r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullFixed operator:+(NotNullFixed l, NotNullFixed r) {
    if (l == null || r == null) {
      throw new NullValueInOperation(l + " + " + r);
    }
    return l.add((NotNullFixed)r);
  }

  /**
   * Operator "+"
   *
   * This add l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Fixed operator:+(double l, Fixed r) {
    if (r == null) {
      return null;
    }
    return r.add(new NotNullFixed(l));
  }

  /**
   * Operator "+"
   *
   * This add l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static NotNullFixed operator:+(double l, NotNullFixed r) {
    if (r == null) {
      return null;
    }
    return r.add(new NotNullFixed(l));
  }

  /**
   * Operator "+"
   *
   * This add r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Fixed operator:+(Fixed l, Number r) {
    if (l == null || r == null) {
      return null;
    }
    return l.add(new NotNullFixed(r.doubleValue()));
  }

  /**
   * Operator "+"
   *
   * This add r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Fixed operator:+(Number l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return r.add(new NotNullFixed(l.doubleValue()));
  }

  // ----------------------------------------------------------------------
  // MINUS OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "-"
   *
   * This minus r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Fixed operator:-(Fixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.subtract(new NotNullFixed(r));
  }

  /**
   * Operator "-"
   *
   * This minus r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static NotNullFixed operator:-(NotNullFixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.subtract(new NotNullFixed(r));
  }

  /**
   * Operator "-"
   *
   * This minus r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Fixed operator:-(Fixed l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return l.subtract((NotNullFixed)r);
  }

  /**
   * Operator "-"
   *
   * This minus r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static NotNullFixed operator:-(NotNullFixed l, NotNullFixed r) {
    if (l == null || r == null) {
      throw new NullValueInOperation(l + " - " + r);
    }
    return l.subtract((NotNullFixed)r);
  }

  /**
   * Operator "-"
   *
   * This minus l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Fixed operator:-(double l, Fixed r) {
    if (r == null) {
      return null;
    }
    return new NotNullFixed(l).setScale(r.getScale()).subtract((NotNullFixed)r);
  }

  /**
   * Operator "-"
   *
   * This minus l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static NotNullFixed operator:-(double l, NotNullFixed r) {
    if (r == null) {
      return null;
    }
    return new NotNullFixed(l).subtract((NotNullFixed)r);
  }

  /**
   * Operator "-"
   *
   * This minus r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Fixed operator:-(Fixed l, Number r) {
    if (l == null || r == null) {
      return null;
    }
    return l.subtract(new NotNullFixed(r.doubleValue()));
  }

  /**
   * Operator "-"
   *
   * This minus r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Fixed operator:-(Number l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return new NotNullFixed(l.doubleValue()).subtract((NotNullFixed)r);
  }

  // ----------------------------------------------------------------------
  // MULTIPLICATIVE OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "*"
   *
   * This multiplicative r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Fixed operator:*(Fixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.multiply(new NotNullFixed(r));
  }

  /**
   * Operator "*"
   *
   * This multiplicative r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static NotNullFixed operator:*(NotNullFixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.multiply(new NotNullFixed(r));
  }

  /**
   * Operator "*"
   *
   * This multiplicative r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static NotNullFixed operator:*(NotNullFixed l, NotNullFixed r) {
    if (l == null || r == null) {
      throw new NullValueInOperation(l + " * " + r);
    }
    return l.multiply((NotNullFixed)r);
  }

  /**
   * Operator "*"
   *
   * This multiplicative r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Fixed operator:*(Fixed l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return l.multiply((NotNullFixed)r);
  }

  /**
   * Operator "*"
   *
   * This multiplicative l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Fixed operator:*(double l, Fixed r) {
    if (r == null) {
      return null;
    }
    return new NotNullFixed(l).multiply((NotNullFixed)r);
  }

  /**
   * Operator "*"
   *
   * This multiplicative l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static NotNullFixed operator:*(double l, NotNullFixed r) {
    if (r == null) {
      return null;
    }
    return new NotNullFixed(l).multiply((NotNullFixed)r);
  }

  /**
   * Operator "*"
   *
   * This multiplicative r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Fixed operator:*(Fixed l, Number r) {
    if (l == null || r == null) {
      return null;
    }
    return l.multiply(new NotNullFixed(r.doubleValue()));
  }

  /**
   * Operator "*"
   *
   * This multiplicative r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Fixed operator:*(Number l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return new NotNullFixed(l.doubleValue()).multiply((NotNullFixed)r);
  }

  // ----------------------------------------------------------------------
  // DIVIDE OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "/"
   *
   * This divide r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Fixed operator:/(Fixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.divide(new NotNullFixed(r));
  }

  /**
   * Operator "/"
   *
   * This divide r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static NotNullFixed operator:/(NotNullFixed l, double r) {
    if (l == null) {
      return null;
    }
    return l.divide(new NotNullFixed(r));
  }

  /**
   * Operator "/"
   *
   * This divide r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Fixed operator:/(Fixed l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return l.divide((NotNullFixed)r);
  }

  /**
   * Operator "/"
   *
   * This divide r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static NotNullFixed operator:/(NotNullFixed l, NotNullFixed r) {
    if (l == null || r == null) {
      throw new NullValueInOperation(l + " / " + r);
    }
    return l.divide(r);
  }

  /**
   * Operator "/"
   *
   * This divide l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Fixed operator:/(double l, Fixed r) {
    if (r == null) {
      return null;
    }
    return new NotNullFixed(l).divide((NotNullFixed)r);
  }

  /**
   * Operator "/"
   *
   * This divide l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static NotNullFixed operator:/(double l, NotNullFixed r) {
    if (r == null) {
      return null;
    }
    return new NotNullFixed(l).divide((NotNullFixed)r);
  }

  /**
   * Operator "/"
   *
   * This divide r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Fixed operator:/(Fixed l, Number r) {
    if (l == null || r == null) {
      return null;
    }
    return l.divide(new NotNullFixed(r.doubleValue()));
  }

  /**
   * Operator "/"
   *
   * This divide r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Fixed operator:/(Number l, Fixed r) {
    if (l == null || r == null) {
      return null;
    }
    return new NotNullFixed(l.doubleValue()).divide((NotNullFixed)r);
  }

  // ----------------------------------------------------------------------
  // BINARY OPERATORS (SHIFTS)
  // ----------------------------------------------------------------------

  /**
   * Operator ">>"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l >> r
   */
  public static Fixed operator:>>(Fixed l, Number r) {
    if (l == null || r == null) {
      return null;
    }
    return l.setScale(r.intValue(), java.math.BigDecimal.ROUND_FLOOR);
  }

  /**
   * Operator ">>>"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l >>> r
   */
  public static Fixed operator:>>>(Fixed l, Number r) {
    if (l == null || r == null) {
      return null;
    }
    return l.setScale(r.intValue(), java.math.BigDecimal.ROUND_HALF_UP);
  }

  /**
   * Operator "<<"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l << r
   */
  public static Fixed operator:<<(Fixed l, Number r) {
    if (l == null || r == null) {
      return null;
    }
    return l.setScale(r.intValue(), java.math.BigDecimal.ROUND_UP);
  }

  /**
   * Operator ">>"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l >> r
   */
  public static Fixed operator:>>(Fixed l, int r) {
    if (l == null) {
      return null;
    }
    return l.setScale(r, java.math.BigDecimal.ROUND_FLOOR);
  }

  /**
   * Operator ">>>"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l >>> r
   */
  public static Fixed operator:>>>(Fixed l, int r) {
    if (l == null) {
      return null;
    }
    return l.setScale(r, java.math.BigDecimal.ROUND_HALF_UP);
  }

  /**
   * Operator "<<"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l << r
   */
  public static Fixed operator:<<(Fixed l, int r) {
    if (l == null) {
      return null;
    }
    return l.setScale(r, java.math.BigDecimal.ROUND_UP);
  }
  // ----------------------------------------------------------------------
  // BINARY OPERATORS (SHIFTS) NOT NULL
  // ----------------------------------------------------------------------

  /**
   * Operator ">>"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l >> r
   */
  public static NotNullFixed operator:>>(NotNullFixed l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + " >> " + r);
    }
    return l.setScale(r, java.math.BigDecimal.ROUND_FLOOR);
  }

  /**
   * Operator ">>>"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l >>> r
   */
  public static NotNullFixed operator:>>>(NotNullFixed l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + " >>> " + r);
    }
    return l.setScale(r, java.math.BigDecimal.ROUND_HALF_UP);
  }

  /**
   * Operator "<<"
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l << r
   */
  public static NotNullFixed operator:<<(NotNullFixed l, int r) {
    if (l == null) {
      throw new NullValueInOperation(l + " << " + r);
    }
    return l.setScale(r, java.math.BigDecimal.ROUND_UP);
  }

  // ----------------------------------------------------------------------
  // CONVERTION OF NUMBER TO FIXED
  // ----------------------------------------------------------------------

  protected static Fixed toFixed(Number n) {
    if (n == null) {
      return null;
    }
    if (n instanceof Integer) {
      return new NotNullFixed(((Integer) n).intValue());
    } else if (n instanceof Float) {
      return new NotNullFixed(((Float) n).floatValue());
    } else if (n instanceof Double) {
      return new NotNullFixed(((Double) n).doubleValue());
    } else if (n instanceof Long) {
      return new NotNullFixed(((Long) n).longValue());
    } else if (n instanceof Short) {
      return new NotNullFixed(((Short) n).shortValue());
    } else if (n instanceof Byte) {
      return new NotNullFixed(((Byte) n).byteValue());
    }

    return (Fixed) n;
  }
}
