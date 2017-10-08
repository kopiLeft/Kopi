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

package org.kopi.vkopi.lib.report;

import java.awt.Color;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kopi.vkopi.lib.report.UReport.UTable;

@SuppressWarnings("serial")
public class PExport2XLSX extends PExport2Excel implements Constants {

  /**
   * Constructor
   */
  public PExport2XLSX(UTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig, title);
  }

  protected Workbook createWorkbook() {
    return new SXSSFWorkbook(new XSSFWorkbook(), 10000, false);
  }
  
  @Override
  protected org.apache.poi.ss.usermodel.Color createFillForegroundColor(Color color) {
    return new XSSFColor(color);
  }
}
