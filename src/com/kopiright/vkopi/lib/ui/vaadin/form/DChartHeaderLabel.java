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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import com.kopiright.vkopi.lib.form.UChartLabel;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;

/**
 * The <code>DChartHeaderLabel</code> is the vaadin implementation
 * of the {@link UChartLabel} specifications.
 */
@SuppressWarnings("serial")
public class DChartHeaderLabel extends DLabel implements UChartLabel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DChartHeaderLabel</code> instance.
   * @param text The label text.
   * @param help The label help.
   * @param index The field index.
   * @param model The sort model.
   * @param commands The fields command.
   */
  /*package*/ DChartHeaderLabel(String text,
                                String help,
                                int index,
                                VBlock.OrderModel model,
                                VCommand[] commands)
  {
    super(text, help, commands);
    fieldIndex = index;
    sortModel = model;
    sortModel.addSortingListener(this);
    addLayoutClickListener(new LayoutClickListener() {
      
      @Override
      public void layoutClick(LayoutClickEvent event) {
	sortModel.sortColumn(fieldIndex);
      }
    });	
  }
	
  //---------------------------------------------------
  // UCAHRTLABEL IMPLEMENTATION
  //---------------------------------------------------

  @Override
  public void orderChanged() {
    markAsDirtyRecursive();
  }

  @Override
  public void repaint() {
    markAsDirty();
  }

  //------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------
  
  /*package*/ int                 	fieldIndex;
  /*package*/ VBlock.OrderModel   	sortModel;
}
