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

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents an exported member of a class (fields)
 */
public class XCursorField extends CSourceField {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a field export
   * @param	owner		the owner of this field
   * @param	modifiers	the modifiers on this field
   * @param	ident		the name of this field
   * @param	type		the type of this field
   */
  public XCursorField(CClass owner, int modifiers, String ident, CType type) {
    super(owner, modifiers, ident, type, false, false); // not synthetic
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @param	value		the value known at third pass
   */
  public void setValue(JLiteral value) {
    throw new InconsistencyException();
  }

  /**
   * @return	the value of initializer or null
   */
  public JExpression getValue() {
    throw new InconsistencyException();
  }

  // ----------------------------------------------------------------------
  // GENERATE ACCESS EXPRESSION
  // ----------------------------------------------------------------------

  public JExpression createAccessExpression(TokenReference ref,
					    JExpression prefix,
					    CExpressionContext context)
  {
    return createAccessExpression(ref, getType(), getPosition(), prefix, context);
  }

  public static JExpression createAccessExpression(TokenReference ref,
						   CType type,
						   int pos,
						   JExpression prefix,
						   CExpressionContext context)
  {
    // find method name
    String	name = null;
    boolean	nullable = false;
    boolean	needcast = false;
    boolean     kopiSerializabe = false;
    TypeFactory factory = context.getTypeFactory();

    switch (type.getTypeID()) {
    case TID_BOOLEAN:
      name = "Boolean";
      break;
    case TID_BYTE:
      name = "Byte";
      break;
    case TID_SHORT:
      name = "Short";
      break;
    case TID_INT:
      name = "Int";
      break;
    case TID_LONG:
      name = "Long";
      break;
    case TID_FLOAT:
      name = "Float";
      break;
    case TID_DOUBLE:
      name = "Double";
      break;
    case TID_CHAR:
      name = "Char";
      break;
    case TID_ARRAY:
      name = "ByteArray";
      break;
    case TID_NULL:
    case TID_CLASS:
      // check for primitive data
      nullable = true;
      if (type.equals(factory.createReferenceType(XTypeFactory.RFT_BOOLEAN))) {
	name = "Boolean";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_BYTE))) {
	name = "Byte";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_SHORT))) {
	name = "Short";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_INTEGER))) {
	name = "Int";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_FLOAT))) {
	name = "Float";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_DOUBLE))) {
	name = "Double";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_CHARACTER))) {
	name = "Char";
      }
      if (name != null) {
	break;
      }

      if (type.equals(factory.createReferenceType(TypeFactory.RFT_STRING))) {
	name = "String";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_DATE))) {
	name = "Date";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_MONTH))) {
	name = "Month";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_TIME))) {
	name = "Time";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_TIMESTAMP))) {
	name = "Timestamp";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_WEEK))) {
	name = "Week";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.RFT_FIXED))) {
	name = "Fixed";
      }
      if (name != null) {
	break;
      }

      nullable = false;
      if (type.equals(factory.createReferenceType(XTypeFactory.PRM_PDATE))) {
	name = "Date";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.PRM_PMONTH))) {
	name = "Month";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.PRM_PTIME))) {
	name = "Time";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.PRM_PTIMESTAMP))) {
	name = "Timestamp";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.PRM_PWEEK))) {
	name = "Week";
      } else if (type.equals(factory.createReferenceType(XTypeFactory.PRM_PFIXED))) {
	name = "Fixed";
      }
      if (name != null) {
	break;
      }

      nullable = false;
      if (type.getCClass().descendsFrom(factory.createReferenceType(XTypeFactory.RFT_KOPISERIALIZABLE).getCClass())) {
	kopiSerializabe = true;
	// !!! has kopiConstructor ?
	name = "ByteArray";
      } else {
	name = "SerializedObject";
	needcast = true;
      }
      break;
    default:
      throw new InconsistencyException("INTERNAL ERROR, TYPE UNKNOWN");
    }

    // !!! SPECIAL CASE FOR INPUT STREAM !!! ???

    JMethodCallExpression	expr;

    expr = new JMethodCallExpression(ref,
				     prefix,
				     "get" + (nullable ? "Nullable" : "") + name,
				     new JExpression[]{ new JIntLiteral(ref, pos + 1) });

    if (kopiSerializabe) {
      return buildKopiSerializableCall(ref, (CReferenceType)type, expr, prefix, pos);
    } else if (!needcast) {
      return expr;
    } else {
      return new JParenthesedExpression(ref, new JCastExpression(ref, expr, type));
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES
  // ----------------------------------------------------------------------

  /**
   * Construct the call to kopiSerializabe constructor
   */
  private static JExpression buildKopiSerializableCall(TokenReference ref,
						       CReferenceType type,
						       JExpression data,
						       JExpression prefix,
						       int pos)
  {
    JExpression			obj;
    JMethodCallExpression	expr;
    JExpression			tri;

    obj = new JUnqualifiedInstanceCreation(ref,
				   type,
				   new JExpression[]{ data, new JNullLiteral(ref) });
    expr = new JMethodCallExpression(ref,
				     prefix,
				     "isNull",
				     new JExpression[]{
				       new JIntLiteral(ref, pos + 1)
				     });
    tri = new JConditionalExpression(ref, expr, new JNullLiteral(ref), obj);
    return new JParenthesedExpression(ref, tri);
  }
}
