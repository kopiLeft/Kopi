/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.JBooleanLiteral;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.util.base.NotImplementedException;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKPhylum;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;
import com.kopiright.vkopi.comp.base.VKUtils;

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

  private static CReferenceType	PPOSITION_TYPE	= CReferenceType.lookup(com.kopiright.vkopi.lib.print.PPosition.class.getName().replace('.','/'));
  private static CReferenceType	PSIZE_TYPE	= CReferenceType.lookup(com.kopiright.vkopi.lib.print.PSize.class.getName().replace('.','/'));

  private int		x;
  private int		y;
  private int		width;
  private int		height;
  private String	widthStr;
  private String	heightStr;
  private PRJavaExpression expr;
}
