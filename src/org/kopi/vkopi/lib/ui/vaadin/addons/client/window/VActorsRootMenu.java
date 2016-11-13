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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.window;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleItem;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.VModuleListMenu;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.vaadin.client.ApplicationConnection;

/**
 * The actors root menu widget.
 * This menu aims to show a menu of the available actors for a given window.
 * This is done to show actors that have no icon and no accelerator keys.
 */
public class VActorsRootMenu extends VModuleListMenu {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the actors root menu widget.
   * @param connection The application connection.
   */
  public VActorsRootMenu(ApplicationConnection connection) {
    super(connection, true);
    setStyleName("actors-rootMenu");
    setAutoOpen(false);
    setFocusOnHoverEnabled(false);
    setAnimationEnabled(true);
    doNotCorrectPopupPosition();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected Element createInnerElement() {
    return Document.get().createSpanElement();
  }

  /**
   * Creates the root item of this actors menu.
   * @param caption The item caption.
   * @param image The item image.
   */
  public void createRootItem(String caption) {
    rootItem = (RootItem) addItem(new RootItem(), false);
    if (caption != null) {
      rootItem.setCaption(caption);
    }

    rootItem.setStyleName("actors-rootItem");
  }
  
  /**
   * Sets the actors menu to be shown by this root menu.
   * @param subMenu The sub menu to be shown.
   */
  public void setActorsMenu(VModuleListMenu subMenu) {
    if (rootItem != null) {
      rootItem.setSubMenu(subMenu);
      subMenu.setAutoOpen(true);
      subMenu.setFocusOnHoverEnabled(false);
      subMenu.setAnimationEnabled(true);
    }
  }
  
  /**
   * Returns the icon DOM element.
   * @return The icon DOM element.
   */
  protected Element getIconElement() {
    return rootItem.getIconElement();
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * The root item that shows the actors sub menu.
   * This is a special item where DOM content is changed
   * to feet with the actor panel.
   */
  private class RootItem extends VModuleItem {

    //---------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------
    
    /**
     * Creates the root item widget.
     */
    public RootItem() {
      label = new VSpan();
      icon = new VIcon();
      anchor.add(icon);
      anchor.add(label);
      setRoot(false);
      icon.addStyleDependentName("actors");
    }

    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    
    @Override
    protected Element createInnerElement() {
      return Document.get().createSpanElement();
    }
    
    @Override
    public void setCaption(String caption) {
      label.setText(caption);
    }
    
    @Override
    protected void addChildrenIndicator() {
      // do not create child indicator
    }
    
    /**
     * Returns the icon DOM element.
     * @return The icon DOM element.
     */
    public Element getIconElement() {
      return icon.getElement();
    }

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    private final VSpan                         label;
    private final VIcon                         icon;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private RootItem                              rootItem;
}
