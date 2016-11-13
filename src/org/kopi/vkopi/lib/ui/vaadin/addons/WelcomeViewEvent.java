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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import com.vaadin.ui.Component.Event;

/**
 * A welcome screen event that encapsulates the login informations.
 */
@SuppressWarnings("serial")
public class WelcomeViewEvent extends Event {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new WelcomeViewEvent instance.
   * @param source The source component.
   * @param username The user name.
   * @param password The password.
   * @param locale The locale.
   */
  public WelcomeViewEvent(WelcomeView source,
                          String username,
                          String password,
                          String locale)
  {
    super(source);
    this.username = username;
    this.password = password;
    this.locale = locale;
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the welcome screen component.
   * @return The welcome screen component.
   */
  public WelcomeView getWelcomeView() {
    return (WelcomeView) super.getSource();
  }
  
  /**
   * Returns the login user name.
   * @return The login user name.
   */
  public String getUsername() {
    return username;
  }
  
  /**
   * Returns the login password.
   * @return The login password.
   */
  public String getPassword() {
    return password;
  }
  
  /**
   * Returns the login locale.
   * @return The login locale.
   */
  public String getLocale() {
    return locale;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final String				username;
  private final String				password;
  private final String				locale;
}
