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
 * UploadFailedEvent that indicates that an output stream could not be obtained.
 */
@SuppressWarnings("serial")
public class NoOutputStreamEvent extends UploadFailedEvent {

  /**
   * Creates a new <code>NoOutputStreamEvent</code> instance.
   * @param source The source of the file.
   * @param filename The received file name.
   * @param MIMEType The MIME type of the received file.
   * @param length The length of the received file.
   */
  public NoOutputStreamEvent(Upload source,
                             String filename,
                             String MIMEType,
                             long length)
  {
    super(source, filename, MIMEType, length);
  }
}
