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

package org.kopi.vkopi.lib.ui.vaadin.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.form.KopiAlignment;
import org.kopi.vkopi.lib.form.VActorField;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VBooleanField;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.BlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorHandlingExtension;
import org.kopi.vkopi.lib.ui.vaadin.addons.SingleComponentBlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.FontMetrics;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.SortEvent;
import com.vaadin.event.SortEvent.SortListener;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.ColumnResizeEvent;
import com.vaadin.ui.Grid.ColumnResizeListener;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;

/**
 * Grid based chart block implementation.
 */
@SuppressWarnings("serial")
public class DGridBlock extends DBlock implements ColumnResizeListener, SortListener {
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public DGridBlock(DForm parent, VBlock model) {
    super(parent, model);
  }
  
  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  
  /**
   * Differently create fields for this block
   */
  @Override
  protected void createFields() {
    super.createFields();
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        grid = new Grid(createContainerDataSource()) {
          
          @Override
          protected void doEditItem() {
            if (!inDetailMode()) {
              updateEditors();
              super.doEditItem();
              enterRecord((Integer)getEditedItemId());
            }
          }
        };
        grid.addSortListener(DGridBlock.this);
        grid.setImmediate(true);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setEditorEnabled(model.isAccessible());
        if (grid.isEditorEnabled()) {
          grid.setEditorBuffered(false);
        }
        grid.setColumnReorderingAllowed(false);
        grid.setColumnResizeMode(ColumnResizeMode.ANIMATED);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(model.getDisplaySize());
        grid.setCellStyleGenerator(new DGridBlockCellStyleGenerator(model));
        grid.addColumnResizeListener(DGridBlock.this);
        configure();
        grid.setColumnOrder(getColumnsOrder());
        if (getDetailsGenerator() != null) {
          grid.setDetailsGenerator(getDetailsGenerator());
        }
        new GridEditorHandlingExtension() {
          
          @Override
          protected void onRowEdit(int row, int col) {
            final DGridBlockFieldUI        columnView;

            columnView = getColumnView(col);
            if (columnView != null) {
              model.getForm().performAsyncAction(new KopiAction() {

                @Override
                public void execute() throws VException {
                  if (columnView.hasDisplays() && !columnView.hasAction()) {
                    columnView.transferFocus(columnView.getEditorField());
                  }
                }
              });
            }
          }
          
          @Override
          protected void onCancelEditor() {
            if (!doNotCancelEditor && grid.isEditorEnabled() && grid.isEditorActive()) {
              grid.cancelEditor();
            }
          }
          
          @Override
          protected void onGotoFirstEmptyRecord() {
            int         rec;
            
            for (rec = model.getBufferSize() - 1; rec >= 0; rec -= 1) {
              if (model.isRecordFilled(rec)) {
                break;
              }
            }
            
            editRecord(rec + 1);
          }
          
          /**
           * Returns the column view associated with the given grid position
           * @param position The field position in the grid.
           * @return The column view.
           */
          protected DGridBlockFieldUI getColumnView(int position) {
            for (VFieldUI columnView : columnViews) {
              if (columnView != null && model.getFieldPos(columnView.getModel()) == position) {
                return (DGridBlockFieldUI)columnView;
              }
            }
            
            return null;
          }
          
        }.extend(grid);
        addComponent(grid, 0, 0, 1, 1, false, false);
        // ensures that the drop handler is set because
        // the create fields method is called in a session
        // lock context so call done in DBlock may find the
        // drag and drop wrapper null cause it is not created
        // yet.
        if (model.isDroppable()) {
          setDropHandler(new DBlockDropHandler(model));
          setDragStartMode(DragStartMode.HTML5);
        }
      }
    });
  }

  /**
   * Notifies the block that the UI is focused on the given record.
   * @param recno The record number
   */
  protected void enterRecord(final int recno) {
    model.getForm().performAsyncAction(new KopiAction() {
      
      @Override
      public void execute() throws VException {
        try {
          // go to the correct block if necessary
          if (model != model.getForm().getActiveBlock()) {
            if (model.isAccessible()) {
              model.getForm().gotoBlock(model);
            }
          }
          // go to the correct record if necessary
          // but only if we are in the correct block now
          if (model == model.getForm().getActiveBlock()
              && model.isMulti()
              && recno != model.getActiveRecord()
              && model.isRecordAccessible(recno))
          {
            model.gotoRecord(recno);
          }
        } catch (VException e) {
          // if any error occurs in the goto record process
          // we go back to the active block record again cause
          // in UI the edited record is in fact the next target record.
          // cursor moves to the next grid record before model do it cause
          // jumping between records is done by the UI.
          // see EditorHandlingExtensionConnector#CustomEventHandler.
          if (model.getActiveRecord() != -1 && model.getActiveRecord() != (Integer)grid.getEditedItemId()) {
            editRecord(model.getActiveRecord());
          }
          throw e;
        } finally {
          doNotCancelEditor = false;
        }
      }
    });
  }
  
  /**
   * Returns the detail generator to be applied to this grid block.
   * @return The detail generator to be applied to this grid block.
   */
  protected DetailsGenerator getDetailsGenerator() {
    return null;
  }
  
  @Override
  protected VFieldUI createFieldDisplay(int index, VField model) {
    return new DGridBlockFieldUI(this, model, index);
  }
  
  @Override
  public void add(UComponent comp, KopiAlignment constraints) {}
  
  @Override
  public void blockAccessChanged(VBlock block, final boolean newAccess) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (grid.isEditorActive()) {
          grid.cancelEditor();
        }
        grid.setEditorEnabled(newAccess);
        if (newAccess) {
          grid.setEditorBuffered(false);
        }
      }
    });
  }
  
  @Override
  public BlockLayout createLayout() {
    return new SingleComponentBlockLayout();
  }
  
  @Override
  protected void refresh(boolean force) {
    super.refresh(force);
    refreshAllRows();
  }

  @Override
  public int getDisplayLine(int recno) {
    return 0;
  }
  
  @Override
  public int getRecordFromDisplayLine(int line) { 
    if (grid != null && grid.getEditedItemId() != null) {
      return (Integer)grid.getEditedItemId();
    } else {
      return 0;
    }
  }
  
  @Override
  public void validRecordNumberChanged() {
    // optimized to not fire an item set change if the number
    // of valid records is not changed
    if (getModel().getNumberOfValidRecord() != lastValidRecords) {
      contentChanged();
      lastValidRecords = getModel().getNumberOfValidRecord();
    }
  }
  
  @Override
  public void filterShown() {
    if (filterRow != null) {
      return;
    }
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        filterRow = grid.appendHeaderRow();  
        filterRow.setStyleName("block-filter");
        for (final Object propertyId : (getContainerDatasource().getContainerPropertyIds())) {
          HeaderCell        cell;
          TextField         filter;
          
          cell = filterRow.getCell(propertyId);
          filter = new TextField();
          filter.setStyleName("filter-text");
          filter.setImmediate(true);
          filter.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
              if (grid.isEditorActive()) {
                grid.cancelEditor();
              }
              getContainerDatasource().removeContainerFilters(propertyId);
              if (event.getText().length() > 0) {
                getContainerDatasource().addContainerFilter(propertyId,
                                                            event.getText(),
                                                            true,
                                                            false);
              }
            }
          });
          cell.setComponent(filter);
        } 
      }
    });
  }
  
  @Override
  public void filterHidden() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (filterRow != null) {
          getContainerDatasource().removeAllContainerFilters();
          grid.removeHeaderRow(filterRow);
          filterRow = null;
        } 
      }
    });
  }
  
  @Override
  public void blockChanged() {
    refresh(true);
  }
  
  @Override
  public void blockCleared() {
    contentChanged();
    clear();
  }
  
  @Override
  public void clear() {
    cancelEditor();
    scrollToStart();
  }
  
  /**
   * Scrolls the to beginning of the block
   */
  protected void scrollToStart() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (grid != null) {
          grid.scrollToStart();
        }
      }
    });
  }
  
  /**
   * Clears the grid sort order
   */
  protected void clearSortOrder() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (grid != null) {
          grid.clearSortOrder();
        }
      }
    });
  }
  
  /**
   * Cancels the grid editor
   */
  protected void cancelEditor() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (grid != null) {
          if (grid.isEditorEnabled() && grid.isEditorActive()) {
            grid.cancelEditor();
          }
        }
      }
    });
  }
  
  @Override
  protected void fireValueChanged(int col, int rec, String value) {
    // no client side cache
  }
  
  @Override
  protected void fireColorChanged(int col, int rec, String foreground, String background) {
    // no client side cache
  }
  
  @Override
  protected void fireRecordInfoChanged(int rec, int info) {
    // no client side cache
  }
  
  @Override
  public void setSortedRecords(int[] sortedRecords) {
    if (!getModel().noDetail() && !inDetailMode()) {
      super.setSortedRecords(sortedRecords);
    }
  }
  
  @Override
  protected void fireActiveRecordChanged(int record) {
    if (!getModel().noDetail()) {
      super.fireActiveRecordChanged(record);
    }
  }
  
  @Override
  public void orderChanged() {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        if (grid != null) {
          cancelEditor();
          clearSortOrder();
          getContainerDatasource().doSort();
          contentChanged();
          if (model.getActiveRecord() != -1) {
            editRecord(model.getActiveRecord());
          }
        } 
      }
    });
  }
  
  @Override
  public void sort(SortEvent event) {
    if (event.isUserOriginated()) {
      cancelEditor();
      // update model sorted records
      for (int i = 0; i < getContainerDatasource().getAllItemIds().size(); i++) {
        model.getSortedRecords()[i] = getContainerDatasource().getAllItemIds().get(i);
      }
    }
  }

  @Override
  public void columnResize(ColumnResizeEvent event) {
    // on column resize, we cancel editor to be resized
    // cause size is CSS imposed and not refreshed until
    // editor is cancelled
    if (grid.isEditorEnabled() && grid.isEditorActive()) {
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          Object        lastEditeditemId  = grid.getEditedItemId();
          
          grid.cancelEditor();
          grid.editItem(lastEditeditemId);
        }
      });
    }
  }
  
  /**
   * Notifies the data source that the content of the block has changed.
   */
  protected void contentChanged() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        getContainerDatasource().fireContentChanged();
        // correct grid width to add scroll bar width
        if (getModel().getNumberOfValidRecord() > getModel().getDisplaySize()) {
          if (!widthAlreadyAdapted) {
            grid.setWidth(grid.getWidth() + 16, Unit.PIXELS);
            widthAlreadyAdapted = true;
          }
        }
      }
    });
  }

  /**
   * Refreshes, i.e. causes the client side to re-render all rows.
   */
  protected void refreshAllRows() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        grid.refreshAllRows();
      }
    });
  }
  
  /**
   * Configures the columns of this block
   */
  protected void configure() {
    int         width;
    
    width = 0;
    for (Column column : grid.getColumns()) {
      VField                    field;
      DGridBlockFieldUI         columnView;
      
      field = getField(column.getPropertyId());
      columnView = (DGridBlockFieldUI) columnViews[model.getFieldIndex(field)];
      if (columnView.hasDisplays()) {
        column.setHidable(false);
        column.setEditorField(columnView.getEditor());
        column.setRenderer(columnView.getEditorField().createRenderer());
        column.setConverter(columnView.getEditorField().createConverter());
        column.setSortable(field.isSortable());
        grid.getDefaultHeaderRow().getCell(column.getPropertyId()).setComponent(columnView.getEditorField().label);
        if (field.isNumeric()) {
          column.setWidth(FontMetrics.DIGIT.getWidth() * field.getWidth() + 12); // add padding
        } else {
          if (field instanceof VBooleanField) {
            column.setWidth(46); // boolean field length
          } else if (field instanceof VActorField) {
            column.setWidth(148); // actor field field length
          } else {
            column.setWidth(FontMetrics.LETTER.getWidth() * field.getWidth() + 12);// add padding
          }
        }
        column.setEditable(true);
        column.setHidden(field.getDefaultAccess() == VConstants.ACS_HIDDEN);
        width += column.getWidth();
      }
    }
    grid.setWidth(width + 16, Unit.PIXELS);
  }

  /**
   * Updates the grid editors access and content
   */
  protected void updateEditors() {
    for (VFieldUI columnView : columnViews) {
      if (columnView != null) {
        final DGridBlockFieldUI         rowController =  ((DGridBlockFieldUI)columnView);

        if (rowController.hasDisplays()) {
          rowController.getEditorField().reset();
          if (rowController.getEditorField().modelHasFocus()) {
            rowController.getEditorField().updateFocus();
          }
          rowController.getEditorField().updateAccess();
          rowController.getEditorField().updateText();
          rowController.getEditorField().updateColor();
        }
      }
    }
  }
  
  /**
   * Returns true if the grid editor is active and an item is being edited.
   * @return true if the grid editor is active and an item is being edited.
   */
  protected boolean isEditorActive() {
    return grid != null && grid.getEditedItemId() != null;
  }
  
  /**
   * Returns the edited record in this block
   * @return the edited record in this block
   */
  protected int getEditedRecord() { 
    return grid != null && grid.getEditedItemId() != null ? ((Integer)grid.getEditedItemId()).intValue() : -1;
  }
  
  /**
   * Returns the field model for a given property ID.
   * @param propertyId The column property ID.
   * @return The field model.
   */
  protected VField getField(Object propertyId) {
    return model.getFields()[(Integer)propertyId];
  }
  
  /**
   * Returns the container data source of this block.
   * @return The container data source of this block.
   */
  protected DGridBlockContainer getContainerDatasource() {
    return (DGridBlockContainer) grid.getContainerDataSource();
  }
  
  /**
   * Creates the data source container.
   * @return The data source container.
   */
  protected DGridBlockContainer createContainerDataSource() {
    return new DGridBlockContainer(model);
  }
  
  /**
   * Orders the columns by chart position in the model.
   * @return The chart position order.
   */
  protected Object[] getColumnsOrder() {
    List<Object>               columnsOrder;
    
    columnsOrder = new ArrayList<Object>(getContainerDatasource().getContainerPropertyIds());
    Collections.sort(columnsOrder, new Comparator<Object>() {

      @Override
      public int compare(Object o1, Object o2) {
        return DGridBlock.this.getField(o1).getPosition().chartPos - DGridBlock.this.getField(o2).getPosition().chartPos;
      }
    });
    
    return columnsOrder.toArray();
  }
  
  /**
   * Updates the column access for a given column identified by the associated field model.
   * @param f The field model.
   */
  protected void updateColumnAccess(final VField f, final int rec) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        if (grid != null &&  grid.getColumn(model.getFieldIndex(f)) != null) {
          grid.getColumn(model.getFieldIndex(f)).setHidden(f.getAccess(rec) == VConstants.ACS_HIDDEN);
        }
      }
    });
  }
  
  /**
   * Refreshes a given row in the data grid.
   * @param row The row index
   */
  protected void refreshRow(final int row) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        grid.refreshRows(row);
      }
    });
  }
  
  /**
   * Opens the editor interface for the provided record. 
   * @param record The record number
   */
  protected void editRecord(final int record) {
    if (grid != null) {
      itemToBeEdited = record;
      BackgroundThreadHandler.access(new Runnable() {

        @Override
        public void run() {
          if (grid.isEditorEnabled()
              && (grid.getEditedItemId() == null
              || (itemToBeEdited != null
              && (Integer)grid.getEditedItemId() != itemToBeEdited)))
          {
            if (!getContainerDatasource().containsId(itemToBeEdited)) {
              itemToBeEdited = getContainerDatasource().firstItemId();
            }
            doNotCancelEditor = true;
            if (!inDetailMode()){
              grid.editItem(itemToBeEdited);
            }
          }
        }
      });
    }
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  protected Grid                        grid;
  /*
   * We use this to fire a set item change event only when
   * the valid records is changed. It is not necessary to
   * lock the session when nothing is really changed.
   */
  private int                           lastValidRecords;
  // flag used to adapt grid width when a scroll bar is displayed
  // or when the scroll bar is removed.
  private boolean                       widthAlreadyAdapted;
  /*
   * A workaround for a Grid behavior: If two bind request are sent
   * to the grid editor, the confirm bind callback will be done only
   * for the first bind request. This is not the expected result since
   * we expect that the last bind request will be confirmed.
   * Since the session task execution is done synchronously, this item
   * will only take the last requested item to be binded and thus we force
   * the editor to bind the last record. Typically, this is used when multiple
   * gotoRecord are called via the window action queue.
   */
  private Integer                       itemToBeEdited;
  
  /*
   * A flag used to force disabling the editor cancel when scroll is fired
   * by the application and not by the user.
   * This flag is set true when application requests to edit a specific item.
   * When the item needs to be scrolled through, the editor should not be cancelled.
   * Whereas, the editor should be cancelled when the edited item is not already in
   * the port view after a scroll fired by the user.
   */
  private boolean                       doNotCancelEditor;
  private HeaderRow                     filterRow ;
}
