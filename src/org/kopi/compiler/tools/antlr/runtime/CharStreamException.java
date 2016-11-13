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
 * Anything that goes wrong while generating a stream of characters
 */
public class CharStreamException extends ANTLRException {
  
/**
   * CharStreamException constructor comment.
   * @param s java.lang.String
   */
  public CharStreamException(String s) {
    super(s);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 3905272433808901381L;
  
}
