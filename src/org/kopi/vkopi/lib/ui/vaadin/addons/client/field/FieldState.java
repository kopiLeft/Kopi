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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.annotations.NoLayout;

/**
 * The field shared state.
 */
@SuppressWarnings("serial")
public class FieldState extends AbstractComponentState {

  /**
   * Has an increment button ?
   */
  @NoLayout
  public boolean			hasIncrement;
  
  /**
   * Has a decrement button ?
   */
  @NoLayout
  public boolean			hasDecrement;
  
  /**
   * Has an action trigger ?
   */
  @NoLayout
  public boolean                        hasAction;
  
  /**
   * Is the action trigger enabled ?
   */
  @NoLayout
  public boolean                        isActionEnabled;
  
  /**
   * The field visibility
   */
  @NoLayout
  public boolean			visible = true;
  
  /**
   * The visible field height needed to create layout.
   */
  @NoLayout
  public int				visibleHeight = 1;
  
  /**
   * Tells if this field is never displayed in the detail view.
   */
  @NoLayout
  public boolean                        noDetail;
  
  /**
   * Tells if this field is never displayed in the chart view.
   */
  @NoLayout
  public boolean                        noChart;
  
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
   * The field dynamic access
   */
  @NoLayout
  public int                            dynAccess;
  
  /**
   * The default field access.
   */
  @NoLayout
  public int                            defaultAccess;
  
  /**
   * The position of the field for chart layout.
   */
  @NoLayout                             
  public int                            position;
  
  /**
   * The column view index of this field.
   */
  @NoLayout
  public int                            index;

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
