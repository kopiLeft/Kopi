/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.doc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.form.LatexPrintWriter;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.visual.Application;
import com.kopiright.vkopi.lib.visual.ApplicationConfiguration;
import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.Utils;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.Query;

public class Main {

  private Main() {
    vect = new Vector();
  }

  // ----------------------------------------------------------------------
  // ENTRY POINT
  // ----------------------------------------------------------------------

  public static final void main(final String[] argv) throws Exception {
    if (argv.length < 5 || argv.length > 6) {
      displayUsage();
      System.exit(1);
    }

    // cut login
    String	login = argv[2];
    String	name;
    String	url;
    int		index = login.indexOf("@");
    if (index == -1) {
      displayUsage();
      System.exit(1);
    }
    name = login.substring(0, index);
    url = login.substring(index + 1);

    DBContext.registerDriver(argv[3]);
    context = new DBContext();
    context.setDefaultConnection(context.createConnection(url, name, argv[4]));

    new Thread(new Runnable() {
	public void run() {
	  try {
	    new Main().doIt(argv[0], argv[1], argv.length == 6 && argv[5].equals("image"));
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
      }}).start();
  }

  /**
   *
   */
  private static final void displayUsage() {
    System.err.println("Should indicate the name of the application, the title, the database, the driver, and the password");
    System.err.println("Exemple: java com.kopiright.drivers.kopi.help.Main bugs \"kopiRight Bugs Report\" user@jdbc:tbx://vie:4002/INTERN com.kopiright.tbx.TbxDriver XXXXXX");
  }

  /**
   *
   */
  public void doIt(String name, String title, boolean genImage) throws IOException {
    System.err.println("Makefile help generator");
    File	file = new File(System.getProperty("user.dir"));

    try {
      UIManager.setLookAndFeel(new com.kopiright.vkopi.lib.ui.plaf.KopiLookAndFeel());//UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println("Undefined look and feel: Kopi Look & Feel must be installed!");
      System.exit(1);
    }

    //    Application.installLF(Application.getDefaults().getKopiLFProperties());
    Application.setGeneratingHelp();
    Locale.setDefault(Locale.GERMAN); // !!! laurent 20020708 : do not force GERMAN

    // Load elements
    tree = createTree();
    Enumeration         elems = tree.preorderEnumeration();
    int                 count = 0;

    elems.nextElement(); // jump programme
    while (elems.hasMoreElements()) {
      Module    module = (Module)((DefaultMutableTreeNode)elems.nextElement()).getUserObject();

      if (module.getObject() != null) {
	try {
	  VForm                 form = (VForm)Module.getKopiExecutable(module.getObject());
	  FileWriter            fileWriter = new FileWriter(new File(file, form.getClass().getName().replace('.', '_') + ".tex"));
	  LatexPrintWriter      p = new LatexPrintWriter(new BufferedWriter(fileWriter));

	  form.genHelp(p, module.getDescription(), module.getHelp(), toAscii(module.toString()));
	  fileWriter.close();
	  if (genImage) {
	    form.printSnapshot();
	  }
	  System.err.println("[" + ++count + "] " + module.getDescription());
	} catch (Throwable e) {
	  e.printStackTrace();
	  System.err.println(">>>>>>>>" + module.getObject());
	}
      }
    }

    // Collect Elements
    doFile(new File(System.getProperty("user.dir")), name);

    if (vect.size() < 2) {
      System.err.println("Can't only process help if there is more than one element !");
      System.exit(1);
    }

    // Gen utils files
    genUtilFiles(title);

    // Generating help.tex
    genHelpFile(vect, name, title);

    System.exit(0);
  }

  /**
   *
   */
  private void genUtilFiles(String title) throws IOException {
    String              s;

    BufferedReader      i = new BufferedReader(new InputStreamReader(Utils.getFile("titlepage.sty")));
    PrintWriter         pw = new PrintWriter(new BufferedWriter(new FileWriter("titlepage.sty")));

    while ( (s = i.readLine()) != null) {
      pw.println(s);
    }
    pw.close();

    i = new BufferedReader(new InputStreamReader(Utils.getFile("top.tex")));
    pw = new PrintWriter(new BufferedWriter(new FileWriter("top.tex")));
    while ( (s = i.readLine()) != null) {
      pw.println(s);
    }
    pw.close();

    i = new BufferedReader(new InputStreamReader(Utils.getFile("macros.tex")));
    pw = new PrintWriter(new BufferedWriter(new FileWriter("macros.tex")));
    while ( (s = i.readLine()) != null) {
      pw.println(s);
    }
    pw.close();

    i = new BufferedReader(new InputStreamReader(Utils.getFile("general.tex")));
    pw = new PrintWriter(new BufferedWriter(new FileWriter("general.tex")));
    while ( (s = i.readLine()) != null) {
      pw.println(s);
    }
    pw.close();

    i = new BufferedReader(new InputStreamReader(Utils.getFile("german.ps")));
    pw = new PrintWriter(new BufferedWriter(new FileWriter("german.ps")));
    while ( (s = i.readLine()) != null) {
      pw.println(s);
    }
    pw.close();

    pw = new PrintWriter(new BufferedWriter(new FileWriter("Makefile")));
    pw.println("all:		help ps");
    pw.println();
    pw.println("help:");
    pw.println("		latex help.tex");
    pw.println("ps:");
    pw.println("		dvips -o help.ps help.dvi");
    pw.println("		head -12 help.ps > doc.ps");
    pw.println("		cat german.ps >> doc.ps");
    pw.println("		tail +12 help.ps >> doc.ps");
    pw.close();

    pw = new PrintWriter(new BufferedWriter(new FileWriter("name.tex")));
    pw.println("\\title{");
    pw.println(title);
    pw.println("}");
    pw.close();
  }

  /**
   *
   */
  private void genHelpFile(Vector vect, String appsName, String title) throws IOException {
    PrintWriter         pw = new PrintWriter(new BufferedWriter(new FileWriter("help.tex")));
    Hashtable           hash = new Hashtable();

    for (int i = 0; i < vect.size(); i++) {
      hash.put((String)vect.elementAt(i), (String)vect.elementAt(i));
    }

    BufferedReader      fr = new BufferedReader(new FileReader("top.tex"));

    String              s;
    while ((s = fr.readLine()) != null) {
      pw.println(s);
    }

    Enumeration         elems = tree.preorderEnumeration();

    elems.nextElement(); // jump programme
    while (elems.hasMoreElements()) {
      Module    module = (Module)((DefaultMutableTreeNode)elems.nextElement()).getUserObject();
      String	object = module.getObject();

      if (object == null && module.getParent() == 0) {
	genHelpForModule(vect, appsName, title, module, pw);
      }
    }
    pw.println("\\include{index}");
    pw.println("\\end{document}");

    pw.close();
  }

  /**
   *
   */
  private void genHelpForModule(Vector vect,
                                String appsName,
                                String title,
                                Module parent,
                                PrintWriter pw)
    throws IOException
  {
    Hashtable   hash = new Hashtable();

    pw.println("\\chapter{" + LatexPrintWriter.convert(parent.getDescription()) + "}");
    pw.println(LatexPrintWriter.convert(parent.getHelp()));

    for (int i = 0; i < vect.size(); i++) {
      hash.put((String)vect.elementAt(i), (String)vect.elementAt(i));
    }

    Enumeration                 elems = tree.preorderEnumeration();
    DefaultMutableTreeNode	node = null;

    elems.nextElement(); // jump programme
    while (elems.hasMoreElements()) {
      node = (DefaultMutableTreeNode)elems.nextElement();
      if (parent.equals(node.getUserObject())) {
	break;
      }
    }

    Enumeration         enum2 = node.preorderEnumeration();

    //enum2.nextElement(); // jump programme
    pw.println("\\begin{Menu}{" + LatexPrintWriter.convert(parent.getDescription()) + "}{" + parent  +"}");
    while (enum2.hasMoreElements()) {
      Module    module2 = (Module)((DefaultMutableTreeNode)enum2.nextElement()).getUserObject();
      String	object2 = module2.getObject();

      if (object2 != null) {
	try {
	  Class.forName(object2);
	  pw.println("\\Item{" + LatexPrintWriter.convert(module2.getDescription()) + "}{" + LatexPrintWriter.convert(module2.getHelp()) + "}{" + toAscii(module2.toString())  +"}");
	} catch (Throwable e) {
	  System.err.println(">>>>>>>>>>>" + object2);
	}
      }
    }
    pw.println("\\end{Menu}");

    enum2 = node.preorderEnumeration();
    //enum2.nextElement(); // jump programme
    while (enum2.hasMoreElements()) {
      Module    module2 = (Module)((DefaultMutableTreeNode)enum2.nextElement()).getUserObject();
      String	object2 = module2.getObject();

      if (object2 != null) {
	try {
	  String name = Class.forName(object2).getName().replace('.', '_') ;
	  pw.println("\\IncludeModule{" + name + "}");
	} catch (Throwable e) {
	  System.err.println(">>>>>>>>>>>" + object2);
	}
      }
    }
  }

  public void doFile(File f, String self) throws IOException {
    if (f.isDirectory()) {
      String[]  files = f.list();

      for (int i = 0; i < files.length; i++) {
	if ((files[i].length()> 4) &&
	    !files[i].equals(self + ".tex") &&
	    !files[i].equals("imports.tex") &&
	    files[i].substring(files[i].length() - 4).equals(".tex"))
          {
            vect.addElement(files[i].substring(0, files[i].length() - 4));
          }
      }
    }
  }

  /*
   * Builds the module tree.
   */
  private DefaultMutableTreeNode createTree() {
    Module[]	modules = loadModules(ApplicationConfiguration.getConfiguration().isUnicodeDatabase());

    if (modules == null) {
      System.exit(0);
      return null;
    } else {
      Module                    root;
      DefaultMutableTreeNode    ret;

      root = new Module(0,
			0,
			Message.getMessage("PROGRAM"),
			Message.getMessage("program"),
			null,
			Module.ACS_TRUE,
			null);

      ret = createTree(modules, root, false);

      if (ret == null) {
	System.exit(0);
      }

      return ret;
    }
  }

  /*
   * Builds the module tree.
   */
  private DefaultMutableTreeNode createTree(Module[] modules,
					    Module root,
					    boolean force)
  {
    if (root.getAccessibility() == Module.ACS_TRUE) {
      force = true;
    }

    if (root.getObject() != null) {
      return (! force) ? null : new DefaultMutableTreeNode(root);
    } else {
      DefaultMutableTreeNode    self = null;

      for (int i = 0; i < modules.length; i++) {
	if (modules[i].getParent() == root.getId()) {
	  DefaultMutableTreeNode	node;

	  node = createTree(modules, modules[i], force);
	  if (node != null) {
	    if (self == null) {
	      self = new DefaultMutableTreeNode(root);
	    }

	    self.add(node);
	  }
	}
      }
      return self;
    }
  }

  /*
   * Loads the accessible modules.
   */
  private Module[] loadModules(boolean isUnicode) {
    Query	query = new Query(context.getDefaultConnection());
    Vector	vector = new Vector();

    try {
      context.startWork();	// !!! BEGIN_SYNC

      query.open(QUERY_INIT);
      while (query.next()) {
	vector.addElement(new Module(query.getInt(1),
				     query.getInt(2),
				     query.getString(3),
				     query.getString(4),
				     query.getString(5),
				     Module.ACS_TRUE,
				     null));
      }
      query.close();

      query.open(QUERY_TEXT);
      int i = 0;
      while (query.next()) {
	while (query.getInt(1) != ((Module)(vector.elementAt(i))).getId()) {
	  i++;
	}
	if (!query.getBoolean(2)) {
	  vector.removeElementAt(i);
	} else {
	  ((Module)(vector.elementAt(i))).setAccessibility(Module.ACS_TRUE);
	  i++;
	}
      }
      query.close();

      context.commitWork();
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }

    if (vector.size() == 0) {
      return null;
    } else {
      Module[]		array = new Module[vector.size()];

      for (int i = 0; i < vector.size(); i++) {
	array[i] = (Module)vector.elementAt(i);
      }

      return array;
    }
  }

  private static final String toAscii(String in) {
    StringBuffer        convert = new StringBuffer();

    for (int i = 0; i < in.length(); i++) {
      convert.append(in.charAt(i) > 'z' ? 'X' : in.charAt(i));
    }
    return convert.toString();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final String	QUERY_INIT =
    " SELECT	M.ID, M.Vater, M.Kurzname, M.Source, M.Objekt" +
    " FROM	MODULE M" +
    " ORDER BY	1";

  private static final String	QUERY_TEXT =
    " SELECT	M.ID, B.Zugriff" +
    " FROM	MODULE M, BENUTZER N, BENUTZERRECHTE B" +
    " WHERE	N.ID = B.Benutzer" +
    " AND	M.ID = B.Modul" +
    " AND	N.Kurzname = lower(USER)" +
    " ORDER BY	1";


  private static DBContext              context;

  private Vector                        vect;	// Vector<File>
  private DefaultMutableTreeNode        tree;
}
