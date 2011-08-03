/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.views.data.internal.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.views.data.internal.DataViewPlugin;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;

/**
 * Selects filtered features
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class InstanceServiceSelector implements InstanceSelector {
	
	private static final ALogger log = ALoggerFactory.getLogger(InstanceServiceSelector.class);
	
	/**
	 * Instance selector control
	 */
	private class InstanceSelectorControl extends Composite {
		
		private final ComboViewer schemaSpaces;
		
		private final ComboViewer typeDefinitions;
		
		private final ComboViewer count;
		
//		private final FeatureFilterField filterField;
		
		private Iterable<Instance> selection;
		
		private TypeDefinition selectedType;
		
		private final Image refreshImage;

		private final SchemaServiceListener schemaListener;

		private final InstanceServiceListener instanceListener;
	
		/**
		 * @see Composite#Composite(Composite, int)
		 */
		public InstanceSelectorControl(Composite parent, int style) {
			super(parent, style);
			
			refreshImage = DataViewPlugin.getImageDescriptor("icons/refresh.gif").createImage(); //$NON-NLS-1$
			
			setLayout(new GridLayout((spaceID == null)?(4):(3), false));
			
			// schema type selector
			if (spaceID == null) {
				schemaSpaces = new ComboViewer(this, SWT.READ_ONLY);
				schemaSpaces.setLabelProvider(new LabelProvider() {
		
					@Override
					public String getText(Object element) {
						if (element instanceof SchemaSpaceID) {
							switch ((SchemaSpaceID) element) {
							case SOURCE: return Messages.InstanceServiceFeatureSelector_SourceReturnText;
							case TARGET: return Messages.InstanceServiceFeatureSelector_TargetReturnText;
							default:
								return Messages.InstanceServiceFeatureSelector_defaultReturnText;
							}
						}
						else {
							return super.getText(element);
						}
					}
					
				});
				schemaSpaces.setContentProvider(ArrayContentProvider.getInstance());
				schemaSpaces.setInput(new Object[]{SchemaSpaceID.SOURCE, SchemaSpaceID.TARGET});
				schemaSpaces.setSelection(new StructuredSelection(SchemaSpaceID.SOURCE));
			}
			else {
				schemaSpaces = null;
			}
			
			// feature type selector
			typeDefinitions = new ComboViewer(this, SWT.READ_ONLY);
			typeDefinitions.setContentProvider(ArrayContentProvider.getInstance());
			/*featureTypes.setComparator(new ViewerComparator() {

				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					if (e1 instanceof FeatureType && e2 instanceof FeatureType) {
						return ((FeatureType) e1).getName().getLocalPart().compareTo(
								((FeatureType) e2).getName().getLocalPart());
					}
					return super.compare(viewer, e1, e2);
				}
				
			});*/
			typeDefinitions.setLabelProvider(new DefinitionLabelProvider());
			typeDefinitions.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}
				
			});
			
			// filter field
//			filterField = new FeatureFilterField((selectedType == null)?(null):(DefinitionUtil.getType(selectedType)), this, SWT.NONE);
//			filterField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//			filterField.addListener(new FilterListener() {
//				
//				@Override
//				public void filterChanged() {
//					updateSelection();
//				}
//				
//			});
			
			// refresh button
			/*XXX disabled for now - Button refresh = new Button(this, SWT.PUSH);
			refresh.setImage(refreshImage);
			refresh.setToolTipText("Refresh");
			refresh.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			refresh.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updateSelection();
				}
				
			});*/
			
			// max count selector
			count = new ComboViewer(this, SWT.READ_ONLY);
			count.setContentProvider(ArrayContentProvider.getInstance());
			count.setInput(new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3),
					Integer.valueOf(4), Integer.valueOf(5)});
			count.setSelection(new StructuredSelection(Integer.valueOf(2)));
			count.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelection();
				}
				
			});
			
			updateTypesSelection();
			
			if (schemaSpaces != null) {
				schemaSpaces.addSelectionChangedListener(new ISelectionChangedListener() {
					
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateTypesSelection();
					}
					
				});
			}
			
			// service listeners
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			ss.addSchemaServiceListener(schemaListener = new SchemaServiceListener() {
				
				@Override
				public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						@Override
						public void run() {
							updateTypesSelection();
						}
					});
				}

				@Override
				public void schemasCleared(SchemaSpaceID spaceID) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						@Override
						public void run() {
							updateTypesSelection();
						}
					});
				}
			});
			
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			is.addListener(instanceListener = new InstanceServiceAdapter() {
				
				@Override
				public void datasetChanged(DataSet dataSet) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							updateSelection();
						}
					});
				}
				
			});
		}
		
		/**
		 * Update the feature types selection
		 */
		protected void updateTypesSelection() {
			SchemaSpaceID space = getSchemaSpace();
			
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			TypeIndex schema = ss.getSchemas(space);
			
			List<TypeDefinition> filteredTypes = new ArrayList<TypeDefinition>(schema.getMappableTypes());
			
			Collections.sort(filteredTypes, new Comparator<TypeDefinition>() {

				@Override
				public int compare(TypeDefinition o1, TypeDefinition o2) {
					return o1.getDisplayName().compareTo(o2.getDisplayName());
				}
				
			});
			
			typeDefinitions.setInput(filteredTypes);
			
			// try to determine type to select from data set
			TypeDefinition typeToSelect = null;
			DataSet dataset = (space == SchemaSpaceID.SOURCE)?(DataSet.SOURCE):(DataSet.TRANSFORMED);
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			InstanceCollection instances = is.getInstances(dataset);
			if (instances != null) {
				ResourceIterator<Instance> itInstance = instances.iterator();
				try {
					while (itInstance.hasNext() && typeToSelect == null) {
						Instance instance = itInstance.next();
						typeToSelect = instance.getDefinition();
					}
				} finally {
					itInstance.close();
				}
			}
			
			// fallback selection
			if (typeToSelect == null && !filteredTypes.isEmpty()) {
				typeToSelect = filteredTypes.iterator().next();
			}
			
			if (typeToSelect != null) {
				typeDefinitions.setSelection(new StructuredSelection(typeToSelect));
			}
			
			layout(true, true);
			
			updateSelection();
		}

		/**
		 * Get the selected schema type
		 * 
		 * @return the selected schema type
		 */
		private SchemaSpaceID getSchemaSpace() {
			if (spaceID != null) {
				return spaceID;
			}
			else {
				return (SchemaSpaceID) ((IStructuredSelection) schemaSpaces.getSelection()).getFirstElement();
			}
		}

		/**
		 * Update the selection
		 */
		protected void updateSelection() {
			if (!typeDefinitions.getSelection().isEmpty()) {
				TypeDefinition type = (TypeDefinition) ((IStructuredSelection) typeDefinitions.getSelection()).getFirstElement();
				
//				filterField.setType(type);
				
				SchemaSpaceID space = getSchemaSpace();
				
				Integer max = (Integer) ((IStructuredSelection) count.getSelection()).getFirstElement();
				
				InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
				
				List<Instance> instanceList = new ArrayList<Instance>();
				DataSet dataset = (space == SchemaSpaceID.SOURCE)?(DataSet.SOURCE):(DataSet.TRANSFORMED);
				try {
//					Filter filter = filterField.getFilter();
//					
//					if (filter == null) {
						InstanceCollection instances = is.getInstances(dataset); //FIXME return instances by type
						
						ResourceIterator<Instance> it = instances.iterator();
						try {
							int num = 0;
							while (it.hasNext() && num < max) {
								//XXX only correct type XXX should not be necessary
								Instance instance = it.next();
								if (instance.getDefinition().equals(type)) {
									instanceList.add(instance);
									num++;
								}
							}
						} finally {
							it.close();
						}
//					}
//					else {
//						FeatureCollection<FeatureType, Feature> fc = is.getFeatures(dataset);
//						
//						FeatureIterator<Feature> it = fc.subCollection(filter).features();
//						int num = 0;
//						while (it.hasNext() && num < max) {
//							Feature feature = it.next();
//							if (feature.getType().getName().getLocalPart().equals(type.getDisplayName())) {
//								featureList.add(feature);
//								num++;
//							}
//						}
//					}
				} catch (Exception e) {
					log.warn("Error creating filter"); //$NON-NLS-1$
				}
				
				selection = instanceList;
				selectedType = type;
			}
			else {
				selection = null;
				selectedType = null;
				
//				filterField.setType(null);
			}
			
			for (InstanceSelectionListener listener : listeners) {
				listener.selectionChanged(selectedType, selection);
			}
		}
		
		/**
		 * @see Widget#dispose()
		 */
		@Override
		public void dispose() {
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			
			ss.removeSchemaServiceListener(schemaListener);
			is.removeListener(instanceListener);
			
			refreshImage.dispose();
			
			listeners.clear();
			
			super.dispose();
		}

	}
	
	private final Set<InstanceSelectionListener> listeners = new HashSet<InstanceSelectionListener>();
	
	private InstanceSelectorControl current;
	
	private final SchemaSpaceID spaceID;
	
	/**
	 * Create an instance selector
	 * 
	 * @param spaceID the fixed schema space ID or <code>null</code> to
	 *   allow selecting the schema space
	 */
	public InstanceServiceSelector(SchemaSpaceID spaceID) {
		super();
		
		this.spaceID = spaceID;
	}
	
	/**
	 * @see InstanceSelector#addSelectionListener(InstanceSelectionListener)
	 */
	@Override
	public void addSelectionListener(InstanceSelectionListener listener) {
		listeners.add(listener);
		
		if (current != null && !current.isDisposed()) {
			listener.selectionChanged(current.selectedType, current.selection);
		}
	}
	
	/**
	 * @see InstanceSelector#removeSelectionListener(InstanceSelectionListener)
	 */
	@Override
	public void removeSelectionListener(InstanceSelectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see InstanceSelector#createControl(Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		current = new InstanceSelectorControl(parent, SWT.NONE);
		return current;
	}

}
