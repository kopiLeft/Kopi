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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.form.KopiAlignment;
import com.kopiright.vkopi.lib.form.UMultiBlock;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.form.ViewBlockAlignment;
import com.kopiright.vkopi.lib.ui.vaadin.form.DForm;
import com.kopiright.vkopi.lib.ui.vaadin.form.KopiLayout;
import com.kopiright.vkopi.lib.ui.vaadin.form.KopiMultiBlockLayout;
import com.kopiright.vkopi.lib.ui.vaadin.form.KopiSimpleBlockLayout;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

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
    
    for (int i= 0; i < getModel().getDisplaySize() + 1; i++) {
      chartLayout.addLayoutComponent(new Label(""), new KopiAlignment(0, i, 1, false));
    }
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  public void addToDetail(UComponent comp, KopiAlignment constraints) {
    detailLayout.addLayoutComponent((Component) comp, constraints);
  }
  
  @Override
  public void add(UComponent comp, KopiAlignment constraints) {
    chartLayout.addLayoutComponent((Component) comp, constraints);
  }
  
  @Override
  protected void createFields() {
    detailLayout = new KopiSimpleBlockLayout(2 * maxColumnPos, 
                                             maxRowPos, 
                                             (model.getAlignment() == null) ? 
                                             null : 
                                             new ViewBlockAlignment(getFormView(), model.getAlignment()));
    chartLayout = new KopiMultiBlockLayout(displayedFields + 1, getModel().getDisplaySize() +1);
    super.createFields();
  }
  
  @Override
  public boolean inDetailMode() {
    return getModel().isDetailMode();
  }
  
  @Override
  protected KopiLayout createContent() {
    return null;
  }
  
  @Override
  protected void addScrollBar(ScrollBar bar) {
    chartLayout.addLayoutComponent(bar, null);
  }
  
  @Override
  protected void layoutContainer() {
    chartLayout.layoutContainer();
    detailLayout.layoutContainer();
    setContent(chartLayout);
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
      setContent(chartLayout);
    } else {
      getModel().setDetailMode(true);
      setContent(detailLayout);
    }
    
    fireBlockViewSwitched();
  }
  
  /**
   * Registers a new {@link BlockViewSwitchListener} object.
   * @param listener The listener to be registered.
   */
  public void addBlockViewSwitchListener(BlockViewSwitchListener listener) {
    if (listeners == null) {
      listeners = new ArrayList<BlockViewSwitchListener>();
    }
    
    listeners.add(listener);
  }
  
  /**
   * Removes a new {@link BlockViewSwitchListener} object.
   * @param listener The listener to be removed.
   */
  public void removeBlockViewSwitchListener(BlockViewSwitchListener listener) {
    if (listeners == null) {
      return;
    }
    
    listeners.remove(listener);
  }
  
  /**
   * Notifies the registered listeners that block view has been switched.
   */
  protected void fireBlockViewSwitched() {
    for (BlockViewSwitchListener l : listeners) {
      l.onBlockViewSwitch(new BlockViewSwitchEvent((Component) this, getModel().isDetailMode()));
    }
  }
  
  //---------------------------------------------------
  // BLOCKVIEW LISTENER
  //---------------------------------------------------
  
  /**
   * Block view switch listener
   */
  public interface BlockViewSwitchListener extends Serializable {
    
    /**
     * The block view is changed.
     * @param event The switch event.
     */
    public void onBlockViewSwitch(BlockViewSwitchEvent event);
  }
  
  /**
   * Block switch Event.
   */
  public class BlockViewSwitchEvent extends Component.Event {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>BlockViewSwitchEvent</code> instance.
     * @param source The source component.
     * @param isDetailMode Is it the detail mode context ?
     */
    public BlockViewSwitchEvent(Component source, boolean isDetailMode) {
      super(source);
      this.isDetailMode = isDetailMode;
    }
    
    //---------------------------------------
    // ACCESSORS
    //---------------------------------------
    
    /**
     * Returns the detail mode context ability.
     * @return The detail mode context ability.
     */
    public boolean isDetailMode() {
      return isDetailMode;
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private boolean			isDetailMode;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private KopiLayout				chartLayout;
  private KopiLayout				detailLayout;
  private List<BlockViewSwitchListener>		listeners;	
}
