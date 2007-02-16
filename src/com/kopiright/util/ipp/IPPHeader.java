/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.util.ipp;

import java.io.IOException;

public class IPPHeader {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public IPPHeader() {
  }

  public IPPHeader(IPPInputStream is) throws IOException {
    majorVersion = is.readByte();
    minorVersion = is.readByte();
    operationID = is.readShort();
    requestID = is.readInteger();
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public void setVersion(byte major, byte minor) {
    this.majorVersion = major;
    this.minorVersion = minor;
  }

  public void setOperationID(short operationID) {
    this.operationID = operationID;
  }

  public short getOperationID() {
    return operationID;
  }

  public void setRequestID(int requestID) {
    this.requestID = requestID;
  }

  public int getRequestID() {
    return requestID;
  }

  public void write(IPPOutputStream os) throws IOException {
    os.writeByte(majorVersion);
    os.writeByte(minorVersion);
    os.writeShort(operationID);
    os.writeInteger(requestID);
  }

  public int getSize() {
    return 8;
  }

  public void dump() {
    System.out.println("Major version : " + majorVersion);
    System.out.println("Minor version : " + minorVersion);
    System.out.println("Operation ID : " + operationID);
    System.out.println("Request ID : " + requestID);
  }

  public boolean isAnError() {
    return operationID >= 0x400;
  }

  public String getStatus() {
    int         units;

    if (operationID < 0x400) {
      units = operationID;
      if (units < IPPConstants.ERR_SUCCESSFUL.length) {
        return IPPConstants.ERR_SUCCESSFUL[units];
      }
    } else if (operationID < 0x500) {
      units = operationID - 0x400;
      if (units < IPPConstants.ERR_CLIENT_ERROR.length) {
        return IPPConstants.ERR_CLIENT_ERROR[units];
      }
    } else {
      units = operationID - 0x400;
      if (units < IPPConstants.ERR_SERVER_ERROR.length) {
        return IPPConstants.ERR_SERVER_ERROR[units];
      }
    }
    return null;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private byte          majorVersion = 1;
  private byte          minorVersion = 1;
  private short         operationID;
  private int           requestID;
}
