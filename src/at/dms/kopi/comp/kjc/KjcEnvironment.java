/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.kopi.comp.kjc;

import at.dms.util.base.InconsistencyException;

/**
 * Environment of the Kjc Compiler
 */

public class KjcEnvironment {

  public KjcEnvironment(ClassReader classReader, 
                        TypeFactory typeFactory, 
                        KjcOptions options) {
    this.classReader = classReader;
    this.typeFactory = typeFactory;
    this.options = options;
    this.languageExtensions = new LanguageExtensions();
    this.deprecatedUsed = false;

    if (options.assertion.equals("simple")) {
      this.assertion = AS_SIMPLE; 
    } else if(options.assertion.equals("all")){
      this.assertion = AS_ALL; 
    } else {
      this.assertion = AS_NONE; 
    }
  }

  // ---------------------------------------------------------
  // static environment
  // ---------------------------------------------------------

  public ClassReader getClassReader() {
    return classReader;
  }

  public TypeFactory getTypeFactory() {
    return typeFactory;
  }

  public SignatureParser getSignatureParser() {
    return classReader.getSignatureParser();
  }

  public LanguageExtensions getLanguageExtFactory() {
    return languageExtensions;
  }


  public int getSourceVersion() {
    if (options.source.equals("1.1")) {
      return SOURCE_1_1;
    } else if (options.source.equals("1.2")) {
      return SOURCE_1_2;
    } else if (options.source.equals("1.3")) {
      return SOURCE_1_3;
    } else if (options.source.equals("1.4")){
      return SOURCE_1_4;
    } else if (options.source.equals("1.5")){
      return SOURCE_1_5;
    } else {
      throw new InconsistencyException("Wrong source language in options");
    }
  }

  public int getAssertExtension() {
    return assertion;
  }
  public boolean isGenericEnabled() {
    return getSourceVersion() >= SOURCE_1_5;
  }
  public boolean showDeprecated() {
    return options.deprecation;
  }
  public boolean ignoreUnreachableStatement() {
    return false;
  }
  // ---------------------------------------------------------
  // dynamic environment
  // ---------------------------------------------------------

  /**
   * calles if somewhere anything deprecated is used.
   */
  public void setDeprecatedUsed() {
    deprecatedUsed = true;
  }

  /**
   * returns true if somewhere anything deprecated is used.
   */
  public boolean isDeprecatedUsed() {
    return deprecatedUsed;
  }

  // ---------------------------------------------------------
  // instance variables
  // ---------------------------------------------------------

  private final ClassReader             classReader;
  private final TypeFactory             typeFactory;
  private final KjcOptions              options;
  private final int                     assertion;
  private final LanguageExtensions      languageExtensions;

  private boolean                       deprecatedUsed;

  public static final int               AS_NONE   = 0;
  public static final int               AS_SIMPLE = 1;
  public static final int               AS_ALL    = 2;

  public static final int               SOURCE_1_1 = 101;
  public static final int               SOURCE_1_2 = 102;
  public static final int               SOURCE_1_3 = 103;
  public static final int               SOURCE_1_4 = 104;
  public static final int               SOURCE_1_5 = 105;
}
