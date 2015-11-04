/**
 */
package org.eclipse.mdht.uml.fhir.provider;


import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.mdht.uml.fhir.FHIRPackage;
import org.eclipse.mdht.uml.fhir.StructureDefinition;
import org.eclipse.mdht.uml.fhir.common.util.ModelIndexer;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.openhealthtools.mdht.uml.common.notation.NotationUtil;
import org.openhealthtools.mdht.uml.common.util.UMLUtil;

/**
 * This is the item provider adapter for a {@link org.eclipse.mdht.uml.fhir.StructureDefinition} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class StructureDefinitionItemProvider extends ElementItemProvider {
	
	private ModelIndexer modelIndexer = null;
	
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StructureDefinitionItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}
	
	protected ModelIndexer getModelIndexer(Element umlElement) {
		if (modelIndexer == null) {
			modelIndexer = new ModelIndexer();
			modelIndexer.indexValueSets(UMLUtil.getTopPackage(umlElement));
		}
		return modelIndexer;
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addBase_ClassPropertyDescriptor(object);
			addUriPropertyDescriptor(object);
			addNamePropertyDescriptor(object);
			addDisplayPropertyDescriptor(object);
			addFhirVersionPropertyDescriptor(object);
			addContextTypePropertyDescriptor(object);
			addContextPropertyDescriptor(object);
			addPublisherPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Base Class feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addBase_ClassPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_base_Class_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_base_Class_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__BASE_CLASS,
				 true,
				 false,
				 true,
				 null,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Uri feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUriPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_uri_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_uri_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__URI,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNamePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_name_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_name_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__NAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Display feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDisplayPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_display_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_display_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__DISPLAY,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Fhir Version feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addFhirVersionPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_fhirVersion_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_fhirVersion_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__FHIR_VERSION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Context Type feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addContextTypePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_contextType_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_contextType_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__CONTEXT_TYPE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Context feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addContextPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_context_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_context_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__CONTEXT,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Publisher feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addPublisherPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_StructureDefinition_publisher_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_StructureDefinition_publisher_feature", "_UI_StructureDefinition_type"),
				 FHIRPackage.Literals.STRUCTURE_DEFINITION__PUBLISHER,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This returns StructureDefinition.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
//		return overlayImage(object, getResourceLocator().getImage("full/obj16/StructureDefinition"));

		Object elementImage = NotationUtil.getElementImage((Class) object);
		if (elementImage == null) {
			elementImage = super.getImage(object);
		}

		return elementImage;
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		return ((StyledString)getStyledText(object)).getString();
	}
	

	/**
	 * This returns the label styled text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getStyledText(Object object) {
		String label = ((StructureDefinition)object).getName();
    	StyledString styledLabel = new StyledString();
		if (label == null || label.length() == 0) {
			styledLabel.append(getString("_UI_StructureDefinition_type"), StyledString.Style.QUALIFIER_STYLER); 
		} else {
			styledLabel.append(getString("_UI_StructureDefinition_type"), StyledString.Style.QUALIFIER_STYLER).append(" " + label);
		}
		return styledLabel;
	}

	@Override
	public String getColumnText(Object object, int columnIndex) {
		StructureDefinition structureDef = (StructureDefinition) object;

		switch (columnIndex) {
			case IStructureDefinitionTableProperties.CLASS_INDEX:
				if (structureDef.getBase_Class() != null) {
					return structureDef.getBase_Class().getName();
				}
				else {
					return null;
				}
			case IStructureDefinitionTableProperties.BASE_TYPE_INDEX:
				if (structureDef.getBase_Class() != null) {
					Classifier constrainedType = getModelIndexer(structureDef.getBase_Class()).getConstrainedType(structureDef.getBase_Class());
					if (constrainedType != null) {
						return constrainedType.getName();
					}
					else {
						return structureDef.getBase_Class().getName();
					}
				}
				else {
					return null;
				}
			case IStructureDefinitionTableProperties.PROFILE_INDEX:
				if (structureDef.getUri() != null) {
					return structureDef.getUri();
				}
				else {
					return null;
				}
			case IStructureDefinitionTableProperties.AGGREGATION_INDEX:
				// TODO
				return null;
				
			default:
				return null;
		}
	}

	@Override
	public Object getColumnImage(Object object, int columnIndex) {
		StructureDefinition structureDef = (StructureDefinition) object;

		switch (columnIndex) {
			case IStructureDefinitionTableProperties.CLASS_INDEX:
				if (structureDef.getBase_Class() != null) {
					return getImage(structureDef.getBase_Class());
				}
				else {
					return null;
				}
				
			default:
				return null;
		}
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(StructureDefinition.class)) {
			case FHIRPackage.STRUCTURE_DEFINITION__URI:
			case FHIRPackage.STRUCTURE_DEFINITION__NAME:
			case FHIRPackage.STRUCTURE_DEFINITION__DISPLAY:
			case FHIRPackage.STRUCTURE_DEFINITION__FHIR_VERSION:
			case FHIRPackage.STRUCTURE_DEFINITION__CONTEXT_TYPE:
			case FHIRPackage.STRUCTURE_DEFINITION__CONTEXT:
			case FHIRPackage.STRUCTURE_DEFINITION__PUBLISHER:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
		}
		super.notifyChanged(notification);
	}

}
