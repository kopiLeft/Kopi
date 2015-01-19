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

package com.kopiright.vkopi.lib.ui.vaadin.visual; 

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.kopiright.vkopi.lib.ui.vaadin.base.HorizontalMenu;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Tree;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.UMenuTree;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VMenuTree;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.xkopi.lib.base.Query;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;

/**
 * The <code>DMenuTree</code> is the vaadin implementation of the
 * {@link UMenuTree}.
 * 
 * <p>The implementation is based on {@link DWindow}</p>
 */
public class DMenuTree extends DWindow implements UMenuTree {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------

  /**
   * Creates a new <code>DMenuTree</code> instance from a model.
   * @param model The menu tree model.
   */
  @SuppressWarnings("serial")
  public DMenuTree(VMenuTree model) {
    super(model);
    shortcuts = new Hashtable<Module, Shortcut>();
    modules = new ArrayList<Module>();
    orderdShorts = new ArrayList<Handler>();
    if(!model.isSuperUser()){
      menu = new HorizontalMenu(model.getRoot());
      menu.setAutoOpen(true);
      toolbar = new BookmarkPanel();

      for (int i = 0; i < ((VMenuTree) getModel()).getShortcutsID().size() ; i++) {
        int       id = ((Integer)((VMenuTree) getModel()).getShortcutsID().get(i)).intValue();

        for (int j = 0; j < ((VMenuTree) getModel()).getModuleArray().length; j++) {
	  if (((VMenuTree) getModel()).getModuleArray()[j].getId() == id) {
	    addShortcut(((VMenuTree) getModel()).getModuleArray()[j]);
	  }
        }
      }
    
     // getMenuBar().addFavoriteMenu(VlibProperties.getString("toolbar-title")); /*Hedi*/
    
      if (!((VMenuTree) getModel()).getShortcutsID().isEmpty()) {
        toolbar.show();
        toolbar.toFront();
      }
      
      org.kopi.vaadin.menubar.MenuBar.Command launchForm = new org.kopi.vaadin.menubar.MenuBar.Command() {
      
        @Override
        public void menuSelected(final org.kopi.vaadin.menubar.MenuBar.MenuItem selectedItem) {
          if (!selectedItem.hasChildren()) {
	    selectedMenuItem = selectedItem;
	    org.kopi.vaadin.menubar.MenuBar.MenuItem breadCrumbitem = selectedItem;
	    breadCrumb = new BreadCrumb(breadCrumbitem.getText());
	
	    while (breadCrumbitem.getParent() != null) {
	      breadCrumb.addItem(breadCrumbitem.getParent().getText());
	      breadCrumbitem = breadCrumbitem.getParent();
	    }
	    callSelectedForm(); 
          } 
        }
      };
 
      for(int i = 0; i < menu.getItems().size(); i++) {
        if(!menu.getItems().get(i).hasChildren()) {  
	  menu.getItems().get(i).setCommand(launchForm);
        }else{ 
    	  for(int j = 0; j < menu.getItems().get(i).getSize(); j++) {
    	    if(!menu.getItems().get(i).getChildren().get(j).hasChildren()) {
    	      menu.getItems().get(i).getChildren().get(j).setCommand(launchForm);
    	    }else{
    	      for(int k = 0; k < menu.getItems().get(i).getChildren().get(j).getSize(); k++) {
    	        menu.getItems().get(i).getChildren().get(j).getChildren().get(k).setCommand(launchForm); 
    	      }    
    	    }
    	  }   
        }
      }
    
      model.setDisplay(this);
      menu.setWidth("100%");
      setContent(menu);
    } else {
      tree = new Tree(model.getRoot(), model.isSuperUser());
      tree.addStyleName(KopiTheme.TREE_MENU);

      for (int i = 0; i < ((VMenuTree) getModel()).getShortcutsID().size() ; i++) {
        int       id = ((Integer)((VMenuTree) getModel()).getShortcutsID().get(i)).intValue();

        for (int j = 0; j < ((VMenuTree) getModel()).getModuleArray().length; j++) {
  	  if (((VMenuTree) getModel()).getModuleArray()[j].getId() == id) {
  	    addShortcut(((VMenuTree) getModel()).getModuleArray()[j]);
  	  }
        }
      }
      
      tree.addItemClickListener(new ItemClickHandler());
      tree.addCollapseListener(new CollapseHandler());
      tree.addExpandListener(new ExpandHandler());
      tree.addItemSetChangeListener(new ItemSetChangeListener() {
        
        public void containerItemSetChange(ItemSetChangeEvent event) {
          setMenu();
        }
      });
      
      tree.addValueChangeListener(new ValueChangeListener() {

        public void valueChange(ValueChangeEvent event) {
  	  Object	itemId = event.getProperty().getValue();
  	
  	  if (itemId == null) {
  	    return;
  	  }
  	  //tree.restoreLastModifiedItem();
  	  tree.setIcon(itemId,
  	               !tree.areChildrenAllowed(itemId),
  	               tree.getParent(itemId) == null,
  	               tree.getModule(itemId).getAccessibility());
  	  setMenu();
        }
      });
      
      model.setDisplay(this);
      tree.setSizeUndefined();
      tree.expandRow(0);
      setMenu();
      tree.setValue(null);
      tree.setNullSelectionAllowed(false);
      setContent(tree);
    }
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  
  /**
   * Adds the given module to favorites
   */
  @SuppressWarnings({ "serial", "unused" })
  public void addShortcut(final Module module) {
    if (!shortcuts.containsKey(module)) {
      
      Command command = new Command() {
        
        @Override
        public void menuSelected(MenuItem selectedItem) {
          setWaitInfo(VlibProperties.getString("menu_form_started"));
	  getModel().performAsyncAction(new KopiAction("menu_form_started") {
	    public void execute() throws VException {
	      module.run(getModel().getDBContext());
	      unsetWaitInfo();
	    }
	  });  
	}
      };

//      toolbar.addShortcut(command);
      //getMenuBar().addFavoriteMenuItem(module.getDescription(), command, (Image) module.getIcon()); /*Hedi*/
      modules.add(module);
    }
  }
  
  /**
   * Removes the given module from favorites.
   * @param module The module to remove its shortcut.
   */
  public void removeShortcut(final Module module) {
    if (shortcuts.containsKey(module)) {
      Shortcut    	removed;
      
      modules.remove(module);
      removed = (Shortcut) shortcuts.remove(module);
      orderdShorts.remove(removed);
    }
  }

  /**
   * Resets all favorites.
   */
  public void resetShortcutsInDatabase() {
    try {
      getModel().getDBContext().startWork();    // !!! BEGIN_SYNC
      new Query(getModel()).run("DELETE FROM FAVORITEN WHERE Benutzer = " + getModel().getUserID());
      for (int i = 0; i < modules.size(); i++) {
        Module  module = (Module)modules.get(i);

        new Query(getModel()).run("INSERT INTO FAVORITEN VALUES ("
                                  + "{fn NEXTVAL(FAVORITENId)}" + ", "
                                  + (int)(System.currentTimeMillis()/1000) + ", "
                                  + getModel().getUserID() + ", "
                                  + module.getId()
                                  + ")");
      }

      getModel().getDBContext().commitWork();
    } catch (SQLException e) {
      try {
        getModel().getDBContext().abortWork();
      } catch (SQLException ef) {
        ef.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  /**
   * Move the focus from the activated frame to favorites frame.
   */
  @Override
  public void gotoShortcuts() {
    if (toolbar.isVisible()) {
      toolbar.setVisible(false);
    }
    
    toolbar.setVisible(true);
    toolbar.toFront();
  }

  @Override
  public void addSelectedElement() {
    Module      module = getSelectedModule();

    if (module != null && module.getObject() != null) {
      addShortcut(module);
      resetShortcutsInDatabase();
    }
  }
  
  /**
   * Launches the selected form in the menu tree.
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   */
  @Override
  public void launchSelectedForm() throws VException {
    final Module      module = getSelectedModule();
    if (module != null) {
      if (getModel().isSuperUser()) {
	if (tree.getParent(tree.getValue()) != null) {
	  module.setAccessibility((module.getAccessibility() + 1) % 3);
	  tree.setIcon(module.getAccessibility(), module.getId(), module.getObject() != null);
	}
      } else if (module.getObject() != null) {
	setWaitInfo(VlibProperties.getString("menu_form_started"));
	module.run(getModel().getDBContext());
	unsetWaitInfo();
      } else {
	if (tree.isExpanded(tree.getValue())) {
	  tree.collapseItem(tree.getValue());
	} else {
	  tree.expandItem(tree.getValue());
	}
      }
    }
  }
  
  @Override
  public void setMenu() {	
    Module    module = getSelectedModule();

    getModel().setActorEnabled(VMenuTree.CMD_QUIT, !((VMenuTree) getModel()).isSuperUser());
    getModel().setActorEnabled(VMenuTree.CMD_INFORMATION, true);
    getModel().setActorEnabled(VMenuTree.CMD_HELP, true);
    
    if (module != null) {
      ((VMenuTree) getModel()).setToolTip(module.getHelp());
      getModel().setActorEnabled(VMenuTree.CMD_SHOW, shortcuts.size() > 0);
      if (module.getObject() != null) {
	getModel().setActorEnabled(VMenuTree.CMD_OPEN, true);
	getModel().setActorEnabled(VMenuTree.CMD_ADD, !shortcuts.containsKey(module));
	getModel().setActorEnabled(VMenuTree.CMD_REMOVE, shortcuts.containsKey(module));
	getModel().setActorEnabled(VMenuTree.CMD_FOLD, false);
	getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, false);
      } else {
	getModel().setActorEnabled(VMenuTree.CMD_OPEN, ((VMenuTree) getModel()).isSuperUser());
	getModel().setActorEnabled(VMenuTree.CMD_ADD, false);
	if (tree.isExpanded(tree.getValue())) {
	  getModel().setActorEnabled(VMenuTree.CMD_FOLD, true);
	  getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, false);
	} else {
	  getModel().setActorEnabled(VMenuTree.CMD_FOLD, false);
	  getModel().setActorEnabled(VMenuTree.CMD_UNFOLD, true);
	}
      }
    }
  }

  @Override
  public void removeSelectedElement() {
    Module      module = getSelectedModule();

    if (module != null && module.getObject() != null) {
      removeShortcut(module);
      resetShortcutsInDatabase();
    }
  }
  
  /**
   * Returns the selected module.
   * @return The selected module.
   */
  public Module getSelectedModule() {
    if (getModel().isSuperUser()) {
      return tree.getModule(tree.getValue());
    } else {
      return menu.getModule(selectedMenuItem);
    }
  }

  @Override
  public void run() {
    setVisible(true);
    if (getModel().isSuperUser()) {
      tree.focus();
    } else {
      menu.focus();
    }
  }

  /**
   * Calls the selected form in the tree menu.
   */
  private void callSelectedForm() {
    getModel().performAsyncAction(new KopiAction("menu_form_started2") {
      
      @Override
      public void execute() throws VException {
	launchSelectedForm();
      }
    });
  }

  /**
   * Called to close the view (from the user), it does not
   * Definitely close the view(it may ask the user before)
   * Allowed to call outside the event dispatch thread
   */
  @Override
  public final void closeWindow() {//Fix Me
    if (!((VMenuTree) getModel()).isSuperUser()) {
      //  ((VApplication) VApplicationContext.getApplicationContext().getApplication()).quit();//in close listener for ask dialog   
      askUser(Message.getMessage("confirm_quit"));
    }
  }
  
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the {@link HorizontalMenu} object.
   * @return The {@link HorizontalMenu} object.
   */
  public HorizontalMenu getMenu() {
    return menu;
  }

  @Override
  public UBookmarkPanel getBookmark() {
    return toolbar;
  }
  
  @Override
  public VMenuTree getModel() {
    return (VMenuTree) super.getModel();
  }

  public static BreadCrumb getBreadCrumb() {
    return breadCrumb; //!!! FIXME Why it is static ? to check.
  }
  
  //------------------------------------------------------------
  // INNER CLASSES
  //------------------------------------------------------------
  
  /**
   * A menu shortcut.
   */
  /*package*/ final class Shortcut {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------

    /**
     * Creates a new <code>Shortcut</code> instance.
     * @param handler The action handler.
     * @param menubar The tree menu bar.
     */
    public Shortcut(Handler handler, MenuBar menubar) {
      this.handler = handler;
      this.menubar = menubar;
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------

    /*package*/ final Handler		handler;
    /*package*/ final MenuBar		menubar;
  }
   
  /**
   * A dummy implementation of the {@link UBookmarkPanel}.
   */
  /*package*/ final class BookmarkPanel implements UBookmarkPanel {

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
      // nothing to do
    }

    @Override
    public boolean isVisible() {
      return true;
    }

    @Override
    public void setVisible(boolean visible) {
      // nothing to do
    }

    @Override
    public void show() {
      setVisible(true);
    }

    @Override
    public void toFront() {
      // nothing to do
    }
  }
 
  // --------------------------------------------------
  // UMenuTree IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  public UTree getTree() {
    return tree;
  }
  
  // --------------------------------------------------
  // LISTENERS
  // --------------------------------------------------
  
  /**
   * The <code>ItemClickHandler</code> is the menu tree implementation
   * of the {@link ItemClickListener}.
   */
  @SuppressWarnings("serial")
  private final class ItemClickHandler implements ItemClickListener {

    @Override
    public void itemClick(ItemClickEvent event) {
      tree.setValue(event.getItemId());
      
      if (event.isDoubleClick()) {
	callSelectedForm();
      } else {
	setMenu();
      }
    }
  }
  
  /**
   * The <code>CollapseHandler</code> is the menu tree implementation
   * of the {@link CollapseListener}.
   */
  @SuppressWarnings("serial")
  private final class CollapseHandler implements CollapseListener {
    
    @Override
    public void nodeCollapse(CollapseEvent event) {
      tree.setValue(event.getItemId());
      tree.valueChanged();
    }
  }
  
  /**
   * The <code>ExpandHandler</code> is the menu tree implementation
   * of the {@link ExpandListener}.
   */
  @SuppressWarnings("serial")
  private final class ExpandHandler implements ExpandListener {

    @Override
    public void nodeExpand(ExpandEvent event) {
      tree.setValue(event.getItemId());
      tree.valueChanged();
    }
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  private HorizontalMenu				menu;
  private BookmarkPanel        				toolbar;
  private Hashtable<Module, Shortcut>           	shortcuts;
  private List<Handler>             			orderdShorts;
  private List<Module>                  		modules;
  public static BreadCrumb                      	breadCrumb; //!!! FIXME why it is static ?
  private Tree						tree;
  private org.kopi.vaadin.menubar.MenuBar.MenuItem	selectedMenuItem;
  private static final long 				serialVersionUID = -6740174181163603800L;
}
