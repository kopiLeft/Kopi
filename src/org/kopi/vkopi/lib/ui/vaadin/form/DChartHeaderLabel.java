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

package org.kopi.vkopi.lib.ui.vaadin.form;

import org.kopi.vkopi.lib.form.UChartLabel;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.ui.vaadin.addons.SortableLabelListener;

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
                                VBlock.OrderModel model)
  {
    super(text, help);
    fieldIndex = index;
    sortModel = model;
    sortModel.addSortingListener(this);
    setSortable(true);
    addSortableLabelListener(new SortableLabelListener() {
      
      @Override
      public void onSort(int mode) {
	sortModel.sortColumn(fieldIndex);
      }
    });
  }
	
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  public void orderChanged() {
    // TODO correct sort icon in client side if needed
  }

  @Override
  public void repaint() {
    // nothing to do
  }
  
  @Override
  public boolean isEnabled() {
    return true;
  }

  //------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------
  
  /*package*/ int                 	fieldIndex;
  /*package*/ VBlock.OrderModel   	sortModel;
}
