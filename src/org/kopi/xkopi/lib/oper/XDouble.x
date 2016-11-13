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

/**
 * This class implements all overloaded operators for doubles and some utilities
 *
 */
public class XDouble {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Double)"123"
   *
   * Returns the value of the string as an double
   */
  public static Double operator:()(String m) {
    if (m == null) {
      return null;
    } else {
      return new Double(m);
    }
  }

  /**
   * Allows conversion "(Double)1"
   *
   */
  public static Double operator:()(double i) {
    return new Double(i);
  }

  /**
   * Allows conversion "(Double)1"
   *
   */
  public static Double operator:()(Number i) {
    if (i == null) {
      return null;
    } else {
      return new Double(i.doubleValue());
    }
  }

  /**
   * Allows conversion "(Integer)1.0"
   *
   */
  public static double operator:()(Number i) {
    return i.doubleValue();
  }

  /**
   * Allows conversion "(double)fixed"
   */
  public static double operator:()(fixed f) {
    return f.doubleValue();
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from double to Double
   */
  public static Double operator:=(double i) {
    return new Double(i);
  }

  /**
   * Assignments from double to Double
   */
  public static Double operator:=(Number i) {
    return new Double(i.doubleValue());
  }

//  /**
//   * Assignment form Double to double
//   */
//  public static double operator:=(Number i) {
//    return i.doubleValue();
//  }

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
  //public static Double operator:*++(Double l) {
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
  //public static Double operator:++*(Double l) {
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
  //public static Double operator:*--(Double l) {
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
  //public static Double operator:--*(Double l) {
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
  public static boolean operator:==(Double l, Number r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.equals(r);
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Double l, Number r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return !l.equals(r);
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
  public static boolean operator:>(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() > r.intValue();
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
  public static boolean operator:>=(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() >= r.intValue();
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
  public static boolean operator:<(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() < r.intValue();
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
  public static boolean operator:<=(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r.intValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Number l, Double r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.equals(r);
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Number l, Double r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return !l.equals(r);
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
  public static boolean operator:>(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() > r.intValue();
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
  public static boolean operator:>=(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() >= r.intValue();
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
  public static boolean operator:<(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() < r.intValue();
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
  public static boolean operator:<=(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r.intValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Double l, Double r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.equals(r);
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Double l, Double r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return !l.equals(r);
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
  public static boolean operator:>(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() > r.intValue();
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
  public static boolean operator:>=(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() >= r.intValue();
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
  public static boolean operator:<(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() < r.intValue();
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
  public static boolean operator:<=(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r.intValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(double l, Double r) {
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
  public static boolean operator:!=(double l, Double r) {
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
  public static boolean operator:>(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
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
  public static boolean operator:>=(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
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
  public static boolean operator:<(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
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
  public static boolean operator:<=(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
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
  public static boolean operator:==(Double l, double r) {
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
  public static boolean operator:!=(Double l, double r) {
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
  public static boolean operator:>(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
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
  public static boolean operator:>=(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
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
  public static boolean operator:<(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
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
  public static boolean operator:<=(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
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
  public static Double operator:+(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() + r);
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
  public static Double operator:+(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() + r.doubleValue());
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
  public static Double operator:+(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l + r.doubleValue());
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
  public static Double operator:+(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() + r.doubleValue());
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
  public static Double operator:+(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() + r.doubleValue());
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
  public static Double operator:+(Number l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() + r);
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
  public static Double operator:+(double l, Number r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l + r.doubleValue());
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
  public static Double operator:-(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() - r);
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
  public static Double operator:-(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() - r.doubleValue());
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
  public static Double operator:-(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l - r.doubleValue());
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
  public static Double operator:-(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() - r.doubleValue());
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
  public static Double operator:-(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() - r.doubleValue());
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
  public static Double operator:-(Number l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() - r);
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
  public static Double operator:-(double l, Number r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l - r.doubleValue());
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
  public static Double operator:*(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() * r);
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
  public static Double operator:*(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() * r.doubleValue());
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
  public static Double operator:*(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l * r.doubleValue());
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
  public static Double operator:*(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() * r.doubleValue());
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
  public static Double operator:*(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() * r.doubleValue());
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
  public static Double operator:*(Number l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() * r);
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
  public static Double operator:*(double l, Number r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l * r.doubleValue());
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
  public static Double operator:/(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() / r);
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
  public static Double operator:/(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() / r.doubleValue());
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
  public static Double operator:/(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l / r.doubleValue());
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
  public static Double operator:/(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() / r.doubleValue());
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
  public static Double operator:/(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() / r.doubleValue());
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
  public static Double operator:/(Number l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() / r);
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
  public static Double operator:/(double l, Number r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l / r.doubleValue());
  }

  // ----------------------------------------------------------------------
  // MODULO OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "%"
   *
   * This modulo r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Double operator:%(Double l, double r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() % r);
  }

  /**
   * Operator "%"
   *
   * This modulo r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Double operator:%(Double l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() % r.doubleValue());
  }

  /**
   * Operator "%"
   *
   * This modulo l doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Double operator:%(double l, Double r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Double(l % r.doubleValue());
  }

  /**
   * Operator "%"
   *
   * This modulo r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Double operator:%(Double l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() % r.doubleValue());
  }

  /**
   * Operator "%"
   *
   * This modulo r doubles to the double
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Double operator:%(Number l, Double r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Double(l.doubleValue() % r.doubleValue());
  }
}
