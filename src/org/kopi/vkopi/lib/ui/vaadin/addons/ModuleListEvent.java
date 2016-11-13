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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import com.vaadin.ui.Component.Event;

/**
 * The module list event encapsulation the clicked item ID.
 */
@SuppressWarnings("serial")
public class ModuleListEvent extends Event {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new {@code ModuleListEvent} instance.
   * @param source The {@link ModuleList} source component.
   * @param itemId The clicked item ID.
   */
  public ModuleListEvent(ModuleList source, String itemId) {
    super(source);
    this.itemId = itemId;
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the module list source component.
   * @return The module list source component.
   */
  public ModuleList getModuleList() {
    return (ModuleList) super.getSource();
  }
  
  /**
   * Returns the clicked item ID.
   * @return The clicked item ID.
   */
  public String getItemId() {
    return itemId;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final String				itemId;
}
