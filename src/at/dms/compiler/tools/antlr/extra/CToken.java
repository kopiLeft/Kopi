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

package at.dms.compiler.tools.antlr.extra;

import at.dms.compiler.tools.antlr.runtime.Token;

public final class CToken extends Token {

  /**
   * Constructs a token
   */
  public CToken(int type, String text) {
    super(type);
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String		text;
}
