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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.upload;

import org.kopi.vkopi.lib.ui.vaadin.addons.UploadProgress;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * Upload progress connector implementation. 
 */
@Connect(value = UploadProgress.class, loadStyle = LoadStyle.LAZY)
@SuppressWarnings("serial")
public class UploadProgressConnector extends AbstractComponentConnector {

  @Override
  protected void init() {
    super.init();
    registerRpc(UploadProgressClientRpc.class, new UploadProgressClientRpc() {
      
      @Override
      public void onProgress(final long contentLength, final long receivedBytes) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          
          @Override
          public void execute() {
            getWidget().setProgress(receivedBytes, contentLength); 
          }
        });
      }
    });
  }
  
  @Override
  public VUploadProgress getWidget() {
    return (VUploadProgress) super.getWidget();
  }
}
