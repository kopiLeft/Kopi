/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;

/**
 * An enumeration editor field field for grid inline edit 
 */
public class VEditorEnumField extends VEditorTextField {
  
  @Override
  public EditorEnumFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorEnumFieldConnector.class);
  }
  
  @Override
  protected boolean isEnum() {
    return true;
  }
  
  @Override
  protected boolean check(String text) {
    if (enumerations != null && text != null) {
      String    s = text.toLowerCase();

      for (int i = 0; i < enumerations.length; i++) {
        if (enumerations[i].toLowerCase().startsWith(s)) {
          return true;
        }
      }
      
      return false;
    }
    
    return true;
  }
  
  @Override
  public void validate() throws InvalidEditorFieldException {
    /*
     * -1:  no match
     * >=0: one match
     * -2:  two (or more) matches: cannot choose
     */
    int       found = -1;
    String    newText;

    newText = getText().toLowerCase();
    for (int i = 0; found != -2 && i < enumerations.length; i++) {
      if (enumerations[i].toLowerCase().startsWith(newText)) {
        if (enumerations[i].toLowerCase().equals(newText)) {
          found = i;
          break;
        }
        if (found == -1) {
          found = i;
        } else {
          found = -2;
        }
      }
    }
    switch (found) {
    case -1:  /* no match */
      throw new InvalidEditorFieldException(this, "00001");
    case -2:  /* two (or more) exact matches: cannot choose */
      // show the suggestions list
      
      break;
    default:
      setText(enumerations[found]);
    }
  }
  
  /**
   * Sets the possible values of this enumertaion field.
   * @param enumerations The field enumerations.
   */
  public void setEnumerations(String[] enumerations) {
    this.enumerations = enumerations;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private String[]                              enumerations;
}
