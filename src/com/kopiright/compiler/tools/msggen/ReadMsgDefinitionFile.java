
package com.kopiright.compiler.tools.msggen;
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;

import java.util.List;
import java.util.Iterator;

import com.kopiright.util.base.InconsistencyException;

public class ReadMsgDefinitionFile {
  
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
  
  static org.jdom.Document document;
}
