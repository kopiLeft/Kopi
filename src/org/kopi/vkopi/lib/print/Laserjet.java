/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.print;
import org.kopi.vkopi.lib.util.Filter;

/**
 *
 */
public class Laserjet extends LabelPrinter {

  /**
   *
   */

  public Laserjet() {
    super();

    //    RESET	= "@ \033(s1P \033(0N \033(s15H \033(s12V";

    RESET = "@(12U\033(s10J\033(s1p9v0s0bs4148T\033&24D";

    FORMFEED	= "";
    PAGELENGTH	= "";
    PAGELENGTHIN = "";
  }

  public Laserjet(Filter filter) {
    this();
    this.filter = filter;
  }

  public Laserjet(Filter filter, int x,  int y) {
    this();
    this.filter	 = filter;
    setOffset(x, y);
  }

  // ----------------------------------------------------------------------
  // PRINTER SPECIFIC ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Sets the horizontal Position in dots
   */

  public void setHorizontalPos(int pos) {
    lastX = pos;
    // pos = (int)(2.3704 * (double) pos); // switch from dots to mm
    printRawString("&a" + pos + "H");
  }

  /**
   * Sets the vertikal Position
   */

  public void setVerticalPos(int pos) {
    lastY = pos;
    // pos = (int)(14.0741 * (double) pos); // switch to mm
    printRawString("&a" + pos + "V");
  }

}
