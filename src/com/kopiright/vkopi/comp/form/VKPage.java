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

package com.kopiright.vkopi.comp.form;


import com.kopiright.compiler.base.TokenReference;
import com.kopiright.vkopi.comp.base.VKLocalizationWriter;
import com.kopiright.vkopi.comp.base.VKPhylum;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;

/**
 * 
 */
public class VKPage extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param ident		the identifier of the page
   * @param title		the page title in default locale
   */
  public VKPage(TokenReference where, String ident, String title) {
    super(where);

    this.ident = ident;
    this.title = title;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the identifier of this page.
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Returns the title to display for this page.
   */
  public String getTitle() {
    return title;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    ((VKFormPrettyPrinter)p).printPage(ident, title);
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX:taoufik
   */
  public void genLocalization(VKLocalizationWriter writer) {
    ((VKFormLocalizationWriter)writer).genPage(ident, title);
  }
  
  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private final String          ident;
  private final String          title;
}
