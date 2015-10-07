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
/**
 */
package org.hl7.fhir;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Condition Stage</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Use to record detailed information about conditions, problems or diagnoses recognized by a clinician. There are many uses including: recording a diagnosis during an encounter; populating a problem list or a summary statement, such as a discharge summary.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.hl7.fhir.ConditionStage#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.hl7.fhir.ConditionStage#getAssessment <em>Assessment</em>}</li>
 * </ul>
 *
 * @see org.hl7.fhir.FhirPackage#getConditionStage()
 * @model extendedMetaData="name='Condition.Stage' kind='elementOnly'"
 * @generated
 */
public interface ConditionStage extends BackboneElement {
	/**
	 * Returns the value of the '<em><b>Summary</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A simple summary of the stage such as "Stage 3". The determination of the stage is disease-specific.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Summary</em>' containment reference.
	 * @see #setSummary(CodeableConcept)
	 * @see org.hl7.fhir.FhirPackage#getConditionStage_Summary()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='summary' namespace='##targetNamespace'"
	 * @generated
	 */
	CodeableConcept getSummary();

	/**
	 * Sets the value of the '{@link org.hl7.fhir.ConditionStage#getSummary <em>Summary</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Summary</em>' containment reference.
	 * @see #getSummary()
	 * @generated
	 */
	void setSummary(CodeableConcept value);

	/**
	 * Returns the value of the '<em><b>Assessment</b></em>' containment reference list.
	 * The list contents are of type {@link org.hl7.fhir.Reference}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Reference to a formal record of the evidence on which the staging assessment is based.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Assessment</em>' containment reference list.
	 * @see org.hl7.fhir.FhirPackage#getConditionStage_Assessment()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='assessment' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<Reference> getAssessment();

} // ConditionStage
