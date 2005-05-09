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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.Color;

import javax.swing.JTable;

public abstract class PExport {
  /**
   * Constructor
   */
  public PExport(JTable table, MReport model, PConfig pconfig, String title) {
    this.model = model;
    this.table = table;
    this.pconfig = pconfig;
    this.title = title; 

    this.parameters = new DParameters(Color.blue);
    this.firstVisibleColumn = -1;

    for (int j = 0; j <  model.getAccessibleColumnCount(); j++) {
      int             visibleColumn = table.convertColumnIndexToModel(j);
      VReportColumn   column = model.getAccessibleColumn(visibleColumn);

      if (column.isVisible() && !column.isFolded()) {
        if (firstVisibleColumn == -1) {
          firstVisibleColumn = j;
        }
        columnCount += 1;
      }
    }
    if (pconfig.groupFormfeed) {
      // each group an own page:
      // first column is not shown because its the
      // same for all -> it is added to the "title"
      columnCount -= 1;
    }
    maxLevel =  model.getTree().getLevel();
  }

  public void formatColumns() {
    int         index = 0;

    for (int j = 0; j <  model.getAccessibleColumnCount(); j++) {
      int             visibleColumn = table.convertColumnIndexToModel(j);
      VReportColumn   column = model.getAccessibleColumn(visibleColumn);

      if (column.isVisible() && !column.isFolded()
          // if we have a new page for each group, we do not use the first
          // visible column
          && (!pconfig.groupFormfeed || j != firstVisibleColumn)) {
        column.formatColumn(this, index);
        index += 1;
      }
    }
  }

  protected void exportHeader() {
    String[]    data = new String[columnCount];
    int         index = 0;

    for (int j = 0; j <  model.getAccessibleColumnCount(); j++) {
      int             visibleColumn = table.convertColumnIndexToModel(j);
      VReportColumn   column = model.getAccessibleColumn(visibleColumn);

      if (column.isVisible() && !column.isFolded()
          // if we have a new page for each group, we do not use the first
          // visible column
          && (!pconfig.groupFormfeed || j != firstVisibleColumn)) {
        data[index] = column.getLabel();
        index += 1;
      }
    }
    exportHeader(data);
  }

  protected void exportData() {
    VGroupRow   group =  model.getTree();

    if (!pconfig.groupFormfeed) {
      startGroup(null);
      exportHeader();
    }
    addTree(group);
  }

  private void addTree(VReportRow row) {
    boolean restrictedrow	= pconfig.visibleRows;

    if (row.isVisible() || !restrictedrow) { 
      if (pconfig.groupFormfeed && row.getLevel() == maxLevel-1) {
        VReportColumn   column = model.getAccessibleColumn(firstVisibleColumn);

        startGroup(column.format(row.getValueAt(firstVisibleColumn)));
        exportHeader();
      }
      if (pconfig.order != Constants.SUM_AT_TAIL
          // if we have a new page for each group
          // the sum over all groups is not shown
          && (!pconfig.groupFormfeed || row.getLevel() != maxLevel)) {
        // show sum first
        exportRow (row, false);
      }

      for (int i = 0; i < row.getChildCount(); i++) {
	addTree((VReportRow)row.getChildAt(i));
      }

      if (pconfig.order == Constants.SUM_AT_TAIL
          // if we have a new page for each group
          // the sum over all groups is not shown
          && (!pconfig.groupFormfeed || row.getLevel() != maxLevel)) {
        // show sum at the end
        exportRow (row, true);
      }
    }
  }

  private void exportRow (VReportRow row, boolean tail) {
    int      index  = 0;
    String[] newrow = new String[columnCount];
    Object[] newrowOrig = new Object[columnCount];
    int[]    alignments = new int[columnCount];

    for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
      int             visibleColumn = table.convertColumnIndexToModel(i);
      VReportColumn   column = model.getAccessibleColumn(visibleColumn);

      if (!column.isFolded() && column.isVisible()
          // if we have a new page for each group, we do not use the first
          // visible column
          && (!pconfig.groupFormfeed || i != firstVisibleColumn)) {
        if (row.getLevel() < model.getDisplayLevels(model.getReverseOrder(visibleColumn))) {
          newrow[index] = null;
          newrowOrig[index] = null;
        } else {
          newrow[index] = column.format(row.getValueAt(visibleColumn));
          newrowOrig[index] = row.getValueAt(visibleColumn);
        }
        alignments[index] = column.getAlign();
        index += 1;
      }
    }

    if (tail && row.getParent() != null 
        &&  ((VReportRow) row.getParent()).getFirstChild() == row 
        && row.getChildCount() == 0) {
      // if the sums are at the end, at the the first row of the group
      // the group information
      VReportRow        child	= row;
      VReportRow        parent	= (VReportRow)row.getParent();
      
      while (parent != null && parent.getFirstChild() == child) {
        index = 0;
        
        for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
          int           visibleColumn = table.convertColumnIndexToModel(i);
          VReportColumn column = model.getAccessibleColumn(visibleColumn);

          if (!column.isFolded() && column.isVisible()
              // if we have a new page for each group, we do not use the first
              // visible column
              && (!pconfig.groupFormfeed || i != firstVisibleColumn)) {
            if (row.getLevel() < model.getDisplayLevels(model.getReverseOrder(visibleColumn)) &&
                parent.getLevel() >= model.getDisplayLevels(model.getReverseOrder(visibleColumn))) {
              newrow[index] = column.format(row.getValueAt(visibleColumn));
              newrowOrig[index] = row.getValueAt(visibleColumn);
            }
            index += 1;
          }
        }
        child = parent;
        parent = (VReportRow)parent.getParent();
      }
    }
    exportRow(row.getLevel(), newrow, newrowOrig, alignments);
  }

  public  void export(File file) {
    try {
      export(new FileOutputStream("/tmp/ets.afd"));///!!!!!!file));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected abstract void startGroup(String subTitle);
  protected abstract void exportRow(int level, String[] data, Object[] orig, int[] alignment);
  protected abstract void exportHeader(String[] data);
  protected abstract void export(OutputStream stream);

  protected void formatStringColumn(VReportColumn column, int index) {}
  protected void formatDateColumn(VReportColumn column, int index) {}
  protected void formatMonthColumn(VReportColumn column, int index) {}
  protected void formatWeekColumn(VReportColumn column, int index) {}
  protected void formatFixedColumn(VReportColumn column, int index) {}
  protected void formatIntegerColumn(VReportColumn column, int index) {}
  protected void formatBooleanColumn(VReportColumn column, int index) {}
  protected void formatTimeColumn(VReportColumn column, int index) {}
  protected void formatTimestampColumn(VReportColumn column, int index) {}

  public int getColumnCount() {
    return columnCount;
  }

  public int getMaxLevel() {
    return maxLevel;
  }

  public String getTitle() {
    return title;
  }

  public String getColumnLabel(int column) {
    return model.getAccessibleColumn(table.convertColumnIndexToModel(column)).getLabel();    
  }

  public PConfig getPrintConfig() {
    return pconfig;
  }

  public Color getBackgroundForLevel(int level) {
    return parameters.getBackground(level);
  }

  private MReport		model;
  private JTable		table;
  protected PConfig		pconfig;
  private String                title;

  private int                   columnCount;      
  private int                   firstVisibleColumn;
  private int                   maxLevel;
  private DParameters           parameters;
}
