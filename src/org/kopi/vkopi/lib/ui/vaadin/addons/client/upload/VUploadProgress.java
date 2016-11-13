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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.vaadin.client.ApplicationConnection;

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
    grid = new Grid(1, 51);
    grid.setStyleName("k-upload-progress-bar");
    grid.setCellPadding(0);
    grid.setCellSpacing(0);
    for (int i = 0; i < 50; i++) {
      grid.getCellFormatter().getElement(0, i).setClassName("k-upload-progress-empty-cell");
    }
    waitImage = new VImage();
    waitImage.setStyleName("k-upload-progress-wait");
    grid.setWidget(0, 50, waitImage);
    grid.getCellFormatter().setAlignment(0, 50, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
    initWidget(grid);
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
   * Sets the wait image.
   * @param connection The application connection.
   */
  public void setWaitImage(ApplicationConnection connection) {
    waitImage.setSrc(ResourcesUtil.getImageURL(connection, "wait_progress.gif"));
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
  public void setProgress(long currentJob) {
    if (currentJob <= totalJobs) {
      int           completed;   
      float         percentage;

      percentage = (currentJob / totalJobs) * 1f;
      completed = (int)(percentage * 50);
      for (int i = 0; i < 50; i++) {
        if (i <= completed) {
          grid.getCellFormatter().getElement(0, i).setClassName("k-upload-progress-full-cell");
        }
      }
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Grid                            grid;
  private long                                  totalJobs;
  private final VImage                          waitImage;
}
