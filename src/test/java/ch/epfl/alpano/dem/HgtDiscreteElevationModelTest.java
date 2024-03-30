package ch.epfl.alpano.dem;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

final class HgtDiscreteElevationModelTest {

	private static final long HGT_FILE_SIZE = 3601L * 3601L * 2L;
	private static Path FAKE_HGT_DIR;
	private static Path FAKE_HGT_FILE;

	@BeforeAll
	public static void createFakeHgtFiles() throws IOException {
		final var fakeHgtDir = Files.createTempDirectory("hgt");

		final var fakeHgtFile = fakeHgtDir.resolve("empty.hgt");
		try (final var c = FileChannel.open(fakeHgtFile, CREATE_NEW, READ, WRITE)) {
			// make sure the empty hgt file has the right size
			c.map(MapMode.READ_WRITE, 0, HGT_FILE_SIZE).asShortBuffer();
		}

		FAKE_HGT_FILE = fakeHgtFile;
		FAKE_HGT_DIR = fakeHgtDir;
	}

	@AfterAll
	public static void deleteFakeHgtFiles() throws IOException {
		Files.walkFileTree(FAKE_HGT_DIR, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
				if (exc != null) {
					throw exc;
				}
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Test
	void constructorFailsWithTooShortName() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> createHgtDemWithFileNamed("N47E010.hg"));
	}

	@Test
	void constructorFailsWithInvalidLatitudeLetter() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> createHgtDemWithFileNamed("N4xE010.hgt"));
	}

	@Test
	void constructorFailsWithInvalidLongitudeLetter() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> createHgtDemWithFileNamed("N47x010.hgt"));
	}

	@Test
	void constructorFailsWithInexistantFile() throws Exception {
		final var f = FAKE_HGT_DIR.resolve("N40E010.hgt").toFile();
		assertThrows(IllegalArgumentException.class, () -> new HgtDiscreteElevationModel(f));
	}

	@Test
	void constructorFailsWithEmptyFile() throws Exception {
		final var f = FAKE_HGT_DIR.resolve("N41E010.hgt").toFile();
		try (final var s = new FileOutputStream(f)) {
			s.write(0);
		}
		assertThrows(IllegalArgumentException.class, () -> new HgtDiscreteElevationModel(f));
	}

	@Test
	void constructorWorksInEcuador() throws Exception {
		createHgtDemWithFileNamed("S03W078.hgt");
		assertTrue(true);
	}

	@Test
	void extentMatchesFileName() throws Exception {
		final var lons = new int[] { 1, 7 };
		final var lats = new int[] { 1, 47 };
		for (final var lon : lons) {
			for (final var lat : lats) {
				final var expectedExtent = new Interval2D(
						new Interval1D(lon * 3600, (lon + 1) * 3600),
						new Interval1D(lat * 3600, (lat + 1) * 3600));
				final var hgtFileName = String.format("N%02dE%03d.hgt", lat, lon);
				final var p = copyEmptyHgtFileAs(hgtFileName);
				final var dem = new HgtDiscreteElevationModel(p.toFile());
				assertEquals(expectedExtent, dem.extent());
			}
		}
	}

	@Test
	void elevationSampleFailsForIndexNotInExtent() throws Exception {
		final var hgtFileName = "N02E002.hgt";
		final var p = copyEmptyHgtFileAs(hgtFileName);
		final var dem = new HgtDiscreteElevationModel(p.toFile());
		assertThrows(IllegalArgumentException.class, () -> dem.elevationSample(10, 10));

	}

	@Test
	void elevationSampleIsCorrectInFourCorners() throws Exception {
		final var p = FAKE_HGT_DIR.resolve("N01E001.hgt");
		try (final var c = FileChannel.open(p, CREATE_NEW, READ, WRITE)) {
			final var b = c.map(MapMode.READ_WRITE, 0, HGT_FILE_SIZE).asShortBuffer();
			b.put(0, (short) 1);
			b.put(3600, (short) 2);
			b.put(3601 * 3600, (short) 3);
			b.put(3601 * 3601 - 1, (short) 4);
		}
		final var dem = new HgtDiscreteElevationModel(p.toFile());
		assertEquals(0, dem.elevationSample(4000, 4000), 1e-10);
		assertEquals(1, dem.elevationSample(3600, 7200), 1e-10);
		assertEquals(2, dem.elevationSample(7200, 7200), 1e-10);
		assertEquals(3, dem.elevationSample(3600, 3600), 1e-10);
		assertEquals(4, dem.elevationSample(7200, 3600), 1e-10);

	}

	private static void createHgtDemWithFileNamed(final String hgtFileName) throws Exception {
		final var p = copyEmptyHgtFileAs(hgtFileName);
		new HgtDiscreteElevationModel(p.toFile());
	}

	private static Path copyEmptyHgtFileAs(final String hgtFileName) throws IOException {
		return Files.copy(FAKE_HGT_FILE, FAKE_HGT_DIR.resolve(hgtFileName), REPLACE_EXISTING);
	}
}
