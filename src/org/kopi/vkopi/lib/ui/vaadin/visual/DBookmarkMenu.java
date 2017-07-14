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

import java.util.List;

import org.kopi.vkopi.lib.visual.Module;
import org.kopi.vkopi.lib.visual.RootMenu;
import org.kopi.vkopi.lib.visual.VMenuTree;

/**
 * The book mark menu 
 */
@SuppressWarnings("serial")
public class DBookmarkMenu extends DMenu {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  protected DBookmarkMenu(VMenuTree model) {
    super(model);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void buildMenu(List<RootMenu> roots) {
    if (!getModel().getShortcutsID().isEmpty()) {
      for (int i = 0; i < ((VMenuTree) getModel()).getShortcutsID().size() ; i++) {
        int       id = getModel().getShortcutsID().get(i).intValue();

        for (int j = 0; j < getModel().getModuleArray().length; j++) {
          if (getModel().getModuleArray()[j].getId() == id) {
            Module      module = getModel().getModuleArray()[j];
            
            toModuleItem(module, null);
            modules.put(module.getId(), module);
          }
        }
      }
    }
  }
  
  @Override
  public int getType() {
    return VMenuTree.BOOKMARK_MENU;
  }
}
