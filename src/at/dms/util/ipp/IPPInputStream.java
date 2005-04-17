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

package at.dms.util.ipp;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import at.dms.util.base.InconsistencyException;

public class IPPInputStream {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public IPPInputStream(InputStream is) {
    this.is = is;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public byte peekByte() throws IOException {
    int         read;

    verify();

    is.mark(1);
    read = read();
    is.reset();
    return (byte) read;
  }

  public short peekShortAfterFirstByte() throws IOException {
    int         i = 0;

    verify();

    is.mark(3);
    read();
    i |= (read() << 8);
    i |= (read());
    is.reset();
    return (short) i;
  }

  public byte readByte() throws IOException {
    return (byte) read();
  }

  public short readShort() throws IOException {
    short       i = 0;

    i |= (read() << 8);
    i |= (read());
    return i;
  }

  public int readInteger() throws IOException {
    int         i = 0;

    i |= (read() << 24);
    i |= (read() << 16);
    i |= (read() << 8);
    i |= (read());
    return i;
  }

  public String readString(int length) throws IOException {
    byte[]      buf = new byte[length];
    int         nread = 0;

    nread = is.read(buf, 0, length);
    if (nread != length) {
      throw new IOException("Error reading socket: unexpected end of transmission");
    }
    return new String(buf);
  }

  public String readLine() throws IOException {
    StringBuffer        sb = new StringBuffer();
    int                 c;
    boolean             end = false;

    while (!end) {
      c = read();
      if (c == -1 || c == '\n') {
        end = true;
      } else if (c != '\r') {
        sb.append((char) c);
      }
    }

    return sb.toString();
  }

  public byte[] readArray() throws IOException {
    byte[]                      buf = new byte[1024];
    ByteArrayOutputStream       outputStream = new ByteArrayOutputStream();
    int                         nread = 0;

    while ((nread = is.read(buf)) > 0) {
      outputStream.write(buf, 0 , nread);
    }

    return outputStream.toByteArray();
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private int read() throws IOException {
    int         c = is.read();

    if (c == -1) {
      throw new IOException("Error reading socket: unexpected end of transmission");
    }
    return c;
  }

  private void verify() {
    if (!is.markSupported()) {
      throw new InconsistencyException("Mark is not supported");
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private InputStream   is;
}
