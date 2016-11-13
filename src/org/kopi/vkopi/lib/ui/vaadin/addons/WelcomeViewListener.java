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

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.util.ReflectTools;

/**
 * Registered listeners are notified with the login action in
 * the welcome screen.
 */
public interface WelcomeViewListener extends Serializable {

  /**
   * Fired when a login action is fired on the welcome screen.
   * @param event The welcome screen event.
   */
  public void onLogin(WelcomeViewEvent event);
  
  //login method
  Method	ON_LOGIN_METHOD = ReflectTools.findMethod(WelcomeViewListener.class, "onLogin", WelcomeViewEvent.class);
}
