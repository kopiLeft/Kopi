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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.common;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VAnchorPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * A popup window widget with a title above.
 */
public class VPopupWindow extends VPopup {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VNotification</code> instance.
   * @param connection The application connection.
   * @param autoHide Set auto hide mode.
   * @param modal Set the popup modality.
   */
  public VPopupWindow(ApplicationConnection connection, boolean autoHide, boolean modal) {
    super(connection, autoHide, modal);
    FlexTable	content = new FlexTable();
    
    content.setStyleName("olBgClass");
    content.setCellSpacing(0);
    content.setCellPadding(1);
    content.setBorderWidth(0);
    setWidget(content);
    header = new FlexTable();
    header.setStyleName("olCgClass");
    header.setCellSpacing(0);
    header.setCellPadding(2);
    header.setBorderWidth(0);
    header.setWidth("100%");;
    
    body = new FlexTable();
    body.setStyleName("olCgClass");
    body.setCellSpacing(0);
    body.setCellPadding(2);
    body.setBorderWidth(0);
    body.setWidth("100%");;
    
    content.setWidget(0, 0, header);
    content.setWidget(1, 0, body);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Creates the close panel.
   */
  protected Widget createCloseIcon() {
    VAnchorPanel		anchor;
    VSpanPanel			span;
    SimplePanel			imageContainer;
    ImageElement		imageElement;
    
    anchor = new VAnchorPanel();
    span = new VSpanPanel();
    imageContainer = new SimplePanel();
    imageElement = Document.get().createImageElement();
    anchor.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        hide(); // close the popup.
      }
    });
    span.getElement().setPropertyString("style", "color:#eeeeff;font-family:Verdana,Arial,Helvetica;font-size:67%;text-decoration:underline;");
    imageContainer.getElement().setPropertyString("float", "right");
    anchor.add(span);
    span.add(imageContainer);
    DOM.appendChild(imageContainer.getElement(), imageElement);
    imageElement.setSrc(ResourcesUtil.getImageURL(getApplicationConnection(), "close.gif"));
    imageElement.setPropertyString("style", "margin-left:2px; margin-right: 2px");
    
    return anchor;
  }
  
  /**
   * Sets the header text.
   * @param text The header text.
   */
  public void setHeaderText(String text) {
    FlowPanel			container;
    SimplePanel			title;
    
    container = new FlowPanel();
    container.setStyleName("olCapFontClass");
    title = new SimplePanel();
    title.getElement().setPropertyString("float", "left");
    title.getElement().setInnerText(text);
    container.add(title);
    container.add(createCloseIcon());
    header.setWidget(0, 0, container);
    header.getCellFormatter().getElement(0, 0).setClassName("olCgClass");
    header.getCellFormatter().getElement(0, 0).setPropertyString("width", "100%");
  }
  
  /**
   * Sets the popup window content.
   * @param content The content to be shown.
   */
  public void setContent(Widget content) {
    body.setWidget(0, 0, content);
    header.getCellFormatter().getElement(0, 0).setClassName("olFgClass");
    header.getCellFormatter().getElement(0, 0).setPropertyString("valign", "top");
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final FlexTable			header;
  private final FlexTable			body;
}
