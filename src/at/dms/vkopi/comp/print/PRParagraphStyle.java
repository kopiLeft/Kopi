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

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPrettyPrinter;

/**
 * This class represents the definition of a style for a paragraph
 */
public class PRParagraphStyle extends PRStyle {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRParagraphStyle(TokenReference where,
			  String ident,
			  String superStyle,
			  int align,
			  int indentLeft,
			  int borderMode,
			  int border,
                          int marginLeft,
                          int marginRight,
			  Color color,
			  boolean noBackground,
			  PRTabStop[] tabSet) {
    super(where, ident, superStyle);
    this.align = align;
    this.indentLeft = indentLeft;
    this.borderMode = borderMode;
    this.border = border;
    this.marginLeft = marginLeft;
    this.marginRight = marginRight;
    this.color = color;
    this.noBackground = noBackground;
    this.tabSet = tabSet;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, PRPage page) throws PositionedError {
    check(getParent() == null || page.getStyle(getParent()) != null, PrintMessages.UNDEFINED_STYLE, getParent());
  }

  /**
   *
   */
  public JStatement genInit() {
    TokenReference	ref = getTokenReference();
    JExpression		tabs;

    if (tabSet.length > 0) {
      JExpression[] init = new JExpression[tabSet.length];
      for (int i = 0; i < tabSet.length; i++) {
	init[i] = tabSet[i].genCode();
      }
      tabs = PRUtils.createArray(ref, TAB_STOP, init);
    } else {
      tabs = PRUtils.toExpression(ref, (String)null);
    }

    JExpression		expr = new JUnqualifiedInstanceCreation(ref,
							TYPE,
							new JExpression[] {
							  PRUtils.toExpression(ref, getIdent()),
							  PRUtils.toExpression(ref, getParent()),
							  PRUtils.toExpression(ref, align),
							  PRUtils.toExpression(ref, indentLeft),
							  PRUtils.toExpression(ref, borderMode),
							  PRUtils.toExpression(ref, border),
							  PRUtils.toExpression(ref, marginLeft),
							  PRUtils.toExpression(ref, marginRight),
							  PRUtils.toExpression(ref, color),
							  PRUtils.toExpression(ref, noBackground),
							  tabs
							});

    expr = new JMethodCallExpression(ref, null, "addStyle", new JExpression[]{expr});

    return new JExpressionStatement(ref, expr, null);
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
   * @param p		the listwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private static CReferenceType	TYPE = CReferenceType.lookup(at.dms.vkopi.lib.print.PParagraphStyle.class.getName().replace('.','/'));
  private static CReferenceType	TAB_STOP = CReferenceType.lookup(at.dms.vkopi.lib.print.PTabStop.class.getName().replace('.','/'));

  private boolean	noBackground;
  private int		align;
  private int		indentLeft;
  private int		border;
  private int		borderMode;
  private int		marginLeft;
  private int		marginRight;
  private Color		color;
  private PRTabStop[]	tabSet;
}
