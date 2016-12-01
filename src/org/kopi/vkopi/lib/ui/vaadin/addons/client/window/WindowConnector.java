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

import org.kopi.vkopi.lib.ui.vaadin.addons.Window;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.ActorConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.VActorsNavigationPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.FormConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

@Connect(value = Window.class, loadStyle = LoadStyle.DEFERRED)
@SuppressWarnings("serial")
public class WindowConnector extends AbstractHasComponentsConnector {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the connector instance.
   */
  public WindowConnector() {
    super();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    getWidget().init(getConnection());
    //getConnection().getVTooltip().connectHandlersToWidget(getWidget());
  }
  
  @Override
  public VWindow getWidget() {
    return (VWindow) super.getWidget();
  }
  
  @Override
  public boolean hasTooltip() {
    return true;
  }

  @Override
  public TooltipInfo getTooltipInfo(Element element) {
    if (element == getWidget().getActorsMenuElement()) {
      return new TooltipInfo(LocalizedProperties.getString(VMainWindow.getLocale(), "actorsMenuHelp"));
    } else {
      return null;
    }
  }

  @Override
  public void updateCaption(ComponentConnector connector) {}

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    VActorsNavigationPanel              panel;
    
    panel = new VActorsNavigationPanel();
    for (ComponentConnector child : getChildComponents()) {
      // add only actors here. content and footer will be added by other connectors.
      if (child instanceof ActorConnector) {
	ActorConnector		actor = ((ActorConnector)child);
	
	if (actor.isVisible()) {
	  actor.getWidget().setEnabled(isEnabled());
	  getWidget().addActor(actor.getWidget());
	}
	panel.addActor(actor);
      } else {
	// it should be the window content
	getWidget().setContent(child.getWidget());
      }
    }
    // finally adds the menu.
    getWidget().addActorsNavigationPanel(panel);
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  @OnStateChange("caption")
  /*void*/ void setCaption() {
    getWidget().setCaption(getState().caption);
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
    super.onUnregister();
  }
  
  /**
   * Cleans the dirty values of this window
   */
  public void cleanDirtyValues(BlockConnector active) {
    cleanDirtyValues(active, true);
  }
  
  /**
   * Cleans the dirty values of this window
   */
  public void cleanDirtyValues(BlockConnector active, boolean transferFocus) {
    for (ComponentConnector child : getChildComponents()) {
      if (child instanceof FormConnector) {
        ((FormConnector) child).cleanDirtyValues(active, transferFocus);
      }
    }
  }
  
  /**
   * Sets the actor having the given number to be enabled or disabled.
   * @param actor The actor connector instance.
   * @param enabled The enabled status.
   */
  public void setActorEnabled(Connector actor, boolean enabled) {
    for (ComponentConnector child : getChildComponents()) {
      if (child instanceof ActorConnector) {
        if (((ActorConnector) child) == actor) {
          ((ActorConnector) child).setActorEnabled(enabled);
        }
      }
    }
  }
}
