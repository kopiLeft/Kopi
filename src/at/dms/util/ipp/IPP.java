/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.util.ipp;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

public class IPP {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public IPP() {
    this.header = new IPPHeader();
    this.attributes = new LinkedList();
    this.data = new byte[0];
  }

  public IPP(IPPInputStream is) throws IOException {
    boolean     endAttributes = false;
    byte        groupTag = IPPConstants.TAG_ZERO;
    byte        read;

    this.header = new IPPHeader(is);
    this.attributes = new LinkedList();
    this.data = new byte[0];

    while (!endAttributes) {
      read = is.peekByte();
      if (read == IPPConstants.TAG_END) {
        is.readByte();
        endAttributes = true;
      } else if (read < IPPConstants.TAG_UNSUPPORTED_VALUE) {
        // it is a new group tag
        groupTag = is.readByte();
      } else {
        // new attribute
        attributes.add(new IPPAttribute(is, groupTag));
      }
    }

    this.data = is.readArray();
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public boolean isAnError() {
    return header.isAnError();
  }

  public String getStatus() {
    return header.getStatus();
  }

  public IPPHeader getHeader() {
    return header;
  }

  public Iterator getAttributes() {
    return attributes.iterator();
  }

  public void addAttribute(IPPAttribute attribute) {
    attributes.add(attribute);
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public byte[] getData() {
    return data;
  }

  public void setRequest(int requestID, short operationID) {
    header.setRequestID(requestID);
    header.setOperationID(operationID);
  }


  public int getSize() {
    int         size = header.getSize();
    Iterator    atts = attributes.iterator();
    int         lastGroup = -1;

    while (atts.hasNext()) {
      IPPAttribute      attribute = (IPPAttribute) atts.next();

      size += attribute.getSize(lastGroup);
      lastGroup = attribute.getGroup();
    }

    return size + 1 + data.length;      // 1 for the TAG_END
  }

  public void write(IPPOutputStream os) throws IOException {
    Iterator    atts = attributes.iterator();
    int         lastGroup = -1;

    header.write(os);

    while (atts.hasNext()) {
      IPPAttribute      attribute = (IPPAttribute) atts.next();

      attribute.write(os, lastGroup);
      lastGroup = attribute.getGroup();
    }

    os.writeByte(IPPConstants.TAG_END);

    os.writeArray(data);
  }

  public void dump() {
    Iterator    atts = attributes.iterator();

    header.dump();

    while (atts.hasNext()) {
      IPPAttribute attribute = (IPPAttribute) atts.next();

      attribute.dump();
    }
  }

  public void simpleDump() {
    Iterator    atts = attributes.iterator();

    while (atts.hasNext()) {
      IPPAttribute      attribute = (IPPAttribute) atts.next();

      attribute.simpleDump();
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private IPPHeader     header;
  private List          attributes;
  private byte[]        data;

  public static final boolean   DEBUG = false;
}
