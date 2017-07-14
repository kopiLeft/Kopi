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
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSmall;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
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
    inner = new FlowPanel();
    info = new FlowPanel();
    value = new VSpan();
    caption = new VSmall();
    icon = new VIcon();
    inner.setStyleName(Styles.ACTOR_FIELD_INNER);
    info.setStyleName(Styles.ACTOR_FIELD_INFO);
    value.setStyleName(Styles.ACTOR_FIELD_VALUE);
    caption.setStyleName(Styles.ACTOR_FIELD_CAPTION);
    icon.addStyleName(Styles.ACTOR_FIELD_ICON);
    setStyleName(Styles.ACTOR_FIELD);
    info.add(value);
    info.add(caption);
    inner.add(icon);
    inner.add(info);
    setWidget(inner);
    sinkEvents(Event.ONCLICK);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Sets the actor field caption.
   * @param caption The field caption.
   */
  public void setCaption(String caption) {
    this.caption.setText(caption);
  }
  
  /**
   * Sets the actor field icon name.
   * @param icon The actor field icon name.
   */
  public void setIcon(String icon) {
    this.icon.setName(icon);
  }
  
  /**
   * Sets the actor field value.
   * @param value The field value.
   */
  public void setValue(String value) {
    if (value == null || value.length() == 0) {
      this.value.setText("X");
      this.value.getElement().getStyle().setVisibility(Visibility.HIDDEN);
    } else {
      this.value.setText(value);
      this.value.getElement().getStyle().setVisibility(Visibility.VISIBLE);
    }
  }
  
  @Override
  public void setColor(String foreground, String background) {
    if (foreground != null && foreground.length() > 0) {
      icon.getElement().getStyle().setColor(foreground);
      value.getElement().getStyle().setColor(foreground);
    } else {
      icon.getElement().getStyle().setColor("inherit");
      value.getElement().getStyle().setColor(foreground);
    }
    if (background != null && background.length() > 0) {
      inner.getElement().getStyle().setBackgroundColor(background);
    } else {
      inner.getElement().getStyle().setBackgroundColor("inherit");
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
    return value.getText();
  }

  @Override
  public void setBlink(boolean blink) { }

  @Override
  public void validate() throws InvalidEditorFieldException {}
  
  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addHandler(handler, ClickEvent.getType());
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
  
  private final FlowPanel                       inner;
  private final FlowPanel                       info;
  private final VSpan                           value;
  private final VSmall                          caption;
  private final VIcon                           icon;
  private boolean                               enabled;
}
