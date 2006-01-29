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

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents the definition of a type
 */
public abstract class VKCodeType extends VKType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param code		a list of code pair
   */
  public VKCodeType(TokenReference where, VKCodeDesc[] code) {
    super(where, 0, 0);
    this.code = code;
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
				    new JExpression[]{ genNames(), genValues() });
  }

  /**
   * Generates the names of this type
   */
  public JExpression genNames()  {
    TokenReference	ref = getTokenReference();
    JExpression[]	init = new JExpression[code.length];
    for (int i = 0; i < code.length; i++) {
      init[i] = new JStringLiteral(ref, code[i].getName());
    }
    return VKUtils.createArray(ref, CStdType.String, init);
  }

  /**
   * Generate the value of this type
   */
  public abstract JExpression genValues();

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public abstract void genVKCode(VKPrettyPrinter p);

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected VKCodeDesc[] code;
}
