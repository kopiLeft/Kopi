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

package org.kopi.vkopi.lib.form;

import org.kopi.vkopi.lib.visual.VException;

/**
 * This class represents exceptions occuring during execution process.
 */
public class VSkipRecordException extends VException {

/**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public VSkipRecordException(String message) {
    super(message);
  }

  /**
   * Constructs an exception with no message.
   */
  public VSkipRecordException() {
    this((String)null);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 6534950848256780433L;
}
