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

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.form.KopiAlignment;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.ui.vaadin.addons.BlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.addons.BlockListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.ChartBlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;

import com.vaadin.ui.Component;

/**
 * The <code>DChartBlock</code> is a {@link DBlock} representing
 * a chart block where data is represented in a data grid.
 */
@SuppressWarnings("serial")
public class DChartBlock extends DBlock implements BlockListener {

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
      addBlockListener(this);
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void add(UComponent comp, KopiAlignment constraints) {
    addComponent((Component)comp,
                 constraints.x,
                 constraints.y,
                 constraints.width,
                 constraints.alignRight,
                 constraints.useAll);
  }
  
  @Override
  public BlockLayout createLayout() {
    ChartBlockLayout		layout;
    
    layout = new ChartBlockLayout(displayedFields, getModel().getDisplaySize() + 1);
    layout.setScrollable(getModel().getDisplaySize() < getModel().getBufferSize());
    return layout;
  }

  @Override
  public void validRecordNumberChanged() {
    if (getModel().getDisplaySize() < getModel().getBufferSize()) {
      updateScrollbar();
    }
  }
  
  @Override
  public void recordInfoChanged(int rec, int info) {
    // send the new records info
    fireRecordInfoChanged(rec, info);
    getModel().updateColor(rec);
  }
  
  @Override
  protected void refresh(boolean force) {
    super.refresh(force);
    // don't update scroll bar when the scroll position
    // is being changing from the client side.
    if (!scrolling) {
      updateScrollbar();
    }
  }

  @Override
  public void onScroll(final int value) {
    if (((DForm)getFormView()).getInAction()) {
      // do not change the rows if there is currently a
      // another command executed
      return;
    }
    if (!init) {
      init = true; // on initialization, we do not scroll.
    } else {
      try {
        scrolling = true;
	setScrollPos(value);
	scrolling = false;
      } catch (VException e) {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void onActiveRecordChange(final int record, final int sortedTopRec) {
    if (record != getModel().getActiveRecord()) {
      getModel().getForm().performAsyncAction(new KopiAction() {

        @Override
        public void execute() throws VException {
          if (fireTriggers()) {
            sortedToprec = sortedTopRec;
            getModel().gotoRecord(record);
          } else {
            getModel().setActiveRecord(record);
            getModel().setCurrentRecord(record);
            sortedToprec = sortedTopRec;
            refresh(false);
          }
        }

        /**
         * Returns {@code true} if there some triggers that should be fired
         * when changing the active record of the block model.
         * @return {@code true} if there some triggers that should be fired.
         * @throws VException Visual errors.
         */
        private boolean fireTriggers() throws VException {
          return isActiveBlock() && (fireTriggerOnBlock() || fireTriggersOnActiveField());
        }

        /**
         * Returns {@code true} if this block is the active block in the form.
         * @return {@code true} if this block is the active block in the form.
         * @throws VException Visual errors.
         */
        private boolean isActiveBlock() throws VException {
          return getModel().getForm().getActiveBlock() != null
            && getModel().getForm().getActiveBlock() == getModel();
        }
        
        /**
         * Returns {@code true} is there are trigger that should be fired on this block.
         * @return {@code true} is there are trigger that should be fired on this block.
         * @throws VException Visual errors.
         */
        private boolean fireTriggerOnBlock() throws VException {
          return getModel().hasTrigger(VConstants.TRG_PREREC)
           || getModel().hasTrigger(VConstants.TRG_VALREC);
        }

        /**
         * Returns {@code true} is there are trigger that should be fired on the active field.
         * @return {@code true} is there are trigger that should be fired on the active field.
         * @throws VException Visual errors.
         */
        private boolean fireTriggersOnActiveField() throws VException {
          if (getModel().getActiveField() != null) {
            return getModel().getActiveField().hasTrigger(VConstants.TRG_POSTCHG)
             || getModel().getActiveField().hasTrigger(VConstants.TRG_VALFLD)
             || getModel().getActiveField().hasTrigger(VConstants.TRG_FORMAT)
             || getModel().getActiveField().hasTrigger(VConstants.TRG_PREVAL)
             || getModel().getActiveField().hasTrigger(VConstants.TRG_POSTFLD);
          } else {
            return false;
          }
        }
      });
    }
  }

  /**
   * Updates the scroll bar position.
   */
  private void updateScrollbar() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	int		validRecords = getModel().getNumberOfValidRecord();
	int		dispSize = getModel().getDisplaySize();
	
	updateScroll(dispSize,
	             validRecords,
	             validRecords > dispSize,
	             getModel().getNumberOfValidRecordBefore(getRecordFromDisplayLine(0)));
      }
    });
  }
  
  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  
  private boolean			init = false;
  /*
   * This flag was added to avoid mutual communication
   * between client and server side when the scroll bar
   * position is changed from the client side.
   * Thus, scroll bar position is not changed by server
   * side when it is changed from the client side
   * @see {@link #refresh(boolean) 
   */
  private boolean                       scrolling = false;
}