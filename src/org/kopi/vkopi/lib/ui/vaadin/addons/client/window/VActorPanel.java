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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.window;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.VActorsNavigationPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * The actor components container.
 */
public class VActorPanel extends FlowPanel implements ResizeHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new actor panel.
   * @param connection The application connection
   */
  public VActorPanel(ApplicationConnection connection) {
    setStyleName(Styles.WINDOW_VIEW_ACTORS);
    getElement().setId("actors");
    actorsContainer = new FlowPanel();
    actorsPane = new SimplePanel();
    menu = new SimplePanel();
    menu.getElement().setId("menu");
    actorsContainer.setStyleName("actors-slide-menu");
    actorsPane.setWidget(actorsContainer);
    add(menu);
    add(actorsPane);
    actors = new LinkedList<Widget>();
    actorsWidths = new HashMap<Widget, Integer>();
    moreActors = new VMoreActors(connection);
    actorsNavigationItem = new VActorsRootNavigationItem(connection);
    menu.setWidget(actorsNavigationItem);
    Window.addResizeHandler(this);
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Adds an actor to this actor panel.
   * @param actor The actor to be added.
   */
  public void addActor(Widget actor) {
    actors.add(actor);
    actorsContainer.add(actor);
  }
  
  /**
   * Adds the actors menu to be shown.
   * @param panel The menu to be shown.
   */
  public void addActorsNavigationPanel(VActorsNavigationPanel panel) {
    actorsNavigationItem.setActorsNavigationPanel(panel);
  }
  
  /**
   * Removes an actor from this panel.
   * @param actor The actor to be removed.
   */
  protected void removeActor(Widget actor) {
    actors.remove(actor);
    actorsContainer.remove(actor);
  }
  
  /**
   * Returns the total actor width.
   * @return The total actor width.
   */
  protected int getActorsWidth() {
    if (actors == null || actors.isEmpty()) {
      return 0;
    }
    
    int		width = 0;
    
    for (Widget actor : actors) {
      if (actor != null) {
	width += actor.getElement().getClientWidth();
	if (!actorsWidths.containsKey(actor)) {
	  actorsWidths.put(actor, actor.getElement().getClientWidth());
	}
      }
    }
    
    return width;
  }
  
  /**
   * Renders the actor panel according to the total actor width.
   * @param width The browser window width.
   */
  protected void render(int width) {
    if (getActorsWidth() > width) {
      addMoreActors(width);
    } else {
      restoreExtraActors(width);
    }
  }
  
  /**
   * Renders the more actors list.
   * @param width The browser window width.
   */
  @SuppressWarnings("unchecked")
  protected <T extends Widget & HasEnabled> void addMoreActors(final int width) {
    LinkedList<Widget>		extraActors;
    
    extraActors = getExtraActors(width);
    if (!extraActors.isEmpty()) {
      if (moreActors.isEmpty()) {
	for (Widget actor : extraActors) {
	  if (actor != null) {
	    moreActors.addActor((T)actor);
	  }
	}
      } else {
	LinkedList<Widget>	allActors;
	
	allActors = new LinkedList<Widget>(extraActors);
	allActors.addAll(moreActors.getActors());
	moreActors.clear();
	for (Widget actor : allActors) {
	  if (actor != null) {
	    moreActors.addActor((T)actor);
	  }
	}
      }
      
      if (!actors.contains(moreActors)) {
	addActor(moreActors);
      }
      
      // Ensure that all actors are well calculated after the final attach.
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

	@Override
	public void execute() {
	  addMoreActors(width);
	}
      });
    }
  }
  
  /**
   * Returns the list of actors that has exceeded the total browser window width.
   * @param width The browser window width.
   * @return The of the extra actors.
   */
  protected LinkedList<Widget> getExtraActors(int width) {
    LinkedList<Widget>		extraActors;
    
    extraActors = new LinkedList<Widget>();
    // add spacing between actors.
    while (getActorsWidth() + getMoreActorsWidth() > width) {
      Widget		actor;

      actor = actors.peekLast();
      if (moreActors != null && actor == moreActors) {
	if (actors.size() >= 2) {
	  actor = actors.get(actors.size() - 2);
	} else {
	  actor = null;
	}
      }

      if (actor != null) {
	extraActors.addFirst(actor);
	removeActor(actor);
      }
    }
    
    return extraActors;
  }
  
  /**
   * Returns the more item client width.
   * @return The more item client width.
   */
  protected int getMoreActorsWidth() {
    return actors.contains(moreActors) ? moreActors.getElement().getOffsetWidth() :  0;
  }
  
  @Override
  public void onResize(ResizeEvent event) {
    render(event.getWidth() - RESERVED_WIDTH);
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
        render(Window.getClientWidth() - RESERVED_WIDTH);
      }
    });
  }
  
  /**
   * Restores the extra actors in the actor panel.
   */
  protected void restoreExtraActors(int width) {
    if (actors == null || !actors.contains(moreActors)) {
      return; // no extra actors are added.
    }
    
    for (Widget actor : moreActors.getActors()) {
      if (getMoreActorsWidth() + getActorsWidth() + actorsWidths.get(actor) < width) {
	// the actor should be restored
        moreActors.removeActor(actor);
	removeActor(moreActors);
	addActor(actor);
	if (moreActors.isEmpty()) {
	  removeActor(moreActors);
	  break;
	} else {
	  addActor(moreActors);
	}
      } else {
	break;
      }
    }
  }
  
  /**
   * Returns the element associated with the actor menu to be used for tooltip.
   * @return The element associated with the actor menu.
   */
  protected Element getActorsNavigationElement() {
    return actorsNavigationItem.getIconElement();
  }
  
  @Override
  public void clear() {
    super.clear();
    actorsContainer.clear();
    actorsContainer = null;
    actors.clear();
    actors = null;
    moreActors = null;
    actorsWidths.clear();
    actorsWidths = null;
    actorsNavigationItem = null;
    menu = null;
    actorsPane = null; 
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private FlowPanel                             actorsContainer;
  private SimplePanel                           menu;
  private SimplePanel                           actorsPane;
  private LinkedList<Widget>                    actors;
  private VMoreActors                           moreActors;
  private Map<Widget, Integer>                  actorsWidths;
  private VActorsRootNavigationItem             actorsNavigationItem;
  private final static int                      RESERVED_WIDTH = 80;
}
