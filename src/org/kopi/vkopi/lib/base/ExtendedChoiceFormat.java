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

package org.kopi.vkopi.lib.base;

import java.text.ChoiceFormat;
import java.text.FieldPosition;

/**
 * A customized choice format to support null test on objects.
 * The implementation transforms a boolean condition to a numeric
 * one in order to use the standard choice format implementation.
 *
 * In this conditions, we can pass any kind of object as an argument
 * to this extended choice format and for theses kind of objects, a null
 * test is performed and then transformed to {0, 1} numeric condition.
 * Knowing that 1 means that the object is not null and 0 means that the
 * object is null.
 *
 * @see #format(Object, StringBuffer, FieldPosition)
 */
public class ExtendedChoiceFormat extends ChoiceFormat {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  public ExtendedChoiceFormat(String newPattern, boolean hasNotNullMarker) {
    super(newPattern);
    this.hasNotNullMarker = hasNotNullMarker;
  }

  public ExtendedChoiceFormat(double[] limits, String[] formats, boolean hasNotNullMarker) {
    super(limits, formats);
    this.hasNotNullMarker = hasNotNullMarker;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /*
   * (non-Javadoc)
   * @see java.text.NumberFormat#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
   */
  public StringBuffer format(Object argument, StringBuffer toAppendTo, FieldPosition pos) {
    // a null test is performed before :
    // false --> 0
    // true --> 1
    if (argument instanceof Boolean || hasNotNullMarker && !(argument instanceof Number)) {
      return formatObject(argument, toAppendTo, pos);
    } else {
      // default behavior so number instances should pass here including fixed values
      return super.format(argument, toAppendTo, pos);
    }
  }

  /**
   * Formats any kind of non numeric object. Only null test is performed and transformed to {0, 1} condition.
   * @param argument The argument to be formatted.
   * @param toAppendTo The resulting string buffer.
   * @param pos The field position.
   * @return The formatted string.
   */
  protected StringBuffer formatObject(Object argument, StringBuffer toAppendTo, FieldPosition pos) {
    if (argument instanceof Boolean) {
      return super.format(toNumeric(((Boolean) argument).booleanValue()), toAppendTo, pos);
    } else {
      return super.format(toNumeric(argument != null && argument != ExtendedMessageFormat.NULL_REPRESENTATION), toAppendTo, pos);
    }
  }

  /**
   * Transforms the given boolean value to a numeric value.
   * The translation is done in a way that 0 is equivalent to false
   * and 1 is equivalent to true.
   * @param value The boolean value.
   * @return The equivalent numeric value.
   */
  protected int toNumeric(boolean value) {
    return value ? 1 : 0;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final boolean         hasNotNullMarker;
  /**
   * Generated serial ID.
   */
  private static final long     serialVersionUID = -17531293681639232L;
}
