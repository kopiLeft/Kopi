/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: CharFormatter.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.compiler.tools.antlr.runtime;

/**
 * Interface used by BitSet to format elements of the set when
  * converting to string
  */
public interface CharFormatter {
  String escapeChar(int c, boolean forCharLiteral);
  String escapeString(String s);
  String literalChar(int c);
  String literalString(String s);
}
