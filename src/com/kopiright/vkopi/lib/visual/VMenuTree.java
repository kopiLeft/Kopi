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

package com.kopiright.vkopi.lib.visual;

import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.Query;

public class VMenuTree extends VWindow {

  static {
    WindowController.getWindowController().registerUIBuilder(Constants.MDL_MENU_TREE, new UIBuilder() {
      public DWindow createView(VWindow model) {
	DMenuTree         view = new DMenuTree((VMenuTree) model);

	return view;
      }
    });
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new instance of VMenuTree.
   * @param ctxt the context where to look for application
   */
  public VMenuTree(DBContext ctxt) {
    this(ctxt, false, null, true);
  }

  /**
   * Constructs a new instance of VMenuTree.
   * @param ctxt the context where to look for application
   * @param isSuperUser is it a super user ?
   * @param userName the user name
   * @param loadFavorites should load favorites ?
   */
  public VMenuTree(DBContext ctxt,
                   boolean isSuperUser,
                   String userName,
                   final boolean loadFavorites)
  {
    this(ctxt, isSuperUser, userName, null, loadFavorites);
  }

  /**
   * Constructs a new instance of VMenuTree.
   * @param ctxt the context where to look for application
   * @param isSuperUser is it a super user ?
   * @param userName the user name.
   * @param groupName the group name
   * @param loadFavorites should load favorites ?
   */
  public VMenuTree(DBContext ctxt,
                   boolean isSuperUser,
                   String userName,
                   String groupName,
                   final boolean loadFavorites)
  {
    super(ctxt);
    this.isSuperUser = isSuperUser;
    this.userName = userName;
    this.groupName = groupName;
    actors = new SActor[9];
    items = new ArrayList();
    shortcutsID = new ArrayList();
    createActor(CMD_QUIT, "File", "Close", "quit", 0 /*KeyEvent.VK_ESCAPE*/, 0);
    createActor(CMD_OPEN, "Edit", "Open", "open", KeyEvent.VK_ENTER, 0);
    createActor(CMD_SHOW, "Edit", "Show", null, 0, 0);
    createActor(CMD_ADD, "Edit", "Add", null, 0, 0);
    createActor(CMD_REMOVE, "Edit", "Remove", null, 0, 0);
    createActor(CMD_FOLD, "Edit", "Fold", "fold", KeyEvent.VK_ENTER, 0);
    createActor(CMD_UNFOLD, "Edit", "Unfold", "unfold", KeyEvent.VK_ENTER, 0);
    createActor(CMD_INFORMATION, "Help", "Information", null, 0, 0);
    createActor(CMD_HELP, "Help", "Help", "help", KeyEvent.VK_F1, 0);
    setActors(actors);

    localizeActors(Locale.getDefault());
    createTree(loadFavorites);
    localizeModules(Locale.getDefault());
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------

  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  public void localizeActors(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, Application.getDefaultLocale());
    try {
      super.localizeActors(manager); // localizes the actors in VWindow
    } catch (InconsistencyException e) {
      Application.reportTrouble("MenuTree Actor localization",
                                "MenuTreeModel.localize",
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
   * Returns the ID of the current user
   */
  public int getUserID() {
    return getDBContext().getDefaultConnection().getUserID();
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
    actors[number] = new SActor(menu,
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
   * @param   actor           the number of the actor.
   * @return  true if an action was found for the specified number
   */
  public void executeVoidTrigger(final int key) throws VException {
    DMenuTree  currentDisplay = getDMenuTree();

    switch (key) {
    case CMD_QUIT:
      currentDisplay.closeWindow();
      break;
    case CMD_OPEN:
      currentDisplay.launchSelectedForm();
      break;
    case CMD_SHOW:
      currentDisplay.getBookmark().show();
      currentDisplay.getBookmark().toFront();
      break;
    case CMD_ADD:
      currentDisplay.addSelectedElement();
      currentDisplay.setMenu();
      break;
    case CMD_REMOVE:
      currentDisplay.removeSelectedElement();
      currentDisplay.setMenu();
      break;
    case CMD_FOLD:
      currentDisplay.getTree().collapseRow(currentDisplay.getTree().getSelectionRows()[0]);
      break;
    case CMD_UNFOLD:
      currentDisplay.getTree().expandRow(currentDisplay.getTree().getSelectionRows()[0]);
      break;
    case CMD_INFORMATION:
      {
        String[]      versionArray = Utils.getVersion();
        String      version = "";
        for (int i=0; i<versionArray.length; i++) {
          version += "\n" + versionArray[i];
        }
        String informationText;
        try {
          informationText = Application.getDefaults().getInformationText();
        } catch (PropertyException e) {
          e.printStackTrace();
          informationText = "";
        }
        getDisplay().showApplicationInformation(informationText + version);
      }
      break;
    case CMD_HELP:
      //try {
      // !!!!!! KopiWindowFactory.getHelpWindow().doNotModal();
      //} catch (Exception e) {}
      break;
    default:
      super.executeVoidTrigger(key);
    }
  }

  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  public void localizeModules(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, Application.getDefaultLocale());

    // localizes the modules
    for (ListIterator i = items.listIterator(); i.hasNext(); ) {
      Module          item;

      item = (Module)i.next();
      item.localize(manager);
    }

    manager = null;
  }

  /**
   * Builds the module tree.
   */
  private void createTree(boolean loadFavorites) {
    Module[]                    localModules;
    DefaultMutableTreeNode      localTree;

    localModules = loadModules(loadFavorites);
    if (localModules.length == 0) {
      localTree = null;
    } else {
      Module                    root;

      root = new Module(0,
                        0,
                        VlibProperties.getString("PROGRAM"),
                        VlibProperties.getString("program"),
                        null,
                        Module.ACS_PARENT,
                        null);
      localTree = createTree(localModules, root, false);
    }

    if (localTree == null) {
      error(MessageCode.getMessage("VIS-00042"));
      throw new InconsistencyException();//never accessed
    }

    root = localTree;
  }

  /**
   * Builds the module tree.
   */
  private DefaultMutableTreeNode createTree(Module[] modules,
                                            Module root,
                                            boolean force)
  {
    if (root.getAccessibility() == Module.ACS_TRUE || isSuperUser()) {
      force = true;
    }

    if (root.getObject() != null) {
      return force ? new DefaultMutableTreeNode(root) : null;
    } else {
      DefaultMutableTreeNode    self = null;

      for (int i = 0; i < modules.length; i++) {
        if (modules[i].getParent() == root.getId()) {
          DefaultMutableTreeNode        node;

          node = createTree(modules, modules[i], force);
          if (node != null) {
            if (self == null) {
              self = new DefaultMutableTreeNode(root);
            }

            self.add(node);
          }
        }
      }
      return self;
    }
  }

  /**
   * Fetches the modules from the database.
   */
  private List fetchModules(boolean isUnicode) throws SQLException {
    Query       getModules = new Query(getDBContext().getDefaultConnection());
    List        localModules = new ArrayList();

    getModules.open(SELECT_MODULES);
    while (getModules.next()) {
      Query     getIcons = new Query(getDBContext().getDefaultConnection());
      String    icon = null;

      try {
        if (getModules.getNullableInt(7) != null
            && getModules.getInt(7) != 0) {
          getIcons.open("SELECT Objekt FROM SYMBOLE WHERE ID = " + getModules.getInt(7));
          icon = getIcons.next() ? getIcons.getString(1) : null;
          getIcons.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      Module module = new Module(getModules.getInt(1),
                                 getModules.getInt(2),
                                 getModules.getString(3, isUnicode),
                                 getModules.getString(4, isUnicode),
                                 getModules.getString(5, isUnicode),
                                 Module.ACS_PARENT,
                                 icon);
      localModules.add(module);
      items.add(module);
    }
    getModules.close();

    return localModules;
  }

  private void fetchGroupRightsByUserId(List modules) throws SQLException {
    fetchRights(modules, SELECT_GROUP_RIGHTS_BY_USERID);
  }

  private void fetchGroupRightsByGroupId(List modules) throws SQLException {
    fetchRights(modules, SELECT_GROUP_RIGHTS_BY_GROUPID);
  }

  private void fetchUserRights(List modules) throws SQLException {
    fetchRights(modules, SELECT_USER_RIGHTS);
  }

  private void fetchRights(List modules, String queryText) throws SQLException {
    Query       getRights = new Query(getDBContext().getDefaultConnection());

    if (groupName != null) {
      getRights.addString("(SELECT ID FROM GRUPPEN WHERE Kurzname = \'" + groupName + "\')");
    } else if (userName != null) {
      getRights.addString("(SELECT ID FROM KOPI_USERS WHERE Kurzname = \'" + userName + "\')");
    } else {
      getRights.addInt(getUserID());
    }
    getRights.open(queryText);

    while (getRights.next()) {
      Module    module = findModuleById(modules, getRights.getInt(1));

      if (module != null) {
        module.setAccessibility(getRights.getBoolean(2) ? Module.ACS_TRUE : Module.ACS_FALSE);
      }
    }
    getRights.close();
  }

  private Module findModuleById(List modules, int id) {
    for (int i = 0; i < modules.size(); i++) {
      Module    module = (Module)modules.get(i);

      if (module.getId() == id) {
        return module;
      }
    }

    return null;
  }

  /**
   * Fetches the favorites from the database.
   */
  private void fetchFavorites() throws SQLException {
    Query       getFavorites = new Query(getDBContext().getDefaultConnection());

    getFavorites.open("SELECT F.Modul, F.ID FROM FAVORITEN F WHERE F.Benutzer = " + getUserID() + " ORDER BY 2");
    while (getFavorites.next()) {
      int       id = getFavorites.getInt(1);

      if (id != 0) {
        shortcutsID.add(new Integer(id));
      }
    }
    getFavorites.close();
  }

  /*
   * Loads the accessible modules.
   */
  private Module[] loadModules(boolean loadFavorites) {
    List        localModules = new ArrayList();

    try {
      getDBContext().startWork(); // !!! BEGIN_SYNC

      localModules = fetchModules(ApplicationConfiguration.getConfiguration().isUnicodeDatabase());

      if (groupName != null) {
        fetchGroupRightsByGroupId(localModules);
      } else {
        fetchGroupRightsByUserId(localModules);
        fetchUserRights(localModules);
      }

      if (loadFavorites) {
        fetchFavorites();
      }

      getDBContext().commitWork();
    } catch (SQLException e) {
      try {
        getDBContext().abortWork();
      } catch (SQLException ef) {
        //!!!
      }
      e.printStackTrace();
    }

    if (! isSuperUser()) {
      // walk downwards because we remove elements
      ListIterator      iterator = localModules.listIterator(localModules.size() - 1);

      while (iterator.hasPrevious()) {
        Module  module = (Module)iterator.previous();

        // remove all modules where access is explicitly denied
        if (module.getAccessibility() == Module.ACS_FALSE) {
          iterator.remove();
        }
      }
    }

    array = (Module[])com.kopiright.util.base.Utils.toArray(localModules, Module.class);
    return array;
  }

  /**
   * Sets the title of the frame
   */
  public void setTitle(String s) {
    if (getTitle().contains(VlibProperties.getString("program_menu"))) {
      super.setTitle(s);
    } else {
      super.setTitle(s + " - " + VlibProperties.getString("program_menu"));
    }
  }

  /**
   * Sets the tool tip in the foot panel
   */
  public void setToolTip(String s) {
    setInformationText(s);
  }

  /**
   * Sets the accessibility of the module
   */
  public boolean isSuperUser() {
    return isSuperUser;
  }

  /**
   * Sets the accessibility of the module
   */
  public void setSuperUser(boolean isSuperUser) {
    this.isSuperUser = isSuperUser;
  }

  public List getModules() {
    return items;
  }

  public Module getModule(KopiExecutable ke) {
    ListIterator        iterator = items.listIterator();

    while (iterator.hasNext()) {
      Module    item = (Module)iterator.next();

      if (item.getObject() != null && item.getObject().equals(ke.getClass().getName())) {
        return item;
      }
    }

    return null;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  public TreeNode getRoot() {
    return root;
  }

  public List getShortcutsID() {
    return shortcutsID;
  }

  public Module[] getModuleArray() {
    return array;
  }

  public int getType() {
    return Constants.MDL_MENU_TREE;
  }

  public DMenuTree getDMenuTree() {
    return (DMenuTree) getDisplay();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private TreeNode			root;
  private SActor[]       		actors;
  private boolean               	isSuperUser;
  private Module[]              	array;
  private List                  	items;
  private String                	userName;
  private String                	groupName;
  private List                  	shortcutsID;

  private static final String   	SELECT_MODULES =
    " SELECT    M.ID, M.Vater, M.Kurzname, M.Quelle, M.Objekt, M.Prioritaet, M.Symbol" +
    " FROM      MODULE M" +
    " ORDER BY  6 DESC, 1";

  private static final String   	SELECT_USER_RIGHTS =
    " SELECT    M.ID, B.Zugriff, M.Prioritaet" +
    " FROM      MODULE M, BENUTZERRECHTE B" +
    " WHERE     M.ID = B.Modul" +
    " AND       B.Benutzer = $1" +
    " ORDER BY  3, 1";

  private static final String   	SELECT_GROUP_RIGHTS_BY_USERID =
    " SELECT    DISTINCT M.ID, G.Zugriff, M.Prioritaet" +
    " FROM      MODULE M, GRUPPENRECHTE G, GRUPPENZUGEHOERIGKEITEN Z" +
    " WHERE     M.ID = G.Modul" +
    " AND       G.Gruppe = Z.Gruppe" +
    " AND       Z.Benutzer = $1" +
    " ORDER BY  3, 1";

  private static final String   	SELECT_GROUP_RIGHTS_BY_GROUPID =
    " SELECT    DISTINCT M.ID, G.Zugriff, M.Prioritaet" +
    " FROM      MODULE M, GRUPPENRECHTE G" +
    " WHERE     M.ID = G.Modul" +
    " AND       G.Gruppe = $1" +
    " ORDER BY  3, 1";

  public static final int      		CMD_QUIT        = 0;
  public static final int      		CMD_OPEN        = 1;
  public static final int      		CMD_SHOW        = 2;
  public static final int      		CMD_ADD         = 3;
  public static final int      		CMD_REMOVE      = 4;
  public static final int      		CMD_FOLD        = 5;
  public static final int      		CMD_UNFOLD      = 6;
  public static final int      		CMD_INFORMATION = 7;
  public static final int      		CMD_HELP        = 8;

  private static final String           MENU_LOCALIZATION_RESOURCE = "com/kopiright/vkopi/lib/resource/Menu";
}
