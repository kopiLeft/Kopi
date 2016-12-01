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

package org.kopi.util.mailer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class Attachment implements DataSource {

  /**
   * Creates an attachment object.
   *
   * @param     name            the file name given to the attachment.
   * @param     type            the content type of the attachment.
   * @param     input           the input stream delivering the content
   */
  public Attachment(String name, String type, InputStream input) {
    this.name = name;
    this.type = type == null ? DEFAULT_TYPE : type;
    this.input = input;
  }

  /**
   * Creates an attachment object.
   */
  public Attachment(File file) throws FileNotFoundException {
    this(file.getName(), null, new FileInputStream(file));
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public InputStream getStream() {
    return this.input;
  }

  // ----------------------------------------------------------------------
  // interface DataSource
  // ----------------------------------------------------------------------

  public String getName() {
    return this.name;
  }

  public String getContentType() {
    // graf 20060108: javax.activation.FileDataSouce verwenden?
    return this.type;
  }

  /**
   * Return an InputStream for the data.
   * !!! Note - a new stream must be returned each time.
   */
  public InputStream getInputStream() throws IOException {
    return getStream();
  }

  public OutputStream getOutputStream() throws IOException {
    throw new IOException("cannot do this");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private static final String           DEFAULT_TYPE= "application/octet-stream";

  private final String                  name;
  private final String                  type;
  private final InputStream             input;
}
