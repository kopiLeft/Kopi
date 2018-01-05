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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VButton;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * The actor field widget.
 */
public class VActorField extends VObjectField implements HasClickHandlers {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new actor field widget.
   */
  public VActorField() {
    button = new VButton();
    button.setStyleName(Styles.ACTOR_FIELD_BUTTON);
    setStyleName(Styles.ACTOR_FIELD);
    setWidget(button);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return button.addClickHandler(handler);
  }
  
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
  public void onFocus(FocusEvent event) {}
  
  @Override
  public void onBlur(BlurEvent event) {}
  
  @Override
  protected boolean isNull() {
    return true;
  }

  @Override
  protected void setValue(Object o) {}

  @Override
  protected void setColor(String foreground, String background) {
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
  protected Object getValue() {
    return null;
  }

  @Override
  protected void checkValue(int rec) {}
  
  @Override
  protected void setParentVisibility(boolean visible) {}
  
  @Override
  public void clear() {
    super.clear();
    button = null;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private VButton                       button;
}
