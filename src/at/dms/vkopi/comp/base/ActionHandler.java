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

package at.dms.vkopi.comp.base;

import at.dms.kopi.comp.kjc.JStatement;
import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents an action handler, ie an object that support action
 */
public interface ActionHandler extends Commandable {

  /**
   * Returns a collector for definitiion
   */
  VKDefinitionCollector getDefinitionCollector();

  /**
   * Returns an unique command number
   */
  int getCommandNumber(String name);

  /**
   * Returns a trigger handler position
   */
  int addCommandHandler(JStatement code) throws UnpositionedError;
}
