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
 * $Id: ReadOptDefinitionFile.java 27828 2007-02-11 12:03:44Z wael $
 */

package com.kopiright.compiler.tools.optgen;
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;

import com.kopiright.util.base.InconsistencyException;

import java.util.List;
import java.util.Iterator;
public class ReadOptDefinitionFile {
	
  /**
   * Reads and parses xml definition file
   *
   * @param     sourceFile              the name of the source file
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
    String version =root.getAttributeValue("version");
    String usage =root.getAttributeValue("usage");
    OptionDefinition[] options = getOptions(root);
    	 		 
    file = new DefinitionFile(sourceFile,fileHeader,packageName,parent,prefix,version,usage,options);
    return file;
  }
  /**
   * Reads options from the xml definition file
   *
   * @param     e      the xml root element
   * @return    a class info structure holding the information from the source
   *
   */
  public static OptionDefinition[] getOptions(Element e ){
    String longname ;
    String shortname;
    String type;
    String defaultValue;
    String argument;
    String help   ;
    List params = e.getChildren("param");
    OptionDefinition[] options = new OptionDefinition[ params.size()];
    Iterator i = params.iterator();
    int j =0;
    while(i.hasNext()) {
      Element current = (Element)i.next();
      longname = current.getAttributeValue("longname");
      shortname = current.getAttributeValue("shortname");
      type = current.getAttributeValue("type");
      defaultValue = current.getAttributeValue("default");
      help = current.getAttributeValue("help");  
      argument = null; 
      if( current.getAttributeValue("optionalDefault")== null) {
        if(!type.equals("boolean")){ 
	  argument = ""; 
	}
      }
      else {
        argument=current.getAttributeValue("optionalDefault");	 
      }
      options[j] = new OptionDefinition(longname,shortname,type,defaultValue,argument,help);
      j++; 
    }
    return options;
  }
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  static org.jdom.Document document;	 
}


