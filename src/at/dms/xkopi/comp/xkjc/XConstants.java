/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.Constants;

/**
 * Defines all constants shared by compiler
 */
public interface XConstants extends Constants {

  String JAV_BOOLEAN            = "java/lang/Boolean";
  String JAV_BYTE               = "java/lang/Byte";
  String JAV_CHAR               = "java/lang/Character";
  String JAV_DOUBLE             = "java/lang/Double";
  String JAV_FLOAT              = "java/lang/Float";
  String JAV_INTEGER            = "java/lang/Integer";
  String JAV_LONG               = "java/lang/Long";
  String JAV_NUMBER             = "java/lang/Number";
  String JAV_SHORT              = "java/lang/Short";

  // ----------------------------------------------------------------------
  // PRIMITIVE SQL TYPES
  // ----------------------------------------------------------------------

  String XKJ_DATE		= "at/dms/xkopi/lib/type/Date";
  String XKJ_FIXED		= "at/dms/xkopi/lib/type/Fixed";
  String XKJ_MONTH		= "at/dms/xkopi/lib/type/Month";
  String XKJ_TIME		= "at/dms/xkopi/lib/type/Time";
  String XKJ_TIMESTAMP		= "at/dms/xkopi/lib/type/Timestamp";
  String XKJ_WEEK		= "at/dms/xkopi/lib/type/Week";

  // ----------------------------------------------------------------------
  // TYPES
  // ----------------------------------------------------------------------

  String JAV_SQLEXCEPTION	= "java/sql/SQLException";

  String XKJ_CONNECTION		= at.dms.xkopi.lib.base.Connection.class.getName().replace('.','/');
  String XKJ_CURSOR		= at.dms.xkopi.lib.base.Cursor.class.getName().replace('.','/');
  String XKJ_DBCONTEXTHANDLER	= at.dms.xkopi.lib.base.DBContextHandler.class.getName().replace('.','/');
  String XKJ_KOPISERIALIZABLE	= at.dms.xkopi.lib.base.KopiSerializable.class.getName().replace('.','/');

  // ----------------------------------------------------------------------
  // UNARY OPERATORS
  // ----------------------------------------------------------------------

  int OPE_CAST			= 26;

  // ----------------------------------------------------------------------
  // OPERATOR NAMES
  // ----------------------------------------------------------------------

  String[] OPERATOR_NAMES	= {
    "=", "+", "-", "*", "/", "%", ">>", ">>>",
    "<<", "&", "^", "|", "!", "!", "<", "<=",
    ">", ">=", "==", "!=", "++", "--", "++", "--",
    "()"
  };
}
