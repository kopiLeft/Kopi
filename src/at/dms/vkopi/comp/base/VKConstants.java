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

package at.dms.vkopi.comp.base;

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

  String VKO_FORM			= at.dms.vkopi.lib.form.VForm.class.getName().replace('.','/');
  String VKO_BLOCK			= at.dms.vkopi.lib.form.VBlock.class.getName().replace('.','/');
  String VKO_IMPORTEDBLOCK		= at.dms.vkopi.lib.form.VImportedBlock.class.getName().replace('.','/');
  String VKO_FIELD			= at.dms.vkopi.lib.form.VField.class.getName().replace('.','/');

  String VKO_VDICTIONARYFORM		= at.dms.vkopi.lib.form.VDictionaryForm.class.getName().replace('.','/');
  String VKO_VLISTCOLUMN		= at.dms.vkopi.lib.form.VListColumn.class.getName().replace('.','/');
  String VKO_VLIST			= at.dms.vkopi.lib.form.VList.class.getName().replace('.','/');
  String VKO_SQLEXCEPTION		= "java/sql/SQLException";
  String VKO_VEXCEPTION			= at.dms.vkopi.lib.visual.VException.class.getName().replace('.','/');
  String VKO_VRUNTIMEEXCEPTION		= at.dms.vkopi.lib.visual.VRuntimeException.class.getName().replace('.','/');
}
