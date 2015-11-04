/*******************************************************************************
 * Copyright (c) 2015 David A Carlson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David A Carlson (Clinical Cloud Solutions, LLC) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.mdht.uml.fhir.ui.filter;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mdht.uml.fhir.StructureDefinition;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.openhealthtools.mdht.uml.common.ui.filters.AbstractFilter;

/**
 * Selects a class where StructureDefinition stereotype (or sub-stereotypes) is applied.
 */
public class StructureDefinitionItemFilter extends AbstractFilter {

	public boolean select(Object object) {
		Element element = getElement(object);

		if (element != null) {
			if (element instanceof Class) {
				for (EObject stereoApp : element.getStereotypeApplications()) {
					if (stereoApp instanceof StructureDefinition) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
