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

package com.kopiright.vkopi.comp.report;

import java.util.Vector;
import java.util.Stack;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.vkopi.comp.base.VKParseVKWindowContext;
import com.kopiright.vkopi.comp.base.VKEnvironment;

import com.kopiright.util.base.Utils;

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
    interfaces.setSize(0);
    fields.setSize(0);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void addInterface(CReferenceType inter) {
    interfaces.addElement(inter);
  }

  public void addField(VRField field) {
    fields.addElement(field);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public CReferenceType[] getInterfaces() {
    return (CReferenceType[])Utils.toArray(interfaces, CReferenceType.class);
  }

  public VRField[] getFields() {
    addField(new VRSeparatorField(TokenReference.NO_REF));

    return (VRField[])Utils.toArray(fields, VRField.class);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector	interfaces = new Vector();
  private Vector	fields = new Vector();

  private static Stack stack = new Stack();
}
