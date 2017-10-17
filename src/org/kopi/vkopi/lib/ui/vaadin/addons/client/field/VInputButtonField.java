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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

/**
 * A field that wraps an input button as an element. 
 */
public class VInputButtonField extends VInputTextField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the button field instance.
   */
  public VInputButtonField() {
    super(Document.get().createButtonInputElement());
    sinkEvents(Event.ONCLICK);
    addStyleDependentName("action");
    lazyWidthSetter = new Timer() {
      
      @Override
      public void run() {
        getElement().getStyle().setWidth(getInputElement().getSize() * CHAR_WIDTH, Unit.PX);
      }
    };
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void onFocus(FocusEvent event) {}
  
  @Override
  public void onBlur(BlurEvent event) {}
  
  @Override
  public void setFocus(boolean focused) {}
  
  @Override
  public void onBrowserEvent(Event event) {
    if (event.getTypeInt() == Event.ONCLICK) {
      getFieldConnector().actionPerformed();
    }
    super.onBrowserEvent(event);
  }
  
  @Override
  protected boolean hasAutoComplete() {
    return false;
  }
  
  @Override
  public void setEnabled(boolean enabled) {}
  
  @Override
  protected void onLoad() {
    lazyWidthSetter.schedule(WIDTH_SETTER_DELAY);
  }
  
  @Override
  public void release() {
    lazyWidthSetter = null;
    super.release();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Timer                         lazyWidthSetter;
  /**
   * TODO use font metric to detected the 
   */
  private static final double           CHAR_WIDTH=  9.833;
  private static final int              WIDTH_SETTER_DELAY = 40; 
}
