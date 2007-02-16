/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
  public static CReferenceType FixedField;
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
  public static CReferenceType FixedCodeField;
  public static CReferenceType StringCodeField;
  public static CReferenceType TextField;

  public static CReferenceType StringColumn;
  public static CReferenceType IntegerColumn;
  public static CReferenceType FixedColumn;
  public static CReferenceType BooleanColumn;
  public static CReferenceType DateColumn;
  public static CReferenceType MonthColumn;
  public static CReferenceType TimeColumn;
  public static CReferenceType TimestampColumn;
  public static CReferenceType WeekColumn;
  public static CReferenceType BooleanCodeColumn;
  public static CReferenceType FixedCodeColumn;
  public static CReferenceType IntegerCodeColumn;
  public static CReferenceType StringCodeColumn;

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
    FixedField = tf.createType(com.kopiright.vkopi.lib.form.VFixedField.class.getName().replace('.','/'), false);
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
    FixedCodeField = tf.createType(com.kopiright.vkopi.lib.form.VFixedCodeField.class.getName().replace('.','/'), false);
    IntegerCodeField = tf.createType(com.kopiright.vkopi.lib.form.VIntegerCodeField.class.getName().replace('.','/'), false);
    StringCodeField = tf.createType(com.kopiright.vkopi.lib.form.VStringCodeField.class.getName().replace('.','/'), false);

    StringColumn = tf.createType(com.kopiright.vkopi.lib.report.VStringColumn.class.getName().replace('.','/'), false);
    IntegerColumn = tf.createType(com.kopiright.vkopi.lib.report.VIntegerColumn.class.getName().replace('.','/'), false);
    FixedColumn = tf.createType(com.kopiright.vkopi.lib.report.VFixedColumn.class.getName().replace('.','/'), false);
    BooleanColumn = tf.createType(com.kopiright.vkopi.lib.report.VBooleanColumn.class.getName().replace('.','/'), false);
    DateColumn = tf.createType(com.kopiright.vkopi.lib.report.VDateColumn.class.getName().replace('.','/'), false);
    MonthColumn = tf.createType(com.kopiright.vkopi.lib.report.VMonthColumn.class.getName().replace('.','/'), false);
    TimeColumn = tf.createType(com.kopiright.vkopi.lib.report.VTimeColumn.class.getName().replace('.','/'), false);
    TimestampColumn = tf.createType(com.kopiright.vkopi.lib.report.VTimestampColumn.class.getName().replace('.','/'), false);
    WeekColumn = tf.createType(com.kopiright.vkopi.lib.report.VWeekColumn.class.getName().replace('.','/'), false);
    BooleanCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VBooleanCodeColumn.class.getName().replace('.','/'), false);
    FixedCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VFixedCodeColumn.class.getName().replace('.','/'), false);
    IntegerCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VIntegerCodeColumn.class.getName().replace('.','/'), false);
    StringCodeColumn = tf.createType(com.kopiright.vkopi.lib.report.VStringCodeColumn.class.getName().replace('.','/'), false);

    Color = tf.createReferenceType(GTypeFactory.RFT_COLOR);
    Image = tf.createReferenceType(GTypeFactory.RFT_IMAGE);

    try {
      Field = (CReferenceType) Field.checkType(context);
      BooleanField = (CReferenceType) BooleanField.checkType(context);
      IntegerField = (CReferenceType) IntegerField.checkType(context);
      FixedField = (CReferenceType) FixedField.checkType(context);
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
      FixedCodeField = (CReferenceType) FixedCodeField.checkType(context);
      IntegerCodeField = (CReferenceType) IntegerCodeField.checkType(context);
      StringCodeField = (CReferenceType) StringCodeField.checkType(context);

      StringColumn = (CReferenceType) StringColumn.checkType(context);
      IntegerColumn = (CReferenceType) IntegerColumn.checkType(context);
      FixedColumn = (CReferenceType) FixedColumn.checkType(context);
      BooleanColumn =  (CReferenceType) BooleanColumn.checkType(context);
      DateColumn = (CReferenceType) DateColumn.checkType(context);
      MonthColumn =  (CReferenceType) MonthColumn.checkType(context);
      TimeColumn = (CReferenceType) TimeColumn.checkType(context);
      TimestampColumn = (CReferenceType) TimestampColumn.checkType(context);
      WeekColumn = (CReferenceType) WeekColumn.checkType(context);
      BooleanCodeColumn =  (CReferenceType) BooleanCodeColumn.checkType(context);
      FixedCodeColumn = (CReferenceType) FixedCodeColumn.checkType(context);
      IntegerCodeColumn = (CReferenceType) IntegerCodeColumn.checkType(context);
      StringCodeColumn = (CReferenceType) StringCodeColumn.checkType(context);

      Color = (CReferenceType) Color.checkType(context);
      Image = (CReferenceType) Image.checkType(context);
    } catch (UnpositionedError cue) {
      compiler.reportTrouble(new PositionedError(TokenReference.NO_REF, GKjcMessages.CANT_LOAD_CLASSES_ZIP));
    }
  }
}
