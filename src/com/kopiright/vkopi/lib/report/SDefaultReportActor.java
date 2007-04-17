/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
 * $Id: SActor.java 27892 2007-02-16 16:09:48Z graf $
 */

package com.kopiright.vkopi.lib.report;

import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.visual.SActor;
import com.kopiright.vkopi.lib.visual.VlibProperties;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


public class SDefaultReportActor extends SActor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  public SDefaultReportActor(String menuIdent,
                             String actorIdent,
                             String iconIdent,
                             int acceleratorKey,
                             int acceleratorModifier) {
                                         
    super(menuIdent,null,actorIdent,null,null,acceleratorKey,acceleratorModifier);
    this.iconIdent = iconIdent;
    localize();
  }
  
  
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  
  private void localize() {
    menuName = VlibProperties.getString(menuIdent);
    menuItem = VlibProperties.getString(actorIdent);
    help = VlibProperties.getString(actorIdent + "-help");
    iconName = VlibProperties.getString(iconIdent);
    initAction();
  }  
  private String iconIdent;
}
