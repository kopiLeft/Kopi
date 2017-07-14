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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.vaadin.client.WidgetUtil;

/**
 * The boolean field widget is an association of two check buttons
 * working exclusively to handle the three possible values handled
 * by a boolean field.
 * 
 * yes is checked & no is not checked --> true
 * no is checked & yes is not checked --> false
 * yes and no are both unchecked --> null
 * 
 * yes and no cannot be checked at the same time
 */
public class VBooleanField extends VObjectField implements KeyPressHandler, KeyDownHandler, ValueChangeHandler<Boolean>, HasValueChangeHandlers<Boolean> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public VBooleanField() {
    setStyleName(Styles.BOOLEAN_FIELD);
    content = new HorizontalPanel();
    yes = new CheckBox();
    no = new CheckBox();
    yes.addStyleName("true");
    no.addStyleName("false");
    content.add(yes);
    content.add(no);
    content.setCellVerticalAlignment(yes, HasVerticalAlignment.ALIGN_BOTTOM);
    content.setCellVerticalAlignment(no, HasVerticalAlignment.ALIGN_BOTTOM);
    setWidget(content);
    yes.addValueChangeHandler(this);
    no.addValueChangeHandler(this);
    addKeyPressHandler(this);
    addKeyDownHandler(this);
    sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    if (event.getTypeInt() == Event.ONMOUSEOVER) {
      setVisible(true);
    } else if (event.getTypeInt() == Event.ONMOUSEOUT) {
      if (!getElement().isOrHasChild(WidgetUtil.getFocusedElement()) && getValue() == null) {
        setVisible(false);
      }
    }
  }
  
  @Override
  public void onBlur(BlurEvent event) {
    super.onBlur(event);
    if (getValue() == null) {
      setVisible(false);
    }
  }
  
  @Override
  public void onFocus(FocusEvent event) {
    super.onFocus(event);
    setVisible(true);
  }
  
  @Override
  protected void setParentVisibility(boolean visible) {
    if (getValue() == null) {
      yes.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      no.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      removeStyleDependentName("visible");
    } else {
      setVisible(visible);
    }
    this.forceHiddenVisibility = !visible;
  }
  
  @Override
  public void setVisible(boolean visible) {
    if (!forceHiddenVisibility && visible) {
      yes.getElement().getStyle().setVisibility(Visibility.VISIBLE);
      no.getElement().getStyle().setVisibility(Visibility.VISIBLE);
      addStyleDependentName("visible");
    } else {
      yes.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      no.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      removeStyleDependentName("visible");
    }
  }
  
  @Override
  public boolean isVisible() {
    return yes.getElement().getStyle().getVisibility().equals(Visibility.VISIBLE)
      && no.getElement().getStyle().getVisibility().equals(Visibility.VISIBLE);
  }
  
  @Override
  protected boolean isNull() {
    return !yes.getValue() && !no.getValue();
  }

  @Override
  protected void setValue(Object o) {
    if (o == null) {
      yes.setValue(false);
      no.setValue(false);
    } else if (o instanceof Boolean) {
      if ((Boolean)o) {
        yes.setValue(true);
        no.setValue(false);
      } else {
        yes.setValue(false);
        no.setValue(true);
      }
    }
    handleComponentVisiblity();
  }
  
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    yes.setEnabled(enabled);
    no.setEnabled(enabled);
  }

  @Override
  protected void setColor(String foreground, String background) {
    // NOT SUPPORTED FOR BOOLEAN FIELDS
  }

  @Override
  protected Object getValue() {
    if (!yes.getValue() && !no.getValue()) {
      return null;
    } else if (yes.getValue()) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected void checkValue(int rec) {}
  
  @Override
  public void onValueChange(ValueChangeEvent<Boolean> event) {
    if (event.getSource() == yes && event.getValue()) {
      no.setValue(false);
    } else if (event.getSource() == no && event.getValue()) {
      yes.setValue(false);
    } else if (event.getSource() == yes && !event.getValue()) {
      if (mandatory && !no.getValue()) {
        yes.setValue(true);
      }
    } else if (event.getSource() == no && !event.getValue()) {
      if (mandatory && !yes.getValue()) {
        no.setValue(true);
      }
    }
    handleComponentVisiblity();
    ValueChangeEvent.fire(this, (Boolean)getValue());
  }
  
  @Override
  protected BooleanFieldConnector getConnector() {
    return ConnectorUtils.getConnector(client, this, BooleanFieldConnector.class);
  }
  
  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    String      text = String.valueOf(event.getCharCode()).toLowerCase();
    
    if (yes.getText().startsWith(text)) {
      yes.setValue(true, true);
    } else if (no.getText().startsWith(text)) {
      no.setValue(true, true);
    }
  }
  
  @Override
  public void onKeyDown(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
      if (!yes.getValue() && !no.getValue()) {
        yes.setValue(true, true);
      } else if (yes.getValue()) {
        no.setValue(true, true);
      } else {
        yes.setValue(false, true);
        no.setValue(false, true);
        if (mandatory) {
          yes.setValue(true, true);
        }
      }
      event.preventDefault();
      event.stopPropagation();
    }
  }
  
  /**
   * Handles the component visibility according to its value.
   */
  protected void handleComponentVisiblity() {
    if (getElement().isOrHasChild(WidgetUtil.getFocusedElement())) {
      // field is focused set it visible
      setVisible(true);
    } else if (getValue() == null) {
      setVisible(false);
    } else {
      setVisible(true);
    }
  }
  
  /**
   * Sets the name of the radio button inside the boolean field.
   * @param label The name of the radio buttons.
   * @param yes The localized label for true value.
   * @param no The localized label for false value.
   */
  public void setLabel(String label, String yes, String no) {
    label = label.replaceAll("\\s", "_");
    this.yes.setName(label);
    this.no.setName(label);
    this.yes.setTitle(yes);
    this.no.setTitle(no);
  }
  
  /**
   * Sets the value of this boolean field.
   * @param value The field value.
   */
  public void setValue(Boolean value) {
    setValue((Object)value);
  }
  
  /**
   * Sets this boolean field to be a mandatory field
   * @param mandatory Is it a mandatory field ?
   */
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }
  
  /**
   * Sets the blink state of the boolean field.
   * @param blink The blink state.
   */
  public void setBlink(boolean blink) {
    if (blink) {
      addStyleDependentName("blink");
    } else {
      removeStyleDependentName("blink");
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final HorizontalPanel                 content;
  private final CheckBox                        yes;
  private final CheckBox                        no;
  private boolean                               mandatory;
  private boolean                               forceHiddenVisibility;
}
