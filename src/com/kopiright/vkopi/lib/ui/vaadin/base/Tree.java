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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.UMenuTree.UTree;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Component;

/**
 * The vaadin implementation of an {@link UTree}.
 * The component is based on the {@link com.vaadin.ui.Tree}
 */
public class Tree extends com.vaadin.ui.Tree implements UTree {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>Tree</code> instance.
   * @param root The root tree item.
   * @param isSuperUser Is it a super user ?
   */
  public Tree(TreeNode root, boolean isSuperUser) {
    this.isSuperUser = isSuperUser;
    setImmediate(true);
    setSizeFull();
    setMultiselectMode(MultiSelectMode.SIMPLE);
    setContainerDataSource(build(root));
    setItemCaptionPropertyId(MODULE_PROPERTY_NAME);
    setItemIconPropertyId(MODULE_PROPERTY_ICON);
    setItemCaptionMode(ItemCaptionMode.PROPERTY);
    setItemDescriptionGenerator(new ItemDescriptionHandler());
    addStyleName(KopiTheme.TREE_STYLE);
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
    HierarchicalContainer	container;
    
    container = new HierarchicalContainer();
    container.addContainerProperty(MODULE_PROPERTY, Module.class, null);
    container.addContainerProperty(MODULE_PROPERTY_NAME, String.class, null);
    container.addContainerProperty(MODULE_PROPERTY_ICON, Resource.class, null);
    // Add the root node
    addNode(container, root);
    
    return container;
  }
  
  /**
   * Adds a module nodes in the container.
   * @param container The tree container.
   * @param node The node to be added to the tree.
   */
  @SuppressWarnings("unchecked")
  private void addNode(HierarchicalContainer container, TreeNode node) {
    Module	module = (Module) ((DefaultMutableTreeNode)node).getUserObject();
    Item 	item = container.addItem(module.getId());
    
    item.getItemProperty(MODULE_PROPERTY).setValue(module);
    item.getItemProperty(MODULE_PROPERTY_NAME).setValue(module.getDescription());
    item.getItemProperty(MODULE_PROPERTY_ICON).setValue(module.getIcon());
    container.setParent(module.getId(), module.getParent());
    container.setChildrenAllowed(module.getId(), !node.isLeaf());
    setIcon(module.getId(), node.isLeaf(), node.getParent() == null, module.getAccessibility());
    // Add children nodes
    for (int i = 0; i < node.getChildCount(); i++) {
      addNode(container, node.getChildAt(i));
    }
  }
  
  /**
   * Sets the item icon.
   * @param itemId The item ID.
   * @param isLeaf Is it a leaf tree item ?
   * @param isRoot Is it a root tree item ?
   * @param access The tree item access.
   */
  public void setIcon(Object itemId, boolean isLeaf, boolean isRoot, int access) {
    if (isRoot) {
      setItemIcon(itemId, Utils.getImage("home.png").getResource());
    } else {
      if (isLeaf) {
	if (!isSuperUser) {
	  if (itemId.equals(getValue())) {
	    setItemIcon(itemId, Utils.getImage("form_selected.png").getResource());
	  } else {
	    setItemIcon(itemId, Utils.getImage("forms.png").getResource());
	  }
	} else {
	  setIcon(access, itemId, true);
	}
      } else {
	if (!isSuperUser) {
	  if (isExpanded(itemId)) {
	    setItemIcon(itemId, Utils.getImage("expanded.png").getResource());
	  } else {
	    setItemIcon(itemId, Utils.getImage("collapsed.png").getResource());
	  }
	} else {
	  setIcon(access, itemId, false);
	}
      }
    }
    
    lastModifiedItemId = itemId;
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
   * Set icon according to module accessibility.
   * @param access The module accessibility.
   * @param itemId The tree item ID.
   * @param isLeaf Is it a leaf node ?
   */
  public void setIcon(final int access, final Object itemId, final boolean isLeaf) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {   
        switch (access) {
        case Module.ACS_FALSE:
          if (isLeaf) {
	    setItemIcon(itemId, Utils.getImage("form_p.png").getResource());
          } else {
	    if (isExpanded(itemId)) {
	      setItemIcon(itemId, Utils.getImage("expanded_p.png").getResource());
	    } else {
	      setItemIcon(itemId, Utils.getImage("collapsed_p.png").getResource());
	    }
          }
          break;
        case Module.ACS_TRUE:
          if (isLeaf) {
	    setItemIcon(itemId, Utils.getImage("form_a.png").getResource());
          } else {
	    if (isExpanded(itemId)) {
	      setItemIcon(itemId, Utils.getImage("expanded_a.png").getResource());
	    } else {
	      setItemIcon(itemId, Utils.getImage("collapsed_a.png").getResource());
	    }
          }
          break;
        default:
          if (isLeaf) {
	    setItemIcon(itemId, Utils.getImage("forms.png").getResource());
          } else {
	    if (isExpanded(itemId)) {
	      setItemIcon(itemId, Utils.getImage("expanded.png").getResource());
	    } else {
	      setItemIcon(itemId, Utils.getImage("collapsed.png").getResource());
	    }
          }
          break;
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
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        getContainerDataSource().removeAllContainerFilters();
        getContainerDataSource().addContainerFilter(MODULE_PROPERTY_NAME,
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
   * Returns the {@link Module} corresponding of the given tree item.
   * @param itemId The tree item ID.
   * @return The corresponding tree item module.
   */
  public Module getModule(Object itemId) {
    Item item = getItem(itemId);
    
    if (item != null) {
      return (Module)item.getItemProperty(Tree.MODULE_PROPERTY).getValue();
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
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() { 
        collapseItem(row);
      }
    });
  }

  @Override
  public void expandRow(final int row) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        expandItem(row);
      }
    });
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
  
  @SuppressWarnings("serial")
  private class ItemDescriptionHandler implements ItemDescriptionGenerator {

    @Override
    public String generateDescription(Component source,
	                               Object itemId,
	                               Object propertyId)
    {
      if (source == Tree.this) {
	
	Module module = getModule(itemId);
	if (module != null) {
	  return module.getHelp();
	}
      }
      
      return null;
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Object			lastModifiedItemId;
  private boolean			isSuperUser;
  
  public static final String		MODULE_PROPERTY_NAME = "name";
  public static final String		MODULE_PROPERTY_ICON = "icon";
  public static final String		MODULE_PROPERTY = "module";
  private static final long 		serialVersionUID = 7209478283052268611L;
}
