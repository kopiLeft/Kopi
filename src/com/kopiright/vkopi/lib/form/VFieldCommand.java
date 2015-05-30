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

import java.io.Serializable;

import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.ActionHandler;
import com.kopiright.vkopi.lib.visual.KopiAction;

@SuppressWarnings("serial")
public class VFieldCommand extends VCommand implements ActionHandler, Serializable {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  public VFieldCommand(VForm form, int type) {
    super(0xFFFF, null, null, type, "Standard " + type);
    this.form = form;
  }

  /**
   * Returns the actor
   */
  public void setEnabled(boolean enabled) {
    if (actor == null) {
      handler = this;
      actor = form.getDefaultActor(getTrigger());
    }
    actor.setEnabled(enabled);
    actor.setNumber(trigger);
    actor.setHandler(handler);
    //    actor.setSynchronous(!isAsynchronous());
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   * @param	block		This action should block the UI thread ?
   * @deprecated                use method performAsyncAction
   */
  public void performAction(final KopiAction action, boolean block) {
    form.performAsyncAction(action);
  }
  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   */
  public void performAsyncAction(final KopiAction action) {
    form.performAsyncAction(action);
  }

  // NO USED ANYMORE
//   /**
//    * Get the execution mode
//    */
//   public boolean isAsynchronous() {
//     return true;
//   }

  /**
   * Performs a void trigger
   *
   * @param	VKT_Type	the number of the trigger
   */
  public void executeVoidTrigger(final int type) throws VException {
    switch (type) {
    case VForm.CMD_AUTOFILL:
      //      form.getActiveBlock().getActiveField().autofill(true, true);
      form.getActiveBlock().getActiveField().predefinedFill();
      break;
    case VForm.CMD_EDITITEM:
    case VForm.CMD_EDITITEM_S:
      //      form.getActiveBlock().getActiveField().getUI().loadItem(VForm.CMD_EDITITEM);
      form.getActiveBlock().getActiveField().loadItem(VForm.CMD_EDITITEM);
      break;
    case VForm.CMD_NEWITEM:
      //      form.getActiveBlock().getActiveField().getUI().loadItem(VForm.CMD_NEWITEM);
      form.getActiveBlock().getActiveField().loadItem(VForm.CMD_EDITITEM);
      break;
    }
  }

  /**
   *
   */
  public int getKey() {
    if (actor == null) {
      handler = this;
      actor = form.getDefaultActor(getTrigger());
    }

    return super.getKey();
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  public void helpOnCommand(com.kopiright.vkopi.lib.visual.VHelpGenerator help) {
    if (actor == null) {
      handler = this;
      actor = form.getDefaultActor(getTrigger());
    }
    if (actor == null) {
      return;
    }
    actor.helpOnCommand(help);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VForm						form;
}
