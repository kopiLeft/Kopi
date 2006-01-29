/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.report;

public interface Constants extends com.kopiright.vkopi.lib.visual.Constants {

  // --------------------------------------------------------------------
  // COLUMN OPTIONS
  // --------------------------------------------------------------------

  int CLO_VISIBLE			= 0;
  int CLO_HIDDEN			= 1;

  // ---------------------------------------------------------------------
  // ALIGNMENT
  // ---------------------------------------------------------------------

  int ALG_DEFAULT			= 0;
  int ALG_LEFT				= 1;
  int ALG_CENTER			= 2;
  int ALG_RIGHT				= 4;

  // ---------------------------------------------------------------------
  // COLOR
  // ---------------------------------------------------------------------

  int	CLR_WHITE			= 0;
  int	CLR_BLACK			= 1;
  int	CLR_RED				= 2;
  int	CLR_GREEN			= 3;
  int	CLR_BLUE			= 4;
  int	CLR_YELLOW			= 5;
  int	CLR_PINK			= 6;
  int	CLR_CYAN			= 7;
  int	CLR_GRAY			= 8;

  // ---------------------------------------------------------------------
  // PRINT OPTIONS
  // ---------------------------------------------------------------------

  int SUM_AT_HEAD			= 1;
  int SUM_AT_TAIL			= 2;

  // ---------------------------------------------------------------------
  // TRIGGERED EVENTS (MAX 32)
  // ---------------------------------------------------------------------

  int TRG_PREREPORT			= 0;
  int TRG_POSTREPORT			= 1;
  int TRG_INIT				= 2;
  int TRG_FORMAT			= 3;
  int TRG_COMPUTE			= 4;

  int TRG_VOID				= com.kopiright.vkopi.lib.form.VConstants.TRG_VOID;
  int TRG_OBJECT			= com.kopiright.vkopi.lib.form.VConstants.TRG_OBJECT;

  // ---------------------------------------------------------------------
  // CELL STATE
  // ---------------------------------------------------------------------

  int STA_SEPARATOR			= -2;
  int STA_FOLDED			= -1;
  int STA_STANDARD			= 0;
  int STA_EMPTY				= 1;
  int STA_NEGATIVE			= 2;
  int STA_NULL				= 3;
  int STA_DEFAULT			= 4;

  // ---------------------------------------------------------------------
  // TRIGGER INFO
  // ---------------------------------------------------------------------

  String[] TRG_NAMES			= new String[] {
    "TRG_PREREPORT", "TRG_POSTREPORT", "TRG_INIT", "TRG_FORMAT", "TRG_COMPUTE"};

  int[] TRG_TYPES			= new int[] {
    TRG_VOID, TRG_VOID, TRG_VOID, TRG_OBJECT, TRG_OBJECT};
}
