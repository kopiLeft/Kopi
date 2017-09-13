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

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.communication.ServerRpc;

/**
 * Server to client communication for rich text field. 
 */
public interface RichTextFieldNavigationServerRpc extends ServerRpc {

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
}
