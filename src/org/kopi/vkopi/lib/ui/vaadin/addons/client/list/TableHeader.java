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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.list;

import java.util.ArrayList;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VAnchor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * The table header widget that supports click events
 * for rows sorting.
 */
public class TableHeader extends Composite implements ClickHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>TableHeader</code> instance.
   */
  public TableHeader() {
    initWidget(content = new HorizontalPanel());
    listeners = new ArrayList<SortListener>();
    content.setSpacing(0);
    content.setStyleName("header-content");
    deck = new DeckPanel();
    deck.setStyleName("sort-icons");
    caption = new VAnchor();
    caption.setHref("#");
    caption.setStyleName("header-caption");
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
    content.add(caption);
    caption.addClickHandler(this);
    content.add(deck);
    deck.add(none);
    deck.add(asc);
    deck.add(desc);
    deck.showWidget(0);
    sortMode = Sortable.SORT_NONE; // no sort at the beginning.
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the header caption.
   * @param caption The header caption.
   */
  public void setCaption(String caption) {
    this.caption.setText(caption);
  }
  
  /**
   * Sets the header index.
   * @param headerIndex The header index.
   */
  public void setHeaderIndex(int headerIndex) {
    this.headerIndex = headerIndex;
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
  
  @Override
  public void onClick(ClickEvent event) {
    // switch sort
    switchSortImage();
    // fire event.
    fireOnSort();
  }
  
  /**
   * Registers a sort listener on this header.
   * @param l The listener to be registered.
   */
  public void addSortListener(SortListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a sort listener from this header.
   * @param l The listener to be removed.
   */
  public void removeSortListener(SortListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires on sort event.
   */
  protected void fireOnSort() {
    for (SortListener l : listeners) {
      if (l != null) {
	l.onSort(headerIndex, sortMode);
      }
    }
  }
  
  /**
   * Switches the sort image.
   */
  protected void switchSortImage() {
    sortMode++;
    if (sortMode > Sortable.SORT_DESC) {
      sortMode = Sortable.SORT_NONE;
    }
    deck.showWidget(sortMode);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final HorizontalPanel			content;
  private final VAnchor				caption;
  private final VImage				asc;
  private final VImage				desc;
  private final VImage				none;
  private final DeckPanel			deck;
  private final ArrayList<SortListener>		listeners;
  private int					sortMode;
  private int					headerIndex;
}
