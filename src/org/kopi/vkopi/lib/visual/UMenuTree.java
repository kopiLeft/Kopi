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

import org.kopi.vkopi.lib.base.UComponent;

/**
 * {@code UMenuTree} is the top-level interface that represents the application
 * menu tree. The visible component is built from a {@link VMenuTree} model.
 */
public interface UMenuTree extends UWindow {
    
  /**
   * Returns The Tree instance
   *   
   * @return a {@link UTree}
   */
  public UTree getTree();
  
  /**
   * Returns The favorites panel
   * 
   * @return a {@link UBookmarkPanel}
   */
  public UBookmarkPanel getBookmark();
  
  /**
   * This method launches the selected form in the menu tree and throws 
   * a VException
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   * 
   * @throws VException
   */
  public void launchSelectedForm() throws VException;
  
  /**
   * Adds the selected module in the menu tree to favorites
   * 
   */
  public void addSelectedElement();
  
  /**
   * Sets the menu tree menu. This allows enabling and disabling menu tree actors
   * according to the selected tree node.
   * 
   */
  public void setMenu();
  
  /**
   * Removes the selected module in the menu tree from favorites
   * 
   */
  public void removeSelectedElement();
  
  /**
   * Move the focus from the activated frame to favorites frame.
   */
  public void gotoShortcuts();
  
  /**
   * Shows the application information
   * @param message The message to be shown as application information 
   */
  public void showApplicationInformation(String message);

  /**
   * {@code UTree} is the top-level interface representing a visible tree
   * component.  
   */
  public interface UTree extends UComponent {
      
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
  }
  
  /**
   * {@code UBookmarkPanel} is the top-level interface representing a bookmark
   * panel or window.
   */
  public interface UBookmarkPanel extends UComponent {
     /**
     * Makes the {@code UBookmarkPanel} visible. If the panel and/or its owner
     * are not yet displayable, both are made displayable.  The
     * {@code UBookmarkPanel} will be validated prior to being made visible.
     * @see UComponent#setVisible(boolean).
     */
    public void show();
    
    /**
     * If this {@code UBookmarkPanel} is visible, brings this panel to the front and may make
     * it the focused Window.
     */
    public void toFront();
  }
}
