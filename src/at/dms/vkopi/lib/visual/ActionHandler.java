/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: ActionHandler.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.visual;

/**
 * Child of this class represents objects than can be executed asynchronously
 * by the kopi action mechanism
 */
public interface ActionHandler {

  /**
   * Performs a void trigger
   *
   * @param	VKT_Type	the number of the trigger
   */
  void executeVoidTrigger(final int VKT_Type) throws VException;

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   * @param	block		This action should block the UI thread ?
   * @deprecated                use method performAsyncAction
   */
  void performAction(final KopiAction action, boolean block);

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   */
  void performAsyncAction(final KopiAction action);
}
