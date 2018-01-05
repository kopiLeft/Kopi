/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.comp.base;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.TabbedPrintWriter;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JCompilationUnit;
import org.kopi.kopi.comp.kjc.JCompoundStatement;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFormalParameter;
import org.kopi.kopi.comp.kjc.KjcPrettyPrinter;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.comp.trig.GKjcPrettyPrinter;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.xkopi.comp.sqlc.TableReference;
import org.kopi.xkopi.lib.type.Fixed;

/**
 * This class implements a Kopi pretty printer
 */
public class VKPrettyPrinter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public VKPrettyPrinter(String fileName, TypeFactory factory) throws IOException {
    this(new TabbedPrintWriter(new BufferedWriter(new FileWriter(fileName))),  // + ".gen"
         factory);
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public VKPrettyPrinter(TabbedPrintWriter p, TypeFactory factory) {
    this.p = p;
    this.factory = factory;
    this.pos = 0;
    this.in_pp = new GKjcPrettyPrinter(this.p, factory);
  }

  /**
   * Close the stream at the end
   */
  public void close() {
    p.close();
  }

  /**
   * Returns the java pretty printer
   */
  public KjcPrettyPrinter getJavaPrettyPrinter() {
    in_pp.setPos(pos);
    return in_pp;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * prints a compilation unit
   */
  public void printCommandBody(String name,
			       VKAction action)
  {
    print("ITEM " + name);
    newLine();
    print("ACTION ");
    action.genVKCode(this);
  }

  /**
   * prints a compilation unit
   */
  public void printCommandName(int modes,
			       String name)
  {
    printModes(modes);
    print("COMMAND " + name);
  }

  /**
   * prints a compilation unit
   */
  public void printFieldColumn(String table,
			       String column,
			       boolean isKey,
                               boolean nullable)
  {
    print((nullable ? "NULLABLE " : "") +(isKey ? "KEY " : "") + table + "." + column);
  }

  /**
   * prints a compilation unit
   */
  public void printCoordinatePosition(int line,
                                      int endLine,
				      int column,
				      int endColumn)
  {
    print(" AT <" + line);
    if (endLine != line){
      print("-" + endLine);
    }
    print(", " + column);
    if (endColumn != column) {
      print("-" + endColumn);
    }
    print(">");
  }

  /**
   * prints a compilation unit
   */
  public void printDescriptionPosition(String name) {
    print(" FOLLOW " + name);
  }

  /**
   * prints a compilation unit
   */
  public void printBlockTable(String name,
			      String corr)
  {
    newLine();
    print("TABLE <" + name + ", " + corr + ">");
  }

  /**
   * prints a compilation unit
   */
  public void printDefaultCommand(int modes, VKCommandBody body) {
    printModes(modes);
    print("COMMAND");
    pos += TAB_SIZE;
    newLine();
    body.genVKCode(this);
    pos -= TAB_SIZE;
    newLine();
    print("END COMMAND");
  }

  /**
   * prints a compilation unit
   */
  public void printTrigger(long events, VKAction action) {
    printEvents(events);
    action.genVKCode(this);
  }

  /**
   * prints a compilation unit
   */
  public void printExternAction(String name) {
    print("EXTERN " + name);
  }

  /**
   * prints a compilation unit
   */
  public void printInternAction(String name) {
    print(" " + name);
  }

  /**
   * prints a compilation unit
   */
  public void printBlockAction(JCompoundStatement stmts) {
    print("{");
    pos += 2;
    stmts.accept(getJavaPrettyPrinter());
    pos -= 2;
    //getJavaPrettyPrinter().newLine();
    newLine();
    print("}");
  }

  /**
   * prints a compilation unit
   */
  public void printMethodAction(JFormalParameter[] params,
                                JCompoundStatement stmts)
  {
    print("(");
    params[0].accept(getJavaPrettyPrinter());
    print(") ");
    print("{");
    pos += 2;
    stmts.accept(getJavaPrettyPrinter());
    pos -= 2;
    // getJavaPrettyPrinter().newLine();
    newLine();
    print("}");
  }

  /**
   * Prints a menu.
   */
  public void printMenu(String ident, String label) {
    print("MENU ");
    print(ident);
    pos += TAB_SIZE;
    if (label != null) {
      newLine();
      print("LABEL   " + '"'  + label + '"');
    }
    pos -= TAB_SIZE;
    newLine();
    print("END MENU");
  }

  /**
   * prints a compilation unit
   */
  public void printActor(String ident,
                         String menu,
                         String label,
                         String help,
                         String key,
                         String icon)
  {
    print("ACTOR ");
    print(ident);
    pos += TAB_SIZE;
    print("MENU    " + menu);
    if (label != null) {
      newLine();
      print("LABEL   " + '"' + label + '"');
    }
    printHelp(help);
    if (key != null) {
      newLine();
      print("KEY     " + '"' + key + '"');
    }
    if (icon != null) {
      newLine();
      print("ICON    " + '"' + icon + '"');
    }
    pos -= TAB_SIZE;
    newLine();
    print("END ACTOR");
  }

  /**
   * prints a compilation unit
   */
  public void printVKCommandDefinition(String name, VKCommandBody body) {
    newLine();
    print("COMMAND " + name);

    pos += TAB_SIZE;
    newLine();
    body.genVKCode(this);
    pos -= TAB_SIZE;
    newLine();
    print("END COMMAND");
  }

  /**
   * prints a compilation unit
   */
  public void printVKTypeDefinition(String name,
                                    JFormalParameter[] params,
                                    VKType type)
  {
    newLine();
    print("TYPE " + name);
    if (params != null) {
      for (int i = 0; i < params.length; i++) {
	params[i].accept(getJavaPrettyPrinter());
      }
    }
    print(" IS");

    pos += TAB_SIZE;
    newLine();
    type.genVKCode(this);
    pos -= TAB_SIZE;
    newLine();
    print("END TYPE");
  }

  /**
   * Prints a message definition
   */
  public void printMessageDefinition(String ident, String text) {
    newLine();
    print("MESSAGE ");
    print(ident);
    if (text != null) {
      print(" ");
      print('"' + text + '"');
    }
  }

  // ----------------------------------------------------------------------
  // TYPES
  // ----------------------------------------------------------------------

  /**
   * Prints a type
   */
  public void printStringType(int width, int height, int convert) {
    newLine();
    print("STRING(");
    print(width);
    if (height != 1) {
      print(", " + height);
    }
    print(")");
    String str = null;
    switch (convert) {
    case VConstants.FDO_CONVERT_NONE:
      return;
    case VConstants.FDO_CONVERT_UPPER:
      str = "UPPER";
      break;
    case VConstants.FDO_CONVERT_LOWER:
      str = "LOWER";
      break;
    case VConstants.FDO_CONVERT_NAME:
      str = "NAME";
      break;
    default:
      throw new InconsistencyException();
    }
    newLine();
    print(space(TAB_SIZE) + "CONVERT " + str);
  }

  /**
   * Prints a type
   */
  public void printBooleanType() {
    newLine();
    print("BOOL");
  }

  /**
   * Prints a type
   */
  public void printColorType() {
    newLine();
    print("COLOR");
  }

  /**
   * Prints a type
   */
  public void printImageType(int width, int height) {
    newLine();
    print("IMAGE(");
    print(width);
    print(", " + height);
    print(")");
  }

  /**
   * Prints a type
   */
  public void printTextType(int width, int height, int visibleHeight) {
    newLine();
    print("TEXT(");
    print(width);
    print(", " + height);
    print(", " + visibleHeight);
    print(")");
  }

  /**
   * Prints a type
   */
  public void printFixedType(int width,
                             int scale,
                             boolean fraction,
                             Fixed min,
                             Fixed max)
  {
    newLine();
    if (fraction) {
      print("FRACTION(");
    } else {
      print("FIXED(");
    }
    print(width);
    print(", " + scale);
    print(")");
    if (min != null) {
      newLine();
      print(space(TAB_SIZE) + "MINVAL " + min);
    }
    if (max != null) {
      newLine();
      print(space(TAB_SIZE) + "MAXVAL " + max);
    }
  }

  /**
   * Prints a type
   */
  public void printIntegerType(int width, int min, int max) {
    newLine();
    print("LONG(");
    print(width);
    print(")");
    if (min != Integer.MIN_VALUE) {
      newLine();
      print(space(TAB_SIZE) + "MINVAL " + min);
    }
    if (max != Integer.MAX_VALUE) {
      newLine();
      print(space(TAB_SIZE) + "MAXVAL " + max);
    }
  }

  /**
   * Prints a type
   */
  public void printDateType() {
    newLine();
    print("DATE");
  }

  /**
   * Prints a type
   */
  public void printTimeType() {
    newLine();
    print("TIME");
  }

  /**
   * Prints a type
   */
  public void printTimestampType() {
    newLine();
    print("TIMESTAMP");
  }

  /**
   * Prints a type
   */
  public void printMonthType() {
    newLine();
    print("MONTH");
  }

  /**
   * Prints a type
   */
  public void printWeekType() {
    newLine();
    print("WEEK");
  }

  /**
   * Prints a type
   */
  public void printCodeType(String baseType, VKCodeDesc[] code) {
    newLine();
    print("CODE " + baseType + " IS");
    pos += TAB_SIZE;
    printCode(code);
    pos -= TAB_SIZE;
    newLine();
    print("END CODE");
  }

  /**
   * Prints a type
   */
  public void printAlias(String name) {
    newLine();
    print("IS " + name);
  }

  /**
   * Prints a type
   */
  public void printFieldTypeName(String name, JExpression[] params) {
    newLine();
    print("TYPE " + name);
    if (params != null && params.length != 0) {
      print("(");
      for (int i = 0; i < params.length; i++) {
	params[i].accept(getJavaPrettyPrinter());
      }
      print(")");
    }
  }

  /**
   * Prints a type
   */
  public void printList(TableReference table,
                        CType newForm,
                        VKListDesc[] columns,
                        boolean access)
  {
    newLine();
    print("LIST ");
    // !!!!    table.accept(getJavaPrettyPrinter());
    if (newForm != null) {
      if (access) {
	print(" ACCESS ");
      } else {
	print(" NEW ");
      }
      print(newForm.toString());
    }
    pos += TAB_SIZE;
    printList(columns);
    pos -= TAB_SIZE;
    newLine();
    print("END LIST");
  }


  /**
   * Print definition collector
   */
  public void printDefinitionCollector(Vector inserts,
				       Vector types,
				       Vector menus,
				       Vector actors,
				       Vector commands,
				       Vector messages)
  {
    if (inserts != null && inserts.size() != 0) {
      newLine();
      for (int  i = 0; i < inserts.size(); i++) {
	print("INSERT \"" + inserts.elementAt(i) + "\"");
	newLine();
      }
    }

    pos += TAB_SIZE;

    if (types != null && types.size() != 0) {
      newLine();
      newLine();
      for (int  i = 0; i < types.size(); i++) {
	((VKDefinition)types.elementAt(i)).genVKCode(this);
      }
    }
    if (menus != null && menus.size() != 0) {
      newLine();
      newLine();
      for (int  i = 0; i < menus.size(); i++) {
	((VKDefinition)menus.elementAt(i)).genVKCode(this);
      }
    }
    if (actors != null && actors.size() != 0) {
      newLine();
      newLine();
      for (int  i = 0; i < actors.size(); i++) {
	((VKDefinition)actors.elementAt(i)).genVKCode(this);
      }
    }
    if (commands != null && commands.size() != 0) {
      newLine();
      newLine();
      for (int  i = 0; i < commands.size(); i++) {
	((VKDefinition)commands.elementAt(i)).genVKCode(this);
      }
    }
    if (messages != null && messages.size() != 0) {
      newLine();
      newLine();
      for (int  i = 0; i < messages.size(); i++) {
        ((VKDefinition)messages.elementAt(i)).genVKCode(this);
      }
    }
    newLine();

    pos -= TAB_SIZE;
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  /**
   * Print a compilation unit def
   */
  protected void printCUnit(JCompilationUnit unit) {
/*
    String packageName = unit.getPackageName();
    JPackageName[] importedPackages = unit.getImportedPackages();
    JClassImport[] importedClasses = unit.getImportedClasses();

    int	count = (packageName.length() > 0 ? 1 : 0) +
	importedPackages.length +
	importedClasses.length;

    if (count > 0) {
      print("{");
      pos += 2;

      if (packageName.length() > 0) {
	newLine();
	print("package\t" + packageName.replace('/', '.') + ";");
      }

      for (int i = 0; i < importedPackages.length ; i++) {
	String pack = importedPackages[i].getName().replace('/', '.');
	if (pack.equals("org.kopi.vkopi.lib.visual")) {
	  continue;
	}
	if (pack.equals("org.kopi.vkopi.lib.util")) {
	  continue;
	}
	if (pack.equals("org.kopi.xkopi.lib.base")) {
	  continue;
	}
	if (pack.equals("java.sql")) {
	  continue;
	}
	if (pack.equals("org.kopi.vkopi.lib.form")) {
	  continue;
	}
	newLine();
	print("import\t" + pack + ".*;");
      }

      for (int i = 0; i < importedClasses.length ; i++) {
	newLine();
	print("import\t" +
		importedClasses[i].getClassName().replace('/', '.') +
	      ";");
      }

      pos -= 2;
      newLine();
      print("}");
    }*/
  }

  public void printCodeDesc(String ident, String label, String value) {
    newLine();
    if (label != null) {
      print('"' + label + '"' + " = ");
    }
    if (ident != null) {
      print(ident + " : ");
    }
    print(value);
  }

  public void printListDesc(String title, String column, VKType type) {
    newLine();
    if (title != null) {
      print('"' + title + '"' + "= ");
    }
    print(column + " : ");
    type.genVKCode(this);
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  protected void printCode(VKCodeDesc[] code) {
    for (int i = 0; i < code.length; i++) {
      code[i].genVKCode(this);
    }
  }

  protected void printList(VKListDesc[] code) {
    for (int i = 0; i < code.length; i++) {
      code[i].genVKCode(this);
    }
  }

  protected void printModes(int modes) {
    int	count = 0;
    if (modes == 7 || modes == 0) {
      return;
    }
    if ((modes & (1 << VConstants.MOD_QUERY)) > 0) {
      print((count++ == 0 ? "ON " : ", ") + "QUERY");
    }
    if ((modes & (1 << VConstants.MOD_INSERT)) > 0) {
      print((count++ == 0 ? "ON " : ", ") + "INSERT");
    }
    if ((modes & (1 << VConstants.MOD_UPDATE)) > 0) {
      print((count++ == 0 ? "ON " : ", ") + "UPDATE");
    }
    print(" ");
  }

  protected void printEvents(long events) {
    int	count = 0;

    if ((events & (1 << VConstants.TRG_PREQRY)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREQRY");
    }
    if ((events & (1 << VConstants.TRG_POSTQRY)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTQRY");
    }
    if ((events & (1 << VConstants.TRG_PREDEL)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREDEL");
    }
    if ((events & (1 << VConstants.TRG_POSTDEL)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTDEL");
    }
    if ((events & (1 << VConstants.TRG_PREINS)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREINS");
    }
    if ((events & (1 << VConstants.TRG_POSTINS)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTINS");
    }
    if ((events & (1 << VConstants.TRG_PREUPD)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREUPD");
    }
    if ((events & (1 << VConstants.TRG_POSTUPD)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTUPD");
    }
    if ((events & (1 << VConstants.TRG_PRESAVE)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PRESAVE");
    }
    if ((events & (1 << VConstants.TRG_PREREC)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREREC");
    }
    if ((events & (1 << VConstants.TRG_POSTREC)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTREC");
    }
    if ((events & (1 << VConstants.TRG_PREBLK)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREBLK");
    }
    if ((events & (1 << VConstants.TRG_POSTBLK)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTBLK");
    }
    if ((events & (1 << VConstants.TRG_VALBLK)) > 0) {
      print((count++ == 0 ? "" : ", ") + "VALBLK");
    }
    if ((events & (1 << VConstants.TRG_VALREC)) > 0) {
      print((count++ == 0 ? "" : ", ") + "VALREC");
    }
    if ((events & (1 << VConstants.TRG_DEFAULT)) > 0) {
      print((count++ == 0 ? "" : ", ") + "DEFAULT");
    }
    if ((events & (1 << VConstants.TRG_INIT)) > 0) {
      print((count++ == 0 ? "" : ", ") + "INIT");
    }
    if ((events & (1 << VConstants.TRG_RESET)) > 0) {
      print((count++ == 0 ? "" : ", ") + "RESET");
    }
    if ((events & (1 << VConstants.TRG_CHANGED)) > 0) {
      print((count++ == 0 ? "" : ", ") + "CHANGED");
    }
    if ((events & (1 << VConstants.TRG_POSTCHG)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTCHG");
    }
    if ((events & (1 << VConstants.TRG_PREFLD)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREFLD");
    }
    if ((events & (1 << VConstants.TRG_POSTFLD)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTFLD");
    }
    if ((events & (1 << VConstants.TRG_PREVAL)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREVAL");
    }
    if ((events & (1 << VConstants.TRG_VALFLD)) > 0) {
      print((count++ == 0 ? "" : ", ") + "VALFLD");
    }
    if ((events & (1 << VConstants.TRG_FORMAT)) > 0) {
      print((count++ == 0 ? "" : ", ") + "FORMAT");
    }
    if ((events & (1 << VConstants.TRG_PREFORM)) > 0) {
      print((count++ == 0 ? "" : ", ") + "PREFORM");
    }
    if ((events & (1 << VConstants.TRG_POSTFORM)) > 0) {
      print((count++ == 0 ? "" : ", ") + "POSTFORM");
    }
    if ((events & (1 << VConstants.TRG_ACCESS)) > 0) {
      print((count++ == 0 ? "" : ", ") + "ACCESS");
    }
    if ((events & (1 << VConstants.TRG_FLDACCESS)) > 0) {
      print((count++ == 0 ? "" : ", ") + "ACCESS");
    }
    if ((events & (1 << VConstants.TRG_CMDACCESS)) > 0) {
      print((count++ == 0 ? "" : ", ") + "ACCESS");
    }
    if ((events & (1 << VConstants.TRG_VALUE)) > 0) {
      print((count++ == 0 ? "" : ", ") + "VALUE");
    }

    print(" ");
  }

  /**
   * prints an array length expression
   */
  public void printComment(JavaStyleComment[] comments) {
    // no comments
  }

  protected void printSeparator() {
    print("///////////////////////////////////////////////////////////////////////////");
  }

  protected void printSeparator4() {
    print("///////////////////////////////////////////////////////////////////////");
  }

  protected void printSeparator8() {
    print("///////////////////////////////////////////////////////////////////////");
  }

  protected void printBlockBanner(String name, String shortcut) {
    newLine();
    print("///////////////////////////////////////////////////////////////////////////");
    newLine();
    print("/// ");
    print(name);
    if (shortcut != null) {
      print(" . " + shortcut);
    }
    print(space(73 - 8 - name.length() - (shortcut == null ? 0 : shortcut.length())));
    print("///");
    newLine();
    print("///////////////////////////////////////////////////////////////////////////");
    newLine();
  }

  protected void printCopyright(String name) {
    print("///////////////////////////////////////////////////////////////////////////////");
    newLine();
    print("/// Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH                ///");
    newLine();
    print("/// All rights reserved.                                                    ///");
    newLine();
    print("///////////////////////////////////////////////////////////////////////////////");
    newLine();
    print("/// $Id$");
    newLine();
    print("///////////////////////////////////////////////////////////////////////////////");
    newLine();
  }

  protected void printHelp(String help) {
    if (help == null || help.equals("")) {
      return;
    }

    StringTokenizer tok = new StringTokenizer(help);
    int		ps = 10000;
    while (tok.hasMoreTokens()) {
      String s = tok.nextToken();
      if (ps + s.length() > 66) {
	if (ps != 10000) {
	  print("\"");
	}
	ps = pos;
	newLine();
	print("HELP \"");
      }
      print(s + (tok.hasMoreTokens() ? " " : ""));
      ps += s.length();
    }
    print("\"");
  }

  protected void print(int i) {
    p.print("" + i);
  }

  protected void print(String str) {
    p.print(str);
  }

  protected void println() {
    p.println();
  }

  protected void newLine() {
    p.println();
  }

  protected String space(int i) {
    return new String(new char[i]).replace((char)0, ' ');
  }

  // ----------------------------------------------------------------------
  // PROTECTED DATA MEMBERS
  // ----------------------------------------------------------------------

  protected String			old_menu;	// is on a for init
  protected int				TAB_SIZE = 4;

  protected int				pos;

  private TabbedPrintWriter		p;
  protected GKjcPrettyPrinter		in_pp;
  protected TypeFactory                 factory;
}
