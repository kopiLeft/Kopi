/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.comp.base;

/**
 * Defines all constants shared by compiler
 */
public interface VKConstants {

  // ----------------------------------------------------------------------
  // COMPILER CONSTANTS
  // ----------------------------------------------------------------------

  String CMP_BLOCK_TYP			= "VKT_BLOCK_";
  String CMP_BLOCK_ARRAY		= "VKT_Triggers";

  String CMP_FLD_TYP			= "_TYPE";

  String CMP_TRIG_PARAM			= "VKT_Type";

  // ----------------------------------------------------------------------
  // VFIELDINFOS
  // ----------------------------------------------------------------------

  String FLD_SET_INFO			= "setInfo";

  // ----------------------------------------------------------------------
  // PREDEFINED COMMANDS
  // ----------------------------------------------------------------------

  String CMD_AUTOFILL			= "Autofill";
  String CMD_SHORTCUT			= "EditItem_S";
  String CMD_NEWITEM			= "NewItem";
  String CMD_EDITITEM			= "EditItem";

  // ----------------------------------------------------------------------
  // CLASS CONSTANTS
  // ----------------------------------------------------------------------

  String VKO_FORM			= org.kopi.vkopi.lib.form.VForm.class.getName().replace('.','/');
  String VKO_BLOCK			= org.kopi.vkopi.lib.form.VBlock.class.getName().replace('.','/');
  String VKO_IMPORTEDBLOCK		= org.kopi.vkopi.lib.form.VImportedBlock.class.getName().replace('.','/');
  String VKO_FIELD			= org.kopi.vkopi.lib.form.VField.class.getName().replace('.','/');

  String VKO_VDICTIONARYFORM		= org.kopi.vkopi.lib.form.VDictionaryForm.class.getName().replace('.','/');
  String VKO_VLISTCOLUMN		= org.kopi.vkopi.lib.list.VListColumn.class.getName().replace('.','/');
  String VKO_VLIST			= org.kopi.vkopi.lib.list.VList.class.getName().replace('.','/');
  String VKO_SQLEXCEPTION		= "java/sql/SQLException";
  String VKO_VEXCEPTION			= org.kopi.vkopi.lib.visual.VException.class.getName().replace('.','/');
  String VKO_VRUNTIMEEXCEPTION		= org.kopi.vkopi.lib.visual.VRuntimeException.class.getName().replace('.','/');
}
