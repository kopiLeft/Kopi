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

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class WaitIndicator extends VerticalLayout { // FIXME : use CSSLayout instead

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>WaitIndicator</code> instance.
   */
  public WaitIndicator () {
    setStyleName(KopiTheme.WAIT_INDICATOR_LAYOUT_STYLE);
    setImmediate(true);
    Image waitProgressBar = new Image(null, Utils.getImage("loading.gif").getResource());
    waitProgressBar.addStyleName(KopiTheme.WAIT_INDICATOR_STYLE);
    addComponent(waitProgressBar);
    setComponentAlignment(waitProgressBar, Alignment.TOP_CENTER);
    waitMessage = new Label();
    waitMessage.addStyleName(KopiTheme.WAIT_MESSAGE_STYLE);
    waitMessage.setImmediate(true);
    addComponent(waitMessage);
    setComponentAlignment(waitMessage, Alignment.MIDDLE_CENTER);
    setVisible(false);
  }
  
  //--------------------------------------------------------------
  // UTILS
  //--------------------------------------------------------------
  
  /**
   * Shows the wait window.
   * @param message the wait message.
   */
  public void show(final String message) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        WaitIndicator.this.setVisible(true);	 
        waitMessage.setValue(message);
      }
    });
  }
  
  /**
   * Hides the wait window.
   */
  public void hide() {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        waitMessage.setValue("");
        WaitIndicator.this.setVisible(false);     
      }
    });
  }
   
  //------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------
  
  private Label			waitMessage;
}
