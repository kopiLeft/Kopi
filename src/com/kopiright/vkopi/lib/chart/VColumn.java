/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.chart;

import java.io.Serializable;

import com.kopiright.vkopi.lib.l10n.ChartLocalizer;
import com.kopiright.vkopi.lib.l10n.FieldLocalizer;

@SuppressWarnings("serial")
public abstract class VColumn implements Serializable {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /**
   * Creates a new chart column from its identifier.
   * @param ident The column identifier.
   */
  protected VColumn(String ident) {
    this.ident = ident;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  
  /**
   * Returns the column identifier.
   * @return The column identifier.
   */
  public String getIdent() {
    return ident;
  }
  
  /**
   * Sets the column ident.
   * @param ident The column ident to be set.
   */
  public void setIdent(String ident) {
    this.ident = ident;
  }
  
  /**
   * Returns the column label.
   * @return The column label.
   */
  public String getLabel() {
    return label;
  }
  
  /**
   * Sets the column label.
   * @param label The column label.
   */
  public void setLabel(String label) {
    this.label = label;
  }
  
  /**
   * Returns the column help.
   * @return The column help.
   */
  public String getHelp() {
    return help;
  }
  
  /**
   * Sets the column help.
   * @param help The column help.
   */
  public void setHelp(String help) {
    this.help = help;
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Generates the help for this column.
   * @param help The help generator.
   */
  public void helpOnColumn(VHelpGenerator help) {
    help.helpOnColumn(label, this.help);
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  public void localize(ChartLocalizer parent) {
    if (! ident.equals("")){
      FieldLocalizer      	loc;
 
      loc = parent.getFieldLocalizer(ident);
      label = loc.getLabel();
      help = loc.getHelp();
      localize(loc);
    }
  }

  /**
   * Localizes this column
   *
   * @param     parent         the caller localizer
   */
  protected void localize(FieldLocalizer loc) {
    // by default nothing to do
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private String					ident;
  private String					label;
  private String					help;
}
