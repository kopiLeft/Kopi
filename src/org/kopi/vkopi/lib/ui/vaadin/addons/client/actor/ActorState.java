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
package org.kopi.vkopi.lib.ui.vaadin.addons.client.actor;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.NoLayout;

/**
 * The actor shared state.
 */
@SuppressWarnings("serial")
public class ActorState extends AbstractComponentState {

  /**
   * The icon name. The name will be translated after to a
   * font awesome icon.
   */
  @NoLayout
  public String                 icon;
  
  /**
   * The actor accelerator key.
   */
  @NoLayout
  public int			acceleratorKey = 0;

  /**
   * The actor modifiers key.
   */
  @NoLayout
  public int[]			modifiersKey = new int[] {0};
  
  /**
   * The menu containing this actor.
   */
  @NoLayout
  public String                 menu;
}
