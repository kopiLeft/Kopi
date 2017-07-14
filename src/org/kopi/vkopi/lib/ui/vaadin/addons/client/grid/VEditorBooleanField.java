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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;

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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;

/**
 * An editor for boolean field widget.
 * 
 * The widget is an association of two check buttons
 * working exclusively to handle the three possible values handled
 * by a boolean field.
 * 
 * yes is checked & no is not checked --> true
 * no is checked & yes is not checked --> false
 * yes and no are both unchecked --> null
 * 
 * yes and no cannot be checked at the same time 
 */
public class VEditorBooleanField extends VFocusableEditorField<Boolean> implements KeyPressHandler, KeyDownHandler, ValueChangeHandler<Boolean>, HasValueChangeHandlers<Boolean> {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new boolean editor instance.
   */
  public VEditorBooleanField() {
    this(false);
  }
  
  /**
   * Creates a new boolean editor instance.
   */
  public VEditorBooleanField(boolean renderer) {
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    setStyleName("editor-booleanfield");
    content = new HorizontalPanel();
    setWidget(content);
    addKeyPressHandler(this);
    addKeyDownHandler(this);
    this.renderer = renderer;
    if (renderer) {
      sinkEvents(Event.ONCLICK);
    }
    sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @SuppressWarnings("deprecation")
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
    if (renderer && event.getTypeInt() == Event.ONCLICK) {
      if (yes.getElement().isOrHasChild(event.getTarget())) {
        ValueChangeEvent.fire(this, true);
      } else if (no.getElement().isOrHasChild(event.getTarget())) {
        ValueChangeEvent.fire(this, false);
      }
    }
  }
  
  @Override
  public void onBlur(BlurEvent event) {
    if (getValue() == null) {
      setVisible(false);
    }
  }
  
  @Override
  public void onFocus(FocusEvent event) {
    setVisible(true);
  }
  
  @Override
  public void setVisible(boolean visible) {
    if (visible) {
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
  protected void init(ApplicationConnection connection) {
    super.init(connection);
    yes = new CheckBox();
    no = new CheckBox();
    yes.addStyleName("true");
    no.addStyleName("false");
    content.add(yes);
    content.add(no);
    content.setCellVerticalAlignment(yes, HasVerticalAlignment.ALIGN_MIDDLE);
    content.setCellVerticalAlignment(no, HasVerticalAlignment.ALIGN_MIDDLE);
    yes.addValueChangeHandler(this);
    no.addValueChangeHandler(this);
    DOM.getParent(yes.getElement()).setClassName("editor-cell");
    if (renderer) {
      yes.setEnabled(false);
      no.setEnabled(false);
    }
  }

  @Override
  public Boolean getValue() {
    if (!yes.getValue() && !no.getValue()) {
      return null;
    } else if (yes.getValue()) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void setBlink(boolean blink) {
    if (blink) {
      addStyleDependentName("blink");
    } else {
      removeStyleDependentName("blink");
    }
  }

  @Override
  public void setColor(String foreground, String background) {
    // NOT SUPPORTED
  }

  @Override
  public void validate() throws InvalidEditorFieldException {}
  
  
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
    ValueChangeEvent.fire(this, getValue());
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

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    yes.setEnabled(enabled);
    no.setEnabled(enabled); 
  }
  
  @Override
  protected EditorBooleanFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorBooleanFieldConnector.class);
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
   * Sets the editor value.
   * @param value The new editor value.
   */
  public void setValue(Boolean value) {
    if (value == null) {
      yes.setValue(false);
      no.setValue(false);
    } else {
      if (value) {
        yes.setValue(true);
        no.setValue(false);
      } else {
        yes.setValue(false);
        no.setValue(true);
      }
    }
    handleComponentVisiblity();
  }
  
  /**
   * Sets this boolean field to be a mandatory field
   * @param mandatory Is it a mandatory field ?
   */
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
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

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final HorizontalPanel                 content;
  private CheckBox                              yes;
  private CheckBox                              no;
  private boolean                               mandatory;
  private boolean                               renderer;
}
