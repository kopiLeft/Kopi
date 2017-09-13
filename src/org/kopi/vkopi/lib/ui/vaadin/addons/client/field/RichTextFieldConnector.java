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

import org.kopi.vkopi.lib.ui.vaadin.addons.RichTextField;
import org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditorConnector;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * Connector implementation for the rich text field.
 */
@Connect(value = RichTextField.class, loadStyle = LoadStyle.LAZY)
@SuppressWarnings("serial")
public class RichTextFieldConnector extends CKEditorConnector {
  
  @Override
  public VRichTextField getWidget() {
    return (VRichTextField) super.getWidget();
  }

  /**
   * Navigates to next empty must fill field in container block.
   */
  protected void gotoNextEmptyMustfill() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoNextEmptyMustfill();
  }

  /**
   * Performs a request to go to the next block.
   */
  protected void gotoNextBlock() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoNextBlock();
  }

  /**
   * Performs a request to go to the next record.
   */
  protected void gotoNextRecord() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoNextRecord();
  }

  /**
   * Performs a request to go to the previous record.
   */
  protected void gotoPrevRecord() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoPrevRecord();
  }

  /**
   * Performs a request to go to the first record.
   */
  protected void gotoFirstRecord() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoFirstRecord();
  }

  /**
   * Performs a request to go to the last record.
   */
  protected void gotoLastRecord() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoLastRecord();
  }

  /**
   * Performs a request to go to the next field.
   */
  protected void gotoNextField() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoNextField();
  }

  /**
   * Performs a request to go to the previous field.
   */
  protected void gotoPrevField() {
    getRpcProxy(RichTextFieldNavigationServerRpc.class).gotoPrevField();
  }
}
