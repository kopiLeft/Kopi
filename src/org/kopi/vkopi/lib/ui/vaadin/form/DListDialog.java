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
import org.kopi.vkopi.lib.list.VBooleanCodeColumn;
import org.kopi.vkopi.lib.list.VBooleanColumn;
import org.kopi.vkopi.lib.list.VDateColumn;
import org.kopi.vkopi.lib.list.VFixnumCodeColumn;
import org.kopi.vkopi.lib.list.VFixnumColumn;
import org.kopi.vkopi.lib.list.VIntegerCodeColumn;
import org.kopi.vkopi.lib.list.VIntegerColumn;
import org.kopi.vkopi.lib.list.VMonthColumn;
import org.kopi.vkopi.lib.list.VTimeColumn;
import org.kopi.vkopi.lib.list.VTimestampColumn;
import org.kopi.vkopi.lib.list.VWeekColumn;
import org.kopi.vkopi.lib.ui.vaadin.addons.InformationNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.ListDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.ListDialogListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.NotificationListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.TableModel.ColumnType;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.visual.VApplication;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.UWindow;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.vkopi.lib.visual.VlibProperties;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * The <code>DListDialog</code> is the vaadin implementation of the
 * {@link UListDialog} specifications.
 */
@SuppressWarnings("serial")
public class DListDialog extends ListDialog implements UListDialog, ListDialogListener {

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
    addListDialogListener(this);
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
      showRelativeTo((Component)field);
    }
    showDialogAndWait();
    return handleClientResponse();
  }

  @Override
  public int selectFromDialog(UWindow window, boolean showSingleEntry) {
    return selectFromDialog(window, null, showSingleEntry);
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
	return doNewForm((VForm)model.getForm(), model.getNewForm());
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
    setModel(model.getTitles(),
             buildColumnsTypes(),
             buildColumnsAlignment(),
             model.getTranslatedIdents(),
             createModelObjects(),
             model.getCount());
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
   * Creates the data model objects.
   * @return The data model objects.
   */
  protected String[][] createModelObjects() {
    String[][]		objects = new String[model.getData().length][model.getData()[0].length];

    for (int x = 0; x < model.getData().length; x++) {
      for(int y = 0; y < model.getTranslatedIdents().length; y++) {
	objects[x][y] = model.getColumns()[x].formatObject(model.getData()[x][model.getTranslatedIdents()[y]]).toString();
      }
    }
    
    return objects;
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
   * Builds the columns alignment.
   * @return The columns alignment.
   */
  protected int[] buildColumnsAlignment() {
    int[]		aligns;
    
    aligns = new int[model.getColumns().length];
    for (int i = 0; i < model.getColumns().length; i++) {
      aligns[i] = model.getColumns()[i].getAlign();
    }
    
    return aligns;
  }
  
  /**
   * Builds the columns types.
   * @return The columns types.
   */
  protected ColumnType[] buildColumnsTypes() {
    ColumnType[]        types;
    
    types = new ColumnType[model.getColumns().length];
    for (int i = 0; i < model.getColumns().length; i++) {
      if (model.getColumns()[i] instanceof VBooleanCodeColumn
          || model.getColumns()[i] instanceof VBooleanColumn)
      {
        types[i] = ColumnType.BOOLEAN;
      } else if (model.getColumns()[i] instanceof VIntegerCodeColumn
                 || model.getColumns()[i] instanceof VIntegerColumn
                 || model.getColumns()[i] instanceof VFixnumCodeColumn
                 || model.getColumns()[i] instanceof VFixnumColumn)
      {
        types[i] = ColumnType.NUMBER;
      } else if (model.getColumns()[i] instanceof VDateColumn) {
        types[i] = ColumnType.DATE;
      } else if (model.getColumns()[i] instanceof VTimeColumn) {
        types[i] = ColumnType.TIME;
      } else if (model.getColumns()[i] instanceof VTimestampColumn) {
        types[i] = ColumnType.TIMESTAMP;
      } else if (model.getColumns()[i] instanceof VWeekColumn) {
        types[i] = ColumnType.WEEK;
      } else if (model.getColumns()[i] instanceof VMonthColumn) {
        types[i] = ColumnType.MONTH;
      } else {
        // consider all other columns as string
        types[i] = ColumnType.STRING;
      }
    }
    
    return types;
  }
  
  //------------------------------------------------------
  // LIST DIALOG LISTENER IMPLEMENTATION
  //------------------------------------------------------
  
  @Override
  public void onSelection(int selectedPos, boolean escaped, boolean doNewForm) {
    this.selectedPos = selectedPos;
    this.escaped = escaped;
    this.doNewForm = doNewForm;
    getApplication().detachComponent(this);
    BackgroundThreadHandler.releaseLock(this); // release the background thread lock.
  }
  
  //------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------
  
  private VListDialog			    model;
  private boolean                  	    escaped = true;
  private boolean               	    doNewForm;
  private int				    selectedPos = -1;
}
