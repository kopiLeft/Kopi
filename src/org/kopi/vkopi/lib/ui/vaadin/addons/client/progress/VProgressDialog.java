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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VH4;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ApplicationConnection;

/**
 * The progress dialog widget.
 */
public class VProgressDialog extends FocusPanel implements CloseHandler<PopupPanel> {

  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  
  /**
   * Creates a progress dialog with a title, a message and 
   * a progress bar widget.
   */
  public VProgressDialog() {
    setStyleName(Styles.PROGRESS_DIALOG);
    
    table = new FlexTable();
    table.setCellSpacing(5);
    table.setCellPadding(2);
    table.getElement().setPropertyString("align", "center");
    
    title = new VH4();
    table.setWidget(0, 0, title);
    table.getFlexCellFormatter().setStyleName(0, 0, Styles.PROGRESS_TITLE);
    
    message = new VSpan();
    table.setWidget(1, 0, message);
    table.getFlexCellFormatter().setStyleName(1, 0, Styles.PROGRESS_MESSAGE);
    
    bar = new VProgressBar();
    table.setWidget(2, 0, bar);
    
    table.setStyleName(Styles.PROGRESS_DIALOG + "-table");
    setWidget(table);
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
      popup.center();
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
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent();
  }
  
  @Override
  public void clear() {
    super.clear();
    table = null;
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
  // DATA MEMBERS
  //-------------------------------------------------
  
  private FlexTable                      	table;
  private VPopup                          	popup;
  private VH4                           	title;
  private VSpan                           	message;
  private VProgressBar                    	bar;
}
