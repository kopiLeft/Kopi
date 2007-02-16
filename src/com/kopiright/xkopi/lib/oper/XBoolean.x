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

/**
 * This class implements all overloaded operators for booleans and some utilities
 */
public class XBoolean {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Boolean)"true"
   *
   * Returns the value of the string as an boolean
   */
  public static Boolean operator:()(String m) {
    if (m == null) {
      return null;
    } else {
      return new Boolean(m);
    }
  }

  /**
   * Allows conversion "(Boolean)true"
   */
  public static Boolean operator:()(boolean b) {
    return new Boolean(b);
  }

  /**
   * Allows conversion "(Boolean)true"
   */
  public static boolean operator:()(Boolean b) {
    return b.booleanValue();
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from boolean to Boolean
   */
  public static Boolean operator:=(boolean i) {
    return new Boolean(i);
  }

//  /**
//   * Assignment form Boolean to boolean
//   */
//  public static boolean operator:=(Boolean i) {
//    return i.booleanValue();
//  }

  // ----------------------------------------------------------------------
  // UNARY OPERATORS  BOOLEANS ARE INMUTABLE => the compiler will
  //                  transform i++ into (i += 1)
  // ----------------------------------------------------------------------

  /*
   * Operator "*++"
   *
   * This add a boolean to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  //public static Boolean operator:*++(Boolean l) {
  //}

  /*
   * Operator "++*"
   *
   * This add a boolean to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  //public static Boolean operator:++*(Boolean l) {
  //}

  /*
   * Operator "*--"
   *
   * This substract a boolean to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  //public static Boolean operator:*--(Boolean l) {
  //}

  /*
   * Operator "--*"
   *
   * This substract a boolean to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  //public static Boolean operator:--*(Boolean l) {
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
  public static boolean operator:==(Boolean l, Boolean r) {
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
  public static boolean operator:!=(Boolean l, Boolean r) {
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
  public static boolean operator:==(boolean l, Boolean r) {
    if (r == null) {
      return false;
    } else {
      return r.booleanValue() == l;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(boolean l, Boolean r) {
    if (r == null) {
      return true;
    } else {
      return r.booleanValue() != l;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Boolean l, boolean r) {
    if (l == null) {
      return false;
    } else {
      return l.booleanValue() == r;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Boolean l, boolean r) {
    if (l == null) {
      return true;
    } else {
      return l.booleanValue() != r;
    }
  }

  // ----------------------------------------------------------------------
  // BAND OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "&"
   *
   * This band r booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l & r
   */
  public static Boolean operator:&(Boolean l, boolean r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Boolean(l.booleanValue() & r);
  }

  /**
   * Operator "&"
   *
   * This band r booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l & r
   */
  public static Boolean operator:&(Boolean l, Boolean r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Boolean(l.booleanValue() & r.booleanValue());
  }

  /**
   * Operator "&"
   *
   * This band l booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l & r
   */
  public static Boolean operator:&(boolean l, Boolean r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Boolean(r.booleanValue() & l);
  }

  // ----------------------------------------------------------------------
  // BOR OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "|"
   *
   * This bor r booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l | r
   */
  public static Boolean operator:|(Boolean l, boolean r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Boolean(l.booleanValue() | r);
  }

  /**
   * Operator "|"
   *
   * This bor r booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l | r
   */
  public static Boolean operator:|(Boolean l, Boolean r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Boolean(l.booleanValue() | r.booleanValue());
  }

  /**
   * Operator "|"
   *
   * This bor l booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l | r
   */
  public static Boolean operator:|(boolean l, Boolean r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Boolean(r.booleanValue() | l);
  }

  // ----------------------------------------------------------------------
  // BXOR OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "^"
   *
   * This bxor r booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l ^ r
   */
  public static Boolean operator:^(Boolean l, boolean r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Boolean(l.booleanValue() ^ r);
  }

  /**
   * Operator "^"
   *
   * This bxor r booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l ^ r
   */
  public static Boolean operator:^(Boolean l, Boolean r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Boolean(l.booleanValue() ^ r.booleanValue());
  }

  /**
   * Operator "^"
   *
   * This bxor l booleans to the boolean
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l ^ r
   */
  public static Boolean operator:^(boolean l, Boolean r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Boolean(r.booleanValue() ^ l);
  }
}
