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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.actor;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.AcceleratorKeyCombination;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.AcceleratorKeyHandler.HasAcceleratorKey;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.ActionListener;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * The actor widget. Composed of a link and an icon.
 */
@SuppressWarnings("deprecation")
public class VActor extends VSpanPanel implements ClickHandler, HasAcceleratorKey, HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the actor widget.
   */
  public VActor() {
    AnchorElement 		anchor;
    
    sinkEvents(Event.ONCLICK);
    setStyleName(Styles.ACTOR);
    listeners = new ArrayList<ActionListener>();
    anchor = Document.get().createAnchorElement();
    anchor.setHref("#");
    anchor.setClassName(Styles.ACTOR_ANCHOR);
    DOM.appendChild(getElement(), anchor);
    image = DOM.createImg();
    image.setClassName(Styles.ACTOR_IMAGE);
    label = DOM.createSpan();
    label.setClassName(Styles.ACTOR_LABEL);
    DOM.appendChild(anchor, image);
    DOM.appendChild(anchor, label);
    addDomHandler(this, ClickEvent.getType());
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds an action listener.
   * @param l The action listener to be added.
   */
  public void addActionListener(ActionListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes an action listener.
   * @param l The action listener to be removed.
   */
  public void removeActionListener(ActionListener l) {
    listeners.remove(l);
  }
  
  @Override
  public void onKeyPress() {
    if (isEnabled()) {
      fireAction();
    }
  }
  
  @Override
  public void onClick(ClickEvent event) {
    if (isEnabled()) {
      fireAction();
    }
  }

  /**
   * Sets whether this actor is enabled.
   * @param enabled <code>true</code> to enable the actor, <code>false</code> to disable it
   */
  @Override
  public final void setEnabled(boolean enabled) {
    if (isEnabled() != enabled) {
      this.enabled = enabled;
    }
  }

  @Override
  public final boolean isEnabled() {
    return enabled;
  }
  
  /**
   * Fires the action. Notify all registered listeners.
   */
  protected void fireAction() {
    for (ActionListener l : listeners) {
      l.onAction();
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean				enabled;
  private final List<ActionListener>		listeners;
  /*package*/ Element				image;
  /*package*/ Element				label;
  public AcceleratorKeyCombination		keyCombination;
}
