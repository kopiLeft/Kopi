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

package org.kopi.vkopi.lib.form;

import java.sql.SQLException;

import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.base.Query;

/**
 * An actor field is a special field that does not handle
 * any value. It consists in a simple action that have
 * a label and an optional help. If an ACTION trigger is defined, it can
 * be fired by a click an the field UI representation.
 */
@SuppressWarnings("serial")
public class VActorField extends VField {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /**
   * Creates a new actor field instance.
   */
  public VActorField(int width, int height) {
    super(1, 1);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  /**
   * @Override
   */
  public boolean checkText(String s) {
    return true;
  }

  /**
   * @Override
   */
  public void checkType(int rec, Object s) throws VException {}

  /**
   * @Override
   */
  public Class getDataType() {
    return Void.class;
  }

  /**
   * @Override
   */
  protected VListColumn getListColumn() {
    return null;
  }

  /**
   * @Override
   */
  public void setNull(int r) {}

  /**
   * @Override
   */
  public void setObject(int r, Object v) {}

  /**
   * @Override
   */
  public Object retrieveQuery(Query query, int column) throws SQLException {
    return null;
  }

  /**
   * @Override
   */
  public boolean isNullImpl(int r) {
    return false;
  }

  /**
   * @Override
   */
  public Object getObjectImpl(int r) {
    return null;
  }

  /**
   * @Override
   */
  public String toText(Object o) {
    return null;
  }

  /**
   * @Override
   */
  public Object toObject(String s) throws VException {
    return null;
  }

  /**
   * @Override
   */
  public String getTextImpl(int r) {
    return null;
  }

  /**
   * @Override
   */
  public String getSqlImpl(int r) {
    return null;
  }

  /**
   * @Override
   */
  public void copyRecord(int f, int t) {}

  /**
   * @Override
   */
  public String getTypeInformation() {
    return VlibProperties.getString("actor-type-field");
  }

  /**
   * @Override
   */
  public String getTypeName() {
    return VlibProperties.getString("Actor");
  }
  
  /**
   * @Override
   */
  public int getType() {
    return MDL_FLD_ACTOR;
  }
}
