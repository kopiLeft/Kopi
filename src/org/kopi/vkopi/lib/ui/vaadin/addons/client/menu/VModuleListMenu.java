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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;

/**
 * The module list menu bar.
 */
@SuppressWarnings("deprecation")
public class VModuleListMenu extends ComplexPanel implements HasAnimation, PopupListener, HasCloseHandlers<PopupPanel> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the module list menu bar.
   */
  public VModuleListMenu(ApplicationConnection connection, boolean root) {
    this.connection = connection;
    this.vertical = !root; // root menu is always horizontal. sub menus are vertical
    setElement(createInnerElement());
    sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONFOCUS | Event.ONKEYDOWN);
    // Hide focus outline in Mozilla/Webkit/Opera
    getElement().getStyle().setProperty("outline", "0px");
    // Hide focus outline in IE 6/7
    getElement().setAttribute("hideFocus", "true");
    // Deselect items when blurring without a child menu.
    addDomHandler(new BlurHandler() {
      
      @Override
      public void onBlur(BlurEvent event) {
        if (shownChildMenu == null) {
          selectItem(null);
        }
      }
    }, BlurEvent.getType());
    getElement().setTabIndex(0);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Creates the inner element of this module menu.
   * @return The inner element of the complex panel.
   */
  protected Element createInnerElement() {
    return Document.get().createULElement();
  }
  
  @Override
  public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler) {
    return addHandler(handler, CloseEvent.getType());
  }

  @Override
  public void onPopupClosed(PopupPanel sender, boolean autoClosed) {

    // If the menu popup was auto-closed, close all of its parents as well.
    if (autoClosed) {
      closeAllParents();
    }

    onHide(!autoClosed && focusOnHover);
    CloseEvent.fire(VModuleListMenu.this, sender);
    // When the menu popup closes, remember that no item is
    // currently showing a popup menu.
    shownChildMenu = null;
    popup = null;
    if (parentMenu != null && parentMenu.popup != null) {
      parentMenu.popup.setPreviewingAllNativeEvents(true);
    }
  }

  @Override
  public boolean isAnimationEnabled() {
    return isAnimationEnabled;
  }

  @Override
  public void setAnimationEnabled(boolean enable) {
    isAnimationEnabled = enable;
  }

  @Override
  public void onBrowserEvent(Event event) {
    VModuleItem		item = findItem(DOM.eventGetTarget(event));
    
    switch (DOM.eventGetType(event)) {
      case Event.ONCLICK: {
	focus();
        // Fire an item's command when the user clicks on it.
        if (item != null) {
          doItemAction(item, true, true);
        }
        break;
      }

      case Event.ONMOUSEOVER: {
        if (item != null) {
          itemOver(item, true);
        }
        break;
      }

      case Event.ONMOUSEOUT: {
        if (item != null) {
          itemOver(null, false);
        }
        break;
      }

      case Event.ONFOCUS: {
        selectFirstItemIfNoneSelected();
        break;
      }

      case Event.ONKEYDOWN: {
        int keyCode = event.getKeyCode();
        boolean isRtl = LocaleInfo.getCurrentLocale().isRTL();
        keyCode = KeyCodes.maybeSwapArrowKeysForRtl(keyCode, isRtl);
        switch (keyCode) {
        case KeyCodes.KEY_LEFT:
          moveToPrevItem();
          eatEvent(event);
          break;
        case KeyCodes.KEY_RIGHT:
          moveToNextItem();
          eatEvent(event);
          break;
        case KeyCodes.KEY_UP:
          moveSelectionUp();
          eatEvent(event);
          break;
        case KeyCodes.KEY_DOWN:
          moveSelectionDown();
          eatEvent(event);
          break;
        case KeyCodes.KEY_ESCAPE:
          closeAllParentsAndChildren();
          eatEvent(event);
          break;
        case KeyCodes.KEY_TAB:
          closeAllParentsAndChildren();
          break;
        case KeyCodes.KEY_ENTER:
          if (!selectFirstItemIfNoneSelected()) {
            doItemAction(selectedItem, true, true);
            eatEvent(event);
          }
          break;
        } // end switch(keyCode)

        break;
      } // end case Event.ONKEYDOWN
    } // end switch (DOM.eventGetType(event))
    
    super.onBrowserEvent(event);
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        if (requiredHeight == 0) {
          requiredHeight = WidgetUtil.getRequiredHeight(VModuleListMenu.this);
        }
        if (requiredHeight + getAbsoluteTop() > Window.getClientHeight() - 24) {
          getElement().getStyle().setHeight(Window.getClientHeight() - (24 + getAbsoluteTop()), Unit.PX);
          getElement().getStyle().setOverflowX(Overflow.AUTO);
        }
      }
    });
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------

  /**
   * Adds a menu item to the bar.
   *
   * @param item the item to be added
   * @return the {@link VModuleItem} object
   */
  public VModuleItem addItem(VModuleItem item, boolean root, boolean shortcutNavigation) {
    item.setRoot(root);
    item.setShortcutNavigation(shortcutNavigation);
    item.buildContent();
    return insertItem(item);
  }
  
  /**
   * Removes an item from this bar.
   * @param item The item to be removed
   */
  public void removeItem(VModuleItem item) {
    if (item != null) {
      items.remove(item);
      item.setParentMenu(null);
      item.setSelectionStyle(false);
      remove(item);
    }
  }

  /**
   * Adds a menu item to the bar.
   * @param id The item ID.
   * @param caption The item caption.
   * @return The added item.
   */
  public VModuleItem addItem(String id, String caption, boolean root, boolean shortcutNavigation) {
    VModuleItem			item;
    
    item = new VModuleItem();
    item.setId(id);
    item.setCaption(caption);
    return addItem(item, root, shortcutNavigation);
  }
  
  @Override
  public void clear() {
    for (VModuleItem item : getItems()) {
      if (item != null) {
	item.clear();
      }
    }
    
    super.clear();
  }

  /**
   * Closes this menu and all child menu popups.
   *
   * @param focus true to move focus to the parent
   */
  public void closeAllChildren(boolean focus) {
    if (shownChildMenu != null) {
      // Hide any open submenus of this item
      shownChildMenu.onHide(focus);
      shownChildMenu = null;
      selectItem(null);
    }
    // Close the current popup
    if (popup != null) {
      popup.hide();
    }
    // If focus is true, set focus to parentMenu
    if (focus && parentMenu != null) {
      parentMenu.focus();
    }
  }

  /**
   * Select the given VModuleItem, which must be a direct child of this MenuBar.
   *
   * @param item the VModuleItem to select, or null to clear selection
   */
  public void selectItem(VModuleItem item) {
    assert item == null || item.getParentMenu() == this;

    if (item == selectedItem) {
      return;
    }

    if (selectedItem != null) {
      selectedItem.removeStyleName("active");
      selectedItem.setSelectionStyle(false);
    }

    if (item != null) {
      item.setSelectionStyle(true);
    }

    selectedItem = item;
  }

  /**
   * Moves the menu selection down to the next item. If there is no selection,
   * selects the first item. If there are no items at all, does nothing.
   */
  public void moveSelectionDown() {
    if (selectFirstItemIfNoneSelected()) {
      return;
    }

    if (vertical) {
      selectNextItem();
    } else {
      if (selectedItem.getSubMenu() != null
          && !selectedItem.getSubMenu().getItems().isEmpty()
          && (shownChildMenu == null || shownChildMenu.getSelectedItem() == null)) {
        if (shownChildMenu == null) {
          doItemAction(selectedItem, false, true);
        }
        selectedItem.getSubMenu().focus();
      } else if (parentMenu != null) {
        if (parentMenu.vertical) {
          parentMenu.selectNextItem();
        } else {
          parentMenu.moveSelectionDown();
        }
      }
    }
  }

  /**
   * Give this module list focus.
   */
  public void focus() {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        setFocus(true);
      }
    });
  }
  
  /**
   * Loose focus on this module list
   */
  public void blur() {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {

      @Override
      public void execute() {
        setFocus(false);
      }
    });
  }

  /**
   * Sets/Removes the keyboard focus to the panel.
   * 
   * @param focus If set to true then the focus is moved to the panel, if set to
   *              false the focus is removed
   */
  public void setFocus(boolean focus) {
    if (focus) {
      FocusImpl.getFocusImplForPanel().focus(getElement());
    } else {
      FocusImpl.getFocusImplForPanel().blur(getElement());
    }
  }

  /**
   * Performs the action associated with the given menu item. If the item has a
   * popup associated with it, the popup will be shown. If it has a command
   * associated with it, and 'fireCommand' is true, then the command will be
   * fired. Popups associated with other items will be hidden.
   *
   * @param item the item whose popup is to be shown. @param fireCommand
   * <code>true</code> if the item's command should be fired, <code>false</code>
   * otherwise.
   */
  /*package*/ void doItemAction(final VModuleItem item, boolean fireCommand, boolean focus) {
    // Ensure that the item is selected.
    //selectItem(item);

    // if the command should be fired and the item has one, fire it
    if (fireCommand && item.getScheduledCommand() != null) {
      // if the item is not enabled do nothing
      if (item instanceof HasEnabled && !((HasEnabled) item).isEnabled()) {
        return;
      }
      
      // Close this menu and all of its parents.
      closeAllParents();

      // Remove the focus from the menu
      getElement().blur();

      // Fire the item's command. The command must be fired in the same event
      // loop or popup blockers will prevent popups from opening.
      final	ScheduledCommand cmd = item.getScheduledCommand();
      
      Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
	
        @Override
        public void execute() {
          cmd.execute();
        }
      });

      // hide any open submenus of this item
      if (shownChildMenu != null) {
        shownChildMenu.onHide(focus);
        popup.hide();
        shownChildMenu = null;
        selectItem(null);
      }
    } else if (item.getSubMenu() != null) {
      if (shownChildMenu == null) {
        // open this submenu
        openPopup(item);
      } else if (item.getSubMenu() != shownChildMenu) {
        // close the other submenu and open this one
        shownChildMenu.onHide(focus);
        popup.hide();
        openPopup(item);
      } else if (fireCommand && !autoOpen) {
        // close this submenu
        shownChildMenu.onHide(focus);
        popup.hide();
        shownChildMenu = null;
        selectItem(item);
      }
    } else if (autoOpen && shownChildMenu != null) {
      // close submenu
      shownChildMenu.onHide(focus);
      popup.hide();
      shownChildMenu = null;
    }
    
    if (selectedItem == item && item.isRoot() || item.isShortcutNavigation()) {
      item.addStyleName("active");
    }
  }

  /*
   * Closes all parent menu popups.
   */
  /*package*/ void closeAllParents() {
    if (parentMenu != null) {
      // The parent menu will recursively call closeAllParents.
      close(false);
    } else {
      // If this is the top most menu, deselect the current item.
      selectItem(null);
    }
  }

  /*
   * This method is called when a menu bar is shown.
   */
  protected void onShow() {
    // clear the selection; a keyboard user can cursor down to the first item
    selectItem(null);
  }

  /**
   * Closes all parent and child menu popups.
   */
  /*package*/ void closeAllParentsAndChildren() {
    closeAllParents();
    // Ensure the popup is closed even if it has not been enetered
    // with the mouse or key navigation
    if (parentMenu == null && popup != null) {
      popup.hide();
    }
  }

  /**
   * Moves the menu selection up to the previous item. If there is no selection,
   * selects the first item. If there are no items at all, does nothing.
   */
  public void moveSelectionUp() {
    if (selectFirstItemIfNoneSelected()) {
      return;
    }

    if ((shownChildMenu == null) && vertical) {
      selectPrevItem();
    } else if ((parentMenu != null) && parentMenu.vertical) {
      parentMenu.selectPrevItem();
    } else {
      close(true);
    }
  }

  /**
   * fires an action over an item.
   * @param item The item.
   * @param focus should have focus.
   */
  /*package*/ void itemOver(VModuleItem item, boolean focus) {
    if (item == null) {
      // Don't clear selection if the currently selected item's menu is showing.
      if ((selectedItem != null) && shownChildMenu != null && (shownChildMenu == selectedItem.getSubMenu())) {
        return;
      }
    }

    // Style the item selected when the mouse enters.
    selectItem(item);
    if (focus && focusOnHover) {
      focus();
    }

    // If child menus are being shown, or this menu is itself
    // a child menu, automatically show an item's child menu
    // when the mouse enters.
    if (item != null) {
      if ((shownChildMenu != null) || (parentMenu != null) || autoOpen) {
        doItemAction(item, false, focusOnHover);
      }
    }
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  /**
   * Sets whether this module list bar's child menus will open when the mouse is moved
   * over it.
   *
   * @param autoOpen <code>true</code> to cause child menus to auto-open
   */
  public void setAutoOpen(boolean autoOpen) {
    this.autoOpen = autoOpen;
  }
  
  /**
   * Returns {@code true} if this menu allows the auto open on hover.
   * @return {@code true} if this menu allows the auto open on hover.
   */
  public boolean isAutoOpen() {
    return autoOpen;
  }

  /**
   * Enable or disable auto focus when the mouse hovers over the MenuBar. This
   * allows the MenuBar to respond to keyboard events without the user having to
   * click on it, but it will steal focus from other elements on the page.
   * Enabled by default.
   *
   * @param enabled true to enable, false to disable
   */
  public void setFocusOnHoverEnabled(boolean enabled) {
    focusOnHover = enabled;
  }

  /**
   * Check whether or not this widget will steal keyboard focus when the mouse
   * hovers over it.
   *
   * @return true if enabled, false if disabled
   */
  public boolean isFocusOnHoverEnabled() {
    return focusOnHover;
  }

  /**
   * Returns a list containing the <code>VModuleItem</code> objects in the menu
   * bar. If there are no items in the menu bar, then an empty <code>List</code>
   * object will be returned.
   *
   * @return a list containing the <code>VModuleItem</code> objects in the menu bar
   */
  protected List<VModuleItem> getItems() {
    return this.items;
  }
  
  /**
   * Returns the menu item of the given id.
   * @param id The item id.
   * @return The menu item object.
   */
  protected VModuleItem getItem(String id) {
    for (VModuleItem item : items) {
      if (item.getId().equals(id)) {
        return item;
      }
    }
    
    return null;
  }

  /**
   * Returns the <code>VModuleItem</code> that is currently selected (highlighted)
   * by the user. If none of the items in the menu are currently selected, then
   * <code>null</code> will be returned.
   *
   * @return the <code>VModuleItem</code> that is currently selected, or
   *         <code>null</code> if no items are currently selected
   */
  protected VModuleItem getSelectedItem() {
    return this.selectedItem;
  }
  
  /**
   * Force the menu to be a root menu.
   */
  protected void setRoot() {
    vertical = false;
  }
  
  //---------------------------------------------------
  // PRIVATE METHODS
  //---------------------------------------------------

  /*
   * This method is called when a menu bar is hidden, so that it can hide any
   * child popups that are currently being shown.
   */
  private void onHide(boolean focus) {
    if (shownChildMenu != null) {
      shownChildMenu.onHide(focus);
      popup.hide();
      if (focus) {
        focus();
      }
    }
  }
  
  /**
   * Selects the first item in the menu if no items are currently selected. Has
   * no effect if there are no items.
   *
   * @return true if no item was previously selected, false otherwise
   */
  private boolean selectFirstItemIfNoneSelected() {
    if (selectedItem == null) {
      for (VModuleItem nextItem : items) {
	selectItem(nextItem);
	break;
      }
      return true;
    }

    return false;
  }
  
  /**
   * Adds a menu item to the bar at a specific index.
   *
   * @param item the item to be inserted
   * @return the {@link VModuleItem} object
   */
  private VModuleItem insertItem(VModuleItem item) {
    items.add(item);

    // Setup the menu item
    item.setParentMenu(this);
    item.setSelectionStyle(false);
    add(item, getElement());
    return item;
  }
  
  /**
   * Selects the next item.
   */
  private void selectNextItem() {
    if (selectedItem == null) {
      return;
    }

    int		index = items.indexOf(selectedItem);
    // We know that selectedItem is set to an item that is contained in the
    // items collection.
    // Therefore, we know that index can never be -1.
    assert (index != -1);

    VModuleItem 	itemToBeSelected;

    int firstIndex = index;
    while (true) {
      index = index + 1;
      if (index == items.size()) {
        // we're at the end, loop around to the start
        index = 0;
      }
      if (index == firstIndex) {
        itemToBeSelected = items.get(firstIndex);
        break;
      } else {
	itemToBeSelected = items.get(index);
	break;
      }
    }

    selectItem(itemToBeSelected);
    if (shownChildMenu != null) {
      doItemAction(itemToBeSelected, false, true);
    }
  }

  /**
   * Closes this menu (if it is a popup).
   *
   * @param focus true to move focus to the parent
   */
  private void close(boolean focus) {
    if (parentMenu != null) {
      parentMenu.popup.hide(!focus);
      if (focus) {
        parentMenu.focus();
      }
    }
  }

  /**
   * Opens the popup and show a submenu vertically.
   * @param item The selected item.
   */
  private void openPopup(final VModuleItem item) {
    // Only the last popup to be opened should preview all event
    if (parentMenu != null && parentMenu.popup != null) {
      parentMenu.popup.setPreviewingAllNativeEvents(false);
    }
    // Create a new popup for this item, and position it next to
    // the item (below if this is a horizontal menu bar, to the
    // right if it's a vertical bar).
    popup = new VMenuPopup(connection, this, item);
    //popup.setAnimationEnabled(isAnimationEnabled);
    if (!vertical) {
      popup.setRollDownAnimation();
    } else {
      popup.setOneWayCornerAnimation();
    }
    popup.addPopupListener(this);
    shownChildMenu = item.getSubMenu();
    item.getSubMenu().parentMenu = this;
    // Show the popup, ensuring that the menubar's event preview remains on top
    // of the popup's.
    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
	if (vertical) {
	  if (item.getAbsoluteLeft() + item.getParentMenu().getOffsetWidth() + offsetWidth > Window.getClientWidth()) {
	    popup.setCenterAnimation(); // show it from the center
	    popup.setPopupPosition(VModuleListMenu.this.getAbsoluteLeft() - offsetWidth + 16, item.getAbsoluteTop() + 10);
	  } else {
	    popup.setPopupPosition(VModuleListMenu.this.getAbsoluteLeft() + VModuleListMenu.this.getOffsetWidth() + 6, item.getAbsoluteTop() + 10);
	  }
	} else {
	  if (item.getAbsoluteLeft() + offsetWidth > Window.getClientWidth()) {
	    popup.setPopupPosition(item.getAbsoluteLeft() + item.getOffsetWidth() - offsetWidth  + 10, VModuleListMenu.this.getAbsoluteTop() + VModuleListMenu.this.getOffsetHeight() + 14);
	  } else {
	    popup.setPopupPosition(item.getAbsoluteLeft() + (correctPopupPosition ? 3 : -6), VModuleListMenu.this.getAbsoluteTop() + VModuleListMenu.this.getOffsetHeight() + (correctPopupPosition ? 14 : 0));
	  }
	}
      }
    });
  }

  private void moveToPrevItem() {
    if (selectFirstItemIfNoneSelected()) {
      return;
    }

    if (!vertical) {
      selectPrevItem();
    } else {
      if ((parentMenu != null) && (!parentMenu.vertical)) {
        parentMenu.selectPrevItem();
      } else {
        close(true);
      }
    }
  }

  /**
   * Moves to the next item.
   */
  private void moveToNextItem() {
    if (selectFirstItemIfNoneSelected()) {
      return;
    }

    if (!vertical) {
      selectNextItem();
    } else {
      if (selectedItem.getSubMenu() != null
          && !selectedItem.getSubMenu().getItems().isEmpty()
          && (shownChildMenu == null || shownChildMenu.getSelectedItem() == null)) {
        if (shownChildMenu == null) {
          doItemAction(selectedItem, false, true);
        }
        selectedItem.getSubMenu().focus();
      } else if (parentMenu != null) {
        if (!parentMenu.vertical) {
          parentMenu.selectNextItem();
        } else {
          parentMenu.moveToNextItem();
        }
      }
    }
  }

  /**
   * Blocks the event.
   * @param event The event.
   */
  private void eatEvent(Event event) {
    event.stopPropagation();
    event.preventDefault();
  }

  /**
   * Selects previous item.
   */
  private void selectPrevItem() {
    if (selectedItem == null) {
      return;
    }

    int index = items.indexOf(selectedItem);
    // We know that selectedItem is set to an item that is contained in the
    // items collection.
    // Therefore, we know that index can never be -1.
    assert (index != -1);

    VModuleItem		itemToBeSelected;

    int firstIndex = index;
    while (true) {
      index = index - 1;
      if (index < 0) {
        // we're at the start, loop around to the end
        index = items.size() - 1;
      }
      if (index == firstIndex) {
        itemToBeSelected = items.get(firstIndex);
        break;
      } else {
        itemToBeSelected = items.get(index);
        break;
      }
    }

    selectItem(itemToBeSelected);
    if (shownChildMenu != null) {
      doItemAction(itemToBeSelected, false, true);
    }
  }

  /**
   * Finds an item from its elements.
   * @param hItem The item element.
   * @return The item.
   */
  private VModuleItem findItem(Element hItem) {
    for (VModuleItem item : items) {
      if (item.getElement().isOrHasChild(hItem)) {
        return item;
      }
    }
    
    return null;
  }
  
  /**
   * Returns the menu items total width.
   * @return The menu items total width.
   */
  protected int getItemsWidth() {
    int		width = 0;
    
    for (VModuleItem item : items) {
      if (item != null) {
	width += item.getElement().getClientWidth();
	if (!itemsWidths.containsKey(item)) {
	  itemsWidths.put(item, item.getElement().getClientWidth());
	}
      }
    }
    
    return width;
  }
  
  /**
   * Render the module menu according to browser window width.
   */
  protected void render(int width) {
    // margin is added to the size of the 
    if (getItemsWidth() + (2 * items.size()) > width) {
      addMoreItem(width);
    } else {
      restoreExtraItems(width);
    }
  }
  
  /**
   * Adds the moreItem for non visible extra items.
   * @param extraItems The extra items to be added.
   */
  protected void addMoreItem(int width) {
    List<VModuleItem>		extraItems;
    boolean			alreadyHasItems; // more Items already has items

    extraItems = getExtraItems(width);
    alreadyHasItems = true;
    // render only when there are extra items.
    if (!extraItems.isEmpty()) {
      VModuleListMenu		subMenu;

      if (moreItem == null) {
	moreItem = new VModuleItem() {

	  @Override
	  protected void addChildrenIndicator() {
	    // do not add children indicator for items
	  }
	  
	  @Override
	  public void buildContent() {
	    setShortcutNavigation(true);
	    super.buildContent();
	    setIcon("angle-double-right");
	  }
	};
	moreItem.setParentMenu(this);
      }
      subMenu = moreItem.getSubMenu();
      if (subMenu == null) {
	subMenu = new VModuleListMenu(connection, false);
	alreadyHasItems = false;
      }
      if (!alreadyHasItems) {
	for (VModuleItem item : extraItems) {
	  if (item != null) {
	    subMenu.addItem(item, false, false);
	  }
	}
      } else {
	List<VModuleItem>	newExtraItems = new LinkedList<VModuleItem>();
	
	newExtraItems.addAll(extraItems);
	newExtraItems.addAll(subMenu.getItems());
	// remove submenu items
	for (VModuleItem item : subMenu.getItems()) {
	  if (item != null) {
	    item.removeChildrenIndicator();
	    subMenu.removeItem(item);
	  }
	}
	// add the new extra items.
	for (VModuleItem item : newExtraItems) {
	  if (item != null) {
	    subMenu.addItem(item, false, false);
	  }
	}
      }
      if (!items.contains(moreItem)) {
	moreItem.setSubMenu(subMenu);
	addItem(moreItem, true, false);
      }
    }
  }
  
  /**
   * Restore extra items to the root menu.
   * @param moreItem The concerned item.
   */
  protected void restoreExtraItems(int width) {
    if (moreItem == null) {
      return; // give up when no more item is present.
    }
    
    for (VModuleItem item : moreItem.getSubMenu().getItems()) {
      if (item == moreItem) {
	continue;
      }
      
      if (getMoreItemWidth() + getItemsWidth() + (2 * items.size()) + itemsWidths.get(item) + 2 < width) {
	// the item should be restored
	moreItem.getSubMenu().removeItem(item);
	removeItem(moreItem);
	addItem(item, true, false);
	item.removeChildrenIndicator(); // if it exists
	// if the more item is empty, remove it
	if (moreItem.getSubMenu().getItems().isEmpty()) {
	  removeItem(moreItem);
	  moreItem = null;
	  break;
	} else {
	  addItem(moreItem, true, false);
	}
      } else {
	break;
      }
    }
    // if the more item is empty, remove it
    if (moreItem != null && moreItem.getSubMenu().getItems().isEmpty()) {
      removeItem(moreItem);
      moreItem = null;
    }
  }
  
  /**
   * Returns the more item client width.
   * @return The more item client width.
   */
  protected int getMoreItemWidth() {
    return moreItem != null ? 2 + moreItem.getElement().getClientWidth() : 0;
  }
  
  /**
   * Returns the extra items that has exceeded the window browser width.
   * @return The extra items that has exceeded the window browser width.
   */
  protected List<VModuleItem> getExtraItems(int width) {
    LinkedList<VModuleItem>		extraItems;
    
    extraItems = new LinkedList<VModuleItem>();
    while (getItemsWidth() + (2 * items.size()) > width - 80) {
      VModuleItem		item;

      item = items.peekLast();
      if (moreItem != null && item == moreItem) {
	if (items.size() >= 2) {
	  item = items.get(items.size() - 2);
	} else {
	  item = null;
	}
      }

      if (item != null) {
	extraItems.addFirst(item);
	items.remove(item);
      }
    }
    
    return extraItems;
  }
  
  /**
   * Do not apply popup position correction.
   */
  public void doNotCorrectPopupPosition() {
    correctPopupPosition = false;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private int                                   requiredHeight= 0;
  private boolean 				isAnimationEnabled = true; // always enable animation
  private boolean                               correctPopupPosition = true;
  private VModuleListMenu 			parentMenu;
  private VMenuPopup 				popup;
  private VModuleItem 				selectedItem;
  private VModuleListMenu 			shownChildMenu;
  private boolean 				focusOnHover = true;
  /*package*/ boolean 				vertical;
  private boolean 				autoOpen = true; // always auto open.
  private LinkedList<VModuleItem> 		items = new LinkedList<VModuleItem>();
  private VModuleItem				moreItem; // cannot be used if the menu does not exceed window width.
  private final ApplicationConnection 		connection;
  private Map<VModuleItem, Integer>		itemsWidths = new HashMap<VModuleItem, Integer>();
}
