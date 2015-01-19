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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import com.kopiright.vkopi.lib.base.UComponent;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

/**
 * The <code>FieldButton</code> is a {@link ButtonPanel}
 * component having its own style.
 */
@SuppressWarnings("serial")
public class FieldButton extends Button implements UComponent {
	
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>FieldButton</code> instance.
   * @param icon The field button icon.
   */
  public FieldButton(Resource icon) {    
    this.setIcon(icon);
    this.addStyleName(KopiTheme.BUTTON_ICON_ONLY);
  }
}
