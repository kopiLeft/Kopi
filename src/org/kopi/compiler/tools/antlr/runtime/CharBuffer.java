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

package org.kopi.compiler.tools.antlr.runtime;

import java.io.IOException;

/**
 * A Stream of characters fed to the lexer from a InputStream that can
 * be rewound via mark()/rewind() methods.
 * <p>
 * A dynamic array is used to buffer up all the input characters.  Normally,
 * "k" characters are stored in the buffer.  More characters may be stored during
 * guess mode (testing syntactic predicate), or when LT(i>k) is referenced.
 * Consumption of characters is deferred.  In other words, reading the next
 * character is not done by conume(), but deferred until needed by LA or LT.
 * <p>
 *
 * @see org.kopi.compiler.tools.antlr.CharQueue
 */

import java.io.Reader; // SAS: changed to properly read text files

// SAS: Move most functionality into InputBuffer -- just the file-specific
//      stuff is in here
public class CharBuffer extends InputBuffer {
  // char source
  transient Reader input;

  /**
   * Create a character buffer
   */
  public CharBuffer(Reader input_) { // SAS: for proper text i/o
    super();
    input = input_;
  }

  /**
   * Ensure that the character buffer is sufficiently full
   */
  public void fill(int amount) throws CharStreamException {
    try {
      syncConsume();
      // Fill the buffer sufficiently to hold needed characters
      while (queue.nbrEntries < amount + markerOffset) {
	// Append the next character
	queue.append((char) input.read());
      }
    } catch (IOException io) {
      throw new CharStreamIOException(io);
    }
  }
}
