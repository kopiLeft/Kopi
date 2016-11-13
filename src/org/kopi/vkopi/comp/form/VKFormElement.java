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

package org.kopi.vkopi.comp.form;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.JClassDeclaration;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKLocalizationWriter;
import org.kopi.vkopi.comp.base.VKPhylum;
import org.kopi.vkopi.comp.base.VKWindow;

/**
 * A block on a form
 * A block contains fields and reference to database
 */
public abstract class VKFormElement extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param     where           the token reference of this node
   * @param     pkg             the package where this object is defined
   * @param     ident           the identifier of this block
   * @param     shortcut        the shortcut of this block
   */
  public VKFormElement(TokenReference where,
                       String pkg,
                       String ident,
                       String shortcut)
  {
    super(where);

    this.pkg = pkg;
    this.ident = ident;
    this.shortcut = shortcut;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the qualified source file name where this object is defined.
   */
  public String getSource() {
    String      basename;

    basename = getTokenReference().getName().substring(0, getTokenReference().getName().lastIndexOf('.'));
    return pkg == null ? basename : pkg + "/" + basename;
  }

  /**
   * Returns the name of this block
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Returns the shortcut name
   */
  public String getShortcut() {
    return shortcut;
  }

  /**
   * Sets the page number
   */
  public void setPageNumber(int page) {
    pageNumber = page;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @param window	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract void checkCode(VKContext context, VKWindow window) throws PositionedError;

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate a class for this element
   */
  public abstract JClassDeclaration genCode(boolean inner, TypeFactory factory);

  /**
   * Generate a call to the constructor
   */
  public abstract JExpression genConstructorCall();

  public abstract CReferenceType getType();

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX:taoufik
   */
  public abstract void genLocalization(VKLocalizationWriter writer);

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String          pkg;
  private final String          ident;
  private final String          shortcut;
  private int                   pageNumber;
}
