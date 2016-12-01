/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Icons;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasWidgets;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;

/**
 * Error type notification widget.
 */
public class VErrorNotification extends VAbstractNotification {
  
  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  
  /**
   * Creates the error widget.
   */
  public VErrorNotification() {
    sinkEvents(Event.ONKEYDOWN);
  }
  
  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  @Override
  public void setButtons(String locale) {
    close = new VInputButton(LocalizedProperties.getString(locale, "CLOSE"), new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
	hideErrorDetails();
        hide();
        fireOnClose(false);
      }
    });
    
    buttons.add(close);
    
    if (getConnector() != null && getConnector().getState().owner != null) {
      if ((getConnector().getState().owner instanceof ComponentConnector)
	  && ((ComponentConnector)getConnector().getState().owner).getState().errorMessage != null)
      {
	details = new VInputButton();
	details.addClickHandler(new ClickHandler() {

	  @Override
	  public void onClick(ClickEvent event) {
	    if (details.getInputElement().getValue().equals("+")) {
	      // show the details
	      details.getInputElement().setValue("-");
	      showErrorDetails();
	    } else {
	      // hide details
	      details.getInputElement().setValue("+");
	      hideErrorDetails();
	    }
	  }
	});
	// set details caption.
	details.getInputElement().setValue("+");
	buttons.add(details);
      }
    }
  }
  
  @Override
  public void setImage(ApplicationConnection connection) {
    image.setSrc(ResourcesUtil.getImageURL(connection, Icons.ERROR));
  }

  @Override
  public void focus() {
    if (close != null) {
      close.focus();
    }
  }
  
  /**
   * Shows the error details. This is a related
   * to the component component error set for this
   * error notification.
   */
  protected void showErrorDetails() {
    ErrorNotificationConnector		connector;
    ComponentConnector			owner;
    
    connector = getConnector();
    if (connector == null) {
      // give up here but should never happen since this method is called only
      // from details button.
      return;
    }
    
    owner = (ComponentConnector) connector.getState().owner;
    if (owner == null || owner.getState().errorMessage == null) {
      return; // this should never happen
    }
    // application connection is available since we
    // had the component connector here
    detailsPopup = new VErrorMessagePopup(client);
    detailsPopup.setMessage(owner.getState().errorMessage);
    if (popup.getParent() instanceof HasWidgets) {
      ((HasWidgets)popup.getParent()).add(detailsPopup);
    }
    detailsPopup.setPixelSize(getElement().getClientWidth(), 200); // ensure that it would be under the notification.
    // show it relative to this notification.
    detailsPopup.showRelativeTo(this);
  }
  
  /**
   * Hides the error details.
   */
  protected void hideErrorDetails() {
    if (detailsPopup != null) {
      detailsPopup.hide();
      detailsPopup.clear();
      detailsPopup.removeFromParent();
      detailsPopup = null;
    }
  }
  
  /**
   * Returns the component connector.
   * @return The component connector.
   */
  protected ErrorNotificationConnector getConnector() {
    return ConnectorUtils.getConnector(client, this, ErrorNotificationConnector.class);
  }
  
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    if (event.getTypeInt() == Event.ONKEYDOWN) {
      switch (event.getKeyCode()) {
      case KeyCodes.KEY_LEFT:
	if (close != null) {
	  close.focus();
	  closeFocused = true;
	  detailsFocused = false;
	}
	break;
      case KeyCodes.KEY_RIGHT:
	if (details != null) {
	  details.focus();
	  closeFocused = false;
	  detailsFocused = true;
	}
	break;
      case KeyCodes.KEY_ENTER:
	if (closeFocused) {
	  close.click();
	} else if (detailsFocused) {
	  details.click();
	}
	break;
      default:
	break;
      }
    }
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    if (close != null && close.getCaption().toLowerCase().charAt(0) == event.getCharCode()) {
      close.click();
    } else if (details != null && details.getCaption().toLowerCase().charAt(0) == event.getCharCode()) {
      details.click();
    }
  }
  
  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------
  
  private VErrorMessagePopup			detailsPopup;
  private VInputButton				details;
  private VInputButton          		close;
  /*package*/ ApplicationConnection		client; // used to get the component connector.
  private boolean				closeFocused;
  private boolean				detailsFocused;
}