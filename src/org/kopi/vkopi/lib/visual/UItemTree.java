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
 * $Id$
 */

package org.kopi.vkopi.lib.visual;

import org.kopi.vkopi.lib.base.UComponent;

/**
 * {@code UItemTree} is the top-level interface that represents a tree.
 * The visible component is built from a {@link VTreeWindow} model.
 */
public interface UItemTree extends UWindow {

  /**
   * Returns The Tree instance
   *
   * @return a {@link UTreeComponent}
   */
  public UTreeComponent getTree();

  /**
   * Change item selection state
   *
   * @throws VException
   */
  public void setSelectedItem() throws VException;

  /**
   * Set selected item as default element
   *
   */
  public void setDefaultItem() throws VException;

  /**
   * Adds new item to the tree
   *
   */
  public void addItem() throws VException;

  /**
   * Sets the tree. This allows enabling and disabling item tree actors
   * according to the selected tree node.
   *
   */
  public void setTree();

  /**
   * Removes the selected item from the tree
   *
   */
  public void removeSelectedItem();

  /**
   * Edit the selected item
   *
   */
  public void editSelectedItem();

  /**
   * Localise the selected item
   *
   */
  public void localiseSelectedItem();


  /**
   * {@code UTreeComponent} is the top-level interface representing a visible tree
   * component.
   */
  public interface UTreeComponent extends UComponent {

     /**
     * Ensures that the node in the specified row is collapsed.
     *
     * @param row
     *            an integer specifying a display row, where 0 is the first
     *            row in the display
     */
    public void collapseRow(int row);

    /**
     * Ensures that the node in the specified row is expanded and
     * viewable.
     *
     * @param row  an integer specifying a display row, where 0 is the
     *             first row in the display
     */
    public void expandRow(int row);

    /**
     * Ensures that the selected nodes are expanded and viewable.
     */
    public void expandTree();

    /**
     * Returns the first row of the currently selected rows.
     * @return an integer that identifies the first row of currently selected rows
     */
    public int getSelectionRow();

    /**
     * Returns true if the node identified by the path is currently expanded,
     *
     * @param path  the path specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed,
     *               true if all nodes in the path are expanded
     */
    public boolean isExpanded(Object path);

    /**
     * Returns true if the value identified by path is currently collapsed,
     * this will return false if any of the values in path are currently
     * not being displayed.
     *
     * @param path  the path to check
     * @return true if any of the nodes in the node's path are collapsed,
     *               false if all nodes in the path are expanded
     */
    public boolean isCollapsed(Object path);

    /**
     * Returns the items of the tree as an array, returns null if there is no items
     * @return an array that contain the items of the tree
     */
    public Item[] getItems();

    /**
     * Returns the root item
     */
    public Item getRootItem();

    /**
     * Returns true if the item name done is unique in this tree
     */
    public boolean isUnique(String name);
  }
}
