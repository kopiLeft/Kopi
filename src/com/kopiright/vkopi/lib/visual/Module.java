/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
import javax.swing.ImageIcon;

import com.kopiright.xkopi.lib.base.DBContext;

public class Module {

  /**
   * Constructor
   */
  public Module(int id,
		int parent,
		String shortname,
		String description,
		String object,
		String help,
		int access,
		String icon)
  {
    this.id = id;
    this.parent = parent;
    this.shortname = shortname;
    this.description = description;
    this.help = help;
    this.object = object;
    //!!! graf 2006.01.30: temporary work-around
    //!!! remove as soon as all modules have been
    //!!! renamed to "com.kopiright." at every
    //!!! customer installation.
    if (this.object != null && this.object.startsWith("at.dms.")) {
      this.object = "com.kopiright." + this.object.substring("at.dms.".length());
    }
    //!!! graf 2006.01.30: end
    this.access = access;
    if (icon != null) {
      this.icon = Utils.getImage(icon);
      smallIcon = (ImageIcon)icons.get(icon);
      if (smallIcon == null) {
 	icons.put(icon, smallIcon = new ImageIcon(this.icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH)));
      }
    }
  }

  /**
   * return the ident of the module
   */
  public int getId() {
    return id;
  }

  /**
   * return the ident of the parent of the current module
   */
  public int getParent() {
    return parent;
  }

  /**
   * return description of the module
   */
  public String getDescription() {
    return description;
  }

  /**
   * return the name of the object which are linked with the module
   * this object is the name of the class to be executed when this module
   * is called.
   */
  public String getObject() {
    return object;
  }

  /**
   * return the help stirng
   */
  public String getHelp() {
    return help;
  }


  /**
   * return the mnemonic
   */
  public ImageIcon getIcon() {
    return icon;
  }

  /**
   * return the mnemonic
   */
  public ImageIcon getSmallIcon() {
    return smallIcon;
  }

  /**
   * return access of the current user
   */
  public int getAccessibility() {
    return access;
  }

  /**
   * Sets the accessibility of the module
   */
  public void setAccessibility(int access) {
    this.access = access;
  }

  /**
   *
   * @param	ctxt		the context where to look for application
   */
  public void run(DBContext ctxt) throws VException {
    startForm(ctxt, object, description, getSmallIcon());
  }

  /**
   *
   */
  public static KopiExecutable getKopiExecutable(String object) {
    try {
      return (KopiExecutable)Class.forName(object).newInstance();
    } catch (IllegalAccessException iae) {
      throw new VRuntimeException(iae);
    } catch (InstantiationException ie) {
      throw new VRuntimeException(ie);
    } catch (ClassNotFoundException cnfe) {
      throw new VRuntimeException(cnfe);
    }
  }

  /**
   *
   */
  public static KopiExecutable startForm(DBContext ctxt,
                                  String object,
                                  String description)
    throws VException
  {
    return startForm(ctxt, object, description, null);
  }

  /**
   *
   */
  public static KopiExecutable startForm(DBContext ctxt,
                                  String object,
                                  String description,
                                  ImageIcon icon)
    throws VException
  {
    try {
      if (Application.getDefaults().isDebugModeEnabled()) {
        System.gc();
	Thread.yield();
      }

      KopiExecutable	form = getKopiExecutable(object);

      if (form instanceof VWindow) {
	((VWindow)form).setSmallIcon(icon);
      }
      form.setDBContext(ctxt);
      form.doNotModal();
      return form;
    } catch (VException v) {
      v.printStackTrace();
      throw v;
    } catch (Throwable t) {
      System.out.println(t.getMessage());
      t.printStackTrace();
      System.out.println("Error while loading " + description + ":" + t);
      return null;
    }
  }

  /**
   *
   */
  public String toString() {
    return shortname;
  }

  public static final int ACS_PARENT	= 0;
  public static final int ACS_TRUE	= 1;
  public static final int ACS_FALSE	= 2;

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int			id;
  private int			parent;
  private String		shortname;
  private String		description;
  private String		object;
  private String		help;
  private int			access;
  private ImageIcon		icon;
  private ImageIcon		smallIcon;

  private static Hashtable	icons = new Hashtable();
}
