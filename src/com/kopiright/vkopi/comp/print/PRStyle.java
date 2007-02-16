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

package com.kopiright.vkopi.comp.print;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.JExpressionStatement;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.NotImplementedException;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKDefinition;
import com.kopiright.vkopi.comp.base.VKLatexPrintWriter;
import com.kopiright.vkopi.comp.base.VKLocalizationWriter;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;

/**
 * This class represents the definition of a style
 */
public abstract class PRStyle extends VKDefinition {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRStyle(TokenReference where, String ident, String superId) {
    super(where, null, ident);
    this.superId = superId;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the parent style
   */
  public String getParent() {
    return superId;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public final void checkCode(VKContext context) {
    throw new InconsistencyException();
  }

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract void checkCode(VKContext context, PRPage page) throws PositionedError;

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract JStatement genInit();

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract JExpressionStatement genCode();

  // ----------------------------------------------------------------------
  // HELP GENERATION
  // ----------------------------------------------------------------------

  /**
   * Print out the help to stream
   *
   * @param p		the print writer into the help is generated
   */
  public void genVKHelp(VKLatexPrintWriter p) {
    throw new NotImplementedException();
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
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX:taoufik
   */
  public void genLocalization(VKLocalizationWriter writer) {
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String	superId;
}
