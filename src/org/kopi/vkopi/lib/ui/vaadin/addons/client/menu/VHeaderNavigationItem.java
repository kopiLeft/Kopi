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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.menu;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * A header navigation item to be as the title item 
 * in a navigation panel.
 */
public class VHeaderNavigationItem extends VNavigationItem {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new header navigation item.
   */
  public VHeaderNavigationItem() {}
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  protected Element[] createInnerElements() {
    return new Element[] {Document.get().createElement("h3")};
  }

  @Override
  protected String getClassname() {
    return "header";
  }
  
  @Override
  public void setCaption(String text) {
    DOM.getFirstChild(getElement()).setInnerText(text);
  }

  @Override
  public String getCaption() {
    return DOM.getFirstChild(getElement()).getInnerText();
  }

  @Override
  public void setDescription(String text) {
    // not used
  }
  
  @Override
  public void setIcon(String icon) {
    // not used
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void setEnabled(boolean enabled) {
    // not used
  }
}
