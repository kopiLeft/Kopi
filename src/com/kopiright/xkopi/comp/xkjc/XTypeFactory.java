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

package com.kopiright.xkopi.comp.xkjc;

/**
 * Factory for extended Java Types
 */
public interface XTypeFactory extends com.kopiright.kopi.comp.kjc.TypeFactory {

  // ----------------------------------------------------------------------
  // STANDARD JAVA WRAPPER TYPES
  // ----------------------------------------------------------------------

  int           MIN_TYPE_ID             = (com.kopiright.kopi.comp.kjc.TypeFactory.MAX_TYPE_ID + 1);

  int           RFT_NUMBER              = (MIN_TYPE_ID + 0);

  int           RFT_BOOLEAN             = (MIN_TYPE_ID + 1);
  int           RFT_BYTE                = (MIN_TYPE_ID + 2);
  int           RFT_CHARACTER           = (MIN_TYPE_ID + 3);
  int           RFT_DOUBLE              = (MIN_TYPE_ID + 4);
  int           RFT_FLOAT               = (MIN_TYPE_ID + 5);
  int           RFT_INTEGER             = (MIN_TYPE_ID + 6);
  int           RFT_LONG                = (MIN_TYPE_ID + 7);
  int           RFT_SHORT               = (MIN_TYPE_ID + 8);

  int           PRM_PDATE               = (MIN_TYPE_ID + 9);
  int           PRM_PMONTH              = (MIN_TYPE_ID + 10);
  int           PRM_PTIME               = (MIN_TYPE_ID + 11);
  int           PRM_PWEEK               = (MIN_TYPE_ID + 12);
  int           PRM_PFIXED              = (MIN_TYPE_ID + 13);
  int           PRM_PTIMESTAMP          = (MIN_TYPE_ID + 14);

  int           RFT_DATE                = (MIN_TYPE_ID + 15);
  int           RFT_MONTH               = (MIN_TYPE_ID + 16);
  int           RFT_TIME                = (MIN_TYPE_ID + 17);
  int           RFT_TIMESTAMP           = (MIN_TYPE_ID + 18);
  int           RFT_WEEK                = (MIN_TYPE_ID + 19);
  int           RFT_FIXED               = (MIN_TYPE_ID + 20);

//   int RFT_COLOR                 = 35;
//   int RFT_IMAGE                 = 36;

  int           RFT_CURSOR              = (MIN_TYPE_ID + 21);
  int           RFT_CONNECTION          = (MIN_TYPE_ID + 22);
  int           RFT_DBCONTEXTHANDLER    = (MIN_TYPE_ID + 23);
  int           RFT_KOPISERIALIZABLE    = (MIN_TYPE_ID + 24);

  int           MAX_TYPE_ID             = RFT_KOPISERIALIZABLE;
}
