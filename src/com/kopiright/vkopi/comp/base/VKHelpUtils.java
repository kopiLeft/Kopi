/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.base;

/**
 * Some utilities to generate the documentation
 */
public class VKHelpUtils {

  /**
   * Generate an html anchor in the document
   */
  public static void genAnchor(VKLatexPrintWriter p, String name) {
    p.uncheckedPrintln("\\begin{rawhtml} <A NAME=\"" + name + "\"></A> \n\\end{rawhtml}");
  }

  /**
   * Generate an image in the document
   */
  public static void genImage(VKLatexPrintWriter p, String name) {
    if (name.indexOf(".") == -1) {
       p.uncheckedPrintln("\\begin{rawhtml}\n<img src=\"images/" + name + ".gif\" BORDER = 0 > \n\\end{rawhtml}");
    } else {
      p.uncheckedPrintln("\\begin{rawhtml}\n<img src=\"images/" + name + "\" BORDER = 0 > \n\\end{rawhtml}");
    }
  }

  /**
   * Generate an image in the document
   */
  public static void genKey(VKLatexPrintWriter p, String name) {
    int	shiftIndex = name.indexOf("Shift-");
    if (shiftIndex != -1) {
      name = name.substring(shiftIndex + 1);
    }

    if (shiftIndex >= 0) {
      genImage(p, "Shift");
    }

    genImage(p, name);
  }

  /**
   * Generate an mode field description
   */
  public static void genModeField(java.io.PrintWriter p, String name, String desc) {
    //    p.println("{\\bf " + VlibProperties.getString("Mode")+ ":}");
    p.println(" {\\it " + name + "}");
    p.println(desc + "\n");
  }

  /**
   * Generate an mode field description
   */
  public static void genTypeField(java.io.PrintWriter p, String name, String desc) {
    //    p.println("{\\bf " + VlibProperties.getString("Type")+ ":}");
    p.println(" {\\it " + name + "}");
    p.println(desc + "\n");
  }
}
