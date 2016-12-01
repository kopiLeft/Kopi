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

package org.kopi.vkopi.lib.l10n;

import org.jdom.Document;
import org.jdom.Element;
import org.kopi.util.base.InconsistencyException;

/**
 * Implements an actor localizer.
 */
public class MessageLocalizer {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /**
   * Constructor
   *
   * @param     document        the document containing the actor localization
   * @param     ident           the identifier of the actor localization
   */
  public MessageLocalizer(Document document, String ident) {
    Element root;
    
    root = document.getRootElement();
    if (! root.getName().equals("messages")) {
      throw new InconsistencyException("bad root element " + root.toString());
    }
    
    self = Utils.lookupChild(root, "message", "ident", ident);
  }
  
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  
  /**
   * Returns the value of the text attribute.
   */
  public String getText() {
    return self.getAttributeValue("text");
  }
  
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Element                 self;
}
