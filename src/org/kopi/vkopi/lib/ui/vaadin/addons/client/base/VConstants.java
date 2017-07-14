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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

/**
 * Collects needed constants.
 */
public interface VConstants {

  // --------------------------------------------------
  // ACCESS
  // --------------------------------------------------

  // access flags
  int ACS_HIDDEN                        = 0;
  int ACS_SKIPPED                       = 1;
  int ACS_VISIT                         = 2;
  int ACS_MUSTFILL                      = 4;
  int ACS_ACCESS                        = 1 + 2 + 4;
  
  // record info flags
  int RCI_FETCHED                       = 0x00000001;
  int RCI_CHANGED                       = 0x00000002;
  int RCI_DELETED                       = 0x00000004;
  int RCI_TRAILED                       = 0x00000008;
  
  // menus types
  int MAIN_MENU                         = -1;
  int USER_MENU                         = -2;
  int ADMIN_MENU                        = -3;
  int BOOKMARK_MENU                     = -4;
}
