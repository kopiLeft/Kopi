/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: LpdException.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.util.lpr;

/**
 * This exception is thrown when lpd return an error code
 */
public class LpdException extends Exception {

  /**
   * Constructs an exception with a message.
   *
   * @param	code		the exception code
   * @param	message		the associated message
   */
  public LpdException(int code, String message) {
    super(message);
    this.code = code;
  }

  /**
   * Return the error code returned by lpd
   */
  public int getCode() {
    return code;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int		code;
}
