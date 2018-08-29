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

package org.kopi.vkopi.lib.ui.vaadin.visual;

import java.util.Collection;

import org.kopi.vkopi.lib.ui.vaadin.addons.PopupWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.Item;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.UItemTree;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.vkopi.lib.visual.VItemTree;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.vkopi.lib.visual.VlibProperties;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;

/**
 * The <code>DItemTree</code> is the vaadin implementation of the
 * {@link UItemTree}.
 *
 * <p>The implementation is based on {@link DWindow}</p>
 *
 * TODO Externalize favorites handling.
 */
public class DItemTree extends DWindow implements UItemTree {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------

  /**
   * Creates a new <code>DItemTree</code> instance from a model.
   * @param model The item tree model.
   */
  @SuppressWarnings("serial")
  public DItemTree(VItemTree model) {
    super(model);
    Panel               content;

    tree = new Tree(model.getRoot(), model.isNoEdit(), model.isLocalised());
    content = new Panel(tree);
    content.setStyleName("itemtree");

    tree.addItemClickListener(new ItemClickHandler());
    tree.addCollapseListener(new CollapseHandler());
    tree.addExpandListener(new ExpandHandler());
    tree.addItemSetChangeListener(new ItemSetChangeListener() {

      public void containerItemSetChange(ItemSetChangeEvent event) {
        setTree();
      }
    });

    tree.addValueChangeListener(new ValueChangeListener() {

      public void valueChange(ValueChangeEvent event) {
        Object          itemId = event.getProperty().getValue();

        if (itemId == null) {
          return;
        }
        Item    item = tree.getITEM(itemId);

        tree.setIcon(item, isLeaf());
        setTree();
      }
    });

    model.setDisplay(this);
    tree.setSizeUndefined();
    if (model.isNoEdit()) {
      tree.expandRow(0);
    } else {
      tree.expandTree();
    }
    setTree();
    tree.setValue(null);
    tree.setNullSelectionAllowed(false);
    // allow scrolling when an overflow is detected
    content.setWidth(900, Unit.PIXELS);
    content.setHeight(800, Unit.PIXELS);
    setContent(content);
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  /**
   * Sets item selection state.
   */
  @Override
  public void setSelectedItem() throws VException {
    final Item          item = getSelectedItem();

    if (item != null) {
      if (!getModel().isNoEdit()) {
        if (getModel().isSingleSelection()) {
          if (!item.isSelected()) {
            unselectAll(-1);
            item.setSelected(true);
          } else {
            item.setSelected(false);
          }
        } else {
          item.setSelected(!item.isSelected());
        }
        tree.setIcon(item);
        getModel().refresh();
      } else {
        if (tree.isExpanded(tree.getValue())) {
          tree.collapseItem(tree.getValue());
        } else {
          tree.expandItem(tree.getValue());
        }
      }
    }
  }

  /**
   * Set selection value to false for all children
   */
  private void unselectAll(int itemID)
  {
    Object[]            children = (tree.getChildren(itemID)).toArray();

    for (int i = 0; i < children.length; i++) {
      Item              item = (Item)tree.getITEM(children[i]);

      if (item.isSelected()) {
        item.setSelected(false);
        tree.setIcon(item);
      }
      Collection<?>     Grandsons = tree.getChildren(item.getId());

      if (Grandsons != null && Grandsons.size() > 0) {
        unselectAll (item.getId());
      }
    }
  }

  @Override
  public void setDefaultItem() throws VException {
    final Item          item = getSelectedItem();

    if (item != null) {
      if (!item.isDefaultItem()) {
        setDefault(-1);
        item.setDefault(true);
      } else {
        item.setDefault(false);
      }
      item.setSelected(true);
      tree.setIcon(item);
      getModel().refresh();
    }
  }

  /**
   * Set default value for all children
   */
  private void setDefault(int itemID)
  {
    Object[]            children = (tree.getChildren(itemID)).toArray();

    for (int i = 0; i < children.length; i++) {
      Item              item = (Item)tree.getITEM(children[i]);

      if (item.isDefaultItem()) {
        item.setDefault(false);
        tree.setIcon(item);
      }
      Collection<?>     Grandsons = tree.getChildren(item.getId());

      if (Grandsons != null && Grandsons.size() > 0) {
        setDefault (item.getId());
      }
    }
  }

  @Override
  public void addItem() throws VException {
    final Item          item = getSelectedItem();

    if (item != null) {
      if (getModel().getDepth() > 0 && ((item.getLevel() + 1) > getModel().getDepth())) {
        throw new VExecFailedException(MessageCode.getMessage("VIS-00069" , getModel().getDepth()));
      }
      localisation = false;
      showInputDialog(getApplication(), item, true);
    }
  }

  @Override
  public void removeSelectedItem() {
    final Item          item = getSelectedItem();

    if (item != null) {
      BackgroundThreadHandler.access(new Runnable() {

        @Override
        public void run() {
          if (getModel().isRemoveDescendantsAlowed()) {
            removeChildren(item);
          } else {
            attacheToParent(item);
          }
          int           parentId = item.getParent();
          getModel().getRemovedItems().add(item);
          tree.removeItem(item.getId());
          if (tree.getChildren(parentId) == null) {
            tree.setChildrenAllowed(parentId, false);
          }
        }
      });
    }
  }

  /**
   * Remove all children of an item
   * @param parentId the parent ID
   */
  public void removeChildren(Item parent) {
    Collection<?>       children = tree.getChildren(parent.getId());

    if (children != null && children.size() > 0) {
      Object[] childrenList = children.toArray();
      for (int i = 0; i < childrenList.length; i++) {
        removeChildren((Item)tree.getITEM(childrenList[i]));
      }
    }
    getModel().getRemovedItems().add(parent);
    tree.removeItem(parent.getId());
  }

  /**
   * Attache children to removed item parent
   */
  public void attacheToParent(Item item) {
    Collection<?>       children = tree.getChildren(item.getId());

    if (children != null && children.size() > 0) {
      Object[]          childrenList = children.toArray();

      for (int i = 0; i < childrenList.length; i++) {
        tree.setParent(childrenList[i], item.getParent());
        tree.getITEM(childrenList[i]).setParent(item.getParent());;
        setLevel(childrenList[i]);
      }
    }
  }

  /**
   * Sets level value for all children of an item
   * @param itemId the item ID
   */
  public void setLevel(Object itemId) {
    Collection<?>       children = tree.getChildren(itemId);

    if (children != null && children.size() > 0) {
      Object[]          childrenList = children.toArray();

      for (int i = 0; i < childrenList.length; i++) {
        setLevel(childrenList[i]);
      }
    }
    tree.getITEM(itemId).decrementLevel();
  }

  /**
   * Localise the selected item
   */
  public void localiseSelectedItem() {
    final Item          item = getSelectedItem();

    if (item != null) {
      localisation = true;
      showInputDialog(getApplication(), item, false);
    }
  }

  /**
   * Edit selected item's name
   */
  public void editSelectedItem() {
    final Item          item = getSelectedItem();

    if (item != null) {
      localisation = false;
      showInputDialog(getApplication(), item, false);
    }
  }

  /**
   * Shows a modal window in an inputDialog view. This will handle
   * a window view
   * @param application The application instance.
   * @param item item to edit or add new child
   * @param newItem if true, it is a new item to insert
   */
  protected void showInputDialog(final VApplication application,
                                 Item item,
                                 boolean newItem)
  {
    int         maxLength;

    maxLength = Math.min(getModel().MAX_LENGTH,
                         (localisation ? getModel().getLocalisedNameLength() :
                           getModel().getNameLength()));

    if (application == null) {
      return;
    }

    if (inputDialog == null) {
      createInputDialog(application, localisation);
    }
    editTextField.setValue(newItem ? "" : (localisation ?
                                            (item.getLocalisedName() != null ?
                                              item.getLocalisedName()
                                              : "")
                                            : item.getName()));
    editTextField.setMaxLength(maxLength);
    editTextField.setWidth(maxLength, Unit.EM);
    this.newItem = newItem;
    inputDialog.setCaption(localisation ? item.getName()
                                          : VlibProperties.getString(newItem ?
                                                                      "New_item"
                                                                      : "OpenLine"));

    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        editTextField.focus();
        application.attachComponent(inputDialog);
        application.push(); // a push is needed to see the inputDialog.
      }
    });
  }

  /**
   * Create an input dialog
   *
   * @param application the application context
   * @param localisation if true, the edit text will
   * 			 contain the localised item's name
   */
  private void createInputDialog(final VApplication application, boolean localisation) {
    VerticalLayout              popupContent;
    Button                      okButton;
    Button                      cancelButton;

    popupContent = new VerticalLayout();
    popupContent.setWidth(400, Unit.PIXELS);
    editTextField = new TextField("");
    //editTextField.setWidth(180, Unit.PIXELS);
    editTextField.setStyleName(Styles.TEXT_INPUT);
    errorLabel = new Label(MessageCode.getMessage("VIS-00020", getModel().getTitle()));
    errorLabel.setImmediate(true);
    errorLabel.setVisible(false);
    errorLabel.addStyleName("notificationlabel");

    popupContent.addComponent(errorLabel);
    popupContent.addComponent(editTextField);
    popupContent.setComponentAlignment(editTextField, Alignment.MIDDLE_CENTER);
    HorizontalLayout buttonsContainer = new HorizontalLayout();
    okButton = new Button("OK", new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        editItem(application, getSelectedItem());
      }
    });
    okButton.setWidth(80, Unit.PIXELS);
    okButton.setStyleName("inputbutton");

    cancelButton = new Button("Annuler", new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        errorLabel.setVisible(false);
        application.removeWindow(inputDialog);
      }
    });
    cancelButton.setWidth(80, Unit.PIXELS);
    cancelButton.setStyleName("inputbutton");
    buttonsContainer.addComponent(okButton);
    buttonsContainer.addComponent(cancelButton);
    popupContent.addComponent(buttonsContainer);
    popupContent.setComponentAlignment(buttonsContainer, Alignment.MIDDLE_CENTER);

    inputDialog = new PopupWindow();
    inputDialog.setModal(true);
    inputDialog.setContent(popupContent);
    inputDialog.setStyleName("inputdialog");
  }

  /**
   * Edit or add new item to the tree
   *
   * @param application the application context
   * @param item        the selected item
   */
  private void editItem(final VApplication application, Item item) {
    String itemName = editTextField.getValue();

    if (!itemName.isEmpty()) {
      if (getTree().isUnique(itemName)) {
        errorLabel.setVisible(false);
        application.removeWindow(inputDialog);
        if (newItem) {
          addItem(itemName, null, null, item);
        } else {
          if (localisation) {
            item.setLocalisedName(itemName);
          } else {
            item.setName(itemName);
          }
          tree.getItem(item.getId()).getItemProperty(Tree.ITEM_PROPERTY_NAME).
          setValue(item.getFormattedName(getModel().isLocalised()));
        }
      } else {
        errorLabel.setVisible(true);
        editTextField.focus();
      }
    }
  }

  /**
   * Add new Item
   *
   * @param itemName            the new item's name
   * @param localisedName       the itme localised name
   * @param description         the itme description
   * @param parent              the parent item
   */
  private void addItem(String itemName,
                       String localisedName,
                       String description,
                       Item parent)
  {
    HierarchicalContainer       container;
    Item                        item;

    item = new Item(getModel().getNextId(),
                    parent.getId(),
                    itemName,
                    localisedName,
                    description,
                    false,
                    false,
                    null,
                    itemName);
    item.setLevel(parent.getLevel() + 1);

    com.vaadin.data.Item dataItem = tree.addItem(item.getId());
    dataItem.getItemProperty(Tree.ITEM_PROPERTY).setValue(item);
    dataItem.getItemProperty(Tree.ITEM_PROPERTY_NAME).setValue(item.getFormattedName(getModel().isLocalised()));
    dataItem.getItemProperty(Tree.ITEM_PROPERTY_ICON).setValue(item.getIcon());
    tree.setChildrenAllowed(parent.getId(), true);
    tree.setParent(item.getId(), parent.getId());
    tree.setChildrenAllowed(item.getId(), false);
    tree.setIcon(item, true);
    tree.expandItem(parent.getId());
  }

  /**
   * Returns the application instance.
   * @return The application instance.
   */
  protected VApplication getApplication() {
    return (VApplication) ApplicationContext.getApplicationContext().getApplication();
  }

  @Override
  public void setTree() {
    Item   item;

    if (getModel() != null) {   // The model can be destroyed by it's itemTreeManager:
                                // the save action close the Item Tree window
      item = getSelectedItem();
      getModel().setActorEnabled(VItemTree.CMD_QUIT, true);
      if (item != null) {
        getModel().setActorEnabled(VItemTree.CMD_ADD, getModel().isInsertMode());
        getModel().setActorEnabled(VItemTree.CMD_REMOVE, (getModel().isInsertMode() && item.getId() != -1));
        getModel().setActorEnabled(VItemTree.CMD_EDIT, (getModel().isInsertMode() && item.getId() != -1));
        getModel().setActorEnabled(VItemTree.CMD_LOCALISE, (getModel().isInsertMode() && getModel().isLocalised() && item.getId() != -1));
        getModel().setActorEnabled(VItemTree.CMD_SELECT, (!getModel().isNoEdit() && item.getId() != -1));
        getModel().setActorEnabled(VItemTree.CMD_DEFAULT, (getModel().isMultiSelectionDefaultValue() && item.getId() != -1));
        if (isLeaf()) {
          getModel().setActorEnabled(VItemTree.CMD_FOLD, false);
          getModel().setActorEnabled(VItemTree.CMD_UNFOLD, false);
        } else {
          if (tree.isExpanded(tree.getValue())) {
            getModel().setActorEnabled(VItemTree.CMD_FOLD, true);
            getModel().setActorEnabled(VItemTree.CMD_UNFOLD, false);
          } else {
            getModel().setActorEnabled(VItemTree.CMD_FOLD, false);
            getModel().setActorEnabled(VItemTree.CMD_UNFOLD, true);
          }
        }
      } else {
        getModel().setActorEnabled(VItemTree.CMD_FOLD, false);
        getModel().setActorEnabled(VItemTree.CMD_UNFOLD, false);
        getModel().setActorEnabled(VItemTree.CMD_ADD, false);
        getModel().setActorEnabled(VItemTree.CMD_REMOVE, false);
        getModel().setActorEnabled(VItemTree.CMD_EDIT, false);
        getModel().setActorEnabled(VItemTree.CMD_LOCALISE, false);
        getModel().setActorEnabled(VItemTree.CMD_SELECT, false);
        getModel().setActorEnabled(VItemTree.CMD_DEFAULT, false);
      }
      getModel().setActorEnabled(VItemTree.CMD_SAVE, getModel().isChanged());
    }
  }

  @Override
  public void run() {
    setVisible(true);
    tree.focus();
    tree.expandItem(-1);
  }

  /**
   * Returns the selected item.
   * @return The selected item.
   */
  public Item getSelectedItem() {
    if (tree.getValue() != null) {
      return (Item) tree.getITEM(tree.getValue());
    }
    return null;
  }

  /**
   * Returns true if the selected item is leaf
   */
  public boolean isLeaf() {
      return !tree.getContainerDataSource().hasChildren(tree.getValue());
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  @Override
  public VItemTree getModel() {
    return (VItemTree) super.getModel();
  }

  // --------------------------------------------------
  // UItemTree IMPLEMENTATION
  // --------------------------------------------------

  @Override
  public UTreeComponent getTree() {
    return tree;
  }

  // --------------------------------------------------
  // LISTENERS
  // --------------------------------------------------

  /**
   * The <code>ItemClickHandler</code> is the item tree implementation
   * of the {@link ItemClickListener}.
   */
  @SuppressWarnings("serial")
  private final class ItemClickHandler implements ItemClickListener {

    @Override
    public void itemClick(ItemClickEvent event) {
      tree.setValue(event.getItemId());

      if (event.isDoubleClick()) {
        try {
          setSelectedItem();
        } catch (VException e) {
          throw new VRuntimeException(e.getMessage(), e);
        }
      } else {
        setTree();
      }
    }
  }

  /**
   * The <code>CollapseHandler</code> is the item tree implementation
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
   * The <code>ExpandHandler</code> is the item tree implementation
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
  private PopupWindow                           inputDialog;
  private TextField                             editTextField;
  private Label                                 errorLabel;
  private boolean                               newItem;
  private boolean                               localisation;

  private static final long                     serialVersionUID = -1673877931730110797L;
}
