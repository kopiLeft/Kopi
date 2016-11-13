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

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * More actor container widget.
 */
public class VMoreActorsItem extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new more actors item widget.
   */
  public VMoreActorsItem() {
    super(Document.get().createLIElement());
  }

  //---------------------------------------------------
  // IMPLEMENATATION
  //---------------------------------------------------
  
  /**
   * Sets the actor in this container.
   * @param actor The actor widget.
   */
  public void setActor(Widget actor) {
    setWidget(actor);
  }
  
  /**
   * Returns the encapsulated actor.
   * @return The encapsulated actor.
   */
  public Widget getActor() {
    return getWidget();
  }
}
