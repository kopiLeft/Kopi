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

import java.io.Serializable;

/**
 * The serialized for of an auto complete suggestion.
 */
@SuppressWarnings("serial")
public class AutocompleteSuggestion implements Serializable {
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  public String getDisplayStringAsHTML(int col) {
    if (col == 0) {
      return "<span class=\"notMatch\">" + getNotMatchingLeftPortion(col) + "</span>" +
        "<span class=\"match\">" + getMatchingPortion(col) + "</span>" +
        "<span class=\"notMatch\">" + getNotMatchingRightPortion(col) + "</span>";
    } else {
      return "<span class=\"infoCol\">" + getDisplayString(col) + "</span>";
    }
  }
  
  /**
   * Returns the display string.
   * @return The display string.
   */
  public String getDisplayString(int col) {
    return displayStrings[col];
  }
  
  /**
   * Returns the suggestion ID.
   * @return The suggestion ID.
   */
  public Integer getId() {
    return id;
  }
  
  /**
   * Sets the suggestion ID.
   * @param id The suggestion ID.
   */
  public void setId(Integer id) {
    this.id = id;
  }
  
  /**
   * Sets the display string.
   * @param displayString The display string.
   */
  public void setDisplayStrings(String[] displayStrings) {
    this.displayStrings = displayStrings;
  }
  
  /**
   * Returns the display strings.
   * @return The display strings.
   */
  public String[] getDisplayStrings() {
    return displayStrings;
  }
  
  /**
   * Sets the query string.
   * @param query The query string.
   */
  public void setQuery(String query) {
    this.query = query;
  }
  
  /**
   * Returns the query string.
   * @return the query string.
   */
  public String getQuery() {
    return query;
  }
  
  /**
   * Returns the column count.
   * @return The column count.
   */
  public int getColumnCount() {
    return displayStrings.length;
  }
  
  /**
   * Returns the query string.
   * @return the query string.
   */
  protected String getQueryLowerCase() {
    return query.toLowerCase();
  }
  
  /**
   * Returns the display string.
   * @return The display string.
   */
  protected String getDisplayStringLowerCase(int col) {
    return displayStrings[col].toLowerCase();
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Returns the matching portion of the suggestion.
   * @return The matching portion of the suggestion.
   */
  protected String getMatchingPortion(int col) {
    return displayStrings[col].substring(getDisplayStringLowerCase(col).indexOf(getQueryLowerCase()), getDisplayStringLowerCase(col).indexOf(getQueryLowerCase()) + query.length());
  }
  
  /**
   * Returns the non matching left portion of the suggestion.
   * @return The non matching left portion of the suggestion.
   */
  protected String getNotMatchingLeftPortion(int col) {
    return displayStrings[col].substring(0, getDisplayStringLowerCase(col).indexOf(getQueryLowerCase()));
  }
  
  /**
   * Returns the non matching right portion of the suggestion.
   * @return The non matching right portion of the suggestion.
   */
  protected String getNotMatchingRightPortion(int col) {
    return displayStrings[col].substring(getDisplayStringLowerCase(col).indexOf(getQueryLowerCase()) + query.length(), displayStrings[col].length());
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private String				query;
  private Integer 				id;
  private String[] 				displayStrings;
}
