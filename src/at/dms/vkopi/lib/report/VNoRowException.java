/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.report;

import at.dms.vkopi.lib.visual.VRuntimeException;

/**
 * This class represents exceptions occuring during execution process.
 */
public class VNoRowException extends VRuntimeException {

  /**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public VNoRowException(String message) {
    super(message);
  }

  /**
   * Constructs an exception with no message.
   */
  public VNoRowException() {
    this(null);
  }
}
