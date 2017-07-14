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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.notification;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.Event;

/**
 * Confirm type notification widget.
 */
public class VConfirmNotification extends VAbstractNotification {
  
  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public void setButtons(String locale) {
    ok = new VInputButton(LocalizedProperties.getString(locale, "OK"), new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        hide();
        fireOnClose(true);
      }
    });
    
    cancel = new VInputButton(LocalizedProperties.getString(locale, "NO"), new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        hide();
        fireOnClose(false);
      }
    });
    buttons.add(ok);
    buttons.add(cancel);
  }
  
  @Override
  public void focus() {
    if (yesIsDefault && ok != null) {
      ok.focus();
      okFocused = true;
      cancelFocused = false;
    } else if (cancel != null) {
      cancel.focus();
      okFocused = false;
      cancelFocused = true;
    }
  }
  
  @Override
  protected String getIconName() {
    return "question-circle";
  }
  
  @Override
  protected boolean showGlassPane() {
    return true;
  }
  
  @Override
  protected boolean goBackToLastFocusedWindow() {
    return true;
  }
  
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    if (event.getTypeInt() == Event.ONKEYDOWN) {
      switch (event.getKeyCode()) {
      case KeyCodes.KEY_LEFT:
	if (ok != null) {
	  ok.focus();
	  okFocused = true;
	  cancelFocused = false;
	}
	break;
      case KeyCodes.KEY_RIGHT:
	if (cancel != null) {
	  cancel.focus();
	  okFocused = false;
	  cancelFocused = true;
	}
	break;
      case KeyCodes.KEY_ENTER:
	if (okFocused) {
	  ok.click();
	} else if (cancelFocused) {
	  cancel.click();
	}
	break;
      default:
	break;
      }
    }
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    if (cancel != null && event.getCharCode() == cancel.getCaption().toLowerCase().charAt(0)) {
      cancel.click();
    } else if (ok != null && event.getCharCode() == ok.getCaption().toLowerCase().charAt(0)) {
      ok.click();
    }
  }
  
  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------
  
  private VInputButton                  ok;
  private VInputButton                  cancel;
  private boolean			okFocused;
  private boolean			cancelFocused;
}
