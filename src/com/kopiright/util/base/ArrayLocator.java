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

import java.util.Hashtable;

/**
 * This class allows to find the position of an object in an array.
 */
public class ArrayLocator {

  /**
   * Constructs a new ArrayLocator object.
   */
  public ArrayLocator(Object[] array) {
    if (array.length < MIN_HASH) {
      this.array = array;
    } else {
      System.err.println("WARNING HASH DOES NOT WORK");
      this.hashed = new Hashtable(array.length + 1, 1.0f);
      for (int i = 0; i < array.length; i++) {
	this.hashed.put(array[i], new Integer(i));
      }
    }
  }

  /**
   * Returns the index of the specified object in the array, or -1
   * if the object cannot be found.
   * !!! TEST: linear search
   */
  public int getIndex(Object object) {
    if (this.array != null) {
      for (int i = 0; i < array.length; i++) {
	if (object == array[i]) {
	  return i;
	}
      }
      return -1;
    } else {
      Object	index = hashed.get(object);

      if (index == null) {
	return -1;
      } else {
	return ((Integer)index).intValue();
      }
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static final int	MIN_HASH = Integer.MAX_VALUE; // Hashtable does not work !!!
  // The equals method differs from == !!!
  private Object[]		array;
  private Hashtable		hashed;
}
