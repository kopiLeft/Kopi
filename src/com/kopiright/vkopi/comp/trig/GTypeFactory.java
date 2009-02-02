/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.vkopi.comp.trig;

/**
 * Factory for visual Java Types
 */
public interface GTypeFactory extends com.kopiright.xkopi.comp.xkjc.XTypeFactory {

  int           MIN_TYPE_ID             = (com.kopiright.xkopi.comp.xkjc.XTypeFactory.MAX_TYPE_ID + 1);

  int           RFT_FORM                = (MIN_TYPE_ID + 0);
  int           RFT_BLOCK               = (MIN_TYPE_ID + 1);
  int           RFT_FIELD               = (MIN_TYPE_ID + 2);
  int           RFT_BOOLEANFIELD        = (MIN_TYPE_ID + 3);
  int           RFT_INTEGERFIELD        = (MIN_TYPE_ID + 4);
  int           RFT_FIXEDFIELD          = (MIN_TYPE_ID + 5);
  int           RFT_STRINGFIELD         = (MIN_TYPE_ID + 6);
  int           RFT_IMAGEFIELD          = (MIN_TYPE_ID + 7);
  int           RFT_COLORFIELD          = (MIN_TYPE_ID + 8);
  int           RFT_DATEFIELD           = (MIN_TYPE_ID + 9);
  int           RFT_MONTHFIELD          = (MIN_TYPE_ID + 10);
  int           RFT_TIMEFIELD           = (MIN_TYPE_ID + 11);
  int           RFT_WEEKFIELD           = (MIN_TYPE_ID + 12);
  int           RFT_BOOLEANCODEFIELD    = (MIN_TYPE_ID + 13);
  int           RFT_FIXEDCODEFIELD      = (MIN_TYPE_ID + 14);
  int           RFT_INTEGERCODEFIELD    = (MIN_TYPE_ID + 15);
  int           RFT_STRINGCODEFIELD     = (MIN_TYPE_ID + 16);
  int           RFT_TEXTFIELD           = (MIN_TYPE_ID + 17);

  int           RFT_STRINGCOLUMN        = (MIN_TYPE_ID + 18);
  int           RFT_INTEGERCOLUMN       = (MIN_TYPE_ID + 19);
  int           RFT_FIXEDCOLUMN         = (MIN_TYPE_ID + 20);
  int           RFT_BOOLEANCOLUMN       = (MIN_TYPE_ID + 21);
  int           RFT_DATECOLUMN          = (MIN_TYPE_ID + 22);
  int           RFT_MONTHCOLUMN         = (MIN_TYPE_ID + 23);
  int           RFT_TIMECOLUMN          = (MIN_TYPE_ID + 24);
  int           RFT_WEEKCOLUMN          = (MIN_TYPE_ID + 25);
  int           RFT_BOOLEANCODECOLUMN   = (MIN_TYPE_ID + 26);
  int           RFT_FIXEDCODECOLUMN     = (MIN_TYPE_ID + 27);
  int           RFT_INTEGERCODECOLUMN   = (MIN_TYPE_ID + 28);
  int           RFT_STRINGCODECOLUMN    = (MIN_TYPE_ID + 29);

  int           RFT_COLOR               = (MIN_TYPE_ID + 30);
  int           RFT_IMAGE               = (MIN_TYPE_ID + 31);

  int           MAX_TYPE_ID             = RFT_IMAGE;
}
