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

import org.kopi.vaadin.addons.ObjectFieldListener;

import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.VException;

/**
 * The <code>DObjectField</code> is the vaadin implementation
 * of the object field.
 */
@SuppressWarnings("serial")
public abstract class DObjectField extends DField implements ObjectFieldListener {

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
  }
  
  // --------------------------------------------------
  // UI MANAGEMENT
  // --------------------------------------------------

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
  
  //---------------------------------------------------
  // OBJECT FIELD LISTENER IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void gotoNextField() {
    performAction(new KopiAction("key: next field") {

      @Override
      public void execute() throws VException {
	getModel().getForm().getActiveBlock().gotoNextField();
      }
    });
  }

  @Override
  public void gotoPrevField() {
    performAction(new KopiAction("key: previous field") {

      @Override
      public void execute() throws VException {
	getModel().getForm().getActiveBlock().gotoPrevField();;
      }
    });
  }

  @Override
  public void gotoNextBlock() {
    performAction(new KopiAction("key: next block") {

      @Override
      public void execute() throws VException {
	getModel().getForm().gotoNextBlock();
      }
    });
  }

  @Override
  public void gotoPrevRecord() {
    performAction(new KopiAction("key: previous record") {

      @Override
      public void execute() throws VException {
	getModel().getForm().getActiveBlock().gotoPrevRecord();
      }
    });
  }

  @Override
  public void gotoNextRecord() {
    performAction(new KopiAction("key: next record") {

      @Override
      public void execute() throws VException {
	getModel().getForm().getActiveBlock().gotoNextRecord();
      }
    });
  }

  @Override
  public void gotoFirstRecord() {
    performAction(new KopiAction("key: first record") {

      @Override
      public void execute() throws VException {
	getModel().getForm().getActiveBlock().gotoFirstRecord();
      }
    });
  }

  @Override
  public void gotoLastRecord() {
    performAction(new KopiAction("key: last record") {

      @Override
      public void execute() throws VException {
	getModel().getForm().getActiveBlock().gotoLastRecord();
      }
    });
  }
  
  /**
   * Executes the given action in the event dispatch handler.
   * @param action The action to be executed.
   */
  protected void performAction(KopiAction action) {
    if (action != null && model != null) {
      getModel().getForm().performAsyncAction(action);
    }
  }
}
