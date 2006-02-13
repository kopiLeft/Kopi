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
import java.io.IOException;

import java.util.ArrayList;

import com.kopiright.vkopi.lib.util.Rexec;

/**
 * Manages Applicatin configuration data
 */
public class ApplicationConfiguration {

  protected ApplicationConfiguration() {
    // if you use this method 
    // overload getStringFor
  }

  public ApplicationConfiguration(String file) {
    resources = new PropertyManager(file);
  }

  public static ApplicationConfiguration getConfiguration() {
    return configuration;
  }
  public static void setConfiguration(ApplicationConfiguration conf) {
    assert conf != null : "configuration must not be null";
    configuration = conf;
  }

  // --------------------------------------------------------------
  //   Application Properties
  // --------------------------------------------------------------

  /**
   * Property app.version  
   * Returns the version of the application
   */
  public String getVersion() throws PropertyException {
    return getStringFor("app.version");
  }

  /**
   * Property app.name
   * Returns the application name
   */
  public String getApplicationName() throws PropertyException {
    return getStringFor("app.name");
  }

  /**
   * Property app.comment  
   * Returns the information text about this application
   */
  public String getInformationText() throws PropertyException {
    return getStringFor("app.comment");
  }

  // --------------------------------------------------------------
  //   Application Debugging
  // --------------------------------------------------------------

  /**
   * Property debug.logfile  
   * Returns the failure file to add errors
   */
  public String getLogFile() throws PropertyException {
    return getStringFor("debugging.logfile");
  }

  /**
   * Property debug.mail.recipient
   * Returns the nail recipient for failure messages
   *
   * For instance: failure.sys-admin@aHost.com
   */
  public String getDebugMailRecipient() throws PropertyException {
    return getStringFor("debugging.mail.recipient");
  }

  public boolean mailErrors() throws PropertyException {
    return getBooleanFor("debugging.report.mail");
  }

  public boolean logErrors() throws PropertyException {
    return getBooleanFor("debugging.report.log");
  }

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  public boolean debugMessageInTransaction() throws PropertyException {
    return getBooleanFor("debug.report.messageInTransaction");
  }

  // --------------------------------------------------------------
  //   Application Properties
  // --------------------------------------------------------------

  /**
   * Property smtp.server  
   * Returns the name of the SMTP server to use
   */
  public String getSMTPServer() throws PropertyException {
    return getStringFor("smtp.server");
  }

  /**
   * Property print.server  
   * Returns the name of the print server to use. for printing
   */
  public String getPrintServer() throws PropertyException {
    return getStringFor("print.server");    
  }

  /**
   * Property fax.server 
   * Returns the name of the fax server to use.
   */
  public String getFaxServer() throws PropertyException {
    return getStringFor("fax.server");    
  }

  // --------------------------------------------------------------
  //  RExec
  // --------------------------------------------------------------

  /**
   * Returns a RExec command handler
   */
  public Rexec getRExec() throws PropertyException {
    String	user = getStringFor("rexec.user");
    String	pass = getStringFor("rexec.password");
    String      host = getStringFor("rexec.server");

    if (pass == null || user == null || host == null) {
      return null;
    } else {
      Rexec	rexec = new Rexec(host);

      rexec.setUser(user, pass);
      return rexec;
    }
  }

  // --------------------------------------------------------------
  //   Spell checking
  // --------------------------------------------------------------

  /**
   * Gets the url of the Dicionary Server e.g. c:/aspell
   */
  public String getDictionaryServer() throws PropertyException {
    return getStringFor("aspell.server"); // no dict
  }

  /**
   * Gets options for a language
   */
  public Language[] getDictionaryLanguages() {
 // no languages
    ArrayList   langs = new ArrayList();
    String      lang;
    int         i=0;

    try {
      while ((lang = getStringFor("aspell."+i+".language")) != null) {
        langs.add(new Language(lang, getStringFor("aspell."+i+".options")));
        i++;
      }
    } catch (PropertyException e) {
      // no more languages definied
    }

    return (Language[]) langs.toArray(new Language[langs.size()]);
  }

  public class Language {
    public Language(String language, String options) {
      this.language = language;
      this.options = options;
    }

    public final String      language; //name of the language
    public final String      options; // options
  }

  // --------------------------------------------------------------
  //   Basic Methods
  // --------------------------------------------------------------

  /**
   * Reads the value of the property from a file
   * Override this meht to read it from somewhere else 
   * (e.g. a Database)
   *
   * @return the property
   */
  public String getStringFor(String key) throws PropertyException {
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

  public boolean getBooleanFor(String key) throws PropertyException {
    return Boolean.valueOf(getStringFor(key)).booleanValue();
  }

  public int getIntFor(String key) throws PropertyException {
    return Integer.parseInt(getStringFor(key));
  }
  
  /**
   * Returns a directory on the local machine for file generation
   * Use the user.home property because the path
   * of the home directory is OS dependant.
   */
  public File getDefaultDirectory() {
    return new File(System.getProperty("user.home"));
  }

  // --------------------------------------------------------------
  // Database Encoding 
  // --------------------------------------------------------------

  public boolean isUnicodeDatabase() {
    return false;
  }

  // ----------------------------------------------------------------------
  // User configuration
  // ----------------------------------------------------------------------

  public UserConfiguration getUserConfiguration() {
    return null;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private PropertyManager                       resources;
  private static ApplicationConfiguration       configuration;
}
