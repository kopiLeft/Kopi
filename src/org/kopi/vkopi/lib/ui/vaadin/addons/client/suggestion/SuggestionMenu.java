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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * The SuggestionMenu class is used for the display and selection of
 * suggestions in the SuggestBox widget. SuggestionMenu differs from MenuBar
 * in that it always has a vertical orientation, and it has no submenus. It
 * also allows for programmatic selection of items in the menu, and
 * programmatically performing the action associated with the selected item.
 * In the MenuBar class, items cannot be selected programatically - they can
 * only be selected when the user places the mouse over a particlar item.
 * Additional methods in SuggestionMenu provide information about the number
 * of items in the menu, and the index of the currently selected item.
 */
public class SuggestionMenu extends MenuBar {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public SuggestionMenu(boolean vertical) {
    super(vertical);
    // Make sure that CSS styles specified for the default Menu classes
    // do not affect this menu
    setStyleName("k-suggestBoxMenu");
    setFocusOnHoverEnabled(false);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Returns the number of items.
   * @return The number of items.
   */
  public int getNumItems() {
    return getItems().size();
  }

  /**
   * Returns the index of the menu item that is currently selected.
   *
   * @return returns the selected item
   */
  public int getSelectedItemIndex() {
    // The index of the currently selected item can only be
    // obtained if the menu is showing.
    MenuItem selectedItem = getSelectedItem();
    if (selectedItem != null) {
      return getItems().indexOf(selectedItem);
    }
    
    return -1;
  }

  /**
   * Selects the item at the specified index in the menu. Selecting the item
   * does not perform the item's associated action; it only changes the style
   * of the item and updates the value of SuggestionMenu.selectedItem.
   *
   * @param index index
   */
  public void selectItem(int index) {
    List<MenuItem> items = getItems();
    if (index > -1 && index < items.size()) {
      itemOverHack(items.get(index), false);
    }
  }
  
  @Override
  public MenuItem getSelectedItem() {
    return super.getSelectedItem();
  }

  /**
   * Set the IDs of the menu items.
   *
   * @param baseID the base ID
   */
  public void setMenuItemDebugIDs(String baseID) {
    int itemCount = 0;
    for (MenuItem item : getItems()) {
      item.ensureDebugId(baseID + "-item" + itemCount);
      itemCount++;
    }
  }
  
  /**
   * Sets the scroll top position.
   * @param scrollTop The top position.
   */
  public final void setScrollTop(int scrollTop) {
    getBodyElement().setScrollTop(scrollTop);
  }
  
  /**
   * Returns the table body element.
   * @return The table body element.
   */
  protected Element getBodyElement() {
    return DOM.getFirstChild(DOM.getChild(getElement(), 1));
  }
  
  /**
   * Calculates the visible region of the table rows.
   */
  protected void calculateVisibleRegion() {
    int		rowHeight = DOM.getFirstChild(getBodyElement()).getClientHeight() + 1;
    int		bodyHeight = getBodyElement().getClientHeight();
    int		scrollTop = getBodyElement().getScrollTop();
    int 	minVisibleRow = (int)(scrollTop / rowHeight);
    int 	maxVisibleRow = minVisibleRow + (int)(bodyHeight / rowHeight);
    
    if (getSelectedItemIndex() > maxVisibleRow) {
      setScrollTop(scrollTop + ((getSelectedItemIndex() - maxVisibleRow) * rowHeight));
    }
    if (getSelectedItemIndex() < minVisibleRow) {
      setScrollTop(scrollTop - ((minVisibleRow - getSelectedItemIndex()) * rowHeight));
    }
  }
  
  /**
   * Sets the animation type to roll down.
   */
  private native void itemOverHack(MenuItem item, boolean focus) /*-{
    this.@com.google.gwt.user.client.ui.MenuBar::itemOver(Lcom/google/gwt/user/client/ui/MenuItem;Z)(item, focus);
  }-*/;
  
  @Override
  protected void onLoad() {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
        // Set the menu height when it over flows the screen height
        if (getElement().getAbsoluteTop() + getElement().getClientHeight() > Window.getClientHeight()) {
          DOM.getFirstChild(DOM.getChild(getElement(), 1)).getStyle().setHeight(Window.getClientHeight() - getElement().getAbsoluteTop() - 10, Unit.PX);
          setWidth(getElement().getClientWidth() + 14 + "px"); // take scroll bar into consideration
        }
      }
    });
  }
}
