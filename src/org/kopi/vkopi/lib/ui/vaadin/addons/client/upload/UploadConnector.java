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

import org.kopi.vkopi.lib.ui.vaadin.addons.Upload;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

@SuppressWarnings({ "serial", "deprecation" })
@Connect(value = Upload.class, loadStyle = LoadStyle.LAZY)
public class UploadConnector extends AbstractComponentConnector implements Paintable {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>UploadConnector</code> instance.
   */
  public UploadConnector() {
    registerRpc(UploadClientRpc.class, new UploadClientRpc() {

      @Override
      public void submitUpload() {
	getWidget().submit();
      }

      @Override
      public void onProgress(final long contentLength, final long receivedBytes) {
        Scheduler.get().scheduleEntry(new ScheduledCommand() {

          @Override
          public void execute() {
            getWidget().setProgress(receivedBytes, contentLength);
          }
        });
      }
    });
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public VUpload getWidget() {
    return (VUpload) super.getWidget();
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  @Override
  protected void init() {
    super.init();
    getWidget().setImage(getConnection());
    getWidget().hideForm();
    getWidget().addChangeHandler(new ChangeHandler() {
      
      @Override
      public void onChange(ChangeEvent event) {
	if (hasEventListener(EventId.CHANGE)) {
	  getRpcProxy(UploadServerRpc.class).change(getWidget().getSelectedFile());
	}
      }
    });
  }

  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    super.onStateChanged(stateChangeEvent);
    getWidget().disableTitle(hasTooltip());
  }
  
  @Override
  public UploadState getState() {
    return (UploadState) super.getState();
  }
  
  /**
   * Cancels the upload.
   */
  public void cancel() {
    getRpcProxy(UploadServerRpc.class).cancel();
  }

  @Override
  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    if (!isRealUpdate(uidl)) {
      return;
    }
    
    if (uidl.hasAttribute("notStarted")) {
      getWidget().schedule(400);
      return;
    }
    getWidget().setFormClient(client);
    getWidget().setFormPaintableId(uidl.getId());
    getWidget().setNextUploadId(uidl.getIntAttribute("nextid"));
    getWidget().setAction(client.translateVaadinUri(uidl.getStringVariable("action")));
    getWidget().setName(uidl.getId() + "_file");
    if (!isEnabled() || isReadOnly()) {
      getWidget().disableUpload();
    } else if (!uidl.getBooleanAttribute("state")) {
      // Enable the button only if an upload is not in progress
      getWidget().enableUpload();
      getWidget().ensureTargetFrame();
    }
  }
  
  @Override
  public void onUnregister() {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        getWidget().hide();
        getWidget().clear();
        getWidget().removeFromParent();
        UploadConnector.super.onUnregister();
      }
    });
  }
  
  @Override
  public boolean hasTooltip() {
    return true;
  }

  @Override
  public TooltipInfo getTooltipInfo(Element element) {
    return new TooltipInfo(LocalizedProperties.getString(getState().locale, "UPHELP"), null);
  }
}

