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

package com.kopiright.vkopi.comp.form;

import java.util.Vector;
import com.kopiright.vkopi.comp.base.VKPhylum;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;
import com.kopiright.vkopi.comp.base.VKUtils;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class describe the alignment of multi blocks
 */
public class VKBlockAlign extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class describe the alignment of multi blocks
   *
   * @param where		the token reference of this node
   * @param block		block alignment
   * @param target		the target column vector
   * @param source		the source column vector
   */
  public VKBlockAlign(TokenReference where, String block, int[] target, int[] source) {
    super(where);

    this.block = block;
    this.target = target;
    this.source = source;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKBlock block) throws PositionedError {
    verify(source.length == target.length);
    self = block;
    other = ((VKForm)block.getWindow()).getFormElement(this.block);

    check(other != null, FormMessages.BLOCK_NOT_FOUND, this.block);
    check(block.getAlignment() == com.kopiright.vkopi.lib.form.VConstants.ALG_LEFT,
	  FormMessages.BLOCK_ALIGN_BAD, block.getIdent());
    // !!! 28.05.02 lackner; comment out, because VKBock returned always 
    // com.kopiright.vkopi.lib.form.VConstants.ALG_LEFT
    //    check(other.getAlignment() == com.kopiright.vkopi.lib.form.VConstants.ALG_LEFT,
    //	  FormMessages.BLOCK_ALIGN_BAD, other.getIdent());

    // check that columns have increasing numbers and columns exist
    for (int i = 0; i < source.length - 1; i++) {
      if (source[i] >= source[i + 1]) {
        check(false, FormMessages.BAD_COLUMN_ORDER, "" + source[i], "" + source[i + 1]);
      }
    }

    check(source[0] >= 1, FormMessages.BAD_COLUMN_ORDER, "" + source[0]);
    // !!!!check(source[source.length - 1] <= self.getColumnCount(), "k-bad-column-number-2", "" + source[source.length - 1]);

    // check that columns have increasing numbers and columns exist
    for (int i = 0; i < target.length - 1; i++) {
      if (target[i] >= target[i + 1]) {
        check(false, FormMessages.BAD_COLUMN_ORDER, "" + target[i], "" + target[i + 1]);
      }
    }

    check(target[0] >= 1, FormMessages.BAD_COLUMN_ORDER, "" + target[0]);
    //!!!check(target[target.length - 1] <= other.getColumnCount(), "vk-bad-column-number-2", "" + target[target.length - 1]);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void genCode(Vector body) {
    TokenReference	ref = getTokenReference();
    JExpression[]	array = new JExpression[self.getMaxDisplayWidth()];
    int			columnCount = self.getMaxDisplayWidth();
    int			pos = 0;

    for (int i = 0; i < columnCount; i++) {
      if (source[pos] != i + 1) {
	array[i] = VKUtils.toExpression(ref, -1);
      } else {
	array[i] = VKUtils.toExpression(ref, target[pos] - 1);
	pos += 1;
      }
    }

    JExpression expr =  new JUnqualifiedInstanceCreation(ref,
                                                         TYPE,
                                                         new JExpression[] {
                                                           new JNameExpression(ref, other.getIdent()),
                                                           VKUtils.createArray(ref, CStdType.Integer, array)
                                                         });
    expr = new JMethodCallExpression(ref, null, "setAlignments", new JExpression[] {expr});
    body.addElement(new JExpressionStatement(ref, expr, null));
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    /*
    genComments(p);
    ((VKFormPrettyPrinter)p).printBlockAlign(block, source, target);
    */
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBER
  // ----------------------------------------------------------------------

  private static final CReferenceType	TYPE = CReferenceType.lookup(com.kopiright.vkopi.lib.form.BlockAlignment.class.getName().replace('.','/'));

  private String	block;
  private int[]		source;
  private int[]		target;

  private VKBlock	self;
  private VKFormElement	other;
}
