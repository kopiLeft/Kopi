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

package com.kopiright.vkopi.lib.visual;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;

import com.kopiright.vkopi.lib.util.Utils;

/**
 * This class implements a Kopi pretty printer
 */
public class VHelpGenerator {


  /**
   * Key to name
   */
  public String keyToName(int key) {
    switch (key) {
    case KeyEvent.VK_F1:
      return "F1";
    case KeyEvent.VK_F2:
      return "F2";
    case KeyEvent.VK_F3:
      return "F3";
    case KeyEvent.VK_F4:
      return "F4";
    case KeyEvent.VK_F5:
      return "F5";
    case KeyEvent.VK_F6:
      return "F6";
    case KeyEvent.VK_F7:
      return "F7";
    case KeyEvent.VK_F8:
      return "F8";
    case KeyEvent.VK_F9:
      return "F9";
    case KeyEvent.VK_F10:
      return "F10";
    case KeyEvent.VK_F11:
      return "F11";
    case KeyEvent.VK_F12:
      return "F12";
    case KeyEvent.VK_ESCAPE:
      return "Esc";
    default:
      return "?";
    }
  }

  /**
   * print commands
   */
  public void helpOnCommands(VCommand[] commands) {
    if (commands.length > 0) {
      p.println("<TABLE valign=\"top\">");
      for (int i = 0; i < commands.length; i++) {
        commands[i].helpOnCommand(this);
      }
      p.println("</TABLE>");
    }
  }

  /**
   * print a command
   */
  public void helpOnCommand(String menu,
                            String item,
                            String icon,
                            int accKey,
                            int accMod,
                            String help)
  {
    //p.println("<DT>");
    p.println("<TR><TD>");
    if (icon != null) {
      addButton(icon +".png");
    } else {
      p.println("&nbsp;");
    }

    p.println("</TD><TD>");
    if (accMod != 0) {
      if (accMod == InputEvent.SHIFT_MASK) {
	p.print("Shift-");
      }
    }
    p.println(keyToName(accKey));

    p.println("</TD><TD><STRONG>" + menu + ":" + item + ":" + "</STRONG></TD><TD>");
    //p.println("<DD>");
    if (help != null) {
      p.println(help);
    } else {
      p.println("no help");
    }
    p.println("</TD></TR>");
  }


  /**
   * Add an image
   */
  private void addImage(String name, int border) {
    p.print("<img src=\"" + Utils.getURLFromResource(name));
    p.print("\" BORDER =\"" + border);
    p.println("\" alt=\"" + name + "\">");
  }

  /**
   * Add an image
   */
  public void addImage(String name) {
    addImage(name, 0);
  }

  /**
   * Add a button
   */
  public void addButton(String name) {
    addImage(name, 1);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected PrintWriter p;
}
