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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.Map;

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.communication.ServerRpc;

/**
 * The text change client to server communication.
 */
public interface TextChangeServerRpc extends ServerRpc {

  /**
   * Fired when the text content has changed.
   * @param text The text widget text.
   */
  @NoLoadingIndicator
  public void onTextChange(String text);
  
  /**
   * Fired when the text content has changed for the mentioned records in the map key.
   * @param values The field value per record number..
   */
  @NoLoadingIndicator
  public void onDirtyValues(Map<Integer, String> values);
}
