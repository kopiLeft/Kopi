/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.report;

import java.util.EventListener;

/**
 * {@code TableModelListener} defines the interface for an object that listens
 * to changes in {@link MReport} content.
 *
 * @see javax.swing.table.TableModel
 */
public interface ReportListener extends EventListener {

  /**
   * This fine grain notification tells listeners that
   * the report model has changed and the display should
   * update it self
   */
  public void contentChanged();
}
