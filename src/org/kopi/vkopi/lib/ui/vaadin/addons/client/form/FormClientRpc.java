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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.form;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

/**
 * The form client RPC communication.
 */
public interface FormClientRpc extends ClientRpc {

  /**
   * Selects the given page.
   * @param page The page index.
   */
  public void selectPage(int page);
  
  /**
   * Sets the given page enabled or disabled.
   * @param enabled The ability state.
   * @param page The page index
   */
  public void setEnabled(boolean enabled, int page);
  
  /**
   * Shows the position panel.
   * @param first The first record limit.
   * @param last The last record limit.
   */
  @NoLayout
  public void setPosition(int current, int total);
}
