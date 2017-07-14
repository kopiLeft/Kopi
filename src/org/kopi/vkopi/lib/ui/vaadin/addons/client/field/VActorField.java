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
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSmall;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

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
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addHandler(handler, ClickEvent.getType());
  }
  
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
  
  /**
   * Sets the inner element width to hold the maximum value.
   * @param width The field width
   */
  public void setMaxWidth(int width) {
    inner.getElement().getStyle().setWidth(32 + 16 * width, Unit.PX);
  }
  
  @Override
  public void onFocus(FocusEvent event) {}
  
  @Override
  public void onBlur(BlurEvent event) {}
  
  @Override
  protected boolean isNull() {
    return value.getText() == null || value.getText().length() == 0;
  }

  @Override
  protected void setValue(Object o) {
    value.setText((String)o);
  }

  @Override
  protected void setColor(String foreground, String background) {
    if (foreground != null && foreground.length() > 0) {
      icon.getElement().getStyle().setColor(foreground);
      value.getElement().getStyle().setColor(foreground);
    } else {
      icon.getElement().getStyle().setColor("inherit");
      value.getElement().getStyle().setColor(foreground);
    }
    if (background != null && background.length() > 0) {
      inner.getElement().getStyle().setBackgroundColor(foreground);
    } else {
      inner.getElement().getStyle().setBackgroundColor("inherit");
    }
  }

  @Override
  protected Object getValue() {
    return value.getText();
  }

  @Override
  protected void checkValue(int rec) {}
  
  @Override
  protected void setParentVisibility(boolean visible) {}
  
  @Override
  public void clear() {
    super.clear();
    inner.clear();
    inner = null;
    info.clear();
    info = null;
    value = null;
    caption = null;
    icon = null;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
 
  private FlowPanel                     inner;
  private FlowPanel                     info;
  private VSpan                         value;
  private VSmall                        caption;
  private VIcon                         icon;
}
