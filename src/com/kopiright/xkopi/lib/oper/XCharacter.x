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

package com.kopiright.xkopi.lib.oper;

/**
 * This class implements all overloaded operators for chars and some utilities
 *
 */
public class XCharacter {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Character)"123"
   *
   * Returns the value of the string as an int
   */
  public static Character operator:()(char m) {
    return new Character(m);
  }

  /**
   * Allows conversion "(Character)1"
   *
   */
  public static Character operator:()(int i) {
    return new Character((char)(i));
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from char to Character
   */
  public static Character operator:=(char i) {
    return new Character((char)(i));
  }

//  /**
//   * Assignment form Character to char
//   */
//  public static char operator:=(Character i) {
//    return i.charValue();
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
  //public static Character operator:*++(Character l) {
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
  //public static Character operator:++*(Character l) {
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
  //public static Character operator:*--(Character l) {
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
  //public static Character operator:--*(Character l) {
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
  public static boolean operator:==(Character l, Number r) {
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
  public static boolean operator:!=(Character l, Number r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return !l.equals(r);
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Number l, Character r) {
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
  public static boolean operator:!=(Number l, Character r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return !l.equals(r);
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Character l, Character r) {
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
  public static boolean operator:!=(Character l, Character r) {
    if (l == null && r == null) {
      return false;
    } else if (l == null || r == null) {
      return true;
    } else {
      return !l.equals(r);
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(char l, Character r) {
    if (r == null) {
      return false;
    } else {
      return r.charValue() == l;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(char l, Character r) {
    if (r == null) {
      return true;
    } else {
      return r.charValue() != l;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Character l, char r) {
    if (l == null) {
      return false;
    } else {
      return l.charValue() == r;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Character l, char r) {
    if (l == null) {
      return true;
    } else {
      return l.charValue() != r;
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
  public static boolean operator:>(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() > r.intValue();
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
  public static boolean operator:>=(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() >= r.intValue();
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
  public static boolean operator:<(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() < r.intValue();
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
  public static boolean operator:<=(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() <= r.intValue();
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
  public static boolean operator:>(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() > r.charValue();
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
  public static boolean operator:>=(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() >= r.charValue();
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
  public static boolean operator:<(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() < r.charValue();
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
  public static boolean operator:<=(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.intValue() <= r.charValue();
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
  public static boolean operator:>(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() > r.charValue();
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
  public static boolean operator:>=(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() >= r.charValue();
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
  public static boolean operator:<(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() < r.charValue();
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
  public static boolean operator:<=(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.charValue() <= r.charValue();
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
  public static boolean operator:>(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l > r.charValue();
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
  public static boolean operator:>=(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l >= r.charValue();
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
  public static boolean operator:<(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l < r.charValue();
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
  public static boolean operator:<=(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l <= r.charValue();
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
  public static boolean operator:>(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.charValue() > r;
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
  public static boolean operator:>=(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.charValue() >= r;
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
  public static boolean operator:<(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.charValue() < r;
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
  public static boolean operator:<=(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.charValue() <= r;
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
  public static Character operator:+(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() + r));
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
  public static Character operator:+(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() + r.charValue()));
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
  public static Character operator:+(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() + l));
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
  public static Character operator:+(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() + r.intValue()));
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
  public static Character operator:+(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() + r.charValue()));
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
  public static Character operator:-(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() - r));
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
  public static Character operator:-(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() - r.charValue()));
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
  public static Character operator:-(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() - l));
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
  public static Character operator:-(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() - r.intValue()));
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
  public static Character operator:-(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() - r.charValue()));
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
  public static Character operator:*(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() * r));
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
  public static Character operator:*(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() * r.charValue()));
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
  public static Character operator:*(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() * l));
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
  public static Character operator:*(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() * r.intValue()));
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
  public static Character operator:*(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() * r.charValue()));
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
  public static Character operator:/(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() / r));
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
  public static Character operator:/(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() / r.charValue()));
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
  public static Character operator:/(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() / l));
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
  public static Character operator:/(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() / r.intValue()));
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
  public static Character operator:/(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() / r.charValue()));
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
  public static Character operator:%(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() % r));
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
  public static Character operator:%(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() % r.charValue()));
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
  public static Character operator:%(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() % l));
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
  public static Character operator:%(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() % r.intValue()));
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
  public static Character operator:%(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() % r.charValue()));
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
  public static Character operator:>>(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() >> r.intValue()));
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
  public static Character operator:>>>(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() >>> r.intValue()));
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
  public static Character operator:<<(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() << r.intValue()));
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
  public static Character operator:>>(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() >> r.charValue()));
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
  public static Character operator:>>>(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() >>> r.charValue()));
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
  public static Character operator:<<(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() << r.charValue()));
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
  public static Character operator:>>(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() >> r.charValue()));
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
  public static Character operator:>>>(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() >>> r.charValue()));
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
  public static Character operator:<<(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() << r.charValue()));
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
  public static Character operator:>>(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l >> r.charValue()));
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
  public static Character operator:>>>(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l >>> r.charValue()));
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
  public static Character operator:<<(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l << r.charValue()));
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
  public static Character operator:>>(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() >> r));
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
  public static Character operator:>>>(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() >>> r));
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
  public static Character operator:<<(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() << r));
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
  public static Character operator:&(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() & r));
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
  public static Character operator:&(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() & r.charValue()));
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
  public static Character operator:&(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() & l));
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
  public static Character operator:&(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() & r.intValue()));
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
  public static Character operator:&(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() & r.charValue()));
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
  public static Character operator:|(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() | r));
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
  public static Character operator:|(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() | r.charValue()));
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
  public static Character operator:|(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() | l));
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
  public static Character operator:|(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() | r.intValue()));
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
  public static Character operator:|(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() | r.charValue()));
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
  public static Character operator:^(Character l, char r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() ^ r));
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
  public static Character operator:^(Character l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() ^ r.charValue()));
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
  public static Character operator:^(char l, Character r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(r.charValue() ^ l));
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
  public static Character operator:^(Character l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.charValue() ^ r.intValue()));
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
  public static Character operator:^(Number l, Character r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Character((char)(l.intValue() ^  r.charValue()));
  }
}
