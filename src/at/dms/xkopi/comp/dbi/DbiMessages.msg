/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: DbiMessages.msg,v 1.5 2004/05/14 11:14:38 mike Exp $
 */

package at.dms.xkopi.comp.dbi

prefix Dbi
parent at.dms.compiler.base.CompilerMessages

// DRIVER ERRORS
error		CONNECTION_FAILED
		"Cannot connect to database: {0}"

// SEMANTIC ERRORS
error		COLUMN_IN_TABLE_NOT_CORRECT
		"The column \"{0}\" in the table \"{1}\" is not correct"
		"DBI"

error		KEY_IN_TABLE_NOT_CORRECT
		"The key of the table \"{0}\" is not correct"
		"DBI"

error		TOO_MUCH_COLUMNS_IN_TABLE
		"The table \"{0}\" has too much columns"
		"DBI"

error		KEY_NOT_CORRECT
		"The key \"{0}\" is not correct"
		"DBI"

error		VIEWS_DIFFERENT
		"The views in the database \"{0}\" and in against \"{1}\" are different"
		"DBI"

error		INSERTS_DIFFERENT
		"The inserts in the database \"{0}\" and in against \"{1}\" are different"
		"DBI"

error		TABLE_NOT_CORRECT
		"The table \"{0}\" is not correct"
		"DBI"

error		TABLE_NOT_EXIST_IN_FILE
		"Table \"{0}\" doesn''t exist in the file"
		"DBI"

error		BAD_INSERT_NOT_STR_LIT
		"Bad insert statement in makeDB (not StringLiteral)"
		"DBI"

error		BAD_INSERT_REFERENZEN
		"Bad insert statement in makeDB for REFERENZEN \"{0}\" or \"{1}\""
		"DBI"

error		BAD_INSERT_NOT_INT_LIT
		"Bad insert statement in makeDB (not IntegerLiteral)"
		"DBI"

error		BAD_INSERT_MODULE
		"Bad insert statement in makeDB for MODULE \"Bad insert statement in makeDB for MODULE \"{0}\" or \"{1}\"{0}\" or \"{1}\""
		"DBI"

error		NO_CONNECTION
		"Unable to connect any database"
		"DBI"

error		INT_TOO_SMALL
		"The int \"{0}\" has to be bigger than the minimum required \"{1}\""
		"DBI"

error		INT_TOO_BIG
		"The int \"{0}\" has to be smaller than the maximum required \"{1}\""
		"DBI"

error		SHORT_TOO_SMALL
		"The short \"{0}\" has to be bigger than the minimum required \"{1}\""
		"DBI"

error		SHORT_TOO_BIG
		"The short \"{0}\" has to be smaller than the maximum required \"{1}\""
		"DBI"

error		BYTE_TOO_SMALL
		"The short \"{0}\" has to be bigger than the minimum required \"{1}\""
		"DBI"

error		BYTE_TOO_BIG
		"The short \"{0}\" has to be smaller than the maximum required \"{1}\""
		"DBI"

error		TYPE_NOT_EQUALS
		"The type \"{0}\" and the type \"{1}\" are not equals"
		"DBI"

error		STRING_SIZE
		"No size specified for string column."
		"DBI"

// SQL EXECUTION ERRORS
error           SQL_EXCEPTION
                "A database exception has occured: \"{0}\""
                "DBI"
