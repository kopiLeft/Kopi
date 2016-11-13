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

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.annotations.DelegateToWidget;

/**
 * The field shared state.
 */
@SuppressWarnings("serial")
public class TextFieldState extends AbstractFieldState {

  /**
   * The column number.
   */
  public int 				col;
  
  /**
   * The row number.
   */
  public int 				rows;
  
  /**
   * The visible rows
   */
  public int 				visibleRows;
  
  /**
   * Use fixed new line transformer in multiple line field ?
   * Dynamic new line means that we use '\n' for line break.
   * Fixed new line means that we complete the messing field columns
   * with space character instead of using line separator.
   */
  public boolean                        fixedNewLine;
  
  /**
   * Is it a password field ?
   */
  public boolean 			noEcho;
  
  /**
   * Is it a scanner field ?
   */
  public boolean 			scanner;
  
  /**
   * Is it a no edit field.
   */
  public boolean 			noEdit;
  
  /**
   * The field text alignment.
   */
  public int 				align;
  
  /**
   * The field type: integer, fixnum, date, time, timestamp, ...
   */
  public int				type;

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
}
