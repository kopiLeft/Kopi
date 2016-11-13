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

import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.compiler.base.TokenReference;

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
    return org.kopi.vkopi.lib.print.PListBlock.class.getName().replace('.','/');
  }
}
