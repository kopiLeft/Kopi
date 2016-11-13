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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import java.util.List;

import org.gwt.advanced.client.ui.widget.AdvancedFlexTable;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

/**
 * An abstract implementation for the block layout.
 */
public abstract class VAbstractBlockLayout extends AdvancedFlexTable implements BlockLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the block layout widget.
   */
  protected VAbstractBlockLayout() {
    enableVerticalScrolling(false);
    setStyleName(Styles.BLOCK_LAYOUT);
    setCellPadding(0);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Initialize layout size
   * @param columns The number of column.
   * @param rows The number of lines.
   */
  public void initSize(int columns, int rows) {
    widgets = new Widget[columns][rows];
    aligns = new ComponentConstraint[columns][rows];
  }
  
  /**
   * Sets a header column span.
   * @param column The column index.
   * @param colSpan The number of column spanned.
   */
  protected void setHeaderColSpan(int column, int colSpan) {
    getHeaderElement(column).setPropertyInt("colSpan", colSpan);
  }
  
  /**
   * Sets the header column alignment.
   * @param column The header column.
   * @param hAlign The horizontal alignment.
   * @param vAlign The vertical alignment.
   */
  protected void setHeaderAlignment(int column, HorizontalAlignmentConstant hAlign, VerticalAlignmentConstant vAlign) {
    if (getHeaderElement(column).getFirstChildElement() != null) {
      String style = "text-align : " + hAlign.getTextAlignString()  + " !important;";
      getHeaderElement(column).getFirstChildElement().setAttribute("style", style);
    }
    getHeaderElement(column).getStyle().setProperty("verticalAlign", vAlign.getVerticalAlignString());
  }
  
  /**
   * Returns the header element of a given column.
   * @param column The column index.
   * @return The header element.
   */
  protected Element getHeaderElement(int column) {
    return DOM.getChild(DOM.getFirstChild(getTHeadElement()), column);
  }
  
  /**
   * Sets the widget in the given layout cell.
   * @param widget The widget to be set.
   * @param row The cell row.
   * @param column The Cell column.
   * @param colSpan The column span width
   * @param rowSpan The row span width.
   */
  protected void setWidget(Widget widget, int column, int row, int colSpan, int rowSpan) {
    setWidget(row, column, widget);
    if (colSpan > 1) {
      getFlexCellFormatter().setColSpan(row, column, colSpan);
    }
    if (rowSpan > 1) {
      getFlexCellFormatter().setRowSpan(row, column, rowSpan);
    }
  }
  
  /**
   * Sets the widget in the given layout cell.
   * @param widget The widget to be set.
   * @param column The Cell column.
   * @param row The cell row.
   */
  protected void setWidget(Widget widget, int column, int row) {
    setWidget(row, column, widget);
  }
  
  /**
   * Sets the alignment of a cell. 
   * @param row The cell row.
   * @param column The cell column.
   * @param right Is it right aligned ?
   */
  protected void setAlignment(int row, int column, boolean right) {
    getCellFormatter().setHorizontalAlignment(row, column, right ? HasHorizontalAlignment.ALIGN_RIGHT : HasHorizontalAlignment.ALIGN_LEFT);
    getCellFormatter().setVerticalAlignment(row, column, HasVerticalAlignment.ALIGN_TOP);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T extends Widget> T cast() {
    return (T)this;
  }
  
  @Override
  public void addAlignedWidget(Widget widget, ComponentConstraint constraint) {
    if (alignPane != null) {
      alignPane.addWidget(widget, constraint);
    }
  }
  
  @Override
  public void layoutAlignedWidgets() {
    if (alignPane != null) {
      setWidget(0, 0, alignPane);
    }
  }
 
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
	layoutAlignedWidgets();
	handleLayoutVisibility();
      }
    });
  }
  
  /**
   * Sets the extra row style.
   * @param row The concerned row.
   */
  protected void addExtraRowStyle(int row) {
    getRowFormatter().addStyleName(row, "extra");;
    for (int column = 0; column < getCellCount(row); column++) {
      getCellFormatter().addStyleName(row, column, "extra");
    }
  }
  
  /**
   * Returns the text field encapsulated by a cell 
   */
  protected InputElement getTextField(int row, int column) {
    return getInputElement(getCellFormatter().getElement(row, column));
  }
  
  /**
   * Returns the input element.
   * @param root The root search element.
   * @return The input element.
   */
  protected InputElement getInputElement(Element root) {
    while (root != null) {
      if (root instanceof InputElement
	  && (Styles.TEXT_INPUT.equals(root.getClassName())
	      || Styles.TEXT_AREA_INPUT.equals(root.getClassName())))
      {
	return (InputElement)root;
      }
      
      root = DOM.getFirstChild(root);
    }
    
    return null;
  }

  /**
   * Setter for property 'currentRow'.
   *
   * @param currentRow Value to set for property 'currentRow'.
   */
  public void setCurrentRow(int currentRow) {
    if (selectedRow == currentRow) {
      return;
    }
    
    if (!(this instanceof VChartBlockLayout)) {
      return;
    }
    
    HTMLTable.RowFormatter 	rowFormatter = getRowFormatter();
    
    if (selectedRow >= 0) {
      rowFormatter.removeStyleName(selectedRow, "selected-row");
    }
    
    if (currentRow >= 0 && currentRow < getRowCount()) {
      selectRow(currentRow);
    }
  }

  /**
   * This method marks the specified row as selected.<p/>
   * It works similarly to the {@link #setCurrentRow(int)} method but doesn't clear a previous selection.
   * If the multiple rows selection is disabled it checks whether there is at least one selected row and if no
   * it makes selection. Otherwise it does nothing.<p/>
   * If multiple mode is enabled it always selects a row.
   *
   * @param row is a row number to make selected.
   */
  public void selectRow(int row) {
    HTMLTable.RowFormatter	rowFormatter = getRowFormatter();
    
    rowFormatter.addStyleName(row, "selected-row");
    selectedRow = row;
  }
  
  /**
   * Handles the layout visibility according to fields and header visibilities.
   */
  public void handleLayoutVisibility() {
    VBlock              parent;
    boolean             isFullyInvisible;
    
    parent = WidgetUtils.getParent(this, VBlock.class);
    isFullyInvisible = isFullyInvisible();
    if (parent != null && parent.getCaption() != null) {
      parent.getCaption().setVisible(!isFullyInvisible);
    }
    // sets the block visibility
    setVisible(!isFullyInvisible);
  }
  
  @Override
  public void setVisible(boolean visible) {
    getElement().getStyle().setVisibility(visible ? Visibility.VISIBLE : Visibility.HIDDEN);
  }
  
  /**
   * Returns {@code true} if the layout should be fully invisible.
   * @return {@code true} if the layout should be fully invisible.
   */
  protected boolean isFullyInvisible() {
    for (int row = 0; row < getRowCount(); row++) {
      for (int col = 0; col < getCellCount(row); col++) {
        if (WidgetUtils.isVisible(getCellFormatter().getElement(row, col))) {
          return false;
        }
      }
    }
    
    return true;
  }

  @Override
  public void setHeaderWidget(int column, Widget widget) {
    prepareHeaderCell(column);

    if (widget != null) {
      widget.removeFromParent();

      Element th = DOM.getChild(DOM.getFirstChild(getTHeadElement()), column);
      internalClearCell(th, true);

      // Physical attach.
      DOM.appendChild(th, widget.getElement());

      List<Widget> headerWidgets = getHeaderWidgets();
      if (headerWidgets.size() > column && headerWidgets.get(column) != null) {
	headerWidgets.set(column, widget);
      } else {
	headerWidgets.add(widget);
      }

      adopt(widget);
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  protected Widget[][]				widgets;
  protected ComponentConstraint[][]		aligns;
  private int					selectedRow = -1;
  // special panel for aligned blocks
  protected VAlignPanel				alignPane;
  public BlockAlignment				align;
}
