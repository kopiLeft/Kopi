/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import java.util.LinkedList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An absolute panel widget.
 */
public class VAlignPanel extends AbsolutePanel {
  
  //---------------------------------------------------
  // CCONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VAlignPanel</code> instance.
   * @param align The alignment info
   */
  public VAlignPanel(BlockAlignment align) {
    setStyleName("k-align-pane");
    aligns = new LinkedList<ComponentConstraint>();
    widgets = new LinkedList<Widget>();
    this.align = align;
    getElement().getStyle().setOverflow(Overflow.VISIBLE);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds a constrained widget.
   * @param w The widget.
   * @param align The widget constraint.
   */
  public void addWidget(Widget w, ComponentConstraint align) {
    add(w);
    widgets.add(w);
    aligns.add(align);
  }
  
  /**
   * Layouts the block content.
   */
  protected void layout() {
    if (align == null) {
      return;
    }
    
    VAbstractBlockLayout 	ori = (VAbstractBlockLayout)align.getBlock().getLayout();

    if (ori == null || ori.getCellFormatter() == null) {
      return;
    }

    for (int i = 0; i < aligns.size(); i++) {
      ComponentConstraint	align;

      align = aligns.get(i);
      if (align.x != -1) {
	try {
	  Element		cell = ori.getCellFormatter().getElement(ori.getRowCount() - 1, align.x);

	  if (cell != null) {
	    int		offsetWidth = 0;
	    Widget	overlap = getOverlappingWidget(i, align.x, align.y);
	    
	    if (overlap != null) {
	      offsetWidth = overlap.getElement().getClientWidth() + 10; // horizontal gap
	    }
	    setWidgetPosition(getWidget(i),
		              (cell.getOffsetLeft() + offsetWidth) - (align.x == 0 ? 0 : Math.max(0, getWidget(i).getElement().getClientWidth() - cell.getClientWidth())),
		              align.y * 21); // text fields height is 15px
	  }
	} catch (IndexOutOfBoundsException e) {
	  // hide the widget.
	  getWidget(i).setVisible(false);
	}
      }
    }
  }
  
  /**
   * Returns The overlapping widget if it exists.
   * @param end: The end searching index.
   * @param x The column number.
   * @param y The row number.
   * @return The overlapping widget. {@code null} otherwise.
   */
  protected Widget getOverlappingWidget(int end, int x, int y) {
    for (int i = 0; i < end ; i++) {
      if (aligns.get(i).x == x && aligns.get(i).y == y) {
	return widgets.get(i);
      }
    }
    
    return null;
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
	layout();
	setPanelSize();
      }
    });
  }
  
  /**
   * Calculates the size of the content panel.
   */
  protected void setPanelSize() {
    if (aligns == null) {
      return;
    }
    
    int		width = 0;
    int		height = 0;
    
    for (int i = 0; i < getWidgetCount(); i++) {
      Widget			child;
      ComponentConstraint	align;
      
      child = getWidget(i);
      align = aligns.get(i);
      if (align.x != -1) {
	width = Math.max(width, child.getElement().getAbsoluteRight());
	height = Math.max(height, child.getElement().getOffsetTop() + child.getElement().getClientHeight());
      }
    }
    
    setPixelSize(width, height);
  }
  
  /**
   * Releases this align block.
   */
  public void release() {
    widgets = null;
    aligns = null;
    align = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private LinkedList<Widget>			widgets;
  private LinkedList<ComponentConstraint>	aligns;
  private BlockAlignment 			align;
}
