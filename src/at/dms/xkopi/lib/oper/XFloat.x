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
 * $Id: XFloat.x,v 1.2 2001/05/24 19:28:57 graf Exp $
 */

package at.dms.xkopi.lib.oper;

/**
 * This class implements all overloaded operators for floats and some utilities
 *
 */
public class XFloat {

  // ----------------------------------------------------------------------
  // SPECIALS OPERATORS
  // ----------------------------------------------------------------------

  /**
   * Allows conversion (Float)"123"
   *
   * Returns the value of the string as an float
   */
  public static Float operator:()(String m) {
    if (m == null) {
      return null;
    } else {
      return new Float(m);
    }
  }

  /**
   * Allows conversion "(Float)1"
   *
   */
  public static Float operator:()(float i) {
    return new Float(i);
  }

  /**
   * Allows conversion "(Float)1.0"
   *
   */
  public static Float operator:()(double i) {
    return new Float((float)i);
  }

  /**
   * Allows conversion "(Short)1.0"
   *
   */
  public static Float operator:()(Number i) {
    if (i == null) {
      return null;
    } else {
      return new Float(i.floatValue());
    }
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Assignments from float to Float
   */
  public static Float operator:=(float i) {
    return new Float(i);
  }

  /**
   * Assignment form Float to float
   */
  public static Float operator:=(Integer i) {
    return new Float(i.floatValue());
  }

  /**
   * Assignment form Float to float
   */
  public static Float operator:=(Short i) {
    return new Float(i.floatValue());
  }

  /**
   * Assignment form Float to float
   */
  public static Float operator:=(Byte i) {
    return new Float(i.floatValue());
  }

//  /**
//   * Assignment form Float to float
//   */
//  public static float operator:=(Float i) {
//    return i.floatValue();
//  }
//
//  /**
//   * Assignment form Float to float
//   */
//  public static float operator:=(Integer i) {
//    return i.floatValue();
//  }
//
//  /**
//   * Assignment form Float to float
//   */
//  public static float operator:=(Short i) {
//    return i.floatValue();
//  }
//
//  /**
//   * Assignment form Float to float
//   */
//  public static float operator:=(Byte i) {
//    return i.floatValue();
//  }

  // ----------------------------------------------------------------------
  // UNARY OPERATORS  FLOATS ARE INMUTABLE => the compiler will
  //                  transform i++ into (i += 1)
  // ----------------------------------------------------------------------

  /*
   * Operator "*++"
   *
   * This add a float to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l++
   */
  //public static Float operator:*++(Float l) {
  //}

  /*
   * Operator "++*"
   *
   * This add a float to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for ++l
   */
  //public static Float operator:++*(Float l) {
  //}

  /*
   * Operator "*--"
   *
   * This substract a float to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for l--
   */
  //public static Float operator:*--(Float l) {
  //}

  /*
   * Operator "--*"
   *
   * This substract a float to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the operand
   * @return	the value for --l
   */
  //public static Float operator:--*(Float l) {
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
  public static boolean operator:==(Float l, Number r) {
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
  public static boolean operator:!=(Float l, Number r) {
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
  public static boolean operator:>(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() > r.floatValue();
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
  public static boolean operator:>=(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() >= r.floatValue();
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
  public static boolean operator:<(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() < r.floatValue();
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
  public static boolean operator:<=(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() <= r.floatValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Number l, Float r) {
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
  public static boolean operator:!=(Number l, Float r) {
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
  public static boolean operator:>(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() > r.floatValue();
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
  public static boolean operator:>=(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() >= r.floatValue();
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
  public static boolean operator:<(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() < r.floatValue();
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
  public static boolean operator:<=(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() <= r.floatValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Float l, Float r) {
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
  public static boolean operator:!=(Float l, Float r) {
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
  public static boolean operator:>(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() > r.floatValue();
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
  public static boolean operator:>=(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() >= r.floatValue();
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
  public static boolean operator:<(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() < r.floatValue();
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
  public static boolean operator:<=(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return l.floatValue() <= r.floatValue();
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(float l, Float r) {
    if (r == null) {
      return false;
    } else {
      return l == r.floatValue();
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(float l, Float r) {
    if (r == null) {
      return true;
    } else {
      return l != r.floatValue();
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
  public static boolean operator:>(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l > r.floatValue();
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
  public static boolean operator:>=(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l >= r.floatValue();
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
  public static boolean operator:<(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l < r.floatValue();
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
  public static boolean operator:<=(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return l <= r.floatValue();
  }


  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:==(Float l, float r) {
    if (l == null) {
      return false;
    } else {
      return l.floatValue() == r;
    }
  }

  /**
   * Operator "=="
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l == r
   */
  public static boolean operator:!=(Float l, float r) {
    if (l == null) {
      return true;
    } else {
      return l.floatValue() != r;
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
  public static boolean operator:>(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.floatValue() > r;
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
  public static boolean operator:>=(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.floatValue() >= r;
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
  public static boolean operator:<(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.floatValue() < r;
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
  public static boolean operator:<=(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return l.floatValue() <= r;
  }

  // ----------------------------------------------------------------------
  // ADD OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "+"
   *
   * This add r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Float operator:+(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() + r);
  }

  /**
   * Operator "+"
   *
   * This add r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Float operator:+(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() + r.floatValue());
  }

  /**
   * Operator "+"
   *
   * This add l floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Float operator:+(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Float(l + r.floatValue());
  }

  /**
   * Operator "+"
   *
   * This add r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Float operator:+(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() + r.floatValue());
  }

  /**
   * Operator "+"
   *
   * This add r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l + r
   */
  public static Float operator:+(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() + r.floatValue());
  }

  // ----------------------------------------------------------------------
  // MINUS OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "-"
   *
   * This minus r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Float operator:-(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() - r);
  }

  /**
   * Operator "-"
   *
   * This minus r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Float operator:-(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() - r.floatValue());
  }

  /**
   * Operator "-"
   *
   * This minus l floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Float operator:-(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Float(l - r.floatValue());
  }

  /**
   * Operator "-"
   *
   * This minus r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Float operator:-(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() - r.floatValue());
  }

  /**
   * Operator "-"
   *
   * This minus r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l - r
   */
  public static Float operator:-(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() - r.floatValue());
  }

  // ----------------------------------------------------------------------
  // MULTIPLICATIVE OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "*"
   *
   * This multiplicative r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Float operator:*(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() * r);
  }

  /**
   * Operator "*"
   *
   * This multiplicative r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Float operator:*(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() * r.floatValue());
  }

  /**
   * Operator "*"
   *
   * This multiplicative l floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Float operator:*(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Float(l * r.floatValue());
  }

  /**
   * Operator "*"
   *
   * This multiplicative r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Float operator:*(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() * r.floatValue());
  }

  /**
   * Operator "*"
   *
   * This multiplicative r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l * r
   */
  public static Float operator:*(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() * r.floatValue());
  }

  // ----------------------------------------------------------------------
  // DIVIDE OPERATOR
  // ----------------------------------------------------------------------

  /**
   * Operator "/"
   *
   * This divide r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Float operator:/(Float l, float r) {
    if (l == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() / r);
  }

  /**
   * Operator "/"
   *
   * This divide r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Float operator:/(Float l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() / r.floatValue());
  }

  /**
   * Operator "/"
   *
   * This divide l floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Float operator:/(float l, Float r) {
    if (r == null) {
      throw new NullPointerException();
    }
    return new Float(l - r.floatValue());
  }

  /**
   * Operator "/"
   *
   * This divide r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Float operator:/(Float l, Number r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() / r.floatValue());
  }

  /**
   * Operator "/"
   *
   * This divide r floats to the float
   *
   * If one operand is null, this will throw a null pointer exception
   *
   * @param	l		the left operand
   * @param	r		the right operand
   * @return	the value for l / r
   */
  public static Float operator:/(Number l, Float r) {
    if (l == null || r == null) {
      throw new NullPointerException();
    }
    return new Float(l.floatValue() / r.floatValue());
  }
}
