/*******************************************************************************
 * Copyright (c) 2024 Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Mathias Garstenauer - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.typemanagement.refactoring.connection;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.fordiac.ide.model.data.DataFactory;
import org.eclipse.fordiac.ide.model.data.StructuredType;
import org.eclipse.fordiac.ide.model.helpers.InterfaceListCopier;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.model.typelibrary.TypeEntry;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibraryManager;
import org.eclipse.fordiac.ide.typemanagement.preferences.TypeManagementPreferencesHelper;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;

public class CreateStructChange extends Change {
	private final URI structURI;
	private final Collection<VarDeclaration> vars;

	public CreateStructChange(final URI structURI, final Collection<VarDeclaration> vars) {
		this.structURI = structURI;
		this.vars = vars;
	}

	@Override
	public String getName() {
		return "Create new Structured Type";
	}

	@Override
	public void initializeValidationData(final IProgressMonitor pm) {
		// TODO Auto-generated method stub

	}

	@Override
	public RefactoringStatus isValid(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Change perform(final IProgressMonitor pm) throws CoreException {
//		final String TEMPLATE_PATH = Platform.getInstallLocation().getURL().getFile() + File.separatorChar + "template";
//
//		final File template = new File(TEMPLATE_PATH + File.separatorChar + "Struct.dtp"); //$NON-NLS-1$

		final IPath path = new Path(structURI.toPlatformString(true));
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

//		final TypeFromTemplateCreator creator = new TypeFromTemplateCreator(file, template, "");
//		creator.createTypeFromTemplate(new NullProgressMonitor());
//
//		if (TypeLibraryManager.INSTANCE.getTypeEntryForURI(structURI) instanceof final StructuredType struct) {
//
//		}

		final TypeEntry entry = TypeLibraryManager.INSTANCE.getTypeLibrary(file.getProject()).createTypeEntry(file);

		final StructuredType type = DataFactory.eINSTANCE.createStructuredType();

		InterfaceListCopier.copyVarList(type.getMemberVariables(), vars, true);

		TypeManagementPreferencesHelper.setupVersionInfo(type);
		final String datatypeName = TypeEntry.getTypeNameFromFile(file);
		type.setName(datatypeName);

		entry.save(type);

		return new DeleteResourceChange(path, false);
	}

	@Override
	public Object getModifiedElement() {
		// TODO Auto-generated method stub
		return null;
	}

}
