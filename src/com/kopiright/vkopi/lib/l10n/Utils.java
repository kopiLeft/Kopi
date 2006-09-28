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
 * //!!!FIX:taoufik
 */
public class Utils {


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
      if (e.getAttributeValue(attribute).equals(value)) {
        return e;
      }
    }
    throw new InconsistencyException(type + " " + attribute + " = " + value + " not found");
  }

}
