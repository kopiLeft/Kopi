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

import org.kopi.vkopi.lib.ui.vaadin.addons.SimpleBlockLayout;

import com.vaadin.client.ComponentConnector;
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

  @Override
  protected void initSize() {
    getWidget().initSize(getState().columns, getState().rows);
    getWidget().setAlignment(getState().align);
  }
}
