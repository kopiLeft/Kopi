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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VAnchorPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * The editor label widget
 */
public class VEditorLabel extends VAnchorPanel implements HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  public VEditorLabel() {
    caption = new VSpan();
    infoText = new VSpan();
    caption.setStyleName("caption");
    infoText.setStyleName("info");
    add(caption);
    add(infoText);
    // Hide focus outline in Mozilla/Webkit/Opera
    getElement().getStyle().setProperty("outline", "0px");
    // Hide focus outline in IE 6/7
    getElement().setAttribute("hideFocus", "true");
    setStyleName("editor-label");
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the label caption.
   * @param caption The caption text.
   */
  public void setCaption(String caption) {
    this.caption.setText(caption);
  }
  
  /**
   * Sets the info text of this label.
   * @param infoText The info text
   */
  public void setInfoText(String infoText) {
    this.infoText.setText(infoText);
  }
  
  /**
   * Sets this editor label to have the action trigger option.
   * @param hasAction The action ability
   */
  public void setHasAction(boolean hasAction) {
    if (hasAction) {
      addStyleDependentName("has-action");
      setHref("#");
    } else {
      removeStyleDependentName("has-action");
      
    }
  }
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (!enabled) {
      caption.addStyleName("v-disabled");
      infoText.addStyleName("v-disabled");
    } else {
      caption.removeStyleName("v-disabled");
      infoText.removeStyleName("v-disabled");
    }
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    // done via a timer and not GWT Scheduler to wait for Grid calculations and styles
    // to be applied. 10 ms is enough to have the right table header element size.
    new Timer() {
      
      @Override
      public void run() {
        if (DOM.getParent(getElement()) != null) {
          // we subtract 8px for CSS padding applied to the element.
          getElement().getStyle().setWidth(DOM.getParent(getElement()).getClientWidth() - 8, Unit.PX);
        }
      }
    }.schedule(100);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VSpan                   caption;
  private final VSpan                   infoText;
  private boolean                       enabled;
}
