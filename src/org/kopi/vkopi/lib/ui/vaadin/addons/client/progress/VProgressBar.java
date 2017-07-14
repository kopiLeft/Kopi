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

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A custom progress bar widget
 */
public class VProgressBar extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  public VProgressBar(VProgressDialog parent) {
    this.parent = parent;
    setStyleName(Styles.PROGRESS_BAR);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  public void createProgressBar(int jobs) {
    elements = jobs;
    progress = 0;
    elementsGrid = new Grid(1, DEFAULT_BAR_NUMBER);
    elementsGrid.setCellPadding(0);
    elementsGrid.setCellSpacing(0);
    
    for (int i = 0; i < DEFAULT_BAR_NUMBER; i++) {
      elementsGrid.getCellFormatter().getElement(0, i).setClassName(Styles.PROGRESS_BAR_BLANK);
      elementsGrid.getCellFormatter().getElement(0, i).addClassName(Styles.PROGRESS_BAR_BLANK);
    }

    elementsGrid.setWidth("100%");
    add(elementsGrid);
    setProgress(0f);
  }

  /**
   * Progress with one step.
   */
  public void progress() {
    progress++;
    setProgress(progress);
  }

  /**
   * Set the progress bar to the given job.
   * @param job progress to set
   */
  public void setProgress(float job) {
    int           completed;   
    float         percentage;

    if (job <= elements) {
      progress = job;

      percentage = (progress / elements) * 1f;
      completed = (int)(percentage * DEFAULT_BAR_NUMBER);
      for (int i = 0; i < DEFAULT_BAR_NUMBER; i++) {
        if (i <= completed) {
          elementsGrid.getCellFormatter().getElement(0, i).removeClassName(Styles.PROGRESS_BAR_BLANK);
          elementsGrid.getCellFormatter().getElement(0, i).addClassName(Styles.PROGRESS_BAR_FULL);
        }
      }
      setProgressPercentage(percentage * 100);
    } else {
      setProgressPercentage(100f);
    }
  }

  /**
   * Sets the progress percentage.
   * @param percentage The percentage of the progress.
   */
  public void setProgressPercentage(float percentage) {
    parent.setPercentageText(percentage);
  }

  /**
   * Returns the progress percentage.
   * @return The progress percentage.
   */
  public float getProgress() {
    return progress;
  }
  
  @Override
  public void clear() {
    super.clear();
    elementsGrid = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private int                           elements;
  private float                         progress;
  private Grid                          elementsGrid;
  private final VProgressDialog         parent;
  private static final int              DEFAULT_BAR_NUMBER = 60;
}
