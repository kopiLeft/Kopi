/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import java.util.Date;
import java.util.Locale;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.ui.vaadin.form.DForm;
import com.kopiright.vkopi.lib.ui.vaadin.report.DReport;
import com.kopiright.vkopi.lib.visual.Application;
import com.kopiright.vkopi.lib.visual.ApplicationConfiguration;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.FileHandler;
import com.kopiright.vkopi.lib.visual.ImageHandler;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.PropertyException;
import com.kopiright.vkopi.lib.visual.Registry;
import com.kopiright.vkopi.lib.visual.UIFactory;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VMenuTree;
import com.kopiright.vkopi.lib.visual.VerifyConfiguration;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.vkopi.lib.visual.WindowController;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBException;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.steinwedel.messagebox.MessageBox;

@SuppressWarnings("serial")
@PreserveOnRefresh
public abstract class VApplication extends UI implements Application {
 
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------

  /**
   * Creates a new <code>VApplication</code> instance.
   * @param registry The {@link Registry} object.
   */
  public VApplication(Registry registry){
    this.registry = registry;
  }
  
  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  
  //!!! FIXME : Review all this. Too long method
  @SuppressWarnings("deprecation")
  @Override
  protected void init(VaadinRequest request) {
    initLocalisation();   
		
    parentLayout = new VerticalLayout();
    parentLayout.setWidth("100%");
    parentLayout.addLayoutClickListener(new LayoutClickListener() {
      
      @Override
      public void layoutClick(LayoutClickEvent event) {
        notificationPanel.hide();
      }
    });
    
    setContent(parentLayout);
    
    if(headerIcon == null){
      headerIcon = new Image(null, Utils.getImage("logo_kopi.png").getResource());
    }
    
    headerLayout = new GridLayout(2,1);
    headerLayout.addStyleName(KopiTheme.HEADER_LAYOUT_STYLE);
    headerLayout.setMargin(false);
    headerLayout.setWidth("100%");
    
    headerLayout.addComponent(headerIcon, 0, 0); 
    headerLayout.setComponentAlignment(headerIcon, Alignment.MIDDLE_LEFT); 

    VerticalLayout loginPanelLayout = new VerticalLayout();
    
    final Label welcomeMessage = new Label(VlibProperties.getString("welcomeMessage")); 
    welcomeMessage.setImmediate(true);
    welcomeMessage.addStyleName(KopiTheme.WELCOM_MESAGE_STYLE);
    
    final Label loginInstructionMsg = new Label(VlibProperties.getString("loginInstructionMsg"));
    loginInstructionMsg.setImmediate(true);
    
    loginPanel = new Panel();
    loginPanel.setStyleName(KopiTheme.LOGIN_PANEL_STYLE);
    
    if(login_panel_image == null){
    login_panel_image = new Image(null, Utils.getImage("slogan.png").getResource());
    }
    login_panel_image.addStyleName(KopiTheme.LOGIN_PANEL_IMAGE);
    loginPanel.setHeight("10cm");
    loginPanel.setWidth("20.9cm");	
      
    loginPanelLayout.addComponent(welcomeMessage);
    loginPanelLayout.addComponent(login_panel_image); 
    
    loginPanelLayout.setComponentAlignment(welcomeMessage, Alignment.MIDDLE_LEFT);
    loginPanelLayout.setComponentAlignment(login_panel_image, Alignment.MIDDLE_LEFT);
		 
    username = new TextField();
    username.setImmediate(true);
    
    password = new PasswordField();
    password.setImmediate(true);
    
    TextChangeListener textChangeHandler = new TextChangeListener() {

      public void textChange(TextChangeEvent event) {
	notificationPanel.hide();      
      }
    };
    
    username.addListener(textChangeHandler);
    username.addShortcutListener(new ShortcutListener("", KeyCode.ENTER, null) {
      
      @Override
      public void handleAction(Object sender, Object target) {
	connect();
      }
    });
    password.addListener(textChangeHandler); 
    password.addShortcutListener(new ShortcutListener("", KeyCode.ENTER, null) {
      
      @Override
      public void handleAction(Object sender, Object target) {
	connect();
      }
    });
    
    final Label userNameLabel = new Label(VlibProperties.getString("userName"));
    userNameLabel.setStyleName(KopiTheme.LOGIN_LABEL_STYLE);
    userNameLabel.setImmediate(true);
    final Label passwordLabel = new Label(VlibProperties.getString("password"));  
    passwordLabel.setStyleName(KopiTheme.LOGIN_LABEL_STYLE);
    passwordLabel.setImmediate(true);
    
    final Button connexionButton = new Button(VlibProperties.getString("login"));
    connexionButton.setImmediate(true);
    connexionButton.addClickListener(new UserConnexionListener());
    
    GridLayout loginParamsGrid=new GridLayout(3,5);
    loginParamsGrid.setSpacing(true);
    loginParamsGrid.setSizeUndefined();
    
    loginParamsGrid.addComponent(userNameLabel, 0, 0);
    loginParamsGrid.addComponent(username, 1, 0);
    loginParamsGrid.addComponent(passwordLabel, 0, 1);
    loginParamsGrid.addComponent(password, 1, 1);
    loginParamsGrid.addComponent(connexionButton, 1, 3);
    
    loginParamsGrid.setComponentAlignment(userNameLabel, Alignment.TOP_RIGHT);
    loginParamsGrid.setComponentAlignment(passwordLabel, Alignment.TOP_RIGHT);
    
    
    
    GridLayout loginGrid=new GridLayout(1,2);
    loginGrid.setSpacing(true);
    loginGrid.setWidth("9cm");
    
    loginGrid.addComponent(loginInstructionMsg, 0, 0);
    loginGrid.addComponent(loginParamsGrid, 0, 1);   
		
    loginPanelLayout.addComponent(loginGrid);
    loginPanelLayout.setComponentAlignment(loginGrid, Alignment.MIDDLE_CENTER);
    
    CssLayout loginLayout = new CssLayout();
    loginLayout.addStyleName(KopiTheme.LOGIN_PANEL_LAYOUT_STYLE);
    loginLayout.addComponent(loginPanelLayout);
    
    loginPanel.setContent(loginLayout);
    
    VerticalLayout bodyLayout = new VerticalLayout();
    bodyLayout.setWidth("100%");
    
    bodyTopLayout = new VerticalLayout();
    bodyTopLayout.setWidth("100%");
    bodyTopLayout.addStyleName(KopiTheme.BODY_TOP_LAYOUT_STYLE);
    
    bodyLayout.addComponent(bodyTopLayout);
    bodyLayout.addComponent(getTabsheet());
    
    
    bodyLayout.setComponentAlignment(bodyTopLayout, Alignment.TOP_CENTER);
    bodyLayout.setComponentAlignment(mainTabsheet, Alignment.MIDDLE_CENTER);
    mainTabsheet.setVisible(false);
    
    bodyTopLayout.addComponent(loginPanel);
    bodyTopLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
    
    ribbon = new CssLayout();
    ribbon.setWidth("100%");
    ribbon.setHeight(2, Unit.PIXELS);
    ribbon.addStyleName(KopiTheme.RIBBON_STYLE);
    
    
    parentLayout.addComponent(headerLayout);
    parentLayout.addComponent(ribbon);
    parentLayout.addComponent(bodyLayout);  
   
    final Label welcomLabel = new Label(VlibProperties.getString("welcomeMessage")); 
    welcomLabel.addStyleName(KopiTheme.WELCOM_LABEL_STYLE);
    welcomLabel.setImmediate(true);
    
    userLink = new Link(VlibProperties.getString("admin"), null);
    userLink.addStyleName(KopiTheme.USER_LINK);
    userLink.setImmediate(true);
    userLink.setDescription(VlibProperties.getString("user"));
	    
    Label labelOpenBrackets = new Label("["); 
	    
    final Button    logoutLink = new Button(VlibProperties.getString("logout"));
    logoutLink.setImmediate(true);
    logoutLink.addClickListener(new UserLogoutListener());
    logoutLink.setDescription(VlibProperties.getString("logout"));
    logoutLink.addStyleName(KopiTheme.BUTTON_LINK_STYLE);
    logoutLink.addStyleName(KopiTheme.LOGOUT_BUTTON_STYLE);
        
    final Link    helpLink = new Link(VlibProperties.getString("help"), null);
    helpLink.addStyleName(KopiTheme.HELP_LINK);
    helpLink.setImmediate(true);
    helpLink.setDescription(VlibProperties.getString("help"));
	    
    Label labelCloseBrackets = new Label("]"); 
       	
    Separator link_separator = new Separator();
	    
    headerRightLayout = new GridLayout(13,1);
    headerRightLayout.addComponent(welcomLabel);
    headerRightLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
    headerRightLayout.addComponent(userLink);
    headerRightLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
    headerRightLayout.addComponent(labelOpenBrackets);
    headerRightLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
    headerRightLayout.addComponent(logoutLink);	
    headerRightLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
    headerRightLayout.addComponent(labelCloseBrackets);
    headerRightLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));  
    headerRightLayout.addComponent(link_separator);
    headerRightLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
    headerRightLayout.addComponent(helpLink);
    headerRightLayout.setHeight("100%"); 
    
    final Label languageLabel = new Label(VlibProperties.getString("language"));
    languageLabel.setImmediate(true);
    languageLabel.setStyleName(KopiTheme.LOGIN_LABEL_STYLE);
	
    //languages = new String[] {"Deutsch (Österreich)", "English (Great Britain)", "Francais (France)", "العربية" };
    languages = new String[] {"Deutsch (Österreich)", "Francais (France)", "العربية" };

    NativeSelect languageSwitcher = new NativeSelect();
    languageSwitcher.setImmediate(true);
    languageSwitcher.addShortcutListener(new ShortcutListener("", KeyCode.ENTER, null) {
      
      @Override
      public void handleAction(Object sender, Object target) {
	connect();
      }
    });
    
    for (int i = 0; i < languages.length; i++) {
      languageSwitcher.addItem(languages[i]);
    }
    
    Object currentLanguage = getCurrentLanguage();
    if( currentLanguage != null){
      languageSwitcher.setValue(currentLanguage);
    }
    
    languageSwitcher.addValueChangeListener(new ValueChangeListener() {
		
      @Override
      public void valueChange(ValueChangeEvent event) {
	notificationPanel.hide(); 
	Object	selectedLanguage = event.getProperty().getValue();
	
	if(selectedLanguage != null){
	  if (selectedLanguage.equals(languages[0])){
	    Locale.setDefault(new Locale("de", "AT"));
	  } else if(selectedLanguage.equals(languages[1])){
	    Locale.setDefault(new Locale("fr", "FR"));	
	  } else if(selectedLanguage.equals(languages[2])){
	    Locale.setDefault(new Locale("ar", "TN"));	
	  }
	  initLocalisation();
	  welcomeMessage.setValue(VlibProperties.getString("welcomeMessage"));
	  loginInstructionMsg.setValue(VlibProperties.getString("loginInstructionMsg"));
	  userNameLabel.setValue(VlibProperties.getString("userName"));
	  passwordLabel.setValue(VlibProperties.getString("password")); 
	  connexionButton.setCaption(VlibProperties.getString("login"));
	  welcomLabel.setValue(VlibProperties.getString("welcomeMessage"));
	  userLink.setCaption(VlibProperties.getString("admin"));
	  logoutLink.setCaption(VlibProperties.getString("logout"));
	  helpLink.setCaption(VlibProperties.getString("help"));
	  languageLabel.setValue(VlibProperties.getString("language"));
	  mainTabsheet.getTab(0).setCaption(VlibProperties.getString("home"));
	}
      }
    });
  
    loginParamsGrid.addComponent(languageLabel, 0, 2);
    loginParamsGrid.addComponent(languageSwitcher, 1, 2);
    
    loginParamsGrid.setComponentAlignment(languageLabel, Alignment.TOP_RIGHT);
    
    notificationPanel = new NotificationPanel("", "");
    
    parentLayout.addComponent(notificationPanel);
    parentLayout.setComponentAlignment(notificationPanel, Alignment.TOP_LEFT);

    // do customer-sepcific initialisations
    initialize();
    username.focus();
    
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
      UI.getCurrent().access(new Runnable() {
      
        @Override
        public void run() {
          UI.getCurrent().push();
        }
      }); 
    }
  }
  
  // ---------------------------------------------------------------------
  // MESSAGE LISTENER IMPLEMENTATION
  // ---------------------------------------------------------------------
  
  @Override
  public void notice(String message) {
    if (menuTree != null) {
      menuTree.notice(message);
    }
  }

  @Override
  public void error(String message) {
    if (menuTree != null) {
      menuTree.error(message);
    }
  }

  @Override
  public void warn(String message) {
    if (menuTree != null) {
      menuTree.warn(message);
    }
  }

  @Override
  public int ask(String message, boolean yesIsDefault) {
    return AWR_UNDEF;
  }

  //---------------------------------------------------------------------
  // APPLICATION IMPLEMENTATION
  // ---------------------------------------------------------------------
  
  @Override
  public void startApplication() {
    try {
      String	url = getURL();    
      menuTree = new VMenuTree(context);
      menuTree.setTitle(getUserName() + "@" + url.substring(url.indexOf("//") + 2));
      menuTree.doNotModal();
    } catch (VException e) {
      e.printStackTrace();
      getNotificationPanel().displayNotification(VlibProperties.getString("Error"),e.getMessage());
    } catch (InconsistencyException ie) {
      getNotificationPanel().displayNotification(VlibProperties.getString("Error"),ie.getMessage());
    }
  }
  
  @Override
  public boolean allowQuit() {
    return true;
  }
  
  /**
   * Connects to the database
   */
  private boolean connectToDatabase() {
    try {		  
      DBContext.registerDriver(VaadinServlet.getCurrent().getInitParameter("driver"));
      context = new DBContext();
      context.setDefaultConnection(context.createConnection(VaadinServlet.getCurrent().getInitParameter("database"),
	                                                    VaadinServlet.getCurrent().getInitParameter("username"),
	                                                    VaadinServlet.getCurrent().getInitParameter("password"),
	                                                    true,
	                                                    VaadinServlet.getCurrent().getInitParameter("schema")));
    } catch (Exception e) {
      error(MessageCode.getMessage("VIS-00054"));
      context = null;
    }

    if (context == null) {
      context = login(VaadinServlet.getCurrent().getInitParameter("database"),
	              VaadinServlet.getCurrent().getInitParameter("driver"),
	              username.getValue(),
	              password.getValue(),
	              VaadinServlet.getCurrent().getInitParameter("schema"));
    }
    
    return context != null;
  }
  
  @Override
  public DBContext login(String database,
                         String driver,
                         String username,
                         String password,
                         String schema)
  {
    return null;
  }
  
  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------
  
  @Override
  public boolean isNobugReport() {
    return Boolean.parseBoolean(VaadinServlet.getCurrent().getInitParameter("nobugreport"));
  }

  @Override
  public Date getStartupTime() {
    return startupTime;
  }

  @Override
  public VMenuTree getMenu() {
    return new VMenuTree(context);
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
    return context.getDefaultConnection().getUserName();
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
  public void displayError(UComponent parent, String message) {
    getNotificationPanel().displayNotification("", message);
    UI.getCurrent().access(new Runnable() {
      
      @Override
      public void run() {
	mainTabsheet.setSelectedTab(0);
      }
    });
  }
  
  /**
   * Returns the {@link NotificationPanel} object.
   * @return The {@link NotificationPanel} object.
   */
  public NotificationPanel getNotificationPanel() {
    return notificationPanel;
  }
  
  //---------------------------------------------------
  // UTILS
  // --------------------------------------------------

  /**
   * Returns the database URL.
   * @return The database URL.
   */
  public String getURL() {
    return context.getDefaultConnection().getURL();
  }

  /**
   * This methods is called at the beginning
   * you should use it to define {@link Locale}, debugMode...
   */
  public void initialize() {
    if (registry != null) {
      registry.buildDependencies();
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
   * Sets the menu tree view.
   * @param view The menu tree view.
   */
  public void setMenuTree(DMenuTree view) {
    headerLayout.addComponent(headerRightLayout, 1, 0);
    headerLayout.setComponentAlignment(headerRightLayout, Alignment.TOP_RIGHT);
    dMenuTree = view;
    dMenuTree.setWidth("100%");
    ribbon.setVisible(false);
    username.setValue("");
    password.setValue("");
    bodyTopLayout.removeComponent(loginPanel);
    bodyTopLayout.addComponent(dMenuTree);    
    bodyTopLayout.setComponentAlignment(dMenuTree, Alignment.TOP_CENTER);
    if(dMenuTree.getModel().getUserName() != null){
      userLink.setCaption(menuTree.getUserName()); 
    }
    mainTabsheet.setVisible(true);
  }
  
  /**
   * Returns the application tab sheet.
   * @return The application tab sheet.
   */
  public TabSheet getTabsheet() {
    if (mainTabsheet == null) {
      mainTabsheet = new TabSheet();
      mainTabsheet.setWidth("100%");
      mainTabsheet.addStyleName(KopiTheme.MAIN_TABSHEET_STYLE);
      mainTabsheet.setImmediate(true);
      mainTabsheet.setVisible(true);
      mainTabsheet.addComponentDetachListener(new ComponentDetachListener() {
        
        @Override
        public void componentDetachedFromContainer(final ComponentDetachEvent event) {
          BackgroundThreadHandler.start(new Runnable() {
            
            @Override
            public void run() { 
              if (mainTabsheet.getComponentCount() > 1) {
        	if ((((VerticalLayout)event.getDetachedComponent()).getComponent(3)  instanceof DReport) 
        	    && (((DReport)((VerticalLayout)event.getDetachedComponent()).getComponent(3)).getParentTab() != null)) {
        	  mainTabsheet.setSelectedTab(((DReport)((VerticalLayout)event.getDetachedComponent()).getComponent(3)).getParentTab());
        	} else {
        	  mainTabsheet.setSelectedTab(1);
        	}         
              } 
            }
          });
        }
      });
      mainTabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener() {
        
        @Override
        public void selectedTabChange(SelectedTabChangeEvent event) {
          if (((VerticalLayout)event.getTabSheet().getSelectedTab()).getComponent(0)  instanceof BreadCrumb){
            ((DForm)((VerticalLayout)event.getTabSheet().getSelectedTab()).getComponent(3)).focus();
          }
        }
      });      
       
      mainTabsheet.addTab(new HomeForm(),VlibProperties.getString("home")).setClosable(false);
    }

    return mainTabsheet;
  }
  
  /**
   * Set login panel slogan
   * @param loginImage The login image.
   */
  public void setLoginImage(Image loginImage) {	
    if (loginImage != null){
      this.login_panel_image = loginImage ;
    }
  }
  
  /**
   * Sets the header icon.
   * @param headerIcon The header icon.
   */
  public void setHeaderIcon(Image headerIcon) {	
    if(headerIcon != null){
      this.headerIcon = headerIcon ;
    }
  }
  
  /**
   * Displays the login window.
   */
  public void showLoginWindow() {
    bodyTopLayout.removeComponent(dMenuTree);
    ribbon.setVisible(true);
    bodyTopLayout.addComponent(loginPanel);
    headerLayout.removeComponent(headerRightLayout);
    bodyTopLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
    mainTabsheet.removeAllComponents();
    mainTabsheet.addTab(new HomeForm(),VlibProperties.getString("home")).setClosable(false);
    mainTabsheet.setVisible(false);
    username.focus();
  }
  
  /**
   * Localization initialization.
   */
  public void initLocalisation() {
    if (VaadinServlet.getCurrent().getInitParameter("locale") == null) {
      System.err.println("Warning: a default locale was not specified!");
      defaultLocale = null;
    } else {
      char[]    chars = VaadinServlet.getCurrent().getInitParameter("locale").toCharArray();

      if(chars.length != 5
	  || chars[0] < 'a' || chars[0] > 'z'
          || chars[1] < 'a' || chars[1] > 'z'
          || chars[2] != '_'
          || chars[3] < 'A' || chars[3] > 'Z'
          || chars[4] < 'A' || chars[4] > 'Z'
	  )
      {
	System.err.println("Error: Wrong locale format.");
      } else {
	defaultLocale = new Locale(VaadinServlet.getCurrent().getInitParameter("locale").substring(0,2),
	    			   VaadinServlet.getCurrent().getInitParameter("locale").substring(3,5));
      }
    }
    
    localizationManager = new LocalizationManager(Locale.getDefault(), defaultLocale);
  }
  
  /**
   * Returns the current used language.
   * @return the current used language.
   */
  public Object getCurrentLanguage() {
    Object result = null;
    if (Locale.getDefault().getLanguage().equals("de")){
      result = languages[0];
    } else if (Locale.getDefault().getLanguage().equals("fr")){
      result = languages[1];
    } else if (Locale.getDefault().getLanguage().equals("ar")){
      result = languages[2];
    }
    
    return result;
  }
  
  // ---------------------------------------------------------------------
  // SEPARATOR COMPONENT
  // ---------------------------------------------------------------------
  
  //!!! hacheni 20140623: this class exists in horizontal panel.
  // please define it once.
  public class Separator extends Label {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>Separator</code> instance.
     */
    public Separator() {
      setIcon(Utils.getImage("separator.png").getResource());
      addStyleName(KopiTheme.SEPARATOR_STYLE);
    }
  }

  //---------------------------------------------------------------------
  // CONNEXION BUTTON CLICK LISTENER 
  // --------------------------------------------------------------------
 
  /**
   * The <code>UserConnexionListener</code> is the application
   * implementation of the {@link ClickListener}
   */
  public class UserConnexionListener implements ClickListener {

    @Override
    public void buttonClick(ClickEvent event) {
      connect();
    }
  }
  
  /**
   * The <code>UserLogoutListener</code> is the application
   * implementation of the {@link ClickListener}
   */
  public class UserLogoutListener implements ClickListener {

    @Override
    public void buttonClick(final ClickEvent event) {     
      try {
	context.close();
      } catch (DBException e) {
	e.printStackTrace();
      }
         
      showLoginWindow();
    }
  }
  
  /**
   * Get the current UI theme Id.
   * @return The current UI theme Id.
   */
  public int getThemeId() {
    //To be implemented in child class
    return 0;
  }
  
  /**
   * Set the current UI theme Id
   * @param themeId The Theme ID.
   */
  public void setThemeId(int themeId) {
    //To be implemented in child class
  }
  
  /**
   * Get the number of UI themes.
   * @return the number of UI themes.
   */
  public int getThemeCount() {
    //To be implemented in child class
    return 0;
  }
  
  /**
   * Get the list of UI themes.
   * @return the list of UI themes.
   */
  public String[] getThemeList() {
    //To be implemented in child class
    return null;
  }
  
  /**
   * Creates the theme selector.
   * @return The theme selector.
   */
  public NativeSelect createThemeSwitcher() {
    themeSwitcher = new NativeSelect(VlibProperties.getString("available-themes"));
    themeSwitcher.setImmediate(true);
    
    for (int i = 0; i < getThemeCount(); i++) {
      themeSwitcher.addItem(getThemeList()[i]);
    }

    themeSwitcher.setValue(getTheme());
    
    return themeSwitcher;
  }
  
  /**
   * Connect to the application.
   */
  public void connect() {
    if ((!username.getValue().trim().isEmpty()) && (!password.getValue().trim().isEmpty())){
      if (! connectToDatabase()) {          
        notificationPanel.displayNotification("",VlibProperties.getString("loginErrorMsg"));
      } else {
  	startApplication();
      }
      
    } else {
      notificationPanel.displayNotification("",VlibProperties.getString("loginErrorMsg"));	
    }
  }
  
  //--------------------------------------------------
  // DATA MEMBEERS
  //--------------------------------------------------
  private DMenuTree                   		dMenuTree;	
  private VMenuTree                   	        menuTree;	
  private DBContext                   	        context;
  private Registry 		       	        registry;
  private boolean		       	        isGeneratingHelp;
  private Locale                                defaultLocale;
  private LocalizationManager                   localizationManager;
  private TextField 		                username;
  private PasswordField 		        password;
  private VerticalLayout 		        bodyTopLayout;
  private VerticalLayout 		        parentLayout;
  private GridLayout 		                headerLayout;
  private GridLayout 		                headerRightLayout;
  private CssLayout 		                ribbon;
  private Panel 			        loginPanel;
  private Image                                 login_panel_image;
  private Image                                 headerIcon;
  private TabSheet                              mainTabsheet;
  private String[]                              languages;
  private MessageBox                            messageBox;
  private NativeSelect                          themeSwitcher;
  private NotificationPanel                     notificationPanel;
  private Link	 	                        userLink;
  
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
