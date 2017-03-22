package ch.epfl.alpano.dem;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.alpano.Distance;


/**
 *
 *
 * @author Charline Montial (274902)
 * @author Yves Zumbach (269845)
*/
public class DiscreteElevationModelTest__Global {

	@Test
	public void testSamplePerDegreeValue () {
		assertEquals(3600, DiscreteElevationModel.SAMPLES_PER_DEGREE);
	}

	@Test
	public void testSamplePerRadianValue () {
		assertEquals((double) DiscreteElevationModel.SAMPLES_PER_DEGREE * (180.0 / Math.PI), DiscreteElevationModel.SAMPLES_PER_RADIAN, 10e2);
	}
}
