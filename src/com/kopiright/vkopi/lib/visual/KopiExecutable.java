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

import com.kopiright.xkopi.lib.base.DBContext;

/**
 * An interface for stand alone apps that can be executed from the
 * Menu tree.
 *
 * If old instance of your app can be reused, use KopiExecutable2.
 */
public interface KopiExecutable {

  /**
   * MenuTree sets the context of new executable to the default connection
   */
  void setDBContext(DBContext context);

  /**
   * The start method called every time the user launch this app from menu
   * it should be not modal
   * @exception	VException	an exception may be raised by your app
   */
  void doNotModal() throws VException;
}
