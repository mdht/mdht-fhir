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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mdht.uml.fhir.FHIRPackage;
import org.eclipse.mdht.uml.fhir.StructureDefinition;
import org.eclipse.mdht.uml.fhir.TypeChoice;
import org.eclipse.mdht.uml.fhir.common.util.FhirModelUtil;
import org.eclipse.mdht.uml.fhir.ui.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.openhealthtools.mdht.uml.common.ui.dialogs.DialogLaunchUtil;
import org.openhealthtools.mdht.uml.common.ui.search.IElementFilter;
import org.openhealthtools.mdht.uml.common.util.UMLUtil;
import org.openhealthtools.mdht.uml.ui.properties.sections.WrapperAwareModelerPropertySection;

/**
 * The properties section for a Property TypeChoice list.
 * 
 * @author dcarlson
 */
public class TypeChoiceSection extends WrapperAwareModelerPropertySection {

	protected Property umlProperty;

	protected TypeChoice typeChoice;

	private FHIRTableViewer typeChoiceViewer = null;

	private Button addButton = null;

	private Button removeButton = null;

	private void addChoiceMember() {
		try {
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(typeChoice);

			IUndoableOperation operation = new AbstractEMFOperation(editingDomain, "temp") {
				@Override
				protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info) {
					this.setLabel("Add Type Choice");

					// search for a StructureDefinition
					// add TypeChoice stereotype, if not already applied
					//    -- move current non-abstract type into choice list
					
					String baseTypeName = umlProperty.getType().getName();
					Class type = selectChoiceMember(baseTypeName, "Extension");
					if (type != null) {
						typeChoice.getTypes().add(type);
					}
					
					refresh();

					return Status.OK_STATUS;
				}
			};

			execute(operation);

		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
	}

	protected Class selectChoiceMember(String hasBaseTypeNames, String notBaseTypeNames) {
		// select a structure definition class
		Profile fhirProfile = org.eclipse.uml2.uml.util.UMLUtil.getProfile(FHIRPackage.eINSTANCE.getStructureDefinition().getEPackage(), umlProperty);
		if (fhirProfile == null) {
			return null;
		}
		
		Stereotype structureDefStereotype = fhirProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getStructureDefinition().getName());
		IElementFilter filter = new IElementFilter() {
			public boolean accept(Element element) {
				boolean hasStereotype =  (element instanceof Class) && element.isStereotypeApplied(structureDefStereotype);
				boolean hasBaseType = true;
				boolean notBaseType = true;
				
				if (hasStereotype && hasBaseTypeNames != null) {
					hasBaseType = false;
					String[] names = hasBaseTypeNames.split(",| ");
					for (int i = 0; i < names.length; i++) {
						boolean isKindOf = FhirModelUtil.isKindOf((Class)element, names[i]);
						if (isKindOf) {
							hasBaseType = true;
							break;
						}
					}
				}
				if (hasStereotype && hasBaseType && notBaseTypeNames != null) {
					String[] names = notBaseTypeNames.split(",| ");
					for (int i = 0; i < names.length; i++) {
						boolean isKindOf = FhirModelUtil.isKindOf((Class)element, names[i]);
						if (isKindOf) {
							notBaseType = false;
							break;
						}
					}
				}
				
				return hasStereotype && hasBaseType && notBaseType;
			}
		};

		final Class selectedClass = (Class) DialogLaunchUtil.chooseElement(
			filter, UMLUtil.getTopPackage(umlProperty), getPart().getSite().getShell(), null, "Select a type");

		return selectedClass;
	}

	private void removeChoiceMember(final Classifier choiceMember) {
		try {
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(typeChoice);

			IUndoableOperation operation = new AbstractEMFOperation(editingDomain, "temp") {
				@Override
				protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info) {
					this.setLabel("Remove Choice Member");

					if (typeChoice != null) {
						typeChoice.getTypes().remove(choiceMember);
					}
					refresh();

					return Status.OK_STATUS;
				}
			};

			execute(operation);

		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
	}

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

		Composite composite = getWidgetFactory().createGroup(parent, "Type Choice");
		FormLayout layout = new FormLayout();
		layout.marginWidth = ITabbedPropertyConstants.HSPACE;
		layout.marginHeight = ITabbedPropertyConstants.VSPACE;
		layout.spacing = ITabbedPropertyConstants.VMARGIN;
		composite.setLayout(layout);

		addButton = getWidgetFactory().createButton(composite, null, SWT.PUSH);
		Image addImage = Activator.getDefault().getBundledImage("icons/full/eview16/add.gif");
		addButton.setImage(addImage);
		addButton.setToolTipText("Add Type");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				addChoiceMember();
			}
		});

		removeButton = getWidgetFactory().createButton(composite, null, SWT.PUSH);
		Image removeImage = Activator.getDefault().getBundledImage("icons/full/eview16/remove.gif");
		removeButton.setImage(removeImage);
		removeButton.setToolTipText("Delete selected type(s)");
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ISelection selection = typeChoiceViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					for (Object selected : ((IStructuredSelection)selection).toList()) {
						if (selected instanceof StructureDefinition) {
							selected = ((StructureDefinition)selected).getBase_Class();
						}
						if (selected instanceof Classifier) {
							removeChoiceMember((Classifier)selected);
						}
					}
				}
			}
		});

		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		addButton.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(addButton, 0);
		removeButton.setLayoutData(data);
		
		Table table = getWidgetFactory().createTable(composite, SWT.V_SCROLL | SWT.MULTI);
		data = new FormData();
		data.left = new FormAttachment(addButton, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		table.setLayoutData(data);

		typeChoiceViewer = new FHIRTableViewer(table) {
			@Override
			protected AdapterFactoryContentProvider createContentProvider(AdapterFactory adapterFactory) {
		        return new AdapterFactoryContentProvider(adapterFactory) {
					@Override
					public Object[] getElements(Object object) {
						List<Object> choiceMembers = new ArrayList<Object>();
						if (typeChoice != null) {
							for (Classifier member : typeChoice.getTypes()) {
								StructureDefinition structureDef = FhirModelUtil.getStructureDefinition(member);
								choiceMembers.add(structureDef != null ? structureDef : member);
							}
						}
						return choiceMembers.toArray();
					}
		        };
			}

			@Override
		    protected void createColumns() {
		        String[] titles = { "Class", "Base Type", "Profile", "Aggregation" };
		        int[] bounds = { 150, 150, 300, 100 };

		        TableViewerColumn classColumn = createTableViewerColumn(titles[0], bounds[0], 0);
		        TableViewerColumn baseTypeColumn = createTableViewerColumn(titles[1], bounds[1], 1);
		        TableViewerColumn profileColumn = createTableViewerColumn(titles[2], bounds[2], 2);
		        TableViewerColumn aggregationColumn = createTableViewerColumn(titles[3], bounds[3], 3);

		        // TODO add editing support for each editable column
		        
		    }
		
		};
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
		this.typeChoice = null;
		
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

		if (element instanceof TypeChoice) {
			this.typeChoice = (TypeChoice) element;
		}
	}

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	public void refresh() {
		if (typeChoice == null) {
			typeChoiceViewer.setInput(null);
			return;
		}
		
		typeChoiceViewer.setInput(typeChoice);
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
