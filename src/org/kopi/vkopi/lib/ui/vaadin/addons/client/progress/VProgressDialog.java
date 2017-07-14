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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.progress;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VH4;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.FocusableFlowPanel;

/**
 * The progress dialog widget.
 */
public class VProgressDialog extends FocusableFlowPanel implements CloseHandler<PopupPanel> {

  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  
  /**
   * Creates a progress dialog with a title, a message and 
   * a progress bar widget.
   */
  public VProgressDialog() {
    setStyleName(Styles.PROGRESS_DIALOG);
    content = new FlowPanel();
    title = new VH4();
    message = new VParagraph();
    bar = new VProgressBar(this);
    percentageLabel = new VParagraph();
    content.add(message);
    content.add(bar);
    content.add(percentageLabel);
    add(title);
    add(content);
  }
  
  /**
   * Initializes the widget.
   * @param connection The application connection.
   */
  public void init(ApplicationConnection connection) {
    popup = new VPopup(connection, false, true);
    popup.addCloseHandler(this);
    popup.setGlassEnabled(true);
    popup.setGlassStyleName(Styles.PROGRESS_DIALOG + "-glass");
    popup.setAnimationEnabled(true);
  }
  
  /**
   * Shows the progress bar.
   */
  public void show(HasWidgets parent) {
    if (popup != null) {
      popup.setWidget(this);
      parent.add(popup);
      popup.center(VMainWindow.get().getCurrentWindow());
      popup.setWaiting();
    }
  }
  
  /**
   * Hides the progress bar.
   */
  public void hide() {
    if (popup != null) {
      popup.unsetWaiting();
      popup.hide();
    }
  }

  /**
   * Sets the percentage text.
   * @param percentage The percentage text.
   */
  public void setPercentageText(float percentage) {
    percentageLabel.setText(new Float(percentage).intValue() + "%");
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent();
  }
  
  @Override
  public void clear() {
    super.clear();
    content = null;
    popup = null;
    title = null;
    message = null;
    bar = null;
  }

  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------
  
  /**
   * Sets the progress bar title.
   * @param text The progress title.
   */
  public void setTitle(String text) {
    title.setText(text);
  }
  
  /**
   * Sets the progress bar message.
   * @param text The message.
   */
  public void setMessage(String text) {
    message.setText(text);
  }
  
  /**
   * Sets the progress bar current job.
   * @param progress The current job.
   */
  public void setProgress(int progress) {
    bar.setProgress(progress);
  }
  
  /**
   * Returns the bar progress percentage.
   * @return The bar progress percentage.
   */
  public int getProgress() {
    return (int) bar.getProgress();
  }
  
  /**
   * Creates the progress bar.
   * @param totalJobs The total jobs.
   */
  public void createProgressBar(int totalJobs) {
    bar.clear();
    bar.createProgressBar(totalJobs); 
  }
  
  /**
   * Updates the bar progress.
   */
  public void progress() {
    bar.progress();
  }
  
  //-------------------------------------------------
  // INNER CLASSES
  //-------------------------------------------------
  
  
  /**
   * A simple panel that wraps a p html tag. 
   */
  private static class VParagraph extends Widget {
    
    public VParagraph() {
      setElement(Document.get().createElement("p"));
    }
    
    /**
     * Sets the inner text for this widget element.
     * @param text The widget inner text.
     */
    public void setText(String text) {
      getElement().setInnerText(text);
    }
  }
  
  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  
  private FlowPanel                             content;
  private VPopup                                popup;
  private VH4                                   title;
  private VParagraph                            message;
  private VProgressBar                          bar;
  private VParagraph                            percentageLabel;
}
