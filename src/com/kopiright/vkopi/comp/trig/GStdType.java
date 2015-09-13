/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.trig;

import com.kopiright.kopi.comp.kjc.Constants;
import com.kopiright.kopi.comp.kjc.CBinaryTypeContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;

/**
 * Root for type hierarchy
 */
public class GStdType extends com.kopiright.util.base.Utils implements Constants {

  // ----------------------------------------------------------------------
  // PRIMITIVE TYPES
  // ----------------------------------------------------------------------

  public static CReferenceType Form;
  public static CReferenceType Block;
  public static CReferenceType Field;
  public static CReferenceType BooleanField;
  public static CReferenceType IntegerField;
  public static CReferenceType FixnumField;
  public static CReferenceType StringField;
  public static CReferenceType ImageField;
  public static CReferenceType ColorField;
  public static CReferenceType DateField;
  public static CReferenceType MonthField;
  public static CReferenceType TimeField;
  public static CReferenceType TimestampField;
  public static CReferenceType WeekField;
  public static CReferenceType BooleanCodeField;
  public static CReferenceType IntegerCodeField;
  public static CReferenceType FixnumCodeField;
  public static CReferenceType StringCodeField;
  public static CReferenceType TextField;

  public static CReferenceType ReportStringColumn;
  public static CReferenceType ReportIntegerColumn;
  public static CReferenceType ReportFixnumColumn;
  public static CReferenceType ReportBooleanColumn;
  public static CReferenceType ReportDateColumn;
  public static CReferenceType ReportMonthColumn;
  public static CReferenceType ReportTimeColumn;
  public static CReferenceType ReportTimestampColumn;
  public static CReferenceType ReportWeekColumn;
  public static CReferenceType ReportBooleanCodeColumn;
  public static CReferenceType ReportFixnumCodeColumn;
  public static CReferenceType ReportIntegerCodeColumn;
  public static CReferenceType ReportStringCodeColumn;
  
  public static CReferenceType ChartStringDimension;
  public static CReferenceType ChartIntegerDimension;
  public static CReferenceType ChartFixnumDimension;
  public static CReferenceType ChartBooleanDimension;
  public static CReferenceType ChartDateDimension;
  public static CReferenceType ChartMonthDimension;
  public static CReferenceType ChartTimeDimension;
  public static CReferenceType ChartTimestampDimension;
  public static CReferenceType ChartWeekDimension;
  public static CReferenceType ChartBooleanCodeDimension;
  public static CReferenceType ChartFixnumCodeDimension;
  public static CReferenceType ChartIntegerCodeDimension;
  public static CReferenceType ChartStringCodeDimension;
  public static CReferenceType ChartIntegerMeasure;
  public static CReferenceType ChartFixnumMeasure;
  public static CReferenceType ChartFixnumCodeMeasure;
  public static CReferenceType ChartIntegerCodeMeasure;

  public static CReferenceType Color;
  public static CReferenceType Image;

  // ----------------------------------------------------------------------
  // INITIALIZERS
  // ----------------------------------------------------------------------

  /**
   * Initialize all constants
   */
  public static void init(CBinaryTypeContext context, Compiler compiler) {
    if (Field != null) {
      return;
    }
    TypeFactory tf = context.getTypeFactory();
    System.err.println();

    Form = tf.createType(com.kopiright.vkopi.lib.form.VForm.class.getName().replace('.','/'), false);
    Block = tf.createType(com.kopiright.vkopi.lib.form.VBlock.class.getName().replace('.','/'), false);
    Field = tf.createType(com.kopiright.vkopi.lib.form.VField.class.getName().replace('.','/'), false);
    BooleanField = tf.createType(com.kopiright.vkopi.lib.form.VBooleanField.class.getName().replace('.','/'), false);
    IntegerField = tf.createType(com.kopiright.vkopi.lib.form.VIntegerField.class.getName().replace('.','/'), false);
    FixnumField = tf.createType(com.kopiright.vkopi.lib.form.VFixnumField.class.getName().replace('.','/'), false);
    StringField = tf.createType(com.kopiright.vkopi.lib.form.VStringField.class.getName().replace('.','/'), false);
    ImageField = tf.createType(com.kopiright.vkopi.lib.form.VImageField.class.getName().replace('.','/'), false);
    ColorField = tf.createType(com.kopiright.vkopi.lib.form.VColorField.class.getName().replace('.','/'), false);
    DateField = tf.createType(com.kopiright.vkopi.lib.form.VDateField.class.getName().replace('.','/'), false);
    MonthField = tf.createType(com.kopiright.vkopi.lib.form.VMonthField.class.getName().replace('.','/'), false);
    TimeField = tf.createType(com.kopiright.vkopi.lib.form.VTimeField.class.getName().replace('.','/'), false);
    TimestampField = tf.createType(com.kopiright.vkopi.lib.form.VTimestampField.class.getName().replace('.','/'), false);
    WeekField = tf.createType(com.kopiright.vkopi.lib.form.VWeekField.class.getName().replace('.','/'), false);
    TextField = tf.createType(com.kopiright.vkopi.lib.form.VTextField.class.getName().replace('.','/'), false);
    BooleanCodeField = tf.createType(com.kopiright.vkopi.lib.form.VBooleanCodeField.class.getName().replace('.','/'), false);
    FixnumCodeField = tf.createType(com.kopiright.vkopi.lib.form.VFixnumCodeField.class.getName().replace('.','/'), false);
    IntegerCodeField = tf.createType(com.kopiright.vkopi.lib.form.VIntegerCodeField.class.getName().replace('.','/'), false);
    StringCodeField = tf.createType(com.kopiright.vkopi.lib.form.VStringCodeField.class.getName().replace('.','/'), false);

    ReportStringColumn = tf.createType(com.kopiright.vkopi.lib.report.VStringColumn.class.getName().replace('.','/'), false);
    ReportIntegerColumn = tf.createType(com.kopiright.vkopi.lib.report.VIntegerColumn.class.getName().replace('.','/'), false);
    ReportFixnumColumn = tf.createType(com.kopiright.vkopi.lib.report.VFixnumColumn.class.getName().replace('.','/'), false);
    ReportBooleanColumn = tf.createType(com.kopiright.vkopi.lib.report.VBooleanColumn.class.getName().replace('.','/'), false);
    ReportDateColumn = tf.createType(com.kopiright.vkopi.lib.report.VDateColumn.class.getName().replace('.','/'), false);
    ReportMonthColumn = tf.createType(com.kopiright.vkopi.lib.report.VMonthColumn.class.getName().replace('.','/'), false);
    ReportTimeColumn = tf.createType(com.kopiright.vkopi.lib.report.VTimeColumn.class.getName().replace('.','/'), false);
    ReportTimestampColumn = tf.createType(com.kopiright.vkopi.lib.report.VTimestampColumn.class.getName().replace('.','/'), false);
    ReportWeekColumn = tf.createType(com.kopiright.vkopi.lib.report.VWeekColumn.class.getName().replace('.','/'), false);
    ReportBooleanCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VBooleanCodeColumn.class.getName().replace('.','/'), false);
    ReportFixnumCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VFixnumCodeColumn.class.getName().replace('.','/'), false);
    ReportIntegerCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VIntegerCodeColumn.class.getName().replace('.','/'), false);
    ReportStringCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VStringCodeColumn.class.getName().replace('.','/'), false);
    
    ChartStringDimension = tf.createType(com.kopiright.vkopi.lib.chart.VStringDimension.class.getName().replace('.','/'), false);
    ChartIntegerDimension = tf.createType(com.kopiright.vkopi.lib.chart.VIntegerDimension.class.getName().replace('.','/'), false);
    ChartFixnumDimension = tf.createType(com.kopiright.vkopi.lib.chart.VFixnumDimension.class.getName().replace('.','/'), false);
    ChartBooleanDimension = tf.createType(com.kopiright.vkopi.lib.chart.VBooleanDimension.class.getName().replace('.','/'), false);
    ChartDateDimension = tf.createType(com.kopiright.vkopi.lib.chart.VDateDimension.class.getName().replace('.','/'), false);
    ChartMonthDimension = tf.createType(com.kopiright.vkopi.lib.chart.VMonthDimension.class.getName().replace('.','/'), false);
    ChartTimeDimension = tf.createType(com.kopiright.vkopi.lib.chart.VTimeDimension.class.getName().replace('.','/'), false);
    ChartTimestampDimension = tf.createType(com.kopiright.vkopi.lib.chart.VTimestampDimension.class.getName().replace('.','/'), false);
    ChartWeekDimension = tf.createType(com.kopiright.vkopi.lib.chart.VWeekDimension.class.getName().replace('.','/'), false);
    ChartBooleanCodeDimension = tf.createType(com.kopiright.vkopi.lib.chart.VBooleanCodeDimension.class.getName().replace('.','/'), false);
    ChartFixnumCodeDimension = tf.createType(com.kopiright.vkopi.lib.chart.VFixnumCodeDimension.class.getName().replace('.','/'), false);
    ChartIntegerCodeDimension = tf.createType(com.kopiright.vkopi.lib.chart.VIntegerCodeDimension.class.getName().replace('.','/'), false);
    ChartStringCodeDimension = tf.createType(com.kopiright.vkopi.lib.chart.VStringCodeDimension.class.getName().replace('.','/'), false);
    ChartIntegerMeasure =  tf.createType(com.kopiright.vkopi.lib.chart.VIntegerMeasure.class.getName().replace('.','/'), false);
    ChartFixnumMeasure = tf.createType(com.kopiright.vkopi.lib.chart.VFixnumMeasure.class.getName().replace('.','/'), false);
    ChartFixnumCodeMeasure = tf.createType(com.kopiright.vkopi.lib.chart.VFixnumCodeMeasure.class.getName().replace('.','/'), false);
    ChartIntegerCodeMeasure = tf.createType(com.kopiright.vkopi.lib.chart.VIntegerCodeMeasure.class.getName().replace('.','/'), false);

    Color = tf.createReferenceType(GTypeFactory.RFT_COLOR);
    Image = tf.createReferenceType(GTypeFactory.RFT_IMAGE);

    try {
      Field = (CReferenceType) Field.checkType(context);
      BooleanField = (CReferenceType) BooleanField.checkType(context);
      IntegerField = (CReferenceType) IntegerField.checkType(context);
      FixnumField = (CReferenceType) FixnumField.checkType(context);
      StringField = (CReferenceType) StringField.checkType(context);
      ImageField = (CReferenceType) ImageField.checkType(context);
      ColorField = (CReferenceType) ColorField.checkType(context);
      DateField =  (CReferenceType) DateField.checkType(context);
      MonthField = (CReferenceType) MonthField.checkType(context);
      TimeField = (CReferenceType) TimeField.checkType(context);
      TimestampField = (CReferenceType) TimestampField.checkType(context);
      WeekField = (CReferenceType) WeekField.checkType(context);
      TextField = (CReferenceType) TextField.checkType(context);
      BooleanCodeField = (CReferenceType) BooleanCodeField.checkType(context);
      FixnumCodeField = (CReferenceType) FixnumCodeField.checkType(context);
      IntegerCodeField = (CReferenceType) IntegerCodeField.checkType(context);
      StringCodeField = (CReferenceType) StringCodeField.checkType(context);

      ReportStringColumn = (CReferenceType) ReportStringColumn.checkType(context);
      ReportIntegerColumn = (CReferenceType) ReportIntegerColumn.checkType(context);
      ReportFixnumColumn = (CReferenceType) ReportFixnumColumn.checkType(context);
      ReportBooleanColumn =  (CReferenceType) ReportBooleanColumn.checkType(context);
      ReportDateColumn = (CReferenceType) ReportDateColumn.checkType(context);
      ReportMonthColumn =  (CReferenceType) ReportMonthColumn.checkType(context);
      ReportTimeColumn = (CReferenceType) ReportTimeColumn.checkType(context);
      ReportTimestampColumn = (CReferenceType) ReportTimestampColumn.checkType(context);
      ReportWeekColumn = (CReferenceType) ReportWeekColumn.checkType(context);
      ReportBooleanCodeColumn =  (CReferenceType) ReportBooleanCodeColumn.checkType(context);
      ReportFixnumCodeColumn = (CReferenceType) ReportFixnumCodeColumn.checkType(context);
      ReportIntegerCodeColumn = (CReferenceType) ReportIntegerCodeColumn.checkType(context);
      ReportStringCodeColumn = (CReferenceType) ReportStringCodeColumn.checkType(context);
      
      ChartStringDimension = (CReferenceType) ChartStringDimension.checkType(context);
      ChartIntegerDimension = (CReferenceType) ChartIntegerDimension.checkType(context);
      ChartFixnumDimension = (CReferenceType) ChartFixnumDimension.checkType(context);
      ChartBooleanDimension =  (CReferenceType) ChartBooleanDimension.checkType(context);
      ChartDateDimension = (CReferenceType) ChartDateDimension.checkType(context);
      ChartMonthDimension =  (CReferenceType) ChartMonthDimension.checkType(context);
      ChartTimeDimension = (CReferenceType) ChartTimeDimension.checkType(context);
      ChartTimestampDimension = (CReferenceType) ChartTimestampDimension.checkType(context);
      ChartWeekDimension = (CReferenceType) ChartWeekDimension.checkType(context);
      ChartBooleanCodeDimension =  (CReferenceType) ChartBooleanCodeDimension.checkType(context);
      ChartFixnumCodeDimension = (CReferenceType) ChartFixnumCodeDimension.checkType(context);
      ChartIntegerCodeDimension = (CReferenceType) ChartIntegerCodeDimension.checkType(context);
      ChartStringCodeDimension = (CReferenceType) ChartStringCodeDimension.checkType(context);
      ChartIntegerMeasure =  (CReferenceType) ChartIntegerMeasure.checkType(context);
      ChartFixnumMeasure = (CReferenceType) ChartFixnumMeasure.checkType(context);
      ChartFixnumCodeMeasure = (CReferenceType) ChartFixnumCodeMeasure.checkType(context);
      ChartIntegerCodeMeasure = (CReferenceType) ChartIntegerCodeMeasure.checkType(context);

      Color = (CReferenceType) Color.checkType(context);
      Image = (CReferenceType) Image.checkType(context);
    } catch (UnpositionedError cue) {
      compiler.reportTrouble(new PositionedError(TokenReference.NO_REF, GKjcMessages.CANT_LOAD_CLASSES_ZIP));
    }
  }
}
