/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.instance.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.orientechnologies.orient.core.record.impl.ODocument;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableInstance;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;

/**
 * Instance implementation based on {@link ODocument}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class OInstance extends OGroup implements MutableInstance {

	/**
	 * Name for the special field for an instance value
	 */
	public static final String FIELD_VALUE = "___value___";

	/**
	 * The set of special field names, e.g. for the instance value
	 */
	private static final Set<String> SPECIAL_FIELDS = new HashSet<String>();
	static {
		SPECIAL_FIELDS.add(FIELD_VALUE);
	}
	
	/**
	 * Creates an empty instance associated with the given type.
	 * 
	 * @param typeDef the definition of the instance's type 
	 */
	public OInstance(TypeDefinition typeDef) {
		super(typeDef);
	}
	
	/**
	 * Creates an instance based on the given document
	 * 
	 * @param document the document
	 * @param typeDef the definition of the instance's type
	 */
	public OInstance(ODocument document, TypeDefinition typeDef) {
		super(document, typeDef);
	}
	
	/**
	 * Copy constructor.
	 * Creates an instance based on the properties and values of the given 
	 * instance.
	 * 
	 * @param org the instance to copy
	 */
	public OInstance(Instance org) {
		super(org);
		
		setValue(org.getValue());
	}

	/**
	 * @see MutableInstance#setValue(Object)
	 */
	@Override
	public void setValue(Object value) {
		document.field(FIELD_VALUE, value);
	}

	/**
	 * @see Instance#getValue()
	 */
	@Override
	public Object getValue() {
		return document.field(FIELD_VALUE);
	}

	/**
	 * @see OGroup#getSpecialFieldNames()
	 */
	@Override
	protected Collection<String> getSpecialFieldNames() {
		Collection<String> superFields = super.getSpecialFieldNames();
		if (superFields.isEmpty()) {
			return SPECIAL_FIELDS;
		}
		else {
			Set<String> result = new HashSet<String>(superFields);
			result.addAll(SPECIAL_FIELDS);
			return result;
		}
	}

	/**
	 * @see Instance#getDefinition()
	 */
	@Override
	public TypeDefinition getDefinition() {
		return (TypeDefinition) super.getDefinition();
	}

}
