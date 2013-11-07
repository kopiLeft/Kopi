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

package com.kopiright.vkopi.lib.form;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.list.VStringColumn;
import com.kopiright.vkopi.lib.ui.base.UComponent;
import com.kopiright.vkopi.lib.visual.UIFactory;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VModel;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.xkopi.lib.type.Date;

public class VListDialog implements VModel {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Creates a dialog with specified data
   */
  public VListDialog(VListColumn[] list,
                     Object[][] data,
                     int[] idents,
                     int rows)
  {
    this(list, data, idents, rows, true);
  }

  /**
   * Creates a dialog with specified data
   */
  public VListDialog(VListColumn[] list,
                     Object[][] data,
                     int rows,
                     String newForm)
  {
    this(list, data, makeIdentArray(rows), rows, false);
    this.newForm = newForm;
  }

  /**
   * Creates a dialog with specified data
   */
  public VListDialog(VListColumn[] list,
                     Object[][] data,
                     int rows)
  {
    this(list, data, makeIdentArray(rows), rows, false);
  }

  /**
   * Creates a dialog with specified data and title bar.
   */
  public VListDialog(VListColumn[] list, Object[][] data) {
    this(list, data, data[0].length);
  }

  /**
   * Creates a dialog with specified data and title bar.
   */
  public VListDialog(String title,
                     String[] data,
                     int rows)
  {
    this(new VListColumn[]{ new VStringColumn(title,
                                              null,
                                              VConstants.ALG_LEFT,
                                              getMaxLength(data),
                                              true) },
         new String[][]{ data },
         rows);
  }

  /**
   * Creates a dialog with specified data and title bar.
   */
  public VListDialog(String title, String[] data) {
    this(title, data, data.length);
  }

  /**
   * Creates a dialog with specified data
   */
  public VListDialog(VListColumn[] list,
                     Object[][] data,
                     int[] idents,
                     int rows,
                     boolean skipFirstLine)
  {
    if (list.length != data.length) {
      throw new InconsistencyException("WRONG NUMBER OF COLUMN OR TITLES: " +
                                       "list.length = " + list.length +
                                       " does not match data.length = " + data.length);
    }

    this.skipFirstLine = skipFirstLine;
    this.data = data;
    this.idents = idents;
    count = rows;
    columns = list;
    sizes = new int[list.length];
    titles = new String[list.length];

    if (idents[0] != 0) {
      this.skipFirstLine = false;
    }

    tab = new int[idents.length - (this.skipFirstLine ? 1 : 0)];
    for (int i = 0; i < sizes.length; i++) {
      sizes[i] = Math.max(list[i].getWidth(), list[i].getTitle().length());
      titles[i] = list[i].getTitle();
    }

    for (int i = 0; i < tab.length; i ++) {
      tab[i] = i + (this.skipFirstLine ? 1 : 0);
    }

    display = (UListDialog)UIFactory.getUIFactory().createView(this);
  }

  // --------------------------------------------------------------------
  // IMPLEMENATION
  // --------------------------------------------------------------------

  /**
   * Displays a dialog box returning position of selected element.
   */
  public static int selectFromDialog(VWindow window, String[] str) {
    int      size = 0;

    for (int i = 0; i < str.length; i++) {
      size = Math.max(size, str[i].length());
    }

    return new VListDialog(new VListColumn[] {new VStringColumn("Auswahl", null, 0, size, true)},
                           new Object[][] {str}).selectFromDialog(window, null, true);
  }

  /**
   * Displays a dialog box returning position of selected element.
   */
  public int selectFromDialog(VWindow window, VField field) {
    return selectFromDialog(window, field, true);
  }

  /**
   * Displays a dialog box returning position of selected element.
   */
  public int selectFromDialog(VForm form, VWindow window, VField field) {
    this.form = form;
    return selectFromDialog(window, field, true);
  }

  /**
   * Displays a dialog box returning position of selected element.
   * @exception VException       an exception may be raised by string formater
   */
  public int selectFromDialog(boolean showSingleEntry) {
    return selectFromDialog(null, showSingleEntry);
  }

  /**
   * Displays a dialog box returning position of selected element.
   */
  public int selectFromDialog(VWindow window, VField field, boolean showSingleEntry) {
    return display.selectFromDialog(window != null ? window.getDisplay() : null,
	                            field != null ? field.getDisplay() : null,
	                            showSingleEntry);
  }

  /**
   * Displays a dialog box returning position of selected element.
   * @exception VException       an exception may be raised by string formater
   */
  public int selectFromDialog(VWindow window, boolean showSingleEntry) {
    return display.selectFromDialog(window != null ? window.getDisplay() : null, showSingleEntry);
  }

  /**
   * Sorts the model
   */
  public void sort() {
    sort(0);
  }

  public void sort(int left) {
    if (data.length == 0) { // one element
      return;
    }

    for (int i = 0; i < count; i++) {
      tab[i] = i + (skipFirstLine ? 1 : 0); // reinit
    }

    for (int i = count; --i >= 0; ) {
      for (int j = 0; j < i; j++) {
        Object        value1 = data[left][tab[j]];
        Object        value2 = data[left][tab[j+1]];
        boolean       swap;

        if ((value1 != null) && (value2 != null)) {
          if (value1 instanceof String) {
            swap = (((String)value1).compareTo((String)value2) > 0);
          } else if (value1 instanceof Number) {
            swap = (((Number)value1).doubleValue() > ((Number)value2).doubleValue());
          } else if (value1 instanceof Boolean) {
            swap = (((Boolean)value1).booleanValue() && !((Boolean)value2).booleanValue());
          } else if (value1 instanceof Date) {
            swap = (((Date)value1).compareTo((Date)value2) > 0);
          } else {
            //!!! graf 010125 can we ever come here ? throw InconsistencyException()
            swap = false;
          }
        } else {
          swap = (value1 == null) && (value2 != null);
        }

        if (swap) {
          int tmp = tab[j];

          tab[j] = tab[j+1];
          tab[j+1] = tmp;
        }
      }
    }

    if (columns[left].isSortAscending()) {
      // reverse sorting
      for (int i = 0; i < count / 2; i++) {
        int tmp = tab[i];
        tab[i] = tab[count - 1 - i];
        tab[count - 1 - i] = tmp;
      }
    }
  }

  // --------------------------------------------------------------------
  // VMODEL IMPLEMENTATION
  // --------------------------------------------------------------------

  
  public void setDisplay(UComponent display) {
    assert display instanceof UListDialog : "Display must be UListDialog";

    this.display = (UListDialog)display;
  }

  
  public UListDialog getDisplay() {
    return display;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Enables the insertion of a new record
   */
  public void setForceNew() {
    forceNew = true;
  }

  /**
   * Enables the too many rows message
   */
  public void setTooManyRows() {
    tooManyRows = true;
  }
  /**
   * @return the skipFirstLine
   */
  public boolean isSkipFirstLine() {
    return skipFirstLine;
  }

  /**
   * @return the forceNew
   */
  public boolean isForceNew() {
    return forceNew;
  }

  /**
   * @return the tooManyRows
   */
  public boolean isTooManyRows() {
    return tooManyRows;
  }

  /**
   * @return the sizes
   */
  public int[] getSizes() {
    return sizes;
  }

  /**
   * @return the columns
   */
  public VListColumn[] getColumns() {
    return columns;
  }

  /**
   * @return the data
   */
  public Object[][] getData() {
    return data;
  }

  /**
   * @return the titles
   */
  public String[] getTitles() {
    return titles;
  }

  /**
   * @return the idents
   */
  public int[] getIdents() {
    return idents;
  }

  /**
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * @return the newForm
   */
  public String getNewForm() {
    return newForm;
  }

  public VForm getForm() {
    return form;
  }

  public void setForm(VForm form) {
    this.form = form;
  }

  /**
   * Returns a value at a given position
   */
  public Object getValueAt(int row, int col) {
    return data[col][row];
  }

  public int[] getTranslatedIdents() {
    return tab;
  }

  /**
   * Makes an identifiers array
   */
  private static int[] makeIdentArray(int rows) {
    int[]       idents = new int[rows];

    for (int i = 0; i < rows; i++) {
      idents[i] = i;
    }
    return idents;
  }

  /**
   * Returns the max length in a given String array.
   */
  private static int getMaxLength(String[] values) {
    int result = 0;

    for (int i = 0; i < values.length; i++) {
      if (values[i] != null) {
        result = Math.max(result, values[i].length());
      }
    }

    return result;
  }

  public int convert(int pos) {
    return pos == -1 ? -1 : idents[tab[pos]];
  }

  public int getColumnCount() {
    return data.length;
  }

  public String getColumnName(int column) {
    return titles[column];
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private boolean 				skipFirstLine = true;
  private VForm                 		form;
  private boolean               		forceNew;
  private boolean               		tooManyRows;
  private int[]                 		sizes;
  private VListColumn[]         		columns;
  private Object[][]  				data;
  private String[]				titles;
  private int[]       				idents;
  private int					count;
  private String                		newForm;
  private int[]       				tab;
  private UListDialog				display;

  // returned value if a user click on a forced new button and there
  // is no form to create a record
  public static int             		NEW_CLICKED     = -2;
}
