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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.login;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VHiddenSeparator;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputLabel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputPassword;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputText;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ui.SimpleFocusablePanel;

/**
 * The login box widget.
 */
@SuppressWarnings("deprecation")
public class VLoginBox extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the login box widget.
   */
  public VLoginBox() {
    setStyleName(Styles.LOGIN_BOX);
    table = new Table();
    add(table);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the welcome text.
   * @param text The welcome text.
   */
  public void setWelcomeText(String text) {
    table.setWelcomeText(text);
  }
  
  /**
   * Sets the welcome image.
   * @param uri The welcome image URI.
   */
  public void setWelcomeImage(String uri) {
    table.setWelcomeImage(uri);
  }
  
  /**
   * Sets the error.
   * @param error The error message.
   */
  public void setError(String error) {
    table.setError(error);
  }
  
  /**
   * Removes an error
   */
  public void removeError() {
    table.removeError();
  }
  
  /**
   * Sets the information text.
   * @param text The information text.
   */
  public void setInformationText(String text) {
    table.setInformationText(text);
  }
  
  /**
   * Sets the user name label.
   * @param label the user name label.
   */
  public void setUsernameLabel(String label) {
    table.setUsernameLabel(label);
  }
  
  /**
   * Returns the user name value.
   * @return the user name value.
   */
  public String getUsername() {
    return table.getUsername();
  }
  
  /**
   * Sets the password label.
   * @param label The password label.
   */
  public void setPasswordLabel(String label) {
    table.setPasswordLabel(label);
  }
  
  /**
   * Returns the password value.
   * @return The password value.
   */
  public String getPassword() {
    return table.getPassword();
  }
  
  /**
   * Sets the language label.
   * @param label the language label.
   */
  public void setLanguageLabel(String label) {
    table.setLanguageLabel(label);
  }
  
  /**
   * Appends a supported language.
   * @param language The language display name.
   * @param isocode The iso code of the language and the name to be returned for the selection.
   */
  public void addLanguage(String language, String isocode) {
    table.addLanguage(language, isocode);
  }
  
  /**
   * Returns the language selected value.
   * @return The selected value.
   */
  public String getSelectedLanguage() {
    return table.getSelectedLanguage();
  }
  
  /**
   * Sets the selected language.
   * @param language The language index.
   */
  public void setSelectedLanguage(int language) {
    table.setSelectedLanguage(language);
  }
  
  /**
   * Sets the login displayed text.
   * @param text The login text.
   */
  public void setLoginText(String text) {
    table.setLoginText(text);
  }

  /**
   * Adds a language change handler.
   * @param handler The change handler.
   */
  public void addChangeHandler(ChangeHandler handler) {
    table.addChangeHandler(handler);
  }
  
  /**
   * Adds a click handler.
   * @param handler The click handler.
   */
  public void addClickHandler(ClickHandler handler) {
    table.addClickHandler(handler);
  }
  
  /**
   * Focus on the first field in the login box.
   */
  /*package*/ void focus() {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        table.focus();
      }
    });
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * Special table element for login box.
   */
  private static class Table extends ComplexPanel {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates the table widget.
     */
    public Table() {
      Element			headerRow;
      Element			contentRow;
      Element			headerColumn;
      Element			contentColumn;
      
      setElement(DOM.createTable());
      getElement().setPropertyString("align", "center");
      getElement().setPropertyString("border", "0");
      getElement().setPropertyString("cellPadding", "0");
      getElement().setPropertyString("cellSpacing", "0");
      headerRow = DOM.createTR();
      contentRow = DOM.createTR();
      DOM.appendChild(getElement(), headerRow);
      DOM.appendChild(getElement(), contentRow);
      headerColumn = DOM.createTD();
      contentColumn = DOM.createTD();
      DOM.appendChild(headerRow, headerColumn);
      DOM.appendChild(contentRow, contentColumn);
      headerColumn.setPropertyString("align", "left");
      contentColumn.setPropertyString("align", "center");
      welcomeText = Document.get().createElement("b").cast();
      welcomeImage = DOM.createImg();
      welcomeImage.setClassName(Styles.LOGIN_BOX_IMAGE);
      DOM.appendChild(headerColumn, welcomeText);
      DOM.appendChild(headerColumn, Document.get().createBRElement());
      DOM.appendChild(headerColumn, welcomeImage);
      content = new LoginPane();
      //DOM.appendChild(contentColumn, content.getElement());
      add(content, contentColumn);
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    /**
     * Sets the welcome text.
     * @param text The welcome text.
     */
    public void setWelcomeText(String text) {
      welcomeText.setInnerText(text);
    }
    
    /**
     * Sets the welcome image.
     * @param uri The welcome image URI.
     */
    public void setWelcomeImage(String uri) {
      welcomeImage.setClassName(Styles.LOGIN_BOX_IMAGE);
      welcomeImage.setAttribute("src", uri);
      welcomeImage.addClassName(ResourcesUtil.getResourceName(uri));
    }
    
    /**
     * Sets the error.
     * @param error The error message.
     */
    public void setError(String error) {
      content.setError(error);
    }
    
    /**
     * Removes an error
     */
    public void removeError() {
      content.removeError();
    }
    
    /**
     * Sets the information text.
     * @param text The information text.
     */
    public void setInformationText(String text) {
      content.setInformationText(text);
    }
    
    /**
     * Sets the user name label.
     * @param label the user name label.
     */
    public void setUsernameLabel(String label) {
      content.setUsernameLabel(label);
    }
    
    /**
     * Returns the user name value.
     * @return the user name value.
     */
    public String getUsername() {
      return content.getUsername();
    }
    
    /**
     * Sets the password label.
     * @param label The password label.
     */
    public void setPasswordLabel(String label) {
      content.setPasswordLabel(label);
    }
    
    /**
     * Returns the password value.
     * @return The password value.
     */
    public String getPassword() {
      return content.getPassword();
    }
    
    /**
     * Sets the language label.
     * @param label the language label.
     */
    public void setLanguageLabel(String label) {
      content.setLanguageLabel(label);
    }
    
    /**
     * Appends a supported language.
     * @param language The language display name.
     * @param isocode The iso code of the language and the name to be returned for the selection.
     */
    public void addLanguage(String language, String isocode) {
      content.addLanguage(language, isocode);
    }
    
    /**
     * Returns the language selected value.
     * @return The selected value.
     */
    public String getSelectedLanguage() {
      return content.getSelectedLanguage();
    }
    
    /**
     * Sets the selected language.
     * @param language The language index.
     */
    public void setSelectedLanguage(int language) {
      content.setSelectedLanguage(language);
    }
    
    /**
     * Sets the login displayed text.
     * @param text The login text.
     */
    public void setLoginText(String text) {
      content.setLoginText(text);
    }

    /**
     * Adds a language change handler.
     * @param handler The change handler.
     */
    public void addChangeHandler(ChangeHandler handler) {
      content.addChangeHandler(handler);
    }
    
    /**
     * Adds a click handler.
     * @param handler The click handler.
     */
    public void addClickHandler(ClickHandler handler) {
      content.addClickHandler(handler);
    }
    
    /**
     * Focus on the first field in the login box.
     */
    /*package*/ void focus() {
      Scheduler.get().scheduleEntry(new ScheduledCommand() {
        
        @Override
        public void execute() {
          content.focus();
        }
      });
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private Element			welcomeText;
    private Element			welcomeImage;
    private LoginPane			content;
  }
  
  /**
   * The login panel.
   */
  private static class LoginPane extends SimpleFocusablePanel implements KeyPressHandler {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates the login panel
     */
    public LoginPane() {
      setStyleName(Styles.LOGIN_BOX_PANE);
      getElement().setAttribute("hideFocus", "true");
      getElement().getStyle().setProperty("outline", "0px");
      table = new FlexTable();
      errorIndicator = new VSpanPanel();
      errorIndicator.getElement().setId("post_error");
      errorIndicator.setStyleName(Styles.LOGIN_BOX_PANE_ERROR);
      InformationText = new VSpanPanel();
      InformationText.setStyleName(Styles.LOGIN_BOX_PANE_INFO);
      usernameLabel = new VInputLabel();
      usernameLabel.setHtmlFor("user_name");
      username = new VInputText();
      username.setId("user_name");
      username.setName("user_name");
      username.setSize(10);
      username.setTabIndex(1);
      username.addKeyPressHandler(this);
      passwordLabel = new VInputLabel();
      passwordLabel.setHtmlFor("user_password");
      password = new VInputPassword();
      password.setId("user_password");
      password.setName("user_password");
      password.setTabIndex(2);
      password.setSize(20);
      password.addKeyPressHandler(this);
      languageLabel = new VInputLabel();
      language = new ListBox(false);
      language.setName("login_language");
      language.setWidth("152px");
      login = new VInputButton();
      login.getElement().setId("login_button");
      login.setStyleName(Styles.INPUT_BUTTON_PRIMARY);
      login.getInputElement().setName("Login");
      login.getInputElement().setTabIndex(3);
      table.setCellSpacing(2);
      table.setCellPadding(0);
      table.setBorderWidth(0);
      table.getElement().setPropertyString("align", "center");
      table.getElement().setPropertyString("width", "100%");
      table.setWidget(0, 0, errorIndicator);
      table.getFlexCellFormatter().setColSpan(0, 0, 2);
      table.getCellFormatter().setHeight(0, 0, "15px");
      table.setWidget(1, 0, InformationText);
      table.getFlexCellFormatter().setColSpan(1, 0, 2);
      table.getCellFormatter().setHeight(1, 0, "15px");
      table.setWidget(2, 0, new VHiddenSeparator(12));
      table.getFlexCellFormatter().setColSpan(2, 0, 2);
      table.setWidget(3, 0, usernameLabel);
      table.getCellFormatter().setHeight(3, 0, "15px");
      table.getCellFormatter().getElement(3, 0).setAttribute("scope", "row");
      table.setWidget(3, 1, username);
      table.getCellFormatter().setHeight(3, 1, "15px");
      table.setWidget(4, 0, passwordLabel);
      table.getCellFormatter().setHeight(4, 0, "20%");
      table.setWidget(4, 1, password);
      table.getCellFormatter().setHeight(4, 1, "15px");
      table.setWidget(5, 0, languageLabel);
      table.getCellFormatter().setWidth(5, 0, "15px");
      table.setWidget(5, 1, language);
      table.getCellFormatter().setHeight(5, 1, "15px");
      table.setWidget(6, 0, new VHiddenSeparator(16));
      table.getFlexCellFormatter().setColSpan(6, 0, 2);
      table.getCellFormatter().setWidth(7, 0, "150px");
      table.setWidget(7, 1, login);
      table.getCellFormatter().setHeight(7, 1, "15px");
      table.getCellFormatter().setWidth(7, 1, "250px");
      setWidget(table);
      sinkEvents(Event.ONKEYDOWN);
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public void onBrowserEvent(Event event) {
      super.onBrowserEvent(event);
      if (event.getTypeInt() == Event.ONKEYDOWN) {
	if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
	  event.preventDefault();
	  if (login != null) {
	    login.click();
	  }
	}
      }
    }
    
    /**
     * Sets the error.
     * @param error The error message.
     */
    public void setError(String error) {
      errorIndicator.getElement().setInnerText(error);
    }
    
    /**
     * Removes an error
     */
    public void removeError() {
      if (errorIndicator.getElement().getInnerText() != null
	  && errorIndicator.getElement().getInnerText().length() > 0)
      {
	errorIndicator.getElement().setInnerText("");
      }
    }
    
    /**
     * Sets the information text.
     * @param text The information text.
     */
    public void setInformationText(String text) {
      InformationText.getElement().setInnerText(text);
    }
    
    /**
     * Sets the user name label.
     * @param label the user name label.
     */
    public void setUsernameLabel(String label) {
      usernameLabel.getElement().setInnerText(label);
    }
    
    /**
     * Returns the user name value.
     * @return the user name value.
     */
    public String getUsername() {
      return username.getValue();
    }
    
    /**
     * Sets the password label.
     * @param label The password label.
     */
    public void setPasswordLabel(String label) {
      passwordLabel.getElement().setInnerText(label);
    }
    
    /**
     * Returns the password value.
     * @return The password value.
     */
    public String getPassword() {
      return password.getValue();
    }
    
    /**
     * Sets the language label.
     * @param label the language label.
     */
    public void setLanguageLabel(String label) {
      languageLabel.getElement().setInnerText(label);
    }
    
    /**
     * Appends a supported language.
     * @param language The language display name.
     * @param isocode The iso code of the language and the name to be returned for the selection.
     */
    public void addLanguage(String language, String isocode) {
      this.language.addItem(language, isocode);
    }
    
    /**
     * Returns the language selected value.
     * @return The selected value.
     */
    public String getSelectedLanguage() {
      return language.getSelectedValue();
    }
    
    /**
     * Sets the selected language.
     * @param language The language index.
     */
    public void setSelectedLanguage(int language) {
      this.language.setSelectedIndex(language);
    }
    
    /**
     * Sets the login displayed text.
     * @param text The login text.
     */
    public void setLoginText(String text) {
      login.getInputElement().setValue(text);
    }

    /**
     * Adds a language change handler.
     * @param handler The change handler.
     */
    public void addChangeHandler(ChangeHandler handler) {
      language.addChangeHandler(handler);
    }
    
    /**
     * Adds a click handler.
     * @param handler The click handler.
     */
    public void addClickHandler(ClickHandler handler) {
      login.addClickHandler(handler);
    }
    
    @Override
    public void onKeyPress(KeyPressEvent event) {
      // clear error on key press for user name and password
      removeError();
    }
    
    /**
     * Focus on the first field in the login box.
     */
    public void focus() {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          username.getElement().focus();
        }
      });
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final FlexTable		table;
    private final VSpanPanel		errorIndicator;
    private final VSpanPanel		InformationText;
    private final VInputLabel		usernameLabel;
    private final VInputText		username;
    private final VInputLabel		passwordLabel;
    private final VInputPassword	password;
    private final VInputLabel		languageLabel;
    private final ListBox		language;
    private final VInputButton		login;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Table				table;
}
