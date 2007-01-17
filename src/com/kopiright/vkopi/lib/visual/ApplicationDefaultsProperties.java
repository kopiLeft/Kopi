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

package com.kopiright.vkopi.lib.visual;

import java.io.File;
import java.util.ArrayList;

import com.kopiright.vkopi.lib.util.PreviewPrinter;
import com.kopiright.vkopi.lib.util.Printer;
import com.kopiright.vkopi.lib.util.PropertyManager;
import com.kopiright.vkopi.lib.util.RPrinter;
import com.kopiright.vkopi.lib.util.Rexec;

/**
 * An interface for kopi application defaults data
 *
 * Such data may be statically defined:
 *   public String getSMTPServer() {
 *     return "liz";
 *   }
 *
 * usinf resource files
 */
public class ApplicationDefaultsProperties extends ApplicationDefaultsAdapter {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param file               the properties file name
   */
  public ApplicationDefaultsProperties(String file) {
    resources = new PropertyManager(file);
  }

  /**
   * Returns the failure file to add errors
   *
   * For instance: /tmp/kopi.log
   */
  public String getFailureLog() {
    final String	ret = getString("getFailureLog");

    return (ret == null) ? super.getFailureLog() : ret;
  }

  /**
   * Returns the name of the SMTP server to use.
   */
  public String getSMTPServer() {
    final String	ret = getString("getSMTPServer");

    return (ret == null) ? super.getSMTPServer() : ret;
  }

  /**
   * Returns the name of the print server to use.
   */
  public String getPrintServer() {
    // !!! 20030623 laurent : the property "host" is deprecated to
    // define the print server. It must be replaced with the property
    // "getPrintServer"
    final String	printServer = getString("getPrintServer");
    final String	host = getString("host");

    return (printServer != null) ? printServer : host != null ? host : super.getPrintServer();
  }

  /**
   * Returns the mail recipient for failure messages
   *
   * For instance: failure.sys-admin@aHost.com
   */
  public String getFailureHost() {
    String	ret = getString("getFailureHost");

    return (ret == null) ? super.getFailureHost() : ret;
  }

  /**
   * Returns a RExec command handler
   */
  public Rexec getRExec() {
    String	user = getString("user");
    String	pass = getString("pass");
    String      host = getString("host");

    if (pass == null || user == null || host == null) {
      return super.getRExec();
    } else {
      Rexec	rexec = new Rexec(host);

      rexec.setUser(user, pass);
      return rexec;
    }
  }

  /**
   * Returns a print for previewing document
   */
  public Printer getPreviewPrinter() {
    String	command = getString("getPreviewPrinter");

    if (command == null) {
      return super.getPreviewPrinter();
    } else {
//       File file = null;
//       try {
//         file = Utils.getTempFile("PREVIEW", "PS");
//       } catch (IOException e) {
//         e.printStackTrace();
//       }
//       return new PreviewPrinter("PreviewPrinter", command, file);
      return new PreviewPrinter("PreviewPrinter", command);
    }
  }

  /**
   * Returns the currently selected printer for current user
   */
  public Printer getCurrentPrinter() {
    String	user = getString("user");
    String	pass = getString("pass");
    String      lpr = getString("lpr");
    String      printServer = getPrintServer();

    if (lpr == null || pass == null || user == null || printServer == null) {
      return super.getCurrentPrinter();
    } else {
      RPrinter	printer = new RPrinter(STANDARD_PRINTER_NAME, printServer);

      printer.setCommand(user, pass, lpr);
      return printer;
    }
  }

  /**
   * Returns the currently selected printer for current user
   */
  public Printer getPrinterByName(String name) {
    return getSpecificPrinter(name);
  }

  /**
   * Returns a printer that support envelope format
   */
  public Printer getEnvelopePrinter() {
    return getPrinterByName("Envelope");
  }

  /**
   * Returns the specific printer for a form or the default one if none
   */
  public Printer getSpecificPrinter(String formName) {
    String	user = getString("user");
    String	pass = getString("pass");
    String      lpr = getString("lpr_" + formName);
    String      printServer = getPrintServer();

    if (lpr == null || pass == null || user == null || printServer == null) {
      return null;
    } else {
      RPrinter	printer = new RPrinter(STANDARD_PRINTER_NAME, printServer);

      printer.setCommand(user, pass, lpr);
      return printer;
    }
  }

  /**
   * Returns a printer that support this kind of media or the default one if none
   */
  public Printer getPrinterMedia(String media) {
    return getCurrentPrinter();
  }

  /**
   * Returns the application name
   *
   * For instance: http://www.kopright.com/an_apps/help
   */
  public String getHelpURL() {
    String	ret = getString("getHelpURL");

    return (ret == null) ? super.getHelpURL() : ret;
  }

  /**
   * Returns a directory on the local machine for file generation
   *
   * For instance: C:\ users\ anUser
   */
  public File getDefaultDirectory() {
    String	ret = getString("getDefaultDirectory");

    return (ret == null) ? super.getDefaultDirectory() : new File(ret);
  }

  /**
   * Returns the application's name
   * For instance: edt
   *
   * @return the application's name
   */
  public String getApplicationName() {
    String	ret = getString("getApplicationName");

    return ret == null ? super.getApplicationName() : ret;
  }

  /**
   * Returns the information text about this application
   *
   * For instance: edt
   */
  public String getInformationText() {
    String	ret = getString("getInformationText");

    return (ret == null) ?
      super.getInformationText() :
      getApplicationName().toUpperCase() + "\n" + ret;
  }

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  public boolean isDebugModeEnabled() {
    String	ret = getString("isDebugModeEnabled");

    return (ret == null) ? super.isDebugModeEnabled() : ret.equals("true");
  }

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  public boolean debugMessageInTransaction() {
    String	ret = getString("debug.messageInTransaction");

    return (ret == null) ? super.debugMessageInTransaction() : ret.equals("true");
  }

  /**
   * Returns the maximal amount of memory for cache
   */
  public int getMaxCache() {
    String	ret = getString("getMaxCache");

    if (ret != null) {
      try {
	return Integer.parseInt(ret);
      } catch (Exception e) {
	System.err.println("Can't convert getMaxCache in integer");
      }
    }

    return super.getMaxCache();
  }

  /**
   * Returns the default jdbc driver to use if no one is specified
   */
  public String  getDefaultJDBCDriver() {
    String	ret = getString("getDefaultJDBCDriver");

    return (ret == null) ? super.getDefaultJDBCDriver() : ret;
  }

  /**
   * Gets a property with its name
   */
  public String getPropertyByName(String prop) {
    String	ret = getString(prop);

    return (ret == null) ? super.getPropertyByName(prop) : ret;
  }

  /**
   * Returns the version of the application
   */
  public String getVersion() {
    String	ret = getString("version");

    return (ret == null) ? super.getVersion() : ret;
  }

  /**
   * Display all properties
   */
  public void displayProperties() {
    printProperties("getFailureLog");
    printProperties("getSMTPServer");
    printProperties("getFailureHost");
    printProperties("user");
    printProperties("pass");
    printProperties("host");
    printProperties("version");
    printProperties("getPreviewPrinter");
    printProperties("Envelope");
    printProperties("getHelpURL");
    printProperties("getApplicationName");
    printProperties("getInformationText");
    printProperties("getVersion");
    printProperties("isDebugModeEnabled");
    printProperties("getMaxCache");
  }

  private void printProperties(String name) {
    System.err.println(name + " = " + getString(name));
  }

  // ----------------------------------------------------------------------
  // PROTECTED METHODS
  // ----------------------------------------------------------------------

  /**
   * Returns the property
   *
   * @return the property
   */
  protected String getString(String key) {
    if (resources == null) {
      return null;
    } else {
      String    value;

      try {
        value = resources.getString(key);
      } catch (Exception e) {
        value = null;
      }
      if (value == null) {
        // operating system specific property ?
        String  platform;

        platform = System.getProperty("os.name").toLowerCase();
        if (platform.startsWith("linux")) {
          platform = "linux";
        } else if (platform.startsWith("windows")) {
          platform = "windows";
        } else {
          System.err.println("Unknown platform " + platform);
          platform = null;
        }

        if (platform != null) {
          try {
            value = resources.getString(key + "." + platform);
          } catch (Exception e) {
            value = null;
          }
        }
      }

      return value;
    }
  }

  /**
   * Gets the url of the Dicionary Server e.g. c:/aspell
   */
  public String getDictionaryServer() {
    return getString("aspell.server"); // no dict
  }

  /**
   * Gets options for a language
   */
  public Language[] getDictionaryLanguages() {
    // no languages
    ArrayList   langs = new ArrayList();
    String      lang;
    int         i=0;

    while ((lang = getString("aspell."+i+".language")) != null) {
      langs.add(new Language(lang, getString("aspell."+i+".options")));
      i++;
    }

    return (Language[]) langs.toArray(new Language[langs.size()]);
  }
  


  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private PropertyManager       resources;

  public static final String    STANDARD_PRINTER_NAME = "<Standard>";
}
