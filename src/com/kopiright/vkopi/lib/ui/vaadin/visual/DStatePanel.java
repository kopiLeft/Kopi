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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;

/**
 * The <code>DStatePanel</code> is a panel displaying the window state.
 */
@SuppressWarnings("serial")
public class DStatePanel extends CssLayout {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DStatePanel</code> instance.
   */
  public DStatePanel() {
    addStyleName(KopiTheme.STATE_PANEL);
    setSizeFull();
  }
  
  /**
   * Sets the info panel component.
   * @param info The info panel component.
   */
  public void setInfo(Panel info) { //!!! FIXME Use CSSLayout instead of panel.
    if (this.info != null) {
      removeComponent(this.info);
    }
    
    this.info = info;
    this.info.addStyleName(KopiTheme.PANEL_LIGHT);
    addComponent(this.info);
  }
  
  /**
   * Sets the info panel that current process accept user interrupt
   * @param allowed Is the interrupt allowed ?
   */
  public void setUserInterrupt(boolean allowed) {
    // nothing to be set.
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Panel					info;
}
