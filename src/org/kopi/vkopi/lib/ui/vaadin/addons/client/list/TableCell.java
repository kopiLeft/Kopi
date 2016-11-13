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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.list;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.TableModel.ColumnType;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

/**
 * A table cell representation.
 * A cell can be compared to another cell for sort purposes
 */
public class TableCell extends VSpan implements Comparable<TableCell> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>TableCell</code> instance.
   * @param row The cell row index.
   * @param column The cell column index.
   * @param type The cell type.
   * @param align The cell align
   */
  public TableCell(int row, int column, ColumnType type, int align) {
    this.row = row;
    this.column = column;
    this.type = type;
    this.align = align;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Attach this cell to a given table.
   * @param table The table to attach this cell.
   */
  protected void attach(FlexTable table) {
    if (table != null) {
      table.setWidget(row, column, this);
      table.getCellFormatter().setHorizontalAlignment(row, column, getAlignment());
    }
  }
  
  /**
   * Returns the cell text.
   * @return the cell text.
   */
  public String getText() {
    return getElement().getInnerText();
  }
  
  /**
   * Returns {@code true} if the column contains a boolean true value.
   * @return {@code true} if the column contains a boolean true value.
   */
  protected boolean isTrue() {
    return "oui".equalsIgnoreCase(getText())
      || "yes".equalsIgnoreCase(getText())
      || "true".equalsIgnoreCase(getText())
      || "ja".equalsIgnoreCase(getText());
  }
  
  /**
   * Returns the comparable value of this cell.
   * @return The comparable value of this cell.
   */
  @SuppressWarnings("rawtypes")
  public Comparable getValue() {
    Comparable<?>               value;
    
    if (getText() == null || getText().trim().length() == 0) {
      // empty values ignore
      return null;
    }
    
    switch (type) {
    case NUMBER:
      value = new Double(getText());
      break;
    case BOOLEAN:
      value = new Boolean(isTrue());
      break;
    case DATE:
      value = DateTimeFormat.getFormat("dd.MM.yyyy").parse(getText());
      break;
    case TIME:
      value = DateTimeFormat.getFormat("HH:mm").parse(getText());
      break;
    case TIMESTAMP:
      value = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(getText().substring(0, 23));
      break;
    case MONTH:
      value = DateTimeFormat.getFormat("MM.yyyy").parse(getText());
      break;
    case WEEK:
      // weeks will not be treated as a date, we will transform it to a number
      // weeks have the following format : nn.yyyy
      // we will create a number with the concatenation of the week number and the year.
      int               period;
      
      period = getText().indexOf('.');
      value = new Double(String.valueOf(getText().substring(0, period) + getText().substring(period + 1)));
      break;
    default:
      value = getText().toLowerCase(); // Comparison is not case sensitive
      break;
    }
    
    return value;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(TableCell cell) {
    if (cell == null) {
      return 1; // if cell is null, we consider that this one is bigger
    } else if (getValue() == null) {
      if (cell.getValue() == null) {
        return 0; //equals
      } else {
        return -1; // the other cell is bigger
      }
    } else {
      if (cell.getValue() == null) {
        return 1; // this cell is bigger
      } else {
        // compare
        return getValue().compareTo(cell.getValue());
      }
    }
  }
  
  /**
   * Returns the horizontal alignment to put in the call table.
   * @return The horizontal alignment to put in the call table.
   */
  protected HorizontalAlignmentConstant getAlignment() {
    switch (align) {
    case 0: // CENTER
      return HasHorizontalAlignment.ALIGN_CENTER;
    case 2: // LEFT
      return HasHorizontalAlignment.ALIGN_LEFT;
    case 4: // RIGHT
      return HasHorizontalAlignment.ALIGN_RIGHT;
    default:
      return HasHorizontalAlignment.ALIGN_LEFT;
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /*package*/ int				row; // this is not final because we can sort
  /*package*/ final int				column;
  /*package*/ final ColumnType                  type;
  /*package*/ final int				align;
}
