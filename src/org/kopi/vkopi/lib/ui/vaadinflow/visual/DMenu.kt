/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.vkopi.lib.ui.vaadinflow.visual

import java.io.File

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

import org.kopi.vkopi.lib.ui.vaadinflow.base.BackgroundThreadHandler
import org.kopi.vkopi.lib.ui.vaadinflow.base.BackgroundThreadHandler.accessAndPush
import org.kopi.vkopi.lib.ui.vaadinflow.menu.ModuleItem
import org.kopi.vkopi.lib.ui.vaadinflow.menu.ModuleList
import org.kopi.vkopi.lib.ui.vaadinflow.wait.WaitWindow
import org.kopi.vkopi.lib.visual.Action
import org.kopi.vkopi.lib.visual.ApplicationContext
import org.kopi.vkopi.lib.visual.Module
import org.kopi.vkopi.lib.visual.RootMenu
import org.kopi.vkopi.lib.visual.UMenuTree
import org.kopi.vkopi.lib.visual.VException
import org.kopi.vkopi.lib.visual.VMenuTree
import org.kopi.vkopi.lib.visual.VlibProperties

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.MenuItem

/**
 * A module menu implementation that uses the menu tree
 * model. This will not display a menu tree but an horizontal
 * menu with vertical sub menus drops.
 *
 * @param model The menu tree model.
 */
abstract class DMenu protected constructor(private val model: VMenuTree) : ModuleList(), UMenuTree {

  /**
   * Returns the type associated with this menu.
   * This is the ID of a root menu provided by the
   * menu tree model.
   * @return The type of this menu.
   */
  abstract val type: Int

  protected val modules = hashMapOf<Int, Module>()

  private val waitIndicator = WaitWindow()

  init {
    model.setDisplay(this)
    // directly build menu content.
    buildMenu(model.getRoots())
  }

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------

  protected open fun buildMenu(roots: List<RootMenu>) {
    for (rootMenu in roots) {
      if (rootMenu.getId() == type && !rootMenu.isEmpty()) {
        buildModuleMenu(rootMenu.root, null)
      }
    }
  }

  /**
   * Builds the menu bar from a root item and a parent menu item.
   * @param root The menu bar root item.
   * @param parent The parent menu item.
   */
  protected open fun buildModuleMenu(root: TreeNode?, parent: MenuItem?) {
    for (i in 0 until root!!.childCount) {
      toModuleItem(root.getChildAt(i) as DefaultMutableTreeNode, parent)
    }
  }

  /**
   * Converts the given [DefaultMutableTreeNode] to a [ModuleItem]
   * @param node The [DefaultMutableTreeNode] object.
   * @param parent The parent [ModuleItem] that holds the created menu item.
   */
  protected open fun toModuleItem(node: DefaultMutableTreeNode, parent: MenuItem?) {
    val module = node.userObject as Module

    val item = toModuleItem(module, parent)
    modules[module.id] = module
    // build module children
    for (i in 0 until node.childCount) {
      toModuleItem(node.getChildAt(i) as DefaultMutableTreeNode, item)
    }
  }

  /**
   * Converts the given [Module] to a [ModuleItem]
   * @param module The [Module] object.
   * @param parent The parent [ModuleItem] that holds the created menu item.
   * @return The created item for the given module.
   */
  protected open fun toModuleItem(module: Module, parent: MenuItem?): MenuItem {
    val menu = if (parent != null) {
      parent.subMenu.addItem(module.description)
    } else {
      // add it as a root module
      addItem(module.description, module.help)
    }

    if(module.`object` != null) {
      menu.addClickListener {
        launchModule(module)
      }
    }

    return menu
  }

  /**
   * Returns the module having the given id.
   * @param id
   * @return
   */
  protected fun getModuleById(id: String?): Module? = modules[Integer.valueOf(id)]

  /**
   * Returns the menu model.
   * @return The menu model.
   */
  override fun getModel(): VMenuTree = model

  /**
   * Launches the given [Module].
   * @param module The module to be launched.
   */
  protected fun launchModule(module: Module) {
    performAsyncAction(object : Action("menu_form_started2") {
      override fun execute() {
        try {
          setWaitInfo(VlibProperties.getString("menu_form_started"))
          module.run(model.dbContext!!)
        } finally {
          unsetWaitInfo()
        }
      }
    })
  }

  /**
   * Returns the current application instance.
   * @return the current application instance.
   */
  protected val application: VApplication
    get() = ApplicationContext.getApplicationContext().getApplication() as VApplication

  //---------------------------------------------------
  // MENU TREE IMPLEMENTATION
  //---------------------------------------------------
  override fun run() {
  }

  override fun setTitle(title: String) {}
  override fun setInformationText(text: String?) {}
  override fun setTotalJobs(totalJobs: Int) {}
  override fun setCurrentJob(currentJob: Int) {}
  override fun updateWaitDialogMessage(message: String) {}
  override fun closeWindow() {
    application.logout()
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {}
  override fun performBasicAction(action: Action) {
    action.run()
  }

  override fun performAsyncAction(action: Action) {
    // Force the current UI in case the thread is started before attaching the menu to the UI.
    if (currentUI == null) {
      currentUI = BackgroundThreadHandler.locateUI()
    }
    Thread {
      BackgroundThreadHandler.setUI(currentUI)
      try {
        action.execute()
      } catch (e: VException) {
        application.error(e.message)
      }
    }.start()
  }

  override fun modelClosed(type: Int) {}
  override fun setWaitDialog(message: String, maxtime: Int) {}
  override fun unsetWaitDialog() {}
  override fun setWaitInfo(message: String?) {
    accessAndPush(currentUI) {
      waitIndicator.setText(message)
      waitIndicator.show()
    }
  }

  override fun unsetWaitInfo() {
    accessAndPush(currentUI) {
      waitIndicator.setText(null)
      waitIndicator.close()
    }
  }


  var currentUI: UI? = null

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {}
  override fun unsetProgressDialog() {}
  override fun fileProduced(file: File, name: String) {}
  override fun getTree(): UMenuTree.UTree? = null
  override fun getBookmark(): UMenuTree.UBookmarkPanel? = null

  override fun launchSelectedForm() {
  }

  override fun addSelectedElement() {}
  override fun setMenu() {}
  override fun removeSelectedElement() {}
  override fun gotoShortcuts() {
    // TODO
  }

  override fun showApplicationInformation(message: String) {}
}
