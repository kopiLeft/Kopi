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

import java.awt.Color;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.util.base.NotImplementedException;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;

/**
 * This class represents the definition of a block in a page
 */
public class PRTextStyle extends PRStyle {

  public static final int	FCE_BOLD		= 1 << 0;
  public static final int	FCE_ITALIC		= 1 << 1;
  public static final int	FCE_SUBSCRIPT		= 1 << 2;
  public static final int	FCE_SUPERSCRIPT		= 1 << 3;
  public static final int	FCE_UNDERLINE		= 1 << 4;
  public static final int	FCE_STRIKETHROUGHT	= 1 << 5;
  public static final int	FCE_NO_BOLD		= 1 << 8;
  public static final int	FCE_NO_ITALIC		= 1 << 9;
  public static final int	FCE_NO_SUBSCRIPT	= 1 << 10;
  public static final int	FCE_NO_SUPERSCRIPT	= 1 << 11;
  public static final int	FCE_NO_UNDERLINE	= 1 << 12;
  public static final int	FCE_NO_STRIKETHROUGHT	= 1 << 13;

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRTextStyle(TokenReference where,
		     String ident,
		     String superId,
		     Color background,
		     Color foreground,
		     int face,
		     String font,
		     int size) {
    super(where, ident, superId);
    
    this.face = face;
    this.font = font;
    this.size = size;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   */
  public void checkCode(VKContext context, PRPage page) {
    // nothing to do
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JStatement genInit() {
    TokenReference	ref = getTokenReference();

    JExpression		expr = new JUnqualifiedInstanceCreation(ref,
							TYPE,
							new JExpression[] {
							  PRUtils.toExpression(ref, getIdent()),
							  PRUtils.toExpression(ref, font),
							  PRUtils.toExpression(ref, face),
							  PRUtils.toExpression(ref, size)
							});
    expr = new JMethodCallExpression(ref, null, "addStyle", new JExpression[]{ expr });

    return new JExpressionStatement(ref, expr, null);
  }

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpressionStatement genCode() {
    return null;
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
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private static CReferenceType	TYPE = CReferenceType.lookup(com.kopiright.vkopi.lib.print.PTextStyle.class.getName().replace('.','/'));

  private int		face;
  private String	font;
  private int		size;
}
