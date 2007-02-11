/*
 * Created on Feb 7, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
	
  static org.jdom.Document document;
  
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
	 
}


