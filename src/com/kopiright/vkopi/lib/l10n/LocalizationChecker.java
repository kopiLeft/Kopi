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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kopiright.util.base.InconsistencyException;

/**
 * This class checks a localization file against a model
 */
public class LocalizationChecker {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor.
   *
   * @param             model           the localization model
   * @param             model           the localization condidate
   */
  public LocalizationChecker(String model, String candidate) {
    this.modelDoc = loadDocument(model);
    this.candidateDoc = loadDocument(candidate);

    initWriter();
  }

  /**
   * Initializes the XML writer.
   */
  private void initWriter() {
    Format              format;

    format = Format.getPrettyFormat();
    format.setEncoding("UTF-8");
    format.setLineSeparator("\n");
    writer = new XMLOutputter(format);
  }
  
  // ----------------------------------------------------------------------
  // MAIN
  // ----------------------------------------------------------------------

  /**
   * Entry point.
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      fail("Usage: java com.kopiright.vkopi.lib.l10n.LocalizationChecker <model> <candidate>");
    }
    LocalizationChecker checker;

    checker = new LocalizationChecker(args[0], args[1]);
    
    checker.run(args[1] + ".gen");
    
  }
  
  /**
   * Runs the check process.
   *
   * @param             outFile         name of the file to generate
   */
  private void run(String outFile) {
    
    check();
    if (modified) {
      write(outFile);      
      System.out.println("Candidate file not compatible,"
                         + " new auto-genrated one: " + outFile );
    } else {
      System.out.println("Candidate file OK.");
    }
  }

  /**
   * Checks that the condidate document matches the model,
   * otherwise and generates a new valid one.
   */
  private void check() {
    Element model;
    Element candidate;
    Element validated;
    
    model = modelDoc.getRootElement();
    candidate = candidateDoc.getRootElement();
    
    // check we have the same root name
    if(! model.getName().equals(candidate.getName())) {
      fail("The given documents doen't have the same root element");
    }
    
    validated = checkAttributes(model, candidate);
    
    checkChildren(model, candidate, validated);
    
    validatedDoc = new Document(validated);
  }
  
  /**
   * Checks the children of the given elements.
   *
   * @param             model           the model element
   * @param             candidate       the candidate element
   * @param             validated       the element to generate
   */
  private void checkChildren(Element model, Element candidate, Element validated) {
    List        children;
    
    children = model.getChildren();
    for (Iterator i = children.iterator(); i.hasNext(); ) {
      Element   em; // model
      Element   ec; // candidate
      Element   ev; // validated

      em = (Element)i.next();
      // elements with no ident
      if (getIdentOf(em.getName()) == null) {
        ec = Utils.lookupChild(candidate, em.getName());
        ec.detach();
      } else {
        try {
          ec = Utils.lookupChild(candidate,
                                 em.getName(),
                                 getIdentOf(em.getName()),
                                 em.getAttributeValue(getIdentOf(em.getName())));
          ec.detach();
        } catch (InconsistencyException e) {
          ec = null;
        }
      }
      ev = checkAttributes(em, ec);
      validated.addContent(ev);
      checkChildren(em, ec, ev);
    }
    if (candidate != null
        && candidate.getChildren().size() != 0) {
      modified = true;
      children = candidate.getChildren();
      for (Iterator i = children.iterator(); i.hasNext(); ) {
        Element e;
        
        e = (Element)i.next();
        validated.addContent(toComment(e));
      }
    }
  }
  
  /**
   * Constructs a comment content from an element.
   *
   * @param             element         the element to comment
   * @return            the constructed comment
   */
  private Comment toComment(Element element) {
    return new Comment(" " +  writer.outputString(element) + " ");
  }

  /**
   * Checks the attributes of the given elements and return a valid one.
   * 
   * @param             model           the model element
   * @param             candidate       the candidate element
   * @return            a valid element
   */
  private Element checkAttributes(Element model, Element candidate) {
    Element     e;
    List        attributes;
    
    e = new Element(model.getName());
    attributes = model.getAttributes();
    for (Iterator i = attributes.iterator(); i.hasNext(); ) {
      Attribute am; // model
      Attribute ac; // candidate
      
      am = (Attribute)i.next();
      if (candidate != null 
          && (ac = candidate.getAttribute(am.getName())) != null) {
        e.setAttribute((Attribute)ac.clone());
        // remove this attribute
        ac.detach();
      } else {
        modified = true;
        e.setAttribute(am.getName(),
                       (am.getName().equals(getIdentOf(e.getName()))? "" : "!!! ") + am.getValue());
      }
    }
    
    // test if the candidate still have attributes
    if (candidate == null || candidate.getAttributes().size() != 0) {
      modified = true;
    }
      
    return e;
  }
  
  /**
   * Writes the XML tree to the specified file.
   *
   * @param             fileName                the name of the file
   */
  private void write(String fileName) {
    try {
      writer.output(validatedDoc, new FileOutputStream(new File(fileName)));
    } catch(IOException e) {
      fail("cannot write file " + fileName + ": " + e.getMessage());
    }
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  
  /**
   * Used on a failure.
   *
   * @param             msg             the error message to output
   */
  public static void fail(String msg) {
    System.err.println(msg);
    System.exit(1);
  }
  
  /**
   * Retruns the attribute used for the identification of an element.
   *
   * @param             name            the name of the element
   * @return            the name of the identifier attribute
   */
  private String getIdentOf(String name) {
    return (String)idents.get(name);
  }

  // ----------------------------------------------------------------------
  // FILE HANDLING
  // ----------------------------------------------------------------------

  /**
   * Loads the XML document.
   *
   * @param             fileName          the name of the file.
   * @return            the document associated to the given file
   */
  private Document loadDocument(String fileName) {
    SAXBuilder  builder;
    Document    document;

    builder = new SAXBuilder();
    try {
      document = builder.build(new File(fileName));
    } catch (Exception e) {
      document = null;
      fail("Cannot load file " + fileName + ": " + e.getMessage());
    }
    
    return document;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private Document                      modelDoc;
  private Document                      candidateDoc;
  private Document                      validatedDoc;
  private String                        candidateName;
  private boolean                       modified = false;
  private XMLOutputter                  writer;

  private static Hashtable              idents = new Hashtable();
  
  static {
    // sets the ident name of each element
    idents.put("actor",    "ident");
    idents.put("block",    "name");
    idents.put("codedesc", "ident");
    idents.put("field",    "ident");
    idents.put("index",    "ident");
    idents.put("listdesc", "column");
    idents.put("menu",     "ident");
    idents.put("page",     "ident");
    idents.put("type",     "ident");
  }
}
