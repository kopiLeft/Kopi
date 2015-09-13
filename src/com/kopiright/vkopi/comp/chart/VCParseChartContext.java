/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.vkopi.comp.chart;

import java.util.Stack;
import java.util.Vector;

import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.vkopi.comp.base.VKEnvironment;
import com.kopiright.vkopi.comp.base.VKParseVKWindowContext;

public class VCParseChartContext extends VKParseVKWindowContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  private VCParseChartContext(VKEnvironment environment) {
    super(environment);
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Static access of this parse context.
   * @param environment The compilation environment.
   * @return The chart parse context instance.
   */
  public static VCParseChartContext getInstance(VKEnvironment environment) {
    return new VCParseChartContext(environment);
  }

  /**
   * Releases this parse context.
   */
  public void release() {
    release(this);
  }

  /**
   * Releases the given parse context.
   * @param context The chart parse context.
   */
  public static void release(VCParseChartContext context) {
    context.clear();
    stack.push(context);
  }

  /**
   * Clears this parse context.
   */
  private void clear() {
    interfaces.clear();
    dimensions.clear();
    measures.clear();
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  /**
   * Adds an interface to this parse context.
   * @param inter Te interface to be added.
   */
  public void addInterface(CReferenceType inter) {
    interfaces.addElement(inter);
  }

  /**
   * Adds a field to this parse context.
   * @param field The field to be added.
   */
  public void addField(VCField field) {
    if (field instanceof VCDimension) {
      dimensions.addElement((VCDimension) field);
    } else if (field instanceof VCMeasure) {
      measures.addElement((VCMeasure) field);
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  /**
   * Returns the interfaces array.
   * @return The interfaces array.
   */
  public CReferenceType[] getInterfaces() {
    return interfaces.toArray(new CReferenceType[interfaces.size()]);
  }

  /**
   * Returns the measures array.
   * @return The measures array.
   */
  public VCMeasure[] getMeasures() {
    return measures.toArray(new VCMeasure[measures.size()]);
  }

  /**
   * Returns the dimensions array.
   * @return The dimensions array.
   */
  public VCDimension[] getDimensions() {
    return dimensions.toArray(new VCDimension[dimensions.size()]);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector<CReferenceType>		interfaces = new Vector<CReferenceType>();
  private Vector<VCDimension>			dimensions = new Vector<VCDimension>();
  private Vector<VCMeasure>			measures = new Vector<VCMeasure>();
  private static Stack<VCParseChartContext> 	stack = new Stack<VCParseChartContext>();
}
