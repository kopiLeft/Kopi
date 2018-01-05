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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VButton;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ApplicationConnection;

/**
 * The actor field grid editor widget
 */
public class VEditorActorField extends SimplePanel implements EditorField<String>, HasClickHandlers {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new actor field widget.
   */
  public VEditorActorField() {
    button = new VButton();
    button.setStyleName(Styles.ACTOR_FIELD_BUTTON);
    setStyleName(Styles.ACTOR_FIELD);
    setWidget(button);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Sets the actor field caption.
   * @param caption The field caption.
   */
  public void setCaption(String caption) {
    button.setText(caption);
  }
  
  /**
   * Sets the actor field icon name.
   * @param icon The actor field icon name.
   */
  public void setIcon(String icon) {
    button.setIcon(icon);
  }
  
  @Override
  public void setColor(String foreground, String background) {
    if (foreground != null && foreground.length() > 0) {
      button.getElement().getStyle().setColor(foreground);
    } else {
      button.getElement().getStyle().setColor("inherit");
    }
    if (background != null && background.length() > 0) {
      button.getElement().getStyle().setBackgroundColor(foreground);
    } else {
      button.getElement().getStyle().setBackgroundColor("inherit");
    }
  }
  
  @Override
  public void focus() {
    // focus is not handled by this editor
  }

  @Override
  public ApplicationConnection getConnection() {
    return null;
  }

  @Override
  public String getValue() {
    return null;
  }

  @Override
  public void setBlink(boolean blink) { }

  @Override
  public void validate() throws InvalidEditorFieldException {}
  
  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return button.addClickHandler(handler);
  }
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      this.enabled = enabled;
      if (enabled) {
        removeStyleName("v-disabled");
      } else {
        addStyleName("v-disabled");
      }
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private VButton                               button;
  private boolean                               enabled;
}
