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
 * Shared state implementation for boolean editor 
 */
@SuppressWarnings("serial")
public class EditorBooleanFieldState extends EditorFieldState {
  
  /**
   * Sets the boolean field to be mandatory
   * This will remove to choose the null option
   * from the two check boxes
   */
  @DelegateToWidget
  public boolean                mandatory;
  
  /**
   * The label attached with this field.
   * Needed to set the for attribute of the input radio
   * in the field widget.
   */
  @NoLayout
  public String                 label;
  
  /**
   * The localized true value for this field
   */
  @NoLayout
  public String                 trueRepresentation;
  
  /**
   * The localized false value for this field
   */
  @NoLayout
  public String                 falseRepresentation;
}
