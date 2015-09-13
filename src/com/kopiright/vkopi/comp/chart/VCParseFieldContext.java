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

package com.kopiright.vkopi.comp.chart;

import java.util.Stack;

import com.kopiright.vkopi.comp.base.VKParseContext;

public class VCParseFieldContext extends VKParseContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  private VCParseFieldContext() {}

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Creates a new field parse context.
   * @return The new parse context instance.
   */
  public static VCParseFieldContext getInstance() {
    return new VCParseFieldContext();
  }

  /**
   * Releases this context.
   */
  public void release() {
    release(this);
  }

  /**
   * Releases the given parse context.
   * @param context The parse context to be released.
   */
  public static void release(VCParseFieldContext context) {
    stack.push(context);
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static Stack<VCParseFieldContext> 		stack = new Stack<VCParseFieldContext>();
}
