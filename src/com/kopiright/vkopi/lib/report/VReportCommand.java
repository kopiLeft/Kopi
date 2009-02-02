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

package com.kopiright.vkopi.lib.report;

import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.ActionHandler;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.SActor;
import com.kopiright.vkopi.lib.visual.ApplicationConfiguration;
import com.kopiright.vkopi.lib.visual.PrinterManager;
import com.kopiright.vkopi.lib.print.PrintManager;
import com.kopiright.vkopi.lib.print.DefaultPrintManager;

public class VReportCommand extends VCommand implements ActionHandler {

  public VReportCommand(VReport report, SActor actor) {
    super(0xFFFF, null, actor, actor.getNumber(), actor.getActorIdent());
    this.report = report;
  }

  /**
   * Returns the actor
   */
  public void setEnabled(boolean enabled) {
    if (actor != null ) {
      actor.setEnabled(enabled);
      actor.setNumber(trigger);
      actor.setHandler(this);
    }
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
    try {
      executeVoidTrigger(getTrigger());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   */
  public void performAsyncAction(final KopiAction action) {
    try {
      executeVoidTrigger(getTrigger());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Performs a void trigger
   *
   * @param	VKT_Type	the number of the trigger
   */

  public void executeVoidTrigger(final int type) throws VException {
    switch (type) {
    case Constants.CMD_QUIT:
      report.close();
      break;
    case Constants.CMD_PRINT:
      PrintManager pm = DefaultPrintManager.getPrintManager();
      pm.print(report,
               report,
               1,
               PrinterManager.getPrinterManager().getCurrentPrinter(),
               null,
               null);
      break;
//  case Constants.CMD_PREVIEW:
//    break;
//  case Constants.CMD_PRINT_OPTIONS:
//    break;
    case Constants.CMD_EXPORT_CSV:
      report.export(VReport.TYP_CSV);
      break;
    case Constants.CMD_EXPORT_XLS:
      report.export(VReport.TYP_XLS);
      break;
    case Constants.CMD_EXPORT_PDF:
      report.export(VReport.TYP_PDF);
      break;
    case Constants.CMD_FOLD:
      report.foldSelection();
      break;
    case Constants.CMD_UNFOLD:
      report.unfoldSelection();
      break;
    case Constants.CMD_SORT:
      report.sortSelectedColumn();
      break;
    case Constants.CMD_FOLD_COLUMN:
      report.foldSelectedColumn();
      break;
    case Constants.CMD_UNFOLD_COLUMN:
      report.unfoldSelectedColumn();
      break;
//  case Constants.CMD_COLUMN_INFO:
//    break;
//  case Constants.CMD_OPEN_LINE:
//    break;
//  case Constants.CMD_REMOVE_CONFIGURATION:
//    break;
//  case Constants.CMD_LOAD_CONFIGURATION:
//    break;
    case Constants.CMD_HELP:
      report.showHelp();
      break;
    }
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  public void helpOnCommand(com.kopiright.vkopi.lib.visual.VHelpGenerator help) {
    
    if (actor == null) {
      return;
    }
    actor.helpOnCommand(help);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VReport     report;
}
