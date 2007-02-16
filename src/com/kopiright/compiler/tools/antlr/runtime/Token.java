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

/**
 * A token is minimally a token type.  Subclasses can add the text matched
 *  for the token and line info.
 */
public class Token implements Cloneable {
  // constants
  public static final int MIN_USER_TYPE = 4;
  public static final int NULL_TREE_LOOKAHEAD = 3;
  public static final int INVALID_TYPE = 0;
  public static final int EOF_TYPE = 1;
  public static final int SKIP = -1;

  // each Token has at least a token type
  int type=INVALID_TYPE;

  // the illegal token object
  public static Token badToken = new Token(INVALID_TYPE, "<no text>");

  public Token() {}
  public Token(int t) { type = t; }
  public Token(int t, String txt) { type = t; setText(txt); }
  public int getColumn() { return 0; }
  public int getLine() { return 0; }
  public String getText() { return "<no text>"; }
  public int getType() { return type; }
  public void setColumn(int c) {}
  public void setLine(int l) {}
  public void setText(String t) {}
  public void setType(int t) { type = t; }
  public String toString() {
    return "[\""+getText()+"\",<"+type+">]";
  }
}
