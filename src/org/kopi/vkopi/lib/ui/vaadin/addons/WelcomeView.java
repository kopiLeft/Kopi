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

import java.util.Locale;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.login.WelcomeViewClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.login.WelcomeViewServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.login.WelcomeViewState;

import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponent;

/**
 * The server side of the welcome screen.
 */
@SuppressWarnings("serial")
public class WelcomeView extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the welcome view server component.
   * @param locale The application locale.
   * @param languages The supported languages.
   * @param logo The application logo
   */
  public WelcomeView(Locale locale,
                     Locale[] languages,
                     Resource logo,
                     String href)
  {
    registerRpc(rpc);
    setImmediate(true);
    getState().locale = locale.toString();
    for (Locale language : languages) {
      getState().languages.put(language.getDisplayName(), language.toString());
    }
    getState().href = href;
    setIcon(logo);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected WelcomeViewState getState() {
    return (WelcomeViewState) super.getState();
  }
  
  /**
   * Registers a welcome screen listener on this component.
   * @param listener The listener to be registered.
   */
  public void addWelcomeViewListener(WelcomeViewListener listener) {
    addListener(WelcomeViewEvent.class, listener, WelcomeViewListener.ON_LOGIN_METHOD);
  }

  /**
   * Removes the welcome screen listener.
   * @param listener The Listener to be removed.
   */
  public void removeActionListener(WelcomeViewListener listener) {
    removeListener(WelcomeViewEvent.class, listener, WelcomeViewListener.ON_LOGIN_METHOD);
  }
  
  /**
   * Fires an action performed event to all registered listeners.
   */
  protected void fireLogin(String username, String password, String language) {
    fireEvent(new WelcomeViewEvent(this, username, password, language));
  }
  
  /**
   * Sets the login error cause.
   * @param cause The error cause.
   */
  public void setError(Throwable cause) {
    getRpcProxy(WelcomeViewClientRpc.class).onError(cause.getMessage());
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private WelcomeViewServerRpc			rpc = new WelcomeViewServerRpc() {
    
    @Override
    public void onLogin(String username, String password, String language) {
      fireLogin(username, password, language);
    }
  };
}
