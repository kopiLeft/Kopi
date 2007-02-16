/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
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
 * $Id$
 */

package com.kopiright.util.base;

/**
 * An InconsistencyException indicates that an inconsistent internal state has
 * been discovered, usually due to incorrect program logic.
 */
public class InconsistencyException extends RuntimeException {

  
/**
   * Constructs am InconsistencyException with no specified detail message.
   * @deprecated        Use the constructor with the message or build it with the 
   *                    exception which cause this case.
   */
  public InconsistencyException() {
    super();
  }

  /**
   * Constructs am InconsistencyException with the specified detail message.
   *
   * @param	message		the detail message
   */
  public InconsistencyException(String message) {
    super(message);
  }

  /**
   * Constructs am InconsistencyException with the specified detail message.
   *
   * @param	message		the detail message
   */
  public InconsistencyException(Throwable e) {
    super(e);
  }

  /**
   * Constructs am InconsistencyException with the specified detail message.
   *
   * @param	message		the detail message
   */
  public InconsistencyException(String message, Throwable e) {
    super(message, e);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -5494569374304974143L;
}
