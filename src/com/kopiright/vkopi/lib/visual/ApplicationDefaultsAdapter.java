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

package com.kopiright.vkopi.lib.visual;

import java.io.File;
import java.io.IOException;

import com.kopiright.vkopi.lib.util.Rexec;
import com.kopiright.vkopi.lib.util.Printer;
import com.kopiright.vkopi.lib.util.PreviewPrinter;

/**
 * An interface for kopi application defaults data
 *
 * Such data may be statically defined:
 *   public String getSMTPServer() {
 *     return "liz";
 *   }
 *
 * Or dynamic using database or resource files.
 */
public class ApplicationDefaultsAdapter implements ApplicationDefaults {

  /**
   * Returns the failure file to add errors
   *
   * For instance: /tmp/kopi.log
   */
  public String getFailureLog() {
    return null;
  }

  /**
   * Returns the name of the SMTP server to use.
   */
  public String getSMTPServer() {
    return null;
  }

  /**
   * Returns the name of print server to use.
   */
  public String getPrintServer() {
    return null;
  }

  /**
   * Returns the nail recipient for failure messages
   *
   * For instance: failure.sys-admin@aHost.com
   */
  public String getFailureHost() {
    return "fehler@kopiright.com";
  }

  /**
   * Returns a RExec command handler
   */
  public Rexec getRExec() {
    return null;
  }

  /**
   * Returns a print for previewing document
   */
  public Printer getPreviewPrinter() {
    return new PreviewPrinter("PreviewPrinter", "gv");
  }

  /**
   * Returns the currently selected printer for current user
   */
  public Printer getCurrentPrinter() {
    return null;
  }

  /**
   * Returns a printer from its name
   */
  public Printer getPrinterByName(String name) {
    return getCurrentPrinter();
  }

  /**
   * Returns a printer that support envelope format
   */
  public Printer getEnvelopePrinter() {
    return getCurrentPrinter();
  }

  /**
   * Returns the specific printer for a form or the default one if none
   */
  public Printer getSpecificPrinter(String formName) {
    return getCurrentPrinter();
  }

  /**
   * Returns a printer that support this kind of media or the default one if none
   */
  public Printer getPrinterMedia(String media) {
    return getCurrentPrinter();
  }

  /**
   * Returns a directory on the local machine for file generation
   * Use the user.home property because the path
   * of the home directory is OS dependant.
   */
  public File getDefaultDirectory() {
    return new File(System.getProperty("user.home"));
  }

  /**
   * <p>Creates a temporary file in the default temporary directory.
   * The filename will look like this : prefixXXXX.extension</p>
   *
   * @param prefix the prefix of the temp file
   * @param extension the extension of the temp file (can be null. in this case default is "tmp")
   * @return an empty temp file on the local machine
   */
  public File getTempFile(String prefix, String extension) throws IOException {
    // 20020501 laurent : I have added to be sure backward compliant :
    // this temp file will not be deleted at the end of the application,
    // but perhaps it should be set to true.
    return com.kopiright.vkopi.lib.util.Utils.getTempFile(prefix, extension, false);
  }

  /**
   * Returns the location of image for the help
   */
  public String getHelpURL() {
    return "file:" + System.getProperty("user.dir") + "/help/";
  }

  /**
   * Returns the application name
   */
  public String getApplicationName() {
    String	name = System.getProperty("user.dir");
    int		index = name.lastIndexOf(System.getProperty("file.separator"));

    return index == -1 ? name : name.substring(index + 1);
  }

  /**
   * Returns the information text about this application
   */
  public String getInformationText() {
    return getApplicationName();
  }

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  public boolean isDebugModeEnabled() {
    return false;
  }

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  public boolean debugMessageInTransaction() {
    return true;
  }


  /**
   * Returns the maximal amount of memory for cache
   */
  public int getMaxCache() {
    return Integer.MAX_VALUE;
  }

  /**
   * Returns the default jdbc driver to use if no one is specified
   */
  public String  getDefaultJDBCDriver() {
    return "com.kopiright.kconnect.Driver";
  }

  /**
   * Returns the version of the application
   */
  public String getVersion() {
    return "0.0";
  }

  /**
   * Gets a property with its name
   */
  public String getPropertyByName(String prop) {
    return null;
  }

  /**
   * Gets the url of the Dicionary Server e.g. c:/aspell
   */
  public String getDictionaryServer() {
    return null; // no dict
  }

  /**
   * Gets options for a language
   */
  public Language[] getDictionaryLanguages() {
    // no languages
    return null;
  }
  

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

}
