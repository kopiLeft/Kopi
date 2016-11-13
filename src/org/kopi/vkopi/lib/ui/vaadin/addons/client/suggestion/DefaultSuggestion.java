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

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class DefaultSuggestion implements Suggestion {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public DefaultSuggestion(AutocompleteSuggestion wrappedSuggestion, int column) {
    this.wrappedSuggestion = wrappedSuggestion;
    this.column = column;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public String getDisplayString() {
    return wrappedSuggestion.getDisplayStringAsHTML(column);
  }

  @Override
  public String getReplacementString() {
    return wrappedSuggestion.getDisplayString(column);
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  public AutocompleteSuggestion getWrappedSuggestion() {
    return wrappedSuggestion;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final AutocompleteSuggestion 		wrappedSuggestion;
  private final int				column;
}
