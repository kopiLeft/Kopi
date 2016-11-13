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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

/**
 * A text input validation strategy.
 */
public interface TextValidationStrategy {

  /**
   * Returns {@code true} if the character is accepted.
   * @param c The character to validate.
   * @return {@code true} if the character is accepted.
   */
  public boolean validate(char c);
  
  /**
   * Returns {@code true} if the text is accepted.
   * @param text The text to be validated.
   * @param maxLength The maximum permitted length.
   * @return {@code true} if the text is accepted.
   */
  public boolean validate(String text, int maxLength);
}
