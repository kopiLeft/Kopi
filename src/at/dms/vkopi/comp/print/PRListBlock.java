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
 * $Id: PRListBlock.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.print;

import at.dms.kopi.comp.kjc.CParseClassContext;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents the definition of a block in a page
 */
public class PRListBlock extends PRTextBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRListBlock(TokenReference where,
		     CParseClassContext context,
		     String ident,
		     PRPosition pos,
		     String blockStyle,
		     PRStyle[] styles,
		     PRTrigger[] triggers) {
    super(where, context, ident, pos, blockStyle, false, triggers);

    // !!! this.styles = styles;
  }

  public String getSuperName() {
    return at.dms.vkopi.lib.print.PListBlock.class.getName().replace('.','/');
  }
}
