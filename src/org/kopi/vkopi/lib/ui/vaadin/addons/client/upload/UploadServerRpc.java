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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.upload;

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.communication.ServerRpc;

/**
 * The upload server RPC for communication between client and server side.
 */
public interface UploadServerRpc extends ServerRpc {

  /**
   * Event sent when the file name of the upload component is changed.
   * @param filename The filename
   */
  @NoLoadingIndicator
  public void change(String filename);
  
  /**
   * Fired when the upload is cancelled by the user.
   */
  @NoLoadingIndicator
  public void cancel();
}
