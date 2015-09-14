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

package com.kopiright.vkopi.lib.chart;

/**
 * Collects some constants for the chart implementation
 */
public interface CConstants extends com.kopiright.vkopi.lib.visual.Constants {

  // ---------------------------------------------------------------------
  // CHART TYPES
  // ---------------------------------------------------------------------
  
  int TYPE_PIE				= 0;
  int TYPE_COLUMN			= 1;
  int TYPE_BAR				= 2;
  int TYPE_LINE				= 3;
  int TYPE_AREA				= 4;
  
  // ---------------------------------------------------------------------
  // EMPTY TEXT
  // ---------------------------------------------------------------------
  
  String EMPTY_TEXT			= "";
  
  // ---------------------------------------------------------------------
  // TRIGGERED EVENTS
  // ---------------------------------------------------------------------

  int TRG_PRECHART			= 0;
  int TRG_POSTCHART			= 1;
  int TRG_CHARTTYPE			= 2;
  int TRG_INIT				= 3;
  int TRG_FORMAT			= 4;
  int TRG_COLOR				= 5;
  int TRG_CMDACCESS			= 6;

  int TRG_VOID				= com.kopiright.vkopi.lib.form.VConstants.TRG_VOID;
  int TRG_OBJECT			= com.kopiright.vkopi.lib.form.VConstants.TRG_OBJECT;
  int TRG_BOOLEAN			= com.kopiright.vkopi.lib.form.VConstants.TRG_BOOLEAN;

  // ---------------------------------------------------------------------
  // PREDEFINED COMMANDS
  // ---------------------------------------------------------------------

  int CMD_QUIT                          = 0;
  int CMD_PRINT                         = 1;
  int CMD_PREVIEW                       = 2;
  int CMD_EXPORT_CSV                    = 3;
  int CMD_EXPORT_XLS                    = 4;
  int CMD_EXPORT_PDF                    = 5;
  int CMD_HELP                          = 6;

  // ---------------------------------------------------------------------
  // TRIGGER INFO
  // ---------------------------------------------------------------------

  String[] TRG_NAMES			= new String[] {
    "TRG_PRECHART",
    "TRG_POSTCHART",
    "TRG_CHARTTYPE",
    "TRG_INIT",
    "TRG_FORMAT",
    "TRG_COLOR",
    "TRG_CMDACCESS"
  };

  int[] TRG_TYPES			= new int[] {
    TRG_VOID,
    TRG_VOID,
    TRG_OBJECT,
    TRG_VOID,
    TRG_OBJECT,
    TRG_OBJECT,
    TRG_BOOLEAN
  };
}
