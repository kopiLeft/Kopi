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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.list;

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.communication.ServerRpc;

/**
 * The list dialog server RPC from communication between client and server side.
 */
public interface ListDialogServerRpc extends ServerRpc {

  /**
   * Fired when a row is selected in the list dialog.
   * @param row The selected model row.
   * @param escaped Is it an escape ?
   * @param newForm Should we do a new form ?
   */
  @NoLoadingIndicator
  public void onSelection(int row, boolean escaped, boolean newForm);
}
