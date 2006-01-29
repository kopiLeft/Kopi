/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This error display all parameters of method call
 */
public class CMethodNotFoundError extends PositionedError {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * An error with two parameters
   * @param	where		the reference to token where error happen
   * @param	caller		the location of method invocation
   * @param	name		the method name
   * @param	types		the parameter types
   */
  public CMethodNotFoundError(TokenReference where,
			      JMethodCallExpression caller,
			      String name,
			      CType[] types)
  {
    super(where, KjcMessages.METHOD_NOT_FOUND, buildSignature(name, types));
    this.caller = caller;
  }

  private static String buildSignature(String name, CType[] types) {
    StringBuffer	buffer = new StringBuffer();

    buffer.append(name);
    buffer.append("(");
    if (types != null) {
      for (int i = 0; i < types.length; i++) {
	if (i != 0) {
	  buffer.append(", ");
	}
	buffer.append(types[i].toString());
      }
    }
    buffer.append(")");

    return buffer.toString();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the caller of the method that was not found.
   */
  public JMethodCallExpression getCaller() {
    return caller;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final JMethodCallExpression		caller;
}
