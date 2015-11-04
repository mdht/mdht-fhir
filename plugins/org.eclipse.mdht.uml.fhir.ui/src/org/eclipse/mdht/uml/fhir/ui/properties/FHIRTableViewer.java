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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mdht.uml.fhir.provider.FHIRItemProviderAdapterFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.openhealthtools.mdht.uml.common.ui.util.AdapterFactoryCellModifier;
import org.openhealthtools.mdht.uml.edit.provider.UML2ExtendedAdapterFactory;

public abstract class FHIRTableViewer extends TableViewer {
		private AdapterFactory adapterFactory = null;
		private AdapterFactoryContentProvider adapterFactoryContentProvider = null;
		private AdapterFactoryLabelProvider adapterFactoryLabelProvider = null;
		private ICellModifier adapterFactoryCellModifier = null;
		
	    public FHIRTableViewer(Table table)  {
	        super(table);

	        createColumns();
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);

	        getAdapterFactory();
	        setContentProvider(adapterFactoryContentProvider);
	        setLabelProvider(adapterFactoryLabelProvider);
//			setCellModifier(adapterFactoryCellModifier);
	    }

		protected AdapterFactory getAdapterFactory() {
			if (adapterFactory == null) {
				List<AdapterFactory> factories = new ArrayList<AdapterFactory>();
				factories.add(new FHIRItemProviderAdapterFactory());
				factories.add(new UML2ExtendedAdapterFactory());
		
				adapterFactory = new ComposedAdapterFactory(factories);
		        adapterFactoryContentProvider = createContentProvider(adapterFactory);
		        adapterFactoryLabelProvider = createLabelProvider(adapterFactory);
		        adapterFactoryCellModifier = createCellModifier(adapterFactory);
			}
			
			return adapterFactory;
		}

		protected AdapterFactoryContentProvider createContentProvider(AdapterFactory adapterFactory) {
			return  new AdapterFactoryContentProvider(adapterFactory);
		}
	
		protected AdapterFactoryLabelProvider createLabelProvider(AdapterFactory adapterFactory) {
			return  new AdapterFactoryLabelProvider(adapterFactory);
		}

		protected ICellModifier createCellModifier(AdapterFactory adapterFactory) {
			return  new AdapterFactoryCellModifier(adapterFactory);
		}
	
	    protected void createColumns() {
	    	// implemented by subclass
	    }
	
	    protected TableViewerColumn createTableViewerColumn(String header, int width, int idx)  {
	        TableViewerColumn column = new TableViewerColumn(this, SWT.LEFT, idx);
	        column.getColumn().setText(header);
	        column.getColumn().setWidth(width);
	        column.getColumn().setResizable(true);
	        column.getColumn().setMoveable(true);
	
	        return column;
	    }
	    
	    public List<EObject> getSelectedElements() {
	    	List<EObject> selectedElements = new ArrayList<EObject>();
			ISelection selection = getSelection();
			if (selection instanceof IStructuredSelection) {
				for (Object selected : ((IStructuredSelection)selection).toList()) {
					if (selected instanceof EObject) {
						selectedElements.add((EObject)selected);
					}
				}
			}
	    	
	    	return selectedElements;
	    }
	}