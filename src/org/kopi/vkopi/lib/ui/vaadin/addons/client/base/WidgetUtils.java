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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.Widget;

/**
 * Some widget utilities.
 */
public class WidgetUtils {
  
  /**
   * Returns {@code true} if it undefined width.
   * @param width The widget width.
   * @return {@code true} if it undefined width.
   */
  public static final boolean isUndefinedWidth(String width) {
    return width == null || "".equals(width);
  }

  /**
   * Returns {@code true} if it undefined height.
   * @param width The widget height.
   * @return {@code true} if it undefined height.
   */
  public static final boolean isUndefinedHeight(String height) {
    return height == null || "".equals(height);
  }
  
  /**
   * Returns {@code true} if it relative width.
   * @param width The widget width.
   * @return {@code true} if it relative width.
   */
  public static final boolean isRelativeWidth(String width) {
    return width != null && width.endsWith("%");
  }
  
  /**
   * Returns {@code true} if it relative height.
   * @param width The widget width.
   * @return {@code true} if it relative height.
   */
  public static final boolean isRelativeHeight(String height) {
    return height != null && height.endsWith("%");
  }
  
  /**
   * Returns the right of a given widget.
   * @param widget The widget
   * @return The widget right position.
   */
  public static final int getRightOfWidget(Widget widget) {
    return widget.getElement().getOffsetLeft() + widget.getElement().getOffsetWidth();
  }
  
  /**
   * Parses a CSS position.
   * @param positionString The CSS position.
   * @return The integer position.
   */
  public static final int parsePosition(String positionString) {
    try {
      for (int i = 0; i < positionString.length(); i++) {
	char	c = positionString.charAt(i);
	
	if (c != '-' && !(c >= '0' && c <= '9')) {
	  positionString = positionString.substring(0, i);
	}
      }

      return Integer.parseInt(positionString);
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  /**
   * Looks for the parent widget having T as a reference type.
   * @param child The child widget.
   * @param clazz The searched type.
   * @return The parent having T as a type or {@code null} if not found.
   */
  @SuppressWarnings("unchecked")
  public static final <T extends Widget> T getParent(Widget child, Class<T> clazz) {
    Widget      parent = child.getParent();
    
    while (parent != null) {
      if (clazz == null || parent.getClass() == clazz) {
        return (T)parent;
      }
      
      parent = parent.getParent();
    }
    
    return null;
  }
  
  /**
   * Returns {@code true} if the element is visible.
   * @param element The tested element.
   * @return {@code true} if the element is visible.
   */
  public static boolean isVisible(Element element) {
    return element.getStyle().getVisibility().equalsIgnoreCase(Visibility.VISIBLE.getCssName());
  }
}
