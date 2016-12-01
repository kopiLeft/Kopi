/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

/**
 * An integer validation strategy.
 */
public class IntegerValidationStrategy extends AllowAllValidationStrategy {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new integer validation strategy instance.
   * @param minval The minimum accepted value.
   * @param maxval The maximum accepted value.
   */
  public IntegerValidationStrategy(Double minval, Double maxval) {
    Integer             min;
    Integer             max;
    
    if (minval == null) {
      min = null;
    } else {
      min = new Integer(minval.intValue());
    }
    
    if (maxval == null) {
      max = null;
    } else {
      max = new Integer(maxval.intValue());
    }
    
    this.minval = min;
    this.maxval = max;
  }
  
  /**
   * Creates a new integer validation strategy instance.
   * @param minval The minimum accepted value.
   * @param maxval The maximum accepted value.
   */
  public IntegerValidationStrategy(Integer minval, Integer maxval) {
    this.minval = minval;
    this.maxval = maxval;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public boolean validate(char c) {
    return Character.isDigit(c) || c == '.' || c == '-';
  }
  
  @Override
  public void checkType(VInputTextField field, String text) throws CheckTypeException {
    if ("".equals(text)) {
      field.setText(null);
    } else {
      int       v;

      try {
        v = Integer.parseInt(text);
      } catch (NumberFormatException e) {
        throw new CheckTypeException(field, "00006");
      }

      if (minval != null && v < minval) {
        throw new CheckTypeException(field, "00012", minval);
      }
      if (maxval != null && v > maxval) {
        throw new CheckTypeException(field, "00009", maxval);
      }

      field.setText(text);
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Integer                         minval;
  private final Integer                         maxval;
}
