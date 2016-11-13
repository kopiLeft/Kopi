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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;

/**
 * A representation of a single horizontal scroll bar.
 * @see HorizontalScrollbarBundle#getElement()
 */
public final class HorizontalScrollbarBundle extends ScrollbarBundle {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void setStylePrimaryName(String primaryStyleName) {
    super.setStylePrimaryName(primaryStyleName);
    root.addClassName(primaryStyleName + "-scroller-horizontal");
  }

  @Override
  protected void internalSetScrollPos(int px) {
    root.setScrollLeft(px);
  }

  @Override
  protected int internalGetScrollPos() {
    return root.getScrollLeft();
  }

  @Override
  protected void internalSetScrollSize(double px) {
    scrollSizeElement.getStyle().setWidth(px, Unit.PX);
  }

  @Override
  protected String internalGetScrollSize() {
    return scrollSizeElement.getStyle().getWidth();
  }

  @Override
  protected void internalSetOffsetSize(double px) {
    root.getStyle().setWidth(px, Unit.PX);
  }

  @Override
  public String internalGetOffsetSize() {
    return root.getStyle().getWidth();
  }

  @Override
  protected void internalSetScrollbarThickness(double px) {
    root.getStyle().setPaddingBottom(px, Unit.PX);
    root.getStyle().setHeight(0, Unit.PX);
    scrollSizeElement.getStyle().setHeight(px, Unit.PX);
  }

  @Override
  protected String internalGetScrollbarThickness() {
    return scrollSizeElement.getStyle().getHeight();
  }

  @Override
  protected void internalForceScrollbar(boolean enable) {
    if (enable) {
      root.getStyle().setOverflowX(Overflow.SCROLL);
    } else {
      root.getStyle().clearOverflowX();
    }
  }

  @Override
  public Direction getDirection() {
    return Direction.HORIZONTAL;
  }
}
