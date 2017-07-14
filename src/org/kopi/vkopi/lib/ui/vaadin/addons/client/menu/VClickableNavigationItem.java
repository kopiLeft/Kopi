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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VAnchor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;

/**
 * A clickable navigation item used to execute a 
 * {@link ScheduledCommand} when this item is
 * clicked.
 */
public class VClickableNavigationItem extends VNavigationItem {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new clickable navigation item.
   * @param cmd The command to be executed when this item is clicked.
   */
  public VClickableNavigationItem(final ScheduledCommand cmd) {
    sinkEvents(Event.ONCLICK);
    addDomHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        if (!isEnabled()) {
          return;
        }
        // Fire the item's command. The command must be fired in the same event
        // loop or popup blockers will prevent popups from opening.
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
          
          @Override
          public void execute() {
            VNavigationMenu             menu;
            
            cmd.execute();
            // close the parent popup
            menu = WidgetUtils.getParent(VClickableNavigationItem.this, VNavigationMenu.class);
            if (menu != null) {
              menu.close();
            }
          }
        });
      }
    }, ClickEvent.getType());
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  protected Element[] createInnerElements() {
    caption = new VAnchor();
    acceleratorKey = new VSpan();
    icon = new VIcon();
    caption.setHref("#");
    acceleratorKey.setStyleName("acceleratorKey");
    
    return new Element[] {icon.getElement(), caption.getElement(), acceleratorKey.getElement()};
  }

  @Override
  protected String getClassname() {
    return "";
  }

  @Override
  public void setCaption(String text) {
    caption.setText(text);
  }
  
  @Override
  public String getCaption() {
    return caption.getElement().getInnerText();
  }

  @Override
  public void setDescription(String text) {
    acceleratorKey.setText(text);
  }
  
  @Override
  public void setIcon(String icon) {
    this.icon.setName(icon);
  }
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (enabled) {
      removeStyleDependentName(DEPENDENT_STYLENAME_DISABLED_ITEM);
    } else {
      addStyleDependentName(DEPENDENT_STYLENAME_DISABLED_ITEM);
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VAnchor                               caption;
  private VSpan                                 acceleratorKey;
  private VIcon                                 icon;
  private boolean                               enabled;
  private static final String                   DEPENDENT_STYLENAME_DISABLED_ITEM = "disabled";
}
