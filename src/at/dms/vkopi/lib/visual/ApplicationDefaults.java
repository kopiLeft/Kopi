/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.visual;

import java.io.File;
import java.io.IOException;

import at.dms.vkopi.lib.util.Rexec;
import at.dms.vkopi.lib.util.Printer;

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
public interface ApplicationDefaults {

  /**
   * Returns the failure file to add errors
   *
   * For instance: /tmp/kopi.log
   */
  String getFailureLog();

  /**
   * Returns the smtp server to use for sending mail
   *
   * For instance: liz.dms.at
   */
  String getSMTPServer();

  /**
   * Returns the print server to use for printing
   *
   * For instance: print.dms.at
   */
  String getPrintServer();

  /**
   * Returns the nail recipient for failure messages
   *
   * For instance: failure.sys-admin@aHost.com
   */
  String getFailureHost();

  /**
   * Returns a RExec command handler
   */
  Rexec getRExec();

  /**
   * Returns a print for previewing document
   */
  Printer getPreviewPrinter();

  /**
   * Returns the currently selected printer for current user
   */
  Printer getCurrentPrinter();

  /**
   * Returns a printer from its name
   */
  Printer getPrinterByName(String name);

  /**
   * Returns a printer that support envelope format
   */
  Printer getEnvelopePrinter();

  /**
   * Returns the specific printer for a form or the default one if none
   */
  Printer getSpecificPrinter(String formName);

  /**
   * Returns a printer that support this kind of media or the default one if none
   */
  Printer getPrinterMedia(String media);

  /**
   * Returns the application name
   *
   * For instance: edt
   */
  String getApplicationName();

  /**
   * Returns the information text about this application
   *
   * For instance: edt
   */
  String getInformationText();

  /**
   * Returns a directory on the local machine for file generation
   *
   * For instance: C:\ users\ anUser
   */
  File getDefaultDirectory();

  /**
   * <p>Creates a temporary file in the default temporary directory.
   * The filename will look like this : prefixXXXX.extension</p>
   *
   * @param prefix the prefix of the temp file
   * @param extension the extension of the temp file (can be null. in this case default is "tmp")
   * @return an empty temp file on the local machine
   */
  File getTempFile(String prefix, String extension) throws IOException;

  /**
   * Returns the location of image for the help
   *
   * For instance: http://www.dms.at/an_apps/help
   */
  String getHelpURL();

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  boolean isDebugModeEnabled();

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  boolean debugMessageInTransaction();

  /**
   * Returns the maximal amount of memory for cache
   */
  int getMaxCache();

  /**
   * Returns the default jdbc driver to use if no one is specified
   */
  String getDefaultJDBCDriver();

  /**
   * Gets a property with its name
   */
  String getPropertyByName(String prop);

  /**
   * Gets the url of the Dicionary Server e.g. c:/aspell
   */
  String getDictionaryServer();

  /**
   * Gets options for a language
   */
  Language[] getDictionaryLanguages();

  /**
   * Returns the version of the application
   */
  String getVersion();


  class Language {
    public Language(String language, String options) {
      this.language = language;
      this.options = options;
    }

    public final String      language; //name of the language
    public final String      options; // options
  }
}
