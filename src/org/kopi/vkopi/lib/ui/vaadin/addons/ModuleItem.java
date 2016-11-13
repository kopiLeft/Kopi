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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.ModuleItemState;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

/**
 * The module item model.
 */
@SuppressWarnings("serial")
public class ModuleItem extends AbstractComponent implements HasComponents {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new module item.
   * @param id The item ID.
   * @param caption The item caption.
   * @param description The item description
   * @param isLeaf Is it a leaf item.
   */
  public ModuleItem(String id,
                    String caption,
                    String description,
                    boolean isLeaf)
  {
    setImmediate(true);
    getState().itemId = id;
    setCaption(caption);
    setDescription(description);
    getState().isLeaf = isLeaf;
    children = new LinkedList<ModuleItem>();
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
  public ModuleItem addItem(String id,
                            String caption,
                            String description,
                            boolean isLeaf)
  {
    return addItem(new ModuleItem(id, caption, description, isLeaf));
  }
  
  /**
   * Adds a child item to this item.
   * @param newItem The item to be added.
   * @return The added item.
   */
  public ModuleItem addItem(ModuleItem newItem) {
    newItem.setParentItem(this);
    if (children == null) {
      children = new LinkedList<ModuleItem>();
    }
    children.add(newItem);
    newItem.setParent(this);
    
    return newItem;
  }
  
  /**
   * Checks if the item has children (if it is a sub-menu).
   * 
   * @return True if this item has children
   */
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  /**
   * For the containing item. This will return null if the item is in the
   * top-level menu bar.
   * 
   * @return The containing {@link ModuleItem} , ornull if there is none
   */
  public ModuleItem getParentItem() {
    return parent;
  }

  /**
   * This will return the children of this item or null if there are none.
   * 
   * @return List of children items, or null if there are none
   */
  public List<ModuleItem> getChildren() {
      return children;
  }

  /**
   * Gets the objects text
   * 
   * @return The text
   */
  public String getText() {
    return getCaption();
  }

  /**
   * Returns the number of children.
   * 
   * @return The number of child items
   */
  public int getSize() {
    if (children != null) {
      return children.size();
    }
    
    return -1;
  }

  /**
   * Get the unique identifier for this item.
   * @return The id of this item
   */
  public String getId() {
    return getState().itemId;
  }
  
  /**
   * Returns {@code true} if it is a leaf item.
   * @return {@code true} if it is a leaf item.
   */
  public boolean isLeaf() {
    return getState().isLeaf;
  }

  /**
   * Set the parent of this item. This is called by the addItem method.
   * @param parent The parent item
   */
  protected void setParentItem(ModuleItem parent) {
    this.parent = parent;
  }
  
  /**
   * <p>
   * Gets the items's description. The description can be used to briefly
   * describe the state of the item to the user. The description string
   * may contain certain XML tags:
   * </p>
   * 
   * <p>
   * <table border=1>
   * <tr>
   * <td width=120><b>Tag</b></td>
   * <td width=120><b>Description</b></td>
   * <td width=120><b>Example</b></td>
   * </tr>
   * <tr>
   * <td>&lt;b></td>
   * <td>bold</td>
   * <td><b>bold text</b></td>
   * </tr>
   * <tr>
   * <td>&lt;i></td>
   * <td>italic</td>
   * <td><i>italic text</i></td>
   * </tr>
   * <tr>
   * <td>&lt;u></td>
   * <td>underlined</td>
   * <td><u>underlined text</u></td>
   * </tr>
   * <tr>
   * <td>&lt;br></td>
   * <td>linebreak</td>
   * <td>N/A</td>
   * </tr>
   * <tr>
   * <td>&lt;ul><br>
   * &lt;li>item1<br>
   * &lt;li>item1<br>
   * &lt;/ul></td>
   * <td>item list</td>
   * <td>
   * <ul>
   * <li>item1
   * <li>item2
   * </ul>
   * </td>
   * </tr>
   * </table>
   * </p>
   * 
   * <p>
   * These tags may be nested.
   * </p>
   * 
   * @return item's description <code>String</code>
   */
  public String getDescription() {
    return super.getDescription();
  }
  
  @Override
  public Iterator<Component> iterator() {
    return new LinkedList<Component>(children).iterator();
  }
  
  @Override
  protected ModuleItemState getState() {
    return (ModuleItemState) super.getState();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private ModuleItem				parent;
  private List<ModuleItem>			children;
}
