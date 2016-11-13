/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.util.html;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.kopi.util.base.InconsistencyException;

public class HtmlDocument extends Element {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public HtmlDocument() {
    super("html");
    
    Element     head;
    
    head = new Element("head");
    meta = new Element("meta");
    body = new Element("body");
    addContent(head);
    addContent(body);
    head.addContent(meta);
    meta.setAttribute("http-equiv", "content-type");
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Sets the charset to be used within this document.
   */
  public void setCharset(String charset) {
    meta.setAttribute("content", "text/html; charset=" + charset);
  }
  
  /**
   * Sets the background color of this document.
   */
  public void setBackgroundColor(String color) {
    body.setAttribute("bgcolor", color);
  }
  
  /**
   * Sets the foreground color of this document.
   */
  public void setForegroundColor(String color) {
    body.setAttribute("text", color);
  }
  
  /**
   * Adds a table to this document.
   */
  public void addTable(HtmlTable table) {
    body.addContent(table);
  }
  
  /**
   * Adds a br HTML tag to the body of this document.
   */
  public void addBreak() {
    body.addContent(new Element("br"));
  }
  
  /**
   * Returns the string representation of this document.
   */
  public String toString() {
    try {
      XMLOutputter                outputter;
      StringWriter                writer;

      outputter = new XMLOutputter(Format.getPrettyFormat());
      writer = new StringWriter();
      outputter.output(this, writer);
      writer.close();

      return writer.toString();
    } catch (IOException e) {
      throw new InconsistencyException(e);
    }
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final Element                         meta;
  private final Element                         body;
}
