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
 * $Id: VKFormPrettyPrinter.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.comp.form;

import java.io.IOException;
import java.util.Vector;

import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.TabbedPrintWriter;
import at.dms.kopi.comp.kjc.JClassDeclaration;
import at.dms.kopi.comp.kjc.JCompilationUnit;
import at.dms.kopi.comp.kjc.TypeFactory;
import at.dms.vkopi.comp.base.*;
import at.dms.vkopi.lib.form.VConstants;

/**
 * This class implements a Kopi pretty printer // MOVE TO main !!!
 */
public class VKFormPrettyPrinter extends VKPrettyPrinter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public VKFormPrettyPrinter(String fileName, TypeFactory factory) 
    throws IOException
  {
    super(fileName, factory);
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public VKFormPrettyPrinter(TabbedPrintWriter p, TypeFactory factory) {
    super(p, factory);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * prints a compilation unit
   */
  public void printForm(String name,
			String superForm,
			JCompilationUnit unit,
			VKDefinitionCollector coll,
			int options,
			Vector commands,
			Vector triggers,
			Vector blocks,
			JClassDeclaration decl,
			JavaStyleComment[] comment)
  {
    printCopyright(name);
    printComment(comment);

    newLine();
    print("FORM \"" + name + "\"");
    if (superForm != null) {
      print(" IS " + superForm.replace('/', '.'));
    }

    newLine();
    newLine();

    printCUnit(unit);

    newLine();

    coll.genVKCode(this);

    newLine();
    print("BEGIN");
    newLine();

    pos += TAB_SIZE;

    // COMMANDS
    if (commands != null) {
      newLine();
      printSeparator();
      newLine();
      newLine();
      for (int i = 0; i < commands.size(); i++) {
	((VKCommand)commands.elementAt(i)).genVKCode(this);
	newLine();
      }
    }

    // TRIGGERS
    if (triggers != null) {
      newLine();
      printSeparator();
      newLine();
      newLine();
      for (int i = 0; i < triggers.size(); i++) {
	((VKTrigger)triggers.elementAt(i)).genVKCode(this);
	newLine();
      }
    }

    // BLOCKS
    for (int i = 0; i < blocks.size(); i++) {
      newLine();
      ((VKPhylum)blocks.elementAt(i)).genVKCode(this);
    }

    pos -= TAB_SIZE;

    // BODY
    /*
    if (decl.getBody().size() > 0) {
      newLine();
      getJavaPrettyPrinter().printClassBody(decl.getBody());
    }
    */

    newLine();
    print("END FORM");
    newLine();
  }

  /**
   * prints a compilation unit
   */
  public void printBlockInsert(String name,
			       String superForm,
			       JCompilationUnit unit,
			       VKDefinitionCollector coll,
			       VKBlock block,
			       JClassDeclaration decl,
			       JavaStyleComment[] comments) {
    printCopyright(name);
    printComment(comments);

    newLine();
    print("BLOCK INSERT");
    newLine();
    newLine();

    printCUnit(unit);

    newLine();
    coll.genVKCode(this);
    newLine();

    // BLOCKS
    pos += TAB_SIZE;
    block.genVKCode(this);
    pos -= TAB_SIZE;

    newLine();
    print("END INSERT");
    newLine();
  }

  /**
   * prints a compilation unit
   */
  public void printBlock(String name,
			 String shortCut,
			 int vis,
			 int buff,
			 String title,
			 Vector slaves,
			 int border,
			 int alignment,
			 VKBlockAlign align,
			 String help,
			 Vector tables,
			 Vector indices,
			 int[] access,
			 int options,
			 Vector commands,
			 Vector triggers,
			 Vector objects,
			 String page,
			 JClassDeclaration decl) {
    if (page != null && !page.equals(current_page)) {
      current_page = page;
      newLine();
      print("NEW PAGE \"" + page + '"');
      newLine();
    }

    printBlockBanner(name, shortCut);
    newLine();

    print("BLOCK(" + buff + ", " + vis + ") ");
    print(name);
    if (shortCut != null) {
      print(" . " + shortCut);
    }
    if (title != null) {
      print(" \"" + title + "\"");
    }

    pos += TAB_SIZE;

    // SLAVES
    if (slaves != null) {
      newLine();
      print("SLAVES(");
      for (int i = 0; i < slaves.size(); i++) {
	print((i == 0 ? "" : ", ") + (String)slaves.elementAt(i));
      }
      print(")");
    }

    // BORDER
    if (border != 0) {
      newLine();
      print("BORDER ");
      switch (border) {
      case VConstants.BRD_LINE:
	print("LINE");
      break;
      case VConstants.BRD_RAISED:
	print("RAISED");
      break;
      case VConstants.BRD_LOWERED:
	print("LOWERED");
      break;
      case VConstants.BRD_ETCHED:
	print("ETCHED");
      break;
      }
    }

    // BLOCK ALIGNMENT
    if (align != null) {
      align.genVKCode(this);
    }

    // HELP
    if (help != null) {
      newLine();
      printHelp(help);
    }

    pos += TAB_SIZE;
    // OPTIONS
    if ((options & VConstants.BKO_NODELETE) > 0) {
      newLine();
      print("NO DELETE");
    }
    if ((options & VConstants.BKO_NOINSERT) > 0) {
      newLine();
      print("NO INSERT");
    }
    if ((options & VConstants.BKO_NOMOVE) > 0) {
      newLine();
      print("NO MOVE");
    }
    if ((options & VConstants.BKO_INDEXED) > 0) {
      newLine();
      print("UPDATE INDEX");
    }
    pos -= TAB_SIZE;

    // TABLES
    if (tables != null) {
      newLine();
      for (int i = 0; i < tables.size(); i++) {
	((VKBlockTable)tables.elementAt(i)).genVKCode(this);
      }
    }

    // INDICES
    if (indices != null) {
      newLine();
      for (int i = 0; i < indices.size(); i++) {
	newLine();
	print("INDEX \"" + (String)indices.elementAt(i) + "\"");
      }
    }

    newLine();

    // MODES
    for (int i = 0; i < 3; i++) {
      if (access[i] != VConstants.ACS_MUSTFILL) {
	newLine();
	printModes(1 << i);
	switch (access[i]) {
	case VConstants.ACS_HIDDEN:
	  print("HIDDEN");
	  break;
	case VConstants.ACS_SKIPPED:
	  print("SKIPPED");
	  break;
	case VConstants.ACS_VISIT:
	  print("VISIT");
	  break;
	}
      }
    }

    newLine();

    // COMMANDS
    if (commands != null) {
      newLine();
      printSeparator4();
      newLine();
      newLine();
      for (int i = 0; i < commands.size(); i++) {
	((VKCommand)commands.elementAt(i)).genVKCode(this);
	newLine();
      }
    }

    // TRIGGERS
    if (triggers != null) {
      newLine();
      printSeparator4();
      newLine();
      newLine();
      for (int i = 0; i < triggers.size(); i++) {
	((VKTrigger)triggers.elementAt(i)).genVKCode(this);
	newLine();
      }
    }

    // OBJECTS
    if (objects.size() > 0) {
      newLine();
      printSeparator4();
      newLine();
      for (int i = 0; i < objects.size(); i++) {
	((VKField)objects.elementAt(i)).genVKCode(this);
	if (i != objects.size() - 1) {
	  newLine();
	}
      }
    }
/*
    if (decl.getBody().size() > 0) {
      newLine();
      getJavaPrettyPrinter().printClassBody(decl.getBody());
    }
*/
    pos -= TAB_SIZE;

    newLine();
    print("END BLOCK");
    //newLine();
  }

  /**
   * Prints imported block
   */
  public void printImporterBlock(String name, String shortcut, String page) {
    if (page != null && !page.equals(current_page)) {
      current_page = page;
      newLine();
      print("NEW PAGE \"" + page + '"');
      newLine();
    }


    printBlockBanner(name, shortcut);
    newLine();

    print("INSERT " + name + " . " + shortcut);
    newLine();
  }

  /**
   * prints a compilation unit
   */
  public void printField(String name,
			 String label,
			 String help,
			 VKPosition pos,
			 VKFieldType type,
			 int align,
			 int options,
			 VKFieldColumns columns,
			 int[] access, // !!!
			 Vector commands,
			 Vector triggers) {

    newLine();

    int maxAccess = getMaxAccess(access);

    printAccess(maxAccess);

    print(" " + (name.indexOf("$") == -1 ? name : ""));
    this.pos += TAB_SIZE;

    // POSITION
    if (pos != null) {
      pos.genVKCode(this);
    }

    // LABEL
    if (label != null) {
      newLine();
      print("LABEL");
      if (!label.equals(name)) {
	print(" \"" + label + "\"");
      }
    }

    // HELP
    printHelp(help);

    // TYPE
    type.genVKCode(this);

    // ALIGN
    if (align != 0 && align != VConstants.ALG_LEFT) { // !!!
      newLine();
      print("ALIGN ");
      switch (align) {
      case VConstants.ALG_LEFT:
	print("LEFT");
	break;
      case VConstants.ALG_RIGHT:
	print("RIGHT");
	break;
      case VConstants.ALG_CENTER:
	print("CENTER");
	break;
      }
    }

    this.pos += TAB_SIZE;
    // OPTIONS
    if ((options & VConstants.FDO_NOEDIT) > 0) {
      newLine();
      print("NOEDIT");
    }
    if ((options & VConstants.FDO_NOECHO) > 0) {
      newLine();
      print("NOECHO");
    }
    if ((options & VConstants.FDO_TRANSIENT) > 0) {
      newLine();
      print("TRANSIENT");
    }
    this.pos -= TAB_SIZE;

    //COLUMNS
    if (columns != null) {
      columns.genVKCode(this);
    }

    for (int i = 0; i < 3; i++) {
      if (access[i] != maxAccess) {
	newLine();
	printModes(1 << i);
	printAccess(access[i]);
      }
    }

    // COMMANDS
    if (commands != null) {
      newLine();
      for (int i = 0; i < commands.size(); i++) {
	((VKCommand)commands.elementAt(i)).genVKCode(this);
	newLine();
      }
    }

    // TRIGGERS
    if (triggers != null) {
      newLine();
      for (int i = 0; i < triggers.size(); i++) {
	((VKTrigger)triggers.elementAt(i)).genVKCode(this);
	newLine();
      }
    }

    this.pos -= TAB_SIZE;

    newLine();
    print("END FIELD");
  }

  /**
   * Prints field access
   */
  public int getMaxAccess(int[] access) {
    int		max = VConstants.ACS_HIDDEN;
    for (int i = 0; i < 3; i++) {
      max = Math.max(access[i], max);
    }
    return max;
  }

  /**
   * Print an access code
   */
  public void printBlockAlign(String block, Vector sources, Vector targets) {
    newLine();
    print("ALIGN " + block + " <");
    for (int i = 0; i < sources.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      print(sources.elementAt(i).toString());
      print("-");
      print(targets.elementAt(i).toString());
    }
    print(">");
  }
  /**
   * Print an access code
   */
  public void printAccess(int access) {
    switch (access) {
    case VConstants.ACS_HIDDEN:
      print("HIDDEN");
      break;
    case VConstants.ACS_SKIPPED:
      print("SKIPPED");
	break;
    case VConstants.ACS_VISIT:
      print("VISIT");
      break;
    case VConstants.ACS_MUSTFILL:
      print("MUSTFILL");
      break;
    }
  }

  /**
   * prints a compilation unit
   */
  public void printFieldColumns(Vector columns,
				int indices,
				int priority) {
    newLine();
    print("COLUMNS(");
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      ((VKFieldColumn)columns.elementAt(i)).genVKCode(this);
    }
    print(")");

    pos += TAB_SIZE;
    if (indices != 0) {
      newLine();
      print("INDEX");
      for (int i = 0; i < 32; i++) {
	if (((1 << i) & indices) != 0) {
	  print(" " + i);
	}
      }
    }
    if (priority != 0){
      newLine();
      print("PRIORITY " + priority);
    }
    pos -= TAB_SIZE;
  }

  /**
   * prints a compilation unit
   */
  public void printFieldColumn(String table,
			       String column,
			       boolean isKey) {
    print((isKey ? "KEY " : "") + table + "." + column);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String current_page;
}
