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
 * $Id$
 */

package at.dms.vkopi.lib.report;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JTable;
import at.dms.vkopi.lib.util.PPaperType;
import at.dms.vkopi.lib.util.Printer;
import at.dms.vkopi.lib.util.PrintJob;
import at.dms.vkopi.lib.util.PrintException;

public class PGenPS extends RDataExtractor {

  /**
   * Constructor
   */
  public PGenPS(JTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig);

    page_number = 1;
    parameters = new DParameters(Color.gray);

    // setting format parameters
    PPaperType	paper = PPaperType.getPaperTypeFromCode(pconfig.papertype);
    if (pconfig.paperlayout.equals("Landscape")) {
      width = paper.getHeight();
      height = paper.getWidth();
    } else {
      width = paper.getWidth();
      height = paper.getHeight();
    }

    top = pconfig.topmargin;
    bottom = pconfig.bottommargin;
    left = pconfig.leftmargin;
    right = pconfig.rightmargin;
    header = pconfig.headermargin;
    footer = pconfig.footermargin;

    grid_H = pconfig.grid_H;
    grid_V = pconfig.grid_V;
    borderSize = pconfig.border;
    innerSpace = 1;

    groupFormfeed = pconfig.groupFormfeed;

    this.title = title;

    // get the number of column
    int columnCount = model.getAccessibleColumnCount();
    int newColumnCount = 0;
    for (int i = 0; i < columnCount; i++) {
      if (getVisibleColumn(i).isVisible() && !getVisibleColumn(i).isFolded()) {
	newColumnCount += 1;
      }
    }
    columnCount = newColumnCount;

    // if we cut it by level, we want to print a column less (we don't want the first column)
    if (groupFormfeed) {
      columnCount --;
    }

    int index		= 0;
    double widthSum	= 0;

    columnsWidth	= new double[columnCount];
    columnsLabel	= new String[columnCount];
    columnsAlign	= new int[columnCount];
    rowHeight		= 0;

    int j = 0;
    // if we cut it by level, we don't want to print the first column
    if (groupFormfeed) {
      j = 1;
      columnDeleted = getVisibleColumn(0).getLabel();
    }
    while (j < model.getAccessibleColumnCount()) {
      if (!getVisibleColumn(j).isFolded()) {
	// get the right column width
	if (getVisibleColumn(j).getLabel().length() > getVisibleColumn(j).getWidth()) {
	  columnsWidth[index] = getVisibleColumn(j).getLabel().length();
	} else {
	  columnsWidth[index] = getVisibleColumn(j).getPrintedWidth();
	}
	widthSum += columnsWidth[index];

	columnsLabel[index] = getVisibleColumn(j).getLabel();
	columnsAlign[index] = getVisibleColumn(j).getAlign();
	index += 1;
	if (rowHeight < getVisibleColumn(j).getHeight()) {
	  rowHeight = getVisibleColumn(j).getHeight();
	}
      }
      j++;
    }

    // scale calcul
    scale = getScale(widthSum, 0, 11, 0.1);

    for (int i = 0; i < columnsWidth.length; i++) {
      columnsWidth[i] = columnsWidth[i] * scale * 0.7 + innerSpace * 2;
    }

    rowHeight = rowHeight * scale + 2 * innerSpace;
    headerHeight = rowHeight;

    // select data to be displayed
    selectDisplayData();
  }

  public PrintJob printInto() throws PrintException {
    try {
      Vector                    pages = addData();
      PrintJob                  printJob = new PrintJob();

      writer = new BufferedWriter(new PrintWriter(new PrintStream(printJob.getOutputStream(), true, "ISO-8859-15")));

      addPostscriptHeader();
      copyHeaderFile();
      addVariables();

      for (int i = 0; i < pages.size(); i++) {
	((PageData)pages.elementAt(i)).print();
      }

      writer.flush();
      setInformation(printJob);
      return printJob;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Copies postscript header file as is to output.
   */
  private void copyHeaderFile() throws IOException {
    BufferedReader	reader;
    String              s;

    reader = new BufferedReader(new InputStreamReader(at.dms.vkopi.lib.visual.Utils.getFile("report.ps"), "ISO-8859-15"));

    while ((s = reader.readLine()) != null) {
      writeln(s);
    }
    reader.close();
  }

  /**
   * Add the postscript header to the file
   */
  public void addPostscriptHeader() {
    date = at.dms.xkopi.lib.type.Date.now();
    time = at.dms.xkopi.lib.type.Time.now();

    writeln("%!PS-Adobe-3.0");
    writeln("%%Creator: Kopi report generator");
    writeln("%%CreationDate:" + at.dms.vkopi.lib.form.VDateField.toText(date));
    writeln("%%Pages: (atend)");
    writeln("%%EndComments");
    writeln("%%BeginDefaults");
    if (pconfig.paperlayout.equals("Landscape")) {
      writeln("%%PageOrientation: Landscape");
    } else {
      writeln("%%PageOrientation: Portrait");
    }
    writeln("%%EndDefaults");
    writeln("/toprinter {true} def\n");
  }

  /**
   * Add the postcript variables to the file
   */
  public void addVariables() {
    writeln("% Postscript variables");
    writeln("/pagewidth          " + width			+ "		def");
    writeln("/pageheight         " + height			+ "		def");
    writeln("/topmargin          " + top			+ "		def");
    writeln("/bottommargin       " + bottom			+ "		def");
    writeln("/leftmargin         " + left			+ "		def");
    writeln("/rightmargin        " + right			+ "		def");
    writeln("/headermargin       " + header			+ "		def");
    writeln("/footermargin       " + footer			+ "		def");
    writeln("/min_x              " + left			+ "		def");
    writeln("/min_y              " + (bottom + footer)		+ "		def");
    writeln("/max_x              " + (width - right)		+ "		def");
    writeln("/max_y              " + (height - top - header)	+ "		def");
    writeln("/grid_H             " + grid_H			+ "		def");
    writeln("/grid_V             " + grid_V			+ "		def");
    writeln("/borderSize         " + borderSize			+ "		def");
    writeln("/reportFont         " + "/Helvetica-German"	+ "		def");
    writeln("/headerFont         " + "/Helvetica-German"	+ "		def");
    writeln("/footerFont         " + "/Helvetica-German"	+ "		def");
    writeln("/headScale          " + "15"			+ "		def");
    writeln("/footScale          " + "8"			+ "		def");
    writeln("/headFontName       " + "/Helvetica-Bold-German"	+ "		def");
    writeln("/footFontName       " + "/Helvetica-German"	+ "		def");
    writeln("/rowHeight          " + rowHeight			+ "		def");
    writeln("/headerHeight       " + headerHeight		+ "		def");
    writeln("/scale              " + scale			+ "		def");
    writeln("/innerSpace         " + innerSpace			+ "		def");
    writeln("/alg_default        " + Constants.ALG_DEFAULT	+ "		def");
    writeln("/alg_left           " + Constants.ALG_LEFT		+ "		def");
    writeln("/alg_center         " + Constants.ALG_CENTER	+ "		def");
    writeln("/alg_right          " + Constants.ALG_RIGHT	+ "		def");
    writeln("/nbsheet           (" + nbsheet                    + ")            def");
    writeln("% array of column width");
    write("/columnsWidth       [ ");
    for (int i = 0; i < columnsWidth.length; i++) {
      write(columnsWidth[i] + " ");
    }
    writeln("]		def");
    writeln("% array of columns label");
    write("/columnsLabel       [ ");
    for (int i = 0; i < columnsLabel.length; i++) {
      write("(" + columnsLabel[i] + ") ");
    }
    writeln("]		def");
    writeln("% array of columns align");
    write("/columnsAlign       [ ");
    for (int i = 0; i < columnsAlign.length; i++) {
      write(columnsAlign[i] + " ");
    }
    writeln("]	def");
    writeln("% array of colors");
    writeln("/colors           [ ");
    for (int i = 0; i < 10; i++) {
      Color c = parameters.getBackground(i);
      writeln("[ " + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + " ]");
    }
    writeln("]		def");

    writeln("/styles [");
    String[]	strs = new String[style_H.size()];

    for (Enumeration elems = style_H.keys(); elems.hasMoreElements(); ) {
      String	key = (String)elems.nextElement();

      strs[((Integer)style_H.get(key)).intValue()] = key;
    }
    for (int i = 0; i < strs.length; i++) {
      write(strs[i]);
      write(" ");
    }
    writeln("] def");
  }

  /**
   * Return the suffix of the title
   */
  private String findTitleSuffix(String value) {
    String titleSuffix = null;

    if (value != null) {
      StringTokenizer	tok = new StringTokenizer(value, "\n");

      titleSuffix = "";
      while (tok.hasMoreTokens()) {
	titleSuffix += toPostscript(tok.nextToken()) + " ";
      }
    }
    return titleSuffix;
  }

  /**
   * Return the maximum level on the table
   */
  private int findMaxLevel() {
    int			maxLevel = 0;

    for (int j = 0; j < rowLevel.length; j++) {
      if (maxLevel < rowLevel[j])
	maxLevel = rowLevel[j];
    }
    return maxLevel;
  }

  /**
   * Create a vector witch contains all the groupData
   */
  private Vector findGroupData() {
    Vector		groupsVector = new Vector();
    int			start = 0;
    int			length = 0;
    GroupData		group = new GroupData(start, data.length);
    int			begin = 0;
    int			end = data.length;
    int			maxLevel = 0;
    String		titleSuffix = null;

    // page cut by level (each big level on a new page)
    // we dont want the line with the global sum
    if (groupFormfeed) {
      if (pconfig.order == Constants.SUM_AT_TAIL) {
	// case where the maximum level is after his details
	// we delete the last line
	// we don't print the last line of the table (= the total)
	end = data.length - 1;
	length = end;
      } else {
	// here : pconfig.order == Constants.SUM_AT_HEAD
	// case where the maximum level is before his details
	// we don't print the first line of the table (= the total)
	begin += 1;
	start = begin;
      }
    }

    // Search the level maximum
    maxLevel = findMaxLevel();
    // The maximun level is for the total but we don't want this one
    maxLevel--;

    for (int i = begin; i < end; i++) {

      // create for each level a group, if the pages are cut by level
      if (groupFormfeed) {
	if (pconfig.order == Constants.SUM_AT_TAIL || pconfig.order == Constants.SUM_AT_HEAD) {

	  // special case 1 : if pconfig.order == Constants.SUM_AT_TAIL
	  // we want the value of the first column for the first element of the table (= at the first level)
	  // special case 2 : if the level is at the end
	  if ((pconfig.order == Constants.SUM_AT_HEAD && i == begin) ||
	      (pconfig.order == Constants.SUM_AT_TAIL && (rowLevel[i] == maxLevel))) {
	    titleSuffix = findTitleSuffix(data[i][0]);
	  }

	  // case where the maximum level is after his details
	  if (i != 0) {
	    // Cut the page after the line of the max level or
	    // Cut the page before the line of the max level
	    if ((pconfig.order == Constants.SUM_AT_TAIL && (rowLevel[i-1] == maxLevel && rowLevel[i-1] > rowLevel[i])) ||
		(pconfig.order == Constants.SUM_AT_TAIL && (rowLevel[i] == maxLevel && i == end - 1)) ||
		(pconfig.order == Constants.SUM_AT_HEAD && (rowLevel[i] == maxLevel && rowLevel[i-1] < rowLevel[i])) ||
		(pconfig.order == Constants.SUM_AT_HEAD && (i == end - 1))) {

	      // create a new object GroupData
	      group = new GroupData(start, i - start + 1);
	      group.addTitleSuffix(titleSuffix);
	      start = i + 1;

	      // take the suffix of the title
	      if (pconfig.order == Constants.SUM_AT_HEAD && (rowLevel[i] == maxLevel)) {
		titleSuffix = findTitleSuffix(data[i][0]);
	      }
	      // add the new group to the vector of groups
	      groupsVector.addElement(group);
	    }
	  }
	}
      }
    }

    // if the pages are not cut by level, we have only one group = all the datas
    // in this case we don't have a suffix for the title
    if (groupsVector.size() < 1) {
      groupsVector.addElement(group);
    }

    return groupsVector;
  }

  /**
   * return the postcript code used to print a line
   */
  private StringBuffer printPostcriptLine(int i, int j) {
    StringBuffer	currentLine = new StringBuffer();

    // print a line
    currentLine.append("[");
    currentLine.append(rowLevel[i] + " ");
    currentLine.append("[");

    for (int k = 0; k < data[j].length; k++) {
      // We don't print the column if we cut the page by level
      if (!(k == 0 && groupFormfeed)) {

	currentLine.append("[");
	currentLine.append("" + styles[i][k]);

	if (data[i][k] != null) {
	  StringTokenizer	tok = new StringTokenizer(data[i][k], "\n");
	  boolean		hasValue = false;

	  currentLine.append("[");
	  while (tok.hasMoreTokens()) {
	    String		value = toPostscript(tok.nextToken());
	    hasValue = true;
	    currentLine.append("(" + value + ")");
	  }

	  currentLine.append(hasValue ? "]" : "()]");
	} else {
	  currentLine.append("[()]");
	}

	currentLine.append("]");
      }
    }
    currentLine.append("]]\n");

    return currentLine;
  }

  /**
   * Find if a new page is needed or not
   */
  private boolean findIfNewPageIsNeeded (Vector groupsVector, double currentY, int end, int i, int j) {
    boolean		newPageNeeded = false;	// new page needed ?

    // not enough place for another line
    if ((currentY - rowHeight - borderSize) < (bottom + footer)) {
      newPageNeeded = true;
    }

    // nice page cutting
    if (rowLevel[i] != 0 && rowLevel[i] < rowLevel.length) {
      int	nextrow = i + 1;

      if (pconfig.order == Constants.SUM_AT_TAIL) {
	while (nextrow < rowLevel.length && rowLevel[nextrow] > rowLevel[nextrow-1]) {
	      nextrow += 1;
	}
      } else {
	while (nextrow < rowLevel.length && rowLevel[nextrow] < rowLevel[nextrow-1]) {
	  nextrow += 1;
	}
      }
      nextrow = nextrow - i;	// nbrows to check
      if (nextrow > 1) {
	if ((currentY - (nextrow * rowHeight + (nextrow - 1) * grid_H) - borderSize) < (bottom + footer)) {
	      newPageNeeded = true;
	}
      }
    }

    // at the end of the group we want a new page, to print the next group on a new page (if we are not at the last group)
    if (j != groupsVector.size() - 1 && i == end - 1) {
      newPageNeeded = true;
    }

    return newPageNeeded;
  }

  /**
   * Add data to the document
   */
  private Vector workOnGroupData (Vector groupsVector) {
    Vector		pages = new Vector();
    double		currentY = height - top - header - borderSize;
    double		currentX = left + borderSize;
    boolean		newPageNeeded = false;	// new page needed ?
    StringBuffer	currentLine = new StringBuffer();
    PageData		currentPage = new PageData(currentX, currentY);

    currentY -= headerHeight + borderSize;

    for (int j = 0; j < groupsVector.size(); j++) {
      int start = ((GroupData)groupsVector.elementAt(j)).start;
      int end = start + ((GroupData)groupsVector.elementAt(j)).length;

      // on each group of the vector groupsVector
      for (int i = start; i < end; i++) {

	// find if a new page is needed or not
	newPageNeeded = findIfNewPageIsNeeded(groupsVector, currentY, end, i, j);

	if (newPageNeeded) {
	  nbsheet += 1;
	  pages.addElement(currentPage);
	  currentY = height - top - header - borderSize;
	  currentPage = new PageData(currentX, currentY);
	  currentY -= headerHeight + borderSize;
	  newPageNeeded = false;
	}

	// print a line
	currentLine = printPostcriptLine(i,j);
	currentX = left + borderSize;
	currentY -= (rowHeight + grid_H);
	currentPage.addLine(currentLine.toString());

	// add the suffix of the title of the page
	currentPage.addTitleSuffix(((GroupData)groupsVector.elementAt(j)).titleSuffix);

	currentLine.setLength(0);
      }
    }

    pages.addElement(currentPage);
    return pages;
  }

  /**
   * Add data to the document
   */
  public Vector addData() {
    Vector groupsVector = findGroupData();

    return workOnGroupData(groupsVector);
  }

  /**
   * Add the page to the document
   */
  public void addShowPage() {
    writeln("showpage");
    page_number += 1;
  }

  /**
   * Add vertical grid to the document
   */
  public void addVerticalGrid(int nbrows) {
    writeln("% vertical grid");
    writeln(nbrows + " verticalGrid");
  }

  /**
   * Add horizontal grid to the document
   */
  public void addHorizontalGrid(int nbrows) {
    writeln("% horizontal grid");
    writeln(nbrows + " horizontalGrid");
  }

  /**
   * Add a border to the document
   */
  public void addBorder(int nbrows) {
    writeln("% border");
    writeln(nbrows + " createBorder");
  }

  // --------------------------------------------------------------------
  // PRIVATE METHOD
  // --------------------------------------------------------------------

  /**
   * Writes a String in a file
   */
  private void write(String str) {
    try {
      writer.write(str);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Writes a string in a file, and add a new line caracter
   */
  private void writeln(String str) {
    try {
      writer.write(str);
      writer.write("\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the scale to be used for this report
   */
  private double getScale(double widthSum, double min, double max, double precision) {
    if ((widthSum * max * 0.7 + grid_V * (columnsWidth.length - 1) + innerSpace * 2 * columnsWidth.length) <= (width - left - right - 2 * borderSize)) {
      return max;
    }
    if (max - min <= precision) {
      return min;
    }
    if ((widthSum * min * 0.7 + grid_V * (columnsWidth.length - 1) + innerSpace * 2 * columnsWidth.length) <= (width - left - right - 2 * borderSize) &&
	(widthSum * (min + (max-min)/2) * 0.7 + grid_V * (columnsWidth.length - 1) + innerSpace * 2 * columnsWidth.length) >= (width - left - right -2 * borderSize)) {
      return getScale(widthSum, min, min + (max - min)/2, precision);
    } else {
      return getScale(widthSum, max - (max - min)/2, max, precision);
    }
  }

  /**
   * Replace '(' by '\(' and ')' by '\)'
   */
  private String toPostscript(String str) {
    str = str.replaceAll("\\\\", "\\\\\\\\");
    str = str.replaceAll("\\(", "\\\\(");
    str = str.replaceAll("%", "\\\\%");
    str = str.replaceAll("\\)", "\\\\)");

    return str;
  }

  private void setInformation(PrintJob job) {
    job.setPrintInformation(title,
                            pconfig.paperlayout.equals("Landscape"),
                            width,
                            height,
                            page_number - 1);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private BufferedWriter	writer;

  // dynamic construction
  private int			page_number;
  private int			nbsheet	= 1;
  private double[]		columnsWidth;
  private String[]		columnsLabel;
  private int[]			columnsAlign;
  private double		rowHeight;
  private double		headerHeight;
  private double		scale;

  // format parameters
  private int			width;
  private int			height;
  private int			top;
  private int			bottom;
  private int			left;
  private int			right;
  private int			header;
  private int			footer;
  private double		grid_H;
  private double		grid_V;
  private double		borderSize;
  private double		innerSpace;
  private String		title;
  private String		columnDeleted = null;  // name of the column deleted if it exists
  private at.dms.xkopi.lib.type.Date	date;
  private at.dms.xkopi.lib.type.Time	time;
  private boolean		groupFormfeed;


  // --------------------------------------------------------------------
  // CLASS
  // --------------------------------------------------------------------

  /**
   * This class contains the lines displayed on a page.
   */
  class PageData {

    /**
     * Constructs a new page data object.
     * @param	startX		the x coordinate (horizontal position)
     * @param	startY		the y cordinate  (vertical position)
     */
    public PageData(double startX, double startY) {
      this.startX = startX;
      this.startY = startY;
      this.nbRows = 0;
      this.lines = new Vector();
      this.titleSuffix = null;
    }

    /**
     * Adds a line to the page.
     * @param	line		the line to add to the page
     */
    public void addLine(String line) {
      lines.addElement(line);
      nbRows += 1;
    }

    /**
     * Adds a end to the title of the page.
     * @param	titleSuffix		the end of the title to add to the title of the page
     */
    public void addTitleSuffix(String titleSuffix) {
      this.titleSuffix = titleSuffix;
    }

    /**
     * Print the page.
     */
    public void print() {
      printHead();
      printBody();
      printTail();
    }

    /**
     * Print the head of the page
     */
    private void printHead() {
      writeln("% ---------------------------------------------------------");
      writeln("% NEW PAGE");
      writeln("% ---------------------------------------------------------");
      writeln("%%Page: " + page_number + " " + page_number);
      if (pconfig.paperlayout.equals("Landscape")) {
	writeln("landscapeMode		% landscape orientation");
      }

      // add the end of the title if it exists (cut by level --> end Title has a value)
      String finalTitle = title;
      if (titleSuffix != null) {
	finalTitle += " ("+ columnDeleted + ": " + titleSuffix + ")";
      }

      writeln("(" + finalTitle + ") pageheader		% Page header");
      writeln("(" + page_number + ") (" + finalTitle + ") pagefooter		% Page footer");
      writeln("( " + at.dms.vkopi.lib.form.VDateField.toText(date) + " " +  time + ") show");
      writeln(startX + " " + startY + " reportheader		% Report header\n[");
    }

    /**
     * Pint the body of the page
     */
    private void printBody() {
      for (int j = 0; j < lines.size(); j++) {
	writeln((String)lines.elementAt(j));
      }
    }

    /**
     * Print the tail of the page
     */
    private void printTail() {
      writeln("] printpage");
      addVerticalGrid(nbRows);
      addHorizontalGrid(nbRows);
      addBorder(nbRows);
      addShowPage();
    }

    // ------------------------------------------------------------------
    // DATA MEMBERS
    // ------------------------------------------------------------------

    private double 	startX;		// the x coordinate (horizontal position)
    private double 	startY;		// the y cordinate  (vertical position)
    private int		nbRows;		// the number of rows on this page
    private Vector	lines;		// the vector of the lines on this page
    private String	titleSuffix;	// the text to add at the end of the title
  }

  // --------------------------------------------------------------------
  // CLASS
  // --------------------------------------------------------------------

  /**
   * This class contains the different groups of data :
   * a group by level if we cut the pages by level or
   * an unique group on the other case)
   */
  class GroupData {

    /**
     * Constructs a new group of data
     * @param	start		the place of the first element of the group in the vector
     * @param	length		the number of element in this group
     */
    public GroupData(int start, int length) {
      this.start = start;
      this.length = length;
      this.titleSuffix = null;
    }

    public void addTitleSuffix (String titleSuffix) {
      this.titleSuffix = titleSuffix;
    }

    // ------------------------------------------------------------------
    // DATA MEMBERS
    // ------------------------------------------------------------------

    private int 	start;		// the place of the first element of the group in the vector
    private int		length;		// the number of element in this group
    private String	titleSuffix;	// the suffixe of the title for this group
  }
}
