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

package com.kopiright.compiler.tools.antlr.runtime;

public class ParserException extends ANTLRException {

public ParserException() {
    super("parsing error");
  }

  public ParserException(String message) {
    super(message);
  }

  public String getFilename() {
    return null;
  }

  /**
   * @return the line number that this exception happened on.
   * @author Shawn P. Vincent (svincent@svincent.com)
   */
  public int getLine() {
    return -1;
  }

  /**
   * @return the column number that this exception happened on.
   * @author Shawn P. Vincent (svincent@svincent.com)
   */
  public int getColumn() {
    return -1;
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 1552690380937780408L;
}
