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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VScrollablePanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditor;
import org.vaadin.openesignforms.ckeditor.widgetset.client.ui.VCKEditorTextField;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasEnabled;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;

/**
 * Widget implementation for the rich text field. 
 */
public class VRichTextField extends VCKEditorTextField implements HasEnabled {
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void onInstanceReady() {
    super.onInstanceReady();
    ((CKEditor) getEditor()).setReadOnly(!enabled);
    // instance Ready we get the JS editor
    handleKeyDownEvent((CKEditor) getEditor(), this);
  }
  
  @Override
  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    // never add resize listener for the editor in the layout manager
    setResizeListenerInPlace(true);
    getElement().setId(uidl.getId());
    if (Document.get().getElementById(uidl.getId()) == null) {
      // when the rich text element is not attached to the DOM
      // we prevent CK editor loading to avoid attach errors.
      setEditorBeingLoaded(true);
    }
    super.updateFromUIDL(uidl, client);
  }
  
  @Override
  protected void onLoad() {
    // load editor when element is attached to the DOM
    setEditorBeingLoaded(false);
    super.onLoad();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
        final VScrollablePanel        scrollPanel;
        
        scrollPanel = WidgetUtils.getParent(VRichTextField.this, VScrollablePanel.class);
        if (scrollPanel != null) {
          new Timer() {
            
            @Override
            public void run() {
              scrollPanel.scrollToTop();
            }
          }.schedule(2000);
        }
      }
    });
  }
  
  @Override
  public void onFocus() {
    super.onFocus();
    addStyleDependentName("focus");
    clientToServer.getMessageSender().sendInvocationsToServer();
  }
  
  @Override
  public void onBlur() {
    super.onBlur();
    removeStyleDependentName("focus");
  }
  
  /**
   * Returns the attached connector to this widget.
   * @return The attached connector to this widget.
   */
  protected RichTextFieldConnector getConnector() {
    return ConnectorUtils.getConnector(clientToServer, this, RichTextFieldConnector.class);
  }
  
  /**
   * Navigates to next empty must fill field in container block.
   */
  public void gotoNextEmptyMustfill() {
    onChange();
    getConnector().gotoNextEmptyMustfill();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }

  /**
   * Performs a request to go to the next block.
   */
  protected void gotoNextBlock() {
    onChange();
    getConnector().gotoNextBlock();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }

  /**
   * Performs a request to go to the next record.
   */
  protected void gotoNextRecord() {
    onChange();
    getConnector().gotoNextRecord();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }

  /**
   * Performs a request to go to the previous record.
   */
  protected void gotoPrevRecord() {
    onChange();
    getConnector().gotoPrevRecord();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }

  /**
   * Performs a request to go to the first record.
   */
  protected void gotoFirstRecord() {
    onChange();
    getConnector().gotoFirstRecord();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }

  /**
   * Performs a request to go to the last record.
   */
  protected void gotoLastRecord() {
    onChange();
    getConnector().gotoLastRecord();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }

  /**
   * Performs a request to go to the next field.
   */
  protected void gotoNextField() {
    onChange();
    getConnector().gotoNextField();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }

  /**
   * Performs a request to go to the previous field.
   */
  protected void gotoPrevField() {
    onChange();
    getConnector().gotoPrevField();
    clientToServer.getMessageSender().sendInvocationsToServer();
  }
  
  /**
   * Quits the current window.
   */
  protected void closeWindow() {
    // NOT IMPLEMENTED
  }
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  /**
   * Sets the field value.
   * @param value The field value.
   */
  public void setValue(Object value) {
    if (getEditor() != null) {
      ((CKEditor) getEditor()).setData((String)value);
    }
  }
  
  /**
   * Returns the field value.
   * @return The field value.
   */
  public Object getValue() {
    return getEditor() == null ? null : ((CKEditor) getEditor()).getData();
  }
  
  /**
   * Checks if the content of this field is empty.
   * @return {@code true} if this field is empty.
   */
  public boolean isNull() {
    return getValue() == null;
  }
  
  /**
   * Checks the internal value of this field.
   * @param rec The active record.
   */
  protected void checkValue(int rec) {}
  
  /**
   * Handles the key down event on the editor editable area
   * @param editor The JS editor instance
   */
  private static native void handleKeyDownEvent(CKEditor editor, VRichTextField field) /*-{
    editor.on('contentDom', function(evt) {
      editor.document.on('keydown', function(event) {
        var     KEY_ENTER = 13;
        var     EY_PAGEDOWN = 34;
        var     KEY_PAGEUP = 33;
        var     KEY_HOME = 36;
        var     KEY_END = 35;
        var     KEY_LEFT = 37;
        var     KEY_TAB = 9;
        var     KEY_RIGHT = 39;
        var     KEY_UP = 38;
        var     KEY_DOWN = 40;
        var     KEY_ESCAPE = 27;
        
        switch(event.data.$.keyCode) {
          case KEY_ENTER:
            if (event.data.$.ctrlKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoNextEmptyMustfill()();
              event.data.$.preventDefault();
            } else if (event.data.$.shiftKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoNextBlock()();
              event.data.$.preventDefault();
            }
            break;
          case EY_PAGEDOWN:
            if ((!event.data.$.ctrlKey && !event.data.$.shiftKey && !event.data.$.metaKey && !event.data.$.alttKey) || event.data.$.shiftKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoNextRecord()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_PAGEUP:
            if ((!event.data.$.ctrlKey && !event.data.$.shiftKey && !event.data.$.metaKey && !event.data.$.alttKey) || event.data.$.shiftKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoPrevRecord()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_HOME:
            if (event.data.$.shiftKey) {
               field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoFirstRecord()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_END:
            if (event.data.$.shiftKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoLastRecord()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_LEFT:
            if (event.data.$.ctrlKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoPrevField()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_TAB:
            if (event.data.$.shiftKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoPrevField()();
              event.data.$.preventDefault(); 
            } else if (!event.data.$.ctrlKey && !event.data.$.shiftKey && !event.data.$.metaKey && !event.data.$.alttKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoNextField()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_UP:
            if (event.data.$.shiftKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoPrevField()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_RIGHT:
            if (event.data.$.ctrlKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoNextField()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_DOWN:
            if (event.data.$.shiftKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::gotoNextField()();
              event.data.$.preventDefault(); 
            }
            break;
          case KEY_ESCAPE:
            if (!event.data.$.ctrlKey && !event.data.$.shiftKey && !event.data.$.metaKey && !event.data.$.alttKey) {
              field.@org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VRichTextField::closeWindow()();
              event.data.$.preventDefault();
            }
            break;
          default:
            // nothing to do
        }
      });
    }, editor.element.$);
  }-*/;
  
  /**
   * Returns the CK editor handled by this text field.
   * @return The CK editor handled by this text field.
   */
  private native JavaScriptObject getEditor() /*-{
    return this.@org.vaadin.openesignforms.ckeditor.widgetset.client.ui.VCKEditorTextField::ckEditor;
  }-*/;
  
  /**
   * Sets the editor to be in loading state.
   * @param editorIsBeingLoaded Is the CK editor in loding state ?
   */
  private native void setEditorBeingLoaded(boolean editorIsBeingLoaded) /*-{
    this.@org.vaadin.openesignforms.ckeditor.widgetset.client.ui.VCKEditorTextField::ckEditorIsBeingLoaded = editorIsBeingLoaded;
  }-*/;
  
  /**
   * Sets the resize listener to be in place for the layout manager.
   * @param resizeListenerInPlace Is the resize listener in place ?
   */
  private native void setResizeListenerInPlace(boolean resizeListenerInPlace) /*-{
    this.@org.vaadin.openesignforms.ckeditor.widgetset.client.ui.VCKEditorTextField::resizeListenerInPlace = resizeListenerInPlace;
  }-*/;
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private CKEditor                              editor;
  private boolean                               enabled;
}
