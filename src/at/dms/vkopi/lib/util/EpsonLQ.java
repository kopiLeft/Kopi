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

package at.dms.vkopi.lib.util;

import java.io.IOException;

/**
 *
 */
public class EpsonLQ extends LabelPrinter {

  /**
   *
   */

  public EpsonLQ() {
    super();

    RESET	= "@";
    FORMFEED	= "";
    PAGELENGTH	= "C";
    PAGELENGTHIN = "C0";

    DRAFT	= "x0";
    LETTER	= "x1";
    PT15	= "g";
    SETHPOS	= "$";
    SETVPOS	= "(V ";
    MASTERSELECT	= "!";
  }

  public EpsonLQ(Filter filter) {
    this();
    this.filter = filter;
  }

  public EpsonLQ(Filter filter, int x,  int y) {
    this();
    this.filter	 = filter;
    setOffset(x, y);
  }

  // ----------------------------------------------------------------------
  // PRINTER SPECIFIC ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Sets the horizontal Position in mm
   */

  public void setHorizontalPos(int pos) {
    lastX = pos;
    pos = (int)(2.3704 * (double) pos); // switch from inch to mm
    printRawString(SETHPOS + (char)(pos % 256) + (char)(pos / 256 + 64));
  }

  /**
   * Sets the vertikal Position
   */

  public void setVerticalPos(int pos) {
    lastY = pos;
    pos = (int)(14.0741 * (double) pos); // switch to mm
    printRawString(SETVPOS + (char)(pos % 256) + (char)(pos / 256));
  }

  // ----------------------------------------------------------------------
  // PRINTER SPECIFIC DATA MEMBERS
  // ----------------------------------------------------------------------

  private final char   CPI10		= 0;
  private final char   CPI12		= 1;
  private final char   PROPORTIONAL	= 2;
  private final char   SMALL		= 4;
  private final char   BOLD		= 8;
  private final char   DOUBLE		= 16;
  private final char   ITALIC		= 32;
  private final char   UNDERLINE	= 64;
}
