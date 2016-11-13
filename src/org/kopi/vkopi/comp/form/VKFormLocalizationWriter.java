/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.comp.form;

import org.jdom.Element;

import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKLocalizationWriter;

/**
 * This class implements an  XML localization file generator
 */
public class VKFormLocalizationWriter extends VKLocalizationWriter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct 
   * @param	fileName
   */
  public VKFormLocalizationWriter() {
    super();
  }

  // ----------------------------------------------------------------------

  /**
   *
   */
  public void genForm(String title,
                      VKDefinitionCollector coll,
                      VKPage[] pages,
                      VKFormElement[] blocks)
  {
    Element     self;

    self = new Element("form");
    self.setAttribute("title", title);
    pushNode(self);
    coll.genLocalization(this);
    for (int i = 0; i < pages.length; i++) {
      pages[i].genLocalization(this);
    }
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].genLocalization(this);
    }
    // do not pop: this is the root element
  }

  /**
   *
   */
  public void genBlockInsert(VKDefinitionCollector coll,
                             VKBlock block)
  {
    Element     self;

    self = new Element("blockinsert");
    pushNode(self);
    coll.genLocalization(this);
    block.genLocalization(this);
    // do not pop: this is the root element
  }

  // ----------------------------------------------------------------------

  /**
   *  FIX:taoufik
   */
  public void genPage(String ident, String title) {
    Element             self;

    self = new Element("page");
    self.setAttribute("ident", ident);
    self.setAttribute("title", title);
    peekNode(null).addContent(self);
  }

  /**
   *  FIX:taoufik
   *  !!!FIX:taoufik handle NEW PAGE
   */
  public void genBlock(String name,
                       String title,
                       String help,
                       VKBlockIndex[] indices,
                       VKField[] fields)
  {
    Element             self;

    self = new Element("block");
    self.setAttribute("name", name);
    if (title != null) {
      self.setAttribute("title", title);
    }
    if (help != null) {
      self.setAttribute("help", help);
    }
    pushNode(self);
    for (int i = 0; i < indices.length; i++) {
      indices[i].genLocalization(this);
    }
    for (int i = 0; i < fields.length; i++) {
      fields[i].genLocalization(this);
    }
    popNode(self);
    peekNode(null).addContent(self);
  }

  public void genBlockIndex(String ident, String message) {
    Element             self;

    self = new Element("index");
    self.setAttribute("ident", ident);
    self.setAttribute("message", message);
    peekNode(null).addContent(self);
  }

  /**
   * !!!FIX:taoufik
   */
  public void genField(String ident, String label, String help) {
    Element     self;
    
    self = new Element("field");
    self.setAttribute("ident", ident);
    if (label != null) {
      self.setAttribute("label", label);
    }
    if (help != null) {
      self.setAttribute("help", help);
    }
    peekNode(null).addContent(self);
  }
   
}
