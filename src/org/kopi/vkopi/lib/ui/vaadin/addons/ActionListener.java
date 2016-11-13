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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;


/**
 * Interface for listening for a {@link ActionEvent} fired by a
 * {@link Component}.
 */
public interface ActionListener extends Serializable {

  /**
   * Called when a {@link Actor} action has been performed. A reference to the
   * actor is given by {@link ActionEvent#getActor()}.
   * 
   * @param event An event containing information about the action.
   */
  public void actionPerformed(ActionEvent event);
  
  // action method
  Method	ACTOR_ACTION_METHOD = ReflectTools.findMethod(ActionListener.class, "actionPerformed", ActionEvent.class);
}
