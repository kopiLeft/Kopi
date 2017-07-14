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

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * The upload progress bar widget.
 */
public class VUploadProgress extends Composite {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the upload progress widget
   */
  public VUploadProgress() {
    grid = new Grid(1, CELLS_NUMBER + 1);
    grid.setStyleName("k-upload-progress-bar");
    grid.setCellPadding(0);
    grid.setCellSpacing(0);
    for (int i = 0; i < CELLS_NUMBER; i++) {
      grid.getCellFormatter().getElement(0, i).setClassName("k-upload-progress-empty-cell");
    }
    grid.getCellFormatter().setAlignment(0, CELLS_NUMBER, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    grid.getCellFormatter().getElement(0, CELLS_NUMBER).setClassName("k-upload-percentage");
    initWidget(grid);
    removeStyleName("v-widget");
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void setVisible(boolean visible) {
    if (visible) {
      getElement().getStyle().setVisibility(Visibility.VISIBLE);
    } else {
      getElement().getStyle().setVisibility(Visibility.HIDDEN);
    }
  }
  
  /**
   * Sets the progress of the upload.
   * @param receivedBytes The received bytes.
   * @param contentLength The content length.
   */
  public void setProgress(long receivedBytes, long contentLength) {
    setTotalJobs(contentLength);
    if (contentLength > 0) {
      setVisible(true);
    }
    setProgress(receivedBytes);
  }
  
  /**
   * Sets the total number of jobs.
   * @param totalJobs The total number of jobs.
   */
  public void setTotalJobs(long totalJobs) {
    this.totalJobs = totalJobs;
  }

  /**
   * Set the progress bar to the given job.
   * @param job progress to set
   */
  public void setProgress(float currentJob) {
    if (currentJob <= totalJobs) {
      int           completed;   
      float         percentage;

      percentage = (currentJob / totalJobs) * 1f;
      completed = (int)(percentage * CELLS_NUMBER);
      for (int i = 0; i < CELLS_NUMBER; i++) {
        if (i <= completed) {
          grid.getCellFormatter().getElement(0, i).removeClassName("k-upload-progress-empty-cell");
          grid.getCellFormatter().getElement(0, i).setClassName("k-upload-progress-full-cell");
          grid.getCellFormatter().getElement(0, CELLS_NUMBER).setInnerText(String.valueOf((int) (percentage * 100)) + "%");
        }
      }
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Grid                            grid;
  private long                                  totalJobs;
  private static final int                      CELLS_NUMBER = 170;
}
