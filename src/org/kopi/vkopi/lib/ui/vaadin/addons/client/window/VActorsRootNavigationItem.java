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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.window;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.VActorsNavigationPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VNavigationMenu;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

public class VActorsRootNavigationItem extends Widget {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public VActorsRootNavigationItem(ApplicationConnection connection) {
    SpanElement                 inner;
    AnchorElement               anchor;
    
    inner = Document.get().createSpanElement();
    anchor = Document.get().createAnchorElement();
    label = new VSpan();
    icon = new VIcon();
    anchor.setHref("#");
    icon.addStyleDependentName("actors");
    icon.setName("bars");
    DOM.appendChild(inner, anchor);
    DOM.appendChild(anchor, icon.getElement());
    DOM.appendChild(anchor, label.getElement());
    setElement(inner);
    menu = new VNavigationMenu(connection);
    menu.setStyleName("actors-navigationMenu");
    setStyleName("actors-rootNavigationItem");
    sinkEvents(Event.ONCLICK);
    addDomHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        if (menu.isOpened()) {
          menu.close();
          getParent().getElement().removeClassName("open");
        } else {
          menu.open(VActorsRootNavigationItem.this);
          getParent().getElement().addClassName("open");
        }
      }
    }, ClickEvent.getType());
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  public void setCaption(String text) {
    label.setText(text);
  }

  public String getCaption() {
    return label.getElement().getInnerText();
  }
  
  /**
   * Sets the actors navigation panel associated with this root navigation item.
   * @param panel The actors navigation item.
   */
  public void setActorsNavigationPanel(VActorsNavigationPanel panel) {
    menu.setNavigationPanel(panel);
  }
  
  /**
   * Returns the icon DOM element.
   * @return The icon DOM element.
   */
  public Element getIconElement() {
    return icon.getElement();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private final VNavigationMenu                 menu;
  private VIcon                                 icon;
  private VSpan                                 label;
}
