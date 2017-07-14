/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.VActorsNavigationPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * The window view widget composed of an actor panel,
 * a view content and a view footer.
 */
public class VWindowView extends FlexTable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the window view widget.
   * @param connection The application connection.
   */
  public VWindowView(ApplicationConnection connection) {
    setStyleName(Styles.WINDOW_VIEW);
    actors = new VActorPanel(connection);
    // actors.setWidth("100%"); // not really needed
    setWidget(0, 0, actors);
    setWidget(2, 0, null); // footer
    getCellFormatter().getElement(2, 0).setClassName(Styles.WINDOW_VIEW + "-footer");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds an actor to this window view.
   * @param actor The actor to be added.
   */
  public void addActor(Widget actor) {
    actors.addActor(actor);
  }
  
  /**
   * Adds the actors menu to be shown.
   * @param panel The menu to be shown.
   */
  public void addActorsNavigationPanel(VActorsNavigationPanel panel) {
    actors.addActorsNavigationPanel(panel);
  }
  
  /**
   * Sets the window view content.
   * @param content The view content.
   */
  public void setContent(Widget content) {
    if (content != null) {
      content.setStyleName(Styles.WINDOW_VIEW_CONTENT);
      setWidget(1, 0, content);
    }
  }
  
  /**
   * Sets the footer widget.
   * @param footer The footer widget.
   */
  public void setFooter(Widget footer) {
    setWidget(2, 0, footer);
  } 
  
  /**
   * Clears the footer content.
   */
  public void clearFooter() {
    setWidget(2, 0, null);
  }
  
  /**
   * Sets the footer widget alignment.
   * @param hAlign The horizontal alignment.
   * @param vAlign The vertical alignment.
   */
  public void setFooterAlignment(HorizontalAlignmentConstant hAlign, VerticalAlignmentConstant vAlign) {
    getCellFormatter().setAlignment(2, 0, hAlign, vAlign);
  }
  
  /**
   * Returns the window view content.
   * @return The window view content.
   */
  public Widget getContent() {
    return getWidget(1, 0);
  }
  
  /**
   * Returns the element associated with the actor menu to be used for tooltip.
   * @return The element associated with the actor menu.
   */
  protected Element getActorsMenuElement() {
    return actors.getActorsNavigationElement();
  }
  
  @Override
  public void clear() {
    super.clear();
    actors.clear();
    actors = null;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VActorPanel                           actors;
}
