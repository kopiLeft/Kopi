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

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.comp.base.VKPhylum;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKUtils;

/**
 * This class represents the definition of a block in a page
 */
public class PRTabStop extends VKPhylum {

  public PRTabStop(TokenReference where, String ident, int pos, int align) {
    super(where);

    this.ident = ident;
    this.pos = pos;
    this.align = align;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates the code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode() {
    TokenReference	ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
				    TYPE,
				    new JExpression[] {
				      VKUtils.toExpression(ref, ident),
				      VKUtils.toExpression(ref, pos),
				      VKUtils.toExpression(ref, align)
				    });
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

  private static CReferenceType	TYPE = CReferenceType.lookup(org.kopi.vkopi.lib.print.PTabStop.class.getName().replace('.','/'));
  public static final PRTabStop[]	EMPTY = new PRTabStop[0];

  private String	ident;
  private int		pos;
  private int		align;
}
