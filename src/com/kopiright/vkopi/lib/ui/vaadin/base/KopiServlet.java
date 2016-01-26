/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import java.util.Locale;

import javax.servlet.ServletException;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

/**
 * A customized servlet that handles the localization
 * of the "session expired" message.
 */
@SuppressWarnings("serial")
public class KopiServlet extends VaadinServlet implements SessionInitListener {

  @Override
  protected void servletInitialized() throws ServletException {
    super.servletInitialized();
    String		locale = getInitParameter("locale");
    
    // check if it is a valid locale.
    if (checkLocale(getInitParameter("locale"))) {
      this.locale = new Locale(locale.substring(0, 2), locale.substring(3, 5));
      getService().addSessionInitListener(this);
      // localize the session expired message
      getService().setSystemMessagesProvider(new SystemMessagesProvider() {

	@Override
	public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
	  CustomizedSystemMessages		message;

	  message = new CustomizedSystemMessages();
	  message.setSessionExpiredNotificationEnabled(true);
	  message.setSessionExpiredCaption(getLocalizedProperty("session-expired-caption"));
	  message.setSessionExpiredMessage(getLocalizedProperty("session-expired-message"));

	  return message;
	}
      });
    }
  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException {
    event.getSession().setLocale(locale);
  }
  
  @Override
  protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
    throws ServiceException
  {
    if (isCommunicationStatisticsEnabled()) {
      if (statistics == null) {
        statistics = new CommunicationStatistics(getDisplayStatisticsInterval(), isShowCommunicatedMessages());
        statistics.installBroadcasterListener();
        statistics.start();
      }

      return statistics.createServletService(this, deploymentConfiguration);
    } else {
      return super.createServletService(deploymentConfiguration);
    }
  }
  
  @Override
  public void destroy() {
    if (statistics != null) {
      statistics.stop();
    }
    
    super.destroy();
  }
  
  /**
   * Checks the given locale format.
   * @param locale The locale to be checked.
   * @return {@code true} if the locale has a valid format.
   */
  private boolean checkLocale(String locale) {
    char[]    		chars;
    
    chars = locale.toCharArray();
    if (chars.length != 5
        || chars[0] < 'a' || chars[0] > 'z'
        || chars[1] < 'a' || chars[1] > 'z'
        || chars[2] != '_'
        || chars[3] < 'A' || chars[3] > 'Z'
        || chars[4] < 'A' || chars[4] > 'Z')
    {
      return false;
    }
    
    return true;
  }
  
  /**
   * Returns a localized property from XML property file.
   * @param key The property key.
   * @param locale The localization locale.
   * @return The localized property.
   */
  private String getLocalizedProperty(String key) {
    LocalizationManager			manager;
    
    manager = new LocalizationManager(locale, Locale.getDefault());
    try {
      // Within a String, "''" represents a single quote in java.text.MessageFormat.
      return manager.getPropertyLocalizer(VLIB_PROPERTIES_RESOURCE_FILE, key).getValue().replaceAll("'", "''");
    } catch (InconsistencyException e) {
      return "!" + key + "!";
    }
  }
  
  /**
   * Returns the display statistics interval in seconds.
   * @return The display statistics interval in seconds.
   */
  protected int getDisplayStatisticsInterval() {
    if (getInitParameter("displayStatisticsInterval") == null) {
      return 60; // 1 minutes by default
    }
    
    return Integer.parseInt(getInitParameter("displayStatisticsInterval"));
  }
  
  /**
   * Returns <code>true</code> if we should show the communicated messages.
   * @return <code>true</code> if we should show the communicated messages.
   */
  protected boolean isShowCommunicatedMessages() {
    return Boolean.parseBoolean(getInitParameter("showCommunicatedMessages"));
  }
  
  /**
   * Returns <code>true</code> if we should display the communication statistics.
   * @return <code>true</code> if we should display the communication statistics.
   */
  protected boolean isCommunicationStatisticsEnabled() {
    return Boolean.parseBoolean(getInitParameter("communicationStatisticsEnabled"));
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  private Locale				locale;
  private CommunicationStatistics               statistics;
  private final static String 			VLIB_PROPERTIES_RESOURCE_FILE = "com/kopiright/vkopi/lib/resource/VlibProperties" ;
}
