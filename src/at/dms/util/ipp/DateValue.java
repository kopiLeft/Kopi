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

public class DateValue extends IPPValue {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public DateValue(byte[] value) {
    this.value = value;
  }

  public DateValue(IPPInputStream is) throws IOException {
    is.readShort();                     //value-length
    value = new byte[11];
    for (int i = 0; i < 11; ++i) {
      value[i] = is.readByte();
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public int getSize() {
    return 2 + 11;  // value-length + value
  }

  public void write(IPPOutputStream os) throws IOException {
    os.writeShort(11);
    for (int i = 0; i < 11; ++i) {
      os.writeByte(value[i]);
    }
  }

  public void dump() {
    System.out.print("\tdate : ");
    for (int i = 0; i < 11; ++i) {
      System.out.print(value[i]);
    }
    System.out.println("");
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private byte[]        value;
}
