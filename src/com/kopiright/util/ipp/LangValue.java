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

import java.io.IOException;

public class LangValue extends IPPValue {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public LangValue(String charset, String value) {
    this.charset = charset;
    this.value = value;
  }

  public LangValue(IPPInputStream is) throws IOException {
    int         n;

    is.readShort();             //value-length
    n = is.readShort();
    charset = is.readString(n);
    n = is.readShort();
    value = is.readString(n);
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public int getSize() {
    return 6 + charset.length() + value.length();
  }

  public void write(IPPOutputStream os) throws IOException {
    os.writeShort(4 + value.length() + charset.length());
    os.writeShort(charset.length());
    os.writeString(charset);
    os.writeShort(value.length());
    os.writeString(value);
  }

  public void dump() {
    System.out.println("\tcharset : " + charset + "\tvalue : " + value);
  }

  public String toString() {
    return value;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String        charset;
  private String        value;
}
