package ch.epfl.alpano.summit;

import static ch.epfl.alpano.summit.GazetteerParser.readSummitsFrom;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import ch.epfl.alpano.GeoPoint;

final class GazetteerParserTest {

	@Test
	void parserFailsOnNonExistantFile() throws IOException {
		final var f = new File("/   /d:/ééé");
		assertThrows(IOException.class, () -> readSummitsFrom(f));
	}

	@Test
	void parserFailsOnGarbageLine() throws IOException {
		final var f = tempFileWithLines("blabla");
		assertThrows(IOException.class, () -> readSummitsFrom(f));
	}

	private void paserFailsOnInvalidInfo(final String info) throws IOException {
		final var f = tempFileWithLines(info);
		assertThrows(IOException.class, () -> readSummitsFrom(f));
	}

	@Test
	void parserFailsOnInvalidLongitude() throws IOException {
		paserFailsOnInvalidInfo("  7:25:1x 45:08:25  1325  R0 E07 BA MONTE CURT");
	}

	@Test
	void parserFailsOnInvalidLatitude() throws IOException {
		paserFailsOnInvalidInfo("  7:25:12 45:08:2_  1325  R0 E07 BA MONTE CURT");
	}

	@Test
	void parserFailsOnInvalidElevation() throws IOException {
		paserFailsOnInvalidInfo("  7:25:12 45:08:25  leet  R0 E07 BA MONTE CURT");
	}

	@Test
	void summitListIsUnmodifiable() throws IOException {
		final var l = "  7:01:02 46:32:56  2002  H1 B01 D7 LE MOLESON";
		final var f = tempFileWithLines(l);
		final var list = readSummitsFrom(f);
		assertThrows(UnsupportedOperationException.class, () -> list.clear());
	}

	@Test
	void parserSucceedsWithNegativeValue() throws IOException {
		final var l = " -7:01:02 46:32:56  2002  H1 B01 D7 LA MALMESON";
		final var f = tempFileWithLines(l);
		final var list = readSummitsFrom(f);
		assertTrue(list.get(0).position().longitude() < 0);
	}

	@Test
	void parserWorksOnValidFile() throws IOException {
		final var summits = Arrays.asList(
				new Summit("A MONT UN", hmsPoint(7, 30, 00, 15, 16, 17), 30),
				new Summit("B MONT ZWEI", hmsPoint(6, 12, 34, 1, 23, 45), 10),
				new Summit("C MONTE TRE", hmsPoint(1, 33, 33, 15, 66, 66), 1000),
				new Summit("D MONT AU NOM TRES LONG", hmsPoint(5, 00, 00, 5, 00, 00), 8000));
		final var formattedSummits = new String[summits.size()];
		var i = 0;
		for (final var s : summits) {
			formattedSummits[i++] = formatSummit(s);
		}
		final var f = tempFileWithLines(formattedSummits);
		final var readSummits = new ArrayList<>(readSummitsFrom(f));
		assertEquals(summits.size(), readSummits.size());

		readSummits.sort(Comparator.comparing(Summit::name));
		final var expectedIt = summits.iterator();
		final var actualIt = readSummits.iterator();
		while (expectedIt.hasNext()) {
			final var expected = expectedIt.next();
			final var actual = actualIt.next();
			assertEquals(expected.name(), actual.name());
			assertEquals(expected.elevation(), actual.elevation());
			assertEquals(
					expected.position().longitude(),
					actual.position().longitude(),
					toRadians(1d / 3600d));
			assertEquals(
					expected.position().latitude(),
					actual.position().latitude(),
					toRadians(1d / 3600d));
		}
	}

	private String formatSummit(final Summit s) {
		final var lon = s.position().longitude();
		final var lat = s.position().latitude();
		return String.format("%3d:%02d:%02d %2d:%02d:%02d %5d  R0 E07 BA %s",
				h(lon), m(lon), s(lon), h(lat), m(lat), s(lat), s.elevation(), s.name());
	}

	private static GeoPoint hmsPoint(final int hLon, final int mLon, final int sLon, final int hLat, final int mLat,
			final int sLat) {
		return new GeoPoint(hmsToRad(hLon, mLon, sLon), hmsToRad(hLat, mLat, sLat));
	}

	private static double hmsToRad(final int h, final int m, final int s) {
		return toRadians(h + (m / 60d) + (s / 3600d));
	}

	private static int h(final double d) {
		return (int) toDegrees(d);
	}

	private static int m(final double d) {
		return (int) (toDegrees(d) * 60d) % 60;
	}

	private static int s(final double d) {
		return (int) (toDegrees(d) * 3600d) % 60;
	}

	private static File tempFileWithLines(final String... lines) throws IOException {
		final var f = Files.createTempFile("summits", ".txt").toFile();
		f.deleteOnExit();
		try (final var b = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), US_ASCII))) {
			for (final var l : lines) {
				b.write(l);
				b.newLine();
			}
		}
		return f;
	}
}
