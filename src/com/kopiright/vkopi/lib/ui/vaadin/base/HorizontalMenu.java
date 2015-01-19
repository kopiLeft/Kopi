/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.kopi.vaadin.menubar.MenuBar;
import org.kopi.vaadin.menubar.client.MenuBarState;

import com.kopiright.vkopi.lib.visual.Module;

/**
 * The <code>HorizontalMenu</code> is an horizontal
 * menu using standard vaadin {@link MenuBar}.
 */
@SuppressWarnings("serial")
public class HorizontalMenu extends MenuBar {
    
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>HorizontalMenu</code> from the root item.
   * @param root The root item of the menu.
   */
  public HorizontalMenu(TreeNode root) {
    modules = new Hashtable<String, Module>();
    buildMenu(root, null);
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Builds the menu bar from a root item and a parent menu item.
   * @param root The menu bar root item.
   * @param parent The parent menu item.
   */
  public void buildMenu(TreeNode root, MenuItem parent) {
    for (int i = 0; i < root.getChildCount(); i++) {
      toMenuItem((DefaultMutableTreeNode) root.getChildAt(i), null);
    }
  }
  
  /**
   * Converts the given {@link DefaultMutableTreeNode} to a {@link MenuItem}
   * @param node The {@link DefaultMutableTreeNode} object.
   * @param parent The parent {@link MenuItem} that holds the created menu item.
   */
  public void toMenuItem(DefaultMutableTreeNode node, MenuItem parent) {
    Module		module;
    MenuItem		item;
    
    module = (Module)(node.getUserObject());
    modules.put(module.getDescription(), module);
    
    if (parent != null) {
      item = parent.addItem(module.getDescription(), null, null);
    } else {
      item = addItem(module.getDescription(), null, null);
    }
    
    for (int i = 0; i < node.getChildCount(); i++) {
      toMenuItem((DefaultMutableTreeNode) node.getChildAt(i), item);
    }
  }
      
  /**
   * Returns the module corresponding to the given menu item.
   * @param item The menu item.
   * @return The {@link Module} corresponding to the menu item.
   */
  public Module getModule(MenuItem item) {
    if (item == null) {
      return null;
    }
    
    return modules.get(item.getText());
  }
  
  /**
   * Returns the selected {@link MenuItem}.
   * @return The selected {@link MenuItem}.
   */
  public MenuItem getSelectedItem() {
    MenuItem selectedItem = null;
    
    for(int i = 0; i< getItems().size(); i++) {
      if(getItems().get(i).isEnabled()) {
	selectedItem = getItems().get(i);
      }
    }
    
    return selectedItem;    
  }
  
  /**
   * @Override
   */
  protected MenuBarState getState() {
    return super.getState();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  public Hashtable<String, Module>		modules;
}