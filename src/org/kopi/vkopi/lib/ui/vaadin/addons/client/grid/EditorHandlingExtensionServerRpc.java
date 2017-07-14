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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import com.vaadin.shared.communication.ServerRpc;

public interface EditorHandlingExtensionServerRpc extends ServerRpc {

  /**
   * Fired when a row edit request is done by clicking on a row.
   * @param row The row index.
   * @param col The column index.
   */
  void editRow(String row, String col);
  
  /**
   * Fired when the server side should cancel grid editing
   */
  void cancelEditor();
  
  /**
   * Performs a request to go to the first empty record
   */
  void gotoFirstEmptyRecord();
}
