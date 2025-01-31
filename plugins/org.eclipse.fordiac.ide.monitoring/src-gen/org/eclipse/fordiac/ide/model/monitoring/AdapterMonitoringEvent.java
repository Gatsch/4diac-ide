/*******************************************************************************
 * Copyright (c) 2012, 2013, 2015 - 2017 Profactor GmbH, fortiss GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Gerhard Ebenhofer, Alois Zoitl, Gerd Kainz
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.model.monitoring;

import org.eclipse.emf.common.util.EList;
import org.eclipse.fordiac.ide.gef.editparts.IEditPartCreator;
import org.eclipse.fordiac.ide.model.data.DataType;
import org.eclipse.fordiac.ide.model.libraryElement.Event;
import org.eclipse.fordiac.ide.model.libraryElement.INamedElement;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Adapter
 * Monitoring Event</b></em>'. <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.fordiac.ide.model.monitoring.MonitoringPackage#getAdapterMonitoringEvent()
 * @model superTypes="org.eclipse.fordiac.ide.model.monitoring.IEditPartCreator org.eclipse.fordiac.ide.model.libraryElement.Event"
 * @generated
 */
public interface AdapterMonitoringEvent extends IEditPartCreator, Event {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	@Override
	EList<INamedElement> getInputParameters();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	@Override
	EList<INamedElement> getOutputParameters();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	@Override
	EList<INamedElement> getInOutParameters();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	@Override
	DataType getReturnType();
} // AdapterMonitoringEvent
