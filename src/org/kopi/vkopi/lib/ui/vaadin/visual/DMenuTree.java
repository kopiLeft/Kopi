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

package org.kopi.vkopi.lib.ui.vaadin.visual; 

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.base.Tree;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.Module;
import org.kopi.vkopi.lib.visual.UMenuTree;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VMenuTree;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.base.Query;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;

/**
 * The <code>DMenuTree</code> is the vaadin implementation of the
 * {@link UMenuTree}.
 * 
 * <p>The implementation is based on {@link DWindow}</p>
 * 
 * TODO Externalize favorites handling.
 */
public class DMenuTree extends DWindow implements UMenuTree, Handler {

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
    ADD_BOOKMARK = new Action(model.getActor(VMenuTree.CMD_ADD).menuItem);
    REMOVE_BOOKMARK =  new Action(model.getActor(VMenuTree.CMD_REMOVE).menuItem);
    if(!model.isSuperUser()) {
      // if we are not in a super user context, the menu is
      // handled by the module menu component.
      // The menu tree is handled differently comparing to swing
      // version.
      // The tree component is used only in a super user context.
    } else {
      Panel		content;
      
      tree = new Tree(model.getRoot(), model.isSuperUser());
      content = new Panel(tree);
      tree.addActionHandler(this);
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
  	  // tree.restoreLastModifiedItem();
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
      // allow scrolling when an overflow is detected
      content.setWidth(310, Unit.PIXELS);
      content.setHeight(410, Unit.PIXELS);
      setContent(content);
    }
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  /**
   * Adds the given module to favorites.
   * @param module The module to be added to favorites.
   */
  public void addShortcut(final Module module) {
    if (!getModel().getShortcutsID().contains(module.getId())) {
      getModel().getShortcutsID().add(module.getId());
      addShortcutsInDatabase(module.getId());
    }
  }
  
  /**
   * Removes the given module from favorites.
   * @param module The module to be removed from favorites.
   */
  public void removeShortcut(final Module module) {
    if (getModel().getShortcutsID().contains(module.getId())) {
      getModel().getShortcutsID().remove(new Integer(module.getId()));
      removeShortcutsFromDatabase(module.getId());
    }
  }

  /**
   * Add a favorite into database.
   */
  protected void addShortcutsInDatabase(int id) {
    try {
      getModel().getDBContext().startWork();    // !!! BEGIN_SYNC
      new Query(getModel()).run("INSERT INTO FAVORITEN VALUES ("
                                + "{fn NEXTVAL(FAVORITENId)}" + ", "
                                + (int)(System.currentTimeMillis()/1000) + ", "
                                + getModel().getUserID() + ", "
                                + id
                                + ")");

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
   * Remove favorite from database.
   */
  protected void removeShortcutsFromDatabase(int id) {
    try {
      getModel().getDBContext().startWork();    // !!! BEGIN_SYNC
      new Query(getModel()).run("DELETE FROM FAVORITEN WHERE Benutzer = " + getModel().getUserID() + " AND Modul = " + id);
      getModel().getDBContext().commitWork();
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        getModel().getDBContext().abortWork();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * Move the focus from the activated frame to favorites frame.
   */
  @Override
  public void gotoShortcuts() {}

  @Override
  public void addSelectedElement() {
    final Module      module = getSelectedModule();
    
    if (module != null) {
      addShortcut(module);
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
    Module    		module = getSelectedModule();

    getModel().setActorEnabled(VMenuTree.CMD_QUIT, !((VMenuTree) getModel()).isSuperUser());
    getModel().setActorEnabled(VMenuTree.CMD_INFORMATION, true);
    getModel().setActorEnabled(VMenuTree.CMD_HELP, true);
    if (module != null) {
      ((VMenuTree) getModel()).setToolTip(module.getHelp());
      getModel().setActorEnabled(VMenuTree.CMD_SHOW, getModel().getShortcutsID().size() > 0);
      if (module.getObject() != null) {
        getModel().setActorEnabled(VMenuTree.CMD_OPEN, true);
        getModel().setActorEnabled(VMenuTree.CMD_ADD, !getModel().getShortcutsID().contains(module.getId()));
        getModel().setActorEnabled(VMenuTree.CMD_REMOVE, getModel().getShortcutsID().contains(module.getId()));
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
    final Module      module = getSelectedModule();
    
    if (module != null) {
      removeShortcut(module);
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
      return null;
    }
  }

  @Override
  public void run() {
    setVisible(true);
    if (getModel().isSuperUser()) {
      tree.focus();
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
  public final void closeWindow() {
    if (!((VMenuTree) getModel()).isSuperUser()) {
      getModel().ask(Message.getMessage("confirm_quit"), false);
    }
  }
  
  @Override
  public Action[] getActions(Object target, Object sender) {
    List<Action>                actions;
    
    actions = new ArrayList<Action>();
    if (target != null) {
      Module            module;
      
      module = getModuleByID(((Integer)target).intValue());
      if (module == null) {
        return null;
      }
      if (!getModel().getShortcutsID().contains(target)) {
        if (module.getObject() != null) {
          actions.add(ADD_BOOKMARK);
        }
      } else {
        actions.add(REMOVE_BOOKMARK);
      }
    }
    
    return actions.toArray(new Action[actions.size()]);
  }

  @Override
  public void handleAction(Action action, Object sender, Object target) {
    if (target != null) {
      if (action == ADD_BOOKMARK) {
        addShortcutsInDatabase(((Integer)target).intValue());
        getModel().getShortcutsID().add((Integer) target);
      } else if (action == REMOVE_BOOKMARK) {
        removeShortcutsFromDatabase(((Integer)target).intValue());
        getModel().getShortcutsID().remove(target);
      }
      markAsDirtyRecursive();
    }
  }
  
  /**
   * Returns the module having the given ID.
   * @param id The module ID.
   * @return The module object.
   */
  private Module getModuleByID(int id) {
    for (int i = 0; i < ((VMenuTree) getModel()).getModuleArray().length; i++) {
      if (((VMenuTree) getModel()).getModuleArray()[i].getId() == id) {
        return ((VMenuTree) getModel()).getModuleArray()[i];
      }
    }

    return null;
  }
  
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  @Override
  public UBookmarkPanel getBookmark() {
    return null;
  }
  
  @Override
  public VMenuTree getModel() {
    return (VMenuTree) super.getModel();
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

  private Tree                                  tree;
  private final Action                          ADD_BOOKMARK;
  private final Action                          REMOVE_BOOKMARK;
  private static final long                     serialVersionUID = -6740174181163603800L;
}
