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

import org.jdom.Element;

/**
 * Implements a field localizer.
 */
public class FieldLocalizer extends Localizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             manager         the manager to use for localization
   * @param             self            the field element
   */
  public FieldLocalizer(LocalizationManager manager, Element self) {
    super(manager);
    this.self = self;
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

  /**
   * Returns the value of the help attribute.
   */
  public String getHelp() {
    return self.getAttributeValue("help");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Element                 self;
}
