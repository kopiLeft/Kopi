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

import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.SimpleStringBuffer;
import com.kopiright.util.base.Utils;

/**
 * Root for type hierarchy
 */
public abstract class CType extends Utils implements Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a type signature
   */
  protected CType(int type) {
    this.type = type;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * equals
   */
  public boolean equals(CType other) {
    return this == other;
  }
  public boolean equals(CType other, CReferenceType[] substitution) {
    return equals(other);
  }

  /**
   * Transforms this type to a string
   */
  public abstract String toString();

  /**
   * Returns the VM signature of this type.
   */
  public String getSignature() {
    SimpleStringBuffer	buffer;
    String		result;

    buffer = SimpleStringBuffer.request();
    appendSignature(buffer);
    result = buffer.toString();
    SimpleStringBuffer.release(buffer);
    return result;
  }

  /**
   * Returns the generic signature (attribute JSR 41) of this type.
   */
  public String getGenericSignature() {
    SimpleStringBuffer	buffer;
    String		result;

    buffer = SimpleStringBuffer.request();
    appendGenericSignature(buffer);
    result = buffer.toString();
    SimpleStringBuffer.release(buffer);
    return result;
  }

  /**
   * Appends the VM signature of this type to the specified buffer.
   */
  protected abstract void appendSignature(SimpleStringBuffer buffer);

  /**
   * Appends the generic signature (attribute) of this type to the specified buffer.
   */
  protected void appendGenericSignature(SimpleStringBuffer buffer) {
    appendSignature(buffer);
  }

  /**
   * Returns the stack size used by a value of this type.
   */
  public abstract int getSize();

  /**
   * Check if a type is a numeric type
   * @return is it a numeric type ?
   */
  public boolean isNumeric() {
    return false;
  }

  /**
   * Check if a type is an integer type
   * @return is it a integer type ?
   */
  public boolean isOrdinal() {
    return false;
  }

  /**
   * Check if a type is an integer type
   * @return is it a real number type ?
   */
  public boolean isFloatingPoint() {
    return false;
  }

  /**
   * Check if a type is a class type
   * @return is it a subtype of ClassType ?
   */
  public boolean isPrimitive() {
    return isNumeric() ; // in CBooleanType this method is overridden
  }

  /**
   * Check if a type is a class type
   * @return is it a subtype of ClassType ?
   */
  public boolean isReference() {
    return false;
  }

  /**
   * Check if a type is a class type
   * @return is it a subtype of ClassType ?
   */
  public boolean isClassType() {
    return false;
  }

  /**
   * Check if a type is a type variable
   * @return is it a Typevariable ?
   */
  public boolean isTypeVariable() {
    return false;
  }

  /**
   * Check if a type is a generic class type
   * @return	is it a generic type?
   */
  public boolean isGenericType() {
    return false;
  }

  public CType getErasure(CTypeContext context) throws UnpositionedError {
   return this;
  }
  public CReferenceType[] getArguments(){
    throw new InconsistencyException("no Arguments in this type!");
  }
  public CReferenceType[][] getAllArguments(){
    throw new InconsistencyException("no Arguments in this type!");
  }

  /**
   * @return is this type an array ?
   */
  public boolean isArrayType() {
    return false;
  }

  /**
   * @return true if this type is valid
   */
  public boolean checked() {
    return true;
  }

  /**
   * Returns the ID of this type
   */
  public final int getTypeID() {
    return type;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * check that type is valid
   * necessary to resolve String into java/lang/String
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public abstract CType checkType(CTypeContext context) throws UnpositionedError;

  /**
   * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public abstract boolean isCastableTo(CTypeContext context, CType dest);

  /**
   * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
   * @param	dest		the destination type
   * @return	true iff the conversion is valid
   */
  public abstract boolean isAssignableTo(CTypeContext context, CType dest);

  public boolean isAssignableTo(CTypeContext context, CType dest, CReferenceType[] substitution){
    return isAssignableTo(context, dest);
  }

  public boolean isAssignableTo(CTypeContext context, CType dest, boolean inst) {
    return isAssignableTo(context, dest);
  }


  /**
   * @return	true if this type corrspond to a checked exception
   */
  public boolean isCheckedException(CTypeContext context) {
    return false;
  }

  /**
   * @return the object class of this type
   */
  public CClass getCClass() {
    throw new InconsistencyException();
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns the opcode to load a local variable of this type.
   */
  public int getLoadOpcode() {
    switch (type) {
    case TID_NULL:
    case TID_CLASS:
    case TID_ARRAY:
      return opc_aload;
    case TID_DOUBLE:
      return opc_dload;
    case TID_FLOAT:
      return opc_fload;
    case TID_LONG:
      return opc_lload;
    case TID_BYTE:
    case TID_SHORT:
    case TID_CHAR:
    case TID_INT:
    case TID_BOOLEAN:
      return opc_iload;
    default:
      throw new InconsistencyException("INTERNAL ERROR: " + type);
    }
  }

  /**
   * Returns the opcode to store a local variable of this type.
   */
  public int getStoreOpcode() {
    switch (type) {
    case TID_NULL:
    case TID_CLASS:
    case TID_ARRAY:
      return opc_astore;
    case TID_DOUBLE:
      return opc_dstore;
    case TID_FLOAT:
      return opc_fstore;
    case TID_LONG:
      return opc_lstore;
    case TID_BYTE:
    case TID_SHORT:
    case TID_CHAR:
    case TID_INT:
    case TID_BOOLEAN:
      return opc_istore;
    default:
      throw new InconsistencyException("INTERNAL ERROR: " + type);
    }
  }

  /**
   * Returns the opcode used to load a value from an array.
   */
  public int getArrayLoadOpcode() {
    switch (type) {
    case TID_NULL:
    case TID_CLASS:
    case TID_ARRAY:
      return opc_aaload;
    case TID_INT:
      return opc_iaload;
    case TID_LONG:
      return opc_laload;
    case TID_FLOAT:
      return opc_faload;
    case TID_DOUBLE:
      return opc_daload;
    case TID_BYTE:
    case TID_BOOLEAN:
      return opc_baload;
    case TID_CHAR:
      return opc_caload;
    case TID_SHORT:
      return opc_saload;
    default:
      throw new InconsistencyException("INTERNAL ERROR: " + type);
    }
  }

  /**
   * Returns the opcode used to store a value into an array.
   */
  public int getArrayStoreOpcode() {
    switch (type) {
    case TID_NULL:
    case TID_CLASS:
    case TID_ARRAY:
      return opc_aastore;
    case TID_INT:
      return opc_iastore;
    case TID_LONG:
      return opc_lastore;
    case TID_FLOAT:
      return opc_fastore;
    case TID_DOUBLE:
      return opc_dastore;
    case TID_BYTE:
    case TID_BOOLEAN:
      return opc_bastore;
    case TID_CHAR:
      return opc_castore;
    case TID_SHORT:
      return opc_sastore;
    default:
      throw new InconsistencyException("INTERNAL ERROR: " + type);
    }
  }

  /**
   * Returns the opcode to return a value of this type.
   */
  public int getReturnOpcode() {
    switch (type) {
    case TID_VOID:
      return opc_return;
    case TID_NULL:
    case TID_CLASS:
    case TID_ARRAY:
      return opc_areturn;
    case TID_DOUBLE:
      return opc_dreturn;
    case TID_FLOAT:
      return opc_freturn;
    case TID_LONG:
      return opc_lreturn;
    case TID_BYTE:
    case TID_SHORT:
    case TID_CHAR:
    case TID_INT:
    case TID_BOOLEAN:
      return opc_ireturn;
    default:
      throw new InconsistencyException("INTERNAL ERROR: " + type);
    }
  }

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  /**
   * Parse a java type signature
   *  Description : Attempts to parse the provided string as if it started with
   *    the Java VM-standard signature for a type.
   */
//   public static CType parseSignature(TypeFactory factory, String signature) {
//     return instance.parseSignature(factory, signature);
//   }

  /**
   * Returns an array of types represented by the type signature
   * For methods, the return type is the last element of the array
   */
//   public static CType[] parseMethodSignature(TypeFactory factory, String sig) {
//     return instance.parseMethodSignature(factory, sig);
//   }

  /**
   * Generates the signature of a method.
   *
   * @param	returnType		the return type of the method
   * @param	parameters		the parameter types of the method
   */
//   public static String genMethodSignature(CType returnType, CType[] parameters) {
//     SimpleStringBuffer	buffer;
//     String		result;

//     buffer = SimpleStringBuffer.request();
//     buffer.append('(');
//     for (int i = 0; i < parameters.length; i++) {
//       parameters[i].appendSignature(buffer);
//     }
//     buffer.append(')');
//     returnType.appendSignature(buffer);

//     result = buffer.toString();
//     SimpleStringBuffer.release(buffer);
//     return result;
//   }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected int	type;

  //  protected static MethodSignatureParser instance = new MethodSignatureParser();
}
