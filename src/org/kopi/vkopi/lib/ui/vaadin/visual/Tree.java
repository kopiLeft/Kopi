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

package org.kopi.vkopi.lib.ui.vaadin.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;
import org.kopi.vkopi.lib.visual.Item;
import org.kopi.vkopi.lib.visual.UItemTree.UTreeComponent;

import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MultiSelectMode;

/**
 * The vaadin implementation of an {@link UTreeComponent}.
 * The component is based on the {@link com.vaadin.ui.Tree}
 */
public class Tree extends com.vaadin.ui.Tree implements UTreeComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>Tree</code> instance.
   * @param root The root tree item.
   * @param isNoEdit Is it a super user ?
   */
  public Tree(TreeNode root, boolean isNoEdit, boolean localised) {
    this.isNoEdit = isNoEdit;
    this.localised = localised;
    setImmediate(true);
    setSizeFull();
    setMultiselectMode(MultiSelectMode.SIMPLE);
    setContainerDataSource(build(root));
    setItemCaptionPropertyId(ITEM_PROPERTY_NAME);
    setItemIconPropertyId(ITEM_PROPERTY_ICON);
    setItemCaptionMode(ItemCaptionMode.PROPERTY);
    // addStyleName(KopiTheme.TREE_STYLE);
  }

  //----------------------------------------------------------
  // IMPLEMENTATIONS
  //----------------------------------------------------------

  /**
   * Builds the tree container from a root item.
   * @param root The tree root item.
   * @return The tree container.
   */
  protected Container build(TreeNode root) {
    HierarchicalContainer       container;

    container = new HierarchicalContainer();
    container.addContainerProperty(ITEM_PROPERTY, Item.class, null);
    container.addContainerProperty(ITEM_PROPERTY_NAME, String.class, null);
    container.addContainerProperty(ITEM_PROPERTY_ICON, Resource.class, null);
    // Add the root node
    addNode(container, root);

    return container;
  }

  /**
   * Adds an item node in the container.
   * @param container The tree container.
   * @param node The node to be added to the tree.
   */
  @SuppressWarnings("unchecked")
  private void addNode(HierarchicalContainer container, TreeNode node) {
    Item                        item = (Item)((DefaultMutableTreeNode)node).getUserObject();
    com.vaadin.data.Item        dataItem = container.addItem(item.getId());

    dataItem.getItemProperty(ITEM_PROPERTY).setValue(item);
    dataItem.getItemProperty(ITEM_PROPERTY_NAME).setValue(item.getFormattedName(localised));
    dataItem.getItemProperty(ITEM_PROPERTY_ICON).setValue(item.getIcon());
    container.setParent(item.getId(), item.getParent());
    container.setChildrenAllowed(item.getId(), !node.isLeaf());
    setIcon(item, node.isLeaf());

    // Add children nodes
    for (int i = 0; i < node.getChildCount(); i++) {
      addNode(container, node.getChildAt(i));
    }
  }

  /**
   * Sets the item icon.
   * @param item The item.
   * @param isLeaf Is it a leaf tree item ?
   */
  public void setIcon(Item item, boolean isLeaf) {
    if (isNoEdit || (item.getId() == -1)) {
      if (isLeaf) {
        setItemIcon(item.getId(), Utils.getImage("item.png").getResource());
      } else {
        setItemIcon(item.getId(), Utils.getImage("node.png").getResource());
      }
    } else {
      setIcon(item);
    }

    lastModifiedItemId = item.getId();
  }

  /**
   * Restores the last modified tree item.
   */
  public void restoreLastModifiedItem() {
    if (lastModifiedItemId != null && !areChildrenAllowed(lastModifiedItemId)) {
      setItemIcon(lastModifiedItemId, Utils.getImage("forms.png").getResource());
    }
  }

  /**
   * Set icon according to item status.
   * @param item The tree item .
   */
  public void setIcon(final Item item) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        if (item.getId() != -1) {
          if (item.isDefaultItem()) {
            setItemIcon(item.getId(), Utils.getImage("default.png").getResource());
          } else {
            if (item.isSelected()) {
              setItemIcon(item.getId(), Utils.getImage("checked.png").getResource());
            } else  {
              setItemIcon(item.getId(), Utils.getImage("unchecked.png").getResource());
            }
          }
        }
      }
    });
  }

  /**
   * Returns the {@link HierarchicalContainer} of this tree.
   */
  public HierarchicalContainer getContainerDataSource() {
    return (HierarchicalContainer) super.getContainerDataSource();
  }

  /**
   * The filter accepts items for which toString() of the value of the given property
   * contains or starts with given filterString.
   * <p>Other items are not visible in the container when filtered.</p>
   * @param text String that must match the value of the property.
   * @param ignoreCase Determine if the casing can be ignored when comparing strings.
   * @param onlyMatchPrefix Only match prefixes; no other matches are included.
   */
  public void filter(final String text, final boolean ignoreCase, final boolean onlyMatchPrefix) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        getContainerDataSource().removeAllContainerFilters();
        getContainerDataSource().addContainerFilter(ITEM_PROPERTY_NAME,
                                                    text,
                                                    ignoreCase,
                                                    onlyMatchPrefix);
        if (text.length() != 0) {
          expandItemsRecursively(getContainerDataSource().firstItemId());
        } else {
          collapseItemsRecursively(0);
          expandItem(0);
        }
      }
    });
  }

  /**
   * Returns the {@link ITEM} corresponding of the given tree item.
   * @param itemId The tree item ID.
   * @return The corresponding tree item
   */
  public Item getITEM(Object itemId) {
    com.vaadin.data.Item dataItem = getItem(itemId);

    if (dataItem != null) {
      return (Item)dataItem.getItemProperty(Tree.ITEM_PROPERTY).getValue();
    }

    return null;
  }

  /**
   * Emits the value change event. The value contained in the field is validated before the event is created.
   */
  public void valueChanged() {
    fireValueChange(false);
  }

  //----------------------------------------------------------
  // TREE IMPLEMENTATION
  //----------------------------------------------------------

  @Override
  public void collapseRow(final int row) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        collapseItem(row);
      }
    });
  }

  @Override
  public void expandRow(final int row) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        expandItem(row);
      }
    });
  }

  /**
   * Ensures that the selected nodes are expanded and viewable.
   */
  @Override
  public void expandTree() {
    expandNode(-1);
  }

  /**
   * Return items list
   */
  public void expandNode(Object node) {
    Item                        item;
    Collection<?>               childrenCollection;

    item = (Item)getITEM(node);

    childrenCollection = getChildren(node);
    if (childrenCollection != null && childrenCollection.size() > 0) {
      Object[]                  childrenList;

      childrenList = childrenCollection.toArray();
      for (int i = 0; i < childrenList.length; i++) {
        expandNode(childrenList[i]);
      }
    }
    if (item.isSelected() || isExpanded(node)) {
      expandRow(item.getParent());
    }
  }

  @Override
  public int getSelectionRow() {
    if (getValue() == null) {
      return 0;
    }

    return ((Integer)getValue()).intValue();
  }

  @Override
  public boolean isCollapsed(Object path) {
    return !isExpanded(path);
  }

  /**
   * Return items array
   */
  @Override
  public Item[] getItems() {
    List<Item> itemsList = getItems(getItem(-1));

    if (itemsList != null) {
      return (Item[])Utils.toArray(itemsList, Item.class);
    }

    return null;
  }

  /**
   * Return true if the item name done is unique in this tree
   */
  public boolean isUnique(String name) {
    return isUnique(name, -1);
  }

  /**
   * Return true if the item name done is unique in this tree
   */
  public boolean isUnique(String name, Object node) {
    Item                        item;
    Collection<?>               childrenCollection;

    item = (Item)getITEM(node);

    if (name.equals(item.getName()) && (item.getId() != (int)getValue())) {
      return false;
    } else {
      childrenCollection = getChildren(node);
      if (childrenCollection !=  null && childrenCollection.size() > 0) {
        Object[] childrenList = childrenCollection.toArray();
        boolean isUnique = true;
        int i = 0;
        while (isUnique && i < childrenList.length) {
          isUnique = isUnique(name, childrenList[i]);
          i++;
        }
        return isUnique;
      } else {
        return true;
      }
    }
  }

  /**
   * Return items list
   */
  public List<Item> getItems(Object node) {
    List<Item>                  items;
    Item                        item;
    Collection<?>               childrenCollection;

    items = new ArrayList<Item>();
    item = (Item)getITEM(node);
    if (item.getId() >= 0) {
      items.add(item);
    }

    childrenCollection = getChildren(node);
    if (childrenCollection !=  null && childrenCollection.size() > 0) {
      Object[] childrenList = childrenCollection.toArray();

      item.setChildCount(childrenList.length);
      for (int i = 0; i < childrenList.length; i++) {
        items.addAll(getItems(childrenList[i]));
      }
    } else {
      item.setChildCount(0);
    }

    return items;
  }

  /**
   * Return items array
   */
  @Override
  public Item getRootItem() {
    return getRootItem(-1);
  }

  /**
   * Return items list
   */
  public Item getRootItem(Object node) {
    Item                        item;
    Collection<?>               childrenCollection;

    item = (Item)getITEM(node);

    childrenCollection = getChildren(node);
    if (childrenCollection !=  null && childrenCollection.size() > 0) {
      Item[]                    children;
      Object[]                  childrenList;

      childrenList = childrenCollection.toArray();
      children = new Item[childrenList.length];
      for (int i = 0; i < childrenList.length; i++) {
        children[i] = getRootItem(childrenList[i]);
      }
      item.setChildCount(childrenList.length);
      item.setChildren(children);
    } else {
      item.setChildCount(0);
      item.setChildren(null);
    }

    return item;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private Object                        lastModifiedItemId;
  private boolean                       isNoEdit;
  private boolean                       localised;

  public static final String            ITEM_PROPERTY_NAME      = "name";
  public static final String            ITEM_PROPERTY_ICON      = "icon";
  public static final String            ITEM_PROPERTY           = "item";
  private static final long             serialVersionUID        = 2081395525463017815L;
}
