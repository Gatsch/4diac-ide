/**
 * generated by Xtext 2.25.0
 */
package org.eclipse.fordiac.ide.model.structuredtext.structuredText;

import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Primary Variable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.PrimaryVariable#getVar <em>Var</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage#getPrimaryVariable()
 * @model
 * @generated
 */
public interface PrimaryVariable extends Variable
{
  /**
   * Returns the value of the '<em><b>Var</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Var</em>' reference.
   * @see #setVar(VarDeclaration)
   * @see org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage#getPrimaryVariable_Var()
   * @model
   * @generated
   */
  VarDeclaration getVar();

  /**
   * Sets the value of the '{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.PrimaryVariable#getVar <em>Var</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Var</em>' reference.
   * @see #getVar()
   * @generated
   */
  void setVar(VarDeclaration value);

} // PrimaryVariable
