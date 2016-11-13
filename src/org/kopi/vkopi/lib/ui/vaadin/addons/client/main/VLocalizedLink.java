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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.main;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VAnchor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VLink;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * The help link widget.
 */
public class VLocalizedLink extends VLink {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Create a localized link widget.
   * @param locale The application locale
   */
  public VLocalizedLink(String locale) {
    link = new VAnchor();
    this.locale = locale;
    link.setHref("#");
    addLink(link);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the internal text of the link from a given key.
   * @param key The key to be localized.
   */
  public void setText(String key) {
    link.setText(LocalizedProperties.getString(locale, key));
  }
  
  /**
   * Sets the internal text of the link from a given key.
   * @param key The key to be localized.
   */
  public void setId(String id) {
    link.getElement().setId(id);
  }
  
  /**
   * Registers a click handler for this link.
   * @param handler The click handler to be registered
   * @return The registration handler.
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return link.addClickHandler(handler);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VAnchor				link;
  private final String				locale;
}
