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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.form;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VCaption;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VTabSheet;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.FormListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.PositionPanelListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * The form widget filled with blocks
 */
public class VForm extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the form widget.
   */
  public VForm() {
    listeners = new ArrayList<FormListener>();
    setStyleName(Styles.FORM);
    blockInfo = new VPositionPanel();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Initializes the form widget content.
   * @param connection The application connection.
   * @param locale The application locale.
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  public void init(ApplicationConnection connection,
                   String locale,
                   int pageCount,
                   String[] titles,
                   String separator)
  { 
    createBlockInfoWidget(connection, locale);
    pages = new VPage[pageCount == 0 ? 1 : pageCount];
    for (int i = 0; i < pages.length; i++) {
      if (pageCount != 0) {
	if (titles[i].endsWith("<CENTER>")) {
	  pages[i] = new VPage(new HorizontalPanel());
	} else {
	  pages[i] = new VPage(new VerticalPanel());
	}
      } else {
	pages[i] = new VPage(new VerticalPanel());
      }
    }
    // setPages content.
    setContent(pageCount, titles, separator);
  }
  
  /**
   * Creates the block info widget.
   * @param connection The application connection.
   * @param locale The application locale.
   */
  protected void createBlockInfoWidget(ApplicationConnection connection, String locale) {
    VWindow		window;
    
    window = WidgetUtils.getParent(this, VWindow.class);
    if (window != null) {
      blockInfo.setClient(connection);
      blockInfo.setLocale(locale);
      blockInfo.setVisible(false); // hide it initially
      window.setFooter(blockInfo);
      window.setFooterAlignment(HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
    }
  }
  
  /**
   * Sets the form content.
   * @param pageCount The page count.
   * @param titles The pages titles.
   */
  protected void setContent(int pageCount, String[] titles, String separator) {
    if (pageCount == 0) {
      setWidget(pages[0]);
    } else {
      tabPanel = new VTabSheet(separator);
      tabPanel.setStyleName(Styles.FORM_TAB_PANEL);
      for (int i = 0; i < pages.length; i++) {
	tabPanel.addTab(createCaption(titles[i]), pages[i]);
	tabPanel.setTabEnabled(i, false);
      }
      
      tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
        
        @Override
        public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
          // before leaving page, send current focused text field value to the server side. 
          if (fireSelectionEvent) {
            event.cancel();
            firePageSelected(event.getItem().intValue());
          }
        }
      });
      
      setWidget(tabPanel);
    }
  }
  
  /**
   * Selects the given page
   * @param page The page index.
   */
  public void selectPage(final int page) {
    // we delay the page selection event since it can
    // cause browser freeze when a field focus is called
    // at the same time.
    new Timer() {
      
      @Override
      public void run() {
        if (tabPanel != null) {
          fireSelectionEvent = false;
          // for some reasons, the page ability event is fired after
          // the page selection event. A workaround is to enable the 
          // selected page before to allow selection.
          setEnabled(true, page);
          tabPanel.selectTab(page);
          fireSelectionEvent = true;
        }
      }
    }.schedule(40);
  }
  
  /**
   * Creates a page caption.
   * @param title The page title.
   * @return The page caption
   */
  protected VCaption createCaption(String title) {
    VCaption		caption;
    
    caption = new VCaption();
    caption.setCaption(title.endsWith("<CENTER>") ? title.substring(0,  title.length() - 8) : title);
    return caption;
  }
  
  /**
   * Sets the page enabled or disabled.
   * @param enabled the page ability.
   * @param page The page index.
   */
  public void setEnabled(boolean enabled, int page) {
    if (tabPanel != null) {
      tabPanel.setTabEnabled(page, enabled);
    }
  }
  
  /**
   * Adds a block widget to this form.
   * @param block The block widget.
   * @param page The page number.
   */
  public void addBlock(Widget block, int page, boolean isFollow, boolean isChart) {
    HorizontalAlignmentConstant 	hAlign;
    VerticalAlignmentConstant 		vAlign;
    
    if (isChart) {
      hAlign = HasHorizontalAlignment.ALIGN_CENTER;
      vAlign = HasVerticalAlignment.ALIGN_TOP;
    } else {
      hAlign = HasHorizontalAlignment.ALIGN_LEFT;
      vAlign = HasVerticalAlignment.ALIGN_TOP;
    }
    if (isFollow) {
      pages[page].addFollow(block, hAlign, vAlign);
    } else {
      pages[page].add(block, hAlign, vAlign);
    }
  }
  
  /**
   * Sets the the position panel position.
   * @param current The first record.
   * @param total The last record.
   */
  public void setPosition(int current, int total) {
    if (blockInfo != null) {
      blockInfo.setPosition(current, total);
    }
  }
  
  /**
   * Registers a form listener.
   * @param l The listener to be registered.
   */
  public void addFormListener(FormListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a form listener.
   * @param l The listener to be removed.
   */
  public void removeFormListener(FormListener l) {
    listeners.remove(l);
  }
  
  /**
   * Registers a position panel listener.
   * @param l The listener to be registered.
   */
  public void addPositionPanelListener(PositionPanelListener l) {
    if (blockInfo != null) {
      blockInfo.addPositionPanelListener(l);
    }
  }
  
  /**
   * Removes a position panel listener.
   * @param l The listener to be removed.
   */
  public void removePositionPanelListener(PositionPanelListener l) {
    if (blockInfo != null) {
      blockInfo.removePositionPanelListener(l);
    }
  }
  
  /**
   * Fires a page selection event.
   * @param page The page index.
   */
  protected void firePageSelected(int page) {
    for (FormListener l : listeners) {
      if (l != null) {
	l.onPageSelection(page);
      }
    }
  }
  
  @Override
  public void clear() {
    super.clear();
    listeners.clear();
    listeners = null;
    for (VPage page : pages) {
      page.release();
    }
    pages = null;
    if (tabPanel != null) {
      tabPanel.clear();
    }
    tabPanel = null;
    blockInfo.clear();
    blockInfo = null;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<FormListener>		        listeners;
  private VPage[]				pages;
  private VTabSheet				tabPanel;
  private boolean				fireSelectionEvent = true;
  private VPositionPanel			blockInfo;
}
