/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.xkjc;

import com.kopiright.kopi.comp.kjc.CBinaryTypeContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.CStdType;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;

/**
 * library of XType
 */
public class XStdType /*extends com.kopiright.util.base.Utils*/ implements XConstants {

  // ----------------------------------------------------------------------
  // PRIMITIVE Sql TYPES
  // ----------------------------------------------------------------------

  public static CReferenceType	Boolean;
  public static CReferenceType	Byte;
  public static CReferenceType	Short;
  public static CReferenceType	Int;
  public static CReferenceType	Long;
  public static CReferenceType	Float;
  public static CReferenceType	Double;
  public static CReferenceType	Char;
  public static CReferenceType	Date;
  public static CReferenceType	Month;
  public static CReferenceType	Time;
  public static CReferenceType	Timestamp;
  public static CReferenceType	Week;
  public static CReferenceType	Fixed;

  public static XDateType	PDate;
  public static XMonthType	PMonth;
  public static XTimeType	PTime;
  public static XTimestampType	PTimestamp;
  public static XWeekType	PWeek;
  public static XFixedType	PFixed;

  public static CReferenceType	Number;
  public static CReferenceType  Color;
  public static CReferenceType  Image;


  // ----------------------------------------------------------------------
  // KOPI UTILS TYPES
  // ----------------------------------------------------------------------

  public static CReferenceType	Cursor;
  public static CReferenceType	Connection;
  public static CReferenceType	DBContextHandler;
  public static CReferenceType	KopiSerializable;

  // ----------------------------------------------------------------------
  // INITIALIZERS
  // ----------------------------------------------------------------------

  /**
   * Initialize all constants
   */
  public static void init(Compiler compiler, CBinaryTypeContext context) {
    TypeFactory         tf = context.getTypeFactory();

    Cursor = tf.createType(XConstants.XKJ_CURSOR, false);
    Connection = tf.createType(XConstants.XKJ_CONNECTION, false);
    DBContextHandler = tf.createType(XConstants.XKJ_DBCONTEXTHANDLER, false);
    KopiSerializable = tf.createType(XConstants.XKJ_KOPISERIALIZABLE, false);

    Boolean = tf.createReferenceType(XTypeFactory.RFT_BOOLEAN);
    Byte = tf.createReferenceType(XTypeFactory.RFT_BYTE);
    Short = tf.createReferenceType(XTypeFactory.RFT_SHORT);
    Int = tf.createReferenceType(XTypeFactory.RFT_INTEGER);
    Long = tf.createReferenceType(XTypeFactory.RFT_LONG);
    Float = tf.createReferenceType(XTypeFactory.RFT_FLOAT);
    Double = tf.createReferenceType(XTypeFactory.RFT_DOUBLE);
    Char = tf.createReferenceType(XTypeFactory.RFT_CHARACTER);
    Date = tf.createType(XConstants.XKJ_DATE, false);
    Fixed = tf.createType(XConstants.XKJ_FIXED, false);
    Month = tf.createType(XConstants.XKJ_MONTH, false);
    Time = tf.createType(XConstants.XKJ_TIME, false);
    Timestamp = tf.createType(XConstants.XKJ_TIMESTAMP, false);
    Week = tf.createType(XConstants.XKJ_WEEK, false);

    PDate = new XDateType();
    PFixed = new XFixedType();
    PMonth = new XMonthType();
    PTime = new XTimeType();
    PTimestamp = new XTimestampType();
    PWeek = new XWeekType();

    Number = tf.createReferenceType(XTypeFactory.RFT_NUMBER);
    Color = tf.createType("java/awt/Color", true);
    Image = tf.createType("javax/swing/ImageIcon", true);

    try {
      Date = (CReferenceType) Date.checkType(context);
      Fixed = (CReferenceType) Fixed.checkType(context);
      Month = (CReferenceType) Month.checkType(context);
      Time = (CReferenceType) Time.checkType(context);
      Timestamp = (CReferenceType) Timestamp.checkType(context);
      Week = (CReferenceType) Week.checkType(context);

      PDate =(XDateType)  PDate.checkType(context);
      PFixed = (XFixedType) PFixed.checkType(context);
      PMonth =  (XMonthType) PMonth.checkType(context);
      PTime = (XTimeType) PTime.checkType(context);
      PTimestamp = (XTimestampType) PTimestamp.checkType(context);
      PWeek = (XWeekType) PWeek.checkType(context);

      Cursor = (CReferenceType) Cursor.checkType(context);
      Connection = (CReferenceType) Connection.checkType(context);
      DBContextHandler =(CReferenceType)  DBContextHandler.checkType(context);
      KopiSerializable = (CReferenceType) KopiSerializable.checkType(context);
      Color = (CReferenceType) Color.checkType(context);
      Image = (CReferenceType) Image.checkType(context);
    } catch (UnpositionedError e) {
      compiler.reportTrouble(new PositionedError(TokenReference.NO_REF,
						 XKjcMessages.BAD_XKJCPATH,
						 e.getMessage()));
    }
  }

  // ----------------------------------------------------------------------
  // PRIMITIVE TYPE
  // ----------------------------------------------------------------------

  public static boolean isSqlPrimitiveType(CType type) {
    if (!type.isReference()) {
      return true;
    } else if (type.equals(Boolean)) {
      return true;
    } else if (type.equals(Byte)) {
      return true;
    } else if (type.equals(Short)) {
      return true;
    } else if (type.equals(Int)) {
      return true;
    } else if (type.equals(Long)) {
      return true;
    } else if (type.equals(Float)) {
      return true;
    } else if (type.equals(Double)) {
      return true;
    } else if (type.equals(Char)) {
      return true;
    } else if (type.equals(Fixed)) {
      return true;
    } else if (type.equals(Date)) {
      return true;
    } else if (type.equals(Month)) {
      return true;
    } else if (type.equals(Time)) {
      return true;
    } else if (type.equals(Timestamp)) {
      return true;
    } else if (type.equals(Week)) {
      return true;
    } else if (type.equals(PFixed)) {
      return true;
    } else if (type.equals(PDate)) {
      return true;
    } else if (type.equals(PMonth)) {
      return true;
    } else if (type.equals(PTime)) {
      return true;
    } else if (type.equals(PTimestamp)) {
      return true;
    } else if (type.equals(PWeek)) {
      return true;
    } else if (type.equals(CStdType.String)) {
      return true;
    } else {
      return false;
    }
  }
}
