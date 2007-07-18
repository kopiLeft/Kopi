/*
 * Copyright (c) 2000-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.util;

import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * Charset used to convert from UTF-16 to IBM437
 */
public class Charset437 extends Charset implements java.lang.Comparable {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  public Charset437() {
    super("437",null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public boolean contains(Charset csd) {
    return false;
  }

  public CharsetDecoder newDecoder() {
    return new Decoder437(this);
  }

  public CharsetEncoder newEncoder() {
    return new Encoder437(this);
  }

  // ----------------------------------------------------------------------
  // INTERFACES
  // ----------------------------------------------------------------------

  public int compareTo(Object ob) {
    return this.compareTo((Charset)ob);
  }
}

/**
 * Decoder not implemented.
 */
class Decoder437 extends CharsetDecoder {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  public Decoder437(Charset cs) {
    super(cs, 1, 1);
  }

  // ----------------------------------------------------------------------
  // DECODING
  // ----------------------------------------------------------------------

  protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
     while (in.hasRemaining() && out.hasRemaining()) {
       out.put('?');
     }
     if (in.hasRemaining()) {
       return CoderResult.OVERFLOW;
     }
    return CoderResult.UNDERFLOW;
  }
}

/**
 * Convert from UTF-16 to the charset 437.
 */
class Encoder437 extends CharsetEncoder {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  public Encoder437(Charset cs) {
    super(cs, 1, 1);
  }

  // ----------------------------------------------------------------------
  // ENCODING
  // ----------------------------------------------------------------------

  protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
    while (in.hasRemaining() && out.hasRemaining()) {
      char      c = in.get();

      // the char we get is only 1 byte value.
      if (c > 255) {
        return CoderResult.unmappableForLength(in.length());
      } else {
        out.put(convert(c));
      }
    }
    if (in.hasRemaining()) {
      return CoderResult.OVERFLOW;
    }
    return CoderResult.UNDERFLOW;
  }

  private byte convert(char c) {
    return (byte)((c >= 128) ? conversionTable [c - 128] : c);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static char[]     conversionTable  = new char [] {
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0377, 0255, 0233, 0234, 0200, 0235, 0000, 0000,
      0000, 0304, 0246, 0256, 0252, 0000, 0000, 0000,
      0370, 0361, 0375, 0000, 0000, 0346, 0000, 0372,
      0000, 0000, 0247, 0257, 0254, 0253, 0000, 0250,
      0000, 0000, 0000, 0000, 0216, 0217, 0222, 0200,
      0000, 0220, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0245, 0000, 0000, 0000, 0000, 0231, 0000,
      0000, 0000, 0000, 0000, 0232, 0000, 0000, 0341,
      0205, 0240, 0203, 0000, 0204, 0206, 0221, 0207,
      0212, 0202, 0210, 0211, 0215, 0241, 0214, 0213,
      0000, 0244, 0225, 0242, 0223, 0000, 0224, 0366,
      0000, 0227, 0243, 0226, 0201, 0000, 0000, 0230
    };
}
