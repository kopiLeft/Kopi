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
 * $Id: GKjcPrettyPrinter.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.trig;

import java.io.IOException;

import at.dms.compiler.base.TabbedPrintWriter;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.TypeFactory;
import at.dms.xkopi.comp.xkjc.XKjcPrettyPrinter;

/**
 * This class implements a Java pretty printer
 */
public class GKjcPrettyPrinter extends XKjcPrettyPrinter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public GKjcPrettyPrinter(String fileName, TypeFactory factory) throws IOException {
    super(fileName, factory);
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public GKjcPrettyPrinter(TabbedPrintWriter p, TypeFactory factory) {
    super(p, factory);
  }

  // ----------------------------------------------------------------------
  // STATEMENT
  // ----------------------------------------------------------------------

  /**
   * prints a local cursor statement
   */
  public void visitKopiInsertStatement(JExpression conn,
                                       JExpression table,
                                       String[] columns,
                                       JExpression[] body)
  {
    newLine();
    print("#insert ");
    if (conn != null) {
      print("[");
      conn.accept(this);
      print("] ");
    }
    print("(");
    table.accept(this);
    print(") ");
    print(" { ");
    pos += TAB_SIZE;
    for (int i = 0; i < columns.length; i++) {
      if (i != 0) {
	print(",");
      }
      newLine();
      print('"' + columns[i].toString() + '"');
      pos += 20;
      print("= ");
      body[i].accept(this);
      pos -= 20;
    }
    pos -= TAB_SIZE;
    newLine();
    print("}");
  }
}
