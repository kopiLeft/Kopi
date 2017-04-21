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

import java.io.CharArrayWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import org.kopi.util.mailer.Mailer;
import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.l10n.LocalizationManager;
import org.kopi.xkopi.lib.base.DBContext;

/**
 * {@code ApplicationContext} is a kopi application context that contains the
 * running {@link Application} instance in the context thread. The {@code ApplicationContext}
 * handles all shared applications components.
 */
public abstract class ApplicationContext {

  //-----------------------------------------------------------
  // ACCESSORS
  //-----------------------------------------------------------

  /**
   * Returns the {@code ApplicationContext} instance.
   * @return The {@code ApplicationContext} instance.
   */
  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * Sets the {@code ApplicationContext} instance.
   * @param context The {@code ApplicationContext} instance.
   */
  public static void setApplicationContext(ApplicationContext context) {
    assert context != null : "The ApplicationContext cannot be null";

    applicationContext = context;
  }

  //-----------------------------------------------------------
  // UTILS
  //-----------------------------------------------------------

  /**
   * Returns the default configuration of the Application
   */
  public static ApplicationConfiguration getDefaults() {
    return applicationContext.getApplication().getApplicationConfiguration();
  }

  /**
   * Returns the {@link Application} menu.
   * @return The {@link Application} menu.
   */
  public static VMenuTree getMenu() {
    return applicationContext.getApplication().getMenu();
  }

  /**
   * Returns the {@link LocalizationManager} instance.
   * @return The {@link LocalizationManager} instance.
   */
  public static LocalizationManager getLocalizationManager() {
    return applicationContext.getApplication().getLocalizationManager();
  }

  /**
   * Returns the default application {@link Locale}.
   * @return The default application {@link Locale}.
   */
  public static Locale getDefaultLocale() {
    return applicationContext.getApplication().getDefaultLocale();
  }

  /**
   * Returns the application {@link Registry}.
   * @return the application {@link Registry}.
   */
  public static Registry getRegistry() {
    return applicationContext.getApplication().getRegistry();
  }

  /**
   * Returns the application {@link DBContext}.
   * @return The application {@link DBContext}.
   */
  public static DBContext getDBContext() {
    return applicationContext.getApplication().getDBContext();
  }

  /**
   * Returns {@code true} if the {@link Application} should only generate help.
   * @return {@code true} if the {@link Application} should only generate help.
   */
  public static boolean isGeneratingHelp() {
    return applicationContext.getApplication().isGeneratingHelp();
  }

  /**
   * Displays an error message outside a model context. This can happen when launching a module.
   * @param parent The parent component.
   * @param message The message to be displayed.
   */
  public static void displayError(UComponent parent, String message) {
    applicationContext.getApplication().displayError(parent, message);
  }

  // ---------------------------------------------------------------------
  // SEND A BUG REPORT
  // ---------------------------------------------------------------------

  /**
   * Reports a trouble at execution time.
   *
   * @param     module          the module where the trouble was detected
   * @param     reason          the exception that triggered the bug report
   */
  public static void reportTrouble(String module,
                                   String place,
                                   String data,
                                   Throwable reason)
  {
    if (applicationContext.getApplication().isNobugReport()) {
      System.out.println("notice: reporting trouble is disabled, no mail will be sent.");
      System.err.println(reason.getMessage());
      reason.printStackTrace(System.err);
    } else {
      String			smtpServer;
      String                    applicationName;
      String                    version;
      String                    logFile;
      boolean                   sendMail;
      boolean                   writeLog;
      String                    revision = null;
      String                    releaseDate = null;
      String[]                  versionArray = org.kopi.vkopi.lib.base.Utils.getVersion();

      for (int i = 0; i < versionArray.length; i++) {
        if (versionArray[i].startsWith("Revision: ")) {
          revision = versionArray[i].substring(10);
        } else if ((versionArray[i].startsWith("Last Changed Date: "))) {
          releaseDate = versionArray[i].substring(19);
        }
      }

      if (getDefaults() == null) {
        System.err.println("ERROR: No application configuration available");
        return;
      }

      try {
        applicationName = getDefaults().getApplicationName();
      } catch (PropertyException e) {
        applicationName = "application name not defined";
      }
      try {
        version = getDefaults().getVersion();
      } catch (PropertyException e) {
        version = "version not defined";
      }
      try {
        smtpServer = getDefaults().getSMTPServer();
      } catch (PropertyException e) {
        smtpServer = null;
      }
      try {
        logFile = getDefaults().getLogFile();
      } catch (PropertyException e) {
        logFile = null;
      }

      try {
        sendMail = getDefaults().mailErrors();
      } catch (PropertyException e) {
        sendMail = false;
      }
      try {
        writeLog = getDefaults().logErrors();
      } catch (PropertyException e) {
        writeLog = false;
      }

      if (smtpServer != null && sendMail) {
        String  recipient;
        String  cc;
        String  bcc;
        String  sender;

        try {
          recipient = getDefaults().getDebugMailRecipient();
        } catch (PropertyException e) {
          recipient = "fehler@kopiright.com";
        }
        try {
          cc = getDefaults().getStringFor("debugging.mail.cc");
        } catch (PropertyException e) {
          cc = null;
        }
        try {
          bcc = getDefaults().getStringFor("debugging.mail.bcc");
        } catch (PropertyException e) {
          bcc = null;
        }
        try {
          sender = getDefaults().getStringFor("debugging.mail.sender");
        } catch (PropertyException e) {
          sender = "kopi@kopiright.com";
        }

        StringWriter    buffer = new StringWriter();
        PrintWriter     writer = new PrintWriter(buffer);
        // failureID is added to the subject of the mail.
        // similar error mail should have the smae id which makes the
        // easier to find duplicated messages.
        String            failureID;

        writer.println("Application Name:    " + applicationName);
        writer.println("SVN Version:         " + (revision == null ? "no revision available." : revision));
        writer.println("Version:             " + version);
        writer.println("Release Date:        " + (releaseDate == null ? "not available." : releaseDate));
        writer.println("Module:              " + module);
        writer.println("Started at:          " + applicationContext.getApplication().getStartupTime());
        writer.println();
        writer.println("Architecture:        " + System.getProperty("os.arch",""));
        writer.print  ("Operating System:    " + System.getProperty("os.name","") + " " );
        writer.println(System.getProperty("os.version",""));

        writeNetworkInterfaces(writer);

        writer.println("Local Time:          "+(new Date()).toString() + ":");
        writer.println("Default Locale:      "+Locale.getDefault());
        writer.println("Default Encoding:    "+(new InputStreamReader(System.in)).getEncoding());
        writer.println();
        try {
          writer.println("Kopi-User:           "+applicationContext.getApplication().getUserName());
        } catch (Exception e) {
          writer.println("Kopi-User:           <not available>");
        }
        writer.println("System-User/Name:    "+System.getProperty("user.name",""));
        writer.println("System-User/Home:    "+System.getProperty("user.home",""));
        writer.println("System-User/Dir:     "+System.getProperty("user.dir",""));
        writer.println();
        writer.println("Java Version:        "+System.getProperty("java.version",""));
        writer.println("Java Vendor:         "+System.getProperty("java.vendor",""));
        writer.println("Java Home:           "+System.getProperty("java.home",""));
        writer.println("Java VM Version:     "+System.getProperty("java.vm.version",""));
        writer.println("Java VM Vendor:      "+System.getProperty("java.vm.vendor",""));
        writer.println("Java VM Name:        "+System.getProperty("java.vm.name",""));
        writer.println("Java Class Version:  "+System.getProperty("java.class.version",""));
        writer.println("Java Class Path:     "+System.getProperty("java.class.path",""));
        writer.println("Java Libr. Path:     "+System.getProperty("java.library.path",""));
        writer.println("Java Tmp. Directory: "+System.getProperty("java.io.tmpdir",""));
        writer.println("Java Compiler:       "+System.getProperty("java.compiler",""));
        writer.println("Java Ext. Direct.:   "+System.getProperty("java.ext.dirs",""));
        writer.println();
        writer.println("Memory Usage:        total = "+ Runtime.getRuntime().totalMemory()
                       + "  free = " + Runtime.getRuntime().freeMemory()
                       + "  used = " + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
        writer.println();
        writer.println("Catched at:          "+place);
        if (reason != null) {
          writer.println("Message:             "+reason.getMessage());
          writer.println("Exception:           ");
          reason.printStackTrace(writer);

          try {
            CharArrayWriter       write;

            write = new CharArrayWriter();
            reason.printStackTrace(new PrintWriter(write));
            failureID = " " + write.toString().hashCode();
          } catch (Exception e) {
            failureID = " " + e.getMessage();
          }
        } else {
          failureID = " "; // no information
        }
        writer.println();
        writer.println("Information:         " + data);
        writer.flush();

        Mailer.sendMail(smtpServer,
                        recipient,
                        cc,
                        bcc,
                        "[KOPI ERROR] " + applicationName + failureID,
                        buffer.toString(),
                        sender);
      }

      if (logFile != null && writeLog) {
        try {
          final PrintWriter       writer;

          writer = new PrintWriter(new FileWriter(logFile, true));
          writer.println();
          writer.println();
        try {
          writer.println(applicationContext.getApplication().getUserName() + ":" + new Date());
        } catch (Exception e) {
          writer.println("<user no available>" + ":" + new Date());
        }
        writer.println(reason.getMessage());
        reason.printStackTrace(writer);

        if (writer.checkError()) {
          throw new IOException("error while writing");
        }
        writer.close();
        } catch (IOException e) {
          System.err.println("Can't write to file:" + logFile);
          System.err.println(": " + e.getMessage());
        }
      }
      System.err.println(reason.getMessage());
      reason.printStackTrace(System.err);
    }
  }

  /**
   * Write the network interfaces.
   * @param writer The Writer object.
   */
  private static void writeNetworkInterfaces(PrintWriter writer) {
    try {
      // find out which ip-addresses this host has
      for (Enumeration<NetworkInterface> netInterfaces = java.net.NetworkInterface.getNetworkInterfaces();netInterfaces.hasMoreElements();) {
	NetworkInterface        ni = netInterfaces.nextElement();

	writer.println("Netzwerk:            " + ni.getDisplayName());
	for (Enumeration<InetAddress> addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
	  InetAddress           address = addresses.nextElement();

	  writer.println("                     "
	                 + address.getHostAddress() + " "
	                 + address.getCanonicalHostName());
	}
      }
    } catch (SocketException e) {
    }
  }


  //-----------------------------------------------------------
  // ABSTRACT METHODS
  //-----------------------------------------------------------

  /**
   * Returns the <b>current</b> {@link Application} instance.
   * @return The <b>current</b> {@link Application} instance.
   */
  public abstract Application getApplication();
  
  /**
   * Returns the <b>current</b> {@link PreviewRunner} instance.
   * @return The <b>current</b> {@link PreviewRunner} instance.
   */
  public abstract PreviewRunner getPreviewRunner();
  
  /**
   * Returns <code>true</code> if we are in a web application context.
   * @return <code>true</code> if we are in a web application context.
   */
  public abstract boolean isWebApplicationContext();
  
  //-----------------------------------------------------------
  // DATA MEMBERS
  //-----------------------------------------------------------

  private static ApplicationContext		applicationContext;
  public static int compt = 0;
}
