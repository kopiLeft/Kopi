/*
 * Copyright (c) 1990-2018 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.visual;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.tree.TreeNode;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.l10n.LocalizationManager;

@SuppressWarnings("serial")
public class VItemTree extends VWindow {

  static {
    WindowController.getWindowController().registerWindowBuilder(Constants.MDL_ITEM_TREE, new WindowBuilder() {

      public UWindow createWindow(VWindow model) {
        return (UItemTree)UIFactory.getUIFactory().createView(model);
      }
    });
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new instance of VItemTree.
   * @param rootName the rot item name
   * @param items the items list
   * @param insertMode enable Add and Remove items
   * @param selectionType item selection mode :
   * 			1 : no edit
   * 			2 : Single selection
   * 			3 : Multi selection
   * 			4 : Multi selection with default value
   * @param localised if true, enable item localisation
   * @param itemTreeManager the tree save manager
   * @param removeDescendants if true, remove item descendants when removig item
   * @param nameLength max length of item name
   * @param localisedNameLength max length of item localisation
   * @param descriptionLength max length of item description
   */
  public VItemTree(String rootName,
                   Item[] items,
                   int depth,
                   boolean insertMode,
                   int selectionType,
                   boolean localised,
                   ItemTreeManager itemTreeManager,
                   boolean removeDescendants,
                   int nameLength,
                   int localisedNameLength,
                   int descriptionLength)
    throws VException
  {
    super();
    this.items = items;
    this.depth = depth;
    this.insertMode = insertMode;
    this.selectionType = selectionType;
    this.localised = localised;
    this.itemTreeManager = itemTreeManager;
    this.removeDescendants = removeDescendants;
    this.nameLength = nameLength;
    this.localisedNameLength = localisedNameLength;
    this.descriptionLength = descriptionLength;
    this.rootName = rootName != null ? rootName : "Items";
    this.changed = false;
    this.removedItems = new ArrayList();
    if (rootName != null) {
      setTitle(rootName);
    }
    actors = new VActor[10];
    createActor(CMD_QUIT, "File", "Close", "quit", 0 /*KeyEvent.VK_ESCAPE*/, 0);
    createActor(CMD_SELECT, "Edit", "Select", "done", KeyEvent.VK_ENTER, 0);
    createActor(CMD_ADD, "Edit", "AddItem", "insertline", 0, 0);
    createActor(CMD_REMOVE, "Edit", "RemoveItem", "deleteline", 0, 0);
    createActor(CMD_EDIT, "Edit", "EditItem", "edit", 0, 0);
    createActor(CMD_FOLD, "Edit", "Fold", "fold", KeyEvent.VK_ENTER, 0);
    createActor(CMD_UNFOLD, "Edit", "Unfold", "unfold", KeyEvent.VK_ENTER, 0);
    createActor(CMD_DEFAULT, "Edit", "Default", "top", 0, 0);
    createActor(CMD_LOCALISE, "Edit", "Localise", "bold", 0, 0);
    createActor(CMD_SAVE, "Edit", "Save", "save", 0, 0);
    setActors(actors);
    localizeActors(ApplicationContext.getDefaultLocale());
    createTree();
    if (insertMode) {
      initMaxId();
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------

  /**
   * Localize an actor
   *
   * @param     locale  the locale to use
   */
  public void localizeActors(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, Locale.getDefault());
    try {
      super.localizeActors(manager); // localizes the actors in VWindow
    } catch (InconsistencyException e) {
      ApplicationContext.reportTrouble("ItemTree Actor localization",
                                       "ItemTreeModel.localize",
                                       e.getMessage(),
                                       e);
      System.exit(1);
    }
    manager = null;
  }

  /**
   * Enables or disable the given actor
   */
  public void setActorEnabled(int actor, boolean enabled) {
    actors[actor].setHandler(this);
    actors[actor].setEnabled(enabled);
  }

  /**
   * Returns the actor having the given number.
   */
  public VActor getActor(int number) {
    return actors[number];
  }

  /**
   * Creates a new actor
   */
  private void createActor(int number,
                           String menu,
                           String item,
                           String icon,
                           int key,
                           int modifier)
  {
    actors[number] = new VActor(menu,
                                MENU_LOCALIZATION_RESOURCE,
                                item,
                                MENU_LOCALIZATION_RESOURCE,
                                icon,
                                key,
                                modifier);
    actors[number].setNumber(number);
  }

  /**
   * Performs the appropriate action.
   *
   * @param   key           the number of the actor.
   */
  public void executeVoidTrigger(final int key) throws VException {
    UItemTree  currentDisplay = getDisplay();

    switch (key) {
    case CMD_QUIT:
      if (isChanged()) {
        if (ask(Message.getMessage("confirm_quit"), false)) {
          currentDisplay.closeWindow();
        }
      } else {
        currentDisplay.closeWindow();
      }
      break;
    case CMD_SELECT:
      currentDisplay.setSelectedItem();
      break;
    case CMD_ADD:
      currentDisplay.addItem();
      refresh();
      break;
    case CMD_REMOVE:
      if (ask(Message.getMessage("confirm_delete"), false)) {
        currentDisplay.removeSelectedItem();
        refresh();
      }
      break;
    case CMD_EDIT:
      currentDisplay.editSelectedItem();
      refresh();
      break;
    case CMD_FOLD:
      currentDisplay.getTree().collapseRow(currentDisplay.getTree().getSelectionRow());
      break;
    case CMD_UNFOLD:
      currentDisplay.getTree().expandRow(currentDisplay.getTree().getSelectionRow());
      break;
    case CMD_DEFAULT:
      currentDisplay.setDefaultItem();
      break;
    case CMD_LOCALISE:
      currentDisplay.localiseSelectedItem();
      refresh();
      break;
    case CMD_SAVE:
      if (isChanged() && itemTreeManager != null) {
        setWaitInfo("");
        itemTreeManager.save();
        unsetWaitInfo();
        setChanged(false);
        if (currentDisplay != null) {
          currentDisplay.setTree();
        }
      }
      break;
    default:
      super.executeVoidTrigger(key);
    }
  }

  /**
   * Refresh the item Tree view
   */
  public void refresh() {
    setChanged(true);
    ((UItemTree)getDisplay()).setTree();
  }

  /**
   * Builds the item tree.
   */
  @SuppressWarnings("deprecation")
  private void createTree() throws VException{
    rootItem = new RootItem(-1, rootName);
    rootItem.createTree(items);

    root = rootItem.getRoot();
  }

  /**
   * Return items array
   */
  public Item[] getItems() {
    return getDisplay().getTree().getItems();
  }

  /**
   * Return the root item
   */
  public Item getRootItem() {
    return getDisplay().getTree().getRootItem();
  }

  /**
   * Init value of max ID
   */
  private void initMaxId() {
    maxId = -1;
    for (int i = 0; i < items.length; i++) {
      if (maxId < items[i].getId()) {
        maxId = items[i].getId();
      }
    }
  }

  /**
   * Returns true if the model is changed
   */
  public boolean isChanged() {
    return changed;
  }

  /**
   * Set model changed
   */
  public void setChanged(boolean changed) {
    this.changed = changed;
  }

  /**
   * Returns true if removing descendants is alowed
   */
  public boolean isRemoveDescendantsAlowed() {
    return removeDescendants;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the root Item of this tree.
   * @return The root node of this tree.
   */
  public TreeNode getRoot() {
    return root;
  }

  public int getType() {
    return Constants.MDL_ITEM_TREE;
  }

  public UItemTree getDisplay() {
    return (UItemTree) super.getDisplay();
  }

  public boolean isInsertMode() {
    return insertMode;
  }

  public boolean isNoEdit() {
    return selectionType == NO_EDIT;
  }

  public boolean isSingleSelection() {
    return selectionType == SINGLE_SELECTION;
  }

  public boolean isMultiSelection() {
    return selectionType == MULTI_SELECTION;
  }

  public boolean isMultiSelectionDefaultValue() {
    return selectionType == MULTI_SELECTION_DEFAULT_VALUE;
  }

  public boolean isLocalised() {
    return localised;
  }

  /**
   * Return the current item name length
   */
  public int getNameLength() {
    return nameLength;
  }

  /**
   * Return the current item localised name length
   */
  public int getLocalisedNameLength() {
    return localisedNameLength;
  }

  /**
   * Return the current item descripton length
   */
  public int getDescriptonLength() {
    return descriptionLength;
  }

  /**
   * Return the item tree depth
   */
  public int getDepth() {
    return depth;
  }

  public int getNextId() {
    maxId ++;
    return maxId;
  }

  /**
   * Return the removed items list
   */
  public List<Item> getRemovedItems () {
    return removedItems;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private TreeNode                      root;
  private RootItem                      rootItem;
  private VActor[]                      actors;
  private Item[]                        items;
  private int                           depth;
  private int                           maxId;
  private String                        rootName;
  private boolean                       insertMode;
  private int                           selectionType;
  private boolean                       localised;
  private ItemTreeManager               itemTreeManager;
  private boolean                       removeDescendants;
  private int                           nameLength;
  private int                           localisedNameLength;
  private int                           descriptionLength;
  private boolean                       changed;
  private List<Item>                    removedItems;

  public static final int               MAX_LENGTH = 32;

  public static final int               NO_EDIT                         = 1;
  public static final int               SINGLE_SELECTION                = 2;
  public static final int               MULTI_SELECTION                 = 3;
  public static final int               MULTI_SELECTION_DEFAULT_VALUE   = 4;

  public static final int               CMD_QUIT                        = 0;
  public static final int               CMD_SELECT                      = 1;
  public static final int               CMD_DEFAULT                     = 2;
  public static final int               CMD_FOLD                        = 3;
  public static final int               CMD_UNFOLD                      = 4;
  public static final int               CMD_SAVE                        = 5;
  public static final int               CMD_LOCALISE                    = 6;
  public static final int               CMD_EDIT                        = 7;
  public static final int               CMD_ADD                         = 8;
  public static final int               CMD_REMOVE                      = 9;


  private static final String           MENU_LOCALIZATION_RESOURCE      = "org/kopi/vkopi/lib/resource/Menu";
}
