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

import org.kopi.vkopi.lib.form.UChartLabel;
import org.kopi.vkopi.lib.form.ULabel;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorLabel;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;
import org.kopi.vkopi.lib.visual.VActor;
import org.kopi.vkopi.lib.visual.VCommand;

/**
 * The editor label used as grid component header.
 */
@SuppressWarnings("serial")
public class DGridEditorLabel extends GridEditorLabel implements ULabel, UChartLabel {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public DGridEditorLabel(String text, String help) {
    super(text);
    init(text, help);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void init(final String text, final String tooltip) {
    this.tooltip = tooltip;
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        setCaption(text);
        setDescription(Utils.createTooltip(tooltip));
      }
    });
  }
  
  @Override
  public void orderChanged() {}

  @Override
  public void repaint() {}
  
  @Override
  public void setInfoText(final String info) {
    infoText = info;
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        DGridEditorLabel.super.setInfoText(info);
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
        if (model.getModel().getAccess(row) == VConstants.ACS_SKIPPED) {
          // Only show base help on a skipped field
          // Actors are not shown since they are not active.
          setDescription(Utils.createTooltip(tooltip));
        } else {
          setDescription(Utils.createTooltip(buildDescription(model, tooltip)));
        }
      }
    });
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
    String              description;
    VCommand[]          commands;
    
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
          description += getDescription(commands[i].getActor());
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
  
  private String                        infoText;
  private String                        tooltip;
}
