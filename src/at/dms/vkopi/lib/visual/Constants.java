/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: Constants.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.vkopi.lib.visual;

public interface Constants {


  // --------------------------------------------------------------------
  // MODEL TYPES
  // --------------------------------------------------------------------

  int MDL_UNKOWN = 0;
  int MDL_HELP = 5;
  int MDL_FORM = 10;
  int MDL_REPORT = 20;
  int MDL_PREVIEW = 30;

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

  String RESOURCE_DIR		= "at/dms/vkopi/lib/util/resource";
}
