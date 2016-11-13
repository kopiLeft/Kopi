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

package org.kopi.vkopi.comp.report;

import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKLocalizationWriter;

import org.jdom.Element;

/**
 * This class implements an  XML localization file generator
 */
public class VRReportLocalizationWriter extends VKLocalizationWriter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VRReportLocalizationWriter() {
    super();
  }

  // ----------------------------------------------------------------------

  /**
   * 
   */
  public void genReport(String title,
                        String help,
                        VKDefinitionCollector coll,
                        VRField[] fields)
  {
    Element     self;

    self = new Element("report");
    self.setAttribute("title", title);
    if (help != null) {
      self.setAttribute("help", help);
    }
    pushNode(self);
    coll.genLocalization(this);
    for (int i = 0; i < fields.length; i++) {
      fields[i].genLocalization(this);
    }
    // do not pop: this is the root element
  }


  // ----------------------------------------------------------------------


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
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
 

}
