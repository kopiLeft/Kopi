/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.visual;

import java.util.Hashtable;

public class Registry {
  
  // ---------------------------------------------------------------------
  // CONSTRUCTOR
  // ---------------------------------------------------------------------
  
  /**
   * Constructor
   *
   * @param     domain          the 3 upper-case letter identifier of this registry.
   * @param     parents         the parent registers of this instance.
   */
  protected Registry(String domain, Registry[] parents) {
    this.domain = domain;
    this.parents = parents;
  }
  
  
  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------
  
  /**
   * Returns the domain of this registry.
   */
  public String getDomain() {
    return domain;
  }
  
  /**
   * Builds the dependencies of this registry. 
   */
  /*package*/ void buildDependencies() {
    buildDependencies(dependencies);
  }
  
  /**
   * Builds the dependencies of this registry.
   *
   * @param     deps            the dependency hashtable.
   */
  private void buildDependencies(Hashtable deps) {
    if (! deps.containsKey(domain)) {
      deps.put(domain, this.getClass().getPackage().getName() + ".Messages");
    }
    if (parents != null) {
      for(int i=0; i<parents.length; i++) {
        parents[i].buildDependencies(deps);
      }
    }
  }
  
  /**
   * Returns the message source for the given key.
   *
   * @param     key             a 3 upper-case letter registry identifier.
   */
  public String getMessageSource(String key) {
    return (String)dependencies.get(key);
  }
  
  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
  
  private final String                  domain;
  private final Registry[]              parents;
  private Hashtable                     dependencies = new Hashtable();
}
