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
import com.kopiright.xkopi.comp.database.DatabaseEnumColumn;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.NotImplementedException;

/**
 * This class represents the definition of an enum type
 */
public class VKEnumType extends VKType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param names		the list of names
   */
  public VKEnumType(TokenReference where, String[] names) {
    super(where, 0, 0);
    this.names = names;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VEnumColumn;
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

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructor() {
    return new JUnqualifiedInstanceCreation(getTokenReference(),
				    getType(),
				    new JExpression[]{ genNames() });
  }

  /**
   * Generates the names of this type
   */
  protected JExpression genNames() {
    TokenReference	ref = getTokenReference();
    JExpression[]	init = new JExpression[names.length];
    for (int i = 0; i < names.length; i++) {
      init[i] = new JStringLiteral(ref, names[i].toString());
    }
    return VKUtils.createArray(ref, CStdType.String, init);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getType() {
    return com.kopiright.vkopi.comp.trig.GStdType.EnumField;
  }


  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo(){
    if (names != null) {
      return new DatabaseEnumColumn(true, names);  
    } else {
      return new DatabaseEnumColumn(true);
    }
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    throw new NotImplementedException();
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
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String[]	names;
}
