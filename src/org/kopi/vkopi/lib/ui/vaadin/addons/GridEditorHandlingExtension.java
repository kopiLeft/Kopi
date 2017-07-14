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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorHandlingExtensionServerRpc;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;

/**
 * The grid editor navigation handling extension 
 */
@SuppressWarnings("serial")
public class GridEditorHandlingExtension extends AbstractExtension {

  public GridEditorHandlingExtension() {
    registerRpc(new EditorHandlingExtensionServerRpc() {
      
      @Override
      public void editRow(String row, String col) {
        if (row != null && col != null) {
          onRowEdit(Integer.parseInt(row), Integer.parseInt(col)); 
        }
      }

      @Override
      public void cancelEditor() {
        onCancelEditor();
      }

      @Override
      public void gotoFirstEmptyRecord() {
        onGotoFirstEmptyRecord();
      }
    });
  }
  
  @Override
  public void extend(AbstractClientConnector target) {
    super.extend(target);
  }

  protected void onRowEdit(int row, int col) {
    // to be redefined
  }
  
  protected void onCancelEditor() {
    // to be redefined
  }
  
  protected void onGotoFirstEmptyRecord() {
    // to be redefined
  }
}
