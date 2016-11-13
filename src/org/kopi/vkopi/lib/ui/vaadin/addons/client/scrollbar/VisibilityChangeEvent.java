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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Visibility change event for scroll bars.
 */
public class VisibilityChangeEvent extends GwtEvent<VisibilityHandler> {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new VisibilityChangeEvent instance.
   * @param isScrollerVisible Is scroll bar visible ?
   */
  protected VisibilityChangeEvent(boolean isScrollerVisible) {
    this.isScrollerVisible = isScrollerVisible;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Checks whether the scroll handle is currently visible or not
   * 
   * @return <code>true</code> if the scroll handle is currently visible.
   *         <code>false</code> if not.
   */
  public boolean isScrollerVisible() {
    return isScrollerVisible;
  }

  @Override
  public Type<VisibilityHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(VisibilityHandler handler) {
    handler.visibilityChanged(this);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private final boolean                         isScrollerVisible;
  public static final Type<VisibilityHandler>   TYPE = new Type<VisibilityHandler>() {
    
    @Override
    public String toString() {
      return "VisibilityChangeEvent";
    }
  };
}
