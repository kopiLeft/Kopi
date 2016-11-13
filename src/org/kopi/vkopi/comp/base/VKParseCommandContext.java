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

package org.kopi.vkopi.comp.base;

import java.util.Vector;

import org.kopi.util.base.Utils;

/**
 * A parse command context needed to hold command triggers.
 */
public class VKParseCommandContext {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /**
   * Creates a new parse command context.
   */
  public VKParseCommandContext() {
    triggers = new Vector();
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  /**
   * Adds a trigger to this parse command context.
   * @param trigger The trigger to be added.
   */
  public void addTrigger(VKTrigger trigger) {
    triggers.addElement(trigger);
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Releases the parse context.
   */
  public void release() {
    clear();
  }
  
  /**
   * Clears the parse context.
   */
  protected void clear() {
    triggers.clear();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  /**
   * Returns the trigger array hold in this parse context.
   * @return The trigger array hold in this parse context.
   */
  public VKTrigger[] getTriggers() {
    return (VKTrigger[])Utils.toArray(triggers, VKTrigger.class);
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private Vector			triggers;
}
