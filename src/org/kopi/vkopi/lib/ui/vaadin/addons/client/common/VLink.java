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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.common;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSeparator;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;

/**
 * The link is a list item element encapsulation a HTM link (a element tag)
 * The link can contains also a separator span element.
 */
public class VLink extends ComplexPanel {

  //-----------------------------------------------------
  // CONSTRUCTORS
  //-----------------------------------------------------
  
  /**
   * Creates the link widget.
   */
  public VLink() {
    setElement(Document.get().createLIElement());
  }

  //-----------------------------------------------------
  // IMPLEMENTATIONS
  //-----------------------------------------------------
  
  /**
   * Adds a link widget to this complex link.
   * @param link The link widget.
   */
  @SuppressWarnings("deprecation")
  public void addLink(VAnchor link) {
    add(link, getElement());
  }
  
  /**
   * Appends a separator between links.
   */
  @SuppressWarnings("deprecation")
  public void addSeparator() {
    insert(new VSeparator(), getElement(), 0, true);
  }
}
