/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.comp.print;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.*;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKPhylum;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKUtils;

import java.util.StringTokenizer;

/**
 * This class represents the definition of a block in a page
 */
public class PRProlog extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRProlog(TokenReference where,
		  boolean portrait,
		  String format) {
    super(where);

    this.format =  format == null ? "a4" : format;
    this.portrait = portrait;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void checkCode(VKContext context) throws PositionedError{
    if (format.toLowerCase().equals("a5")) {
      width = 420;
      height = 595;
      border = 25;
    } else if (format.toLowerCase().equals("a4")) {
      width = 595;
      height = 842;
      border = 25;
    } else if (format.toLowerCase().equals("a3")) {
      width = 842;
      height = 1190;
      border = 25;
    } else if (format.toLowerCase().equals("letter")) {
      width = 612;
      height = 1008;
      border = 25;
    } else if (format.toLowerCase().equals("legal")) {
      width = 612;
      height = 792;
      border = 25;
    } else {
      try {
	StringTokenizer tok = new StringTokenizer(format);
	if (tok.countTokens() == 3) {
	  String	num = tok.nextToken();
	  width = Integer.valueOf(num).intValue();
	  num = tok.nextToken();
	  height = Integer.valueOf(num).intValue();
	  num = tok.nextToken();
	  border = Integer.valueOf(num).intValue();
	} else {
	  throw new CLineError(getTokenReference(), PrintMessages.BAD_FORMAT, format);
	  // !!!> should be a3, a4, letter, legal, "width height border"
	}
      } catch (Exception e) {
	throw new CLineError(getTokenReference(), PrintMessages.BAD_FORMAT, format);
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JStatement genCode() {
    TokenReference ref = getTokenReference();

    JUnqualifiedInstanceCreation rectangle = new JUnqualifiedInstanceCreation(ref,
                                                                              new CClassNameType(ref, "com/lowagie/text/Rectangle", true),
                                                                              new JExpression[] {
                                                                                VKUtils.toExpression(ref, portrait? width : height),
                                                                                VKUtils.toExpression(ref, portrait? height : width)
                                                                              });

    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      null,
							      "setProlog",
							      new JExpression[] {
                                                                rectangle,
                                                                VKUtils.toExpression(ref, border)
                                                              }),
                                    null);
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
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int		width;
  private int		height;
  private int		border;
  private String	format;
  private boolean	portrait;
}
