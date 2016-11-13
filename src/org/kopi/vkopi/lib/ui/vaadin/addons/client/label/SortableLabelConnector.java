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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.label;

import org.kopi.vkopi.lib.ui.vaadin.addons.SortableLabel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Icons;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The sortable label connector.
 */
@SuppressWarnings({ "serial", "deprecation" })
@Connect(value = SortableLabel.class, loadStyle = LoadStyle.DEFERRED)
public class SortableLabelConnector extends LabelConnector implements SortableLabelListener, Paintable {

  @Override
  protected void init() {
    super.init();
    getWidget().addSortableLabelListener(this);
    getWidget().setNoneImage(getNoneImage());
    getWidget().setDescImage(getDESCImage());
    getWidget().setAscImage(getASCImage());
  }
  
  @Override
  public VSortableLabel getWidget() {
    return (VSortableLabel) super.getWidget();
  }
  
  @Override
  public void onSort(int mode) {
    if (isEnabled()) {
      getRpcProxy(SortableLabelServerRpc.class).onSort(mode);
    } else {
      // use the variables mechanism to send the sort event
      getConnection().updateVariable(getConnectorId(), "sort", mode, true);
    }
  }
  
  @OnStateChange("sortable")
  /*package*/ void setSortable() {
    getWidget().setSortable(getState().sortable);
  }
  
  @Override
  public void onUnregister() {
    getWidget().removeSortableLabelListener(this);
    getWidget().clear();
  }
  
  /**
   * Returns the none sorting image.
   * @return The none sorting image.
   */
  protected String getNoneImage() {
    return ResourcesUtil.getImageURL(getConnection(), Icons.ARROW);
  }
  
  /**
   * Returns the ASC sorting image.
   * @return The ASC sorting image.
   */
  protected String getASCImage() {
    return ResourcesUtil.getImageURL(getConnection(), Icons.ARROW_UP);
  }
  
  /**
   * Returns the DESC sorting image.
   * @return The DESC sorting image.
   */
  protected String getDESCImage() {
    return ResourcesUtil.getImageURL(getConnection(), Icons.ARROW_DOWN);
  }

  @Override
  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    // not used. Only to send variable updates
  }
}
