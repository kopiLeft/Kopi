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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VIcon;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.SimpleFocusablePanel;

/**
 * The upload widget composed of an upload form and
 * from confirm box to validate or to invalidate the
 * file choice.
 */
public class VUpload extends SimpleFocusablePanel implements CloseHandler<PopupPanel> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the upload widget.
   */
  public VUpload() {
    HorizontalPanel     buttons;
    
    title = new VSpan();
    form = new VUploadForm();
    content = new FlexTable();
    textInput = new VUploadTextInput();
    ok = new VInputButton();
    cancel = new VInputButton();
    browse = new VInputButton();
    image = new VIcon();
    buttons = new HorizontalPanel();
    setStyleName("k-upload");
    content.setStyleName("k-upload-content");
    buttons.setStyleName("k-upload-buttons");
    browse.setStyleName("k-upload-browse");
    textInput.setStyleName("k-upload-textinput");
    textInput.setReadOnly(true);
    textInput.setCharacterWidth(80);
    content.setWidget(0, 0, title);
    content.getFlexCellFormatter().setColSpan(0, 0, 3);
    content.setWidget(1, 0, image);
    content.setWidget(1, 1, textInput);
    content.setWidget(1, 2, browse);
    content.getFlexCellFormatter().setColSpan(2, 0, 3);
    buttons.add(cancel);
    buttons.add(ok);
    content.setWidget(3, 0, buttons);
    content.getFlexCellFormatter().setColSpan(3, 0, 3);
    content.setWidget(4, 0, form);
    content.getFlexCellFormatter().setColSpan(3, 0, 3);
    content.getCellFormatter().setStyleName(0, 0, "k-upload-title");
    content.getCellFormatter().setStyleName(2, 0, "k-upload-progress");
    content.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    content.getCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    content.getCellFormatter().setAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    content.getCellFormatter().setAlignment(1, 2, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    content.getCellFormatter().setAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    content.getCellFormatter().setAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    setWidget(content);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the upload widget image.
   * @param connection The application connection.
   */
  protected void setImage(ApplicationConnection connection) {
    image.setName("upload");
    image.addStyleName("k-upload-image");
  }
  
  /**
   * Localizes the upload widget.
   * @param locale The locale to be used.
   */
  protected void setLocale(String locale) {
    ok.setCaption(LocalizedProperties.getString(locale, "UPLOAD"));
    cancel.setCaption(LocalizedProperties.getString(locale, "CANCEL"));
    browse.setCaption(LocalizedProperties.getString(locale, "BROWSE"));
    title.setText(LocalizedProperties.getString(locale, "UPTITLE"));
  }
  
  /**
   * Shows the upload component.
   * @param connection The application connection.
   * @param parent The parent widget.
   */
  public void show(ApplicationConnection connection, HasWidgets parent) {
    popup = new VPopup(connection, false, true);
    
    popup.setWidget(this);
    parent.add(popup);
    addHandlers();
    popup.setGlassEnabled(true);
    popup.setGlassStyleName("k-upload-glass");
    ok.setEnabled(false);
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        popup.center();
      }
    });
  }
  
  /**
   * Sets the progress bar widget for this upload.
   * @param progress The progress bar widget.
   */
  public void setProgressWidget(VUploadProgress progress) {
    this.progress = progress;
    content.setWidget(2, 0, this.progress);
    progress.setVisible(false);
  }
  
  /**
   * Hides the upload form.
   */
  public void hideForm() {
    form.hide();
  }
  
  /**
   * Sets the mime type to be selected.
   * @param mimeType The mime type.
   */
  public void setMimeType(String mimeType) {
    form.fu.setMimeType(mimeType);
  }
  
  /**
   * Adds a change handler to this upload widget.
   * @param handler The change handler.
   * @return The handler registration.
   */
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return form.fu.addChangeHandler(handler);
  }

  /**
   * Disables or enables the frame title.
   * @param disable The ability state.
   */
  public void disableTitle(boolean disable) {
    form.disableTitle(disable);
  }
  
  /**
   * Sets the selected file.
   * @param file The selected file.
   */
  public void setSelectedFile(String file) {
    textInput.setValue(file);
  }
  
  /**
   * Returns the selected file.
   * @return The selected file.
   */
  public String getSelectedFile() {
    return form.fu.getFilename();
  }
  
  /**
   * Submits the upload
   */
  public void submit() {
    form.submit();
  }
  
  /**
   * Disables the upload
   */
  public void disableUpload() {
    form.disableUpload();
  }

  /**
   * For internal use only. May be removed or replaced in the future.
   */
  public void ensureTargetFrame() {
    form.ensureTargetFrame();
  }
  
  /**
   * Enables the upload.
   */
  public void enableUpload() {
    form.enableUpload();
  }
  
  /**
   * Server-side form handler.
   * @param action The action name.
   */
  protected void setAction(String action) {
    form.getFormElement().setAction(action);
  }
  
  /**
   * Sets the upload name.
   * @param name The upload name.
   */
  protected void setName(String name) {
    form.fu.setName(name);
  }
  
  /**
   * Sets the form application connection.
   * @param client The application connection.
   */
  protected void setFormClient(ApplicationConnection client) {
    form.client = client;
  }
  
  /**
   * Sets the next upload ID.
   * @param nextUploadId The next upload ID.
   */
  protected void setNextUploadId(int nextUploadId) {
    form.nextUploadId = nextUploadId;
  }
  
  /**
   * Sets the form paintable ID.
   * @param paintableId The paintable ID.
   */
  protected void setFormPaintableId(String paintableId) {
    form.paintableId = paintableId;
  }
  
  /**
   * Schedules an upload.
   * @param delayMillis The delay in ms.
   */
  protected void schedule(int delayMillis) {
    form.timer.schedule(delayMillis);
  }
  
  /**
   * Hides the upload widget.
   */
  protected void hide() {
    if (popup != null) {
      popup.hide();
    }
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent();
  }
  
  /**
   * Adds necessary handlers for this upload widget.
   */
  private void addHandlers() {
    ok.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        ok.setEnabled(false);
        // The upload my block for network reasons
        // we allow the user to cancel the upload
        // and get the control of the browser again.
        //cancel.setEnabled(false);
        form.submit();
      }
    });
    form.fu.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        if (form.fu.getFilename() != null && form.fu.getFilename().length() > 0) {
          setSelectedFile(form.fu.getFilename());
          ok.setEnabled(true);
        }
      }
    });
    browse.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        form.fu.click();
      }
    });
    cancel.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        ConnectorUtils.getConnector(form.client, VUpload.this, UploadConnector.class).cancel();
      }
    });
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final FlexTable                       content;
  private final VSpan                           title;
  private final VUploadForm                     form;
  private final VUploadTextInput                textInput;
  private final VInputButton                    ok;
  private final VInputButton                    cancel;
  private final VInputButton                    browse;
  private final VIcon                           image;
  private VUploadProgress                       progress;
  private VPopup                                popup;
}
