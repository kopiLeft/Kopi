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

package com.kopiright.util.base;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;

/**
 * This class defines severals utilities methods used in source code
 */
public abstract class Utils {

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /*
   * Returns a string representation of an integer, padding it
   * with leading zeroes to the specified length.
   */
  public static String formatInteger(int value, int length) {
    StringBuffer	buffer;

    buffer = new StringBuffer(length);
    for (int i = length - ("" + value).length(); i > 0; i--) {
      buffer.append("0");
    }
    buffer.append(value);

    return buffer.toString();
  }

  /**
   * Check if an assertion is valid
   *
   * @exception	RuntimeException	the entire token reference
   */
  public static final void verify(boolean b) {
    if (!b) {
      throw new InconsistencyException();
    }
  }

  /**
   * Creates a list and fills it with the elements of the specified array.
   *
   * @param	array		the array of elements
   */
  public static List toList(Object[] array) {
    if (array == null) {
      return new Vector();
    } else {
      List	list = new Vector(array.length);

      for (int i = 0; i < array.length; i++) {
	list.add(array[i]);
      }
      return list;
    }
  }

  /**
   * Creates a typed array from a list.
   *
   * @param	list		the list containing the elements
   * @param	type		the type of the elements
   */
  public static Object[] toArray(List list, Class type) {
    if (list != null && list.size() > 0) {
      Object[]	array = (Object[])Array.newInstance(type, list.size());

      try {
	list.toArray(array);
      } catch (ArrayStoreException e) {
	System.err.println("Array was:" + list.get(0));
	System.err.println("New type :" + array.getClass());
	throw e;
      }
      return array;
    } else {
      return (Object[])Array.newInstance(type, 0);
    }
  }

  /**
   * Creates a int array from a list.
   *
   * @param	list		the list containing the elements
   * @param	type		the type of the elements
   */
  public static int[] toIntArray(List list) {
    if (list != null && list.size() > 0) {
      int[]	array = new int[list.size()];

      for (int i = array.length - 1; i >= 0; i--) {
	array[i] = ((Integer)list.get(i)).intValue();
      }

      return array;
    } else {
      return new int[0]; // $$$ static ?
    }
  }

  /**
   * Splits a string like:
   *   "java/lang/System/out"
   * into two strings:
   *    "java/lang/System" and "out"
   */
  public static String[] splitQualifiedName(String name, char separator) {
    String[]	result = new String[2];
    int		pos;

    pos = name.lastIndexOf(separator);

    if (pos == -1) {
      // no '/' in string
      result[0] = "";
      result[1] = name;
    } else {
      result[0] = name.substring(0, pos);
      result[1] = name.substring(pos + 1);
    }

    return result;
  }


  /**
   * Splits a string like:
   *   "java/lang/System/out"
   * into two strings:
   *    "java/lang/System" and "out"
   */
  public static String[] splitQualifiedName(String name) {
    return splitQualifiedName(name, '/');
  }

  /**
   * Returns a substring of this string.
   *
   * Provides a more robust implementation of java.lang.String.substring,
   * handling gracefully the following cases :
   * - the specified string is null
   * - a specified index is beyond the limits of the input string
   */
  public static String substring(String baseString, int beginIndex, int endIndex) {
    if (baseString == null) {
      return "";
    } else {
      return baseString.substring(Math.min(beginIndex, baseString.length()),
                                  Math.min(endIndex, baseString.length()));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

}
