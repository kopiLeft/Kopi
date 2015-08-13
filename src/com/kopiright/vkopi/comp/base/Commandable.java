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

package com.kopiright.vkopi.comp.base;

import java.util.Vector;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CStdType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.CTypeVariable;
import com.kopiright.kopi.comp.kjc.JBlock;
import com.kopiright.kopi.comp.kjc.JBreakStatement;
import com.kopiright.kopi.comp.kjc.JCatchClause;
import com.kopiright.kopi.comp.kjc.JCompoundStatement;
import com.kopiright.kopi.comp.kjc.JEmptyStatement;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JExpressionStatement;
import com.kopiright.kopi.comp.kjc.JFormalParameter;
import com.kopiright.kopi.comp.kjc.JIntLiteral;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JMethodDeclaration;
import com.kopiright.kopi.comp.kjc.JNameExpression;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.kopi.comp.kjc.JSuperExpression;
import com.kopiright.kopi.comp.kjc.JSwitchGroup;
import com.kopiright.kopi.comp.kjc.JSwitchLabel;
import com.kopiright.kopi.comp.kjc.JSwitchStatement;
import com.kopiright.kopi.comp.kjc.JThisExpression;
import com.kopiright.kopi.comp.kjc.JThrowStatement;
import com.kopiright.kopi.comp.kjc.JTryCatchStatement;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents an action handler, ie an object that support action
 */
public class Commandable implements com.kopiright.vkopi.lib.form.VConstants, com.kopiright.kopi.comp.kjc.Constants {

  public Commandable(String access) {
    this.access = access;
    voidProtectedTriggers = new Vector();
    voidTriggers = new Vector();
    objectTriggers = new Vector();
    booleanTriggers = new Vector();
    integerTriggers = new Vector();
  }

  public Commandable(String access, Commandable superCommandable) {
    this.access = access;
    voidProtectedTriggers = superCommandable.voidProtectedTriggers;
    voidTriggers = superCommandable.voidTriggers;
    objectTriggers = superCommandable.objectTriggers;
    booleanTriggers = superCommandable.booleanTriggers;
    integerTriggers = superCommandable.integerTriggers;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  JExpression getThis(TokenReference ref) {
    return access == null ?
      new JThisExpression(ref) :
      (JExpression)new JNameExpression(ref, new JThisExpression(ref), access);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  public int[] getTriggerArray() {
    return triggerArray;
  }

  public VKDefinitionCollector getDefinitionCollector() {
    return collector;
  }

  public int countTriggers(int type) {
    Vector	switchBody;

    switch (type) {
    case TRG_VOID:
      switchBody = voidTriggers;
      break;
    case TRG_PRTCD:
      switchBody = voidProtectedTriggers;
      break;
    case TRG_OBJECT:
      switchBody = objectTriggers;
      break;
    case TRG_BOOLEAN:
      switchBody = booleanTriggers;
      break;
    case TRG_INT:
      switchBody = integerTriggers;
      break;
    default:
      throw new InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG " + type);
    }

    return switchBody.size();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check visual code is correct
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context,
			VKDefinitionCollector collector,
			VKCommand[] commands,
			VKTrigger[] triggers) throws PositionedError {
    this.allowSQLInTriggers = context.allowSQLInTriggers();
    this.collector = collector;
    this.commands = commands;
    this.triggers = triggers;

    for (int i = 0; i < commands.length; i++) {
      commands[i].checkCode(context, this);
    }

    for (int i = 0; i < triggers.length; i++) {
      triggers[i].checkCode(context, this);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Build a trigger handler if there is at least one event to handle
   */
  public JMethodDeclaration buildTriggerHandler(TypeFactory tf, TokenReference ref, String methodName, int type) {
    CType		returnType;
    CReferenceType[]	protecteds = VKUtils.TRIGGER_EXCEPTION;
    Vector		switchBody;
    JStatement		defaultStmt;

    switch (type) {
    case TRG_VOID:
      returnType = CStdType.Void;
      switchBody = voidTriggers;
      defaultStmt = new JCompoundStatement(ref, new JStatement[] {
	new JExpressionStatement(ref,
				 new JMethodCallExpression(ref,
							   new JSuperExpression(ref),
							   "executeVoidTrigger",
							   new JExpression[] {
							     new JNameExpression(ref, "VKT_Type")
							   }),
				 null),
	new JBreakStatement(ref, null, null)
	  });

      break;
    case TRG_PRTCD:
      returnType = CStdType.Void;
      switchBody = voidProtectedTriggers;
      protecteds = VKUtils.PROTECTED_TRIGGER_EXCEPTION;
      defaultStmt = new JEmptyStatement(ref, null);
      break;
    case TRG_OBJECT:
      returnType = CStdType.Object;
      switchBody = objectTriggers;
      defaultStmt = new JEmptyStatement(ref, null);
      break;
    case TRG_BOOLEAN:
      returnType = CStdType.Boolean;
      switchBody = booleanTriggers;
      defaultStmt = new JEmptyStatement(ref, null);
      break;
    case TRG_INT:
      returnType = CStdType.Integer;
      switchBody = integerTriggers;
      defaultStmt = new JEmptyStatement(ref, null);
      break;
    default:
      throw new InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG " + type);
    }

    JSwitchGroup	defaultGroup;

    defaultGroup = new JSwitchGroup(ref,
				    new JSwitchLabel[]{ new JSwitchLabel(ref, null /*default*/) },
				    new JStatement[]{ defaultStmt });
    switchBody.addElement(defaultGroup);

    JStatement	switchStmt;

    switchStmt = new JSwitchStatement(ref,
				      new JNameExpression(ref, "VKT_Type"),
				      (JSwitchGroup[])com.kopiright.util.base.Utils.toArray(switchBody, JSwitchGroup.class),
				      null);

    if (allowSQLInTriggers) {
      JFormalParameter  param = new JFormalParameter(ref,
                                                     0,
                                                     tf.createType("java/sql/SQLException", false),
                                                     "exc",
                                                     true);
      JExpression       exc2;
      exc2 = new JUnqualifiedInstanceCreation(ref,
                                              tf.createType("com/kopiright/vkopi/lib/visual/VExecFailedException", false),
                                              new JExpression[]{new JNameExpression(ref, "exc")});
      JStatement        stmt = new JThrowStatement(ref, exc2, null);
      JBlock            body = new JBlock(ref,
                                          new JStatement[]{stmt},
                                          null);

      JCatchClause      exc = new JCatchClause(ref, param, body);

      switchStmt = new JTryCatchStatement(ref,
                                          new JBlock(ref,
                                                     new JStatement[]{switchStmt},
                                                     null),
                                          new JCatchClause[]{exc},
                                          null);
    }

    JBlock		block;

    block = new JBlock(ref,
		       (type == TRG_VOID || type == TRG_PRTCD) ?
		       new JStatement[] { switchStmt } :
		       new JStatement[] {
			 switchStmt,
			 new JThrowStatement(ref,
					     new JUnqualifiedInstanceCreation(ref,
								      VKStdType.VRuntimeException,
								      JExpression.EMPTY),
					     null)
		       },
		       null);
    return new JMethodDeclaration(ref,
				  ACC_PUBLIC | ACC_FINAL, 
                                  CTypeVariable.EMPTY,
				  returnType,
				  methodName,
				  VKUtils.TRIGGER_PARAM,
				  protecteds,
				  block,
				  null,
				  null);
  }

  /**
   *
   */
  public int addTrigger(JStatement code, long events, int type) {
    TokenReference	ref = code.getTokenReference();
    int			pos;

    switch (type) {
    case TRG_VOID:
      pos = voidTriggers.size();
      break;
    case TRG_PRTCD:
      pos = voidProtectedTriggers.size();
      break;
    case TRG_OBJECT:
      pos = objectTriggers.size();
      break;
    case TRG_BOOLEAN:
      pos = booleanTriggers.size();
      break;
    case TRG_INT:
      pos = integerTriggers.size();
      break;
    default:
      throw new InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG " + type);
    }
    pos += 1; // we want to start our switches at 1

    JSwitchGroup group = new JSwitchGroup(ref,
					  new JSwitchLabel[] {
					    new JSwitchLabel(ref,
							     new JIntLiteral(ref, pos))
					      },
					  (type == TRG_VOID || type == TRG_PRTCD) ?
 					  new JStatement[] {
					    new JBlock(ref, new JStatement[]{code}, null),
					    new JBreakStatement(ref, null, null)
					  } :
 					  new JStatement[] {
					    new JBlock(ref, new JStatement[]{code}, null)
					      }
					  );
    switch (type) {
    case TRG_VOID:
      voidTriggers.addElement(group);
      break;
    case TRG_PRTCD:
      voidProtectedTriggers.addElement(group); // $$$ REMOVE
      break;
    case TRG_OBJECT:
      objectTriggers.addElement(group);
      break;
    case TRG_BOOLEAN:
      booleanTriggers.addElement(group);
      break;
    case TRG_INT:
      integerTriggers.addElement(group);
      break;
    default:
      throw new InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG " + type);
    }

    for (int i = 0; i < TRG_TYPES.length; i++) {
      if (((events >> i & 1) > 0)) {
	triggerArray[i] = pos;
      }
    }

    return pos;
  }

  /**
   * Returns a trigger handler position
   */
  public int addCommand(JStatement code) {
    TokenReference	ref = code.getTokenReference();
    JSwitchGroup	group = new JSwitchGroup(ref,
						 new JSwitchLabel[] {
						   new JSwitchLabel(ref,
								    new JIntLiteral(ref, voidTriggers.size() + 1))},
						 new JStatement[] {
						   new JBlock(ref, new JStatement[] {code}, null),
						   new JBreakStatement(ref, null, null)
						     });

    voidTriggers.addElement(group);
    return voidTriggers.size();
  }

  /**
   * Generates the code
   */
  public void genCode(TokenReference ref,
		      Vector body,
		      boolean generateActors,
		      boolean generateCommands)
  {
    for (int i = 0; i < commands.length; i++) {
      commands[i].genCode(this);
    }
    for (int i = 0; i < commands.length; i++) {
      if (commands[i].getBody() != null) {
	commands[i].getBody().getCommandable().genCode(ref, body, false, false);
      }
    }
    for (int i = 0; i < triggers.length; i++) {
      triggers[i].genCode(this);
    }

    if (generateActors) {
      body.addElement(new JExpressionStatement(ref,
					       new JMethodCallExpression(ref,
									 null,
									 "setActors",
									 new JExpression[] {
									   getDefinitionCollector().genCode(ref, this)
									 }),
					       null));
    }

    // COMMANDS
    if (generateCommands) {
      JExpression[] init1 = new JExpression[commands.length];
      for (int i = 0; i < commands.length; i++) {
	init1[i] = commands[i].genConstructorCall(this);
      }
      body.addElement(VKUtils.assign(ref, "commands", VKUtils.createArray(ref, VKStdType.VCommand, init1)));
    }
  }

   // ----------------------------------------------------------------------
   // CODE GENERATION
   // ----------------------------------------------------------------------

  private	Vector		voidProtectedTriggers;
  private	Vector		voidTriggers;
  private	Vector		objectTriggers;
  private	Vector		booleanTriggers;
  private	Vector		integerTriggers;

  private	int[]		triggerArray = new int[TRG_TYPES.length];
  private	VKCommand[]	commands;
  private	VKTrigger[]	triggers;
  private	VKDefinitionCollector collector;
  private	String		access;
  private       boolean         allowSQLInTriggers;
}
