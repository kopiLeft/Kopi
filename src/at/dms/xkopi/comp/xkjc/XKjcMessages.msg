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
 * $Id: XKjcMessages.msg,v 1.7 2003/09/02 08:21:51 vgp Exp $
 */

package at.dms.xkopi.comp.xkjc

prefix XKjc

error		ASSERT_BAD_TYPE
		"Second argument of assert must be a String"
		"XKJC"

error		ASSERT_COND_BAD_TYPE
		"First argument of assert must be a boolean expression"
		"XKJC"

error		ASSIGNMENT_TO_NOT_NULL
		"Try to assign a value of type \"{0}\" to a variable that can't be null"
		"XKJC"

error		BAD_CONNECTION
		"The object \"{0}\" must inherit from Connection or DBContextHandler"
		"XKJC"

error		BAD_DEFAULT_CONTEXT
		"You must provide a Connection for this statement, \"{0}\" does not inherit from DBContextHandler"
		"XKJC"

error		BAD_FIXED_FORMAT
		"This is not a fixed: \"{0}\""
		"XKJC"

error		BAD_KEYWORD
		"Illegal KOPI keyword \"{0}\""
		"XKJC"

error		BAD_XKJCPATH
		"The XKJC_PATH is corrupted: {0}"
		"XKJC"

error		DUPLICATE_IDENT
		"The column \"{0}\" appears more than one time in this SELECT"
		"XKJC"

error		DUMMY
		"This an unused dummy message"
		"XKJC"

warning		EXECUTE_BAD_PRIMITIVE
		"An execute statement may return null values for SetFunctions if no rows are found"
		"XKJC"

error		FAIL_BAD_TYPE
		"First argument of fail must be a String"
		"XKJC"

error		OPERATOR_IDENT
		"Operator must be named \"operator:xxx\""
		"XKJC"

error		OPERATOR_STATIC
		"Operator definition must be static"
		"XKJC"

error		OPERATOR_PUBLIC
		"Operator definition must be static"
		"XKJC"

error		PROTECTED_BAD_CONTEXT
		"The context for a protected statement must inherit from DBContextHandler (\"{0}\" doesn''t)"
		"XKJC"

error		PROTECTED_BAD_MESSAGE
		"The message parameter of a protected statement must be a String and not {0}"
		"XKJC"

error		PROTECTED_DEFAULT_BAD
		"The default context for protected should inherit from DBContextHandler but this class ({0}) doesn''t"
		"XKJC"

error		PROTECTED_NO_CONTEXT
		"The class enclosing this protected statement does not inherit from DBContextHandler; therefore you must provide an explicit context"
		"XKJC"

error		PROTECTED_RETURN
		"Return statement not allowed in protected code"
		"XKJC"

error		GOTO_NOT_ALLOWED
		"Goto statement not allowed in Java (use --allowGoto)"
		"XKJC"

error		SELECT_LIST_LENGTH
		"The number of parameters in the INTO part should match those of the SELECT part"
		"XKJC"

caution		SHARP_ASSERT
		"\"#assert\" is the old syntax of assert. Use \"@assert\" instead."
		"KOPI"
