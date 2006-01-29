/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
 * $Id: XInteger.x,v 1.2 2001/05/24 19:28:57 graf Exp $
 */

package com.kopiright.xkopi.lib.oper;

/**
 * This class implements all overloaded operators for integers and some utilities
 *
 */
public class XInteger {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Integer)"123"
   *
   * Returns the value of the string as an int
   */
  public static Integer operator:()(String m) {
    if (m == null) {
      return null;
    } else {
      return new Integer(m);
    }
  }

  /**
   * Allows conversion "(Integer)1"
   *
   */
  public static Integer operator:()(int i) {
    return new Integer(i);
  }

  /**
   * Allows conversion "(Integer)1.0"
   *
   */
  public static Integer operator:()(double i) {
    return new Integer((int)i);
  }

  /**
   * Allows conversion "(Integer)1.0"
   *
   */
  public static Integer operator:()(Number i) {
    if (i == null) {
      return null;
    } else {
      return new Integer(i.intValue());
    }
  }

  /**
   * Allows conversion "(Integer)1.0"
   *
   */
  public static int operator:()(Number i) {
    return i.intValue();
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from int to Integer
   */
  public static Integer operator:=(int i) {
    return new Integer(i);
  }

//  /**
//   * Assignment form Integer to int
//   */
//  public static int operator:=(Integer i) {
//    return i.intValue();
//  }
//
//  /**
//   * Assignment form Integer to int
//   */
//  public static int operator:=(Short i) {
//    return i.intValue();
//  }
//
//  /**
//   * Assignment form Integer to int
//   */
//  public static int operator:=(Byte i) {
//    return i.intValue();
//  }


  /**
   * Assignment form Integer to int
   */
  public static Integer operator:=(Short i) {
    return i == null ? null : new Integer(i.intValue());
  }

  /**
   * Assignment form Integer to int
   */
  public static Integer operator:=(Byte i) {
    return i == null ? null : new Integer(i.intValue());
  }

  // ----------------------------------------------------------------------
  // UNARY OPERATORS  INTEGERS ARE INMUTABLE => the compiler will
  //                  transform i++ into (i += 1)
  // ----------------------------------------------------------------------

  /*
   * Operator "*++"
   *
   * This add a integer to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  //public static Integer operator:*++(Integer l) {
  //}

  /*
   * Operator "++*"
   *
   * This add a integer to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  //public static Integer operator:++*(Integer l) {
  //}

  /*
   * Operator "*--"
   *
   * This substract a integer to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  //public static Integer operator:*--(Integer l) {
  //}

  /*
   * Operator "--*"
   *
   * This substract a integer to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  //public static Integer operator:--*(Integer l) {
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
  public static boolean operator:==(Integer l, Number r) {
    if (l == null && r == null) {
      return true;
    } else if (l == null || r == null) {
      return false;
    } else {
      return l.equals(r);
    }
  }

  /**
   * Operator "!="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l != r
   */
  public static boolean operator:!=(Integer l, Number r) {
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
  public static boolean operator:>(Integer l, Number r) {
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
  public static boolean operator:>=(Integer l, Number r) {
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
  public static boolean operator:<(Integer l, Number r) {
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
  public static boolean operator:<=(Integer l, Number r) {
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
  public static boolean operator:==(Number l, Integer r) {
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
  public static boolean operator:!=(Number l, Integer r) {
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
  public static boolean operator:>(Number l, Integer r) {
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
  public static boolean operator:>=(Number l, Integer r) {
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
  public static boolean operator:<(Number l, Integer r) {
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
  public static boolean operator:<=(Number l, Integer r) {
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
  public static boolean operator:==(Integer l, Integer r) {
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
  public static boolean operator:!=(Integer l, Integer r) {
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
  public static boolean operator:>(Integer l, Integer r) {
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
  public static boolean operator:>=(Integer l, Integer r) {
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
  public static boolean operator:<(Integer l, Integer r) {
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
  public static boolean operator:<=(Integer l, Integer r) {
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
  public static boolean operator:==(int l, Integer r) {
    if (r == null) {
      return false;
    } else {
      return l == r.intValue();
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(int l, Integer r) {
    if (r == null) {
      return true;
    } else {
      return l != r.intValue();
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
  public static boolean operator:>(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l > r.intValue();
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
  public static boolean operator:>=(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l >= r.intValue();
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
  public static boolean operator:<(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l < r.intValue();
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
  public static boolean operator:<=(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l <= r.intValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Integer l, int r) {
    if (l == null) {
      return false;
    } else {
      return l.intValue() == r;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Integer l, int r) {
    if (l == null) {
      return true;
    } else {
      return l.intValue() != r;
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
  public static boolean operator:>(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.intValue() > r;
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
  public static boolean operator:>=(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.intValue() >= r;
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
  public static boolean operator:<(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.intValue() < r;
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
  public static boolean operator:<=(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r;
  }

  // ----------------------------------------------------------------------
  // ADD OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "+"
   *
   * This add r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Integer operator:+(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() + r);
  }

  /**
   * Operator "+"
   *
   * This add r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Integer operator:+(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() + r.intValue());
  }

  /**
   * Operator "+"
   *
   * This add l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Integer operator:+(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() + l);
  }

  /**
   * Operator "+"
   *
   * This add r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Integer operator:+(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() + r.intValue());
  }

  /**
   * Operator "+"
   *
   * This add r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Integer operator:+(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() + r.intValue());
  }

  // ----------------------------------------------------------------------
  // MINUS OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "-"
   *
   * This minus r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() - r);
  }

  /**
   * Operator "-"
   *
   * This minus r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() - r.intValue());
  }

  /**
   * Operator "-"
   *
   * This minus l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() - l);
  }

  /**
   * Operator "-"
   *
   * This minus r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() - r.intValue());
  }

  /**
   * Operator "-"
   *
   * This minus r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Integer operator:-(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() - r.intValue());
  }

  // ----------------------------------------------------------------------
  // MULTIPLICATIVE OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "*"
   *
   * This multiplicative r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:*(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() * r);
  }

  /**
   * Operator "*"
   *
   * This multiplicative r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:*(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() * r.intValue());
  }

  /**
   * Operator "*"
   *
   * This multiplicative l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:*(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() * l);
  }

  /**
   * Operator "*"
   *
   * This multiplicative r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:*(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() * r.intValue());
  }

  /**
   * Operator "*"
   *
   * This multiplicative r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:*(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() * r.intValue());
  }

  // ----------------------------------------------------------------------
  // DIVIDE OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "/"
   *
   * This divide r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Integer operator:/(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() / r);
  }

  /**
   * Operator "/"
   *
   * This divide r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Integer operator:/(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() / r.intValue());
  }

  /**
   * Operator "/"
   *
   * This divide l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Integer operator:/(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() / l);
  }

  /**
   * Operator "/"
   *
   * This divide r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Integer operator:/(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() / r.intValue());
  }

  /**
   * Operator "/"
   *
   * This divide r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Integer operator:/(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() / r.intValue());
  }

  // ----------------------------------------------------------------------
  // MODULO OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "%"
   *
   * This modulo r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Integer operator:%(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() % r);
  }

  /**
   * Operator "%"
   *
   * This modulo r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Integer operator:%(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() % r.intValue());
  }

  /**
   * Operator "%"
   *
   * This modulo l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Integer operator:%(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() % l);
  }

  /**
   * Operator "%"
   *
   * This modulo r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l % r
   */
  public static Integer operator:%(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() % r.intValue());
  }

  /**
   * Operator "%"
   *
   * This modulo r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:%(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() % r.intValue());
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
  public static Integer operator:>>(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >> r.intValue());
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
  public static Integer operator:>>>(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >>> r.intValue());
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
  public static Integer operator:<<(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() << r.intValue());
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
  public static Integer operator:>>(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >> r.intValue());
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
  public static Integer operator:>>>(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >>> r.intValue());
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
  public static Integer operator:<<(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() << r.intValue());
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
  public static Integer operator:>>(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >> r.intValue());
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
  public static Integer operator:>>>(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >>> r.intValue());
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
  public static Integer operator:<<(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() << r.intValue());
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
  public static Integer operator:>>(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(l >> r.intValue());
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
  public static Integer operator:>>>(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(l >>> r.intValue());
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
  public static Integer operator:<<(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(l << r.intValue());
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
  public static Integer operator:>>(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >> r);
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
  public static Integer operator:>>>(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() >>> r);
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
  public static Integer operator:<<(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() << r);
  }

  // ----------------------------------------------------------------------
  // BAND OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "&"
   *
   * This band r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l & r
   */
  public static Integer operator:&(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() & r);
  }

  /**
   * Operator "&"
   *
   * This band r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l & r
   */
  public static Integer operator:&(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() & r.intValue());
  }

  /**
   * Operator "&"
   *
   * This band l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l & r
   */
  public static Integer operator:&(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() & l);
  }

  /**
   * Operator "&"
   *
   * This band r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l & r
   */
  public static Integer operator:&(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() & r.intValue());
  }

  /**
   * Operator "&"
   *
   * This band r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:&(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() & r.intValue());
  }

  // ----------------------------------------------------------------------
  // BOR OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "|"
   *
   * This bor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l | r
   */
  public static Integer operator:|(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() | r);
  }

  /**
   * Operator "|"
   *
   * This bor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l | r
   */
  public static Integer operator:|(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() | r.intValue());
  }

  /**
   * Operator "|"
   *
   * This bor l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l | r
   */
  public static Integer operator:|(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() | l);
  }

  /**
   * Operator "|"
   *
   * This bor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l | r
   */
  public static Integer operator:|(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() | r.intValue());
  }

  /**
   * Operator "|"
   *
   * This bor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:|(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() | r.intValue());
  }

  // ----------------------------------------------------------------------
  // BXOR OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "^"
   *
   * This bxor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l ^ r
   */
  public static Integer operator:^(Integer l, int r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() ^ r);
  }

  /**
   * Operator "^"
   *
   * This bxor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l ^ r
   */
  public static Integer operator:^(Integer l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() ^ r.intValue());
  }

  /**
   * Operator "^"
   *
   * This bxor l integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l ^ r
   */
  public static Integer operator:^(int l, Integer r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Integer(r.intValue() ^ l);
  }

  /**
   * Operator "^"
   *
   * This bxor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l ^ r
   */
  public static Integer operator:^(Integer l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() ^ r.intValue());
  }

  /**
   * Operator "^"
   *
   * This bxor r integers to the integer
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Integer operator:^(Number l, Integer r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Integer(l.intValue() ^  r.intValue());
  }
}
