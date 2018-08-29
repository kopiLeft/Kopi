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

import org.kopi.vkopi.lib.base.Image;


public class Item implements Comparable<Item> {

  // ---------------------------------------------------------------------
  // CONSTUCTOR
  // ---------------------------------------------------------------------

  public Item(int id,
              int parent,
              String name,
              String localisedName,
              String description,
              boolean selected,
              boolean defaultItem,
              String icon,
              String originalName)
  {
    this.id = id;
    this.parent = parent;
    this.name = name;
    this.localisedName = localisedName;
    this.description = description;
    this.selected = selected;
    this.defaultItem = defaultItem;
    this.originalName = originalName;
    if (icon != null) {
      this.icon = ImageHandler.getImageHandler().getImage(icon);
      smallIcon = ImageHandler.getImageHandler().getImage(icon);
      if (smallIcon == null) {
        smallIcon = smallIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
      }
    }
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------

  /**
   * Return the ident of the item
   */
  public int getId() {
    return id;
  }

  /**
   * Return the ident of the parent of the current item
   */
  public int getParent() {
    return parent;
  }

  /**
   * Sets the ident of the parent of the current item
   */
  public void setParent(int parent) {
    this.parent = parent;
  }

  /**
   * Return the current item name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the current item name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Return the current item original name
   */
  public String getOriginalName() {
    return originalName;
  }

  /**
   * Sets the current item original name
   */
  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  /**
   * Sets the current item name
   */
  public String getFormattedName(boolean localiserd) {
    if (localiserd && (id != -1)) {
      return "[" + name + "]  " + (localisedName != null ? localisedName : "........");
    } else {
      return name;
    }
  }

  /**
   * Return the current item localised name
   */
  public String getLocalisedName() {
    return localisedName;
  }

  /**
   * Sets the current item localised name
   */
  public void setLocalisedName(String localisedName) {
    this.localisedName = localisedName;
  }

  /**
   * Return the current item description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the current item description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Return the mnemonic
   */
  public Image getIcon() {
    return icon;
  }

  /**
   * Return the mnemonic
   */
  public Image getSmallIcon() {
    return smallIcon;
  }

  /**
   * Return true if the item is selected
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * Sets item selection state
   */
  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  /**
   * Return true if it is the default item
   */
  public boolean isDefaultItem() {
    return defaultItem;
  }

  /**
   * Sets to default item
   */
  public void setDefault(boolean defaultItem) {
    this.defaultItem = defaultItem;
  }

  /**
   * Return the name
   */
  public String toString() {
    return name;
  }

  /**
   * Return the item level
   */
  public int getLevel() {
    return level;
  }

  /**
   * Sets the item level
   */
  public void setLevel(int level) {
    this.level = level;
  }

  /**
   * Decrement the item level
   */
  public void decrementLevel() {
    this.level --;
  }

  /**
   * Return numbre of children
   */
  public int getChildCount() {
    return childCount;
  }

  /**
   * Sets numbre of children
   */
  public void setChildCount(int childCount){
    this.childCount = childCount;
  }

  /**
   * Return the children as an array
   */
  public Item[] getChildren() {
    return children;
  }

  /**
   * Sets children array
   */
  public void setChildren(Item[] children){
    this.children = children;
  }

  // ---------------------------------------------------------------------
  // COMPARABLE IMPLEMENTATION
  // ---------------------------------------------------------------------

  public int compareTo(Item item) {
    return (id == item.id
           && parent == item.parent
           && name.equals(item.name)
           && localisedName.equals(item.localisedName)
           && description.equals(item.description)
           && selected == item.selected
           && defaultItem == item.defaultItem) ? 1 : -1;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int                           id;
  private int                           parent;
  private String                        name;
  private String                        localisedName;
  private String                        description;
  private boolean                       selected;
  private boolean                       defaultItem;
  private String                        originalName;
  private Image                         icon;
  private Image                         smallIcon;
  private int                           childCount;
  private Item[]                        children;
  private int                           level;
}
