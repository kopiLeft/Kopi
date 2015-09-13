/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.chart.VChart;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.form.VListDialog;
import com.kopiright.vkopi.lib.preview.VPreviewWindow;
import com.kopiright.vkopi.lib.report.VReport;
import com.kopiright.vkopi.lib.ui.vaadin.chart.DChart;
import com.kopiright.vkopi.lib.ui.vaadin.form.DForm;
import com.kopiright.vkopi.lib.ui.vaadin.form.DListDialog;
import com.kopiright.vkopi.lib.ui.vaadin.preview.DPreviewWindow;
import com.kopiright.vkopi.lib.ui.vaadin.report.DReport;
import com.kopiright.vkopi.lib.visual.UIFactory;
import com.kopiright.vkopi.lib.visual.VHelpViewer;
import com.kopiright.vkopi.lib.visual.VMenuTree;
import com.kopiright.vkopi.lib.visual.VModel;

/**
 * The {@code JUIFactory} is a vaadin implementation of the {@link UIFactory}.
 */
public class VUIFactory extends UIFactory {

  /*
   * The only way to do here is to use the compiler javac with -sourcepath option to handle non yet
   * compiler UForm and UListDialog classes
   */
  @Override
  public UComponent createView(VModel model) {
    UComponent		view = null;

    if (model instanceof VMenuTree) {
      view = createMenuTree((VMenuTree)model);
    } else if (model instanceof VForm) {
      view = createForm((VForm)model);
    } else if (model instanceof VPreviewWindow) {
      view = createPreviewWindow((VPreviewWindow)model);
    } else if (model instanceof VReport) {
      view = createReport((VReport) model);
    } else if (model instanceof VChart) {
      view = createChart((VChart) model);
    } else if (model instanceof VHelpViewer) {
      view = createHelpViewer((VHelpViewer) model);
    } else if (model instanceof VListDialog) {
      view = createListDialog((VListDialog) model);
    } else {
      throw new IllegalArgumentException("NO UI IMPLEMENTATION FOR " + model.getClass());
    }
    
    model.setDisplay(view);
    return view;
  }

  //-----------------------------------------------------------
  // UI COMPONENTS CREATION
  //-----------------------------------------------------------

  /**
   * Creates the {@link DMenuTree} from a given model.
   * @param model The menu tree model
   * @return The  {@link DMenuTree} view.
   */
  protected DMenuTree createMenuTree(VMenuTree model) {
    return new DMenuTree(model);
  }

  /**
   * Creates the {@link DForm} from a given model.
   * @param model The form model
   * @return The  {@link DForm} view.
   */
  protected DForm createForm(VForm model) {
    return new DForm(model);
  }

  /**
   * Creates the {@link DPreviewWindow} from a given model.
   * @param model The preview model
   * @return The  {@link DPreviewWindow} view.
   */
  protected DPreviewWindow createPreviewWindow(VPreviewWindow model) {
    return new DPreviewWindow(model);
  }

  /**
   * Creates the {@link DReport} from a given model.
   * @param model The report model
   * @return The  {@link DReport} view.
   */
  protected DReport createReport(VReport model) {
    return new DReport(model);
  }

  /**
   * Creates the {@link DReport} from a given model.
   * @param model The report model
   * @return The  {@link DReport} view.
   */
  protected DChart createChart(VChart model) {
    return new DChart(model);
  }

  /**
   * Creates the {@link DHelpViewer} from a given model.
   * @param model The help viewer model
   * @return The  {@link DHelpViewer} view.
   */
  protected DHelpViewer createHelpViewer(VHelpViewer model) {
    return new DHelpViewer(model);
  }

  /**
   * Creates the {@link DListDialog} from a given model.
   * @param model The list dialog model
   * @return The  {@link DListDialog} view.
   */
  protected DListDialog createListDialog(VListDialog model) {
    return new DListDialog(model);
  }
}
