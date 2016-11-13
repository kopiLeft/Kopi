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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

/**
 * The field client RPC used to communicate between
 * server component and client component.
 */
public interface TextFieldClientRpc extends ClientRpc {

  /**
   * Sets the field focus.
   * @param focus The field focus.
   */
  @NoLayout
  public void setFocus(boolean focused);
  
  /**
   * Sets the field text.
   * @param text The field text.
   */
  @NoLayout
  public void setText(String text);
  
  /**
   * Sets the text field blink state.
   * @param blink The blink state.
   */
  @NoLayout
  public void setBlink(boolean blink);
  
  /**
   * Sets the list of available suggestions.
   * @param suggestions The list of available suggestions.
   */
  @NoLayout
  public void setSuggestions(List<AutocompleteSuggestion> suggestions);
}
