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

import at.dms.util.base.InconsistencyException;

public class PPaperType {

  public PPaperType(int width, int height, String name, int code) {
    this.width	= width;
    this.height = height;
    this.name	= name;
    this.code	= code;
  }

  /**
   * Returns the width of the paper in points
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the height of the paper in points
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns the name of the paper type
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the code of the paper type
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns a String representation of the object
   */
  public String toString() {
    return name;
  }

  /**
   * Conversions
   */
  public static PPaperType getPaperTypeFromCode(int code) {
    switch (code) {
    case 1:
      return PPT_LETTER;
    case 2:
      return PPT_TABLOID;
    case 3:
      return PPT_LEDGER;
    case 4:
      return PPT_LEGAL;
    case 5:
      return PPT_STATEMENT;
    case 6:
      return PPT_EXECUTIVE;
    case 7:
      return PPT_A3;
    case 8:
      return PPT_A4;
    case 9:
      return PPT_A5;
    case 10:
      return PPT_B4;
    case 11:
      return PPT_B5;
    case 12:
      return PPT_FOLIO;
    case 13:
      return PPT_QUARTO;
    case 14:
      return PPT_10X14;
    }
    throw new InconsistencyException("Undefined paper");
  }

  // --------------------------------------------------------------------
  // PREDEFINED PAPER TYPES
  // --------------------------------------------------------------------

  public static final PPaperType PPT_LETTER	= new PPaperType(612,	792,	"Letter",	1);
  public static final PPaperType PPT_TABLOID	= new PPaperType(792,	1224,	"Tabloid",	2);
  public static final PPaperType PPT_LEDGER	= new PPaperType(1224,	792,	"Ledger",	3);
  public static final PPaperType PPT_LEGAL	= new PPaperType(612,	1008,	"Legal",	4);
  public static final PPaperType PPT_STATEMENT	= new PPaperType(396,	612,	"Statement",	5);
  public static final PPaperType PPT_EXECUTIVE	= new PPaperType(540,	720,	"Executive",	6);
  public static final PPaperType PPT_A3		= new PPaperType(842,	1190,	"A3",		7);
  public static final PPaperType PPT_A4		= new PPaperType(595,	842,	"A4",		8);
  public static final PPaperType PPT_A5		= new PPaperType(420,	595,	"A5",		9);
  public static final PPaperType PPT_B4		= new PPaperType(729,	1032,	"B4",		10);
  public static final PPaperType PPT_B5		= new PPaperType(516,	729,	"B5",		11);
  public static final PPaperType PPT_FOLIO	= new PPaperType(612,	936,	"Folio",	12);
  public static final PPaperType PPT_QUARTO	= new PPaperType(610,	780,	"Quarto",	13);
  public static final PPaperType PPT_10X14	= new PPaperType(720,	1008,	"10x14",	14);

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int		width;		// width of the paper in points
  private int		height;		// height of the paper in points
  private String	name;		// name of the paper type
  private int		code;		// code of the type, must match with the printOption code
}
