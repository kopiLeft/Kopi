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

package com.kopiright.util.base;

import java.util.Stack;

/**
 * This class implements a cache of char arrays
 */
public class CharArrayCache {

  /**
   * Returns a char array.
   */
  public static char[] request() {
    if (stack.empty()) {
      return new char[ARRAY_SIZE];
    } else {
      return (char[])stack.pop();
    }
  }

  /**
   * Releases a char array.
   */
  public static void release(char[] array) {
    stack.push(array);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int	ARRAY_SIZE = 100000;

  private static Stack		stack = new Stack();
}
