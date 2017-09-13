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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.RichTextFieldNavigationServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.RichTextFieldState;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.FontMetrics;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;
import org.vaadin.openesignforms.ckeditor.CKEditorConfig;
import org.vaadin.openesignforms.ckeditor.CKEditorTextField;
import org.vaadin.openesignforms.ckeditor.widgetset.client.ui.VCKEditorTextField;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * A rich text field implementation based on CKEditor
 */
@SuppressWarnings("serial")
public class RichTextField extends CKEditorTextField {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the rich text field server component.
   * @param col The column number.
   * @param rows The rows number.
   * @param visibleRows The visible rows number.
   * @param noEdit Is it no edit field ?
   * @param locale The locale to be used for this rich text
   */
  public RichTextField(int col,
                       int rows,
                       int visibleRows,
                       boolean noEdit,
                       Locale locale)
  {
    getState().col = col;
    getState().rows = rows;
    getState().noEdit = noEdit;
    createConfiguration(locale, visibleRows);
    setHeight(TOOLBAR_HEIGHT + LINE_HEIGHT * visibleRows, Unit.PIXELS);
    setReadOnly(noEdit);
    if (FontMetrics.LETTER.getWidth() * col < MIN_WIDTH) {
      setWidth(MIN_WIDTH, Unit.PIXELS);
    } else {
      setWidth(FontMetrics.LETTER.getWidth() * col, Unit.PIXELS);
    }
    navigationListeners = new ArrayList<NavigationListener>();
    registerRpc(new NavigationHandler());
  }
  
  @Override
  protected RichTextFieldState getState() {
    return (RichTextFieldState) super.getState();
  }
  
  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    super.paintContent(target);
    target.addAttribute(VCKEditorTextField.ATTR_IMMEDIATE, isImmediate());
  }
  
  @Override
  protected Boolean getExplicitImmediateValue() {
    return true;
  }
  
  /**
   * Creates the configuration to be used for this rich text.
   * @return the configuration to be used for this rich text.
   */
  protected void createConfiguration(final Locale locale, final int visibleRows) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        CKEditorConfig  configuration;
        
        configuration = new CKEditorConfig();
        configuration.useCompactTags();
        configuration.disableElementsPath();
        configuration.setLanguage(locale.getLanguage());
        configuration.disableSpellChecker();
        configuration.setHeight(LINE_HEIGHT * visibleRows + "px");
        configuration.addExtraConfig("toolbarGroups", createEditorToolbarGroups());
        configuration.addExtraConfig("removeButtons", getRemovedToolbarButtons());
        configuration.addExtraConfig("contentsCss", "'" + Utils.getThemeResourceURL("ckeditor.css") + "'");
        setConfig(configuration);
      }
    });
  }
  
  /**
   * Creates the editor toolbar groups configurations
   * @return The editor toolbar groups configurations
   */
  protected String createEditorToolbarGroups() {
    StringBuffer        toolbarGroups;
      
    toolbarGroups = new StringBuffer();
    toolbarGroups.append("[");
    toolbarGroups.append("{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },");
    toolbarGroups.append("{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },");
    toolbarGroups.append("{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },");
    toolbarGroups.append("'/',");
    toolbarGroups.append("{ name: 'styles', groups: [ 'styles' ] },");
    toolbarGroups.append("{ name: 'links', groups: [ 'links' ] },");
    toolbarGroups.append("{ name: 'colors', groups: [ 'colors' ] },");
    toolbarGroups.append("{ name: 'insert', groups: [ 'insert' ] },");
    toolbarGroups.append("{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },");
    toolbarGroups.append("{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },");
    toolbarGroups.append("{ name: 'forms', groups: [ 'forms' ] },");
    toolbarGroups.append("{ name: 'tools', groups: [ 'tools' ] },");
    toolbarGroups.append("'/',");
    toolbarGroups.append("{ name: 'others', groups: [ 'others' ] },");
    toolbarGroups.append("{ name: 'about', groups: [ 'about' ] }");
    toolbarGroups.append("]");
    
    return toolbarGroups.toString();
  }
  
  protected String getRemovedToolbarButtons() {
    return "'Blockquote,"
      + "CreateDiv,"
      + "BidiLtr,"
      + "BidiRtl,"
      + "Language,"
      + "Source,"
      + "Save,"
      + "Templates,"
      + "NewPage,"
      + "Preview,"
      + "Print,"
      + "Anchor,"
      + "Flash,"
      + "Smiley,"
      + "PageBreak,"
      + "Iframe,"
      + "PasteFromWord,"
      + "PasteText,"
      + "Paste,"
      + "ImageButton,"
      + "Button,"
      + "Select,"
      + "Textarea,"
      + "TextField,"
      + "Radio,"
      + "Checkbox,"
      + "HiddenField,"
      + "Form,"
      + "Styles,"
      + "About,"
      + "Replace,"
      + "SelectAll,"
      + "RemoveFormat,"
      + "CopyFormatting,"
      + "ShowBlocks'";
  }
  
  //---------------------------------------------------
  // NAVGATION
  //---------------------------------------------------
  
  /**
   * Registers a navigation listener on this rich text.
   * @param l The listener to be registered.
   */
  public void addNavigationListener(NavigationListener l) {
    navigationListeners.add(l);
  }
  
  /**
   * RPC navigation handler 
   */
  public class NavigationHandler implements RichTextFieldNavigationServerRpc {

    @Override
    public void gotoNextField() {
      System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx NEXT FIELD");
      for (NavigationListener l : navigationListeners) {
        l.onGotoNextField();
      }
    }

    @Override
    public void gotoPrevField() {
      System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx PREV FIELD");
      for (NavigationListener l : navigationListeners) {
        l.onGotoPrevField();
      }
    }

    @Override
    public void gotoNextBlock() {
      for (NavigationListener l : navigationListeners) {
        l.onGotoNextBlock();
      }
    }

    @Override
    public void gotoPrevRecord() {
      for (NavigationListener l : navigationListeners) {
        l.onGotoPrevRecord();
      }
    }

    @Override
    public void gotoNextRecord() {
      for (NavigationListener l : navigationListeners) {
        l.onGotoNextRecord();
      }
    }

    @Override
    public void gotoFirstRecord() {
      for (NavigationListener l : navigationListeners) {
        l.onGotoFirstRecord();
      }
    }

    @Override
    public void gotoLastRecord() {
      for (NavigationListener l : navigationListeners) {
        l.onGotoLastRecord();
      }      
    }

    @Override
    public void gotoNextEmptyMustfill() {
      for (NavigationListener l : navigationListeners) {
        l.onGotoNextEmptyMustfill();
      }      
    }
  }
  
  /**
   * The grid editor field navigation listener
   */
  public interface NavigationListener extends Serializable {
    
    /**
     * Fired when a goto next field event is called by the user.
     */
    void onGotoNextField();
    
    /**
     * Fired when a goto previous field event is called by the user.
     */
    void onGotoPrevField();
    
    /**
     * Fired when a goto next block event is called by the user.
     */
    void onGotoNextBlock();
    
    /**
     * Fired when a goto previous record event is called by the user.
     */
    void onGotoPrevRecord();
    
    /**
     * Fired when a goto next field event is called by the user.
     */
    void onGotoNextRecord();
    
    /**
     * Fired when a goto first record event is called by the user.
     */
    void onGotoFirstRecord();
    
    /**
     * Fired when a goto last record event is called by the user.
     */
    void onGotoLastRecord();
    
    /**
     * Fired when a goto next empty mandatory field event is called by the user.
     */
    void onGotoNextEmptyMustfill();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Minimal field width to see the toolbar in 56 px height (2 lines)
   */
  private static final int              MIN_WIDTH = 540;
  private static final int              LINE_HEIGHT = 20;
  private static final int              TOOLBAR_HEIGHT = 66;
  
  private List<NavigationListener>      navigationListeners;
}
