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

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Global link list composed of multiple links
 * wrapping different actions.
 */
public class VGlobalLinks extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the global links widget.
   */
  public VGlobalLinks() {
    linkGroup = new VLinkGroup();
    getElement().setId("globalLinks");
    add(linkGroup);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds a link without separator.
   * @param link The link to be added.
   */
  public void addLink(VLink link) {
    linkGroup.addLink(link);
  }
  
  /**
   * Adds a link with separator.
   * @param link The link to be added.
   */
  public void addLinkWithSeparator(VLink link) {
    linkGroup.addLinkWithSeparator(link);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VLinkGroup			linkGroup;
}
