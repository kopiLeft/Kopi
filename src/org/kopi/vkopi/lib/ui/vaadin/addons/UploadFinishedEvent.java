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

import com.vaadin.ui.Component;

/**
 * UploadFinishedEvent is sent when the upload receives a file, regardless
 * of whether the reception was successful or failed. If you wish to
 * distinguish between the two cases, use either UploadSucceededEvent or
 * UploadFailedEvent, which are both subclasses of the UploadFinishedEvent.
 *
 */
@SuppressWarnings("serial")
public class UploadFinishedEvent extends Component.Event {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>UploadFinishedEvent</code> instance.
   * @param source The source of the file.
   * @param filename The received file name.
   * @param MIMEType The MIME type of the received file.
   * @param length The length of the received file.
   */
  public UploadFinishedEvent(Upload source,
                             String filename,
                             String MIMEType,
                             long length)
  {
    super(source);
    type = MIMEType;
    this.filename = filename;
    this.length = length;
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Uploads where the event occurred.
   * @return the Source of the event.
   */
  public Upload getUpload() {
    return (Upload) getSource();
  }

  /**
   * Gets the file name.
   * @return the filename.
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Gets the MIME Type of the file.
   * @return the MIME type.
   */
  public String getMIMEType() {
    return type;
  }

  /**
   * Gets the length of the file.
   * @return the length.
   */
  public long getLength() {
    return length;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Length of the received file.
   */
  private final long 				length;

  /**
   * MIME type of the received file.
   */
  private final String 				type;

  /**
   * Received file name.
   */
  private final String 				filename;

}
