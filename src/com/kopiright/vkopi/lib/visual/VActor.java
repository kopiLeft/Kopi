/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import com.kopiright.vkopi.lib.l10n.ActorLocalizer;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.l10n.MenuLocalizer;
import com.kopiright.vkopi.lib.ui.base.UComponent;


public class VActor implements VModel {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Creates a new actor.
   */
  public VActor(String menuIdent,
                String menuSource,
                String actorIdent,
                String actorSource,
		String iconName,
		int acceleratorKey,
		int acceleratorModifier)
  {
    this.menuIdent = menuIdent;
    this.menuSource = menuSource;
    this.actorIdent = actorIdent;
    this.actorSource = actorSource;
    this.iconName = iconName;
    this.acceleratorKey = acceleratorKey;
    this.acceleratorModifier = acceleratorModifier;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS / MUTATORS
  // ----------------------------------------------------------------------

  public String getMenuIdent() {
    return menuIdent;
  }

  public String getActorIdent() {
    return actorIdent;
  }

  /**
   * Checks whether the actor is enabled.
   */
  public boolean isEnabled() {
    return display != null && display.isEnabled();
  }

  /**
   * Enables/disables the actor.
   */
  public void setEnabled(boolean enabled) {
    if (display != null) {
      display.setEnabled(enabled);
    }
  }

  /**
   * Sets the number for the actor.
   */
  public void setNumber(int number) {
    this.number = number;
  }

  /**
   * Sets the handler for the actor.
   */
  public void setHandler(ActionHandler handler) {
    this.handler = handler;
  }

  /**
   * get the number for the actor.
   */
  public int getNumber() {
    return number;
  }

  /**
   * Sets the model display
   */
  public void setDisplay(UActor display) {
    this.display = display;
  }

  @Override
  public UActor getDisplay() {
    return display;
  }

  @Override
  public void setDisplay(UComponent display) {
    assert display instanceof UActor : "VActor display should be UActor";

    this.display = (UActor)display;
  }

  // ----------------------------------------------------------------------
  // ACTIONS HANDLING
  // ----------------------------------------------------------------------

  @SuppressWarnings("deprecation")
  public void performAction() {
    handler.performAction(new KopiAction(menuItem + " in " + menuName) {
      public void execute() throws VException {
	handler.executeVoidTrigger(number);
      }
    }, false);
  }

  public void performBasicAction() throws VException {
    handler.executeVoidTrigger(number);
  }

  // ----------------------------------------------------------------------
  // HASHCODE AND EQUALS REDEFINITION
  // ----------------------------------------------------------------------

  public int hashCode() {
    return actorIdent.hashCode() * actorIdent.hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof VActor)) {
      return false;
    } else {
      VActor	actor = (VActor)obj;

      return
        menuName.equals(actor.menuName)
        && menuItem.equals(actor.menuItem)
        && ((iconName == null && actor.iconName == null)
            || (iconName != null
                && actor.iconName != null
                && iconName.equals(actor.iconName)));
    }
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes this actor
   *
   * @param     manager         the manger to use for localization
   */
  public void localize(LocalizationManager manager) {
    ActorLocalizer      actorLoc;
    MenuLocalizer       menuLoc;

    menuLoc = manager.getMenuLocalizer(menuSource, menuIdent);
    actorLoc = manager.getActorLocalizer(actorSource, actorIdent);

    menuName = menuLoc.getLabel();
    menuItem = actorLoc.getLabel();
    help = actorLoc.getHelp();
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  public void helpOnCommand(VHelpGenerator help) {
    help.helpOnCommand(menuName,
                       menuItem,
                       iconName,
                       acceleratorKey,
                       acceleratorModifier,
                       this.help);
  }

  // --------------------------------------------------------------------
  // DEBUG
  // --------------------------------------------------------------------

  public String toString() {
    StringBuffer        buffer;

    buffer = new StringBuffer();
    buffer.append("VActor[");
    buffer.append("menu=" + menuName + ":" + menuItem);
    if (iconName != null) {
      buffer.append(", ");
      buffer.append("icon=" + iconName);
    }
    if (acceleratorKey != 0) {
      buffer.append(", ");
      buffer.append("key=" + acceleratorKey + ":" + acceleratorModifier);
    }
    buffer.append(", ");
    buffer.append("help=" + help);
    buffer.append("]");
    return buffer.toString();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public final int					acceleratorKey;
  public final int					acceleratorModifier;

  public String                 			menuName;
  public String                 			menuItem;

  private String                			menuSource;  // qualified name of menu's source file
  private String                			actorSource; // qualified name of actor's source file
  private UActor                			display;
  private int                   			number;
  private ActionHandler         			handler;

  public String						iconName;
  public String              				menuIdent;
  protected String              			actorIdent;
  public String              				help;
}
