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
import com.google.gwt.dom.client.Style.Unit;
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
        if (isMain) {
          getElement().getStyle().setWidth(Window.getClientWidth() - RESERVED_WIDTH, Unit.PX);
          menu.render(Window.getClientWidth() - RESERVED_WIDTH);
        }
      }
    }); 
  }

  @Override
  public void onResize(ResizeEvent event) {
    if (isMain) {
      // 250 px is the width reserved for logo and extra menus 
      menu.render(event.getWidth() - RESERVED_WIDTH);
    }
  }
  
  /**
   * Sets this module list to handle main menu
   * @param isMain Should handle main menu ?
   */
  public void setMain(boolean isMain) {
    this.isMain = isMain;
  }
  
  /**
   * Returns the module list menu.
   * @return The module list menu.
   */
  public VModuleListMenu getMenu() {
    return menu;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VModuleListMenu			menu;
  private boolean                               isMain;
  /*
   * Reserved width for company logo and extra menus
   * 180 (logo) + 200 (extra menu) + 70 (windows link)
   */
  private static final int                      RESERVED_WIDTH = 450;
}
