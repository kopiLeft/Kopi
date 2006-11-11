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

package com.kopiright.xkopi.comp.sqlc

prefix Sqlc
parent com.kopiright.compiler.base.CompilerMessages

// SYNTAX ERRORS
error		INVALID_DATE_LITERAL
		"\"{0}\" is not a valid date literal."
		"JDBC"

error		INVALID_TIME_LITERAL
		"\"{0}\" is not a valid time literal."
		"JDBC"

error		INVALID_TIMESTAMP_LITERAL
		"\"{0}\" is not a valid timestamp literal."
		"JDBC"

error		INVALID_NULL_COMPARISON
		"\"{0}\" comparison with NULL is not allowed"
		"SQLC"
// SEMANTIC ERRORS
warning		TABLE_NOT_FOUND
		"The table \"{0}\" doesn''t exist."
		"SQLC"

warning		ALIAS_NOT_FOUND
		"The alias \"{0}\" doesn''t exist."
		"SQLC"

warning		ALIAS_NOT_UNIQUE
		"The alias \"{0}\" is not unique."
		"SQLC"

warning		COLUMN_NOT_IN_TABLE
		"Table \"{1}\" has no column \"{0}\"."
		"SQLC"

warning		COLUMN_NOT_RESOLVABLE
		"The column \"{0}\" is not resolvable."
		"SQLC"

warning		COLUMN_NOT_UNIQUE
		"The column \"{0}\" is not unique in the list."
		"SQLC"

warning		IDENT_NOT_RESOLVABLE
		"The Ident \"{0}\" is not resolvable."
		"SQLC"

warning		NUMBER_COLUMNS_NOT_CORRECT
		"The number of columns isn't correct."
		"SQLC"

warning		NUMBER_COLUMNS_VALUES_NOT_EQUAL
		"The number of columns in the ORDER BY isn't correct."
		"SQLC"

warning		SET_FUNCTION_IS_SQL
		"\"{0}\" is defined in the grammar: it should not be enclosed in fn-escapes"
		"SQLC"

warning		BAD_DATABASE_TYPE
		"The column \"{0}\" is not compatible with \"{1}\""
		"SQLC"

warning		NOT_ANSI_SYNTAX
		"Not ANSI syntax, use a AS"
		"SQLC"

warning		ASSIGN_NULL
		"Not ANSI syntax, use \"IS NULL\" instead of \"= NULL\""
		"SQLC"

warning		NE_NULL
		"Not ANSI syntax, use \"IS NOT NULL\" instead of \"<> NULL\""
		"SQLC"

warning		IF_EXPR
		"Not ANSI syntax, use \"DECODE(<cond>, {<search>, <result>}, <default>)\" instead of \"IF <cond> THEN <if-part> ELSE <else-part> FI\""
		"SQLC"
