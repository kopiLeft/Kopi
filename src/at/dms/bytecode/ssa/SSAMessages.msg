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
 * $Id: SSAMessages.msg,v 1.1 2004/08/17 13:49:15 lackner Exp $
 */

package at.dms.bytecode.ssa

prefix SSA
parent at.dms.compiler.base.CompilerMessages

// BACKEND ERRORS
error		SEMANTIC_ERROR
		"Semantic error found on file \"{0}\""
		"OPTIMIZE"

