/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: PRPostscriptBlock.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.print;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKPrettyPrinter;

/**
 * This class represents the definition of a block in a page
 */
public class PRPostscriptBlock extends PRBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRPostscriptBlock(TokenReference where, String ident, PRPosition pos, String style, String name) {
    super(where, ident, pos, style);
    this.name = name;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns the type of this block
   */
  public CType getType() {
    return POSTSCRIPT_TYPE;
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   *
  public JExpression genCode() {
    TokenReference	ref = getTokenReference();
    return new JFieldAccessExpression(ref, getIdent());
  }
  */

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructorCall() {
    TokenReference ref = getTokenReference();

    JExpression	expr = new JUnqualifiedInstanceCreation(ref,
						(CReferenceType)getType(),
						new JExpression[] {
						  PRUtils.toExpression(ref, getIdent()),
						  getPosition().genPosition(),
						  getPosition().genSize(),
						  PRUtils.toExpression(ref, name)
						});
    JExpression left = new JFieldAccessExpression(ref, getIdent());
    return new JAssignmentExpression(ref, left, expr);
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

  private static CReferenceType	POSTSCRIPT_TYPE	= CReferenceType.lookup(at.dms.vkopi.lib.print.PPostscriptBlock.class.getName().replace('.','/'));

  private String	name;
}
