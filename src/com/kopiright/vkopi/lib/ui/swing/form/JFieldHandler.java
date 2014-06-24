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

package com.kopiright.vkopi.lib.ui.swing.form;

import com.kopiright.vkopi.lib.form.AbstractFieldHandler;
import com.kopiright.vkopi.lib.form.VConstants;
import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.ui.swing.visual.SwingThreadHandler;
import com.kopiright.vkopi.lib.visual.VException;

/**
 * {@code JFieldHandler} is a swing implementation of the {@link AbstractFieldHandler}.
 */
public class JFieldHandler extends AbstractFieldHandler {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public JFieldHandler(VFieldUI rowController) {
    super(rowController);
  }

  // ----------------------------------------------------------------------
  // ABSTRACTFIELDHANDLER IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  /**
   * 
   */
  public boolean predefinedFill() throws VException {
    boolean     filled;

    filled = getModel().fillField(new JPredefinedValueHandler(getRowController(), getModel().getForm(), getModel()));
    if (filled) {
      getRowController().getBlock().gotoNextField();
    }
    
    return filled;
  }

  /**
   * 
   */
  public void enter() {
    // this is the correct thread to calculate the display of the
    // field NOT later in the event thread
    final DField      enterMe = (DField)getCurrentDisplay();

    if (enterMe != null) {
      SwingThreadHandler.start(new Runnable() {
	public void run() {
	  getRowController().resetCommands();
	  enterMe.enter(true);
	}
      });
    }
  }

  /**
   * 
   */
  public void leave() {
    // this is the correct thread to calculate the display of the
    // field NOT later in the event thread
    final DField      leaveMe = (DField)getCurrentDisplay();

    if (leaveMe != null) {
      SwingThreadHandler.start(new Runnable() {
	public void run() {
	  getRowController().resetCommands();
	  leaveMe.leave();
	}
      });
    }
  }

  /**
   * 
   */
  public void labelChanged() {
    SwingThreadHandler.startEnqueued(new Runnable() {
      public void run() {
	getRowController().resetLabel();
      }
    });
  }

  /**
   * 
   */
  public void searchOperatorChanged() {
    int               operator = getModel().getSearchOperator();
    final String      info = operator == VConstants.SOP_EQ ? null : VConstants.OPERATOR_NAMES[operator];

    SwingThreadHandler.startEnqueued(new Runnable() {
      public void run() {
	if (getRowController().getLabel() != null) {
	  ((DLabel)getRowController().getLabel()).setInfoText(info);
	}
	if (getRowController().getDetailLabel() != null) {
	  ((DLabel)getRowController().getDetailLabel()).setInfoText(info);
	}
      }
    });
  }

  /**
   * 
   */
  public void valueChanged(int r) {
    final int         dispRow = getRowController().getBlockView().getDisplayLine(r);

    if (dispRow != -1) {
      SwingThreadHandler.startEnqueued(new Runnable() {
	public void run() {
	  if (getRowController().getDisplays() != null) {
	    getRowController().getDisplays()[dispRow].updateText();
	  }
	  if (getRowController().getDetailDisplay() != null) {
	    getRowController().getDetailDisplay().updateText();
	  }
	}
      });
    }
  }

  /**
   * 
   */
  public void accessChanged(final int row) {
    if (getRowController().getBlockView().getDisplayLine(row) != -1) {
      SwingThreadHandler.startEnqueued(new Runnable() {
	public void run() {
	  getRowController().fireAccessHasChanged(row);
	}
      });
    }
  }
}
