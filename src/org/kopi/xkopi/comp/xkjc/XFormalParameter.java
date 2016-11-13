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

package org.kopi.xkopi.comp.xkjc;

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JFormalParameter;

/**
 * This class represents a parameter declaration in the syntax tree
 */
public class XFormalParameter extends JFormalParameter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	ident		the name of this variable
   * @param	initializer	the initializer
   */
  public XFormalParameter(TokenReference where,
			  int desc,
			  CType type,
			  String ident,
			  boolean isFinal) {
    super(where, ident == null ? DES_GENERATED : desc, type, ident, isFinal);
    if (ident == null) {
      //we add a $ because synthetic generated
      // fields are not pretty printed
      this.name = "anonymous_$"+toString();
    }
  }

}
