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

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import com.kopiright.util.mailer.Mailer;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.Query;

public abstract class Application extends java.applet.Applet implements MessageListener {

  // ---------------------------------------------------------------------
  // CONSTRUCTOR
  // ---------------------------------------------------------------------

  /**
   * Constructor, show splashscreen
   *
   * @param     defaults        the application defaults.
   * @param     register        the register to use for this appilcation.
   */
  public Application(ApplicationDefaults defaults, Registry registry) {
    Application.instance = this;
    this.defaults = defaults;
    this.registry = registry;
  }

  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------

  public static Application getApplication() {
    return instance;
  }

  /**
   * Get the defaults application s of the Application
   */
  public static ApplicationDefaults getDefaults() {
    return instance == null ? null : instance.defaults;
  }

  /**
   * Returns the menu window of this application
   */
  public static MenuTree getMenu() {
    return instance == null ? null : instance.menuTree;
  }

  /**
   * Sets the appliction in help generating mode
   */
  public static void setGeneratingHelp() {
    instance.isGeneratingHelp = true;
  }

  /**
   * Returns true if the application in help generating  mode
   */
  public static boolean isGeneratingHelp() {
    return instance.isGeneratingHelp;
  }

  /**
   * get the DBContext
   */
  public static DBContext getDBContext() {
    return instance.context;
  }

  /**
   * get the user name
   */
  public static String getUserName() {
    return instance.context.getDefaultConnection().getUserName();
  }

  /**
   * get the database url
   */
  public static String getURL() {
    return instance.context.getDefaultConnection().getURL();
  }

  /**
   *
   */
  public static boolean isStarted() {
    return instance != null && instance.isStarted;
  }

  /**
   *
   */
  public static void quit() {
    if (instance == null || instance.allowQuit()) {
      System.exit(0);
    }
  }

  /**
   * Retruns the register for this application.
   */
  public static Registry getRegistry() {
    return instance.registry;
  }

  // --------------------------------------------------------------------

  /**
   * This methods is called to log an user
   */
  public abstract DBContext login(String database,
                                  String driver,
                                  String username,
                                  String password);

  /**
   * This methods is called when an user want to quit the application
   */
  public boolean allowQuit() {
    // default does nothing
    return true;
  }

  /**
   * This methods is called at the begining
   * you should use it to define locale, debugMode...
   */
  public void initialize() {
    if (registry != null) {
      registry.buildDependencies();
    }
  }

  /**
   *
   */
//   public static void installLF(KopiLFProperties prop) {
//     DObject.loadKopiLFProperties(prop);
//   }

  // ---------------------------------------------------------------------
  // INITIALISATION
  // ---------------------------------------------------------------------

  /**
   * Runs the application.
   */
  public boolean run(String[] args) {
    try {
      UIManager.setLookAndFeel(new com.kopiright.vkopi.lib.ui.plaf.KopiLookAndFeel());//UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println("Undefined look and feel: Kopi Look & Feel must be installed!");
      System.exit(1);
      return false;
    }

    if (! processCommandLine(args)) {
      return false;
    }

    displaySplashScreen();

    // do customer-sepcific initialisations
    initialize();

    verifyConfiguration();

    if (! connectToDatabase()) {
      exitWithError(1);
      return false;
    }

     try {
       UIManager.setLookAndFeel(new com.kopiright.vkopi.lib.ui.plaf.KopiLookAndFeel());//UIManager.getSystemLookAndFeelClassName());
     } catch (Exception e) {
       System.err.println("Undefined look and feel: Kopi Look & Feel must be installed!");
     }
    //   if (!DObject.isLookAndFeelInstalled()) {
    //  installLF(defaults.getKopiLFProperties());
      //    }

    startApplication();


    isStarted = true;

    return true;
  }

  /**
   * Processes the command line
   */
  private boolean processCommandLine(String[] args) {
    options = new ApplicationOptions();

    if (! options.parseCommandLine(args)) {
      return false;
    }

    if (options.driver == null) {
      options.driver = defaults.getDefaultJDBCDriver();
    }

    return true;
  }

  private void verifyConfiguration() {
    VerifyConfiguration       verifyConfiguration = VerifyConfiguration.getVerifyConfiguration();

    verifyConfiguration.verifyConfiguration(defaults.getSMTPServer(), defaults.getFailureHost(), defaults.getApplicationName());
  }

  /**
   * Connects to the database
   */
  private boolean connectToDatabase() {
    if (options.username != null) {
      try {
	DBContext.registerDriver(options.driver);
	context = new DBContext();
	context.setDefaultConnection(context.createConnection(options.database,
							      options.username,
							      options.password));
      } catch (Exception e) {
	System.err.println(e.getMessage());
	options.usage();
	context = null;
      }
    }

    if (context == null) {
      //      installLF(defaults.getKopiLFProperties());

      removeSplashScreen();
      context = login(options.database,
                      options.driver,
                      options.username,
                      options.password);
      displaySplashScreen();
    }

    if (context != null) {
      Query.setTraceLevel(options.trace);
    }

    return context != null;
  }

  /**
   *
   */
  protected void startApplication() {
    if (options.form != null) {
      String		form;

      if (options.form.indexOf(".") != -1) {
	form = options.form;
      } else {
	// form name without qualification: qualify with application package
	String	appli = getClass().getName();
	int	index = appli.lastIndexOf('.');

	form = appli.substring(0, index + 1) + options.form;
      }

      try {
        KopiExecutable  module;

	module = Module.startForm(context, form, "initial form");
        if (module instanceof VWindow) {
          ((VWindow) module).addModelCloseListener(new ModelCloseListener() {
              public void modelClosed(int type) {
                exitWithError(type);
              }
            });
        } else {
          exitWithError(1);
        }
      } catch (VException e) {
	e.printStackTrace();
        exitWithError(1);
      }
    } else {
      String url = getURL();
      
      menuTree = new MenuTree(context);
      menuTree.setTitle(getUserName() + "@" + url.substring(url.indexOf("//") + 2));
    }
    removeSplashScreen();
  }

  /**
   * Displays the splash screen.
   */
  private void displaySplashScreen() {
    ImageIcon		img = com.kopiright.vkopi.lib.util.Utils.getImage("splash.jpg");

    if (img != null) {
      splash = new SplashScreen(img.getImage(), null);
      splash.setVisible(true);
    }
  }

  /**
   * Removes the splash screen from the display.
   */
  private void removeSplashScreen() {
    if (splash != null) {
      splash.setVisible(false);
      splash.dispose();
      splash = null;
    }
  }

  /**
   * Exits on error
   *
   * @param     code    code of the error
   */
  private void exitWithError(int code) {
    removeSplashScreen();
    System.exit(code);
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
    ApplicationConfiguration    defaults = ApplicationConfiguration.getConfiguration();
    String                      smtpServer;
    String                      applicationName;
    String                      version;
    String                      logFile;
    boolean                     sendMail;
    boolean                     writeLog;

    if (defaults == null) {
      System.err.println("ERROR: No application configuration available");
      return;
    }

    try {
      applicationName = defaults.getApplicationName();
    } catch (PropertyException e) {
      applicationName = "application name not defined";
    }
    try {
      version = defaults.getVersion();
    } catch (PropertyException e) {
      version = "version not defined";
    }
    try {
      smtpServer = defaults.getSMTPServer();
    } catch (PropertyException e) {
      smtpServer = null;
    }
    try {
      logFile = defaults.getLogFile();
    } catch (PropertyException e) {
      logFile = null;
    }

    try {
      sendMail = defaults.mailErrors();
    } catch (PropertyException e) {
      sendMail = false;
    }
    try {
      writeLog = defaults.logErrors();
    } catch (PropertyException e) {
      writeLog = false;
    }

    if (smtpServer != null && sendMail) {
      String    recipient, cc, bcc; 

      try {
        recipient = defaults.getDebugMailRecipient();
      } catch (PropertyException e) {
        recipient = "fehler@kopiright.com";
      }
      try {
        cc = defaults.getStringFor("debugging.mail.cc");
      } catch (PropertyException e) {
        cc = null;
      }
      try {
        bcc = defaults.getStringFor("debugging.mail.bcc");
      } catch (PropertyException e) {
        bcc = null;
      }


      StringWriter	buffer = new StringWriter();
      PrintWriter       writer = new PrintWriter(buffer);
      // failureID is added to the subject of the mail.
      // similar error mail should have the smae id which makes the
      // easier to find duplicated messages.
      String            failureID; 

      writer.println("Application Name:    " + applicationName);
      writer.println("Version:             " + version);
      writer.println("Module:              " + module);
      writer.println("Started at:          " + startupTime);
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
        writer.println("Kopi-User:           "+getDBContext().getDefaultConnection().getUserName());
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
      writer.println("Information:         "+data);
      writer.flush();

      Mailer.sendMail(smtpServer,
                      recipient,
		      cc,       
		      bcc,      
                      "[KOPI ERROR] " +applicationName + failureID,
                      buffer.toString(),
                      "kopi@kopiright.com");
    }

    if (logFile != null && writeLog) {
      try {
        final PrintWriter       writer;

        writer = new PrintWriter(new FileWriter(logFile, true));
 	writer.println();
	writer.println();
        try {
          writer.println(getDBContext().getDefaultConnection().getUserName() + ":" + new Date());
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

  private static void writeNetworkInterfaces(PrintWriter writer) {
    try {
      // find out which ip-addresses this host has
      

      for (Enumeration netInterfaces = java.net.NetworkInterface.getNetworkInterfaces();
           netInterfaces.hasMoreElements();
           )
      {
        NetworkInterface        ni = (NetworkInterface) netInterfaces.nextElement();
        
        writer.println("Netzwerk:            " + ni.getDisplayName());
        for (Enumeration addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
          InetAddress           address = ((InetAddress) addresses.nextElement());

          writer.println("                     "
                         + address.getHostAddress() + " " 
                         + address.getCanonicalHostName());
        }
      }
    } catch (SocketException e) {
    }
  }

  // ---------------------------------------------------------------------
  // Implement a default message listener.
  // ---------------------------------------------------------------------

  /**
   * Displays a notice.
   */
  public void notice(String message) {
    // use model, because we are outside
    // swing event-dispatch-thread
    menuTree.getModel().notice(message);
  }

  /**
   * Displays an error message.
   */
  public void error(String message) {
    // use model, because we are outside
    // swing event-dispatch-thread
    menuTree.getModel().error(message);
  }

  /**
   * Displays a warning message.
   */
  public void warn(String message) {
    // use model, because we are outside
    // swing event-dispatch-thread
    menuTree.getModel().warn(message);
  }

  /**
   * Displays an ask dialog box
   */
  public int ask(String message, boolean yesIsDefault) {
    return AWR_UNDEF;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private static Application            instance;

  private ApplicationOptions            options;
  private ApplicationDefaults           defaults;
  private MenuTree                      menuTree;
  private DBContext                     context;
  private boolean                       isGeneratingHelp;
  private SplashScreen                  splash;
  private boolean                       isStarted;
  private Registry                      registry;

  // ---------------------------------------------------------------------
  // Failure cause informations
  // ---------------------------------------------------------------------

  private static final Date             startupTime; // remembers the startup time
  
  static {
    startupTime = new Date();
  }
}
