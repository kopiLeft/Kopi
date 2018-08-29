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

package org.kopi.vkopi.lib.visual;

public interface Constants {


  // --------------------------------------------------------------------
  // MODEL TYPES
  // --------------------------------------------------------------------

  int MDL_UNKOWN = 0;
  int MDL_HELP = 5;
  int MDL_FORM = 10;
  int MDL_REPORT = 20;
  int MDL_PREVIEW = 30;
  int MDL_MENU_TREE = 40;
  int MDL_CHART = 50;
  int MDL_ITEM_TREE = 60;

  // --------------------------------------------------------------------
  // STANDARD FONT FAMILIES
  // --------------------------------------------------------------------

  String FNT_FIXED_WIDTH	= "dialoginput";
  String FNT_PROPORTIONAL	= "helvetica";

  // ---------------------------------------------------------------------
  // Predefined triggers
  // ---------------------------------------------------------------------

  int PRE_AUTOFILL		= -3;

  // ---------------------------------------------------------------------
  // Predefined commands
  // ---------------------------------------------------------------------

  int CMD_AUTOFILL		= -3;
  int CMD_HELP			= -7;
  int CMD_GOTO_SHORTCUTS	= -8;

  // ---------------------------------------------------------------------
  // Known bug work-arounds
  // ---------------------------------------------------------------------

  boolean BUG_JDC_4103341	= true;

  // ---------------------------------------------------------------------
  //
  // ---------------------------------------------------------------------

  String RESOURCE_DIR		= "org/kopi/vkopi/lib/util/resource";
}
