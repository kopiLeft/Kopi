/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.ui.base.JBookmarkPanel;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.type.NotNullTimestamp;

public class MenuTree extends DWindow {

  /**
   * Constructs a new instance of MenuTree.
   * @param ctxt the context where to look for application
   */
  public MenuTree(DBContext ctxt) {
    this(ctxt, false, null, true);
  }

  /**
   * Constructs a new instance of MenuTree.
   * @param ctxt the context where to look for application
   */
  public MenuTree(DBContext ctxt,
		  boolean isSuperUser,
		  String userName,
		  final boolean loadFavorites)
  {
    super(new MenuTreeModel());

    setSuperUser(isSuperUser);
    this.userName = userName;
    shortcuts = new Hashtable();
    orderdShorts = new ArrayList();
    modules = new ArrayList();
    shortcutsID = new ArrayList();

    getModel().setDBContext(ctxt);

    items = new ArrayList();
    tree = new JTree(createTree(loadFavorites));
    localize(Locale.getDefault());

    tree.addMouseListener(new MouseAdapter() {
      private long lastClick;

      public void mouseClicked(MouseEvent e) {
	if (e.getClickCount() == 2 && !isSuperUser()) {
	  callSelectedForm();
	} else {
	  if (e.getWhen() - lastClick < 400) {
	    // for slow NT users
	    callSelectedForm();
	  }
	}
	lastClick = e.getWhen();
      }
    });

    tree.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_SPACE
            || (e.getKeyCode() == KeyEvent.VK_ENTER
                && getSelectedNode().isLeaf())) {
          e.consume();
          callSelectedForm();
        }
      }
    });

    tree.addTreeExpansionListener(new TreeExpansionListener() {
      public void treeExpanded(TreeExpansionEvent event) {
	setMenu();
      }
      public void treeCollapsed(TreeExpansionEvent event) {
	setMenu();
      }
    });

    tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
	setMenu();
      }
    });

    tree.setCellRenderer(itemRenderer = new MenuItemRenderer(isSuperUser()));
    tree.putClientProperty("JTree.lineStyle", "None");

    /* Make tree ask for the height of each row. */
    tree.setRowHeight(-1);
    tree.setBackground(UIManager.getColor("menu.background"));

    JScrollPane	sp = new JScrollPane();

    sp.setBorder(null);
    sp.getViewport().add(tree);
    getContentPanel().setLayout(new BorderLayout());
    getContentPanel().add(sp, BorderLayout.CENTER);
    toolbar = new JBookmarkPanel(Message.getMessage("toolbar-title"));


    for (int i = 0; i < shortcutsID.size() ; i++) {
      int	id = ((Integer)shortcutsID.get(i)).intValue();

      for (int j = 0; j < array.length; j++) {
	if (array[j].getId() == id) {
	  addShortcut(array[j]);
	}
      }
    }
    if (!shortcutsID.isEmpty()) {
      toolbar.show();
      toolbar.toFront();
    }

    try {
      getModel().setDisplay(this);
      WindowController.getWindowController().doNotModal(this);
      if (tree.getRowCount() > 0) {
	tree.setSelectionInterval(0, 0);
      }
      setMenu();
      setVisible(true);
      tree.requestFocusInWindow();
    } catch (VException v) {
      System.err.println("ERROR " + v.getMessage());
    }
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

  /**
   *
   */
  public List getModules() {
    return items;
  }

  /**
   *
   */
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

  /**
   *
   */
  public void setMenu() {
    DefaultMutableTreeNode	node = getSelectedNode();

    setActorEnabled(CMD_QUIT, !isSuperUser());
    setActorEnabled(CMD_INFORMATION, true);
    setActorEnabled(CMD_HELP, true);

    if (node != null) {
      Module	module = (Module)node.getUserObject();

      setToolTip(module.getHelp());
      setActorEnabled(CMD_SHOW, shortcuts.size() > 0);
      if (node.isLeaf()) {
	setActorEnabled(CMD_OPEN, true);
	setActorEnabled(CMD_ADD, !shortcuts.containsKey(module));
	setActorEnabled(CMD_REMOVE, shortcuts.containsKey(module));
	setActorEnabled(CMD_FOLD, false);
	setActorEnabled(CMD_UNFOLD, false);
      } else {
	setActorEnabled(CMD_OPEN, isSuperUser());
	setActorEnabled(CMD_ADD, false);
	if (node == null) {
	  setActorEnabled(CMD_FOLD, false);
	  setActorEnabled(CMD_UNFOLD, false);
	} else if (tree.isExpanded(tree.getSelectionPath())) {
          setActorEnabled(CMD_FOLD, true);
          setActorEnabled(CMD_UNFOLD, false);
        } else {
          setActorEnabled(CMD_FOLD, false);
          setActorEnabled(CMD_UNFOLD, true);
        }
      }
    }
  }

  public void addSelectedElement() {
    DefaultMutableTreeNode	node = getSelectedNode();

    if (node != null && node.isLeaf()) {
      addShortcut((Module)node.getUserObject());
      resetShortcutsInDatabase();
    }
  }

  public void removeSelectedElement() {
    DefaultMutableTreeNode	node = getSelectedNode();

    if (node != null && node.isLeaf()) {
      removeShortcut((Module)node.getUserObject());
      resetShortcutsInDatabase();
    }
  }



  public void addShortcut(final Module module) {
    if (!shortcuts.containsKey(module)) {
      AbstractAction    action = new AbstractAction(module.getDescription(),
                                                    module.getIcon()) {
	public void actionPerformed(ActionEvent e) {
	  setWaitInfo(Message.getMessage("menu_form_started"));
	  getModel().performAsyncAction(new KopiAction("menu_form_started") {
	    public void execute() throws VException {
	      module.run(getModel().getDBContext());
	      unsetWaitInfo();
	    }
	  });
	}
        };

      toolbar.addShortcut(action);
      shortcuts.put(module, action);
      orderdShorts.add(action);
      modules.add(module);
    }
  }

  public Action[] getBookmarkActions() {
    return (Action[]) orderdShorts.toArray(new Action[shortcuts.size()]);
  }

  public void removeShortcut(final Module module) {
    if (shortcuts.containsKey(module)) {
      modules.remove(module);
      Action    removed = (Action) shortcuts.remove(module);

      orderdShorts.remove(removed);
      toolbar.removeShortcut(removed);
    }
  }

  public void resetShortcutsInDatabase() {
    try {
      getModel().getDBContext().startWork();	// !!! BEGIN_SYNC

      new Query(getModel()).run("DELETE FROM FAVORITEN WHERE Benutzer = " + getUserID());

      for (int i = 0; i < modules.size(); i++) {
	Module	module = (Module)modules.get(i);

	new Query(getModel()).run("INSERT INTO FAVORITEN VALUES ("
				  + "(SELECT FAVORITENId.NEXTVAL FROM DUMMY)" + ", "
				  + (int)(System.currentTimeMillis()/1000) + ", "
				  + getUserID() + ", "
				  + module.getId()
				  + ")");
      }

      getModel().getDBContext().commitWork();
    } catch (SQLException e) {
      try {
	getModel().getDBContext().abortWork();
      } catch (SQLException ef) {
	//!!!
      }
      e.printStackTrace();
    }
  }

  public void gotoShortcuts() {
    try {
      if (toolbar.isVisible()) {
	toolbar.setVisible(false);
      }
      toolbar.setVisible(true);
      toolbar.toFront();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Returns the TreeNode instance that is selected in the tree.
   * If nothing is selected, null is returned.
   */
  protected DefaultMutableTreeNode getSelectedNode() {
    TreePath   selPath = tree.getSelectionPath();

    if (selPath != null) {
      return (DefaultMutableTreeNode)selPath.getLastPathComponent();
    } else {
      return null;
    }
  }

  /*
   * Builds the module tree.
   */
  private DefaultMutableTreeNode createTree(boolean loadFavorites) {
    Module[]            	localModules;
    DefaultMutableTreeNode      localTree;

    localModules = loadModules(loadFavorites);
    if (localModules.length == 0) {
      localTree = null;
    } else {
      Module            	root;

      root = new Module(0,
			0,
			Message.getMessage("PROGRAM"),
			Message.getMessage("program"),
			null,
			Module.ACS_PARENT,
			null);
      localTree = createTree(localModules, root, false);
    }

    if (localTree == null) {
      displayError(Message.getMessage("no_menu_available"));
      System.exit(0);
      throw new InconsistencyException();//never accessed
    }
    return localTree;
  }

  /*
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
      DefaultMutableTreeNode	self = null;

      for (int i = 0; i < modules.length; i++) {
	if (modules[i].getParent() == root.getId()) {
	  DefaultMutableTreeNode	node;

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
    Query	getModules = new Query(getModel().getDBContext().getDefaultConnection());
    List	localModules = new ArrayList();

    getModules.open(SELECT_MODULES);
    while (getModules.next()) {
      Query	getIcons = new Query(getModel().getDBContext().getDefaultConnection());
      String	icon = null;

      try {
	if (getModules.getInt(7) != 0) {
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

  private void fetchGroupRights(List modules) throws SQLException {
    fetchRights(modules, SELECT_GROUP_RIGHTS);
  }

  private void fetchUserRights(List modules) throws SQLException {
    fetchRights(modules, SELECT_USER_RIGHTS);
  }

  private void fetchRights(List modules, String queryText) throws SQLException {
    Query	getRights = new Query(getModel().getDBContext().getDefaultConnection());

    if (userName != null) {
      getRights.addString("(SELECT ID FROM KOPI_USERS WHERE Kurzname = \'" + userName + "\')");
    } else {
      getRights.addInt(getUserID());
    }
    getRights.open(queryText);
    while (getRights.next()) {
      Module		module = findModuleById(modules, getRights.getInt(1));

      if (module != null) {
	module.setAccessibility(getRights.getBoolean(2) ? Module.ACS_TRUE : Module.ACS_FALSE);
      }
    }
    getRights.close();
  }

  private Module findModuleById(List modules, int id) {
    for (int i = 0; i < modules.size(); i++) {
      Module	module = (Module)modules.get(i);

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
    Query	getFavorites = new Query(getModel().getDBContext().getDefaultConnection());

    getFavorites.open("SELECT F.Modul, F.ID FROM FAVORITEN F WHERE F.Benutzer = " + getUserID() + " ORDER BY 2");
    while (getFavorites.next()) {
      int		id = getFavorites.getInt(1);

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
    List	localModules = new ArrayList();

    try {
      getModel().getDBContext().startWork();	// !!! BEGIN_SYNC

      localModules = fetchModules(ApplicationConfiguration.getConfiguration().isUnicodeDatabase());
      fetchGroupRights(localModules);
      fetchUserRights(localModules);
      if (loadFavorites) {
	fetchFavorites();
      }

      getModel().getDBContext().commitWork();
    } catch (SQLException e) {
      try {
	getModel().getDBContext().abortWork();
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

    array = (Module[])Utils.toArray(localModules, Module.class);
    return array;
  }

  /**
   * Sets the tool tip in the foot panel
   */
  public void setToolTip(String s) {
    setInformationText(s);
  }

  /**
   * Sets the title of the frame
   */
  public void setTitle(String s) {
    super.setTitle(s + " - " + Message.getMessage("program_menu"));
  }

  private void callSelectedForm() {
    getModel().performAsyncAction(new KopiAction("menu_form_started2") {
        public void execute() throws VException {
          launchSelectedForm();
        }
      });
  }
  /**
   *
   */
  private void launchSelectedForm() throws VException {
    DefaultMutableTreeNode	node = getSelectedNode();

    if (node != null) {
      final Module      module = (Module)node.getUserObject();

      if (isSuperUser()) {
	module.setAccessibility((module.getAccessibility() + 1) % 3);
	((DefaultTreeModel)tree.getModel()).nodeChanged(node);
      } else if (node.isLeaf()) {
        setWaitInfo(Message.getMessage("menu_form_started"));

        module.run(getModel().getDBContext());
        unsetWaitInfo();
      }
    }
  }

    /**
     * start a block and enter in the good field (rec)
     */
    public void run() {
    }

  /**
   * Called to close the view (from the user), it does not
   * definitly close the view(it may ask the user before)
   * Allowed to call outside the event disp. thread
   */
  public final void closeWindow() {
    // ensure that it is executed in event dispatch Thread
    SwingThreadHandler.startAndWait(new Runnable() {
        public void run () {
          if (!isSuperUser()
              && askUser(Message.getMessage("confirm_quit"), false))
            {
              Application.quit();
            } 
        }
      });
  }

  // ---------------------------------------------------------------------
  // INNER CLASSES
  // ---------------------------------------------------------------------

  private static class MenuTreeModel extends VWindow {

    public MenuTreeModel() {
      super.setTitle(Message.getMessage("program_menu"));

      MenuTree.actors = new SActor[9];

      createActor(CMD_QUIT, "File", "Close", "quit", 0 /*KeyEvent.VK_ESCAPE*/, 0);
      createActor(CMD_OPEN, "Edit", "Open", "open", KeyEvent.VK_ENTER, 0);
      createActor(CMD_SHOW, "Edit", "Show", null, 0, 0);
      createActor(CMD_ADD, "Edit", "Add", null, 0, 0);
      createActor(CMD_REMOVE, "Edit", "Remove", null, 0, 0);
      createActor(CMD_FOLD, "Edit", "Fold", "fold", KeyEvent.VK_ENTER, 0);
      createActor(CMD_UNFOLD, "Edit", "Unfold", "unfold", KeyEvent.VK_ENTER, 0);
      createActor(CMD_INFORMATION, "Help", "Information", null, 0, 0);
      createActor(CMD_HELP, "Help", "Help", "help", KeyEvent.VK_F1, 0);

      setActors(MenuTree.actors);

      // localize the menu tree using the default locale
      localize(Locale.getDefault());

    }

    private void createActor(int number,
                             String menu,
                             String item,
                             String icon,
                             int key,
                             int modifier)
    {
      MenuTree.actors[number] = new SActor(menu,
                                           MENU_LOCALIZATION_RESOURCE,
                                           item,
                                           MENU_LOCALIZATION_RESOURCE,
                                           icon,
                                           key,
                                           modifier);
      MenuTree.actors[number].setNumber(number);
    }
    
    /**
     * Localize this menu tree
     * 
     * @param     locale  the locale to use
     */
    public void localize(Locale locale) {
      LocalizationManager         manager;
      
      manager = new LocalizationManager(locale);
      
      // localizes the actors in VWindow
      super.localizeActors(manager);
      
      manager = null;
    }
    
    /**
     * Performs the appropriate action.
     *
     * @param	actor		the number of the actor.
     * @return	true iff an action was found for the specified number
     */
    public void executeVoidTrigger(final int key) throws VException {
      MenuTree	currentDisplay = (MenuTree)getDisplay();

      switch (key) {
      case CMD_QUIT:
	currentDisplay.closeWindow();
	break;
      case CMD_OPEN:
	currentDisplay.launchSelectedForm();
	break;
      case CMD_SHOW:
	currentDisplay.toolbar.show();
	currentDisplay.toolbar.toFront();
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
	currentDisplay.tree.collapseRow(currentDisplay.tree.getSelectionRows()[0]);
	break;
      case CMD_UNFOLD:
	currentDisplay.tree.expandRow(currentDisplay.tree.getSelectionRows()[0]);
	break;
      case CMD_INFORMATION:
        {
          String[]      versionArray = Utils.getVersion();
          String      version = "";
          for (int i=0; i<versionArray.length; i++) {
            version += "\n" + versionArray[i];
          }
          getDisplay().showApplicationInformation(Application.getDefaults().getInformationText() + version);
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


    protected void buildDisplay() {
      // already made
    }
  }

  private void setActorEnabled(int actor, boolean enabled) {
    actors[actor].setHandler(getModel());
    actors[actor].setEnabled(enabled);
  }

  /*
   * Returns the ID of the current user
   */
  private int getUserID() {
    return getModel().getDBContext().getDefaultConnection().getUserID();
  }


  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

    /**
     * Localize this menu tree
     * 
     * @param     locale  the locale to use
     */
    public void localize(Locale locale) {
      LocalizationManager         manager;
      
      manager = new LocalizationManager(locale);
      
      // localizes the modules
      for (ListIterator i = items.listIterator(); i.hasNext(); ) {
        Module          item;

        item = (Module)i.next();
        item.localize(manager);
      }
      
      manager = null;
    }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private static final String	SELECT_MODULES =
    " SELECT    M.ID, M.Vater, M.Kurzname, M.Quelle, M.Objekt, M.Prioritaet, M.Symbol" +
    " FROM      MODULE M" +
    " ORDER BY  6 DESC, 1";

  private static final String	SELECT_USER_RIGHTS =
    " SELECT	M.ID, B.Zugriff, M.Prioritaet" +
    " FROM	MODULE M, BENUTZERRECHTE B" +
    " WHERE	M.ID = B.Modul" +
    " AND	B.Benutzer = $1" +
    " ORDER BY	3, 1";

  private static final String	SELECT_GROUP_RIGHTS =
    " SELECT	DISTINCT M.ID, G.Zugriff, M.Prioritaet" +
    " FROM	MODULE M, GRUPPENRECHTE G, GRUPPENZUGEHOERIGKEITEN Z" +
    " WHERE	M.ID = G.Modul" +
    " AND	G.Gruppe = Z.Gruppe" +
    " AND	Z.Benutzer = $1" +
    " ORDER BY	3, 1";

  private static final int	CMD_QUIT	= 0;
  private static final int	CMD_OPEN	= 1;
  private static final int	CMD_SHOW	= 2;
  private static final int	CMD_ADD		= 3;
  private static final int	CMD_REMOVE	= 4;
  private static final int	CMD_FOLD	= 5;
  private static final int	CMD_UNFOLD	= 6;
  private static final int	CMD_INFORMATION = 7;
  private static final int	CMD_HELP	= 8;

  private static final String   MENU_LOCALIZATION_RESOURCE = "com/kopiright/vkopi/lib/resource/Menu";

  private static SActor[]	actors;

  private JTree                 tree;
  private List  		items;
  private JPanel		buttons;
  private JBookmarkPanel	toolbar;
  private Hashtable		shortcuts;
  private ArrayList		orderdShorts;
  private List                  modules;

  private MenuItemRenderer	itemRenderer;
  private String		userName;
  private boolean		isSuperUser;
  private List                  shortcutsID;
  private Module[]		array;
}
