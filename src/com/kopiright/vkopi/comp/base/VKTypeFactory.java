/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.base;

/**
 * Factory for extended Java Types
 */
public interface VKTypeFactory extends com.kopiright.vkopi.comp.trig.GTypeFactory {

  int           MIN_TYPE_ID             = (com.kopiright.vkopi.comp.trig.GTypeFactory.MAX_TYPE_ID + 1);

  int           RFT_VDICTIONARYFORM     = (MIN_TYPE_ID + 0);
  int           RFT_VREPORT             = (MIN_TYPE_ID + 1);
  int           RFT_VFORM               = (MIN_TYPE_ID + 2);
  int           RFT_VBLOCK              = (MIN_TYPE_ID + 3);
  int           RFT_DBLOCK              = (MIN_TYPE_ID + 4);
  int           RFT_DMULTIBLOCK         = (MIN_TYPE_ID + 5);
  int           RFT_VFIELD              = (MIN_TYPE_ID + 6);
  int           RFT_VREPORTCOLUMN       = (MIN_TYPE_ID + 7);

  int           RFT_SACTOR              = (MIN_TYPE_ID + 8);
  int           RFT_SDEFAULTACTOR       = (MIN_TYPE_ID + 9);
  int           RFT_VLIST               = (MIN_TYPE_ID + 10);
  int           RFT_VCOLUMN             = (MIN_TYPE_ID + 11);
  int           RFT_VPOSITION           = (MIN_TYPE_ID + 12);
  int           RFT_VCOMMAND            = (MIN_TYPE_ID + 13);

  int           RFT_VEXCEPTION          = (MIN_TYPE_ID + 14);
  int           RFT_VRUNTIMEEXCEPTION   = (MIN_TYPE_ID + 15);

  // ListColumn
  int           RFT_VLISTCOLUMN         = (MIN_TYPE_ID + 16);
  int           RFT_VSTRINGCOLUMN       = (MIN_TYPE_ID + 17);
  int           RFT_VFIXEDCOLUMN        = (MIN_TYPE_ID + 18);
  int           RFT_VINTEGERCOLUMN      = (MIN_TYPE_ID + 19);
  int           RFT_VDATECOLUMN         = (MIN_TYPE_ID + 20);
  int           RFT_VMONTHCOLUMN        = (MIN_TYPE_ID + 21);
  int           RFT_VTIMECOLUMN         = (MIN_TYPE_ID + 22);
  int           RFT_VWEEKCOLUMN         = (MIN_TYPE_ID + 23);
  int           RFT_VTEXTCOLUMN         = (MIN_TYPE_ID + 24);
  int           RFT_VBOOLEANCOLUMN      = (MIN_TYPE_ID + 25);
  int           RFT_VBOOLEANCODECOLUMN  = (MIN_TYPE_ID + 26);
  int           RFT_VFIXEDCODECOLUMN    = (MIN_TYPE_ID + 27);
  int           RFT_VINTEGERCODECOLUMN  = (MIN_TYPE_ID + 28);
  int           RFT_VSTRINGCODECOLUMN   = (MIN_TYPE_ID + 29);
  int           RFT_VCOLORCOLUMN        = (MIN_TYPE_ID + 30);
  int           RFT_VIMAGECOLUMN        = (MIN_TYPE_ID + 31);

  int           MAX_TYPE_ID             = RFT_VIMAGECOLUMN;
}
