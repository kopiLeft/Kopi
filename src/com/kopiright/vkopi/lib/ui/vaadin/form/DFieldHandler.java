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

import com.kopiright.vkopi.lib.form.AbstractFieldHandler;
import com.kopiright.vkopi.lib.form.VConstants;
import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.visual.VException;

/**
 * The <code>DFieldHandler</code> is the vaadin implementation of the
 * field handler specifications.
 */
@SuppressWarnings("serial")
public class DFieldHandler extends AbstractFieldHandler {
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  /**
   * Creates a new <code>DFieldHandler</code> instance.
   * @param rowController The row controller.
   */
  protected DFieldHandler(VFieldUI rowController) {
    super(rowController);
  }

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  
  @Override
  public boolean predefinedFill() throws VException {
    boolean     filled;

    filled = getModel().fillField(new VPredefinedValueHandler(getRowController(), getModel().getForm(), getModel()));
    if (filled) {
      getRowController().getBlock().gotoNextField();
    }
	    
    return filled;
  }

  @Override
  public void enter() {
    final DField      enterMe = (DField)getCurrentDisplay();

    if (enterMe != null) {
      getRowController().resetCommands();
      enterMe.enter(true);
    }
  }

  @Override
  public void leave() {
    final DField      leaveMe = (DField)getCurrentDisplay();

    if (leaveMe != null) {
      getRowController().resetCommands();
      leaveMe.leave();
    }
  }

  @Override
  public void labelChanged() {
    getRowController().resetLabel();
  }

  @Override
  public void searchOperatorChanged() {
    int               operator = getModel().getSearchOperator();
    final String      info = operator == VConstants.SOP_EQ ? null : VConstants.OPERATOR_NAMES[operator];

    if (getRowController().getLabel() != null) {
      ((DLabel)getRowController().getLabel()).setInfoText(info);
    }
    if (getRowController().getDetailLabel() != null) {
      ((DLabel)getRowController().getDetailLabel()).setInfoText(info);
    }	      
  }

  @Override
  public void valueChanged(int r) {
    final int         dispRow = getRowController().getBlockView().getDisplayLine(r);

    if (dispRow != -1) {
      if (getRowController().getDisplays() != null) {
        getRowController().getDisplays()[dispRow].updateText();
      }
      if (getRowController().getDetailDisplay() != null) {
        getRowController().getDetailDisplay().updateText();
      }
    }	     	    
  }

  @Override
  public void accessChanged(int row) {
    if (getRowController().getBlockView().getDisplayLine(row) != -1) {
      getRowController().fireAccessHasChanged(row);
    }
  }
}
