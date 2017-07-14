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

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

/**
 * Server to client side communication for upload progress bar. 
 */
public interface UploadProgressClientRpc extends ClientRpc {
  
  /**
   * Fires a progress event on the upload progress bar.
   * @param contentLength The content length.
   * @param receivedBytes The number of received bytes
   */
  @NoLayout
  public void onProgress(long contentLength, long receivedBytes);
}
