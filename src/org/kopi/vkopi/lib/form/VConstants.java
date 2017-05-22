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

package org.kopi.vkopi.lib.form;

import javax.swing.SwingConstants;

public interface VConstants extends org.kopi.vkopi.lib.visual.Constants {

  // ---------------------------------------------------------------------
  // ACCESS
  // ---------------------------------------------------------------------

  int ACS_HIDDEN			= 0;
  int ACS_SKIPPED			= 1;
  int ACS_VISIT				= 2;
  int ACS_MUSTFILL			= 4;
  int ACS_ACCESS			= 1 + 2 + 4;

  // ---------------------------------------------------------------------
  // MODES
  // ---------------------------------------------------------------------

  int MOD_QUERY				= 0;
  int MOD_INSERT			= 1;
  int MOD_UPDATE			= 2;
  int MOD_ANY				= 1 + 2 + 4;

  // ---------------------------------------------------------------------
  // TRIGGERED EVENTS (MAX 32)
  // ---------------------------------------------------------------------

  int TRG_PREQRY			= 0;
  int TRG_POSTQRY			= 1;
  int TRG_PREDEL			= 2;
  int TRG_POSTDEL			= 3;
  int TRG_PREINS			= 4;
  int TRG_POSTINS			= 5;
  int TRG_PREUPD			= 6;
  int TRG_POSTUPD			= 7;
  int TRG_PRESAVE			= 8;
  int TRG_PREREC			= 9;
  int TRG_POSTREC			= 10;
  int TRG_PREBLK			= 11;
  int TRG_POSTBLK			= 12;
  int TRG_VALBLK			= 13;
  int TRG_VALREC			= 14;
  int TRG_DEFAULT			= 15;
  int TRG_INIT				= 16;
  int TRG_RESET				= 17;
  int TRG_CHANGED			= 18;
  int TRG_ACCESS			= 27;
  int TRG_AUTOLEAVE			= 31;

  int TRG_POSTCHG			= 19;
  int TRG_PREFLD			= 20;
  int TRG_POSTFLD			= 21;
  int TRG_PREVAL			= 22;
  int TRG_VALFLD			= 23;
  int TRG_FORMAT			= 24;
  int TRG_PREDROP                       = 33;
  int TRG_POSTDROP                      = 34;
  int TRG_ACTION                        = 35;

  int TRG_PREFORM			= 25;
  int TRG_POSTFORM			= 26;

  int TRG_FLDACCESS			= 28;
  int TRG_VALUE				= 29;
  int TRG_QUITFORM			= 30;

  int TRG_VOID				= 0;
  int TRG_BOOLEAN			= 1;
  int TRG_INT				= 2;
  int TRG_OBJECT			= 3;
  int TRG_PRTCD				= 4;
  
  int TRG_CMDACCESS                     = 32;

  // ---------------------------------------------------------------------
  // OPTIONS FOR BLOCKS
  // ---------------------------------------------------------------------

  int BKO_NOINSERT			= 1;
  int BKO_NODELETE			= 2;
  int BKO_NOMOVE			= 4;
  int BKO_INDEXED			= 8;
  int BKO_ALWAYS_ACCESSIBLE		= 16;
  int BKO_NOCHART			= 32;
  int BKO_NODETAIL			= 64;

  // ---------------------------------------------------------------------
  // OPTIONS FOR FIELDS
  // ---------------------------------------------------------------------

  int FDO_NOECHO			= 1;
  int FDO_NOEDIT			= 2;
  int FDO_TRANSIENT			= 4;
  int FDO_DO_NOT_ERASE_ON_LOOKUP	= 8;

  int FDO_SEARCH_MASK			= 0x00F0;
  int FDO_SEARCH_NONE			= 0x0000;
  int FDO_SEARCH_UPPER			= 0x0010;
  int FDO_SEARCH_LOWER			= 0x0020;

  int FDO_CONVERT_MASK			= 0x0F00;
  int FDO_CONVERT_NONE			= 0x0000;
  int FDO_CONVERT_UPPER			= 0x0100;
  int FDO_CONVERT_LOWER			= 0x0200;
  int FDO_CONVERT_NAME			= 0x0400;

  int FDO_NODETAIL			= 0x1000;
  int FDO_NOCHART			= 0x2000;
  int FDO_SORT                          = 0x4000;

  int FDO_DYNAMIC_NL                    = 0x10000;
  int FDO_FIX_NL                        = 0x20000;

  // ---------------------------------------------------------------------
  // SEARCH OPERATORS
  // ---------------------------------------------------------------------

  int SOP_EQ				= 0;
  int SOP_LT				= 1;
  int SOP_GT				= 2;
  int SOP_LE				= 3;
  int SOP_GE				= 4;
  int SOP_NE				= 5;

  // ---------------------------------------------------------------------
  // SEARCH OPERATORS NAMES
  // ---------------------------------------------------------------------

  String[] OPERATOR_NAMES = { "=", "<", ">", "<=", ">=", "<>" };

  // ---------------------------------------------------------------------
  // SEARCH TYPE
  // ---------------------------------------------------------------------

  int STY_NO_COND			= 0;  // no conditions for field
  int STY_EXACT				= 1;  // requires exact match
  int STY_MANY				= 2;  // many values can match

  // ---------------------------------------------------------------------
  // BORDERS
  // ---------------------------------------------------------------------

  int BRD_NONE				= 0;
  int BRD_LINE				= 1;
  int BRD_RAISED			= 2;
  int BRD_LOWERED			= 3;
  int BRD_ETCHED			= 4;
  int BRD_HIDDEN			= 5;

  // ---------------------------------------------------------------------
  // ALIGNMENT
  // ---------------------------------------------------------------------

  int ALG_DEFAULT			= SwingConstants.LEFT;
  int ALG_LEFT				= SwingConstants.LEFT;
  int ALG_CENTER			= SwingConstants.CENTER;
  int ALG_RIGHT				= SwingConstants.RIGHT;

  // ---------------------------------------------------------------------
  // POSITION
  // ---------------------------------------------------------------------

  int POS_LEFT				= 0;
  int POS_TOP				= 1;

  // ---------------------------------------------------------------------
  // TRIGGER INFO
  // ---------------------------------------------------------------------

  String[] TRG_NAMES			= new String[] {
    "TRG_PREQRY", "TRG_POSTQRY", "TRG_PREDEL", "TRG_POSTDEL", "TRG_PREINS", "TRG_POSTINS",
    "TRG_PREUPD", "TRG_POSTUPD", "TRG_PRESAVE", "TRG_PREREC", "TRG_POSTREC", "TRG_PREBLK",
    "TRG_POSTBLK", "TRG_VALBLK", "TRG_VALREC", "TRG_DEFAULT", "TRG_INIT", "TRG_RESET",
    "TRG_CHANGED", "TRG_POSTCHG", "TRG_PREFLD", "TRG_POSTFLD", "TRG_PREVAL",
    "TRG_VALFLD", "TRG_FORMAT", "TRG_PREFORM", "TRG_POSTFORM", "TRG_ACCESS",
    "TRG_FLDACCESS", "TRG_VALUE", "TRG_AUTOLEAVE", "TRG_QUITFORM", "TRG_CMDACCESS",
    "TRG_PREDROP", "TRG_POSTDROP", "TRG_ACTION"
  };

  int[] TRG_TYPES			= new int[] {
    TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_PRTCD,
    TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_VOID, TRG_VOID, TRG_VOID,
    TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID, TRG_BOOLEAN,
    TRG_BOOLEAN, TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID,
    TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID, TRG_BOOLEAN,
    TRG_INT, TRG_OBJECT, TRG_BOOLEAN, TRG_BOOLEAN, TRG_BOOLEAN,
    TRG_VOID, TRG_VOID, TRG_VOID
  };

  // ---------------------------------------------------------------------
  //
  // ---------------------------------------------------------------------

  String EMPTY_TEXT			= "";
  String RESOURCE_DIR			= "org.kopi.vkopi.lib.util/resource";
  
  // ---------------------------------------------------------------------
  // IMAGE DOCUMENT EXTENSIONS
  // ---------------------------------------------------------------------
  
  int IMAGE_DOC_PDF		= 0;
  int IMAGE_DOC_JPEG		= 1;
  int IMAGE_DOC_TIF		= 2;
}
