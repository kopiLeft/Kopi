/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: VReportSelectionForm.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.vkopi.lib.cross;

import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.report.VNoRowException;
import at.dms.vkopi.lib.report.VReport;
import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.form.VForm;
import at.dms.vkopi.lib.form.VBlock;
import at.dms.xkopi.lib.base.DBContext;
import at.dms.xkopi.lib.base.DBContextHandler;


public abstract class VReportSelectionForm extends VForm {

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
      error(Message.getMessage("report_no_data"));
    }

    b.setRecordChanged(0, false);
  }

  /**
   * create a report for this form
   */
  protected abstract VReport createReport() throws VException;
}
