/*******************************************************************************
 * Copyright (c) 2023 Johannes Kepler University Linz
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Melanie Winter - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.fordiac.ide.fb.interpreter.testappgen.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.fordiac.ide.model.FordiacKeywords;
import org.eclipse.fordiac.ide.model.NameRepository;
import org.eclipse.fordiac.ide.model.libraryElement.Algorithm;
import org.eclipse.fordiac.ide.model.libraryElement.BasicFBType;
import org.eclipse.fordiac.ide.model.libraryElement.ECAction;
import org.eclipse.fordiac.ide.model.libraryElement.Event;
import org.eclipse.fordiac.ide.model.libraryElement.FBType;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;

public class MuxFBGenerator extends AbstractFBGenerator {

	public MuxFBGenerator(final FBType type, final TestSuite testSuite) {
		super(type, testSuite);

	}

	public BasicFBType generateMuxFB() {
		createBasicFB();
		return destinationFB;
	}

	@Override
	protected List<Event> createInputEventList() {
		final List<Event> list = new ArrayList<>();
		for (final TestCase testCase : testSuite.getTestCases()) {
			list.add(createEvent(testCase.getName() + "_TEST", true)); //$NON-NLS-1$
		}
		list.add(createEvent("ERROR", true));//$NON-NLS-1$
		list.add(createEvent("SUCCESS", true)); //$NON-NLS-1$
		return list;

	}

	@Override
	protected List<Event> createOutputEventList() {
		final List<Event> list = new ArrayList<>();
		list.add(createEvent("ERR", false));//$NON-NLS-1$
		list.add(createEvent("SUCC", false)); //$NON-NLS-1$
		return list;
	}

	@Override
	protected List<VarDeclaration> createInputDataList() {
		if (sourceType.getInterfaceList().getOutputVars().isEmpty()) {
			return new ArrayList<>();
		}
		final List<VarDeclaration> list = new ArrayList<>();
		for (int i = 0; i < testSuite.getTestCases().size(); i++) {
			final VarDeclaration varDecl = createVarDeclaration("DO" + i + "_match", true); //$NON-NLS-1$ //$NON-NLS-2$
			varDecl.setType(sourceType.getTypeLibrary().getDataTypeLibrary().getType(FordiacKeywords.BOOL));
			varDecl.setComment(""); //$NON-NLS-1$

			list.add(varDecl);
		}
		return list;
	}

	@Override
	protected List<VarDeclaration> createOutputDataList() {
		if (sourceType.getInterfaceList().getOutputVars().isEmpty()) {
			return new ArrayList<>();
		}
		final List<VarDeclaration> list = new ArrayList<>();
		final VarDeclaration varDecl = createVarDeclaration("DO1", false);
		varDecl.setType(sourceType.getTypeLibrary().getDataTypeLibrary().getType(FordiacKeywords.BOOL));
		varDecl.setComment(""); //$NON-NLS-1$
		list.add(varDecl);
		return list;
	}

	@Override
	protected void generateECC() {
		final TestEccGenerator eccGen = new TestEccGenerator(destinationFB.getECC(), 0);
		for (int i = 0; i < destinationFB.getInterfaceList().getEventInputs().size(); i++) {
			final Event ev = destinationFB.getInterfaceList().getEventInputs().get(i);
			int stateCnt = 0;
			if (ev.getName().contains("TEST")) { //$NON-NLS-1$
				// create state from input event
				eccGen.createState("S", 0); //$NON-NLS-1$
				eccGen.getLast().setName(NameRepository.createUniqueName(eccGen.getLast(), "S1")); //$NON-NLS-1$
				eccGen.createTransitionFromTo(eccGen.getEcc().getStart(), eccGen.getLast(), ev);
				stateCnt++;

				// create state for error output
				eccGen.createState("S", stateCnt); //$NON-NLS-1$
				eccGen.getLast().setName(NameRepository.createUniqueName(eccGen.getLast(), "S1")); //$NON-NLS-1$
				eccGen.createTransitionFromTo(eccGen.getNTimesLast(1), eccGen.getLast(),
						destinationFB.getInterfaceList().getEventInputs()
								.get(destinationFB.getInterfaceList().getEventInputs().size() - 1));
				eccGen.createTransitionFromTo(eccGen.getLast(), eccGen.getEcc().getStart(), null);

				final ECAction errAction = TestEccGenerator.createAction();
				final ECAction sucAction = TestEccGenerator.createAction();
				errAction.setOutput(destinationFB.getInterfaceList().getEventInputs()
						.get(destinationFB.getInterfaceList().getEventInputs().size() - 2));
				if (!destinationFB.getInterfaceList().getOutputVars().isEmpty()) {
					final Algorithm alg = TestEccGenerator.createMuxFbAlgorithm(destinationFB, i);
					errAction.setAlgorithm(alg);
					sucAction.setAlgorithm(alg);

				}
				eccGen.getLast().getECAction().add(errAction);
				eccGen.increaseCaseCount();

				// create state for success output
				eccGen.createState("S", stateCnt); //$NON-NLS-1$
				eccGen.getLast().setName(NameRepository.createUniqueName(eccGen.getLast(), "S1")); //$NON-NLS-1$
				eccGen.createTransitionFromTo(eccGen.getNTimesLast(2), eccGen.getLast(),
						destinationFB.getInterfaceList().getEventInputs()
								.get(destinationFB.getInterfaceList().getEventInputs().size() - 2));
				eccGen.createTransitionFromTo(eccGen.getLast(), eccGen.getEcc().getStart(), null);

				sucAction.setOutput(destinationFB.getInterfaceList().getEventInputs()
						.get(destinationFB.getInterfaceList().getEventInputs().size() - 1));
				eccGen.getLast().getECAction().add(sucAction);
			}
			eccGen.increaseCaseCount();
			eccGen.increaseCaseCount();
		}
	}

	@Override
	protected String getTypeName() {
		return sourceType.getName() + "_MUX"; //$NON-NLS-1$
	}

	@Override
	protected FBType getSourceFB() {
		return sourceType;
	}

}
