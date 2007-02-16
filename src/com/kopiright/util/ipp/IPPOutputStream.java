/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.util.ipp;

import java.io.OutputStream;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.ByteBuffer;

public class IPPOutputStream {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public IPPOutputStream(OutputStream os) {
    this.os = os;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public void writeByte(int b) throws IOException {
    os.write((byte) (b  & 0xff));
  }

  public void writeShort(int s) throws IOException {
    os.write((byte) ((s & 0xff00) >> 8));
    os.write((byte)  (s & 0xff));
  }

  public void writeInteger(int i) throws IOException {
    os.write((byte) ((i & 0xff000000) >> 24));
    os.write((byte) ((i & 0xff0000) >> 16));
    os.write((byte) ((i & 0xff00) >> 8));
    os.write((byte)  (i & 0xff));
  }

  public void writeString(String s) throws IOException {

    Charset charset = Charset.forName("iso-8859-1");

    ByteBuffer byteBuffer = charset.encode(s);

    for (int i = 0; i < s.length(); ++i) {
      os.write((byte) byteBuffer.get(i));
    }
  }

  public void writeArray(byte[] array) throws IOException {
    os.write(array, 0, array.length);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private OutputStream  os;
}
