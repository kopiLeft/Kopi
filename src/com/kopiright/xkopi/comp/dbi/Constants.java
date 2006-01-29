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

package com.kopiright.xkopi.comp.dbi;

/**
 * Defines disassembler constants
 */
public interface Constants {

  // ----------------------------------------------------------------------
  // OPTIONS
  // ----------------------------------------------------------------------

  int OPT_SIMULATE		= (1 << 0);
  int OPT_STDOUT		= (1 << 1);
  int OPT_STDIN			= (1 << 2);
  int OPT_BEAUTIFY		= (1 << 3);
  int OPT_INTERFACE		= (1 << 4);

  // ----------------------------------------------------------------------
  // TYPES
  // ----------------------------------------------------------------------

  int TYP_BOOL		= 0;
  int TYP_STRING	= 1;
  int TYP_INT		= 2;
  int TYP_SHORT		= 3;
  int TYP_BYTE		= 4;
  int TYP_FIXED		= 5;
  int TYP_DATE		= 6;
  int TYP_MONTH		= 7;
  int TYP_TIME		= 8;
  int TYP_TIMESTAMP	= 9;
  int TYP_BLOB		= 10;
  int TYP_ENUM		= 11;
  int TYP_CODE		= 12;
  int TYP_TEXT		= 13;
  int TYP_COLOR		= 14;
  int TYP_IMAGE		= 15;
  int TYP_WEEK		= 16;
  int TYP_ERREUR	= -1;
}
