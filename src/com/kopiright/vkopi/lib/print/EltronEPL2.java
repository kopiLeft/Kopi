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

package com.kopiright.vkopi.lib.print;
import  com.kopiright.vkopi.lib.util.Filter;
/**
 *
 */
public class EltronEPL2 extends LabelPrinter {

  /**
   *
   */

  public EltronEPL2() {
    super();
  }

  public EltronEPL2(Filter filter) {
    this();
    this.filter = filter;
  }

  public EltronEPL2(Filter filter, int x,  int y) {
    this();
    this.filter	 = filter;
    setOffset(x, y);
  }
}
