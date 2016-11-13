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

import org.kopi.vkopi.lib.chart.VChart;
import org.kopi.vkopi.lib.chart.VNoChartRowException;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VDictionaryForm;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.DBContextHandler;

@SuppressWarnings("serial")
public abstract class VChartSelectionForm extends VDictionaryForm {

  //---------------------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------------------
  
  public VChartSelectionForm(DBContextHandler parent) throws VException {
    super(parent);
  }

  public VChartSelectionForm(DBContext parent) throws VException {
    super(parent);
  }

  public VChartSelectionForm() throws VException {
    super();
  }

  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------

  /**
   * static call to createReport.
   */
  public static void createChart(VChart chart, VBlock b) throws VException {
    b.validate();
    
    try {
      chart.setWaitInfo(Message.getMessage("chart_generation"));
      chart.setDBContext(chart.getDBContext());
      chart.doNotModal();
      chart.unsetWaitInfo();
    } catch (VNoChartRowException e) {
      chart.unsetWaitInfo();
      chart.error(MessageCode.getMessage("VIS-00057"));
    }
    b.setRecordChanged(0, false);
  }

  /**
   * Implements interface for COMMAND CreateChart
   */
  public void createChart(VBlock b) throws VException {
    b.validate();
    try {
      setWaitInfo(Message.getMessage("chart_generation"));
      VChart chart = createChart();
      chart.setDBContext(getDBContext());
      chart.doNotModal();
      unsetWaitInfo();
    } catch (VNoChartRowException e) {
      unsetWaitInfo();
      error(MessageCode.getMessage("VIS-00057"));
    }
    b.setRecordChanged(0, false);
  }

  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------

  /**
   * create a report for this form
   */
  protected abstract VChart createChart() throws VException;
}
