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

package eu.esdihumboldt.hale.rcp.wizards.io.wfs;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.wizards.io.FeatureTypeList;
import eu.esdihumboldt.hale.rcp.wizards.io.FeatureTypeList.TypeSelectionListener;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureTypesPage extends AbstractTypesPage<WfsConfiguration> {
	
	private FeatureTypeList list;

	/**
	 * Constructor
	 * 
	 * @param configuration the WFS configuration 
	 * @param capsPage the capabilities page
	 */
	public FeatureTypesPage(WfsConfiguration configuration, CapabilitiesPage capsPage) {
		super(configuration, capsPage, Messages.getString("FeatureTypesPage.0")); //$NON-NLS-1$
		
		setTitle(Messages.getString("FeatureTypesPage.1")); //$NON-NLS-1$
		setMessage(Messages.getString("FeatureTypesPage.2")); //$NON-NLS-1$
	}

	/**
	 * @see AbstractTypesPage#update(List)
	 */
	@Override
	protected void update(List<FeatureType> types) {
		list.setFeatureTypes(types);
		
		//XXX the update doesn't refresh the buttons when the page is shown the first time
		//update();
		
		//XXX so try something nasty instead
		final Display display = Display.getCurrent();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				display.asyncExec(new Runnable() {
					
					@Override
					public void run() {
						FeatureTypesPage.this.update();
					}
					
				});
			}
		}, 500);
	}

	/**
	 * @see AbstractWfsPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(1, false));
		
		list = new FeatureTypeList(page, getConfiguration().getFixedNamespace());
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		list.addTypeSelectionListener(new TypeSelectionListener() {
			
			@Override
			public void selectionChanged() {
				FeatureTypesPage.this.update();
			}
			
		});
		
		setControl(page);
		
		update();
	}

	/**
	 * @see AbstractTypesPage#getSelection()
	 */
	@Override
	protected List<FeatureType> getSelection() {
		return list.getSelection();
	}

	private void update() {
		boolean valid = true;
		
		// test namespace
		if (valid) {
			String ns = getConfiguration().getFixedNamespace();
			
			if (ns != null) {
				valid = list.getNamespace().equals(ns);
				if (!valid) {
					setErrorMessage(Messages.getString("FeatureTypesPage.3") + ns); //$NON-NLS-1$
				}
			}
		}
		
		// test selection
		if (valid) {
			List<FeatureType> selection = list.getSelection();
			valid = selection != null && !selection.isEmpty();
			if (!valid) {
				setErrorMessage(Messages.getString("FeatureTypesPage.4")); //$NON-NLS-1$
			}
		}
		
		if (valid) {
			setErrorMessage(null);
		}
		
		setPageComplete(valid);
	}

}
