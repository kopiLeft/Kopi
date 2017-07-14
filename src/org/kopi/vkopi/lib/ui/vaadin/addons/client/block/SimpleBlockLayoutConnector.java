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

import org.kopi.vkopi.lib.ui.vaadin.addons.SimpleBlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.ActorFieldConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.LabelConnector;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.connectors.GridConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The simple block layout connector.
 */
@SuppressWarnings("serial")
@Connect(value = SimpleBlockLayout.class, loadStyle = LoadStyle.DEFERRED)
public class SimpleBlockLayoutConnector extends AbstractBlockLayoutConnector {
  
  @Override
  public VSimpleBlockLayout getWidget() {
    return (VSimpleBlockLayout) super.getWidget();
  }
  
  @Override
  public SimpleBlockLayoutState getState() {
    return (SimpleBlockLayoutState) super.getState();
  }

  @Override
  protected void handleHierarchyChange() {
    ColumnView           columnView;
    
    columnView = null;
    if (getParent() instanceof GridConnector) {
      getBlock().setLayoutBelongsToGridDetail(true);
    }
    for (ComponentConnector componentConnector : getChildComponents()) {
      if (componentConnector != null) {
	ComponentConstraint			constraints;
	
	constraints = getConstraint(componentConnector);
	if (constraints != null) {
	  getWidget().add(componentConnector.getWidget(), constraints);
	  if (componentConnector instanceof LabelConnector) {
	    if (columnView != null) {
	      getBlock().addField(columnView);
	    }
	    columnView = new ColumnView(getBlock());
	    columnView.setLabel((LabelConnector) componentConnector);
	    if (blockInDetailMode()) {
	      columnView.setDetailLabel((LabelConnector) componentConnector);
	    }
	  } else if (componentConnector instanceof FieldConnector) {
	    // a follow field has no label
	    // an actor field has no label too.
	    // we treat this cases separately
	    if (constraints.width < 0 || ((FieldConnector)componentConnector).getContent() instanceof ActorFieldConnector) {
	      if (columnView != null) {
	        getBlock().addField(columnView);
	      }
              columnView = new ColumnView(getBlock());
              columnView.setLabel(null);
              columnView.addField((FieldConnector) componentConnector);
              if (blockInDetailMode()) {
                columnView.setDetailLabel(null);
                columnView.setDetailDisplay((FieldConnector) componentConnector);
              }
            } else if (columnView != null) {
              columnView.addField((FieldConnector) componentConnector);
              if (blockInDetailMode()) {
                columnView.setDetailDisplay((FieldConnector) componentConnector);
              }
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
    getWidget().initSize(getState().columns, getState().rows);
    getWidget().setAlignment(getState().align);
  }
  
  /**
   * Returns if the block is in detail mode from the block state directly cause hiearchy change
   * event is sent before the state change event and thus it is too early to call
   * {@link BlockConnector#inDetailMode()}
   * @return True if the block is in detail mode.
   */
  protected boolean blockInDetailMode() {
    return getBlock().getState().noChart;
  }
}
