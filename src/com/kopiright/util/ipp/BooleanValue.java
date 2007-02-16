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

public class BooleanValue extends IPPValue {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public BooleanValue(boolean value) {
    this.value = value;
  }

  public BooleanValue(IPPInputStream is) throws IOException {
    is.readShort();                     // value-length
    value = (is.readByte() != 0);       // value
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public int getSize() {
    return 2 + 1;  // value-length + value
  }

  public void write(IPPOutputStream os) throws IOException {
    os.writeShort(1);
    os.writeByte((value) ? 1 : 0);
  }

  public void dump() {
    System.out.println("\tboolean : " + value);
  }

  public String toString() {
    return value + "";
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private boolean       value;
}
