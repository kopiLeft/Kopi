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

import org.kopi.vkopi.lib.form.VFieldUI;

/**
 * Specific field handling for grid block 
 */
@SuppressWarnings({"serial", "deprecation"})
public class DGridBlockFieldHandler extends DFieldHandler {
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public DGridBlockFieldHandler(VFieldUI rowController) {
    super(rowController);
  }
  
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  public void enter() {
    if (getBlockView().getModel().isChart() && getBlockView().inDetailMode()) {
      super.enter();
    } else {
      DGridEditorField<?>       editor;

      editor = (DGridEditorField<?>) getCurrentDisplay();
      if (getRowController().getBlock().getActiveRecord() != -1) {
        getBlockView().editRecord(getRowController().getBlock().getActiveRecord());
      }
      if (editor != null) {
        getRowController().resetCommands();
        editor.enter();
      }
    }
  }
  
  @Override
  public void leave() {
    if (getBlockView().getModel().isChart() && getBlockView().inDetailMode()) {
      super.leave();
    } else {
      DGridEditorField<?>       editor;

      editor = (DGridEditorField<?>) getCurrentDisplay();

      if (editor != null) {
        getRowController().resetCommands();
        editor.leave();
      }
    }
  }
  
  @Override
  public void searchOperatorChanged() {
    // not yet implemented for grid labels
    if (getBlockView().getModel().isChart() && getBlockView().inDetailMode()) {
      super.searchOperatorChanged();
    }
  }
  
  @Override
  public void valueChanged(int r) {
    if (getBlockView().getModel().isChart() && getBlockView().inDetailMode()) {
      super.valueChanged(r);
    } else {
      DGridEditorField<?>       editor;

      editor = (DGridEditorField<?>) getCurrentDisplay();
      if (editor != null && getBlockView().isEditorActive() && getBlockView().getEditedRecord() == r) {
        editor.updateText();
      }
      getBlockView().refreshRow(r);
    }
  }
  
  @Override
  public void colorChanged(int r) {
    if (getBlockView().getModel().isChart() && getBlockView().inDetailMode()) {
      super.colorChanged(r);
    } else {
      DGridEditorField<?>       editor;

      editor = (DGridEditorField<?>) getCurrentDisplay();
      if (editor != null && getBlockView().isEditorActive() && getBlockView().getEditedRecord() == r) {
        editor.updateColor();
      }
    }
  }
  
  @Override
  public void accessChanged(int row) {
    if (getBlockView().getModel().isChart() && getBlockView().inDetailMode()) {
      super.accessChanged(row);
    } else {
      getBlockView().updateColumnAccess(getModel(), row);
      if (getBlockView().isEditorActive() && getBlockView().getEditedRecord() == row) {
        getRowController().fireAccessHasChanged(row);
      }
    }
  }
  
  /**
   * Returns the grid block view attached with this field handler.
   * @return The grid block view attached with this field handler.
   */
  protected DGridBlock getBlockView() {
    return (DGridBlock) getRowController().getBlockView();
  }
}
