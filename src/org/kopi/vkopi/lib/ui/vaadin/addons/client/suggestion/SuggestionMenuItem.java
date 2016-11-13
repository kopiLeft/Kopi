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

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * Class for menu items in a SuggestionMenu. A SuggestionMenuItem differs from
 * a MenuItem in that each item is backed by a Suggestion object. The text of
 * each menu item is derived from the display string of a Suggestion object,
 * and each item stores a reference to its Suggestion object.
 */
public class SuggestionMenuItem extends MenuItem {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public SuggestionMenuItem(Suggestion suggestion, boolean asHTML) {
    super(suggestion.getDisplayString(), asHTML, (MenuBar)null);
    // Each suggestion should be placed in a single row in the suggestion
    // menu. If the window is resized and the suggestion cannot fit on a
    // single row, it should be clipped (instead of wrapping around and
    // taking up a second row).
    getElement().getStyle().setProperty("whiteSpace", "nowrap");
    setStyleName(STYLENAME_DEFAULT);
    setSuggestion(suggestion);
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the suggestion.
   * @return The suggestion.
   */
  public Suggestion getSuggestion() {
    return suggestion;
  }

  /**
   * Sets the suggestion.
   * @param suggestion The suggestion.
   */
  public void setSuggestion(Suggestion suggestion) {
    this.suggestion = suggestion;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static final String 		STYLENAME_DEFAULT = "item";
  private Suggestion 			suggestion;
}
