/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JNullLiteral;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.compiler.base.CompilerMessages;

/**
 * The compilation unit for a VK element
 */
public class VKDefinitionCollector extends com.kopiright.util.base.Utils {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a wrapper to all definitions
   */
  public VKDefinitionCollector(String[] insertDirectories) {
    this.insertDirectories = insertDirectories;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Add insert statment
   */
  public void addInsert(String name, File file) {
    inserts_V.addElement(name);
    // lackner 12.11.01 FIX
    // set file only once
    this.file = file;
  }

  /**
   * Add insert statment
   */
  public void setInsert(VKInsert insert) {
    this.insert = insert;
  }

  /**
   * search the field type in compilation unit
   */
  public VKTypeDefinition getFieldTypeDef(String name) {
    Object	obj = types.get(name);

    if (obj != null) {
      return (VKTypeDefinition)obj;
    } else {
      for (int i = inserts.length - 1; i >= 0 && inserts[i] != null; i--) {
	obj = inserts[i].getFieldTypeDef(name);
	if (obj != null) {
	  types.put(name, obj);
	  return (VKTypeDefinition)obj;
	}
      }
      return null;
    }
  }

  /**
   * add new type on top of definition for name
   */
  public void addFieldTypeDef(VKTypeDefinition type) {
    own_types.addElement(type);
    types.put(type.getIdent(), type);
  }

  /**
   * search the command in compilation unit
   */
  public VKCommandDefinition getCommandDef(String name) {
    Object	obj = commands.get(name);

    if (obj != null) {
      return (VKCommandDefinition)obj;
    } else {
      for (int i = inserts.length - 1; i >= 0; i--) {
	obj = inserts[i].getCommandDef(name);
	if (obj != null) {
	  commands.put(name, obj);
	  return (VKCommandDefinition)obj;
	}
      }
      return null;
    }
  }

  /**
   * add new command on top of definition for name
   */
  public void addCommandDef(VKCommandDefinition command) {
    own_commands.addElement(command);
    commands.put(command.getIdent(), command);
  }

  /**
   * search the Actor in compilation unit
   */
  public VKActor getActorDef(String name) {
    Object	obj = actors.get(name);

    if (obj != null) {
      if (!usedActors.containsKey(name)) {
	usedActors.put(name, new Integer(countActors++));
      }
      return (VKActor)obj;
    } else {
      for (int i = inserts.length - 1; i >= 0; i--) {
	obj = inserts[i].getActorDef(name);
	if (obj != null) {
	  actors.put(name, obj);
 	  usedActors.put(name, new Integer(countActors++));
	  return (VKActor)obj;
	}
      }
      return null;
    }
  }

  /**
   * add new actor on top of definition for name
   */
  public void addActorDef(VKActor actor) {
    own_actors.addElement(actor);
    actors.put(actor.getIdent(), actor);
  }

  public int getActorPos(String actor) {
    Integer	integer = (Integer)usedActors.get(actor);

    return integer == null ? countActors++ : integer.intValue();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Insert all local definition to the class declaration
   */
  public void checkCode(VKContext context) throws PositionedError {
    if (insert != null) {
      insert.checkDefinition(context);
    }
    //checkCode(own_types, context);
    //checkCode(own_commands, context);
    //checkCode(own_actors, context);
    for (int i = inserts.length - 1; i >= 0; i--) {
      inserts[i].checkCode(context);
    }
  }

  /**
   * Insert all local definition to the class declaration
   */
  public void checkDefinition(VKContext context) throws PositionedError {
    checkDefinition(own_types, context);
    checkDefinition(own_commands, context);
    checkDefinition(own_actors, context);
  }

  /**
   * Insert all local definition to the class declaration
   */
  protected void checkDefinition(Vector table, VKContext context) throws PositionedError {
    for (int i = 0; i < table.size(); i++) {
      ((VKDefinition)table.elementAt(i)).checkCode(context);
    }
  }

  /**
   * Insert all local definition to the class declaration
   */
  protected void checkCode(Vector table, VKContext context) throws PositionedError {
    for (int i = 0; i < table.size(); i++) {
      ((VKDefinition)table.elementAt(i)).checkCode(context);
    }
  }

  /**
   * Verify that imported files are loaded
   */
  public boolean checkInsert(VKTopLevel top) throws UnpositionedError {
    if (inserts != null) {
      return true; // already checked
    }
    File[] verifiedFiles = null;

    if (!inserts_V.isEmpty()) {
      try {
        verifiedFiles = verifyFiles(inserts_V);
      } catch (UnpositionedError e) {
        inserts = new VKDefinitionCollector[0];
        throw e;
      }
      inserts = new VKDefinitionCollector[verifiedFiles.length];

      for (int i = 0; i < verifiedFiles.length; i++) {
        inserts[i] = top.getCollector(verifiedFiles[i]);
        inserts[i].checkInsert(top);
      }

    } else {
      inserts = new VKDefinitionCollector[0];
    }

    return true;
  }

  private File[] verifyFiles(Vector temp) throws UnpositionedError{
    File[]	files = new File[temp.size()];

    for (int i = 0; i < temp.size(); i++) {
      final String      name = (String)temp.elementAt(i);
      boolean           found = false;
      File              file;

      // try to find the file is the current directory first
      file = new File(this.file.getParentFile(), name);
      found = verifyFile(file);

      // try to find the files in the specified directories
      for (int dir = 0; !found && dir < insertDirectories.length; dir++) {
        file = new File(insertDirectories[dir], name);
        found = verifyFile(file);
      }
      if (!found) {
        throw new UnpositionedError(CompilerMessages.FILE_NOT_FOUND, name);
      }
      files[i] = file;
    }
    return files;
  }

  private boolean verifyFile(File file) {
    return file.exists() && !file.isDirectory() && file.canRead();
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates the code for all used actors
   */
  public JExpression genCode(TokenReference ref, Commandable commandable) {
    if (actors.size() == 0) {
      return new JNullLiteral(ref);
    } else {
      JExpression[]     init1 = new JExpression[actors.size()];

      for (Enumeration elems = actors.elements(); elems.hasMoreElements(); ) {
        VKActor	actor = (VKActor)elems.nextElement();
        int	actorPos = getActorPos(actor.getIdent());

        if (actorPos != -1) {
          init1[actorPos] = actor.genCode(ref, actorPos);
        }
      }
      return VKUtils.createArray(ref, VKStdType.SActor, init1);
    }
  }

  // ----------------------------------------------------------------------
  // VKCODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    p.printDefinitionCollector(inserts_V,
                               own_types,
                               own_actors,
                               own_commands);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected VKDefinitionCollector[]	inserts		= null;
  private VKInsert			insert;
  private File                          file;
  private Vector			inserts_V	= new Vector();
  private Vector			own_types	= new Vector();
  private Vector			own_commands	= new Vector();
  private Vector			own_actors	= new Vector();
  private Hashtable			types		= new Hashtable();
  private Hashtable			commands	= new Hashtable();
  private Hashtable			actors		= new Hashtable(); // $$$ CHANGE
  private Hashtable			usedActors	= new Hashtable(); // $$$ CHANGE
  private int				countActors;
  private String[]                      insertDirectories;
}
