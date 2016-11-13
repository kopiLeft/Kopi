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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleItem;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * An actor menu item that fires the same action fired by the actor
 * when clicked. The actor menu aims to show all available actors
 * for a window even those who has no icon and no accelerator
 */
@SuppressWarnings("deprecation")
public class VActorMenuItem extends VModuleItem implements HasEnabled {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Constructs a new actor menu item that fires a command when it is selected.
   *
   * @param text the item's text
   * @param menu The parent menu name of this menu item.
   * @param acceleratorKey The accelerator key description.
   * @param cmd the command to be fired when it is selected
   */
  public VActorMenuItem(String text, String menu, String acceleratorKey, ScheduledCommand cmd) {
    setCaption(text);
    setRoot(false);
    setScheduledCommand(cmd);
    this.menu = menu;
    this.acceleratorKey = acceleratorKey;
    setStyleName("actor-item");
    addAcceleratorText();
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the parent menu of this item.
   * @return The parent menu of this item.
   */
  public String getMenu() {
    return menu;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (enabled) {
      removeStyleDependentName(DEPENDENT_STYLENAME_DISABLED_ITEM);
    } else {
      addStyleDependentName(DEPENDENT_STYLENAME_DISABLED_ITEM);
    }
    this.enabled = enabled;
  }
  
  @Override
  protected void onUnload() {
    super.onUnload();
    menu = null;
    acceleratorKey = null;
  }
  
  /**
   * Adds the children indicator.
   */
  private void addAcceleratorText() {
    Element             acceleratorKey;
    
    acceleratorKey = DOM.createAnchor();
    acceleratorKey.setInnerText(this.acceleratorKey);
    acceleratorKey.setClassName("acceleratorKey");
    DOM.appendChild(getElement(), acceleratorKey);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private String                                menu;
  private String                                acceleratorKey;
  private boolean                               enabled;
  private static final String                   DEPENDENT_STYLENAME_DISABLED_ITEM = "disabled";
}
