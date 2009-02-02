/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.bytecode.ksm;

/**
 * This class includes some utilities to parse a ksm file
 */
public class Utils extends com.kopiright.util.base.Utils {

  // ----------------------------------------------------------------------
  // CONVERT STRING
  // ----------------------------------------------------------------------

  /**
   * Converts string
   */
  public static String convertString(String image) {
    StringBuffer s = new StringBuffer();
    for (int i = 1; i < image.length() - 1; i++) {
      char	c = image.charAt(i);

      if (c == '\\') {
	if (i + 1 < image.length() - 1) {
	  i++;
	  c = image.charAt(i);
	  switch (c) {
	  case 'n': c = '\n'; break;
	  case 'r': c = '\r'; break;
	  case 't': c = '\t'; break;
	  case 'b': c = '\b'; break;
	  case 'f': c = '\f'; break;
	  case '"': c = '\"'; break;
	  case '\'': c = '\''; break;
	  case '\\': c = '\\'; break;
	  default:
	    //!!! graf 000402 what to do with \x ?
	  }
	}
      }
      s.append(c);
    }
    return s.toString();
  }
}
