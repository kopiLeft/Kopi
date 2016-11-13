/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.bytecode.classfile;

/**
 * A method can not have more than 65535 local variables. If this limit is 
 * exceeded by the method, this Exception is thrown.
 */
public class LocalVariableOverflowException extends ClassFileFormatException {

/**
   * Constructs a class file read exception
   */
  public LocalVariableOverflowException() {
    super();
  }

  /**
   * Constructs a class file read exception
   *
   * @param	message		the detail message
   */
  public LocalVariableOverflowException(String message) {
    super(message);
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  private String        method;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -8008931401046318642L;
}
