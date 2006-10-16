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

import org.jdom.Element;
import org.jdom.JDOMException;

import com.kopiright.util.base.InconsistencyException;

/**
 * Defines methods used for localization.
 */
public class Utils {

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  /**
   * Returns the child with specified type and attribute = value.
   */
  public static Element lookupChild(Element parent,
                                    String type,
                                    String attribute,
                                    String value)
  {
    List        childs;
    
    childs = parent.getChildren(type);
    for (Iterator i = childs.iterator(); i.hasNext(); ) {
      Element   e;

      e = (Element)i.next();
      if (e.getAttributeValue(attribute) != null
          && e.getAttributeValue(attribute).equals(value)) {
        return e;
      }
    }
    throw new InconsistencyException(parent.getDocument().getBaseURI() + ": "
                                     + type + " " + attribute + " = " + value + " not found");
  }

  /**
   * Returns the child with specified type.
   */
  public static Element lookupChild(Element parent, String type) {
    List        childs;
    
    childs = parent.getChildren(type);
    if (childs.size() == 0) {
      throw new InconsistencyException(parent.getDocument().getBaseURI() + ": "
                                       + type + " not found");
    } else if (childs.size() > 1) {
      throw new InconsistencyException(parent.getDocument().getBaseURI() + ": "
                                       + type + " not unique");
    } else {
      return (Element)childs.get(0);
    }
  }
}
