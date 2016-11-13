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

import java.util.LinkedList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.FocusableFlowPanel;

/**
 * Navigation panel widget to produce a navigation menu like
 * the one existing in http://www.telemark-pyrenees.com/fr/
 */
public class VNavigationPanel extends FocusableFlowPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new navigation panel widget
   */
  public VNavigationPanel() {
    columns = new LinkedList<VNavigationColumn>();
    sinkEvents(Event.ONKEYDOWN);
    addKeyDownHandler(new KeyDownHandler() {
      
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
          VNavigationMenu       menu;
          
          menu = getNavigationMenu();
          if (menu != null) {
            menu.close();
          }
        }
      }
    });
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds the given navigation column to this navigation panel.
   * @param column The column to be added.
   */
  public void addColumn(VNavigationColumn column) {
    columns.add(column);
    add(column);
  }
  
  /**
   * Removes the given column from this navigation panel.
   * @param column The column to be removed.
   */
  public void removeColumn(VNavigationColumn column) {
    remove(column);
    columns.remove(column);
  }
  
  /**
   * Returns the navigation columns contained in this panel.
   * @return The navigation columns contained in this panel.
   */
  public List<VNavigationColumn> getColumns() {
    return columns;
  }
  
  /**
   * Returns the navigation menu where this panel is attached.
   * @return The navigation menu where this panel is attached.
   */
  protected VNavigationMenu getNavigationMenu() {
    return WidgetUtils.getParent(this, VNavigationMenu.class);
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        focus();
      }
    });
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final List<VNavigationColumn>         columns;
}
