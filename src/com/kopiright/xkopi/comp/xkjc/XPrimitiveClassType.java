/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.CTypeContext;

/**
 * This class represents primitive class type
 */
public abstract class XPrimitiveClassType extends com.kopiright.kopi.comp.kjc.CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a class type
   * @param	qualifiedName	the class qualified name of the class
   */
  public XPrimitiveClassType(String qualifiedName) {
    super();
    this.qualifiedName = qualifiedName;
  }

//   static {
//     instance = new com.kopiright.kopi.comp.kjc.MethodSignatureParser() {
// 	// USE THE OPTIMIZED WAYU !!! $$$
// 	/**
// 	 * Parse a java type signature
// 	 *  Description : Attempts to parse the provided string as if it started with
// 	 *    the Java VM-standard signature for a type.
// 	 */
//       public CType parseSignature(TypeFactory factory, String signature, int from, int to) {
// 	CType	type = super.parseSignature(factory, signature, from, to);

// 	if (type instanceof CReferenceType) {
// 	  if (((CReferenceType)type).getQualifiedName() == com.kopiright.xkopi.lib.type.NotNullDate.class.getName().replace('.','/')) {
// 	    return XStdType.PDate;
// 	  } else if (((CReferenceType)type).getQualifiedName() == com.kopiright.xkopi.lib.type.NotNullFixed.class.getName().replace('.','/')) {
// 	    return XStdType.PFixed;
// 	  } else if (((CReferenceType)type).getQualifiedName() == com.kopiright.xkopi.lib.type.NotNullMonth.class.getName().replace('.','/')) {
// 	    return XStdType.PMonth;
// 	  } else if (((CReferenceType)type).getQualifiedName() == com.kopiright.xkopi.lib.type.NotNullTime.class.getName().replace('.','/')) {
// 	    return XStdType.PTime;
// 	  } else if (((CReferenceType)type).getQualifiedName() == com.kopiright.xkopi.lib.type.NotNullWeek.class.getName().replace('.','/')) {
// 	    return XStdType.PWeek;
// 	  }
// 	}

// 	return type;
//       }
//     };
//   }

  /**
   * check that type is valid
   * necessary to resolve String into java/lang/String
   * @param	context		the context (may be be null)
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
    if (!isChecked()) {
      setClass(context.getClassReader().loadClass(context.getTypeFactory(), qualifiedName));
    }
    return this;
  }
  /**
   * Check if a type is a class type
   * @return is it a subtype of ClassType ?
   */
  public boolean isPrimitive() {
    return true;
  }

  // ----------------------------------------------------------------------
  // ABSTRACT METHODS
  // ----------------------------------------------------------------------

  public abstract com.kopiright.kopi.comp.kjc.CReferenceType getNotNullableClass();

  private String qualifiedName;
}
