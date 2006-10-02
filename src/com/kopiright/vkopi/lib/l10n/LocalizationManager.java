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
import java.util.Hashtable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kopiright.util.base.InconsistencyException;

/**
 * Implements a localization manager.
 */
public class LocalizationManager {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param             locale          the locale used for localization management
   */
  public LocalizationManager(Locale locale) {
    this.locale = locale;
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the locale managed by this class.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Constructs a form localizer using the specified source.
   *
   * @param             source          the source qualified name
   */
  public FormLocalizer getFormLocalizer(String source) {
    return new FormLocalizer(getDocument(source));
  }

  /**
   * Constructs a block localizer using the specified source.
   *
   * @param             source          the source qualified name
   * @param             name            the identifier of the block
   */
  public BlockLocalizer getBlockLocalizer(String source, String name) {
    return new BlockLocalizer(this, getDocument(source), name);
  }

  /**
   * Constructs an actor localizer using the specified source.
   *
   * @param             source          the source qualified name
   * @param             name            the identifier of the actor
   */
  public ActorLocalizer getActorLocalizer(String source, String name) {
    return new ActorLocalizer(getDocument(source), name);
  }
  
  /**
   * Constructs a menu localizer using the specified source.
   *
   * @param             source          the source qualified name
   * @param             name            the identifier of the menu
   */
  public MenuLocalizer getMenuLocalizer(String source, String name) {
    return new MenuLocalizer(getDocument(source), name);
  }

  /**
   * Constructs a list localizer using the specified source.
   *
   * @param             source          the source qualified name
   * @param             name            the identifier of the list
   */
  public ListLocalizer getListLocalizer(String source, String name) {
    return new ListLocalizer(this, getDocument(source), name);
  }

  /**
   * Constructs a type localizer using the specified source.
   *
   * @param             source          the source qualified name
   * @param             name            the identifier of the type
   */
  public TypeLocalizer getTypeLocalizer(String source, String name) {
    return new TypeLocalizer(this, getDocument(source), name);
  }

  /**
   * Constructs a report localizer using the specified source.
   *
   * @param             source          the source qualified name
   */
  public ReportLocalizer getReportLocalizer(String source) {
    return new ReportLocalizer(this, getDocument(source));
  }

  // ----------------------------------------------------------------------
  // FILE HANDLING
  // ----------------------------------------------------------------------

  /**
   * Returns the document manging the given source.
   *
   * @param             source          the source qualied name
   */
  private Document getDocument(String source) {
    if (! documents.contains(source)) {
      documents.put(source, loadDocument(source));
    }
    return (Document)documents.get(source);
  }

  /**
   * Loads the XML document with specified qualified name in current locale.
   *
   * @param     source          the qualified name of the document.
   */
  private Document loadDocument(String source) {
    String      fileName;
    SAXBuilder  builder;
    Document    document;

    //!!!FIX:taoufik locale.toString() will probably output 'de' instead of 'de_AT'
    fileName = source.replace('.', '/') + "-" + locale.toString() + ".xml";
    builder = new SAXBuilder();
    try {
      document = builder.build(LocalizationManager.class.getClassLoader().getResourceAsStream(fileName));
    } catch (Exception e) {
      throw new InconsistencyException("Cannot load file " + fileName + ": " + e.getMessage());
    }
    return document;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final Hashtable       documents = new Hashtable();
  private final Locale          locale;
}
