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

package org.kopi.vkopi.lib.cross;

import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VDictionaryForm;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.report.VNoRowException;
import org.kopi.vkopi.lib.report.VReport;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.DBContextHandler;

@SuppressWarnings("serial")
public abstract class VReportSelectionForm extends VDictionaryForm {
  
  protected VReportSelectionForm() throws VException {
  }

  protected VReportSelectionForm(VForm caller) throws VException {
    super(caller);
  }

  protected VReportSelectionForm(DBContextHandler caller) throws VException {
    super(caller);
  }

  protected VReportSelectionForm(DBContext caller) throws VException {
    super(caller);
  }

  /**
   * static call to createReport.
   */
  public static void createReport(VReport report, VBlock b) throws VException {
    b.validate();
    try {
      report.setWaitInfo(Message.getMessage("report_generation"));
      report.setDBContext(report.getDBContext());
      report.doNotModal();
      report.unsetWaitInfo();
    } catch (VNoRowException e) {
      report.unsetWaitInfo();
      report.error(MessageCode.getMessage("VIS-00057"));
    }
    b.setRecordChanged(0, false);
  }

  /**
   * Implements interface for COMMAND CreateReport
   */
  public void createReport(VBlock b) throws VException {
    b.validate();
    try {
      setWaitInfo(Message.getMessage("report_generation"));
      VReport report = createReport();
      report.setDBContext(getDBContext());
      report.doNotModal();
      unsetWaitInfo();
    } catch (VNoRowException e) {
      unsetWaitInfo();
      error(MessageCode.getMessage("VIS-00057"));
    }
    b.setRecordChanged(0, false);
  }

  /**
   * create a report for this form
   */
  protected abstract VReport createReport() throws VException;
}
