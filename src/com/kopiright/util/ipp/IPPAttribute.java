/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

public class IPPAttribute {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  public IPPAttribute( int groupTag, int valueTag, String name) {
    this.groupTag = groupTag;
    this.valueTag = valueTag;
    this.name = name;
    this.values = new LinkedList();
  }

  public IPPAttribute(IPPInputStream is, int groupTag) throws IOException {
    byte        read;
    int         n;
    boolean     endAttribute = false;

    this.groupTag = groupTag;
    this.values = new LinkedList();
    this.valueTag = is.readByte();          // value-tag

    n = is.readShort();                 // name-length
    this.name = is.readString(n);       // name

    while (!endAttribute) {
      switch (valueTag) {
      case IPPConstants.TAG_INTEGER :
      case IPPConstants.TAG_ENUM :
        values.add(new IntegerValue(is));
        break;

      case IPPConstants.TAG_BOOLEAN :
        values.add(new BooleanValue(is));
        break;

      case IPPConstants.TAG_TEXT :
      case IPPConstants.TAG_NAME :
      case IPPConstants.TAG_KEYWORD :
      case IPPConstants.TAG_STRING :
      case IPPConstants.TAG_URI :
      case IPPConstants.TAG_URISCHEME :
      case IPPConstants.TAG_CHARSET :
      case IPPConstants.TAG_LANGUAGE :
      case IPPConstants.TAG_MIMETYPE :
        values.add(new StringValue(is));
        break;


      case IPPConstants.TAG_DATE :
        values.add(new DateValue(is));
        break;

      case IPPConstants.TAG_RESOLUTION :
        values.add(new ResolutionValue(is));
        break;

      case IPPConstants.TAG_RANGE :
        values.add(new RangeValue(is));
        break;

      case IPPConstants.TAG_TEXTLANG :
      case IPPConstants.TAG_NAMELANG :
        values.add(new LangValue(is));

      default :
        n = is.readShort();
        is.readString(n);
        break;
      }
      read = is.peekByte();
      if (read < IPPConstants.TAG_UNSUPPORTED_VALUE) {
        endAttribute = true;
      } else {
        short   nameLengthNextAttribute = is.peekShortAfterFirstByte();

        if (nameLengthNextAttribute == 0) {
          //additional-value
          is.readByte();                // value-tag
          is.readShort();               // name-length
        } else {
          endAttribute = true;
        }
      }
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public String getName() {
    return name;
  }

  public Iterator getValues() {
    return values.iterator();
  }

  public int getGroup() {
    return groupTag;
  }

  public void addValue(IPPValue value) {
    values.add(value);
  }

  public int getSize(int lastGroup) {
    int         size = 0;
    boolean     firstValue = true;
    Iterator    vals = values.iterator();
    IPPValue    value;

    // if it is a new group, adding a group tag
    if (lastGroup != groupTag) {
      size++;
    }

    while (vals.hasNext()) {
      value = (IPPValue) vals.next();

      size++;                           // value-tag
      size += 2;                        // name-length
      if (firstValue) {
        size += name.length();          // name
        firstValue = false;
      }

      size += value.getSize();
    }

    return size;
  }

  public void write(IPPOutputStream os, int lastGroup) throws IOException {
    boolean     firstValue = true;
    Iterator    vals = values.iterator();
    IPPValue    value;

    // if it is a new group, adding a group tag
    if (lastGroup != groupTag) {
      os.writeByte(groupTag);
    }

    while (vals.hasNext()) {
      value = (IPPValue) vals.next();

      os.writeByte(valueTag);           // value-tag
      if (firstValue) {
        os.writeShort(name.length());   // name-length
        os.writeString(name);
        firstValue = false;
      } else {
        os.writeShort(0);               // name-length
      }

      value.write(os);
    }
  }

  public void dump() {
    Iterator    vals = values.iterator();
    IPPValue    value;

    System.out.println("");
    System.out.println("Group Tag : " + groupTag);
    System.out.println("Value Tag : " + valueTag);
    System.out.println("Att Name : " + name);
    System.out.println("Values :");

    while (vals.hasNext()) {
      value = (IPPValue) vals.next();
      value.dump();
    }
  }

  public void simpleDump() {
    Iterator    vals = values.iterator();
    IPPValue    value;

    System.out.print(name + " = ");

    while (vals.hasNext()) {
      value = (IPPValue) vals.next();
      System.out.print(value.toString());
      if (vals.hasNext()) {
        System.out.print(", ");
      }
    }
    System.out.println();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int           groupTag;
  private int           valueTag;
  private String        name;
  private List          values;
}
