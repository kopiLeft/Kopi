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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.menu;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VAnchorPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A module list item that wraps a list element item.
 */
@SuppressWarnings("deprecation")
public class VModuleItem extends ComplexPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new module item widget.
   */
  public VModuleItem() {
    setElement(createInnerElement());
    tabLeft = new VSpanPanel();
    tabLeft.getElement().setInnerText(" ");
    tab = new VSpanPanel();
    tabRight = new VSpanPanel();
    tabRight.getElement().setInnerText(" ");
    anchor = new VAnchorPanel();
    anchor.setHref("#");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Creates the inner element of this module item.
   * @return The inner element of this module item.
   */
  protected com.google.gwt.dom.client.Element createInnerElement() {
    return Document.get().createLIElement();
  }
  
  /**
   * Builds the item content.
   */
  protected void buildContent() {
    super.clear();
    if (root) {
      // add all tabs
      add(tabLeft);
      add(tab);
      tab.add(anchor);
      add(tabRight);
    } else {
      // only add central anchor
      add(anchor);
    }
  }
  
  /**
   * Sets the selection style ability.
   * @param selected The selection style ability.
   */
  protected void setSelectionStyle(boolean selected) {
    if (root) {
      if (!selected) {
        tabLeft.setStyleName("notCurrentTabLeft");
        tab.setStyleName("notCurrentTab");
        tabRight.setStyleName("notCurrentTabRight");
      } else {
        tabLeft.setStyleName("currentTabLeft");
        tab.setStyleName("currentTab");
        tabRight.setStyleName("currentTabRight");
      }
    } else {
      if (selected) {
        addStyleName("selected");
      } else {
        removeStyleName("selected");
      }
    }
  }
  
  /**
   * Sets the parent item.
   * @param parent The parent parent.
   */
  public void setParentMenu(VModuleListMenu parent) {
    this.parentMenu = parent;
  }
  
  @Override
  public void add(Widget child) {
    super.add(child, getElement());
  }
  
  /**
   * Returns {@code true} if it is a root item.
   * @return {@code true} if it is a root item.
   */
  public boolean isRoot() {
    return root;
  }
  
  /**
   * Sets this item as root.
   * @param root It is a root item ?
   */
  public void setRoot(boolean root) {
    this.root = root;
  }
  
  /**
   * Returns the item caption.
   * @return The item caption.
   */
  public String getCaption() {
    return caption;
  }
  
  /**
   * Sets the item caption.
   * @param caption The item caption.
   */
  public void setCaption(String caption) {
    this.caption = caption;
    anchor.getElement().setInnerText(caption);
  }
  
  /**
   * Returns the item ID.
   * @return The item ID.
   */
  public String getId() {
    return id;
  }
  
  /**
   * Sets the item ID.
   * @param id The item ID.
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Returns the item parent;
   */
  public VModuleListMenu getParentMenu() {
    return parentMenu;
  }

  /**
   * Sets the scheduled command associated with this item.
   * @param cmd the scheduled command to be associated with this item
   */
  public void setScheduledCommand(ScheduledCommand cmd) {
    command = cmd;
  }

  /**
   * Gets the scheduled command associated with this item.
   *
   * @return this item's scheduled command, or <code>null</code> if none exists
   */
  public ScheduledCommand getScheduledCommand() {
    return command;
  }
  
  /**
   * An item is leaf when it has a command inside.
   * @return {@code true} if it is a leaf item.
   */
  public boolean isLeaf() {
    return command != null;
  }

  /**
   * Gets the sub-menu associated with this item.
   *
   * @return this item's sub-menu, or <code>null</code> if none exists
   */
  public VModuleListMenu getSubMenu() {
    return subMenu;
  }

  /**
   * Sets the sub-menu associated with this item.
   *
   * @param subMenu this item's new sub-menu
   */
  public void setSubMenu(VModuleListMenu subMenu) {
    this.subMenu = subMenu;
    if (parentMenu != null) {
      subMenu.setAutoOpen(true);
      subMenu.setAnimationEnabled(parentMenu.isAnimationEnabled());
      subMenu.setFocusOnHoverEnabled(parentMenu.isFocusOnHoverEnabled());
    }
    addChildrenIndicator();
  }
  
  /**
   * Adds the children indicator.
   */
  protected void addChildrenIndicator() {
    if (subMenu != null && !root && parentMenu != null) {
      // do not set submenu indicator for root elements.
      childrenIndicator = DOM.createAnchor();
      childrenIndicator.setInnerText(">>");
      childrenIndicator.setClassName("children-indicator");
      DOM.appendChild(getElement(), childrenIndicator);
    }
  }
  
  /**
   * Removes the children indicator.
   */
  protected void removeChildrenIndicator() {
    if (childrenIndicator != null) {
      DOM.removeChild(getElement(), childrenIndicator);
    }
  }
  
  @Override
  public void clear() {
    if (subMenu != null) {
      subMenu.clear();
    }
    
    super.clear();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VSpanPanel			tabLeft;
  private final VSpanPanel			tab;
  private final VSpanPanel			tabRight;
  protected final VAnchorPanel			anchor;
  
  private String				id;
  private String				caption;
  private boolean				root;
  private VModuleListMenu			parentMenu;
  private VModuleListMenu			subMenu;
  private ScheduledCommand			command;
  private Element				childrenIndicator;
}
