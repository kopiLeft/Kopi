/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.xkopi.comp.xkjc;

import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.CTypeContext;
import com.kopiright.kopi.comp.kjc.CContext;
import com.kopiright.kopi.comp.kjc.CStdType;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.SimpleStringBuffer;

/**
 * Root for type converter
 * Actually this class work only for Sql primitive types
 */
public class XMutableType extends CType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a type signature
   */
  public XMutableType(CType original) {
    super(0);

    this.original = original;

    if (original == CStdType.Byte) {
      code = CODE_SBYTE;
    } else if (original == CStdType.Integer) {
      code = CODE_SINTEGER;
    } else if (original == CStdType.Long) {
      code = CODE_SLONG;
    } else if (original == CStdType.String) {
      code = CODE_SSTRING;
    } else if (original == CStdType.Boolean) {
      code = CODE_SBOOLEAN;
    } else if (original == CStdType.Double) {
      code = CODE_SDOUBLE;
    } else if (original == CStdType.Float) {
      code = CODE_SFLOAT;
    } else if (original == CStdType.Short) {
      code = CODE_SSHORT;
    } else if (original == CStdType.Char) {
      code = CODE_SCHAR;
    } else if (original.equals(XStdType.Boolean)) {
      code = CODE_XBOOLEAN;
    } else if (original.equals(XStdType.Byte)) {
      code = CODE_XBYTE;
    } else if (original.equals(XStdType.Short)) {
      code = CODE_XSHORT;
    } else if (original.equals(XStdType.Int)) {
      code = CODE_XINT;
    } else if (original.equals(XStdType.Long)) {
      code = CODE_XLONG;
    } else if (original.equals(XStdType.Fixed)) {
      code = CODE_XBIGDECIMAL;
    } else if (original.equals(XStdType.Float)) {
      code = CODE_XFLOAT;
    } else if (original.equals(XStdType.Double)) {
      code = CODE_XDOUBLE;
    } else if (original.equals(XStdType.Char)) {
      code = CODE_XCHAR;
    } else if (original.equals(XStdType.Date)) {
      code = CODE_XDATE;
    } else if (original.equals(XStdType.Month)) {
      code = CODE_XMONTH;
    } else if (original.equals(XStdType.Time)) {
      code = CODE_XTIME;
    } else if (original.equals(XStdType.Week)) {
      code = CODE_XWEEK;
    }
  }

  /**
   * check that type is valid
   * necessary to resolve String into java/lang/String
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
    return this;
  }

  // ----------------------------------------------------------------------
  // UNIMPLEMENTED ABSTRACT METHODS
  // ----------------------------------------------------------------------

  /**
   * Transforms this type to a string
   */
  public String toString() {
    return "XMUTABLETYPE:" + original;
  }

  /**
   * Transforms this type to a string
   */
  public String getSignature() {
    throw new InconsistencyException();
  }

  /**
   * Appends the VM signature of this type to the specified buffer.
   */
  public void appendSignature(SimpleStringBuffer buffer) {
    throw new InconsistencyException();
  }

  /**
   * Returns the stack size used by a value of this type.
   */
  public int getSize() {
    throw new InconsistencyException();
  }

  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isCastableTo(CTypeContext context, CType dest) {
    throw new InconsistencyException();
  }

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest, CReferenceType[] substitution) {
    return isAssignableTo(context, dest);
  }
  public boolean isAssignableTo(CTypeContext context, CType dest, boolean inst) {
    return isAssignableTo(context, dest);
  }
  public boolean isAssignableTo(CTypeContext context, CType dest) {
    if (original.isAssignableTo(context, dest)) {
      return true;
    } else {
      // graf 000521 !!! NEEDS A CLOSER LOOK
      // TRY OVERLOADING
      switch (code) {
      case CODE_SBYTE:
	if (!dest.isClassType()) {
	  return false;
	} else {
	  return dest.getCClass().descendsFrom(XStdType.Number.getCClass());
	}

      case CODE_SLONG:
	if (!dest.isClassType()) {
	  return false;
	} else {
	  return dest.getCClass().descendsFrom(XStdType.Number.getCClass()) &&
	    !dest.equals(XStdType.Byte) &&
	    !dest.equals(XStdType.Short) &&
	    !dest.equals(XStdType.Int);
	}

      case CODE_SINTEGER:
	if (!dest.isClassType()) {
	  return false;
	} else {
	  return dest.getCClass().descendsFrom(XStdType.Number.getCClass()) &&
	    !dest.equals(XStdType.Byte) && !dest.equals(XStdType.Short);
	}

      case CODE_SBOOLEAN:
	return dest.equals(XStdType.Boolean);

      case CODE_SDOUBLE:
	if (!dest.isClassType()) {
	  return false;
	} else {
	  return dest.equals(XStdType.Double) ||
	    dest.equals(XStdType.Fixed);
	}

      case CODE_SFLOAT:
	if (!dest.isClassType()) {
	  return false;
	} else {
	  return dest.equals(XStdType.Float) ||
	    dest.equals(XStdType.Double) ||
	    dest.equals(XStdType.Fixed);
	}

      case CODE_SSHORT:
	if (!dest.isClassType()) {
	  return false;
	} else {
	  return dest.getCClass().descendsFrom(XStdType.Number.getCClass()) &&
	    !dest.equals(XStdType.Byte);
	}

      case CODE_SCHAR:
	return dest.equals(XStdType.Char);

      case CODE_XBOOLEAN:
	return false;

      case CODE_XBYTE:
	return dest == XStdType.Short || dest == XStdType.Int || dest == XStdType.Long;

      case CODE_XSHORT:
	return dest == XStdType.Int || dest == XStdType.Long;

      case CODE_XINT:
	return false;

      case CODE_XLONG:
	return false;

      case CODE_XBIGDECIMAL:
	return false;

      case CODE_XFLOAT:
	return false;

      case CODE_XDOUBLE:
	return false;

      case CODE_XCHAR:
	return false;

      case CODE_XDATE:
      case CODE_XMONTH:
      case CODE_XTIME:
      case CODE_XWEEK:
      default:
	return false; // can't overload
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int	CODE_SBYTE		= 0;
  private static final int	CODE_SINTEGER		= 1;
  private static final int	CODE_SLONG		= 2;
  private static final int	CODE_SSTRING		= 3;
  private static final int	CODE_SBOOLEAN		= 4;
  private static final int	CODE_SDOUBLE		= 5;
  private static final int	CODE_SFLOAT		= 6;
  private static final int	CODE_SSHORT		= 7;
  private static final int	CODE_SCHAR		= 8;
  private static final int	CODE_XBOOLEAN		= 9;
  private static final int	CODE_XBYTE		= 10;
  private static final int	CODE_XSHORT		= 11;
  private static final int	CODE_XINT		= 12;
  private static final int	CODE_XLONG		= 13;
  private static final int	CODE_XBIGDECIMAL	= 14;
  private static final int	CODE_XFLOAT		= 15;
  private static final int	CODE_XDOUBLE		= 16;
  private static final int	CODE_XCHAR		= 17;
  private static final int	CODE_XDATE		= 18;
  private static final int	CODE_XMONTH		= 19;
  private static final int	CODE_XTIME		= 20;
  private static final int	CODE_XWEEK		= 21;

  private CType		original;
  private int		code;
}
