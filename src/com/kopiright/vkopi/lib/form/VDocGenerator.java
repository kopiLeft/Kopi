/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VlibProperties;

/**
 * This class implements a Kopi pretty printer
 */
public class VDocGenerator extends VHelpGenerator {

  public VDocGenerator(LatexPrintWriter p) {
    super.p = this.p = p;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * prints a compilation unit
   */
  public String helpOnForm(String name, 
                           VCommand[] commands, 
                           VBlock[] blocks, 
                           String title, 
                           String help, 
                           String code)
  {
    p.println("\\section{" + title + "}");
    p.println("\\label{" + code +"}");
    p.println(help);
    p.println();

    if (commands != null) {
      sortCommands(commands);
      p.println("\\begin{description}");
      for (int i = 0; i < commands.length; i++) {
	//p.print("<BR>");
	commands[i].helpOnCommand(this);
      }
      p.println("\\end{description}");
    }

    if (blocks != null) {
      for (int i = 0; i < blocks.length; i++) {
	VBlock block = blocks[i];
	block.helpOnBlock(this);
      }
    }
    p.close();
    p.println("\\pagebreak");
    return "";
  }

  /**
   * printlns a compilation unit
   */
  public void helpOnBlock(String formCode, 
                          String title, 
                          String help, 
                          VCommand[] commands, 
                          VField[] fields, 
                          boolean alone) 
  {
    p.println("\\subsection{" + title + "}");
    p.uncheckedPrintln("\\begin{center}\\includegraphics{images/" +
		       formCode + "_" + title.replace(' ', '_') + ".ps"+
		       "}\\end{center}");
    if (help != null) {
      p.println(help);
    }
    p.println();

    int countNNull = 0;
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getLabel() != null) {
	countNNull++;
	break;
      }
    }

    if (countNNull == 0 && (commands == null || commands.length == 0)) {
      return;
    }

    p.uncheckedPrintln("\\begin{itemize}");
    if (commands != null && commands.length != 0) {
      int	query = 0;
      int	update = 0;
      int	insert = 0;
      int	all = 0;
      sortCommands(commands);
      for (int i = 0; i < commands.length; i++) {
	VCommand	command = commands[i];
	if (command.isActive(MOD_QUERY) && command.isActive(MOD_UPDATE) && command.isActive(MOD_INSERT)) {
	  all += 1;
	} else {
	  query += command.isActive(MOD_QUERY) ? 1 : 0;
	  update += command.isActive(MOD_UPDATE) ? 1 : 0;
	  insert += command.isActive(MOD_INSERT) ? 1 : 0;
	}
      }

      if (all > 0) {
	p.println("\\item{Funktionen}");
	p.uncheckedPrintln("\\begin{description}");
	for (int i = 0; i < commands.length; i++) {
	  VCommand	command = commands[i];
	  if (command.isActive(MOD_QUERY) && command.isActive(MOD_UPDATE) && command.isActive(MOD_INSERT)) {
	    command.helpOnCommand(this);
	  }
	}
	p.println("\\end{description}");
      }
      if (query > 0) {
	p.println("\\item{Funktionen - Suchmodus}");
	p.uncheckedPrintln("\\begin{description}");
	for (int i = 0; i < commands.length; i++) {
	  VCommand	command = commands[i];
	  if (command.isActive(MOD_QUERY) &&
	      !(command.isActive(MOD_QUERY) && command.isActive(MOD_UPDATE) && command.isActive(MOD_INSERT))) {
	    command.helpOnCommand(this);
	  }
	}
	p.println("\\end{description}");
      }
      if (update > 0) {
	p.println("\\item{Funktionen - \u00C4nderungsmodus}");
	p.uncheckedPrintln("\\begin{description}");
	for (int i = 0; i < commands.length; i++) {
	  VCommand	command = commands[i];
	  if (command.isActive(MOD_UPDATE) &&
	      !(command.isActive(MOD_QUERY) && command.isActive(MOD_UPDATE) && command.isActive(MOD_INSERT))) {
	    command.helpOnCommand(this);
	  }
	}
	p.println("\\end{description}");
      }
      if (insert > 0) {
	p.println("\\item{Funktionen - Einf\u00FCgemodus}");
	p.uncheckedPrintln("\\begin{description}");
	for (int i = 0; i < commands.length; i++) {
	  VCommand	command = commands[i];
	  if (command.isActive(MOD_INSERT) &&
	      !(command.isActive(MOD_QUERY) && command.isActive(MOD_UPDATE) && command.isActive(MOD_INSERT))) {
	    command.helpOnCommand(this);
	  }
	}
	p.println("\\end{description}");
      }
    }

    if (fields != null && fields.length != 0) {
      if (countNNull > 0) {
	p.println("\\item{Felder}");
	p.println("\\begin{description}");
	for (int i = 0; i < fields.length; i++) {
	  fields[i].helpOnField(this);
	}
	p.println("\\end{description}");
      }
    }
    p.uncheckedPrintln("\\end{itemize}");
  }

  /**
   * printlns a compilation unit
   */
  public void helpOnField(String blockTitle,
			  int pos,
			  String label,
			  String anchor,
			  String help) 
  {
    p.printItem(label);
    p.println("\\index{" + label + "}");

    if (help != null && help.length() > 0) {
      p.println(help + "\\\\");
    } else {
      p.println("...\\\\");
      p.println("");
    }
  }

  public void helpOnType(String modeName,
			 String modeDesc,
			 String typeName,
			 String typeDesc,
			 String[] names) 
  {
    p.uncheckedPrint(" \\makebox[0.7in][l]{{\\bf ");
    p.print(VlibProperties.getString("Mode"));
    p.uncheckedPrintln(":}}");
    p.uncheckedPrintln(" \\makebox[0.7in][l]{{\\it " + modeName + "}}");
    p.uncheckedPrintln(" \\parbox[t]{4in}{");
    p.print(modeDesc);
    p.uncheckedPrintln("}\n");
    p.uncheckedPrint(" \\makebox[0.7in][l]{{\\bf ");
    p.print(VlibProperties.getString("Type"));
    p.uncheckedPrintln(":}}");
    p.uncheckedPrintln(" \\makebox[0.7in][l]{{\\it " + typeName + "}}");
    p.uncheckedPrintln(" \\parbox[t]{4in}{");
    p.print(typeDesc);
    p.uncheckedPrintln("\n");

    if (names != null) {
      p.println("\\begin{enumerate}");
      for (int i = 0; i < names.length; i++) {
	p.print("\\item{" + names[i] + "}");
      }
      p.println("\\end{enumerate}");
    }

    p.uncheckedPrintln("}");
  }

  /**
   * printlns a compilation unit
   */
  public void helpOnFieldCommand(VCommand[] commands) {
    if (commands != null && commands.length != 0) {
      sortCommands(commands);
      p.println();
      p.println("\\begin{description}");
      for (int i = 0; i < commands.length; i++) {
	commands[i].helpOnCommand(this);
      }
      p.println("\\end{description}");
    }
    p.println();
  }

  /**
   * printlns a compilation unit
   */
  public void helpOnCommand(String menu, String item, String icon, int accKey, int accMod, String help) {
    p.print("\\item{");
    if (accMod == java.awt.Event.SHIFT_MASK) {
      p.print("\\Taste{Shift} ");
    }
    if (accKey != 0) {
      p.print("\\Taste{" + keyToName(accKey) + "} ");
    }
    p.print(menu + " $\\Rightarrow$ " + item + ": ");
    p.print("}");
    p.println("\\index{" + menu + ":" + item + "}");
    if (help != null) {
      p.println(help);
    } else {
      p.println("no help");
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES
  // ----------------------------------------------------------------------

  private void sortCommands(VCommand[] cmds) {
    for (int i = cmds.length; --i >= 0; ) {
      for (int j = 0; j < i; j++) {
	boolean swap = false;
	if (cmds[j].getKey() > cmds[j+1].getKey()) {
	  swap = true;
	} else if (cmds[j].getKey() == 0) {
	  swap = true;
	}
	if (swap) {
	  VCommand		tmp = cmds[j];

	  cmds[j] = cmds[j+1];
	  cmds[j+1] = tmp;
	}
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  LatexPrintWriter	p;
}
