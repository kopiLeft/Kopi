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
 * $Id: VKLatexPrintWriter.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.base;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * This class implements a special printwriter that map source line number 2 it
 */
public class VKLatexPrintWriter extends PrintWriter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a new LatexPrintWriter
   * This object translate special chars into latex syntax
   * @param w			the writer to filter
   */
  public VKLatexPrintWriter(Writer w) {
    super(w);
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p			the buffered printwriter on which we write code
   */
  public final void uncheckedPrintln(String src) {
    super.println(src);
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p			the buffered printwriter on which we write code
   */
  public final void println(String src) {
    StringBuffer dest = new StringBuffer();
    for (int i = 0; i < src.length(); i++) {
      switch (src.charAt(i)) {
      case '%':
	dest.append("\\%");
	break;
      case '(':
	dest.append("\\(");
	break;
      case ')':
	dest.append("\\)");
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

    super.println(dest.toString());
  }
}
