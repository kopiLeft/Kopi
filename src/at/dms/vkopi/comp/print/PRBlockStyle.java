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

import java.awt.Color;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPrettyPrinter;
import at.dms.vkopi.comp.base.VKUtils;

/**
 * This class represents the definition of a style for a block
 */
public class PRBlockStyle extends PRStyle {

  public static final	int	POS_TOP		= 0;
  public static final	int	POS_CENTER	= 1;
  public static final	int	POS_BOTTOM	= 2;
  public static final	int	POS_LEFT	= 3;
  public static final	int	POS_RIGHT	= 4;
  public static final	int	BRD_LINE	= 5;
  public static final	int	BRD_NONE	= 6;

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRBlockStyle(TokenReference where,
		      String ident,
		      String superId,
		      int border,
		      Color background) {
    super(where, ident, superId);

    this.border = border;
    this.background = background;
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
  // COMPILER METHODS
  // ----------------------------------------------------------------------

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JStatement genInit() {
    TokenReference	ref = getTokenReference();

    JExpression expr = new JUnqualifiedInstanceCreation(ref,
							BLOCK_STYLE,
							new JExpression[] {
							  VKUtils.toExpression(ref, getIdent()),
							  VKUtils.toExpression(ref, getParent()),
							  VKUtils.toExpression(ref, border),
							  PRUtils.toExpression(ref, background)
							});

    expr = new JMethodCallExpression(ref, null, "addBlockStyle", new JExpression[] {expr});
    return new JExpressionStatement(ref, expr, null);
  }

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression getStyle() {
    return PRUtils.getBlockStyle(getTokenReference(), getIdent());
  }

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpressionStatement genCode() {
    throw new InconsistencyException();
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

  private static CReferenceType	BLOCK_STYLE	= CReferenceType.lookup(at.dms.vkopi.lib.print.PBlockStyle.class.getName().replace('.','/'));

  private int		border;
  private Color		background;
}
