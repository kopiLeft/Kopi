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

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

/**
 * The block Client RPC for communication between server and client side.
 */
public interface BlockClientRpc extends ClientRpc {

  /**
   * Switches the block view from chart to detail or the inverse.
   * @param detail Should we switch to detail view ? Otherwise, it is the chart view.
   */
  @NoLayout
  public void switchView(boolean detail);
  
  /**
   * Updates block scroll details.
   * @param pageSize The page size.
   * @param maxValue The max scroll position.
   * @param enable should we enable scroll bar ?
   * @param value The scroll position.
   */
  @NoLayout
  public void updateScroll(int pageSize, int maxValue, boolean enable, int value);
}
