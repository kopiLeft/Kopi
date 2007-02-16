/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

public class NoViableAltException extends RecognitionException {
  
public Token token;

  public NoViableAltException(Token t, String fileName) {
    super("NoViableAlt");
    token = t;
    line = t.getLine();
    column = t.getColumn();
    this.fileName = fileName;
  }

  /**
   * Returns a clean error message (no line number/column information)
   */
  public String getMessage() {
    return "unexpected token: "+token.getText();
  }

  /**
   * Returns a string representation of this exception.
   */
  public String toString() {
    return FileLineFormatter.getFormatter().getFormatString(fileName,line)+getMessage();
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 8091207225599225404L;
}
