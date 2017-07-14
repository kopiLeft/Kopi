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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ApplicationConnection;

/**
 * The menu special popup panel.
 */
public class VMenuPopup extends VPopup {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new {@code VMenuPopup} object.
   * @param connection The application connection.
   */
  public VMenuPopup(ApplicationConnection connection,
                    VModuleListMenu owner,
                    VModuleItem item)
  {
    super(connection, true, false);
    this.owner = owner;
    this.item = item;
    setWidget(item.getSubMenu());
    setPreviewingAllNativeEvents(true);
    item.getSubMenu().addStyleName("submenu");
    item.getSubMenu().onShow();
    addCloseHandler(this);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void onPreviewNativeEvent(NativePreviewEvent event) {
    // Hook the popup panel's event preview. We use this to keep it from
    // auto-hiding when the parent menu is clicked.
    if (!event.isCanceled()) {
      switch (event.getTypeInt()) {
      case Event.ONMOUSEDOWN:
        // If the event target is part of the parent menu, suppress the
        // event altogether.
        EventTarget	target = event.getNativeEvent().getEventTarget();
        Element 	parentMenuElement = item.getParentMenu().getElement();
        
        if (parentMenuElement.isOrHasChild(Element.as(target))) {
          event.cancel();
          return;
        }
        super.onPreviewNativeEvent(event);
        if (event.isCanceled()) {
          owner.selectItem(null);
        }
        return;
      }
    }
    
    super.onPreviewNativeEvent(event);
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    super.onClose(event);
    event.getTarget().clear();
    event.getTarget().removeFromParent();
  }
  
  /**
   * Sets the animation type to roll down.
   */
  public native void setRollDownAnimation() /*-{
    this.@com.google.gwt.user.client.ui.PopupPanel::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ROLL_DOWN);
  }-*/;
  
  /**
   * Sets the animation type to roll one way corner.
   */
  public native void setOneWayCornerAnimation() /*-{
    this.@com.google.gwt.user.client.ui.PopupPanel::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::ONE_WAY_CORNER);
  }-*/;
  
  /**
   * Sets the animation type to roll one way corner.
   */
  public native void setCenterAnimation() /*-{
    this.@com.google.gwt.user.client.ui.PopupPanel::setAnimationType(Lcom/google/gwt/user/client/ui/PopupPanel$AnimationType;)(@com.google.gwt.user.client.ui.PopupPanel.AnimationType::CENTER);
  }-*/;

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VModuleListMenu		 	owner;
  private final VModuleItem 			item;
}
