/**
 * generated by Xtext 2.27.0
 */
package org.eclipse.fordiac.ide.globalconstantseditor.globalConstants.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fordiac.ide.globalconstantseditor.globalConstants.GlobalConstantsFactory;
import org.eclipse.fordiac.ide.globalconstantseditor.globalConstants.GlobalConstantsPackage;
import org.eclipse.fordiac.ide.globalconstantseditor.globalConstants.STGlobalConstsSource;
import org.eclipse.fordiac.ide.globalconstantseditor.globalConstants.STVarGlobalDeclarationBlock;

import org.eclipse.fordiac.ide.model.data.DataPackage;

import org.eclipse.fordiac.ide.model.libraryElement.LibraryElementPackage;

import org.eclipse.fordiac.ide.structuredtextcore.stcore.STCorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GlobalConstantsPackageImpl extends EPackageImpl implements GlobalConstantsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stGlobalConstsSourceEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stVarGlobalDeclarationBlockEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fordiac.ide.globalconstantseditor.globalConstants.GlobalConstantsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private GlobalConstantsPackageImpl() {
		super(eNS_URI, GlobalConstantsFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link GlobalConstantsPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static GlobalConstantsPackage init() {
		if (isInited) return (GlobalConstantsPackage)EPackage.Registry.INSTANCE.getEPackage(GlobalConstantsPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredGlobalConstantsPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		GlobalConstantsPackageImpl theGlobalConstantsPackage = registeredGlobalConstantsPackage instanceof GlobalConstantsPackageImpl ? (GlobalConstantsPackageImpl)registeredGlobalConstantsPackage : new GlobalConstantsPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		STCorePackage.eINSTANCE.eClass();
		LibraryElementPackage.eINSTANCE.eClass();
		DataPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theGlobalConstantsPackage.createPackageContents();

		// Initialize created meta-data
		theGlobalConstantsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theGlobalConstantsPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(GlobalConstantsPackage.eNS_URI, theGlobalConstantsPackage);
		return theGlobalConstantsPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSTGlobalConstsSource() {
		return stGlobalConstsSourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSTGlobalConstsSource_Name() {
		return (EAttribute)stGlobalConstsSourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSTGlobalConstsSource_Imports() {
		return (EReference)stGlobalConstsSourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSTGlobalConstsSource_Elements() {
		return (EReference)stGlobalConstsSourceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSTVarGlobalDeclarationBlock() {
		return stVarGlobalDeclarationBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GlobalConstantsFactory getGlobalConstantsFactory() {
		return (GlobalConstantsFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		stGlobalConstsSourceEClass = createEClass(ST_GLOBAL_CONSTS_SOURCE);
		createEAttribute(stGlobalConstsSourceEClass, ST_GLOBAL_CONSTS_SOURCE__NAME);
		createEReference(stGlobalConstsSourceEClass, ST_GLOBAL_CONSTS_SOURCE__IMPORTS);
		createEReference(stGlobalConstsSourceEClass, ST_GLOBAL_CONSTS_SOURCE__ELEMENTS);

		stVarGlobalDeclarationBlockEClass = createEClass(ST_VAR_GLOBAL_DECLARATION_BLOCK);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		STCorePackage theSTCorePackage = (STCorePackage)EPackage.Registry.INSTANCE.getEPackage(STCorePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		stGlobalConstsSourceEClass.getESuperTypes().add(theSTCorePackage.getSTSource());
		stVarGlobalDeclarationBlockEClass.getESuperTypes().add(theSTCorePackage.getSTVarDeclarationBlock());

		// Initialize classes and features; add operations and parameters
		initEClass(stGlobalConstsSourceEClass, STGlobalConstsSource.class, "STGlobalConstsSource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSTGlobalConstsSource_Name(), ecorePackage.getEString(), "name", null, 0, 1, STGlobalConstsSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSTGlobalConstsSource_Imports(), theSTCorePackage.getSTImport(), null, "imports", null, 0, -1, STGlobalConstsSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSTGlobalConstsSource_Elements(), this.getSTVarGlobalDeclarationBlock(), null, "elements", null, 0, -1, STGlobalConstsSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stVarGlobalDeclarationBlockEClass, STVarGlobalDeclarationBlock.class, "STVarGlobalDeclarationBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //GlobalConstantsPackageImpl
