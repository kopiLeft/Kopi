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

package org.kopi.vkopi.lib.report;

import org.kopi.xkopi.lib.type.NotNullFixed;

/**
 * This class implements predefined report triggers
 */

public class Triggers {

  /**
   * Compute the integer sum in a report column
   */
  public static VCalculateColumn sumInteger(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int     childCount = row.getChildCount();
        int     result = 0;

        for (int i = 0; i < childCount; i++) {
          VReportRow    child = (VReportRow)row.getChildAt(i);
          Integer       value = (Integer)child.getValueAt(column);

          if  (value != null) {
            result += value.intValue();
          }
        }
        return new Integer(result);
      }
    };
  }

  /**
   * Compute the number of entries in a report column
   */
  public static VCalculateColumn countInteger(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int     childCount = row.getChildCount();

        if (row.getLevel() > 1) {
          // the value of a node is the sum of
          // other nodes (contain no leafs)
          int   result = 0;

          for (int i = 0; i < childCount; i++) {
            VReportRow  child = (VReportRow)row.getChildAt(i);
            Integer     value = (Integer)child.getValueAt(column);

            if  (value != null) {
              result += value.intValue();
            }
          }
          return new Integer(result);
        } else {
          // the value is the number of the childs
          // if the childs are leafs
          return new Integer(childCount);
        }
      }
    };
  }


  /**
   * Compute the fixed sum in a report column
   */
  public static VCalculateColumn sumFixed(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int             childCount = row.getChildCount();
        NotNullFixed    result = new NotNullFixed(0, 2);

        for (int i = 0; i < childCount; i++) {
          VReportRow    child = (VReportRow)row.getChildAt(i);
          NotNullFixed value = (NotNullFixed)child.getValueAt(column);

          if  (value != null) {
            result = result.add(value);
          }
        }
        return result;
      }
    };
  }


  /**
   * Compute the integer sum in a report column
   */
  public static VCalculateColumn sumNullInteger(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int     childCount = row.getChildCount();
        int     result = 0;
        boolean valueFound = false;

        for (int i = 0; i < childCount; i++) {
          VReportRow    child = (VReportRow)row.getChildAt(i);
          Integer       value = (Integer)child.getValueAt(column);

          if (value != null) {
            valueFound = true;
            result += value.intValue();
          }
        }
        return valueFound ? new Integer(result) : null;
      }
    };
  }


  /**
   * Compute the fixed sum in a report column
   */
  public static VCalculateColumn sumNullFixed(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int childCount = row.getChildCount();
        boolean         valueFound = false;
        NotNullFixed    result = new NotNullFixed(0, 2);

        for (int i = 0; i < childCount; i++) {
          VReportRow    child = (VReportRow)row.getChildAt(i);
          NotNullFixed value = (NotNullFixed)child.getValueAt(column);

          if  (value != null) {
            valueFound = true;
            result = result.add(value);
          }
        }
        return valueFound ? result : null;
      }
    };
  }


  /**
   * Report a value when all child are identical
   */
  public static VCalculateColumn reportIdenticalValue(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int     childCount = row.getChildCount();
        Object  value = ((VReportRow)row.getChildAt(0)).getValueAt(column);

        if (value == null) {
          return null;
        } else {
          for (int i = 1; i < childCount; i++) {
            VReportRow  child = (VReportRow)row.getChildAt(i);

            if (!value.equals(child.getValueAt(column))) {
              return null;
            }
          }
          return value;
        }
      }
    };
  }


  /**
   * Compute the integer average in a report column
   */
  public static VCalculateColumn avgInteger(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int     childCount = row.getChildCount();
        int     result = 0;

        for (int i = 0; i < childCount; i++) {
          VReportRow    child = (VReportRow)row.getChildAt(i);
          Integer value = (Integer)child.getValueAt(column);

          if  (value != null) {
            result += value.intValue();
          }
        }
        return new Integer(result / childCount);
      }
    };
  }


  /**
   * Compute the integer sum in a report column and the the value
   * in the leaves with a serial number
   */
  public static VCalculateColumn serialInteger(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {

      public Object evalNode(VReportRow row, int column) {
        int     childCount = row.getChildCount();

        if (row.getLevel() > 1) {
          // the value of a node is the sum of
          // other nodes (contain no leafs)
          int   result = 0;

          for (int i = 0; i < childCount; i++) {
            VReportRow  child = (VReportRow)row.getChildAt(i);
            Integer     value = (Integer)child.getValueAt(column);

            if  (value != null) {
              result += value.intValue();
            }
          }
          return new Integer(result);
        } else {
          // the value is the number of the childs
          // if the childs are leafs
          return new Integer(childCount);
        }
      }

      /**
       * Add calculated data into the report row
       */
      public void calculate(VGroupRow tree, int column) {
        if (tree.getLevel() > 1) {
          int childCount = tree.getChildCount();

          for (int i = 0; i < childCount; i++) {
            calculate((VGroupRow)tree.getChildAt(i), column);
          }
        } else {
          int childCount = tree.getChildCount();

          for (int i = 0; i < childCount; i++) {
            // set leave to serial number
            ((VBaseRow)tree.getChildAt(i)).setValueAt(column, new Integer(i+1));
          }
        }
        tree.setValueAt(column, evalNode(tree, column));
      }
    };
  }


  /**
   * Compute the fixed average in a report column
   */
  public static VCalculateColumn avgFixed(VReportColumn c) {
    return new VCCDepthFirstCircuitN() {
      public Object evalNode(VReportRow row, int column) {
        int             leafCount = row.getLeafCount();
        int             notNullLeafCount = 0;
        NotNullFixed    result = new NotNullFixed(0, 2);
        VReportRow      leaf = (VReportRow)row.getFirstLeaf();
        
        for (int i = 0; i < leafCount; i++) {
          NotNullFixed value = (NotNullFixed)leaf.getValueAt(column);
          if (value != null) {
            result = result.add(value);
            notNullLeafCount ++;
          }
          leaf = (VReportRow)leaf.getNextLeaf();
        }
        if (notNullLeafCount != 0) {
          return result.divide(new NotNullFixed(notNullLeafCount)).setScale(2);
        } else {
          return new NotNullFixed(0, 2);
        }
      }
    };
  }
}
