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

import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.CBinaryTypeContext;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.TypeFactory;

public class VKStdType {
  public static CReferenceType VDictionaryForm;
  public static CReferenceType VReport;
  public static CReferenceType VForm;
  public static CReferenceType VBlock;
  public static CReferenceType VBlockUIProperties;
  public static CReferenceType DBlock;
  public static CReferenceType DMultiBlock;
  public static CReferenceType VField;
  public static CReferenceType VReportColumn;

  public static CReferenceType SActor;
  public static CReferenceType SDefaultActor;
  public static CReferenceType VList;
  public static CReferenceType VColumn;
  public static CReferenceType VPosition;
  public static CReferenceType VCommand;

  public static CReferenceType VException;
  public static CReferenceType VRuntimeException;

  // ListColumn
  public static CReferenceType VListColumn;
  public static CReferenceType VStringColumn;
  public static CReferenceType VFixedColumn;
  public static CReferenceType VIntegerColumn;
  public static CReferenceType VDateColumn;
  public static CReferenceType VMonthColumn;
  public static CReferenceType VTimeColumn;
  public static CReferenceType VTimestampColumn;
  public static CReferenceType VWeekColumn;
  public static CReferenceType VTextColumn;
  public static CReferenceType VBooleanColumn;
  public static CReferenceType VBooleanCodeColumn;
  public static CReferenceType VIntegerCodeColumn;
  public static CReferenceType VFixedCodeColumn;
  public static CReferenceType VColorColumn;
  public static CReferenceType VImageColumn;
  public static CReferenceType VEnumColumn;

  // ----------------------------------------------------------------------
  // INITIALIZERS
  // ----------------------------------------------------------------------

  /**
   * Initialize all constants
   */
  public static void init(CBinaryTypeContext context, Compiler compiler) {

    if (VField != null) {
      return;
    }
    TypeFactory tf = context.getTypeFactory();

    VField = tf.createType(at.dms.vkopi.lib.form.VField.class.getName().replace('.','/'), false);
    VBlock = tf.createType(at.dms.vkopi.lib.form.VBlock.class.getName().replace('.','/'), false);
    VBlockUIProperties = tf.createType(at.dms.vkopi.lib.form.VBlock.UIProperties.class.getName().replace('.','/'), false);
    DBlock = tf.createType(at.dms.vkopi.lib.form.DBlock.class.getName().replace('.','/'), false);
    DMultiBlock = tf.createType(at.dms.vkopi.lib.form.DMultiBlock.class.getName().replace('.','/'), false);
    VDictionaryForm = tf.createType(at.dms.vkopi.lib.form.VDictionaryForm.class.getName().replace('.','/'), false);
    VForm = tf.createType(at.dms.vkopi.lib.form.VForm.class.getName().replace('.','/'), false);
    VReport = tf.createType(at.dms.vkopi.lib.report.VReport.class.getName().replace('.','/'), false);
    VReportColumn = tf.createType(at.dms.vkopi.lib.report.VReportColumn.class.getName().replace('.','/'), false);

    SActor = tf.createType(at.dms.vkopi.lib.visual.SActor.class.getName().replace('.','/'), false);
    SDefaultActor = tf.createType(at.dms.vkopi.lib.visual.SDefaultActor.class.getName().replace('.','/'), false);
    VList = tf.createType(at.dms.vkopi.lib.form.VList.class.getName().replace('.','/'), false);
    VException = tf.createType(at.dms.vkopi.lib.visual.VException.class.getName().replace('.','/'), false);
    VRuntimeException = tf.createType(at.dms.vkopi.lib.visual.VRuntimeException.class.getName().replace('.','/'), false);
    VCommand = tf.createType(at.dms.vkopi.lib.visual.VCommand.class.getName().replace('.','/'), false);
    VPosition = tf.createType(at.dms.vkopi.lib.form.VPosition.class.getName().replace('.','/'), false);
    VColumn = tf.createType(at.dms.vkopi.lib.form.VColumn.class.getName().replace('.','/'), false);
    VListColumn = tf.createType(at.dms.vkopi.lib.form.VListColumn.class.getName().replace('.','/'), false);
    VStringColumn = tf.createType(at.dms.vkopi.lib.form.VStringColumn.class.getName().replace('.','/'), false);
    VFixedColumn = tf.createType(at.dms.vkopi.lib.form.VFixedColumn.class.getName().replace('.','/'), false);
    VIntegerColumn = tf.createType(at.dms.vkopi.lib.form.VIntegerColumn.class.getName().replace('.','/'), false);
    VDateColumn = tf.createType(at.dms.vkopi.lib.form.VDateColumn.class.getName().replace('.','/'), false);
    VMonthColumn = tf.createType(at.dms.vkopi.lib.form.VMonthColumn.class.getName().replace('.','/'), false);
    VTimeColumn = tf.createType(at.dms.vkopi.lib.form.VTimeColumn.class.getName().replace('.','/'), false);
    VTimestampColumn = tf.createType(at.dms.vkopi.lib.form.VTimestampColumn.class.getName().replace('.','/'), false);
    VWeekColumn = tf.createType(at.dms.vkopi.lib.form.VWeekColumn.class.getName().replace('.','/'), false);
    VTextColumn = tf.createType(at.dms.vkopi.lib.form.VTextColumn.class.getName().replace('.','/'), false);
    VBooleanColumn = tf.createType(at.dms.vkopi.lib.form.VBooleanColumn.class.getName().replace('.','/'), false);
    VBooleanCodeColumn = tf.createType(at.dms.vkopi.lib.form.VBooleanCodeColumn.class.getName().replace('.','/'), false);
    VIntegerCodeColumn = tf.createType(at.dms.vkopi.lib.form.VIntegerCodeColumn.class.getName().replace('.','/'), false);
    VFixedCodeColumn = tf.createType(at.dms.vkopi.lib.form.VFixedCodeColumn.class.getName().replace('.','/'), false);
    VColorColumn = tf.createType(at.dms.vkopi.lib.form.VColorColumn.class.getName().replace('.','/'), false);
    VImageColumn = tf.createType(at.dms.vkopi.lib.form.VImageColumn.class.getName().replace('.','/'), false);
    VEnumColumn = tf.createType(at.dms.vkopi.lib.form.VEnumColumn.class.getName().replace('.','/'), false);
    try {
      VField = (CReferenceType) VField.checkType(context);
      VBlock = (CReferenceType) VBlock.checkType(context);
      VBlockUIProperties = (CReferenceType) VBlockUIProperties.checkType(context);
      DBlock = (CReferenceType) DBlock.checkType(context);
      DMultiBlock = (CReferenceType) DMultiBlock.checkType(context);
      VDictionaryForm = (CReferenceType) VDictionaryForm.checkType(context);
      VForm = (CReferenceType) VForm.checkType(context);
      VReport =  (CReferenceType) VReport.checkType(context);
      VReportColumn = (CReferenceType) VReportColumn.checkType(context);

      SActor = (CReferenceType) SActor.checkType(context);
      SDefaultActor = (CReferenceType) SDefaultActor.checkType(context);
      VList = (CReferenceType) VList.checkType(context);
      VException = (CReferenceType) VException.checkType(context);
      VRuntimeException = (CReferenceType) VRuntimeException.checkType(context);
      VCommand = (CReferenceType) VCommand.checkType(context);
      VPosition = (CReferenceType) VPosition.checkType(context);
      VColumn = (CReferenceType) VColumn.checkType(context);
      VListColumn = (CReferenceType) VListColumn.checkType(context);
      VStringColumn = (CReferenceType) VStringColumn.checkType(context);
      VFixedColumn = (CReferenceType) VFixedColumn.checkType(context);
      VIntegerColumn = (CReferenceType) VIntegerColumn.checkType(context);
      VDateColumn = (CReferenceType) VDateColumn.checkType(context);
      VMonthColumn = (CReferenceType) VMonthColumn.checkType(context);
      VTimestampColumn = (CReferenceType) VTimestampColumn.checkType(context);
      VWeekColumn = (CReferenceType) VWeekColumn.checkType(context);
      VTextColumn = (CReferenceType) VTextColumn.checkType(context);
      VBooleanColumn = (CReferenceType) VBooleanColumn.checkType(context);
      VBooleanCodeColumn = (CReferenceType) VBooleanCodeColumn.checkType(context);
      VIntegerCodeColumn = (CReferenceType) VIntegerCodeColumn.checkType(context);
      VFixedCodeColumn = (CReferenceType) VFixedCodeColumn.checkType(context);
      VColorColumn = (CReferenceType) VColorColumn.checkType(context);
      VImageColumn = (CReferenceType) VImageColumn.checkType(context);
      VEnumColumn = (CReferenceType) VEnumColumn.checkType(context);
    } catch (UnpositionedError cue) {
      compiler.reportTrouble(new PositionedError(TokenReference.NO_REF, BaseMessages.CANT_LOAD_CLASSES));
    }
  }
}
