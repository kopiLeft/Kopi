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

package com.kopiright.compiler.tools.msggen;


import java.io.PrintWriter;
import java.util.Hashtable;


class MessageDefinition {

  /**
   * Constructs a message definition
   * @param     identifier      the message identifier
   * @param     format          the textual message format (with placeholders)
   * @param     reference       the document describing the reason for this message
   * @param     level           the severity level of this message
   */
  public MessageDefinition(String identifier,
                           String format,
                           String reference,
                           int level)
  {
    this.identifier = identifier;
    this.reference = reference;
    this.format = format;
    this.level = level;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Checks for duplicate identifiers.
   * @param     identifiers     a table of all token identifiers
   * @param     prefix          the literal prefix
   * @param     id              the id of the token
   * @param     sourceFile      the file where the token is defined
   */
  public void checkIdentifiers(Hashtable identifiers, String sourceFile)
    throws MsggenError
  {

    String      stored = (String)identifiers.get(identifier);

    if (stored != null) {
      throw new MsggenError(MsggenMessages.DUPLICATE_DEFINITION,
                            new Object[] {
                              identifier,
                              sourceFile,
                              stored
                            });
    }
    identifiers.put(identifier, sourceFile);
  }

  /**
   * Prints the token definition to interface file (java)
   * @param     out          the output stream
   * @param     prefix          the literal prefix
   */
  public void printInterface(PrintWriter out, String prefix) {

    out.print("  MessageDescription\t");
    out.print(identifier);
    out.print(" = new MessageDescription(\"");
    out.print(format);
    out.print("\", ");
    out.print(reference == null ? "null" : "\"" + reference + "\"");
    out.print(", ");
    out.print(level);
    out.println(");");
  }


  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final String          identifier;
  private final String          format;
  private final String          reference;
  private final int             level;
}
