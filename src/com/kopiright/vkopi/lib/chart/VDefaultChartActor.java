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

package com.kopiright.vkopi.lib.chart;

import com.kopiright.vkopi.lib.visual.VActor;
import com.kopiright.vkopi.lib.visual.VlibProperties;

@SuppressWarnings("serial")
public class VDefaultChartActor extends VActor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  
  public VDefaultChartActor(String menuIdent,
                            String actorIdent,
                            String iconName,
                            int acceleratorKey,
                            int acceleratorModifier)
  {
    super(menuIdent, null, actorIdent, null, null, acceleratorKey, acceleratorModifier);
    this.iconName = iconName;
    localize();
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  private void localize() {
    menuName = VlibProperties.getString(menuIdent);
    menuItem = VlibProperties.getString(actorIdent);
    help = VlibProperties.getString(actorIdent + "-help");
  }
}
