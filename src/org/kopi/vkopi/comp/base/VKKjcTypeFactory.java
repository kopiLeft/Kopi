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

import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.util.base.InconsistencyException;

import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.ClassReader;


/**
 * Factory for visual Java Types
 */
public class VKKjcTypeFactory
  extends org.kopi.vkopi.comp.trig.GKjcTypeFactory
  implements VKTypeFactory, org.kopi.kopi.comp.kjc.Constants
{
  public VKKjcTypeFactory (Compiler compiler, ClassReader reader, boolean genericEnabled) {
    super(compiler, reader, genericEnabled);

    VFieldType = createType(org.kopi.vkopi.lib.form.VField.class.getName().replace('.','/'), false);
    VBlockType = createType(org.kopi.vkopi.lib.form.VBlock.class.getName().replace('.','/'), false);
    VDictionaryFormType = createType(org.kopi.vkopi.lib.form.VDictionaryForm.class.getName().replace('.','/'), false);
    VFormType = createType(org.kopi.vkopi.lib.form.VForm.class.getName().replace('.','/'), false);
    VReportType = createType(org.kopi.vkopi.lib.report.VReport.class.getName().replace('.','/'), false);
    VReportColumnType = createType(org.kopi.vkopi.lib.report.VReportColumn.class.getName().replace('.','/'), false);

    VActorType = createType(org.kopi.vkopi.lib.visual.VActor.class.getName().replace('.','/'), false);
    VDefaultActorType = createType(org.kopi.vkopi.lib.visual.VDefaultActor.class.getName().replace('.','/'), false);
    VListType = createType(org.kopi.vkopi.lib.list.VList.class.getName().replace('.','/'), false);
    VExceptionType = createType(org.kopi.vkopi.lib.visual.VException.class.getName().replace('.','/'), false);
    VRuntimeExceptionType = createType(org.kopi.vkopi.lib.visual.VRuntimeException.class.getName().replace('.','/'), false);
    VCommandType = createType(org.kopi.vkopi.lib.visual.VCommand.class.getName().replace('.','/'), false);
    VPositionType = createType(org.kopi.vkopi.lib.form.VPosition.class.getName().replace('.','/'), false);
    VColumnType = createType(org.kopi.vkopi.lib.list.VColumn.class.getName().replace('.','/'), false);
    VListColumnType = createType(org.kopi.vkopi.lib.list.VListColumn.class.getName().replace('.','/'), false);
    VStringColumnType = createType(org.kopi.vkopi.lib.list.VStringColumn.class.getName().replace('.','/'), false);
    VFixnumColumnType = createType(org.kopi.vkopi.lib.list.VFixnumColumn.class.getName().replace('.','/'), false);
    VIntegerColumnType = createType(org.kopi.vkopi.lib.list.VIntegerColumn.class.getName().replace('.','/'), false);
    VDateColumnType = createType(org.kopi.vkopi.lib.list.VDateColumn.class.getName().replace('.','/'), false);
    VMonthColumnType = createType(org.kopi.vkopi.lib.list.VMonthColumn.class.getName().replace('.','/'), false);
    VTimeColumnType = createType(org.kopi.vkopi.lib.list.VTimeColumn.class.getName().replace('.','/'), false);
    VWeekColumnType = createType(org.kopi.vkopi.lib.list.VWeekColumn.class.getName().replace('.','/'), false);
    VTextColumnType = createType(org.kopi.vkopi.lib.list.VTextColumn.class.getName().replace('.','/'), false);
    VBooleanColumnType = createType(org.kopi.vkopi.lib.list.VBooleanColumn.class.getName().replace('.','/'), false);
    VBooleanCodeColumnType = createType(org.kopi.vkopi.lib.list.VBooleanCodeColumn.class.getName().replace('.','/'), false);
    VFixnumCodeColumnType = createType(org.kopi.vkopi.lib.list.VFixnumCodeColumn.class.getName().replace('.','/'), false);
    VIntegerCodeColumnType = createType(org.kopi.vkopi.lib.list.VIntegerCodeColumn.class.getName().replace('.','/'), false);
    VStringCodeColumnType = createType(org.kopi.vkopi.lib.list.VStringCodeColumn.class.getName().replace('.','/'), false);
    VColorColumnType = createType(org.kopi.vkopi.lib.list.VColorColumn.class.getName().replace('.','/'), false);
    VImageColumnType = createType(org.kopi.vkopi.lib.list.VImageColumn.class.getName().replace('.','/'), false);

    try {
      VFieldType = (CReferenceType) VFieldType.checkType(context);
      VBlockType = (CReferenceType) VBlockType.checkType(context);
      VDictionaryFormType = (CReferenceType) VDictionaryFormType.checkType(context);
      VFormType = (CReferenceType) VFormType.checkType(context);
      VReportType =  (CReferenceType) VReportType.checkType(context);
      VReportColumnType = (CReferenceType) VReportColumnType.checkType(context);
      VActorType = (CReferenceType) VActorType.checkType(context);
      VDefaultActorType = (CReferenceType) VDefaultActorType.checkType(context);
      VListType = (CReferenceType) VListType.checkType(context);
      VExceptionType = (CReferenceType) VExceptionType.checkType(context);
      VRuntimeExceptionType = (CReferenceType) VRuntimeExceptionType.checkType(context);
      VCommandType = (CReferenceType) VCommandType.checkType(context);
      VPositionType = (CReferenceType) VPositionType.checkType(context);
      VColumnType = (CReferenceType) VColumnType.checkType(context);
      VListColumnType = (CReferenceType) VListColumnType.checkType(context);
      VStringColumnType = (CReferenceType) VStringColumnType.checkType(context);
      VFixnumColumnType = (CReferenceType) VFixnumColumnType.checkType(context);
      VIntegerColumnType = (CReferenceType) VIntegerColumnType.checkType(context);
      VDateColumnType = (CReferenceType) VDateColumnType.checkType(context);
      VMonthColumnType = (CReferenceType) VMonthColumnType.checkType(context);
      VTimeColumnType = (CReferenceType) VTimeColumnType.checkType(context);
      VWeekColumnType = (CReferenceType) VWeekColumnType.checkType(context);
      VTextColumnType = (CReferenceType) VTextColumnType.checkType(context);
      VBooleanColumnType = (CReferenceType) VBooleanColumnType.checkType(context);
      VBooleanCodeColumnType = (CReferenceType) VBooleanCodeColumnType.checkType(context);
      VFixnumCodeColumnType = (CReferenceType) VFixnumCodeColumnType.checkType(context);
      VIntegerCodeColumnType = (CReferenceType) VIntegerCodeColumnType.checkType(context);
      VStringCodeColumnType = (CReferenceType) VStringCodeColumnType.checkType(context);
      VColorColumnType = (CReferenceType) VColorColumnType.checkType(context);
      VImageColumnType = (CReferenceType) VImageColumnType.checkType(context);
    } catch (UnpositionedError cue) {
      throw new InconsistencyException("Failure while loading standard types.");
    }

  }

  public CReferenceType createReferenceType(int typeShortcut) {
    switch(typeShortcut){
    case RFT_VDICTIONARYFORM:
      return VDictionaryFormType;
    case RFT_VREPORT:
      return VReportType;
    case RFT_VFORM:
      return VFormType;
    case RFT_VBLOCK:
      return VBlockType;
    case RFT_VFIELD:
      return VFieldType;
    case RFT_VREPORTCOLUMN:
      return VReportColumnType;
    case RFT_SACTOR:
      return VActorType;
    case RFT_SDEFAULTACTOR:
      return VDefaultActorType;
    case RFT_VLIST:
      return VListType;
    case RFT_VCOLUMN:
      return VColumnType;
    case RFT_VPOSITION:
      return VPositionType;
    case RFT_VCOMMAND:
      return VCommandType;

    case RFT_VEXCEPTION:
      return VExceptionType;
    case RFT_VRUNTIMEEXCEPTION:
      return VRuntimeExceptionType;

  // ListColumn
    case RFT_VLISTCOLUMN:
      return VListColumnType;
    case RFT_VSTRINGCOLUMN:
      return VStringColumnType;
    case RFT_VFIXEDCOLUMN:
      return VFixnumColumnType;
    case RFT_VINTEGERCOLUMN:
      return VIntegerColumnType;
    case RFT_VDATECOLUMN:
      return VDateColumnType;
    case RFT_VMONTHCOLUMN:
      return VMonthColumnType;
    case RFT_VTIMECOLUMN:
      return VTimeColumnType;
    case RFT_VWEEKCOLUMN:
      return VWeekColumnType;
    case RFT_VTEXTCOLUMN:
      return VTextColumnType;
    case RFT_VBOOLEANCOLUMN:
      return VBooleanColumnType;
    case RFT_VBOOLEANCODECOLUMN:
      return VBooleanCodeColumnType;
    case RFT_VINTEGERCODECOLUMN:
      return VIntegerCodeColumnType;
    case RFT_VFIXEDCODECOLUMN:
      return VFixnumCodeColumnType;
    case RFT_VCOLORCOLUMN:
      return VColorColumnType;
    case RFT_VIMAGECOLUMN:
      return VImageColumnType;
    case RFT_VSTRINGCODECOLUMN:
      return VStringCodeColumnType;
    default:
      return super.createReferenceType(typeShortcut);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private CReferenceType         VDictionaryFormType;
  private CReferenceType         VReportType;
  private CReferenceType         VFormType;
  private CReferenceType         VBlockType;
  private CReferenceType         VFieldType;
  private CReferenceType         VReportColumnType;

  private CReferenceType         VActorType;
  private CReferenceType         VDefaultActorType;
  private CReferenceType         VListType;
  private CReferenceType         VColumnType;
  private CReferenceType         VPositionType;
  private CReferenceType         VCommandType;

  private CReferenceType         VExceptionType;
  private CReferenceType         VRuntimeExceptionType;

  // ListColumn
  private CReferenceType         VListColumnType;
  private CReferenceType         VStringColumnType;
  private CReferenceType         VFixnumColumnType;
  private CReferenceType         VIntegerColumnType;
  private CReferenceType         VDateColumnType;
  private CReferenceType         VMonthColumnType;
  private CReferenceType         VTimeColumnType;
  private CReferenceType         VWeekColumnType;
  private CReferenceType         VTextColumnType;
  private CReferenceType         VBooleanColumnType;
  private CReferenceType         VBooleanCodeColumnType;
  private CReferenceType         VIntegerCodeColumnType;
  private CReferenceType         VFixnumCodeColumnType;
  private CReferenceType         VColorColumnType;
  private CReferenceType         VImageColumnType;
  private CReferenceType         VStringCodeColumnType;
}
