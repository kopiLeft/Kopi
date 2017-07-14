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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.ModuleListClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.ModuleListServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.ModuleListState;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

/**
 * The module list server side component.
 */
@SuppressWarnings("serial")
public class ModuleList extends AbstractComponent implements HasComponents {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new module list component.
   */
  public ModuleList() {
    registerRpc(rpc);
    setImmediate(true);
    moduleItems = new LinkedList<ModuleItem>();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Add a new item inside this item, thus creating a sub-menu. Command
   * can be null, but a caption must be given.
   * @param id The item ID.
   * @param caption The item caption.
   * @param description The item description
   * @param isLeaf Is it a leaf item.
   */
  public ModuleItem addItem(String id, String caption, String description, boolean isLeaf) {
    return addItem(new ModuleItem(id, caption, description, isLeaf));
  }
  
  /**
   * Adds a child item to this item.
   * @param newItem The item to be added.
   * @return The added item.
   */
  public ModuleItem addItem(ModuleItem newItem) {
    if (newItem.getText() == null) {
      throw new IllegalArgumentException("caption cannot be null");
    }
    
    moduleItems.add(newItem);
    newItem.setParent(this);
    return newItem;
  }

  /**
   * Using this method menubar can be put into a special mode where top level
   * menus opens without clicking on the menu, but automatically when mouse
   * cursor is moved over the menu. In this mode the menu also closes itself
   * if the mouse is moved out of the opened menu.
   * <p>
   * Note, that on touch devices the menu still opens on a click event.
   * 
   * @param autoOpenTopLevelMenu
   *            true if menus should be opened without click, the default is
   *            false
   */
  public void setAutoOpen(boolean autoOpenTopLevelMenu) {
    if (autoOpenTopLevelMenu != getState().openRootOnHover) {
      getState().openRootOnHover = autoOpenTopLevelMenu;
    }
  }
  
  /**
   * Sets the animation ability of this module list.
   * @param animated The animation ability.
   */
  public void setAnimationEnabled(boolean animated) {
    getState().animated = animated;
  }
  
  /**
   * Executes the item action.
   * @param itemId The item ID.
   */
  public void doItemAction(String itemId) {
    getRpcProxy(ModuleListClientRpc.class).doItemAction(itemId);
  }

  /**
   * Detects whether the menubar is in a mode where top level menus are
   * automatically opened when the mouse cursor is moved over the menu.
   * Normally root menu opens only by clicking on the menu. Submenus always
   * open automatically.
   * 
   * @return true if the root menus open without click, the default is false
   */
  public boolean isAutoOpen() {
    return getState().openRootOnHover;
  }
  
  /**
   * Sets the module list type.
   * The type is the root menu ID associated with this module list.
   * @param type The menu type
   */
  public void setType(int type) {
    getState().type = type;
  }
  
  /**
   * Registers a module list listener on this component.
   * @param listener The listener to be registered.
   */
  public void addModuleListListener(ModuleListListener listener) {
    addListener(ModuleListEvent.class, listener, ModuleListListener.CLICK_METHOD);
  }

  /**
   * Removes the module list listener.
   * @param listener The Listener to be removed.
   */
  public void removeActionListener(ModuleListListener listener) {
    removeListener(ModuleListEvent.class, listener, ModuleListListener.CLICK_METHOD);
  }
  
  /**
   * Fires an action performed event to all registered listeners.
   */
  protected void fireClicked(String itemId) {
    fireEvent(new ModuleListEvent(this, itemId));
  }
  
  @Override
  public Iterator<Component> iterator() {
    return new LinkedList<Component>(moduleItems).iterator();
  }
  
  @Override
  protected ModuleListState getState() {
    return (ModuleListState) super.getState();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  // Items of the top-level menu
  private final List<ModuleItem> 		moduleItems;
  private ModuleListServerRpc			rpc = new ModuleListServerRpc() {
    
    @Override
    public void onClick(String itemId) {
      fireClicked(itemId);
    }
  };
}
