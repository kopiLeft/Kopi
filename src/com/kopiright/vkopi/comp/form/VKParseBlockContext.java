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

import java.util.Vector;
import java.util.Stack;

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.vkopi.comp.base.*;
import com.kopiright.util.base.Utils;

public class VKParseBlockContext extends VKParseContext {
  public static VKParseBlockContext getInstance() {
    return new VKParseBlockContext();
  }

  public void release() {
    // release(this);
  }

  public static void release(VKParseBlockContext context) {
    context.clear();
    stack.push(context);
  }

  private VKParseBlockContext() {
  }

  private void clear() {
    tables.setSize(0);
    fields.setSize(0);
    interfaces.setSize(0);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void addTable(VKBlockTable table) {
    tables.addElement(table);
  }

  public void addField(VKField field) {
    fields.addElement(field);
  }

  public void addInterface(CReferenceType inter) {
    interfaces.addElement(inter);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public CParseClassContext getClassContext() {
    return classContext;
  }

  public VKBlockTable[] getTables() {
    return (VKBlockTable[])Utils.toArray(tables, VKBlockTable.class);
  }

  public VKField[] getFields() {
    return (VKField[])Utils.toArray(fields, VKField.class);
  }

  public CReferenceType[] getInterfaces() {
    return (CReferenceType[])Utils.toArray(interfaces, CReferenceType.class);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector                tables = new Vector();
  private Vector                fields = new Vector();
  private Vector                interfaces = new Vector();

  private CParseClassContext	classContext = new CParseClassContext();

  private static Stack          stack = new Stack();
}
