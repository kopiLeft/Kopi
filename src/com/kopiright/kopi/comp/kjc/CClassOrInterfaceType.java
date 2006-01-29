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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.SimpleStringBuffer;

/**
 * This class represents class type in the type structure
 */
public class CClassOrInterfaceType extends CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------


  /**
   * Construct a class type
   * @param	clazz		the class that will represent this type
   */
  public CClassOrInterfaceType(TokenReference ref, CClass clazz) {
    this(ref, clazz, CReferenceType.EMPTY_ARG);
  }

  /**
   * Construct a class type
   * @param	clazz		the class that will represent this type
   */
  public CClassOrInterfaceType(TokenReference ref, CClass clazz, CReferenceType[][] arguments) {
    super(clazz);

    this.arguments = arguments;
    this.checked = false;
    this.sourceRef = ref;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Appends the generic signature (attribute) of this type to the specified buffer.
   */
  protected void appendGenericSignature(SimpleStringBuffer buffer) {
    buffer.append('L');
    buffer.append(getQualifiedName());
    if (isGenericType() && arguments.length > 0) {
      buffer.append('<');
      for (int i = 0; i < arguments[arguments.length-1].length; i++) {
        arguments[arguments.length-1][i].appendGenericSignature(buffer);
      }
      buffer.append('>');
    }
    buffer.append(';');
  }

  public boolean isGenericType() {
    return getCClass().isGenericClass();
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * check that type is valid
   * necessary to resolve String into java/lang/String
   * @param	context		the context (may be be null)
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
    CClass              clazz = getCClass();

    if (arguments.length == 0) {
      arguments = EMPTY_ARG;
      if (clazz.isGenericClass()) {
	context.reportTrouble(new CWarning(sourceRef,
                                           KjcMessages.ERASURE_USED,
                                           getQualifiedName()));
        
      }
    }

    for (int k = arguments.length-1; k  >= 0 ; k--) {
      if (clazz == null && arguments[k] != null && arguments[k].length > 0) {
        // e.g. com.kopiright<T>.Main<S>
        throw new UnpositionedError(KjcMessages.UNUSED_TYPEARG, getCClass()); 
      } else if (clazz == null &&  (arguments[k] == null || arguments[k].length  == 0)) {
        continue;
      } else {
        if (arguments[k] == null) {
          arguments[k] = CReferenceType.EMPTY;
        }
        for (int i = 0; i < arguments[k].length; i++) {
          arguments[k][i] = (CReferenceType) arguments[k][i].checkType(context);
        }
        // is this type a correct instantiantion of this type?
        clazz.checkInstantiation(sourceRef, context, arguments[k]);
        clazz = clazz.getOwner();
      }
    }
    checked = true;
    return this;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean c) {
    checked = c;
  }
 // ----------------------------------------------------------------------
  // BODY CHECKING
  // ----------------------------------------------------------------------



  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isCastableTo(CTypeContext context, CType dest) {
    // test for array first because array types are classes

    if (getCClass().isInterface()) {
      if (! dest.isClassType()) {
	return false;
      } else if (dest.getCClass().isInterface()) {
	// if T is an interface type and if T and S contain methods
	// with the same signature but different return types,
	// then a compile-time error occurs.
	return getCClass().testAbstractMethodReturnTypes(context, dest.getCClass());
      } else if (dest.isArrayType()) {
        if (getQualifiedName() == JAV_CLONEABLE 
            || getQualifiedName() == JAV_SERIALIZABLE) {
          return true;
        } else {
          return false;
        }
      } else if (! dest.getCClass().isFinal()) {
	return true;
      } else {
	return dest.getCClass().descendsFrom(getCClass());
      }
    } else {
      // this is a class type
      if (dest.isArrayType()) {
	return equals(CStdType.Object);
      } else if (! dest.isClassType()) {
	return false;
      } else if (dest.getCClass().isInterface()) {
	if (! getCClass().isFinal()) {
	  return true;
	} else {
	  return getCClass().descendsFrom(dest.getCClass());
	}
      } else {
	return getCClass().descendsFrom(dest.getCClass())
	  || dest.getCClass().descendsFrom(getCClass());
      }
    }
  }

  public CReferenceType[] getArguments() {
    return (arguments == CReferenceType.EMPTY_ARG) ? CReferenceType.EMPTY : arguments[arguments.length-1];
  }

  public CReferenceType[][] getAllArguments() {
    return arguments;
  }


  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private boolean checked;
  private TokenReference        sourceRef;
}
