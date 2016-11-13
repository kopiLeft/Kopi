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

import org.kopi.vkopi.lib.base.UComponent;

/**
 * {@code UField} is the top-level interface that must be implemented
 * by all kopi fields. It is the visual component of the {@link VField} model.
 */
public interface UField extends UComponent {

  /**
   * Returns the field model.
   * @return the field model.
   */
  public VField getModel();
  
  /**
   * Returns the field block view.
   * @return The {@link UBlock} containing this view.
   */
  public UBlock getBlockView();

  /**
   * Sets the blink state of the field.
   * @param blink the blink state.
   */
  public void setBlink(boolean blink);

  /**
   * Returns the field access.
   * @return the field access.
   */
  public int getAccess();

  /**
   * Returns the position in chart (0..nb Display)
   * @return The position in chart (0..nb Display)
   */
  public int getPosition();

  /**
   * Sets the field position in the chart.
   * @param position The field position in the chart.
   */
  public void setPosition(int position);

  /**
   * Returns the auto fill button.
   * @return The auto fill button.
   */
  public UComponent getAutofillButton();

  /**
   * Sets is the field is in detail view.
   * @param detail The detail model state.
   */
  public void setInDetail(boolean detail);

  /**
   * Updates the field text
   */
  public void updateText();

  /**
   * Updates the field access
   */
  public void updateAccess();

  /**
   * Updates the field focus
   */
  public void updateFocus();
   
  /**
   * Updates the field color properties (background and foreground).
   */
  public void updateColor();

  /**
   * Prepares the field snapshot.
   * @param fieldPos The field position.
   * @param activ Is the field active.
   */
  public void prepareSnapshot(int fieldPos, boolean activ);

  /**
   * Returns the field content.
   * @return The field value.
   */
  public Object getObject();
}
