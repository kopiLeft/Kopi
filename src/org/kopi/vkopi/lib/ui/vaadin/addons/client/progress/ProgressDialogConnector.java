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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.progress;

import org.kopi.vkopi.lib.ui.vaadin.addons.ProgressDialog;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The progress dialog connector.
 */
@SuppressWarnings("serial")
@Connect(value = ProgressDialog.class, loadStyle = LoadStyle.EAGER)
public class ProgressDialogConnector extends AbstractComponentConnector {
  
  //--------------------------------------------------
  // IMPLEMENTATION
  //--------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    registerRpc(ProgressDialogClientRpc.class, rpc);
    getWidget().init(getConnection());
  }
  
  @Override
  public VProgressDialog getWidget() {
    return (VProgressDialog) super.getWidget();
  }
  
  @Override
  public ProgressDialogState getState() {
    return (ProgressDialogState) super.getState();
  }
  
  @OnStateChange("title")
  /*package*/ void setTitle() {
    getWidget().setTitle(getState().title);
  }
  
  @OnStateChange("message")
  /*package*/ void setMessage() {
    getWidget().setMessage(getState().message);
  }
  
  @OnStateChange("totalJobs")
  /*package*/ void createProgressBar() {
    getWidget().createProgressBar(getState().totalJobs);
    poller.scheduleRepeating(getState().pollingInterval);
  }
  
  @Override
  public void onUnregister() {
    getWidget().removeFromParent();
    cancelPolling();
    super.onUnregister();
  }
  
  /**
   * Cancel polling action
   */
  public void cancelPolling() {
    poller.cancel();
    // ensure that progress bar is hidden.
    new Timer() {
      public void run() {
        getWidget().hide();
        getWidget().clear();
        getWidget().removeFromParent();
      };
    }.schedule(400);
  }
  
  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------

  private ProgressDialogClientRpc               rpc = new ProgressDialogClientRpc() {
    
    @Override
    public void onJobProgress(final int currentJob) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().setProgress(currentJob);
        }
      });     
    }
    
    @Override
    public void onProgress() {
      getWidget().progress();
    }
  };

  private Timer poller = new Timer() {

    @Override
    public void run() {
      getRpcProxy(ProgressDialogServerRpc.class).poll(getWidget().getProgress());
    }
  };
}
