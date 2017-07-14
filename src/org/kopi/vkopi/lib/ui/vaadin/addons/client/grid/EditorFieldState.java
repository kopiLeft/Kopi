/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.annotations.NoLayout;

/**
 * Grid editor field shared state
 */
@SuppressWarnings("serial")
public class EditorFieldState extends AbstractFieldState {
  
  /**
   * The navigation delegation to server mode.
   */
  @NoLayout
  public NavigationDelegationMode       navigationDelegationMode = NavigationDelegationMode.ALWAYS;
  
  /**
   * Tells that this field has a PREFLD trigger. This will tell that
   * the navigation should be delegated to server if the next target
   * field has a PREFLD trigger.
   */
  @NoLayout
  public boolean                        hasPreFieldTrigger;
  
  /**
   * The actors associated with this field.
   */
  @NoLayout
  public List<Connector>                actors = new ArrayList<Connector>();
  
  /**
   * The foreground color of the field.
   */
  public String                         foreground;
  
  /**
   * The background color of the field.
   */
  public String                         background;

  /**
   * The navigation delegation to server mode.
   */
  public static enum NavigationDelegationMode {
    /**
     * do not delegate navigation to server
     */
    NONE,
    
    /**
     * delegate navigation to server if the content of this field has changed
     */
    ONCHANGE,
    
    /**
     * delegate navigation to server side when the field is not empty. 
     */
    ONVALUE,
    
    /**
     * Always delegate navigation to server.
     */
    ALWAYS
  }
}
