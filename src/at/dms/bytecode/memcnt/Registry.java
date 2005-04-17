/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.bytecode.memcnt;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class allows the inspected classes to register themselves.
 * The registy can then be inquired to get the number of allocated
 * objects.
 */
public class Registry {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * It is a completely static class, so no constructors.
   */
  private Registry() {}

  // --------------------------------------------------------------------
  // ACCESSORS & MUTATORS
  // --------------------------------------------------------------------

  /**
   * Registers the specified class as being inspected.
   */
  public static void register(String name) {
    classes.addElement(name);
  }

  /**
   *
   */
  public static void dumpCount() throws NoSuchFieldException {
    System.err.println("*** DUMPING MEMORY COUNT ****");
    for (Enumeration elems = classes.elements(); elems.hasMoreElements(); ) {
      String	name = (String)elems.nextElement();

      try {
	Class	clazz = Class.forName(name);
	Field	field = clazz.getDeclaredField("memcnt$totins");

	System.err.println(name + ": " + field.getInt(null));
      } catch (ClassNotFoundException e) {
	System.err.println("*** " + name + " ***"); System.err.flush();
      } catch (NoSuchFieldException e) {
	e.printStackTrace();
	throw e;
      } catch (IllegalAccessException e) {
        /* ignore */
      }
    }
    System.err.println("*** FINISHED DUMPING MEMORY COUNT ****"); System.err.flush();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static Vector		classes = new Vector();
}
