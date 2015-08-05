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

import java.io.IOException;

import org.kopi.vaadin.addons.TextField;
import org.kopi.vaadin.addons.TextFieldListener;
import org.kopi.vaadin.addons.client.suggestion.AutocompleteSuggestion;

import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.PrinterManager;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.VWindow;

/**
 * Text field key navigation.
 */
@SuppressWarnings("serial")
public class KeyNavigator implements TextFieldListener {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  public KeyNavigator(VField model, TextField box) {
    this.model = model;
    this.box = box;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void gotoNextField() {
    performAction(new KopiAction("keyKEY_TAB") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().getForm().getActiveBlock().gotoNextField();
	}
      }
    });
  }

  @Override
  public void gotoPrevField() {
    performAction(new KopiAction("keyKEY_STAB") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().getForm().getActiveBlock().gotoPrevField();
	}
      }
    });
  }

  @Override
  public void gotoNextBlock() {
    performAction(new KopiAction("keyKEY_BLOCK") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().getForm().gotoNextBlock();
	}
      }
    });
  }

  @Override
  public void gotoPrevRecord() {
    performAction(new KopiAction("keyKEY_REC_UP") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().gotoPrevRecord();
	}
      }
    });
  }

  @Override
  public void gotoNextRecord() {
    performAction(new KopiAction("keyKEY_REC_DOWN") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().gotoNextRecord();
	}
      }
    });
  }

  @Override
  public void gotoFirstRecord() {
    performAction(new KopiAction("keyKEY_REC_FIRST") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().getForm().getActiveBlock().gotoFirstRecord();
	}
      }
    });
  }

  @Override
  public void gotoLastRecord() {
    performAction(new KopiAction("keyKEY_REC_LAST") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().getForm().getActiveBlock().gotoLastRecord();
	}
      }
    });
  }

  @Override
  public void gotoNextEmptyMustfill() {
    performAction(new KopiAction("keyKEY_ALTENTER") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().getForm().getActiveBlock().gotoNextEmptyMustfill();
	}
      }
    });
  }

  @Override
  public void closeWindow() {
    performAction(new KopiAction("keyKEY_ESCAPE") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  model.getBlock().getForm().close(VWindow.CDE_QUIT);
	}
      }
    });
  }

  @Override
  public void printForm() {
    performAction(new KopiAction("keyKEY_ALTENTER") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  try {
	    PrintJob    job = model.getDisplay().getBlockView().getFormView().printForm();

	    PrinterManager.getPrinterManager().getCurrentPrinter().print(job);
	  } catch (PrintException e) {
	    throw new VExecFailedException(e.getMessage());
	  } catch (IOException e) {
	    throw new VExecFailedException(e.getMessage());
	  }
	}
      }
    });
  }

  @Override
  public void previousEntry() {
    performAction(new KopiAction("keyKEY_LIST_UP") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  ((DField)model.getDisplay()).getRowController().previousEntry();
	}
      }
    });
  }

  @Override
  public void nextEntry() {
    performAction(new KopiAction("keyKEY_LIST_UP") {

      @Override
      public void execute() throws VException {
	if (model != null) {
	  ((DField)model.getDisplay()).getRowController().nextEntry();
	}
      }
    });
  }

  @Override
  public void onQuery(final String query) {
    try {
      final String[][]		suggestions;
      
      suggestions = model.getSuggestions(query);
      if (box != null && suggestions != null) {
	BackgroundThreadHandler.access(new Runnable() {
	  
	  @Override
	  public void run() {
	    box.setSuggestions(suggestions, query);
	  }
	});
      }
    } catch (VException e) {
      // ignore errors on auto completion process
      // just print stack trace for debugging
      e.printStackTrace();
    }
  }

  @Override
  public void onSuggestion(AutocompleteSuggestion suggestion) {
    // not working cause objects passed in the the shared state are not properly
    // serialized. We will set the field value at the client side and fire a go to next field
    // here to convert the field displayed value to model value.
    // model.setObject(model.getBlock().getActiveRecord(), suggestion.getValue());
  }
  
  /**
   * Executes the given action in the event dispatch handler.
   * @param action The action to be executed.
   */
  protected void performAction(KopiAction action) {
    if (action != null && model != null) {
      model.getForm().performAsyncAction(action);
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VField 				model;
  private final TextField 			box;
}
