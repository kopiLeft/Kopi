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
 * $Id: RangeValue.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.util.ipp;

import java.io.IOException;

public class RangeValue extends IPPValue {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public RangeValue(int lower, int upper) {
    this.lower = lower;
    this.upper = upper;
  }

  public RangeValue(IPPInputStream is) throws IOException {
    is.readShort();             //value-length
    lower = is.readInteger();
    upper = is.readInteger();
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public int getSize() {
    return 2 + 8;  // value-length + value
  }

  public void write(IPPOutputStream os) throws IOException {
    os.writeShort(8);
    os.writeInteger(lower);
    os.writeInteger(upper);
  }

  public void dump() {
    System.out.println("\tlower : " + lower + "\tupper : " + upper);
  }

  public String toString() {
    return "<" + lower + ", " + upper + ">";
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int           lower;
  private int           upper;
}
