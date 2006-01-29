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

import java.io.IOException;

/**
 * Wraps an IOException in a TokenStreamException
 */
public class TokenStreamIOException extends TokenStreamException {
  public IOException io;
  /**
   * TokenStreamIOException constructor comment.
   * @param s java.lang.String
   */
  public TokenStreamIOException(IOException io) {
    super(io.getMessage());
    this.io = io;
  }
}
