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
 * $Id: VKParseBlockContext.java,v 1.2 2004/09/29 16:34:11 taoufik Exp $
 */

package at.dms.vkopi.comp.form;

import java.util.Vector;
import java.util.Stack;

import at.dms.kopi.comp.kjc.*;
import at.dms.vkopi.comp.base.*;
import at.dms.util.base.Utils;

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
    indices.setSize(0);
    fields.setSize(0);
    interfaces.setSize(0);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void addTable(VKBlockTable table) {
    tables.addElement(table);
  }

  public void addIndice(String indice) {
    indices.addElement(indice);
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

  public String[] getIndices() {
    return (String[])Utils.toArray(indices, String.class);
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

  private Vector	tables = new Vector();
  private Vector	indices = new Vector();
  private Vector	fields = new Vector();
  private Vector	interfaces = new Vector();

  private CParseClassContext		classContext = new CParseClassContext();

  private static Stack stack = new Stack();
}
