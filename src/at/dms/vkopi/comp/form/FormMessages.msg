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
 * $Id: FormMessages.msg,v 1.10 2004/02/05 13:51:37 graf Exp $
 */

package at.dms.vkopi.comp.form

prefix Form

error		UNDEFINED_INDEX
		"The index number {0} is not defined in this block"
		"FORM"

error		BLOCK_NOT_FOUND
		"Block \"{0}\" is not defined."
		"FORM"

error		BLOCK_ALIGN_BAD
		"only left-aligned blocks can be aligned together"
		"FORM"

error		BAD_COLUMN_ORDER
		"The column order should be increasing"
		"FORM"

error		UNDEFINED_TABLENUM
		"The table shortcut \"{0}\" is not defined in this block"
		"FORM"

error		BLOCK_NO_SHORTCUT
		"Block \"{0}\" should have a shortcut"
		"FORM"

error		INDEX_NOT_USED
		"Index number {0} is not used in this block"
		"FORM"

error		CHART_FIELD_POSITIONED
		"Fields in multi-line blocks should not be positioned"
		"FORM"

error		HIDDEN_FIELD_POSITIONED
		"Hidden fields should not be positioned"
		"FORM"

error		HIDDEN_FIELD_NOCHART
		"Hidden fields should not have the option NO CHART"
		"FORM"

error		HIDDEN_FIELD_NODETAIL
		"Hidden fields should not have the option NO DETAIL"
		"FORM"

error		ALIAS_FIELD_VISIBLE
		"Only hidden fields can be ALIAS"
		"FORM"

error		POSITION_NEGATIVE_LENGTH
		"The element is smaller than 0"
		"FORM"

error		FIELD_NOT_FOUND
		"Field \"{0}\" is not defined."
		"FORM"

error		FOLLOW_INTERNAL
		"This field follows the internal field \"{0}\"."
		"FORM"

error		MORE_VISIBLE_THAN_BUFFERED
		"This block has more record visible than buffered"
		"FORM"

error		SINGLE_NOCHART
		"The option NO CHART is not allowed for a single block"
		"FORM"

error		SINGLE_NODETAIL
		"The option NO DETAIL is not allowed for a single block"
		"FORM"

error		MULTI_NODETAIL_ALLUNPOSITIONED
		"All the fields in the block \"{0}\" have no position, you must disable the detailed view"
		"FORM"

error		FIELD_CANT_HAVE_NODETAIL
		"This field can't declare NO DETAIL because the block declares NO CHART"
		"FORM"

error		FIELD_CANT_HAVE_NOCHART
		"This field can't declare NO CHART because the block declares NO DETAIL"
		"FORM"

error		FIELD_NOCHART_NOPOSITION
		"A field with the option NO CHART must be positioned."
		"FORM"

error		FIELD_NODETAIL_POSITION
		"A field with the option NO DETAIL cannot have a position."
		"FORM"

error		FIELD_NOCHART_NODETAIL
		"The options NO CHART NO DETAIL are exclusive."
		"FORM"

error		FIELD_IN_DETAILED_BLOCK
		"A field in a block which has a detailed view must have either a position or declare NO DETAIL."
		"FORM"

error		MULTI_MULTI_DISPLAY_NOCHART
		"Option NO CHART cannot be set for multi-blocks with more than one line displayed."
		"FORM"

error		FIELD_MUST_HAVE_POSITION
		"This field must have a position"
		"FORM"

error		TRANSIENT_DATABASE_FIELD
		"A transient field must not be connected to a database column."
		"FORM"

warning		BOTH_NOCHART
		"Redundant option NO CHART since the block already declared it"
		"FORM"

warning		BOTH_NODETAIL
		"Redundant option NO DETAIL since the block already declared it"
		"FORM"


warning		POSITION_USED
		"The position <{0}, {1}> of this field is already used by the field \"{2}\""
		"FORM"

warning         NO_REC_TRG_FOR_SINGLE_BLOCK
		"The single block \"{0}\" can not have a {1}REC trigger; use the trigger {1}BLK instead."
		"FORM"
