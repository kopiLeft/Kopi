/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.comp.kjc;

import java.util.ArrayList;

public class CParseClassContext {
	
  public void release() {
    release(this);
  }

  public static void release(CParseClassContext context) {
    context.clear();
  }

  private void clear() {
    fields.clear();
    methods.clear();
    inners.clear();
    assertions.clear();
    body.clear();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void addFieldDeclaration(JFieldDeclaration decl) {
    fields.add(decl);
    body.add(decl);
  }
  
  public void addConstructorDeclaration(JConstructorDeclaration decl) {
    addMethodDeclaration(decl);
  }
  
  public void addMethodDeclaration(JMethodDeclaration decl) {
    methods.add(decl);
  }

  public void addAssertionDeclaration(JMethodDeclaration decl) {
    assertions.add(decl);
  }

  public void addInnerDeclaration(JTypeDeclaration decl) {
    inners.add(decl);
  }

  public void addBlockInitializer(JClassBlock block) {
    body.add(block);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public JFieldDeclaration[] getFields() {
    return (JFieldDeclaration[])fields.toArray(new JFieldDeclaration[fields.size()]);
  }

  public JMethodDeclaration[] getMethods() {
    return (JMethodDeclaration[])methods.toArray(new JMethodDeclaration[methods.size()]);
  }

  public JMethodDeclaration[] getAssertions() {
    return (JMethodDeclaration[])assertions.toArray(new JMethodDeclaration[assertions.size()]);
  }

  public JTypeDeclaration[] getInnerClasses() {
    return (JTypeDeclaration[])inners.toArray(new JTypeDeclaration[inners.size()]);
  }

  public JPhylum[] getBody() {
    return (JPhylum[])body.toArray(new JPhylum[body.size()]);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected ArrayList fields = new ArrayList();
  protected ArrayList methods = new ArrayList();
  protected ArrayList assertions = new ArrayList();
  protected ArrayList inners = new ArrayList();
  protected ArrayList body = new ArrayList();
}
