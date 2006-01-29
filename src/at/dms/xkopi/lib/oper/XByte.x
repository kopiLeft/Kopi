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
 * $Id: XByte.x,v 1.3 2002/08/21 14:41:42 lackner Exp $
 */

package at.dms.xkopi.lib.oper;

/**
 * This class implements all overloaded operators for bytes and some utilities
 */
public class XByte {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Byte)"123"
   *
   * Returns the value of the string as an int
   */
  public static Byte operator:()(String m) {
    if (m == null) {
      return null;
    } else {
      return new Byte(m);
    }
  }

  /**
   * Allows conversion "(Byte)1"
   *
   */
  public static Byte operator:()(int i) {
    return new Byte((byte)(i));
  }

  /**
   * Allows conversion "(Byte)1.0"
   *
   */
  public static Byte operator:()(double i) {
    return new Byte((byte)i);
  }

 /**
   * Allows conversion "(Short)1.0"
   *
   */
  public static Byte operator:()(Number i) {
    if (i == null) {
      return null;
    } else {
      return new Byte(i.byteValue());
    }
  }

 /**
   * Allows conversion "(Short)1.0"
   *
   */
  public static byte operator:()(Byte i) {
    return i.byteValue();
  }
  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from byte to Byte
   */
  public static Byte operator:=(byte i) {
    return new Byte((byte)(i));
  }

//  /**
//   * Assignment form Byte to byte
//   */
//  public static byte operator:=(Byte i) {
//    return i.byteValue();
//  }

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
  //public static Byte operator:*++(Byte l) {
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
  //public static Byte operator:++*(Byte l) {
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
  //public static Byte operator:*--(Byte l) {
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
  //public static Byte operator:--*(Byte l) {
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
  public static boolean operator:==(Byte l, Number r) {
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
  public static boolean operator:!=(Byte l, Number r) {
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
  public static boolean operator:>(Byte l, Number r) {
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
  public static boolean operator:>=(Byte l, Number r) {
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
  public static boolean operator:<(Byte l, Number r) {
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
  public static boolean operator:<=(Byte l, Number r) {
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
  public static boolean operator:==(Number l, Byte r) {
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
  public static boolean operator:!=(Number l, Byte r) {
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
  public static boolean operator:>(Number l, Byte r) {
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
  public static boolean operator:>=(Number l, Byte r) {
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
  public static boolean operator:<(Number l, Byte r) {
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
  public static boolean operator:<=(Number l, Byte r) {
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
  public static boolean operator:==(Byte l, Byte r) {
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
  public static boolean operator:!=(Byte l, Byte r) {
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
  public static boolean operator:>(Byte l, Byte r) {
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
  public static boolean operator:>=(Byte l, Byte r) {
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
  public static boolean operator:<(Byte l, Byte r) {
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
  public static boolean operator:<=(Byte l, Byte r) {
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
  public static boolean operator:>(byte l, Byte r) {
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
  public static boolean operator:>=(byte l, Byte r) {
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
  public static boolean operator:<(byte l, Byte r) {
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
  public static boolean operator:<=(byte l, Byte r) {
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
  public static boolean operator:==(Byte l, byte r) {
    if (l == null) {
      return false;
    } else {
      return l.byteValue() == r;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Byte l, byte r) {
    if (l == null) {
      return true;
    } else {
      return l.byteValue() != r;
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
  public static boolean operator:>(Byte l, byte r) {
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
  public static boolean operator:>=(Byte l, byte r) {
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
  public static boolean operator:<(Byte l, byte r) {
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
  public static boolean operator:<=(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r;
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(byte l, Byte r) {
    if (r == null) {
      return false;
    } else {
      return r.byteValue() == l;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(byte l, Byte r) {
    if (r == null) {
      return true;
    } else {
      return r.byteValue() != l;
    }
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
  public static Byte operator:+(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() + r));
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
  public static Byte operator:+(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() + r.intValue()));
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
  public static Byte operator:+(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() + l));
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
  public static Byte operator:+(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() + r.intValue()));
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
  public static Byte operator:+(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() + r.intValue()));
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
  public static Byte operator:-(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() - r));
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
  public static Byte operator:-(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() - r.intValue()));
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
  public static Byte operator:-(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() - l));
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
  public static Byte operator:-(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() - r.intValue()));
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
  public static Byte operator:-(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() - r.intValue()));
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
  public static Byte operator:*(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() * r));
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
  public static Byte operator:*(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() * r.intValue()));
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
  public static Byte operator:*(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() * l));
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
  public static Byte operator:*(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() * r.intValue()));
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
  public static Byte operator:*(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() * r.intValue()));
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
  public static Byte operator:/(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() / r));
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
  public static Byte operator:/(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() / r.intValue()));
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
  public static Byte operator:/(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() / l));
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
  public static Byte operator:/(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() / r.intValue()));
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
  public static Byte operator:/(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() / r.intValue()));
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
  public static Byte operator:%(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() % r));
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
  public static Byte operator:%(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() % r.intValue()));
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
  public static Byte operator:%(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() % l));
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
  public static Byte operator:%(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() % r.intValue()));
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
  public static Byte operator:%(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() % r.intValue()));
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
  public static Byte operator:>>(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >> r.intValue()));
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
  public static Byte operator:>>>(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >>> r.intValue()));
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
  public static Byte operator:<<(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() << r.intValue()));
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
  public static Byte operator:>>(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >> r.intValue()));
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
  public static Byte operator:>>>(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >>> r.intValue()));
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
  public static Byte operator:<<(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() << r.intValue()));
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
  public static Byte operator:>>(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >> r.intValue()));
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
  public static Byte operator:>>>(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >>> r.intValue()));
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
  public static Byte operator:<<(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() << r.intValue()));
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
  public static Byte operator:>>(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l >> r.intValue()));
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
  public static Byte operator:>>>(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l >>> r.intValue()));
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
  public static Byte operator:<<(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l << r.intValue()));
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
  public static Byte operator:>>(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >> r));
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
  public static Byte operator:>>>(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() >>> r));
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
  public static Byte operator:<<(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() << r));
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
  public static Byte operator:&(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() & r));
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
  public static Byte operator:&(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() & r.intValue()));
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
  public static Byte operator:&(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() & l));
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
  public static Byte operator:&(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() & r.intValue()));
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
  public static Byte operator:&(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() & r.intValue()));
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
  public static Byte operator:|(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() | r));
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
  public static Byte operator:|(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() | r.intValue()));
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
  public static Byte operator:|(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() | l));
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
  public static Byte operator:|(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() | r.intValue()));
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
  public static Byte operator:|(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() | r.intValue()));
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
  public static Byte operator:^(Byte l, byte r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() ^ r));
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
  public static Byte operator:^(Byte l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() ^ r.intValue()));
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
  public static Byte operator:^(byte l, Byte r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(r.intValue() ^ l));
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
  public static Byte operator:^(Byte l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() ^ r.intValue()));
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
  public static Byte operator:^(Number l, Byte r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Byte((byte)(l.intValue() ^  r.intValue()));
  }
}
