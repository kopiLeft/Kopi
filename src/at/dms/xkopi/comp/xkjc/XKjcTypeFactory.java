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

package at.dms.xkopi.comp.xkjc;

import java.util.Hashtable;
import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.InconsistencyException;

import at.dms.kopi.comp.kjc.*;
import at.dms.kopi.comp.kjc.ClassReader;
import at.dms.kopi.comp.kjc.KjcTypeFactory;

/**
 * Factory for extended Java Types
 */
public class XKjcTypeFactory extends KjcTypeFactory implements XTypeFactory, XConstants {

  public XKjcTypeFactory (Compiler compiler, ClassReader reader, boolean genericEnabled) {
    super(compiler, reader, genericEnabled);

    nullType            = new XNullType();

    numberType          = createType(JAV_NUMBER, true);
    booleanType         = createType(JAV_BOOLEAN, true);
    byteType            = createType(JAV_BYTE, true);
    characterType       = createType(JAV_CHAR, true);
    doubleType          = createType(JAV_DOUBLE, true);
    floatType           = createType(JAV_FLOAT, true);
    integerType         = createType(JAV_INTEGER, true);
    longType            = createType(JAV_LONG, true);
    shortType           = createType(JAV_SHORT, true);

    dateType            = createType(XKJ_DATE, true);
    monthType           = createType(XKJ_MONTH, true);
    timeType            = createType(XKJ_TIME, true);
    timestampType       = createType(XKJ_TIMESTAMP, true);
    weekType            = createType(XKJ_WEEK, true);
    fixedType           = createType(XKJ_FIXED, true);

//     pDateType           = createType(JAV_PDATE, true);
//     pMonthType          = createType(JAV_PMONTH, true);
//     pTimeType           = createType(JAV_PTIME, true);
//     pTimestampType      = createType(JAV_PTIMESTAMP, true);
//     pWeekType           = createType(JAV_PWEEK, true);
//     pFixedType          = createType(JAV_PFIXED, true);

    pDateType           = new XDateType();
    pFixedType          = new XFixedType();
    pMonthType          = new XMonthType();
    pTimeType           = new XTimeType();
    pTimestampType      = new XTimestampType();
    pWeekType           = new XWeekType();

//     colorType           = createType(JAV_COLOR, true);
//     imageType           = createType(JAV_IMAGE, true);

    cursorType          = createType(XKJ_CURSOR, true);
    connectionType      = createType(XKJ_CONNECTION, true);
    dBContextHandlerType = createType(XKJ_DBCONTEXTHANDLER, true);
    kopiSerializableType = createType(XKJ_KOPISERIALIZABLE, true);

    try {
      numberType        = (CReferenceType)numberType.checkType(context);
      booleanType       = (CReferenceType)booleanType.checkType(context);
      byteType          = (CReferenceType)byteType.checkType(context);
      characterType     = (CReferenceType)characterType.checkType(context);
      doubleType        = (CReferenceType)doubleType.checkType(context);
      floatType         = (CReferenceType)floatType.checkType(context);
      integerType       = (CReferenceType)integerType.checkType(context);
      longType          = (CReferenceType)longType.checkType(context);
      shortType         = (CReferenceType)shortType.checkType(context);

      dateType          = (CReferenceType)dateType.checkType(context);
      monthType         = (CReferenceType)monthType.checkType(context);
      timeType          = (CReferenceType)timeType.checkType(context);
      timestampType     = (CReferenceType)timestampType.checkType(context);
      weekType          = (CReferenceType)weekType.checkType(context);
      fixedType         = (CReferenceType)fixedType.checkType(context);

      pDateType         = (CReferenceType)pDateType.checkType(context);
      pMonthType        = (CReferenceType)pMonthType.checkType(context);
      pTimeType         = (CReferenceType)pTimeType.checkType(context);
      pTimestampType    = (CReferenceType)pTimestampType.checkType(context);
      pWeekType         = (CReferenceType)pWeekType.checkType(context);
      pFixedType        = (CReferenceType)pFixedType.checkType(context);

//       colorType         = (CReferenceType)colorType.checkType(context);
//       imageType         = (CReferenceType)imageType.checkType(context);

      cursorType        = (CReferenceType)cursorType.checkType(context);
      connectionType    = (CReferenceType)connectionType.checkType(context);
      dBContextHandlerType = (CReferenceType)dBContextHandlerType.checkType(context);
      kopiSerializableType = (CReferenceType)kopiSerializableType.checkType(context);
    } catch (UnpositionedError e){
      throw new InconsistencyException("Failure while loading extended types.");
    }

    addKnownTypes(JAV_NUMBER, numberType);
    addKnownTypes(JAV_BOOLEAN, booleanType);
    addKnownTypes(JAV_BYTE, byteType);
    addKnownTypes(JAV_CHAR, characterType);
    addKnownTypes(JAV_DOUBLE, doubleType);
    addKnownTypes(JAV_FLOAT, floatType);
    addKnownTypes(JAV_INTEGER, integerType);
    addKnownTypes(JAV_SHORT, shortType);
    addKnownTypes(JAV_LONG, longType);

    addKnownTypes(XKJ_DATE, dateType);
    addKnownTypes(XKJ_MONTH, monthType);
    addKnownTypes(XKJ_TIME, timeType);
    addKnownTypes(XKJ_TIMESTAMP, timestampType);
    addKnownTypes(XKJ_WEEK, weekType);
    addKnownTypes(XKJ_FIXED, fixedType);

//     addKnownTypes(JAV_PDATE, pDateType);
//     addKnownTypes(JAV_PMONTH, pMonthType);
//     addKnownTypes(JAV_PTIME, pTimeType);
//     addKnownTypes(JAV_PTIMESTAMP, pTimestampType);
//     addKnownTypes(JAV_PWEEK, pWeekType);
//     addKnownTypes(JAV_PFIXED, pFixedType);

//     addKnownTypes(JAV_COLOR, colorType);
//     addKnownTypes(JAV_IMAGE, imageType);

    addKnownTypes(XKJ_CURSOR, cursorType);
    addKnownTypes(XKJ_CONNECTION, connectionType);
    addKnownTypes(XKJ_DBCONTEXTHANDLER, dBContextHandlerType);
    addKnownTypes(XKJ_KOPISERIALIZABLE, kopiSerializableType);

  }

  public CNullType getNullType() {
    return nullType;
  }

  public CReferenceType createReferenceType(int typeShortcut) {
    switch(typeShortcut){
      // ----------------------------------------------------------------------
      // EXTENDED JAVA TYPES
      // ----------------------------------------------------------------------
   case RFT_NUMBER:
      return numberType;
   case RFT_BOOLEAN:
      return booleanType;
    case RFT_BYTE:
      return byteType;
    case RFT_CHARACTER:
      return characterType;
    case RFT_DOUBLE:
      return doubleType;
    case RFT_FLOAT:
      return floatType;
    case RFT_INTEGER:
      return integerType;
    case RFT_LONG:
      return longType;
    case RFT_SHORT:
      return shortType;

    case RFT_DATE:
      return dateType;
    case RFT_MONTH:
      return monthType;
    case RFT_TIME:
      return timeType;
    case RFT_TIMESTAMP:
      return timestampType;
    case RFT_WEEK:
      return weekType;
    case RFT_FIXED:
      return fixedType;

    case PRM_PDATE:
      return pDateType;
    case PRM_PMONTH:
      return pMonthType;
    case PRM_PTIME:
      return pTimeType;
    case PRM_PTIMESTAMP:
      return pTimestampType;
    case PRM_PWEEK:
      return pWeekType;
    case PRM_PFIXED:
      return pFixedType;

//     case RFT_COLOR:
//       return colorType;
//     case RFT_IMAGE:
//       return imageType;

    case RFT_CURSOR:
      return cursorType;
    case RFT_CONNECTION:
      return connectionType;
    case RFT_DBCONTEXTHANDLER:
      return dBContextHandlerType;
    case RFT_KOPISERIALIZABLE:
      return kopiSerializableType;

    default:
      return super.createReferenceType(typeShortcut);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CReferenceType	numberType;
  private CReferenceType	booleanType;
  private CReferenceType	byteType;
  private CReferenceType	characterType;
  private CReferenceType	doubleType;
  private CReferenceType	floatType;
  private CReferenceType	integerType;
  private CReferenceType	longType;
  private CReferenceType	shortType;

  private CReferenceType	dateType;
  private CReferenceType	monthType;
  private CReferenceType	timeType;
  private CReferenceType	timestampType;
  private CReferenceType	weekType;
  private CReferenceType	fixedType;

  private CReferenceType	pDateType;
  private CReferenceType	pMonthType;
  private CReferenceType	pTimeType;
  private CReferenceType	pTimestampType;
  private CReferenceType	pWeekType;
  private CReferenceType	pFixedType;

  private CReferenceType        colorType;
  private CReferenceType        imageType;

  private CReferenceType	cursorType;
  private CReferenceType	connectionType;
  private CReferenceType	dBContextHandlerType;
  private CReferenceType	kopiSerializableType;

  private CNullType             nullType;

  class XNullType extends CNullType {
    /**
     * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
     * @param	dest		the destination type
     * @return	true iff the conversion is valid
     */
    public boolean isAssignableTo(CTypeContext context, CType dest) {
      if (dest instanceof XPrimitiveClassType) {
        return false;
      } else {
        return dest.isReference();
      }
    }
  }
}
