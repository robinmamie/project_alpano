package ch.epfl.alpano.summit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.epfl.alpano.GeoPoint;

/**
 *
 *
 * @author Charline Montial (274902)
 * @author Yves Zumbach (269845)
 */
public class GazetteerParserTest {

	List<Summit> oneLine = Arrays.asList(new Summit("MONTE CURT",
			new GeoPoint(degreeMinuteSecondToRadian("7:25:12"), degreeMinuteSecondToRadian("45:08:25")), 1325));
	List<Summit> multipleLines = Arrays.asList(
			new Summit("MONTE CURT",
					new GeoPoint(degreeMinuteSecondToRadian("7:25:12"), degreeMinuteSecondToRadian("45:08:25")), 1325),
			new Summit("TRUC DEL FARO",
					new GeoPoint(degreeMinuteSecondToRadian("7:24:25"), degreeMinuteSecondToRadian("45:09:21")), 1206),
			new Summit("ROCCA DELLA SELLA",
					new GeoPoint(degreeMinuteSecondToRadian("7:21:08"), degreeMinuteSecondToRadian("45:08:23")), 1508),
			new Summit("MONTE SAPEI",
					new GeoPoint(degreeMinuteSecondToRadian("7:20:47"), degreeMinuteSecondToRadian("45:09:14")), 1615),
			new Summit("MONTE ARPON",
					new GeoPoint(degreeMinuteSecondToRadian("7:21:57"), degreeMinuteSecondToRadian("45:08:45")), 1236),
			new Summit("MONTE MUSINE",
					new GeoPoint(degreeMinuteSecondToRadian("7:27:18"), degreeMinuteSecondToRadian("45:06:50")), 1150));
	List<Summit> negativeLatitude = Arrays.asList(new Summit("CECI EST UN TEST",
			new GeoPoint(Math.toRadians(-7.42), degreeMinuteSecondToRadian("45:08:25")), 1325));

	@Test
	public void testReadSummitFrom() {
		try {
			GazetteerParser.readSummitsFrom(new File("alps.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testFileParsingWithOneLine() {
		List<Summit> ols = null;
		try {
			ols = GazetteerParser.readSummitsFrom(new File("alpsTestOneLine.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not parse the file!");
		}
		for (int i = 0; i < ols.size(); ++i) {
			compareTwoSummits(oneLine.get(i), ols.get(i));
		}
	}

	@Test
	public void testFileParsingWithMultipleLines() {
		List<Summit> mls = null;
		try {
			mls = GazetteerParser.readSummitsFrom(new File("alpsTestMultipleLines.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not parse the file!");
		}
		for (int i = 0; i < mls.size(); ++i) {
			compareTwoSummits(multipleLines.get(i), mls.get(i));
		}
	}

	@Test
	public void testFileParsingWithNegativeLatitudes() {
		List<Summit> nls = null;
		try {
			nls = GazetteerParser.readSummitsFrom(new File("alpsTestNegativeLatitudes.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not parse the file!");
		}
		for (int i = 0; i < nls.size(); ++i) {
//			System.out.println(negativeLatitude.get(i).toString() + " " + nls.get(i).toString());
			compareTwoSummits(negativeLatitude.get(i), nls.get(i));
		}
	}

	private double degreeMinuteSecondToRadian(String s) {
		String[] splittedS = s.split(":");
		return Math.toRadians(Integer.valueOf(splittedS[0]) + Integer.valueOf(splittedS[1]) / 60.0
				+ Integer.valueOf(splittedS[2]) / 3600.0);
	}

	private void compareTwoSummits(Summit s1, Summit s2) {
		assertEquals(s1.name(), s2.name());
		assertEquals(s1.elevation(), s2.elevation());
		assertEquals(s1.position().toString(), s2.position().toString());
	}
}
