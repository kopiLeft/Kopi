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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion;

import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * <p>
 * The default implementation of {@link SuggestionDisplay} displays
 * suggestions in a {@link PopupPanel} beneath the {@link SuggestBox}.
 * </p>
 */
public class DefaultSuggestionDisplay extends SuggestionDisplay implements HasAnimation {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Construct a new {@link DefaultSuggestionDisplay}.
   * @param connection The application connection
   */
  public DefaultSuggestionDisplay(ApplicationConnection connection) {
    suggestionPopup = createPopup(connection);
    setAnimationEnabled(true);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void hideSuggestions() {
    suggestionPopup.hide();
  }

  @Override
  public boolean isAnimationEnabled() {
    return suggestionPopup.isAnimationEnabled();
  }

  /**
   * Check whether or not the suggestion list is hidden when there are no
   * suggestions to display.
   *
   * @return true if hidden when empty, false if not
   */
  public boolean isSuggestionListHiddenWhenEmpty() {
    return hideWhenEmpty;
  }

  /**
   * Check whether or not the list of suggestions is being shown.
   *
   * @return true if the suggestions are visible, false if not
   */
  public boolean isSuggestionListShowing() {
    return suggestionPopup.isShowing();
  }

  public void setAnimationEnabled(boolean enable) {
    suggestionPopup.setAnimationEnabled(enable);
  }

  /**
   * Sets the style name of the suggestion popup.
   *
   * @param style the new primary style name
   * @see UIObject#setStyleName(String)
   */
  public void setPopupStyleName(String style) {
    suggestionPopup.setStyleName(style);
  }

  /**
   * Sets the UI object where the suggestion display should appear next to.
   *
   * @param uiObject the uiObject used for positioning, or null to position
   *     relative to the suggest box
   */
  public void setPositionRelativeTo(UIObject uiObject) {
    positionRelativeTo = uiObject;
  }

  /**
   * Set whether or not the suggestion list should be hidden when there are
   * no suggestions to display. Defaults to true.
   *
   * @param hideWhenEmpty true to hide when empty, false not to
   */
  public void setSuggestionListHiddenWhenEmpty(boolean hideWhenEmpty) {
    this.hideWhenEmpty = hideWhenEmpty;
  }

  /**
   * Create the PopupPanel that will hold the list of suggestions.
   *
   * @return the popup panel
   */
  protected VPopup createPopup(ApplicationConnection connection) {
    VPopup 		popup = new VPopup(connection, false, false);
    
    popup.setStyleName("k-suggestBoxPopup");
    popup.setPreviewingAllNativeEvents(true);
    popup.setAnimationType(AnimationType.ROLL_DOWN);
    
    return popup;
  }

  /**
   * Wrap the list of suggestions before adding it to the popup. You can
   * override this method if you want to wrap the suggestion list in a
   * decorator.
   *
   * @param suggestionList the widget that contains the list of suggestions
   * @return the suggestList, optionally inside of a wrapper
   */
  protected Widget decorateSuggestionList(Widget suggestionList) {
    return suggestionList;
  }

  @Override
  public Suggestion getCurrentSelection() {
    if (!isSuggestionListShowing()) {
      return null;
    }
    
    int row = table.getSelectedRow();
    
    return row == -1 ? null : table.getSelectedSuggestion();
  }

  /**
   * Get the {@link PopupPanel} used to display suggestions.
   *
   * @return the popup panel
   */
  protected PopupPanel getPopupPanel() {
    return suggestionPopup;
  }

  /**
   * Get the {@link MenuBar} used to display suggestions.
   *
   * @return the suggestions menu
   */
  protected SuggestionTable getSuggestionTable() {
    return table;
  }

  @Override
  public void moveSelectionDown() {
    // Make sure that the menu is actually showing. These keystrokes
    // are only relevant when choosing a suggestion.
    if (isSuggestionListShowing()) {
      // If nothing is selected, getSelectedItemIndex will return -1 and we
      // will select index 0 (the first item) by default.
      /*suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() + 1);
      suggestionMenu.calculateVisibleRegion();*/
      table.shiftDown(1);
    }
  }

  @Override
  public void moveSelectionUp() {
    // Make sure that the menu is actually showing. These keystrokes
    // are only relevant when choosing a suggestion.
    if (isSuggestionListShowing()) {
      // if nothing is selected, then we should select the last suggestion by
      // default. This is because, in some cases, the suggestions menu will
      // appear above the text box rather than below it (for example, if the
      // text box is at the bottom of the window and the suggestions will not
      // fit below the text box). In this case, users would expect to be able
      // to use the up arrow to navigate to the suggestions.
      table.shiftUp(1);
      /*if (suggestionMenu.getSelectedItemIndex() == -1) {
	suggestionMenu.selectItem(suggestionMenu.getNumItems() - 1);
      } else {
	suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() - 1);
      }
      suggestionMenu.calculateVisibleRegion();*/
    }
  }

  /**
   * <b>Affected Elements:</b>
   * <ul>
   * <li>-popup = The popup that appears with suggestions.</li>
   * <li>-item# = The suggested item at the specified index.</li>
   * </ul>
   *
   * @see UIObject#onEnsureDebugId(String)
   */
  @Override
  public void onEnsureDebugId(String baseID) {
    suggestionPopup.ensureDebugId(baseID + "-popup");
  }

  @Override
  public void showSuggestions(final Widget suggestBox,
                              final List<AutocompleteSuggestion> suggestions,
                              final boolean isDisplayStringHTML,
                              final boolean isAutoSelectEnabled,
                              final SuggestionCallback callback)
  { 
    // This horrible hack is copied from VFilterSelect
    // Apparently, Vaadin layouts mess with paddings and margins, so we need to
    // wait until the layout code has executed before computing the left/top offsets
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
	// Hide the popup if there are no suggestions to display.
	boolean anySuggestions = (suggestions != null && suggestions.size() > 0);
	
	if (!anySuggestions && hideWhenEmpty) {
	  hideSuggestions();
	  return;
	} else if (anySuggestions && suggestions.size() == 1) {
	  callback.onSuggestionSelected(new DefaultSuggestion(suggestions.get(0), 0));
	  return;
	}

	// Hide the popup before we manipulate the menu within it. If we do not
	// do this, some browsers will redraw the popup as items are removed
	// and added to the menu.
	if (suggestionPopup.isAttached()) {
	  suggestionPopup.hide();
	}
	
	table = new SuggestionTable(suggestions);
	table.addClickHandler(new ClickHandler() {

	  @Override
	  public void onClick(ClickEvent event) {
	    int	row = table.getClickedRow(event);

	    if (row != -1 && callback != null) {
	      if (table.getSuggestion(row) != null) {
		suggestionPopup.hide();
	      }
	      callback.onSuggestionSelected(table.getSuggestion(row));
	    }
	  }
	});
	suggestionPopup.setWidget(decorateSuggestionList(table));
	/*for (final Suggestion curSuggestion : suggestions) {
	  final SuggestionMenuItem menuItem = new SuggestionMenuItem(curSuggestion, isDisplayStringHTML);
	  
	  menuItem.setScheduledCommand(new ScheduledCommand() {
	    
	    @Override
	    public void execute() {
	      if (menuItem.getSuggestion() != null) {
		suggestionPopup.hide();
	      }
	      callback.onSuggestionSelected(menuItem.getSuggestion());
	    }
	  });

	  suggestionMenu.addItem(menuItem);
	}*/

	if (isAutoSelectEnabled && anySuggestions) {
	  // Select the first item in the suggestion menu.
	  // suggestionMenu.selectItem(0);
	  table.selectRow(0);
	}

	// Link the popup autoHide to the TextBox.
	if (lastSuggestBox != suggestBox) {
	  // If the suggest box has changed, free the old one first.
	  if (lastSuggestBox != null) {
	    suggestionPopup.removeAutoHidePartner(lastSuggestBox.getElement());
	  }
	  lastSuggestBox = suggestBox;
	  suggestionPopup.addAutoHidePartner(suggestBox.getElement());
	}

	// Show the popup under the TextBox.
	/*if (suggestBox.getElement().getClientWidth() <= 35) {
	  suggestionMenu.setWidth(suggestBox.getElement().getClientWidth() + 10 + "px");
	} else {
	  suggestionMenu.setWidth(suggestBox.getElement().getClientWidth() + "px");
	}*/
	suggestionPopup.showRelativeTo(positionRelativeTo != null ? positionRelativeTo : suggestBox);
      }
    });
  }

  @Override
  public boolean isAnimationEnabledImpl() {
    return isAnimationEnabled();
  }

  @Override
  public boolean isSuggestionListShowingImpl() {
    return isSuggestionListShowing();
  }

  @Override
  public void setAnimationEnabledImpl(boolean enable) {
    setAnimationEnabled(enable);
  }

  @Override
  public void setPopupStyleNameImpl(String style) {
    setPopupStyleName(style);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private final VPopup 			suggestionPopup;
  private SuggestionTable		table;

  /**
   * We need to keep track of the last {@link SuggestBox} because it acts as
   * an autoHide partner for the {@link PopupPanel}. If we use the same
   * display for multiple {@link SuggestBox}, we need to switch the autoHide
   * partner.
   */
  private Widget 			lastSuggestBox = null;

  /**
   * Sub-classes making use of {@link decorateSuggestionList} to add
   * elements to the suggestion popup _may_ want those elements to show even
   * when there are 0 suggestions. An example would be showing a "No
   * matches" message.
   */
  private boolean 			hideWhenEmpty = true;

  /**
   * Object to position the suggestion display next to, instead of the
   * associated suggest box.
   */
  private UIObject 			positionRelativeTo;
}
