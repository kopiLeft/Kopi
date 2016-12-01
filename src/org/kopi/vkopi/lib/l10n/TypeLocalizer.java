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
 * Implements a type localizer.
 */
public class TypeLocalizer extends Localizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             manager         the manager to use for localization
   * @param             document        the document containing the type localization
   * @param             ident           the identifier of the type
   */
  public TypeLocalizer(LocalizationManager manager,
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
    self = Utils.lookupChild(type, "code");
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the title of the specified item.
   */
  public String getCodeLabel(String column) {
    Element     e;

    e = Utils.lookupChild(self, "codedesc", "ident", column);
    return e.getAttributeValue("label");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Element                 self;
}
