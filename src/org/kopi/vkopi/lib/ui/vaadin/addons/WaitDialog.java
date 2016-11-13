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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.wait.WaitDialogState;

import com.vaadin.ui.AbstractComponent;

/**
 * The wait dialog server side component.
 */
@SuppressWarnings("serial")
public class WaitDialog extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
    
  /**
   * Creates a new <code>WaitDialog</code> instance.
   * @param title The WaitDialog title.
   * @param message The WaitDialog message.
   * @param maxTime The time to wait in milliseconds.
   */
  public WaitDialog(String title, String message, int maxTime) {
    setImmediate(true);
    getState().title = title;
    getState().message = message;
    getState().maxTime = maxTime;
  }
  
  /**
   *  Creates a new <code>WaitDialog</code> instance.
   */
  public WaitDialog() {
    this("", "", 0);
  }
  
  //---------------------------------------------------
  // IMPLEMENATIONS
  //---------------------------------------------------
  
  /**
   * Sets the progress window title.
   * @param title The progress title.
   */
  public void setTitle(String title) {
    getState().title = title;
  }
  
  /**
   * Sets the progress window message.
   * @param message The progress message.
   */
  public void setMessage(String message) {
    getState().message = message;
  }
  
  /**
   * Sets the wait time.
   * @param maxTime The progress total jobs.
   */
  public void setMaxTime(int maxTime) {
    getState().maxTime = maxTime;
  }
  
  @Override
  protected WaitDialogState getState() {
    return (WaitDialogState) super.getState();
  }
  
  @Override
  protected WaitDialogState getState(boolean markAsDirty) {
    return (WaitDialogState) super.getState(markAsDirty);
  }
}
