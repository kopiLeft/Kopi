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

package org.kopi.vkopi.lib.ui.vaadin.form;

import java.awt.event.KeyEvent;

import org.kopi.vkopi.lib.form.ULabel;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.SortableLabel;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.VActor;
import org.kopi.vkopi.lib.visual.VCommand;

@SuppressWarnings("serial")
public class DLabel extends SortableLabel implements ULabel {
 
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DLabel</code> instance.
   * @param text The label text.
   * @param help The label help.
   */
  public DLabel(String text, String help) {
    super(text);
    setSortable(false);
    init(text, help);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the label in detail mode.
   * @param mode The detail mode ability.
   */
  public void setInDetailMode(boolean mode) {
    this.detailMode = mode;
  }
  
  /**
   * Returns {@code true} is the label is in detail mode.
   * @return {@code true} is the label is in detail mode.
   */
  public boolean isInDetail() {
    return detailMode;
  }
  
  /**
   * Prepares the label's snapshot.
   * @param activ The field state.
   */
  public void prepareSnapshot(boolean activ) {
    // TODO
  }
  
  @Override
  public void init(final String text, final String toolTip) {
    this.tooltip = toolTip;
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	setCaption(text);
	setDescription(toolTip);
      }
    });
  }
  
  /**
   * Sets the info text.
   * @param info The info text.
   */
  public void setInfoText(final String info) {
    infoText = info;
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	DLabel.super.setInfoText(info);
      }
    });
  }
  
  /**
   * Returns the info text.
   * @return The info text.
   */
  public String getInfoText() {
    return infoText;
  }
  
  /**
   * Updates the label content.
   * @param model The field model.
   * @param row The field row.
   */
  public void update(final VFieldUI model, final int row) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
	updateStyles(model.getModel().getAccess(row), model.getModel().hasFocus());
	if (model.getModel().getAccess(row) == VConstants.ACS_SKIPPED) {
	  // Only show base help on a skipped field
	  // Actors are not shown since they are not active.
	  setDescription(tooltip);
	} else {
	  setDescription(buildDescription(model, tooltip));
	}
	setAutofill(model.hasAutofill());
	if (model.getModel().getAccess(row) == VConstants.ACS_HIDDEN) {
	  if (getState().visible) {
	    setVisible(false);
	  }
	} else {
	  if (!getState().visible) {
	    setVisible(true);
	  }
	}
      }
    });
  }
  
  /**
   * Updates the label styles according to the field access.
   * @param access The field access
   */
  private void updateStyles(int access, boolean focused) {
    removeStyleName("visit");
    removeStyleName("skipped");
    removeStyleName("mustfill");
    removeStyleName("hidden");
    removeStyleName("focused");
    // The focus style is the major style
    if (focused) {
      addStyleName("focused");      
    } else {
      switch (access) {
      case VConstants.ACS_VISIT:
        addStyleName("visit");
        break;
      case VConstants.ACS_SKIPPED:
        addStyleName("skipped");
        break;
      case VConstants.ACS_MUSTFILL:
        addStyleName("mustfill");
        break;
      case VConstants.ACS_HIDDEN:
        addStyleName("hidden");
        break;
      default:
        addStyleName("visit");
        break;
      }
    }
  }
  
  /**
   * Returns the label text.
   * @return The label text.
   */
  public String getText() {
    return getCaption();
  }
  
  /**
   * Builds full field description.
   * @param model The field model.
   * @param tooltip The initial field tooltip.
   * @return The full field description.
   */
  protected String buildDescription(VFieldUI model, String tooltip) {
    String		description;
    VCommand[]		commands;
    
    commands = model.getAllCommands();
    if (tooltip == null) {
      tooltip = ""; // avoid writing null in help tooltip.
    }
    description = tooltip;
    if (commands.length > 0) {
      description = "<html>" + tooltip;
      
      for (int i = 0; i < commands.length; i++) {
        if (commands[i].getActor() != null) {
          if (description.trim().length() > 0) {
            description += "<br>";
          }
          description+= getDescription(commands[i].getActor());
        }
      }
    }
    
    return description;
  }
  
  /**
   * Creates the actor description.
   * @param actor The actor model.
   * @return The actor description.
   */
  private static String getDescription(VActor actor) {
    if (actor.acceleratorKey > 0) {
      if (actor.acceleratorModifier == 0) {
        return actor.menuItem + " [" + KeyEvent.getKeyText(actor.acceleratorKey) + "]";
      } else {
        return actor.menuItem + " [" + KeyEvent.getKeyModifiersText(actor.acceleratorModifier) + "-" + KeyEvent.getKeyText(actor.acceleratorKey) + "]";
      }
    } else {
      return actor.menuItem;
    }
  }
 
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private String			infoText;
  private boolean			detailMode;
  private String			tooltip;
}
