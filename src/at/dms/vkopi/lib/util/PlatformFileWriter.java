/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: PlatformFileWriter.java,v 1.2 2004/08/25 09:32:38 lackner Exp $
 */

package at.dms.vkopi.lib.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;

/**
 * An class that simplifies file writing
 */
public class PlatformFileWriter {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Sets the number of copy to print
   */
  public PlatformFileWriter(File file,
                            String encoding,
                            String lineSeparator)
    throws IOException
  {
    this.encoding = encoding;
    this.lineSeparator = lineSeparator;
    this.dataStream = new FileOutputStream(file);
  }

  /**
   * Sets the number of copy to print
   */
  public PlatformFileWriter(OutputStream stream,
                            String encoding,
                            String lineSeparator)
  {
    this.encoding = encoding;
    this.lineSeparator = lineSeparator;
    this.dataStream = stream;
  }

  /**
   * Sets the number of copy to print
   */
  public PlatformFileWriter(String fileName,
                            String encoding, 
                            String lineSeparator)
    throws IOException
  {
    this(new File(fileName), encoding, lineSeparator);
  }


  /**
   * Writes a string to the file
   */
  public void write(String string) throws IOException {
    if (string != null) {
      dataStream.write(string.getBytes(encoding));
    }
  }

  /**
   * Write a newline character
   */
  public final void nl() throws IOException {
    write(lineSeparator);
  }

  /**
   * Writes a string and a newline character into the file 
   */
  public void writeln(String string) throws IOException {
    write(string);
    nl();
  }

  // ----------------------------------------------------------------------
  // CLOSE
  // ----------------------------------------------------------------------

  /**
   * Close the file
   */
  public void close() throws IOException {
    dataStream.flush();
    dataStream.close();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String                  encoding;
  private final String                  lineSeparator;
  private final OutputStream            dataStream;
}
