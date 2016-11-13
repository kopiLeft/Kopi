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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.date;

import java.util.Date;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.annotations.NoLayout;

/**
 * The date chooser state.
 */
@SuppressWarnings("serial")
public class DateChooserState extends AbstractComponentState {

  /**
   * The selected date. This date will be focused when the date pane appears.
   */
  @NoLayout
  public Date				selected;
  
  /**
   * The offset regarding to UTC time zone.
   */
  @NoLayout
  public int				offset;
  
  /**
   * The reference widget. Should be not null for a date chooser.
   */
  @NoLayout
  public Connector			reference;
  
  /**
   * The date chooser locale.
   */
  @NoLayout
  public String				locale;
  
  /**
   * Localized today caption.
   */
  @NoLayout
  public String				today;
}
