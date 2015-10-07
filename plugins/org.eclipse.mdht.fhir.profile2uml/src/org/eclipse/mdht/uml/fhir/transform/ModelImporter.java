/*******************************************************************************
 * Copyright (c) 2015 David A Carlson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David A Carlson (Clinical Cloud Solutions, LLC) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mdht.uml.fhir.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.mdht.uml.fhir.BindingStrengthKind;
import org.eclipse.mdht.uml.fhir.TypeChoice;
import org.eclipse.mdht.uml.fhir.ValueSetBinding;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.hl7.fhir.ConstraintSeverityList;
import org.hl7.fhir.ElementDefinition;
import org.hl7.fhir.ElementDefinitionBinding;
import org.hl7.fhir.ElementDefinitionConstraint;
import org.hl7.fhir.ElementDefinitionType;
import org.hl7.fhir.Extension;
import org.hl7.fhir.Id;
import org.hl7.fhir.ImplementationGuide;
import org.hl7.fhir.StructureDefinition;
import org.hl7.fhir.StructureDefinitionKindList;
import org.hl7.fhir.Uri;
import org.hl7.fhir.ValueSet;
import org.hl7.fhir.ValueSetConcept;
import org.hl7.fhir.ValueSetContains;
import org.hl7.fhir.util.FhirResourceFactoryImpl;
import org.openhealthtools.mdht.uml.validation.Diagnostic;
import org.openhealthtools.mdht.uml.validation.SeverityKind;
import org.openhealthtools.mdht.uml.validation.ValidationPackage;

public class ModelImporter implements ModelConstants {
	private String[] constraintLanguages = { "Analysis", "XPath", "OCL" };
	
	private ModelIndexer modelIndexer = new ModelIndexer();
	
	// key = id, value = StructureDefinition
	private Map<String,StructureDefinition> structureDefinitionMap = new HashMap<String,StructureDefinition>();

	// key = id, value = ValueSet
	private Map<String,ValueSet> valueSetMap = new HashMap<String,ValueSet>();

	// key = id, value = ImplementationGuide
	private Map<String,ImplementationGuide> implementationGuideMap = new HashMap<String,ImplementationGuide>();
	
	private Package model;
	private Package xmlPrimitiveTypes;
	
	private Class baseClass;
	private Class dataTypeClass;

	public ModelImporter(Package model) {
		this.model = model;
		initializeLibraries(model);
		
		modelIndexer.indexMembers(model);
	}
	
	private void initModel(Package model) {
		initValueSets();
		initAbstractTypes(model);
	}
	
	private void initializeLibraries(Package umlPackage) {
		URI libraryURI = URI.createPlatformPluginURI(XML_PRIMITIVE_TYPES_LIBRARY, false);
		Resource libraryResource = umlPackage.eResource().getResourceSet().getResource(libraryURI, true);
		if (libraryResource != null) {
			for (EObject eObject : libraryResource.getContents()) {
				if (eObject instanceof Package) {
					xmlPrimitiveTypes = (Package) eObject;
					break;
				}
			}
		}
		
		if (xmlPrimitiveTypes != null) {
			PackageImport libraryImport = null;
			for (PackageImport pkgImport : model.getPackageImports()) {
				if (xmlPrimitiveTypes == pkgImport.getImportedPackage()) {
					libraryImport = pkgImport;
					break;
				}
			}
			if (libraryImport == null) {
				model.createPackageImport(xmlPrimitiveTypes);
			}
		}
	}
	
	private void initAbstractTypes(Package umlPackage) {
		/* - create abstract type: Base
		 * - import Element, add extends Base
		 * - import Resource, add extends Base
		 * - create abstract type: DataType, add extends Element
		 */

		Profile fhirUmlProfile = UMLUtil.getProfile(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getTypeChoice().getEPackage(), umlPackage);
		
		baseClass = modelIndexer.getStructureDefinitionForURI(MDHT_STRUCTURE_URI_BASE + BASE_CLASS_NAME);
		if (baseClass == null) {
			baseClass = umlPackage.createOwnedClass(BASE_CLASS_NAME, true);
			if (fhirUmlProfile != null) {
				org.eclipse.mdht.uml.fhir.StructureDefinition structureDefStereotype = (org.eclipse.mdht.uml.fhir.StructureDefinition) UMLUtil.safeApplyStereotype(baseClass, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getStructureDefinition().getName()));
				structureDefStereotype.setUri(MDHT_STRUCTURE_URI_BASE + BASE_CLASS_NAME);
			}
		}
		dataTypeClass = modelIndexer.getStructureDefinitionForURI(MDHT_STRUCTURE_URI_BASE + DATATYPE_CLASS_NAME);
		if (dataTypeClass == null) {
			dataTypeClass = umlPackage.createOwnedClass(DATATYPE_CLASS_NAME, true);
			if (fhirUmlProfile != null) {
				org.eclipse.mdht.uml.fhir.StructureDefinition structureDefStereotype = (org.eclipse.mdht.uml.fhir.StructureDefinition) UMLUtil.safeApplyStereotype(dataTypeClass, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getStructureDefinition().getName()));
				structureDefStereotype.setUri(MDHT_STRUCTURE_URI_BASE + DATATYPE_CLASS_NAME);
			}
		}
	}

	private void initValueSets() {
		if (modelIndexer.getValueSetForURI(FHIR_VALUESET_URI_BASE + VALUESET_ID_RESOURCE_TYPES) == null) {
			ValueSet valueSet = valueSetMap.get(VALUESET_ID_DATA_TYPES);
			if (valueSet != null) {
				importValueSet((ValueSet)valueSet);
			}
			valueSet = valueSetMap.get(VALUESET_ID_RESOURCE_TYPES);
			if (valueSet != null) {
				importValueSet((ValueSet)valueSet);
			}
			valueSetMap.get(VALUESET_ID_DEFINED_TYPES);
			if (valueSet != null) {
				importValueSet((ValueSet)valueSet);
			}
		}
		
		if (modelIndexer.getValueSetForURI(FHIR_VALUESET_URI_BASE + VALUESET_ID_RESOURCE_TYPES) == null) {
			importValueSetFromServer(VALUESET_ID_DATA_TYPES, false);
			importValueSetFromServer(VALUESET_ID_RESOURCE_TYPES, false);
			importValueSetFromServer(VALUESET_ID_DEFINED_TYPES, true);
		}
	}
	
	public Classifier importStructureDefinitionFromServer(String resourceId) {
		String uriString = REGISTRY_SERVER + "StructureDefinition/" + resourceId + "?_format=xml";
		return importResource(URI.createURI(uriString));
	}
	
	public Classifier importValueSetFromServer(String resourceId, boolean expand) {
		String uriString = TERMINOLOGY_SERVER + "ValueSet/" + resourceId;
		if (expand) {
			uriString += "/$expand" + "?_format=xml";
		}
		else {
			uriString += "?_format=xml";
		}
		return importResource(URI.createURI(uriString));
	}
	
	public Classifier importResource(URI resourceURI) {
		Classifier umlClassifier = null;
		ResourceFactoryImpl resourceFactory = new FhirResourceFactoryImpl();
		Resource resource = resourceFactory.createResource(resourceURI);
		try {
			resource.load(new HashMap<String,String>());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		TreeIterator<?> iterator = EcoreUtil.getAllContents(Collections.singletonList(resource));

		while (iterator != null && iterator.hasNext()) {
			Object child = iterator.next();
			if (child instanceof StructureDefinition) {
				umlClassifier = importStructureDefinition((StructureDefinition)child);
				iterator.prune();
			}
			else if (child instanceof ValueSet) {
				umlClassifier = importValueSet((ValueSet)child);
				iterator.prune();
			}
		}
		
		return umlClassifier;
	}

	public void indexResource(URI resourceURI) {
		ResourceFactoryImpl resourceFactory = new FhirResourceFactoryImpl();
		Resource resource = resourceFactory.createResource(resourceURI);
		try {
			resource.load(new HashMap<String,String>());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TreeIterator<?> iterator = EcoreUtil.getAllContents(Collections.singletonList(resource));

		while (iterator != null && iterator.hasNext()) {
			Object child = iterator.next();
			if (child instanceof StructureDefinition) {
				StructureDefinition structureDef = (StructureDefinition) child;
				structureDefinitionMap.put(structureDef.getId().getValue(), structureDef);
				iterator.prune();
			}
			else if (child instanceof ValueSet) {
				ValueSet valueSet = (ValueSet) child;
				valueSetMap.put(valueSet.getId().getValue(), valueSet);
				iterator.prune();
			}
			else if (child instanceof ImplementationGuide) {
				ImplementationGuide implGuide = (ImplementationGuide) child;
				implementationGuideMap.put(implGuide.getId().getValue(), implGuide);
				iterator.prune();
			}
		}
	}

	public void importIndexedResources() {
		initModel(model);
		importStructureDefinition(ELEMENT_CLASS_NAME);
		importStructureDefinition(RESOURCE_CLASS_NAME);
		
		// import each FHIR resource that was previously indexed
		for (ValueSet valueSet : valueSetMap.values()) {
			importValueSet(valueSet);
		}
		for (StructureDefinition structureDef : structureDefinitionMap.values()) {
			importStructureDefinition(structureDef);
		}
		for (ImplementationGuide guide : implementationGuideMap.values()) {
			importImplementationGuide(guide);
		}
		
		valueSetMap.clear();
		structureDefinitionMap.clear();
		implementationGuideMap.clear();
	}
	
	public Package importImplementationGuide(ImplementationGuide guide) {
		Package umlGuide = null;
		System.out.println("Implementation Guide: " + guide.getId().getValue() + ", " + guide.getName().getValue());
		return umlGuide;
	}
	
	public Enumeration importValueSet(ValueSet valueSet) {
		String valueSetUrl = valueSet.getUrl().getValue();
		Enumeration valueSetEnum = modelIndexer.getValueSetForURI(valueSetUrl);
		if (valueSetEnum != null) {
			return valueSetEnum;
		}

		String packageName = PACKAGE_NAME_VALUESETS;
		Package valueSetPkg = model.getNestedPackage(packageName, true, UMLPackage.eINSTANCE.getPackage(), true);
		
		String valueSetName = valueSet.getName().getValue();
		valueSetEnum = valueSetPkg.createOwnedEnumeration(valueSetName);

		Profile fhirUmlProfile = UMLUtil.getProfile(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getValueSet().getEPackage(), valueSetEnum);
		org.eclipse.mdht.uml.fhir.ValueSet valueSetStereotype = null;
		if (fhirUmlProfile != null) {
			valueSetStereotype = (org.eclipse.mdht.uml.fhir.ValueSet) UMLUtil.safeApplyStereotype(valueSetEnum, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getValueSet().getName()));
			valueSetStereotype.setUri(valueSet.getUrl().getValue());
			if (valueSet.getId() != null) {
				valueSetStereotype.setId(valueSet.getId().getValue());
			}
			if (valueSet.getName() != null) {
				valueSetStereotype.setName(valueSet.getName().getValue());
			}
			if (valueSet.getVersion() != null) {
				valueSetStereotype.setVersion(valueSet.getVersion().getValue());
			}
			if (valueSet.getPublisher() != null) {
				valueSetStereotype.setPublisher(valueSet.getPublisher().getValue());
			}
		}

		if (valueSet.getExpansion() != null) {
			for (ValueSetContains contains : valueSet.getExpansion().getContains()) {
				// TODO code was null in some cases, investigate
				if (contains.getCode() != null) {
					valueSetEnum.createOwnedLiteral(contains.getCode().getValue());
				}
			}
		}
		else if (valueSet.getCodeSystem() != null) {
			for (ValueSetConcept concept : valueSet.getCodeSystem().getConcept()) {
				EnumerationLiteral literal = valueSetEnum.createOwnedLiteral(concept.getCode().getValue());
				if (concept.getDefinition() != null) {
					literal.createOwnedComment().setBody(concept.getDefinition().getValue());
				}
			}
		}
		
		if (valueSetStereotype != null) {
			modelIndexer.addElement(valueSetStereotype);
		}
		
		return valueSetEnum;
	}

	public Class importProfileForURI(String profileURI) {
		Class umlClass = modelIndexer.getStructureDefinitionForURI(profileURI);
		if (umlClass == null) {
			String profileId = profileURI.substring(profileURI.lastIndexOf("/") + 1);
			umlClass = importStructureDefinition(profileId);
		}
		
		return umlClass;
	}
	
	public Class importStructureDefinition(String profileId) {
		Class umlClass = modelIndexer.getStructureDefinitionForId(profileId);
		if (umlClass == null) {
			// this is for a few profiles that have error, using String instead of string.
			umlClass = modelIndexer.getStructureDefinitionForId(profileId.toLowerCase());
		}

		if (umlClass == null) {
			// look in the indexed bundle(s)
			StructureDefinition structureDef = structureDefinitionMap.get(profileId);
			if (structureDef != null) {
				umlClass = importStructureDefinition(structureDef);
			}
		}

		/*
		if (umlClass == null) {
			// Try reading from Registry Server
			umlClass = (Class) importStructureDefinitionFromServer(profileName);
		}
		*/
		
		if (umlClass == null) {
			System.err.println("Cannot find Profile: " + profileId);
		}
		
		return umlClass;
	}
	
	public Class importStructureDefinition(StructureDefinition structureDef) {
		Class profileClass = modelIndexer.getStructureDefinitionForURI(structureDef.getUrl().getValue());
		if (profileClass != null) {
			return profileClass;
		}

		boolean isFhirDefinedType = modelIndexer.isDefinedType(structureDef.getName().getValue());
		PrimitiveType primitiveType = null;
		String packageName = PACKAGE_NAME_PROFILES;
		StructureDefinitionKindList structureKind = structureDef.getKind().getValue();
		if (structureDef.getContextType() != null) {
			packageName = PACKAGE_NAME_EXTENSIONS;
		}
		else if (StructureDefinitionKindList.DATATYPE == structureKind) {
			if (modelIndexer.isCoreDataType(structureDef.getName().getValue())) {
				packageName = PACKAGE_NAME_DATATYPES;
				primitiveType = getPrimitiveType(structureDef.getName().getValue());
			}
		}
		else if (StructureDefinitionKindList.RESOURCE == structureKind) {
			if (modelIndexer.isCoreResourceType(structureDef.getName().getValue())) {
				packageName = PACKAGE_NAME_RESOURCES;
			}
		}

		Package kindPackage = model.getNestedPackage(packageName, true, UMLPackage.eINSTANCE.getPackage(), true);
		
		String profileClassName = structureDef.getId().getValue();
		boolean isAbstract = structureDef.getAbstract().isValue();
		profileClass = kindPackage.createOwnedClass(profileClassName, isAbstract);

		Map<String,Constraint> constraintMap = new HashMap<String,Constraint>();
		createConstraints(profileClass, structureDef, constraintMap);

		Profile fhirUmlProfile = UMLUtil.getProfile(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getTypeChoice().getEPackage(), profileClass);
		if (fhirUmlProfile != null) {
			org.eclipse.mdht.uml.fhir.StructureDefinition structureDefStereotype = (org.eclipse.mdht.uml.fhir.StructureDefinition) UMLUtil.safeApplyStereotype(profileClass, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getStructureDefinition().getName()));
			structureDefStereotype.setUri(structureDef.getUrl().getValue());
			if (structureDef.getId() != null) {
				structureDefStereotype.setId(structureDef.getId().getValue());
			}
			if (structureDef.getName() != null) {
				structureDefStereotype.setName(structureDef.getName().getValue());
			}
			if (structureDef.getDisplay() != null) {
				structureDefStereotype.setDisplay(structureDef.getDisplay().getValue());
			}
			if (structureDef.getVersion() != null) {
				structureDefStereotype.setFhirVersion(structureDef.getVersion().getValue());
			}
			if (structureDef.getPublisher() != null) {
				structureDefStereotype.setPublisher(structureDef.getPublisher().getValue());
			}
			if (structureDef.getContextType() != null) {
				structureDefStereotype.setContextType(structureDef.getContextType().getValue().getName());
			}
			if (structureDef.getContext() != null) {
				for (org.hl7.fhir.String fhirString : structureDef.getContext()) {
					structureDefStereotype.getContexts().add(fhirString.getValue());
				}
			}
			
			modelIndexer.addElement(structureDefStereotype);
		}
		
		// Apply UML stereotypes for kinds of Comment
		if (structureDef.getDescription() != null) {
			Comment description = profileClass.createOwnedComment();
			description.setBody(structureDef.getDescription().getValue());
			UMLUtil.safeApplyStereotype(description, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getDescription().getName()));
		}
		if (structureDef.getRequirements() != null) {
			Comment requirements = profileClass.createOwnedComment();
			requirements.setBody(structureDef.getRequirements().getValue());
			UMLUtil.safeApplyStereotype(requirements, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getRequirements().getName()));
		}

		// Primitive types have unique representation and "known by magic" from reading specification.
		if (primitiveType != null) {
			profileClass.createGeneralization(dataTypeClass);
		}
		else if (ELEMENT_CLASS_NAME.equals(profileClassName)) {
			profileClass.createGeneralization(baseClass);
			dataTypeClass.createGeneralization(profileClass);
			// move DataType class to types package
			dataTypeClass.setPackage(profileClass.getNearestPackage());
		}
		else if (RESOURCE_CLASS_NAME.equals(profileClassName)) {
			profileClass.createGeneralization(baseClass);
		}
		else if (structureDef.getBase() != null) {
			String base = structureDef.getBase().getValue();
			Class baseProfileClass = null;
			if (base.startsWith("http://")) {
				baseProfileClass = importProfileForURI(base);
			}
			else {
				baseProfileClass = importStructureDefinition(base);
			}
			
			if (baseProfileClass != null) {
				// Add "DataType" abstract superclass for all data types
				if (StructureDefinitionKindList.DATATYPE == structureKind
						&& ELEMENT_CLASS_NAME.equals(baseProfileClass.getName())) {
					baseProfileClass = dataTypeClass;
				}
				
				if (baseProfileClass != null) {
					profileClass.createGeneralization(baseProfileClass);
				}
			}
			else {
				System.err.println("Cannot find base class: " + base + " in: " + structureDef.getUrl().getValue());
			}
		}
		
		// the classes defined by element definitions in this structure, profile class and nested classes
		// key = path, value = UML Element
		Map<String,Element> elementPathMap = new HashMap<String,Element>();

		// Nested classes reused via name/nameReference
		// key = name, value = UML class
		Map<String,Classifier> nameReferenceMap = new HashMap<String,Classifier>();
		
		// Set of element paths that are sliced
		Set<String> slicedElements = new HashSet<String>();

		// the first element definition is always the "profile element" defining the main profile class.
		boolean isProfileElement = true;
		
		for (ElementDefinition elementDef : structureDef.getDifferential().getElement()) {
			if (elementDef.getSlicing() != null) {
				slicedElements.add(elementDef.getPath().getValue());

				// TODO for now omit slicing elements that don't also have a type, causes extra 'extension' property
				if (elementDef.getType() == null) {
					continue;
				}
			}
			boolean isSliced = slicedElements.contains(elementDef.getPath().getValue());
			
			// parse path segments to identify nested classes and property names
			String path = elementDef.getPath().getValue();
			String[] pathSegments = path.split("\\.");
			
			// Get the list of element types, then create a property, and maybe a nested class
			List<Classifier> typeList = getTypeList(elementDef);
			
			if (isProfileElement) {
				// the first ElementDefinition
				isProfileElement = false;
				elementPathMap.put(path, profileClass);
				
				if (elementDef.getName() != null && elementDef.getName().getValue() != null) {
					profileClass.setName(elementDef.getName().getValue());
				}

				// create generalization only if not created from 'base' profile
				if (profileClass.getGeneralizations().isEmpty() && typeList.size() == 1) {
					Classifier baseType = typeList.get(0);
					
					//TODO Element has type Element, expand check for circular generalization references
					if (!baseType.equals(profileClass)) {
						// Add "DataType" abstract superclass for all data types
						if (StructureDefinitionKindList.DATATYPE == structureKind
								&& ELEMENT_CLASS_NAME.equals(baseType.getName())) {
							baseType = dataTypeClass;
						}
						
						profileClass.createGeneralization(baseType);
					}
				}

				addComments(profileClass, elementDef);

				// Find UML Constraint and add property to constrainedElements list.
				for (ElementDefinitionConstraint fhirConstraint : elementDef.getConstraint()) {
					if (fhirConstraint.getKey() != null) {
						Constraint umlConstraint = constraintMap.get(fhirConstraint.getKey().getValue());
						if (umlConstraint != null) {
							umlConstraint.getConstrainedElements().add(profileClass);
						}
					}
				}
				
				// don't create a Property for profile element
				continue;
			}
			
			Class ownerClass = null;
			String ownerPath = path.substring(0, path.lastIndexOf("."));
			Element ownerElement = elementPathMap.get(ownerPath);

			if (ownerElement instanceof Class) {
				ownerClass = (Class) ownerElement;
			}
			else if (ownerElement instanceof Property && ((Property)ownerElement).getType() != null) {
				// Test for null type required for a mis-formed profile, sub-path elements on a prohibited parent, multiplicity 0..0
				// this may be called recursively for multi-segment paths in a constraint profile
				ownerClass = getOwnerClass(elementDef, elementPathMap, path);

				// use element type(s) as superclass(es)
				//TODO if multi-segment path, will this be the correct superclass type?
				if (ownerClass.getGeneralizations().isEmpty()) {
					for (Classifier parent : typeList) {
						ownerClass.createGeneralization(parent);
					}
				}
			}
			
			if (ownerClass == null) {
				System.err.println("Cannot find owner class, structDef: " + structureDef.getId().getValue() + " path: " + elementDef.getPath().getValue());
				continue;
			}

			boolean isProhibitedElement = elementDef.getMax() != null && "0".equals(elementDef.getMax().getValue());
			String propertyName = getPropertyName(elementDef);

			// Map of all inherited wildcard properties, e.g. value[]
			Map<String,Property> wildcardProperties = new HashMap<String,Property>();

			if (ownerClass != null) {
				for (Property attr : ownerClass.allAttributes()) {
					if (attr.getName().endsWith("[x]")) {
						String wildcardName = attr.getName().substring(0, attr.getName().length() - 3);
						if (wildcardProperties.get(wildcardName) == null) {
							wildcardProperties.put(wildcardName, attr);
						}
					}
				}
			}
			
			Property inheritedProperty = null;
			
			// Look for inherited wildcard property, unless this is a wildcard (ends with [x]).
			if (propertyName.indexOf("[x]") == -1) {
				for (String wildcardName : wildcardProperties.keySet()) {
					// check for name length to avoid unintended matching, e.g. datatype 'value' attribute
					if (propertyName.length() > wildcardName.length() && propertyName.startsWith(wildcardName)) {
						inheritedProperty = wildcardProperties.get(wildcardName);
						
						// if no explicit type, find data type from wildcard suffix
						if (typeList.isEmpty()) {
							String typeName = propertyName.substring(wildcardName.length());
							Class wildcardType = importStructureDefinition(typeName);
							if (wildcardType != null) {
								typeList.add(wildcardType);
							}
							else {
								System.err.println("Cannot find wildcard type: '" + typeName + "' in structDef: " + structureDef.getId().getValue() + " path: " + elementDef.getPath().getValue());
							}
						}
						break;
					}
				}
			}
			
			if (inheritedProperty == null && !isFhirDefinedType) {
				inheritedProperty = org.openhealthtools.mdht.uml.common.util.UMLUtil.getInheritedProperty(ownerClass, propertyName);
			}

			 // Get inherited property.  If typeList is empty, and inherited not null, add inherited property type to typeList.
			if (!isProhibitedElement && typeList.isEmpty() && inheritedProperty != null 
					&& inheritedProperty.getType() instanceof Classifier) {
				typeList.add((Classifier)inheritedProperty.getType());
			}
			
			Classifier propertyType = null;
			if (primitiveType != null) {
				propertyType = primitiveType;
			}
			else if (elementDef.getNameReference() != null) {
				String referencedName = elementDef.getNameReference().getValue();
				propertyType = nameReferenceMap.get(referencedName);
				if (propertyType == null) {
					System.err.println("Cannot find referencedName: " + referencedName + " from owner class " 
							+ownerClass.getName() + " at path: "+ path);
				}
			}
			else if (!isProhibitedElement && typeList.isEmpty()) {
				// create a new nested class
				String nestedClassName = getClassName(elementDef);
				propertyType = (Class) ownerClass.createNestedClassifier(nestedClassName, UMLPackage.eINSTANCE.getClass_());
				elementPathMap.put(path, (Class) propertyType);
				
				Class backboneElement = importStructureDefinition(BACKBONE_ELEMENT_CLASS_NAME);
				if (backboneElement != null) {
					propertyType.createGeneralization(backboneElement);
				}
			}
			else if (typeList.size() == 1) {
				propertyType = typeList.get(0);
			}
			else if (typeList.size() > 1) {
				// All types must be same kind, some elements mix Resource and CodeableConcept
				Class resourceType = importStructureDefinition(RESOURCE_CLASS_NAME);
				if (FhirModelUtil.allSubclassOf(typeList, RESOURCE_CLASS_NAME)) {
					propertyType = resourceType;
				}
				else if (FhirModelUtil.allSubclassOf(typeList, DATATYPE_CLASS_NAME)) {
					propertyType = dataTypeClass;
				}
				else {
					// TODO in FHIR profiles, Reference is a kind of DataType. Using Base is too general.
					propertyType = baseClass;
				}
			}
			
			// Create the UML Property
			// if this is an Extension element and 'name' is specified, use as Property name
			if (elementDef.getName() != null && propertyType != null && modelIndexer.isExtension(propertyType)) {
				propertyName = elementDef.getName().getValue();
			}
			Property property = ownerClass.createOwnedAttribute(propertyName, propertyType);
			elementPathMap.put(path, property);
			assignMultiplicity(property, elementDef);
			
			if (fhirUmlProfile != null) {
				org.eclipse.mdht.uml.fhir.ElementDefinition elementDefStereotype = (org.eclipse.mdht.uml.fhir.ElementDefinition) UMLUtil.safeApplyStereotype(property, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getElementDefinition().getName()));
				if (elementDef.getId() != null) {
					elementDefStereotype.setId(elementDef.getId());
				}
				if (elementDef.getName() != null) {
					elementDefStereotype.setName(elementDef.getName().getValue());
					nameReferenceMap.put(elementDef.getName().getValue(), propertyType);
				}
				if (elementDef.getNameReference() != null) {
					elementDefStereotype.setName(elementDef.getNameReference().getValue());
				}
				if (elementDef.getLabel() != null) {
					elementDefStereotype.setLabel(elementDef.getLabel().getValue());
				}
				if (elementDef.getMustSupport() != null) {
					elementDefStereotype.setMustSupport(elementDef.getMustSupport().isValue());
				}
				if (elementDef.getIsModifier() != null) {
					elementDefStereotype.setIsModifier(elementDef.getIsModifier().isValue());
				}
				if (elementDef.getIsSummary() != null) {
					elementDefStereotype.setIsSummary(elementDef.getIsSummary().isValue());
				}
				
				// TODO generalize for any feature fixed*
				if (elementDef.getFixedUri() != null) {
					property.setDefault(elementDef.getFixedUri().getValue());
					property.setIsReadOnly(true);
				}
				
				if (elementDef.getBinding() != null) {
					ElementDefinitionBinding binding = elementDef.getBinding();
					ValueSetBinding valueSetBinding = (ValueSetBinding) UMLUtil.safeApplyStereotype(property, fhirUmlProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getValueSetBinding().getName()));
					valueSetBinding.setStrength(BindingStrengthKind.get(binding.getStrength().getValue().getLiteral()));
					if (binding.getDescription() != null) {
						valueSetBinding.setDescription(binding.getDescription().getValue());
					}
					if (binding.getValueSetUri() != null) {
						valueSetBinding.setValueSetUri(binding.getValueSetUri().getValue());
					}
					if (binding.getValueSetReference() != null) {
						valueSetBinding.setValueSetReference(binding.getValueSetReference().getReference().getValue());
					}
				}
			}
			
			// skip for prohibited elements in constraint profiles
			if (!isProhibitedElement) {
				property.setIsOrdered(true);
				if (isAssociation(property)) {
					createAssociation(ownerClass, property);
				}
			}
			
			/*
			 * Redefined or subsetted (sliced) from an inherited property
			 */
			// extension attributes within an extension (complex extension) don't specify slicing
			String inheritedName = pathSegments[pathSegments.length - 1];
			if (!isProhibitedElement && (isSliced || "extension".equals(inheritedName))) {
				Property subsettedProperty = org.openhealthtools.mdht.uml.common.util.UMLUtil.getInheritedProperty(property.getClass_(), inheritedName);
				if (subsettedProperty != null) {
					property.getSubsettedProperties().add(subsettedProperty);
				}
			}
			else if (inheritedProperty != null) {
				property.getRedefinedProperties().add(inheritedProperty);
			}
			else if (!isFhirDefinedType) {
				System.err.println("Inherited property not found: " + property.getQualifiedName());
			}

			addComments(property, elementDef);
			
			if (typeList.size() > 1) {
				addTypeChoice(property, typeList);
			}
			
			// Find UML Constraint and add property to constrainedElements list.
			for (ElementDefinitionConstraint fhirConstraint : elementDef.getConstraint()) {
				if (fhirConstraint.getKey() != null) {
					Constraint umlConstraint = constraintMap.get(fhirConstraint.getKey().getValue());
					if (umlConstraint != null) {
						umlConstraint.getConstrainedElements().add(property);
					}
				}
			}
			for (Id conditionId : elementDef.getCondition()) {
				String key = conditionId.getValue();
				Constraint umlConstraint = constraintMap.get(key);
				
				if (umlConstraint != null) {
					umlConstraint.getConstrainedElements().add(property);
				}
				else {
					System.err.println("Cannot resolve condition reference: " + key + " in: " + property.getQualifiedName());
				}
			}
		}
		
		return profileClass;
	}
	
	private void addComments(Element umlElement, ElementDefinition elementDef) {
		Profile fhirProfile = UMLUtil.getProfile(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getTypeChoice().getEPackage(), umlElement);
		if (elementDef.getShort() != null) {
			Comment comment = umlElement.createOwnedComment();
			comment.setBody(elementDef.getShort().getValue());
			UMLUtil.safeApplyStereotype(comment, fhirProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getShortDescription().getName()));
		}
		if (elementDef.getDefinition() != null) {
			Comment comment = umlElement.createOwnedComment();
			comment.setBody(elementDef.getDefinition().getValue());
			UMLUtil.safeApplyStereotype(comment, fhirProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getDefinition().getName()));
			
			// assure that definition is first comment, for display in UML tooling
			umlElement.getOwnedComments().move(0, comment);
		}
		if (elementDef.getComments() != null) {
			Comment comment = umlElement.createOwnedComment();
			comment.setBody(elementDef.getComments().getValue());
			UMLUtil.safeApplyStereotype(comment, fhirProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getComments().getName()));
		}
		if (elementDef.getRequirements() != null) {
			Comment comment = umlElement.createOwnedComment();
			comment.setBody(elementDef.getRequirements().getValue());
			UMLUtil.safeApplyStereotype(comment, fhirProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getRequirements().getName()));
		}
	}

	/**
	 * Determines the class-name that the element would have if it was defining
	 * a class. This means it uses "name", if present, and the last "path"
	 * component otherwise.
	 * 
	 * @param elementDef
	 * @return
	 */
	private String getClassName(ElementDefinition elementDef) {
		String name = null;
		for (Extension extension : elementDef.getExtension()) {
			if (EXTENSION_EXPLICIT_TYPE_NAME.equals(extension.getUrl())) {
				name = extension.getValueString().getValue();
			}
		}
		
		if (name == null) {
			if (elementDef.getName() != null && elementDef.getName().getValue() != null) {
				name = elementDef.getName().getValue();
			}
			else {
				String[] path = elementDef.getPath().getValue().split("\\.");
				name = path[path.length - 1];
			}
			
			//TODO toUpperCamelCase, remove "-" etc.
			StringBuffer camelCaseNameBuffer = new StringBuffer();
			camelCaseNameBuffer.append(name.substring(0, 1).toUpperCase());
			camelCaseNameBuffer.append(name.substring(1));
			name = camelCaseNameBuffer.toString();
		}
		
		return name;
	}

	private String getUniqueNestedClassifierName(Class owner, String name) {
		int seqNo = 1;
		String uniqueName = name + "-" + seqNo;

		while (null != owner.getNestedClassifier(uniqueName)) {
			uniqueName = name + "-" + String.valueOf(seqNo++);
		}

		return uniqueName;
	}
	/**
	 * Determines the property from the last "path" component.
	 * 
	 * @param elementDef
	 * @return
	 */
	private String getPropertyName(ElementDefinition elementDef) {
		String[] path = elementDef.getPath().getValue().split("\\.");
		if (path.length == 1) {
			// datatype profiles have a simple path, e.g. 'value'
			return path[0];
		}
		else {
			return path[path.length - 1];
		}
	}
	
	private PrimitiveType getPrimitiveType(String typeName) {
		switch (typeName) {
			case "boolean":
				return getXMLPrimitiveType("boolean");
			case "integer":
				return getXMLPrimitiveType("int");
			case "string":
				return getXMLPrimitiveType("string");
			case "decimal":
				return getXMLPrimitiveType("decimal");
			case "uri":
				return getXMLPrimitiveType("anyURI");
			case "base64Binary":
				return getXMLPrimitiveType("base64Binary");
			case "instant":
				return getXMLPrimitiveType("dateTime");
			case "date":
				// should be union of xs:date, xs:gYearMonth, xs:gYear
				return getXMLPrimitiveType("date");
			case "dateTime":
				// should be union of xs:dateTime, xs:date, xs:gYearMonth, xs:gYear
				return getXMLPrimitiveType("dateTime");
			case "time":
				return getXMLPrimitiveType("time");
			default:
				return null;
		}
	}

	private PrimitiveType getXMLPrimitiveType(String typeName) {
		return (PrimitiveType) xmlPrimitiveTypes.getOwnedType(typeName, true, UMLPackage.eINSTANCE.getPrimitiveType(), false);
	}
	
	private List<Classifier> getTypeList(ElementDefinition elementDef) {
		List<Classifier> typeList = new ArrayList<Classifier>();
		
		for (ElementDefinitionType elementDefType : elementDef.getType()) {
			Classifier typeClass = null;
			if (!elementDefType.getProfile().isEmpty()) {
				for (Uri profileURI : elementDefType.getProfile()) {
					typeClass = importProfileForURI(profileURI.getValue());

					//TODO for now, use only first profile type
					if (typeClass != null) {
						break;
					}
				}
			}
			if (typeClass == null && elementDefType.getCode() != null && elementDefType.getCode().getValue() != null) {
				String typeName = elementDefType.getCode().getValue();
				if ("*".equals(typeName)) {
					// TODO this should be limited to Open Type list, http://hl7.org/fhir/2015May/datatypes.html#open
					typeClass = dataTypeClass;
				}
				else {
					typeClass = importStructureDefinition(typeName);
				}
			}
			
			if (typeClass != null) {
				typeList.add(typeClass);
			}
		}
		return typeList;
	}
	
	private Class getOwnerClass(ElementDefinition elementDef, Map<String,Element> elementPathMap, String path) {
		Class ownerClass = null;
		String ownerPath = path.substring(0, path.lastIndexOf("."));
		Element ownerElement = elementPathMap.get(ownerPath);

		if (ownerElement instanceof Class) {
			ownerClass = (Class) ownerElement;
		}
		else if (ownerElement instanceof Property) {
			Property ownerProperty = (Property) ownerElement;

			// If current nested class type is from superclass, create a new derived nested class.
			if (ownerProperty.getType().getOwner() instanceof Class 
					&& ownerProperty.getType().getOwner() == ownerProperty.getClass_()) {
				ownerClass = (Class) ownerProperty.getType();
			}
			else {
				// Replace property type with a new nested class derived from prior type.
				// If owner property does not have nested type, create a nested type extending its current type.
				
				Class propertySupertype = (Class) ownerProperty.getType();
				String nestedClassName =  propertySupertype.getName();
//				if (!nestedClassName.endsWith("_Nested")) {
//					nestedClassName += "_Nested";
//				}
				nestedClassName = getUniqueNestedClassifierName(ownerProperty.getClass_(), nestedClassName);
				ownerClass = (Class) ownerProperty.getClass_().createNestedClassifier(nestedClassName, UMLPackage.eINSTANCE.getClass_());
				ownerClass.createGeneralization(propertySupertype);
				ownerProperty.setType(ownerClass);
				
//				elementPathMap.put(path, (Class) ownerClass);
			}
		}
		else {
			//TODO test this
			// may be recursive for multi-segment paths that jump levels of nesting
			String ownerOwnerClassPath = ownerPath.substring(0, path.lastIndexOf("."));
			ownerClass =  getOwnerClass(elementDef, elementPathMap, ownerOwnerClassPath);
		}
		
		return ownerClass;
	}

	/**
	 * Association if property type is a subclass of DomainResource, or type is a nested class.
	 */
	private boolean isAssociation(Property property) {
		if (property.getType() instanceof Classifier 
				&& (FhirModelUtil.isKindOf((Classifier)property.getType(), RESOURCE_CLASS_NAME) 
						|| FhirModelUtil.isSubclassOf((Classifier)property.getType(), BACKBONE_ELEMENT_CLASS_NAME))) {
			return true;
		}
		
		return false;
	}

	private Property createAssociation(Class sourceClass, Property targetProp) {
		if (isAssociation(targetProp)) {
			Association association = (Association) sourceClass.getNearestPackage().createOwnedType(null, UMLPackage.eINSTANCE.getAssociation());
			Property sourceProp = UMLFactory.eINSTANCE.createProperty();
			sourceProp.setType(sourceClass);
			sourceProp.setLower(1);
			sourceProp.setUpper(1);
			association.getOwnedEnds().add(sourceProp);
			association.getMemberEnds().add(targetProp);
			
			// associations to nested classes must be composite
			if (FhirModelUtil.hasNestedType(targetProp)) {
				targetProp.setAggregation(AggregationKind.COMPOSITE_LITERAL);
			}
		}
		
		return targetProp;
	}
	
	private void assignMultiplicity(Property property, ElementDefinition elementDef) {
		int lower = 0;
		int upper = 1;
		
		if (elementDef.getMin() != null) {
			lower = elementDef.getMin().getValue();
		}
		if (elementDef.getMax() != null) {
			String max = elementDef.getMax().getValue();
			if ("*".equals(max)) {
				upper = -1;
			}
			else {
				try {
					upper = Integer.parseInt(max);
				}
				catch (NumberFormatException e) {
					// TODO error handling
					System.err.println("Invalid max cardinality: " + max + " on " + property.getQualifiedName());
				}
			}
		}
		
		property.setLower(lower);
		property.setUpper(upper);
	}
	
	private void addTypeChoice(Property property, List<Classifier> typeList) {
		Profile fhirProfile = UMLUtil.getProfile(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getTypeChoice().getEPackage(), property);
		if (fhirProfile != null) {
			TypeChoice typeChoice = (TypeChoice) UMLUtil.safeApplyStereotype(property, fhirProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getTypeChoice().getName()));
			typeChoice.getTypes().addAll(typeList);
		}
	}
	
	private void createConstraints(Class profileClass, StructureDefinition structureDef, Map<String,Constraint> constraintMap) {
		// iterate over differential elements to find all constraint definitions
		TreeIterator<?> iterator = EcoreUtil.getAllContents(Collections.singletonList(structureDef.getDifferential()));

		while (iterator != null && iterator.hasNext()) {
			Object child = iterator.next();
			if (child instanceof ElementDefinitionConstraint) {
				ElementDefinitionConstraint fhirConstraint = (ElementDefinitionConstraint) child;
				if (fhirConstraint.getKey() != null) {
					Constraint umlConstraint = addConstraint(profileClass, fhirConstraint);
					String key = fhirConstraint.getKey().getValue();
					constraintMap.put(key, umlConstraint);
				}
				else {
					System.err.println("ElementDefinitionConstraint entry missing key, " + fhirConstraint);
				}
				iterator.prune();
			}
		}
	}
	
	private Constraint addConstraint(Namespace owner, ElementDefinitionConstraint fhirConstraint) {
		Constraint umlConstraint = owner.createOwnedRule(null);
		
		if (fhirConstraint.getKey() != null) {
			String key = fhirConstraint.getKey().getValue();
			umlConstraint.setName(key);
		}
		
		if (fhirConstraint.getRequirements() != null) {
			Comment comment = umlConstraint.createOwnedComment();
			comment.setBody(fhirConstraint.getRequirements().getValue());

			Profile fhirProfile = UMLUtil.getProfile(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getTypeChoice().getEPackage(), owner);
			UMLUtil.safeApplyStereotype(comment, fhirProfile.getOwnedStereotype(org.eclipse.mdht.uml.fhir.FHIRPackage.eINSTANCE.getRequirements().getName()));
		}
		
		OpaqueExpression spec = (OpaqueExpression) umlConstraint.createSpecification(
				null, null, UMLPackage.eINSTANCE.getOpaqueExpression());
		
//		if (fhirConstraint.getHuman() != null) {
//			spec.getLanguages().add(constraintLanguages[0]);
//			spec.getBodies().add(fhirConstraint.getHuman().getValue());
//		}
		if (fhirConstraint.getXpath() != null) {
			spec.getLanguages().add(constraintLanguages[1]);
			spec.getBodies().add(fhirConstraint.getXpath().getValue());
		}

		Stereotype diagnosticStereo = umlConstraint.getApplicableStereotype("Validation::Diagnostic");
		if (diagnosticStereo != null) {
			EObject instance = umlConstraint.applyStereotype(diagnosticStereo);
			EStructuralFeature code = instance.eClass().getEStructuralFeature("code");
			if (code != null) {
				instance.eUnset(code); // initialize the code to blank
			}
			
			Diagnostic diagnostic = (Diagnostic) EcoreUtil.getObjectByType(
				umlConstraint.getStereotypeApplications(), ValidationPackage.Literals.DIAGNOSTIC);
			if (diagnostic != null) {
				if (fhirConstraint.getSeverity() != null) {
					if (ConstraintSeverityList.WARNING == fhirConstraint.getSeverity().getValue()) {
						diagnostic.setSeverity(SeverityKind.WARNING);
					}
					else {
						diagnostic.setSeverity(SeverityKind.ERROR);
					}
				}

				if (fhirConstraint.getHuman() != null) {
					diagnostic.setMessage(fhirConstraint.getHuman().getValue());
				}
			}
		}
		
		return umlConstraint;
	}
	
}
