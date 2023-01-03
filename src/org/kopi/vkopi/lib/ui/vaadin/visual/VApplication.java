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

package org.kopi.vkopi.lib.ui.vaadin.visual;

import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.l10n.LocalizationManager;
import org.kopi.vkopi.lib.print.PrintManager;
import org.kopi.vkopi.lib.ui.vaadin.addons.AbstractNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.ConfirmNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.ErrorNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.InformationNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.MainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.MainWindowListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.NotificationListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.WarningNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.WelcomeView;
import org.kopi.vkopi.lib.ui.vaadin.addons.WelcomeViewEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.WelcomeViewListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.login.WelcomeViewState.FontMetricsResponse;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.FontMetrics;
import org.kopi.vkopi.lib.ui.vaadin.base.StylesInjector;
import org.kopi.vkopi.lib.visual.Application;
import org.kopi.vkopi.lib.visual.ApplicationConfiguration;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.FileHandler;
import org.kopi.vkopi.lib.visual.ImageHandler;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.MessageListener;
import org.kopi.vkopi.lib.visual.PrinterManager;
import org.kopi.vkopi.lib.visual.PropertyException;
import org.kopi.vkopi.lib.visual.Registry;
import org.kopi.vkopi.lib.visual.UIFactory;
import org.kopi.vkopi.lib.visual.VMenuTree;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.vkopi.lib.visual.VerifyConfiguration;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.vkopi.lib.visual.WindowController;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.Query;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.shared.ui.ui.UIState;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * The entry point for all KOPI WEB applications.
 */
@SuppressWarnings("serial")
@PreserveOnRefresh
@Push(value = PushMode.MANUAL, transport = Transport.WEBSOCKET)
public abstract class VApplication extends UI implements Application, WelcomeViewListener, MainWindowListener {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------

  /**
   * Creates a new <code>VApplication</code> instance.
   * @param registry The {@link Registry} object.
   */
  public VApplication(Registry registry) {
    this.registry = registry;
  }

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------

  @Override
  protected void init(VaadinRequest request) {
    checkAlternateLocale(); // needs to do this to ensure that we start with a valid locale;
    // registry and locale initialization
    initialize();
    gotoWelcomeView();
    // android and IOS specifics
    if (getPage().getWebBrowser().isAndroid() || getPage().getWebBrowser().isIOS()) {
      getPage().getJavaScript().execute("document.head.innerHTML += '<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no\">'");
      getPage().getJavaScript().execute("function downloadJSAtOnload() {"
		  			+"var element = document.createElement(\"script\");"
		  			+"element.src = \"/js/someScript.js\";"
		  			+"document.body.appendChild(element);"
		  			+"}"
		  			+"if (window.addEventListener)"
		  			+"window.addEventListener(\"load\", downloadJSAtOnload, false);"
		  			+"else if (window.attachEvent)"
		  			+"window.attachEvent(\"onload\", downloadJSAtOnload);"
	  	  			+"else window.onload = downloadJSAtOnload;");
      // really need to do this ?
      push();
    }
    stylesInjector = new StylesInjector();
    this.askAnswer = MessageListener.AWR_UNDEF;
  }

  // ---------------------------------------------------------------------
  // MESSAGE LISTENER IMPLEMENTATION
  // ---------------------------------------------------------------------

  @Override
  public void notice(String message) {
    final InformationNotification       dialog;
    final Object                        lock;

    lock = new Object();
    dialog = new InformationNotification(VlibProperties.getString("Notice"), message);
    dialog.addNotificationListener(new NotificationListener() {

      @Override
      public void onClose(boolean yes) {
        detachComponent(dialog);
        BackgroundThreadHandler.releaseLock(lock);
      }
    });
    showNotification(dialog, lock);
  }

  @Override
  public UIState getState() {
    return super.getState();
  }

  @Override
  public void error(String message) {
    final ErrorNotification     dialog;
    final Object                lock;

    lock = new Object();
    dialog = new ErrorNotification(VlibProperties.getString("Error"), message);
    dialog.setOwner(this);
    dialog.addNotificationListener(new NotificationListener() {

      @Override
      public void onClose(boolean yes) {
        setComponentError(null); // remove any further error.
        detachComponent(dialog);
        BackgroundThreadHandler.releaseLock(lock);
      }
    });
    showNotification(dialog, lock);
  }

  @Override
  public void warn(String message) {
    final WarningNotification   dialog;
    final Object                lock;

    lock = new Object();
    dialog = new WarningNotification(VlibProperties.getString("Warning"), message);
    dialog.addNotificationListener(new NotificationListener() {

      @Override
      public void onClose(boolean yes) {
        detachComponent(dialog);
        BackgroundThreadHandler.releaseLock(lock);
      }
    });
    showNotification(dialog, lock);
  }

  @Override
  public int ask(String message, boolean yesIsDefault) {
    final ConfirmNotification   dialog;
    final Object                lock;

    lock = new Object();
    dialog = new ConfirmNotification(VlibProperties.getString("Question"), message);
    dialog.setYesIsDefault(yesIsDefault);
    dialog.addNotificationListener(new NotificationListener() {

      @Override
      public void onClose(boolean yes) {
        if (yes) {
          askAnswer = MessageListener.AWR_YES;
        } else {
          askAnswer = MessageListener.AWR_NO;
        }
        detachComponent(dialog);
        BackgroundThreadHandler.releaseLock(lock);
      }
    });
    // attach the notification to the application.
    showNotification(dialog, lock);

    return askAnswer;
  }

  /**
   * Shows a notification.
   * @param notification The notification to be shown
   */
  protected void showNotification(final AbstractNotification notification, final Object lock) {
    if (notification == null) {
      return;
    }

    notification.setLocale(getDefaultLocale().toString());
    BackgroundThreadHandler.startAndWait(new Runnable() {

      @Override
      public void run() {
        attachComponent(notification);
        push();
      }
    }, lock);
  }

  //---------------------------------------------------------------------
  // APPLICATION IMPLEMENTATION
  // ---------------------------------------------------------------------

  @Override
  public void logout() {
    final ConfirmNotification           dialog;

    dialog = new ConfirmNotification(VlibProperties.getString("Question"), Message.getMessage("confirm_quit"));
    dialog.setYesIsDefault(false);
    dialog.addNotificationListener(new NotificationListener() {

      @Override
      public void onClose(boolean yes) {
        detachComponent(dialog);
        if (yes) {
          // close DB connection
          closeConnection();
          // show welcome screen
          gotoWelcomeView();
        }
        push();
      }
    });
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        attachComponent(dialog);
        push();
      }
    });
  }

  @Override
  public void startApplication() {
    String		url = getURL();

    menuTree = new VMenuTree(context);
    menuTree.setTitle(getUserName() + "@" + url.substring(url.indexOf("//") + 2));
    mainWindow = new MainWindow(getDefaultLocale(), getLogoImage(), getLogoHref());
    mainWindow.addMainWindowListener(this);
    mainWindow.setConnectedUser(getUserName());
    mainWindow.addMenu(new DMainMenu(menuTree));
    mainWindow.addMenu(new DUserMenu(menuTree));
    mainWindow.addMenu(new DAdminMenu(menuTree));
    mainWindow.addMenu(new DBookmarkMenu(menuTree));
    mainWindow.addDetachListener(new DetachListener() {
      public void detach(DetachEvent event) {
        closeConnection();
      }
    });
  }

  @Override
  public boolean allowQuit() {
    return getInitParameter("allowQuit") == null ? true : Boolean.parseBoolean(getInitParameter("allowQuit"));
  }

  @Override
  public PrintManager getPrintManager() {
    return printManager;
  }

  @Override
  public void setPrintManager(PrintManager printManager) {
    this.printManager = printManager;
  }

  @Override
  public PrinterManager getPrinterManager() {
    return printerManager;
  }

  @Override
  public void setPrinterManager(PrinterManager printerManager) {
    this.printerManager = printerManager;
  }

  @Override
  public ApplicationConfiguration getApplicationConfiguration() {
    return configuration;
  }

  @Override
  public void setApplicationConfiguration(ApplicationConfiguration configuration) {
    this.configuration = configuration;
  }

  // --------------------------------------------------
  // WELCOME VIEW LISTENER IMPLEMENTATION
  // --------------------------------------------------

  @Override
  public void onLogin(WelcomeViewEvent event) {
    // set font metrics width and height from client side
    handleFontMetricsResponse(event);
    // reset application locale before.
    setLocalizationContext(new Locale(event.getLocale().substring(0, 2), event.getLocale().substring(3, 5)));
    // now try to connect to database
    try {
      connectToDatabase(event.getUsername(), event.getPassword());
      startApplication(); // create main window and menu
      if (welcomeView != null) {
	AbstractSingleComponentContainer.removeFromParent(welcomeView);
	welcomeView = null;
	setContent(null);
      }
      setContent(mainWindow);
    } catch (SQLException e) {
      // sets the error if any problem occur.
      welcomeView.setError(e);
    } catch (VRuntimeException e) {
      // sets the error if any problem occur.
      welcomeView.setError(e);
    } finally {
      push();
    }
  }

  /**
   * handles the response for the font metrics request.
   * @param event The event holding the response.
   */
  protected void handleFontMetricsResponse(WelcomeViewEvent event) {
    for (FontMetricsResponse fmr : event.getFontMetrics()) {
      FontMetrics       fm;

      fm = getFontMetrics(fmr);
      if (fm != null) {
        fm.setWidth(fmr.width);
        fm.setHeight(fmr.height);
      }
    }
  }

  /**
   * Returns the font metrics for a given response.
   * @param fmr The font metrics client side response.
   * @return The font metrics object.
   */
  protected FontMetrics getFontMetrics(FontMetricsResponse fmr) {
    for (FontMetrics fm : FONT_METRICS) {
      if (fm.getFontFamily().equals(fmr.fontFamily)
          && fm.getFontSize() == fmr.fontSize
          && fm.getText().equals(fmr.text))
      {
        return fm;
      }
    }

    return null;
  }

  // --------------------------------------------------
  // PRIVATE MEMBERS
  // --------------------------------------------------

  /**
   * Tries to connect to the database using user name and password
   * provided by the login window.
   * @param username The login user name.
   * @param password The login password.
   * @throws SQLException When cannot connect to database.
   * @see #login(String, String, String, String, String)
   */
  private void connectToDatabase(String username, String password)
    throws SQLException
  {
    context = login(getInitParameter("database"),
	            getInitParameter("driver"),
	            username,
	            password,
	            getInitParameter("schema"));
    // check if context is created
    if (context == null) {
      throw new SQLException(MessageCode.getMessage("VIS-00054"));
    } else {
      // set query trace level
      setTraceLevel();
    }
  }

  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------

  @Override
  public boolean isNobugReport() {
    return Boolean.parseBoolean(getInitParameter("nobugreport"));
  }

  @Override
  public Date getStartupTime() {
    return startupTime;
  }

  @Override
  public VMenuTree getMenu() {
    return menuTree;
  }

  @Override
  public void setGeneratingHelp() {
    isGeneratingHelp = true;
  }

  @Override
  public boolean isGeneratingHelp() {
    return isGeneratingHelp;
  }

  @Override
  public DBContext getDBContext() {
    return context;
  }

  @Override
  public String getUserName() {
    return context.getConnection().getUserName();
  }

  @Override
  public Registry getRegistry() {
    return registry;
  }

  @Override
  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  @Override
  public LocalizationManager getLocalizationManager() {
    return localizationManager;
  }

  @Override
  public void displayError(UComponent parent, final String message) {
    error(message);
  }

  //---------------------------------------------------
  // UTILS
  // --------------------------------------------------

  /**
   * Attaches the given component to the application.
   * @param component The component to be attached.
   */
  public void attachComponent(Component component) {
    if (component != null && mainWindow != null) {
      mainWindow.addWindow(component);
    }
  }

  /**
   * Detaches the given component from the application.
   * @param component The component to be detached.
   */
  public void detachComponent(Component component) {
    if (component != null) {
      mainWindow.removeWindow(component);
    }
  }

  /**
   * Returns the database URL.
   * @return The database URL.
   */
  public String getURL() {
    return context.getConnection().getURL();
  }

  /**
   * This methods is called at the beginning
   * you should use it to define {@link Locale}, debugMode...
   */
  public void initialize() {
    if (registry != null) {
      registry.buildDependencies();
    }
    // set locale from initialization.
    setLocalizationContext(getInitializationLocale());
    if (getSupportedLocales() != null) {
      for (Locale locale : getSupportedLocales()) {
	setLocale(locale);
      }
    }
  }

  /**
   * Verifies the configuration settings.
   */
  public void verifyConfiguration() {
    VerifyConfiguration       verifyConfiguration = VerifyConfiguration.getVerifyConfiguration();

    try {
      verifyConfiguration.verifyConfiguration(ApplicationConfiguration.getConfiguration().getSMTPServer(),
	  			              ApplicationConfiguration.getConfiguration().getDebugMailRecipient(),
	  			              ApplicationConfiguration.getConfiguration().getApplicationName());
    } catch (PropertyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Attaches a window to this application.
   * @param window The window to be added.
   */
  public void addWindow(final Component window) {
    if (mainWindow != null && window != null) {
      BackgroundThreadHandler.access(new Runnable() {

        @Override
        public void run() {
          window.setSizeFull();
          mainWindow.addWindow(window);
        }
      });
    }
  }

  /**
   * Removes an attached window to this main window.
   * @param window The window to be removed.
   */
  public void removeWindow(final Component window) {
    if (mainWindow != null && window != null) {
      BackgroundThreadHandler.access(new Runnable() {

        @Override
        public void run() {
          mainWindow.removeWindow(window);
          push(); // force UI update
        }
      });
    }
  }

  /**
   * Checks the existence of the application locale.
   * @exception VRuntimeException When no alternate locale is defined.
   */
  protected void checkAlternateLocale() {
    if (getAlternateLocale() == null) {
      throw new VRuntimeException("An alternate locale should be provided with the application");
    }
  }

  /**
   * Sets the localization context.
   * <p>
   * This aims to set the application {@link #defaultLocale}
   * and {@link #localizationManager} internal attributes.
   * </p>
   */
  protected void setLocalizationContext(Locale locale) {
    // default application locale is initialized
    // from application descriptor file (web.xml)
    defaultLocale = locale;
    if (defaultLocale == null) {
      // if no valid local is defined in the application descriptor
      // pick the locale from the extra locale given with application
      // specifics.
      // This is only to be share that we start with a language.
      defaultLocale = getAlternateLocale();
    }
    // Now create the localization manager using the application default locale.
    localizationManager = new LocalizationManager(defaultLocale, Locale.getDefault());
  }

  /**
   * Returns the initialization locale found in the application descriptor file.
   * @return the initialization locale found in the application descriptor file.
   */
  protected Locale getInitializationLocale() {
    String			locale;

    locale = getInitParameter("locale"); // obtain application locale from descriptor file
    if (locale == null) {
      return getAlternateLocale();
    }
    // check the locale format
    if (!checkLocale(locale)) {
      System.err.println("Error: Wrong locale format. Alternate locale will be used");
      return getAlternateLocale();
    } else {
      return new Locale(locale.substring(0, 2), locale.substring(3, 5));
    }
  }

  /**
   * Sets the query trace level.
   */
  protected void setTraceLevel() {
    String              trace;

    trace = getInitParameter("trace");
    if (trace != null) {
      try {
        int           level;
        level = Integer.parseInt(getInitParameter("trace"));
        Query.setTraceLevel(level);
      } catch (NumberFormatException e) {
        System.err.println("Warning: cannot set query trace level");
      }
    }
  }

  /**
   * Closes the database connection
   */
  protected void closeConnection() {
    try {
      if (context != null) {
	context.close();
	context = null;
      }
    } catch (SQLException e) {
      // we don't care, we reinitialize the connection
      context = null;
    }
  }

  /**
   * Returns the client side calculated font metrics for a given font.
   * @param fontFamily The font family.
   * @param fontSize The font size.
   * @param text The text.
   * @return The text width in the given font.
   */
  public int getWidth(String fontFamily, int fontSize, String text) {
    for (FontMetrics fm : FONT_METRICS) {
      if (fm.getFontFamily().equals(fontFamily)
          && fm.getFontSize() == fontSize
          && fm.getText().equals(text))
      {
        return fm.getWidth();
      }
    }

    return 0;
  }

  /**
   * Shows the welcome view.
   */
  protected void gotoWelcomeView() {
    if (mainWindow != null) {
      // it should be attached to the application.
      AbstractSingleComponentContainer.removeFromParent(mainWindow);
      mainWindow = null;
      menuTree = null;
      localizationManager = null;
      isGeneratingHelp = false;
      setContent(null);
    }
    // creates the welcome screen
    welcomeView = new WelcomeView(getDefaultLocale(), getSupportedLocales(), getSologanImage(), getLogoImage(), getLogoHref());
    for (FontMetrics fm : FONT_METRICS) {
      welcomeView.addFontMetricsRequest(fm.getFontFamily(), fm.getFontSize(), fm.getText());
    }
    welcomeView.setSizeFull(); // important to get the full screen size.
    welcomeView.addWelcomeViewListener(this);
    setContent(welcomeView);
  }

  /**
   * Checks the given locale format.
   * @param locale The locale to be checked.
   * @return {@code true} if the locale has a valid format.
   */
  private boolean checkLocale(String locale) {
    char[]    		chars;

    chars = locale.toCharArray();
    if (chars.length != 5
        || chars[0] < 'a' || chars[0] > 'z'
        || chars[1] < 'a' || chars[1] > 'z'
        || chars[2] != '_'
        || chars[3] < 'A' || chars[3] > 'Z'
        || chars[4] < 'A' || chars[4] > 'Z')
    {
      return false;
    }

    return true;
  }

  //---------------------------------------------------
  // MAIN WINDOW LISTENER IMPLEMENTATION
  // --------------------------------------------------

  @Override
  public void onAdmin() {
    // TODO
  }

  @Override
  public void onSupport() {
    // TODO
  }

  @Override
  public void onHelp() {
    // TODO
  }

  @Override
  public void onLogout() {
    // close database connection and show welcome view
    logout();
  }

  @Override
  public void onUser() {
    // TODO
  }

  @Override
  public String getUserIP() {
    try {
      getSession().lock();
      return getPage().getWebBrowser().getAddress();
    } finally {
      getSession().unlock();
    }
  }

  //---------------------------------------------------
  // UTILS
  // --------------------------------------------------

  /**
   * Returns the initialization parameter of the given key.
   * The initialization parameter is contained in the application
   * descriptor (Web.xml) file.
   * @param key The parameter key.
   * @return The initialization parameter contained in the application descriptor file.
   */
  protected String getInitParameter(String key) {
    return VaadinServlet.getCurrent().getInitParameter(key);
  }

  /**
   * Returns the styles injector attached with this application instance.
   * @return The styles injector attached with this application instance.
   */
  public StylesInjector getStylesInjector() {
    return stylesInjector;
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  // --------------------------------------------------

  /**
   * Returns the supported locales that can be used with this application.
   * <pre>
   *  The map will contain the displayed language as key value. Its corresponding
   *  value is the locale ISO code.
   * </pre>
   * @return The supported locales that can be used with this application.
   */
  protected abstract Locale[] getSupportedLocales();

  /**
   * Returns the SLOGAN image to be used in welcome screen.
   * @return The SLOGAN image to be used in welcome screen.
   */
  protected abstract Resource getSologanImage();

  /**
   * Returns the LOGO image to be used with application.
   * @return The LOGO image to be used with application.
   */
  protected abstract Resource getLogoImage();

  /**
   * Returns the LOGO link to be associated with the application LOGO image.
   * @return The LOGO link to be associated with the application LOGO image.
   * @see {{@link #getLogoImage()}
   */
  protected abstract String getLogoHref();

  /**
   * Returns the alternate locale to be used as default locale
   * when no default locale is specified. This will force the application
   * to use the given locale to avoid localization problems.
   * <b> This language should not be {@code null}. Otherwise, the application won't start</b>
   * @return The alternate locale to be used when no default locale is specified.
   */
  protected abstract Locale getAlternateLocale();

  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------

  private VMenuTree                   	        menuTree;
  private DBContext                   	        context;
  private Registry 		       	        registry;
  private boolean		       	        isGeneratingHelp;
  private Locale                                defaultLocale;
  private LocalizationManager                   localizationManager;
  private WelcomeView				welcomeView;
  private MainWindow				mainWindow;
  private int                                   askAnswer;
  private PrintManager                          printManager;
  private PrinterManager                        printerManager;
  private ApplicationConfiguration              configuration;
  private StylesInjector                        stylesInjector;
  private static final FontMetrics[]            FONT_METRICS = new FontMetrics[] {
    FontMetrics.DIGIT,
    FontMetrics.LETTER
  };

  // ---------------------------------------------------------------------
  // Failure cause informations
  // ---------------------------------------------------------------------

  private final Date             		 startupTime = new Date(); // remembers the startup time

  static {
    ApplicationContext.setApplicationContext(new VApplicationContext());
    FileHandler.setFileHandler(new VFileHandler());
    ImageHandler.setImageHandler(new VImageHandler());
    WindowController.setWindowController(new VWindowController());
    UIFactory.setUIFactory(new VUIFactory());
  }
}
