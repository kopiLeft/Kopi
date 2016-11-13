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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion;

import java.util.List;

public class Response extends com.google.gwt.user.client.ui.SuggestOracle.Response {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public Response() {}

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the auto complete suggestions.
   * @param suggestions The auto complete suggestions.
   */
  public void setAutocompleteSuggestions(List<AutocompleteSuggestion> suggestions) {
    this.suggestions = suggestions;
  }
  
  /**
   * Returns the auto complete suggestions.
   * @return The auto complete suggestions.
   */
  public List<AutocompleteSuggestion> getAutocompleteSuggestion() {
    return suggestions;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<AutocompleteSuggestion>		suggestions;
}
