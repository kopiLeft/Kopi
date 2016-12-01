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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VContent;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VHeader;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VLink;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VMain;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.LoginWindowListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The welcome screen widget composed of header and content.
 * <pre>The header contains the logo company and usefull links.</pre>
 * <pre>The content contains the login panel used for users authentication.</pre>
 */
public class VWelcomeView extends FlowPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new welcome screen widget.
   */
  public VWelcomeView() {
    VContent		content;
    VLoginView		loginView;
    
    setStyleName(Styles.WELCOME_VIEW);
    header = new VHeader();
    main = new VMain();
    loginWindow = new VLoginWindow();
    add(header);
    add(main);
    header.setModuleList(new VEmptyModuleList(), 0);
    content = new VContent();
    main.setContent(content);
    loginView = new VLoginView();
    loginView.setLoginWindow(loginWindow);
    content.setContent(loginView, true);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the href for the anchor element.
   * @param href the href
   */
  public void setHref(String href) {
    header.setHref(href);
  }
  
  /**
   * Sets the target frame.
   * @param target The target frame.
   */
  public void setTarget(String target) {
    header.setTarget(target);
  }
  
  /**
   * Sets the company logo image.
   * @param url The logo image URL.
   * @param alt The alternate text.
   */
  public void setLogo(String url, String alt) {
    header.setImage(url, alt);
  }
  
  /**
   * Adds a link without separator.
   * @param link The link to be added.
   */
  public void addLink(VLink link) {
    header.addLink(link);
  }
  
  /**
   * Adds a link with separator.
   * @param link The link to be added.
   */
  public void addLinkWithSeparator(VLink link) {
    header.addLinkWithSeparator(link);
  }
  
  /**
   * Adds a supported language for the application.
   * @param language The language display name.
   * @param isocode The language ISO code.
   */
  public void addSupportedLanguage(String language, String isocode) {
    loginWindow.addSupportedLanguage(language, isocode);
  }
  
  /**
   * Adds a login window listener.
   * @param l The listener to be added.
   */
  public void addLoginWindowListener(LoginWindowListener l) {
    loginWindow.addLoginWindowListener(l);
  }
  
  /**
   * Removes a login window listener.
   * @param l The listener to be removed.
   */
  public void removeLoginWindowListener(LoginWindowListener l) {
    loginWindow.addLoginWindowListener(l);
  }
  
  /**
   * Sets the slogan image.
   * @param slogan The slogan image URL.
   */
  public void setSloganImage(String slogan) {
    loginWindow.setSloganImage(slogan);
  }
  
  /**
   * Sets the default welcome screen locale.
   * @param locale The welcome screen locale.
   */
  public void setDefaultLocale(String locale) {
    loginWindow.setLocale(locale);
  }
  
  /**
   * Sets the selected language.
   * @param language The language index.
   */
  public void setSelectedLanguage(int language) {
    loginWindow.setSelectedLanguage(language);
  }
  
  /**
   * Sets the error.
   * @param error The error message.
   */
  public void setError(String error) {
    loginWindow.setError(error);
  }
  
  /**
   * Focus on the first field in the login panel.
   */
  /*package*/ void focus() {
    loginWindow.focus();
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
        focus();
      }
    });
  }
  
  @Override
  public void clear() {
    super.clear();
    header = null;
    main = null;
    loginWindow = null;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VHeader				header;
  private VMain				        main;
  private VLoginWindow                          loginWindow;
}
