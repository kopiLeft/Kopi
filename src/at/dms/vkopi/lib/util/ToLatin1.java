/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: ToLatin1.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.util;

/**
 * Filters characters according to a conversion table
 */

public class ToLatin1 extends Filter {

  /**
   * Create a new Filter
   */

  public ToLatin1() {
    conversionTable  = new char [] {
      0307, 0374, 0351, 0342, 0344, 0340, 0345, 0347,
      0352, 0353, 0350, 0357, 0356, 0354, 0304, 0305,
      0311, 0346, 0306, 0364, 0366, 0362, 0373, 0371,
      0377, 0326, 0334, 0242, 0243, 0245, 0000, 0000,
      0341, 0355, 0363, 0372, 0361, 0321, 0252, 0272,
      0277, 0000, 0254, 0275, 0274, 0241, 0253, 0273,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0251, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0337, 0000, 0000, 0000, 0000, 0265, 0000,
      0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000,
      0000, 0261, 0000, 0000, 0000, 0000, 0367, 0000,
      0260, 0000, 0267, 0000, 0000, 0262, 0000, 0240,
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
