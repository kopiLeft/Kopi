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

package at.dms.vkopi.comp.trig;

import java.util.Hashtable;
import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.InconsistencyException;

import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.ClassReader;


/**
 * Factory for visual Java Types
 */
public class GKjcTypeFactory extends at.dms.xkopi.comp.xkjc.XKjcTypeFactory implements GTypeFactory, at.dms.kopi.comp.kjc.Constants {

  public GKjcTypeFactory (Compiler compiler, ClassReader reader, boolean genericEnabled) {
    super(compiler, reader, genericEnabled);

    formType = createType(at.dms.vkopi.lib.form.VForm.class.getName().replace('.','/'), false);
    blockType = createType(at.dms.vkopi.lib.form.VBlock.class.getName().replace('.','/'), false);
    fieldType = createType(at.dms.vkopi.lib.form.VField.class.getName().replace('.','/'), false);
    enumFieldType = createType(at.dms.vkopi.lib.form.VEnumField.class.getName().replace('.','/'), false);
    booleanFieldType = createType(at.dms.vkopi.lib.form.VBooleanField.class.getName().replace('.','/'), false);
    integerFieldType = createType(at.dms.vkopi.lib.form.VIntegerField.class.getName().replace('.','/'), false);
    fixedFieldType = createType(at.dms.vkopi.lib.form.VFixedField.class.getName().replace('.','/'), false);
    stringFieldType = createType(at.dms.vkopi.lib.form.VStringField.class.getName().replace('.','/'), false);
    imageFieldType = createType(at.dms.vkopi.lib.form.VImageField.class.getName().replace('.','/'), false);
    colorFieldType = createType(at.dms.vkopi.lib.form.VColorField.class.getName().replace('.','/'), false);
    dateFieldType = createType(at.dms.vkopi.lib.form.VDateField.class.getName().replace('.','/'), false);
    monthFieldType = createType(at.dms.vkopi.lib.form.VMonthField.class.getName().replace('.','/'), false);
    timeFieldType = createType(at.dms.vkopi.lib.form.VTimeField.class.getName().replace('.','/'), false);
    weekFieldType = createType(at.dms.vkopi.lib.form.VWeekField.class.getName().replace('.','/'), false);
    textFieldType = createType(at.dms.vkopi.lib.form.VTextField.class.getName().replace('.','/'), false);
    fixedCodeFieldType = createType(at.dms.vkopi.lib.form.VFixedCodeField.class.getName().replace('.','/'), false);
    booleanCodeFieldType = createType(at.dms.vkopi.lib.form.VBooleanCodeField.class.getName().replace('.','/'), false);
    integerCodeFieldType = createType(at.dms.vkopi.lib.form.VIntegerCodeField.class.getName().replace('.','/'), false);

    stringColumnType = createType(at.dms.vkopi.lib.report.VStringColumn.class.getName().replace('.','/'), false);
    integerColumnType = createType(at.dms.vkopi.lib.report.VIntegerColumn.class.getName().replace('.','/'), false);
    fixedColumnType = createType(at.dms.vkopi.lib.report.VFixedColumn.class.getName().replace('.','/'), false);
    booleanColumnType = createType(at.dms.vkopi.lib.report.VBooleanColumn.class.getName().replace('.','/'), false);
    dateColumnType = createType(at.dms.vkopi.lib.report.VDateColumn.class.getName().replace('.','/'), false);
    monthColumnType = createType(at.dms.vkopi.lib.report.VMonthColumn.class.getName().replace('.','/'), false);
    timeColumnType = createType(at.dms.vkopi.lib.report.VTimeColumn.class.getName().replace('.','/'), false);
    weekColumnType = createType(at.dms.vkopi.lib.report.VWeekColumn.class.getName().replace('.','/'), false);
    booleanCodeColumnType = createType(at.dms.vkopi.lib.report.VBooleanCodeColumn.class.getName().replace('.','/'), false);
    integerCodeColumnType = createType(at.dms.vkopi.lib.report.VIntegerCodeColumn.class.getName().replace('.','/'), false);
    fixedCodeColumnType = createType(at.dms.vkopi.lib.report.VFixedCodeColumn.class.getName().replace('.','/'), false);

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
    case RFT_ENUMFIELD:
      return enumFieldType;
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
  private CReferenceType        enumFieldType;
  private CReferenceType        booleanCodeFieldType;
  private CReferenceType        integerCodeFieldType;
  private CReferenceType        fixedCodeFieldType;
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
