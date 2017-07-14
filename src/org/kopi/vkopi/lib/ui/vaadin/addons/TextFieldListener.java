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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.io.Serializable;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;

/**
 * A text field listener to be registered on a text field
 * for communication handler between client and server side.
 */
public interface TextFieldListener extends Serializable {

  /**
   * Performs a request to go to the next field.
   */
  public void gotoNextField();

  /**
   * Performs a request to go to the previous field.
   */
  public void gotoPrevField();

  /**
   * Performs a request to go to the next block.
   */
  public void gotoNextBlock();

  /**
   * Performs a request to go to the previous record.
   */
  public void gotoPrevRecord();

  /**
   * Performs a request to go to the next record.
   */
  public void gotoNextRecord();

  /**
   * Performs a request to go to the first record.
   */
  public void gotoFirstRecord();

  /**
   * Performs a request to go to the last field.
   */
  public void gotoLastRecord();

  /**
   * Performs a request to go to the next empty must fill field field.
   */
  public void gotoNextEmptyMustfill();

  /**
   * Performs a request to go to close the window.
   */
  public void closeWindow();

  /**
   * Performs a request to go to print the form.
   */
  public void printForm();

  /**
   * Performs a request to go to the previous entry.
   */
  public void previousEntry();

  /**
   * Performs a request to go to the next entry.
   */
  public void nextEntry();
  
  
  /**
   * Fired when query is being performed.
   * @param query The query to be performed.
   */
  public void onQuery(String query);
  
  /**
   * Fired when suggestion is selected.
   * @param suggestion The selected suggestion.
   */
  public void onSuggestion(AutocompleteSuggestion suggestion);
  
  /**
   * Fires an autofill action for thie attached field.
   */
  public void autofill();
}
