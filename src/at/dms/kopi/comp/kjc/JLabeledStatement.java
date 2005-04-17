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

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.JavaStyleComment;

/**
 * JLS 14.7: Labeled Statement
 *
 * Statements may have label prefixes.
 */
public class JLabeledStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	label		the label of the enclosing labeled statement
   * @param	body		the contained statement
   * @param	comments	comments in the source text
   */
  public JLabeledStatement(TokenReference where,
			   String label,
			   JStatement body,
			   JavaStyleComment[] comments)
  {
    super(where, comments);

    this.label = label;
    this.body = body;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the label of this statement.
   */
  /*package*/ String getLabel() {
    return label;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    check(context,
	  context.getLabeledStatement(label) == null,
	  KjcMessages.LABEL_ALREADY_EXISTS, label);

    CLabeledContext	labeledContext;

    labeledContext = new CLabeledContext(context, context.getEnvironment(), this);
    body.analyse(labeledContext);
    labeledContext.close(getTokenReference());
  }

  // ----------------------------------------------------------------------
  // BREAK/CONTINUE HANDLING
  // ----------------------------------------------------------------------

  /**
   * Returns the actual target statement of a break or continue whose
   * label is the label of this statement.
   *
   * If the statement referencing this labeled statement is either a break
   * or a continue statement :
   * - if it is a continue statement, the target is the contained statement
   *   which must be a loop statement.
   * - if it is a break statement, the target is the labeled statement
   *   itself ; however, if the contained statement is a loop statement,
   *   the target address for a break of the contained statement is the same
   *   as the target address of this labeled statement.
   * Thus, if the contained statement is a loop statement, the target
   * for a break or continue to this labeled statement is the same as the
   * target address of this labeled statement.
   */
  public JStatement getTargetStatement() {
    if (body instanceof JLoopStatement) {
      // JLS 14.15: do, while or for statement
      return body;
    } else {
      return this;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitLabeledStatement(this, label, body);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    code.plantLabel(beginLabel);
    body.genCode(context);
    code.plantLabel(endLabel);
    //endLabel = null;
    //beginLabel = null;
  }

  /**
   * Returns the end of this block (for break statement).
   */
  public CodeLabel getBreakLabel() {
    return endLabel;
  }

  /**
   * Returns the begining of this block.
   */
  public CodeLabel getBeginLabel() {
    return beginLabel;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		label;
  private JStatement		body;
  private CodeLabel		endLabel = new CodeLabel();
  private CodeLabel		beginLabel = new CodeLabel();
}
