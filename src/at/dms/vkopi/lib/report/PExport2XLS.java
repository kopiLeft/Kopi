/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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
 * $Id: PGenExcelFile.java 22806 2005-04-05 16:49:10Z taoufik $
 */

package at.dms.vkopi.lib.report;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.GregorianCalendar;
import java.util.Calendar;

import javax.swing.JTable;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import at.dms.util.base.InconsistencyException;
import at.dms.xkopi.lib.type.Fixed;
import at.dms.xkopi.lib.type.Date;
import at.dms.xkopi.lib.type.Month;
import at.dms.xkopi.lib.type.Week;

import at.dms.xkopi.lib.type.NotNullDate;
import at.dms.xkopi.lib.type.NotNullTime;

public class  PExport2XLS extends PExport implements Constants {
  /**
   * Constructor
   */
  public PExport2XLS(JTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig, title);

    datatype = new int[getColumnCount()];
    dataformats = new short[getColumnCount()];
    widths = new short[getColumnCount()];
  }

  public void export(OutputStream out ) {
    rowNumber = 0;
    sheetIndex = 0;

    try {
      colorindex = 10;
      colorpalete = new Hashtable();
      

      wb = new HSSFWorkbook();

      palette = wb.getCustomPalette();
      format = wb.createDataFormat();

      formatColumns();
      exportData();

      wb.write(out);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  protected void startGroup(String subTitle) {
    if (subTitle == null) {
      subTitle = getTitle();
    }

    // Sheet name cannot be blank, greater than 31 chars,
    // or contain any of /\*?[]
    subTitle.replaceAll("/|\\\\|\\*|\\?|\\[|\\]", "");

    if (subTitle.length() > 31) {
      subTitle = subTitle.substring(0, 28) + "...";
    } else if (subTitle.length() == 0) {
      subTitle = " ";
    }
    rowNumber = 0;
    sheet = wb.createSheet(subTitle);
    for (short i = 0; i < getColumnCount(); i++) {
      sheet.setColumnWidth(i, widths[i]);
    }
    wb.setRepeatingRowsAndColumns(sheetIndex, 0, getColumnCount()-1, 0, 0);

    HSSFFooter footer = sheet.getFooter();
    HSSFHeader header = sheet.getHeader();

    header.setLeft(getTitle());
    
    footer.setLeft(getTitle() + " - Seite " + HSSFFooter.page() + " / " + HSSFFooter.numPages() );
    footer.setRight(NotNullDate.now().format("dd.MM.yyyy") + " "+ NotNullTime.now().format("HH:mm"));
    sheetIndex += 1;

    HSSFPrintSetup ps = sheet.getPrintSetup();
    
    sheet.setAutobreaks(true);
    ps.setFitWidth((short)1);  
    ps.setFitHeight((short)999);  
    ps.setLandscape(getPrintConfig().paperlayout.equals("Landscape"));
    ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); /// !!! no always A4
  }

  protected void exportHeader(String[] data) {
    HSSFRow titlerow = sheet.createRow((short)0);
    int       cellPos = 0;

    for (int i = 0; i < data.length; i++) {
      titlerow.createCell((short)cellPos++).setCellValue(data[i]);
    }
  }

  protected void exportRow(int level, String[] data,  Object[] orig, int[] alignments) {
    HSSFRow         row = sheet.createRow((short) (rowNumber+1));
    String[]	    strings = data;
    Color           color = getBackgroundForLevel(level);
    HSSFColor       rowCol = (HSSFColor) colorpalete.get(color);
    int             cellPos;    

    if (rowCol == null) {
      palette.setColorAtIndex( colorindex, (byte) color.getRed(), (byte) color.getGreen(),(byte) color.getBlue());
      rowCol = palette.getColor(colorindex);
      colorindex++;
      colorpalete.put(color, rowCol);
    }

    cellPos = 0;
    for (int j = 0; j < strings.length; j++) {
      HSSFCellStyle       cellStyle =  wb.createCellStyle();
      HSSFCell            cell =row.createCell((short)cellPos);

      if (strings[j] != null && orig[j] != null) {
        if (datatype[j] == HSSFCell.CELL_TYPE_STRING) {
          cell.setCellValue(strings[j].replace('\n', ' '));
        } else {
          if (orig[j] instanceof Fixed) {
            cell.setCellValue(((Fixed) orig[j]).doubleValue());
          } else if (orig[j] instanceof Integer) {
            cell.setCellValue(((Integer) orig[j]).doubleValue());
          } else if (orig[j] instanceof Boolean) {
            cell.setCellValue(((Boolean) orig[j]).booleanValue());
          } else if (orig[j] instanceof Date) {
            Date                date = (Date) orig[j];
            GregorianCalendar   cal = new GregorianCalendar();

            cal.set(Calendar.YEAR, date.getYear());
            cal.set(Calendar.MONTH, date.getMonth() - 1);
            cal.set(Calendar.DAY_OF_MONTH, date.getDay());

            cell.setCellValue(cal);
          } else if (orig[j] instanceof Month) {
            Date                date = ((Month) orig[j]).getFirstDay();
            GregorianCalendar   cal = new GregorianCalendar();

            cal.set(Calendar.YEAR, date.getYear());
            cal.set(Calendar.MONTH, date.getMonth() - 1);
            cal.set(Calendar.DAY_OF_MONTH, date.getDay());

            cell.setCellValue(cal);
          } else if (orig[j] instanceof Week) {
            Date                date = ((Week) orig[j]).getFirstDay();
            GregorianCalendar   cal = new GregorianCalendar();

            cal.set(Calendar.YEAR, date.getYear());
            cal.set(Calendar.MONTH, date.getMonth() - 1);
            cal.set(Calendar.DAY_OF_MONTH, date.getDay());

            cell.setCellValue(cal);
          } else {
            throw new InconsistencyException("Beinhaltet noch nicht unterstützten Typ:" + orig[j].getClass() +" von " + orig[j]);
          }
        }
        cell.setCellType(datatype[j]);
      } else {
        cell.setCellType(HSSFCell.CELL_TYPE_BLANK);        
      }

      cellStyle.setFillForegroundColor(rowCol.getIndex());
      cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
      
      cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
      cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
      cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
      cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
      cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
      cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
      cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
      cellStyle.setTopBorderColor(HSSFColor.BLACK.index);

      if (datatype[j] != HSSFCell.CELL_TYPE_STRING) {
        cellStyle.setDataFormat(dataformats[j]);
      }

      switch (alignments[j]) {
      case ALG_DEFAULT:
      case ALG_LEFT:
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        break;
      case ALG_CENTER:
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        break;
      case ALG_RIGHT:
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        break;
      default:
        throw new InconsistencyException("Unkown alignment");
      }
      cell.setCellStyle(cellStyle);
      cellPos++;
    }
    rowNumber += 1;
            System.out.println();
          
  }

  protected void formatStringColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = HSSFCell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatWeekColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = HSSFCell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatDateColumn(VReportColumn column, int index) {
    dataformats[index] = format.getFormat("dd.mm.yyyy");
    datatype[index] = HSSFCell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatMonthColumn(VReportColumn column, int index) {
    dataformats[index] = format.getFormat("mm.yyyy");
    datatype[index] = HSSFCell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatFixedColumn(VReportColumn column, int index) {
    dataformats[index] = format.getFormat("#.##0,00");
    datatype[index] = HSSFCell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatIntegerColumn(VReportColumn column, int index) {
    dataformats[index] = format.getFormat("0");;
    datatype[index] = HSSFCell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatBooleanColumn(VReportColumn column, int index) {
    dataformats[index] = 0; // General type
    datatype[index] = HSSFCell.CELL_TYPE_BOOLEAN;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatTimeColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = HSSFCell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * column.getWidth());
  }

  protected void formatTimestampColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = HSSFCell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * column.getWidth());
  }

  private HSSFPalette           palette;
  private short                 colorindex;
  private int                   rowNumber;
 
  private Hashtable             colorpalete;
  private HSSFWorkbook          wb;
  private HSSFSheet             sheet;
  private HSSFDataFormat        format;
  private int[]                 datatype;
  private short[]               dataformats;
  private short[]               widths;
  private int                   sheetIndex;
}
