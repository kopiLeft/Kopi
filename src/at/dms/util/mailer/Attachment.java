/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.util.mailer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.DataSource;

public class Attachment implements DataSource {

  /**
   * Creates an attachment object.
   */
  public Attachment(File file) throws FileNotFoundException {
    this(file, file.getName());
  }

  /**
   * Creates an attachment object.
   */
  public Attachment(File file, String name)
    throws FileNotFoundException
  {
    this(new FileInputStream(file), name);
  }

  /**
   * Creates an attachment object.
   */
  public Attachment(InputStream input, String name) {
    this.input = input;
    this.name = name;
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

  public String getContentType() {
    // !!! verbessern oder javax.activation.FileDataSouce verwenden
    return "application/octet-stream";
  }

  public String getName() {
    return this.name;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String                  name;
  private final InputStream             input;
}
