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

package at.dms.vkopi.comp.print;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPhylum;
import at.dms.vkopi.comp.base.VKPrettyPrinter;

/**
 * This class represents the definition of a block in a page
 */
public abstract class PRBlock extends VKPhylum implements at.dms.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where		position in source code
   * @param parent		compilation unit where it is defined
   */
  public PRBlock(TokenReference where, String ident, PRPosition pos, String style) {
    super(where);

    this.ident = ident;
    this.pos = pos;
    this.style = style;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return the position of this block
   */
  public PRPosition getPosition() {
    return pos;
  }

  /**
   * Returns the ident of this block
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Returns the ident of this block
   */
  public String getLastIdent() {
    int		dot = ident.indexOf('.');

    return dot >= 0 ? ident.substring(dot + 1) : ident;
  }

  /**
   * Returns the style of this block
   */
  public PRBlockStyle getStyle() {
    return blockStyle;
  }

  /**
   * Returns the page that layout this block
   */
  public PRPage getPage() {
    return page;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param page	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, PRPage page) throws PositionedError {
    this.page = page;

    page.addBlock(this);
    if (ident == null) {
      ident = page.genUniqueIdent();
    }

    if (pos != null) {
      pos.checkCode(context, page);
    }

    if (style != null) {
      check((blockStyle = (PRBlockStyle)page.getStyle(style)) != null, PrintMessages.UNDEFINED_STYLE, style);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns the type of this block
   */
  public abstract CType getType();

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JClassDeclaration genCode(TypeFactory factory) {
    return null;
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract JExpression genConstructorCall();

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JFieldDeclaration genFieldDeclaration() {
    TokenReference	ref = getTokenReference();
    JVariableDefinition	def = new JVariableDefinition(ref,
						      ACC_PUBLIC,
						      getType(),
						      getIdent(),
						      null);

    return new JFieldDeclaration(ref, def, null, null);
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
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected PRPage	page;
  private String	ident;
  private PRPosition	pos;
  private String	style;
  private PRBlockStyle  blockStyle;
}
