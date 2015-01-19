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

import org.kopi.vaadin.fields.AbstractField;

import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.VException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Panel;

/**
 * The <code>DObjectField</code> is the vaadin implementation
 * of the object field.
 */
@SuppressWarnings("serial")
public abstract class DObjectField extends DField {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------

  /**
   * Creates a new <code>DObjectField</code> instance.
   * @param model The row controller.
   * @param label The field label.
   * @param align The field alignment.
   * @param options The field options.
   * @param detail Does the field belongs to the detail view ?
   */
  public DObjectField(VFieldUI model,
		      DLabel label,
		      int align,
		      int options,
	              boolean detail)
  {
    super(model, label, align, options, detail);
    empty = new Panel();
    empty.addStyleName("light");
    empty.setImmediate(true);
    empty.setSizeUndefined();
    addComponentAsFirst(empty);
    addNavigationKey((DForm)model.getModel().getForm().getDisplay());
  }
	
  //---------------------------------------------------
  // KEY EVENT
  //---------------------------------------------------

  /**
   * Add special navigation keys.
   * @param form The form view.
   */
  /*package*/ void addNavigationKey(DForm form) {
    addKey(form, KEY_TAB, KeyCode.ENTER, null);
    addKey(form, KEY_TAB, KeyCode.TAB, null);
    addKey(form, KEY_STAB, KeyCode.TAB, ModifierKey.SHIFT);
    addKey(form, KEY_BLOCK, KeyCode.ENTER, ModifierKey.SHIFT);
    addKey(form, KEY_REC_UP, KeyCode.PAGE_UP, null);
    addKey(form, KEY_REC_DOWN, KeyCode.PAGE_DOWN, null);
    addKey(form, KEY_REC_FIRST, KeyCode.HOME, null);
    addKey(form, KEY_REC_LAST, KeyCode.END, null);
    addKey(form, KEY_STAB, KeyCode.ARROW_LEFT, ModifierKey.CTRL);
    addKey(form, KEY_TAB, KeyCode.ARROW_RIGHT, ModifierKey.CTRL);
    addKey(form, KEY_REC_UP, KeyCode.ARROW_UP, ModifierKey.CTRL);
    addKey(form, KEY_REC_DOWN, KeyCode.ARROW_DOWN, ModifierKey.CTRL);
  }

  /**
   * Add a navigation key.
   * @param form The form view.
   * @param code The key code.
   * @param key The shortcut key.
   * @param mods The modifiers key.
   */
  private void addKey(DForm form, int code, int key, int... mods) {
    form.addShortcutListener(new Navigator(code, key, mods));
  }
  
  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  @Override
  public void enter(boolean refresh) {
    super.enter(refresh);
    //layout.focus();
    setBlink(true);
  }

  @Override
  public void leave() {
    setBlink(false);
    super.leave();
  }
	  
  //-----------------------------------------------
  // FOCUS
  //-----------------------------------------------

  /**
   * A navigator key for object field.
   */
  /*package*/ static class Navigator extends ShortcutListener {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------

    /**
     * Creates a new <code>Navigator</code> instance.
     * @param code The key code.
     * @param keyCode The shortcut key.
     * @param modifierKeys The modifiers key.
     */
    public Navigator(int code, int keyCode, int[] modifierKeys) {
      super("navigation-key" + code, keyCode, modifierKeys);
      this.keyCode = code;
    }

    //-------------------------------------
    // CONSTRUCTOR
    //-------------------------------------

    @Override
    public void handleAction(Object sender, Object target) {
      KopiAction	action = null;

      if (target == null) {
	return;
      }
      final VField	field = ((DField)((AbstractField)target).getParent()).getModel();
      if (field == null ||
	  field.getForm() == null ||
	  field.getForm().getActiveBlock() == null ||
	  (field.getForm().getActiveBlock().getActiveField() != field &&
	  keyCode != KEY_BLOCK))
      {
	return;
      }
      switch (keyCode) {
      case KEY_TAB:
	action = new KopiAction("key: next field") {

	  @Override
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoNextField();
	  }
	};
	break;
      case KEY_STAB:
	action = new KopiAction("key: previous field") {

	  @Override
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoPrevField();
	  }
	};
	break;
      case KEY_BLOCK:
	action = new KopiAction("key: next block") {

	  @Override
	  public void execute() throws VException {
	    field.getForm().gotoNextBlock();
	  }
	};
	break;
      case KEY_REC_UP:
	action = new KopiAction("key: previous record") {

	  @Override
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoPrevRecord();
	  }
	};
	break;
      case KEY_REC_DOWN:
	action = new KopiAction("key: next record") {

	  @Override
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoNextRecord();
	  }
	};
	break;
      case KEY_REC_FIRST:
	action = new KopiAction("key: first record") {

	  @Override
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoFirstRecord();
	  }
	};
	break;
      case KEY_REC_LAST:
	action = new KopiAction("key: last record") {

	  @Override
	  public void execute() throws VException {
	    field.getForm().getActiveBlock().gotoLastRecord();
	  }
	};
	break;
      }

      field.getForm().performAsyncAction(action);      
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------

    private final int			keyCode;
  }
	
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  protected Panel	        empty;
  
  private static final int	KEY_TAB			= 0;
  private static final int	KEY_STAB		= 1;
  private static final int	KEY_REC_UP		= 2;
  private static final int	KEY_REC_DOWN		= 3;
  private static final int	KEY_REC_FIRST		= 4;
  private static final int	KEY_REC_LAST		= 5;
  private static final int	KEY_BLOCK		= 6;
  private static final long 	serialVersionUID 	= 1L;
}
