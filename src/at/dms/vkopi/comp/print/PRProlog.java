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
 * $Id: PRProlog.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.print;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPhylum;
import at.dms.vkopi.comp.base.VKPrettyPrinter;
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
		  String name,
		  boolean portrait,
		  String format) {
    super(where);

    this.name = name;
    this.format =  format == null ? "A4" : format;
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

    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      null,
							      "setProlog",
							      new JExpression[] {
								new JStringLiteral(ref, name == null ? "NO PROLOG" : name),
								PRUtils.toExpression(ref, width),
								PRUtils.toExpression(ref, height),
								PRUtils.toExpression(ref, border),
								PRUtils.toExpression(ref, !portrait)
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
  private String	name;
  private String	format;
  private boolean	portrait;
}
