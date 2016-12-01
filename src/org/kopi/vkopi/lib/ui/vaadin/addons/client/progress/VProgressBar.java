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
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A custom progress bar widget
 */
public class VProgressBar extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  public VProgressBar() {
    setStyleName(Styles.PROGRESS_BAR);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  public void createProgressBar(int jobs) { 
    HorizontalPanel pane = new HorizontalPanel();
    pane.setStyleName(Styles.PROGRESS_BAR + "-pane");
    pane.setSpacing(5);
    elements = jobs;
    progress = 0;
    percentageLabel = new VSpan();
    elementsGrid = new Grid(1, DEFAULT_BAR_NUMBER);
    elementsGrid.setCellPadding(0);
    elementsGrid.setCellSpacing(0);
    
    for (int i = 0; i < DEFAULT_BAR_NUMBER; i++) {
      elementsGrid.getCellFormatter().getElement(0, i).setClassName(Styles.PROGRESS_BAR_BLANK);
      elementsGrid.getCellFormatter().getElement(0, i).addClassName(Styles.PROGRESS_BAR_BLANK);
    }

    elementsGrid.setWidth("100%");
    pane.add(elementsGrid);
    pane.add(percentageLabel);
    pane.setCellHorizontalAlignment(elementsGrid, HasHorizontalAlignment.ALIGN_CENTER);
    pane.setCellHorizontalAlignment(percentageLabel, HasHorizontalAlignment.ALIGN_CENTER);
    pane.setCellVerticalAlignment(elementsGrid, HasVerticalAlignment.ALIGN_MIDDLE);
    pane.setCellVerticalAlignment(percentageLabel, HasVerticalAlignment.ALIGN_MIDDLE);
    add(pane);
    setPercentageText(0f);
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
      setPercentageText(percentage * 100);
    } else {
      setPercentageText(100f);
    }
  }

  /**
   * Sets the percentage text.
   * @param percentage The percentage text.
   */
  public void setPercentageText(float percentage) {
    percentageLabel.setText(new Float(percentage).intValue() + "%");
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
    percentageLabel = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private int                            elements;
  private float                          progress;
  private Grid                           elementsGrid;         
  private VSpan                          percentageLabel;
  private static final int               DEFAULT_BAR_NUMBER = 60;
}
