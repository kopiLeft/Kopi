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

public class NoViableAltForCharException extends RecognitionException {
  
public char foundChar;

  public NoViableAltForCharException(char c, CharScanner scanner) {
    super("NoViableAlt");
    foundChar = c;
    this.line = scanner.getLine();
    this.fileName = scanner.getFilename();
  }

  public NoViableAltForCharException(char c, String fileName, int line) {
    super("NoViableAlt");
    foundChar = c;
    this.line = line;
    this.fileName = fileName;
  }

  /**
   * Returns a clean error message (no line number/column information)
   */
  public String getMessage() {
    return "unexpected char: "+(char)foundChar;
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 5200391943809889730L;
}
