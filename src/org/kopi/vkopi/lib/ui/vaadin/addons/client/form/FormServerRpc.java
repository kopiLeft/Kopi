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

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.communication.ServerRpc;

/**
 * The form server RPC communication.
 */
public interface FormServerRpc extends ServerRpc {

  /**
   * Fired when a page is selected inside a form.
   * @param page The page index.
   */
  @NoLoadingIndicator
  public void onPageSelection(int page);
  
  /**
   * Requests to go to the next position.
   */
  @NoLoadingIndicator
  public void gotoNextPosition();

  /**
   * Requests to go to the previous position.
   */
  @NoLoadingIndicator
  public void gotoPrevPosition();

  /**
   * Requests to go to the last position.
   */
  @NoLoadingIndicator
  public void gotoLastPosition();

  /**
   * Requests to go to the last position.
   */
  @NoLoadingIndicator
  public void gotoFirstPosition();

  /**
   * Requests to go to the specified position.
   * @param posno The position number.
   */
  @NoLoadingIndicator
  public void gotoPosition(int posno);
}
