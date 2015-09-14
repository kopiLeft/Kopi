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

package com.kopiright.vkopi.lib.cross;

import com.kopiright.vkopi.lib.chart.VChart;
import com.kopiright.vkopi.lib.chart.VNoRowException;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.form.VDictionaryForm;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBContextHandler;

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
    } catch (VNoRowException e) {
      chart.unsetWaitInfo();
      chart.error(MessageCode.getMessage("VIS-00057"));
    }
    b.setRecordChanged(0, false);
  }

  /**
   * Implements interface for COMMAND CreateChart
   */
  public void createChart(VBlock b) throws VException {
    VChartSelectionForm.createChart(createChart(), b);
  }

  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------

  /**
   * create a report for this form
   */
  protected abstract VChart createChart() throws VException;
}
