/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.comp.form;

import at.dms.vkopi.comp.base.VKPhylum;
import at.dms.vkopi.comp.base.VKWindow;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.JClassDeclaration;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.TypeFactory;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

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
   * @param where		the token reference of this node
   * @param name		the name of this form
   */
  public VKFormElement(TokenReference where, String name) {
    super(where);

    int index = name.indexOf(".");

    this.ident = index > 0 ? name.substring(0, index) : name;
    this.shortcut = index > 0 ? name.substring(index + 1) : null;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

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
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String	ident;
  private String	shortcut;
  private int		pageNumber;
}
