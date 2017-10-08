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
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.report.UReport.UTable;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Fixed;
import org.kopi.xkopi.lib.type.Month;
import org.kopi.xkopi.lib.type.Time;
import org.kopi.xkopi.lib.type.Timestamp;
import org.kopi.xkopi.lib.type.Week;

@SuppressWarnings("serial")
public abstract class PExport2Excel extends PExport implements Constants {

  /**
   * Constructor
   */
  public PExport2Excel(UTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig, title);

    datatype = new int[getColumnCount()];
    dataformats = new short[getColumnCount()];
    widths = new short[getColumnCount()];
    cellStyleCacheManager = new CellStyleCacheManager();
  }

  public void export(OutputStream out ) {
    rowNumber = 0;
    sheetIndex = 0;
    
    try {
      wb = createWorkbook();
      format = wb.createDataFormat();
      formatColumns();
      exportData();
      wb.write(out);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (wb instanceof SXSSFWorkbook) {
        ((SXSSFWorkbook) wb).dispose();
      }
    }
  }

  protected void startGroup(String subTitle) {
    if (subTitle == null) {
      subTitle = getTitle();
    }

    // Sheet name cannot be blank, greater than 31 chars,
    // or contain any of /\*?[]
    subTitle = subTitle.replaceAll("/|\\\\|\\*|\\?|\\[|\\]", "");
    if (subTitle.length() > 31) {
      subTitle = subTitle.substring(0, 28) + "...";
    } else if (subTitle.length() == 0) {
      subTitle = " ";
    }
    rowNumber = 0;
    try {
      sheet = wb.createSheet(subTitle);
    } catch (IllegalArgumentException e) {
      sheet = wb.createSheet("" + subTitle.hashCode());
    }
    for (short i = 0; i < getColumnCount(); i++) {
      sheet.setColumnWidth(i, widths[i]);
    }
    wb.setRepeatingRowsAndColumns(sheetIndex, 0, getColumnCount()-1, 0, 0);

    Footer footer = sheet.getFooter();
    Header header = sheet.getHeader();

    header.setLeft(getTitle() + "  " + getColumnLabel(0) + " : " + subTitle);

    //!!!FIXME graf 20140622 - language specific
    footer.setLeft(getTitle() + " - " + VlibProperties.getString("print-page") + " &P / &N ");
    footer.setRight(Date.now().format("dd.MM.yyyy") + " "+ Time.now().format("HH:mm"));
    sheetIndex += 1;

    PrintSetup ps = sheet.getPrintSetup();

    sheet.setAutobreaks(true);
    ps.setFitWidth((short)1);
    ps.setFitHeight((short)999);
    ps.setLandscape(getPrintConfig().paperlayout.equals("Landscape"));
    ps.setPaperSize(PrintSetup.A4_PAPERSIZE); /// !!! no always A4
  }

  protected void exportHeader(String[] data) {
    Row     titlerow = sheet.createRow((short)0);
    int         cellPos = 0;

    for (int i = 0; i < data.length; i++) {
      titlerow.createCell((short)cellPos++).setCellValue(data[i]);
    }
  }

  protected void exportRow(int level, String[] data, Object[] orig, int[] alignments) {
    Row             row = sheet.createRow(rowNumber + 1);
    Color           color = getBackgroundForLevel(level);
    int             cellPos = 0;

    for (int j = 0; j < data.length; j++) {
      CellStyle         cellStyle;
      Cell              cell;

      cell = row.createCell((short)cellPos);
      cellStyle = cellStyleCacheManager.getStyle(this,
                                                 wb,
                                                 getAlignment(alignments, j),
                                                 getDataFormat(j),
                                                 color);
      setCellValue(cell, j, data[j], orig[j]);
      cell.setCellStyle(cellStyle);
      cellPos++;
    }
    rowNumber += 1;
  }
  
  protected void setCellValue(Cell cell, int cellPos, String data, Object orig) {
    if (data != null && orig != null) {
      if (datatype[cellPos] == Cell.CELL_TYPE_STRING) {
        cell.setCellValue(data.replace('\n', ' '));
      } else {
        if (orig instanceof Fixed) {
          cell.setCellValue(((Fixed) orig).doubleValue());
        } else if (orig instanceof Integer) {
          if (datatype[cellPos] == Cell.CELL_TYPE_BOOLEAN) {
            cell.setCellValue(((Integer) orig).doubleValue() == 1 ? true : false);
          } else {
            cell.setCellValue(((Integer) orig).doubleValue());
          }
        } else if (orig instanceof Boolean) {
          cell.setCellValue(((Boolean) orig).booleanValue());
        } else if (orig instanceof Date) {
          setCellValue(cell, (Date)orig);
        } else if (orig instanceof Timestamp
                    || orig instanceof java.sql.Timestamp) {
          // date columns can be returned as a timestamp by the jdbc driver.
          cell.setCellValue(data);
          datatype[cellPos] = Cell.CELL_TYPE_STRING;
        } else if (orig instanceof Month) {
          setCellValue(cell, ((Month)orig).getFirstDay());
        } else if (orig instanceof Week) {
          setCellValue(cell, ((Week) orig).getFirstDay());
        } else if (orig instanceof String && orig.equals("")) {
          // myabe reportIdenticalValue Trigger used
          // nothing
        } else {
          throw new InconsistencyException("Type not supported: datatype=" + datatype[cellPos]
                                           + "  " + " CellNumber= " + cellPos
                                           + " " + orig.getClass() + " of " + orig);
        }
      }
      cell.setCellType(datatype[cellPos]);
    } else {
      cell.setCellType(Cell.CELL_TYPE_BLANK);
    }
  }
  
  protected short getAlignment(int[] alignments, int cellPos) {
    switch (alignments[cellPos]) {
      case ALG_DEFAULT:
      case ALG_LEFT:
        return CellStyle.ALIGN_LEFT;
      case ALG_CENTER:
        return CellStyle.ALIGN_CENTER;
      case ALG_RIGHT:
        return CellStyle.ALIGN_RIGHT;
      default:
        throw new InconsistencyException("Unkown alignment");
    }
  }
  
  protected short getDataFormat(int cellPos) {
    if (datatype[cellPos] != Cell.CELL_TYPE_STRING) {
      return dataformats[cellPos];
    } else {
      return -1;
    }
  }
  
  /**
   * Set the value of the cell to the specified date value.
   */
  protected static void setCellValue(Cell cell, Date value) {
    GregorianCalendar   cal = new GregorianCalendar();

    cal.clear();
    cal.set(Calendar.YEAR, value.getYear());
    cal.set(Calendar.MONTH, value.getMonth() - 1);
    cal.set(Calendar.DAY_OF_MONTH, value.getDay());

    cell.setCellValue(cal);
  }

  private int computeColumnWidth(VReportColumn column) {
    return (column.getLabel().length() < column.getWidth()) ? column.getWidth() : column.getLabel().length() + 2;
  }

  protected void formatStringColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = Cell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatWeekColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = Cell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatDateColumn(VReportColumn column, int index) {
    dataformats[index] = format.getFormat("dd.mm.yyyy");
    datatype[index] = Cell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatMonthColumn(VReportColumn column, int index) {
    dataformats[index] = format.getFormat("mm.yyyy");
    datatype[index] = Cell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatFixedColumn(VReportColumn column, int index) {
    String fixnumFormat = "#,##0";

    for (int i= 0; i < ((VFixnumColumn)column).getMaxScale(); i ++) {
      fixnumFormat += (i == 0 ? ".0" : "0");
    }
    dataformats[index] = format.getFormat(fixnumFormat);
    datatype[index] = Cell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatIntegerColumn(VReportColumn column, int index) {
    dataformats[index] = format.getFormat("0");
    datatype[index] = Cell.CELL_TYPE_NUMERIC;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatBooleanColumn(VReportColumn column, int index) {
    dataformats[index] = 0; // General type
    datatype[index] = Cell.CELL_TYPE_BOOLEAN;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatTimeColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = Cell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected void formatTimestampColumn(VReportColumn column, int index) {
    dataformats[index] = 0;
    datatype[index] = Cell.CELL_TYPE_STRING;
    widths[index] = (short) (256 * computeColumnWidth(column));
  }

  protected abstract Workbook createWorkbook();
  
  protected abstract org.apache.poi.ss.usermodel.Color createFillForegroundColor(Color color);

  protected Workbook getWorkbook() {
    return wb;
  }
  
  //-----------------------------------------------------------
  // DATA MEMBERS
  //-----------------------------------------------------------

  private int                   		rowNumber;

  private Workbook                		wb;
  private Sheet                                 sheet;
  private DataFormat            		format;
  private int[]                 		datatype;
  private short[]               		dataformats;
  private short[]               		widths;
  private int                   		sheetIndex;
  // cell style cache
  private CellStyleCacheManager                 cellStyleCacheManager;
}
