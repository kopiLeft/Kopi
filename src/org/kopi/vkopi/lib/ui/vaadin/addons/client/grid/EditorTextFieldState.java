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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;

/**
 * Grid editor text field shared state. 
 */
@SuppressWarnings("serial")
public class EditorTextFieldState extends EditorFieldState {

  /**
   * The column number.
   */
  @NoLayout
  public int                            col;
  
  /**
   * The field text alignment.
   */
  @NoLayout
  public int                            align;
  
  /**
   * The text in the field
   */
  @DelegateToWidget
  public String                         text = "";
  
  /**
   * If the field has the auto complete feature.
   */
  public boolean                        hasAutocomplete;
  
  /**
   * The auto complete minimum length to begin querying for suggestions
   */
  public int                            autocompleteLength;
  
  /**
   * Sets the field to have the autofill option.
   */
  public boolean                        hasAutofill;
  
  /**
   * The convert type to be applied to the field.
   */
  public ConvertType                    convertType = ConvertType.NONE;
  
  /**
   * The convert type to be applied to this text field.
   * The convert type can be to upper case, to lower case or to name.
   */
  public static enum ConvertType {
    /**
     * no conversion.
     */
    NONE,
    /**
     * upper case conversion.
     */
    UPPER,
    /**
     * lower case conversion.
     */
    LOWER,
    /**
     * name conversion.
     */
    NAME
  }
}
