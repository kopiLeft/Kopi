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
 * $Id: GKjcMessages.msg,v 1.5 2002/01/06 13:56:00 graf Exp $
 */

package at.dms.vkopi.comp.trig

prefix GKjc

error		EXEC_KOPI_INSERT_TABLE_BAD_TYPE
		"The type of the table parameter in an insert should be String"
		"GKJC"

error		BAD_DATE_FORMAT
		"This is not a valide date:\"{0}\""
		"GKJC"

error		BAD_TIME_FORMAT
		"This is not a valide time:\"{0}\""
		"GKJC"

error		BAD_MONTH_FORMAT
		"This is not a valide month:\"{0}\""
		"GKJC"

error		ACCESS_NOT_FIELD
		"This is not a field, you can't use the '@' operator"
		"GKJC"

error		BAD_RECORD_ACCESS
		"The record number should be an int"
		"GKJC"

error		CANT_LOAD_CLASSES_ZIP
		"Some types are undefined (not in the class path)"
		"GKJC"

error		NO_FILES_GIVEN
		"Parameters should contain at least one file"
		"GKJC"

warning		MISSING_NOTNULL_COLUMN
		"Not-null column \"{0}\" missing in #insert expression"
		"GKJC"

