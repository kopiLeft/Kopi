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

import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.PrinterManager;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField;

/**
 * A VAADIN key navigator.
 */
public class KeyNavigator extends ShortcutListener {
	
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>KeyNavigator</code> instance.
   * @param code The navigator code.
   * @param keyCode The key code.
   * @param modfiersKeys The key modifiers.
   */
  protected KeyNavigator(int code,
	                 int keyCode,
	                 int... modfiersKeys)
  {
    super("key-navigator" + code, keyCode, modfiersKeys);
    this.keyCode = code;
  }
	
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void handleAction(Object sender, Object target) {
    DField              fieldView = null;

    // !!! field view is extracted from target object ==> AbstractTextField ==> DTextField ==> DField
    if (target instanceof AbstractTextField) {
      try {
	fieldView = (DField) ((AbstractTextField)target).getParent();
      } catch (ClassCastException e) {
	return;
      }
    }
    if (fieldView == null) {
     return;
    }
    final VField	field = (VField) fieldView.getModel();

    if (field == null ||
        field.getForm() == null ||
        field.getForm().getActiveBlock() == null ||
	(field.getForm().getActiveBlock().getActiveField() != field
	&& keyCode != KEY_NEXT_BLOCK ))
    {
      return;
    } else {
      processKeyCode(fieldView, field, (AbstractTextField)target);
    }
  }

  /**
   * Process the given key code.
   * @param fieldView The field view.
   * @param field The field model.
   * @param sharedText The UI text component.
   */
  protected final void processKeyCode(final DField fieldView, final VField field, final AbstractTextField sharedText) {
    KopiAction			action;

    switch (keyCode) {
    case KEY_NEXT_FIELD:
      action = new KopiAction("keyKEY_TAB") {
	
	@Override
        public void execute() throws VException {
          field.getBlock().getForm().getActiveBlock().gotoNextField();
        }
      };
      break;
    case KEY_PREV_FIELD:
      action = new KopiAction("keyKEY_STAB") {
	
	@Override
        public void execute() throws VException {
          field.getBlock().getForm().getActiveBlock().gotoPrevField();
        }
      };
      break;
    case KEY_NEXT_BLOCK:
      action = new KopiAction("keyKEY_BLOCK") {
	
	@Override
        public void execute() throws VException {
	  field.getBlock().getForm().gotoNextBlock();
        }
      };
      break;
    case KEY_REC_UP:
      action = new KopiAction("keyKEY_REC_UP") {
	
	@Override
        public void execute() throws VException {
	  ((DBlock)fieldView.getBlockView()).gotoPrevRecord();
        }
      };
      break;
    case KEY_REC_DOWN:
      action = new KopiAction("keyKEY_REC_DOWN") {
	
	@Override
        public void execute() throws VException {
	  ((DBlock)fieldView.getBlockView()).gotoNextRecord();
	}
      };
      break;
    case KEY_REC_FIRST:
      action = new KopiAction("keyKEY_REC_FIRST") {
	
	@Override
	public void execute() throws VException {
	  field.getBlock().getForm().getActiveBlock().gotoFirstRecord();
	}
      };
      break;
    case KEY_REC_LAST:
      action = new KopiAction("keyKEY_REC_LAST") {
	
	@Override
	public void execute() throws VException {
	  field.getBlock().getForm().getActiveBlock().gotoLastRecord();
	}
      };
      break;
    case KEY_EMPTY_FIELD:
      action = new KopiAction("keyKEY_ALTENTER") {
	
	@Override
	public void execute() throws VException {
	  field.getBlock().getForm().getActiveBlock().gotoNextEmptyMustfill();
	}
      };
      break;
    case KEY_DIAMETER:
      action = new KopiAction("keyKEY_DIAMETER") {
	
	@Override
	public void execute() throws VException {
	  AbstractTextField	text = sharedText;
	  text.setValue(text.getValue() + "\u00D8");
	}
      };
      // execute it in the event-dispatching-thread!
      ((DForm)fieldView.getBlockView().getFormView()).performBasicAction(action);
      return;
    case KEY_ESCAPE: 
      action = new KopiAction("keyKEY_ESCAPE") {
	
	@Override
	public void execute() throws VException {
	  fieldView.getBlockView().getFormView().closeWindow();
	}
      };
      break;
    case KEY_PRINTFORM:
      action = new KopiAction("keyKEY_ALTENTER") {
	
	@Override
	public void execute() throws VException {
	  try {
	    PrintJob    job = fieldView.getBlockView().getFormView().printForm();
	    PrinterManager.getPrinterManager().getCurrentPrinter().print(job);
	  } catch (PrintException e) {
	    throw new VExecFailedException(e.getMessage());
	  } catch (IOException e) {
	    throw new VExecFailedException(e.getMessage());
	  }
	}
      };
      break;
    default:
      action = processSpecificKeyCode(fieldView, field);
    }
    if (action != null) {
      fieldView.getBlockView().getFormView().performAsyncAction(action);
    }
  }

  /**
   * Subclasses must override this method to process their specific
   * keys they are added by addSpecificNavigationKeys.
   * @param fieldView The field view.
   * @param field The field model.
   */
  protected KopiAction processSpecificKeyCode(final DField fieldView, final VField field) {
    KopiAction	action;
    switch (keyCode) {
    case KEY_PREV_VAL:
      action = new KopiAction("keyKEY_LIST_UP") {
	
	@Override
	public void execute() throws VException {
	  fieldView.getRowController().previousEntry();
	}
      };
      break;
    case KEY_NEXT_VAL:
      action = new KopiAction("keyKEY_LIST_DOWN") {
	
	@Override
	public void execute() throws VException {
	  fieldView.getRowController().nextEntry();
	}
      };
      break;
    default:
      action = null;
    }

    return action;
  }

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  protected final int		keyCode;

  public static final int	KEY_NEXT_FIELD          =  0;
  public static final int	KEY_PREV_FIELD		=  1;
  public static final int	KEY_REC_UP		=  2;
  public static final int	KEY_REC_DOWN		=  3;
  public static final int	KEY_REC_FIRST		=  4;
  public static final int	KEY_REC_LAST		=  5;
  public static final int	KEY_EMPTY_FIELD         =  6;
  public static final int	KEY_NEXT_BLOCK		=  7;
  public static final int	KEY_PREV_VAL		=  8;
  public static final int	KEY_NEXT_VAL		=  9;
  public static final int	KEY_DIAMETER		= 10;
  public static final int	KEY_ESCAPE		= 11;
  public static final int	KEY_PRINTFORM		= 12;

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -3277175963522587180L;
}
