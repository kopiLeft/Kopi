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
 * UploadChangeEvent event is sent when the value (filename) of the upload
 * changes.
 */
@SuppressWarnings("serial")
public class UploadChangeEvent extends Component.Event {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>UploadChangeEvent</code> instance.
   * @param source The upload source.
   * @param filename The new file name.
   */
  public UploadChangeEvent(Upload source, String filename) {
    super(source);
    this.filename = filename;
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Uploads where the event occurred.
   *
   * @return the Source of the event.
   */
  @Override
  public Upload getSource() {
    return (Upload) super.getSource();
  }
  
  /**
   * Gets the file name.
   * @return the filename.
   */
  public String getFilename() {
    return filename;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final String 			filename;
}
