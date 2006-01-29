/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

/**
 * Thrown to indicate that an assertion has failed.
 *
 * In JDK 1.4 this class will be replaced with <code> java.lang.AssertionError </code>
 */
public class AssertionError extends Error {

  /**
   * Constructs an AssertionError with no detail message.
   */
  public AssertionError() {
  }

  private AssertionError(String detailMessage) {
    super(detailMessage);
  }

  public AssertionError(Object detailMessage) {
    this("" +  detailMessage);
  }
 
  public AssertionError(boolean detailMessage) {
    this("" +  detailMessage);
  }
 
  public AssertionError(char detailMessage) {
    this("" +  detailMessage);
  }
 
  public AssertionError(int detailMessage) {
    this("" +  detailMessage);
  }
 
  public AssertionError(long detailMessage) {
    this("" +  detailMessage);
  }
 
  public AssertionError(float detailMessage) {
    this("" +  detailMessage);
  }
 
  public AssertionError(double detailMessage) {
    this("" +  detailMessage);
  }
}
