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
 * $Id: IndentingWriter.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.bytecode.dis;

import java.io.PrintWriter;

/**
 * This class allows indented output
 */
class IndentingWriter {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Creates a new indenting writer.
   *
   * @param	out		the print writer to writer to
   */
  public IndentingWriter(PrintWriter out) {
    this.out = out;
    this.level = 0;
  }

  // --------------------------------------------------------------------
  // ACCESSORS & MUTATORS
  // --------------------------------------------------------------------

  /**
   * Increments the indentation level.
   */
  public void incrementLevel() {
    level += 1;
  }

  /**
   * Decrements the indentation level.
   */
  public void decrementLevel() {
    level -= 1;
  }

  // --------------------------------------------------------------------
  // WRITING
  // --------------------------------------------------------------------

  /**
   * Prints a string.
   *
   * @param	value			the string to print
   */
  public void print(String value) {
    out.print(value);
  }

  /**
   * Prints an integer.
   *
   * @param	value			the integer to print
   */
  public void print(int value) {
    out.print("" + value);
  }

  /**
   * Terminates the current line by writing the line separator string
   * and moving to the current indentation level.
   */
  public void println() {
    out.println();

    for (int i = level; i > 0; i--) {
      out.print(TAB);
    }
  }

  /**
   * Prints a string and then terminates the line.
   *
   * @param	str			the string to print
   */
  public void println(String str) {
    print(str);
    println();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static final String	TAB = "    ";

  private PrintWriter		out;
  private int			level;
}
