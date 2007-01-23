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

package com.kopiright.vkopi.comp.print;

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.PositionedError;

import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKUtils;

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
      init[i] = VKUtils.toExpression(ref, blocks[i].getLastIdent());
    }

    return new JUnqualifiedInstanceCreation(ref,
				    TYPE,
				    new JExpression[] {
				      VKUtils.toExpression(getTokenReference(), getIdent()),
				      getPosition().genPosition(),
				      getPosition().genSize(),
				      getStyle() == null ? new JNullLiteral(ref) : getStyle().getStyle(),
				      VKUtils.createArray(ref, CStdType.String, init)
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

  private static CReferenceType	TYPE	= CReferenceType.lookup(com.kopiright.vkopi.lib.print.PRecursiveBlock.class.getName().replace('.','/'));

  private PRBlock[]	blocks;
}
