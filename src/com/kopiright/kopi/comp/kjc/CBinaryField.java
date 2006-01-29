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

import com.kopiright.bytecode.classfile.FieldInfo;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;

import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents loaded (compiled) class fields.
 */
public class CBinaryField extends CField {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a field export
   * @param	owner		the owner of this field
   * @param	fieldInfo	a field info from a class file
   */
  public CBinaryField(SignatureParser sigParser, 
                      TypeFactory factory, 
                      CClass owner, 
                      FieldInfo fieldInfo) {
    super(owner,
	  fieldInfo.getModifiers(),
	  fieldInfo.getName().intern(),
	  sigParser.parseSignature(factory, fieldInfo.getSignature()),
	  fieldInfo.isDeprecated(),
          fieldInfo.isSynthetic());

    if (isFinal() && isStatic()) {
      Object		value = fieldInfo.getConstantValue();

      if (value != null) {
	setValue(createLiteral(factory, getType(), value));
      }
    }
  }

  public void checkTypes(CBinaryTypeContext context) throws UnpositionedError {
    setType(getType().checkType(context));
  }
  /*
   * Returns a literal representing the constant value.
   */
  private static JLiteral createLiteral(TypeFactory factory, CType type, Object value) {
    switch (type.getTypeID()) {
    case TID_BYTE:
      return new JByteLiteral(TokenReference.NO_REF, (byte)((Integer)value).intValue());
    case TID_SHORT:
      return new JShortLiteral(TokenReference.NO_REF, (short)((Integer)value).intValue());
    case TID_CHAR:
      return new JCharLiteral(TokenReference.NO_REF, (char)((Integer)value).intValue());
    case TID_INT:
      return new JIntLiteral(TokenReference.NO_REF, ((Integer)value).intValue());
    case TID_LONG:
      return new JLongLiteral(TokenReference.NO_REF, ((Long)value).longValue());
    case TID_FLOAT:
      return new JFloatLiteral(TokenReference.NO_REF, ((Float)value).floatValue());
    case TID_DOUBLE:
      return new JDoubleLiteral(TokenReference.NO_REF, ((Double)value).doubleValue());
    case TID_CLASS:
      if (! type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
	throw new InconsistencyException("bad type " + type + "(literal: " + value.getClass() + ")");
      }
      return new JStringLiteral(TokenReference.NO_REF, (String)value);
    case TID_BOOLEAN:
      return new JBooleanLiteral(TokenReference.NO_REF, ((Integer)value).intValue() != 0);
    default:
      throw new InconsistencyException("bad type " + type + "(literal: " + value.getClass() + ")");
    }
  }
}
