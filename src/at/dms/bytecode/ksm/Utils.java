/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: Utils.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.bytecode.ksm;

/**
 * This class includes some utilities to parse a ksm file
 */
public class Utils extends at.dms.util.base.Utils {

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
