/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.form;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * This class implements a special printwriter that map source line number 2 it
 */
public class LatexPrintWriter extends PrintWriter {
  /**
   * Construct a new LatexPrintWriter
   * This object translate special chars into latex syntax
   * @param w the writer to filter
   */
  public LatexPrintWriter(Writer w) {
    super(w);
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  public final void uncheckedPrintln(String src) {
    super.print(src);
    super.println();
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  public final void uncheckedPrint(String src) {
    super.print(src);
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  public final void printItem(String src) {
    super.print("\\item[");
    print(src);
    super.print("]");
    super.println();
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  public final void println(String src) {
    print(src);
    super.println();
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  public final void print(String src) {
    super.print(convert(src));
  }

  public static String convert(String src) {
    StringBuffer dest = new StringBuffer();
    for (int i = 0; i < src.length(); i++) {
      switch (src.charAt(i)) {
      case '&':
	dest.append("\\&");
	break;
      case '%':
	dest.append("\\%");
	break;
      case '(':
	dest.append("$($");
	break;
      case ')':
	dest.append("$)$");
	break;
      case '[':
	dest.append("$[$");
	break;
      case ']':
	dest.append("$]$");
	break;
     case '<':
	dest.append("$<$");
	break;
      case '>':
	dest.append("$>$");
	break;
      case '_':
	dest.append("\\_");
	break;
      case '#':
	dest.append("\\#");
	break;
      case 'ü':
	dest.append("\\\"{u}");
	break;
      case 'ä':
	dest.append("\\\"{a}");
	break;
      case 'ö':
	dest.append("\\\"{o}");
	break;
      case 'Ä':
	dest.append("\\\"{A}");
	break;
      case 'Ö':
	dest.append("\\\"{O}");
	break;
      case 'Ü':
	dest.append("\\\"{U}");
	break;
      case 'ß':
	dest.append("{\\ss}");
	break;
      default:
	dest.append(src.charAt(i));
      }
    }
    return dest.toString();
  }
}
