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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * The <code>DInfoPanel</code> is an UI component used to display a text
 * in its HTML representation.
 * <p>
 *   The {@link CssLayout} is used to display a lightweight HTML DOM tree
 *   at the client side.
 * </p>
 */
@SuppressWarnings("serial")
public class DInfoPanel extends CssLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DInfoPanel</code> instance.
   */
  public DInfoPanel() {
    text = new Label("\"visualKopi\" kopiRight Managed Solutions GmbH");
    text.setContentMode(ContentMode.HTML);
    text.addStyleName(KopiTheme.INFO_LABEL);
    addStyleName(KopiTheme.HLAYOUT_INFO_PANEL);
    addComponent(text);
    setSizeFull();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the info panel text.
   * @param text The info panel text.
   */
  public void setText(String text) {
    setText(text, false);
  }

  /**
   * Sets the info panel text.
   * @param text The info text.
   * @param highlight Should we apply a text highlight ?
   */
  public void setText(final String text, final boolean highlight) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
	final	String currentText = getText();
	   
        if (currentText != null && !currentText.equals(text)) {
          DInfoPanel.this.text.setValue(text);
        }
		
        if (highlight) {
	  DInfoPanel.this.text.addStyleName(KopiTheme.LABEL_HIGHLIGHT);
	}
      }
    });
  }

  /**
   * Returns the info panel text.
   * @return The info panel text.
   */
  public String getText() {
    return (String) text.getValue();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
	  
  private Label					text;  
}
