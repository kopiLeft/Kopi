/*
 * Copyright (C) 1990-99 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: GClassType.java,v 1.11 1999/08/25 14:38:28 graf Exp $
 */

package at.dms.vkjc.trig;

import java.util.Hashtable;
import at.dms.kjc.*;
import at.dms.xkjc.*;

/**
 * This class represents class type in the type structure
 */
public class GClassType extends CClassType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a class type
   * @param	qualifiedName	the class qualified name of the class
   */
  public GClassType(String qualifiedName) {
    super(qualifiedName);
  }

  /**
   * Construct a class type
   * @param	qualifiedName	the class qualified name of the class
   * @param	checked		has the name already be checked
   */
  public GClassType(String qualifiedName, boolean checked) {
    super(qualifiedName, checked);
  }

  /**
   * Construct a class type
   * @param	clazz		the class that will represent this type
   */
  protected GClassType(CClass clazz) {
    super(clazz);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Transforms this type to a string
   */
  public String toString() {
    //!!! graf 990824 WHAT IS THIS ???
    if (true /*Main.verboseMode()*/) {
      return super.toString();
    } else {
      String	java = super.toString();
      String	kopi = (String)names.get(java.replace('.', '/'));
      return kopi == null ? java : kopi;
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final void init() {
    names = new Hashtable(20);
    names.put(XConstants.JAV_BOOLEAN, "nullable boolean");
    names.put(XConstants.JAV_BYTE, "nullable byte");
    names.put(XConstants.JAV_SHORT, "nullable short");
    names.put(XConstants.JAV_INT, "nullable int");
    names.put(XConstants.JAV_LONG, "nullable long");
    names.put(XConstants.JAV_FLOAT, "nullable float");
    names.put(XConstants.JAV_DOUBLE, "nullable double");
    names.put(XConstants.JAV_CHAR, "nullable char");
    names.put(XConstants.XKJ_FIXED, "fixed");
    names.put(XConstants.XKJ_DATE, "date");
    names.put(XConstants.XKJ_MONTH, "month");
    names.put(XConstants.XKJ_TIME, "time");
    names.put("at/dms/xtype/Fixed", "fixed");
    names.put("at/dms/xtype/Date", "date");
    names.put("at/dms/xtype/Month", "month");
    names.put("at/dms/xtype/Time", "time");
  }

  static {
    init();
  }

  private static Hashtable	names;
}
