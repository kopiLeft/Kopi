/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: VRParseReportContext.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.vkopi.comp.report;

import java.util.Vector;
import java.util.Stack;

import at.dms.compiler.base.TokenReference;
import at.dms.vkopi.comp.base.VKParseVKWindowContext;
import at.dms.vkopi.comp.base.VKEnvironment;

import at.dms.util.base.Utils;

public class VRParseReportContext extends VKParseVKWindowContext {
  public static VRParseReportContext getInstance(VKEnvironment environment) {
    return new VRParseReportContext(environment);
  }

  public void release() {
    // !!! not yet> release(this);
  }

  public static void release(VRParseReportContext context) {
    context.clear();
    stack.push(context);
  }

  private VRParseReportContext(VKEnvironment environment) {
    super(environment);
  }

  private void clear() {
    fields.setSize(0);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void addField(VRField field) {
    fields.addElement(field);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public VRField[] getFields() {
    addField(new VRSeparatorField(TokenReference.NO_REF));

    return (VRField[])Utils.toArray(fields, VRField.class);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector	fields = new Vector();

  private static Stack stack = new Stack();
}
