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
 * $Id$
 */

package at.dms.vkopi.lib.report;

import java.awt.Color;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTable;

public class RDataExtractor {

  /**
   * Constructor
   */
  public RDataExtractor(JTable table, MReport model, PConfig pconfig) {
    this.model = model;
    this.table = table;
    this.pconfig = pconfig;
  }

  /**
   * selects data to be displayed
   */
  public String[][] selectDisplayData() {
    Vector rows		= new Vector(model.getRowCount());
    Vector colors	= new Vector(model.getRowCount());
    Vector levels	= new Vector(model.getRowCount());

    // get the number of column
    int columnCount = model.getAccessibleColumnCount();
    int newColumnCount = 0;
    for (int i = 0; i < columnCount; i++) {
      if (getVisibleColumn(i).isVisible() && !getVisibleColumn(i).isFolded()) {
	newColumnCount += 1;
      }
    }
    columnCount = newColumnCount;

    VReportColumn[] cols = new VReportColumn[columnCount];
    DColumnStyle[][] renderers = new DColumnStyle[columnCount][];
    newColumnCount = 0;
    for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
      if (getVisibleColumn(i).isVisible() && !getVisibleColumn(i).isFolded()) {
	cols[newColumnCount] = getVisibleColumn(i);
	renderers[newColumnCount] = cols[newColumnCount++].getStyles();
      }
    }

    // make data vector
    VReportRow tree = model.getTree();
    if (pconfig.order == Constants.SUM_AT_TAIL) {
      addTree2(tree, rows, colors, levels, columnCount, -1);
    } else {
      addTree(tree, rows, colors, levels, columnCount);
    }

    // make data tab
    int rowCount	= rows.size();
    data		= new String[rowCount][columnCount];

    styles		= new int[rowCount][columnCount];
    rowcolors		= new Color[rowCount];
    rowLevel		= new int[rowCount];

    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
	data[i][j]	= ((String [])rows.elementAt(i))[j];

	DColumnStyle cell = null;
	// search
	if (renderers[j].length == 1) {
	  // normal way
	  cell = renderers[j][0];
	} else {
	  // specific way
	  int state = DReport.getState(data[i][j]);
	  for (int s = 0; s < renderers[j].length; s++) {
	    if (renderers[j][s].getState() == state) {
	      cell = renderers[j][s];
	    } else if (cell == null && renderers[j][s].getState() == 0) {
	      cell = renderers[j][s];
	    }
	  }
	  if (cell == null) {
	    cell = renderers[j][0];
	  }
	}

	String	postscript = cell.getPostscript();
	Integer	pos = (Integer)style_H.get(postscript);
	if (pos == null) {
	  style_H.put(postscript, pos = new Integer(style_H.size()));
	}
	styles[i][j]	= pos.intValue();
      }
      rowcolors[i]	= (Color)colors.elementAt(i);
      rowLevel[i]	= ((Integer)levels.elementAt(i)).intValue();
    }

    return data;
  }

  /**
   * Add wanted rows to the vector
   */
  public void addTree(VReportRow row, Vector v, Vector c, Vector l, int size) {
    boolean restrictedrow	= pconfig.visibleRows;

    if (row.isVisible() || !restrictedrow) {
      String[] newrow = new String[size];
      int index = 0;

      for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
	if (!getVisibleColumn(i).isFolded()) {
	  if (row.getLevel() < model.getDisplayLevels(model.getReverseOrder(indexToModel(i)))) {
	    newrow[index] = null;
	  } else {
	    newrow[index] = getVisibleColumn(i).format(row.getValueAt(indexToModel(i)));
	  }
	  index += 1;
	}
      }

      v.addElement(newrow);
      c.addElement(parameters.getBackground(row.getLevel()));
      l.addElement(new Integer(row.getLevel()));
      for (int i = 0; i < row.getChildCount(); i++) {
	addTree((VReportRow)row.getChildAt(i), v, c, l, size);
      }
    }
  }

  /**
   * Add wanted rows to the vector
   */
  public void addTree2(VReportRow row, Vector v, Vector c, Vector l, int size, int id) {
    for (int i = 0; i < row.getChildCount(); i++) {
      addTree2((VReportRow)row.getChildAt(i), v, c, l, size, i);
    }

    boolean restrictedrow	= pconfig.visibleRows;

    if (row.isVisible() || !restrictedrow) {
      String[] newrow = new String[size];
      int index = 0;

      for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
	if (!getVisibleColumn(i).isFolded())  {
	  if (row.getLevel() < model.getDisplayLevels(model.getReverseOrder(indexToModel(i)))) {
	    newrow[index] = null;
	  } else {
	    newrow[index] = getVisibleColumn(i).format(row.getValueAt(indexToModel(i)));
	  }
	  index += 1;
	}
      }

      if (id == 0 && row.getChildCount() == 0) {
	VReportRow child	= row;
	VReportRow parent	= (VReportRow)row.getParent();

	while (parent != null && parent.getFirstChild() == child) {
	  index = 0;
	  for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
	    if (!getVisibleColumn(i).isFolded()) {
	      if (row.getLevel() < model.getDisplayLevels(model.getReverseOrder(indexToModel(i))) &&
		  parent.getLevel() >= model.getDisplayLevels(model.getReverseOrder(indexToModel(i)))) {
		newrow[index] = getVisibleColumn(i).format(row.getValueAt(indexToModel(i)));
	      }
	      index += 1;
	    }
	  }
	  child = parent;
	  parent = (VReportRow)parent.getParent();
	}
      }

      v.addElement(newrow);
      c.addElement(parameters.getBackground(row.getLevel()));
      l.addElement(new Integer(row.getLevel()));
    }
  }

  protected VReportColumn getVisibleColumn(int i) {
    return model.getAccessibleColumn(indexToModel(i));
  }

  protected int indexToModel(int i) {
    return table.convertColumnIndexToModel(i);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private MReport		model;
  private JTable		table;
  protected PConfig		pconfig;

  protected String[][]		data;
  protected int[][]		styles;
  protected Color[]		rowcolors;
  protected int[]		rowLevel;
  protected Hashtable		style_H = new Hashtable();

  // report personnalisation
  protected DParameters		parameters;
}
