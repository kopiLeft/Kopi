/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.SimpleStringBuffer;
import at.dms.util.base.InconsistencyException;

/**
 * This class represents class type in the type structure
 */
public class CArrayType extends CReferenceType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs an array type
   * @param	baseType	the base type of the array type
   * @param	arrayBound	the dimension of the array type
   */
  public CArrayType(CType baseType, int arrayBound) {
    super();

    type = TID_ARRAY;
    verify(baseType != null);
    verify(arrayBound > 0);

    if (baseType.isArrayType()) {
      this.arrayBound = arrayBound + ((CArrayType)baseType).arrayBound;
      this.baseType = ((CArrayType)baseType).baseType;
    } else {
      this.arrayBound = arrayBound;
      this.baseType = baseType;
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Transforms this type to a string
   * @return	the ksm form of this type
   */
  public String toString() {
    String	res = baseType.toString();
    for (int i = 0; i < arrayBound; i++) {
      res += "[]";
    }
    return res;
  }

  /**
   *
   */
  public String getQualifiedName() {
    return getSignature();
  }

  /**
   * Appends the VM signature of this type to the specified buffer.
   */
  public void appendSignature(SimpleStringBuffer buffer) {
    for (int i = 0; i < arrayBound; i++) {
      buffer.append('[');
    }
    baseType.appendSignature(buffer);
  }

  /**
   * @return	the size used in stack by value of this type
   */
  public int getSize() {
    return 1;
  }

  /**
   * Checks if a type is a reference type
   * @return	is it a type that accept null value ?
   */
  public boolean isReference() {
    return true;
  }

  /**
   * @return	is this type an array ?
   */
  public boolean isArrayType() {
    return true;
  }

  /**
   * @return	the base class of this type
   */
  public CType getBaseType() {
    verify(baseType != null);
    verify(!(baseType instanceof CArrayType));
    return baseType;
  }

  /**
   * Returns the type of the elements of an array of this type.
   */
  public CType getElementType() {
    verify(baseType != null);
    verify(!(baseType instanceof CArrayType));
    if (arrayBound == 1) {
      return baseType;
    } else {
      CArrayType        arrayType =  new CArrayType(baseType, arrayBound - 1);

      arrayType.setClass(getCClass());
      return arrayType;
    }
  }

  /**
   * @return	the number of array bracket of this type
   */
  public int getArrayBound() {
    return arrayBound;
  }

  public CType getErasure(CTypeContext context) throws UnpositionedError {
    if (baseType.isReference()) {
      return new CArrayType(baseType.getErasure(context), arrayBound).checkType(context);
    } else {
      return this;
    }
  }

  /**
   * @return	true if this type is valid
   */
  public boolean checked() {
    return baseType.checked();
  }

  /**
   * equals
   */
  public boolean equals(CType other) {
    if (!other.isArrayType()) {
      return false;
    } else {
      CArrayType arr = (CArrayType)other;

      return baseType.equals(arr.baseType) && arrayBound == arr.arrayBound;
    }
  }
  public boolean equals(CType other, CReferenceType[] substitution) {
    if (!other.isArrayType()) {
      return false;
    } else {
      CArrayType arr = (CArrayType)other;

      return baseType.equals(arr.baseType, substitution) && arrayBound == arr.arrayBound;
    }
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * check that type is valid
   * necessary to resolve String into java/lang/String
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
    if (!isChecked()) { 
      if (arrayBound > 255) {
        // JVMS 4.10
        // The number of dimensions in an array is limited to 255 by the 
        // size of the dimensions opcode of the multianewarray instruction
        throw new UnpositionedError(KjcMessages.OVERSIZED_ARRAY_BOUND, baseType);
      }
      baseType = baseType.checkType(context);
      if (baseType.getTypeID() == TID_VOID) {
        throw new UnpositionedError(KjcMessages.VOID_ARRAY);
      }
      setClass(context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT).getCClass());
    }
    return this;
  }

  /**
   * Returns the class object associated with this type
   *
   * If this type was never checked (read from class files)
   * check it!
   *
   * @return the class object associated with this type
   */
  public CClass getCClass() {
    if (!isChecked()) {
      throw new InconsistencyException("type not checked");
    }

    return super.getCClass();
  }
  // ----------------------------------------------------------------------
  // BODY CHECKING
  // ----------------------------------------------------------------------

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isAssignableTo(CTypeContext context, CType dest, CReferenceType[] substitution) {
    //  JLS 5.2 Assignment Conversion
    //  If S is an array type SC[], that is, an array of components of type SC: 
    //        If T is a class type, then T must be Object, or a compile-time error occurs. 
    //        If T is an interface type, then a compile-time error occurs unless T is the type 
    //            java.io.Serializable or the type Cloneable, the only interfaces implemented by arrays. 
    //        If T is an array type TC[], that is, an array of components of type TC, then a compile-time
    //            error occurs unless one of the following is true: 
    //           TC and SC are the same primitive type. 
    //           TC and SC are both reference types and type SC is assignable to TC, as determined by a 
    //                    recursive application of these compile-time rules for assignability.
    if (! dest.isArrayType()) {
      try {
        return dest.equals(context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT))
          || dest.equals(context.getTypeFactory().createType(JAV_SERIALIZABLE, true).checkType(context))
          || dest.equals(context.getTypeFactory().createType(JAV_CLONEABLE, true).checkType(context));
      } catch (UnpositionedError e){
        throw new InconsistencyException("Failure while loading standard types.");
      }
    } else {
      if (arrayBound == ((CArrayType)dest).arrayBound) {
        return (((baseType.isPrimitive() && baseType.equals(((CArrayType)dest).baseType))
                 || ((!baseType.isPrimitive()) && baseType.isAssignableTo(context, ((CArrayType)dest).baseType, substitution))));
      } else {
	if (arrayBound < ((CArrayType)dest).arrayBound) {
	  // int[][] i = new int[]; ???
	  return false;
	}
	// May be the unusal case Object[] o = new String[][]...;
	return (((CArrayType)dest).baseType).equals(context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT));
      }
    } 
  }

  public boolean isAssignableTo(CTypeContext context, CType dest, boolean inst) {
    //  JLS 5.2 Assignment Conversion
    //  If S is an array type SC[], that is, an array of components of type SC: 
    //        If T is a class type, then T must be Object, or a compile-time error occurs. 
    //        If T is an interface type, then a compile-time error occurs unless T is the type 
    //            java.io.Serializable or the type Cloneable, the only interfaces implemented by arrays. 
    //        If T is an array type TC[], that is, an array of components of type TC, then a compile-time
    //            error occurs unless one of the following is true: 
    //           TC and SC are the same primitive type. 
    //           TC and SC are both reference types and type SC is assignable to TC, as determined by a 
    //                    recursive application of these compile-time rules for assignability.
    if (! dest.isArrayType()) {
      try {
        return dest.equals(context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT))
          || dest.equals(context.getTypeFactory().createType(JAV_SERIALIZABLE, true).checkType(context))
          || dest.equals(context.getTypeFactory().createType(JAV_CLONEABLE, true).checkType(context));
      } catch (UnpositionedError e){
        throw new InconsistencyException("Failure while loading standard types.");
      }
    } else {
      if (arrayBound == ((CArrayType)dest).arrayBound) {
        return (((baseType.isPrimitive() && baseType.equals(((CArrayType)dest).baseType))
                 || ((!baseType.isPrimitive()) && baseType.isAssignableTo(context, ((CArrayType)dest).baseType, inst))));
      } else {
	if (arrayBound < ((CArrayType)dest).arrayBound) {
	  // int[][] i = new int[]; ???
	  return false;
	}
	// May be the unusal case Object[] o = new String[][]...;
	return (((CArrayType)dest).baseType).equals(context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT));
      }
    }
  }

  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public boolean isCastableTo(CTypeContext context, CType dest) {
    // test for array first because array types are classes

    if (dest.isArrayType()) {
      // optimizes recursive call with base types (indexed types)

      CType		destBase = ((CArrayType)dest).baseType;
      int		destBound = ((CArrayType)dest).arrayBound;

      if (arrayBound == destBound) {
	if (baseType.isPrimitive()) {
	  return baseType == destBase;
	} else {
          return baseType.isCastableTo(context, destBase);
	}
      } else if (arrayBound < destBound) {
	return baseType.isCastableTo(context, new CArrayType(destBase, destBound - arrayBound));
      } else {
	// arrayBound > destBound
	return new CArrayType(baseType, arrayBound - destBound).isCastableTo(context, destBase);
      }
    } else if (dest.isClassType()) {
      if (dest.equals(CStdType.Object)) {
	// if T is a class type, then if T is not Object,
	// then a compile-time error occurs
	return true;
      } else if (dest.getCClass().getQualifiedName().equals(JAV_CLONEABLE)) {
	// if T is an interface type, then a compile-time error
	// occurs unless T is the interface type Cloneable or Serializable
	return true;
      } else if (dest.getCClass().getQualifiedName().equals(JAV_SERIALIZABLE)) {
	// if T is an interface type, then a compile-time error
	// occurs unless T is the interface type Cloneable or Serializable
	return true;
      } else {
	return false;
      }
    } else {
      return false;
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CType		baseType;
  private int		arrayBound;
}
