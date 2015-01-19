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
 * $Id: HomeForm.java 34219 2014-06-23 09:27:49Z hacheni $
 */

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

/**
 * The <code>HomeForm</code> is the  application welcome screen.
 */
@SuppressWarnings("serial")
public class HomeForm extends VerticalLayout { //!!! FIXME: Use CSSLayout instead

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>HomeForm</code> instance.
   */
  public HomeForm() {
    Image  	homeImage;
    
    setStyleName(KopiTheme.HOME_TAB_STYLE);
    homeImage = new Image(null, Utils.getImage("splash_image.png").getResource());
    homeImage.setHeight(150, Unit.MM);
    homeImage.addStyleName(KopiTheme.HOME_IMAGE);
    addComponent(homeImage);
    setComponentAlignment(homeImage, Alignment.MIDDLE_CENTER);
  }
}
