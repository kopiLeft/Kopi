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

/**
 * Wraps a RecognitionException in a TokenStreamException so you
 * can pass it along.
 */
public class TokenStreamRecognitionException extends TokenStreamException {
  
public RecognitionException recog;
  public TokenStreamRecognitionException(RecognitionException re) {
    super(re.getMessage());
    this.recog = re;
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -463302395138481834L;
}
