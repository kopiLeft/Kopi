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
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.kopi.comp.kjc.CBinaryTypeContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.TypeFactory;

public class VKStdType {
  public static CReferenceType VDictionary;
  public static CReferenceType VDictionaryForm;
  public static CReferenceType VReport;
  public static CReferenceType VChart;
  public static CReferenceType VForm;
  public static CReferenceType VBlock;
  public static CReferenceType VField;
  public static CReferenceType VReportColumn;
  public static CReferenceType VChartDimension;
  public static CReferenceType VChartMeasure;

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
  public static CReferenceType VFixnumColumn;
  public static CReferenceType VIntegerColumn;
  public static CReferenceType VDateColumn;
  public static CReferenceType VMonthColumn;
  public static CReferenceType VTimeColumn;
  public static CReferenceType VTimestampColumn;
  public static CReferenceType VWeekColumn;
  public static CReferenceType VTextColumn;
  public static CReferenceType VBooleanColumn;
  public static CReferenceType VBooleanCodeColumn;
  public static CReferenceType VFixnumCodeColumn;
  public static CReferenceType VIntegerCodeColumn;
  public static CReferenceType VStringCodeColumn;
  public static CReferenceType VColorColumn;
  public static CReferenceType VImageColumn;

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

    VField = tf.createType(org.kopi.vkopi.lib.form.VField.class.getName().replace('.','/'), false);
    VBlock = tf.createType(org.kopi.vkopi.lib.form.VBlock.class.getName().replace('.','/'), false);
    VDictionary = tf.createType(org.kopi.vkopi.lib.form.VDictionary.class.getName().replace('.','/'), false);
    VDictionaryForm = tf.createType(org.kopi.vkopi.lib.form.VDictionaryForm.class.getName().replace('.','/'), false);
    VForm = tf.createType(org.kopi.vkopi.lib.form.VForm.class.getName().replace('.','/'), false);
    VReport = tf.createType(org.kopi.vkopi.lib.report.VReport.class.getName().replace('.','/'), false);
    VReportColumn = tf.createType(org.kopi.vkopi.lib.report.VReportColumn.class.getName().replace('.','/'), false);
    VChart = tf.createType(org.kopi.vkopi.lib.chart.VChart.class.getName().replace('.','/'), false);
    VChartDimension = tf.createType(org.kopi.vkopi.lib.chart.VDimension.class.getName().replace('.','/'), false);
    VChartMeasure = tf.createType(org.kopi.vkopi.lib.chart.VMeasure.class.getName().replace('.','/'), false);

    SActor = tf.createType(org.kopi.vkopi.lib.visual.VActor.class.getName().replace('.','/'), false);
    SDefaultActor = tf.createType(org.kopi.vkopi.lib.visual.VDefaultActor.class.getName().replace('.','/'), false);
    VList = tf.createType(org.kopi.vkopi.lib.list.VList.class.getName().replace('.','/'), false);
    VException = tf.createType(org.kopi.vkopi.lib.visual.VException.class.getName().replace('.','/'), false);
    VRuntimeException = tf.createType(org.kopi.vkopi.lib.visual.VRuntimeException.class.getName().replace('.','/'), false);
    VCommand = tf.createType(org.kopi.vkopi.lib.visual.VCommand.class.getName().replace('.','/'), false);
    VPosition = tf.createType(org.kopi.vkopi.lib.form.VPosition.class.getName().replace('.','/'), false);
    VColumn = tf.createType(org.kopi.vkopi.lib.list.VColumn.class.getName().replace('.','/'), false);
    VListColumn = tf.createType(org.kopi.vkopi.lib.list.VListColumn.class.getName().replace('.','/'), false);
    VStringColumn = tf.createType(org.kopi.vkopi.lib.list.VStringColumn.class.getName().replace('.','/'), false);
    VFixnumColumn = tf.createType(org.kopi.vkopi.lib.list.VFixnumColumn.class.getName().replace('.','/'), false);
    VIntegerColumn = tf.createType(org.kopi.vkopi.lib.list.VIntegerColumn.class.getName().replace('.','/'), false);
    VDateColumn = tf.createType(org.kopi.vkopi.lib.list.VDateColumn.class.getName().replace('.','/'), false);
    VMonthColumn = tf.createType(org.kopi.vkopi.lib.list.VMonthColumn.class.getName().replace('.','/'), false);
    VTimeColumn = tf.createType(org.kopi.vkopi.lib.list.VTimeColumn.class.getName().replace('.','/'), false);
    VTimestampColumn = tf.createType(org.kopi.vkopi.lib.list.VTimestampColumn.class.getName().replace('.','/'), false);
    VWeekColumn = tf.createType(org.kopi.vkopi.lib.list.VWeekColumn.class.getName().replace('.','/'), false);
    VTextColumn = tf.createType(org.kopi.vkopi.lib.list.VTextColumn.class.getName().replace('.','/'), false);
    VBooleanColumn = tf.createType(org.kopi.vkopi.lib.list.VBooleanColumn.class.getName().replace('.','/'), false);
    VBooleanCodeColumn = tf.createType(org.kopi.vkopi.lib.list.VBooleanCodeColumn.class.getName().replace('.','/'), false);
    VFixnumCodeColumn = tf.createType(org.kopi.vkopi.lib.list.VFixnumCodeColumn.class.getName().replace('.','/'), false);
    VIntegerCodeColumn = tf.createType(org.kopi.vkopi.lib.list.VIntegerCodeColumn.class.getName().replace('.','/'), false);
    VStringCodeColumn = tf.createType(org.kopi.vkopi.lib.list.VStringCodeColumn.class.getName().replace('.','/'), false);
    VColorColumn = tf.createType(org.kopi.vkopi.lib.list.VColorColumn.class.getName().replace('.','/'), false);
    VImageColumn = tf.createType(org.kopi.vkopi.lib.list.VImageColumn.class.getName().replace('.','/'), false);
    try {
      VField = (CReferenceType) VField.checkType(context);
      VBlock = (CReferenceType) VBlock.checkType(context);
      VDictionary = (CReferenceType) VDictionary.checkType(context);
      VDictionaryForm = (CReferenceType) VDictionaryForm.checkType(context);
      VForm = (CReferenceType) VForm.checkType(context);
      VReport =  (CReferenceType) VReport.checkType(context);
      VReportColumn = (CReferenceType) VReportColumn.checkType(context);
      VChart =  (CReferenceType) VChart.checkType(context);
      VChartDimension = (CReferenceType) VChartDimension.checkType(context);
      VChartMeasure = (CReferenceType) VChartMeasure.checkType(context);

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
      VFixnumColumn = (CReferenceType) VFixnumColumn.checkType(context);
      VIntegerColumn = (CReferenceType) VIntegerColumn.checkType(context);
      VDateColumn = (CReferenceType) VDateColumn.checkType(context);
      VMonthColumn = (CReferenceType) VMonthColumn.checkType(context);
      VTimestampColumn = (CReferenceType) VTimestampColumn.checkType(context);
      VWeekColumn = (CReferenceType) VWeekColumn.checkType(context);
      VTextColumn = (CReferenceType) VTextColumn.checkType(context);
      VBooleanColumn = (CReferenceType) VBooleanColumn.checkType(context);
      VBooleanCodeColumn = (CReferenceType) VBooleanCodeColumn.checkType(context);
      VFixnumCodeColumn = (CReferenceType) VFixnumCodeColumn.checkType(context);
      VIntegerCodeColumn = (CReferenceType) VIntegerCodeColumn.checkType(context);
      VStringCodeColumn = (CReferenceType) VStringCodeColumn.checkType(context);
      VColorColumn = (CReferenceType) VColorColumn.checkType(context);
      VImageColumn = (CReferenceType) VImageColumn.checkType(context);
    } catch (UnpositionedError cue) {
      compiler.reportTrouble(new PositionedError(TokenReference.NO_REF, BaseMessages.CANT_LOAD_CLASSES));
    }
  }
}
