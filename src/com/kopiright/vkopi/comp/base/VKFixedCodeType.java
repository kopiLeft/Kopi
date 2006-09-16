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
import com.kopiright.xkopi.comp.database.DatabaseColumn;
import com.kopiright.xkopi.comp.database.DatabaseFixedColumn;
import com.kopiright.kopi.comp.kjc.*;

/**
 * This class represents the definition of a type
 */
public class VKFixedCodeType extends VKCodeType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * !!!
   *
   * @param where		the token reference of this node
   * @param code		a list of code value pairs
   */
  public VKFixedCodeType(TokenReference where, VKCodeDesc[] code) {
    super(where, code);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VFixedCodeColumn;
  }

  public JExpression genValues() {
    TokenReference	ref = getTokenReference();
    JExpression[]	init =  new JExpression[code.length];
    for (int i = 0; i < code.length; i++) {
      init[i] = VKUtils.toExpression(ref, code[i].getFixed());
    }
    return VKUtils.createArray(ref, com.kopiright.xkopi.comp.xkjc.XStdType.Fixed, init);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getType() {
    return com.kopiright.vkopi.comp.trig.GStdType.FixedCodeField;
  }


  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo(){
    return new DatabaseFixedColumn(true);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    return com.kopiright.vkopi.comp.trig.GStdType.FixedCodeColumn;
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
    p.printCodeType("FIXED", code);
  }
}
