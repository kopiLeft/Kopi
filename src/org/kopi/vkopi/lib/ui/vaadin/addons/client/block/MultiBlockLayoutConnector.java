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

import org.kopi.vkopi.lib.ui.vaadin.addons.MultiBlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.LabelConnector;

import com.vaadin.client.ComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

@SuppressWarnings("serial")
@Connect(value = MultiBlockLayout.class, loadStyle = LoadStyle.LAZY)
public class MultiBlockLayoutConnector extends AbstractBlockLayoutConnector {

  @Override
  public VMultiBlockLayout getWidget() {
    return (VMultiBlockLayout) super.getWidget();
  }

  @Override
  public MultiBlockLayoutState getState() {
    return (MultiBlockLayoutState) super.getState();
  }

  @Override
  protected void handleHierarchyChange() {
    for (ComponentConnector componentConnector : getChildComponents()) {
      if (componentConnector != null && componentConnector instanceof AbstractBlockLayoutConnector) {
	((AbstractBlockLayoutConnector)componentConnector).setHandleHierarchyChange(false); // we do not need to layout components. it will be done in this connector
	if (componentConnector instanceof SimpleBlockLayoutConnector) {
	  handleSimpleBlockLayout((SimpleBlockLayoutConnector) componentConnector);
	} else if (componentConnector instanceof ChartBlockLayoutConnector) {
	  handleChartBlockLayout((ChartBlockLayoutConnector) componentConnector);
	}
      }
    }
  }
  
  /**
   * Handles the simple block layout widget.
   * @param connector The simple layout connector.
   */
  protected void handleSimpleBlockLayout(SimpleBlockLayoutConnector connector) {
    ColumnView          columnView;
    LabelConnector      detailLabel;
    
    columnView = null;
    detailLabel = null;
    getWidget().initDetailSize(connector.getState().columns, connector.getState().rows);
    for (ComponentConnector componentConnector : connector.getChildComponents()) {
      if (componentConnector != null) {
	ComponentConstraint			constraints;
	
	constraints = connector.getState().constrains.get(componentConnector);
	if (constraints != null) {
	  getWidget().addToDetail(componentConnector.getWidget(), constraints);
	  if (componentConnector instanceof LabelConnector) {
	    detailLabel = (LabelConnector) componentConnector;
	  } else if (componentConnector instanceof FieldConnector) {
	    columnView = getBlock().getField(((FieldConnector) componentConnector).getColumnViewIndex());
	    if (columnView != null) {
	      // the column view is already created by the chart layout so we add the detail display field
	      columnView.setDetailLabel(detailLabel);
	      columnView.setDetailDisplay((FieldConnector) componentConnector);
	    } else {
	      // we create the column view and we add it to the block.
	      columnView = new ColumnView(getBlock());
	      columnView.setDetailDisplay((FieldConnector) componentConnector);
	      columnView.setDetailLabel(detailLabel);
	      getBlock().addField(columnView);
	    }
	  }
	}
      }
    }
    // add last column view
    if (columnView != null) {
      getBlock().addField(columnView);
    }
  }
  
  /**
   * Handles the chart block layout widget.
   * @param connector The chart layout connector.
   */
  protected void handleChartBlockLayout(ChartBlockLayoutConnector connector) {
    ColumnView           columnView;
    
    columnView = null;
    getWidget().initChartSize(connector.getState().columns, connector.getState().rows);
    for (ComponentConnector componentConnector : connector.getChildComponents()) {
      if (componentConnector != null) {
	ComponentConstraint			constraints;

	constraints = connector.getState().constrains.get(componentConnector);
	if (constraints != null) {
	  // correction should be done here for column position
	  constraints.x -= 1; 
	  getWidget().add(componentConnector.getWidget(), constraints);
	  if (componentConnector instanceof LabelConnector) {
	    if (columnView != null) {
	      getBlock().addField(columnView);
	    }
	    columnView = new ColumnView(getBlock());
	    columnView.setLabel((LabelConnector) componentConnector);
	  } else if (componentConnector instanceof FieldConnector) {
	    if (columnView != null) {
	      columnView.addField((FieldConnector) componentConnector);
	    }
	  }
	}
      }
    }
    // add last column view
    if (columnView != null) {
      getBlock().addField(columnView);
    }
  }

  @Override
  protected void initSize() {
    // no size initialization
  }
}
