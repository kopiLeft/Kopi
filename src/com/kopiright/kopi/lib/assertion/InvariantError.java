/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.lib.assertion;

public class InvariantError extends AssertionError {
  
public InvariantError() {
    super();
  }

  public InvariantError(Object detailMessage) {
    super(detailMessage);
  }
  public InvariantError(boolean detailMessage) {
    this("" +  detailMessage);
  }
 
  public InvariantError(char detailMessage) {
    this("" +  detailMessage);
  }
 
  public InvariantError(int detailMessage) {
    this("" +  detailMessage);
  }
 
  public InvariantError(long detailMessage) {
    this("" +  detailMessage);
  }
 
  public InvariantError(float detailMessage) {
    this("" +  detailMessage);
  }
 
  public InvariantError(double detailMessage) {
    this("" +  detailMessage);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -3224933084612098957L;

}
