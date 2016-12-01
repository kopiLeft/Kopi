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

public class VCommand {
  public VCommand(int mode, ActionHandler handler, VActor actor, int trigger, String item) {
    this.mode = mode;
    this.actor = actor;
    this.trigger = trigger;
    this.item = item;
    this.handler = handler;
  }

  /**
   * Returns the actor
   */
  public void setEnabled(boolean enabled) {
    if (actor != null && !killed) {
      actor.setEnabled(enabled);
      actor.setNumber(trigger);
      actor.setHandler(handler);
      //      actor.setSynchronous(false);
    }
  }

  /**
   * Kill a command: this command will never been enabled again
   */
  public void kill() {
    killed = true;
  }

  /**
   * Returns the actor
   */
  public boolean isEnabled() {
    return actor != null && actor.isEnabled();
  }

  /**
   * Returns the name has defined in source
   */
  public String getIdent() {
    return item;
  }

  /**
   * Returns the command number
   */
  public int getTrigger() {
    return trigger;
  }

  /**
   * Returns true iff the command is active in given to mode.
   *
   * @param	mode		the mode to test
   */
  public boolean isActive(int mode) {
    return (this.mode & (1 << mode)) != 0;
  }

  /**
   * Returns true iff the command is active in given to mode.
   *
   * @param	b	        set to be active
   */
  public void setActive(boolean b) {
    if (b) {
      mode = 0xFFFF;
    } else {
      mode = 0;
    }
  }

  public void performAction() {
    actor.setNumber(trigger);
    actor.setHandler(handler);
    //    actor.setSynchronous(false);
    actor.performAction();
  }

  public void performBasicAction() throws VException {
    actor.setNumber(trigger);
    actor.setHandler(handler);
    //    actor.setSynchronous(false);
    actor.performBasicAction();
  }

  /**
   *
   */
  public int getKey() {
    return actor != null ? actor.acceleratorKey : 0;
  }
  
  /**
   * Returns the actor associated with this command.
   * @return The actor associated with this command.
   */
  public VActor getActor() {
    return actor;
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  public void helpOnCommand(VHelpGenerator help) {
    actor.helpOnCommand(help);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String	item;
  private int		mode;
  private boolean	killed;
  protected VActor	actor;
  protected int		trigger;
  protected ActionHandler handler;
}
