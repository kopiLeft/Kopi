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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.form.UField;
import com.kopiright.vkopi.lib.form.UListDialog;
import com.kopiright.vkopi.lib.form.VDictionaryForm;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.form.VListDialog;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.UWindow;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnReorderEvent;
import com.vaadin.ui.Table.ColumnReorderListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * The <code>DListDialog</code> is the vaadin implementation of the
 * {@link UListDialog} specifications.
 */
@SuppressWarnings("serial")
public class DListDialog extends Panel implements UListDialog, ItemClickListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DListDialog</code> instance.
   * @param model The list dialog model.
   */
  public DListDialog(VListDialog model) {
    setImmediate(true);
    addStyleName(KopiTheme.PANEL_LIGHT);
    setSizeUndefined();
    this.model = model;
    table = new Table();
    table.setImmediate(true);
    table.addStyleName(KopiTheme.TABLE_BORDERLESS);
    table.addStyleName(KopiTheme.LIST_TABLE);
    table.setColumnReorderingAllowed(true);
    table.setSortEnabled(true);
  }
  
  //---------------------------------------------------
  // LISTDIALOG IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public int selectFromDialog(UWindow window, final UField field, final boolean showSingleEntry) {   
    try {
      /*
       * Case : TooManyRows
       * if (model.isTooManyRows()) {
       * }
       */
      
      if (!showSingleEntry && model.getCount() == 1) {
	return model.convert(0);
      }
      
      popup = new Window();
      
      if (field != null) {
	popup.setCaption(field.getModel().getLabel());
      } else if (window != null) {
	popup.setCaption(window.getModel().getTitle());
      }
      build();
      popup.addCloseListener(new CloseListener() {
	       
        @Override
        public void windowClose(CloseEvent e) {
	  BackgroundThreadHandler.releaseLock(popup);
        }
      });
      BackgroundThreadHandler.startAndWait(new Runnable() {
        
	@Override
        public void run() {
	  prepareAndShowPopup();
        }
      }, popup);
      
      if (escaped) {
	return -1;
      } else if (doNewForm) {
	return doNewForm((VForm)model.getForm(), model.getNewForm());
      } else {
	if (table.getValue() != null){
          return model.convert(((Integer)table.getValue()).intValue());
	} else {
	  return -1;
	}
      }
    } catch (VException v) {
      throw new VRuntimeException(v);
    } 
  }
  
  @Override
  public int selectFromDialog(final UWindow window, final boolean showSingleEntry) {  
    try {
      popup = new Window();
      if (window != null) {
	popup.setCaption(window.getModel().getTitle());
      }
      build();
      popup.addCloseListener(new CloseListener() {
	       
        @Override
        public void windowClose(CloseEvent e) {
          BackgroundThreadHandler.releaseLock(popup);
        }
      });
      BackgroundThreadHandler.startAndWait(new Runnable() {
        
	@Override
        public void run() {
	  prepareAndShowPopup();
        }
      }, popup);	 
	
      if (escaped) {
	return -1;
      } else if (doNewForm) {
	return doNewForm((VForm)model.getForm(), model.getNewForm());
      } else {
	if (table.getValue() != null) {
	  return model.convert(((Integer)table.getValue()).intValue());
	} else {
	  return -1;
	}
      }
    } catch (VException v) {
      throw new VRuntimeException(v);
    }    
  }
  
  //------------------------------------------------------
  // UTILS
  //------------------------------------------------------
  
  /**
   * Displays a window to insert a new record
   * @param form The {@link VForm} instance.
   * @param cstr The class path.
   * @return The selected item.
   * @throws VException Visual errors.
   */
  private int doNewForm(final VForm form, final String cstr) throws VException {
    if (form != null && cstr != null) {
      return ((VDictionaryForm)Module.getKopiExecutable(cstr)).newRecord(form);
    } else {
      return VListDialog.NEW_CLICKED;
    }
  }
  
  /**
   * Builds the display
   */
  private void build() {
    int         tableWidth   = 0;
    
    table.setSelectable(true);
    table.setVisible(true);
    table.setMultiSelect(false);
    table.setMultiSelectMode(MultiSelectMode.SIMPLE);
    table.setColumnCollapsingAllowed(false);
    table.setColumnReorderingAllowed(true);
    table.setEditable(false);
    table.setContainerDataSource(new ListDialogContainer(model.getTitles(),
	                                                 model.getData(),
	                                                 model.getCount()));
    //table.select(0);Fix shortcuts first
    
    table.setColumnHeaders(model.getTitles());
    
    for (int i = 0; i < model.getColumnCount(); i++) {
      tableWidth += model.getSizes()[i] + 10;
    }

    tableWidth = Math.max(tableWidth, (model.getColumnCount() * 44));
    
    table.setWidth(tableWidth + 15, Unit.EX);
    if (model.getCount() < 23) {
      table.setPageLength(model.getCount()); 
      table.setHeight(((model.getCount() + 1 ) * 30) + 10 ,Unit.PIXELS); 
    } else {
      table.setPageLength(22); 
      table.setHeight((23 * 30) + 10 ,Unit.PIXELS); 
    }
    
    table.addItemClickListener(this);
    table.addColumnReorderListener(new ColumnReorderListener() {

      @Override
      public void columnReorder(ColumnReorderEvent event) {
        model.sort();
      }
    });
    
    listContent = new VerticalLayout();
    
    //content.setSizeFull();
    listContent.addStyleName(KopiTheme.PANEL_LISTDIALOG);
    listContent.setVisible(true);
    listContent.addComponent(table);
    listContent.setComponentAlignment(table, Alignment.TOP_CENTER);
    
    if (model.getNewForm() != null || model.isForceNew()) {
      Button		newRecord = new Button(VlibProperties.getString("new-record"));

      newRecord.addStyleName("small");
      newRecord.setVisible(true);
      newRecord.addClickListener(new ClickListener() {

	@Override
        public void buttonClick(ClickEvent event) {
          doNewForm = true;
          escaped = false;
          popup.close();
        }
      });
     
      listContent.addComponent(newRecord);
      listContent.setComponentAlignment(newRecord, Alignment.BOTTOM_CENTER);
    }
    
    table.addShortcutListener(new ShortcutListener(null, KeyCode.ENTER, null) {
      
      @Override
      public void handleAction(Object sender, Object target) {
	if(table.getValue() != null){
          escaped = false;
          popup.close();
          BackgroundThreadHandler.releaseLock(popup);
	}
      }
    });
    
    table.addShortcutListener(new ShortcutListener(null, KeyCode.ESCAPE, null) {
      
      @Override
      public void handleAction(Object sender, Object target) {
        escaped = true;
        popup.close();
        BackgroundThreadHandler.releaseLock(popup);
      }
    });
    
    table.addShortcutListener(new ShortcutListener(null, KeyCode.SPACEBAR, null) {
      
      @Override
      public void handleAction(Object sender, Object target) {
        if (model.getNewForm() != null || model.isForceNew()) {
          escaped = false;
          doNewForm = true;
          popup.close();
          BackgroundThreadHandler.releaseLock(popup);
        }
      }
    });
    
    popup.setContent(listContent);
  }
  
  //---------------------------------------------------
  // SHOW LIST DIALOG
  //---------------------------------------------------
  
  /**
   * Prepares the popup dimensions and position and then show it.
   */
  protected void prepareAndShowPopup() {  
    if (table.getWidth() > 600) {
      popup.setWidth("95%");
      table.setWidth("100%");
    }
    
    popup.setModal(true);
    popup.setImmediate(true);
    popup.center();
    popup.setDraggable(true);
    popup.setResizable(false);
    popup.setClosable(true);
    popup.addStyleName(KopiTheme.LIST_STYLE);
    popup.setVisible(true);
    UI.getCurrent().addWindow(popup);	
    table.focus();
    BackgroundThreadHandler.updateUI();
  }
  
  //---------------------------------------------------
  // ITEMCLICKLISTENER IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void itemClick(final ItemClickEvent event) {
    escaped = false;
    table.setValue(event.getItemId());
    popup.close();
    BackgroundThreadHandler.releaseLock(popup);
  }
 
  //---------------------------------------------------
  // DATA MODEL
  //---------------------------------------------------
  
  /**
   * The <code>ListDialogContainer</code> is the list dialog table data model.
   */
  public final class ListDialogContainer extends AbstractInMemoryContainer<Integer, String, ListDialogContainer.ListDialogContainerItem> implements Sortable {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>ListDialogContainer</code> instance.
     * @param columns The columns names.
     * @param data The list dialog data.
     * @param count The row count.
     */
    public ListDialogContainer(String[] columns,
	                       Object[][] data,
	                       int count)
    { 
      if (data.length != 0 && count > data[0].length) {
        throw new InconsistencyException("UNEXPECTED DIFFERENT SIZE IN SELECTi DIALOG");
      }
      
      if (columns != null) {
        if (data.length > columns.length) {
          throw new InconsistencyException("UNEXPECTED DIFFERENT SIZE IN SELECT DIALOG");
        }
      }
      
      this.columns = columns;
      this.data = data;
      this.count = count;
    }
    
    @Override
    public Collection<?> getContainerPropertyIds() {
      if (propertyIds == null) {
	propertyIds = makeIds(columns.length);
      }
      
      return propertyIds;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
      if (!(itemId instanceof Integer)) {
	return null;
      }
      
      if (((Integer)itemId).intValue() > count
	  || ((Integer)itemId).intValue() < 0) 
      {
	return null;
      }
      
      return new ListDialogContainerProperty((Integer)itemId, ((Integer)propertyId).intValue());
    }
    
    @Override
    public Class<?> getType(Object propertyId) {
      return String.class;
    }
    
    @Override
    protected ListDialogContainer.ListDialogContainerItem getUnfilteredItem(Object itemId) {
      if (!(itemId instanceof Integer)) {
	return null;
      }
      
      if (((Integer)itemId).intValue() > count
	  || ((Integer)itemId).intValue() < 0) 
      {
	return null;
      }
      
      return new ListDialogContainerItem((Integer)itemId);
    }
    
    @Override
    public int size() {
      return count - (model.isSkipFirstLine() ? 1 : 0);
    }
    
    @Override
    protected List<Integer> getAllItemIds() {
      if (itemIds == null) {
	itemIds = makeIds(this.count);
      }
      
      return itemIds;
    }
    
    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {
      sortContainer(propertyId, ascending);
    }
    
    @Override
    public Collection<?> getSortableContainerPropertyIds() {
      return getSortablePropertyIds();
    }
    
    /**
     * Builds the list of row IDs.
     * @param count The row count.
     * @return the list of row IDs.
     */
    private List<Integer> makeIds(int count) {
      List<Integer> ids = new ArrayList<Integer>();
      
      for (int itemId = 0; itemId < count; itemId++) {
	ids.add(itemId);
      }
      
      return ids;
    }
    
    //-------------------------------------------------
    // INNER CLASSES
    //-------------------------------------------------
    
    /**
     * The list dialog {@link Item} implementation.
     */
    private final class ListDialogContainerItem implements Item {

      //-------------------------------------
      // CONSTRUCTOR
      //-------------------------------------
      
      /**
       * Creates a new <code>ListDialogContainerItem</code> instance.
       * @param itemId The item ID.
       */
      public ListDialogContainerItem(int itemId) {
	this.itemId = itemId;
      }

      //-----------------------------------------------
      // IMPLEMENTATIONS
      //-----------------------------------------------
      
      @SuppressWarnings("rawtypes")
      @Override
      public Property getItemProperty(Object id) {
	return new ListDialogContainerProperty(itemId, ((Integer)id).intValue());
      }
      
      @Override
      public Collection<?> getItemPropertyIds() {
	if (propertyIds == null) {
	  propertyIds = makeIds(columns.length);
	}
	
	return propertyIds;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
	throw new UnsupportedOperationException("Adding new property is not supported");
      }
      
      @Override
      public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
	throw new UnsupportedOperationException("Removing new property is not supported");
      }

      @Override
      public String toString() {
	String	retValue = "";

	for (String propertyId : columns) {
	  retValue += getItemProperty(propertyId).toString();
	  
	  if (!propertyId.equals(columns[columns.length - 1])) {
	    retValue += " ";
	  }
	}

	return retValue;
      }

      @Override
      public int hashCode() {
	return itemId;
      }

      @Override
      public boolean equals(Object obj) {
	if (obj == null
	    || !obj.getClass().equals(ListDialogContainerItem.class))
	{
	  return false;
	}
	
	final ListDialogContainerItem	li = (ListDialogContainerItem) obj;
	return getHost() == li.getHost() && itemId == li.itemId;
      }

      /**
       * Returns the item host.
       * @return The item host.
       */
      private ListDialogContainer getHost() {
	return ListDialogContainer.this;
      }
      
      //-------------------------------------
      // DATA MEMBERS
      //-------------------------------------
      
      private final int			itemId;
    }
    
    /**
     * A list dialog {@link Property}  implementation.
     */
    @SuppressWarnings("rawtypes")
    private final class ListDialogContainerProperty implements Property {
      
      //-------------------------------------
      // CONSTRUCTOR
      //-------------------------------------
      
      /**
       * Creates a new <code>ListDialogContainerProperty</code> instance.
       * @param itemId The item ID.
       * @param propertyId The property ID.
       */
      public ListDialogContainerProperty(int itemId, int propertyId) {
	this.itemId = itemId;
	this.propertyId = propertyId;
      }
      
      //-------------------------------------
      // IMPLEMENTATIONS
      //-------------------------------------
      
      @Override
      public Object getValue() {
	return model.getColumns()[propertyId].formatObject(data[propertyId][model.getTranslatedIdents()[itemId]]);
      }
      
      @Override
      public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	throw new ReadOnlyException("The property is in read only mode");
      }
      
      @Override
      public Class<?> getType() {
	return String.class;
      }
      
      @Override
      public boolean isReadOnly() {
	return true;
      }
      
      @Override
      public void setReadOnly(boolean newStatus) {}
      
      @Override
      public String toString() {
	return getValue() == null ? null : getValue().toString();
      }

      @Override
      public int hashCode() {
	return itemId ^ propertyId;
      }

      @Override
      public boolean equals(Object obj) {
	if (obj == null
	    || !obj.getClass().equals(ListDialogContainerProperty.class))
	{
	  return false;
	}
	
	final	ListDialogContainerProperty lp = (ListDialogContainerProperty) obj;
	return lp.getHost() == getHost()
	       && lp.propertyId == propertyId
	       && lp.itemId == itemId;
      }

      /**
       * Returns the host container.
       * @return The host container.
       */
      private ListDialogContainer getHost() {
	return ListDialogContainer.this;
      }
      
      //-------------------------------------
      // DATA MEMBERS
      //-------------------------------------
      
      private final int			   itemId;
      private final int			   propertyId;
    }
    
    //------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------
    
    private String[] 				columns;
    private Object[][] 				data;
    private int				        count;
    private List<Integer>			itemIds;
    private List<Integer>			propertyIds;
  }
  
  //------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------
  
  private VListDialog			    model;
  private VerticalLayout	            listContent;
  private Window		            popup;
  private Table			            table;
  private boolean                  	    escaped = true;
  private boolean               	    doNewForm;
}
