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

import java.io.OutputStream;
import java.io.Serializable;

/**
 * Interface that must be implemented by the upload receivers to provide the
 * Upload component an output stream to write the uploaded data.
 */
public interface UploadReceiver extends Serializable {

  /**
   * Invoked when a new upload arrives.
   *
   * @param filename The desired filename of the upload, usually as specified by the client.
   * @param mimeType The MIME type of the uploaded file.
   * @return Stream to which the uploaded file should be written.
   */
  public OutputStream receiveUpload(String filename, String mimeType);
}
