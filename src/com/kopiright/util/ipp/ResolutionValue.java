/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

public class ResolutionValue extends IPPValue {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public ResolutionValue(byte units, int xres, int yres) {
    this.units = units;
    this.xres = xres;
    this.yres = yres;
  }

  public ResolutionValue(IPPInputStream is) throws IOException {
    is.readShort();             //value-length
    xres = is.readInteger();
    yres = is.readInteger();
    units = is.readByte();
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public int getSize() {
    return 2 + 9;  // value-length + value
  }

  public void write(IPPOutputStream os) throws IOException {
    os.writeShort(9);
    os.writeInteger(xres);
    os.writeInteger(yres);
    os.writeByte(units);
  }

  public void dump() {
    System.out.println("\tunits : " + units +
                       "\txres : " + xres +
                       "\tyres : " + yres);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private byte          units;
  private int           xres;
  private int           yres;
}
