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
 * Upload.StartedEvent event is sent when the upload is started to received.
 */
@SuppressWarnings("serial")
public class UploadStartedEvent extends Component.Event {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>UploadStartedEvent</code> instance.
   * @param source The source of the file.
   * @param filename The received file name.
   * @param MIMEType The MIME type of the received file.
   * @param contentLength The length of the received file.
   */
  public UploadStartedEvent(Upload source,
                            String filename,
                            String MIMEType,
                            long contentLength)
  {
    super(source);
    this.filename = filename;
    type = MIMEType;
    length = contentLength;
  }

  //---------------------------------------------------
  //ACCESSORS
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
   * @return the length of the file that is being uploaded
   */
  public long getContentLength() {
    return length;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final String 			filename;
  private final String 			type;
  
  /**
   * Length of the received file.
   */
  private final long 			length;
}
