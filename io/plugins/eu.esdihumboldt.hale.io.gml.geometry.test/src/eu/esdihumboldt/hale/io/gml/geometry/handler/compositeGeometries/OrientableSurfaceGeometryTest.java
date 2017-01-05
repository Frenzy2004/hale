/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.geometry.handler.compositeGeometries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading orientable surface geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class OrientableSurfaceGeometryTest extends AbstractHandlerTest {

	private Polygon reference;
	private Polygon referenceOnGrid;

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest#init()
	 */
	@Override
	public void init() {
		super.init();

		LinearRing shell = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(-122.44, 37.80),
						new Coordinate(-122.45, 37.80), new Coordinate(-122.45, 37.78),
						new Coordinate(-122.44, 37.78), new Coordinate(-122.44, 37.80) });

		LinearRing[] holes = new LinearRing[1];
		LinearRing hole1 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(-122.24, 37.60),
						new Coordinate(-122.25, 37.60), new Coordinate(-122.25, 37.58),
						new Coordinate(-122.24, 37.58), new Coordinate(-122.24, 37.60) });
		holes[0] = hole1;

		reference = geomFactory.createPolygon(shell, holes);

		// grid
		shell = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(-122.44, 37.80),
				new Coordinate(-122.46, 37.80), new Coordinate(-122.46, 37.78),
				new Coordinate(-122.44, 37.78), new Coordinate(-122.44, 37.80) });

		holes = new LinearRing[1];
		hole1 = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(-122.24, 37.6),
				new Coordinate(-122.25, 37.59), new Coordinate(-122.25, 37.57),
				new Coordinate(-122.24, 37.58), new Coordinate(-122.24, 37.6) });
		holes[0] = hole1;

		referenceOnGrid = geomFactory.createPolygon(shell, holes);
	}

	/**
	 * Test orientable surface geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testOrientableSurfaceGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-orientablesurface-gml32.xml").toURI(),
				true);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// Polygon with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkOrientableSurfacePropertyInstance(instance, true);
		} finally {
			it.close();
		}
	}

	/**
	 * Test orientable surface geometries read from a GML 3.2 file. Geometry
	 * coordinates will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testOrientableSurfaceGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-orientablesurface-gml32.xml").toURI(),
				false, 0.01);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// Polygon with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkOrientableSurfacePropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkOrientableSurfacePropertyInstance(Instance instance, boolean keepOriginal) {
		Object[] geomVals = instance.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance, keepOriginal);
	}

	private void checkGeomInstance(Instance geomInstance, boolean keepOriginal) {
		for (GeometryProperty<?> instance : getGeometries(geomInstance)) {
			Geometry geom = instance.getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					geom.equalsExact(keepOriginal ? reference : referenceOnGrid));
		}
	}

}
