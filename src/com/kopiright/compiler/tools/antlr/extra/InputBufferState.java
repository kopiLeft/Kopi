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

package com.kopiright.compiler.tools.antlr.extra;

/**
 * Acts as communication object between different scanners.
 */
public class InputBufferState {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a new input buffer state object.
   *
   * @param	buffer		contains the current text to be matched
   * @param	startRead	the beginning of the yytext() string
   * @param	endRead		the last character in the buffer
   * @param	currentPos	the the current text position in the buffer
   * @param	markedPos	the position at the last accepting state
   * @param	pushbackPos	the position at the last state to be included in yytext
   * @param	atEOF		is the scanner at the EOF ?
   */
  public InputBufferState(char[] buffer,
			  int startRead,
			  int endRead,
			  int currentPos,
			  int markedPos,
			  int pushbackPos,
			  boolean atEOF)
  {
    this.buffer = buffer;
    this.startRead = startRead;
    this.endRead = endRead;
    this.currentPos = currentPos;
    this.markedPos = markedPos;
    this.pushbackPos = pushbackPos;
    this.atEOF = atEOF;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public char[]		buffer;
  public int		startRead;
  public int		endRead;
  public int		currentPos;
  public int		markedPos;
  public int		pushbackPos;
  public boolean	atEOF;
}
