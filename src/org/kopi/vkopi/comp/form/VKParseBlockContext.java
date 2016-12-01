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

package org.kopi.vkopi.comp.form;

import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.util.base.Utils;
import org.kopi.vkopi.comp.base.VKParseContext;

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
    dropListMap.clear();
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

  /**
   * Adds a field drop list. A check if performed to test if the dropped extension
   * id associated to another field. In this case, the conflicted drop extension is
   * returned. otherwise null is returned.
   */
  public String addDropList(String[] dropList, VKField field) {
    for (int i = 0; i < dropList.length; i++) {
      String	extension = dropList[i].toLowerCase();

      if (dropListMap.get(extension) != null) {
	return extension;
      }

      dropListMap.put(extension, field.getIdent());
    }

    return null;
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

  public HashMap getDropListMap() {
    return dropListMap;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector               tables = new Vector();
  private Vector               fields = new Vector();
  private Vector               interfaces = new Vector();
  private HashMap		dropListMap = new HashMap();

  private CParseClassContext	classContext = new CParseClassContext();

  private static Stack        stack = new Stack();
}
