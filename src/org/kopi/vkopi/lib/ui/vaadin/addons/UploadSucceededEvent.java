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

/**
 * UploadSucceededEvent event is sent when the upload is received
 * successfully.
 */
@SuppressWarnings("serial")
public class UploadSucceededEvent extends UploadFinishedEvent {

  /**
   * Creates a new <code>SucceededEvent</code> instance.
   * @param source The source of the file.
   * @param filename The received file name.
   * @param MIMEType The MIME type of the received file.
   * @param length The length of the received file.
   */
  public UploadSucceededEvent(Upload source,
                              String filename,
                              String MIMEType,
                              long length)
  {
    super(source, filename, MIMEType, length);
  }
}
