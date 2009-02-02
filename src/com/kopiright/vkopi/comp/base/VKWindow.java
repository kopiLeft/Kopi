/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import java.util.Vector;

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents the definition of a form
 */
public abstract class VKWindow extends VKPhylum implements VKCompilationUnit, com.kopiright.vkopi.lib.form.VConstants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   * @param title		the title of this form
   * @param superName		the type of the form
   */
  public VKWindow(TokenReference where,
 		  CParseCompilationUnitContext cunit,
		  CParseClassContext classContext,
		  VKDefinitionCollector coll,
		  String title,
		  String locale,
		  CReferenceType superWindow,
		  CReferenceType[] interfaces,
		  int options,
		  VKCommand[] commands,
		  VKTrigger[] triggers)
  {
    super(where);

    this.title = title;
    this.locale = locale;
    this.cunit = cunit;
    this.classContext = classContext;
    this.coll = coll;
    this.superWindow = superWindow;
    this.interfaces = interfaces;
    this.commands = commands;
    this.triggers = triggers;
    // use base name, remove extension
    this.ident = getTokenReference().getName().substring(0, getTokenReference().getName().lastIndexOf('.'));

    verify(coll != null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * !!!FIX
   */
  public String getLocale() {
    return locale;
  }

  /**
   * Returns a collector for definitiion
   */
  public VKDefinitionCollector getDefinitionCollector() {
    return coll;
  }

  /**
   * Returns the name
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Returns the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the super window name
   */
  public CReferenceType getSuperWindow() {
    return superWindow;
  }

  /**
   * Returns the super window name
   */
  public CReferenceType[] getInterfaces() {
    return interfaces;
  }

  /**
   * Returns the commands of this window
   */
  public VKCommand[] getCommands() {
    return commands;
  }

  /**
   * Returns the commands of this window
   */
  public VKTrigger[] getTriggers() {
    return triggers;
  }

  /**
   * Returns the options of this window
   */
  public int getOptions() {
    return options;
  }

  /**
   * Returns the triggerArray
   */
  public Commandable getCommandable() {
    return commandable;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Returns the full name
   */
  public String getFullName() {
    return cunit.getPackageName().getName() + "/" + getIdent();
  }

  /**
   * Check visual code is correct
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    checkLocale(context);

    context.setMode(true);
    context.setClassContext(getClassContext());
    context.setFullName(getFullName());

    try {
      getDefinitionCollector().checkInsert(context.getTopLevel());
    } catch (com.kopiright.compiler.base.UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    getDefinitionCollector().checkCode(context);

    commandable = new Commandable(null);

    commandable.checkCode(context,
			  getDefinitionCollector(),
			  commands,
			  triggers);
  }

  /**
   * check if the locale is set properly
   * !!! move to Utils
   */
  private void checkLocale(VKContext context) throws PositionedError {
    if (getLocale() != null) {
      char[]    chars = getLocale().toCharArray();
      
      if(chars.length != 5
         || chars[0] < 'a' || chars[0] > 'z'
         || chars[1] < 'a' || chars[1] > 'z'
         || chars[2] != '_'
         || chars[3] < 'A' || chars[3] > 'Z'
         || chars[4] < 'A' || chars[4] > 'Z'
         ) {
        context.reportTrouble(new CWarning(getTokenReference(),
                                           BaseMessages.BAD_LOCALE_DEFINITION,
                                           getLocale()));
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public abstract JCompilationUnit genCode(Compiler compiler);

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void genInit(Vector body) {
    commandable.genCode(getTokenReference(), body, true, true);
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public abstract void genVKCode(String destination, TypeFactory factory);

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // PROTECTED METHODS
  // ----------------------------------------------------------------------

  protected CParseCompilationUnitContext getCompilationUnitContext() {
    return cunit;
  }

  protected CParseClassContext getClassContext() {
    return classContext;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CParseClassContext            classContext;
  private VKDefinitionCollector		coll;
  private CParseCompilationUnitContext	cunit;

  private String                        ident;
  private String                        title;
  private String                        locale;
  private CReferenceType[]		interfaces;
  private CReferenceType		superWindow;
  private int                           options;
  private VKCommand[]                   commands;
  private VKTrigger[]                   triggers;
  private Commandable                   commandable;
}
