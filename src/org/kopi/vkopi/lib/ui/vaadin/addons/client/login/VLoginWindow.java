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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.login;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.LoginWindowListener;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The login window box used for identification of the user.
 * the user and its password are send to the server for verification.
 */
public class VLoginWindow extends SimplePanel implements ClickHandler, ChangeHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the login window box.
   */
  public VLoginWindow() {
    setStyleName(Styles.LOGIN_WINDOW);
    listeners = new ArrayList<LoginWindowListener>();
    loginBox = new VLoginBox();
    setWidget(loginBox);
    loginBox.addClickHandler(this);
    loginBox.addChangeHandler(this);
    setWidth("400px");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Initializes the content of the login box.
   * @param welcomeText The welcome text.
   * @param informationText The information text.
   * @param usernameLabel The user name label.
   * @param passwordLabel The password label.
   * @param languageLabel The language label.
   * @param loginText the login text.
   */
  protected void doInit(String welcomeText,
                        String informationText,
                        String usernameLabel,
                        String passwordLabel,
                        String languageLabel,
                        String loginText)
  {
    loginBox.setWelcomeText(welcomeText);
    loginBox.setInformationText(informationText);
    loginBox.setUsernameLabel(usernameLabel);
    loginBox.setPasswordLabel(passwordLabel);
    loginBox.setLanguageLabel(languageLabel);
    loginBox.setLoginText(loginText);
  }
  
  /**
   * Sets the welcome image.
   * @param welcomeImage The welcome image
   */
  public void setSloganImage(String welcomeImage) {
    loginBox.setWelcomeImage(welcomeImage);
  }
  
  /**
   * Adds a supported language for the application.
   * @param language The language display name.
   * @param isocode The language ISO code.
   */
  public void addSupportedLanguage(String language, String isocode) {
    loginBox.addLanguage(language, isocode);
  }
  
  /**
   * Sets the error.
   * @param error The error message.
   */
  public void setError(String error) {
    loginBox.setError(error);
  }
  
  /**
   * Removes an error
   */
  public void removeError() {
    loginBox.removeError();
  }
  
  /**
   * Sets the selected language.
   * @param language The language index.
   */
  public void setSelectedLanguage(int language) {
    loginBox.setSelectedLanguage(language);
  }
  
  /**
   * Adds a login window listener.
   * @param l The listener to be added.
   */
  protected void addLoginWindowListener(LoginWindowListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a login window listener.
   * @param l The listener to be removed.
   */
  protected void removeLoginWindowListener(LoginWindowListener l) {
    listeners.remove(l);
  }
  
  protected void fireLogin(String username, String password, String language) {
    for (LoginWindowListener l : listeners) {
      l.onLogin(username, password, language);
    }
  }
  
  @Override
  public void onClick(ClickEvent event) {
    removeError();
    fireLogin(loginBox.getUsername(), loginBox.getPassword(), loginBox.getSelectedLanguage());
  }
  
  @Override
  public void onChange(ChangeEvent event) {
    removeError();
    setLocale(loginBox.getSelectedLanguage());
  }
  
  /**
   * Sets the login box locale.
   * @param locale The locale to use.
   */
  protected void setLocale(String locale) {
    doInit(LocalizedProperties.getString(locale, "welcomeText"),
	   LocalizedProperties.getString(locale, "informationText"),
	   LocalizedProperties.getString(locale, "usernameLabel"),
	   LocalizedProperties.getString(locale, "passwordLabel"),
	   LocalizedProperties.getString(locale, "languageLabel"),
	   LocalizedProperties.getString(locale, "loginText"));
  }
  
  /**
   * Focus on the first field in the login box.
   */
  /*package*/ void focus() {
    loginBox.focus();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VLoginBox				loginBox;
  private List<LoginWindowListener>		listeners;
}
