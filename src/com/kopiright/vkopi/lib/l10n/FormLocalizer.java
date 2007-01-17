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

import com.kopiright.util.base.InconsistencyException;

/**
 * Implements a form localizer.
 */
public class FormLocalizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             document        the document containing the form localization
   */
  public FormLocalizer(Document document) {
    this.root = document.getRootElement();
    if (! root.getName().equals("form")) {
      throw new InconsistencyException("bad root element " + root.toString());
    }
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the value of the title attribute.
   */
  public String getTitle() {
    return root.getAttributeValue("title");
  }

  /**
   * Returns the value of the page child.
   *
   * @param             position                the position of the page
   */
  public String getPage(int position) {
    List        pages;

    pages = root.getChildren("page");
    for (Iterator i = pages.iterator(); i.hasNext(); ) {
      Element   p;

      p = (Element)i.next();
      if (p.getAttributeValue("ident").equals("Id$" + position)) {
        return p.getAttributeValue("title");
      }
    }
    throw new InconsistencyException("page " + position + " not found");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final Element         root;
}
