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

package com.kopiright.vkopi.lib.util;

/**
 * Filters characters according to a conversion table
 *
 * @deprecated Use the class Charset437 instead with an OutputStreamWriter.
 */

public class To437 extends Filter {

  /**
   * Create a new Filter
   */

  public To437() {
    conversionTable  = new char [] {
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

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Do some filtering
   */

  public char convert (char c) {
    return (c >= 128) ? conversionTable [c - 128] : c;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
}
