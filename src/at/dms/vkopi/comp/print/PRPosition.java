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
 * $Id: PRPosition.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.print;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.JBooleanLiteral;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPhylum;
import at.dms.vkopi.comp.base.VKPrettyPrinter;
import at.dms.vkopi.comp.base.VKUtils;

/**
 * This class represents the definition of a block in a page
 */
public class PRPosition extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRPosition(TokenReference where,
		    int x,
		    int y,
		    int width,
		    int height,
		    String widthStr,
		    String heightStr,
		    PRJavaExpression expr) {
    super(where);

    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.widthStr = widthStr;
    this.heightStr = heightStr;
    this.expr = expr;
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
    if (widthStr != null && !widthStr.equals("PAGE_WIDTH") && !widthStr.equals("MAX")) {
      throw new PositionedError(getTokenReference(), PrintMessages.UNDEFINDED_AUTOSIZE, widthStr);
    }
    if (heightStr != null && !heightStr.equals("PAGE_HEIGHT") && !heightStr.equals("MAX")) {
      throw new PositionedError(getTokenReference(), PrintMessages.UNDEFINDED_AUTOSIZE, heightStr);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public JExpression genExpression() {
    return expr == null ?
      new JBooleanLiteral(getTokenReference(), true) :
      expr.genExpression();
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genPosition() {
    TokenReference	ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
				    PPOSITION_TYPE,
				    new JExpression[] {
				      VKUtils.toExpression(ref, x),
				      VKUtils.toExpression(ref, y),
				    });
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genSize() {
    TokenReference	ref = getTokenReference();
    JExpression		left = widthStr == null ? VKUtils.toExpression(ref, width) : VKUtils.toExpression(ref, widthStr);
    JExpression		right = heightStr == null ? VKUtils.toExpression(ref, height) : VKUtils.toExpression(ref, heightStr);

    return new JUnqualifiedInstanceCreation(ref,
				    PSIZE_TYPE,
				    new JExpression[] { left, right });
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

  private static CReferenceType	PPOSITION_TYPE	= CReferenceType.lookup(at.dms.vkopi.lib.print.PPosition.class.getName().replace('.','/'));
  private static CReferenceType	PSIZE_TYPE	= CReferenceType.lookup(at.dms.vkopi.lib.print.PSize.class.getName().replace('.','/'));

  private int		x;
  private int		y;
  private int		width;
  private int		height;
  private String	widthStr;
  private String	heightStr;
  private PRJavaExpression expr;
}
