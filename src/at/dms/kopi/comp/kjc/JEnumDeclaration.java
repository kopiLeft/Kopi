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


import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.JavadocComment;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a java enum class in the syntax tree
 */
public class JEnumDeclaration extends JClassDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  /**
   * Constructs an enum declaration node in the syntax tree.
   *
   * @param	where		the line of this node in the source code
   * @param	modifiers	the list of modifiers of this enum class
   * @param	ident		the simple name of this enum class
   * @param	interfaces	the interfaces implemented by this enum class
   * @param	fields		the fields defined by this enum class
   * @param	methods		the methods defined by this enum class
   * @param	inners		the inner classes defined by this enum class
   * @param	initializers	the class and instance initializers defined by this enum class
   * @param	javadoc		java documentation comments
   * @param	comment		other comments in the source code
   */
    public JEnumDeclaration(TokenReference where,
                            int modifiers,
                            String ident,
                            TypeFactory typeFactory,
                            CReferenceType[] interfaces,
                            JFieldDeclaration[] fields,
                            JMethodDeclaration[] methods,
                            JTypeDeclaration[] inners,
                            JPhylum[] initializers,
                            boolean hasAnonymousInners,
                            JavadocComment javadoc,
                            JavaStyleComment[] comment)
  {
        super(where,
              modifiers,
              ident,
              CTypeVariable.EMPTY,
              typeFactory.createType(JAV_ENUM, 
                                     new CReferenceType[][] {{ 
                                         new CTypeVariable(ident, CReferenceType.EMPTY)
                                     }}, 
                                     false),
              interfaces,
              fields,
              methods,
              inners,
              initializers,
              javadoc,
              comment);
        
    this.hasAnonymousInners = hasAnonymousInners;
  }


  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------
  protected void checkModifiers(final CContext context) throws PositionedError {
  	
    int	modifiers = getModifiers();
  	
    // Syntactically valid enum modifiers
    check(context,
          CModifier.getSubsetOf(modifiers, ACC_PROTECTED | ACC_ABSTRACT 
                                | ACC_STATIC | ACC_FINAL) == 0,
                                KjcMessages.INVALID_ENUM_MODIFIERS,
                                CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_PROTECTED 
                                                                         | ACC_ABSTRACT | ACC_STATIC 
                                                                         | ACC_FINAL)));

    // JSR201 : Enum classes are implicitly final.
    if (!hasAnonymousInners) {
        setModifiers(modifiers | ACC_FINAL);
    }
    // inner enums should be static
    if (getCClass().isNested()) {
        setModifiers(modifiers | ACC_STATIC );
    } 
    
    super.checkModifiers(context);
  }
  

  /**
   * Constructs the default constructor.
   */
  protected JConstructorDeclaration constructDefaultConstructor(KjcEnvironment environment) {
      TypeFactory factory = environment.getTypeFactory();
      TokenReference where = getTokenReference();
      
      JFormalParameter[] params = {
              new JFormalParameter(where, 
                                   JLocalVariable.DES_PARAMETER,
                                   factory.createReferenceType(TypeFactory.RFT_STRING),
                                   "#s",
                                   false),
              new JFormalParameter(where, 
                                   JLocalVariable.DES_PARAMETER,
                                   factory.getPrimitiveType(TypeFactory.PRM_INT),
                                   "#i",
                                   false)
      };											  
    
    JExpression[] arguments = {new JNameExpression(where, "#s"), new JNameExpression(where, "#i")};
    
    JConstructorCall ctr = new JConstructorCall(where, false, arguments);
    
    boolean withAssertion = (environment.getAssertExtension() == KjcEnvironment.AS_ALL);

    return new JConstructorDeclaration(where,
                                       ACC_PRIVATE,
                                       ident,
                                       params,
                                       CReferenceType.EMPTY,
                                       withAssertion ? new KopiConstructorBlock(where, ctr, JStatement.EMPTY)
                                               : new JConstructorBlock(where, ctr, JStatement.EMPTY),
                                       null,
                                       null,
                                       factory);
  }

  private boolean hasAnonymousInners;

}
