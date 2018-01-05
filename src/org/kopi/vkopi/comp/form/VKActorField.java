/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.comp.form;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.vkopi.comp.base.VKCommand;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKTrigger;

public class VKActorField extends VKField {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of an actor field
   *
   * @param where               the token reference of this node
   * @param ident               the ident of this field
   * @param pos                 the position within the block
   * @param label               the label (text on the left)
   * @param help                the help text
   * @param access              the access mode
   * @param commands            the commands accessible in this field
   * @param triggers            the triggers executed by this field
   */
  public VKActorField(TokenReference where,
                      String ident,
                      VKPosition pos,
                      String label,
                      String help,
                      int[] access,
                      VKCommand[] commands,
                      VKTrigger[] triggers)
  {
    super(where,
          ident,
          pos,
          label,
          help,
          new VKDefinitionType(where, new VKActorType(where)),
          org.kopi.vkopi.lib.form.VConstants.ALG_LEFT,
          0,
          null,
          access,
          commands,
          triggers,
          null);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  /**
   * @Override
   */
  public void checkCode(VKContext context, VKBlock block, VKDefinitionCollector coll)
    throws PositionedError
  {
    long        allowedTriggers;
    
    super.checkCode(context, block, coll);
    // COMMANDS: check that no command is defined
    if (commands.length > 0) {
      throw new PositionedError(getTokenReference(), FormMessages.ACTOR_FIELD_COMMANDS);
    }
    // TRIGGERS: check that only ACCESS and ACTION triggers are used
    allowedTriggers = (1L << TRG_FLDACCESS) | (1L << TRG_ACTION);
    for (int i = 0; i < triggers.length; i++) {
      if ((triggers[i].getEvents() & allowedTriggers) == 0) {
        throw new PositionedError(triggers[i].getTokenReference(), FormMessages.ACTOR_FIELD_TRIGGERS);
      }
    }
  }
}
