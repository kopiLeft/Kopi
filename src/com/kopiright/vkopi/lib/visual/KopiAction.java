/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

/**
 * Child of this class represents objects than can be executed asynchronously
 * by the kopi action mechanism
 */
public abstract class KopiAction implements Runnable {

  public KopiAction() {
  }

  public KopiAction(String name) {
    this.name = name;
  }

//   /**
//    * Executes the action
//    *
//    * @return	is this action changes the UI
//    * @exception	VException	an exception may be raised by your action
//    */
//   public abstract boolean run() throws VException;

  public abstract void execute() throws VException;

  public final void run() {
    try {
      execute();
    } catch(VException e) {
      throw new VRuntimeException(e.getMessage(), e);
    }
  }

  public String toString() {
    return super.toString()+" "+name;
  }

  protected	String		name;
}
