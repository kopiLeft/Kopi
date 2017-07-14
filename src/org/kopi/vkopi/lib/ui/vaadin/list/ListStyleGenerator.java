/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.list;

import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VListDialog;

import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;

/**
 * Styles generator for list dialog tables. 
 */
@SuppressWarnings("serial")
public class ListStyleGenerator implements CellStyleGenerator {
  
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  public ListStyleGenerator(VListDialog model) {
    this.model = model;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public String getStyle(CellReference cell) {
    switch (model.getColumns()[(Integer)cell.getPropertyId()].getAlign()) {
    case VConstants.ALG_RIGHT:
      return "v-align-right";
    case VConstants.ALG_CENTER:
      return "v-align-center";
    case VConstants.ALG_LEFT:
      return "v-align-left";
    default:
      return "v-align-left";
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VListDialog             model;
}
