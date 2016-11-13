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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple block layout manager. This class aims to correct widget positions in case of
 * column and row span. In fact, GWT flexible table does not handle correctly the indexes
 * of widgets in case of a columns and rows span. For example, a column span of 2 in the first
 * column will lead to index the normally third column to the second column.
 * 
 * [(0,1) - (0,2) - (0, 3)] : if we span the first and the second column, GWT re-index the third
 * column to the second one and the configuration become : [(0,1) - (..) - (0, 2)]
 * 
 * This behavior causes a lot of problems in the simple block layout. This class aims to correct these
 * kind of problems.
 */
public class VLayoutManager {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public VLayoutManager(VAbstractBlockLayout layout) {
    this.layout = layout;
    handler = new ConstraintsHandler();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the widget in the given layout cell.
   * @param widget The widget to be set.
   * @param row The cell row.
   * @param column The Cell column.
   * @param colSpan The column span width
   * @param rowSpan The row span width.
   */
  protected void setWidget(Widget widget, ComponentConstraint c, int colSpan, int rowSpan) {
    ConstraintWrapper		constraint;
    
    constraint = new ConstraintWrapper(c, colSpan, rowSpan);
    constraint.setWidget(widget);
    handler.add(constraint);
  }
  
  /**
   * Layout the container.
   */
  protected void layout() {
    handler.handleColSpan();
    handler.layout(layout);
  }

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * A widget constraint wrapper 
   */
  public static class ConstraintWrapper implements IsSerializable {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    public ConstraintWrapper(ComponentConstraint constraint,
	                     int colSpan,
	                     int rowSpan)
    {
      this.constraint = constraint;
      this.column = constraint.x;
      this.row = constraint.y;
      this.colSpan = colSpan;
      this.rowSpan = rowSpan;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ConstraintWrapper) {
	ConstraintWrapper w = (ConstraintWrapper)obj;
	
	return column == w.column
	  && row == w.row
	  && colSpan == w.colSpan
	  && rowSpan == w.rowSpan;
      }
      
      return false;
    }
    
    @Override
    public int hashCode() {
      return column + row + colSpan + rowSpan;
    }
    
    /**
     * Sets the constraint widget.
     * @param widget The widget.
     */
    public void setWidget(Widget widget) {
      this.widget = widget;
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    protected final ComponentConstraint	constraint;
    public int 				column;
    public int 				row;
    public int 				colSpan;
    public int 				rowSpan;
    public Widget 			widget;
  }
  
  /**
   * A widget constraints wrapper 
   */
  public static class ConstraintsHandler {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    public ConstraintsHandler() {
      constraints = new ArrayList<ConstraintWrapper>();
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    /**
     * Adds a constraint to the buffer.
     * @param constraint The constraint to be buffered.
     */
    public void add(ConstraintWrapper constraint) {
      constraints.add(constraint);
    }
    
    /**
     * Handles the column span problems
     */
    protected void handleColSpan() {
      for (ConstraintWrapper c : constraints) {
	// look for constraints that have column span
	if (c.colSpan > 1) {
	  // correct the position of all widgets beside the spanned widget.
	  // aligns[x][y].x - (colSpan == 0 ? 1 : colSpan) + 1;
	  List<ConstraintWrapper>	constraints;
	  
	  constraints = getBesideConstraints(c.row, c.column);
	  for (ConstraintWrapper constraint : constraints) {
	    constraint.column = constraint.column - c.colSpan + 1;
	    constraint.constraint.x = constraint.column;
	  }
	}
      }
    }
    
    /**
     * Returns the constraints beside the given row and column.
     * @param row The constraint row.
     * @param col The starting column.
     * @return The list of the constraint beside the starting column.
     */
    protected List<ConstraintWrapper> getBesideConstraints(int row, int col) {
      List<ConstraintWrapper>		constraints;
      
      constraints = new ArrayList<ConstraintWrapper>();
      for (ConstraintWrapper c : this.constraints) {
	if (c.row == row && c.column > col) {
	  constraints.add(c);
	}
      }
      
      return constraints;
    }
    
    /**
     * Returns the constraints bottom the given row and column.
     * @param row The constraint row.
     * @param col The starting column.
     * @return The list of the constraint beside the starting column.
     */
    protected List<ConstraintWrapper> getBottomConstraints(int row, int col) {
      List<ConstraintWrapper>		constraints;
      
      constraints = new ArrayList<ConstraintWrapper>();
      for (ConstraintWrapper c : this.constraints) {
	if (c.row > row && c.row <= (row + c.rowSpan) && c.column > col) {
	  constraints.add(c);
	}
      }
      
      return constraints;
    }
    
    /**
     * Layouts the container.
     * @param container The container to be filled.
     */
    public void layout(VAbstractBlockLayout container) {
      for (ConstraintWrapper c : constraints) {
	container.setWidget(c.widget, c.column, c.row, c.colSpan, c.rowSpan);
      }
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final List<ConstraintWrapper>	constraints;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VAbstractBlockLayout			layout;
  private final ConstraintsHandler			handler;
}
