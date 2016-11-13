/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.util.ipp;

public class IPPConstants {
  public static final int TAG_ZERO = 0x00;
  public static final int TAG_OPERATION = 0x01;
  public static final int TAG_JOB = 0x02;
  public static final int TAG_END = 0x03;
  public static final int TAG_PRINTER = 0x04;
  public static final int TAG_UNSUPPORTED_GROUP = 0x05;
  public static final int TAG_SUBSCRIPTION = 0x06;
  public static final int TAG_EVENT_NOTIFICATION = 0x07;
  public static final int TAG_UNSUPPORTED_VALUE = 0x10;
  public static final int TAG_DEFAULT = 0x11;
  public static final int TAG_UNKNOWN = 0x12;
  public static final int TAG_NOVALUE = 0x13;
  public static final int TAG_NOTSETTABLE = 0x15;
  public static final int TAG_DELETEATTR = 0x16;
  public static final int TAG_ADMINDEFINE = 0x17;
  public static final int TAG_INTEGER = 0x21;
  public static final int TAG_BOOLEAN = 0x22;
  public static final int TAG_ENUM = 0x23;
  public static final int TAG_STRING = 0x30;
  public static final int TAG_DATE = 0x31;
  public static final int TAG_RESOLUTION = 0x32;
  public static final int TAG_RANGE = 0x33;
  public static final int TAG_BEGIN_COLLECTION = 0x34;
  public static final int TAG_TEXTLANG = 0x35;
  public static final int TAG_NAMELANG = 0x36;
  public static final int TAG_END_COLLECTION = 0x37;
  public static final int TAG_TEXT = 0x41;
  public static final int TAG_NAME = 0x42;
  public static final int TAG_KEYWORD = 0x44;
  public static final int TAG_URI = 0x45;
  public static final int TAG_URISCHEME = 0x46;
  public static final int TAG_CHARSET = 0x47;
  public static final int TAG_LANGUAGE = 0x48;
  public static final int TAG_MIMETYPE = 0x49;
  public static final int TAG_MEMBERNAME = 0x4A;
  public static final int TAG_MASK = 0x7FFFFFFF;
  public static final int TAG_COPY = 0x80000001;

  public static final short OPS_PRINT_JOB = 0x0002;
  public static final short OPS_VALIDATE_JOB = 0x0004;
  public static final short OPS_CREATE_JOB = 0x0005;
  public static final short OPS_SEND_DOCUMENT = 0x0006;
  public static final short OPS_CANCEL_JOB = 0x0008;
  public static final short OPS_GET_JOB_ATTRIBUTES = 0x0009;
  public static final short OPS_GET_JOBS = 0x000A;
  public static final short OPS_GET_PRINTER_ATTRIBUTES = 0x000B;
  public static final short OPS_HOLD_JOB = 0x000C;
  public static final short OPS_RELEASE_JOB = 0x000D;
  public static final short OPS_PAUSE_PRINTER = 0x0010;
  public static final short OPS_RESUME_PRINTER = 0x0011;
  public static final short OPS_PURGE_JOBS = 0x0012;
  public static final short OPS_SET_JOB_ATTRIBUTES = 0x0014;


  public static final String[]
    ERR_SUCCESSFUL = new String[] {
      "successful-ok",                                          //0x0000
      "successful-ok-ignored-or-substituted-attributes ",       //0x0001
      "successful-ok-conflicting-attributes"                    //0x0002
    };

  public static final String[]
    ERR_CLIENT_ERROR = new String[] {
      "client-error-bad-request",                               //0x0400
      "client-error-forbidden",                                 //0x0401
      "client-error-not-authenticated",                         //0x0402
      "client-error-not-authorized",                            //0x0403
      "client-error-not-possible",                              //0x0404
      "client-error-timeout",                                   //0x0405
      "client-error-not-found",                                 //0x0406
      "client-error-gone",                                      //0x0407
      "client-error-request-entity-too-large",                  //0x0408
      "client-error-request-value-too-long",                    //0x0409
      "client-error-document-format-not-supported",             //0x040A
      "client-error-attributes-or-values-not-supported",        //0x040B
      "client-error-uri-scheme-not-supported",                  //0x040C
      "client-error-charset-not-supported",                     //0x040D
      "client-error-conflicting-attributes",                    //0x040E
      "client-error-compression-not-supported",                 //0x040F
      "client-error-compression-error",                         //0x0410
      "client-error-document-format-error",                     //0x0411
      "client-error-document-access-error"                      //0x0412
    };
  public static final String[]
    ERR_SERVER_ERROR = new String[] {
      "server-error-internal-error",                            //0x0500
      "server-error-operation-not-supported",                   //0x0501
      "server-error-service-unavailable",                       //0x0502
      "server-error-version-not-supported",                     //0x0503
      "server-error-device-error",                              //0x0504
      "server-error-temporary-error",                           //0x0505
      "server-error-not-accepting-jobs",                        //0x0506
      "server-error-busy",                                      //0x0507
      "server-error-job-canceled",                              //0x0508
      "server-error-multiple-document-jobs-not-supported"       //0x0509
    };
}
