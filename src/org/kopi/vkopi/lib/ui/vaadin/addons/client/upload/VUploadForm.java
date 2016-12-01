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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.upload;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.VConsole;

/**
 * The upload form widget used for transferring file from client to server.
 */
@SuppressWarnings("deprecation")
public class VUploadForm extends SimplePanel {
  
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the <code>VUpload</code> widget.
   */
  protected VUploadForm() {
    super(Document.get().createFormElement());
    setEncoding(getElement(), FormPanel.ENCODING_MULTIPART);
    getFormElement().setMethod(FormPanel.METHOD_POST);
    setWidget(panel);
    fu.getElement().setId("upload");
    panel.add(fu);
    setStyleName(CLASSNAME);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Hides the upload widget.
   */
  protected void hide() {
    getElement().getStyle().setPosition(Position.ABSOLUTE);
    getElement().getStyle().setLeft(-2000, Unit.PX);
    getElement().getStyle().setTop(-2000, Unit.PX);
  }

  /**
   * Disables the upload
   */
  public void disableUpload() {
    if (!submitted) {
      // Cannot disable the fileupload while submitting or the file won't
      // be submitted at all
      fu.setEnabled(false);
    }
    
    enabled = false;
  }

  /**
   * Enables the upload.
   */
  public void enableUpload() {
    fu.setEnabled(true);
    enabled = true;
    if (submitted) {
      /*
       * An old request is still in progress (most likely cancelled),
       * ditching that target frame to make it possible to send a new
       * file. A new target frame is created later."
       */
      cleanTargetFrame();
      submitted = false;
    }
  }
  
  /**
   * Submits the upload
   */
  public void submit() {
    if (submitted || !enabled) {
      VConsole.log("Submit cancelled (disabled or already submitted)");
      return;
    }
    if (fu.getFilename().length() == 0) {
      VConsole.log("Submitting empty selection (no file)");
    }
    // flush possibly pending variable changes, so they will be handled
    // before upload
    if (client != null) {
      client.sendPendingVariableChanges();
    }

    // This is done as deferred because sendPendingVariableChanges is also
    // deferred and we want to start the upload only after the changes have
    // been sent to the server
    Scheduler.get().scheduleDeferred(startUploadCmd);
  }

  /**
   * Disables or enables the frame title.
   * @param disable The ability state.
   */
  public void disableTitle(boolean disable) {
    if (disable) {
      // Disable title attribute for upload element.
      if (BrowserInfo.get().isChrome()) {
	// In Chrome title has to be set to " " to make it invisible
	fu.setTitle(" ");
      } else if (BrowserInfo.get().isFirefox()) {
	// In FF title has to be set to empty string to make it
	// invisible
	// Method setTitle removes title attribute when it's an empty
	// string, so setAttribute() should be used here
	fu.getElement().setAttribute("title", "");
      }
      // For other browsers absent title doesn't show default tooltip for
      // input element
    } else {
      fu.setTitle(null);
    }
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    if (client != null) {
      ensureTargetFrame();
    }
  }

  /**
   * For internal use only. May be removed or replaced in the future.
   */
  public void ensureTargetFrame() {
    if (synthesizedFrame == null) {
      // Attach a hidden IFrame to the form. This is the target iframe to
      // which the form will be submitted. We have to create the iframe
      // using innerHTML, because setting an iframe's 'name' property
      // dynamically doesn't work on most browsers.
      DivElement dummy = Document.get().createDivElement();
      dummy.setInnerHTML("<iframe src=\"javascript:''\" name='" + getFrameName() + "' style='position:absolute;width:0;height:0;border:0'>");
      synthesizedFrame = dummy.getFirstChildElement();
      Document.get().getBody().appendChild(synthesizedFrame);
      getFormElement().setTarget(getFrameName());
      onloadstrategy.hookEvents(synthesizedFrame, this);
    }
  }

  private String getFrameName() {
    return paintableId + "_TGT_FRAME";
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    if (!submitted) {
      cleanTargetFrame();
    }
  }

  /**
   * Cleans the target frame.
   */
  private void cleanTargetFrame() {
    if (synthesizedFrame != null) {
      Document.get().getBody().removeChild(synthesizedFrame);
      onloadstrategy.unHookEvents(synthesizedFrame);
      synthesizedFrame = null;
    }
  }

  
  /**
   * Returns the encapsulated form element.
   * @return The encapsulated form element.
   */
  protected FormElement getFormElement() {
    return getElement().cast();
  }
  
  /**
   * Cancels the upload.
   */
  protected void cancel() {
    UploadConnector		connector;
    
    connector = getConnector();
    if (connector != null) {
      connector.cancel();
    }
  }
  
  /**
   * Returns the upload connector.
   * @return The upload connector.
   */
  protected UploadConnector getConnector() {
    return ConnectorUtils.getConnector(client, this, UploadConnector.class);
  }

  private static native void setEncoding(Element form, String encoding)
  /*-{
    form.enctype = encoding;
    // For IE8
    form.encoding = encoding;
  }-*/;
  
  /**
   * Called by JSNI (hooked via {@link #onloadstrategy})
   */
  private void onSubmitComplete() {
    /* Needs to be run dereferred to avoid various browser issues. */
    Scheduler.get().scheduleDeferred(new Command() {
      
      @Override
      public void execute() {
	if (submitted) {
	  if (client != null) {
	    if (timer != null) {
	      timer.cancel();
	    }
	    VConsole.log("VUpload:Submit complete");
	    client.sendPendingVariableChanges();
	  }

	  rebuildPanel();

	  submitted = false;
	  enableUpload();
	  if (!isAttached()) {
	    /*
	     * Upload is complete when upload is already abandoned.
	     */
	    cleanTargetFrame();
	  }
	}
      }
    });
  }

  /**
   * Re-creates file input field and populates panel. This is needed as we
   * want to clear existing values from our current file input field.
   */
  private void rebuildPanel() {
    panel.remove(fu);
    //fu = new VFileUpload(this);
    fu.setName(paintableId + "_file");
    fu.getElement().setPropertyBoolean("disabled", !enabled);
    panel.add(fu);
    fu.sinkEvents(Event.ONCHANGE);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private ScheduledCommand startUploadCmd = new ScheduledCommand() {

    @Override
    public void execute() {
      getFormElement().submit();
      submitted = true;

      disableUpload();

      /*
       * Visit server a moment after upload has started to see possible
       * changes from UploadStarted event. Will be cleared on complete.
       * 
       * Must get the id here as the upload can finish before the timer
       * expires and in that case nextUploadId has been updated and is
       * wrong.
       */
      final int		thisUploadId = nextUploadId;
      
      timer = new Timer() {
	
	@Override
	public void run() {
	  // Only visit the server if the upload has not already
	  // finished
	  if (thisUploadId == nextUploadId) {
	    VConsole.log("Visiting server to see if upload started event changed UI.");
	    if (client != null) {
	      client.updateVariable(paintableId, "pollForStart", thisUploadId, true);
	    }
	  }
	}
      };
      timer.schedule(800);
    }
  };
  
  private VerticalPanel 			panel = new VerticalPanel();
  
  /**
   * When expecting big files, programmer may initiate some UI changes when
   * uploading the file starts. Bit after submitting file we'll visit the
   * server to check possible changes.
   * <p>
   * For internal use only. May be removed or replaced in the future.
   */
  public Timer 					timer;

  /**
   * some browsers tries to send form twice if submit is called in button
   * click handler, some don't submit at all without it, so we need to track
   * if form is already being submitted
   */
  private boolean 				submitted = false;
  private boolean 				enabled = true;
  private com.google.gwt.dom.client.Element 	synthesizedFrame;
  protected int 			        nextUploadId;
  private UploadIFrameOnloadStrategy 		onloadstrategy = GWT.create(UploadIFrameOnloadStrategy.class);

  /**
   * FileUpload component that opens native OS dialog to select file.
   * <p>
   * For internal use only. May be removed or replaced in the future.
   */
  protected VFileUpload 			fu = new VFileUpload();
  protected ApplicationConnection 		client;
  protected String 				paintableId;
  public static final String			CLASSNAME = "k-upload-form";
}