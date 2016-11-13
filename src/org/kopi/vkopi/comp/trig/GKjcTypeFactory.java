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

package org.kopi.vkopi.comp.trig;

import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.util.base.InconsistencyException;

import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.ClassReader;


/**
 * Factory for visual Java Types
 */
public class GKjcTypeFactory
  extends org.kopi.xkopi.comp.xkjc.XKjcTypeFactory
  implements GTypeFactory, org.kopi.kopi.comp.kjc.Constants
{
  public GKjcTypeFactory (Compiler compiler, ClassReader reader, boolean genericEnabled) {
    super(compiler, reader, genericEnabled);

    formType = createType(org.kopi.vkopi.lib.form.VForm.class.getName().replace('.','/'), false);
    blockType = createType(org.kopi.vkopi.lib.form.VBlock.class.getName().replace('.','/'), false);
    fieldType = createType(org.kopi.vkopi.lib.form.VField.class.getName().replace('.','/'), false);
    booleanFieldType = createType(org.kopi.vkopi.lib.form.VBooleanField.class.getName().replace('.','/'), false);
    integerFieldType = createType(org.kopi.vkopi.lib.form.VIntegerField.class.getName().replace('.','/'), false);
    fixedFieldType = createType(org.kopi.vkopi.lib.form.VFixnumField.class.getName().replace('.','/'), false);
    stringFieldType = createType(org.kopi.vkopi.lib.form.VStringField.class.getName().replace('.','/'), false);
    imageFieldType = createType(org.kopi.vkopi.lib.form.VImageField.class.getName().replace('.','/'), false);
    colorFieldType = createType(org.kopi.vkopi.lib.form.VColorField.class.getName().replace('.','/'), false);
    dateFieldType = createType(org.kopi.vkopi.lib.form.VDateField.class.getName().replace('.','/'), false);
    monthFieldType = createType(org.kopi.vkopi.lib.form.VMonthField.class.getName().replace('.','/'), false);
    timeFieldType = createType(org.kopi.vkopi.lib.form.VTimeField.class.getName().replace('.','/'), false);
    weekFieldType = createType(org.kopi.vkopi.lib.form.VWeekField.class.getName().replace('.','/'), false);
    textFieldType = createType(org.kopi.vkopi.lib.form.VTextField.class.getName().replace('.','/'), false);
    booleanCodeFieldType = createType(org.kopi.vkopi.lib.form.VBooleanCodeField.class.getName().replace('.','/'), false);
    fixedCodeFieldType = createType(org.kopi.vkopi.lib.form.VFixnumCodeField.class.getName().replace('.','/'), false);
    integerCodeFieldType = createType(org.kopi.vkopi.lib.form.VIntegerCodeField.class.getName().replace('.','/'), false);
    stringCodeFieldType = createType(org.kopi.vkopi.lib.form.VStringCodeField.class.getName().replace('.','/'), false);

    stringColumnType = createType(org.kopi.vkopi.lib.report.VStringColumn.class.getName().replace('.','/'), false);
    integerColumnType = createType(org.kopi.vkopi.lib.report.VIntegerColumn.class.getName().replace('.','/'), false);
    fixedColumnType = createType(org.kopi.vkopi.lib.report.VFixnumColumn.class.getName().replace('.','/'), false);
    booleanColumnType = createType(org.kopi.vkopi.lib.report.VBooleanColumn.class.getName().replace('.','/'), false);
    dateColumnType = createType(org.kopi.vkopi.lib.report.VDateColumn.class.getName().replace('.','/'), false);
    monthColumnType = createType(org.kopi.vkopi.lib.report.VMonthColumn.class.getName().replace('.','/'), false);
    timeColumnType = createType(org.kopi.vkopi.lib.report.VTimeColumn.class.getName().replace('.','/'), false);
    weekColumnType = createType(org.kopi.vkopi.lib.report.VWeekColumn.class.getName().replace('.','/'), false);
    booleanCodeColumnType = createType(org.kopi.vkopi.lib.report.VBooleanCodeColumn.class.getName().replace('.','/'), false);
    fixedCodeColumnType = createType(org.kopi.vkopi.lib.report.VFixnumCodeColumn.class.getName().replace('.','/'), false);
    integerCodeColumnType = createType(org.kopi.vkopi.lib.report.VIntegerCodeColumn.class.getName().replace('.','/'), false);

    colorType = createType(JAV_COLOR, true);
    imageType = createType(JAV_IMAGE, true);

    try {
      fieldType = (CReferenceType) fieldType.checkType(context);
      booleanFieldType = (CReferenceType) booleanFieldType.checkType(context);
      integerFieldType = (CReferenceType) integerFieldType.checkType(context);
      fixedFieldType = (CReferenceType) fixedFieldType.checkType(context);
      stringFieldType = (CReferenceType) stringFieldType.checkType(context);
      imageFieldType = (CReferenceType) imageFieldType.checkType(context);
      colorFieldType = (CReferenceType) colorFieldType.checkType(context);
      dateFieldType =  (CReferenceType) dateFieldType.checkType(context);
      monthFieldType = (CReferenceType) monthFieldType.checkType(context);
      timeFieldType = (CReferenceType) timeFieldType.checkType(context);
      weekFieldType = (CReferenceType) weekFieldType.checkType(context);
      textFieldType = (CReferenceType) textFieldType.checkType(context);
      fixedCodeFieldType = (CReferenceType) fixedCodeFieldType.checkType(context);
      booleanCodeFieldType = (CReferenceType) booleanCodeFieldType.checkType(context);
      integerCodeFieldType = (CReferenceType) integerCodeFieldType.checkType(context);

      stringColumnType = (CReferenceType) stringColumnType.checkType(context);
      integerColumnType = (CReferenceType) integerColumnType.checkType(context);
      fixedColumnType = (CReferenceType) fixedColumnType.checkType(context);
      booleanColumnType =  (CReferenceType) booleanColumnType.checkType(context);
      dateColumnType = (CReferenceType) dateColumnType.checkType(context);
      monthColumnType =  (CReferenceType) monthColumnType.checkType(context);
      timeColumnType = (CReferenceType) timeColumnType.checkType(context);
      weekColumnType = (CReferenceType) weekColumnType.checkType(context);
      booleanCodeColumnType =  (CReferenceType) booleanCodeColumnType.checkType(context);
      integerCodeColumnType = (CReferenceType) integerCodeColumnType.checkType(context);
      fixedCodeColumnType = (CReferenceType) fixedCodeColumnType.checkType(context);

      colorType = (CReferenceType) colorType.checkType(context);
      imageType = (CReferenceType) imageType.checkType(context);
    } catch (UnpositionedError cue) {
      throw new InconsistencyException("Failure while loading standard types.");
    }
  

    addKnownTypes(JAV_COLOR, colorType);
    addKnownTypes(JAV_IMAGE, imageType);
  }

  public CReferenceType createReferenceType(int typeShortcut) {
    switch(typeShortcut){
      // ----------------------------------------------------------------------
      // VISUAL KOPI TYPES
      // ----------------------------------------------------------------------
    case RFT_FORM:
      return formType;
    case RFT_BLOCK:
      return blockType;
    case RFT_FIELD:
      return fieldType;
    case RFT_BOOLEANFIELD:
      return booleanFieldType;
    case RFT_INTEGERFIELD:
      return integerFieldType;
    case RFT_FIXEDFIELD:
      return fixedFieldType;
    case RFT_STRINGFIELD:
      return stringFieldType;
    case RFT_IMAGEFIELD:
      return imageFieldType;
    case RFT_COLORFIELD:
      return colorFieldType;
    case RFT_DATEFIELD:
      return dateFieldType;
    case RFT_MONTHFIELD:
      return monthFieldType;
    case RFT_TIMEFIELD:
      return timeFieldType;
    case RFT_WEEKFIELD:
      return weekFieldType;
    case RFT_STRINGCODEFIELD:
      return stringCodeFieldType;
    case RFT_BOOLEANCODEFIELD:
      return booleanCodeFieldType;
    case RFT_INTEGERCODEFIELD:
      return integerCodeFieldType;
    case RFT_FIXEDCODEFIELD:
      return fixedCodeFieldType;
    case RFT_TEXTFIELD:
      return textFieldType;

    case RFT_COLOR:
      return colorType;
    case RFT_IMAGE:
      return imageType;

      // ----------------------------------------------------------------------
      // REPORT TYPES
      // ----------------------------------------------------------------------
    case RFT_STRINGCOLUMN:
      return stringColumnType;
    case RFT_INTEGERCOLUMN:
      return integerColumnType;
    case RFT_FIXEDCOLUMN:
      return fixedColumnType;
    case RFT_BOOLEANCOLUMN:
      return booleanColumnType;
    case RFT_DATECOLUMN:
      return dateColumnType;
    case RFT_MONTHCOLUMN:
      return monthColumnType;
    case RFT_TIMECOLUMN:
      return timeColumnType;
    case RFT_WEEKCOLUMN:
      return weekColumnType;
    case RFT_BOOLEANCODECOLUMN:
      return booleanCodeColumnType;
    case RFT_INTEGERCODECOLUMN:
      return integerCodeColumnType;
    case RFT_FIXEDCODECOLUMN:
      return fixedCodeColumnType;

    default:
      return super.createReferenceType(typeShortcut);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CReferenceType        formType;
  private CReferenceType        blockType;
  private CReferenceType        fieldType;
  private CReferenceType        booleanFieldType;
  private CReferenceType        integerFieldType;
  private CReferenceType        fixedFieldType;
  private CReferenceType        stringFieldType;
  private CReferenceType        imageFieldType;
  private CReferenceType        colorFieldType;
  private CReferenceType        dateFieldType;
  private CReferenceType        monthFieldType;
  private CReferenceType        timeFieldType;
  private CReferenceType        weekFieldType;
  private CReferenceType        booleanCodeFieldType;
  private CReferenceType        fixedCodeFieldType;
  private CReferenceType        integerCodeFieldType;
  private CReferenceType        stringCodeFieldType;
  private CReferenceType        textFieldType;

  private CReferenceType        stringColumnType;
  private CReferenceType        integerColumnType;
  private CReferenceType        fixedColumnType;
  private CReferenceType        booleanColumnType;
  private CReferenceType        dateColumnType;
  private CReferenceType        monthColumnType;
  private CReferenceType        timeColumnType;
  private CReferenceType        weekColumnType;
  private CReferenceType        booleanCodeColumnType;
  private CReferenceType        integerCodeColumnType;
  private CReferenceType        fixedCodeColumnType;

  private CReferenceType	colorType;
  private CReferenceType	imageType;
}
