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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorHandlingExtension;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.GridConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.DefaultEditorEventHandler;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.events.ScrollEvent;
import com.vaadin.client.widget.grid.events.ScrollHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.EditorDomEvent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

import elemental.json.JsonObject;

/**
 * Grid editor handling extension connector.
 */
@Connect(value = GridEditorHandlingExtension.class, loadStyle = LoadStyle.DEFERRED)
@SuppressWarnings({ "serial" })
public class EditorHandlingExtensionConnector extends AbstractExtensionConnector {
  
  @Override
  protected void extend(ServerConnector target) {
    if (getParentWidget() != null) {
      getParentWidget().getEditor().setEventHandler(new CustomEventHandler());
      lastTopVisibleRow =-1;
      scrollHandler = getParentWidget().getEscalator().addScrollHandler(new ScrollHandler() {
        
        @Override
        public void onScroll(ScrollEvent event) {
          if (getParentWidget().getEditor().getRow() != -1) {
            int         topRow = (int)(getParentWidget().getEscalator().getScrollTop() / getParentWidget().getEscalator().getBody().getDefaultRowHeight());

            if (topRow != lastTopVisibleRow && getParentWidget().getEditor().getRow() < topRow) {
              getRpcProxy(EditorHandlingExtensionServerRpc.class).cancelEditor();
              lastTopVisibleRow = topRow;
            }
          }
        }
      });
    }
  }

  @Override
  public void onUnregister() {
    if (getParentWidget() != null) {
      getParentWidget().getEditor().setEventHandler(new DefaultEditorEventHandler<JsonObject>());
    }
    if (scrollHandler != null) {
      scrollHandler.removeHandler();
    }
    super.onUnregister();
  }

  @Override
  public GridConnector getParent() {
    return (GridConnector) super.getParent();
  }
  
  /**
   * Returns the parent grid widget of this extension.
   * @return The parent grid widget of this extension.
   */
  protected Grid<JsonObject> getParentWidget() {
    if (getParent() != null) {
      return getParent().getWidget();
    } else {
      return null;
    }
  }
  
  /**
   * Customized event handler for grid editor
   */
  private final class CustomEventHandler extends DefaultEditorEventHandler<JsonObject> {
    
    @Override
    protected boolean isOpenEvent(EditorDomEvent<JsonObject> event) {
      return event.getDomEvent().getTypeInt() == Event.ONCLICK  || isTouchOpenEvent(event);
    }
    
    @Override
    protected boolean handleOpenEvent(EditorDomEvent<JsonObject> event) {
      if (isOpenEvent(event)) {
        getRpcProxy(EditorHandlingExtensionServerRpc.class).editRow(getParent().getRowKey(event.getCell().getRow()), getParent().getColumnId(event.getCell().getColumn()));
      }
      return super.handleOpenEvent(event);
    }

    @Override
    protected boolean handleMoveEvent(EditorDomEvent<JsonObject> event) {
      Event                                     e = event.getDomEvent();
      final EventCellReference<JsonObject>      cell = event.getCell();
      
      // TODO: Move on touch events
      if (e.getTypeInt() == Event.ONCLICK) {
        getRpcProxy(EditorHandlingExtensionServerRpc.class).editRow(getParent().getRowKey(cell.getRow()), getParent().getColumnId(cell.getColumn()));
        editRow(event, cell.getRowIndex(), cell.getColumnIndexDOM());
        return true;
      } else if (e.getTypeInt() == Event.ONKEYDOWN) {
        int             rowDelta = 0;

        if (e.getKeyCode() == KeyCodes.KEY_END) {
          getRpcProxy(EditorHandlingExtensionServerRpc.class).gotoFirstEmptyRecord();
          return true;
        } else if (e.getKeyCode() == KeyCodes.KEY_PAGEDOWN) {
          rowDelta = 1;
        } else if (e.getKeyCode() == KeyCodes.KEY_PAGEUP) {
          rowDelta = -1;
        }

        final boolean   changed = rowDelta != 0;

        if (changed) {
          int           columnCount = event.getGrid().getVisibleColumns().size();
          int           colIndex = event.getFocusedColumnIndex();
          int           rowIndex = event.getRowIndex();

          // Handle row change with horizontal move when column goes out
          // of range.
          e.preventDefault();
          if (rowDelta == 0) {
            if (colIndex >= columnCount && rowIndex < event.getGrid().getDataSource().size() - 1) {
              rowDelta = 1;
              colIndex = 0;
            } else if (colIndex < 0 && rowIndex > 0) {
              rowDelta = -1;
              colIndex = columnCount - 1;
            }
          }

          editRow(event, rowIndex + rowDelta, colIndex);
        }

        return changed;
      }
      
      return true;
    }
  }
  
  private HandlerRegistration           scrollHandler;
  private int                           lastTopVisibleRow;
}
