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

import java.awt.Event;
import java.awt.event.KeyEvent;

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.Image;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.visual.UActor;
import com.kopiright.vkopi.lib.visual.VActor;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

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
public class DActor implements UActor {
	
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------

  /**
   * Creates a new <code>DActor</code> object.
   * @param model The actor model.
   * @param viewer The actor actions viewer.
   */
  public DActor(VActor model, DWindow viewer) {
    this.model = model;
    this.viewer = viewer;
    model.setDisplay(this);
    init();
  }

  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------

  @Override
  public boolean isEnabled() {
    if (button != null) {
      return button.isEnabled();
    } else {
      return viewer.hasAction(shortcutHandler);
    }
  }

  @Override
  public void setEnabled(final boolean enabled) {
    BackgroundThreadHandler.start(new Runnable() {

      @Override
      public void run() {
	if (button != null) {
	  if (button.isEnabled() != enabled) {
	    button.setEnabled(enabled);
	  }
	}
	// set actor shortcut
	if (enabled) {
	  viewer.addAction(shortcutHandler);
	} else {
	  viewer.removeAction(shortcutHandler);
	}
      }
    });
  }

  @Override
  public boolean isVisible() {
    if (button != null) {
      return button.isVisible();
    } else {  
      return false;
    }
  }

  @Override
  public void setVisible(final boolean visible) {
    if (button != null) {
      BackgroundThreadHandler.start(new Runnable() {
	
	@Override
	public void run() {
	  button.setVisible(visible);
	}
      });
    }
  }

  @Override
  public void setModel(VActor model) {
    this.model = model;
  }

  @Override
  public VActor getModel() {
    return model;
  }
  
  // --------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------
  
  /**
   * The <code>DActor</code> initialization.
   */
  public void init() {
    if (model.iconName != null) { 
      button = new Button(model.menuItem, new ClickHandler());
      button.setClickShortcut(correctAcceleratorKey(model.acceleratorKey));
      button.addStyleName(KopiTheme.BUTTON_LINK);	
      button.addStyleName(KopiTheme.BUTTON_PANEL_ACTOR_STYLE);
      button.setIcon(loadImage(model.iconName).getResource());
      button.setEnabled(false);
      if (model.acceleratorKey > 0) {
        button.setDescription(model.help+" ["+KeyEvent.getKeyText(model.acceleratorKey)+"]");
      } else {
	button.setDescription(model.help);
      }
    } else {
      if (model.getActorIdent() == "AddConfiguration") {
	model.iconName = "add"; // FIXME ==> change icon name
	init();
      } else if (model.getActorIdent() == "RemoveConfiguration") {
	model.iconName = "update"; // FIXME ==> change icon name
	init();
      }    
    }
    
    //Shortcuts
    shortcutHandler = new ShortcutHandler(model.menuItem,
		                          correctAcceleratorKey(model.acceleratorKey),
		                          correctAcceleratorModifier(model.acceleratorModifier));
    setEnabled(false);
  }

  /**
   * Returns the actor button. The button is <code>null</code> when the actor is not visible.
   * @return The actor button.
   */
  public Button getButton() {
    return button;
  }  
	
  /**
   * Loads the actor icon.
   * @param iconName The icon name.
   */
  private Image loadImage(String iconName) {
    Image  	image;

    image = Utils.getImage(iconName + ".png");
    if (image == null || image == Utils.UKN_IMAGE) {
      image = Utils.getImage(iconName + ".gif");
    }
    
    return image;
  }

  /**
   * Returns the corrected accelerator key.
   * @param acceleratorKey The original accelerator key.
   * @return The corrected accelerator key.
   */
  private int correctAcceleratorKey(int acceleratorKey) {
    return acceleratorKey == 10 ? 13 : acceleratorKey;
  }
  
  /**
   * Returns the corrected modifier accelerator key.
   * @param acceleratorModifier The original modifier accelerator key.
   * @return The corrected modifier accelerator key.
   */
  private int correctAcceleratorModifier (int acceleratorModifier) {
    int correctAcceleratorModifier = 0;
    
    switch (acceleratorModifier) {
    case Event.SHIFT_MASK:
      correctAcceleratorModifier = ModifierKey.SHIFT;
      break;
    case Event.ALT_MASK:
      correctAcceleratorModifier = ModifierKey.ALT;
      break;
    case Event.CTRL_MASK:
      correctAcceleratorModifier = ModifierKey.CTRL;
      break;
    case Event.META_MASK:
      correctAcceleratorModifier = ModifierKey.META;
      break;
    }
    
    return correctAcceleratorModifier; 
  }
	  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------

  /**
   * The <code>ClickHandler</code> is the {@link ClickListener}
   * implementation of the actor button.
   */
  @SuppressWarnings("serial")
  private final class ClickHandler implements ClickListener {
    
    @Override
    public void buttonClick(ClickEvent event) {
      if (model != null) {
        model.performAction();
      }
    }
  }

  /**
   * The <code>ShortcutHandler</code> is the {@link ShortcutListener}
   * implementation of the actor action.
   */
  @SuppressWarnings("serial")
  private final class ShortcutHandler extends ShortcutListener {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>ShortcutHandler</code> object.
     * @param caption The action caption.
     * @param keyCode The action key code.
     * @param modifierKey The action modifier key.
     */
    public ShortcutHandler(String caption, int keyCode, int modifierKey) {
      super(caption, keyCode, new int[] {modifierKey});
    }

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public void handleAction(Object sender, Object target) {
      if (model != null && sender == viewer) {
	model.performAction();
      }
    }
  }
	  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private Button				button;
  private VActor				model;
  private DWindow				viewer;
  private ShortcutHandler	        	shortcutHandler;
}
