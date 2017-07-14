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

package org.kopi.vkopi.lib.visual;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.kopi.vkopi.lib.l10n.LocalizationManager;

/**
 * A root menu must provide its ID and name. The root tree node
 * of this menu should be provided for further uses. 
 */
public class RootMenu {

  // ---------------------------------------------------------------------
  // CONSTRUCTOR
  // ---------------------------------------------------------------------
  
  /**
   * Creates a root menu from its ID and name.
   * @param id The root menu ID.
   * @param name The root menu name.
   */
  public RootMenu(int id, String name) {
    this.rootModule = new Module(id,
                                 0,
                                 name,
                                 ROOT_MENU_LOCALIZATION_RESOURCE,
                                 null,
                                 Module.ACS_PARENT,
                                 Integer.MAX_VALUE,
                                 null);
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------
  
  /**
   * Creates the module tree nodes for this root menu.
   * @param modules The accessible modules for the connected user.
   * @param isSuperUser Is the connected user is a super user ?
   */
  public void createTree(Module[] modules, boolean isSuperUser) {
    this.rootNode = createTree(modules, rootModule, false, isSuperUser);
  }

  
  /**
   * Creates the module tree for the given root module.
   * @param modules The accessible modules.
   * @param root The root module.
   * @param force Should we force module accessibility ?
   * @param isSuperUser Is the connected user is a super user ?
   * @return The local root tree node.
   */
  protected DefaultMutableTreeNode createTree(Module[] modules,
                                              Module root,
                                              boolean force,
                                              boolean isSuperUser)
  {
    if (root.getAccessibility() == Module.ACS_TRUE || isSuperUser) {
      force = true;
    }
    
    if (root.getObject() != null) {
      return force ? new DefaultMutableTreeNode(root) : null;
    } else {
      DefaultMutableTreeNode    self = null;

      for (int i = 0; i < modules.length; i++) {
        if (modules[i].getParent() == root.getId()) {
          DefaultMutableTreeNode        node;
          
          node = createTree(modules, modules[i], force, isSuperUser);
          if (node != null) {
            if (self == null) {
              self = new DefaultMutableTreeNode(root);
            }

            self.add(node);
          }
        }
      }
      
      return self;
    }
  }
  
  /**
   * Localizes this root menu.
   * @param manager The localization manager.
   */
  public void localize(LocalizationManager manager) {
    if (manager != null && rootModule != null) {
      rootModule.localize(manager);
    }
  }
  
  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------
  
  /**
   * return the identifier of the menu
   */
  public int getId() {
    return rootModule.getId();
  }
  
  /**
   * return the menu tree
   */
  public TreeNode getRoot() {
    return rootNode;
  }
  
  /**
   * Returns true if this root menu does not contain any module.
   * @return True if this root menu does not contain any module.
   */
  public boolean isEmpty() {
    return rootNode == null;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final Module                  rootModule;
  private TreeNode                      rootNode;
  /*package*/ static final String       ROOT_MENU_LOCALIZATION_RESOURCE = "org/kopi/vkopi/lib/resource/RootMenu";
}
