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

package com.kopiright.vkopi.lib.ui.vaadin.preview;

import java.io.File;
import java.io.IOException;

import com.kopiright.vkopi.lib.preview.VPreviewWindow;
import com.kopiright.vkopi.lib.ui.vaadin.visual.DWindow;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Embedded;

/**
 * The <code>DPreviewWindow</code> is the vaadin view side of the
 * {@link VPreviewWindow}.
 */
@SuppressWarnings("serial")
public class DPreviewWindow extends DWindow {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DPreviewWindow</code> instance.
   * @param model The preview window model.
   */
  public DPreviewWindow(VPreviewWindow model) {
    super(model);
    setSizeUndefined();
    this.model = model;
    embedded = new Embedded(); 
    embedded.setSizeFull();
    embedded.setHeight(297, Unit.MM); // size of an A4 document
    setContent(embedded);
  }
  
  /**
   * Customized initializations.
   */
  public void init() {
    // to be overridden in children classes.
  }

  @Override
  public void run() {
    try {
      model.setActorEnabled(VPreviewWindow.CMD_QUIT, true); // force to enable the quit actor
      setEmbeddedContent(model.getPrintJob().getDataFile());
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  } 

  /**
   * Sets the embedded content.
   * @param file The file content.
   */
  @SuppressWarnings("deprecation")
  private void setEmbeddedContent(File file) {
    embedded.setSource(new FileResource(file));
    embedded.setMimeType("application/pdf");
    embedded.setType(Embedded.TYPE_BROWSER);
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  private VPreviewWindow                	model;
  private Embedded 		        	embedded;
}
