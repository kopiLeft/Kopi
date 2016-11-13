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
 * UploadFailedEvent event is sent when the upload is received, but the
 * reception is interrupted for some reason.
 */
@SuppressWarnings("serial")
public class UploadFailedEvent extends UploadFinishedEvent {
  
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new <code>UploadFailedEvent</code> instance.
   * @param source The source of the file.
   * @param filename The received file name.
   * @param MIMEType The MIME type of the received file.
   * @param length The length of the received file.
   * @param exception The failure reason.
   */
  public UploadFailedEvent(Upload source,
                           String filename,
                           String MIMEType,
                           long length,
                           Exception reason)
  {
    super(source, filename, MIMEType, length);
    this.reason = reason;
  }

  /**
   * Creates a new <code>UploadFailedEvent</code> instance.
   * @param source The source of the file.
   * @param filename The received file name.
   * @param MIMEType The MIME type of the received file.
   * @param length The length of the received file.
   */
  public UploadFailedEvent(Upload source,
                           String filename,
                           String MIMEType,
                           long length)
  {
    this(source, filename, MIMEType, length, null);
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Gets the exception that caused the failure.
   * @return the exception that caused the failure, null if n/a
   */
  public Exception getReason() {
    return reason;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Exception 				reason;
}
