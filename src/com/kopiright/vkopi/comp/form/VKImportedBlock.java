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

package com.kopiright.vkopi.comp.form;

import com.kopiright.vkopi.comp.base.VKWindow;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.TokenReference;

/**
 * A block on a form
 * A block contains fields and reference to database
 */
public class VKImportedBlock extends VKFormElement implements com.kopiright.vkopi.lib.form.VConstants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   * @param name		the name of this form
   */
  public VKImportedBlock(TokenReference where, String name) {
    super(where, name);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKWindow window) {
    type = CReferenceType.lookup(getIdent());
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Gen new object
   */
  public JClassDeclaration genCode(boolean inner, TypeFactory factory) {
    return null;
  }

  /**
   * Gen new object
   */
  public JExpression genConstructorCall() {
    return new JUnqualifiedInstanceCreation(getTokenReference(),
				    getType(),
				    new JExpression[]{new JThisExpression(getTokenReference())});
  }

  /**
   * Returns the java type of this block
   */
  public CReferenceType getType() {
    return type;
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
    /*
    genComments(p);
    ((VKFormPrettyPrinter)p).printImporterBlock(getIdent(), getShortcut(), page);
    */
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CReferenceType		type;
}
