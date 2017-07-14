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

import org.kopi.vkopi.lib.ui.vaadin.addons.BooleanField;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The connector implementation of a boolean field 
 */
@SuppressWarnings("serial")
@Connect(value = BooleanField.class, loadStyle = LoadStyle.DEFERRED)
public class BooleanFieldConnector extends ObjectFieldConnector implements ValueChangeHandler<Boolean> {

  @Override
  protected void init() {
    super.init();
    handlerRegistration = getWidget().addValueChangeHandler(this);
    registerRpc(BooleanFieldClientRpc.class, new BooleanFieldClientRpc() {
      
      @Override
      public void setBlink(final boolean blink) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          
          @Override
          public void execute() {
            getWidget().setBlink(blink);            
          }
        });
      }

      @Override
      public void setFocus(final boolean focus) {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
          
          @Override
          public void execute() {
            getWidget().setFocus(focus);
          }
        });
      }
    });
  }
  
  @Override
  public VBooleanField getWidget() {
    return (VBooleanField) super.getWidget();
  }
  
  @Override
  public BooleanFieldState getState() {
    return (BooleanFieldState) super.getState();
  }
  
  /**
   * Sets the caption of the label tag attached with the widget radio button
   */
  @OnStateChange({"label", "trueRepresentation", "falseRepresentation"})
  /*package*/ void setLabel() {
    getWidget().setLabel(getState().label, getState().trueRepresentation, getState().falseRepresentation);
  }

  @Override
  public void onValueChange(ValueChangeEvent<Boolean> event) {
    getRpcProxy(BooleanFieldServerRpc.class).valueChanged(event.getValue());
  }
  
  @Override
  protected void sendValueToServer() {
    getRpcProxy(BooleanFieldServerRpc.class).valueChanged((Boolean)getWidget().getValue());
  }
  
  @Override
  public void onUnregister() {
    super.onUnregister();
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
    }
  }
  
  private HandlerRegistration           handlerRegistration;
}
