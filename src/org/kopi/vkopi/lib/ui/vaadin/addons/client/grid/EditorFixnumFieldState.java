/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

/**
 * Shared state for decimal editor fields. 
 */
@SuppressWarnings("serial")
public class EditorFixnumFieldState extends EditorTextFieldState {
  
  /**
   * The minimum value to be accepted by the field.
   */
  @DelegateToWidget
  public Double                         minValue;
  
  /**
   * The maximum value to be accepted by the field.
   */
  @DelegateToWidget
  public Double                         maxValue;
  
  /**
   * The current scale of the decimal field.
   */
  @DelegateToWidget
  public int                            scale;
  
  /**
   * The max scale to be used with this field if it is a fixnum one.
   */
  @DelegateToWidget
  public int                            maxScale;
  
  /**
   * Is this field a fraction one ?
   */
  @DelegateToWidget
  public boolean                        fraction;
}
