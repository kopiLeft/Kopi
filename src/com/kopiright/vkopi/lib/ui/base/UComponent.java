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
package com.kopiright.vkopi.lib.ui.base;

/**
 * {@code UComponent} is the top-level interface that is and must be implemented
 * by all kopi visual components.
 */
public interface UComponent {

  /**
   * Tests whether the component is enabled or not. A user can not interact
   * with disabled components. Disabled components are rendered in a style
   * that indicates the status, usually in gray color. Children of a disabled
   * component are also disabled. Components are enabled by default.
   * @see #setEnabled(boolean)
   */
  public boolean isEnabled();

  /**
   * Enables or disables the component. The user can not interact disabled
   * components, which are shown with a style that indicates the status,
   * usually shaded in light gray color. Components are enabled by default.
   * @param enabled a boolean value specifying if the component should be enabled or not.
   * @see #isEnabled()
   */
  public void setEnabled(boolean enabled);


  /**
   * Tests the <i>visibility</i> property of the component.
   *
   * <p>
   * Visible components are drawn in the user interface, while invisible ones
   * are not.
   * </p>
   *
   * <p>
   * A component is visible only if all its parents are also visible. This is
   * not checked by this method though, so even if this method returns true,
   * the component can be hidden from the user because a parent is set to
   * invisible.
   * </p>
   *
   * @return <code>true</code> if the component has been set to be visible in
   * the user interface, <code>false</code> if not
   * @see #setVisible(boolean)
   */
  public boolean isVisible();

  /**
   * Sets the visibility of the component.
   *
   * <p>
   * Visible components are drawn in the user interface, while invisible ones
   * are not.
   * </p>
   *
   * <p>
   * A component is visible only if all of its parents are also visible. If a
   * component is explicitly set to be invisible, changes in the visibility of
   * its parents will not change the visibility of the component.
   * </p>
   *
   * @param visible the boolean value specifying if the component should be
   * visible after the call or not.
   * @see #isVisible()
   */
  public void setVisible(boolean visible);
}
