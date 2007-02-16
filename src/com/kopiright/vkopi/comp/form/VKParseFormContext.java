/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
import com.kopiright.vkopi.comp.base.VKEnvironment;
import com.kopiright.util.base.Utils;

public class VKParseFormContext extends VKParseVKWindowContext {

  public static VKParseFormContext getInstance(VKEnvironment environment) {
    return new VKParseFormContext(environment);
  }

  public void release() {
    // !!! not yet> release(this);
  }

  public static void release(VKParseFormContext context) {
    context.clear();
    stack.push(context);
  }

  private VKParseFormContext(VKEnvironment environment) {
    super(environment);
  }

  private void clear() {
    elements.setSize(0);
    pages.setSize(0);
    interfaces.setSize(0);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void addFormElement(VKFormElement element) {
    elements.addElement(element);
    element.setPageNumber(pages.size() == 0 ? 0 : pages.size() - 1);
  }

  public void addPage(VKPage page) {
    pages.addElement(page);
  }

  public void addInterface(CReferenceType inter) {
    interfaces.addElement(inter);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public VKFormElement[] getElements() {
    return (VKFormElement[])Utils.toArray(elements, VKFormElement.class);
  }

  public VKPage[] getPages() {
    return (VKPage[])Utils.toArray(pages, VKPage.class);
  }

  public CReferenceType[] getInterfaces() {
    return (CReferenceType[])Utils.toArray(interfaces, CReferenceType.class);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector	elements = new Vector();
  private Vector	pages = new Vector();
  private Vector	interfaces = new Vector();

  private static Stack stack = new Stack();
}
