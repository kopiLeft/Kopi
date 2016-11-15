/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.visual;

import org.kopi.vkopi.lib.l10n.LocalizationManager;
import org.kopi.vkopi.lib.l10n.ModuleLocalizer;
import org.kopi.vkopi.lib.base.Image;
import org.kopi.xkopi.lib.base.DBContext;


public class Module {

  /**
   * Constructor
   */
  public Module(int id,
                int parent,
                String shortname,
                String source,
                String object,
                int access,
                String icon)
  {
    this.id = id;
    this.parent = parent;
    this.shortname = shortname;
    this.source = source;
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
      this.icon = ImageHandler.getImageHandler().getImage(icon);
      smallIcon = ImageHandler.getImageHandler().getImage(icon);
      if (smallIcon == null) {
        smallIcon = smallIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
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
  public Image getIcon() {
    return icon;
  }

  /**
   * return the mnemonic
   */
  public Image getSmallIcon() {
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
                                         Image icon)
    throws VException
  {
    try {
      if (ApplicationContext.getDefaults().isDebugModeEnabled()) {
        System.gc();
	Thread.yield();
      }

      KopiExecutable    form = getKopiExecutable(object);

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
      ApplicationContext.reportTrouble("Form loading",
                                       "Module.startForm(DBContext ctxt, String object, String description, ImageIcon icon)",
                                       t.getMessage(),
                                       t);
      ApplicationContext.displayError(ApplicationContext.getMenu().getDisplay(), org.kopi.vkopi.lib.visual.MessageCode.getMessage("VIS-00041"));
      return null;
    }
  }

  /**
   *
   */
  public String toString() {
    return shortname;
  }


  // ---------------------------------------------------------------------
  // LOCALIZATION
  // ---------------------------------------------------------------------

  /**
   * Localize this module
   *
   * @param     manager         the manger to use for localization
   */
  public void localize(LocalizationManager manager) {
    ModuleLocalizer             loc;

    try {
      loc = manager.getModuleLocalizer(source, shortname);
      description = loc.getLabel();
      help = loc.getHelp();
    } catch (org.kopi.util.base.InconsistencyException e) {
      // If the module localization is not found, report it
      ApplicationContext.reportTrouble(shortname,
                                       source,
                                       "Module '" + shortname + "' was not found in '" + source + "'",
                                       e);
      description = "!!! " + shortname + " !!!";
      help = description;
    }
  }


  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  public static final int       	ACS_PARENT = 0;
  public static final int       	ACS_TRUE   = 1;
  public static final int       	ACS_FALSE  = 2;

  private int                   	id;
  private int                   	parent;
  private String                	shortname;
  private String                	description;
  private String                	object;
  private String               	 	help;
  private String                	source;
  private int                   	access;
  private Image             		icon;
  private Image             		smallIcon;
}
