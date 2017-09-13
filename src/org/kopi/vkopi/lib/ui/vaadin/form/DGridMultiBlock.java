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
import org.kopi.vkopi.lib.form.UMultiBlock;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.SimpleBlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VRuntimeException;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.RowReference;

/**
 * A based Grid multi block implementation
 */
@SuppressWarnings("serial")
public class DGridMultiBlock extends DGridBlock implements UMultiBlock, DetailsGenerator {
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public DGridMultiBlock(DForm parent, VBlock model) {
    super(parent, model);
  }
  
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  public void switchView(int row) throws VException {
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
    } else if (getDisplayLine() >= 0) {
      getModel().gotoRecord(getRecordFromDisplayLine(getDisplayLine()));
    }
    getModel().setDetailMode(!inDetailMode());
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        if (grid.getEditedItemId() != null) {
          itemHasDetailVisible = new Integer((Integer)grid.getEditedItemId());
          grid.setDetailsVisible(grid.getEditedItemId(), inDetailMode());
          if (grid.isEditorActive()) {
            grid.cancelEditor();
          }
        }
      }
    });
  }
  
  @Override
  public int getRecordFromDisplayLine(int line) {
    if (inDetailMode() && itemHasDetailVisible != null ) {
      return itemHasDetailVisible;
    } else {
      return super.getRecordFromDisplayLine(line);
    }
  }

  @Override
  public void addToDetail(UComponent comp, KopiAlignment constraint) {
    if (detail == null) {
      detail = new SimpleBlockLayout(2 * maxColumnPos, maxRowPos);
      detail.addStyleName("grid-detail");
    }
    // block will not be marked in detail in client side
    // we force the field to be on the chart view as a hack
    // to allow free navigation
    if (comp instanceof DField) {
      ((DField)comp).setNoChart(false);
    }
    
    detail.addComponent((Component)comp,
                        constraint.x,
                        constraint.y,
                        constraint.width,
                        constraint.height,
                        constraint.alignRight,
                        constraint.useAll);
  }

  @Override
  public Component getDetails(RowReference rowReference) {
    return detail;
  }
  
  @Override
  public boolean inDetailMode() {
    return getModel().isDetailMode();
  }
  
  @Override
  public void blockViewModeLeaved(VBlock block, VField activeField) {
    // send active record to client side before view switch
    if (!inDetailMode()) {
      BackgroundThreadHandler.access(new Runnable() {

        @Override
        public void run() {
          fireActiveRecordChanged(getModel().getActiveRecord());
        }
      });
    }
    try {
      // take care that value of current field
      // is visible in the other mode
      // Not field.updateText(); because the field is
      // maybe not visible in the Detail Mode
      if (activeField != null) {
        activeField.leave(true);
      }
    } catch (VException ex) {
      ex.printStackTrace();
      getModel().getForm().error(ex.getMessage());
    }
  }
  
  @Override
  public void blockViewModeEntered(VBlock block, VField activeField) {
    if (inDetailMode()) {
      // update detail view access and texts
      updateDetailDisplay();
      try {
        // Show detail view

        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is
        // maybe not visible in the Detail Mode
        if (activeField == null) {
          //     getModel().gotoFirstField();
        } else {
          if (! activeField.noDetail()) {
            // field is visible in chartView
            activeField.enter();
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.setActiveField(activeField);
            getModel().gotoNextField();
          }
        }
      } catch (VException ex) {
        ex.printStackTrace();
        getModel().getForm().error(ex.getMessage());
      }
    } else {
      try {
        // show chart view

        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is
        // maybe not visible in the Detail Mode

        if (activeField == null) {
          // getModel().gotoFirstField();
        } else {
          if (!activeField.noChart()) {
            // field is visible in chartView
            activeField.enter();
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.setActiveField(activeField);
            getModel().gotoNextField();
          }
        }
      } catch (VException ex) {
        ex.printStackTrace();
        getModel().getForm().error(ex.getMessage());
      }
    }
  }
  
  @Override
  public void blockChanged() {
    super.blockChanged();
    if (itemHasDetailVisible != null && getModel().getActiveRecord() != itemHasDetailVisible) {
      enterRecord(getModel().getActiveRecord());
    }
  }
  
  @Override
  protected void enterRecord(final int recno) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (inDetailMode() && itemHasDetailVisible != null) {
          grid.setDetailsVisible(itemHasDetailVisible, false);
          getModel().setDetailMode(false);
          if (recno != itemHasDetailVisible) {
            itemHasDetailVisible = recno;
            getModel().setDetailMode(true);
            grid.setDetailsVisible(itemHasDetailVisible, true);
          }
        }
      }
    });
    super.enterRecord(recno);
  }
  
  @Override
  protected DetailsGenerator getDetailsGenerator() {
    return this;
  }
  
  /**
   * Updates the state of the detail display
   */
  protected void updateDetailDisplay() {
    for (VFieldUI columnView : columnViews) {
      if (columnView != null && columnView.getDetailDisplay() != null) {
        columnView.getDetailDisplay().updateAccess();
        columnView.getDetailDisplay().updateText();
        columnView.getDetailDisplay().updateColor();
      }
    }
  }
  
  private SimpleBlockLayout             detail;
  private Integer                       itemHasDetailVisible;
}
