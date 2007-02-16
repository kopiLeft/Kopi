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

package com.kopiright.kopi.lib.assertion;

public class PostconditionError extends AssertionError {
  

public PostconditionError() {
    super();
  }

  public PostconditionError(Object detailMessage) {
    super(detailMessage);
  }

  public PostconditionError(boolean detailMessage) {
    this("" +  detailMessage);
  }
 
  public PostconditionError(char detailMessage) {
    this("" +  detailMessage);
  }
 
  public PostconditionError(int detailMessage) {
    this("" +  detailMessage);
  }
 
  public PostconditionError(long detailMessage) {
    this("" +  detailMessage);
  }
 
  public PostconditionError(float detailMessage) {
    this("" +  detailMessage);
  }
 
  public PostconditionError(double detailMessage) {
    this("" +  detailMessage);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
   private static final long serialVersionUID = -409676899400309009L;
}
