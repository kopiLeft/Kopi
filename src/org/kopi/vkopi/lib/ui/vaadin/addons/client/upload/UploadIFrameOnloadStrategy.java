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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.upload;

public class UploadIFrameOnloadStrategy {

  public native void hookEvents(com.google.gwt.dom.client.Element iframe, VUploadForm upload)/*-{
    iframe.onload = $entry(function() {
      upload.@org.kopi.vkopi.lib.ui.vaadin.addons.client.upload.VUploadForm::onSubmitComplete()();
    });
  }-*/;

  /**
   * @param iframe the iframe whose onLoad event is to be cleaned
   */
  public native void unHookEvents(com.google.gwt.dom.client.Element iframe)/*-{
    iframe.onload = null;
  }-*/;
}