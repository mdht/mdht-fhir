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
package org.eclipse.mdht.uml.fhir.ui.properties;


import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mdht.uml.fhir.TypeChoice;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Property;
import org.openhealthtools.mdht.uml.common.util.UMLUtil;
import org.openhealthtools.mdht.uml.ui.properties.sections.WrapperAwareModelerPropertySection;

/**
 * The properties section for a Property TypeChoice list.
 * 
 * @author dcarlson
 */
public class ElementDefinitionSection extends WrapperAwareModelerPropertySection {

	protected Property umlProperty;

	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(final ModifyEvent event) {

		}
	};

	private FocusListener focusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			// do nothing
		}

		public void focusLost(FocusEvent event) {
			modifyFields();
		}
	};

	private void modifyFields() {

	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	public void createControls(final Composite parent, final TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);


	}

	@Override
	protected boolean isReadOnly() {
		if (umlProperty != null) {
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(umlProperty);
			if (editingDomain != null && editingDomain.isReadOnly(umlProperty.eResource())) {
				return true;
			}
		}

		return super.isReadOnly();
	}

	/*
	 * Override super implementation to allow for objects that are not IAdaptable.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.properties.sections.AbstractModelerPropertySection#addToEObjectList(java.lang.Object)
	 */
	@Override
	protected boolean addToEObjectList(Object object) {
		boolean added = super.addToEObjectList(object);
		if (!added && object instanceof Element) {
			getEObjectList().add(object);
			added = true;
		}
		return added;
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		this.umlProperty = null;
		
		EObject element = getEObject();
		if (element instanceof View) {
			element = ((View) element).getElement();
		}
		if (element instanceof Association) {
			element = UMLUtil.getNavigableEnd((Association) element);
		}
		
		if (element instanceof Property) {
			this.umlProperty = (Property) element;
			for (EObject stereoApp : ((Element)element).getStereotypeApplications()) {
				if (stereoApp instanceof TypeChoice) {
					element = stereoApp;
				}
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	public void refresh() {

	}

	/**
	 * Update if necessary, upon receiving the model event.
	 * 
	 * @see #aboutToBeShown()
	 * @see #aboutToBeHidden()
	 * @param notification
	 *            -
	 *            even notification
	 * @param element
	 *            -
	 *            element that has changed
	 */
	@Override
	public void update(final Notification notification, EObject element) {
		if (!isDisposed()) {
			postUpdateRequest(new Runnable() {

				public void run() {
					if (!isDisposed() && !isNotifierDeleted(notification)) {
						refresh();
					}
				}
			});
		}
	}

}
