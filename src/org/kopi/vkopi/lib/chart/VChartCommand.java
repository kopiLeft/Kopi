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

package org.kopi.vkopi.lib.chart;

import org.kopi.vkopi.lib.visual.ActionHandler;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VActor;
import org.kopi.vkopi.lib.visual.VCommand;
import org.kopi.vkopi.lib.visual.VException;

public class VChartCommand extends VCommand implements ActionHandler {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  
  /**
   * Creates a new chart command.
   * @param chart The chart object.
   * @param actor The command actor.
   */
  public VChartCommand(VChart chart, VActor actor) {
    super(0xFFFF, null, actor, actor.getNumber(), actor.getActorIdent());
    this.chart = chart;
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  
  /**
   * @Override
   */
  public void setEnabled(boolean enabled) {
    if (actor != null ) {
      actor.setEnabled(enabled);
      actor.setNumber(trigger);
      actor.setHandler(this);
    }
  }
  
  /**
   * @Override
   */
  public void executeVoidTrigger(int VKT_Type) throws VException {
    // TODO
  }

  /**
   * @Override
   */
  @SuppressWarnings("deprecation")
  public void performAction(KopiAction action, boolean block) {
    chart.performAction(action, block);
  }

  /**
   * @Override
   */
  public void performAsyncAction(KopiAction action) {
    chart.performAsyncAction(action);
  }

  // --------------------------------------------------------------------
  // HELP HANDLING
  // --------------------------------------------------------------------

  /**
   * @Override
   */
  public void helpOnCommand(org.kopi.vkopi.lib.visual.VHelpGenerator help) {
    if (actor == null) {
      return;
    }
    
    actor.helpOnCommand(help);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  
  private final VChart					chart;
}
