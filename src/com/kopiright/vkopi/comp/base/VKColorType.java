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
import com.kopiright.xkopi.comp.database.DatabaseColorColumn;
import com.kopiright.xkopi.comp.database.DatabaseColumn;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JIntLiteral;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.util.base.NotImplementedException;

/**
 * This class represents the definition of an color type
 */
public class VKColorType extends VKType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   */
  public VKColorType(TokenReference where) {
    super(where, 0, 0);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * return whether this type support auto fill command
   */
  public boolean hasAutofill() {
    return true;
  }

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VColorColumn;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructor() {
    TokenReference	ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
				    getType(),
				    new JExpression[] {
				      new JIntLiteral(ref, getWidth()),
				      new JIntLiteral(ref, getHeight())
				    });
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getType() {
    return com.kopiright.vkopi.comp.trig.GStdType.ColorField;
  }

  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo(){
    return new DatabaseColorColumn(true);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    throw new NotImplementedException();
    //    return com.kopiright.vkopi.comp.trig.GStdType.ColorColumn;
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
    p.printColorType();
  }
}
