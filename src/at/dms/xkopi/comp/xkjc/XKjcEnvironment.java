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

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.*;

/**
 */
public class XKjcEnvironment extends KjcEnvironment {

  public XKjcEnvironment(ClassReader classReader, 
                         TypeFactory typeFactory, 
                         KjcOptions options)
  {
    super(classReader, typeFactory , options = options);
    
    this.options = (XKjcOptions)options;
  }


  /**
   * @return AS_SIMPLE if only the assert statement is allowed 
   * and AS_ALL if contract programming is supported
   */
  public int getAssertExtension() {
    int         status;

    status = super.getAssertExtension();
    if (status == AS_NONE) {
      // simple assertions are always supported
      return AS_SIMPLE; 
    } else {
      return status;
    }
  }

  public boolean isGotoAllowed() {
    return options.allowGoto;
  }

  public boolean ignoreUnreachableStatement() {
    return options.ignoreUnreachableStatement;
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private XKjcOptions           options;
}
