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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.main;

import java.util.HashMap;
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VCaption;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main window container widget.
 * This widget will be responsible of displaying only one window.
 * That's why deck panel is used.
 * The control of the displayed widget will be from outside.
 */
/*package*/ class VWindowContainer extends SimplePanel {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new container widget.
   */
  public VWindowContainer() {
    setStyleName("k-window-container");
    content = new FlexTable();
    content.setStyleName("k-container-content");
    pane = new DeckPanel();
    pane.setStyleName("k-container-pane");
    caption = new VCaption();
    caption.setStyleName("k-window-caption");
    widgetToIndexMap = new HashMap<Widget, Integer>();
    widgetToCaptionMap = new HashMap<Widget, String>();
    setWidget(content);
    content.setWidget(0, 0, caption);
    content.setWidget(1, 0, pane);
    content.getCellFormatter().setStyleName(0, 0, "k-content-caption");
    content.getCellFormatter().setStyleName(0, 1, "k-content-pane");
    content.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds the specified window to this container.
   * @param window The window widget.
   * @param title the window caption.
   */
  public void addWindow(Widget window, String title) {
    // first add the window to the container
    pane.add(window);
    // now map the window widget to its index
    widgetToIndexMap.put(window, pane.getWidgetCount() - 1);
    widgetToCaptionMap.put(window, title);
  }
  
  /**
   * Removes the given window from the container.
   * @param window The window to be removed.
   * @return The new shown widget or {@code null} if no window is shown.
   */
  public Widget removeWindow(Widget window) {
    Integer		index;
    
    // look for internal map.
    caption.setCaption(""); // reset window caption
    widgetToCaptionMap.remove(window);
    index = widgetToIndexMap.remove(window);
    if (index != null) {
      pane.remove(index);
      if (index - 1 >= 0) {
	// show previous window in the list
	return showWindow(pane.getWidget(index - 1));
      } else if (index + 1 < pane.getWidgetCount()) {
	// show next window in the list
	return showWindow(pane.getWidget(index));
      }
    } else {
      int	removedWindowIndex;
      // force widget remove if internal map fails.
      removedWindowIndex = pane.getWidgetIndex(window); // get the index of the window to remove
      pane.remove(window);
      
      if (removedWindowIndex - 1 >= 0) {
	return showWindow(pane.getWidget(removedWindowIndex - 1));
      } else if (removedWindowIndex < pane.getWidgetCount()) {
	// show next window in the list
	return showWindow(pane.getWidget(removedWindowIndex));
      }
    }
    
    return null;
  }
  
  /**
   * Updates the given window title.
   * @param window The concerned window.
   * @param title The new title.
   */
  public void updateWindowTitle(Widget window, String title) {
    widgetToCaptionMap.put(window, title);
    if (pane.getVisibleWidget() == pane.getWidgetIndex(window)) {
      caption.setCaption(title);
    }
  }
  
  /**
   * Shows the given window in this container.
   * @param window The window to be shown.
   * @return The new shown widget or {@code null} if no window is shown.
   */
  public Widget showWindow(Widget window) {
    if (window == null) {
      // no window, give up
      return null;
    }
    
    Integer		index;
    
    // look for internal map.
    index = widgetToIndexMap.remove(window);
    if (index != null) {
      // show only if we find a internal mapping
      pane.showWidget(index);
      caption.setCaption(widgetToCaptionMap.get(window));
      Window.setTitle(caption.getCaption());
      return window;
    } else {
      // we use internal complex panel engine.
      index = pane.getWidgetIndex(window);
      if (index != -1) {
	pane.showWidget(index);
	caption.setCaption(widgetToCaptionMap.get(window));
	Window.setTitle(caption.getCaption());
	return window;
      }
    }
    
    return null;
  }
  
  /**
   * Shows the next window in the list.
   */
  public Widget showNextWindow() {
    int                 WidgetIndex;
    
    WidgetIndex = pane.getVisibleWidget();
    WidgetIndex += 1;
    if (WidgetIndex >= pane.getWidgetCount()) {
      WidgetIndex = 0;
    }
    
    return showWindow(pane.getWidget(WidgetIndex));
  }
  
  /**
   * Shows the previous window in the list.
   */
  public Widget showPreviousWindow() {
    int                 WidgetIndex;
    
    WidgetIndex = pane.getVisibleWidget();
    WidgetIndex -= 1;
    if (WidgetIndex < 0) {
      WidgetIndex = pane.getWidgetCount() - 1;
    }
    
    return showWindow(pane.getWidget(WidgetIndex));
  }
  
  /**
   * Sets the transition animation enabled.
   * @param enable The enable state.
   */
  public void setAnimationEnabled(boolean enable) {
    pane.setAnimationEnabled(enable);
  }
  
  /**
   * Returns the visible widget.
   * @return The visible widget.
   */
  public Widget getVisibleWidget() {
    return pane.getWidget(pane.getVisibleWidget());
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Map<Widget, Integer>		widgetToIndexMap;
  private final Map<Widget, String>		widgetToCaptionMap;
  private final DeckPanel			pane;
  private final VCaption			caption;
  private final FlexTable			content;
}
