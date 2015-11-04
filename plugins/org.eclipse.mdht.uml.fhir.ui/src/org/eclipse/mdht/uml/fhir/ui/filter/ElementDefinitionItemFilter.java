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
import org.eclipse.mdht.uml.fhir.ElementDefinition;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Property;
import org.openhealthtools.mdht.uml.common.ui.filters.AbstractFilter;
import org.openhealthtools.mdht.uml.common.util.UMLUtil;

/**
 * Selects a property where ElementDefinition stereotype (or sub-stereotypes) is applied.
 */
public class ElementDefinitionItemFilter extends AbstractFilter {

	public boolean select(Object object) {
		Element element = getElement(object);

		if (element != null) {
			if (element instanceof Association) {
				element = UMLUtil.getNavigableEnd((Association) element);
			}
			if (element instanceof Property) {
				for (EObject stereoApp : element.getStereotypeApplications()) {
					if (stereoApp instanceof ElementDefinition) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
