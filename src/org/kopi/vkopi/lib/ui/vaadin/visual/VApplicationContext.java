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

import org.kopi.vkopi.lib.visual.Application;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.PreviewRunner;

import com.vaadin.ui.UI;

/**
 * The {@code VApplicationContext} is an application context
 * for vaadin applications.
 */
public class VApplicationContext extends ApplicationContext {
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Returns the current application instance using the ThreadLocal pattern.
   * @return the current application instance.
   */
  @Override
  public Application getApplication() {
    return (Application)UI.getCurrent();
  }

  /**
   * Returns the current PreviewRunner.
   * @return The current PreviewRunner.
   */
  @Override
  public PreviewRunner getPreviewRunner() {
    return  previewRunner == null ? new VPreviewRunner() : previewRunner ;
  }
  
  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------
  
  private VPreviewRunner			previewRunner;
}
