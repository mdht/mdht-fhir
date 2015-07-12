/**
 */
package org.hl7.fhir.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.hl7.fhir.Code;
import org.hl7.fhir.ConceptMapDependsOn;
import org.hl7.fhir.ConceptMapElement;
import org.hl7.fhir.ConceptMapMap;
import org.hl7.fhir.FhirPackage;
import org.hl7.fhir.Uri;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concept Map Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.hl7.fhir.impl.ConceptMapElementImpl#getCodeSystem <em>Code System</em>}</li>
 *   <li>{@link org.hl7.fhir.impl.ConceptMapElementImpl#getCode <em>Code</em>}</li>
 *   <li>{@link org.hl7.fhir.impl.ConceptMapElementImpl#getDependsOn <em>Depends On</em>}</li>
 *   <li>{@link org.hl7.fhir.impl.ConceptMapElementImpl#getMap <em>Map</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConceptMapElementImpl extends BackboneElementImpl implements ConceptMapElement {
	/**
	 * The cached value of the '{@link #getCodeSystem() <em>Code System</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCodeSystem()
	 * @generated
	 * @ordered
	 */
	protected Uri codeSystem;

	/**
	 * The cached value of the '{@link #getCode() <em>Code</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCode()
	 * @generated
	 * @ordered
	 */
	protected Code code;

	/**
	 * The cached value of the '{@link #getDependsOn() <em>Depends On</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependsOn()
	 * @generated
	 * @ordered
	 */
	protected EList<ConceptMapDependsOn> dependsOn;

	/**
	 * The cached value of the '{@link #getMap() <em>Map</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMap()
	 * @generated
	 * @ordered
	 */
	protected EList<ConceptMapMap> map;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConceptMapElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FhirPackage.eINSTANCE.getConceptMapElement();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Uri getCodeSystem() {
		return codeSystem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCodeSystem(Uri newCodeSystem, NotificationChain msgs) {
		Uri oldCodeSystem = codeSystem;
		codeSystem = newCodeSystem;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM, oldCodeSystem, newCodeSystem);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCodeSystem(Uri newCodeSystem) {
		if (newCodeSystem != codeSystem) {
			NotificationChain msgs = null;
			if (codeSystem != null)
				msgs = ((InternalEObject)codeSystem).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM, null, msgs);
			if (newCodeSystem != null)
				msgs = ((InternalEObject)newCodeSystem).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM, null, msgs);
			msgs = basicSetCodeSystem(newCodeSystem, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM, newCodeSystem, newCodeSystem));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Code getCode() {
		return code;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCode(Code newCode, NotificationChain msgs) {
		Code oldCode = code;
		code = newCode;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FhirPackage.CONCEPT_MAP_ELEMENT__CODE, oldCode, newCode);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCode(Code newCode) {
		if (newCode != code) {
			NotificationChain msgs = null;
			if (code != null)
				msgs = ((InternalEObject)code).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - FhirPackage.CONCEPT_MAP_ELEMENT__CODE, null, msgs);
			if (newCode != null)
				msgs = ((InternalEObject)newCode).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - FhirPackage.CONCEPT_MAP_ELEMENT__CODE, null, msgs);
			msgs = basicSetCode(newCode, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FhirPackage.CONCEPT_MAP_ELEMENT__CODE, newCode, newCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConceptMapDependsOn> getDependsOn() {
		if (dependsOn == null) {
			dependsOn = new EObjectContainmentEList<ConceptMapDependsOn>(ConceptMapDependsOn.class, this, FhirPackage.CONCEPT_MAP_ELEMENT__DEPENDS_ON);
		}
		return dependsOn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConceptMapMap> getMap() {
		if (map == null) {
			map = new EObjectContainmentEList<ConceptMapMap>(ConceptMapMap.class, this, FhirPackage.CONCEPT_MAP_ELEMENT__MAP);
		}
		return map;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM:
				return basicSetCodeSystem(null, msgs);
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE:
				return basicSetCode(null, msgs);
			case FhirPackage.CONCEPT_MAP_ELEMENT__DEPENDS_ON:
				return ((InternalEList<?>)getDependsOn()).basicRemove(otherEnd, msgs);
			case FhirPackage.CONCEPT_MAP_ELEMENT__MAP:
				return ((InternalEList<?>)getMap()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM:
				return getCodeSystem();
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE:
				return getCode();
			case FhirPackage.CONCEPT_MAP_ELEMENT__DEPENDS_ON:
				return getDependsOn();
			case FhirPackage.CONCEPT_MAP_ELEMENT__MAP:
				return getMap();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM:
				setCodeSystem((Uri)newValue);
				return;
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE:
				setCode((Code)newValue);
				return;
			case FhirPackage.CONCEPT_MAP_ELEMENT__DEPENDS_ON:
				getDependsOn().clear();
				getDependsOn().addAll((Collection<? extends ConceptMapDependsOn>)newValue);
				return;
			case FhirPackage.CONCEPT_MAP_ELEMENT__MAP:
				getMap().clear();
				getMap().addAll((Collection<? extends ConceptMapMap>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM:
				setCodeSystem((Uri)null);
				return;
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE:
				setCode((Code)null);
				return;
			case FhirPackage.CONCEPT_MAP_ELEMENT__DEPENDS_ON:
				getDependsOn().clear();
				return;
			case FhirPackage.CONCEPT_MAP_ELEMENT__MAP:
				getMap().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE_SYSTEM:
				return codeSystem != null;
			case FhirPackage.CONCEPT_MAP_ELEMENT__CODE:
				return code != null;
			case FhirPackage.CONCEPT_MAP_ELEMENT__DEPENDS_ON:
				return dependsOn != null && !dependsOn.isEmpty();
			case FhirPackage.CONCEPT_MAP_ELEMENT__MAP:
				return map != null && !map.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ConceptMapElementImpl
