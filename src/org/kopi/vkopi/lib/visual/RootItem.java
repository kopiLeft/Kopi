/*
 * Copyright (c) 1990-2018 kopiRight Managed Solutions GmbH
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
 * $Id:$
 */

package org.kopi.vkopi.lib.visual;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * A root item must provide its ID and name. The root tree node
 * of this item should be provided for further uses.
 */
public class RootItem {

  // ---------------------------------------------------------------------
  // CONSTRUCTOR
  // ---------------------------------------------------------------------

  /**
   * Creates a root item from its ID and name.
   * @param id The root item ID.
   * @param name The root item name.
   */
  public RootItem(int id, String name) {
    this.rootItem = new Item(id,
                             0,
                             name,
                             null,
                             null,
                             false,
                             false,
                             null,
                             name);
    this.rootItem.setLevel(0);
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------

  /**
   * Creates the item tree nodes for this root item.
   * @param items The accessible items for the connected user.
   * @param isSuperUser Is the connected user is a super user ?
   */
  public void createTree(Item[] items) {
    this.rootNode = createTree(items, rootItem);
  }

  /**
   * Creates the item tree for the given root item.
   * @param items The accessible items.
   * @param root The root item.
   * @param force Should we force item accessibility ?
   * @param isSuperUser Is the connected user is a super user ?
   * @return The local root tree node.
   */
  protected DefaultMutableTreeNode createTree(Item[] items, Item root) {
    DefaultMutableTreeNode      self = null;
    int                         childsCount = 0;

    for (int i = 0; i < items.length; i++) {
      if (items[i].getParent() == root.getId()) {
        DefaultMutableTreeNode          node;

        childsCount++;
        items[i].setLevel(root.getLevel() + 1);
        node = createTree(items, items[i]);
        if (node != null) {
          if (self == null) {
            self = new DefaultMutableTreeNode(root);
          }

          self.add(node);
        }
      }
    }
    if (childsCount == 0) {
      return new DefaultMutableTreeNode(root);
    } else {
      return self;
    }
  }

  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------

  /**
   * Return the root node
   */
  public TreeNode getRoot() {
    return rootNode;
  }

  /**
   * Returns true if this root item does not contain any item.
   * @return True if this root item does not contain any item.
   */
  public boolean isEmpty() {
    return rootNode == null;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final Item                    rootItem;
  private TreeNode                      rootNode;
}
