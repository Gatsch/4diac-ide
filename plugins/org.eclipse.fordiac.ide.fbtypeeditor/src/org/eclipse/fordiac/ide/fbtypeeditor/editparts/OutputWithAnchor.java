/*******************************************************************************
 * Copyright (c) 2011 - 2017 Profactor GmbH, TU Wien ACIN, fortiss GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Gerhard Ebenhofer, Alois Zoitl
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.fbtypeeditor.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;

public class OutputWithAnchor extends WithAnchor {

	public OutputWithAnchor(final IFigure figure, final int pos, final EditPart editPart) {
		super(figure, pos, editPart);
	}

	@Override
	public Point getLocation(final Point reference) {
		final Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBox());
		r.translate(-1, -1);
		r.resize(1, 1);
		getOwner().translateToAbsolute(r);
		final int leftX = (int) (r.x + r.width + (float) ((WITH_DISTANCE * getZoomFactor()) * getPos()));
		final int centerY = r.y + r.height / 2;
		return new Point(leftX, centerY);
	}
}
