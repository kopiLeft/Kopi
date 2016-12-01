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

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;

/**
 * The field shared state.
 */
@SuppressWarnings("serial")
public class TextFieldState extends AbstractFieldState {

  /**
   * The column number.
   */
  @NoLayout
  public int 				col;
  
  /**
   * The row number.
   */
  @NoLayout
  public int 				rows;
  
  /**
   * The visible rows
   */
  @NoLayout
  public int 				visibleRows;
  
  /**
   * Use fixed new line transformer in multiple line field ?
   * Dynamic new line means that we use '\n' for line break.
   * Fixed new line means that we complete the messing field columns
   * with space character instead of using line separator.
   */
  @NoLayout
  public boolean                        fixedNewLine;
  
  /**
   * Is it a password field ?
   */
  @NoLayout
  public boolean 			noEcho;
  
  /**
   * Is it a scanner field ?
   */
  @NoLayout
  public boolean 			scanner;
  
  /**
   * Is it a no edit field.
   */
  @NoLayout
  public boolean 			noEdit;
  
  /**
   * The field text alignment.
   */
  @NoLayout
  public int 				align;
  
  /**
   * The field type: integer, fixnum, date, time, timestamp, ...
   */
  @NoLayout
  public int				type;
  
  /**
   * The minimum value to be accepted by the field.
   */
  public Double                         minval;
  
  /**
   * The maximum value to be accepted by the field.
   */
  public Double                         maxval;
  
  /**
   * The max scale to be used with this field if it is a fixnum one.
   */
  public int                            maxScale;
  
  /**
   * Is this field a fraction one ?
   */
  public boolean                        fraction;

  /**
   * The text in the field
   */
  @DelegateToWidget
  public String 			text = "";
  
  /**
   * The field enumeration for code fields.
   */
  public String[]			enumerations;
  
  /**
   * If the field has the auto complete feature.
   */
  public boolean			hasAutocomplete;
  
  /**
   * The auto complete minimum length to begin querying for suggestions
   */
  public int				autocompleteLength;
  
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
