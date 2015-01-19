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

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;

/**
 * A button panel is a container that contains a list of button
 * aligned in an horizontal layout. The {@link CssLayout} is used
 * instead the standard horizontal layout in order to generate a
 * simple DOM HTML tree in client side.
 */
@SuppressWarnings("serial")
public class ButtonPanel extends CssLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>ButtonPanel</code> instance.
   * @see {@link CssLayout}
   */
  public ButtonPanel() {
    setStyleName(KopiTheme.BUTTON_PANEL_STYLE);
    setWidth("100%");
  }
  
  /**
   * Adds a separator component between the buttons in the panel.
   * @see {@link Separator}
   */
  public void addSeparator() {
    addComponent(new Separator());
  }
  
  //---------------------------------------------------
  // INNER CLASS
  //---------------------------------------------------
  
  /**
   * A button separator in order to distinguish
   * buttons in the panel.
   */
  public static class Separator extends Image {

    public Separator() {
      setStyleName(KopiTheme.IMAGE_SEPARATOR_STYLE);
      setSizeUndefined();
      setSource(Utils.getImage("separator.png").getResource());   
    }

    //--------------------------------------------------
    // DATA MEMBEERS
    //--------------------------------------------------
    
    private static final long	serialVersionUID = -4846198346668410538L;
  }
}
