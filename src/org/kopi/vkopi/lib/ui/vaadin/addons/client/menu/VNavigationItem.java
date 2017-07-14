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
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * The navigation item is the widget to be inserted in
 * a {@link VNavigationPanel}. This is an abstract implementation
 * and subclasses can define their inner HTML elements.
 */
public abstract class VNavigationItem extends Widget implements HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new navigation item widget.
   */
  protected VNavigationItem() {
    setElement(Document.get().createLIElement());
    for (Element inner : createInnerElements()) {
      DOM.appendChild(getElement(), inner);
    }
    setStyleName(getClassname());
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Creates the inner elements of this navigation item.
   * @return The inner elements of this navigation item.
   */
  protected abstract Element[] createInnerElements();
  
  /**
   * Returns the CSS class name to be added to this item.
   * @return The CSS class name to be added to this item.
   */
  protected abstract String getClassname();
  
  /**
   * Sets the text to be displayed by this navigation item.
   * @param text The text to be displayed.
   */
  public abstract void setCaption(String text);
  
  /**
   * Returns the caption of this navigation item.
   * @return The caption of this navigation item.
   */
  public abstract String getCaption();
  
  /**
   * Sets an additional text to be displayed in front of the item caption.
   * @param text The item description.
   */
  public abstract void setDescription(String text);
  
  /**
   * Sets the icon of this navigation item.
   * @param icon The icon name.
   */
  public abstract void setIcon(String icon);
}
