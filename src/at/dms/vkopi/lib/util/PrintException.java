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
 * $Id: PrintException.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.util;

/**
 * An error has occur during printing
 */
public class PrintException extends Exception {

  /**
   * PrintException
   *
   * @param	msg	Explanation of the error
   * @param	type	Kind of the error (see EXEC_ constants)
   */
  public PrintException(String msg, int code) {
    super(msg);
    this.code = code;
  }

  /**
   * PrintException
   *
   * @param	msg	Explanation of the error
   * @param	type	Kind of the error (see EXEC_ constants)
   */
  public PrintException(String msg, Throwable cause, int code) {
    super(msg, cause);
    this.code = code;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Gets the error code
   */
  public int getCode() {
    return code;
  }

  /**
   * is this error is fatal
   */
  public boolean isFatalError() {
    return code == EXC_FATAL;
  }


  /**
   * should we retry
   */
  public boolean shouldRetry() {
    return code == EXC_RETRY;
  }

  // ----------------------------------------------------------------------
  // PUBLIC CONSTANTS
  // ----------------------------------------------------------------------

  public static final int	EXC_UNKNOWN	= 0;
  public static final int	EXC_FATAL	= 1;
  public static final int	EXC_RETRY	= 2;

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int	code;
}
