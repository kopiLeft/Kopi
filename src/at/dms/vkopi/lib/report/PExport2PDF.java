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
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JTable;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;


import at.dms.util.base.InconsistencyException;
import at.dms.xkopi.lib.type.NotNullDate;
import at.dms.xkopi.lib.type.NotNullTime;
import at.dms.vkopi.lib.util.PPaperType;
import at.dms.vkopi.lib.util.Utils;
import at.dms.vkopi.lib.util.PrintJob;

public class  PExport2PDF extends PExport implements Constants {
  /**
   * Constructor
   */
  public PExport2PDF(JTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig, title);

    widths = new float[getColumnCount()];
  }

  public PrintJob export() {
    try {  
      PrintJob        printJob;
      Rectangle       page;
      File            file = Utils.getTempFile("kopi", "prt");

      export(file);

      page = document.getPageSize();
      printJob = new PrintJob(file, true);
      printJob.setDataType(PrintJob.DAT_PDF);
      printJob.setPrintInformation(getTitle(),
                                   false,
				   (int) page.width(),
				   (int) page.height(),
				   pages);
      return printJob;
    } catch (Exception e) {
	throw new InconsistencyException(e);
    } 
  }

  public void export(OutputStream out ) {
    try {
      PPaperType	paper = PPaperType.getPaperTypeFromCode(pconfig.papertype);
      Rectangle         paperSize;

      if (pconfig.paperlayout.equals("Landscape")) {
        paperSize = new Rectangle(paper.getHeight(), paper.getWidth());
      } else {
        paperSize = new Rectangle(paper.getWidth(), paper.getHeight());
      }

      firstPage = true;

      PdfPTable       head = createHeader(); 
      PdfPTable       foot = createFooter(0, 0);

      document = new Document(paperSize, 
                              pconfig.leftmargin, 
                              pconfig.rightmargin, 
                              pconfig.topmargin + head.getTotalHeight() + pconfig.headermargin, 
                              pconfig.bottommargin + foot.getTotalHeight()+ pconfig.footermargin+2); 
                              // 2 to be sure to print the border

      scale = (float) getScale(3, 12, 0.1);

      File      tempFile = Utils.getTempFile("kopiexport", "pdf");
      PdfWriter writer = PdfWriter.getInstance(document,
                                               new FileOutputStream(tempFile));

      writer.setPageEvent(new PdfPageEventHelper() {
          public void onEndPage(PdfWriter writer, Document document) {
            try {
              Paragraph       p;
              PdfPCell        cell;
              Rectangle       page = document.getPageSize();
              PdfPTable       head = createHeader(); 

              head.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
              head.writeSelectedRows(0, -1, 
                                     document.leftMargin(), 
                                     page.height() - document.topMargin() + head.getTotalHeight() + getPrintConfig().headermargin,
                                     writer.getDirectContent());
            } catch (Exception e) {
              throw new ExceptionConverter(e);
            }
          }
        });

      document.open();
      
      exportData();
      document.add(datatable);
 
      document.close();

      addFooter(tempFile, out);

    } catch (Exception e) {
      throw new InconsistencyException(e);
    } 
  }

  private void addFooter(File tempfile, OutputStream out) {
      // write footer;
    try {
      PdfReader           reader = new PdfReader(new FileInputStream(tempfile));
      PdfStamper          stamper = new PdfStamper(reader, out);
      Rectangle           page = document.getPageSize();

      pages = reader.getNumberOfPages();
      for (int i = 1; i <= pages; i++) {
        PdfPTable       foot = createFooter(i, pages);
        PdfContentByte  cb = stamper.getOverContent(i);

        foot.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
        foot.writeSelectedRows(0, -1, document.leftMargin(), getPrintConfig().bottommargin+foot.getTotalHeight(), cb);
      }
      stamper.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }


  private PdfPTable createHeader() {
    PdfPTable       head = new PdfPTable(1);
              
    head.addCell(createCell((currentSubtitle == null) ? getTitle() : getTitle() + "  "+ getColumnLabel(0) +" : " + currentSubtitle, 
                            14, Color.black, Color.white, ALG_LEFT, false));
    return head;
  }

  private PdfPTable createFooter(int page, int allpages) {
    PdfPTable       foot = new PdfPTable(2);
                
    foot.addCell(createCell(getTitle() + " - Seite " + page +"/"+allpages, 7, Color.black, Color.white, ALG_LEFT, false));
    foot.addCell(createCell(NotNullDate.now().format("dd.MM.yyyy") + " "+ NotNullTime.now().format("HH:mm"), 
                            7, Color.black, Color.white, ALG_RIGHT, false));
    return foot;
  }

  protected void startGroup(String subTitle) {
    try {
      if (datatable != null) {
        document.add(datatable);
      }
      currentSubtitle = subTitle;

      datatable = new PdfPTable(getColumnCount());

      datatable.setWidthPercentage(100);
      datatable.getDefaultCell().setBorderWidth(1);
      datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
      if (!firstPage) {
        document.newPage();
      } else {
        for (int i = 0; i < widths.length; i++) {
          widths[i] += datatable.getDefaultCell().getEffectivePaddingLeft()  + datatable.getDefaultCell().getEffectivePaddingRight() ;
        }
      }
      datatable.setWidths(widths);
      firstPage = false;
    } catch (Exception e) {
      throw new InconsistencyException(e);
    } 
  }

  protected void exportHeader(String[] data) {
    for (int i = 0; i < data.length; i++) {
      datatable.addCell(createCell(data[i], scale, Color.white, Color.black, ALG_CENTER, true));
    }
    datatable.setHeaderRows(1); 
  }

  protected void exportRow(int level, String[] strings, Object[] orig, int[] alignments) {
    datatable.getDefaultCell().setBorderWidth(1);
    datatable.getDefaultCell().setBackgroundColor(Color.white);

    int       cell = 0;

    for (int j = 0; j < strings.length; j++) {
      if (strings[j] != null) {
        datatable.addCell(createCell(strings[j], scale, Color.black, getBackgroundForLevel(level), alignments[j], true));
      } else {
        datatable.addCell(createCell(" ", scale,  Color.black, getBackgroundForLevel(level), alignments[j], true));
      }
      cell += 1;
    }
  }

  private PdfPCell createCell(String text, double size, Color textColor, Color background, int alignment, boolean border) {
    PdfPCell    cell;
    Font        font = FontFactory.getFont(FontFactory.HELVETICA, (float) size, 0 , textColor);

    cell = new PdfPCell(new Paragraph(new Chunk(text, font)));

    cell.setVerticalAlignment(Element.ALIGN_TOP);
    switch (alignment) {
    case ALG_DEFAULT:
    case ALG_LEFT:
      cell.setHorizontalAlignment(Element.ALIGN_LEFT);
      break;
    case ALG_CENTER:
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      break;
    case ALG_RIGHT:
      cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
      break;
    default:
      throw new InconsistencyException("Unkown alignment");
    }
    cell.setBackgroundColor(background);
    if (!border) {
      cell.setBorder(0);
    }
    return cell;
  }

  /**
   * Gets the scale to be used for this report
   */
  private double getScale(double min, double max, double precision) {
    int         innerSpace =1;
    int         width, height;
    float       point;

    // setting format parameters
    PPaperType	paper = PPaperType.getPaperTypeFromCode(pconfig.papertype);

    if (pconfig.paperlayout.equals("Landscape")) {
      width = paper.getHeight();
      height = paper.getWidth();
    } else {
      width = paper.getWidth();
      height = paper.getHeight();
    }

    double      widthSumMin;
    double      widthSumMax;
 
    widthSum = 0;
    scale = max;
    formatColumns();
    widthSumMax = widthSum + getColumnCount()*2*2 + getColumnCount() *1;

    if (widthSumMax
        <= (width - document.leftMargin() - document.rightMargin() - 2 * pconfig.border)) {
      return max;
    }
    if (max - min <= precision) {
      return min;
    }

    widthSum = 0;
    scale = min;
    formatColumns();
    widthSumMin = widthSum + getColumnCount()*2*3 + getColumnCount() *1;

    widthSum = 0;
    scale =(float) (min+(max-min)/2);
    formatColumns();
    widthSum = widthSum + getColumnCount()*2*3 + getColumnCount() *1;

    if (widthSumMin <= (width - document.leftMargin() - document.rightMargin() - 2 * pconfig.border) 
        && widthSum  >= (width - document.leftMargin() - document.rightMargin() - 2 * pconfig.border)) {
      return getScale(min, min + (max - min)/2, precision);
    } else {
      return getScale(max - (max - min)/2, max, precision);
    }
  }


  protected void formatStringColumn(VReportColumn column, int index) {
      // maximum of length of titel AND width of column 
    widths[index] = Math.max(new Chunk(column.getLabel(), FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint(),
			     new Chunk("m", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint() * column.getWidth());
    widthSum += widths[index];
  }

  protected void formatWeekColumn(VReportColumn column, int index) {
    widths[index] = new Chunk("00.0000", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint();
    widthSum += widths[index];
  }
  
  protected void formatDateColumn(VReportColumn column, int index) {
    widths[index] = new Chunk("00.00.0000", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint();
    widthSum += widths[index];
  }

  protected void formatMonthColumn(VReportColumn column, int index) {
    widths[index] = 4 + new Chunk("00.0000", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint();
    widthSum += widths[index];
  }

  protected void formatFixedColumn(VReportColumn column, int index) {
    widths[index] = new Chunk("0", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint() * column.getWidth();
    widthSum += widths[index];
  }

  protected void formatIntegerColumn(VReportColumn column, int index) {
    widths[index] = new Chunk("0", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint() * column.getWidth();
    widthSum += widths[index];
  }

  protected void formatBooleanColumn(VReportColumn column, int index) {
    widths[index] = new Chunk("false", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint();
    widthSum += widths[index];
  }

  protected void formatTimeColumn(VReportColumn column, int index) {
    widths[index] = new Chunk("00:00", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint();
    widthSum += widths[index];
  }

  protected void formatTimestampColumn(VReportColumn column, int index) {
    widths[index] = new Chunk("00.00.0000 00:00.0000", FontFactory.getFont(FontFactory.HELVETICA, (float) scale)).getWidthPoint();
    widthSum += widths[index];
  }

  private String                title;
  private PdfPTable             datatable;  
  private int[]			columnsAlign;
  private int                   pages;
  private Document              document;
  private String                currentSubtitle;
  private boolean               firstPage;

  private double                scale;
  private double                widthSum;
  private float                 widths[];
}
