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

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.PositionedError;

import at.dms.vkopi.comp.base.VKContext;

/**
 * This class represents the definition of a block in a page
 */
public class PRRecursiveBlock extends PRBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRRecursiveBlock(TokenReference where,
			  String ident,
			  PRPosition pos,
			  String style,
			  PRBlock[] blocks) {
    super(where, ident, pos, style);

    this.blocks = blocks;
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
    super.checkCode(context, page);
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].checkCode(context, page);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns the type of this block
   */
  public CType getType() {
    return TYPE;
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructorCall() {
    TokenReference	ref = getTokenReference();

    JExpression[] init = new JExpression[blocks.length];
    for (int i = 0; i < blocks.length; i++) {
      init[i] = PRUtils.toExpression(ref, blocks[i].getLastIdent());
    }

    return new JUnqualifiedInstanceCreation(ref,
				    TYPE,
				    new JExpression[] {
				      PRUtils.toExpression(getTokenReference(), getIdent()),
				      getPosition().genPosition(),
				      getPosition().genSize(),
				      getStyle() == null ? new JNullLiteral(ref) : getStyle().getStyle(),
				      PRUtils.createArray(ref, CStdType.String, init)
    });
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpressionStatement genDefinition(JClassDeclaration decl) {
    return null;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static CReferenceType	TYPE	= CReferenceType.lookup(at.dms.vkopi.lib.print.PRecursiveBlock.class.getName().replace('.','/'));

  private PRBlock[]	blocks;
}
