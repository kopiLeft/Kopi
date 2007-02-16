/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
import com.kopiright.xkopi.comp.database.DatabaseEnumColumn;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents the definition of an string code type
 */
public class VKStringCodeType extends VKCodeType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * !!!
   *
   * @param where		the token reference of this node
   * @param pack                the package name of the class defining the type
   * @param type		the identifier of the type definition
   * @param code		a list of code value pairs
   */
  public VKStringCodeType(TokenReference where,
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
    return VKStdType.VStringCodeColumn;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Not allowed here
   */
  public void addList(VKFieldList l) {
    throw new InconsistencyException("LIST NOT ALLOWED IN CODE !!!");
  }

  /**
   * return whether this type support auto fill command
   */
  public boolean hasAutofill() {
    return true;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  public JExpression genValues() {
    TokenReference	ref = getTokenReference();
    JExpression[]	init =  new JExpression[codes.length];

    for (int i = 0; i < codes.length; i++) {
      init[i] = new JStringLiteral(ref, codes[i].getString());
    }
    return VKUtils.createArray(ref, CStdType.String, init);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getType() {
    return com.kopiright.vkopi.comp.trig.GStdType.StringCodeField;
  }


  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo() {
    String[]	values;

    values = new String[codes.length];
    for (int i = 0; i < codes.length; i++) {
      values[i] = codes[i].getString();
    }
    return new DatabaseEnumColumn(true, values);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    return com.kopiright.vkopi.comp.trig.GStdType.StringCodeColumn;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Print out the code
   *
   * @param p		the print writer into the help is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    p.printCodeType("STRING", codes);
  }
}
