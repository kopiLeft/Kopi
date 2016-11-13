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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VAnchor;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * A widget indication that there are more actors
 * to be shown since there is not enough space to show
 * all actors in one line.
 */
public class VMoreActors extends SimplePanel implements ClickHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new widget for indicating
   * that more actors should be shown
   * @param connection The application connection.
   */
  public VMoreActors(ApplicationConnection connection) {
    super(Document.get().createSpanElement());
    menu = new VMoreActorsMenu(connection);
    anchor = new VAnchor();
    anchor.setText(">>");
    anchor.setHref("#");
    anchor.addClickHandler(this);
    setWidget(anchor);
    setStyleName("more-actors");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds an extra actor to this more actors item.
   * @param actor The actor to be added.
   */
  public void addActor(Widget actor) {
    if (actor != null) {
      menu.addActor(actor);
      actor.addDomHandler(new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          if (menu.isPopupShowing()) {
            menu.close();
          }
        }
      }, ClickEvent.getType());
    }
  }
  
  /**
   * Returns the list of actors contained in this panel.
   * @return The list of actors contained in this panel.
   */
  protected LinkedList<Widget> getActors() {
    LinkedList<Widget>		actors;
    
    actors = new LinkedList<Widget>();
    for (VMoreActorsItem item : menu.getItems()) {
      actors.addLast(item.getActor());
    }
    
    return actors;
  }
  
  /**
   * Removes the given actor from the menu.
   * @param actor The actor to be removed.
   */
  protected void removeActor(Widget actor) {
    for (VMoreActorsItem item : menu.getItems()) {
      if (item.getActor() == actor) {
	menu.removeItem(item);
      }
    }
  }
  
  /**
   * Returns <code>true</code> if there is no items to be shown.
   * @return <code>true</code> if there is no items to be shown.
   */
  protected boolean isEmpty() {
    return menu.isEmpty();
  }
  
  @Override
  public void clear() {
    menu.clear();
  }
  
  @Override
  public void onClick(ClickEvent event) {
    if (!menu.isEmpty()) {
      menu.openPopup(this);
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VAnchor				anchor;
  private final VMoreActorsMenu			menu;
}
