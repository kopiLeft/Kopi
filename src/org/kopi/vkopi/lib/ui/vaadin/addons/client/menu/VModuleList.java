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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The Module list widget.
 */
public class VModuleList extends SimplePanel implements ResizeHandler {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the module
   */
  public VModuleList() {
    getElement().setId("moduleList");
    Window.addResizeHandler(this);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the root menu of this module list.
   * @param menu The root menu.
   */
  public void setMenu(VModuleListMenu menu) {
    clear();
    this.menu = menu;
    if (this.menu != null) {
      // ensure that is a root menu
      this.menu.setRoot();
      this.menu.setAnimationEnabled(true);
      this.menu.setAutoOpen(false);
      this.menu.setFocusOnHoverEnabled(false);
      add(this.menu); // add it to container.
    }
  }
  
  /**
   * Sets the auto open on hover ability.
   * @param autoOpen The auto open ability.
   */
  public void setAutoOpen(boolean autoOpen) {
    if (menu != null) {
      menu.setAutoOpen(autoOpen);
    }
  }
  
  /**
   * Sets the animation ability.
   * @param animated The animation ability.
   */
  public void setAnimation(boolean animated) {
    if (menu != null) {
      menu.setAnimationEnabled(animated);
    }
  }
  
  /**
   * Executes the item action.
   * @param itemId The item ID.
   */
  public void doItemAction(String itemId) {
    if (menu != null) {
      VModuleItem       item;
      
      item = menu.getItem(itemId);
      if (item != null) {
        menu.doItemAction(item, true, true);
      }
    }
  }
  
  @Override
  public void clear() {
    if (menu != null) {
      menu.clear();
    }
    super.clear();
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
	menu.render(Window.getClientWidth());
      }
    });
  }

  @Override
  public void onResize(ResizeEvent event) {
    menu.render(event.getWidth());
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VModuleListMenu			menu;
}
