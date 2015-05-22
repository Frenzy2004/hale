/*
 * Copyright (c) 2015 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.extension.function.custom.impl;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Custom property function.
 * 
 * @author Simon Templer
 */
public class DefaultCustomPropertyFunction implements CustomPropertyFunction {

	private DefaultCustomPropertyFunctionEntity target;
	private List<DefaultCustomPropertyFunctionEntity> sources;

	private String identifier;
	private String name;
	private String functionType;
	private Value functionDefinition;
	private final PropertyFunctionDefinition descriptor = new PropertyFunctionDefinition() {

		@Override
		public String getId() {
			return identifier;
		}

		@Override
		public boolean isAugmentation() {
			return sources == null || sources.isEmpty();
		}

		@Override
		public Set<? extends PropertyParameterDefinition> getTarget() {
			if (target == null) {
				return Collections.emptySet();
			}
			return Collections.singleton(createParamDefinition(target));
		}

		@Override
		public Set<? extends PropertyParameterDefinition> getSource() {
			Set<PropertyParameterDefinition> sourceDefs = new HashSet<>();

			if (sources != null && !sources.isEmpty()) {
				for (DefaultCustomPropertyFunctionEntity source : sources) {
					sourceDefs.add(createParamDefinition(source));
				}
			}

			return sourceDefs;
		}

		@Override
		public FunctionParameterDefinition getParameter(String paramName) {
			// TODO Auto-generated method stub
			// XXX no parameters yet
			return null;
		}

		@Override
		public URL getIconURL() {
			return null;
		}

		@Override
		public URL getHelpURL() {
			return null;
		}

		@Override
		public CellExplanation getExplanation() {
			return null;
		}

		@Override
		public String getDisplayName() {
			return name;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getDefiningBundle() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<FunctionParameterDefinition> getDefinedParameters() {
			// TODO Auto-generated method stub
			return Collections.emptyList();
		}

		@Override
		public String getCategoryId() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * @return the functionType
	 */
	public String getFunctionType() {
		return functionType;
	}

	/**
	 * @param target2
	 * @return
	 */
	protected PropertyParameterDefinition createParamDefinition(
			final DefaultCustomPropertyFunctionEntity entity) {
		return entity;
	}

	/**
	 * @param functionType the functionType to set
	 */
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	/**
	 * @return the functionDefinition
	 */
	public Value getFunctionDefinition() {
		return functionDefinition;
	}

	/**
	 * @param functionDefinition the functionDefinition to set
	 */
	public void setFunctionDefinition(Value functionDefinition) {
		this.functionDefinition = functionDefinition;
	}

	/**
	 * @return the target
	 */
	public DefaultCustomPropertyFunctionEntity getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(DefaultCustomPropertyFunctionEntity target) {
		this.target = target;
	}

	/**
	 * @return the sources
	 */
	public List<DefaultCustomPropertyFunctionEntity> getSources() {
		return sources;
	}

	/**
	 * @param sources the sources to set
	 */
	public void setSources(List<DefaultCustomPropertyFunctionEntity> sources) {
		this.sources = sources;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.custom.CustomFunction#getDescriptor()
	 */
	@Override
	public PropertyFunctionDefinition getDescriptor() {
		return descriptor;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.custom.CustomFunction#getTransformation()
	 */
	@Override
	public PropertyTransformation<?> getTransformation() {
		// TODO Auto-generated method stub
		return null;
	}

}
