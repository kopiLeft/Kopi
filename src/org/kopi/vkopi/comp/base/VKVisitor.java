/*
 * Copyright (c) 1990-2025 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.vkopi.comp.base;

import org.kopi.vkopi.comp.form.VKBlock;
import org.kopi.vkopi.comp.form.VKField;
import org.kopi.vkopi.comp.form.VKForm;
import org.kopi.vkopi.comp.form.VKPage;

/**
 * Implementation of Visitor Design Pattern for VK.
 */
public interface VKVisitor {

  // ----------------------------------------------------------------------
  // Form
  // ----------------------------------------------------------------------

  /**
   * visits a form
   */
  void visitVKForm(VKForm vkForm);

  // ----------------------------------------------------------------------
  // Command
  // ----------------------------------------------------------------------

  /**
   * visits a command
   */
  void visitVKCommand(VKCommand vkCommand);

  // ----------------------------------------------------------------------
  // Trigger
  // ----------------------------------------------------------------------

  /**
   * visits a trigger
   */
  void visitVKTrigger(VKTrigger vkTrigger);

  // ----------------------------------------------------------------------
  // Page
  // ----------------------------------------------------------------------

  /**
   * visits a page
   */
  void visitVKPage(VKPage vkPage);

  // ----------------------------------------------------------------------
  // Block
  // ----------------------------------------------------------------------

  /**
   * visits a block
   */
  void visitVKBlock(VKBlock vkBlock);

  // ----------------------------------------------------------------------
  // Field
  // ----------------------------------------------------------------------

  /**
   * visits a field
   */
  void visitVKField(VKField vkField);
}