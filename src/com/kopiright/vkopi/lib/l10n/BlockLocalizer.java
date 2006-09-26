/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.kopiright.util.base.InconsistencyException;

/**
 * //!!!FIX:taoufik
 */
public class BlockLocalizer {


  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /**
   * //!!!FIX:taoufik
   */
  public BlockLocalizer(LocalizationManager manager,
                        Document document,
                        String ident)
  {
    Element     root;

    this.manager = manager;
    root = document.getRootElement();
    if (! root.getName().equals("form")
        && ! root.getName().equals("blockinsert")) {
      throw new InconsistencyException("bad root element " + root.toString());
    }
    self = lookupChild(root, "block", "name", ident);
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the value of the title attribute.
   */
  public String getTitle() {
    return self.getAttributeValue("title");
  }

  /**
   * Returns the value of the help attribute.
   */
  public String getHelp() {
    return self.getAttributeValue("help");
  }

  /**
   * Returns the message for the specified index.
   */
  public String getIndexMessage(String ident) {
    Element     e;

    e = lookupChild(self, "index", "ident", ident);
    return e.getAttributeValue("message");
  }

  /**
   *
   */
  public FieldLocalizer getFieldLocalizer(String ident) {
    return new FieldLocalizer(manager,
                              lookupChild(self, "field", "ident", ident));
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Returns the child with specified type and attribute = value.
   */
  private Element lookupChild(Element parent,
                              String type,
                              String attribute,
                              String value)
  {
    List        childs;
    
    childs = parent.getChildren(type);
    for (Iterator i = childs.iterator(); i.hasNext(); ) {
      Element   e;

      e = (Element)i.next();
      if (e.getAttributeValue(attribute).equals(value)) {
        return e;
      }
    }
    throw new InconsistencyException(type + " " + attribute + " = " + value + " not found");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final LocalizationManager     manager;
  private final Element                 self;
}
