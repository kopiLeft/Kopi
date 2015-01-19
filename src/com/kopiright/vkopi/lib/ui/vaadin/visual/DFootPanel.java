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

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;

/**
 * The <code>DFootPanel</code> is used to display a set of information
 * like state information, statistic information and wait information.
 */
@SuppressWarnings("serial")
public class DFootPanel extends CssLayout {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  /**
   * Creates a new <code>DFootPanel</code> instance.
   * @param parent The foot panel container.
   */
  public DFootPanel(DWindow parent) {	  
    CssLayout east = new CssLayout();   
    east.setWidth("40%");
    addStyleName(KopiTheme.FOOT_PANEL);
    setSizeFull();
    statisticsPanel = new DInfoPanel();
    statisticsPanel.addStyleName(KopiTheme.LABEL_STATISTICS);
    statisticsPanel.setWidth("33%");
    messagePanel = new DInfoPanel();
    messagePanel.addStyleName(KopiTheme.LABEL_MESSAGE);
    messagePanel.setWidth("60%");
    statePanel = new DStatePanel();
    statePanel.setWidth("66%");
    statisticsPanel.setText(" ");
    addComponent(messagePanel);
    addComponent(east);
    east.addComponent(statisticsPanel);
    east.addComponent(statePanel);
    setStatePanel(new Panel());
  }

  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------

  /**
   * Sets the information text.
   * @param message The information text.
   */
  public void setInformationText(String message) {
    messagePanel.setText(message, false);
  }
  
  /**
   * Display statistical information.
   * <p>In case of a report table informations will be displayed.</p>
   * @param message The statistics message.
   */
  public void setStatisticsText(String message) {
    statisticsPanel.setText(message, false);
  }

  /**
   * Changes the mode to wait state.
   * @param message The wait message.
   */
  public void setWaitInfo(final String message) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
	oldMessage = messagePanel.getText();
	if (message != null) {
	  messagePanel.setText(message, true);
	}
      }
    }); 
  }
  
  /**
   * Inform user about the number of records fetched and current one.
   * @param state The state panel
   */
  public void setStatePanel(Panel state) {
    statePanel.setInfo(state);
  }

  /**
   * Change mode to free state
   */
  public void unsetWaitInfo() {
    messagePanel.setText(oldMessage, false);
  }
  
  /**
   * Set the info panel that current process accept user interrupt
   * @param allowed Is the interrupt allowed ?
   */
  public void setUserInterrupt(boolean allowed) {
    statePanel.setUserInterrupt(allowed);
  }

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  private DInfoPanel            		messagePanel;
  private DInfoPanel            		statisticsPanel;
  private DStatePanel           		statePanel;
  private String                		oldMessage;
}
