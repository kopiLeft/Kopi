/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.label;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DeckPanel;

/**
 * A sortable label widget that can fire sort events.
 */
public class VSortableLabel extends VLabel {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new sortable label widget.
   */
  public VSortableLabel() {
    listeners = new ArrayList<SortableLabelListener>();
    deck = new DeckPanel();
    deck.setStyleName("sort-icons");
    asc = new VImage();
    desc = new VImage();
    none = new VImage();
    asc.setHeight("10px");
    asc.setWidth("8px");
    asc.getElement().setPropertyString("align", "absmiddle");
    asc.getElement().setPropertyInt("border", 0);
    none.setHeight("10px");
    none.setWidth("8px");
    none.getElement().setPropertyString("align", "absmiddle");
    none.getElement().setPropertyInt("border", 0);
    desc.setHeight("10px");
    desc.setWidth("8px");
    desc.getElement().setPropertyString("align", "absmiddle");
    desc.getElement().setPropertyInt("border", 0);
    deck.add(none);
    deck.add(asc);
    deck.add(desc);
    deck.showWidget(0);
    deck.sinkEvents(Event.ONCLICK);
    sortMode = SORT_NONE;
    deck.addDomHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
	// switch sort
	switchSortImage();
	// fire event.
	fireOnSort();
      }
    }, ClickEvent.getType());
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the label to be sortable.
   * @param sortable is it a sortable label ?
   */
  public void setSortable(boolean sortable) {
    this.sortable = sortable;
    if (sortable) {
      addStyleDependentName("sortable");
      add(deck);
    }
  }
  
  /**
   * Returns {@code true} if the sort is activated on this label.
   * @return {@code true} if the sort is activated on this label.
   */
  public boolean isSortable() {
    return sortable;
  }
  
  /**
   * Returns the sort icon width.
   * @return the sort icon width.
   */
  public int getSortIconWidth() {
    return deck.getElement().getClientWidth();
  }
  
  /**
   * Registers a sortable label listener.
   * @param l The listener to be registered.
   */
  public void addSortableLabelListener(SortableLabelListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a sortable label listener.
   * @param l The listener to be removed.
   */
  public void removeSortableLabelListener(SortableLabelListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires on sort event.
   */
  protected void fireOnSort() {
    for (SortableLabelListener l : listeners) {
      if (l != null) {
	l.onSort(sortMode);
      }
    }
  }
  
  /**
   * Switches the sort image.
   */
  protected void switchSortImage() {
    sortMode++;
    if (sortMode > SORT_DESC) {
      sortMode = SORT_NONE;
    }
    deck.showWidget(sortMode);
  }
  
  /**
   * Sets the blank image.
   * @param src The blank image
   */
  public void setNoneImage(String src) {
    none.setSrc(src);
  }
  
  /**
   * Sets the ASC image.
   * @param src The ASC image
   */
  public void setAscImage(String src) {
    asc.setSrc(src);
  }
  
  /**
   * Sets the DESC image.
   * @param src The DESC image
   */
  public void setDescImage(String src) {
    desc.setSrc(src);
  }
  
  /**
   * Ensures that sort icons are visible
   * by applying a negative margin when
   * label width is exactly the label
   * container width.
   * @param margin The margin amount to be applied.
   */
  public void setSortIconsMargin(double margin) {
    deck.getElement().getStyle().setPaddingLeft(0, Unit.PX);
    deck.getElement().getStyle().setMarginLeft(margin, Unit.PX);
  }
  
  @Override
  public void clear() {
    super.clear();
    asc = null;
    desc = null;
    none = null;
    deck = null;
    listeners.clear();
    listeners = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VImage				asc;
  private VImage				desc;
  private VImage				none;
  private DeckPanel                             deck;
  private int 					sortMode;
  private boolean				sortable;
  private List<SortableLabelListener>           listeners;
  /**
   * Constants defining the current direction of the sort.
   */
  public static int 				SORT_NONE = 0;
  public static int 				SORT_ASC = 1;
  public static int 				SORT_DESC = 2;
}
