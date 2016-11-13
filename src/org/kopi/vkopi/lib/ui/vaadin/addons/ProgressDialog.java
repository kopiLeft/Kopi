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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.progress.ProgressDialogClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.progress.ProgressDialogServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.progress.ProgressDialogState;

import com.vaadin.ui.AbstractComponent;
  
/**
 * The progress dialog server side component.
 */
@SuppressWarnings("serial")
public class ProgressDialog extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
    
  /**
   * Creates a new <code>ProgressDialog</code> instance.
   * @param title The progress title.
   * @param message The progress message.
   * @param totalJobs The job count.
   */
  public ProgressDialog(String title, String message, int totalJobs) {
    setImmediate(true);
    getState().title = title;
    getState().message = message;
    getState().totalJobs = totalJobs;
    registerRpc(rpc);
  }
  
  /**
   *  Creates a new <code>ProgressDialog</code> instance.
   */
  public ProgressDialog() {
    this("", "", 0);
  }
  
  //---------------------------------------------------
  // IMPLEMENATION
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
   * Sets the progress total jobs.
   * @param totalJobs The progress total jobs.
   */
  public void setTotalJobs(int totalJobs) {
    getState().totalJobs = totalJobs;
  }

  /**
   * Sets the progress dialog polling interval.
   * @param pollingInterval The polling interval in milliseconds.
   */
  public void setPollingInterval(int pollingInterval) {
    getState().pollingInterval = pollingInterval;
  }

  @Override
  protected ProgressDialogState getState() {
    return (ProgressDialogState) super.getState();
  }
  
  @Override
  protected ProgressDialogState getState(boolean markAsDirty) {
    return (ProgressDialogState) super.getState(markAsDirty);
  }
  
  /**
   * Sets the current job progress.
   * @param currentJob The current job progress.
   */
  public void setProgress(int currentJob) {
    if (currentJob > getState().totalJobs) {
      currentJob = getState().totalJobs;
    }
    this.currentJob = currentJob;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private int                                   currentJob;
  private final ProgressDialogServerRpc         rpc = new ProgressDialogServerRpc() {
    
    @Override
    public void poll(int currentJob) {
      if (ProgressDialog.this.currentJob != currentJob) {
        getRpcProxy(ProgressDialogClientRpc.class).onJobProgress(ProgressDialog.this.currentJob);
      }
    }
  };
}
