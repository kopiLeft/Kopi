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

package com.kopiright.xkopi.lib.base;
/**
 * This exception interrupts an protected statement. All 
 * exceptions which should interrupt a protected statement must 
 * be subclasses of this exception.
 */
public class XInterruptProtectedException extends Exception{
  
public XInterruptProtectedException() {
    super();
  }

  public XInterruptProtectedException(String msg) {
    super(msg);
  }

  public XInterruptProtectedException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public XInterruptProtectedException(Throwable cause) {
    super(cause);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -3115536699382779416L;

}
