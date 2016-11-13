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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.wait;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.progress.VProgressDialog;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * The wait dialog window is a progress bar controlled
 * by a time end limit.
 * The progress will be handled in client side and all we
 * want from the server is to have the total time to wait.
 */
public class VWaitDialog extends VProgressDialog {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the wait dialog widget.
   */
  public VWaitDialog() {
    super();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void show(HasWidgets parent) {
    super.show(parent);
    startWait();
  }

  @Override
  public void hide() {
    super.hide();
    timer.cancel();
    timer = null;
  }
  
  /**
   * Start waiting.
   */
  protected void startWait() {
    // wait only if needed
    if (timer == null) {
      timer = new Timer() {

	@Override
	public void run() {
	  if (getProgress() < timeout) {
	    progress();
	  } else {
	    timer.cancel();
	  }
	}
      };
      // progress every second.
      timer.scheduleRepeating(1000);
    }
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Sets the wait dialog timeout.
   * @param timeout The timeout value.
   */
  public void setMaxTime(int timeout) {
    createProgressBar(timeout / 1000);
    this.timeout = timeout;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private int					timeout;
  private Timer					timer;
}
