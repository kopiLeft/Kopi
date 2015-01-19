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

import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;

/**
 * The <code>DChartBlock</code> is a {@link DBlock} representing
 * a chart block where data is represented in a data grid.
 */
@SuppressWarnings("serial")
public class DChartBlock extends DBlock {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DChartBlock</code> instance.
   * @param parent The parent form.
   * @param model The block model.
   */
  public DChartBlock(DForm parent, VBlock model) {
    super(parent, model);
    if (getModel().getDisplaySize() < getModel().getBufferSize()) {
      scrollBar = createScrollBar();
      addScrollBar(scrollBar);
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected KopiLayout createContent() {
    return new KopiMultiBlockLayout(displayedFields, getModel().getDisplaySize() + 1);
  }
  
  /**
   * Adds the scroll bar component.
   * @param bar The {@link ScrollBar} component.
   */
  protected void addScrollBar(ScrollBar bar) {
    ((KopiLayout)getContent()).addLayoutComponent(bar, null);
  }
  
  /**
   * Creates the {@link ScrollBar} object.
   * @return The {@link ScrollBar} object.
   */
  protected ScrollBar createScrollBar() {
    final ScrollBar	scrollBar;
    
    scrollBar = new ScrollBar(this);
    return scrollBar;
  }

  @Override
  public void validRecordNumberChanged() {
    if (getModel().getDisplaySize() < getModel().getBufferSize()) {
      if (scrollBar != null) {
	updateScrollbar();
      }
    }
  }
  
  @Override
  protected void refresh(boolean force) {
    super.refresh(force);
    if (scrollBar != null) {
      updateScrollbar();
    }
  }

  /**
   * Updates the scroll bar component.
   */
  private void updateScrollbar() {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        int         validRecords = getModel().getNumberOfValidRecord();
        int         dispSize     = getModel().getDisplaySize();

        if (validRecords > dispSize) {
          scrollBar.up.setEnabled(true);
          scrollBar.down.setEnabled(true);
        } else {
          scrollBar.up.setEnabled(false);
          scrollBar.down.setEnabled(false);
        }
      }
    });
  }
  
  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  
  private ScrollBar            		scrollBar;
}