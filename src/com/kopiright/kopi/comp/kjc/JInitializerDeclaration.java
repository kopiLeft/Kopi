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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents a java class in the syntax tree
 */
public class JInitializerDeclaration extends JMethodDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	parent		parent in which this methodclass is built
   */
  public JInitializerDeclaration(TokenReference where,
				 JBlock body,
				 boolean isStatic,
				 boolean isDummy,
                                 TypeFactory factory)
  {
    super(where,
	  ACC_PRIVATE | (isStatic ? ACC_STATIC : 0),
          CTypeVariable.EMPTY,
	  factory.getVoidType(),
	  (isStatic ? JAV_STATIC_INIT : JAV_INIT),
	  JFormalParameter.EMPTY,
	  CReferenceType.EMPTY,
	  body,
	  null,
	  null);

    this.isDummy = isDummy;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return true if this initialiser declaration is used only to check code
   * and that it should not generate code
   */
  public boolean isDummy() {
    return isDummy;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @return true iff sub tree is correct enough to check code
   * @exception	PositionedError	an error with reference to the source file
   */
  public CSourceMethod checkInterface(CClassContext context) throws PositionedError {
    CSourceMethod       method = super.checkInterface(context);

    if (! method.isStatic()) {
      // method Block$() is a synthetic method
      method.setSynthetic(true);
    }
    return method;
  }
  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void checkBody1(CClassContext context) {
    // I've made it !
    // nothing to check.
  }

  /**
   * Prepare the inisialisation step, sets contexts of fields
   * Here we set the fields contexts, create the insializer blocks 
   * and synthetic assert stuff
   * 
   * @param	context		the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   * @see	#checkInitializer(CClassContext context)
   */
  public void prepareInitializer(CClassContext context) throws PositionedError {
    if (getMethod().isStatic()) {
      TokenReference  ref = TokenReference.NO_REF;
      JBlock          classBlock = null;
      CClass          clazz = context.getCClass();
      
      if (!clazz.isInterface()) {
        if (context.getEnvironment().getSourceVersion() >= KjcEnvironment.SOURCE_1_4) {
          CClass          topLevelClass;
          String          clazzName;
          CClassContext   evalContext = context;      
          JExpression     methodCall, tmpExpr1, tmpExpr2, tmpExpr3;
          JStatement      tmpStat1, tmpStat2;
          
          while (clazz.getOwner() != null) {
            clazz = clazz.getOwner();
            evalContext = evalContext.getParentContext().getClassContext();
          }
          
          topLevelClass = clazz;
          
          if (clazz.isInterface()) {
            clazz = clazz.getAssertionStatusClass(evalContext);
          }
          
          clazzName = clazz.getIdent();
          if (clazzName.lastIndexOf('/') > 0) {
            clazzName = clazzName.substring(clazzName.lastIndexOf('/')+1);
          }
          clazzName = JAV_IDENT_CLASS + clazzName;
          
          // method call: class$("<class_name>") 
          methodCall = new JMethodCallExpression(ref,
                                                new JTypeNameExpression(ref, clazz.getAbstractType()),
                                                JAV_IDENT_CLASS,
                                                new JExpression[] {
                                                  new JStringLiteral(ref, topLevelClass.getQualifiedName().replace('/','.')) 
                                                });
          
          // field access: class$<class_name>
          tmpExpr1 = new JFieldAccessExpression(ref,
                                               new JTypeNameExpression(ref, clazz.getAbstractType()),
                                               clazzName);

          // equality expr: class$<class_name> == null
          tmpExpr2 = new JEqualityExpression(ref, true, tmpExpr1, new JNullLiteral(ref));
          
          // assignment: class$<class_name> = class$("<class_name>");
          tmpExpr3 = new JAssignmentExpression(ref, tmpExpr1, methodCall);
          tmpStat2 = new JExpressionStatement(ref, tmpExpr3, null);
          
          // if statement: if (class$<class_name> == null) class$test = class$("<class_name>");
          tmpStat1 = new JIfStatement(ref, tmpExpr2, tmpStat2, null, null);

          // method call: class$<class_name>.desiredAssertionStatus()
          methodCall = new JMethodCallExpression(ref, tmpExpr1, "desiredAssertionStatus", JExpression.EMPTY);
          
          // assignment: $assertionsDisabled = !class$<class_name>.desiredAssertionStatus();
          tmpExpr1 = new JAssignmentExpression(ref,
                                               new JFieldAccessExpression(ref, IDENT_ASSERT),
                                               new JLogicalComplementExpression(ref, methodCall)); 
          
          tmpStat2 = new JExpressionStatement(ref, tmpExpr1, null);

          // static block initialyzer: static { ... }
          classBlock =   new JBlock(ref, new JStatement[] { tmpStat1, tmpStat2 }, null);
          
        } else {       
          if (context.getEnvironment().getAssertExtension() == KjcEnvironment.AS_ALL 
              || context.getEnvironment().getAssertExtension() == KjcEnvironment.AS_SIMPLE) {
            JStatement  tmpStat;
            JExpression tmpExpr;
            
            // assignment: $assertionsDisabled = false;
            tmpExpr = new JAssignmentExpression(ref,
                                                new JFieldAccessExpression(ref, IDENT_ASSERT),
                                                new JBooleanLiteral(ref, false));
            tmpStat = new JExpressionStatement(ref, tmpExpr, null);
            
            // static block initialyzer: static { ... }
            classBlock = new JBlock(ref, new JStatement[] { tmpStat }, null);
          }
        }
        if (classBlock != null) {
          body = new JBlock(getTokenReference(), 
                            new JStatement[] { classBlock, body },
                            null);
          ((CSourceMethod) getMethod()).setBody(body);
        }
      }
    }
    
    if (!getMethod().isStatic() && !isDummy()) {
      context.addInitializer();
    }
    
    // we want to do that at check intitializers time
    self = new CInitializerContext(context, context.getEnvironment(), getMethod(), parameters);
    block = new CBlockContext(self, context.getEnvironment(), parameters.length);
    
    if (!getMethod().isStatic()) {
      // add this local var
      block.addThisVariable();
    } 

    for (int i=0; i<body.getBody().length; i++) { 
      if (body.getBody()[i] instanceof JBlock) {
        JBlock innerBody = (JBlock) body.getBody()[i];
        for (int j=0; j<innerBody.getBody().length; j++) {
          if (innerBody.getBody()[j] instanceof JClassFieldDeclarator) {
            ((JClassFieldDeclarator) innerBody.getBody()[j]).analyse(block);
          }
        }
      } else {
        if (body.getBody()[i] instanceof JClassFieldDeclarator) {
        ((JClassFieldDeclarator) body.getBody()[i]).analyse(block);
        }
      }
    }
    
  }
  
  /**
   * In this step we analyse the field and check if there ate initialazed
   * 
   * @param	context		the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkInitializer(CClassContext context) throws PositionedError {

    body.analyse(block);
      
    if (! block.isReachable()) {
      throw new CLineError(getTokenReference(), KjcMessages.STATEMENT_UNREACHABLE);
    }
    
    if (getMethod().isStatic()) {
      CField[]	classFields = context.getCClass().getFields();
      for (int i = 0; i < classFields.length; i++) {
        // JLS 8.3.1.2
        // It is a compile-time error if a blank final class variable is 
        // not definitely assigned by a static initializer
        // of the class in which it is declared.
        if (classFields[i].isStatic() && !CVariableInfo.isInitialized(block.getFieldInfo(i))) {
          check(context,
                !classFields[i].isFinal() || classFields[i].isSynthetic() || classFields[i].getIdent() == JAV_OUTER_THIS,
                KjcMessages.UNINITIALIZED_FINAL_FIELD,
                classFields[i].getIdent());
        }
      }
    }
    block.close(getTokenReference());
    self.close(getTokenReference());
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    // Don t print initializers
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private CBlockContext   block;
  private CMethodContext  self;
  private final boolean   isDummy;
}
