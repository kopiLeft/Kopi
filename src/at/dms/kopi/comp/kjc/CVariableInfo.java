/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.kopi.comp.kjc;

import at.dms.util.base.InconsistencyException;

/**
 * This class represents definite assignment state information for
 * local variables and fields during semantic analysis.
 *
 * The definite assignment state is coded using 2 bits :
 * - bit 0 : is assigned ?
 * - bit 1 : is not unassigned ?
 *
 * There are 4 possible states :
 * - 0 :        is not assigned and is unassigned
 *              -> is definitely unassigned
 * - 1 :        is assigned and is unassigned
 *              -> unreachable
 * - 2 :        is not assigned and is not unassigned
 *              -> may be assigned
 * - 3 :        is assigned and is not unassigned
 *              -> is definitely assigned
 *
 * Implementation notes :
 * - Initially, the state is 0 (definitely unassigned).
 * - The state cannot change from non-zero to zero.
 *
 */

public final class CVariableInfo {

  /**
   * Creates a copy of a definite assignment set.
   */
  private CVariableInfo(CVariableInfo parent, int[] infos) {
    this.parent = parent;
    this.infos = infos;
  }

  /**
   * Creates a new definite assignment set.
   */
  public CVariableInfo(CVariableInfo parent) {
    this(parent, null);
  }

  /**
   * Clones the definite assignment set.
   */
  public Object clone() {
    return new CVariableInfo(this);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the definite assignment state of the specified variable or field.
   *
   * @param     pos             the index of the variable of field
   * @return    the definite assignment state (in the range 0 - 3)
   */
  public int getInfo(int pos) {
    int		sub = subscript(pos);
    int		set = subset(pos);

    if (infos != null && sub < infos.length) {
      return (infos[sub] >> (2 * set)) & 3;
    } else if (parent != null) {
      return parent.getInfo(pos);
    } else {
      return 0;
    }
  }

  /**
   * Create its own information of variables
   */
  public void createInfo() {
      if (infos == null) {
        CVariableInfo   source;

        source = parent;
        while (source != null && source.infos == null) {
          source = source.parent;
        }
        if (source == null) {
          infos = EMPTY_INFO;
        } else {
          infos = (int[])source.infos.clone();
        }
      }
  }

  /**
   * Sets the definite assignment state of the specified variable or field.
   *
   * @param     pos             the index of the variable of field
   * @param     info            the definite assignment state (in the range 0 - 3)
   */
  public void setInfo(int pos, int info) {
    if (info == 0) {
      if (getInfo(pos) != 0) {
        throw new InconsistencyException("info(" + pos + ") is " + getInfo(pos) + " (0 expected)");
      }
    } else {
      createInfo();

      int	sub = subscript(pos);
      int	set = subset(pos);

      if (sub >= infos.length) {
        int[]	temp = new int[sub + 1];

        System.arraycopy(infos, 0, temp, 0, infos.length);
        infos = temp;
      }

      infos[sub] = ((info & 3) << (2 * set)) | (infos[sub] & ~(3 << (2 * set)));
    }
  }

  /**
   * Merges the specified definite assignment state into the definite assignment
   * state of the specified variable or field, i.e. :
   * - sets the variable definitely assigned if both are definitely assigned.
   * - sets the variable definitely unassigned if both are definitely unassigned.
   *
   * @param     pos             the index of the variable of field
   * @param     info            the definite assignment state of the other variable or field
   */
  public void mergeInto(int pos, int other) {
    int         self;

    setInfo(pos, merge(getInfo(pos), other));
  }

  /**
   * Completes the specified definite assignment state from the definite assignment
   * state of the specified variable or field, i.e. :
   * - sets the variable definitely assigned if at least one is definitely assigned.
   * - sets the variable definitely unassigned if both are definitely unassigned.
   *
   * @param     pos             the index of the variable of field
   * @param     info            the definite assignment state of the other variable or field
   */
  public void completeInto(int pos, int other) {
    int         self;

    setInfo(pos, complete(getInfo(pos), other));
  }

  // ----------------------------------------------------------------------
  // PUBLIC UTILITIES
  // ----------------------------------------------------------------------

  /**
   * merge
   * @param	other		the second JLocalVariable info
   * @return	the merging information onto this flags
   */
  public static final int merge(int info1, int info2) {
    return ((info1 | info2) & INF_MAYBE_INITIALIZED) | ((info1 & info2) & INF_INITIALIZED);
  }
  /**
   * merge
   * @param	other		the second JLocalVariable info
   * @return	the merging information onto this flags
   */
  public static final int complete(int info1, int info2) {
    return ((info1 | info2) & INF_MAYBE_INITIALIZED) | ((info1 | info2) & INF_INITIALIZED);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * initialize
   */
  public static final int initialize() {
    return INF_INITIALIZED | INF_MAYBE_INITIALIZED;
  }

  /**
   * isInitialized
   */
  public static final boolean isInitialized(int info) {
    return (info & INF_INITIALIZED) != 0;
  }

  /**
   * mayBeInitialized
   */
  public static final boolean mayBeInitialized(int info) {
    return (info & INF_MAYBE_INITIALIZED) != 0;
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES
  // ----------------------------------------------------------------------

  private static int subscript(int pos) {
    return pos >> 4;
  }

  private static int subset(int pos) {
    return pos % 16;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int	INF_INITIALIZED		= 0x01;
  private static final int	INF_MAYBE_INITIALIZED	= 0x02;

  public static final int	INITIALIZED = INF_INITIALIZED | INF_MAYBE_INITIALIZED;

  private static final int[]    EMPTY_INFO = new int[0];

  private final CVariableInfo   parent;
  private int[]			infos;
}
