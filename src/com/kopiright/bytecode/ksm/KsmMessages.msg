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

package com.kopiright.bytecode.ksm

prefix Ksm
parent com.kopiright.compiler.base.CompilerMessages

message		PROCESSING
		"Processing \"{0}\"."

message		GENERATED
		"Generated: \"{0}\"."

error		ASM_ERROR
		"Assembler error on file {0}: {1}"
		"KSM"

error		SEMANTIC_ERROR
		"Semantic error on file {0}: {1}"
		"KSM"

error		UNDEFINED_LABEL
		"Label \"{0}\" is not defined"
		"KSM"

error		LABEL_REDEFINED
		"Label \"{0}\" is multiply defined"
		"KSM"

error		NO_CODE_AT_LABEL
		"Label \"{0}\" outside of code array"
		"KSM"

error		DOUBLE_ATTR_SOURCE
		"Source attribute (@source) is defined twice."
		"KSM"

error		DOUBLE_ATTR_SIGNATURE
		"Signature attribute (@signature) is defined twice."
		"KSM"

error		DOUBLE_ATTR_DEPRECATED
		"Deprecated attribute (@deprecated) is defined twice."
		"KSM"

error		DOUBLE_ATTR_SYNTHETIC
		"Synthetic attribute (@synthetic) is defined twice."
		"KSM"

error		UNEXPECTED_EXCEPTION
		"Internal error: caught unexpected exception \"{0}\""
