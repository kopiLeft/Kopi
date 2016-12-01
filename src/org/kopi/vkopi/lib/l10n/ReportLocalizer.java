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
 * Implements a report localizer.
 */
public class ReportLocalizer extends Localizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             manager         the manager to use for localization
   * @param             document        the document containing the report localization
   */
  public ReportLocalizer(LocalizationManager manager, Document document) {
    super(manager);
    this.root = document.getRootElement();
    if (! root.getName().equals("report")) {
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
   * Returns the value of the help attribute.
   */
  public String getHelp() {
    return root.getAttributeValue("help");
  }

  /**
   * Constructs a field localizer for the given field.
   *
   * @param             ident           the identifier of the field
   */
  public FieldLocalizer getFieldLocalizer(String ident) {
    return new FieldLocalizer(getManager(),
                              Utils.lookupChild(root, "field", "ident", ident));
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final Element                 root;
}
