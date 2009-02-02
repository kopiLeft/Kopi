/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

public class CommonToken extends Token {
  // most tokens will want line and text information
  protected int line;
  protected String text = null;
  protected int col;

  public CommonToken() {}

  public CommonToken(int t, String txt) {
    type = t;
    setText(txt);
  }

  public CommonToken(String s)	{ text = s; }

  public int  getLine()		{ return line; }

  public String getText()		{ return text; }

  public void setLine(int l)		{ line = l; }

  public void setText(String s)	{ text = s; }

  public String toString() {
    return "[\""+getText()+"\",<"+type+">,line="+line+",col="+col+"]";
  }

  /**
   * Return token's start column
   */
  public int getColumn() { return col; }

  public void setColumn(int c) { col = c; }
}
