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

package com.kopiright.vkopi.lib.form;

import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.xkopi.lib.base.KopiUtils;

public abstract class AbstractFieldHandler implements FieldHandler {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  protected AbstractFieldHandler(VFieldUI rowController) {
    this.rowController = rowController;
  }

  // ----------------------------------------------------------------------
  // MODEL ACCESSOR
  // ----------------------------------------------------------------------
  
  /**
   * Returns the row controller field model.
   * @return The row controller field model.
   */
  public VField getModel() {
    return rowController.getModel();
  }
  
  //-----------------------------------------------------------------------
  // FIELDHANDLER IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  /**
   * 
   */
  public VFieldUI getRowController() {
    return rowController;
  }

  // ----------------------------------------------------------------------
  // FIELDLISTENER IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  /**
   * 
   */
  public void updateModel() throws VException {
    if (getModel().isChanged() && (getModel().hasFocus())) {
      getModel().checkType(getDisplayedValue(true));
    }
  }

  /**
   * 
   */
  public Object getDisplayedValue(boolean trim) throws VException {
    final UField 	field;

    field = getCurrentDisplay();
    if (field instanceof UTextField) {
      String	text = ((UTextField)field).getText();
      if (!trim){
	return text;
      } else if (getModel().getHeight() == 1) {
	return KopiUtils.trimString(text);
      } else {
	return KopiUtils.trailString(text);
      }
    } else {
      return field.getObject();
    }
  }

  /**
   * 
   */
  public UField getCurrentDisplay() {
    return rowController.getDisplay();
  }

  /**
   * 
   */
  public void fieldError(String message) {
    rowController.displayFieldError(message);
  }

  /**
   * 
   */
  public boolean requestFocus() throws VException {
    rowController.transferFocus(getCurrentDisplay());
    return true;
  }

  /**
   * 
   */
  public boolean loadItem(int mode) throws VException {
    int	id = -1;

    if (mode == VForm.CMD_NEWITEM) {
      id = ((VDictionaryForm)Module.getKopiExecutable(getModel().getList().getNewForm())).newRecord(getModel().getForm());
    } else if (mode == VForm.CMD_EDITITEM) {
      try {
	updateModel();
	if (!getModel().isNull(rowController.getBlock().getActiveRecord())) {
	  int	val = getModel().getListID();
	  if  (val != -1) {
	    id = ((VDictionaryForm)Module.getKopiExecutable(getModel().getList().getNewForm())).editWithID(getModel().getForm(), val);
	  } else {
	    mode = VForm.CMD_EDITITEM_S;
	  }
	} else {
	  mode = VForm.CMD_EDITITEM_S;
	}
      } catch (VException e) {
	mode = VForm.CMD_EDITITEM_S;
      }
    }
    if (mode == VForm.CMD_EDITITEM_S) {
      id = ((VDictionaryForm)Module.getKopiExecutable(getModel().getList().getNewForm())).openForQuery(getModel().getForm());
    }
    if (id == -1) {
      if (mode == VForm.CMD_EDITITEM || mode == VForm.CMD_EDITITEM_S) {
	getModel().setNull(rowController.getBlock().getActiveRecord());
      }
      throw new VExecFailedException();	// no message needed
    }
    getModel().setValueID(id);
    return true;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final VFieldUI				rowController;
}
