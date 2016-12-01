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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.AutocompleteSuggestion;

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.communication.ServerRpc;

/**
 * The field server RPC used to communicate between
 * client side component and server component.
 */
public interface TextFieldServerRpc extends ServerRpc {

  /**
   * Performs a request to go to the next field.
   */
  @NoLoadingIndicator
  public void gotoNextField();

  /**
   * Performs a request to go to the previous field.
   */
  @NoLoadingIndicator
  public void gotoPrevField();

  /**
   * Performs a request to go to the next block.
   */
  @NoLoadingIndicator
  public void gotoNextBlock();

  /**
   * Performs a request to go to the previous record.
   */
  @NoLoadingIndicator
  public void gotoPrevRecord();

  /**
   * Performs a request to go to the next record.
   */
  @NoLoadingIndicator
  public void gotoNextRecord();

  /**
   * Performs a request to go to the first record.
   */
  @NoLoadingIndicator
  public void gotoFirstRecord();

  /**
   * Performs a request to go to the last field.
   */
  @NoLoadingIndicator
  public void gotoLastRecord();

  /**
   * Performs a request to go to the next empty must fill field field.
   */
  @NoLoadingIndicator
  public void gotoNextEmptyMustfill();

  /**
   * Performs a request to go to close the window.
   */
  @NoLoadingIndicator
  public void closeWindow();

  /**
   * Performs a request to go to print the form.
   */
  @NoLoadingIndicator
  public void printForm();

  /**
   * Performs a request to go to the previous entry.
   */
  @NoLoadingIndicator
  public void previousEntry();

  /**
   * Performs a request to go to the next entry.
   */
  @NoLoadingIndicator
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
}
