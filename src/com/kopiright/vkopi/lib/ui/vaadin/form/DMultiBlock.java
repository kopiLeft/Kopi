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

import org.kopi.vaadin.addons.BlockLayout;
import org.kopi.vaadin.addons.MultiBlockLayout;

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.form.KopiAlignment;
import com.kopiright.vkopi.lib.form.UMultiBlock;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.vaadin.ui.Component;

/**
 * The <code>DMultiBlock</code> is the vaadin implementation
 * of the {@link UMultiBlock} specification.
 */
@SuppressWarnings("serial")
public class DMultiBlock extends DChartBlock implements UMultiBlock {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /** 
   * Creates a new <code>DMultiBlock</code> instance.
   * @param parent The parent form.
   * @param model The block model.
   */
  public DMultiBlock(DForm parent, VBlock model) {
    super(parent, model);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  public void addToDetail(UComponent comp, KopiAlignment constraints) {
    ((MultiBlockLayout)getLayout()).addToDetail((Component)comp,
                                                constraints.x,
                                                constraints.y,
                                                constraints.width,
                                                constraints.alignRight,
                                                constraints.useAll);
  }
  
  @Override
  public boolean inDetailMode() {
    return getModel().isDetailMode();
  }
  
  @Override
  public BlockLayout createLayout() {
    MultiBlockLayout		layout;
    
    layout = new MultiBlockLayout(2 * maxColumnPos, // labels + fields
	                          maxRowPos,
	                          displayedFields,
	                          getModel().getDisplaySize() + 1);
    if (model.getAlignment() != null) {
     layout.setBlockAlignment((Component)formView.getBlockView(model.getAlignment().getBlock()),
                              model.getAlignment().getTartgets(),
                              model.getAlignment().isChart());
    }
    
    return layout;
  }

  //---------------------------------------------------
  // MULTIBLOCK IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void switchView(int row) throws VException {
    // if this block is not the current block
    //!!! graf 20080521: is this possible?
    if (!(getModel().getForm().getActiveBlock() == getModel())) {
      if (!getModel().isAccessible()) {
        return;
      }
      try {
        getModel().getForm().gotoBlock(getModel());
      } catch (Exception ex) {
        ((DForm)getFormView()).reportError(new VRuntimeException(ex.getMessage(), ex));
        return;
      }
    }
    if (row >= 0) {
      getModel().gotoRecord(getRecordFromDisplayLine(row));
    }
    if (inDetailMode()) {
      getModel().setDetailMode(false);
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          switchView(false);
        }
      });
    } else {
      getModel().setDetailMode(true);
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          switchView(true);
        }
      });
    }
  }
}
