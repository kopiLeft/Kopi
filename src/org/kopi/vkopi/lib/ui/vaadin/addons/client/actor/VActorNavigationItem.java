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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.actor;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VClickableNavigationItem;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class VActorNavigationItem extends VClickableNavigationItem {
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Constructs a new actor navigation menu item that fires a command when it is selected.
   *
   * @param text the item's text
   * @param menu The parent menu name of this menu item.
   * @param acceleratorKey The accelerator key description.
   * @param icon The menu item icon.
   * @param cmd the command to be fired when it is selected
   */
  public VActorNavigationItem(String text,
                              String menu,
                              String acceleratorKey,
                              String icon,
                              ScheduledCommand cmd)
  {
    super(cmd);
    setCaption(text);
    setDescription(acceleratorKey);
    setIcon(icon);
    this.menu = menu;
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the actor navigation item menu.
   * @return The actor navigation item menu.
   */
  public String getMenu() {
    return menu;
  }
  
  @Override
  protected String getClassname() {
    return "actor-navigationItem";
  }
  
  @Override
  protected void onUnload() {
    super.onUnload();
    menu = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private String                        menu;
}
