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

import java.io.IOException;

public class StringValue extends IPPValue {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public StringValue(String value) {
    this.value = value;
  }

  public StringValue(IPPInputStream is) throws IOException {
    int         n = is.readShort();     //value-length

    value = is.readString(n);           //value
  }


  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public int getSize() {
    return 2 + value.length();  // value-length + value
  }

  public void write(IPPOutputStream os) throws IOException {
    os.writeShort(value.length());
    os.writeString(value);
  }

  public void dump() {
    System.out.println("\tString : " + value);
  }

  public String getValue() {
    return value;
  }

  public String toString() {
    return value;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String        value;
}
