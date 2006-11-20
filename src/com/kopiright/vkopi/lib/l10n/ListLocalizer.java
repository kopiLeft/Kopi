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
 * Implements a list localizer.
 */
public class ListLocalizer extends Localizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             manager         the manager to use for localization
   * @param             document        the document containing the list localization
   * @param             ident           the identifier of the list
   */
  public ListLocalizer(LocalizationManager manager,
                       Document document,
                       String ident)
  {
    super(manager);

    Element     root;
    Element     type;

    root = document.getRootElement();
    if (! root.getName().equals("form")
        && ! root.getName().equals("report")
        && ! root.getName().equals("blockinsert")
        && ! root.getName().equals("insert")) {
      throw new InconsistencyException("bad root element " + root.toString());
    }
    type = Utils.lookupChild(root, "type", "ident", ident);
    self = Utils.lookupChild(type, "list");
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the title of the specified item.
   *
   * @param             column          the identifier of the column
   */
  public String getColumnTitle(String column) {
    Element     e;

    e = Utils.lookupChild(self, "listdesc", "column", column);
    return e.getAttributeValue("title");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Element                 self;
}
