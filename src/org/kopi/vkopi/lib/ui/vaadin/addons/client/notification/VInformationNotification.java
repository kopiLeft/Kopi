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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.notification;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;

/**
 * Information type notification widget.
 */
public class VInformationNotification extends VAbstractNotification {
  
  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public void setButtons(String locale) {
    close = new VInputButton(LocalizedProperties.getString(locale, "CLOSE"), new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        hide();
        fireOnClose(false);
      }
    });
    
    buttons.add(close);
  }
  
  @Override
  protected String getIconName() {
    return "info-circle";
  }
  
  @Override
  public void focus() {
    if (close != null) {
      close.focus();
    }
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    if (close != null && close.getCaption().toLowerCase().charAt(0) == event.getCharCode()) {
      close.click();
    }
  }
  
  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------
  
  private VInputButton          		close;
}
