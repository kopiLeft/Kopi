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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.main;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VAnchor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VStrong;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The welcome text widget.
 * Contains also the logout button to disconnect from application.
 */
public class VWelcome extends FlowPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the welcome text widget.
   */
  public VWelcome() {
    VStrong		strong;
    
    strong = new VStrong();
    getElement().setId("welcome");
    welcomeText = new VSpan();
    welcomeLink = new VAnchor();
    welcomeLink.setHref("#");
    logoutLink = new VAnchor();
    logoutLink.setHref("#");
    welcomeLink.setId("welcome_link");
    logoutLink.setId("logout_link");
    strong.setWidget(welcomeLink);
    add(welcomeText);
    add(strong);
    add(new VSpan(" [ "));
    add(logoutLink);
    add(new VSpan(" ]"));
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the welcome text.
   * @param text The welcome text. 
   */
  protected void setWelcomeText(String welcomeText) {
    this.welcomeText.setText(welcomeText);
  }
  
  /**
   * Sets the welcome link text.
   * @param text The welcome link text.
   */
  protected void setWelcomeLink(String text) {
    welcomeLink.setText(text);
  }
  
  /**
   * Sets the logout link text.
   * @param text The logout link text.
   */
  protected void setLogoutLink(String text) {
    logoutLink.setText(text);
  }
  
  /**
   * Registers a click handler to the welcome text.
   * @param handler The handler to be registered.
   * @return The registration handler.
   */
  public HandlerRegistration addWelcomeClickHandler(ClickHandler handler) {
    return welcomeLink.addClickHandler(handler);
  }
  
  /**
   * Registers a click handler to the logout text.
   * @param handler The handler to be registered.
   * @return The registration handler.
   */
  public HandlerRegistration addLogoutClickHandler(ClickHandler handler) {
    return logoutLink.addClickHandler(handler);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VAnchor				welcomeLink;
  private final VAnchor				logoutLink;
  private final VSpan				welcomeText;
}
