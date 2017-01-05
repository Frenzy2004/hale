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

import org.junit.Ignore;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading composite surface geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class CompositeSurfaceGeometryTest extends AbstractHandlerTest {

	private MultiPolygon reference;

	private MultiPolygon referenceOnGrid;

	@Override
	public void init() {
		super.init();

		LinearRing shell = geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(0.01, 3.2), new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2),
				new Coordinate(-3.33, -3.2), new Coordinate(0.01, 3.2) });

		LinearRing[] holes = new LinearRing[2];
		LinearRing hole1 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(0, 1), new Coordinate(1, 1),
						new Coordinate(0, -1), new Coordinate(-1, -1), new Coordinate(0, 1) });
		LinearRing hole2 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(0, 2), new Coordinate(2, 2),
						new Coordinate(0, -2), new Coordinate(-2, -2), new Coordinate(0, 2) });
		holes[0] = hole1;
		holes[1] = hole2;

		Polygon polygon1 = geomFactory.createPolygon(shell, holes);

		shell = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(6.01, 9.2),
				new Coordinate(9.33, 9.33), new Coordinate(6.01, -9.2), new Coordinate(-9.33, -9.2),
				new Coordinate(6.01, 9.2) });

		holes = new LinearRing[2];
		hole1 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(2, 3), new Coordinate(3, 3),
						new Coordinate(2, -3), new Coordinate(-3, -3), new Coordinate(2, 3) });
		hole2 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(2, 4), new Coordinate(4, 4),
						new Coordinate(2, -4), new Coordinate(-4, -4), new Coordinate(2, 4) });
		holes[0] = hole1;
		holes[1] = hole2;

		Polygon polygon2 = geomFactory.createPolygon(shell, holes);

		Polygon[] polygons = new Polygon[] { polygon1, polygon2 };

		reference = geomFactory.createMultiPolygon(polygons);

		// For grid test
		shell = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(0, 3.2),
				new Coordinate(3.3, 3.3), new Coordinate(0, -3.2), new Coordinate(-3.4, -3.2),
				new Coordinate(0, 3.2) });

		holes = new LinearRing[2];
		hole1 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(0, 1), new Coordinate(1, 1),
						new Coordinate(0, -1), new Coordinate(-1, -1), new Coordinate(0, 1) });
		hole2 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(0, 2), new Coordinate(2, 2),
						new Coordinate(0, -2), new Coordinate(-2, -2), new Coordinate(0, 2) });
		holes[0] = hole1;
		holes[1] = hole2;

		polygon1 = geomFactory.createPolygon(shell, holes);

		shell = geomFactory.createLinearRing(new Coordinate[] { new Coordinate(6, 9.2),
				new Coordinate(9.3, 9.3), new Coordinate(6, -9.2), new Coordinate(-9.4, -9.2),
				new Coordinate(6, 9.2) });

		holes = new LinearRing[2];
		hole1 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(2, 3), new Coordinate(3, 3),
						new Coordinate(2, -3), new Coordinate(-3, -3), new Coordinate(2, 3) });
		hole2 = geomFactory
				.createLinearRing(new Coordinate[] { new Coordinate(2, 4), new Coordinate(4, 4),
						new Coordinate(2, -4), new Coordinate(-4, -4), new Coordinate(2, 4) });
		holes[0] = hole1;
		holes[1] = hole2;

		polygon2 = geomFactory.createPolygon(shell, holes);

		polygons = new Polygon[] { polygon1, polygon2 };

		referenceOnGrid = geomFactory.createMultiPolygon(polygons);

	}

	/**
	 * Test composite surface geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	@Ignore("Test case does not represent a valid composite surface")
	public void testCompositeSurfaceGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-compositesurface-gml32.xml").toURI(),
				true);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// CompositeCurveProperty with surfaceMembers defined through
			// PolygonPatch and Polygon
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkCompositeSurfacePropertyInstance(instance, true);
		} finally {
			it.close();
		}
	}

	/**
	 * Test composite surface geometries read from a GML 3.2 file Geometry
	 * coordinates will be moved to universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	@Ignore("Test case does not represent a valid composite surface")
	public void testCompositeSurfaceGml31_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/surface/sample-compositesurface-gml32.xml").toURI(),
				false);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// CompositeCurveProperty with surfaceMembers defined through
			// PolygonPatch and Polygon
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkCompositeSurfacePropertyInstance(instance, false);
		} finally {
			it.close();
		}
	}

	private void checkCompositeSurfacePropertyInstance(Instance instance, boolean keepOriginal) {
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
			@SuppressWarnings("unchecked")
			Polygon polygon = ((GeometryProperty<Polygon>) instance).getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					polygon.equalsExact(keepOriginal ? reference : referenceOnGrid));
		}
	}
}
