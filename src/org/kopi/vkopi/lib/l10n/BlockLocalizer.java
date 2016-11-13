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
 * Implements a block localizer.
 */
public class BlockLocalizer extends Localizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             manager         the manager to use for localization
   * @param             document        the document containing the block localization
   * @param             ident           the identifier of the block
   */
  public BlockLocalizer(LocalizationManager manager,
                        Document document,
                        String ident)
  {
    super(manager);

    Element     root;

    root = document.getRootElement();
    if (! root.getName().equals("form")
        && ! root.getName().equals("blockinsert")) {
      throw new InconsistencyException("bad root element " + root.toString());
    }
    self = Utils.lookupChild(root, "block", "name", ident);
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
   *
   * @param             ident           the identifier of the index
   */
  public String getIndexMessage(String ident) {
    Element     e;

    e = Utils.lookupChild(self, "index", "ident", ident);
    return e.getAttributeValue("message");
  }

  /**
   * Constructs a field localizer for the given field.
   *
   * @param             ident           the identifier of the field
   */
  public FieldLocalizer getFieldLocalizer(String ident) {
    return new FieldLocalizer(getManager(),
                              Utils.lookupChild(self, "field", "ident", ident));
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Element                 self;
}
