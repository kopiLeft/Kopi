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
 * $Id: BaseMessages.msg,v 1.5 2003/10/02 09:35:37 vgp Exp $
 */

package com.kopiright.vkopi.comp.base

prefix Base

error		UNDEFINED_TYPE
		"the type \"{0}\" is not defined"
		"VKJC"

error		MENU_UNDEFINED_KEY
		"the menu \"{0}\" is not defined"
		"VKJC"

error		UNDEFINED_ACTION
		"the action item named \"{0}\" is not defined"
		"VKJC"

error		FILE_UNKNOWN
		"the file \"{0}\" cannot be found"
		"VKJC"

error		FILE_CANT_READ
		"Error while reading file \"{0}\""
		"VKJC"

error		TRIGGER_DIFFERENT_RETURN
		"A trigger list cannot include triggers with different return type"
		"VKJC"

error		COMMAND_DEF_NOT_FOUND
		"Command named \"{0}\" is not defined"
		"VKJC"

error		UNKNOWN_FILE_SUFFIX
		"File \"{0}\" has unknown suffix; should be one of: vf, vp, vr"
		"VKJC"

error		CANT_LOAD_CLASSES
		"Can not loas standard classes in VKStdType"
		"VKJC"

error		BAD_SQL_CASE_OPTION
		"The only supported option for --sqlCase is \"upper\""
		"VKJC"

error		BAD_FIXED_NEW_LINE
		"Option FIXED ON not allowed here"
		"VKJC"

error		BAD_DYNAMIC_NEW_LINE
		"Option FIXED OFF not allowed here"
		"VKJC"

error		BAD_DYNAMIC_AND_FIXED
		"Can't use FIXED ON and FIXED OFF together"
		"VKJC"

error		UNSET_DYNAMIC_AND_FIXED
		"Option FIXED ON/OFF has to be set"
		"VKJC"

error		TRANSLATION_WITHOUT_LOCALE
		"Translation for \"{0}\" given but no locale is specified"
		"VKJC"

error		LOCALE_WITHOUT_TRANSLATION
		"Locale \"{0}\" specified but no translation for \"{1}\" given"
		"VKJC"

warning         BAD_LOCALE_DEFINITION
		"Bad locale \"{0}\", it should be in the form \"ll_CC\" where \"ll\" is the language code and \"CC\" is the country code."
		"VKJC"
