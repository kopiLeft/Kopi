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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion;

import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SuggestOracle;

public class DefaultSuggestOracle extends SuggestOracle {
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  public DefaultSuggestOracle(SuggestionHandler handler) {
    this.handler = handler;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void requestSuggestions(Request request, Callback callback) {
    if (isInitiatedFromServer) {
      // invoke the callback
      org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response		response;
      
      response = new org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion.Response();
      response.setAutocompleteSuggestions(suggestions);
      callback.onSuggestionsReady(request, response);
    } else {
      // send event to the server side
      String 	query = request.getQuery();
      
      if (isTrimQuery()) {
	query = query.trim();
      }
      if (query.length() >= getMinimumQueryCharacters()) {
	scheduleQuery(request.getQuery());
      }
    }
  }

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  public void setSuggestions(List<AutocompleteSuggestion> suggestions) {
    isInitiatedFromServer = true;
    this.suggestions = Collections.unmodifiableList(suggestions);
    if (handler != null) {
      /*
       * Fires twice the query of suggestions from server side. 
       */
      //handler.refreshSuggestionList();
      handler.showSuggestionList();
    }
    isInitiatedFromServer = false;
  }

  private void scheduleQuery(final String query) {
    if (sendQueryToServer != null) {
      sendQueryToServer.cancel();
    }
    sendQueryToServer = new Timer() {
      
      @Override
      public void run() {
	sendQueryToServer = null;
	if (queryListener != null && query != null && query.equals(handler.getText())) {
	  queryListener.handleQuery(query);
	}
      }
    };
    sendQueryToServer.schedule(delayMillis);
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  public boolean isTrimQuery() {
    return trimQuery;
  }
  
  public void setTrimQuery(boolean trimQuery) {
    this.trimQuery = trimQuery;
  }
  
  public int getMinimumQueryCharacters() {
    return minimumQueryCharacters;
  }
  
  public void setMinimumQueryCharacters(int minimumQueryCharacters) {
    this.minimumQueryCharacters = minimumQueryCharacters;
  }

  public void setDelayMillis(int delayMillis) {
    this.delayMillis = delayMillis;
  }

  public void setQueryListener(QueryListener listener) {
    this.queryListener = listener;
  }
  
  @Override
  public boolean isDisplayStringHTML() {
    return true;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final SuggestionHandler 		handler;
  private int 					delayMillis = 800;
  private Timer 				sendQueryToServer = null;
  private QueryListener 			queryListener;
  private boolean 				isInitiatedFromServer = false;
  private boolean 				trimQuery = true;
  private int 					minimumQueryCharacters = 0;
  private List<AutocompleteSuggestion> 		suggestions = Collections.emptyList();
}
