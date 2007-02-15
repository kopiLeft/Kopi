/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */
package com.kopiright.compiler.tools.msggen;
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;

import java.util.List;
import java.util.Iterator;

import com.kopiright.util.base.InconsistencyException;

public class ReadMsgDefinitionFile {
  
  /**
   * Reads and parses xml definition file
   *
   * @param     sourceFile              the name of the xml source file
   * @return    a class info structure holding the information from the source
   *
   */

  public static DefinitionFile read(String sourceFile)  {
    DefinitionFile file = null;  
    SAXBuilder sxb = new SAXBuilder();
    try {
      document = sxb.build(new File(sourceFile));
    } 
    catch (Exception e) {
      throw new InconsistencyException("Cannot load file " + sourceFile + ": " + e.getMessage());
    }
    Element root = document.getRootElement();	
    String fileHeader =root.getAttributeValue("fileHeader");	
    String packageName =root.getAttributeValue("package");
    String parent =root.getAttributeValue("parent");	
    String prefix =root.getAttributeValue("prefix");	
    MessageDefinition[] messages = getMessages(root);
    file = new DefinitionFile(sourceFile,fileHeader,packageName,prefix,parent,messages);
    return file;
  }

  /**
   * Reads options from the xml definition file
   *
   * @param     e      the xml root element.
   * @return    a MessageDefinition class holding the information from the source
   *
   */  

  public static MessageDefinition[] getMessages(Element e ){
    List msg = e.getChildren("msg");
    String identifier;
    String format;
    String reference;
    int level;
    MessageDefinition[] messages = new MessageDefinition[ msg.size()];
    Iterator i = msg.iterator();
    int j =0;
    while(i.hasNext()) {
      Element current = (Element)i.next();
      identifier = current.getAttributeValue("identifier");
      format = current.getAttributeValue("format");
      reference= current.getAttributeValue("reference");
      level = Integer.parseInt(current.getAttributeValue("level"));
      messages[j] = new MessageDefinition(identifier,format, reference, level);	         
      j++; 
    }
    return messages;
  }
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------  
  static org.jdom.Document document;
}
