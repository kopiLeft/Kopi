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

package com.kopiright.vkopi.comp.base;


import com.kopiright.compiler.base.TokenReference;
import com.kopiright.xkopi.comp.database.DatabaseBooleanColumn;
import com.kopiright.xkopi.comp.database.DatabaseColumn;
import com.kopiright.kopi.comp.kjc.*;
/**
 * This class represents the definition of a type
 */
public class VKBooleanCodeType extends VKCodeType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * !!!
   *
   * @param where		the token reference of this node
   * @param pack                the package name of the class defining the type
   * @param type		the identifier of the type definition
   * @param codes		a list of code pairs
   */
  public VKBooleanCodeType(TokenReference where,
                           String pack, 
                           String type,
                           VKCodeDesc[] codes)
  {
    super(where, pack, type, codes);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VBooleanCodeColumn;
  }

  public JExpression genValues() {
    TokenReference	ref = getTokenReference();
    JExpression[]	init = new JExpression[codes.length];

    for (int i = 0; i < codes.length; i++) {
      init[i] = new JBooleanLiteral(ref, codes[i].getBoolean());
    }
    return VKUtils.createArray(ref, CStdType.Boolean, init);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getType() {
    return com.kopiright.vkopi.comp.trig.GStdType.BooleanCodeField;
  }

  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo(){
    return new DatabaseBooleanColumn(true);
  }


  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    return com.kopiright.vkopi.comp.trig.GStdType.BooleanCodeColumn;
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    p.printCodeType("BOOL", codes);
  }
}
