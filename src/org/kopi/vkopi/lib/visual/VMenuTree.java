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

package org.kopi.vkopi.lib.visual;

import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.l10n.LocalizationManager;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.Query;

@SuppressWarnings("serial")
public class VMenuTree extends VWindow {

  static {
    WindowController.getWindowController().registerWindowBuilder(Constants.MDL_MENU_TREE, new WindowBuilder() {

      public UWindow createWindow(VWindow model) {
	return (UMenuTree)UIFactory.getUIFactory().createView(model);
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
    actors = new VActor[9];
    items = new ArrayList<Module>();
    shortcutsID = new ArrayList<Integer>();
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
    localizeActors(ApplicationContext.getDefaultLocale());
    createTree(isSuperUser ? true : loadFavorites);
    localizeRootMenus(ApplicationContext.getDefaultLocale());
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

    manager = new LocalizationManager(locale, Locale.getDefault());
    try {
      super.localizeActors(manager); // localizes the actors in VWindow
    } catch (InconsistencyException e) {
      ApplicationContext.reportTrouble("MenuTree Actor localization",
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
   * Returns the actor having the given number.
   */
  public VActor getActor(int number) {
    return actors[number];
  }

  /**
   * Returns the ID of the current user
   */
  public int getUserID() {
    return getDBContext().getConnection().getUserID();
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
   * @param   actor           the number of the actor.
   * @return  true if an action was found for the specified number
   */
  public void executeVoidTrigger(final int key) throws VException {
    UMenuTree  currentDisplay = getDisplay();

    switch (key) {
    case CMD_QUIT:
      close(0);
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
      currentDisplay.getTree().collapseRow(currentDisplay.getTree().getSelectionRow());
      break;
    case CMD_UNFOLD:
      currentDisplay.getTree().expandRow(currentDisplay.getTree().getSelectionRow());
      break;
    case CMD_INFORMATION:
      {
        String[]      versionArray = org.kopi.vkopi.lib.base.Utils.getVersion();
        String      version = "";
        for (int i=0; i<versionArray.length; i++) {
          version += "\n" + versionArray[i];
        }
        String informationText;
        try {
          informationText = ApplicationContext.getDefaults().getInformationText();
        } catch (PropertyException e) {
          e.printStackTrace();
          informationText = "";
        }

        ((UMenuTree)getDisplay()).showApplicationInformation(informationText + version);
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
   * Localizes the root menus with a given locale
   * @param locale The locale to be used for localization
   */
  protected void localizeRootMenus(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, Locale.getDefault());
    for (RootMenu rootMenu : ROOT_MENUS) {
      rootMenu.localize(manager);
    }
    
    manager = null;
  }

  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  protected void localizeModules(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, Locale.getDefault());

    // localizes the modules
    for (ListIterator<Module> i = items.listIterator(); i.hasNext(); ) {
      Module          item;

      item = i.next();
      item.localize(manager);
    }

    manager = null;
  }

  /**
   * Builds the module tree.
   */
  @SuppressWarnings("deprecation")
  private void createTree(boolean loadFavorites) {
    Module[]            localModules;
    boolean             hasModules;
    
    localModules = loadModules(loadFavorites);
    if (localModules.length == 0) {
      hasModules = false;
    } else {
      hasModules = false;
      for (RootMenu rootMenu : ROOT_MENUS) {
        rootMenu.createTree(localModules, isSuperUser());
        hasModules |= !rootMenu.isEmpty();
      }
    }

    if (!hasModules) {
      error(MessageCode.getMessage("VIS-00042"));
      throw new InconsistencyException();//never accessed
    }
    
    createTopLevelTree();
  }

  /**
   * Creates the root tree that contains all root menus.
   * This is used to keep compatibility with swing implementation
   */
  private void createTopLevelTree() {    
    root = new DefaultMutableTreeNode(new Module(0,
                                                 0,
                                                 VlibProperties.getString("PROGRAM"),
                                                 VlibProperties.getString("program"),
                                                 null,
                                                 Module.ACS_PARENT,
                                                 Integer.MAX_VALUE,
                                                 null));
    for (RootMenu menu : ROOT_MENUS) {
      if (!menu.isEmpty()) {      
        ((DefaultMutableTreeNode)root).add((DefaultMutableTreeNode) menu.getRoot());
      }
    }
  }

  /**
   * Fetches the modules from the database.
   */
  private List<Module> fetchModules(boolean isUnicode) throws SQLException {
    Query       	getModules = new Query(getDBContext().getConnection());
    List<Module>        localModules = new ArrayList<Module>();

    getModules.open(SELECT_MODULES);
    while (getModules.next()) {
      Query     getIcons = new Query(getDBContext().getConnection());
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
                                 getModules.getInt(6),
                                 icon);
      localModules.add(module);
      items.add(module);
    }
    getModules.close();
    
    return localModules;
  }

  private void fetchGroupRightsByUserId(List<Module> modules) throws SQLException {
    fetchRights(modules, SELECT_GROUP_RIGHTS_BY_USERID);
  }

  private void fetchGroupRightsByGroupId(List<Module> modules) throws SQLException {
    fetchRights(modules, SELECT_GROUP_RIGHTS_BY_GROUPID);
  }

  private void fetchUserRights(List<Module> modules) throws SQLException {
    fetchRights(modules, SELECT_USER_RIGHTS);
  }

  private void fetchRights(List<Module> modules, String queryText) throws SQLException {
    Query       getRights = new Query(getDBContext().getConnection());

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

  private Module findModuleById(List<Module> modules, int id) {
    for (int i = 0; i < modules.size(); i++) {
      Module    module = modules.get(i);

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
    Query       getFavorites = new Query(getDBContext().getConnection());

    if (isSuperUser && userName != null) {
      getFavorites.open("SELECT F.Modul, F.ID FROM FAVORITEN F WHERE F.Benutzer = "
        + "(SELECT ID FROM KOPI_USERS WHERE Kurzname = \'" + userName + "\')"
        + " ORDER BY 2");
    } else {
      getFavorites.open("SELECT F.Modul, F.ID FROM FAVORITEN F WHERE F.Benutzer = " + getUserID() + " ORDER BY 2");
    }
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
    List<Module>        localModules = new ArrayList<Module>();

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
      ListIterator<Module>      iterator = localModules.listIterator(localModules.size() - 1);

      while (iterator.hasPrevious()) {
        Module  module = iterator.previous();

        // remove all modules where access is explicitly denied
        if (module.getAccessibility() == Module.ACS_FALSE) {
          iterator.remove();
        }
      }
    }
    // add default modules
    addLogoutModule(localModules);
    // order the menus alphabetically
    localizeModules(ApplicationContext.getDefaultLocale());
    Collections.sort(localModules);
    array = localModules.toArray(new Module[localModules.size()]);
    
    return array;
  }

  /**
   * Add a favorite into database.
   */
  public void addShortcutsInDatabase(int id) {
    try {
      Query                     query;

      getDBContext().startWork();    // !!! BEGIN_SYNC
      query = new Query(this);
      if (getMenuTreeUser() != null) {
        query.run("INSERT INTO FAVORITEN VALUES ("
                + "{fn NEXTVAL(FAVORITENId)}" + ", "
                + (int)(System.currentTimeMillis()/1000) + ", "
                + "(SELECT ID FROM KOPI_USERS WHERE Kurzname = \'" + getMenuTreeUser() + "\')" + ", "
                + id
                + ")");
      } else {
        query.run("INSERT INTO FAVORITEN VALUES ("
                + "{fn NEXTVAL(FAVORITENId)}" + ", "
                + (int)(System.currentTimeMillis()/1000) + ", "
                + getUserID() + ", "
                + id
                + ")");
      }
      getDBContext().commitWork();
    } catch (SQLException e) {
      try {
        getDBContext().abortWork();
      } catch (SQLException ef) {
        ef.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  /**
   * Remove favorite from database.
   */
  public void removeShortcutsFromDatabase(int id) {
    try {
      Query                     query;

      getDBContext().startWork();    // !!! BEGIN_SYNC
      query = new Query(this);
      if (getMenuTreeUser() != null) {
        query.run("DELETE FROM FAVORITEN WHERE Benutzer = "
                + "(SELECT ID FROM KOPI_USERS WHERE Kurzname = \'" + getMenuTreeUser() + "\')"
                + " AND Modul = " + id);
      } else {
        query.run("DELETE FROM FAVORITEN WHERE Benutzer = " + getUserID() + " AND Modul = " + id);
      }
      getDBContext().commitWork();
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        getDBContext().abortWork();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * Resets all favorites
   */
  public void resetShortcutsInDatabase(List<Module> modules) {
    try {
      getDBContext().startWork();    // !!! BEGIN_SYNC

      new Query(this).run("DELETE FROM FAVORITEN WHERE Benutzer = " + getUserID());

      for (int i = 0; i < modules.size(); i++) {
        Module module = (Module)modules.get(i);

        new Query(this).run("INSERT INTO FAVORITEN VALUES ("
                + "{fn NEXTVAL(FAVORITENId)}" + ", "
                + (int)(System.currentTimeMillis()/1000) + ", "
                + getUserID() + ", "
                + module.getId()
                + ")");
      }

      getDBContext().commitWork();
    } catch (SQLException e) {
      try {
        getDBContext().abortWork();
      } catch (SQLException ef) {
        ef.printStackTrace();
      }
      e.printStackTrace();
    }
  }
  
  /**
   * Adds the default logout module
   */
  protected void addLogoutModule(List<Module> localModules) {
    Module              logout;
    
    logout = new Module(Integer.MAX_VALUE,
                        USER_MENU,
                        "logout",
                        RootMenu.ROOT_MENU_LOCALIZATION_RESOURCE,
                        LogoutModule.class.getName(),
                        Module.ACS_TRUE,
                        Integer.MIN_VALUE,
                        null);
    items.add(logout);
    localModules.add(logout);
  }

  /**
   * Sets the title of the frame
   */
  public void setTitle(String s) {
    if (s != null) {
      if (s.contains(VlibProperties.getString("program_menu"))) {
	super.setTitle(s);
      } else {
	super.setTitle(s + " - " + VlibProperties.getString("program_menu"));
      }
    } else {
      super.setTitle(VlibProperties.getString("program_menu"));
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

  public List<Module> getModules() {
    return items;
  }

  public Module getModule(KopiExecutable ke) {
    ListIterator<Module>        iterator = items.listIterator();

    while (iterator.hasNext()) {
      Module    item = iterator.next();

      if (item.getObject() != null && item.getObject().equals(ke.getClass().getName())) {
        return item;
      }
    }

    return null;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the root node of this menu tree.
   * @return The root node of this menu tree.
   */
  public TreeNode getRoot() {
    return root;
  }
  
  /**
   * Returns the list of available root menus.
   * @return The list of available root menus.
   */
  public List<RootMenu> getRoots() {
    return Arrays.asList(ROOT_MENUS);
  }

  public List<Integer> getShortcutsID() {
    return shortcutsID;
  }

  public Module[] getModuleArray() {
    return array;
  }

  public int getType() {
    return Constants.MDL_MENU_TREE;
  }
  
  /**
   * Returns the user loaded with this menu tree instance.
   * @return The user loaded with this menu tree instance.
   */
  public String getMenuTreeUser() {
    return userName;
  }
  
  public UMenuTree getDisplay() {
    return (UMenuTree) super.getDisplay();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private TreeNode			root;
  private VActor[]       		actors;
  private boolean               	isSuperUser;
  private Module[]              	array;
  private List<Module>                  items;
  private String                	userName;
  private String                	groupName;
  private List<Integer>                 shortcutsID;

  private static final String   	SELECT_MODULES =
    " SELECT    M.ID, M.Vater, M.Kurzname, M.Quelle, M.Objekt, M.Prioritaet, M.Symbol" +
    " FROM      MODULE M" +
    " ORDER BY  6 DESC";

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
  
  public static final int               MAIN_MENU       = -1;
  public static final int               USER_MENU       = -2;
  public static final int               ADMIN_MENU      = -3;
  public static final int               BOOKMARK_MENU   = -4;
  
  private static final RootMenu[]       ROOT_MENUS      = new RootMenu[] {
    new RootMenu(MAIN_MENU, "forms"),
    new RootMenu(USER_MENU, "user"),
    new RootMenu(ADMIN_MENU, "admin"),
  };

  private static final String           MENU_LOCALIZATION_RESOURCE = "org/kopi/vkopi/lib/resource/Menu";
}
