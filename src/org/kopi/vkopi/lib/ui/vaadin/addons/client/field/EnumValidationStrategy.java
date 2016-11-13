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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

/**
 * Validation strategy for enumeration fields
 */
public class EnumValidationStrategy extends AllowAllValidationStrategy {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the validation strategy.
   * @param enumerations The enumeration values.
   */
  public EnumValidationStrategy(String[] enumerations) {
    this.enumerations = enumerations;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public boolean validate(String text, int maxLength) {
    if (enumerations != null && text != null) {
      String	s = text.toLowerCase();

      for (int i = 0; i < enumerations.length; i++) {
	if (enumerations[i].toLowerCase().startsWith(s)) {
	  return true;
	}
      }
      
      return false;
    }
    
    return true;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final String[] 			enumerations;
}
