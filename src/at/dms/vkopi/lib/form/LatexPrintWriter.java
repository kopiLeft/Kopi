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
 * $Id: LatexPrintWriter.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

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
      case '�':
	dest.append("\\\"{u}");
	break;
      case '�':
	dest.append("\\\"{a}");
	break;
      case '�':
	dest.append("\\\"{o}");
	break;
      case '�':
	dest.append("\\\"{A}");
	break;
      case '�':
	dest.append("\\\"{O}");
	break;
      case '�':
	dest.append("\\\"{U}");
	break;
      case '�':
	dest.append("{\\ss}");
	break;
      default:
	dest.append(src.charAt(i));
      }
    }
    return dest.toString();
  }
}
