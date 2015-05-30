/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.kopi.vaadin.addons.ModuleItem;
import org.kopi.vaadin.addons.ModuleList;
import org.kopi.vaadin.addons.ModuleListEvent;
import org.kopi.vaadin.addons.ModuleListListener;
import org.kopi.vaadin.addons.WaitWindow;

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VMenuTree;

/**
 * A module menu implementation that uses the menu tree
 * model. This will not display a menu tree but an horizontal
 * menu with vertical sub menus drops.
 */
@SuppressWarnings("serial")
public class DModuleMenu extends ModuleList implements ModuleListListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the module menu from a menu tree model.
   * @param model The menu tree model.
   */
  public DModuleMenu(VMenuTree model) {
    this.model = model;
    modules = new HashMap<Integer, Module>();
    waitIndicator = new WaitWindow();
    setAutoOpen(false);
    setAnimationEnabled(true);
    // directly build menu content.
    buildModuleMenu(model.getRoot(), null);
    fillModulesMap();
    addModuleListListener(this);
  }

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Builds the menu bar from a root item and a parent menu item.
   * @param root The menu bar root item.
   * @param parent The parent menu item.
   */
  protected void buildModuleMenu(TreeNode root, ModuleItem parent) {
    for (int i = 0; i < root.getChildCount(); i++) {
      toModuleItem((DefaultMutableTreeNode) root.getChildAt(i), parent);
    }
  }
  
  /**
   * Converts the given {@link DefaultMutableTreeNode} to a {@link ModuleItem}
   * @param node The {@link DefaultMutableTreeNode} object.
   * @param parent The parent {@link ModuleItem} that holds the created menu item.
   */
  protected void toModuleItem(DefaultMutableTreeNode node, ModuleItem parent) {
    Module			module;
    ModuleItem			item;
    
    module = (Module)(node.getUserObject());
    if (parent != null) {
      item = parent.addItem(String.valueOf(module.getId()), // module ID.
	                    module.getDescription(), // module caption
	                    null, // item help are not injected for z-index issues.
	                    module.getObject() != null); // leaf module ?
    } else {
      // add it as a root module
      item = addItem(String.valueOf(module.getId()), // module ID.
                     module.getDescription(), // module caption
                     null, // item help are not injected for z-index issues.
                     module.getObject() != null);
    }
    // build module children
    for (int i = 0; i < node.getChildCount(); i++) {
      toModuleItem((DefaultMutableTreeNode) node.getChildAt(i), item);
    }
  }
  
  /**
   * Fills the module map in order to facilitate module
   * search for execution.
   */
  protected void fillModulesMap() {
    for (Module module : model.getModules()) {
      modules.put(module.getId(), module);
    }
  }
  
  /**
   * Returns the module having the given id.
   * @param id
   * @return
   */
  protected Module getModuleById(String id) {
    return modules.get(Integer.valueOf(id));
  }
  
  /**
   * Returns the menu model.
   * @return The menu model.
   */
  public VMenuTree getModel() {
    return model;
  }
  
  /**
   * Launches the given {@link Module}.
   * @param module The module to be launched.
   */
  protected void launchModule(final Module module) {
    BackgroundThreadHandler.start(new KopiAction("menu_form_started2") {

      @Override
      public void execute() throws VException {
	setWaitDialog();
	module.run(model.getDBContext());
	unsetWaitDialog();
	getApplication().push();
      }
    });
  }
  
  /**
   * Sets the wait info.
   */
  protected void setWaitDialog() {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
	waitIndicator.setText(MessageCode.getMessage("VIS-00067"));
	getApplication().attachComponent(waitIndicator);
	getApplication().push();
      }
    });
  }
  
  /**
   * Unsets the wait info.
   */
  protected void unsetWaitDialog() {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
	waitIndicator.setText(null);
	getApplication().detachComponent(waitIndicator);
	getApplication().push();
      }
    });
  }
  
  
  /**
   * Returns the current application instance.
   * @return the current application instance.
   */
  protected VApplication getApplication() {
    return (VApplication)ApplicationContext.getApplicationContext().getApplication();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void onClick(ModuleListEvent event) {
    // launch the selected module
    Module		module;
    
    module = getModuleById(event.getItemId());
    if (module != null) {
      launchModule(module);
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VMenuTree			model;
  private final HashMap<Integer, Module> 	modules;
  private final WaitWindow			waitIndicator;
}
