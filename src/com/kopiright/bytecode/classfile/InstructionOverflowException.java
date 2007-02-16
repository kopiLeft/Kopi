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

package com.kopiright.bytecode.classfile;

/**
 * A constant pool can have at most 65524 entrys. If this limit is exeeded
 * this exception is thrown.
 */
public class InstructionOverflowException extends ClassFileFormatException {

  
/**
   * Constructs a class file read exception
   */
  public InstructionOverflowException() {
    super();
  }

  /**
   * Constructs a class file read exception
   *
   * @param	message		the detail message
   */
  public InstructionOverflowException(String message) {
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
  private static final long serialVersionUID = 3143345393219503389L;

}
