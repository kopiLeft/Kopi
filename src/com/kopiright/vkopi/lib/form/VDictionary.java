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

package com.kopiright.vkopi.lib.form;

import com.kopiright.vkopi.lib.visual.KopiExecutable;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VWindow;

/**
 * The {@code VDictionary} is a meaning to handle three basic operations.
 * These operations are :
 * <ol>
 *   <li>Search an existing record: this can be reached by calling {@link #search(VWindow)}</li>
 *   <li>Edit an existing record: this can be reached by calling {@link #edit(VWindow, int)}</li>
 *   <li>Creates a new record: this can be reached by calling {@link #add(VWindow)}</li>
 * </ol>
 * <p>
 *   Implementations can be done in a UI context or in any other possible context.
 * </p>
 * @see VDictionaryForm
 */
public interface VDictionary extends KopiExecutable {

  /**
   * Searches for an existing record.
   * <p>
   *   The implementation can be done in a UI context or by a simple
   *   database query. The returned integer represents the identifier
   *   of the selected record after the search operation.
   * </p>
   * @param parent The parent window. This can be used also as database context handler.
   * @return The selected ID of the searched record.
   * @throws VException Any visual errors that occurs during search operation.
   */
  public int search(VWindow parent) throws VException;
  
  /**
   * Edits an existing record.
   * <p>
   *   The implementation can be done in a UI context or by a simple
   *   database query. The returned integer represents the identifier
   *   of the edited record after the edit operation.
   * </p>
   * @param parent The parent window. This can be used also as database context handler.
   * @param id The record ID to be edited.
   * @return The edited record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  public int edit(VWindow parent, int id) throws VException;
  
  /**
   * Adds a new record.
   * <p>
   *   The implementation can be done in a UI context or by a simple
   *   database query. The returned integer represents the identifier
   *   of the created record.
   * </p>
   * @param parent The parent window. This can be used also as database context handler.
   * @return The created record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  public int add(VWindow parent) throws VException;
}
