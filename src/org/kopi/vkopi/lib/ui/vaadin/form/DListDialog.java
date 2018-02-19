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

import org.kopi.vkopi.lib.form.UField;
import org.kopi.vkopi.lib.form.UListDialog;
import org.kopi.vkopi.lib.form.VDictionary;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.form.VListDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridListDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridListDialog.CloseListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridListDialog.SearchListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridListDialog.SelectionListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.InformationNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.NotificationListener;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.list.ListTable;
import org.kopi.vkopi.lib.ui.vaadin.visual.VApplication;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.UWindow;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.vkopi.lib.visual.VlibProperties;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Grid.ColumnReorderEvent;
import com.vaadin.ui.Grid.ColumnReorderListener;
import com.vaadin.ui.UI;

/**
 * The <code>DListDialog</code> is the vaadin implementation of the
 * {@link UListDialog} specifications.
 */
@SuppressWarnings("serial")
public class DListDialog extends GridListDialog implements UListDialog, CloseListener, SelectionListener, SearchListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DListDialog</code> instance.
   * @param model The list dialog model.
   */
  public DListDialog(VListDialog model) {
    setImmediate(true);
    this.model = model;
    addCloseListener(this);
    addSelectionListener(this);
    addSearchListener(this);
  }

  //---------------------------------------------------
  // LISTDIALOG IMPLEMENTATION
  //---------------------------------------------------

  @Override
  public int selectFromDialog(UWindow window, UField field, boolean showSingleEntry) {
    if (!showSingleEntry && model.getCount() == 1) {
      return model.convert(0);
    }

    // too many rows case
    if (model.isTooManyRows()) {
      handleTooManyRows();
    }
    prepareDialog(); // prepares the dialog data.
    if (field != null) {
      // show the dialog beside the field.
      // otherwise show it centered.
      if (field instanceof DField) {
        showRelativeTo((DField)field);
      } else if (field instanceof DGridEditorField<?>) {
        showRelativeTo(((DGridEditorField<?>)field).getEditor());
      }
    }
    showDialogAndWait();
    return handleClientResponse();
  }

  @Override
  public int selectFromDialog(UWindow window, boolean showSingleEntry) {
    return selectFromDialog(window, null, showSingleEntry);
  }

  @Override
  public void onClose(CloseEvent event) {
    doSelectFromDialog(-1, event.isEscaped(), event.isNewForm());
  }

  @Override
  public void onSelection(SelectionEvent event) {
    if (table.getContainerDataSource().size() == 0) {
      return;
    }

    ensureTableSelection();
    switch (event.getTarget()) {
    case CURRENT_ROW:
      doSelectFromDialog((Integer)table.getSelectedRow(), false, false);
      break;
    case NEXT_ROW:
      table.select(getNextItemId());
      break;
    case PREVIOUS_ROW:
      table.select(getPrevItemId());
      break;
    case NEXT_PAGE:
      table.select(getNextPageItemId());
      break;
    case PREVIOUS_PAGE:
      table.select(getPrevPageItemId());
      break;
    case FIRST_ROW:
      table.select(table.getContainerDataSource().firstItemId());
      break;
    case LAST_ROW:
      table.select(table.getContainerDataSource().lastItemId());
      break;
    default:
      // noting to do
    }
  }

  @Override
  public void onSearch(SearchEvent event) {
    if (!table.getContainerDataSource().hasFilters()) {
      if (event.getPattern() == null || event.getPattern().length() == 0) {
        ensureTableSelection();
      } else {
        Object            itemId;

        itemId = table.search(event.getPattern());
        if (itemId != null) {
          table.select(itemId);
        }
      }
    }
  }

  /**
   * Ensures that a row is selected in the list dialog table.
   * The selected row will be set to the first visible row when
   * the selected row is null
   */
  protected void ensureTableSelection() {
    if (table.getSelectedRow() == null) {
      table.select(table.getContainerDataSource().firstItemId());
    }
  }

  /**
   * Returns the next item ID according to the currently selected one.
   * @return The next item ID according to the currently selected one.
   */
  protected Integer getNextItemId() {
    if ((Integer)table.getSelectedRow() == table.getContainerDataSource().lastItemId()) {
      return table.getContainerDataSource().lastItemId();
    } else {
      return table.getContainerDataSource().nextItemId(table.getSelectedRow());
    }
  }

  /**
   * Returns the previous item ID according to the currently selected one.
   * @return The previous item ID according to the currently selected one.
   */
  protected Integer getPrevItemId() {
    if ((Integer)table.getSelectedRow() == table.getContainerDataSource().firstItemId()) {
      return table.getContainerDataSource().firstItemId();
    } else {
      return table.getContainerDataSource().prevItemId(table.getSelectedRow());
    }
  }

  /**
   * Looks for the next page item ID starting from the selected row.
   * @return The next page item ID.
   */
  protected Integer getNextPageItemId() {
    Integer             nextPageItemId;

    nextPageItemId = (Integer)table.getSelectedRow();
    for (int i = 0; i < 20 && nextPageItemId != table.getContainerDataSource().lastItemId(); i++) {
      nextPageItemId = table.getContainerDataSource().nextItemId(nextPageItemId);
    }

    return nextPageItemId;
  }

  /**
   * Looks for the previous page item ID starting from the selected row.
   * @return The previous page item ID.
   */
  protected Integer getPrevPageItemId() {
    Integer             prevPageItemId;

    prevPageItemId = (Integer)table.getSelectedRow();
    for (int i = 0; i < 20 && prevPageItemId != table.getContainerDataSource().firstItemId(); i++) {
      prevPageItemId = table.getContainerDataSource().prevItemId(prevPageItemId);
    }

    return prevPageItemId;
  }

  //------------------------------------------------------
  // UTILS
  //------------------------------------------------------

  /**
   * Handles the client response after thread release.
   * @return The selected position.
   */
  protected int handleClientResponse() {
    if (escaped) {
      return -1;
    } else if (doNewForm) {
      try {
	return doNewForm(model.getForm(), model.getNewForm());
      } catch (VException e) {
	throw new VRuntimeException(e);
      }
    } else if (selectedPos != -1) {
      return model.convert(selectedPos);
    }

    return -1; // in all other cases return -1 indicating no choice.
  }

  /**
   * Displays a window to insert a new record
   * @param form The {@link VForm} instance.
   * @param cstr The class path.
   * @return The selected item.
   * @throws VException Visual errors.
   */
  protected int doNewForm(final VForm form, final VDictionary cstr) throws VException {
    if (form != null && cstr != null) {
      return cstr.add(form);
    } else {
      return VListDialog.NEW_CLICKED;
    }
  }

  /**
   * Prepares the dialog content.
   */
  protected void prepareDialog() {
    table = new ListTable(model);
    setTable(table);
    table.select(table.getContainerDataSource().firstItemId());
    table.addItemClickListener(new ItemClickListener() {

      @Override
      public void itemClick(ItemClickEvent event) {
        doSelectFromDialog((Integer) event.getItemId(), false, false);
      }
    });
    table.addSelectionListener(new com.vaadin.event.SelectionEvent.SelectionListener() {

      @Override
      public void select(com.vaadin.event.SelectionEvent event) {
        if (!event.getSelected().isEmpty()) {
          table.scrollTo(event.getSelected().toArray()[0]);
        }
      }
    });
    table.addColumnReorderListener(new ColumnReorderListener() {

      @Override
      public void columnReorder(ColumnReorderEvent event) {
        sort();
      }
    });
    // set the new button if needed.
    if (model.getNewForm() != null || model.isForceNew()) {
      setNewText(VlibProperties.getString("new-record"));
    }
  }

  /**
   * Shows the dialog and wait until it is closed from client side.
   */
  protected void showDialogAndWait() {
    BackgroundThreadHandler.startAndWait(new Runnable() {

      @Override
      public void run() {
	getApplication().attachComponent(DListDialog.this);
	UI.getCurrent().push(); // push is need cause this is not enclosed in a locked session
      }
    }, this);
  }

  /**
   * Returns the current application instance.
   * @return Tshe current application instance.
   */
  protected VApplication getApplication() {
    return (VApplication) ApplicationContext.getApplicationContext().getApplication();
  }

  /**
   * Handles the too many rows case.
   * This will show a user notification.
   */
  protected void handleTooManyRows() {
    final InformationNotification		notice;
    final Object				lock;

    lock = new Object();
    notice = new InformationNotification(VlibProperties.getString("Notice"), MessageCode.getMessage("VIS-00028"));
    notice.addNotificationListener(new NotificationListener() {

      @Override
      public void onClose(boolean yes) {
        getApplication().detachComponent(notice);
        BackgroundThreadHandler.releaseLock(lock);
      }
    });
    notice.setLocale(getApplication().getDefaultLocale().toString());
    BackgroundThreadHandler.startAndWait(new Runnable() {

      @Override
      public void run() {
        getApplication().attachComponent(notice);
        getApplication().push();
      }
    }, lock);
  }

  /**
   * Confirms the user selection and closes the list.
   * @param selectedPos The selected position.
   * @param escaped Was the list escaped ?
   * @param doNewForm Should we do a new dictionary form ?
   */
  protected void doSelectFromDialog(int selectedPos, boolean escaped, boolean doNewForm) {
    this.selectedPos = selectedPos;
    this.escaped = escaped;
    this.doNewForm = doNewForm;
    getApplication().detachComponent(this);
    BackgroundThreadHandler.releaseLock(this); // release the background thread lock.
  }

  /**
   * Bubble sort the columns from right to left
   */
  private void sort() {
    int       left = 0;
    int       sel = -1;

    if (table != null) {
      if (table.getSelectedRow() != null) {
        sel = (Integer)table.getSelectedRow();
      } else {
        sel = 0;
      }
      left = (Integer)table.getColumns().get(0).getPropertyId();
    }

    model.sort(left);

    if (table != null) {
      table.tableChanged();
      table.select(sel);
    }
  }

  //------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------

  private VListDialog                   model;
  private ListTable                     table;
  private boolean                       escaped = true;
  private boolean                       doNewForm;
  private int                           selectedPos = -1;
}
