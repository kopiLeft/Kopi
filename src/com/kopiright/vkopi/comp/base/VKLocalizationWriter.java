/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kopiright.util.base.InconsistencyException;

/**
 * This class 
 */
public class VKLocalizationWriter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a localization writer unit
   */
  public VKLocalizationWriter() {
  }

  /**
   * Writes the XML tree to the specified file.
   *
   * @param     directory       the directory where the localization file should be created
   * @param	baseName        the base name of the file to open
   * @param     locale          the locale
   */
  public void write(String directory, String baseName, String locale)
    throws IOException
  {
    Document            doc;
    XMLOutputter        writer;
    Format              format;
    String              fileName;
    
    doc = new Document(peekNode(null));
    format = Format.getPrettyFormat();
    format.setEncoding("UTF-8");
    format.setLineSeparator("\n");
    writer = new XMLOutputter(format);
    fileName = baseName + "-" + locale + ".xml";
    writer.output(doc, new FileOutputStream(new File(directory, fileName)));
  }

  // -------------------------------------------------------------------

  /**
   * Creates localization for a menu.
   */
  public void genInsert(VKDefinitionCollector coll) {
    Element     self;

    self = new Element("insert");
    pushNode(self);
    coll.genLocalization(this);
    // do not pop: this is the root element
  }

  /**
   * Creates localization for a menu.
   */
  public void genMenuDefinition(String ident, String label) {
    Element     self;

    self = new Element("menu");
    self.setAttribute("ident", ident);
    self.setAttribute("label", label);
    peekNode(null).addContent(self);
  }

  /**
   * Creates localization for an actor.
   */
  public void genActorDefinition(String ident, String label, String help) {
    Element     self;

    self = new Element("actor");
    self.setAttribute("ident", ident);
    self.setAttribute("label", label);
    if (help != null) {
      self.setAttribute("help", help);
    }
    peekNode(null).addContent(self);
  }

  /**
   *
   */
  public void genTypeDefinition(String ident, VKType type) {
    Element     self;

    self = new Element("type");
    self.setAttribute("ident", ident);
    pushNode(self);
    type.genLocalization(this);
    popNode(self);
    peekNode(null).addContent(self);
  }

  /**
   *
   */
  public void genType(VKFieldList list) {
    if (list != null) {
      list.genLocalization(this);
    }
  }

  /**
   *
   */
  public void genCodeType(VKCodeDesc[] codes) {
    Element     self;

    self = new Element("code");
    pushNode(self);
    for (int i = 0; i < codes.length; i++) {
      codes[i].genLocalization(this);
    }
    popNode(self);
    peekNode("type").addContent(self);
  }

  /**
   *
   */
  public void genCodeDesc(String ident, String label) {
    Element     self;

    self = new Element("codedesc");
    self.setAttribute("ident", ident);
    self.setAttribute("label", label);
    peekNode("code").addContent(self);
  }

  /**
   *
   */
  public void genFieldList(VKListDesc[] columns) {
    Element     self;

    self = new Element("list");
    pushNode(self);
    for (int i = 0; i < columns.length; i++) {
      columns[i].genLocalization(this);
    }
    popNode(self);
    peekNode("type").addContent(self);
  }

  /**
   *
   */
  public void genListDesc(String column, String title) {
    Element     self;

    self = new Element("listdesc");
    self.setAttribute("column", column);
    self.setAttribute("title", title);
    peekNode("list").addContent(self);
  }

  // -------------------------------------------------------------------
  // ELEMENT STACK
  // -------------------------------------------------------------------

  protected void pushNode(Element node) {
    currentHierarchy.push(node);
  }

  protected void popNode(Element expected) {
    Element     actual;

    actual = (Element)currentHierarchy.pop();
    if (expected != null && !actual.equals(expected)) {
      throw new InconsistencyException();
    }
  }

  protected Element peekNode(String expected) {
    Element     top;

    top = (Element)currentHierarchy.peek();
    if (expected != null && !top.getName().equals(expected)) {
      throw new InconsistencyException();
    }
    return top;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  private Stack                 currentHierarchy = new Stack();
}
