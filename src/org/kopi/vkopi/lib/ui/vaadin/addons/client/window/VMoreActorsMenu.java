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

import java.util.LinkedList;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VULPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;

/**
 * The more actors menu widget.
 */
public class VMoreActorsMenu extends VULPanel implements CloseHandler<PopupPanel>, HasCloseHandlers<PopupPanel> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new more actors menu widget.
   */
  public VMoreActorsMenu(ApplicationConnection connection) {
    this.connection = connection;
    items = new LinkedList<VMoreActorsItem>();
    setStyleName("more-actors-menu");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler) {
    if (popup != null) {
      return popup.addCloseHandler(handler);
    } else {
      return null;
    }
  }
  
  /**
   * Adds an actor to this menu.
   * @param actor The actor to be added.
   */
  public void addActor(Widget actor) {
    VMoreActorsItem		item;
    
    item = new VMoreActorsItem();
    item.setActor(actor);
    addItem(item);
  }
  
  /**
   * Adds an item to this menu.
   * @param item The item to be added.
   */
  public void addItem(VMoreActorsItem item) {
    items.add(item);
    add(item);
  }
  
  /**
   * Removes the given item.
   * @param item The item to be removed
   */
  public void removeItem(VMoreActorsItem item) {
    items.remove(item);
    remove(item);
  }
  
  /**
   * Shows the popup menu containing additional
   * @param parent The parent more actors item.
   */
  public void openPopup(VMoreActors parent) {
    if (connection == null) {
      return;
    }
    
    popup = new VMoreActorsPopup(connection);
    popup.addCloseHandler(this);
    popup.setAnimationEnabled(false);
    popup.setWidget(this);
    popup.showRelativeTo(parent);
  }
  
  /**
   * Closes the more actors menu.
   */
  public void close() {
    if (popup != null) {
      popup.hide();
    }
  }
  
  /**
   * Returns the list of items present in this menu.
   * @return The list of items present in this menu.
   */
  protected LinkedList<VMoreActorsItem> getItems() {
    return items;
  }
  
  /**
   * Returns <code>true</code> if there is no items to be shown.
   * @return <code>true</code> if there is no items to be shown.
   */
  public boolean isEmpty() {
    return items.isEmpty();
  }
  
  /**
   * Returns <code>true</code> if popup is showing.
   * @return <code>true</code> if popup is showing.
   */
  public boolean isPopupShowing() {
    return popup == null ? false : popup.isShowing();
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
  }
  
  @Override
  public void clear() {
    super.clear();
    items.clear();
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        if (WidgetUtil.getRequiredHeight(VMoreActorsMenu.this) + getAbsoluteTop() > Window.getClientHeight() - 24) {
          getElement().getStyle().setHeight(Window.getClientHeight() - (24 + getAbsoluteTop()), Unit.PX);
          getElement().getStyle().setOverflowX(Overflow.AUTO);
        }
      }
    });
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final ApplicationConnection 		connection;
  private final LinkedList<VMoreActorsItem>	items;
  private VMoreActorsPopup			popup;
}
