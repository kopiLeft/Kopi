/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: VKUtils.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.xkopi.comp.xkjc.XStdType;
import at.dms.xkopi.lib.type.Fixed;

/**
 * Some utilities to generate java code
 */
public class VKUtils {

  /**
   * Creates a new array froom a vector of elements
   */
  public static JExpression createArray(TokenReference ref, CType type, JExpression[] elems) {
    return new JNewArrayExpression(ref,
				   type,
				   ARRAY_1,
				   new JArrayInitializer(ref, elems));
  }

  /**
   * Create an assignment statement
   */
  public static JExpressionStatement assign(TokenReference ref, String ident, JExpression val) {
    return new JExpressionStatement(ref,
				    new JAssignmentExpression(ref,
							      new JFieldAccessExpression(ref, ident),
							      val),
				    null);
  }

  /**
   * Create a new Fixed(good_left, good_right)
   */
  public static JExpression toExpression(TokenReference ref, Fixed val) {
    if (val == null) {
      return new JNullLiteral(ref);
    } else {
      //!!! change toSql !!! graf 010123
      return new JUnqualifiedInstanceCreation(ref,
				      XStdType.PFixed,
				      new JExpression[]{ new JStringLiteral(ref, val.toSql())});
    }
  }

  /**
   * Create a new Fixed(good_left, good_right)
   */
  public static JExpression toExpression(TokenReference ref, String val) {
    if (val == null) {
      return new JNullLiteral(ref);
    } else {
      return new JStringLiteral(ref, val.toString());
    }
  }

  /**
   * Create a new boolean literal
   */
  public static JExpression toExpression(TokenReference ref, boolean val) {
    return val ? trueLiteral(ref) : falseLiteral(ref);
  }

  /**
   * Create a new Fixed(good_left, good_right)
   */
  public static JExpression toExpression(TokenReference ref, int val) {
    return new JIntLiteral(ref, val);
  }

  /**
   * Create a new Fixed(good_left, good_right)
   */
  public static JExpression toExpression(TokenReference ref, float val) {
    return new JFloatLiteral(ref, val);
  }

  /**
   * Create a new Fixed(good_left, good_right)
   */
  public static JExpression nullLiteral(TokenReference ref) {
    return new JNullLiteral(ref);
  }

  /**
   * Create a new ordinal literal
   */
  public static JExpression zeroLiteral(TokenReference ref) {
    return toExpression(ref, 0);
  }

  /**
   * Create a new boolean literal
   */
  public static JExpression trueLiteral(TokenReference ref) {
    return new JBooleanLiteral(ref, true);
  }

  /**
   * Create a new boolean literal
   */
  public static JExpression falseLiteral(TokenReference ref) {
    return new JBooleanLiteral(ref, false);
  }

  // ----------------------------------------------------------------------
  // FIELD DECLARATION
  // ----------------------------------------------------------------------

  public static final JFieldDeclaration buildFieldDeclaration(TokenReference ref,
							      int modifiers,
							      CType type,
							      String name,
							      JExpression value) {
    JVariableDefinition	def = new JVariableDefinition(ref,
						      modifiers,
						      type,
						      name,
						      value);

    return new JFieldDeclaration(ref, def, null, null);
  }

  // ----------------------------------------------------------------------
  // METHOD CALL
  // ----------------------------------------------------------------------

  /**
   * Call a method
   */
  public static JMethodCallExpression call(TokenReference ref, JExpression prefix, String name, JExpression[] params) {
    return new JMethodCallExpression(ref, prefix, name, params);
  }

  /**
   * Call a method
   */
  public static JMethodCallExpression call(TokenReference ref, JExpression prefix, String name, JExpression param) {
    return call(ref, prefix, name, new JExpression[]{ param });
  }

  /**
   * Call a method
   */
  public static JMethodCallExpression call(TokenReference ref, JExpression prefix, String name) {
    return call(ref, prefix, name, JExpression.EMPTY);
  }

  /**
   * Call a method
   */
  public static JMethodCallExpression call(TokenReference ref, String name, JExpression[] params) {
    return call(ref, null, name, params);
  }

  /**
   * Call a method
   */
  public static JMethodCallExpression call(TokenReference ref, String name, JExpression param) {
    return call(ref, null, name, new JExpression[]{ param });
  }

  /**
   * Call a method
   */
  public static JMethodCallExpression call(TokenReference ref, String name) {
    return call(ref, null, name, JExpression.EMPTY);
  }

  // ----------------------------------------------------------------------
  // PUBLIC CONSTANTS
  // ----------------------------------------------------------------------

  public static final JExpression[]	ARRAY_1 = new JExpression[] {null};
  public static final CReferenceType[]	VECT_VException = new CReferenceType[] {CReferenceType.lookup(at.dms.vkopi.lib.visual.VException.class.getName().replace('.','/'))};
  public static final JFormalParameter[]	TRIGGER_PARAM = new JFormalParameter[] {
    new JFormalParameter(TokenReference.NO_REF,
			 JLocalVariable.DES_PARAMETER,
			 CStdType.Integer,
			 VKConstants.CMP_TRIG_PARAM,
			 true)};
  public static final CReferenceType[]	TRIGGER_EXCEPTION = new CReferenceType[] {CReferenceType.lookup(VKConstants.VKO_VEXCEPTION)};
  public static final CReferenceType[]	PROTECTED_TRIGGER_EXCEPTION = new CReferenceType[] {
    CReferenceType.lookup(VKConstants.VKO_SQLEXCEPTION),
    CReferenceType.lookup(VKConstants.VKO_VEXCEPTION)
  };
}
