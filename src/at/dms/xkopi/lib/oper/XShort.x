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
 * $Id: XShort.x,v 1.2 2001/05/24 19:28:57 graf Exp $
 */

package at.dms.xkopi.lib.oper;

/**
 * This class implements all overloaded operators for shorts and some utilities
 *
 */
public class XShort {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Short)"123"
   *
   * Returns the value of the string as an int
   */
  public static Short operator:()(String m) {
    if (m == null) {
      return null;
    } else {
      return new Short(m);
    }
  }

  /**
   * Allows conversion "(Short)1"
   *
   */
  public static Short operator:()(int i) {
    return new Short((short)(i));
  }

  /**
   * Allows conversion "(Short)1.0"
   *
   */
  public static Short operator:()(double i) {
    return new Short((short)((int)i));
  }

  /**
   * Allows conversion "(Short)1.0"
   *
   */
  public static Short operator:()(Number i) {
    if (i == null) {
      return null;
    } else {
      return new Short(i.shortValue());
    }
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from short to Short
   */
  public static Short operator:=(short i) {
    return new Short((short)(i));
  }

//  /**
//   * Assignment form Short to short
//   */
//  public static short operator:=(Short i) {
//    return i.shortValue();
//  }
//
//  /**
//   * Assignment form Short to short
//   */
//  public static short operator:=(Byte i) {
//    return i.shortValue();
//  }

  /**
   * Assignment form Short to short
   */
  public static Short operator:=(Byte i) {
    return new Short(i.shortValue());
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
  //public static Short operator:*++(Short l) {
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
  //public static Short operator:++*(Short l) {
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
  //public static Short operator:*--(Short l) {
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
  //public static Short operator:--*(Short l) {
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
  public static boolean operator:==(Short l, Number r) {
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
  public static boolean operator:!=(Short l, Number r) {
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
  public static boolean operator:>(Short l, Number r) {
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
  public static boolean operator:>=(Short l, Number r) {
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
  public static boolean operator:<(Short l, Number r) {
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
  public static boolean operator:<=(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r.intValue();
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
  public static boolean operator:>(Number l, Short r) {
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
  public static boolean operator:>=(Number l, Short r) {
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
  public static boolean operator:<(Number l, Short r) {
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
  public static boolean operator:<=(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r.intValue();
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
  public static boolean operator:>(Short l, Short r) {
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
  public static boolean operator:>=(Short l, Short r) {
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
  public static boolean operator:<(Short l, Short r) {
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
  public static boolean operator:<=(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r.intValue();
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
  public static boolean operator:>(short l, Short r) {
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
  public static boolean operator:>=(short l, Short r) {
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
  public static boolean operator:<(short l, Short r) {
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
  public static boolean operator:<=(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l <= r.intValue();
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
  public static boolean operator:>(Short l, short r) {
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
  public static boolean operator:>=(Short l, short r) {
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
  public static boolean operator:<(Short l, short r) {
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
  public static boolean operator:<=(Short l, short r) {
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
  public static Short operator:+(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() + r));
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
  public static Short operator:+(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() + r.intValue()));
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
  public static Short operator:+(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() + l));
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
  public static Short operator:+(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() + r.intValue()));
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
  public static Short operator:+(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() + r.intValue()));
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
  public static Short operator:-(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() - r));
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
  public static Short operator:-(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() - r.intValue()));
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
  public static Short operator:-(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() - l));
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
  public static Short operator:-(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() - r.intValue()));
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
  public static Short operator:-(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() - r.intValue()));
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
  public static Short operator:*(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() * r));
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
  public static Short operator:*(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() * r.intValue()));
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
  public static Short operator:*(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() * l));
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
  public static Short operator:*(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() * r.intValue()));
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
  public static Short operator:*(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() * r.intValue()));
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
  public static Short operator:/(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() / r));
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
  public static Short operator:/(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() / r.intValue()));
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
  public static Short operator:/(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() / l));
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
  public static Short operator:/(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() / r.intValue()));
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
  public static Short operator:/(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() / r.intValue()));
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
  public static Short operator:%(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() % r));
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
  public static Short operator:%(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() % r.intValue()));
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
  public static Short operator:%(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() % l));
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
  public static Short operator:%(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() % r.intValue()));
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
  public static Short operator:%(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() % r.intValue()));
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
  public static Short operator:>>(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >> r.intValue()));
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
  public static Short operator:>>>(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >>> r.intValue()));
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
  public static Short operator:<<(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() << r.intValue()));
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
  public static Short operator:>>(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >> r.intValue()));
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
  public static Short operator:>>>(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >>> r.intValue()));
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
  public static Short operator:<<(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() << r.intValue()));
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
  public static Short operator:>>(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >> r.intValue()));
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
  public static Short operator:>>>(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >>> r.intValue()));
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
  public static Short operator:<<(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() << r.intValue()));
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
  public static Short operator:>>(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l >> r.intValue()));
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
  public static Short operator:>>>(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l >>> r.intValue()));
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
  public static Short operator:<<(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l << r.intValue()));
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
  public static Short operator:>>(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >> r));
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
  public static Short operator:>>>(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() >>> r));
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
  public static Short operator:<<(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() << r));
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
  public static Short operator:&(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() & r));
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
  public static Short operator:&(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() & r.intValue()));
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
  public static Short operator:&(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() & l));
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
  public static Short operator:&(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() & r.intValue()));
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
  public static Short operator:&(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() & r.intValue()));
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
  public static Short operator:|(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() | r));
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
  public static Short operator:|(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() | r.intValue()));
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
  public static Short operator:|(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() | l));
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
  public static Short operator:|(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() | r.intValue()));
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
  public static Short operator:|(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() | r.intValue()));
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
  public static Short operator:^(Short l, short r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() ^ r));
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
  public static Short operator:^(Short l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() ^ r.intValue()));
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
  public static Short operator:^(short l, Short r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(r.intValue() ^ l));
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
  public static Short operator:^(Short l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() ^ r.intValue()));
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
  public static Short operator:^(Number l, Short r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Short((short)(l.intValue() ^  r.intValue()));
  }
}
