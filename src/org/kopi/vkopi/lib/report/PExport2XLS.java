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
import java.util.Hashtable;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Workbook;
import org.kopi.vkopi.lib.report.UReport.UTable;

@SuppressWarnings("serial")
public class PExport2XLS extends PExport2Excel implements Constants {

  /**
   * Constructor
   */
  public PExport2XLS(UTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig, title);
  }

  protected Workbook createWorkbook() {
    HSSFWorkbook wb =  new HSSFWorkbook();
    colorindex = 10;
    colorpalete = new Hashtable<Color, HSSFColor>(); 
    palette = ((HSSFWorkbook)wb).getCustomPalette();
    
    return wb;
  }
  
  @Override
  protected org.apache.poi.ss.usermodel.Color createFillForegroundColor(Color color) {
    HSSFColor       rowCol = colorpalete.get(color);

    if (rowCol == null) {
      palette.setColorAtIndex(colorindex, (byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
      rowCol = palette.getColor(colorindex);
      colorindex++;
      colorpalete.put(color, rowCol);
    }
    
    return rowCol;
  }
  
  //-----------------------------------------------------------
  // DATA MEMBERS
  //-----------------------------------------------------------

  
  private HSSFPalette                           palette;
  private short                                 colorindex;
  private Hashtable<Color, HSSFColor>           colorpalete;

}
