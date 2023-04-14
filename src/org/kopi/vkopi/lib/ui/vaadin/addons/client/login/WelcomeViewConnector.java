/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.addons.WelcomeView;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.LoginWindowListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.login.WelcomeViewState.FontMetricsRequest;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.login.WelcomeViewState.FontMetricsResponse;

import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The welcome screen connector.
 */
@SuppressWarnings("serial")
@Connect(value = WelcomeView.class, loadStyle = LoadStyle.EAGER)
public class WelcomeViewConnector extends AbstractComponentConnector implements LoginWindowListener {
  
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  public WelcomeViewConnector() {
    registerRpc(WelcomeViewClientRpc.class, rpc);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    getWidget().addLoginWindowListener(this);
  }

  @Override
  public VWelcomeView getWidget() {
    return (VWelcomeView) super.getWidget();
  }
  
  @Override
  public WelcomeViewState getState() {
    return (WelcomeViewState) super.getState();
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }
  
  @OnStateChange("resourcess")
  /*package*/ void setResources() {
    String		logo = getResourceUrl("logo");
    String              slogan = getResourceUrl("slogan");
    
    if (logo != null) {
      getWidget().setLogo(logo, null);
    }
    if (slogan != null) {
      getWidget().setSloganImage(slogan);
    }
  }
  
  @OnStateChange("languages")
  /*package*/ void setLanguages() {
    for (Map.Entry<String, String> language : getState().languages.entrySet()) {
      getWidget().addSupportedLanguage(language.getKey(), language.getValue());
    }
  }
  
  @OnStateChange("fontMetricsRequests")
  /*package*/ void calculateFontMetrics() {
    for (FontMetricsRequest fmr : getState().fontMetricsRequests) {
      if (fmr != null) {
        FontMetrics             fm;
        
        fm = new FontMetrics(fmr.fontFamily, fmr.fontSize, fmr.text);
        fm.calculate();
        fontsMetrics.add(fm);
      }
    }
  }
  
  @OnStateChange("locale")
  /*package*/ void setLocale() {
    getWidget().setDefaultLocale(getState().locale);
  }
  
  @OnStateChange("href")
  /*package*/ void setHref() {
    getWidget().setHref(getState().href);
    getWidget().setTarget("_blank");
  }
  
  @Override
  public void onStateChanged(StateChangeEvent stateChangeEvent) {
    int		index = 0;
    
    super.onStateChanged(stateChangeEvent);
    for (Map.Entry<String, String> language : getState().languages.entrySet()) {
      if (getState().locale.equals(language.getValue())) {
	getWidget().setSelectedLanguage(index);
	break;
      }
      index ++;
    }
  }

  @Override
  public void onLogin(String username, String password, String language) {
    getRpcProxy(WelcomeViewServerRpc.class).onLogin(username, password, language, createFontMetricsResponses());
  }
  
  /**
   * Creates a list of a font metrics responses
   * @return The font metrics responses.
   */
  protected List<FontMetricsResponse> createFontMetricsResponses() {
    List<FontMetricsResponse>   list = new ArrayList<FontMetricsResponse>();
    
    for (FontMetrics fm : fontsMetrics) {
      list.add(new FontMetricsResponse(fm.getFontFamily(),
                                       fm.getFontSize(),
                                       fm.getText(),
                                       fm.getWidth(),
                                       fm.getHeight()));
    }
    
    return list;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<FontMetrics>                     fontsMetrics = new ArrayList<FontMetrics>();
  private WelcomeViewClientRpc			rpc = new WelcomeViewClientRpc() {
    
    @Override
    public void onError(String message) {
      getWidget().setError(message);
    }
  };
}
