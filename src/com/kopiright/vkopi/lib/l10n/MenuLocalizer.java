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

package com.kopiright.vkopi.lib.l10n;

import org.jdom.Document;
import org.jdom.Element;

import com.kopiright.util.base.InconsistencyException;

/**
 * Implements a menu localizer.
 */
public class MenuLocalizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             document        the document containing the menu localization
   * @param             ident           the identifier of the menu localization
   */
  public MenuLocalizer(Document document, String ident) {
    Element root;
    
    root = document.getRootElement();
    if (! root.getName().equals("form")
        && ! root.getName().equals("insert")) {
      throw new InconsistencyException("bad root element " + root.toString());
    }
    
    self = Utils.lookupChild(root, "menu", "ident", ident);
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the value of the label attribute.
   */
  public String getLabel() {
    return self.getAttributeValue("label");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Element                 self;
}
