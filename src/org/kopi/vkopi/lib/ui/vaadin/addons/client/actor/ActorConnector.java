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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.actor;

import org.kopi.vkopi.lib.ui.vaadin.addons.Actor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.AcceleratorKeyCombination;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.ActionListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VInputTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.WindowConnector;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The actor component connector.
 */
@SuppressWarnings("serial")
@Connect(value = Actor.class, loadStyle = LoadStyle.DEFERRED)
public class ActorConnector extends AbstractComponentConnector implements ActionListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the connector instance.
   */
  public ActorConnector() {
    super();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void init() {
    getWidget().addActionListener(this);
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  @Override
  public VActor getWidget() {
    return (VActor) super.getWidget();
  }
  
  @Override
  public ActorState getState() {
    return (ActorState) super.getState();
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
    item = null;
  }
  
  @OnStateChange({"acceleratorKey", "modifiersKey"})
  /*package*/ void setKeyCombination() {
    Connector		parent = getParent();
    
    if (parent instanceof WindowConnector) {
      if (getState().acceleratorKey != 0) {
	((WindowConnector)parent).getWidget().registerKeyCombination(createKeyCombination());
      }
    }
  }

  @OnStateChange("resources")
  /*package*/ void onResourceChange() {
    if (getWidget().image.getAttribute("src") != null) {
      getWidget().image.removeAttribute("src");
    }
    
    Icon	icon = getIcon();

    if (icon != null) {
      getWidget().image.setAttribute("src", icon.getUri());
    }
  }

  @OnStateChange("caption")
  /*package*/ void setCaption() {
    getWidget().label.setInnerText(getState().caption);
  }
  
  /**
   * Handles the enable state of this actor.
   */
  @OnStateChange("enabled")
  /*package*/ void setEnabled() {
    if (item != null) {
      item.setEnabled(getState().enabled);
    }
  }
  
  /**
   * Sets the actor to be enabled.
   * @param enabled The enabled status
   */
  public void setActorEnabled(boolean enabled) {
   if (enabled != internalIsEnabled) {
      getWidget().setEnabled(enabled);
      if (item != null) {
        item.setEnabled(enabled);
      }
      internalIsEnabled = enabled;
    }
  }

  /**
   * The actor is visible only if a resource is defined.
   * @return {@code true} if the actor should be drawn.
   */
  public boolean isVisible() {
    return getIcon() != null;
  }
  
  /**
   * Creates an equivalent menu Item for this actor.
   * @return The actor menu item.
   */
  public VActorNavigationItem createNavigationItem() {
    if (item == null) {
      item = new VActorNavigationItem(getState().caption,
                                      getState().menu,
                                      getAcceleratorKey(),
                                      new ScheduledCommand()
      {
        @Override
        public void execute() {
          onAction();
        }
      });
    }
    
    return item;
  }
  
  /**
   * Returns the accelerator key of this actor.
   * @return The accelerator key of this actor.
   */
  protected String getAcceleratorKey() {
    return new AcceleratorKeyCombination(getState().acceleratorKey,
                                         getState().modifiersKey,
                                         getWidget()).toString(VMainWindow.getLocale());
  }
  
  /**
   * Returns the parent window connector.
   * @return The parent window connector.
   */
  protected WindowConnector getWindow() {
    return ConnectorUtils.getParent(this, WindowConnector.class);
  }
  
  /**
   * Returns the parent block connector.
   * @return The parent block connector.
   */
  protected BlockConnector getBlock() {
    return ConnectorUtils.getParent(VInputTextField.getLastFocusedConnector(), BlockConnector.class);
  }
  
  /**
   * Creates the key combination.
   * @return The key combination.
   */
  protected AcceleratorKeyCombination createKeyCombination() {
    return new AcceleratorKeyCombination(getState().acceleratorKey, getState().modifiersKey, getWidget());
  }

  @Override
  public void onAction() {
    // fire the actor action
    if (isEnabled()) {
      // clean all dirty values in the client side of the parent window.
      getWindow().cleanDirtyValues(getBlock());
      getRpcProxy(ActorServerRpc.class).actionPerformed();
    }
  }
  
  @Override
  public boolean isEnabled() {
    return super.isEnabled() || internalIsEnabled;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VActorNavigationItem                  item;
  private boolean                               internalIsEnabled;
}
