/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.visual;

import java.io.File;
import java.util.ArrayList;

import org.kopi.vkopi.lib.util.Rexec;
/**
 * Manages Applicatin configuration data
 */
public abstract class ApplicationConfiguration {

  public static ApplicationConfiguration getConfiguration() {
    return ApplicationContext.getApplicationContext().getApplication().getApplicationConfiguration();
  }
  
  public static void setConfiguration(ApplicationConfiguration conf) {
    assert conf != null : "configuration must not be null";
    
    ApplicationContext.getApplicationContext().getApplication().setApplicationConfiguration(conf);
  }
  // --------------------------------------------------------------
  //   Application Properties
  // --------------------------------------------------------------

  /**
   * Property app.version
   * Returns the version of the application
   */
  public abstract String getVersion() throws PropertyException;

  /**
   * Property app.name
   * Returns the application name
   */
  public abstract String getApplicationName() throws PropertyException;

  /**
   * Property app.comment
   * Returns the information text about this application
   */
  public abstract String getInformationText() throws PropertyException;

  // --------------------------------------------------------------
  //   Application Debugging
  // --------------------------------------------------------------

  /**
   * Property debug.logfile
   * Returns the failure file to add errors
   */
  public abstract String getLogFile() throws PropertyException;

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  public boolean isDebugModeEnabled() {
    return false;
  }
  /**
   * Property debug.mail.recipient
   * Returns the mail recipient for failure messages
   *
   * For instance: failure.sys-admin@aHost.com
   */
  public abstract String getDebugMailRecipient() throws PropertyException;

  public abstract boolean mailErrors() throws PropertyException;

  public abstract boolean logErrors() throws PropertyException;

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  public abstract boolean debugMessageInTransaction() throws PropertyException;

  // --------------------------------------------------------------
  //   Application Properties
  // --------------------------------------------------------------

  /**
   * Property smtp.server
   * Returns the name of the SMTP server to use
   */
  public abstract String getSMTPServer() throws PropertyException;

  /**
   * Property fax.server
   * Returns the name of the fax server to use.
   */
  public abstract String getFaxServer() throws PropertyException;

  /**
   * Returns a RExec command handler
   */
  public abstract Rexec getRExec() throws PropertyException;

  // --------------------------------------------------------------
  //   Spell checking
  // --------------------------------------------------------------

  /**
   * Gets the url of the Dicionary Server e.g. c:/aspell
   */
  public abstract String getDictionaryServer() throws PropertyException;

  // --------------------------------------------------------------
  //   Basic Methods
  // --------------------------------------------------------------

  /**
   * Reads the value of the property
   *
   * @return the property
   */
  public abstract String getStringFor(String key) throws PropertyException;

  public abstract boolean getBooleanFor(String key) throws PropertyException;

  public abstract int getIntFor(String key) throws PropertyException;

  /**
   * Gets options for a language
   */
  public Language[] getDictionaryLanguages() {
    // no languages
    ArrayList<Language>   	langs = new ArrayList<Language>();
    String      		lang;
    int         		i=0;

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

  /**
   * Returns a directory on the local machine for file generation
   * Use the user.home property because the path
   * of the home directory is OS dependant.
   */
  public File getDefaultDirectory() {
    return new File(System.getProperty("user.home"));
  }


  // --------------------------------------------------------------
  // Preview with acroread
  // --------------------------------------------------------------

  public boolean useAcroread() {
    return false;
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
  
  //---------------------------------------------------------------------
  // Window size
  //---------------------------------------------------------------------
  
  public int getDefaultModalWindowWidth() {
    return 0;
  }
  
  public int getDefaultModalWindowHeight() {
    return 0;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static final String            STANDARD_PRINTER_NAME = "<Standard>";
}
