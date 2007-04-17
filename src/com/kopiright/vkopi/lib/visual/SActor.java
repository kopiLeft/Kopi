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

import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.l10n.ActorLocalizer;
import com.kopiright.vkopi.lib.l10n.MenuLocalizer;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public class SActor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Creates a new actor.
   */
  public SActor(String menuIdent,
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

  /**
   * Creates a new actor.
  public SActor(String menuName,
		String menuItem,
		String iconName,
		int acceleratorKey,
		int acceleratorModifier,
		String help)
  {
    this.menuName = menuName;
    this.menuItem = menuItem;
    this.iconName = iconName;
    this.acceleratorKey = acceleratorKey;
    this.acceleratorModifier = acceleratorModifier;
    this.help = help;
    
    this.action = new SActorAction(menuItem,
                                   (iconName != null) ?
                                       loadImage(iconName) :
                                       null);
    if (acceleratorKey != KeyEvent.VK_UNDEFINED) {
      this.action.putValue(Action.ACCELERATOR_KEY,
                           KeyStroke.getKeyStroke(acceleratorKey,
                                                  acceleratorModifier));
    }
    this.action.putValue(Action.SHORT_DESCRIPTION, help);
    action.setEnabled(false);
  }
  */

  public Action getAction() {
    return action;
  }

  private static ImageIcon loadImage(String iconName) {
    ImageIcon   image;

    image = com.kopiright.vkopi.lib.util.Utils.getImage(iconName + ".png");
    if (image == null || image == com.kopiright.vkopi.lib.util.Utils.UKN_IMAGE) {
      image = com.kopiright.vkopi.lib.util.Utils.getImage(iconName + ".gif");
    }
    return image;
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
   * Checks whether the atcor is enabled.
   */
  public boolean isEnabled() {
    return action.isEnabled();
  }

  /**
   * Enables/disables the actor.
   */
  public void setEnabled(boolean enabled) {
    action.setEnabled(enabled);
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
    return this.number;
  }



  public void performAction() {
    handler.performAction(new KopiAction(menuItem + " in " + menuName) {
      public void execute() throws VException {
	handler.executeVoidTrigger(number);
      }
    }, synchronous);
  }

  public void performBasicAction() throws VException {
    handler.executeVoidTrigger(number);
  }

  public int hashCode() {
    return menuItem.hashCode() * menuItem.hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof SActor)) {
      return false;
    } else {
      SActor	actor = (SActor)obj;

      return
        menuName.equals(actor.menuName)
        && menuItem.equals(actor.menuItem)
        && ((iconName == null && actor.iconName == null)
            || (iconName != null 
                && actor.iconName != null
                && iconName.equals(actor.iconName)));
    }
  }

  /**
   *
   */
  public void initAction() {
    action = new SActorAction(menuItem,
                              (iconName != null) ?
                              loadImage(iconName) :
                              null);
    if (acceleratorKey != KeyEvent.VK_UNDEFINED) {
      action.putValue(Action.ACCELERATOR_KEY,
                      KeyStroke.getKeyStroke(acceleratorKey,
                                             acceleratorModifier));
    }
    action.putValue(Action.SHORT_DESCRIPTION, help);
    action.setEnabled(false);
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
    
    initAction();
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
    buffer.append("SActor[");
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

  private class SActorAction extends AbstractAction {
    
    SActorAction(String name, Icon icon) {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e) {
      SActor.this.performAction();
    }
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -6510825866215273279L;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------



  public final int		acceleratorKey;
  public final int		acceleratorModifier;
    
  public String                 menuName;
  public String                 menuItem;
    
  private String                menuSource;  // qualified name of menu's source file
  private String                actorSource; // qualified name of actor's source file
  private Action                action; // replaces dactor
  private int                   number;
  private ActionHandler         handler;
  private boolean		synchronous;

  protected String		iconName;
  protected String              menuIdent;
  protected String              actorIdent;
  protected String              help;
}
