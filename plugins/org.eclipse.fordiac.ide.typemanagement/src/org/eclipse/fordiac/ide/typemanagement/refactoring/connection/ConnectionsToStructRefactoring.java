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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fordiac.ide.model.IdentifierVerifier;
import org.eclipse.fordiac.ide.model.data.DataType;
import org.eclipse.fordiac.ide.model.data.StructuredType;
import org.eclipse.fordiac.ide.model.libraryElement.Connection;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetworkElement;
import org.eclipse.fordiac.ide.model.libraryElement.FBType;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;
import org.eclipse.fordiac.ide.model.libraryElement.ServiceInterfaceFBType;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.model.search.types.BlockTypeInstanceSearch;
import org.eclipse.fordiac.ide.model.typelibrary.TypeEntry;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibrary;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibraryManager;
import org.eclipse.fordiac.ide.typemanagement.refactoring.RepairBrokenConnectionChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class ConnectionsToStructRefactoring extends Refactoring {
	private final URI sourceURI;
	private final URI destinationURI;
	private final URI netURI;
	private final Map<String, String> replaceableConMap;

	private URI structURI;
	private String sourceVarName;
	private String destinationVarName;
	private boolean conflictResolution;

	private final Collection<VarDeclaration> vars;

	private final TypeLibrary lib;

	public ConnectionsToStructRefactoring(final FBNetwork net, final FBType sourceType, final FBType destinationType,
			final Map<String, String> replacableConMap) {
		this.netURI = EcoreUtil.getURI(net);
		this.sourceURI = EcoreUtil.getURI(sourceType);
		this.destinationURI = EcoreUtil.getURI(destinationType);
		this.replaceableConMap = replacableConMap;

		vars = sourceType.getInterfaceList().getOutputs().filter(port -> replacableConMap.containsKey(port.getName()))
				.filter(VarDeclaration.class::isInstance).map(VarDeclaration.class::cast).toList();

		lib = sourceType.getTypeLibrary();
	}

	@Override
	public final String getName() {
		return "Replace multiple Data Connections with Struct";
	}

	public RefactoringStatus setUserConfig(final URI structURI, final String sourceVarName,
			final String destinationVarName, final boolean conflictResolution) {
		// TODO: create generic function for checks
		final RefactoringStatus status = new RefactoringStatus();

		if (structURI != null) {
			final TypeEntry structTypeEntry = TypeLibraryManager.INSTANCE.getTypeEntryForURI(structURI);
			if (structTypeEntry != null && structTypeEntry.getType() instanceof final DataType type) {
				if (type instanceof final StructuredType structType) {
					if (!structType.getMemberVariables().stream().allMatch(
							structvar -> vars.stream().anyMatch(var -> var.getType().equals(structvar.getType())
									&& var.getName().equals(structvar.getName())))) {
						status.merge(RefactoringStatus.createFatalErrorStatus("Incompatible Structured Type"));
					}
				} else {
					status.merge(RefactoringStatus.createFatalErrorStatus("Selected type is no Structured Type!"));
				}
			} else if (lib.getDataTypeLibrary().getTypeIfExists(structURI.trimFileExtension().lastSegment()) != null) {
				status.merge(RefactoringStatus.createFatalErrorStatus("Structured Type already exists"));
			}
		} else {
			status.merge(RefactoringStatus.createFatalErrorStatus("Invalid Type selected!"));
		}
		if (IdentifierVerifier.verifyIdentifier(sourceVarName).isPresent()) {
			status.merge(RefactoringStatus.createFatalErrorStatus("Invalid Output Name!"));
		}
		if ((TypeLibraryManager.INSTANCE.getTypeEntryForURI(sourceURI).getType() instanceof final FBType sourceFB)
				&& (sourceFB.getInterfaceList().getAllInterfaceElements().stream()
						.anyMatch(port -> port.getName().equals(sourceVarName))
						&& !replaceableConMap.containsKey(sourceVarName))) {
			status.merge(RefactoringStatus.createFatalErrorStatus("Output Name already exists!"));
		}
		if (IdentifierVerifier.verifyIdentifier(destinationVarName).isPresent()) {
			status.merge(RefactoringStatus.createFatalErrorStatus("Invalid Input Name!"));
		}
		if ((TypeLibraryManager.INSTANCE.getTypeEntryForURI(destinationURI)
				.getType() instanceof final FBType destinationFB)
				&& (destinationFB.getInterfaceList().getAllInterfaceElements().stream()
						.anyMatch(port -> port.getName().equals(destinationVarName))
						&& !replaceableConMap.containsValue(destinationVarName))) {
			status.merge(RefactoringStatus.createFatalErrorStatus("Input Name already exists!"));

		}
		if (destinationURI.fragment().equals(sourceURI.fragment()) && sourceVarName.equals(destinationVarName)) {
			status.merge(
					RefactoringStatus.createFatalErrorStatus("Input Name cannot match Output name on the same FB"));
		}
		this.structURI = structURI;
		this.sourceVarName = sourceVarName;
		this.destinationVarName = destinationVarName;
		this.conflictResolution = conflictResolution;
		return status;

	}

	@Override
	public RefactoringStatus checkInitialConditions(final IProgressMonitor pm) {
		final RefactoringStatus status = new RefactoringStatus();
		try {
			// TODO: create generic function for checks
			pm.beginTask("Checking preconditions...", 1);
			if ((TypeLibraryManager.INSTANCE.getTypeEntryForURI(sourceURI)
					.getType() instanceof final FBType sourceFB)) {
				if (sourceFB instanceof ServiceInterfaceFBType) {
					status.merge(RefactoringStatus.createFatalErrorStatus("Source FB is Service Interface!"));
				}
				if (replaceableConMap.keySet().stream()
						.anyMatch(var -> sourceFB.getInterfaceList().getOutput(var) == null)) {
					status.merge(RefactoringStatus.createFatalErrorStatus("Source Outputs do not match!"));
				}
			} else {
				status.merge(RefactoringStatus.createFatalErrorStatus("Source is no FBType!"));
			}

			if ((TypeLibraryManager.INSTANCE.getTypeEntryForURI(destinationURI)
					.getType() instanceof final FBType destinationFB)) {
				if (destinationFB instanceof ServiceInterfaceFBType) {
					status.merge(RefactoringStatus.createFatalErrorStatus("Destination FB is Service Interface!"));
				}
				if (replaceableConMap.values().stream()
						.anyMatch(var -> destinationFB.getInterfaceList().getInput(var) == null)) {
					status.merge(RefactoringStatus.createFatalErrorStatus("Destinaion Inputs do not match!"));
				}
			} else {
				status.merge(RefactoringStatus.createFatalErrorStatus("Destination is no FBType!"));
			}

		} finally {
			pm.done();
		}
		return status;
	}

	@Override
	public RefactoringStatus checkFinalConditions(final IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		final RefactoringStatus status = new RefactoringStatus();

		// TODO: implement conditions

		return status;
	}

	@Override
	public Change createChange(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
		final CompositeChange compChange = new CompositeChange("Convert Connections To Struct");
		pm.beginTask("Convert Connections To Struct", 1);

		if (TypeLibraryManager.INSTANCE.getTypeEntryForURI(structURI) == null) {
			compChange.add(new CreateStructChange(structURI, vars));
		}
		compChange.add(new ReplaceVarsWithStructChange(sourceURI, FBType.class, replaceableConMap.keySet(), structURI,
				sourceVarName, false, 0));
		compChange.add(new ReplaceVarsWithStructChange(destinationURI, FBType.class, replaceableConMap.values(),
				structURI, destinationVarName, true, 0));

		if (TypeLibraryManager.INSTANCE.getTypeEntryForURI(sourceURI).getType() instanceof final FBType sourceType
				&& TypeLibraryManager.INSTANCE.getTypeEntryForURI(destinationURI)
						.getType() instanceof final FBType destinationType) {

			final CompositeChange updates = new CompositeChange(
					"Update all Instances of " + sourceURI.lastSegment() + " and " + sourceURI.lastSegment());
			new BlockTypeInstanceSearch(sourceType.getTypeEntry()).performSearch().stream()
					.map(FBNetworkElement.class::cast).forEach(fbnelem -> {
						updates.add(new UpdateSingleFBChange(EcoreUtil.getURI(fbnelem), FBNetworkElement.class));
					});
			if (!sourceURI.toString().equals(destinationURI.toString())) {
				new BlockTypeInstanceSearch(destinationType.getTypeEntry()).performSearch().stream()
						.map(FBNetworkElement.class::cast).forEach(fbnelem -> {
							updates.add(new UpdateSingleFBChange(EcoreUtil.getURI(fbnelem), FBNetworkElement.class));
						});
			}
			compChange.add(updates);

			final CompositeChange connectStructChange = new CompositeChange(
					"Reconnect all FBs with identical Connections");
			final CompositeChange repairs = new CompositeChange("Repair Broken Connections");

			final Map<FBNetworkElement, FBNetworkElement> map = new HashMap<>();
			new BlockTypeInstanceSearch(destinationType.getTypeEntry()).performSearch().stream()
					.map(FBNetworkElement.class::cast).forEach(instance -> {

						// Collect all correct connections
						final List<Connection> cons = instance.getInterface().getInputs()
								.map(IInterfaceElement::getInputConnections).flatMap(EList::stream)
								.filter(con -> replaceableConMap.containsKey(con.getSource().getName())
										&& replaceableConMap.get(con.getSource().getName())
												.equals(con.getDestination().getName())
										&& con.getSourceElement().getType().getName().equals(sourceType.getName()))
								.toList();

						// Check if all connections between 2 instances are correct
						if (cons.stream().map(Connection::getSourceElement).distinct().count() == 1
								&& cons.size() == replaceableConMap.size()) {
							map.put(instance, cons.get(0).getSourceElement());
							connectStructChange.add(new ConnectSingleStructChange(EcoreUtil.getURI(instance),
									FBNetworkElement.class, replaceableConMap, sourceVarName, destinationVarName));
						} else if (conflictResolution) {
							repairs.add(new RepairBrokenConnectionChange(EcoreUtil.getURI(instance),
									FBNetworkElement.class, structURI, replaceableConMap, false));
						}
					});

			compChange.add(connectStructChange);
			if (conflictResolution) {
				new BlockTypeInstanceSearch(sourceType.getTypeEntry()).performSearch().stream()
						.map(FBNetworkElement.class::cast).forEach(instance -> {
							if (instance.getInterface().getOutputs()
									.filter(output -> replaceableConMap.containsKey(output.getName()))
									.map(IInterfaceElement::getOutputConnections).flatMap(EList::stream)
									.anyMatch(con -> !replaceableConMap.get(con.getSource().getName())
											.equals(con.getDestination().getName())
											|| map.get(con.getDestinationElement()) == null
											|| !map.get(con.getDestinationElement()).getQualifiedName()
													.equals(instance.getQualifiedName()))) {
								repairs.add(new RepairBrokenConnectionChange(EcoreUtil.getURI(instance),
										FBNetworkElement.class, structURI, replaceableConMap, true));
							}
						});

				compChange.add(repairs);
			}
		}
		pm.done();
		return compChange;
	}

	public TypeLibrary getTypeLibrary() {
		return lib;
	}

}
