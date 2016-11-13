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

import java.io.IOException;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VHelpViewer;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * The <code>DHelpViewer</code> is used to display help information.
 * <p>
 *   The help view is used by the UI factory to create vaadin view version
 *   of the the {@link VHelpViewer} model.
 * </p>
 */
@SuppressWarnings("serial")
public class DHelpViewer extends DWindow {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DHelpViewer</code> instance.
   * @param model The help view model.
   */
  public DHelpViewer(final VHelpViewer model) {
    super(model);
    model.setDisplay(this);
    
    try {
      Panel			pane;
      VerticalLayout		layout;
      
      pane = new Panel();
      html = new CustomLayout(model.getURL().openStream());
      layout = new VerticalLayout(html);
      pane.setWidth(600, Unit.PIXELS);
      pane.setHeight(500, Unit.PIXELS);
      pane.setContent(layout);
      layout.setMargin(true);
      setContent(pane);
    } catch (IOException e) {
      throw new InconsistencyException(e);
    } 
  }

  @Override
  public void run() throws VException {
    focus();
    getModel().setActorEnabled(VHelpViewer.CMD_QUIT, true);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private CustomLayout				html;
}
