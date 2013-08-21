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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.kopiright.vkopi.lib.ui.base.JMenuButton;

public class DActor {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public DActor(VActor model) {
    this.model = model;
    model.setDisplay(this);
    init();
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * @return The actor state (enabled or disabled)
   */
  public boolean isEnabled() {
    return action.isEnabled();
  }

  /**
   * Sets the actor state (enabled or disabled)
   */
  public void setEnabled(boolean enabled) {
    action.setEnabled(enabled);
  }

  /**
   * @return The actor visibility
   */
  public boolean isVisible() {
    return button != null && button.isVisible();
  }

  /**
   * Sets the actor visibility
   */
  public void setVisible(boolean visible) {
    if (button != null) {
      button.setVisible(visible);
    }
  }

  /**
   * Sets the actor model
   */
  public void setModel(VActor model) {
    this.model = model;
    init();
  }

  /**
   * @return The actor model
   */
  public VActor getModel() {
    return model;
  }

  public JButton getButton() {
    return button;
  }

  /**
   * Returns the actor action.
   */
  public Action getAction() {
    return action;
  }


  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  /**
   * Loads the actor icon
   */
  private final ImageIcon loadImage(String iconName) {
    ImageIcon   image;

    image = Utils.getImage(iconName + ".png");

    if (image == null || image == Utils.UKN_IMAGE) {
      image = Utils.getImage(iconName + ".gif");
    }

    return image;
  }

  private void init() {
    action = new DActorAction(model.menuItem,
                              (model.iconName != null) ?
                              loadImage(model.iconName) :
                              null);
    if (model.acceleratorKey != KeyEvent.VK_UNDEFINED) {
      action.putValue(Action.ACCELERATOR_KEY,
                      KeyStroke.getKeyStroke(model.acceleratorKey,
                                             model.acceleratorModifier));
    }

    action.putValue(Action.SHORT_DESCRIPTION, model.help);
    action.setEnabled(false);

    if (button == null) {
      button = new JMenuButton(action);
    }
  }

  // --------------------------------------------------------------------
  // DACTOR ACTION
  // --------------------------------------------------------------------

  private class DActorAction extends AbstractAction {

    // -----------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------

    /*package*/ DActorAction(String name, Icon icon) {
      super(name, icon);
    }

    // ------------------------------------------------
    // ABSTRACT ACTION IMPLEMNTATION
    // ------------------------------------------------

    public void actionPerformed(ActionEvent e) {
      model.performAction();
    }

    // -------------------------------------------------
    // DATA MEMBERS
    // -------------------------------------------------

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long	serialVersionUID = -6510825866215273279L;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JButton					button;
  private Action					action;
  private VActor					model;
}
