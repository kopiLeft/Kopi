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

package org.kopi.vkopi.lib.ui.vaadin.form;

import org.kopi.vkopi.lib.form.VFieldUI;

/**
 * The <code>DTextEditor</code> is the vaadin implementation
 * of a text editor UI component.
 */
@SuppressWarnings("serial")
public class DTextEditor extends DTextField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DTextEditor</code> instance.
   * @param model The row controller.
   * @param label The field label.
   * @param align The field alignment.
   * @param options The field options.
   * @param height The field height.
   * @param detail Does the field belongs to the detail view ?
   */
  public DTextEditor(VFieldUI model, 
		     DLabel label,
		     int align,
		     int options,
		     int height,
		     boolean detail) 
  {
    super(model, label, align, options, detail);
  }
}
