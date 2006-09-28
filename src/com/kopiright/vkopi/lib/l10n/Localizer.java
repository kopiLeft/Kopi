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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kopiright.util.base.InconsistencyException;

public class Localizer {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  public Localizer(String source, Locale locale) {
    String      fileName;
    SAXBuilder  builder;
    Document    document;

    //!!!TEST
    System.err.println("*** " + locale);
    //!!!TEST
    //!!!FIX:taoufik locale.toString() will probably output 'de' instead of 'de_AT'
    fileName = source.replace('.', '/') + "-" + locale.toString() + ".xml";
    builder = new SAXBuilder();
    try {
      document = builder.build(Localizer.class.getClassLoader().getResourceAsStream(fileName));
    } catch (Exception e) {
      //!!!FIX:taoufik exception handling
      throw new InconsistencyException("Cannot load file " + fileName + ": " + e.getMessage());
    }
    this.root = document.getRootElement();
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Returns the child with specified type and attribute = value.
   */
  public Element lookupChild(Element parent,
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
  
  private final Element         root;
}

  /**
   * //!!!FIX:taoufik
  private void initLocalization(String source) {
    List        blockList;
    SAXBuilder  builder = new SAXBuilder();
    Document    localization;
  
    //!!!FIX:taoufik locale.toString() will probably output 'de' instead of 'de_AT'
    this.fileName =  source + "-" + this.locale.toString() + ".xml";
    
    try {
      localization = builder.build(FormLocalizationManager.class.getClassLoader().getResourceAsStream(this.fileName));
    } catch (Exception e) {
      //!!!FIX:taoufik more specific catched exception
      //!!!FIX:taoufik execption handling
      System.err.println("Cannot load file " + this.fileName);
      return;
    }
    
    this.root = localization.getRootElement();
    addManager(source, this);
  }
  
  
  **
   * Returns the source managed by this class.
   *
  public String getSource() {
    return source;
  }

  **
   * //!!!FIX:taoufik
   *
  public Element getRoot() {
    return root;
  }

  **
   *
   *
  public String getFileName() {
    return fileName;
  }

  // ----------------------------------------------------------------------
  // 
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final String                  source;
  private final Locale                  locale;
  private String                        fileName;

  private static final Hashtable        managers = new Hashtable();
}
  */
