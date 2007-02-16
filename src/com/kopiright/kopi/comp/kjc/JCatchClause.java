/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents a parameter declaration in the syntax tree
 */
public class JCatchClause extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	exception	the exception caught
   * @param	body		the body of the exception handler
   */
  public JCatchClause(TokenReference where,
		      JFormalParameter exception,
		      JBlock body)
  {
    super(where);

    this.exception = exception;
    this.body = body;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * getType
   * @return	the type of exception catched by this clause
   */
  public CReferenceType getType() {
    return (CReferenceType)exception.getType();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    CBlockContext	block = new CBlockContext(context, context.getEnvironment(), 1);

    exception.analyse(block);

    // JLS 14.19 
    // A catch clause must have exactly one parameter (which is called 
    // an exception parameter); the declared type of the exception 
    // parameter must be the class Throwable or a subclass of Throwable, 
    // or a compile-time error occurs.
    check(context, 
          exception.getType().isAssignableTo(context, context.getTypeFactory().createReferenceType(TypeFactory.RFT_THROWABLE)),
          KjcMessages.CATCH_PARAMETER_THROWABLE,
          exception.getIdent());

    block.setReachable(true);
    //    exception.analyse(block);
    //!!! Throwable !!!
    body.analyse(block);

    block.close(getTokenReference());

    if (body.isEmpty()) {
      context.reportTrouble(new CWarning(getTokenReference(),
					 KjcMessages.EMPTY_CATCH_BLOCK));
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
    p.visitCatchClause(this, exception, body);
  }

  /**
   * Generates bytecode for the exception handler.
   *
   * @param	code		the code sequence
   * @param	start		the beginning of the checked area (inclusive)
   * @param	end		the end of the checked area (exclusive !)
   */
  public void genCode(GenerationContext context, int start, int end) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    int		catchPC;

    catchPC = code.getPC();
    exception.genStore(context);
    body.genCode(context);

    code.addExceptionHandler(start,
			     end,
			     catchPC,
			     exception.getType().getCClass().getQualifiedName());
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JFormalParameter	exception;
  private JBlock		body;
}
