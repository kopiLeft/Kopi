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

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.VLabel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The simple block layout widget.
 */
public class VSimpleBlockLayout extends VAbstractBlockLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new {@code VSimpleBlockLayout} instance.
   * @param col The column number.
   * @param line The row number.
   * @param align The widget which this layout is aligned to.
   * @param alignmentInfo The alignment info if this block is aligned.
   */
  public VSimpleBlockLayout() {
    setCellSpacing(5);
    addStyleDependentName("simple");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void initSize(int columns, int rows) {
    super.initSize(columns, rows);
    follows = new ArrayList<Widget>();
    followsAligns = new ArrayList<ComponentConstraint>();
  }
  
  /**
   * Sets the block alignment.
   * @param align The block alignment.
   */
  public void setAlignment(BlockAlignment align) {
    this.align = align;
    if (this.align != null) {
      alignPane = new VAlignPanel(align);
    }
  }

  @Override
  public void add(Widget widget, ComponentConstraint constraints) {
    if (align == null) {
      if (constraints.width < 0) {
	follows.add(widget);
	followsAligns.add(constraints);
      } else {
	aligns[constraints.x][constraints.y] = constraints;
	widgets[constraints.x][constraints.y] = widget;
      }
    } else {
      if (widget == null) {
	return;
      }
      
      // add to the original block as extra widgets.
      ComponentConstraint		newConstraint;
      
      newConstraint = new ComponentConstraint(align.getTargetPos(constraints.x),
	                                      constraints.y,
	                                      constraints.width,
	                                      constraints.alignRight,
	                                      constraints.useAll);
      // adds an extra widget to the block.
      addAlignedWidget(widget, newConstraint);
    }
  }

  @Override
  public void layout() {
    if (align != null) {
      // aligned blocks will be handled differently
      return;
    } else {
      VLayoutManager		manager = new VLayoutManager(this);
      
      for (int y = 0; y < widgets[0].length; y++) {
	for (int x = 0; x < widgets.length; x++) {
	  if (widgets[x][y] != null && aligns[x][y] != null) {
	    manager.setWidget(widgets[x][y],
		              aligns[x][y],
		              Math.min(aligns[x][y].width, getAllocatedWidth(x, y)),
		              Math.min(getComponentHeight(widgets[x][y]), getAllocatedHeight(x, y)));
	    setAlignment(aligns[x][y].y, aligns[x][y].x, aligns[x][y].alignRight);
	  }
	}
      }
      manager.layout();
      // add follows
      for (int i = 0; i < follows.size(); i++) {
	ComponentConstraint	align = followsAligns.get(i);
	Widget             	comp = follows.get(i);

	addInfoComponentdAt(comp, align.x, align.y);
      }
    }
  }

  @Override
  public void updateScroll(int pageSize, int maxValue, boolean enable, int value) {
    // no scroll bar for simple layouts
  }
  
  /**
   * Returns the component height.
   * @return The component height.
   */
  protected int getComponentHeight(Widget comp) {
    if (comp instanceof VField) {
      return ((VField)comp).getVisibleHeight();
    }
    
    return 1;
  }
  
  /**
   * Returns the allocated height for the given column and row.
   * @return The allocated height for the given column and row.
   */
  protected int getAllocatedHeight(int col, int row) {
    int		allocatedHeight = 1;
    
    for (int y = row + 1 ; y < widgets[col].length; y ++) {
      if (widgets[col][y] != null) {
	break;
      }
      
      allocatedHeight ++;
    }
    
    return allocatedHeight;
  }
  
  /**
   * looks if the column x is already occupied by a component.
   * @param x The column to be tested.
   * @return <code>true</code> if the position is already occupied.
   */
  protected boolean alreadyOccupied(int y, int x) {
    for (int i = 0; i < aligns.length; i++) {
      for (int j = 0; j < aligns[0].length; j++) {
	if (aligns[i][j] != null && aligns[i][j].x == x && aligns[i][j].y == y) {
	  return true;
	}
      }
    }
    
    return false;
  }
  
  /**
   * Returns the allocated width for the given column and row
   * @return The allocated width for the given column and row
   */
  private int getAllocatedWidth(int col, int row) {
    int		allocatedWidth = 1;
    
    for (int x = col + 1; x < widgets.length; x ++) {
      if (widgets[x][row] != null) {
	break;
      }
      
      allocatedWidth ++;
    }
    
    return allocatedWidth;
  }
  
  /**
   * Sets an info widget in the given cell.
   * @param info The info widget.
   * @param x The cell column.
   * @param y The cell row.
   */
  protected void addInfoComponentdAt(Widget info, int x, int y) {
    HorizontalPanel	content = new HorizontalPanel();
    
    content.setStyleName("info-content");
    content.setSpacing(0);
    content.add(widgets[x][y]);
    content.add(info);
    setWidget(content,
	      aligns[x][y].x,
	      aligns[x][y].y,
              Math.min(aligns[x][y].width, getAllocatedWidth(x, y)),
              Math.min(Math.max(getComponentHeight(widgets[x][y]), getComponentHeight(info)), getAllocatedHeight(x, y)));
  }
  
  /**
   * Clean extra cells added by column span
   */
  protected void clean() {
    for (int row = 0; row < getRowCount(); row++) {
      for (int col = 0; col < getCellCount(row); col++) {
        int     rowspan = getFlexCellFormatter().getRowSpan(row, col);
        
	if (rowspan > 1) {
	  // we do not need to clean extra cells when its the last column
	  if (isLeaf(row, col, rowspan)) {
	    continue;
	  }

	  // remove colspan cells
	  cleanExtraCells(row, col);
	}
      }
    }
  }
  
  /**
   * Cleans the col span and row span extra cells.
   * @param row The spanned cell row.
   * @param col The spanned cell column.
   */
  private void cleanExtraCells(int row, int col) {
    for (int r = row + 1; r < row + getFlexCellFormatter().getRowSpan(row, col); r++) {
      for (int c = 0; c < Math.max(1, getFlexCellFormatter().getColSpan(row, col)); c++) {
	try {
	  if (getWidget(r, col) == null) {
	    removeCell(r, col);
	  }
	} catch (IndexOutOfBoundsException e) {
	  // ignore any exception. It does not matter
	}
      }
    }
  }
  
  /**
   * Looks if the given coordinates are leaf.
   * @param row The row index.
   * @param column The column index.
   * @param rowspan The row span value to see if for all rows we don't have
   *        widgets if front of the widget contained in the (row, column) cell.
   * @return {@code true} if the given cell is leaf.
   */
  private boolean isLeaf(int row, int column, int rowspan) {
    for (int r = row; r < row + rowspan; r++) {
      for (int c = column + 1; c < getCellCount(r); c++) {
        if (getWidget(r, c) != null) {
          return false;
        }
      }
    }

    return true;
  }
  
  /**
   * Sets the label width in order to show correctly text info.
   */
  protected void setLabelsWidth() {
    for (int x = 0; x < widgets.length; x++) {
      for (int y = 0; y < widgets[0].length; y++) {
	try {
	  if (getWidget(y, x) instanceof VLabel) {
	    ((VLabel)getWidget(y, x)).setWidth(getCellFormatter().getElement(y, x).getClientWidth());
	  }
	} catch (IndexOutOfBoundsException e) {
	  // ignore all errors
	}
      }
    }
  }
  
  /**
   * Calculates the cell width for those that holds.
   * fields widgets inside.
   */
  protected void calculateCellsWidth() {
    for (int row = 0; row < getRowCount(); row++) {
      for (int col = 0; col < getCellCount(row); col++) {
        if (getCellFormatter().getElement(row, col) != null && getWidget(row, col) != null) {
          getCellFormatter().getElement(row, col).getStyle().setWidth(getWidget(row, col).getElement().getClientWidth(), Unit.PX);
        }
      }
    }
  }
  
  /**
   * Returns the coordinates of a widget.
   * @param widget The searched widget.
   * @return The widget coordinates.
   */
  protected int[] getWidgetCordinates(Widget widget) {
    for (int row = 0; row < getRowCount(); row++) {
      for (int col = 0; col < getCellCount(row); col++) {
	Widget 	w = getWidget(row, col);
	
	if (w == widget) {
	  return new int[] {row, col};
	}
      }
    }
    
    return null;
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    if (!cleaned) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

	@Override
	public void execute() {
	  clean();
	  setLabelsWidth();
	  calculateCellsWidth();
	}
      });
      cleaned = true;
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private List<Widget>				follows;
  private List<ComponentConstraint> 		followsAligns;
  private boolean				cleaned;
}
