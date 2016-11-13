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

import org.kopi.vkopi.lib.ui.vaadin.addons.ChartBlockLayout;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The chart block layout connector.
 */
@SuppressWarnings("serial")
@Connect(value = ChartBlockLayout.class, loadStyle = LoadStyle.DEFERRED)
public class ChartBlockLayoutConnector extends AbstractBlockLayoutConnector {

  @Override
  public VChartBlockLayout getWidget() {
    return (VChartBlockLayout) super.getWidget();
  }

  @Override
  protected void handleHierarchyChange() {
    for (ComponentConnector componentConnector : getChildComponents()) {
      if (componentConnector != null) {
	ComponentConstraint			constraints;
	
	constraints = getConstraint(componentConnector);
	if (constraints != null) {
	  getWidget().add(componentConnector.getWidget(), constraints);
	}
      }
    }
  }
  
  @OnStateChange("hasScroll")
  /*package*/ void setScrollable() {
    // set the connection in order to show scroll bar.
    if (getState().hasScroll) {
      getWidget().createScrollBar(getConnection());
    }
  }

  @Override
  protected void initSize() {
    getWidget().initSize(getState().columns, getState().rows);
  }
  
  @Override
  public ChartBlockLayoutState getState() {
    return (ChartBlockLayoutState) super.getState();
  }
}
