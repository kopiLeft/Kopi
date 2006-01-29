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

package com.kopiright.vkopi.lib.form;

import java.awt.Component;
import java.util.EventListener;

import com.kopiright.vkopi.lib.visual.VException;

public interface FieldListener extends EventListener {

  void updateModel() throws VException;
  Object getDisplayedValue(boolean trim) throws VException;
  Component getCurrentDisplay(); // please do not use!

  void fieldError(String message);

  boolean requestFocus() throws VException;
  boolean loadItem(int i) throws VException;
  boolean predefinedFill() throws VException;

  void enter();
  void leave();
}
