/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.kopi.comp.kjc;

import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.PositionedError;

/**
 * Factory for standard Java Types
 */
public interface TypeFactory {

  // ----------------------------------------------------------------------
  // SPECIAL TYPES
  // ----------------------------------------------------------------------

  CVoidType getVoidType();
  CNullType getNullType();

  // ----------------------------------------------------------------------
  // PRIMITIVE TYPES
  // ----------------------------------------------------------------------

  /**
   * Creates a primitive type
   *
   * @param     typeID integer describing the primitive type (see the PRM_ members)
   * @return    an unchecked type
   */
  CPrimitiveType getPrimitiveType(int typeID);

  // ----------------------------------------------------------------------
  // CONSTANTS FOR PRIMITIVE TYPES
  // ----------------------------------------------------------------------

  int PRM_BOOLEAN               = 0;
  int PRM_BYTE                  = 1;
  int PRM_CHAR                  = 2;
  int PRM_DOUBLE                = 3;
  int PRM_FLOAT                 = 4;
  int PRM_INT                   = 5;
  int PRM_LONG                  = 6;
  int PRM_SHORT                 = 7;

  // ----------------------------------------------------------------------
  // STANDARD JAVA TYPES
  // ----------------------------------------------------------------------

  int RFT_OBJECT                = 8;
  int RFT_CLASS                 = 9;
  int RFT_STRING                = 10;

  int RFT_THROWABLE             = 11;
  int RFT_EXCEPTION             = 12;
  int RFT_ERROR                 = 13;
  int RFT_RUNTIMEEXCEPTION      = 14;

  int RFT_KOPIRUNTIME           = 16;

  int RFT_ENUM                 = 17;

  int MAX_TYPE_ID     = RFT_ENUM;

  // ----------------------------------------------------------------------
  // Create Types for classes
  // ----------------------------------------------------------------------

  /**
   * Creates a reference type. Only used to create standard types
   * like java.lang.Object
   *
   * @param     typeShortcut integer describing the type
   * @return    a checked type
   */
  CReferenceType createReferenceType(int typeShortcut);

  /**
   * Creates a type
   *
   * @param     name is qualified or unqualified name of the type
   * @param     binary is true if this type is load from a binary class
   * @return    an unchecked type
   */
  CReferenceType createType(String name, boolean binary);
  CReferenceType createType(TokenReference sourcePosition, String name, boolean binary);

  /**
   * Creates a (generic) type
   *
   * @param     name is qualified or unqualified name of the type
   * @param     arguments type arguments if this type is generic
   * @param     binary is true if this type is load from a binary class
   * @return    an unchecked type
   */
  CReferenceType createType(String name, CReferenceType[][] arguments, boolean binary);
  CReferenceType createType(TokenReference sourcePosition, String name, CReferenceType[][] arguments, boolean binary);
  /**
   * @return the environment.
   */
  boolean isGenericEnabled();

  /**
   * Reports a trouble (error or warning).
   *
   * @param	trouble		a description of the trouble to report.
   */
  void reportTrouble(PositionedError trouble);
}
