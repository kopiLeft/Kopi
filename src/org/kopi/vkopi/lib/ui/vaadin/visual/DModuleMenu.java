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

package org.kopi.vkopi.lib.ui.vaadin.visual;

import java.io.File;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.kopi.vkopi.lib.ui.vaadin.addons.ModuleItem;
import org.kopi.vkopi.lib.ui.vaadin.addons.ModuleList;
import org.kopi.vkopi.lib.ui.vaadin.addons.ModuleListEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.ModuleListListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.WaitWindow;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.Module;
import org.kopi.vkopi.lib.visual.UMenuTree;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VMenuTree;
import org.kopi.vkopi.lib.visual.VlibProperties;

/**
 * A module menu implementation that uses the menu tree
 * model. This will not display a menu tree but an horizontal
 * menu with vertical sub menus drops.
 */
@SuppressWarnings("serial")
public class DModuleMenu extends ModuleList implements ModuleListListener, UMenuTree {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the module menu from a menu tree model.
   * @param model The menu tree model.
   */
  public DModuleMenu(VMenuTree model) {
    this.model = model;
    model.setDisplay(this);
    modules = new HashMap<Integer, Module>();
    waitIndicator = new WaitWindow();
    setAutoOpen(false);
    setAnimationEnabled(true);
    // directly build menu content.
    buildModuleMenu(model.getRoot(), null);
    fillModulesMap();
    addModuleListListener(this);
    maybeAddShortcuts();
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
    
    module = (Module) node.getUserObject();
    item = toModuleItem(module, parent);
    // build module children
    for (int i = 0; i < node.getChildCount(); i++) {
      toModuleItem((DefaultMutableTreeNode) node.getChildAt(i), item);
    }
  }
  
  /**
   * Converts the given {@link Module} to a {@link ModuleItem}
   * @param module The {@link Module} object.
   * @param parent The parent {@link ModuleItem} that holds the created menu item.
   * @return The created item for the given module.
   */
  protected ModuleItem toModuleItem(Module module, ModuleItem parent) {
    ModuleItem                  item;
    
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
    
    return item;
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
    performAsyncAction(new KopiAction("menu_form_started2") {

      @Override
      public void execute() throws VException {
        try {
          setWaitInfo(VlibProperties.getString("menu_form_started"));
          module.run(model.getDBContext());
        } finally {
          unsetWaitInfo();
        }
      }
    });
  }
  
  /**
   * Adds shortcuts menu item if necessary
   */
  protected final void maybeAddShortcuts() {
    if (!getModel().getShortcutsID().isEmpty()) {
      ModuleItem          favoriteItem;

      favoriteItem = addItem(FAVORITES_ITEM_ID, VlibProperties.getString("toolbar-title"), null, false);
      for (int i = 0; i < ((VMenuTree) getModel()).getShortcutsID().size() ; i++) {
        int       id = getModel().getShortcutsID().get(i).intValue();

        for (int j = 0; j < getModel().getModuleArray().length; j++) {
          if (getModel().getModuleArray()[j].getId() == id) {
            addShortcut(getModel().getModuleArray()[j], favoriteItem);
          }
        }
      }
    }
  }
  
  /**
   * Adds the given module to favorites
   * @param module The module to be added to book mark.
   */
  protected final void addShortcut(Module module, ModuleItem parent) {
    toModuleItem(module, parent);
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
  // MENU TREE IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void run() throws VException {}

  @Override
  public void setTitle(String title) {}

  @Override
  public void setInformationText(String text) {}

  @Override
  public void setTotalJobs(int totalJobs) {}

  @Override
  public void setCurrentJob(int currentJob) {}

  @Override
  public void updateWaitDialogMessage(String message) {}

  @Override
  public void closeWindow() {
    getApplication().logout();
  }

  @Override
  public void setWindowFocusEnabled(boolean enabled) {}

  @Override
  public void performBasicAction(KopiAction action) {
    action.run();
  }

  @Override
  public void performAsyncAction(final KopiAction action) {
    new Thread(new Runnable() {
      
      @Override
      public void run() {
        try {
          action.execute();
        } catch (VException e) {
          getApplication().error(e.getMessage());
        }
      }
    }).start();
  }

  @Override
  public void modelClosed(int type) {}

  @Override
  public void setWaitDialog(String message, int maxtime) {}
  
  @Override
  public void unsetWaitDialog() {}

  @Override
  public void setWaitInfo(final String message) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        waitIndicator.setText(message);
        getApplication().attachComponent(waitIndicator);
        getApplication().push();
      }
    });
  }

  @Override
  public void unsetWaitInfo() {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        waitIndicator.setText(null);
        getApplication().detachComponent(waitIndicator);
        getApplication().push();
      }
    });
  }

  @Override
  public void setProgressDialog(String message, int totalJobs) {}

  @Override
  public void unsetProgressDialog() {}

  @Override
  public void fileProduced(File file) {}

  @Override
  public UTree getTree() {
    return null;
  }

  @Override
  public UBookmarkPanel getBookmark() {
    return null;
  }

  @Override
  public void launchSelectedForm() throws VException {}

  @Override
  public void addSelectedElement() {}

  @Override
  public void setMenu() {}

  @Override
  public void removeSelectedElement() {}

  @Override
  public void gotoShortcuts() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        doItemAction(FAVORITES_ITEM_ID);
      }
    });
  }

  @Override
  public void showApplicationInformation(String message) {}

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VMenuTree			model;
  private final HashMap<Integer, Module> 	modules;
  private final WaitWindow			waitIndicator;
  private static final String                   FAVORITES_ITEM_ID = "favorites";
}
