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

import java.awt.event.KeyEvent;

import org.kopi.vaadin.addons.ActionEvent;
import org.kopi.vaadin.addons.ActionListener;
import org.kopi.vaadin.addons.Actor;

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.Image;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.visual.UActor;
import com.kopiright.vkopi.lib.visual.VActor;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

/**
 * The <code>DActor</code> is the vaadin implementation of
 * the {@link UActor}. The actor can be represented by a {@link Button}
 * if it has a valid icon name.
 * 
 * <p>
 *   The actor action is handled by a {@link ShortcutListener} registered
 *   of the {@link DWindow} which is the receiver of all actors actions.
 * </p>
 */
@SuppressWarnings("serial")
public class DActor extends Actor implements UActor, ActionListener {
	
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------

  /**
   * Creates the visible actor component from a given model.
   * @param model The <b>not null</b> model.
   */
  public DActor(VActor model) {
    super(model.menuItem,
	  getDescription(model),
	  loadResource(model.iconName),
	  correctAcceleratorKey(model.acceleratorKey),
	  correctAcceleratorModifier(model.acceleratorModifier));
    this.model = model;
    setEnabled(false);
    model.setDisplay(this);
    addActionListener(this);
  }

  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------

  @Override
  public void setModel(VActor model) {
    this.model = model;
  }

  @Override
  public VActor getModel() {
    return model;
  }
  
  @Override
  public void setEnabled(final boolean enabled) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	DActor.super.setEnabled(enabled);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (model != null && event.getActor() == this) {
      model.performAction();
    }
  }
  
  // --------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------
  
  /**
   * Creates the actor description.
   * @param model The actor model.
   * @return The actor description.
   */
  private static String getDescription(VActor model) {
    if (model.acceleratorKey > 0) {
      return model.help + " ["+ KeyEvent.getKeyText(model.acceleratorKey) + "]";
    } else {
      return model.help;
    }
  }
	
  /**
   * Loads the actor icon.
   * @param iconName The icon name.
   */
  private static Resource loadResource(String iconName) {
    if (iconName == null) {
      return null;
    }
    
    Image  		image;

    image = Utils.getImage(iconName + ".png");
    if (image == null || image == Utils.UKN_IMAGE) {
      image = Utils.getImage(iconName + ".gif");
    }
    
    return image.getResource();
  }

  /**
   * Returns the corrected accelerator key.
   * @param acceleratorKey The original accelerator key.
   * @return The corrected accelerator key.
   */
  private static int correctAcceleratorKey(int acceleratorKey) {
    return acceleratorKey == 10 ? 13 : acceleratorKey;
  }
  
  /**
   * Returns the corrected modifier accelerator key.
   * @param acceleratorModifier The original modifier accelerator key.
   * @return The corrected modifier accelerator key.
   */
  private static int correctAcceleratorModifier (int acceleratorModifier) {
    int correctAcceleratorModifier = 0;
    
    switch (acceleratorModifier) {
    case java.awt.Event.SHIFT_MASK:
      correctAcceleratorModifier = ModifierKey.SHIFT;
      break;
    case java.awt.Event.ALT_MASK:
      correctAcceleratorModifier = ModifierKey.ALT;
      break;
    case java.awt.Event.CTRL_MASK:
      correctAcceleratorModifier = ModifierKey.CTRL;
      break;
    case java.awt.Event.META_MASK:
      correctAcceleratorModifier = ModifierKey.META;
      break;
    }
    
    return correctAcceleratorModifier; 
  }
	  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private VActor				model;
}
