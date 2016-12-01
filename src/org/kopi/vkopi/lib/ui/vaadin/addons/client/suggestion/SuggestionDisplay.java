/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Used to display suggestions to the user.
 */
public abstract class SuggestionDisplay {

  /**
   * Get the currently selected {@link Suggestion} in the display.
   *
   * @return the current suggestion, or null if none selected
   */
  public abstract Suggestion getCurrentSelection();

  /**
   * Hide the list of suggestions from view.
   */
  public abstract void hideSuggestions();

  /**
   * Highlight the suggestion directly below the current selection in the
   * list.
   */
  public abstract void moveSelectionDown();

  /**
   * Highlight the suggestion directly above the current selection in the
   * list.
   */
  public abstract void moveSelectionUp();

  /**
   * Set the debug id of widgets used in the SuggestionDisplay.
   *
   * @param suggestBoxBaseID the baseID of the {@link SuggestBox}
   * @see UIObject#onEnsureDebugId(String)
   */
  public void onEnsureDebugId(String suggestBoxBaseID) {}

  /**
   * Accepts information about whether there were more suggestions matching
   * than were provided to {@link #showSuggestions}.
   *
   * @param hasMoreSuggestions true if more matches were available
   * @param numMoreSuggestions number of more matches available. If the
   *     specific number is unknown, 0 will be passed.
   */
  public void setMoreSuggestions(boolean hasMoreSuggestions, int numMoreSuggestions) {
    // Subclasses may optionally implement.
  }

  /**
   * Update the list of visible suggestions.
   *
   * Use care when using isDisplayStringHtml; it is an easy way to expose
   * script-based security problems.
   *
   * @param suggestBox the suggest box where the suggestions originated
   * @param suggestions the suggestions to show
   * @param isDisplayStringHTML should the suggestions be displayed as HTML
   * @param isAutoSelectEnabled if true, the first item should be selected automatically
   * @param callback the callback used when the user makes a suggestion
   */
  public abstract void showSuggestions(Widget suggestBox,
                                       List<AutocompleteSuggestion> suggestions,
                                       boolean isDisplayStringHTML,
                                       boolean isAutoSelectEnabled,
                                       SuggestionCallback callback);
  
  
  /**
   * Returns {@code true} if we are waiting for suggestions.
   * @return {@code true} if we are waiting for suggestions.
   */
  public abstract boolean isAboutShowingSuggestions();

  /**
   * This is here for legacy reasons. It is intentionally not visible.
   *
   * @deprecated implemented in DefaultSuggestionDisplay
   */
  @Deprecated
  public boolean isAnimationEnabledImpl() {
    // Implemented in DefaultSuggestionDisplay.
    return false;
  }

  /**
   * This is here for legacy reasons. It is intentionally not visible.
   */
  public boolean isSuggestionListShowingImpl() {
    // Implemented in DefaultSuggestionDisplay.
    return false;
  }

  /**
   * This is here for legacy reasons. It is intentionally not visible.
   *
   * @param enable true to enable animation
   *
   * @deprecated implemented in DefaultSuggestionDisplay
   */
  @Deprecated
  public void setAnimationEnabledImpl(boolean enable) {
    // Implemented in DefaultSuggestionDisplay.
  }

  /**
   * This is here for legacy reasons. It is intentionally not visible.
   *
   * @param style the style name
   *
   * @deprecated implemented in DefaultSuggestionDisplay
   */
  @Deprecated
  public void setPopupStyleNameImpl(String style) {
    // Implemented in DefaultSuggestionDisplay.
  }
}
